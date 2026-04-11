/**
 * 骨架屏加载组件
 * 用于在内容加载时提供视觉反馈
 */

import React from 'react';
import { Skeleton, Row, Col, Card } from 'antd';
import './SkeletonLoader.css';

interface SkeletonLoaderProps {
  type?: 'list' | 'card' | 'text' | 'avatar' | 'input';
  rows?: number;
  count?: number;
  cols?: number;
  active?: boolean;
  title?: boolean;
  avatar?: boolean;
  paragraph?: boolean;
}

/**
 * 列表骨架屏
 */
const ListSkeleton: React.FC<{ count: number; avatar?: boolean }> = ({ count, avatar }) => {
  return (
    <div className="skeleton-list">
      {Array.from({ length: count }).map((_, index) => (
        <div key={index} className="skeleton-list-item">
          <Skeleton active avatar={avatar} paragraph={{ rows: 1 }} />
        </div>
      ))}
    </div>
  );
};

/**
 * 卡片骨架屏
 */
const CardSkeleton: React.FC<{ count: number; cols: number }> = ({ count, cols }) => {
  const cards = Array.from({ length: count }).map((_, index) => (
    <Col key={index} span={24 / cols}>
      <Card className="skeleton-card">
        <Skeleton active avatar title paragraph={{ rows: 3 }} />
      </Card>
    </Col>
  ));

  return <Row gutter={[16, 16]}>{cards}</Row>;
};

/**
 * 文本骨架屏
 */
const TextSkeleton: React.FC<{ rows: number }> = ({ rows }) => {
  return (
    <div className="skeleton-text">
      <Skeleton active title paragraph={{ rows }} />
    </div>
  );
};

/**
 * 头像骨架屏
 */
const AvatarSkeleton: React.FC<{ count: number }> = ({ count }) => {
  return (
    <div className="skeleton-avatars">
      {Array.from({ length: count }).map((_, index) => (
        <Skeleton.Avatar key={index} active size="large" shape="circle" />
      ))}
    </div>
  );
};

/**
 * 输入框骨架屏
 */
const InputSkeleton: React.FC<{ count: number }> = ({ count }) => {
  return (
    <div className="skeleton-inputs">
      {Array.from({ length: count }).map((_, index) => (
        <Skeleton.Input key={index} active size="large" style={{ width: '100%', marginBottom: 12 }} />
      ))}
    </div>
  );
};

/**
 * 骨架屏主组件
 */
const SkeletonLoader: React.FC<SkeletonLoaderProps> = ({
  type = 'list',
  rows = 3,
  count = 3,
  cols = 3,
  active = true,
  title = true,
  avatar = true,
  paragraph = true,
}) => {
  switch (type) {
    case 'list':
      return <ListSkeleton count={count} avatar={avatar} />;
    case 'card':
      return <CardSkeleton count={count} cols={cols} />;
    case 'text':
      return <TextSkeleton rows={rows} />;
    case 'avatar':
      return <AvatarSkeleton count={count} />;
    case 'input':
      return <InputSkeleton count={count} />;
    default:
      return <Skeleton active={active} title={title} avatar={avatar} paragraph={paragraph} />;
  }
};

export default SkeletonLoader;
