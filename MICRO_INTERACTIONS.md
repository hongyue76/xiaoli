# 微交互反馈文档

本文档详细说明了律法先锋法律AI助手的微交互反馈功能。

## 目录

1. [微交互概述](#微交互概述)
2. [按钮悬停交互](#按钮悬停交互)
3. [卡片悬停交互](#卡片悬停交互)
4. [成功反馈动画](#成功反馈动画)
5. [错误反馈动画](#错误反馈动画)
6. [反馈消息提示](#反馈消息提示)
7. [使用示例](#使用示例)
8. [最佳实践](#最佳实践)

---

## 微交互概述

微交互（Micro-interactions）是指用户界面中的小型交互设计，通过视觉、触觉或声音反馈来增强用户体验。

### 微交互的作用

- **即时反馈**：告诉用户操作已被识别
- **引导用户**：通过动画提示可交互元素
- **提升满意度**：让交互更有趣、更流畅
- **降低认知负担**：直观的状态变化

### 设计原则

- ✅ **即时性**：反馈应在 100ms 内出现
- ✅ **一致性**：相似操作应有相似反馈
- ✅ **适度性**：不干扰用户主流程
- ✅ **可预测性**：符合用户心理预期

---

## 按钮悬停交互

### InteractiveButton 组件

增强 Ant Design Button 的交互效果，提供丰富的悬停和点击反馈。

### 交互效果

| 效果 | 说明 | 持续时间 |
|------|------|----------|
| 颜色加深 | 悬停时颜色加深 10-15% | 300ms |
| 轻微上浮 | 悬停时向上移动 2-4px | 300ms |
| 阴影增强 | 悬停时阴影加深并扩大 | 300ms |
| 按压效果 | 点击时向下移动并缩小 | 瞬时 |
| 波纹效果 | 点击时产生波纹扩散 | 600ms |

### 使用示例

```tsx
import { InteractiveButton } from '@/components/MicroInteractions';

// 基础用法
<InteractiveButton type="primary">提交</InteractiveButton>

// 带加载状态
<InteractiveButton type="primary" loading={isLoading}>
  {isLoading ? '提交中...' : '提交'}
</InteractiveButton>

// 危险操作
<InteractiveButton type="primary" danger>
  删除
</InteractiveButton>

// 不同尺寸
<InteractiveButton size="large">大按钮</InteractiveButton>
<InteractiveButton>默认按钮</InteractiveButton>
<InteractiveButton size="small">小按钮</InteractiveButton>
```

### 组件参数

继承 Ant Design Button 的所有属性。

### 效果展示

```tsx
// 主要按钮 - 蓝色主题
<InteractiveButton type="primary">
  悬停时：颜色加深 + 向上浮动 2px + 蓝色阴影
</InteractiveButton>

// 默认按钮 - 白色背景
<InteractiveButton>
  悬停时：边框变蓝 + 向上浮动 2px + 灰色阴影
</InteractiveButton>

// 危险按钮 - 红色主题
<InteractiveButton type="primary" danger>
  悬停时：颜色加深 + 向上浮动 2px + 红色阴影
</InteractiveButton>

// 文本按钮 - 无背景
<InteractiveButton type="link">
  悬停时：颜色变蓝 + 向上浮动 1px
</InteractiveButton>
```

---

## 卡片悬停交互

### InteractiveCard 组件

增强 Ant Design Card 的交互效果，提供多种悬停效果。

### 支持的悬停效果

| 效果类型 | 说明 | 适用场景 |
|----------|------|----------|
| `lift` | 轻微上浮 + 阴影加深 | 通用卡片 |
| `glow` | 发光效果 + 边框高亮 | 重点内容 |
| `border` | 边框高亮 + 轻微阴影 | 可点击卡片 |
| `scale` | 轻微放大 + 阴影加深 | 图片卡片 |

### 使用示例

```tsx
import { InteractiveCard, CardHoverEffect } from '@/components/MicroInteractions';

// 上浮效果
<InteractiveCard hoverEffect="lift">
  <Card.Meta title="标题" description="描述" />
</InteractiveCard>

// 发光效果
<InteractiveCard hoverEffect="glow">
  <Card.Meta title="标题" description="描述" />
</InteractiveCard>

// 可点击卡片
<InteractiveCard hoverEffect="border" clickable onClick={handleClick}>
  <Card.Meta title="标题" description="描述" />
</InteractiveCard>

// 缩放效果
<InteractiveCard hoverEffect="scale">
  <img src="image.jpg" alt="图片" />
</InteractiveCard>
```

### 组件参数

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `hoverEffect` | `CardHoverEffect` | `'lift'` | 悬停效果类型 |
| `clickable` | `boolean` | `false` | 是否可点击 |
| `onClick` | `() => void` | `undefined` | 点击回调 |

继承 Ant Design Card 的所有其他属性。

### 效果展示

```tsx
// 上浮效果
<InteractiveCard hoverEffect="lift">
  {/* 内容 */}
</InteractiveCard>
// 悬停时：向上浮动 4px + 阴影加深

// 发光效果
<InteractiveCard hoverEffect="glow">
  {/* 内容 */}
</InteractiveCard>
// 悬停时：蓝色发光边框 + 蓝色阴影

// 边框高亮
<InteractiveCard hoverEffect="border" clickable>
  {/* 内容 */}
</InteractiveCard>
// 悬停时：蓝色边框 + 可点击光标

// 缩放效果
<InteractiveCard hoverEffect="scale">
  {/* 内容 */}
</InteractiveCard>
// 悬停时：放大到 1.02 倍 + 阴影加深
```

---

## 成功反馈动画

### SuccessFeedback 组件

提供成功操作的可视化反馈，包括对勾动画和绿色提示。

### 动画效果

| 效果 | 说明 | 时序 |
|------|------|------|
| 图标缩放 | 圆形图标从 0 放大到 1.2 倍再到 1 | 0-400ms |
| 对勾动画 | 白色对勾从 0 缩放到 1 并旋转 | 200-600ms |
| 消息淡入 | 成功消息从下方淡入 | 300-700ms |
| 自动消失 | 所有元素淡出 | duration-300ms 到 duration |

### 使用示例

```tsx
import { SuccessFeedback } from '@/components/MicroInteractions';

function MyComponent() {
  const [showSuccess, setShowSuccess] = useState(false);

  const handleSuccess = () => {
    setShowSuccess(true);
    // 2秒后自动消失
    setTimeout(() => setShowSuccess(false), 2000);
  };

  return (
    <div>
      <button onClick={handleSuccess}>触发成功</button>

      <SuccessFeedback
        visible={showSuccess}
        message="操作成功"
        duration={2000}
        onComplete={() => setShowSuccess(false)}
      />
    </div>
  );
}
```

### 组件参数

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `visible` | `boolean` | 必填 | 是否显示 |
| `message` | `string` | `'操作成功'` | 成功消息 |
| `duration` | `number` | `2000` | 显示持续时间（毫秒） |
| `onComplete` | `() => void` | `undefined` | 动画完成回调 |

### 实际应用案例

```tsx
// 表单提交成功
<SuccessFeedback
  visible={formSubmitted}
  message="提交成功"
  duration={2000}
/>

// 文件上传成功
<SuccessFeedback
  visible={uploadSuccess}
  message="文件上传成功"
  duration={3000}
/>

// 数据保存成功
<SuccessFeedback
  visible={saveSuccess}
  message="保存成功"
  duration={1500}
/>
```

---

## 错误反馈动画

### ErrorFeedback 组件

提供错误操作的可视化反馈，包括抖动动画和红色提示。

### 动画效果

| 效果 | 说明 | 时序 |
|------|------|------|
| 图标缩放 | 圆形图标从 0 放大到 1.2 倍再到 1 | 0-400ms |
| 抖动动画 | 图标左右抖动 5 次 | 400-900ms |
| X 图标 | 红色 X 图标显示并旋转抖动 | 200-900ms |
| 消息淡入 | 错误消息从下方淡入并抖动 | 300-900ms |
| 自动消失 | 所有元素淡出 | duration-300ms 到 duration |

### 使用示例

```tsx
import { ErrorFeedback } from '@/components/MicroInteractions';

function MyComponent() {
  const [showError, setShowError] = useState(false);

  const handleError = () => {
    setShowError(true);
    // 2秒后自动消失
    setTimeout(() => setShowError(false), 2000);
  };

  return (
    <div>
      <button onClick={handleError}>触发错误</button>

      <ErrorFeedback
        visible={showError}
        message="操作失败"
        duration={2000}
        onComplete={() => setShowError(false)}
      />
    </div>
  );
}
```

### 组件参数

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `visible` | `boolean` | 必填 | 是否显示 |
| `message` | `string` | `'操作失败'` | 错误消息 |
| `duration` | `number` | `2000` | 显示持续时间（毫秒） |
| `onComplete` | `() => void` | `undefined` | 动画完成回调 |

### 实际应用案例

```tsx
// 表单验证失败
<ErrorFeedback
  visible={validationError}
  message="请填写所有必填项"
  duration={2000}
/>

// 网络请求失败
<ErrorFeedback
  visible={requestError}
  message="网络请求失败，请重试"
  duration={3000}
/>

// 删除失败
<ErrorFeedback
  visible={deleteError}
  message="删除失败，请稍后重试"
  duration={2500}
/>
```

---

## 反馈消息提示

### FeedbackToast 组件

提供轻量级的消息提示，支持多种类型和位置。

### 支持的反馈类型

| 类型 | 图标 | 颜色 | 使用场景 |
|------|------|------|----------|
| `success` | 对勾 | 绿色 | 操作成功 |
| `error` | 叉号 | 红色 | 操作失败 |
| `info` | 信息 | 蓝色 | 信息提示 |
| `warning` | 警告 | 黄色 | 警告提示 |

### 支持的位置

| 位置 | 说明 |
|------|------|
| `top` | 顶部居中（默认） |
| `bottom` | 底部居中 |
| `top-left` | 左上角 |
| `top-right` | 右上角 |
| `bottom-left` | 左下角 |
| `bottom-right` | 右下角 |

### 使用示例

```tsx
import { FeedbackToast, FeedbackType } from '@/components/MicroInteractions';

function MyComponent() {
  const [toast, setToast] = useState<{
    visible: boolean;
    type: FeedbackType;
    message: string;
  }>({ visible: false, type: 'success', message: '' });

  const showToast = (type: FeedbackType, message: string) => {
    setToast({ visible: true, type, message });
    setTimeout(() => setToast(prev => ({ ...prev, visible: false })), 3000);
  };

  return (
    <div>
      <button onClick={() => showToast('success', '操作成功')}>
        显示成功提示
      </button>
      <button onClick={() => showToast('error', '操作失败')}>
        显示错误提示
      </button>
      <button onClick={() => showToast('info', '这是一个提示')}>
        显示信息提示
      </button>
      <button onClick={() => showToast('warning', '这是一个警告')}>
        显示警告提示
      </button>

      <FeedbackToast
        type={toast.type}
        visible={toast.visible}
        message={toast.message}
        duration={3000}
        onClose={() => setToast(prev => ({ ...prev, visible: false }))}
        position="top"
      />
    </div>
  );
}
```

### 组件参数

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `type` | `FeedbackType` | 必填 | 反馈类型 |
| `visible` | `boolean` | 必填 | 是否显示 |
| `message` | `string` | 必填 | 消息内容 |
| `description` | `string` | `undefined` | 消息描述 |
| `duration` | `number` | `3000` | 显示持续时间（毫秒） |
| `onClose` | `() => void` | `undefined` | 关闭回调 |
| `position` | `string` | `'top'` | 显示位置 |

### 实际应用案例

```tsx
// 顶部提示
<FeedbackToast
  type="success"
  visible={toastVisible}
  message="数据已保存"
  position="top"
/>

// 底部提示
<FeedbackToast
  type="info"
  visible={toastVisible}
  message="新版本可用"
  position="bottom"
/>

// 右上角提示
<FeedbackToast
  type="warning"
  visible={toastVisible}
  message="账户即将过期"
  position="top-right"
/>

// 带描述的提示
<FeedbackToast
  type="success"
  visible={toastVisible}
  message="上传成功"
  description="共 3 个文件，2.5MB"
  position="top"
/>
```

---

## 使用示例

### 完整示例：带微交互的表单

```tsx
import React, { useState } from 'react';
import { Form, Input, Button } from 'antd';
import {
  InteractiveButton,
  InteractiveCard,
  SuccessFeedback,
  ErrorFeedback,
  FeedbackToast,
  FeedbackType
} from '@/components/MicroInteractions';

function ContactForm() {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [showSuccess, setShowSuccess] = useState(false);
  const [showError, setShowError] = useState(false);
  const [toast, setToast] = useState<{
    visible: boolean;
    type: FeedbackType;
    message: string;
  }>({ visible: false, type: 'success', message: '' });

  const handleSubmit = async (values: any) => {
    setLoading(true);
    try {
      // 模拟 API 请求
      await new Promise(resolve => setTimeout(resolve, 1500));

      // 显示成功反馈
      setShowSuccess(true);
      setToast({
        visible: true,
        type: 'success',
        message: '表单提交成功'
      });

      // 重置表单
      form.resetFields();
    } catch (error) {
      // 显示错误反馈
      setShowError(true);
      setToast({
        visible: true,
        type: 'error',
        message: '提交失败，请重试'
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: 600, margin: '0 auto' }}>
      <InteractiveCard hoverEffect="lift">
        <h2>联系我们</h2>
        <Form form={form} onFinish={handleSubmit} layout="vertical">
          <Form.Item
            name="name"
            label="姓名"
            rules={[{ required: true, message: '请输入姓名' }]}
          >
            <Input placeholder="请输入姓名" />
          </Form.Item>

          <Form.Item
            name="email"
            label="邮箱"
            rules={[
              { required: true, message: '请输入邮箱' },
              { type: 'email', message: '请输入有效的邮箱地址' }
            ]}
          >
            <Input placeholder="请输入邮箱" />
          </Form.Item>

          <Form.Item
            name="message"
            label="消息"
            rules={[{ required: true, message: '请输入消息' }]}
          >
            <Input.TextArea rows={4} placeholder="请输入消息" />
          </Form.Item>

          <Form.Item>
            <InteractiveButton
              type="primary"
              htmlType="submit"
              loading={loading}
              block
            >
              {loading ? '提交中...' : '提交'}
            </InteractiveButton>
          </Form.Item>
        </Form>
      </InteractiveCard>

      {/* 反馈组件 */}
      <SuccessFeedback
        visible={showSuccess}
        message="提交成功"
        duration={2000}
        onComplete={() => setShowSuccess(false)}
      />

      <ErrorFeedback
        visible={showError}
        message="提交失败"
        duration={2000}
        onComplete={() => setShowError(false)}
      />

      <FeedbackToast
        type={toast.type}
        visible={toast.visible}
        message={toast.message}
        duration={3000}
        onClose={() => setToast(prev => ({ ...prev, visible: false }))}
        position="top"
      />
    </div>
  );
}
```

---

## 最佳实践

### 1. 按钮交互

```tsx
// ✅ 推荐：使用 InteractiveButton
<InteractiveButton type="primary" loading={loading}>
  提交
</InteractiveButton>

// ❌ 避免：使用普通 Button 缺少交互反馈
<Button type="primary">提交</Button>
```

### 2. 卡片交互

```tsx
// ✅ 推荐：根据场景选择合适的效果
<InteractiveCard hoverEffect="lift">
  通用内容卡片
</InteractiveCard>

<InteractiveCard hoverEffect="glow" clickable>
  重点内容卡片
</InteractiveCard>

<InteractiveCard hoverEffect="scale">
  图片展示卡片
</InteractiveCard>
```

### 3. 反馈时机

```tsx
// ✅ 推荐：及时反馈
const handleSubmit = async () => {
  setLoading(true);
  try {
    await submitData();
    setShowSuccess(true); // 立即显示成功反馈
  } catch (error) {
    setShowError(true); // 立即显示错误反馈
  } finally {
    setLoading(false);
  }
};

// ❌ 避免：延迟反馈
const handleSubmit = async () => {
  await submitData();
  setTimeout(() => setShowSuccess(true), 1000); // 不必要的延迟
};
```

### 4. 反馈持续时间

```tsx
// ✅ 推荐：根据内容调整持续时间
<SuccessFeedback duration={2000} />  // 短消息：2秒
<ErrorFeedback duration={3000} />    // 错误消息：3秒
<FeedbackToast duration={3000} />   // Toast：3秒

// ❌ 避免：时间过长或过短
<SuccessFeedback duration={5000} /> // 太长，用户不耐烦
<SuccessFeedback duration={500} />  // 太短，用户看不清
```

### 5. 反馈组合使用

```tsx
// ✅ 推荐：多种反馈组合
const handleAction = async () => {
  setLoading(true); // 按钮加载状态
  try {
    await performAction();
    setShowSuccess(true); // 成功动画
    showToast('success', '操作成功'); // Toast 提示
  } catch (error) {
    setShowError(true); // 错误动画
    showToast('error', '操作失败'); // Toast 提示
  } finally {
    setLoading(false);
  }
};
```

---

## 性能优化

### 1. 使用 GPU 加速

```css
/* ✅ 推荐：使用 transform 和 opacity */
.button:hover {
  transform: translateY(-2px);
  opacity: 0.8;
}

/* ❌ 避免：使用会触发重排的属性 */
.button:hover {
  top: -2px;  /* 触发重排 */
}
```

### 2. 减少重绘

```css
/* ✅ 推荐：使用 will-change */
.button {
  will-change: transform;
}

/* ❌ 避免：频繁改变多个属性 */
.button:hover {
  transform: translateY(-2px);
  background-color: #1890ff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}
```

### 3. 尊重用户偏好

```css
/* 组件已内置 prefers-reduced-motion 支持 */
@media (prefers-reduced-motion: reduce) {
  .interactive-button {
    transition: none !important;
  }
}
```

---

## 组件 API 文档

### InteractiveButton

继承 Ant Design Button 的所有属性。

### InteractiveCard

```tsx
interface InteractiveCardProps extends CardProps {
  hoverEffect?: 'lift' | 'glow' | 'border' | 'scale';
  clickable?: boolean;
  onClick?: () => void;
}
```

### SuccessFeedback

```tsx
interface SuccessFeedbackProps {
  visible: boolean;
  message?: string;
  duration?: number;
  onComplete?: () => void;
}
```

### ErrorFeedback

```tsx
interface ErrorFeedbackProps {
  visible: boolean;
  message?: string;
  duration?: number;
  onComplete?: () => void;
}
```

### FeedbackToast

```tsx
interface FeedbackToastProps {
  type: 'success' | 'error' | 'info' | 'warning';
  visible: boolean;
  message: string;
  description?: string;
  duration?: number;
  onClose?: () => void;
  position?: 'top' | 'bottom' | 'top-left' | 'top-right' | 'bottom-left' | 'bottom-right';
}
```

---

## 常见问题

### Q: 如何自定义动画时长？

A: 组件支持通过 CSS 变量自定义：

```css
.interactive-button {
  --transition-duration: 500ms;
}
```

### Q: 如何禁用动画？

A: 用户可以在系统设置中启用"减少动画"，或者通过 CSS 覆盖：

```css
.interactive-button {
  transition: none !important;
}
```

### Q: 多个反馈同时显示会怎样？

A: 建议一次只显示一种反馈。如果需要同时显示，可以通过 z-index 控制层级。

---

## 相关资源

- [微交互设计原则](https://lawsofux.com/)
- [CSS 动画性能优化](https://web.dev/animations-guide/)
- [Material Design 动画](https://material.io/design/motion)
- [Apple 人机交互指南](https://developer.apple.com/design/human-interface-guidelines/)

---

**最后更新时间**：2026-03-30
