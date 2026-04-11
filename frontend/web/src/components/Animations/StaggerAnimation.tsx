import React, { ReactNode } from 'react';
import './StaggerAnimation.css';

export type StaggerType = 'fade-up' | 'fade-down' | 'fade-left' | 'fade-right' | 'scale-in' | 'slide-up';

interface StaggerAnimationProps {
  children: ReactNode;
  type?: StaggerType;
  duration?: number;
  delay?: number;
  staggerDelay?: number;
  className?: string;
}

/**
 * 列表交错动画组件
 *
 * 为列表项提供交错进入动画效果，使列表项逐个显示
 *
 * 使用示例：
 * ```tsx
 * <StaggerAnimation type="fade-up" duration={300} staggerDelay={50}>
 *   {items.map((item, index) => (
 *     <div key={item.id} className="stagger-item">
 *       {item.name}
 *     </div>
 *   ))}
 * </StaggerAnimation>
 * ```
 */
const StaggerAnimation: React.FC<StaggerAnimationProps> = ({
  children,
  type = 'fade-up',
  duration = 300,
  delay = 0,
  staggerDelay = 50,
  className = '',
}) => {
  // 将子元素包装并添加延迟
  const childrenWithDelay = React.Children.map(children, (child, index) => {
    if (React.isValidElement(child)) {
      const itemDelay = delay + (index * staggerDelay);
      const style = {
        '--stagger-duration': `${duration}ms`,
        '--stagger-delay': `${itemDelay}ms`,
      } as React.CSSProperties;

      return (
        <div
          className={`stagger-item stagger-item-${type} ${className}`}
          style={style}
        >
          {child}
        </div>
      );
    }
    return child;
  });

  return <div className="stagger-container">{childrenWithDelay}</div>;
};

export default StaggerAnimation;
