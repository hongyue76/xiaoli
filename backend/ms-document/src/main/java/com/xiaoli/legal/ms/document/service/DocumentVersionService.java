package com.xiaoli.legal.ms.document.service;

import com.xiaoli.legal.ms.document.model.dto.VersionCompareResult;
import com.xiaoli.legal.ms.document.model.entity.DocumentVersion;
import com.xiaoli.legal.ms.document.model.vo.DocumentVersionVO;

import java.util.List;

/**
 * 文档版本服务
 */
public interface DocumentVersionService {

    /**
     * 保存版本
     */
    DocumentVersion saveVersion(Long documentId, String content, String description, String changeType, Long createBy);

    /**
     * 获取版本列表
     */
    List<DocumentVersionVO> getVersionList(Long documentId);

    /**
     * 获取版本详情
     */
    DocumentVersion getVersionDetail(Long versionId);

    /**
     * 比对两个版本
     */
    VersionCompareResult compareVersions(Long documentId, Integer oldVersion, Integer newVersion);

    /**
     * 回滚到指定版本
     */
    String rollbackToVersion(Long documentId, Integer targetVersion);

    /**
     * 自动保存版本
     */
    void autoSave(Long documentId, String content);
}
