import React, { useState, useRef, useEffect } from 'react';
import { Button, Slider, Tooltip, message, Progress } from 'antd';
import {
  AudioOutlined,
  AudioMutedOutlined,
  CustomerServiceOutlined,
  StopOutlined,
  PlayCircleOutlined,
  PauseCircleOutlined,
  ReloadOutlined,
} from '@ant-design/icons';
import './VoiceInteraction.css';

interface VoiceInteractionProps {
  onVoiceInput?: (text: string) => void;
  onTTSStart?: () => void;
  onTTSEnd?: () => void;
  disabled?: boolean;
}

const VoiceInteraction: React.FC<VoiceInteractionProps> = ({
  onVoiceInput,
  onTTSStart,
  onTTSEnd,
  disabled = false,
}) => {
  const [isRecording, setIsRecording] = useState(false);
  const [isPlaying, setIsPlaying] = useState(false);
  const [recordingTime, setRecordingTime] = useState(0);
  const [volume, setVolume] = useState(80);
  const [audioData, setAudioData] = useState<number[]>([]);
  const [hasPermission, setHasPermission] = useState(false);
  const [isSpeaking, setIsSpeaking] = useState(false);

  const recognitionRef = useRef<any>(null);
  const speechRef = useRef<SpeechSynthesisUtterance | null>(null);
  const timerRef = useRef<NodeJS.Timeout | null>(null);
  const animationRef = useRef<number | null>(null);
  const audioContextRef = useRef<AudioContext | null>(null);
  const analyserRef = useRef<AnalyserNode | null>(null);
  const microphoneRef = useRef<MediaStreamAudioSourceNode | null>(null);

  // 初始化语音识别
  useEffect(() => {
    if ('webkitSpeechRecognition' in window || 'SpeechRecognition' in window) {
      const SpeechRecognition = (window as any).SpeechRecognition || (window as any).webkitSpeechRecognition;
      recognitionRef.current = new SpeechRecognition();
      recognitionRef.current.continuous = true;
      recognitionRef.current.interimResults = true;
      recognitionRef.current.lang = 'zh-CN';

      recognitionRef.current.onresult = (event: any) => {
        let interimTranscript = '';
        let finalTranscript = '';

        for (let i = event.resultIndex; i < event.results.length; i++) {
          const transcript = event.results[i][0].transcript;
          if (event.results[i].isFinal) {
            finalTranscript += transcript;
          } else {
            interimTranscript += transcript;
          }
        }

        if (finalTranscript) {
          onVoiceInput?.(finalTranscript);
        }
      };

      recognitionRef.current.onerror = (event: any) => {
        console.error('Speech recognition error:', event.error);
        if (event.error === 'not-allowed') {
          message.error('请允许麦克风权限以使用语音输入');
          setHasPermission(false);
        }
        stopRecording();
      };
    }

    return () => {
      stopRecording();
      stopTTS();
      if (timerRef.current) {
        clearInterval(timerRef.current);
      }
      if (animationRef.current) {
        cancelAnimationFrame(animationRef.current);
      }
    };
  }, [onVoiceInput]);

  // 请求麦克风权限
  const requestMicrophonePermission = async () => {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      stream.getTracks().forEach(track => track.stop());
      setHasPermission(true);
      return true;
    } catch (error) {
      message.error('无法访问麦克风，请检查权限设置');
      setHasPermission(false);
      return false;
    }
  };

  // 开始录音
  const startRecording = async () => {
    if (!hasPermission) {
      const hasPermission = await requestMicrophonePermission();
      if (!hasPermission) return;
    }

    try {
      await recognitionRef.current?.start();
      setIsRecording(true);
      setRecordingTime(0);

      // 初始化音频上下文
      audioContextRef.current = new AudioContext();
      analyserRef.current = audioContextRef.current.createAnalyser();
      analyserRef.current.fftSize = 256;

      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      microphoneRef.current = audioContextRef.current.createMediaStreamSource(stream);
      microphoneRef.current.connect(analyserRef.current);

      // 启动可视化
      startVisualization();

      // 启动计时器
      timerRef.current = setInterval(() => {
        setRecordingTime(prev => prev + 1);
      }, 1000);

      message.success('开始录音，请说话...');
    } catch (error) {
      message.error('启动录音失败');
      console.error('Failed to start recording:', error);
    }
  };

  // 停止录音
  const stopRecording = () => {
    recognitionRef.current?.stop();
    setIsRecording(false);

    if (timerRef.current) {
      clearInterval(timerRef.current);
      timerRef.current = null;
    }

    if (animationRef.current) {
      cancelAnimationFrame(animationRef.current);
      animationRef.current = null;
    }

    if (microphoneRef.current) {
      microphoneRef.current.disconnect();
    }

    if (audioContextRef.current) {
      audioContextRef.current.close();
    }

    setAudioData([]);
    message.success('录音结束');
  };

  // 声纹可视化
  const startVisualization = () => {
    if (!analyserRef.current) return;

    const bufferLength = analyserRef.current.frequencyBinCount;
    const dataArray = new Uint8Array(bufferLength);

    const animate = () => {
      if (!isRecording) return;

      analyserRef.current?.getByteFrequencyData(dataArray);
      setAudioData(Array.from(dataArray.slice(0, 50)));
      animationRef.current = requestAnimationFrame(animate);
    };

    animate();
  };

  // TTS 朗读
  const speakText = (text: string) => {
    if (!('speechSynthesis' in window)) {
      message.error('您的浏览器不支持语音合成');
      return;
    }

    stopTTS();

    speechRef.current = new SpeechSynthesisUtterance(text);
    speechRef.current.lang = 'zh-CN';
    speechRef.current.volume = volume / 100;
    speechRef.current.rate = 1;
    speechRef.current.pitch = 1;

    speechRef.current.onstart = () => {
      setIsSpeaking(true);
      onTTSStart?.();
    };

    speechRef.current.onend = () => {
      setIsSpeaking(false);
      onTTSEnd?.();
    };

    speechRef.current.onerror = (event) => {
      console.error('TTS error:', event);
      setIsSpeaking(false);
    };

    window.speechSynthesis.speak(speechRef.current);
  };

  // 停止 TTS
  const stopTTS = () => {
    if (window.speechSynthesis.speaking) {
      window.speechSynthesis.cancel();
    }
    setIsSpeaking(false);
  };

  // 暂停/继续 TTS
  const togglePlayPause = () => {
    if (window.speechSynthesis.speaking) {
      if (window.speechSynthesis.paused) {
        window.speechSynthesis.resume();
        setIsPlaying(true);
      } else {
        window.speechSynthesis.pause();
        setIsPlaying(false);
      }
    }
  };

  // 格式化时间
  const formatTime = (seconds: number) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  };

  return (
    <div className="voice-interaction">
      <div className="voice-controls">
        {/* 录音控制 */}
        <div className="control-group">
          <Tooltip title={isRecording ? '停止录音' : '开始录音'}>
            <Button
              type={isRecording ? 'primary' : 'default'}
              danger={isRecording}
              icon={isRecording ? <AudioMutedOutlined /> : <AudioOutlined />}
              onClick={isRecording ? stopRecording : startRecording}
              disabled={disabled}
              className="voice-button"
            >
              {isRecording ? '停止录音' : '语音输入'}
            </Button>
          </Tooltip>

          {isRecording && (
            <div className="recording-info">
              <span className="recording-time">{formatTime(recordingTime)}</span>
              <span className="recording-indicator">
                <span className="pulse"></span>
              </span>
            </div>
          )}
        </div>

        {/* TTS 控制 */}
        <div className="control-group">
          <Tooltip title="朗读答案">
            <Button
              icon={<CustomerServiceOutlined />}
              onClick={() => speakText('这是示例朗读内容')}
              disabled={disabled || isSpeaking}
              className="voice-button"
            >
              朗读答案
            </Button>
          </Tooltip>

          {isSpeaking && (
            <Space className="tts-controls">
              <Tooltip title={isPlaying ? '暂停' : '继续'}>
                <Button
                  type="text"
                  size="small"
                  icon={isPlaying ? <PauseCircleOutlined /> : <PlayCircleOutlined />}
                  onClick={togglePlayPause}
                />
              </Tooltip>
              <Tooltip title="停止">
                <Button
                  type="text"
                  size="small"
                  icon={<StopOutlined />}
                  onClick={stopTTS}
                />
              </Tooltip>
            </Space>
          )}
        </div>
      </div>

      {/* 声纹可视化 */}
      {isRecording && (
        <div className="voice-visualizer">
          <div className="waveform">
            {audioData.map((value, index) => (
              <div
                key={index}
                className="wave-bar"
                style={{
                  height: `${Math.max(value / 255 * 100, 10)}%`,
                  background: `linear-gradient(to top, #1890ff, #40a9ff)`,
                }}
              />
            ))}
          </div>
        </div>
      )}

      {/* 音量控制 */}
      <div className="volume-control">
        <Tooltip title="音量调节">
          <div className="volume-slider">
            <span className="volume-label">音量</span>
            <Slider
              min={0}
              max={100}
              value={volume}
              onChange={setVolume}
              tooltip={{
                formatter: (value) => `${value}%`,
              }}
              className="volume-slider-input"
            />
            <span className="volume-value">{volume}%</span>
          </div>
        </Tooltip>
      </div>
    </div>
  );
};

export default VoiceInteraction;
