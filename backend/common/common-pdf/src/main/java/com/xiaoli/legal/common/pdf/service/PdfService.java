package com.xiaoli.legal.common.pdf.service;

import com.xiaoli.legal.common.pdf.dto.PdfRequest;

import java.io.File;
import java.io.InputStream;

/**
 * PDF服务接口
 */
public interface PdfService {

    /**
     * HTML转PDF
     *
     * @param request 请求
     * @return PDF文件路径
     */
    String htmlToPdf(PdfRequest request);

    /**
     * HTML转PDF (Base64)
     *
     * @param request 请求
     * @return Base64编码的PDF
     */
    String htmlToPdfBase64(PdfRequest request);

    /**
     * HTML转PDF (字节数组)
     *
     * @param request 请求
     * @return PDF字节数组
     */
    byte[] htmlToPdfBytes(PdfRequest request);

    /**
     * 文本转PDF
     *
     * @param content 文本内容
     * @param title   标题
     * @return PDF文件路径
     */
    String textToPdf(String content, String title);

    /**
     * 模板生成PDF
     *
     * @param templatePath 模板路径
     * @param data        数据
     * @return PDF文件路径
     */
    String generateFromTemplate(String templatePath, Object data);

    /**
     * 添加水印
     *
     * @param pdfPath   源PDF路径
     * @param watermark 水印文本
     * @return 带水印的PDF路径
     */
    String addWatermark(String pdfPath, String watermark);

    /**
     * PDF合并
     *
     * @param pdfPaths  PDF文件列表
     * @param outputPath 输出路径
     * @return 合并后的PDF路径
     */
    String merge(String[] pdfPaths, String outputPath);

    /**
     * PDF拆分
     *
     * @param pdfPath    源PDF路径
     * @param outputDir  输出目录
     * @return 拆分后的PDF路径数组
     */
    String[] split(String pdfPath, String outputDir);
}
