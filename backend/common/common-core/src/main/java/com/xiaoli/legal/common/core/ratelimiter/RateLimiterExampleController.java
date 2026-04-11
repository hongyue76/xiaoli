package com.xiaoli.legal.common.core.ratelimiter;

import com.xiaoli.legal.common.core.domain.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 限流示例控制器
 * 展示如何使用 @RateLimiter 注解进行接口限流
 *
 * @author Xiaoli Legal
 * @since 2026-03-31
 */
@RestController
@RequestMapping("/api/example")
public class RateLimiterExampleController {

    private static final Logger log = LoggerFactory.getLogger(RateLimiterExampleController.class);

    /**
     * 示例 1: 基于 IP 的限流
     * 每个 IP 在 60 秒内最多访问 10 次
     */
    @RateLimiter(
            value = "api:example:ip",
            maxRequests = 10,
            duration = 60,
            message = "请求过于频繁，请稍后再试"
    )
    @PostMapping("/test")
    public Result<Map<String, Object>> test() {
        Map<String, Object> data = new HashMap<>();
        data.put("timestamp", System.currentTimeMillis());
        data.put("message", "请求成功");
        return Result.success(data);
    }

    /**
     * 示例 2: 基于用户 ID 的限流
     * 每个用户在 10 秒内最多访问 5 次
     */
    @RateLimiter(
            value = "api:example:user",
            maxRequests = 5,
            duration = 10,
            identifier = "user:123",  // 实际应用中应该从 token 中获取用户ID
            message = "用户操作过于频繁，请稍后再试"
    )
    @GetMapping("/user-limited")
    public Result<Map<String, Object>> userLimited() {
        Map<String, Object> data = new HashMap<>();
        data.put("userId", 123);
        data.put("message", "用户限流测试");
        return Result.success(data);
    }

    /**
     * 示例 3: 严格的限流
     * 每个 IP 在 1 秒内最多访问 1 次
     */
    @RateLimiter(
            value = "api:example:strict",
            maxRequests = 1,
            duration = 1,
            message = "操作太快了，请慢一点"
    )
    @GetMapping("/strict")
    public Result<Map<String, Object>> strict() {
        Map<String, Object> data = new HashMap<>();
        data.put("message", "严格限流测试");
        return Result.success(data);
    }

    /**
     * 示例 4: 宽松的限流
     * 每个 IP 在 300 秒内最多访问 1000 次
     */
    @RateLimiter(
            value = "api:example:loose",
            maxRequests = 1000,
            duration = 300,
            message = "您今天的访问次数已达上限"
    )
    @GetMapping("/loose")
    public Result<Map<String, Object>> loose() {
        Map<String, Object> data = new HashMap<>();
        data.put("message", "宽松限流测试");
        return Result.success(data);
    }

    /**
     * 示例 5: 模拟法律咨询接口限流
     * 每个 IP 在 60 秒内最多咨询 3 次
     */
    @RateLimiter(
            value = "api:consult",
            maxRequests = 3,
            duration = 60,
            message = "咨询过于频繁，请 1 分钟后再试"
    )
    @PostMapping("/consult")
    public Result<Map<String, Object>> consult(@RequestBody Map<String, Object> request) {
        log.info("Consult request: {}", request);

        Map<String, Object> data = new HashMap<>();
        data.put("question", request.get("question"));
        data.put("answer", "这是 AI 的回答内容");
        data.put("timestamp", System.currentTimeMillis());

        return Result.success(data);
    }

    /**
     * 示例 6: 模拟文书生成接口限流
     * 每个用户在 10 分钟内最多生成 5 份文书
     */
    @RateLimiter(
            value = "api:document:generate",
            maxRequests = 5,
            duration = 600,
            identifier = "user:123",
            message = "文书生成过于频繁，请 10 分钟后再试"
    )
    @PostMapping("/document/generate")
    public Result<Map<String, Object>> generateDocument(@RequestBody Map<String, Object> request) {
        log.info("Document generation request: {}", request);

        Map<String, Object> data = new HashMap<>();
        data.put("documentId", System.currentTimeMillis());
        data.put("template", request.get("template"));
        data.put("message", "文书生成成功");

        return Result.success(data);
    }
}
