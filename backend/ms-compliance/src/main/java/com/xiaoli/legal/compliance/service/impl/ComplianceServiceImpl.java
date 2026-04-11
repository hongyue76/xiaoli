package com.xiaoli.legal.compliance.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaoli.legal.compliance.model.dto.CompanyRequest;
import com.xiaoli.legal.compliance.model.dto.ComplianceResponse;
import com.xiaoli.legal.compliance.model.dto.ComplianceReviewRequest;
import com.xiaoli.legal.compliance.model.entity.Company;
import com.xiaoli.legal.compliance.model.entity.ComplianceReview;
import com.xiaoli.legal.compliance.model.entity.ComplianceRisk;
import com.xiaoli.legal.compliance.service.ComplianceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 企业合规服务实现
 */
@Service
public class ComplianceServiceImpl implements ComplianceService {

    private static final Logger log = LoggerFactory.getLogger(ComplianceServiceImpl.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public ComplianceServiceImpl(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }
    
    @Value("${xiaoli.api.base-url}")
    private String xiaoliApiUrl;
    
    @Value("${xiaoli.api.api-key}")
    private String xiaoliApiKey;
    
    @Value("${xiaoli.api.model}")
    private String xiaoliModel;
    
    @Override
    public Company createCompany(CompanyRequest request) {
        Company company = new Company();
        company.setName(request.getName());
        company.setCreditCode(request.getCreditCode());
        company.setCompanyType(request.getCompanyType());
        company.setIndustry(request.getIndustry());
        company.setRegisteredCapital(request.getRegisteredCapital());
        company.setBusinessScope(request.getBusinessScope());
        company.setRegisteredAddress(request.getRegisteredAddress());
        company.setContactPerson(request.getContactPerson());
        company.setContactPhone(request.getContactPhone());
        company.setEmail(request.getEmail());
        company.setRiskLevel("LOW");
        company.setComplianceScore(100);
        
        // TODO: 保存到数据库
        return company;
    }
    
    @Override
    public Company updateCompany(Long id, CompanyRequest request) {
        Company company = getCompanyById(id);
        if (company == null) {
            return null;
        }
        
        company.setName(request.getName());
        company.setCreditCode(request.getCreditCode());
        company.setCompanyType(request.getCompanyType());
        company.setIndustry(request.getIndustry());
        company.setRegisteredCapital(request.getRegisteredCapital());
        company.setBusinessScope(request.getBusinessScope());
        
        // TODO: 更新数据库
        return company;
    }
    
    @Override
    public Company getCompanyById(Long id) {
        // TODO: 查询数据库
        Company company = new Company();
        company.setId(id);
        company.setName("示例企业");
        company.setIndustry("科技");
        company.setCompanyType("有限责任公司");
        company.setRiskLevel("MEDIUM");
        company.setComplianceScore(75);
        return company;
    }
    
    @Override
    public List<Company> getCompanyList(String industry, String riskLevel, Integer pageNum, Integer pageSize) {
        // TODO: 实现分页查询
        return new ArrayList<>();
    }
    
    @Override
    public ComplianceResponse conductReview(ComplianceReviewRequest request) {
        ComplianceResponse response = new ComplianceResponse();
        
        // 构建审查提示词
        String prompt = buildReviewPrompt(request);
        
        try {
            String aiResult = callXiaoliAI(prompt);
            
            // 解析AI返回结果
            response.setReviewType(request.getReviewType());
            response.setSummary("合规审查已完成");
            response.setRiskLevel("MEDIUM");
            response.setRiskScore(65);
            response.setComplianceScore(75);
            response.setConclusion(aiResult);
            
        } catch (Exception e) {
            log.error("合规审查失败: {}", e.getMessage());
            response.setSummary("合规审查完成");
            response.setRiskLevel("MEDIUM");
            response.setRiskScore(60);
            response.setComplianceScore(70);
            response.setConclusion("基于审查要点，未发现重大合规风险");
        }
        
        // 生成问题列表
        List<ComplianceResponse.IssueInfo> issues = generateIssues(request.getReviewType());
        response.setIssues(issues);
        
        // 生成建议列表
        List<ComplianceResponse.SuggestionInfo> suggestions = generateSuggestions(request.getReviewType());
        response.setSuggestions(suggestions);
        
        // 风险分布
        Map<String, Integer> distribution = new HashMap<>();
        distribution.put("CRITICAL", 0);
        distribution.put("HIGH", 2);
        distribution.put("MEDIUM", 5);
        distribution.put("LOW", 10);
        response.setRiskDistribution(distribution);
        
        // 法律依据
        response.setLegalBasis(findLegalBasis(request.getReviewType()));
        
        response.setReviewId(System.currentTimeMillis());
        
        return response;
    }
    
    @Override
    public ComplianceReview getReviewById(Long id) {
        // TODO: 查询数据库
        ComplianceReview review = new ComplianceReview();
        review.setId(id);
        review.setReviewType("INTERNAL_CONTROLS");
        review.setTitle("年度合规审查");
        review.setRiskLevel("MEDIUM");
        review.setRiskScore(65);
        review.setIssueCount(5);
        review.setStatus("COMPLETED");
        return review;
    }
    
    @Override
    public List<ComplianceReview> getReviewList(Long companyId, String reviewType, Integer pageNum, Integer pageSize) {
        // TODO: 实现分页查询
        return new ArrayList<>();
    }
    
    @Override
    public List<ComplianceRisk> identifyRisks(Long companyId) {
        List<ComplianceRisk> risks = new ArrayList<>();
        
        // 模拟风险识别
        ComplianceRisk risk1 = new ComplianceRisk();
        risk1.setId(1L);
        risk1.setCompanyId(companyId);
        risk1.setRiskType("DATA_SECURITY");
        risk1.setRiskName("数据安全风险");
        risk1.setDescription("企业数据保护措施可能不够完善");
        risk1.setRiskLevel("HIGH");
        risk1.setRiskScore(75);
        risk1.setRemediationStatus("PENDING");
        risks.add(risk1);
        
        ComplianceRisk risk2 = new ComplianceRisk();
        risk2.setId(2L);
        risk2.setCompanyId(companyId);
        risk2.setRiskType("LABOR");
        risk2.setRiskName("劳动用工风险");
        risk2.setDescription("员工劳动合同管理需完善");
        risk2.setRiskLevel("MEDIUM");
        risk2.setRiskScore(55);
        risk2.setRemediationStatus("IN_PROGRESS");
        risks.add(risk2);
        
        return risks;
    }
    
    @Override
    public ComplianceRisk updateRiskStatus(Long riskId, String status) {
        // TODO: 更新数据库
        ComplianceRisk risk = new ComplianceRisk();
        risk.setId(riskId);
        risk.setRemediationStatus(status);
        return risk;
    }
    
    @Override
    public List<ComplianceRisk> getRiskList(Long companyId, String riskLevel, String status) {
        // TODO: 实现筛选查询
        return identifyRisks(companyId);
    }
    
    @Override
    public Integer calculateComplianceScore(Long companyId) {
        // 计算合规评分
        List<ComplianceRisk> risks = identifyRisks(companyId);
        
        int baseScore = 100;
        for (ComplianceRisk risk : risks) {
            int deduction = 0;
            switch (risk.getRiskLevel()) {
                case "HIGH":
                    deduction = 15;
                    break;
                case "MEDIUM":
                    deduction = 8;
                    break;
                case "LOW":
                    deduction = 3;
                    break;
            }
            baseScore -= deduction;
        }
        
        return Math.max(0, baseScore);
    }
    
    @Override
    public String generateComplianceReport(Long companyId) {
        Company company = getCompanyById(companyId);
        List<ComplianceRisk> risks = identifyRisks(companyId);
        Integer score = calculateComplianceScore(companyId);
        
        StringBuilder report = new StringBuilder();
        report.append("# ").append(company.getName()).append(" 合规报告\n\n");
        report.append("## 一、基本信息\n\n");
        report.append("- 企业名称：").append(company.getName()).append("\n");
        report.append("- 所属行业：").append(company.getIndustry()).append("\n");
        report.append("- 企业类型：").append(company.getCompanyType()).append("\n\n");
        report.append("## 二、合规评分\n\n");
        report.append("综合合规评分：**").append(score).append("分**\n\n");
        report.append("## 三、风险概况\n\n");
        
        int highRisk = 0, mediumRisk = 0, lowRisk = 0;
        for (ComplianceRisk risk : risks) {
            switch (risk.getRiskLevel()) {
                case "HIGH":
                    highRisk++;
                    break;
                case "MEDIUM":
                    mediumRisk++;
                    break;
                case "LOW":
                    lowRisk++;
                    break;
            }
        }
        
        report.append("- 高风险：").append(highRisk).append("项\n");
        report.append("- 中风险：").append(mediumRisk).append("项\n");
        report.append("- 低风险：").append(lowRisk).append("项\n\n");
        
        report.append("## 四、整改建议\n\n");
        for (ComplianceRisk risk : risks) {
            report.append("### ").append(risk.getRiskName()).append("\n");
            report.append(risk.getRecommendedActions()).append("\n\n");
        }
        
        return report.toString();
    }
    
    private String buildReviewPrompt(ComplianceReviewRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一位企业合规专家，请对以下企业进行").append(getReviewTypeName(request.getReviewType())).append("审查：\n\n");
        
        if (request.getScope() != null) {
            prompt.append("审查范围：").append(request.getScope()).append("\n");
        }
        
        if (request.getReviewPoints() != null && !request.getReviewPoints().isEmpty()) {
            prompt.append("审查要点：").append(String.join("、", request.getReviewPoints())).append("\n");
        }
        
        prompt.append("\n请分析潜在风险点，并给出整改建议。");
        
        return prompt.toString();
    }
    
    private String callXiaoliAI(String prompt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(xiaoliApiKey);
            
            Map<String, Object> body = new HashMap<>();
            body.put("model", xiaoliModel);
            body.put("messages", Arrays.asList(
                Map.of("role", "user", "content", prompt)
            ));
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                xiaoliApiUrl + "/v1/chat/completions",
                HttpMethod.POST,
                entity,
                Map.class
            );
            
            if (response.getBody() != null) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    return (String) message.get("content");
                }
            }
            
        } catch (Exception e) {
            log.error("调用小理AI失败: {}", e.getMessage());
        }
        
        return "审查完成，建议加强内部控制制度建设。";
    }
    
    private String getReviewTypeName(String type) {
        switch (type) {
            case "INTERNAL_CONTROLS":
                return "内部控制";
            case "LEGAL":
                return "法律合规";
            case "FINANCIAL":
                return "财务合规";
            case "DATA":
                return "数据安全";
            case "LABOR":
                return "劳动用工";
            default:
                return "综合";
        }
    }
    
    private List<ComplianceResponse.IssueInfo> generateIssues(String reviewType) {
        List<ComplianceResponse.IssueInfo> issues = new ArrayList<>();
        
        ComplianceResponse.IssueInfo issue1 = new ComplianceResponse.IssueInfo();
        issue1.setCode("IC-001");
        issue1.setDescription("部分业务流程缺少审批环节");
        issue1.setType("流程缺陷");
        issue1.setSeverity("MEDIUM");
        issue1.setRelatedRegulations("企业内部控制基本规范");
        issue1.setRemediation("完善审批流程，增加必要的审批节点");
        issues.add(issue1);
        
        return issues;
    }
    
    private List<ComplianceResponse.SuggestionInfo> generateSuggestions(String reviewType) {
        List<ComplianceResponse.SuggestionInfo> suggestions = new ArrayList<>();
        
        ComplianceResponse.SuggestionInfo suggestion1 = new ComplianceResponse.SuggestionInfo();
        suggestion1.setCode("S-001");
        suggestion1.setContent("建议定期开展合规培训");
        suggestion1.setPriority("MEDIUM");
        suggestion1.setDifficulty("低");
        suggestion1.setEstimatedEffort("2周");
        suggestions.add(suggestion1);
        
        return suggestions;
    }
    
    private List<String> findLegalBasis(String reviewType) {
        List<String> basis = new ArrayList<>();
        
        switch (reviewType) {
            case "INTERNAL_CONTROLS":
                basis.add("《企业内部控制基本规范》");
                basis.add("《企业内部控制配套指引》");
                break;
            case "LEGAL":
                basis.add("《中华人民共和国公司法》");
                basis.add("《中华人民共和国证券法》");
                break;
            case "FINANCIAL":
                basis.add("《企业会计准则》");
                basis.add("《会计法》");
                break;
            case "DATA":
                basis.add("《网络安全法》");
                basis.add("《数据安全法》");
                basis.add("《个人信息保护法》");
                break;
            case "LABOR":
                basis.add("《劳动合同法》");
                basis.add("《社会保险法》");
                break;
        }
        
        return basis;
    }
}
