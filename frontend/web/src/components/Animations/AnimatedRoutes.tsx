import React, { ReactNode, useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import PageTransition, { TransitionType } from './PageTransition';

interface AnimatedRoutesProps {
  children: ReactNode;
  type?: TransitionType;
  duration?: number;
}

/**
 * 动画路由包装组件
 *
 * 为路由切换添加过渡动画效果，根据路由变化自动触发动画
 *
 * 使用示例：
 * ```tsx
 * <AnimatedRoutes type="slide-left" duration={300}>
 *   <Routes>
 *     <Route path="/" element={<Home />} />
 *     <Route path="/about" element={<About />} />
 *   </Routes>
 * </AnimatedRoutes>
 * ```
 */
const AnimatedRoutes: React.FC<AnimatedRoutesProps> = ({
  children,
  type = 'fade',
  duration = 300,
}) => {
  const location = useLocation();
  const [displayLocation, setDisplayLocation] = useState(location);
  const [transitionStage, setTransitionStage] = useState<'enter' | 'exit'>('enter');

  useEffect(() => {
    if (location !== displayLocation) {
      // 开始退出动画
      setTransitionStage('exit');
    }
  }, [location, displayLocation]);

  const handleAnimationEnd = () => {
    if (transitionStage === 'exit') {
      // 退出动画完成后，切换到新路由
      setDisplayLocation(location);
      setTransitionStage('enter');
    }
  };

  // 使用 React.cloneChildren 传递 location 和 key 给 Routes
  const childrenWithProps = React.Children.map(children, (child) => {
    if (React.isValidElement(child)) {
      return React.cloneElement(child as React.ReactElement, {
        location: displayLocation,
        key: displayLocation.pathname,
      } as any);
    }
    return child;
  });

  return (
    <PageTransition
      type={type}
      duration={duration}
      onAnimationEnd={handleAnimationEnd}
    >
      {childrenWithProps}
    </PageTransition>
  );
};

export default AnimatedRoutes;
