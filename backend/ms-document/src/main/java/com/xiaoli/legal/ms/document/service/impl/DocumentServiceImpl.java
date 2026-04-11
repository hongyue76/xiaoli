package com.xiaoli.legal.ms.document.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaoli.legal.common.core.domain.PageResult;
import com.xiaoli.legal.common.core.exception.BusinessException;
import com.xiaoli.legal.common.core.domain.ResultCode;
import com.xiaoli.legal.ms.document.mapper.DocumentTemplateMapper;
import com.xiaoli.legal.ms.document.mapper.LegalDocumentMapper;
import com.xiaoli.legal.ms.document.model.dto.GenerateDocumentRequest;
import com.xiaoli.legal.ms.document.model.dto.GenerateDocumentResponse;
import com.xiaoli.legal.ms.document.model.entity.DocumentTemplate;
import com.xiaoli.legal.ms.document.model.entity.LegalDocument;
import com.xiaoli.legal.ms.document.model.vo.DocumentDetailVO;
import com.xiaoli.legal.ms.document.model.vo.TemplateVO;
import com.xiaoli.legal.ms.document.service.DocumentService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 文书服务实现
 */
@Service
public class DocumentServiceImpl implements DocumentService {

    private static final Logger log = LoggerFactory.getLogger(DocumentServiceImpl.class);

    private final DocumentTemplateMapper templateMapper;
    private final LegalDocumentMapper documentMapper;
    private final Configuration freemarkerConfig;

    public DocumentServiceImpl(DocumentTemplateMapper templateMapper, LegalDocumentMapper documentMapper, Configuration freemarkerConfig) {
        this.templateMapper = templateMapper;
        this.documentMapper = documentMapper;
        this.freemarkerConfig = freemarkerConfig;
    }

    @Override
    public PageResult<TemplateVO> getTemplateList(String caseType, String templateType, Long current, Long size) {
        Page<DocumentTemplate> page = new Page<>(current, size);
        LambdaQueryWrapper<DocumentTemplate> wrapper = new LambdaQueryWrapper<DocumentTemplate>()
                .eq(StrUtil.isNotBlank(caseType), DocumentTemplate::getCaseType, caseType)
                .eq(StrUtil.isNotBlank(templateType), DocumentTemplate::getTemplateType, templateType)
                .eq(DocumentTemplate::getStatus, "PUBLISHED")
                .orderByDesc(DocumentTemplate::getUsageCount);

        Page<DocumentTemplate> result = templateMapper.selectPage(page, wrapper);

        List<TemplateVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(result.getCurrent(), result.getSize(), result.getTotal(), voList);
    }

    @Override
    public TemplateVO getTemplateDetail(Long templateId) {
        DocumentTemplate template = templateMapper.selectById(templateId);
        if (template == null) {
            throw new BusinessException(ResultCode.TEMPLATE_NOT_FOUND);
        }
        return convertToVO(template);
    }

    @Override
    public List<TemplateVO> getAllTemplates() {
        List<DocumentTemplate> templates = templateMapper.selectList(
                new LambdaQueryWrapper<DocumentTemplate>()
                        .eq(DocumentTemplate::getStatus, "PUBLISHED")
                        .orderByDesc(DocumentTemplate::getUsageCount)
        );
        return templates.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GenerateDocumentResponse generateDocument(GenerateDocumentRequest request, Long userId) {
        // 获取模板
        DocumentTemplate template = templateMapper.selectById(request.getTemplateId());
        if (template == null) {
            throw new BusinessException(ResultCode.TEMPLATE_NOT_FOUND);
        }

        // 填充模板
        String content = fillTemplate(template.getContent(), request.getData());

        // 保存文书
        LegalDocument document = new LegalDocument();
        document.setTitle(template.getName() + "-" + LocalDateTime.now().toString().substring(0, 10));
        document.setDocType(template.getTemplateType());
        document.setCaseId(request.getCaseId());
        document.setTemplateId(template.getId());
        document.setContent(content);
        document.setStatus("DRAFT");
        document.setUserId(userId);
        document.setCreateTime(LocalDateTime.now());
        document.setUpdateTime(LocalDateTime.now());
        documentMapper.insert(document);

        // 更新模板使用次数
        template.setUsageCount(template.getUsageCount() + 1);
        templateMapper.updateById(template);

        // 构建响应
        GenerateDocumentResponse response = GenerateDocumentResponse.builder()
                .documentId(document.getId())
                .title(document.getTitle())
                .content(content)
                .build();

        // AI辅助建议（异步执行，不阻塞主流程）
        if (Boolean.TRUE.equals(request.getAiAssist())) {
            generateAISuggestionsAsync(request, content)
                    .thenAccept(suggestions -> response.setAiSuggestions(suggestions));
        }

        return response;
    }

    @Override
    public DocumentDetailVO getDocumentDetail(Long documentId) {
        LegalDocument document = documentMapper.selectById(documentId);
        if (document == null) {
            throw new BusinessException(ResultCode.DOCUMENT_NOT_EXIST);
        }

        DocumentDetailVO vo = new DocumentDetailVO();
        vo.setId(document.getId());
        vo.setTitle(document.getTitle());
        vo.setDocType(document.getDocType());
        vo.setCaseId(document.getCaseId());
        vo.setTemplateId(document.getTemplateId());
        vo.setContent(document.getContent());
        vo.setPdfUrl(document.getPdfPath());
        vo.setStatus(document.getStatus());
        vo.setCreateTime(document.getCreateTime());
        vo.setUpdateTime(document.getUpdateTime());

        // 获取模板名称
        if (document.getTemplateId() != null) {
            DocumentTemplate template = templateMapper.selectById(document.getTemplateId());
            if (template != null) {
                vo.setTemplateName(template.getName());
            }
        }

        return vo;
    }

    @Override
    public PageResult<DocumentDetailVO> getMyDocuments(Long userId, String status, Long current, Long size) {
        Page<LegalDocument> page = new Page<>(current, size);
        LambdaQueryWrapper<LegalDocument> wrapper = new LambdaQueryWrapper<LegalDocument>()
                .eq(LegalDocument::getUserId, userId)
                .eq(StrUtil.isNotBlank(status), LegalDocument::getStatus, status)
                .orderByDesc(LegalDocument::getCreateTime);

        Page<LegalDocument> result = documentMapper.selectPage(page, wrapper);

        List<DocumentDetailVO> voList = result.getRecords().stream()
                .map(doc -> {
                    DocumentDetailVO vo = new DocumentDetailVO();
                    vo.setId(doc.getId());
                    vo.setTitle(doc.getTitle());
                    vo.setDocType(doc.getDocType());
                    vo.setCaseId(doc.getCaseId());
                    vo.setTemplateId(doc.getTemplateId());
                    vo.setContent(doc.getContent());
                    vo.setPdfUrl(doc.getPdfPath());
                    vo.setStatus(doc.getStatus());
                    vo.setCreateTime(doc.getCreateTime());
                    vo.setUpdateTime(doc.getUpdateTime());
                    return vo;
                })
                .collect(Collectors.toList());

        return PageResult.of(result.getCurrent(), result.getSize(), result.getTotal(), voList);
    }

    @Override
    public void updateDocument(Long documentId, String content) {
        LegalDocument document = documentMapper.selectById(documentId);
        if (document == null) {
            throw new BusinessException(ResultCode.DOCUMENT_NOT_EXIST);
        }

        document.setContent(content);
        document.setUpdateTime(LocalDateTime.now());
        documentMapper.updateById(document);
    }

    @Override
    public void deleteDocument(Long documentId) {
        documentMapper.deleteById(documentId);
    }

    @Override
    public String exportPdf(Long documentId) {
        // TODO: 实现PDF导出
        return null;
    }

    /**
     * 填充模板
     */
    private String fillTemplate(String templateContent, Map<String, Object> data) {
        try {
            freemarkerConfig.setClassicCompatible(true);
            Template template = new Template("document", new StringReader(templateContent), freemarkerConfig);
            StringWriter writer = new StringWriter();
            template.process(data, writer);
            return writer.toString();
        } catch (TemplateException | IOException e) {
            log.error("模板填充失败", e);
            throw new BusinessException("模板填充失败: " + e.getMessage());
        }
    }

    /**
     * 转换实体为VO
     */
    private TemplateVO convertToVO(DocumentTemplate template) {
        TemplateVO vo = new TemplateVO();
        vo.setId(template.getId());
        vo.setName(template.getName());
        vo.setTemplateType(template.getTemplateType());
        vo.setCaseType(template.getCaseType());
        vo.setDescription(template.getDescription());
        vo.setUsageCount(template.getUsageCount());
        vo.setCreateTime(template.getCreateTime());

        // 解析变量
        if (StrUtil.isNotBlank(template.getVariables())) {
            JSONArray variables = JSON.parseArray(template.getVariables());
            List<TemplateVO.Variable> variableList = new ArrayList<>();
            for (int i = 0; i < variables.size(); i++) {
                JSONObject obj = variables.getJSONObject(i);
                TemplateVO.Variable var = new TemplateVO.Variable();
                var.setName(obj.getString("name"));
                var.setLabel(obj.getString("label"));
                var.setType(obj.getString("type"));
                var.setRequired(obj.getBoolean("required"));
                var.setPlaceholder(obj.getString("placeholder"));
                var.setExample(obj.getString("example"));
                if (obj.containsKey("options")) {
                    var.setOptions(obj.getJSONArray("options").toJavaList(String.class));
                }
                variableList.add(var);
            }
            vo.setVariables(variableList);
        }

        return vo;
    }

    /**
     * 异步生成AI建议
     */
    @Async("asyncExecutor")
    public CompletableFuture<String> generateAISuggestionsAsync(GenerateDocumentRequest request, String content) {
        try {
            // TODO: 调用小理AI生成建议
            return CompletableFuture.completedFuture("建议检查文书内容的完整性和准确性。");
        } catch (Exception e) {
            log.error("生成AI建议失败", e);
            return CompletableFuture.completedFuture("");
        }
    }
}
