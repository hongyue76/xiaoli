package com.xiaoli.legal.core;

/**
 * 用户意图类型枚举
 * 定义系统支持的各类用户意图
 */
public enum IntentType {

    /**
     * 法律咨询 - 常规法律问题解答
     */
    CONSULT("法律咨询", "常规法律问题解答"),

    /**
     * 案例检索 - 搜索相关法律案例
     */
    CASE_SEARCH("案例检索", "搜索相关法律案例"),

    /**
     * 文书生成 - 生成法律文书
     */
    DOCUMENT("文书生成", "生成法律文书"),

    /**
     * 合同审查 - 审查合同内容
     */
    CONTRACT_REVIEW("合同审查", "审查合同内容"),

    /**
     * 案件分析 - 分析案件事实和法律适用
     */
    CASE_ANALYSIS("案件分析", "分析案件事实和法律适用"),

    /**
     * 证据分析 - 分析证据的合法性和证明力
     */
    EVIDENCE_ANALYSIS("证据分析", "分析证据的合法性和证明力"),

    /**
     * 司法决策 - 量刑建议、审判预测
     */
    DECISION("司法决策", "量刑建议、审判预测"),

    /**
     * 合规检查 - 企业合规审查
     */
    COMPLIANCE("合规检查", "企业合规审查"),

    /**
     * 未知意图 - 无法识别的用户意图
     */
    UNKNOWN("未知意图", "无法识别的用户意图");

    private final String displayName;
    private final String description;

    IntentType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
