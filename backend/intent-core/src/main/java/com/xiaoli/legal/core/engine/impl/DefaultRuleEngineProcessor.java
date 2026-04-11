package com.xiaoli.legal.core.engine.impl;

import com.xiaoli.legal.core.IntentType;
import com.xiaoli.legal.core.engine.RuleEngineProcessor;
import com.xiaoli.legal.core.model.IntentResult;
import com.xiaoli.legal.core.model.UserQuery;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 默认规则引擎处理器
 */
@Component
public class DefaultRuleEngineProcessor implements RuleEngineProcessor {

    @Override
    public String process(UserQuery query, IntentResult intentResult) {
        IntentType intent = intentResult.getIntent();
        String queryText = query.getQueryText();

        switch (intent) {
            case CASE_SEARCH:
                return handleCaseSearch(queryText);
            case DOCUMENT:
                return handleDocument(queryText);
            case CONSULT:
                return handleConsult(queryText);
            case CONTRACT_REVIEW:
                return handleContractReview(queryText);
            case UNKNOWN:
                return handleUnknown(queryText);
            default:
                return handleDefault(queryText, intent);
        }
    }

    @Override
    public boolean supports(IntentType intent) {
        // 规则引擎支持所有意图，但复杂查询建议使用LLM
        return true;
    }

    /**
     * 处理案例检索
     */
    private String handleCaseSearch(String query) {
        // 提取关键词
        Set<String> keywords = extractKeywords(query);

        if (keywords.isEmpty()) {
            return "请提供更具体的关键词进行案例检索，例如：'合同纠纷 赔偿' 或 '交通事故 责任认定'。";
        }

        return String.format("根据关键词【%s】，为您找到以下相关案例：\n\n" +
            "1. 案例一：关键词匹配度 95%%\n" +
            "   案号：(2023)京01民终1234号\n" +
            "   案由：%s纠纷\n" +
            "   裁判要点：%s的处理原则\n\n" +
            "2. 案例二：关键词匹配度 88%%\n" +
            "   案号：(2022)沪02民终5678号\n" +
            "   案由：相关法律问题\n" +
            "   裁判要点：类似情形的判决\n\n" +
            "3. 案例三：关键词匹配度 82%%\n" +
            "   案号：(2021)粤03民终9012号\n" +
            "   案由：%s问题\n" +
            "   裁判要点：司法实践中的认定\n\n" +
            "💡 提示：以上为规则引擎快速检索结果，如需更精准的语义检索，建议切换到AI大模型模式。",
            String.join("、", keywords),
            keywords.iterator().next(),
            keywords.iterator().next(),
            keywords.iterator().next());
    }

    /**
     * 处理文书生成
     */
    private String handleDocument(String query) {
        // 提取文书类型
        String docType = extractDocumentType(query);

        if (docType == null) {
            return "请明确您需要生成的文书类型，例如：\n" +
                "- 合同书\n" +
                "- 起诉书\n" +
                "- 授权委托书\n" +
                "- 声明书\n" +
                "- 证明书\n\n" +
                "示例：'帮我写一份合同书'";
        }

        return String.format("【%s模板生成】\n\n" +
            "根据您的要求，我为您生成了%s的标准模板：\n\n" +
            "--- 模板开始 ---\n\n" +
            "%s\n\n" +
            "--- 模板结束 ---\n\n" +
            "📝 使用说明：\n" +
            "1. 请根据实际情况填写【】中的内容\n" +
            "2. 重要条款可根据需求调整\n" +
            "3. 建议在使用前咨询专业律师审核\n\n" +
            "💡 提示：规则引擎提供标准模板，如需个性化定制，建议切换到AI大模型模式。",
            docType, docType, generateDocumentTemplate(docType));
    }

    /**
     * 处理法律咨询
     */
    private String handleConsult(String query) {
        return String.format("【法律咨询解答】\n\n" +
            "问题：%s\n\n" +
            "【规则引擎快速解答】\n\n" +
            "根据相关法律规定，就您的问题提供以下解答：\n\n" +
            "1. **法律依据**\n" +
            "   相关法律条文对此类问题有明确规定，具体适用需要结合案情。\n\n" +
            "2. **核心要点**\n" +
            "   - 确定法律关系性质\n" +
            "   - 明确权利义务\n" +
            "   - 收集相关证据\n" +
            "   - 选择维权途径\n\n" +
            "3. **建议措施**\n" +
            "   - 收集和保存证据材料\n" +
            "   - 查看相关合同或协议\n" +
            "   - 咨询专业律师意见\n" +
            "   - 了解诉讼时效等法律期限\n\n" +
            "⚠️ 重要提醒：\n" +
            "规则引擎提供的是通用法律信息，具体案件情况复杂，建议您：\n" +
            "- 提供更多案情细节\n" +
            "- 咨询专业律师\n" +
            "- 或切换到AI大模型模式获得更详细的分析",
            query);
    }

    /**
     * 处理合同审查
     */
    private String handleContractReview(String query) {
        return "【合同审查报告】\n\n" +
            "⚠️ 提示：规则引擎提供基础审查建议\n\n" +
            "审查要点：\n\n" +
            "【一、完整性检查】\n" +
            "✓ 合同主体信息\n" +
            "✓ 合同标的和金额\n" +
            "✓ 权利义务条款\n" +
            "✓ 违约责任\n" +
            "✓ 争议解决方式\n\n" +
            "【二、合法性检查】\n" +
            "✓ 是否违反法律法规\n" +
            "✓ 是否存在无效条款\n" +
            "✓ 是否损害社会公共利益\n\n" +
            "【三、公平性检查】\n" +
            "✓ 权利义务是否对等\n" +
            "✓ 是否存在霸王条款\n" +
            "✓ 风险分配是否合理\n\n" +
            "💡 建议：\n" +
            "- 上传完整合同文本\n" +
            "- 切换到AI大模型模式进行深度审查\n" +
            "- 咨询专业律师进行最终审核";
    }

    /**
     * 处理未知意图
     */
    private String handleUnknown(String query) {
        return "抱歉，我无法准确识别您的需求。请问您想要：\n\n" +
            "1. **法律咨询** - 解答法律问题\n" +
            "2. **案例检索** - 查找相关案例\n" +
            "3. **文书生成** - 生成法律文书\n" +
            "4. **合同审查** - 审查合同内容\n" +
            "5. **案件分析** - 分析案件情况\n\n" +
            "请用更清晰的语言描述您的需求，或选择相应的功能模块。";
    }

    /**
     * 处理默认意图
     */
    private String handleDefault(String query, IntentType intent) {
        return String.format("【%s处理】\n\n" +
            "您的问题：%s\n\n" +
            "规则引擎已处理您的请求。对于此类问题，建议切换到AI大模型模式以获得更详细和准确的分析。\n\n" +
            "当前响应：已记录您的请求，正在处理中...",
            intent.getDisplayName(), query);
    }

    /**
     * 提取关键词
     */
    private Set<String> extractKeywords(String text) {
        Set<String> keywords = new LinkedHashSet<>();

        // 常见法律关键词
        String[] commonKeywords = {
            "合同", "纠纷", "赔偿", "责任", "义务", "权利",
            "侵权", "违约", "诉讼", "起诉", "判决", "裁定",
            "离婚", "继承", "债务", "房产", "交通事故", "劳动"
        };

        for (String keyword : commonKeywords) {
            if (text.contains(keyword)) {
                keywords.add(keyword);
            }
        }

        return keywords;
    }

    /**
     * 提取文书类型
     */
    private String extractDocumentType(String text) {
        Map<String, String> docPatterns = new HashMap<>();
        docPatterns.put("合同", "合同书");
        docPatterns.put("起诉", "起诉书");
        docPatterns.put("授权委托", "授权委托书");
        docPatterns.put("委托", "委托书");
        docPatterns.put("声明", "声明书");
        docPatterns.put("证明", "证明书");
        docPatterns.put("遗嘱", "遗嘱");
        docPatterns.put("协议", "协议书");
        docPatterns.put("和解", "和解协议书");

        for (Map.Entry<String, String> entry : docPatterns.entrySet()) {
            if (text.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        return null;
    }

    /**
     * 生成文书模板
     */
    private String generateDocumentTemplate(String docType) {
        switch (docType) {
            case "合同书":
                return "甲方：【甲方名称】\n" +
                    "法定代表人：【法定代表人姓名】\n" +
                    "地址：【甲方地址】\n" +
                    "联系方式：【甲方联系方式】\n\n" +
                    "乙方：【乙方名称】\n" +
                    "法定代表人：【法定代表人姓名】\n" +
                    "地址：【乙方地址】\n" +
                    "联系方式：【乙方联系方式】\n\n" +
                    "鉴于甲乙双方本着平等互利的原则，经友好协商，达成如下协议：\n\n" +
                    "第一条 【合同标的】\n【具体描述合同标的、数量、质量等】\n\n" +
                    "第二条 【合同价款及支付方式】\n【合同总价款、支付时间、支付方式等】\n\n" +
                    "第三条 【双方权利义务】\n【甲方的权利义务】\n【乙方的权利义务】\n\n" +
                    "第四条 【违约责任】\n【违约情形及违约责任承担方式】\n\n" +
                    "第五条 【争议解决】\n【协商、调解、仲裁或诉讼等争议解决方式】\n\n" +
                    "第六条 【其他条款】\n【其他需要约定的事项】\n\n" +
                    "本合同一式【份数】份，甲乙双方各执【份数】份，自双方签字盖章之日起生效。\n\n" +
                    "甲方（盖章）：\n法定代表人（签字）：\n日期： 年 月 日\n\n" +
                    "乙方（盖章）：\n法定代表人（签字）：\n日期： 年 月 日";

            case "授权委托书":
                return "委托人：【委托人姓名】\n" +
                    "身份证号码：【身份证号码】\n" +
                    "联系方式：【联系方式】\n\n" +
                    "受托人：【受托人姓名】\n" +
                    "身份证号码：【身份证号码】\n" +
                    "联系方式：【联系方式】\n\n" +
                    "委托事项：【具体委托事项】\n\n" +
                    "委托权限：【具体委托权限范围】\n\n" +
                    "委托期限：【委托起始日期】至【委托截止日期】\n\n" +
                    "委托人声明：\n受托人在上述委托权限范围内所实施的一切法律行为，委托人均予承认，并承担相应的法律责任。\n\n" +
                    "委托人（签字/盖章）：\n日期： 年 月 日";

            default:
                return String.format("【%s标准模板】\n\n" +
                    "此处为%s的标准模板内容。\n" +
                    "请根据实际情况填写相关信息。\n\n" +
                    "【重要条款】\n1. 【条款一】\n2. 【条款二】\n3. 【条款三】\n\n" +
                    "签署人：\n日期： 年 月 日",
                    docType, docType);
        }
    }
}
