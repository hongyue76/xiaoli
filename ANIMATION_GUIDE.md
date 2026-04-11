# 页面过渡动画文档

本文档详细说明了律法先锋法律AI助手的页面过渡动画和列表动画功能。

## 目录

1. [动画概述](#动画概述)
2. [路由过渡动画](#路由过渡动画)
3. [列表交错动画](#列表交错动画)
4. [列表添加/删除动画](#列表添加删除动画)
5. [动画类型说明](#动画类型说明)
6. [使用示例](#使用示例)
7. [最佳实践](#最佳实践)
8. [性能优化](#性能优化)

---

## 动画概述

为提升用户体验，我们实现了丰富的页面过渡动画和列表动画效果。

### 动画分类

1. **路由过渡动画**：页面切换时的过渡效果
2. **列表交错动画**：列表项逐个显示的效果
3. **列表添加/删除动画**：列表项增删时的动画效果

### 设计原则

- ✅ 流畅自然：使用贝塞尔曲线缓动函数
- ✅ 性能优先：优先使用 transform 和 opacity 属性
- ✅ 无障碍支持：尊重用户的动画偏好设置
- ✅ 响应式设计：适配不同屏幕尺寸

---

## 路由过渡动画

### PageTransition 组件

基础的页面过渡动画组件，提供多种动画效果。

### 支持的动画类型

| 类型 | 说明 | 使用场景 |
|------|------|----------|
| `fade` | 淡入淡出 | 通用页面切换 |
| `slide-left` | 从左侧滑入 | 页面层级较深的内容 |
| `slide-right` | 从右侧滑入 | 返回上一级 |
| `slide-up` | 从下方滑入 | 默认页面切换 |
| `slide-down` | 从上方滑入 | 特殊场景 |
| `zoom` | 缩放效果 | 模态框、详情页 |

### 使用示例

```tsx
import { PageTransition } from '@/components/Animations';

function MyPage() {
  return (
    <PageTransition type="slide-up" duration={300} delay={0}>
      <div>
        <h1>页面内容</h1>
        {/* ... */}
      </div>
    </PageTransition>
  );
}
```

### 组件参数

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `type` | `TransitionType` | `'fade'` | 动画类型 |
| `duration` | `number` | `300` | 动画持续时间（毫秒） |
| `delay` | `number` | `0` | 动画延迟时间（毫秒） |
| `className` | `string` | `''` | 自定义 CSS 类名 |

---

## 列表交错动画

### StaggerAnimation 组件

为列表项提供交错进入动画效果，使列表项逐个显示。

### 支持的动画类型

| 类型 | 说明 | 方向 |
|------|------|------|
| `fade-up` | 从下方淡入 | 垂直向上 |
| `fade-down` | 从上方淡入 | 垂直向下 |
| `fade-left` | 从左侧淡入 | 水平向右 |
| `fade-right` | 从右侧淡入 | 水平向左 |
| `scale-in` | 缩放进入 | 从小到大 |
| `slide-up` | 从下方滑入 | 垂直向上 |

### 使用示例

```tsx
import { StaggerAnimation } from '@/components/Animations';

interface Item {
  id: number;
  name: string;
}

function MyList({ items }: { items: Item[] }) {
  return (
    <StaggerAnimation 
      type="fade-up" 
      duration={300} 
      staggerDelay={50}
    >
      {items.map((item) => (
        <div key={item.id} className="list-item">
          {item.name}
        </div>
      ))}
    </StaggerAnimation>
  );
}
```

### 组件参数

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `type` | `StaggerType` | `'fade-up'` | 动画类型 |
| `duration` | `number` | `300` | 每个列表项的动画持续时间（毫秒） |
| `delay` | `number` | `0` | 首个列表项的动画延迟（毫秒） |
| `staggerDelay` | `number` | `50` | 列表项之间的间隔延迟（毫秒） |
| `className` | `string` | `''` | 自定义 CSS 类名 |

### 实际应用案例

#### 案例搜索页面

```tsx
// CaseSearch/index.tsx
import { StaggerAnimation } from '@/components/Animations';

<StaggerAnimation type="fade-up" duration={300} staggerDelay={50}>
  {cases.map((case) => (
    <Card key={case.id}>
      <h3>{case.title}</h3>
      {/* ... */}
    </Card>
  ))}
</StaggerAnimation>
```

---

## 列表添加/删除动画

### ListAnimation + AnimateItem 组件

为列表项提供添加/删除动画效果。

### 支持的动画类型

| 类型 | 说明 | 适用场景 |
|------|------|----------|
| `scale` | 缩放效果 | 通用列表 |
| `slide` | 滑动效果 | 列表增删 |
| `fade` | 淡入淡出 | 简单过渡 |
| `height` | 高度动画 | 折叠展开 |

### 使用示例

```tsx
import { ListAnimation, AnimateItem } from '@/components/Animations';

interface Todo {
  id: number;
  text: string;
  visible: boolean;
}

function TodoList({ todos, onDelete }: { todos: Todo[], onDelete: (id: number) => void }) {
  return (
    <ListAnimation type="scale" duration={300}>
      {todos.map((todo) => (
        <AnimateItem 
          key={todo.id} 
          isVisible={todo.visible}
          onEnter={() => console.log('Item entered')}
          onExit={() => console.log('Item exited')}
        >
          <div className="todo-item">
            <span>{todo.text}</span>
            <button onClick={() => onDelete(todo.id)}>删除</button>
          </div>
        </AnimateItem>
      ))}
    </ListAnimation>
  );
}
```

### AnimateItem 参数

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `isVisible` | `boolean` | 必填 | 控制列表项是否可见 |
| `type` | `ListAnimationType` | `'scale'` | 动画类型 |
| `duration` | `number` | `300` | 动画持续时间（毫秒） |
| `onEnter` | `() => void` | `undefined` | 进入动画完成回调 |
| `onExit` | `() => void` | `undefined` | 退出动画完成回调 |

### 实际应用案例

#### 收藏夹管理

```tsx
// FavoritesManager.tsx
import { ListAnimation, AnimateItem } from '@/components/Animations';

<ListAnimation type="scale" duration={300}>
  {favorites.map((item) => (
    <AnimateItem key={item.id} isVisible={item.visible}>
      <Card>
        <h3>{item.title}</h3>
        <Button onClick={() => removeFavorite(item.id)}>删除</Button>
      </Card>
    </AnimateItem>
  ))}
</ListAnimation>
```

---

## 动画类型说明

### 路由过渡动画

#### Fade（淡入淡出）
```tsx
<PageTransition type="fade" duration={300}>
  {/* 内容 */}
</PageTransition>
```

**特点**：
- 简洁优雅
- 适用范围广
- 性能开销小

#### Slide（滑动）
```tsx
// 从左侧滑入
<PageTransition type="slide-left" duration={300}>
  {/* 内容 */}
</PageTransition>

// 从右侧滑入
<PageTransition type="slide-right" duration={300}>
  {/* 内容 */}
</PageTransition>

// 从下方滑入
<PageTransition type="slide-up" duration={300}>
  {/* 内容 */}
</PageTransition>
```

**特点**：
- 明确的层级关系
- 适合多级导航
- 视觉引导性强

#### Zoom（缩放）
```tsx
<PageTransition type="zoom" duration={300}>
  {/* 内容 */}
</PageTransition>
```

**特点**：
- 焦点突出
- 适合模态框
- 视觉冲击力强

### 列表动画

#### 交错动画
```tsx
<StaggerAnimation 
  type="fade-up" 
  duration={300} 
  staggerDelay={50}
>
  {items.map((item, index) => (
    <div key={item.id}>{item.name}</div>
  ))}
</StaggerAnimation>
```

**效果**：
- 第 1 项：延迟 0ms
- 第 2 项：延迟 50ms
- 第 3 项：延迟 100ms
- 以此类推...

---

## 使用示例

### 完整示例：带动画的页面

```tsx
import React from 'react';
import { PageTransition, StaggerAnimation, ListAnimation, AnimateItem } from '@/components/Animations';

function AnimatedPage() {
  const [items, setItems] = useState([
    { id: 1, name: '项目 1', visible: true },
    { id: 2, name: '项目 2', visible: true },
    { id: 3, name: '项目 3', visible: true },
  ]);

  const handleDelete = (id: number) => {
    setItems(items.map(item => 
      item.id === id ? { ...item, visible: false } : item
    ));
    // 延迟后真正删除
    setTimeout(() => {
      setItems(items.filter(item => item.id !== id));
    }, 300);
  };

  return (
    <PageTransition type="slide-up" duration={300}>
      <div>
        <h1>动画演示页面</h1>
        
        {/* 交错动画列表 */}
        <h2>交错动画</h2>
        <StaggerAnimation type="fade-up" duration={300} staggerDelay={100}>
          {items.map((item) => (
            <div key={item.id} className="card">
              {item.name}
            </div>
          ))}
        </StaggerAnimation>

        {/* 带添加/删除动画的列表 */}
        <h2>增删动画</h2>
        <ListAnimation type="scale" duration={300}>
          {items.map((item) => (
            <AnimateItem 
              key={item.id} 
              isVisible={item.visible}
            >
              <div className="card">
                <span>{item.name}</span>
                <button onClick={() => handleDelete(item.id)}>
                  删除
                </button>
              </div>
            </AnimateItem>
          ))}
        </ListAnimation>
      </div>
    </PageTransition>
  );
}
```

### AnimatedRoutes 使用示例

```tsx
// App.tsx
import { AnimatedRoutes } from '@/components/Animations';

function App() {
  return (
    <AnimatedRoutes type="slide-up" duration={300}>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/about" element={<About />} />
        <Route path="/contact" element={<Contact />} />
      </Routes>
    </AnimatedRoutes>
  );
}
```

---

## 最佳实践

### 1. 动画持续时间

推荐的时间设置：

```tsx
// 快速过渡（高频操作）
<PageTransition type="fade" duration={150} />

// 标准过渡（页面切换）
<PageTransition type="slide-up" duration={300} />

// 慢速过渡（重要操作）
<PageTransition type="zoom" duration={500} />
```

### 2. 动画类型选择

根据使用场景选择合适的动画：

```tsx
// 页面切换：使用 slide-up
<PageTransition type="slide-up" duration={300} />

// 弹窗/详情：使用 zoom
<PageTransition type="zoom" duration={300} />

// 列表项：使用 fade-up
<StaggerAnimation type="fade-up" duration={300} />

// 删除操作：使用 scale
<AnimateItem isVisible={visible} type="scale">
  {/* 内容 */}
</AnimateItem>
```

### 3. 交错延迟设置

根据列表项数量调整延迟：

```tsx
// 列表项少（<10）：延迟 50-100ms
<StaggerAnimation staggerDelay={50} />

// 列表项中等（10-30）：延迟 30-50ms
<StaggerAnimation staggerDelay={30} />

// 列表项多（>30）：延迟 10-30ms
<StaggerAnimation staggerDelay={15} />
```

### 4. 性能优化

```tsx
// ✅ 推荐：使用 transform 和 opacity
animation: slide-in 300ms ease;
transform: translateY(30px);
opacity: 0;

// ❌ 避免：使用 width/height/top/left
// 这些属性会触发重排
```

---

## 性能优化

### 动画性能

使用 GPU 加速的属性：

- ✅ `transform`
- ✅ `opacity`
- ✅ `filter`

避免使用的属性：

- ❌ `width` / `height`
- ❌ `top` / `left`
- ❌ `margin` / `padding`

### 减少重排重绘

```tsx
// ✅ 推荐：使用 transform
.element {
  transform: translateY(10px);
}

// ❌ 避免：使用 top
.element {
  top: 10px;
}
```

### 无障碍支持

组件已内置 `prefers-reduced-motion` 检测：

```css
@media (prefers-reduced-motion: reduce) {
  .page-transition {
    animation: none !important;
  }
}
```

用户开启"减少动画"后，所有动画将自动禁用。

---

## 组件 API 文档

### PageTransition

```tsx
interface PageTransitionProps {
  children: ReactNode;
  type?: 'fade' | 'slide-left' | 'slide-right' | 'slide-up' | 'slide-down' | 'zoom';
  duration?: number;
  delay?: number;
  className?: string;
}
```

### StaggerAnimation

```tsx
interface StaggerAnimationProps {
  children: ReactNode;
  type?: 'fade-up' | 'fade-down' | 'fade-left' | 'fade-right' | 'scale-in' | 'slide-up';
  duration?: number;
  delay?: number;
  staggerDelay?: number;
  className?: string;
}
```

### AnimateItem

```tsx
interface AnimateItemProps {
  children: ReactNode;
  isVisible: boolean;
  type?: 'scale' | 'slide' | 'fade' | 'height';
  duration?: number;
  onEnter?: () => void;
  onExit?: () => void;
}
```

### AnimatedRoutes

```tsx
interface AnimatedRoutesProps {
  children: ReactNode;
  type?: 'fade' | 'slide-left' | 'slide-right' | 'slide-up' | 'slide-down' | 'zoom';
  duration?: number;
}
```

---

## 常见问题

### Q: 如何自定义动画？

A: 可以通过 CSS 覆盖默认动画：

```css
.my-custom-animation {
  animation: custom-keyframes 500ms ease;
}

@keyframes custom-keyframes {
  from {
    opacity: 0;
    transform: scale(0.5) rotate(45deg);
  }
  to {
    opacity: 1;
    transform: scale(1) rotate(0deg);
  }
}
```

### Q: 动画卡顿怎么办？

A: 检查以下几点：

1. 是否使用了非 GPU 加速的属性
2. 动画持续时间是否过长
3. 是否有大量的 DOM 节点同时动画
4. 检查是否有 JavaScript 阻塞主线程

### Q: 如何禁用所有动画？

A: 用户可以在系统设置中启用"减少动画"，或者通过 CSS 强制禁用：

```css
* {
  animation: none !important;
  transition: none !important;
}
```

---

## 相关资源

- [CSS 动画性能优化](https://web.dev/animations-guide/)
- [React 动画最佳实践](https://react.dev/learn/adding-interactivity)
- [Web Animation API](https://developer.mozilla.org/en-US/docs/Web/API/Web_Animations_API)
- [贝塞尔曲线工具](https://cubic-bezier.com/)

---

**最后更新时间**：2026-03-30
