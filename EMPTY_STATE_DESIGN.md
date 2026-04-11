# 空状态设计文档

## 概述

空状态组件提供了美观且实用的空状态展示，包含插画占位符和引导操作，提升用户体验。

## 功能特性

### 1. 多种预设插画

内置 6 种精美 SVG 插画，覆盖不同场景：

- **chat** - 咨询对话场景（蓝色聊天气泡）
- **document** - 文档场景（绿色文档图标）
- **case** - 案例场景（橙色案例文件）
- **contract** - 合同场景（紫色合同印章）
- **search** - 搜索场景（蓝色放大镜）
- **favorite** - 收藏场景（红色爱心）
- **default** - 默认场景（灰色通用图标）

### 2. 预设文案

每种插画都配有默认的标题和描述：

```typescript
{
  chat: {
    title: '暂无咨询记录',
    description: '开始您的首次法律咨询，AI助手将为您提供专业建议',
    actionText: '开始提问',
  },
  document: {
    title: '暂无文档',
    description: '上传您的法律文档，AI将帮助您分析整理',
    actionText: '上传文档',
  },
  case: {
    title: '暂无案例记录',
    description: '输入关键词搜索相关法律案例',
    actionText: '搜索案例',
  },
  contract: {
    title: '暂无合同记录',
    description: '创建或导入合同，AI将帮助您审查风险',
    actionText: '创建合同',
  },
  search: {
    title: '暂无搜索结果',
    description: '尝试使用不同的关键词进行搜索',
    actionText: '重新搜索',
  },
  favorite: {
    title: '暂无收藏',
    description: '将重要的咨询记录添加到收藏夹',
    actionText: '浏览历史',
  },
}
```

### 3. 灵活配置

支持自定义所有属性：

```typescript
interface EmptyStateProps {
  /** 插画类型 */
  illustration?: 'chat' | 'document' | 'case' | 'contract' | 'search' | 'favorite' | 'default';
  /** 空状态标题 */
  title?: string;
  /** 空状态描述 */
  description?: string;
  /** 操作按钮文本 */
  actionText?: string;
  /** 操作按钮点击事件 */
  onAction?: () => void;
  /** 额外内容 */
  extra?: React.ReactNode;
  /** 是否紧凑模式 */
  compact?: boolean;
  /** 自定义样式 */
  style?: React.CSSProperties;
  /** 自定义类名 */
  className?: string;
}
```

## 使用示例

### 基础用法

```tsx
import EmptyState from '@/components/EmptyState';

<EmptyState
  illustration="chat"
  onAction={() => navigate('/chat')}
/>
```

### 自定义文案

```tsx
<EmptyState
  illustration="document"
  title="暂无上传的文档"
  description="点击下方按钮上传您的法律文档"
  actionText="上传文档"
  onAction={handleUpload}
/>
```

### 紧凑模式

```tsx
<EmptyState
  illustration="search"
  compact
  onAction={handleSearch}
/>
```

### 无操作按钮

```tsx
<EmptyState
  illustration="favorite"
  title="暂无收藏"
  description="将重要的对话添加到收藏夹"
/>
```

### 带额外内容

```tsx
<EmptyState
  illustration="case"
  onAction={handleCreateCase}
  extra={
    <div>
      <Button type="link" onClick={handleImport}>导入案例</Button>
      <Button type="link" onClick={handleBrowse}>浏览模板</Button>
    </div>
  }
/>
```

## 实际应用场景

### 1. 聊天历史空状态

```tsx
// components/Chat/ChatHistoryList.tsx
<EmptyState
  illustration="chat"
  description={searchKeyword ? '未找到匹配的历史记录' : undefined}
  onAction={() => onClose()}
/>
```

### 2. 文档页面空状态

```tsx
// pages/Document/index.tsx
<EmptyState
  illustration="document"
  onAction={() => loadTemplates()}
/>
```

### 3. 案例搜索空状态

```tsx
// pages/CaseSearch/index.tsx
<EmptyState
  illustration="search"
  description="请输入关键词搜索案例"
  onAction={() => keyword && handleSearch(1)}
/>
```

### 4. 收藏夹空状态

```tsx
// components/Chat/ChatHistoryList.tsx
<EmptyState
  illustration="favorite"
  description={searchKeyword ? '未找到匹配的收藏' : undefined}
  onAction={() => setActiveTab('all')}
  actionText="浏览历史"
/>
```

## 样式特性

### 1. 入场动画

使用 `fadeInUp` 动画，从下方淡入并向上移动：

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
```

### 2. 插画悬浮动画

插画会持续轻微浮动，增加生动感：

```css
@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-8px); }
}
```

### 3. 悬停效果

鼠标悬停时，插画会放大 5%，且更加清晰：

```css
.empty-state:hover .empty-state-illustration {
  transform: scale(1.05);
  opacity: 1;
}
```

### 4. 响应式设计

在移动设备上自动调整间距和字体大小：

```css
@media (max-width: 768px) {
  .empty-state {
    padding: 32px 16px;
  }
  .empty-state-title {
    font-size: 14px;
  }
}
```

### 5. 深色模式支持

根据系统偏好自动适配深色主题：

```css
@media (prefers-color-scheme: dark) {
  .empty-state-title {
    color: #e8e8e8;
  }
  .empty-state-description {
    color: #b8b8b8;
  }
}
```

### 6. 减少动画偏好

尊重用户的动画偏好设置：

```css
@media (prefers-reduced-motion: reduce) {
  .empty-state,
  .empty-state-illustration svg,
  .empty-state-action {
    animation: none;
    transition: none;
  }
}
```

### 7. 场景特定样式

提供特定场景的样式变体：

- `.card-empty-state` - 卡片内空状态
- `.list-empty-state` - 列表内空状态
- `.modal-empty-state` - 模态框内空状态
- `.drawer-empty-state` - 抽屉内空状态

## 设计原则

### 1. 友好性

使用柔和的色彩和圆润的插画，传达友好的用户体验。

### 2. 引导性

提供明确的操作按钮，引导用户进行下一步操作。

### 3. 一致性

所有空状态都遵循相同的设计语言和交互模式。

### 4. 可访问性

- 支持深色模式
- 尊重动画偏好
- 使用语义化的 SVG 插画
- 合理的对比度

## 最佳实践

### 1. 场景匹配

根据页面功能选择合适的插画类型：

| 页面类型 | 推荐插画 |
|---------|---------|
| 对话/聊天 | chat |
| 文档/文件 | document |
| 案例/判决 | case |
| 合同/协议 | contract |
| 搜索/筛选 | search |
| 收藏/点赞 | favorite |

### 2. 文案简洁

- 标题不超过 10 个字
- 描述不超过 50 个字
- 按钮文本不超过 8 个字

### 3. 操作明确

- 按钮文本使用动作动词（如"开始提问"、"上传文档"）
- 点击后立即跳转到相应功能
- 避免多个操作按钮造成混淆

### 4. 响应式

在移动端使用 `compact` 模式，减少占用空间。

## 性能优化

1. **SVG 内联**：所有插画使用内联 SVG，避免额外的网络请求

2. **GPU 加速**：动画使用 `transform` 和 `opacity`，启用 GPU 加速

3. **will-change**：对频繁动画的元素添加 `will-change` 提示

4. **按需加载**：仅在需要时渲染空状态组件

## 注意事项

1. 不要在所有地方都使用空状态，只在合适场景使用

2. 预设文案可以根据业务需求调整

3. 插画风格与整体 UI 保持一致

4. 避免过度动画，影响性能或用户体验

## 未来扩展

可以考虑添加的插画类型：

- **evidence** - 证据场景
- **analysis** - 分析场景
- **compliance** - 合规场景
- **defense** - 辩护场景
- **judge** - 法官场景

可以根据实际业务需求持续扩展插画库。
