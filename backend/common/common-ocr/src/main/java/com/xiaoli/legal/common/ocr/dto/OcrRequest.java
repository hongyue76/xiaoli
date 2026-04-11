package com.xiaoli.legal.common.ocr.dto;

/**
 * OCR识别请求
 */
public class OcrRequest {

    /**
     * 图片路径
     */
    private String imagePath;

    /**
     * 图片Base64
     */
    private String imageBase64;

    /**
     * PDF路径
     */
    private String pdfPath;

    /**
     * PDF页码(从1开始)
     */
    private Integer pageNumber = 1;

    /**
     * 语言
     */
    private String language;

    /**
     * 是否需要坐标
     */
    private boolean withCoordinates = false;

    // Getters and Setters
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getImageBase64() { return imageBase64; }
    public void setImageBase64(String imageBase64) { this.imageBase64 = imageBase64; }

    public String getPdfPath() { return pdfPath; }
    public void setPdfPath(String pdfPath) { this.pdfPath = pdfPath; }

    public Integer getPageNumber() { return pageNumber; }
    public void setPageNumber(Integer pageNumber) { this.pageNumber = pageNumber; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public boolean isWithCoordinates() { return withCoordinates; }
    public void setWithCoordinates(boolean withCoordinates) { this.withCoordinates = withCoordinates; }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String imagePath;
        private String imageBase64;
        private String pdfPath;
        private Integer pageNumber = 1;
        private String language;
        private boolean withCoordinates = false;

        public Builder imagePath(String imagePath) { this.imagePath = imagePath; return this; }
        public Builder imageBase64(String imageBase64) { this.imageBase64 = imageBase64; return this; }
        public Builder pdfPath(String pdfPath) { this.pdfPath = pdfPath; return this; }
        public Builder pageNumber(Integer pageNumber) { this.pageNumber = pageNumber; return this; }
        public Builder language(String language) { this.language = language; return this; }
        public Builder withCoordinates(boolean withCoordinates) { this.withCoordinates = withCoordinates; return this; }

        public OcrRequest build() {
            OcrRequest request = new OcrRequest();
            request.setImagePath(imagePath);
            request.setImageBase64(imageBase64);
            request.setPdfPath(pdfPath);
            request.setPageNumber(pageNumber);
            request.setLanguage(language);
            request.setWithCoordinates(withCoordinates);
            return request;
        }
    }
}
