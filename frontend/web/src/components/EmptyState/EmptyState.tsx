import React from 'react';
import { Button } from 'antd';
import { InboxOutlined } from '@ant-design/icons';
import './EmptyState.css';

export interface EmptyStateProps {
  /** 插画类型 */
  illustration?: 'chat' | 'document' | 'case' | 'contract' | 'search' | 'favorite' | 'default';
  /** 空状态标题 */
  title?: string;
  /** 空状态描述 */
  description?: string;
  /** 操作按钮文本 */
  actionText?: string;
  /** 操作按钮点击事件 */
  onAction?: () => void;
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
const Illustrations: Record<string, React.FC<{ size?: number }>> = {
  chat: ({ size = 64 }) => (
    <svg width={size} height={size} viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
      <circle cx="32" cy="32" r="32" fill="#F0F5FF"/>
      <path d="M20 20H44C45.1046 20 46 20.8954 46 22V38C46 39.1046 45.1046 40 44 40H28L22 46V40H20C18.8954 40 18 39.1046 18 38V22C18 20.8954 18.8954 20 20 20Z" fill="#69B1FF"/>
      <circle cx="28" cy="30" r="2" fill="white"/>
      <circle cx="32" cy="30" r="2" fill="white"/>
      <circle cx="36" cy="30" r="2" fill="white"/>
    </svg>
  ),
  
  document: ({ size = 64 }) => (
    <svg width={size} height={size} viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
      <circle cx="32" cy="32" r="32" fill="#F6FFED"/>
      <path d="M24 14H40L48 22V48C48 49.1046 47.1046 50 46 50H24C22.8954 50 22 49.1046 22 48V16C22 14.8954 22.8954 14 24 14Z" fill="#95DE64"/>
      <path d="M40 14V22H48" fill="#73D13D"/>
      <rect x="28" y="28" width="14" height="2" rx="1" fill="white"/>
      <rect x="28" y="34" width="14" height="2" rx="1" fill="white"/>
      <rect x="28" y="40" width="8" height="2" rx="1" fill="white"/>
    </svg>
  ),
  
  case: ({ size = 64 }) => (
    <svg width={size} height={size} viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
      <circle cx="32" cy="32" r="32" fill="#FFF7E6"/>
      <path d="M18 18H46V46H18V18Z" fill="#FFD591" stroke="#FFA940" strokeWidth="2"/>
      <rect x="22" y="22" width="20" height="4" rx="1" fill="#FF7A45"/>
      <rect x="22" y="30" width="16" height="2" rx="1" fill="#FF7A45"/>
      <rect x="22" y="36" width="14" height="2" rx="1" fill="#FF7A45"/>
      <rect x="22" y="42" width="12" height="2" rx="1" fill="#FF7A45"/>
    </svg>
  ),
  
  contract: ({ size = 64 }) => (
    <svg width={size} height={size} viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
      <circle cx="32" cy="32" r="32" fill="#F9F0FF"/>
      <path d="M16 16H32V48H16V16Z" fill="#B37FEB"/>
      <path d="M32 16H48V48H32V16Z" fill="#9254DE"/>
      <rect x="20" y="24" width="8" height="2" rx="1" fill="white"/>
      <rect x="20" y="30" width="6" height="2" rx="1" fill="white"/>
      <rect x="36" y="24" width="8" height="2" rx="1" fill="white"/>
      <rect x="36" y="30" width="6" height="2" rx="1" fill="white"/>
      <rect x="36" y="36" width="6" height="2" rx="1" fill="white"/>
      <circle cx="40" cy="42" r="3" fill="#F759AB"/>
      <path d="M39 42L39.8 42.8L41.2 41.2" stroke="white" strokeWidth="1" strokeLinecap="round"/>
    </svg>
  ),
  
  search: ({ size = 64 }) => (
    <svg width={size} height={size} viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
      <circle cx="32" cy="32" r="32" fill="#E6F7FF"/>
      <circle cx="28" cy="28" r="12" fill="#40A9FF" opacity="0.3"/>
      <circle cx="28" cy="28" r="8" stroke="#1890FF" strokeWidth="3" fill="none"/>
      <path d="M36 36L44 44" stroke="#1890FF" strokeWidth="3" strokeLinecap="round"/>
    </svg>
  ),
  
  favorite: ({ size = 64 }) => (
    <svg width={size} height={size} viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
      <circle cx="32" cy="32" r="32" fill="#FFF1F0"/>
      <path d="M32 48L20 36C16 32 16 26 20 22C24 18 28 18 32 22C36 18 40 18 44 22C48 26 48 32 44 36L32 48Z" fill="#FF4D4F"/>
    </svg>
  ),
  
  default: ({ size = 64 }) => (
    <svg width={size} height={size} viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
      <circle cx="32" cy="32" r="32" fill="#F5F5F5"/>
      <path d="M20 20H44C45.1046 20 46 20.8954 46 22V38C46 39.1046 45.1046 40 44 40H28L22 46V40H20C18.8954 40 18 39.1046 18 38V22C18 20.8954 18.8954 20 20 20Z" fill="#BFBFBF"/>
    </svg>
  ),
};

// 预设文案配置
const presets: Record<string, { title: string; description: string; actionText?: string }> = {
  chat: {
    title: '暂无咨询记录',
    description: '开始您的首次法律咨询，AI助手将为您提供专业建议',
    actionText: '开始提问',
  },
  document: {
    title: '暂无文档',
    description: '上传您的法律文档，AI将帮助您分析整理',
    actionText: '上传文档',
  },
  case: {
    title: '暂无案例记录',
    description: '输入关键词搜索相关法律案例',
    actionText: '搜索案例',
  },
  contract: {
    title: '暂无合同记录',
    description: '创建或导入合同，AI将帮助您审查风险',
    actionText: '创建合同',
  },
  search: {
    title: '暂无搜索结果',
    description: '尝试使用不同的关键词进行搜索',
    actionText: '重新搜索',
  },
  favorite: {
    title: '暂无收藏',
    description: '将重要的咨询记录添加到收藏夹',
    actionText: '浏览历史',
  },
  default: {
    title: '暂无数据',
    description: '暂无相关内容',
  },
};

/**
 * 空状态组件 - 提供美观的空状态展示和引导操作
 */
const EmptyState: React.FC<EmptyStateProps> = ({
  illustration = 'default',
  title,
  description,
  actionText,
  onAction,
  extra,
  compact = false,
  style,
  className,
}) => {
  const preset = presets[illustration];
  const Illustration = Illustrations[illustration] || Illustrations.default;
  
  const displayTitle = title ?? preset.title;
  const displayDescription = description ?? preset.description;
  const displayActionText = actionText ?? preset.actionText;

  return (
    <div
      className={`empty-state ${compact ? 'empty-state-compact' : ''} ${className || ''}`}
      style={style}
    >
      <div className="empty-state-illustration">
        <Illustration size={compact ? 48 : 64} />
      </div>
      
      <div className="empty-state-content">
        <div className="empty-state-title">{displayTitle}</div>
        <div className="empty-state-description">{displayDescription}</div>
        
        {displayActionText && onAction && (
          <Button
            type="primary"
            onClick={onAction}
            className="empty-state-action"
            icon={<InboxOutlined />}
          >
            {displayActionText}
          </Button>
        )}
        
        {extra && <div className="empty-state-extra">{extra}</div>}
      </div>
    </div>
  );
};

export default EmptyState;
