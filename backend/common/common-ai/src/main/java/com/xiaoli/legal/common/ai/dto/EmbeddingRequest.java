package com.xiaoli.legal.common.ai.dto;

/**
 * Embedding请求
 */
public class EmbeddingRequest {

    /**
     * 模型
     */
    private String model;

    /**
     * 输入文本(单条)
     */
    private String input;

    /**
     * 输入文本列表(批量)
     */
    private String[] inputs;

    /**
     * 额外参数
     */
    private Object extraParams;

    // Getters and Setters
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getInput() { return input; }
    public void setInput(String input) { this.input = input; }

    public String[] getInputs() { return inputs; }
    public void setInputs(String[] inputs) { this.inputs = inputs; }

    public Object getExtraParams() { return extraParams; }
    public void setExtraParams(Object extraParams) { this.extraParams = extraParams; }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String model;
        private String input;
        private String[] inputs;
        private Object extraParams;

        public Builder model(String model) { this.model = model; return this; }
        public Builder input(String input) { this.input = input; return this; }
        public Builder inputs(String[] inputs) { this.inputs = inputs; return this; }
        public Builder extraParams(Object extraParams) { this.extraParams = extraParams; return this; }

        public EmbeddingRequest build() {
            EmbeddingRequest req = new EmbeddingRequest();
            req.model = this.model;
            req.input = this.input;
            req.inputs = this.inputs;
            req.extraParams = this.extraParams;
            return req;
        }
    }
}
