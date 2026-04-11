package com.xiaoli.legal.common.core.domain;

/**
 * 响应状态码枚举
 */
public enum ResultCode {

    // 成功
    SUCCESS(200, "操作成功"),

    // 客户端错误
    FAIL(400, "操作失败"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),
    RATE_LIMIT(429, "请求过于频繁，请稍后再试"),

    // 服务端错误
    INTERNAL_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务不可用"),

    // 业务错误
    USER_NOT_EXIST(1001, "用户不存在"),
    USER_DISABLED(1002, "用户已被禁用"),
    PASSWORD_ERROR(1003, "密码错误"),
    TOKEN_EXPIRED(1004, "Token已过期"),
    TOKEN_INVALID(1005, "Token无效"),

    // AI服务错误
    AI_SERVICE_ERROR(2001, "AI服务调用失败"),
    AI_REQUEST_TIMEOUT(2002, "AI请求超时"),
    AI_RATE_LIMIT(2003, "请求频率超限"),
    AI_INVALID_KEY(2004, "API密钥无效"),
    AI_MODEL_NOT_FOUND(2005, "模型不存在"),

    // 业务错误
    CASE_NOT_EXIST(3001, "案件不存在"),
    DOCUMENT_NOT_EXIST(3002, "文书不存在"),
    CONTRACT_NOT_EXIST(3003, "合同不存在"),
    TEMPLATE_NOT_FOUND(3004, "模板不存在"),
    KNOWLEDGE_NOT_FOUND(3005, "知识不存在");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
