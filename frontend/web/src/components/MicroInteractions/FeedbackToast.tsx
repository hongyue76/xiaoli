import React, { useEffect, useState } from 'react';
import { CheckCircleFilled, CloseCircleFilled, InfoCircleFilled, ExclamationCircleFilled } from '@ant-design/icons';
import './FeedbackToast.css';

export type FeedbackType = 'success' | 'error' | 'info' | 'warning';

interface FeedbackToastProps {
  type: FeedbackType;
  visible: boolean;
  message: string;
  description?: string;
  duration?: number;
  onClose?: () => void;
  position?: 'top' | 'bottom' | 'top-left' | 'top-right' | 'bottom-left' | 'bottom-right';
}

/**
 * 反馈消息提示组件
 *
 * 提供操作反馈的可视化提示：
 * - 成功：绿色对勾
 * - 错误：红色叉号
 * - 信息：蓝色提示
 * - 警告：黄色警告
 *
 * 使用示例：
 * ```tsx
 * <FeedbackToast 
 *   type="success"
 *   visible={showToast}
 *   message="操作成功"
 *   description="数据已保存"
 *   duration={3000}
 *   onClose={() => setShowToast(false)}
 * />
 * ```
 */
const FeedbackToast: React.FC<FeedbackToastProps> = ({
  type,
  visible,
  message,
  description,
  duration = 3000,
  onClose,
  position = 'top',
}) => {
  const [show, setShow] = useState(false);
  const [animate, setAnimate] = useState(false);

  useEffect(() => {
    if (visible) {
      setShow(true);
      setAnimate(true);

      const timer = setTimeout(() => {
        setAnimate(false);
        setTimeout(() => {
          setShow(false);
          onClose?.();
        }, 300);
      }, duration);

      return () => clearTimeout(timer);
    }
  }, [visible, duration, onClose]);

  if (!show) return null;

  const getIcon = () => {
    switch (type) {
      case 'success':
        return <CheckCircleFilled className="feedback-icon success" />;
      case 'error':
        return <CloseCircleFilled className="feedback-icon error" />;
      case 'info':
        return <InfoCircleFilled className="feedback-icon info" />;
      case 'warning':
        return <ExclamationCircleFilled className="feedback-icon warning" />;
      default:
        return <CheckCircleFilled className="feedback-icon success" />;
    }
  };

  const getPositionClass = () => {
    switch (position) {
      case 'top':
        return 'position-top';
      case 'bottom':
        return 'position-bottom';
      case 'top-left':
        return 'position-top-left';
      case 'top-right':
        return 'position-top-right';
      case 'bottom-left':
        return 'position-bottom-left';
      case 'bottom-right':
        return 'position-bottom-right';
      default:
        return 'position-top';
    }
  };

  return (
    <div className={`feedback-toast ${animate ? 'show' : ''} ${getPositionClass()} type-${type}`}>
      <div className="feedback-content">
        {getIcon()}
        <div className="feedback-text">
          <div className="feedback-message">{message}</div>
          {description && <div className="feedback-description">{description}</div>}
        </div>
        <button className="feedback-close" onClick={() => {
          setAnimate(false);
          setTimeout(() => {
            setShow(false);
            onClose?.();
          }, 300);
        }}>
          ×
        </button>
      </div>
    </div>
  );
};

export default FeedbackToast;
