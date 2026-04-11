package com.xiaoli.legal.ms.document.service;

import com.xiaoli.legal.common.core.domain.PageResult;
import com.xiaoli.legal.ms.document.model.dto.GenerateDocumentRequest;
import com.xiaoli.legal.ms.document.model.dto.GenerateDocumentResponse;
import com.xiaoli.legal.ms.document.model.entity.DocumentTemplate;
import com.xiaoli.legal.ms.document.model.entity.LegalDocument;
import com.xiaoli.legal.ms.document.model.vo.DocumentDetailVO;
import com.xiaoli.legal.ms.document.model.vo.TemplateVO;

import java.util.List;

/**
 * 文书服务接口
 */
public interface DocumentService {

    /**
     * 获取模板列表
     */
    PageResult<TemplateVO> getTemplateList(String caseType, String templateType, Long current, Long size);

    /**
     * 获取模板详情
     */
    TemplateVO getTemplateDetail(Long templateId);

    /**
     * 获取所有可用模板
     */
    List<TemplateVO> getAllTemplates();

    /**
     * 生成文书
     */
    GenerateDocumentResponse generateDocument(GenerateDocumentRequest request, Long userId);

    /**
     * 获取文书详情
     */
    DocumentDetailVO getDocumentDetail(Long documentId);

    /**
     * 获取我的文书列表
     */
    PageResult<DocumentDetailVO> getMyDocuments(Long userId, String status, Long current, Long size);

    /**
     * 更新文书
     */
    void updateDocument(Long documentId, String content);

    /**
     * 删除文书
     */
    void deleteDocument(Long documentId);

    /**
     * 导出PDF
     */
    String exportPdf(Long documentId);
}
