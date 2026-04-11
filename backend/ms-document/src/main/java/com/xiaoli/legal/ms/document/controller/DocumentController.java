package com.xiaoli.legal.ms.document.controller;

import com.xiaoli.legal.common.core.domain.PageResult;
import com.xiaoli.legal.common.core.domain.Result;
import com.xiaoli.legal.ms.document.model.dto.GenerateDocumentRequest;
import com.xiaoli.legal.ms.document.model.dto.GenerateDocumentResponse;
import com.xiaoli.legal.ms.document.model.vo.DocumentDetailVO;
import com.xiaoli.legal.ms.document.model.vo.TemplateVO;
import com.xiaoli.legal.ms.document.service.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 法律文书接口
 */
@RestController
@RequestMapping("/api/document")
public class DocumentController {

    private static final Logger log = LoggerFactory.getLogger(DocumentController.class);

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * 获取模板列表
     */
    @GetMapping("/templates")
    public Result<PageResult<TemplateVO>> getTemplateList(
            @RequestParam(required = false) String caseType,
            @RequestParam(required = false) String templateType,
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {
        PageResult<TemplateVO> result = documentService.getTemplateList(caseType, templateType, current, size);
        return Result.success(result);
    }

    /**
     * 获取所有模板
     */
    @GetMapping("/templates/all")
    public Result<List<TemplateVO>> getAllTemplates() {
        List<TemplateVO> result = documentService.getAllTemplates();
        return Result.success(result);
    }

    /**
     * 获取模板详情
     */
    @GetMapping("/templates/{id}")
    public Result<TemplateVO> getTemplateDetail(@PathVariable Long id) {
        TemplateVO result = documentService.getTemplateDetail(id);
        return Result.success(result);
    }

    /**
     * 生成文书
     */
    @PostMapping("/generate")
    public Result<GenerateDocumentResponse> generateDocument(
            @RequestBody @Validated GenerateDocumentRequest request,
            @RequestParam(defaultValue = "1") Long userId) {
        log.info("生成文书请求: templateId={}", request.getTemplateId());
        GenerateDocumentResponse result = documentService.generateDocument(request, userId);
        return Result.success(result);
    }

    /**
     * 获取文书详情
     */
    @GetMapping("/{id}")
    public Result<DocumentDetailVO> getDocumentDetail(@PathVariable Long id) {
        DocumentDetailVO result = documentService.getDocumentDetail(id);
        return Result.success(result);
    }

    /**
     * 获取我的文书列表
     */
    @GetMapping("/my")
    public Result<PageResult<DocumentDetailVO>> getMyDocuments(
            @RequestParam Long userId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {
        PageResult<DocumentDetailVO> result = documentService.getMyDocuments(userId, status, current, size);
        return Result.success(result);
    }

    /**
     * 更新文书
     */
    @PutMapping("/{id}")
    public Result<Void> updateDocument(
            @PathVariable Long id,
            @RequestBody String content) {
        documentService.updateDocument(id, content);
        return Result.success();
    }

    /**
     * 删除文书
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return Result.success();
    }

    /**
     * 导出PDF
     */
    @GetMapping("/{id}/export")
    public Result<String> exportPdf(@PathVariable Long id) {
        String pdfUrl = documentService.exportPdf(id);
        return Result.success(pdfUrl);
    }

    /**
     * 获取文书类型
     */
    @GetMapping("/types")
    public Result<List<String>> getDocumentTypes() {
        List<String> types = List.of(
                "PLAINTIFF",      // 起诉状
                "ANSWER",         // 答辩状
                "APPEAL",         // 上诉状
                "DEFENSE",        // 辩护词
                "PROXY",          // 代理词
                "CONTRACT",       // 合同协议
                "AGREEMENT",      // 补充协议
                "LETTER",         // 法律函
                "OPINION",        // 法律意见书
                "OTHER"           // 其他
        );
        return Result.success(types);
    }
}
