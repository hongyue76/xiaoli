package com.xiaoli.legal.common.core.constant;

/**
 * 小理AI常量定义
 */
public class XiaoliConstants {

    /**
     * 小理AI API基础URL
     */
    public static final String XIAOLI_API_BASE_URL = "https://api.xiaoli.ai";

    /**
     * 默认模型
     */
    public static final String DEFAULT_MODEL = "xiaoli-legal";

    /**
     * 对话接口路径
     */
    public static final String CHAT_PATH = "/v1/chat/completions";

    /**
     * Embedding接口路径
     */
    public static final String EMBEDDING_PATH = "/v1/embeddings";

    /**
     * 请求超时时间（毫秒）
     */
    public static final int TIMEOUT = 30000;

    /**
     * 最大重试次数
     */
    public static final int MAX_RETRIES = 3;

    private XiaoliConstants() {}
}
