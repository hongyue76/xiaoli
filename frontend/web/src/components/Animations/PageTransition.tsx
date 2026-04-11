import React, { ReactNode } from 'react';
import './PageTransition.css';

export type TransitionType = 'fade' | 'slide-left' | 'slide-right' | 'slide-up' | 'slide-down' | 'zoom';

interface PageTransitionProps {
  children: ReactNode;
  type?: TransitionType;
  duration?: number;
  delay?: number;
  className?: string;
}

/**
 * 页面过渡动画组件
 *
 * 提供多种页面切换动画效果：
 * - fade: 淡入淡出
 * - slide-left: 从左侧滑入
 * - slide-right: 从右侧滑入
 * - slide-up: 从下方滑入
 * - slide-down: 从上方滑入
 * - zoom: 缩放效果
 *
 * 使用示例：
 * ```tsx
 * <PageTransition type="slide-left" duration={300}>
 *   <div>页面内容</div>
 * </PageTransition>
 * ```
 */
const PageTransition: React.FC<PageTransitionProps> = ({
  children,
  type = 'fade',
  duration = 300,
  delay = 0,
  className = '',
}) => {
  const style = {
    '--transition-duration': `${duration}ms`,
    '--transition-delay': `${delay}ms`,
  } as React.CSSProperties;

  return (
    <div
      className={`page-transition page-transition-${type} ${className}`}
      style={style}
    >
      {children}
    </div>
  );
};

export default PageTransition;
