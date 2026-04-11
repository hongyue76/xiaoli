import React, { useEffect, useState } from 'react';
import { CheckCircleFilled, CheckOutlined } from '@ant-design/icons';
import './SuccessFeedback.css';

interface SuccessFeedbackProps {
  visible: boolean;
  message?: string;
  duration?: number;
  onComplete?: () => void;
}

/**
 * 成功反馈动画组件
 *
 * 提供成功操作的可视化反馈：
 * - 对勾动画
 * - 绿色提示
 * - 自动消失
 *
 * 使用示例：
 * ```tsx
 * <SuccessFeedback 
 *   visible={showSuccess} 
 *   message="操作成功" 
 *   duration={2000}
 *   onComplete={() => setShowSuccess(false)}
 * />
 * ```
 */
const SuccessFeedback: React.FC<SuccessFeedbackProps> = ({
  visible,
  message = '操作成功',
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
    <div className={`success-feedback ${animate ? 'success-animate' : ''}`}>
      <div className="success-feedback-content">
        <div className="success-icon">
          <CheckCircleFilled />
        </div>
        <div className="success-check">
          <CheckOutlined />
        </div>
        {message && <div className="success-message">{message}</div>}
      </div>
    </div>
  );
};

export default SuccessFeedback;
