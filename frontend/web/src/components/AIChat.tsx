import { useState, useRef, useEffect } from 'react';
import { Input, Button, List, Avatar, Space, Typography, Card, message, Select, Alert } from 'antd';
import {
  SendOutlined,
  RobotOutlined,
  UserOutlined,
  LoadingOutlined,
  WarningOutlined,
} from '@ant-design/icons';
import { ProgressBar, LoadingButton } from './LoadingStates';
import './AIChat.css';

const { TextArea } = Input;
const { Text, Paragraph } = Typography;

interface Message {
  id: string;
  type: 'user' | 'ai';
  content: string;
  timestamp: Date;
  showFull?: boolean;
}

const AIChat: React.FC = () => {
  const [messages, setMessages] = useState<Message[]>([
    {
      id: '1',
      type: 'ai',
      content: '您好！我是律法先锋法律AI助手，整合了真实案例库和法律知识库，可以为您提供专业的法律咨询服务。请开始提问吧！',
      timestamp: new Date(),
    },
  ]);
  const [inputValue, setInputValue] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [progressPercent, setProgressPercent] = useState(0);
  const [progressStep, setProgressStep] = useState(0);
  const [aiModel, setAiModel] = useState<string>('xiaoli');
  const [showDisclaimer, setShowDisclaimer] = useState(true);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  // 有用户对话后隐藏免责声明
  const hasUserMessage = messages.some(m => m.type === 'user');

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  // DeepSeek AI API调用
  const callDeepSeekAPI = async (question: string) => {
    try {
      const response = await fetch('https://api.deepseek.com/v1/chat/completions', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer sk-ab6f6d5baa9349c6a735df32f9cc4f16',
        },
        body: JSON.stringify({
          model: 'deepseek-chat',
          messages: [
            {
              role: 'system',
              content: '你是一个专业的法律AI助手，擅长解答各类法律问题。请用专业、准确、易懂的语言回答用户的问题。'
            },
            {
              role: 'user',
              content: question
            }
          ],
          temperature: 0.7,
          max_tokens: 2000,
        }),
      });

      const data = await response.json();
      return data.choices[0]?.message?.content || '抱歉，无法获取回复。';
    } catch (error) {
      console.error('DeepSeek API调用失败:', error);
      throw error;
    }
  };

  const handleSend = async () => {
    if (!inputValue.trim()) return;

    const userMessage: Message = {
      id: Date.now().toString(),
      type: 'user',
      content: inputValue,
      timestamp: new Date(),
    };

    setMessages([...messages, userMessage]);
    const question = inputValue;
    setInputValue('');
    setIsLoading(true);
    setProgressPercent(0);
    setProgressStep(0);

    // 模拟进度条动画
    const progressInterval = setInterval(() => {
      setProgressPercent(prev => {
        const newPercent = prev + Math.random() * 15 + 5;
        return newPercent > 85 ? 85 : newPercent;
      });
      setProgressStep(prev => {
        const newStep = prev + (Math.random() > 0.7 ? 1 : 0);
        return newStep > 2 ? 2 : newStep;
      });
    }, 800);

    try {
      let aiResponse = '';

      if (aiModel === 'xiaoli') {
        // 使用律法先锋后端API（带案例检索）
        const response = await fetch('/api/consult/chat', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            messages: [{ role: 'user', content: question }],
          }),
        });

        if (response.ok) {
          const result = await response.json();
          // 兼容后端响应格式：{ code, data: { answer, similarCases } }
          if (result.data && result.data.answer) {
            aiResponse = result.data.answer;
          } else if (result.answer) {
            aiResponse = result.answer;
          } else {
            aiResponse = JSON.stringify(result);
          }
        } else {
          const errorText = await response.text();
          throw new Error(`后端API调用失败: ${response.status} ${errorText}`);
        }
      } else {
        // 其他模型使用DeepSeek API
        aiResponse = await callDeepSeekAPI(question);
      }

      const aiMessage: Message = {
        id: (Date.now() + 1).toString(),
        type: 'ai',
        content: aiResponse,
        timestamp: new Date(),
      };

      // 完成进度
      setProgressPercent(100);
      setProgressStep(3);

      setTimeout(() => {
        const aiMessage: Message = {
          id: (Date.now() + 1).toString(),
          type: 'ai',
          content: aiResponse,
          timestamp: new Date(),
        };

        setMessages(prev => [...prev, aiMessage]);
        setIsLoading(false);
      }, 500);
    } catch (error: any) {
      console.error('API调用失败:', error);
      message.error(error.message || '请求失败，请稍后重试');

      const errorMessage: Message = {
        id: (Date.now() + 1).toString(),
        type: 'ai',
        content: '抱歉，发生了错误。请尝试切换AI模型或稍后重试。如果使用真实AI，请确保已配置API密钥。',
        timestamp: new Date(),
      };
      setMessages(prev => [...prev, errorMessage]);
      setIsLoading(false);
    } finally {
      clearInterval(progressInterval);
    }
  };

  const formatTime = (date: Date) => {
    return date.toLocaleTimeString('zh-CN', {
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  return (
    <div className="ai-chat">
      <div style={{ marginBottom: 16, padding: 12, background: '#f5f5f5', borderRadius: 8 }}>
        <Space>
          <Text strong>AI模型：</Text>
          <Select
            value={aiModel}
            onChange={setAiModel}
            style={{ width: 200 }}
          >
            <Select.Option value="xiaoli">律法先锋 AI</Select.Option>
            <Select.Option value="deepseek">DeepSeek AI</Select.Option>
          </Select>
        </Space>
      </div>

      <div className="chat-messages">
        <List
          dataSource={messages}
          renderItem={(message) => (
            <div
              key={message.id}
              className={`message-item message-${message.type}`}
            >
              <Avatar
                icon={message.type === 'ai' ? <RobotOutlined /> : <UserOutlined />}
                className={`message-avatar avatar-${message.type}`}
              />
              <div className="message-content">
                <div className="message-header">
                  <Text strong className="message-sender">
                    {message.type === 'ai' ? 'AI助手' : '用户'}
                  </Text>
                  <Text type="secondary" className="message-time">
                    {formatTime(message.timestamp)}
                  </Text>
                </div>
                <Card size="small" className="message-card">
                  <Paragraph className="message-text">
                    {message.content}
                  </Paragraph>
                  {message.type === 'ai' && !hasUserMessage && (
                    <Alert
                      message="内容由 AI 生成，仅供参考"
                      type="info"
                      showIcon
                      icon={<WarningOutlined />}
                      style={{ marginTop: 8, fontSize: 12 }}
                    />
                  )}
                </Card>
              </div>
            </div>
          )}
        />
        {isLoading && (
          <div className="message-item message-ai">
            <Avatar icon={<RobotOutlined />} className="message-avatar avatar-ai" />
            <div className="message-content">
              <ProgressBar
                current={progressStep}
                percent={progressPercent}
                status="active"
                showSteps={true}
                showPercent={true}
                type="default"
              />
            </div>
          </div>
        )}
        <div ref={messagesEndRef} />
      </div>
      <div className="chat-input">
        <TextArea
          value={inputValue}
          onChange={(e) => setInputValue(e.target.value)}
          placeholder="请输入您的法律问题..."
          autoSize={{ minRows: 3, maxRows: 5 }}
          onPressEnter={(e) => {
            if (e.shiftKey) return;
            e.preventDefault();
            handleSend();
          }}
          className="chat-textarea"
        />
        <Button
          type="primary"
          icon={<SendOutlined />}
          onClick={handleSend}
          loading={isLoading}
          disabled={!inputValue.trim()}
          className="send-button"
        >
          发送
        </Button>
      </div>
      <div className="chat-hint">
        <Text type="secondary" style={{ fontSize: 12, opacity: 0.6 }}>
          按 Enter 发送，Shift+Enter 换行
        </Text>
      </div>
    </div>
  );
};

export default AIChat;
