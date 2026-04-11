/**
 * 加载按钮组件
 * 支持加载状态、防重复提交
 */

import React, { useState } from 'react';
import { Button, ButtonProps, Spin } from 'antd';
import { LoadingOutlined } from '@ant-design/icons';
import './LoadingButton.css';

interface LoadingButtonProps extends Omit<ButtonProps, 'loading'> {
  onClick?: (e: React.MouseEvent<HTMLElement>) => void | Promise<void>;
  loading?: boolean;
  preventDoubleSubmit?: boolean;
  loadingText?: string;
  loadingIcon?: React.ReactNode;
}

/**
 * 加载按钮组件
 */
const LoadingButton: React.FC<LoadingButtonProps> = ({
  onClick,
  loading: externalLoading = false,
  preventDoubleSubmit = true,
  loadingText = '处理中...',
  loadingIcon = <LoadingOutlined />,
  children,
  disabled,
  ...restProps
}) => {
  const [internalLoading, setInternalLoading] = useState(false);
  const isDisabled = disabled || internalLoading || externalLoading;
  const isLoading = internalLoading || externalLoading;

  const handleClick = async (e: React.MouseEvent<HTMLElement>) => {
    if (isLoading || !onClick) {
      return;
    }

    // 防止重复提交
    if (preventDoubleSubmit) {
      setInternalLoading(true);
    }

    try {
      await onClick(e);
    } catch (error) {
      console.error('Button click error:', error);
    } finally {
      if (preventDoubleSubmit) {
        setInternalLoading(false);
      }
    }
  };

  return (
    <Button
      {...restProps}
      disabled={isDisabled}
      onClick={handleClick}
      className={`loading-button ${isLoading ? 'is-loading' : ''} ${restProps.className || ''}`}
    >
      {isLoading ? (
        <span className="loading-content">
          <Spin
            indicator={loadingIcon}
            size="small"
            className="loading-spin"
          />
          <span className="loading-text">{loadingText}</span>
        </span>
      ) : (
        children
      )}
    </Button>
  );
};

export default LoadingButton;
