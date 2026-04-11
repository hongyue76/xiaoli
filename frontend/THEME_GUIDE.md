# 律法先锋前端主题配色方案

## 📋 概述

律法先锋法律AI助手采用专业的法律行业配色方案，通过深蓝色主色调体现专业性和权威感，金色点缀象征公正正义，青灰色背景提升质感。

## 🎨 配色方案

### 主色调 - 深蓝色系列

| 颜色名称 | 色值 | 用途 | 说明 |
|---------|------|------|------|
| 深蓝 | `#003a8c` | 主色调 | 体现法律专业性和权威感 |
| 浅蓝 | `#096dd9` | 交互状态 | 悬停、激活状态 |
| 更浅蓝 | `#40a9ff` | 强调色 | 重要信息强调 |
| 更深蓝 | `#002766` | 按压状态 | 按钮点击效果 |

### 辅助色 - 金色/橙色

| 颜色名称 | 色值 | 用途 | 说明 |
|---------|------|------|------|
| 金色 | `#faad14` | 公正象征 | 象征公正和正义 |
| 浅金 | `#ffc53d` | 高亮状态 | 特殊强调 |
| 深金 | `#d48806` | 文本强调 | 重要文本 |

### 中性色

| 颜色名称 | 色值 | 用途 |
|---------|------|------|
| 主文本 | `#1f1f1f` | 主要内容文本 |
| 次要文本 | `#595959` | 辅助说明文本 |
| 第三级文本 | `#8c8c8c` | 提示、标签文本 |
| 禁用文本 | `#bfbfbf` | 禁用状态文本 |

### 背景色

| 颜色名称 | 色值 | 用途 |
|---------|------|------|
| 主背景 | `#ffffff` | 白色背景 |
| 次要背景 | `#f5f7fa` | 青灰色背景 - 提升质感 |
| 第三级背景 | `#fafafa` | 卡片、面板背景 |
| 禁用背景 | `#f5f5f5` | 禁用状态背景 |

### 边框色

| 颜色名称 | 色值 | 用途 |
|---------|------|------|
| 基础边框 | `#d9d9d9` | 默认边框 |
| 浅色边框 | `#e8e8e8` | 轻微边框 |
| 深色边框 | `#bfbfbf` | 强调边框 |

### 功能色

| 颜色名称 | 色值 | 用途 |
|---------|------|------|
| 成功 | `#52c41a` | 成功状态 |
| 警告 | `#faad14` | 警告状态（复用金色） |
| 错误 | `#f5222d` | 错误状态 |
| 信息 | `#1890ff` | 信息提示 |

## ✨ 渐变效果

### 主色渐变
```css
background: linear-gradient(135deg, #003a8c 0%, #096dd9 100%);
```
用途：主按钮、头部导航、激活状态

### 金色渐变
```css
background: linear-gradient(135deg, #faad14 0%, #ffc53d 100%);
```
用途：金色强调按钮、特殊标签

### 蓝金渐变
```css
background: linear-gradient(135deg, #003a8c 0%, #faad14 100%);
```
用途：特殊强调元素、装饰性渐变

### 微妙渐变
```css
background: linear-gradient(135deg, #f5f7fa 0%, #ffffff 100%);
```
用途：背景、卡片、面板

## 🎯 组件样式

### 按钮样式

#### 主按钮
```css
background: linear-gradient(135deg, #003a8c 0%, #096dd9 100%);
color: #ffffff;
border-radius: 8px;
box-shadow: 0 2px 8px rgba(0, 58, 140, 0.3);
```

**悬停效果**：
```css
background: linear-gradient(135deg, #096dd9 0%, #40a9ff 100%);
box-shadow: 0 4px 12px rgba(0, 58, 140, 0.4);
transform: translateY(-2px);
```

#### 金色按钮
```css
background: linear-gradient(135deg, #faad14 0%, #ffc53d 100%);
color: #ffffff;
border-radius: 8px;
box-shadow: 0 2px 8px rgba(250, 173, 20, 0.3);
```

### 卡片样式

#### 基础卡片
```css
background: #ffffff;
border-radius: 12px;
box-shadow: 0 2px 12px rgba(0, 58, 140, 0.08);
border: 1px solid rgba(0, 58, 140, 0.1);
transition: all 0.3s ease;
```

**悬停效果**：
```css
box-shadow: 0 4px 20px rgba(0, 58, 140, 0.15);
transform: translateY(-2px);
```

#### 法律主题卡片
```css
background: #ffffff;
border-radius: 12px;
box-shadow: 0 2px 12px rgba(0, 58, 140, 0.08);
border: 1px solid rgba(0, 58, 140, 0.1);
overflow: hidden;
}
```

**卡片头部**：
```css
background: linear-gradient(135deg, #003a8c 0%, #096dd9 100%);
color: #ffffff;
padding: 16px 24px;
font-weight: 600;
font-size: 16px;
```

### 输入框样式

```css
background: #ffffff;
border: 1px solid rgba(0, 58, 140, 0.2);
border-radius: 12px;
transition: all 0.3s ease;
```

**聚焦效果**：
```css
border-color: #003a8c;
box-shadow: 0 0 0 3px rgba(0, 58, 140, 0.1);
```

### 标签样式

#### 蓝色标签
```css
background: rgba(0, 58, 140, 0.1);
color: #003a8c;
border: 1px solid rgba(0, 58, 140, 0.2);
border-radius: 6px;
padding: 4px 12px;
font-weight: 500;
```

#### 金色标签
```css
background: rgba(250, 173, 20, 0.1);
color: #d48806;
border: 1px solid rgba(250, 173, 20, 0.2);
border-radius: 6px;
padding: 4px 12px;
font-weight: 500;
```

## 📐 阴影系统

| 阴影级别 | CSS | 用途 |
|---------|-----|------|
| 小阴影 | `0 2px 8px rgba(0, 58, 140, 0.08)` | 卡片、面板 |
| 中阴影 | `0 4px 16px rgba(0, 58, 140, 0.12)` | 按钮、悬停状态 |
| 大阴影 | `0 8px 24px rgba(0, 58, 140, 0.16)` | 弹窗、模态框 |
| 特大阴影 | `0 16px 32px rgba(0, 58, 140, 0.2)` | 重要元素 |

## 🔲 圆角系统

| 圆角级别 | 值 | 用途 |
|---------|---|------|
| 小圆角 | `4px` | 按钮、标签 |
| 中圆角 | `8px` | 输入框、选择器 |
| 大圆角 | `12px` | 卡片、面板 |
| 特大圆角 | `16px` | 页面头部、大卡片 |

## 🎭 动画效果

### 淡入上升动画
```css
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.animate-fade-in-up {
  animation: fadeInUp 0.6s ease-out;
}
```

### 滑入动画
```css
@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message-card {
  animation: slideIn 0.3s ease-out;
}
```

## 📱 响应式调整

### 小屏幕优化
```css
@media (max-width: 768px) {
  :root {
    --radius-sm: 2px;
    --radius-md: 6px;
    --radius-lg: 10px;
    --radius-xl: 14px;
  }

  .card-legal,
  .section-legal {
    padding: 16px;
  }
}
```

## 🎨 自定义组件类

### 法律主题按钮
```html
<button class="btn-legal">确定</button>
<button class="btn-gold">重要操作</button>
```

### 法律主题卡片
```html
<div class="card-legal">
  <div class="card-legal-header">标题</div>
  <div class="card-legal-body">内容</div>
</div>
```

### 统计卡片
```html
<div class="card-stat">
  <div class="card-stat-icon">📊</div>
  <div class="card-stat-value">123</div>
  <div class="card-stat-label">数量</div>
</div>
```

### 功能区块
```html
<div class="section-legal">
  <div class="section-legal-title">区块标题</div>
  <div class="section-legal-content">区块内容</div>
</div>
```

## 🚀 使用方法

### 1. 引入主题文件

在 `index.css` 中引入：
```css
@import './theme/legal-theme.css';
```

### 2. 使用CSS变量

```css
.my-element {
  color: var(--color-primary);
  background: var(--gradient-primary);
  border-radius: var(--radius-lg);
}
```

### 3. 使用预定义类

```jsx
<div className="card-legal">
  <button className="btn-legal">确定</button>
  <span className="text-legal-primary">重要文本</span>
</div>
```

## 📊 Ant Design 覆盖

主题已覆盖以下Ant Design组件：
- ✅ Button（按钮）
- ✅ Card（卡片）
- ✅ Input（输入框）
- ✅ Select（选择器）
- ✅ Table（表格）
- ✅ Tag（标签）
- ✅ Tabs（标签页）
- ✅ Menu（菜单）
- ✅ Alert（警告框）

## 🔧 自定义主题

如需调整主题颜色，修改 `frontend/web/src/theme/legal-theme.css` 中的CSS变量：

```css
:root {
  /* 修改主色调 */
  --color-primary: #003a8c;
  --color-primary-light: #096dd9;

  /* 修改金色 */
  --color-gold: #faad14;
  --color-gold-dark: #d48806;

  /* 修改背景色 */
  --color-bg-secondary: #f5f7fa;
}
```

## 📝 注意事项

1. **颜色对比度**：确保文字与背景色有足够的对比度
2. **渐变使用**：适度使用渐变效果，避免过度使用
3. **阴影层次**：使用不同的阴影级别创建层次感
4. **圆角统一**：保持同一类型组件的圆角一致
5. **动画流畅**：动画时间控制在0.3s-0.6s之间

## 🎯 设计理念

1. **专业性**：深蓝色体现法律行业的专业性和权威感
2. **权威性**：稳重的配色方案传达可信赖感
3. **公正性**：金色点缀象征公正正义
4. **易用性**：清晰的视觉层次和良好的可读性
5. **现代感**：渐变、阴影、动画等现代设计元素

## 📚 参考资源

- Ant Design 主题定制：https://ant.design/docs/react/customize-theme-cn
- CSS 变量：https://developer.mozilla.org/zh-CN/docs/Web/CSS/Using_CSS_custom_properties
- 渐变生成器：https://cssgradient.io/
- 配色方案：https://colorhunt.co/

## ✅ 更新日志

- **2024-03-28**：初始版本，创建法律专业配色方案
  - 深蓝色主色调 (#003a8c)
  - 金色点缀 (#faad14)
  - 青灰色背景 (#f5f7fa)
  - 渐变效果支持
  - 完整的组件样式覆盖
