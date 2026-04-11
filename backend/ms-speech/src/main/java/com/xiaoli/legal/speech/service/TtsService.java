package com.xiaoli.legal.speech.service;

import com.xiaoli.legal.speech.model.dto.SpeechResponse;

/**
 * 语音合成服务接口(TTS)
 */
public interface TtsService {
    
    /**
     * 合成语音
     * @param text 待合成文本
     * @return 语音响应(含音频URL或base64)
     */
    SpeechResponse synthesize(String text);
    
    /**
     * 合成语音(指定参数)
     * @param text 待合成文本
     * @param voiceType 音色
     * @param speed 语速
     * @param volume 音量
     * @return 语音响应
     */
    SpeechResponse synthesize(String text, Integer voiceType, Double speed, Integer volume);
    
    /**
     * 流式合成(实时返回)
     * @param text 待合成文本
     * @return 语音响应
     */
    SpeechResponse synthesizeStream(String text);
}
