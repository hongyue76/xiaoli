package com.xiaoli.legal.decision.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 司法决策请求DTO
 */
public class DecisionRequest {
    
    /** 案件ID */
    private Long caseId;
    
    /** 案件类型 */
    @NotBlank(message = "案件类型不能为空")
    private String caseType;
    
    /** 案件描述/事实摘要 */
    @NotBlank(message = "案件描述不能为空")
    private String caseDescription;
    
    /** 涉案金额 */
    private Long amountInvolved;
    
    /** 当事人信息 */
    private List<Map<String, String>> parties;
    
    /** 关键证据列表 */
    private List<String> keyEvidence;
    
    /** 辩护意见 */
    private String defenseOpinion;
    
    /** 检方指控 */
    private String prosecutionClaim;
    
    /** 既往犯罪记录 */
    private List<String> criminalHistory;
    
    /** 加重情节 */
    private List<String> aggravatingCircumstances;
    
    /** 减轻情节 */
    private List<String> mitigatingCircumstances;
    
    /** 赔偿情况 */
    private String compensationStatus;
    
    /** 被害方态度 */
    private String victimAttitude;
    
    /** 决策类型: sentencing(量刑)/trial(审判预测)/judgment(判决建议) */
    private String decisionType;

    // Getters and Setters
    public Long getCaseId() { return caseId; }
    public void setCaseId(Long caseId) { this.caseId = caseId; }

    public String getCaseType() { return caseType; }
    public void setCaseType(String caseType) { this.caseType = caseType; }

    public String getCaseDescription() { return caseDescription; }
    public void setCaseDescription(String caseDescription) { this.caseDescription = caseDescription; }

    public Long getAmountInvolved() { return amountInvolved; }
    public void setAmountInvolved(Long amountInvolved) { this.amountInvolved = amountInvolved; }

    public List<Map<String, String>> getParties() { return parties; }
    public void setParties(List<Map<String, String>> parties) { this.parties = parties; }

    public List<String> getKeyEvidence() { return keyEvidence; }
    public void setKeyEvidence(List<String> keyEvidence) { this.keyEvidence = keyEvidence; }

    public String getDefenseOpinion() { return defenseOpinion; }
    public void setDefenseOpinion(String defenseOpinion) { this.defenseOpinion = defenseOpinion; }

    public String getProsecutionClaim() { return prosecutionClaim; }
    public void setProsecutionClaim(String prosecutionClaim) { this.prosecutionClaim = prosecutionClaim; }

    public List<String> getCriminalHistory() { return criminalHistory; }
    public void setCriminalHistory(List<String> criminalHistory) { this.criminalHistory = criminalHistory; }

    public List<String> getAggravatingCircumstances() { return aggravatingCircumstances; }
    public void setAggravatingCircumstances(List<String> aggravatingCircumstances) { this.aggravatingCircumstances = aggravatingCircumstances; }

    public List<String> getMitigatingCircumstances() { return mitigatingCircumstances; }
    public void setMitigatingCircumstances(List<String> mitigatingCircumstances) { this.mitigatingCircumstances = mitigatingCircumstances; }

    public String getCompensationStatus() { return compensationStatus; }
    public void setCompensationStatus(String compensationStatus) { this.compensationStatus = compensationStatus; }

    public String getVictimAttitude() { return victimAttitude; }
    public void setVictimAttitude(String victimAttitude) { this.victimAttitude = victimAttitude; }

    public String getDecisionType() { return decisionType; }
    public void setDecisionType(String decisionType) { this.decisionType = decisionType; }
}
