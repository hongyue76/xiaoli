import React, { useRef, useEffect } from 'react';
import { Avatar, Tooltip, Button, Space, message } from 'antd';
import { CopyOutlined, LikeOutlined, DislikeOutlined, ThunderboltOutlined, UserOutlined, RobotOutlined } from '@ant-design/icons';
import ReactMarkdown from 'react-markdown';
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter';
import { tomorrow } from 'react-syntax-highlighter/dist/esm/styles/prism';
import './ChatBubble.css';

export interface ChatMessage {
  id: string;
  role: 'user' | 'assistant';
  content: string;
  timestamp: Date;
  avatar?: string;
  isTyping?: boolean;
  metadata?: {
    hasFollowUp?: boolean;
    canCopy?: boolean;
    sources?: Array<{ title: string; url: string }>;
  };
}

interface ChatBubbleProps {
  message: ChatMessage;
  onFollowUp?: (content: string) => void;
  onCopy?: (content: string) => void;
  onLike?: (messageId: string, type: 'like' | 'dislike') => void;
}

const ChatBubble: React.FC<ChatBubbleProps> = ({
  message,
  onFollowUp,
  onCopy,
  onLike,
}) => {
  const bubbleRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (bubbleRef.current) {
      bubbleRef.current.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
    }
  }, [message]);

  const handleCopy = () => {
    navigator.clipboard.writeText(message.content);
    message.success('已复制到剪贴板');
    onCopy?.(message.content);
  };

  const handleFollowUp = () => {
    onFollowUp?.(`能否详细说明一下"${message.content.slice(0, 20)}..."的内容？`);
  };

  const handleLike = (type: 'like' | 'dislike') => {
    onLike?.(message.id, type);
    message.success(type === 'like' ? '感谢您的点赞！' : '感谢您的反馈，我们会改进！');
  };

  const highlightLegalArticles = (text: string) => {
    return text.replace(/《([^》]+)》/g, '<span class="legal-article">《$1》</span>');
  };

  const renderMarkdown = (content: string) => {
    return (
      <ReactMarkdown
        className="markdown-content"
        components={{
          code({ node, inline, className, children, ...props }) {
            const match = /language-(\w+)/.exec(className || '');
            return !inline && match ? (
              <SyntaxHighlighter
                {...props}
                children={String(children).replace(/\n$/, '')}
                style={tomorrow}
                language={match[1]}
                PreTag="div"
              />
            ) : (
              <code {...props} className={className}>
                {children}
              </code>
            );
          },
          table({ children }) {
            return (
              <div className="table-container">
                <table>{children}</table>
              </div>
            );
          },
          ol({ children }) {
            return <ol className="numbered-list">{children}</ol>;
          },
          ul({ children }) {
            return <ul className="bullet-list">{children}</ul>;
          },
          blockquote({ children }) {
            return <blockquote className="quote-block">{children}</blockquote>;
          },
          a({ children, href }) {
            return (
              <a href={href} target="_blank" rel="noopener noreferrer" className="link-text">
                {children}
              </a>
            );
          },
        }}
      >
        {highlightLegalArticles(content)}
      </ReactMarkdown>
    );
  };

  const isUser = message.role === 'user';

  return (
    <div
      ref={bubbleRef}
      className={`chat-bubble ${isUser ? 'user-message' : 'ai-message'}`}
    >
      <div className="message-container">
        <Avatar
          size={40}
          src={message.avatar}
          icon={isUser ? <UserOutlined /> : <RobotOutlined />}
          className="message-avatar"
        />
        <div className="message-content-wrapper">
          <div className="message-header">
            <span className="message-role">
              {isUser ? '您' : '小律助手'}
            </span>
            <span className="message-time">
              {message.timestamp.toLocaleTimeString('zh-CN', {
                hour: '2-digit',
                minute: '2-digit',
              })}
            </span>
          </div>
          <div className={`message-bubble ${message.isTyping ? 'typing' : ''}`}>
            {message.isTyping ? (
              <div className="typing-indicator">
                <span className="dot"></span>
                <span className="dot"></span>
                <span className="dot"></span>
              </div>
            ) : (
              <div className="message-text">
                {renderMarkdown(message.content)}
              </div>
            )}
          </div>
          {!isUser && !message.isTyping && (
            <Space className="message-actions">
              {message.metadata?.hasFollowUp && (
                <Tooltip title="追问">
                  <Button
                    type="text"
                    size="small"
                    icon={<ThunderboltOutlined />}
                    onClick={handleFollowUp}
                  >
                    追问
                  </Button>
                </Tooltip>
              )}
              <Tooltip title="复制">
                <Button
                  type="text"
                  size="small"
                  icon={<CopyOutlined />}
                  onClick={handleCopy}
                />
              </Tooltip>
              <Tooltip title="点赞">
                <Button
                  type="text"
                  size="small"
                  icon={<LikeOutlined />}
                  onClick={() => handleLike('like')}
                />
              </Tooltip>
              <Tooltip title="点踩">
                <Button
                  type="text"
                  size="small"
                  icon={<DislikeOutlined />}
                  onClick={() => handleLike('dislike')}
                />
              </Tooltip>
            </Space>
          )}
          {message.metadata?.sources && message.metadata.sources.length > 0 && (
            <div className="message-sources">
              <span className="sources-label">参考资料：</span>
              {message.metadata.sources.map((source, index) => (
                <a key={index} href={source.url} target="_blank" rel="noopener noreferrer">
                  {source.title}
                </a>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default ChatBubble;
