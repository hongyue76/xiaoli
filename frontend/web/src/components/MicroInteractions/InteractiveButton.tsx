import React from 'react';
import { Button, ButtonProps } from 'antd';
import './InteractiveButton.css';

/**
 * 增强交互按钮组件
 *
 * 在 Ant Design Button 基础上增强交互效果：
 * - 悬停时颜色加深
 * - 悬停时轻微上浮
 * - 点击时有按压效果
 * - 加载时有旋转动画
 *
 * 使用示例：
 * ```tsx
 * <InteractiveButton type="primary" loading={isLoading}>
 *   提交
 * </InteractiveButton>
 * ```
 */
const InteractiveButton: React.FC<ButtonProps> = (props) => {
  return (
    <Button
      {...props}
      className={`interactive-button ${props.className || ''} ${
        props.loading ? 'loading' : ''
      }`}
    >
      {props.children}
    </Button>
  );
};

export default InteractiveButton;
