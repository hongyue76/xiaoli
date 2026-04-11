package com.xiaoli.legal.common.ocr.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * OCR配置
 */
@Configuration
@ConfigurationProperties(prefix = "ocr")
public class OcrProperties {

    /**
     * 是否启用
     */
    private boolean enabled = true;

    /**
     * 语言: chi_sim(简体中文), eng(英文), chi_tra(繁体中文)
     */
    private String language = "chi_sim+eng";

    /**
     * Tesseract数据路径
     */
    private String dataPath = "./tessdata";

    /**
     * DPI
     */
    private Integer dpi = 300;

    /**
     * 图片预处理
     */
    private boolean preprocess = true;

    /**
     * 图片清晰度阈值
     */
    private Integer threshold = 128;

    // Getters and Setters
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getDataPath() { return dataPath; }
    public void setDataPath(String dataPath) { this.dataPath = dataPath; }

    public Integer getDpi() { return dpi; }
    public void setDpi(Integer dpi) { this.dpi = dpi; }

    public boolean isPreprocess() { return preprocess; }
    public void setPreprocess(boolean preprocess) { this.preprocess = preprocess; }

    public Integer getThreshold() { return threshold; }
    public void setThreshold(Integer threshold) { this.threshold = threshold; }
}
