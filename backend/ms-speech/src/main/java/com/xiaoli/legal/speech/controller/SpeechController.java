package com.xiaoli.legal.speech.controller;

import com.xiaoli.legal.common.core.domain.Result;
import com.xiaoli.legal.speech.model.dto.SpeechRequest;
import com.xiaoli.legal.speech.model.dto.SpeechResponse;
import com.xiaoli.legal.speech.service.AsrService;
import com.xiaoli.legal.speech.service.TtsService;
import com.xiaoli.legal.speech.service.VoiceChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 语音服务控制器
 */
@RestController
@RequestMapping("/api/speech")
public class SpeechController {
    
    private static final Logger log = LoggerFactory.getLogger(SpeechController.class);
    
    private final AsrService asrService;
    private final TtsService ttsService;
    private final VoiceChatService voiceChatService;
    private final SimpMessagingTemplate messagingTemplate;
    
    public SpeechController(AsrService asrService, TtsService ttsService, 
                          VoiceChatService voiceChatService, SimpMessagingTemplate messagingTemplate) {
        this.asrService = asrService;
        this.ttsService = ttsService;
        this.voiceChatService = voiceChatService;
        this.messagingTemplate = messagingTemplate;
    }
    
    /**
     * 语音识别(文件上传)
     */
    @PostMapping("/asr")
    public Result<SpeechResponse> recognize(@RequestParam("file") MultipartFile file) {
        log.info("收到语音识别请求, fileName: {}, size: {}", file.getOriginalFilename(), file.getSize());
        
        try {
            byte[] audioData = file.getBytes();
            String format = getFormat(file.getOriginalFilename());
            
            SpeechResponse response = asrService.recognize(
                java.util.Base64.getEncoder().encodeToString(audioData),
                format,
                16000
            );
            
            return Result.success(response);
        } catch (Exception e) {
            log.error("语音识别失败: {}", e.getMessage());
            return Result.fail("语音识别失败: " + e.getMessage());
        }
    }
    
    /**
     * 语音合成
     */
    @PostMapping("/tts")
    public Result<SpeechResponse> synthesize(@RequestBody Map<String, Object> request) {
        String text = (String) request.get("text");
        Integer voiceType = (Integer) request.getOrDefault("voiceType", 0);
        Double speed = (Double) request.getOrDefault("speed", 1.0);
        Integer volume = (Integer) request.getOrDefault("volume", 5);
        
        log.info("收到语音合成请求, textLength: {}", text.length());
        
        try {
            SpeechResponse response = ttsService.synthesize(text, voiceType, speed, volume);
            return Result.success(response);
        } catch (Exception e) {
            log.error("语音合成失败: {}", e.getMessage());
            return Result.fail("语音合成失败: " + e.getMessage());
        }
    }
    
    /**
     * 语音对话(处理语音消息)
     */
    @PostMapping("/chat")
    public Result<SpeechResponse> voiceChat(@RequestBody SpeechRequest request) {
        log.info("收到语音对话请求, sessionId: {}", request.getSessionId());
        
        try {
            SpeechResponse response = voiceChatService.processVoiceChat(request);
            
            if (response.getSuccess()) {
                return Result.success(response);
            } else {
                return Result.fail(response.getError());
            }
        } catch (Exception e) {
            log.error("语音对话处理失败: {}", e.getMessage());
            return Result.fail("处理失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建语音会话
     */
    @PostMapping("/session")
    public Result<Map<String, String>> createSession(@RequestBody Map<String, Object> request) {
        String sessionType = (String) request.getOrDefault("sessionType", "CONSULT");
        Long userId = request.get("userId") != null ? 
            Long.parseLong(request.get("userId").toString()) : null;
        
        String sessionId = voiceChatService.createSession(sessionType, userId);
        
        return Result.success(Map.of("sessionId", sessionId));
    }
    
    /**
     * 结束语音会话
     */
    @DeleteMapping("/session/{sessionId}")
    public Result<Void> endSession(@PathVariable String sessionId) {
        log.info("结束语音会话: {}", sessionId);
        voiceChatService.endSession(sessionId);
        return Result.success(null);
    }
    
    /**
     * 获取对话历史
     */
    @GetMapping("/session/{sessionId}/history")
    public Result<SpeechResponse> getHistory(@PathVariable String sessionId) {
        SpeechResponse response = voiceChatService.getDialogueHistory(sessionId);
        return Result.success(response);
    }
    
    /**
     * WebSocket消息处理
     */
    @MessageMapping("/voice")
    @SendTo("/topic/voice-response")
    public SpeechResponse handleVoiceMessage(SpeechRequest request) {
        log.info("WebSocket收到语音消息, sessionId: {}", request.getSessionId());
        return voiceChatService.processVoiceChat(request);
    }
    
    /**
     * 获取音频格式
     */
    private String getFormat(String filename) {
        if (filename == null) {
            return "wav";
        }
        String lower = filename.toLowerCase();
        if (lower.endsWith(".mp3")) {
            return "mp3";
        } else if (lower.endsWith(".pcm")) {
            return "pcm";
        } else {
            return "wav";
        }
    }
}
