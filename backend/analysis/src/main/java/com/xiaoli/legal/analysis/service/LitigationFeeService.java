package com.xiaoli.legal.analysis.service;

import com.xiaoli.legal.analysis.model.dto.CaseAnalysisRequest;
import com.xiaoli.legal.analysis.model.dto.CaseAnalysisResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 诉讼费用计算服务
 */
@Service
public class LitigationFeeService {

    private static final Logger log = LoggerFactory.getLogger(LitigationFeeService.class);

    /**
     * 计算诉讼费用
     */
    public CaseAnalysisResponse.LitigationFee calculateFee(CaseAnalysisRequest request) {
        CaseAnalysisResponse.LitigationFee fee = new CaseAnalysisResponse.LitigationFee();
        
        // 从诉讼请求中提取金额
        BigDecimal claimAmount = extractClaimAmount(request.getClaims());
        fee.setClaimAmount(claimAmount);
        
        // 计算案件受理费
        BigDecimal caseFee = calculateCaseFee(claimAmount, request.getCaseType());
        fee.setCaseFee(caseFee);
        
        // 估算律师费
        BigDecimal lawyerFee = estimateLawyerFee(claimAmount, request.getCaseType());
        fee.setLawyerFee(lawyerFee);
        
        // 其他费用估算
        BigDecimal otherFees = estimateOtherFees();
        fee.setOtherFees(otherFees);
        
        // 总费用
        BigDecimal total = caseFee.add(lawyerFee).add(otherFees);
        fee.setTotalFee(total);
        
        // 费用明细
        List<CaseAnalysisResponse.FeeItem> items = new ArrayList<>();
        items.add(CaseAnalysisResponse.FeeItem.builder()
                .name("案件受理费")
                .description("向法院缴纳的费用")
                .amount(caseFee)
                .build());
        items.add(CaseAnalysisResponse.FeeItem.builder()
                .name("律师费")
                .description("聘请律师的费用（估算）")
                .amount(lawyerFee)
                .build());
        items.add(CaseAnalysisResponse.FeeItem.builder()
                .name("其他费用")
                .description("公告费、鉴定费等")
                .amount(otherFees)
                .build());
        fee.setFeeItems(items);
        
        // 缴费提示
        fee.setPaymentTip(generatePaymentTip(caseFee));
        
        return fee;
    }

    /**
     * 从诉讼请求中提取金额
     */
    private BigDecimal extractClaimAmount(String claims) {
        if (claims == null || claims.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        // 匹配金额模式：支持各种格式如 "10万元"、"100,000元"、"10万"等
        Pattern pattern = Pattern.compile("(\\d+(?:[,。]\\d+)*(?:\\.\\d+)?)\\s*(?:万|元|千|百)");
        Matcher matcher = pattern.matcher(claims);
        
        if (matcher.find()) {
            String amountStr = matcher.group(1).replace(",", "").replace("。", ".");
            BigDecimal amount = new BigDecimal(amountStr);
            
            // 判断单位
            String unit = matcher.group(0);
            if (unit.contains("万")) {
                amount = amount.multiply(new BigDecimal("10000"));
            } else if (unit.contains("千")) {
                amount = amount.multiply(new BigDecimal("1000"));
            } else if (unit.contains("百")) {
                amount = amount.multiply(new BigDecimal("100"));
            }
            
            return amount;
        }
        
        return BigDecimal.ZERO;
    }

    /**
     * 计算案件受理费（按最高人民法院收费标准）
     */
    private BigDecimal calculateCaseFee(BigDecimal claimAmount, String caseType) {
        if (claimAmount.compareTo(BigDecimal.ZERO) <= 0) {
            // 财产案件以外的费用
            return new BigDecimal("50");
        }
        
        // 财产案件诉讼费计算
        double fee;
        if (claimAmount.compareTo(new BigDecimal("10000")) <= 0) {
            // 1万元以下
            fee = 50;
        } else if (claimAmount.compareTo(new BigDecimal("20000")) <= 0) {
            // 1万-2万元
            fee = 50 + (claimAmount.subtract(new BigDecimal("10000")).divide(new BigDecimal("1000"), 2, RoundingMode.HALF_UP)).doubleValue() * 25;
        } else if (claimAmount.compareTo(new BigDecimal("50000")) <= 0) {
            // 2万-5万元
            fee = 50 + 100 + (claimAmount.subtract(new BigDecimal("20000")).divide(new BigDecimal("1000"), 2, RoundingMode.HALF_UP)).doubleValue() * 20;
        } else if (claimAmount.compareTo(new BigDecimal("100000")) <= 0) {
            // 5万-10万元
            fee = 50 + 100 + 300 + (claimAmount.subtract(new BigDecimal("50000")).divide(new BigDecimal("1000"), 2, RoundingMode.HALF_UP)).doubleValue() * 15;
        } else if (claimAmount.compareTo(new BigDecimal("200000")) <= 0) {
            // 10万-20万元
            fee = 50 + 100 + 300 + 750 + (claimAmount.subtract(new BigDecimal("100000")).divide(new BigDecimal("1000"), 2, RoundingMode.HALF_UP)).doubleValue() * 10;
        } else if (claimAmount.compareTo(new BigDecimal("500000")) <= 0) {
            // 20万-50万元
            fee = 50 + 100 + 300 + 750 + 1000 + (claimAmount.subtract(new BigDecimal("200000")).divide(new BigDecimal("1000"), 2, RoundingMode.HALF_UP)).doubleValue() * 8;
        } else if (claimAmount.compareTo(new BigDecimal("1000000")) <= 0) {
            // 50万-100万元
            fee = 50 + 100 + 300 + 750 + 1000 + 2400 + (claimAmount.subtract(new BigDecimal("500000")).divide(new BigDecimal("1000"), 2, RoundingMode.HALF_UP)).doubleValue() * 7;
        } else if (claimAmount.compareTo(new BigDecimal("2000000")) <= 0) {
            // 100万-200万元
            fee = 50 + 100 + 300 + 750 + 1000 + 2400 + 3500 + (claimAmount.subtract(new BigDecimal("1000000")).divide(new BigDecimal("1000"), 2, RoundingMode.HALF_UP)).doubleValue() * 6;
        } else {
            // 200万元以上
            fee = 50 + 100 + 300 + 750 + 1000 + 2400 + 3500 + 6000 + (claimAmount.subtract(new BigDecimal("2000000")).divide(new BigDecimal("1000"), 2, RoundingMode.HALF_UP)).doubleValue() * 5;
        }
        
        return BigDecimal.valueOf(Math.ceil(fee)).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 估算律师费（按市场行情）
     */
    private BigDecimal estimateLawyerFee(BigDecimal claimAmount, String caseType) {
        if (claimAmount.compareTo(BigDecimal.ZERO) <= 0) {
            // 非财产案件
            return new BigDecimal("5000");
        }
        
        double fee;
        if (claimAmount.compareTo(new BigDecimal("50000")) <= 0) {
            fee = claimAmount.multiply(new BigDecimal("0.08")).doubleValue();
            fee = Math.max(fee, 3000);
        } else if (claimAmount.compareTo(new BigDecimal("100000")) <= 0) {
            fee = 4000 + (claimAmount.subtract(new BigDecimal("50000")).multiply(new BigDecimal("0.06"))).doubleValue();
        } else if (claimAmount.compareTo(new BigDecimal("500000")) <= 0) {
            fee = 7000 + (claimAmount.subtract(new BigDecimal("100000")).multiply(new BigDecimal("0.05"))).doubleValue();
        } else if (claimAmount.compareTo(new BigDecimal("1000000")) <= 0) {
            fee = 27000 + (claimAmount.subtract(new BigDecimal("500000")).multiply(new BigDecimal("0.04"))).doubleValue();
        } else {
            fee = 47000 + (claimAmount.subtract(new BigDecimal("1000000")).multiply(new BigDecimal("0.02"))).doubleValue();
        }
        
        return BigDecimal.valueOf(Math.ceil(fee)).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 估算其他费用
     */
    private BigDecimal estimateOtherFees() {
        // 公告费、鉴定费、翻译费等估算
        return new BigDecimal("1000");
    }

    /**
     * 生成缴费提示
     */
    private String generatePaymentTip(BigDecimal caseFee) {
        return "案件受理费应在收到缴费通知书之日起7日内向法院缴纳。" +
                "如确有困难，可申请缓交或减免。";
    }
}
