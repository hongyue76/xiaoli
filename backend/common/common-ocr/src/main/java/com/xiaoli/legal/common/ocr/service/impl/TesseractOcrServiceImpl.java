package com.xiaoli.legal.common.ocr.service.impl;

import com.xiaoli.legal.common.ocr.config.OcrProperties;
import com.xiaoli.legal.common.ocr.dto.OcrRequest;
import com.xiaoli.legal.common.ocr.dto.OcrResponse;
import com.xiaoli.legal.common.ocr.service.OcrService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tesseract OCR服务实现
 */
@Service
public class TesseractOcrServiceImpl implements OcrService {

    private static final Logger log = LoggerFactory.getLogger(TesseractOcrServiceImpl.class);

    private final OcrProperties properties;
    private final ITesseract tesseract;

    public TesseractOcrServiceImpl(OcrProperties properties) {
        this.properties = properties;
        this.tesseract = new Tesseract();
        
        // 设置Tesseract数据路径
        String dataPath = properties.getDataPath();
        if (dataPath != null && !dataPath.isEmpty()) {
            tesseract.setDatapath(dataPath);
        }
        
        // 设置语言
        tesseract.setLanguage(properties.getLanguage());
    }

    @Override
    public OcrResponse recognizeImage(OcrRequest request) {
        long startTime = System.currentTimeMillis();
        
        try {
            // 获取图片文件
            File imageFile = getImageFile(request);
            if (imageFile == null) {
                throw new RuntimeException("无法获取图片文件");
            }

            // 设置语言
            if (request.getLanguage() != null) {
                tesseract.setLanguage(request.getLanguage());
            } else {
                tesseract.setLanguage(properties.getLanguage());
            }

            // 识别
            String text = tesseract.doOCR(imageFile);

            // 构建响应
            OcrResponse response = OcrResponse.builder()
                    .text(text.trim())
                    .processTime(System.currentTimeMillis() - startTime)
                    .build();

            // 暂时不支持坐标获取，简化处理
            response.setConfidence(90.0); // 默认置信度

            log.info("图片OCR识别完成, 耗时: {}ms", response.getProcessTime());
            return response;

        } catch (Exception e) {
            log.error("OCR识别失败", e);
            throw new RuntimeException("OCR识别失败: " + e.getMessage());
        }
    }

    @Override
    public String recognizeImage(String imagePath) {
        OcrRequest request = OcrRequest.builder()
                .imagePath(imagePath)
                .build();
        return recognizeImage(request).getText();
    }

    @Override
    public OcrResponse recognizePdf(OcrRequest request) {
        long startTime = System.currentTimeMillis();
        
        try {
            String pdfPath = request.getPdfPath();
            int pageNumber = request.getPageNumber();

            try (PDDocument document = PDDocument.load(new File(pdfPath))) {
                PDFRenderer renderer = new PDFRenderer(document);

                // 渲染指定页面
                BufferedImage image = renderer.renderImage(pageNumber - 1, properties.getDpi() / 72);

                // 保存为临时文件
                String tempPath = System.getProperty("java.io.tmpdir");
                String tempFile = tempPath + File.separator + UUID.randomUUID() + ".png";
                ImageIO.write(image, "png", new File(tempFile));

                // OCR识别
                OcrRequest ocrRequest = OcrRequest.builder()
                        .imagePath(tempFile)
                        .language(request.getLanguage())
                        .withCoordinates(request.isWithCoordinates())
                        .build();
                OcrResponse response = recognizeImage(ocrRequest);

                // 清理临时文件
                new File(tempFile).delete();

                response.setProcessTime(System.currentTimeMillis() - startTime);
                return response;
            }

        } catch (IOException e) {
            log.error("PDF处理失败", e);
            throw new RuntimeException("PDF处理失败: " + e.getMessage());
        }
    }

    @Override
    public List<OcrResponse> batchRecognize(List<String> imagePaths) {
        List<OcrResponse> results = new ArrayList<>();
        for (String path : imagePaths) {
            try {
                OcrResponse response = recognizeImage(OcrRequest.builder().imagePath(path).build());
                results.add(response);
            } catch (Exception e) {
                log.error("批量识别失败: {}", path, e);
            }
        }
        return results;
    }

    @Override
    public IdCardInfo recognizeIdCard(String imagePath) {
        String text = recognizeImage(imagePath);
        
        IdCardInfo info = new IdCardInfo();
        
        // 姓名
        Pattern namePattern = Pattern.compile("姓名[：:]?\\s*([\\u4e00-\\u9fa5]{2,4})");
        Matcher nameMatcher = namePattern.matcher(text);
        if (nameMatcher.find()) {
            info.setName(nameMatcher.group(1));
        }
        
        // 性别
        Pattern genderPattern = Pattern.compile("性别[：:]?\\s*([男女])");
        Matcher genderMatcher = genderPattern.matcher(text);
        if (genderMatcher.find()) {
            info.setGender(genderMatcher.group(1));
        }
        
        // 民族
        Pattern ethnicityPattern = Pattern.compile("民族[：:]?\\s*([\\u4e00-\\u9fa5]{1,6})");
        Matcher ethnicityMatcher = ethnicityPattern.matcher(text);
        if (ethnicityMatcher.find()) {
            info.setEthnicity(ethnicityMatcher.group(1));
        }
        
        // 出生日期
        Pattern birthPattern = Pattern.compile("出生[：:]?\\s*(\\d{4})年?\\s*(\\d{1,2})月?\\s*(\\d{1,2})日?");
        Matcher birthMatcher = birthPattern.matcher(text);
        if (birthMatcher.find()) {
            info.setBirthDate(birthMatcher.group(1) + "-" + birthMatcher.group(2) + "-" + birthMatcher.group(3));
        }
        
        // 身份证号
        Pattern idPattern = Pattern.compile("(\\d{17}[\\dXx])");
        Matcher idMatcher = idPattern.matcher(text);
        if (idMatcher.find()) {
            info.setIdNumber(idMatcher.group(1));
        }
        
        // 地址
        Pattern addrPattern = Pattern.compile("住址[：:]?\\s*([\\u4e00-\\u9fa5\\d]{5,30})");
        Matcher addrMatcher = addrPattern.matcher(text);
        if (addrMatcher.find()) {
            info.setAddress(addrMatcher.group(1));
        }
        
        return info;
    }

    @Override
    public BusinessCardInfo recognizeBusinessCard(String imagePath) {
        String text = recognizeImage(imagePath);
        
        BusinessCardInfo info = new BusinessCardInfo();
        
        // 姓名
        Pattern namePattern = Pattern.compile("([\\u4e00-\\u9fa5]{2,4})(?:\\s|　)+(?:先生|女士|经理|总监|老板)");
        Matcher nameMatcher = namePattern.matcher(text);
        if (nameMatcher.find()) {
            info.setName(nameMatcher.group(1));
        }
        
        // 电话
        Pattern phonePattern = Pattern.compile("(\\d{3,4}[-\\s]?\\d{7,8})");
        Matcher phoneMatcher = phonePattern.matcher(text);
        if (phoneMatcher.find()) {
            info.setPhone(phoneMatcher.group(1));
        }
        
        // 手机
        Pattern mobilePattern = Pattern.compile("(1[3-9]\\d{9})");
        Matcher mobileMatcher = mobilePattern.matcher(text);
        if (mobileMatcher.find()) {
            info.setMobile(mobileMatcher.group(1));
        }
        
        // 邮箱
        Pattern emailPattern = Pattern.compile("([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})");
        Matcher emailMatcher = emailPattern.matcher(text);
        if (emailMatcher.find()) {
            info.setEmail(emailMatcher.group(1));
        }
        
        // 公司
        Pattern companyPattern = Pattern.compile("(?:公司|企业|集团|有限公司|股份有限公司)[：:]?\\s*([\\u4e00-\\u9fa5a-zA-Z0-9]{5,30})");
        Matcher companyMatcher = companyPattern.matcher(text);
        if (companyMatcher.find()) {
            info.setCompany(companyMatcher.group(1));
        }
        
        return info;
    }

    /**
     * 获取图片文件
     */
    private File getImageFile(OcrRequest request) {
        if (request.getImagePath() != null) {
            return new File(request.getImagePath());
        }
        
        if (request.getImageBase64() != null) {
            try {
                byte[] bytes = java.util.Base64.getDecoder().decode(request.getImageBase64());
                String tempPath = System.getProperty("java.io.tmpdir");
                String tempFile = tempPath + File.separator + UUID.randomUUID() + ".png";
                try (java.io.FileOutputStream fos = new java.io.FileOutputStream(tempFile)) {
                    fos.write(bytes);
                }
                return new File(tempFile);
            } catch (Exception e) {
                log.error("Base64解码失败", e);
                return null;
            }
        }
        
        return null;
    }
}
