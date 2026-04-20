import { useState, useRef, useEffect } from 'react';
import { Input, Button, List, Avatar, Space, Typography, Card, message, Select, Alert, Spin } from 'antd';
import {
  SendOutlined,
  RobotOutlined,
  UserOutlined,
  LoadingOutlined,
  WarningOutlined,
} from '@ant-design/icons';
import './AIChat.css';

const { TextArea } = Input;
const { Text, Paragraph } = Typography;

interface Message {
  id: string;
  type: 'user' | 'ai';
  content: string;
  timestamp: Date;
  displayContent?: string; // 用于打字机效果
  isTyping?: boolean;      // 是否正在打字
}

const AIChat: React.FC = () => {
  const [messages, setMessages] = useState<Message[]>([
    {
      id: '1',
      type: 'ai',
      content: '您好！我是律法先锋法律AI助手，整合了真实案例库和法律知识库，可以为您提供专业的法律咨询服务。请开始提问吧！',
      displayContent: '您好！我是律法先锋法律AI助手，整合了真实案例库和法律知识库，可以为您提供专业的法律咨询服务。请开始提问吧！',
      timestamp: new Date(),
    },
  ]);
  const [inputValue, setInputValue] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [aiModel, setAiModel] = useState<string>('deepseek');
  const [showDisclaimer, setShowDisclaimer] = useState(true);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const typingSpeed = 30; // 打字速度：每字符30ms

  // 有用户对话后隐藏免责声明
  const hasUserMessage = messages.some(m => m.type === 'user');

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  // 打字机效果
  useEffect(() => {
    const typingMessage = messages.find(m => m.isTyping);
    if (!typingMessage) return;

    const fullContent = typingMessage.content;
    let currentIndex = typingMessage.displayContent?.length || 0;

    const timer = setInterval(() => {
      currentIndex++;
      if (currentIndex >= fullContent.length) {
        // 打字完成
        clearInterval(timer);
        setMessages(prev => prev.map(m => 
          m.id === typingMessage.id 
            ? { ...m, displayContent: fullContent, isTyping: false }
            : m
        ));
      } else {
        // 继续打字
        setMessages(prev => prev.map(m => 
          m.id === typingMessage.id 
            ? { ...m, displayContent: fullContent.substring(0, currentIndex) }
            : m
        ));
      }
    }, typingSpeed);

    return () => clearInterval(timer);
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
      displayContent: inputValue,
      timestamp: new Date(),
    };

    setMessages([...messages, userMessage]);
    const question = inputValue;
    setInputValue('');
    setIsLoading(true);

    try {
      // 使用 DeepSeek API
      const aiResponse = await callDeepSeekAPI(question);

      const aiMessage: Message = {
        id: (Date.now() + 1).toString(),
        type: 'ai',
        content: aiResponse,
        displayContent: '',
        isTyping: true,
        timestamp: new Date(),
      };

      setMessages(prev => [...prev, aiMessage]);
      setIsLoading(false);
    } catch (error: any) {
      console.error('API调用失败:', error);
      message.error(error.message || '请求失败，请稍后重试');

      const errorMessage: Message = {
        id: (Date.now() + 1).toString(),
        type: 'ai',
        content: '抱歉，发生了错误。请检查网络连接或API配置后重试。',
        displayContent: '抱歉，发生了错误。请检查网络连接或API配置后重试。',
        timestamp: new Date(),
      };
      setMessages(prev => [...prev, errorMessage]);
      setIsLoading(false);
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
                    {message.displayContent}
                    {message.isTyping && <span className="typing-cursor">|</span>}
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
        {isLoading && !messages.some(m => m.isTyping) && (
          <div className="message-item message-ai">
            <Avatar icon={<RobotOutlined />} className="message-avatar avatar-ai" />
            <div className="message-content">
              <Spin indicator={<LoadingOutlined style={{ fontSize: 20 }} spin />} />
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
