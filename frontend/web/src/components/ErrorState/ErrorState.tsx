import React, { useState, useEffect, useCallback } from 'react';
import { Button, message } from 'antd';
import { ReloadOutlined } from '@ant-design/icons';
import './ErrorState.css';

export interface ErrorStateProps {
  /** 错误类型 */
  errorType?: 'network' | 'timeout' | 'server' | 'permission' | 'notFound' | 'default';
  /** 错误标题 */
  title?: string;
  /** 错误描述 */
  description?: string;
  /** 重试按钮文本 */
  retryText?: string;
  /** 重试回调 */
  onRetry?: () => void | Promise<void>;
  /** 是否显示自动重连倒计时 */
  showAutoRetry?: boolean;
  /** 自动重连间隔（秒） */
  autoRetryInterval?: number;
  /** 额外内容 */
  extra?: React.ReactNode;
  /** 是否紧凑模式 */
  compact?: boolean;
  /** 自定义样式 */
  style?: React.CSSProperties;
  /** 自定义类名 */
  className?: string;
}

// 预设插画组件
const ErrorIllustrations: Record<string, React.FC<{ size?: number }>> = {
  network: ({ size = 64 }) => (
    <svg width={size} height={size} viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
      <circle cx="32" cy="32" r="32" fill="#FFF1F0"/>
      <path d="M32 16V20M32 44V48M16 32H20M44 32H48M20.5 20.5L23.3 23.3M40.7 40.7L43.5 43.5M20.5 43.5L23.3 40.7M40.7 23.3L43.5 20.5" stroke="#FF4D4F" strokeWidth="3" strokeLinecap="round"/>
      <path d="M32 40C36.4183 40 40 36.4183 40 32C40 27.5817 36.4183 24 32 24C27.5817 24 24 27.5817 24 32C24 36.4183 27.5817 40 32 40Z" stroke="#FF4D4F" strokeWidth="3" strokeLinecap="round"/>
      <line x1="32" y1="26" x2="32" y2="38" stroke="#FF4D4F" strokeWidth="3" strokeLinecap="round"/>
    </svg>
  ),
  
  timeout: ({ size = 64 }) => (
    <svg width={size} height={size} viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
      <circle cx="32" cy="32" r="32" fill="#FFF7E6"/>
      <circle cx="32" cy="32" r="20" stroke="#FFA940" strokeWidth="3" fill="none"/>
      <path d="M32 17V32L40 40" stroke="#FFA940" strokeWidth="3" strokeLinecap="round"/>
      <path d="M28 10H36V14H28V10Z" fill="#FFA940"/>
    </svg>
  ),
  
  server: ({ size = 64 }) => (
    <svg width={size} height={size} viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
      <circle cx="32" cy="32" r="32" fill="#F0F5FF"/>
      <rect x="14" y="18" width="36" height="8" rx="2" fill="#69B1FF"/>
      <rect x="14" y="28" width="36" height="8" rx="2" fill="#69B1FF"/>
      <rect x="14" y="38" width="36" height="8" rx="2" fill="#69B1FF"/>
      <circle cx="20" cy="22" r="2" fill="#FF4D4F"/>
      <circle cx="26" cy="22" r="2" fill="#FFA940"/>
      <circle cx="32" cy="22" r="2" fill="#52C41A"/>
      <path d="M40 46L48 54L56 46" stroke="#FF4D4F" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
      <line x1="48" y1="34" x2="48" y2="54" stroke="#FF4D4F" strokeWidth="2" strokeLinecap="round"/>
    </svg>
  ),
  
  permission: ({ size = 64 }) => (
    <svg width={size} height={size} viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
      <circle cx="32" cy="32" r="32" fill="#F9F0FF"/>
      <rect x="22" y="14" width="20" height="30" rx="2" stroke="#9254DE" strokeWidth="3" fill="white"/>
      <circle cx="32" cy="26" r="5" stroke="#9254DE" strokeWidth="2" fill="none"/>
      <path d="M24 38H40" stroke="#9254DE" strokeWidth="2" strokeLinecap="round"/>
      <path d="M26 34H38" stroke="#9254DE" strokeWidth="2" strokeLinecap="round"/>
      <line x1="20" y1="46" x2="44" y2="46" stroke="#FF4D4F" strokeWidth="3" strokeLinecap="round"/>
      <path d="M36 46L40 50L44 46" stroke="#FF4D4F" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
    </svg>
  ),
  
  notFound: ({ size = 64 }) => (
    <svg width={size} height={size} viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
      <circle cx="32" cy="32" r="32" fill="#F6FFED"/>
      <circle cx="32" cy="32" r="18" stroke="#73D13D" strokeWidth="3" fill="white"/>
      <text x="32" y="38" textAnchor="middle" fontSize="24" fontWeight="bold" fill="#73D13D">404</text>
      <circle cx="44" cy="20" r="8" fill="#FFA940"/>
      <text x="44" y="24" textAnchor="middle" fontSize="10" fill="white">?</text>
    </svg>
  ),
  
  default: ({ size = 64 }) => (
    <svg width={size} height={size} viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
      <circle cx="32" cy="32" r="32" fill="#F5F5F5"/>
      <circle cx="32" cy="32" r="16" stroke="#BFBFBF" strokeWidth="3" fill="none"/>
      <text x="32" y="37" textAnchor="middle" fontSize="20" fontWeight="bold" fill="#BFBFBF">!</text>
    </svg>
  ),
};

// 预设文案配置
const presets: Record<string, { title: string; description: string; retryText?: string }> = {
  network: {
    title: '网络连接失败',
    description: '请检查您的网络连接后重试',
    retryText: '重新连接',
  },
  timeout: {
    title: '请求超时',
    description: '网络连接较慢，请稍后重试',
    retryText: '重试',
  },
  server: {
    title: '服务器错误',
    description: '服务器出现问题，我们正在努力修复',
    retryText: '刷新页面',
  },
  permission: {
    title: '没有权限',
    description: '您没有访问该资源的权限',
    retryText: '返回首页',
  },
  notFound: {
    title: '资源未找到',
    description: '您访问的资源不存在或已被删除',
    retryText: '返回',
  },
  default: {
    title: '出现错误',
    description: '发生未知错误，请稍后重试',
    retryText: '重试',
  },
};

/**
 * 错误状态组件 - 提供友好的错误提示和重试机制
 */
const ErrorState: React.FC<ErrorStateProps> = ({
  errorType = 'default',
  title,
  description,
  retryText,
  onRetry,
  showAutoRetry = false,
  autoRetryInterval = 5,
  extra,
  compact = false,
  style,
  className,
}) => {
  const [retrying, setRetrying] = useState(false);
  const [countdown, setCountdown] = useState(autoRetryInterval);

  const preset = presets[errorType];
  const Illustration = ErrorIllustrations[errorType] || ErrorIllustrations.default;
  
  const displayTitle = title ?? preset.title;
  const displayDescription = description ?? preset.description;
  const displayRetryText = retryText ?? preset.retryText;

  // 自动重连倒计时
  useEffect(() => {
    if (!showAutoRetry || countdown <= 0) return;

    const timer = setInterval(() => {
      setCountdown((prev) => {
        if (prev <= 1) {
          handleAutoRetry();
          return autoRetryInterval;
        }
        return prev - 1;
      });
    }, 1000);

    return () => clearInterval(timer);
  }, [showAutoRetry, countdown]);

  const handleAutoRetry = async () => {
    if (onRetry) {
      try {
        await onRetry();
      } catch (error) {
        console.error('自动重试失败:', error);
      }
    }
  };

  const handleRetry = async () => {
    if (!onRetry) return;

    setRetrying(true);
    try {
      await onRetry();
      message.success('重试成功');
    } catch (error) {
      console.error('重试失败:', error);
      message.error('重试失败，请稍后再试');
    } finally {
      setRetrying(false);
    }
  };

  return (
    <div
      className={`error-state ${compact ? 'error-state-compact' : ''} ${className || ''}`}
      style={style}
    >
      <div className="error-state-illustration">
        <Illustration size={compact ? 48 : 64} />
      </div>
      
      <div className="error-state-content">
        <div className="error-state-title">{displayTitle}</div>
        <div className="error-state-description">{displayDescription}</div>
        
        {onRetry && (
          <Button
            type="primary"
            onClick={handleRetry}
            loading={retrying}
            className="error-state-retry"
            icon={<ReloadOutlined />}
          >
            {displayRetryText}
          </Button>
        )}
        
        {showAutoRetry && onRetry && (
          <div className="error-state-auto-retry">
            <span className="auto-retry-text">将在</span>
            <span className="auto-retry-countdown">{countdown}</span>
            <span className="auto-retry-text">秒后自动重试</span>
          </div>
        )}
        
        {extra && <div className="error-state-extra">{extra}</div>}
      </div>
    </div>
  );
};

export default ErrorState;
