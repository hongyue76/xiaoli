import React, { ReactNode, ReactElement, useState, useEffect } from 'react';
import './ListAnimation.css';

export type ListAnimationType = 'scale' | 'slide' | 'fade' | 'height';

interface ListAnimationProps {
  children: ReactNode;
  type?: ListAnimationType;
  duration?: number;
}

interface AnimateItemProps {
  children: ReactNode;
  isVisible: boolean;
  type?: ListAnimationType;
  duration?: number;
  onEnter?: () => void;
  onExit?: () => void;
}

/**
 * 列表项动画组件
 *
 * 用于单个列表项的添加/删除动画
 *
 * 使用示例：
 * ```tsx
 * <AnimateItem isVisible={show} type="scale" duration={300}>
 *   <div>列表项内容</div>
 * </AnimateItem>
 * ```
 */
const AnimateItem: React.FC<AnimateItemProps> = ({
  children,
  isVisible,
  type = 'scale',
  duration = 300,
  onEnter,
  onExit,
}) => {
  const [mounted, setMounted] = useState(isVisible);
  const [exiting, setExiting] = useState(false);

  useEffect(() => {
    if (isVisible && !mounted) {
      setMounted(true);
      onEnter?.();
    } else if (!isVisible && mounted && !exiting) {
      setExiting(true);
      // 动画完成后卸载
      const timer = setTimeout(() => {
        setMounted(false);
        setExiting(false);
        onExit?.();
      }, duration);
      return () => clearTimeout(timer);
    }
  }, [isVisible, mounted, exiting, duration, onEnter, onExit]);

  if (!mounted) return null;

  const style = {
    '--list-animation-duration': `${duration}ms`,
  } as React.CSSProperties;

  const className = `list-animate-item list-animate-${type} ${
    exiting ? 'exiting' : 'entering'
  }`;

  return <div className={className} style={style}>{children}</div>;
};

/**
 * 列表动画包装组件
 *
 * 为整个列表提供统一的添加/删除动画支持
 *
 * 使用示例：
 * ```tsx
 * <ListAnimation type="scale" duration={300}>
 *   {items.map((item) => (
 *     <AnimateItem key={item.id} isVisible={item.visible}>
 *       <div>{item.name}</div>
 *     </AnimateItem>
 *   ))}
 * </ListAnimation>
 * ```
 */
const ListAnimation: React.FC<ListAnimationProps> = ({
  children,
  type = 'scale',
  duration = 300,
}) => {
  return (
    <div className="list-animation-container">
      {React.Children.map(children, (child) => {
        if (React.isValidElement(child) && child.type === AnimateItem) {
          return React.cloneElement(child as ReactElement, {
            type,
            duration,
          });
        }
        return child;
      })}
    </div>
  );
};

export { AnimateItem };
export default ListAnimation;
