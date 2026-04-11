package com.xiaoli.legal.common.ai.dto;

/**
 * TTS请求
 */
public class TtsRequest {

    /**
     * 模型
     */
    private String model;

    /**
     * 输入文本
     */
    private String input;

    /**
     * 音色
     */
    private String voice = "affectate_female";

    /**
     * 语速(0.5-2.0)
     */
    private Double speed = 1.0;

    /**
     * 音量 (0-100)
     */
    private Integer volume = 80;

    /**
     * 输出格式
     */
    private String format = "mp3";

    /**
     * 可用音色
     */
    public static final String[] AVAILABLE_VOICES = {
            "affectate_female",   // 柔和女声
            "standard_male",      // 标准男声
            "mature_male",        // 成熟男声
            "warm_female",       // 温暖女声
            "energetic_male",    // 活力男声
            "gentle_female"      // 柔美女声
    };

    // Getters and Setters
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getInput() { return input; }
    public void setInput(String input) { this.input = input; }

    public String getVoice() { return voice; }
    public void setVoice(String voice) { this.voice = voice; }

    public Double getSpeed() { return speed; }
    public void setSpeed(Double speed) { this.speed = speed; }

    public Integer getVolume() { return volume; }
    public void setVolume(Integer volume) { this.volume = volume; }

    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String model;
        private String input;
        private String voice = "affectate_female";
        private Double speed = 1.0;
        private Integer volume = 80;
        private String format = "mp3";

        public Builder model(String model) { this.model = model; return this; }
        public Builder input(String input) { this.input = input; return this; }
        public Builder voice(String voice) { this.voice = voice; return this; }
        public Builder speed(Double speed) { this.speed = speed; return this; }
        public Builder volume(Integer volume) { this.volume = volume; return this; }
        public Builder format(String format) { this.format = format; return this; }

        public TtsRequest build() {
            TtsRequest req = new TtsRequest();
            req.model = this.model;
            req.input = this.input;
            req.voice = this.voice;
            req.speed = this.speed;
            req.volume = this.volume;
            req.format = this.format;
            return req;
        }
    }
}
