package com.xiaoli.legal.common.ai.dto;

import java.util.List;

/**
 * Embedding响应
 */
public class EmbeddingResponse {

    /**
     * 响应ID
     */
    private String id;

    /**
     * 模型
     */
    private String model;

    /**
     * 向量数据列表
     */
    private List<EmbeddingData> data;

    /**
     * 用量统计
     */
    private Usage usage;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public List<EmbeddingData> getData() { return data; }
    public void setData(List<EmbeddingData> data) { this.data = data; }

    public Usage getUsage() { return usage; }
    public void setUsage(Usage usage) { this.usage = usage; }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String id;
        private String model;
        private List<EmbeddingData> data;
        private Usage usage;

        public Builder id(String id) { this.id = id; return this; }
        public Builder model(String model) { this.model = model; return this; }
        public Builder data(List<EmbeddingData> data) { this.data = data; return this; }
        public Builder usage(Usage usage) { this.usage = usage; return this; }

        public EmbeddingResponse build() {
            EmbeddingResponse resp = new EmbeddingResponse();
            resp.id = this.id;
            resp.model = this.model;
            resp.data = this.data;
            resp.usage = this.usage;
            return resp;
        }
    }

    public static class EmbeddingData {
        /**
         * 索引
         */
        private Integer index;

        /**
         * 向量值
         */
        private float[] embedding;

        public Integer getIndex() { return index; }
        public void setIndex(Integer index) { this.index = index; }

        public float[] getEmbedding() { return embedding; }
        public void setEmbedding(float[] embedding) { this.embedding = embedding; }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private Integer index;
            private float[] embedding;

            public Builder index(Integer index) { this.index = index; return this; }
            public Builder embedding(float[] embedding) { this.embedding = embedding; return this; }

            public EmbeddingData build() {
                EmbeddingData data = new EmbeddingData();
                data.index = this.index;
                data.embedding = this.embedding;
                return data;
            }
        }
    }

    public static class Usage {
        /**
         * 输入token
         */
        private Integer promptTokens;

        /**
         * 总token
         */
        private Integer totalTokens;

        public Integer getPromptTokens() { return promptTokens; }
        public void setPromptTokens(Integer promptTokens) { this.promptTokens = promptTokens; }

        public Integer getTotalTokens() { return totalTokens; }
        public void setTotalTokens(Integer totalTokens) { this.totalTokens = totalTokens; }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private Integer promptTokens;
            private Integer totalTokens;

            public Builder promptTokens(Integer promptTokens) { this.promptTokens = promptTokens; return this; }
            public Builder totalTokens(Integer totalTokens) { this.totalTokens = totalTokens; return this; }

            public Usage build() {
                Usage u = new Usage();
                u.promptTokens = this.promptTokens;
                u.totalTokens = this.totalTokens;
                return u;
            }
        }
    }
}
