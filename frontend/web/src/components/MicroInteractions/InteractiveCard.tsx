import React from 'react';
import { Card, CardProps } from 'antd';
import './InteractiveCard.css';

export type CardHoverEffect = 'lift' | 'glow' | 'border' | 'scale';

interface InteractiveCardProps extends CardProps {
  hoverEffect?: CardHoverEffect;
  clickable?: boolean;
  onClick?: () => void;
}

/**
 * 增强交互卡片组件
 *
 * 在 Ant Design Card 基础上增强交互效果：
 * - 悬停时阴影加深
 * - 悬停时边框高亮
 * - 悬停时轻微上浮
 * - 支持点击效果
 *
 * 使用示例：
 * ```tsx
 * <InteractiveCard hoverEffect="lift" clickable onClick={handleClick}>
 *   <Card.Meta title="标题" description="描述" />
 * </InteractiveCard>
 * ```
 */
const InteractiveCard: React.FC<InteractiveCardProps> = ({
  hoverEffect = 'lift',
  clickable = false,
  onClick,
  children,
  ...props
}) => {
  const handleClick = () => {
    if (clickable && onClick) {
      onClick();
    }
  };

  return (
    <Card
      {...props}
      className={`interactive-card interactive-card-${hoverEffect} ${
        clickable ? 'clickable' : ''
      } ${props.className || ''}`}
      onClick={handleClick}
      style={{
        cursor: clickable ? 'pointer' : 'default',
        ...props.style,
      }}
    >
      {children}
    </Card>
  );
};

export default InteractiveCard;
