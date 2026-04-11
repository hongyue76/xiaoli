/**
 * 进度反馈组件
 * 用于显示AI生成内容时的进度
 */

import React, { useEffect, useState } from 'react';
import { Progress, Steps, Card, Typography } from 'antd';
import {
  LoadingOutlined,
  CheckCircleOutlined,
  RobotOutlined,
  FileSearchOutlined,
  ThunderboltOutlined,
  BulbOutlined,
} from '@ant-design/icons';
import './ProgressBar.css';

const { Text } = Typography;

interface StepConfig {
  key: string;
  title: string;
  description?: string;
  icon?: React.ReactNode;
}

interface ProgressBarProps {
  steps?: StepConfig[];
  current?: number;
  percent?: number;
  status?: 'active' | 'exception' | 'normal' | 'success';
  showSteps?: boolean;
  showPercent?: boolean;
  type?: 'default' | 'line' | 'circle' | 'dashboard';
}

const defaultSteps: StepConfig[] = [
  {
    key: 'thinking',
    title: '正在思考',
    description: '分析您的问题',
    icon: <RobotOutlined />,
  },
  {
    key: 'searching',
    title: '查阅法条',
    description: '搜索相关法律条文',
    icon: <FileSearchOutlined />,
  },
  {
    key: 'analyzing',
    title: '分析案情',
    description: '整理法律要点',
    icon: <ThunderboltOutlined />,
  },
  {
    key: 'generating',
    title: '生成回复',
    description: '撰写法律建议',
    icon: <BulbOutlined />,
  },
];

/**
 * AI进度等待文案
 */
const waitingMessages = [
  '正在思考中...',
  '查阅法条中...',
  '分析案情中...',
  '生成回复中...',
  '整理法律要点...',
  '搜索相关案例...',
  '撰写法律建议...',
  '即将完成...',
];

/**
 * 随机获取等待文案
 */
const getRandomMessage = () => {
  return waitingMessages[Math.floor(Math.random() * waitingMessages.length)];
};

/**
 * 进度条组件
 */
const ProgressBar: React.FC<ProgressBarProps> = ({
  steps = defaultSteps,
  current = 0,
  percent = 0,
  status = 'active',
  showSteps = true,
  showPercent = true,
  type = 'default',
}) => {
  const [displayMessage, setDisplayMessage] = useState<string>(waitingMessages[0]);
  const [displayPercent, setDisplayPercent] = useState<number>(0);

  // 动态更新等待文案
  useEffect(() => {
    const interval = setInterval(() => {
      setDisplayMessage(getRandomMessage());
    }, 2000);

    return () => clearInterval(interval);
  }, []);

  // 平滑进度条动画
  useEffect(() => {
    let start = displayPercent;
    const end = percent;
    const duration = 500;
    const startTime = performance.now();

    const animate = (currentTime: number) => {
      const elapsed = currentTime - startTime;
      const progress = Math.min(elapsed / duration, 1);
      const easeProgress = 1 - Math.pow(1 - progress, 3); // cubic easing

      setDisplayPercent(start + (end - start) * easeProgress);

      if (progress < 1) {
        requestAnimationFrame(animate);
      }
    };

    requestAnimationFrame(animate);
  }, [percent]);

  const renderDefault = () => (
    <Card className="progress-card">
      <div className="progress-header">
        <LoadingOutlined className="progress-icon" spin />
        <Text className="progress-message">{displayMessage}</Text>
      </div>
      {showPercent && (
        <Progress
          percent={Math.round(displayPercent)}
          status={status}
          strokeColor={{
            '0%': '#003a8c',
            '50%': '#096dd9',
            '100%': '#faad14',
          }}
          className="progress-bar"
        />
      )}
      {showSteps && (
        <Steps
          current={current}
          size="small"
          className="progress-steps"
          items={steps.map(step => ({
            title: step.title,
            description: step.description,
            icon: step.icon,
          }))}
        />
      )}
    </Card>
  );

  const renderLine = () => (
    <div className="progress-line-wrapper">
      <Text className="progress-message">{displayMessage}</Text>
      <Progress
        percent={Math.round(displayPercent)}
        status={status}
        strokeColor={{
          '0%': '#003a8c',
          '100%': '#faad14',
        }}
      />
    </div>
  );

  const renderCircle = () => (
    <div className="progress-circle-wrapper">
      <Progress
        type="circle"
        percent={Math.round(displayPercent)}
        status={status}
        strokeColor={{
          '0%': '#003a8c',
          '50%': '#096dd9',
          '100%': '#faad14',
        }}
      />
      <Text className="progress-message">{displayMessage}</Text>
    </div>
  );

  const renderDashboard = () => (
    <div className="progress-dashboard-wrapper">
      <Progress
        type="dashboard"
        percent={Math.round(displayPercent)}
        status={status}
        strokeColor={{
          '0%': '#003a8c',
          '50%': '#096dd9',
          '100%': '#faad14',
        }}
        gapDegree={120}
      />
      <Text className="progress-message">{displayMessage}</Text>
      {showSteps && (
        <Steps
          current={current}
          direction="vertical"
          size="small"
          className="progress-steps-vertical"
          items={steps.map(step => ({
            title: step.title,
            description: step.description,
            icon: step.icon,
          }))}
        />
      )}
    </div>
  );

  switch (type) {
    case 'line':
      return renderLine();
    case 'circle':
      return renderCircle();
    case 'dashboard':
      return renderDashboard();
    default:
      return renderDefault();
  }
};

export default ProgressBar;
export { defaultSteps, waitingMessages };
