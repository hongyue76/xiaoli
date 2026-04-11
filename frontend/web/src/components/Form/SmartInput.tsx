/**
 * 智能输入组件
 * 支持自动补全、实时校验、友好提示
 */

import React, { useState, useRef, useEffect } from 'react';
import { Input, AutoComplete, Select, Form, Space, Typography, Tooltip } from 'antd';
import {
  CheckCircleOutlined,
  CloseCircleOutlined,
  InfoCircleOutlined,
  LoadingOutlined,
} from '@ant-design/icons';
import './SmartInput.css';

const { Text } = Typography;

interface SmartInputProps {
  type?: 'text' | 'autocomplete' | 'select';
  label?: string;
  placeholder?: string;
  value?: string;
  onChange?: (value: string) => void;
  onBlur?: () => void;
  options?: Array<{ value: string; label: string; description?: string }>;
  selectOptions?: Array<{ value: string; label: string }>;
  rules?: Array<{ required?: boolean; pattern?: RegExp; message: string; min?: number; max?: number }>;
  loading?: boolean;
  disabled?: boolean;
  autoFocus?: boolean;
  allowClear?: boolean;
  maxLength?: number;
  showCount?: boolean;
  description?: string;
  hint?: string;
  style?: React.CSSProperties;
}

/**
 * 校验结果类型
 */
type ValidationResult = 'success' | 'error' | 'warning' | 'validating' | null;

/**
 * 智能输入组件
 */
const SmartInput: React.FC<SmartInputProps> = ({
  type = 'text',
  label,
  placeholder,
  value,
  onChange,
  onBlur,
  options = [],
  selectOptions = [],
  rules = [],
  loading = false,
  disabled = false,
  autoFocus = false,
  allowClear = true,
  maxLength,
  showCount = false,
  description,
  hint,
  style,
}) => {
  const [internalValue, setInternalValue] = useState(value || '');
  const [validationResult, setValidationResult] = useState<ValidationResult>(null);
  const [errorMessage, setErrorMessage] = useState('');
  const [showSuggestions, setShowSuggestions] = useState(false);
  const inputRef = useRef<any>(null);

  // 同步外部value变化
  useEffect(() => {
    if (value !== undefined) {
      setInternalValue(value);
    }
  }, [value]);

  // 自动聚焦
  useEffect(() => {
    if (autoFocus && inputRef.current) {
      inputRef.current.focus();
    }
  }, [autoFocus]);

  /**
   * 实时校验
   */
  const validate = (val: string): ValidationResult => {
    for (const rule of rules) {
      if (rule.required && !val.trim()) {
        setErrorMessage(rule.message || '此项为必填项');
        return 'error';
      }

      if (rule.min && val.length < rule.min) {
        setErrorMessage(rule.message || `最少输入${rule.min}个字符`);
        return 'warning';
      }

      if (rule.max && val.length > rule.max) {
        setErrorMessage(rule.message || `最多输入${rule.max}个字符`);
        return 'warning';
      }

      if (rule.pattern && !rule.pattern.test(val)) {
        setErrorMessage(rule.message || '格式不正确');
        return 'error';
      }
    }

    setErrorMessage('');
    if (val && rules.length > 0) {
      return 'success';
    }
    return null;
  };

  /**
   * 处理值变化
   */
  const handleChange = (newValue: string) => {
    setInternalValue(newValue);

    // 实时校验
    const result = validate(newValue);
    setValidationResult(result);

    // 显示自动补全建议
    if (type === 'autocomplete' && newValue.length > 0) {
      setShowSuggestions(true);
    }

    onChange?.(newValue);
  };

  /**
   * 处理失去焦点
   */
  const handleBlur = () => {
    setShowSuggestions(false);
    const result = validate(internalValue);
    setValidationResult(result);
    onBlur?.();
  };

  /**
   * 获取校验图标
   */
  const getValidationIcon = () => {
    if (loading) {
      return <LoadingOutlined className="validation-icon loading" />;
    }

    switch (validationResult) {
      case 'success':
        return <CheckCircleOutlined className="validation-icon success" />;
      case 'error':
        return <CloseCircleOutlined className="validation-icon error" />;
      case 'warning':
        return <InfoCircleOutlined className="validation-icon warning" />;
      case 'validating':
        return <LoadingOutlined className="validation-icon validating" />;
      default:
        return null;
    }
  };

  /**
   * 获取校验状态颜色类名
   */
  const getStatusClass = () => {
    if (!validationResult) return '';
    return `smart-input-${validationResult}`;
  };

  /**
   * 渲染普通输入框
   */
  const renderTextInput = () => (
    <Input
      ref={inputRef}
      placeholder={placeholder}
      value={internalValue}
      onChange={(e) => handleChange(e.target.value)}
      onBlur={handleBlur}
      disabled={disabled}
      allowClear={allowClear}
      maxLength={maxLength}
      showCount={showCount}
      className={`smart-input ${getStatusClass()}`}
      suffix={getValidationIcon()}
      style={style}
    />
  );

  /**
   * 渲染自动补全输入框
   */
  const renderAutocompleteInput = () => (
    <AutoComplete
      ref={inputRef}
      placeholder={placeholder}
      value={internalValue}
      onChange={handleChange}
      onBlur={handleBlur}
      disabled={disabled}
      allowClear={allowClear}
      options={options.filter(opt =>
        opt.value.toLowerCase().includes(internalValue.toLowerCase()) ||
        opt.label.toLowerCase().includes(internalValue.toLowerCase())
      )}
      className={`smart-input-autocomplete ${getStatusClass()}`}
      style={style}
      filterOption={false}
    >
      <Input suffix={getValidationIcon()} />
    </AutoComplete>
  );

  /**
   * 渲染选择框
   */
  const renderSelectInput = () => (
    <Select
      ref={inputRef}
      placeholder={placeholder}
      value={internalValue || undefined}
      onChange={(val) => handleChange(val)}
      onBlur={handleBlur}
      disabled={disabled}
      allowClear={allowClear}
      options={selectOptions}
      loading={loading}
      className={`smart-input-select ${getStatusClass()}`}
      style={style}
      suffixIcon={loading ? <LoadingOutlined /> : undefined}
    />
  );

  return (
    <div className="smart-input-wrapper">
      {label && (
        <div className="smart-input-label">
          <Text strong>{label}</Text>
          {description && (
            <Tooltip title={description}>
              <InfoCircleOutlined className="label-icon" />
            </Tooltip>
          )}
        </div>
      )}

      <Space direction="vertical" style={{ width: '100%' }}>
        {type === 'text' && renderTextInput()}
        {type === 'autocomplete' && renderAutocompleteInput()}
        {type === 'select' && renderSelectInput()}

        {/* 错误/警告提示 */}
        {errorMessage && validationResult === 'error' && (
          <Text type="danger" className="error-message">
            {errorMessage}
          </Text>
        )}

        {errorMessage && validationResult === 'warning' && (
          <Text type="warning" className="warning-message">
            <InfoCircleOutlined /> {errorMessage}
          </Text>
        )}

        {/* 提示信息 */}
        {hint && !errorMessage && (
          <Text type="secondary" className="hint-message">
            <InfoCircleOutlined /> {hint}
          </Text>
        )}
      </Space>
    </div>
  );
};

export default SmartInput;
