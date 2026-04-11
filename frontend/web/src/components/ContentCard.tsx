/**
 * 通用卡片组件 - 用于内容区域的卡片式设计
 */

import React, { ReactNode } from 'react';
import { Card, CardProps } from 'antd';
import './ContentCard.css';

interface ContentCardProps extends Omit<CardProps, 'className'> {
  title?: ReactNode;
  extra?: ReactNode;
  children: ReactNode;
  hoverable?: boolean;
  shadow?: 'default' | 'light' | 'heavy';
  gradient?: boolean;
}

const ContentCard: React.FC<ContentCardProps> = ({
  title,
  extra,
  children,
  hoverable = true,
  shadow = 'default',
  gradient = false,
  ...restProps
}) => {
  const getShadowClass = () => {
    switch (shadow) {
      case 'light':
        return 'shadow-light';
      case 'heavy':
        return 'shadow-heavy';
      default:
        return 'shadow-default';
    }
  };

  const cardClass = `content-card ${getShadowClass()} ${hoverable ? 'hoverable' : ''} ${gradient ? 'gradient' : ''}`;

  return (
    <Card
      className={cardClass}
      title={title}
      extra={extra}
      bordered={false}
      {...restProps}
    >
      {children}
    </Card>
  );
};

export default ContentCard;
