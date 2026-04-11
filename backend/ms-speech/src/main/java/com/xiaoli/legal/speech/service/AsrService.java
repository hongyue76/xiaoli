package com.xiaoli.legal.speech.service;

import com.xiaoli.legal.speech.model.dto.SpeechResponse;

/**
 * 语音识别服务接口(ASR)
 */
public interface AsrService {
    
    /**
     * 识别语音文件
     * @param audioData 音频数据(base64或URL)
     * @param format 音频格式
     * @param sampleRate 采样率
     * @return 识别结果
     */
    SpeechResponse recognize(String audioData, String format, Integer sampleRate);
    
    /**
     * 流式识别
     * @param audioData 音频数据
     * @param isEnd 是否结束
     * @return 识别结果
     */
    SpeechResponse recognizeStream(String audioData, boolean isEnd);
    
    /**
     * 识别URL音频
     * @param audioUrl 音频URL
     * @return 识别结果
     */
    SpeechResponse recognizeUrl(String audioUrl);
}
