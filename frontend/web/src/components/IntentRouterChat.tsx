import { useState, useRef, useEffect } from 'react';
import { Input, Button, List, Avatar, Space, Typography, Card, message, Tag, Divider } from 'antd';
import {
  SendOutlined,
  RobotOutlined,
  UserOutlined,
  LoadingOutlined,
  ThunderboltOutlined,
  ExperimentOutlined,
  CheckCircleOutlined,
  ClockCircleOutlined,
} from '@ant-design/icons';
import './AIChat.css';

const { TextArea } = Input;
const { Text, Paragraph } = Typography;

interface Message {
  id: string;
  type: 'user' | 'ai';
  content: string;
  timestamp: Date;
  metadata?: {
    intent?: string;
    engine?: string;
    confidence?: number;
    responseTime?: number;
  };
}

const IntentRouterChat: React.FC = () => {
  const [messages, setMessages] = useState<Message[]>([
    {
      id: '1',
      type: 'ai',
      content: '您好！我是律法先锋AI助手，具备意图识别和双引擎路由能力。我可以：\n\n' +
        '🎯 自动识别您的意图（咨询/检索/文书/审查等）\n' +
        '⚡ 智能选择最佳引擎（规则引擎或AI大模型）\n' +
        '📊 提供详细的意图分析和引擎选择说明\n\n' +
        '请开始提问，我会自动识别您的需求并提供最合适的回答！',
      timestamp: new Date(),
      metadata: {
        intent: 'SYSTEM',
        engine: 'SYSTEM',
      },
    },
  ]);
  const [inputValue, setInputValue] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

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

    try {
      // 调用意图路由API - 使用相对路径，通过vite代理
      const response = await fetch('/api/intent-router/query', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          queryText: question,
          sessionId: Date.now().toString(),
          userId: 'user-' + Date.now(),
          context: {},
        }),
      });

      if (!response.ok) {
        throw new Error('API调用失败');
      }

      const result = await response.json();
      const data = result.data;

      // 构建AI回复
      const aiResponse = buildResponse(data);

      const aiMessage: Message = {
        id: (Date.now() + 1).toString(),
        type: 'ai',
        content: aiResponse.content,
        timestamp: new Date(),
        metadata: {
          intent: data.intentResult?.intent,
          engine: data.engineResult?.engineType,
          confidence: data.intentResult?.confidence,
          responseTime: data.responseTime,
        },
      };

      setMessages(prev => [...prev, aiMessage]);
    } catch (error: any) {
      console.error('API调用失败:', error);

      message.error('请求失败，请检查后端服务是否启动');
    } finally {
      setIsLoading(false);
    }
  };

  const buildResponse = (data: any) => {
    const intentResult = data.intentResult || {};
    const engineResult = data.engineResult || {};

    const intentName = intentResult.intent || '未知意图';
    const engineName = engineResult.engineType === 'RULE_BASED' ? '规则引擎' : 'AI大模型';
    const confidence = (intentResult.confidence * 100).toFixed(0);
    const responseTime = data.responseTime;

    return {
      content: `【${engineName} 回复】\n\n${data.response}\n\n` +
        `--- 意图分析 ---\n` +
        `📋 识别意图: ${intentName}\n` +
        `📊 置信度: ${confidence}%\n` +
        `⚡ 引擎: ${engineName}\n` +
        `⏱️ 响应时间: ${responseTime}ms\n` +
        `💡 路由原因: ${engineResult.reason}`,
    };
  };

  const formatTime = (date: Date) => {
    return date.toLocaleTimeString('zh-CN', {
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  const renderEngineTag = (engine?: string) => {
    if (!engine) return null;

    if (engine === 'RULE_BASED' || engine === '规则引擎') {
      return (
        <Tag color="blue" icon={<ThunderboltOutlined />}>
          规则引擎
        </Tag>
      );
    }
    if (engine === 'LLM_BASED' || engine === 'AI大模型') {
      return (
        <Tag color="purple" icon={<ExperimentOutlined />}>
          AI大模型
        </Tag>
      );
    }
    return <Tag>{engine}</Tag>;
  };

  return (
    <div className="ai-chat">
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
                  <Space>
                    <Text strong className="message-sender">
                      {message.type === 'ai' ? 'AI助手' : '用户'}
                    </Text>
                    {message.type === 'ai' && renderEngineTag(message.metadata?.engine)}
                  </Space>
                  <Text type="secondary" className="message-time">
                    {formatTime(message.timestamp)}
                  </Text>
                </div>
                {message.metadata?.intent && message.metadata?.confidence && (
                  <div style={{ marginBottom: 8 }}>
                    <Space size="small">
                      <Tag color="green" icon={<CheckCircleOutlined />}>
                        意图: {message.metadata.intent}
                      </Tag>
                      <Tag color="orange">
                        置信度: {Math.round(message.metadata.confidence)}%
                      </Tag>
                      {message.metadata.responseTime && (
                        <Tag icon={<ClockCircleOutlined />}>
                          {message.metadata.responseTime}ms
                        </Tag>
                      )}
                    </Space>
                  </div>
                )}
                <Card size="small" className="message-card">
                  <Paragraph
                    className="message-text"
                    ellipsis={{ rows: 8, expandable: true, symbol: '展开' }}
                  >
                    {message.content}
                  </Paragraph>
                </Card>
              </div>
            </div>
          )}
        />
        {isLoading && (
          <div className="message-item message-ai">
            <Avatar icon={<RobotOutlined />} className="message-avatar avatar-ai" />
            <div className="message-content">
              <div className="message-header">
                <Space>
                  <Text strong className="message-sender">
                    AI助手
                  </Text>
                  <Tag color="processing" icon={<LoadingOutlined />}>
                    思考中...
                  </Tag>
                </Space>
              </div>
              <Card size="small" className="message-card loading-card">
                <Space>
                  <LoadingOutlined />
                  <Text type="secondary">正在识别意图并选择最佳引擎...</Text>
                </Space>
              </Card>
            </div>
          </div>
        )}
        <div ref={messagesEndRef} />
      </div>
      <div className="chat-input">
        <TextArea
          value={inputValue}
          onChange={(e) => setInputValue(e.target.value)}
          placeholder="请输入您的问题，我会自动识别意图并选择最佳引擎回复..."
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
        <Space>
          <Text type="secondary" style={{ fontSize: 12 }}>
            💡 提示：按 Enter 发送，Shift+Enter 换行 | 系统会自动识别您的意图
          </Text>
        </Space>
      </div>
    </div>
  );
};

export default IntentRouterChat;
