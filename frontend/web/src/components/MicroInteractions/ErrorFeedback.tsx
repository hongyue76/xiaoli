import React, { useEffect, useState } from 'react';
import { CloseCircleFilled, CloseOutlined } from '@ant-design/icons';
import './ErrorFeedback.css';

interface ErrorFeedbackProps {
  visible: boolean;
  message?: string;
  duration?: number;
  onComplete?: () => void;
}

/**
 * 错误反馈动画组件
 *
 * 提供错误操作的可视化反馈：
 * - 抖动动画
 * - 红色提示
 * - 自动消失
 *
 * 使用示例：
 * ```tsx
 * <ErrorFeedback 
 *   visible={showError} 
 *   message="操作失败" 
 *   duration={2000}
 *   onComplete={() => setShowError(false)}
 * />
 * ```
 */
const ErrorFeedback: React.FC<ErrorFeedbackProps> = ({
  visible,
  message = '操作失败',
  duration = 2000,
  onComplete,
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
          onComplete?.();
        }, 300);
      }, duration);

      return () => clearTimeout(timer);
    }
  }, [visible, duration, onComplete]);

  if (!show) return null;

  return (
    <div className={`error-feedback ${animate ? 'error-animate' : ''}`}>
      <div className="error-feedback-content">
        <div className="error-icon">
          <CloseCircleFilled />
        </div>
        <div className="error-x">
          <CloseOutlined />
        </div>
        {message && <div className="error-message">{message}</div>}
      </div>
    </div>
  );
};

export default ErrorFeedback;
