package com.xiaoli.legal.common.pdf.service.impl;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.xiaoli.legal.common.pdf.dto.PdfRequest;
import com.xiaoli.legal.common.pdf.service.PdfService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;
import java.nio.file.Files;
import java.util.Base64;
import java.util.UUID;

/**
 * PDF服务实现
 */
@Service
public class PdfServiceImpl implements PdfService {

    private static final Logger log = LoggerFactory.getLogger(PdfServiceImpl.class);

    @Value("${pdf.output-dir:./uploads/pdf}")
    private String outputDir;

    @Override
    public String htmlToPdf(PdfRequest request) {
        try {
            // 确保输出目录存在
            File dir = new File(outputDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 生成文件名
            String fileName = UUID.randomUUID().toString() + ".pdf";
            String outputPath = outputDir + "/" + fileName;

            // HTML转PDF
            ITextRenderer renderer = new ITextRenderer();
            
            // 添加字体支持中文
            ITextFontResolver fontResolver = renderer.getFontResolver();
            try {
                // 尝试添加系统字体
                String[] fontPaths = {
                        "C:/Windows/Fonts/simsun.ttc",  // 宋体
                        "/usr/share/fonts/truetype/wqy/wqy-microhei.ttc"  // Linux
                };
                for (String fontPath : fontPaths) {
                    File fontFile = new File(fontPath);
                    if (fontFile.exists()) {
                        fontResolver.addFont(fontPath, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
                    }
                }
            } catch (Exception e) {
                log.warn("添加中文字体失败，使用默认字体", e);
            }

            // 设置HTML内容
            String htmlContent = buildHtmlContent(request);
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();

            // 输出PDF
            OutputStream os = new FileOutputStream(outputPath);
            renderer.createPDF(os);
            os.close();

            log.info("HTML转PDF完成: {}", outputPath);
            return outputPath;

        } catch (Exception e) {
            log.error("HTML转PDF失败", e);
            throw new RuntimeException("HTML转PDF失败: " + e.getMessage());
        }
    }

    @Override
    public byte[] htmlToPdfBytes(PdfRequest request) {
        try {
            // HTML转PDF
            ITextRenderer renderer = new ITextRenderer();
            
            // 添加字体支持中文
            ITextFontResolver fontResolver = renderer.getFontResolver();
            try {
                String[] fontPaths = {
                        "C:/Windows/Fonts/simsun.ttc",
                        "/usr/share/fonts/truetype/wqy/wqy-microhei.ttc"
                };
                for (String fontPath : fontPaths) {
                    File fontFile = new File(fontPath);
                    if (fontFile.exists()) {
                        fontResolver.addFont(fontPath, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
                    }
                }
            } catch (Exception e) {
                log.warn("添加中文字体失败", e);
            }

            // 设置HTML内容
            String htmlContent = buildHtmlContent(request);
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();

            // 输出到字节数组
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            renderer.createPDF(baos);
            baos.close();

            log.info("HTML转PDF字节完成");
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("HTML转PDF字节失败", e);
            throw new RuntimeException("HTML转PDF字节失败: " + e.getMessage());
        }
    }

    @Override
    public String htmlToPdfBase64(PdfRequest request) {
        byte[] pdfBytes = htmlToPdfBytes(request);
        return Base64.getEncoder().encodeToString(pdfBytes);
    }

    /**
     * 构建HTML内容
     */
    private String buildHtmlContent(PdfRequest request) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html><head><meta charset='UTF-8'>");
        html.append("<style>");
        html.append("body { font-family: 'SimSun', sans-serif; margin: 40px; }");
        html.append("h1 { color: #333; border-bottom: 2px solid #333; padding-bottom: 10px; }");
        html.append("h2 { color: #666; margin-top: 20px; }");
        html.append("p { line-height: 1.8; text-align: justify; }");
        html.append(".content { margin: 20px 0; }");
        html.append(".footer { margin-top: 40px; font-size: 12px; color: #999; }");
        html.append("</style>");
        html.append("</head><body>");

        if (request.getTitle() != null && !request.getTitle().isEmpty()) {
            html.append("<h1>").append(escapeHtml(request.getTitle())).append("</h1>");
        }

        if (request.getContent() != null) {
            html.append("<div class='content'>").append(escapeHtml(request.getContent())).append("</div>");
        }

        html.append("<div class='footer'>生成时间: ").append(java.time.LocalDateTime.now()).append("</div>");
        html.append("</body></html>");

        return html.toString();
    }

    /**
     * HTML转义
     */
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;")
                   .replace("\n", "<br/>");
    }

    @Override
    public String textToPdf(String content, String title) {
        PdfRequest request = new PdfRequest();
        request.setTitle(title);
        request.setContent(content);
        return htmlToPdf(request);
    }

    @Override
    public String generateFromTemplate(String templatePath, Object data) {
        throw new UnsupportedOperationException("模板生成PDF暂未实现");
    }

    @Override
    public String addWatermark(String pdfPath, String watermark) {
        throw new UnsupportedOperationException("添加水印暂未实现");
    }

    @Override
    public String merge(String[] pdfPaths, String outputPath) {
        throw new UnsupportedOperationException("PDF合并暂未实现");
    }

    @Override
    public String[] split(String pdfPath, String outputDir) {
        throw new UnsupportedOperationException("PDF拆分暂未实现");
    }
}
