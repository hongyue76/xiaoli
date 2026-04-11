import React, { useState, useRef, useEffect } from 'react';
import { Input, Button, List, Tag, Divider, Space, Card, message, Spin, Alert, Typography } from 'antd';
import { SendOutlined, RobotOutlined, UserOutlined, SearchOutlined, BulbOutlined } from '@ant-design/icons';
import intentRouterService from '@/services/intentRouterService';
import type { Intent, RouteResponse } from '@/services/intentRouterService';
import './IntentAwareAIChat.css';

const { Text, Paragraph } = Typography;

interface Message {
  id: string;
  role: 'user' | 'assistant';
  content: string;
  intent?: Intent;
  searchResults?: string;
  timestamp: Date;
}

/**
 * 基于意图路由的 AI 聊天组件
 */
export const IntentAwareAIChat: React.FC = () => {
  const [messages, setMessages] = useState<Message[]>([]);
  const [inputValue, setInputValue] = useState('');
  const [loading, setLoading] = useState(false);
  const [routeInfo, setRouteInfo] = useState<RouteResponse | null>(null);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  // 滚动到底部
  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const handleSend = async () => {
    if (!inputValue.trim() || loading) return;

    const userMessage: Message = {
      id: Date.now().toString(),
      role: 'user',
      content: inputValue.trim(),
      timestamp: new Date(),
    };

    setMessages(prev => [...prev, userMessage]);
    setInputValue('');
    setLoading(true);
    setRouteInfo(null);

    try {
      // 调用意图路由服务
      const response = await intentRouterService.routeQuestion(userMessage.content);
      setRouteInfo(response);

      if (!response.success) {
        message.error(response.errorMessage || '处理失败');
        setLoading(false);
        return;
      }

      // 构建助手消息
      const assistantMessage: Message = {
        id: (Date.now() + 1).toString(),
        role: 'assistant',
        content: response.answer || '抱歉，无法处理您的问题。',
        timestamp: new Date(),
        intent: response.intentType ? {
          type: response.intentType,
          needSearch: response.processType === 'PROFESSIONAL_WITH_SEARCH',
          confidence: 0.8,
          reason: '基于 DeepSeek 意图分析',
          originalQuestion: userMessage.content,
        } : undefined,
        searchResults: response.searchResults,
      };

      setMessages(prev => [...prev, assistantMessage]);

      // 显示处理信息
      const processType = response.processType;
      if (processType === 'PROFESSIONAL_WITH_SEARCH') {
        message.success('检测到专业法律问题，已调用检索系统');
      } else if (processType === 'GENERAL') {
        message.info('检测到通用问题，直接回答');
      }

    } catch (error: any) {
      console.error('发送消息失败:', error);
      message.error('发送失败，请重试');
    } finally {
      setLoading(false);
    }
  };

  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  const getIntentTagColor = (type: string) => {
    const colors: Record<string, string> = {
      PROFESSIONAL: 'red',
      GENERAL: 'blue',
      CHAT: 'green',
      WEATHER: 'orange',
      SUMMARY: 'purple',
      AMBIGUOUS: 'default',
    };
    return colors[type] || 'default';
  };

  const renderIntentIndicator = (intent: Intent) => {
    return (
      <div className="intent-indicator">
        <Tag color={getIntentTagColor(intent.type)} icon={<BulbOutlined />}>
          {intent.type === 'PROFESSIONAL' ? '专业法律问题' : 
           intent.type === 'GENERAL' ? '通用问题' :
           intent.type === 'CHAT' ? '日常闲聊' :
           intent.type === 'WEATHER' ? '天气查询' :
           intent.type === 'SUMMARY' ? '内容总结' : '意图不明确'}
        </Tag>
        <span className="intent-confidence">
          置信度: {(intent.confidence * 100).toFixed(0)}%
        </span>
      </div>
    );
  };

  return (
    <div className="intent-aware-chat">
      <Card
        title={
          <Space>
            <RobotOutlined />
            <span>智能法律助手</span>
            {routeInfo && (
              <Tag color={routeInfo.processType === 'PROFESSIONAL_WITH_SEARCH' ? 'blue' : 'green'}>
                {routeInfo.processType === 'PROFESSIONAL_WITH_SEARCH' ? '专业模式' : '通用模式'}
              </Tag>
            )}
          </Space>
        }
        className="chat-container"
      >
        {/* 处理流程说明 */}
        {routeInfo && (
          <Alert
            message={
              <Space direction="vertical" size="small">
                <Text strong>处理流程：</Text>
                <Text>
                  {routeInfo.processType === 'PROFESSIONAL_WITH_SEARCH' ? (
                    <Space>
                      <SearchOutlined style={{ color: '#1890ff' }} />
                      <span>意图识别 → 法律检索 → AI生成专业回答</span>
                    </Space>
                  ) : (
                    <Space>
                      <BulbOutlined style={{ color: '#52c41a' }} />
                      <span>意图识别 → AI直接回答</span>
                    </Space>
                  )}
                </Text>
                {routeInfo.duration && (
                  <Text type="secondary">耗时：{routeInfo.duration}ms</Text>
                )}
              </Space>
            }
            type={routeInfo.processType === 'PROFESSIONAL_WITH_SEARCH' ? 'info' : 'success'}
            closable
            onClose={() => setRouteInfo(null)}
            style={{ marginBottom: 16 }}
          />
        )}

        {/* 消息列表 */}
        <div className="messages-container">
          {messages.length === 0 && (
            <div className="empty-state">
              <RobotOutlined style={{ fontSize: 48, color: '#1890ff', marginBottom: 16 }} />
              <Paragraph type="secondary">
                您好！我是您的智能法律助手。
              </Paragraph>
              <Paragraph>
                我可以帮您：
              </Paragraph>
              <ul className="feature-list">
                <li>📚 解答专业法律问题（基于案例和法规）</li>
                <li>💬 日常闲聊和问答</li>
                <li>📄 内容总结和提炼</li>
                <li>🔍 各种查询（天气、时间等）</li>
              </ul>
              <Paragraph type="secondary">
                请输入您的问题，我会智能判断并为您提供最合适的回答。
              </Paragraph>
            </div>
          )}

          <List
            dataSource={messages}
            renderItem={(message) => (
              <List.Item
                key={message.id}
                className={`message-item ${message.role}`}
              >
                <div className="message-content">
                  {/* 意图指示器（仅助手消息） */}
                  {message.intent && renderIntentIndicator(message.intent)}

                  {/* 搜索结果展示（专业模式） */}
                  {message.searchResults && (
                    <div className="search-results">
                      <Alert
                        message="相关法律检索结果"
                        description={
                          <details>
                            <summary>查看检索到的法律信息</summary>
                            <pre style={{
                              maxHeight: 200,
                              overflow: 'auto',
                              background: '#f5f5f5',
                              padding: 12,
                              borderRadius: 4,
                              marginTop: 8,
                            }}>
                              {JSON.stringify(JSON.parse(message.searchResults), null, 2)}
                            </pre>
                          </details>
                        }
                        type="info"
                        showIcon
                        style={{ marginBottom: 12 }}
                      />
                    </div>
                  )}

                  {/* 消息内容 */}
                  <div className="message-text">
                    {message.role === 'assistant' ? (
                      <div dangerouslySetInnerHTML={{ __html: formatMessage(message.content) }} />
                    ) : (
                      message.content
                    )}
                  </div>

                  {/* 消息时间 */}
                  <div className="message-time">
                    {new Date(message.timestamp).toLocaleTimeString('zh-CN')}
                  </div>
                </div>
              </List.Item>
            )}
          />

          {/* 滚动到底部的锚点 */}
          <div ref={messagesEndRef} />
        </div>

        {/* 输入区域 */}
        <div className="input-area">
          <Input.TextArea
            value={inputValue}
            onChange={(e) => setInputValue(e.target.value)}
            onKeyPress={handleKeyPress}
            placeholder={
              routeInfo?.processType === 'PROFESSIONAL_WITH_SEARCH'
                ? '输入专业法律问题（将进行案例和法规检索）...'
                : '输入任何问题（智能识别意图）...'
            }
            autoSize={{ minRows: 2, maxRows: 6 }}
            disabled={loading}
          />
          <Button
            type="primary"
            icon={<SendOutlined />}
            onClick={handleSend}
            loading={loading}
            disabled={!inputValue.trim()}
            style={{ marginTop: 8 }}
          >
            发送
          </Button>
        </div>
      </Card>
    </div>
  );
};

/**
 * 格式化消息（支持简单markdown）
 */
function formatMessage(content: string): string {
  return content
    .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
    .replace(/`(.*?)`/g, '<code>$1</code>')
    .replace(/\n/g, '<br />');
}

export default IntentAwareAIChat;
