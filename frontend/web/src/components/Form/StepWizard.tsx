/**
 * 步骤引导组件
 * 支持分步表单、进度显示、预计用时
 */

import React, { useState, useEffect } from 'react';
import { Steps, Button, Card, Space, Typography, Divider, Progress } from 'antd';
import {
  LeftOutlined,
  RightOutlined,
  ClockCircleOutlined,
  CheckCircleOutlined,
} from '@ant-design/icons';
import './StepWizard.css';

const { Title, Text, Paragraph } = Typography;

interface Step {
  key: string;
  title: string;
  description?: string;
  content: React.ReactNode;
  estimatedTime?: number; // 预计用时（分钟）
  icon?: React.ReactNode;
  validate?: () => boolean | Promise<boolean>;
}

interface StepWizardProps {
  steps: Step[];
  onFinish?: (data: any) => void;
  onCancel?: () => void;
  onFinishFailed?: () => void;
  initialStep?: number;
  showEstimatedTime?: boolean;
  showProgress?: boolean;
  allowSkip?: boolean;
  showStepButtons?: boolean;
}

/**
 * 步骤引导组件
 */
const StepWizard: React.FC<StepWizardProps> = ({
  steps,
  onFinish,
  onCancel,
  onFinishFailed,
  initialStep = 0,
  showEstimatedTime = true,
  showProgress = true,
  allowSkip = false,
  showStepButtons = true,
}) => {
  const [currentStep, setCurrentStep] = useState(initialStep);
  const [stepData, setStepData] = useState<Record<string, any>>({});
  const [isValidating, setIsValidating] = useState(false);
  const [validationErrors, setValidationErrors] = useState<Record<string, string>>({});
  const [completedSteps, setCompletedSteps] = useState<Set<string>>(new Set());

  // 计算总进度
  const progress = ((currentStep + 1) / steps.length) * 100;

  // 计算总预计用时
  const totalEstimatedTime = steps.reduce((sum, step) => sum + (step.estimatedTime || 0), 0);

  // 当前步骤
  const currentStepData = steps[currentStep];

  /**
   * 验证当前步骤
   */
  const validateCurrentStep = async (): Promise<boolean> => {
    setIsValidating(true);
    setValidationErrors({});

    if (currentStepData.validate) {
      try {
        const isValid = await currentStepData.validate();
        setIsValidating(false);

        if (!isValid) {
          return false;
        }
      } catch (error: any) {
        setValidationErrors({ [currentStep]: error.message || '验证失败' });
        setIsValidating(false);
        return false;
      }
    }

    setIsValidating(false);
    return true;
  };

  /**
   * 上一步
   */
  const handlePrevious = () => {
    if (currentStep > 0) {
      setCurrentStep(currentStep - 1);
    }
  };

  /**
   * 下一步
   */
  const handleNext = async () => {
    const isValid = await validateCurrentStep();
    if (isValid) {
      // 标记当前步骤为完成
      setCompletedSteps(prev => new Set([...prev, currentStepData.key]));
      setCurrentStep(currentStep + 1);
    } else if (allowSkip) {
      setCurrentStep(currentStep + 1);
    }
  };

  /**
   * 跳转到指定步骤
   */
  const handleStepChange = async (step: number) => {
    if (step < currentStep) {
      // 可以跳回到已完成的步骤
      setCurrentStep(step);
    } else if (allowSkip) {
      setCurrentStep(step);
    } else {
      // 需要先验证前面的步骤
      const isValid = await validateCurrentStep();
      if (isValid) {
        setCompletedSteps(prev => new Set([...prev, currentStepData.key]));
        setCurrentStep(step);
      }
    }
  };

  /**
   * 完成所有步骤
   */
  const handleFinish = async () => {
    const isValid = await validateCurrentStep();
    if (isValid) {
      // 标记最后一步为完成
      setCompletedSteps(prev => new Set([...prev, currentStepData.key]));
      onFinish?.(stepData);
    } else if (allowSkip) {
      onFinish?.(stepData);
    } else {
      onFinishFailed?.();
    }
  };

  /**
   * 取消操作
   */
  const handleCancel = () => {
    onCancel?.();
  };

  /**
   * 保存步骤数据
   */
  const saveStepData = (data: any) => {
    setStepData(prev => ({
      ...prev,
      [currentStepData.key]: data,
    }));
  };

  // 将保存数据函数传递给子组件
  useEffect(() => {
    // 这里可以通过context或其他方式传递给子组件
  }, [currentStep]);

  return (
    <div className="step-wizard">
      {/* 步骤进度条 */}
      {showProgress && (
        <div className="wizard-progress">
          <div className="progress-header">
            <Text className="progress-text">
              进度：{currentStep + 1} / {steps.length}
            </Text>
            {showEstimatedTime && totalEstimatedTime > 0 && (
              <Text className="time-estimate">
                <ClockCircleOutlined />
                预计总用时：{totalEstimatedTime} 分钟
              </Text>
            )}
          </div>
          <Progress
            percent={progress}
            strokeColor={{
              '0%': '#003a8c',
              '100%': '#faad14',
            }}
            showInfo={false}
          />
        </div>
      )}

      {/* 步骤指示器 */}
      <Steps
        current={currentStep}
        onChange={handleStepChange}
        className="wizard-steps"
        items={steps.map((step, index) => ({
          title: step.title,
          description: step.description,
          icon: completedSteps.has(step.key) ? <CheckCircleOutlined /> : step.icon,
          disabled: index > currentStep && !allowSkip,
        }))}
      />

      <Divider className="step-divider" />

      {/* 步骤内容 */}
      <Card className="wizard-card">
        <div className="wizard-header">
          <Title level={4}>{currentStepData.title}</Title>
          {currentStepData.description && (
            <Paragraph type="secondary">{currentStepData.description}</Paragraph>
          )}
          {showEstimatedTime && currentStepData.estimatedTime && (
            <Text type="secondary" className="step-time">
              <ClockCircleOutlined />
              预计用时：{currentStepData.estimatedTime} 分钟
            </Text>
          )}
        </div>

        <div className="wizard-content">
          {React.cloneElement(currentStepData.content as React.ReactElement, {
            onSave: saveStepData,
            data: stepData[currentStepData.key],
          })}
        </div>

        {/* 验证错误提示 */}
        {validationErrors[currentStep] && (
          <div className="validation-error">
            <Text type="warning">
              ⚠️ {validationErrors[currentStep]}
            </Text>
          </div>
        )}

        {/* 操作按钮 */}
        {showStepButtons && (
          <div className="wizard-footer">
            <Space>
              {currentStep > 0 && (
                <Button
                  icon={<LeftOutlined />}
                  onClick={handlePrevious}
                >
                  上一步
                </Button>
              )}

              {currentStep < steps.length - 1 ? (
                <Button
                  type="primary"
                  icon={<RightOutlined />}
                  onClick={handleNext}
                  loading={isValidating}
                >
                  下一步
                </Button>
              ) : (
                <Button
                  type="primary"
                  icon={<CheckCircleOutlined />}
                  onClick={handleFinish}
                  loading={isValidating}
                >
                  完成
                </Button>
              )}

              {onCancel && (
                <Button onClick={handleCancel}>
                  取消
                </Button>
              )}
            </Space>
          </div>
        )}
      </Card>
    </div>
  );
};

export default StepWizard;
