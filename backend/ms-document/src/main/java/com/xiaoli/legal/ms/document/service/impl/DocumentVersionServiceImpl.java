package com.xiaoli.legal.ms.document.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xiaoli.legal.ms.document.mapper.DocumentVersionMapper;
import com.xiaoli.legal.ms.document.model.dto.VersionCompareResult;
import com.xiaoli.legal.ms.document.model.entity.DocumentVersion;
import com.xiaoli.legal.ms.document.model.vo.DocumentVersionVO;
import com.xiaoli.legal.ms.document.service.DocumentVersionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文档版本服务实现
 */
@Service
public class DocumentVersionServiceImpl implements DocumentVersionService {

    private static final Logger log = LoggerFactory.getLogger(DocumentVersionServiceImpl.class);

    private final DocumentVersionMapper versionMapper;

    public DocumentVersionServiceImpl(DocumentVersionMapper versionMapper) {
        this.versionMapper = versionMapper;
    }

    private static final int MAX_AUTO_SAVE_VERSIONS = 10;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DocumentVersion saveVersion(Long documentId, String content, String description, String changeType, Long createBy) {
        // 获取当前最新版本号
        Integer maxVersion = getMaxVersion(documentId);
        int newVersion = (maxVersion == null ? 0 : maxVersion) + 1;

        // 计算差异摘要
        String diffSummary = calculateDiffSummary(documentId, content);

        DocumentVersion version = new DocumentVersion();
        version.setDocumentId(documentId);
        version.setVersion(newVersion);
        version.setContent(content);
        version.setDescription(description);
        version.setChangeType(changeType);
        version.setDiffSummary(diffSummary);
        version.setCreateBy(createBy);
        version.setCreateTime(LocalDateTime.now());

        versionMapper.insert(version);

        // 清理自动保存的历史版本
        if ("AUTO".equals(changeType)) {
            cleanOldAutoSaveVersions(documentId);
        }

        return version;
    }

    @Override
    public List<DocumentVersionVO> getVersionList(Long documentId) {
        List<DocumentVersion> versions = versionMapper.selectList(
                new LambdaQueryWrapper<DocumentVersion>()
                        .eq(DocumentVersion::getDocumentId, documentId)
                        .orderByDesc(DocumentVersion::getVersion)
        );

        return versions.stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public DocumentVersion getVersionDetail(Long versionId) {
        return versionMapper.selectById(versionId);
    }

    @Override
    public VersionCompareResult compareVersions(Long documentId, Integer oldVersion, Integer newVersion) {
        DocumentVersion oldVer = getVersionByDocumentAndVersion(documentId, oldVersion);
        DocumentVersion newVer = getVersionByDocumentAndVersion(documentId, newVersion);

        if (oldVer == null || newVer == null) {
            throw new RuntimeException("版本不存在");
        }

        return compareContent(oldVer.getContent(), newVer.getContent(), documentId, oldVersion, newVersion);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String rollbackToVersion(Long documentId, Integer targetVersion) {
        DocumentVersion targetVer = getVersionByDocumentAndVersion(documentId, targetVersion);
        if (targetVer == null) {
            throw new RuntimeException("目标版本不存在");
        }

        // 保存当前版本作为新版本
        Integer maxVersion = getMaxVersion(documentId);
        DocumentVersion currentVer = getVersionByDocumentAndVersion(documentId, maxVersion);

        if (currentVer != null) {
            // 保存当前内容为新版本
            saveVersion(documentId, currentVer.getContent(), "回滚操作前", "MANUAL", null);
        }

        // 更新为回滚目标版本的内容
        return targetVer.getContent();
    }

    @Override
    @Async("asyncExecutor")
    @Transactional(rollbackFor = Exception.class)
    public void autoSave(Long documentId, String content) {
        // 检查内容是否有变化
        Integer maxVersion = getMaxVersion(documentId);
        if (maxVersion != null && maxVersion > 0) {
            DocumentVersion latest = getVersionByDocumentAndVersion(documentId, maxVersion);
            if (latest != null && content.equals(latest.getContent())) {
                return; // 内容相同，不保存
            }
        }

        // 自动保存版本
        saveVersion(documentId, content, "自动保存", "AUTO", null);
    }

    private Integer getMaxVersion(Long documentId) {
        List<DocumentVersion> versions = versionMapper.selectList(
                new LambdaQueryWrapper<DocumentVersion>()
                        .eq(DocumentVersion::getDocumentId, documentId)
                        .orderByDesc(DocumentVersion::getVersion)
                        .last("LIMIT 1")
        );
        return versions.isEmpty() ? null : versions.get(0).getVersion();
    }

    private DocumentVersion getVersionByDocumentAndVersion(Long documentId, Integer version) {
        return versionMapper.selectOne(
                new LambdaQueryWrapper<DocumentVersion>()
                        .eq(DocumentVersion::getDocumentId, documentId)
                        .eq(DocumentVersion::getVersion, version)
        );
    }

    private void cleanOldAutoSaveVersions(Long documentId) {
        // 只保留最近N个自动保存版本
        List<DocumentVersion> autoVersions = versionMapper.selectList(
                new LambdaQueryWrapper<DocumentVersion>()
                        .eq(DocumentVersion::getDocumentId, documentId)
                        .eq(DocumentVersion::getChangeType, "AUTO")
                        .orderByDesc(DocumentVersion::getVersion)
        );

        if (autoVersions.size() > MAX_AUTO_SAVE_VERSIONS) {
            List<Long> toDelete = autoVersions.stream()
                    .skip(MAX_AUTO_SAVE_VERSIONS)
                    .map(DocumentVersion::getId)
                    .collect(Collectors.toList());

            if (!toDelete.isEmpty()) {
                versionMapper.deleteBatchIds(toDelete);
            }
        }
    }

    private String calculateDiffSummary(Long documentId, String newContent) {
        Integer maxVersion = getMaxVersion(documentId);
        if (maxVersion == null || maxVersion == 0) {
            return "初始版本";
        }

        DocumentVersion oldVer = getVersionByDocumentAndVersion(documentId, maxVersion);
        if (oldVer == null) {
            return "初始版本";
        }

        String[] oldLines = oldVer.getContent().split("\n");
        String[] newLines = newContent.split("\n");

        int added = 0, deleted = 0;
        for (String line : newLines) {
            if (!Arrays.asList(oldLines).contains(line)) {
                added++;
            }
        }
        for (String line : oldLines) {
            if (!Arrays.asList(newLines).contains(line)) {
                deleted++;
            }
        }

        StringBuilder sb = new StringBuilder();
        if (added > 0) sb.append("+").append(added).append("行");
        if (deleted > 0) sb.append("-").append(deleted).append("行");
        return sb.length() > 0 ? sb.toString() : "无变化";
    }

    private VersionCompareResult compareContent(String oldContent, String newContent,
                                                Long documentId, Integer oldVersion, Integer newVersion) {
        String[] oldLines = oldContent != null ? oldContent.split("\n") : new String[0];
        String[] newLines = newContent != null ? newContent.split("\n") : new String[0];

        // 使用简单的行对比算法
        List<VersionCompareResult.DiffLine> diffLines = new ArrayList<>();
        int oldIdx = 0, newIdx = 0;

        while (oldIdx < oldLines.length || newIdx < newLines.length) {
            if (oldIdx >= oldLines.length) {
                // 新版本中有新增行
                diffLines.add(VersionCompareResult.DiffLine.builder()
                        .oldLineNo(null)
                        .newLineNo(newIdx + 1)
                        .oldContent(null)
                        .newContent(newLines[newIdx])
                        .type("ADD")
                        .build());
                newIdx++;
            } else if (newIdx >= newLines.length) {
                // 新版本中有删除的行
                diffLines.add(VersionCompareResult.DiffLine.builder()
                        .oldLineNo(oldIdx + 1)
                        .newLineNo(null)
                        .oldContent(oldLines[oldIdx])
                        .newContent(null)
                        .type("DELETE")
                        .build());
                oldIdx++;
            } else if (oldLines[oldIdx].equals(newLines[newIdx])) {
                // 相同行
                diffLines.add(VersionCompareResult.DiffLine.builder()
                        .oldLineNo(oldIdx + 1)
                        .newLineNo(newIdx + 1)
                        .oldContent(oldLines[oldIdx])
                        .newContent(newLines[newIdx])
                        .type("CONTEXT")
                        .build());
                oldIdx++;
                newIdx++;
            } else {
                // 查找是否在其他位置存在
                int foundIdx = -1;
                for (int i = newIdx + 1; i < newLines.length; i++) {
                    if (oldLines[oldIdx].equals(newLines[i])) {
                        foundIdx = i;
                        break;
                    }
                }

                if (foundIdx >= 0) {
                    // 新版本中此行被修改或移动
                    diffLines.add(VersionCompareResult.DiffLine.builder()
                            .oldLineNo(oldIdx + 1)
                            .newLineNo(newIdx + 1)
                            .oldContent(oldLines[oldIdx])
                            .newContent(newLines[newIdx])
                            .type("MODIFY")
                            .build());
                    oldIdx++;
                    newIdx++;
                } else {
                    // 被删除
                    diffLines.add(VersionCompareResult.DiffLine.builder()
                            .oldLineNo(oldIdx + 1)
                            .newLineNo(null)
                            .oldContent(oldLines[oldIdx])
                            .newContent(null)
                            .type("DELETE")
                            .build());
                    oldIdx++;
                }
            }
        }

        // 统计
        int addedLines = (int) diffLines.stream().filter(l -> "ADD".equals(l.getType())).count();
        int deletedLines = (int) diffLines.stream().filter(l -> "DELETE".equals(l.getType())).count();
        int modifiedLines = (int) diffLines.stream().filter(l -> "MODIFY".equals(l.getType())).count();
        int unchangedLines = (int) diffLines.stream().filter(l -> "CONTEXT".equals(l.getType())).count();

        StringBuilder summary = new StringBuilder();
        if (addedLines > 0) summary.append("新增").append(addedLines).append("行");
        if (modifiedLines > 0) {
            if (summary.length() > 0) summary.append("，");
            summary.append("修改").append(modifiedLines).append("行");
        }
        if (deletedLines > 0) {
            if (summary.length() > 0) summary.append("，");
            summary.append("删除").append(deletedLines).append("行");
        }

        String diffType;
        if (addedLines > 0 && deletedLines == 0) {
            diffType = "ADDED";
        } else if (deletedLines > 0 && addedLines == 0) {
            diffType = "DELETED";
        } else {
            diffType = "MODIFIED";
        }

        return VersionCompareResult.builder()
                .documentId(documentId)
                .oldVersion(oldVersion)
                .newVersion(newVersion)
                .diffType(diffType)
                .summary(summary.length() > 0 ? summary.toString() : "无变化")
                .diffLines(diffLines)
                .stats(VersionCompareResult.DiffStats.builder()
                        .addedLines(addedLines)
                        .deletedLines(deletedLines)
                        .modifiedLines(modifiedLines)
                        .unchangedLines(unchangedLines)
                        .build())
                .build();
    }

    private DocumentVersionVO toVO(DocumentVersion version) {
        String content = version.getContent();
        String preview = content != null && content.length() > 100
                ? content.substring(0, 100) + "..."
                : content;

        return DocumentVersionVO.builder()
                .id(version.getId())
                .documentId(version.getDocumentId())
                .version(version.getVersion())
                .description(version.getDescription())
                .changeType(version.getChangeType())
                .diffSummary(version.getDiffSummary())
                .createBy(version.getCreateBy())
                .createTime(version.getCreateTime())
                .contentPreview(preview)
                .build();
    }
}
