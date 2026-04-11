package com.xiaoli.legal.common.ocr.service;

import com.xiaoli.legal.common.ocr.dto.OcrRequest;
import com.xiaoli.legal.common.ocr.dto.OcrResponse;

import java.util.List;

/**
 * OCR服务接口
 */
public interface OcrService {

    /**
     * 图片OCR识别
     *
     * @param request 请求
     * @return 识别结果
     */
    OcrResponse recognizeImage(OcrRequest request);

    /**
     * 简单图片识别
     *
     * @param imagePath 图片路径
     * @return 识别文本
     */
    String recognizeImage(String imagePath);

    /**
     * PDF OCR识别
     *
     * @param request 请求
     * @return 识别结果
     */
    OcrResponse recognizePdf(OcrRequest request);

    /**
     * 批量识别
     *
     * @param imagePaths 图片路径列表
     * @return 识别结果列表
     */
    List<OcrResponse> batchRecognize(List<String> imagePaths);

    /**
     * 身份证识别
     *
     * @param imagePath 图片路径
     * @return 身份证信息
     */
    IdCardInfo recognizeIdCard(String imagePath);

    /**
     * 名片识别
     *
     * @param imagePath 图片路径
     * @return 名片信息
     */
    BusinessCardInfo recognizeBusinessCard(String imagePath);

    /**
     * 身份证信息
     */
    class IdCardInfo {
        private String name;
        private String gender;
        private String ethnicity;
        private String birthDate;
        private String address;
        private String idNumber;
        private String issuingAuthority;
        private String validPeriod;

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
        public String getEthnicity() { return ethnicity; }
        public void setEthnicity(String ethnicity) { this.ethnicity = ethnicity; }
        public String getBirthDate() { return birthDate; }
        public void setBirthDate(String birthDate) { this.birthDate = birthDate; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getIdNumber() { return idNumber; }
        public void setIdNumber(String idNumber) { this.idNumber = idNumber; }
        public String getIssuingAuthority() { return issuingAuthority; }
        public void setIssuingAuthority(String issuingAuthority) { this.issuingAuthority = issuingAuthority; }
        public String getValidPeriod() { return validPeriod; }
        public void setValidPeriod(String validPeriod) { this.validPeriod = validPeriod; }
    }

    /**
     * 名片信息
     */
    class BusinessCardInfo {
        private String name;
        private String title;
        private String company;
        private String phone;
        private String mobile;
        private String email;
        private String address;
        private String website;

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getCompany() { return company; }
        public void setCompany(String company) { this.company = company; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getMobile() { return mobile; }
        public void setMobile(String mobile) { this.mobile = mobile; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getWebsite() { return website; }
        public void setWebsite(String website) { this.website = website; }
    }
}
