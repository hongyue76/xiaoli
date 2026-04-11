# 快捷键支持文档

## 概述

快捷键支持提供了完整的键盘快捷键功能，提升用户操作效率，包括搜索、新建咨询、保存草稿、关闭弹窗和快捷键帮助等功能。

## 核心组件

### 1. HotkeyProvider

全局快捷键上下文 Provider，提供快捷键注册和事件处理。

```tsx
import { HotkeyProvider } from '@/components/Hotkeys';

function App() {
  return (
    <HotkeyProvider enabled={true}>
      <YourApp />
      <HotkeyHelp />
    </HotkeyProvider>
  );
}
```

#### 参数

```typescript
interface HotkeyProviderProps {
  children: React.ReactNode;
  enabled?: boolean;  // 是否启用快捷键，默认 true
}
```

### 2. useHotkey Hook

注册单个快捷键。

```tsx
import { useHotkey } from '@/components/Hotkeys';

function MyComponent() {
  useHotkey({
    keys: ['ctrl+k', 'cmd+k'],  // 支持多个快捷键
    description: '打开搜索',
    priority: 10,               // 优先级，数字越大优先级越高
    callback: (event) => {
      console.log('快捷键触发');
    },
    disabled: false,             // 是否禁用
  });

  return <div>...</div>;
}
```

#### 参数说明

```typescript
interface HotkeyAction {
  keys: string | string[];          // 快捷键组合
  description: string;             // 动作描述
  callback: (event: KeyboardEvent) => void;  // 回调函数
  disabled?: boolean;              // 是否禁用
  priority?: number;               // 优先级
}
```

#### 快捷键格式

- `ctrl+k` - Ctrl + K
- `cmd+k` - Command + K（Mac）
- `ctrl+shift+k` - Ctrl + Shift + K
- `escape` - Escape 键
- `?` - 问号键

### 3. useHotkeys Hook

获取快捷键上下文，显示帮助面板。

```tsx
import { useHotkeys } from '@/components/Hotkeys';

function MyComponent() {
  const { hotkeys, helpVisible, showHelp, hideHelp, toggleHelp } = useHotkeys();

  return (
    <div>
      <button onClick={showHelp}>显示帮助</button>
      <button onClick={toggleHelp}>切换帮助</button>
    </div>
  );
}
```

#### 返回值

```typescript
interface HotkeyContextType {
  registerHotkey: (action: HotkeyAction) => () => void;  // 注册快捷键
  hotkeys: HotkeyAction[];                                // 所有已注册的快捷键
  helpVisible: boolean;                                   // 帮助面板是否可见
  toggleHelp: () => void;                                 // 切换帮助面板
  showHelp: () => void;                                   // 显示帮助面板
  hideHelp: () => void;                                   // 隐藏帮助面板
}
```

### 4. HotkeyHelp 组件

快捷键帮助面板，显示所有已注册的快捷键。

```tsx
import { HotkeyHelp } from '@/components/Hotkeys';

function App() {
  return (
    <HotkeyProvider>
      <YourApp />
      <HotkeyHelp />
    </HotkeyProvider>
  );
}
```

### 5. 应用级快捷键 (AppHotkeys)

预设的应用级快捷键，在 `App.tsx` 中自动注册。

#### 预设快捷键

| 快捷键 | 功能 | 优先级 |
|--------|------|--------|
| Ctrl + K | 打开搜索 | 10 |
| Ctrl + N | 新建咨询 | 10 |
| Ctrl + S | 保存草稿 | 10 |
| Esc | 关闭弹窗 | 5 |
| ? | 查看快捷键帮助 | 20 |
| Ctrl + H | 返回首页 | 5 |
| Ctrl + R | 刷新页面 | 3 |

#### 自定义事件

应用级快捷键会触发以下自定义事件：

- `open-search` - 打开搜索
- `save-draft` - 保存草稿
- `close-modal` - 关闭弹窗

组件可以监听这些事件：

```tsx
useEffect(() => {
  const handleOpenSearch = () => {
    setSearchVisible(true);
  };

  window.addEventListener('open-search', handleOpenSearch);

  return () => {
    window.removeEventListener('open-search', handleOpenSearch);
  };
}, []);
```

### 6. 辅助组件

#### ShortcutDisplay

显示快捷键组合。

```tsx
import { ShortcutDisplay } from '@/components/Hotkeys/AppHotkeys';

<ShortcutDisplay keys={['Ctrl', 'K']} />
// 输出: [Ctrl] + [K]

<ShortcutDisplay keys={['Ctrl', 'K']} compact />
// 输出: [Ctrl][K] (紧凑模式)
```

#### ShortcutIndicator

快捷键指示器，在按钮旁边显示快捷键。

```tsx
import { ShortcutIndicator } from '@/components/Hotkeys/AppHotkeys';

<Button>
  打开搜索
  <ShortcutIndicator keys={['Ctrl', 'K']} />
</Button>
```

#### ShortcutTooltip

带快捷键提示的 Tooltip。

```tsx
import { ShortcutTooltip } from '@/components/Hotkeys/ShortcutTooltip';

<ShortcutTooltip
  keys={['Ctrl', 'K']}
  description="点击或按 Ctrl+K 打开搜索"
>
  <Button>打开搜索</Button>
</ShortcutTooltip>
```

## 使用示例

### 基础用法

```tsx
function MyComponent() {
  useHotkey({
    keys: ['ctrl+k'],
    description: '打开搜索',
    callback: () => {
      console.log('打开搜索');
    },
  });

  return <div>内容</div>;
}
```

### 多个快捷键

```tsx
useHotkey({
  keys: ['ctrl+k', 'cmd+k'],  // Windows 和 Mac 都支持
  description: '打开搜索',
  callback: () => {
    console.log('打开搜索');
  },
});
```

### 带优先级

```tsx
// 优先级高的先触发
useHotkey({
  keys: ['escape'],
  description: '关闭弹窗',
  priority: 20,  // 高优先级
  callback: () => {
    setModalVisible(false);
  },
});

useHotkey({
  keys: ['escape'],
  description: '其他功能',
  priority: 10,  // 低优先级
  callback: () => {
    // 这个不会触发
  },
});
```

### 动态禁用

```tsx
const [disabled, setDisabled] = useState(false);

useHotkey({
  keys: ['ctrl+s'],
  description: '保存',
  callback: () => save(),
  disabled,  // 动态控制
});

return (
  <Button onClick={() => setDisabled(!disabled)}>
    {disabled ? '启用' : '禁用'}快捷键
  </Button>
);
```

### 在表单中使用

```tsx
function FormComponent() {
  const [form] = Form.useForm();

  useHotkey({
    keys: ['ctrl+s'],
    description: '保存表单',
    callback: () => {
      form.validateFields().then((values) => {
        console.log('保存:', values);
      });
    },
  });

  return (
    <Form form={form}>
      <Form.Item name="name" label="名称">
        <Input />
      </Form.Item>
    </Form>
  );
}
```

## 快捷键命名规范

### 功能快捷键

| 功能 | 快捷键 | 说明 |
|------|--------|------|
| 搜索 | Ctrl + K | 通用搜索快捷键 |
| 新建 | Ctrl + N | 通用新建快捷键 |
| 保存 | Ctrl + S | 通用保存快捷键 |
| 关闭 | Esc | 关闭弹窗/抽屉 |
| 帮助 | ? | 显示帮助 |
| 刷新 | Ctrl + R | 刷新页面 |

### 导航快捷键

| 功能 | 快捷键 | 说明 |
|------|--------|------|
| 首页 | Ctrl + H | 返回首页 |
| 返回 | Alt + ← | 返回上一页 |
| 前进 | Alt + → | 前进下一页 |

### 编辑快捷键

| 功能 | 快捷键 | 说明 |
|------|--------|------|
| 复制 | Ctrl + C | 复制选中内容 |
| 粘贴 | Ctrl + V | 粘贴内容 |
| 剪切 | Ctrl + X | 剪切内容 |
| 撤销 | Ctrl + Z | 撤销操作 |
| 重做 | Ctrl + Shift + Z | 重做操作 |

## 注意事项

### 1. 输入框中的快捷键

在输入框（input、textarea）中，除了 Esc 外的快捷键会被禁用，避免干扰用户输入。

```tsx
// 这个在输入框中不会触发
useHotkey({
  keys: ['ctrl+k'],
  callback: () => {
    console.log('不会触发');
  },
});

// 这个在输入框中会触发
useHotkey({
  keys: ['escape'],
  callback: () => {
    console.log('会触发');
  },
});
```

### 2. 快捷键冲突

如果多个组件注册了相同的快捷键，优先级高的会先触发。

```tsx
// 组件 A
useHotkey({
  keys: ['escape'],
  priority: 10,
  callback: () => console.log('A'),
});

// 组件 B
useHotkey({
  keys: ['escape'],
  priority: 20,  // 更高优先级
  callback: () => console.log('B'),
});

// 输出: B
```

### 3. 清理快捷键

组件卸载时，快捷键会自动清理。也可以手动清理：

```tsx
useEffect(() => {
  const unregister = registerHotkey({
    keys: ['ctrl+k'],
    callback: () => console.log('快捷键'),
  });

  return () => {
    unregister();  // 手动清理
  };
}, []);
```

### 4. 快捷键显示

在按钮上显示快捷键，提升用户发现率：

```tsx
<Button>
  搜索
  <ShortcutIndicator keys={['Ctrl', 'K']} />
</Button>
```

或者使用 Tooltip：

```tsx
<ShortcutTooltip keys={['Ctrl', 'K']} description="打开搜索">
  <Button>搜索</Button>
</ShortcutTooltip>
```

## 最佳实践

### 1. 优先级设置

- 高优先级（20+）：全局操作，如帮助（?）、关闭弹窗（Esc）
- 中优先级（10）：页面级操作，如搜索、新建
- 低优先级（5）：次要功能，如返回首页

### 2. 快捷键一致性

使用业界通用的快捷键：

- 保存：Ctrl + S
- 搜索：Ctrl + K
- 新建：Ctrl + N
- 关闭：Esc

### 3. 用户提示

在按钮上显示快捷键，使用户容易发现：

```tsx
<Button>
  搜索
  <ShortcutIndicator keys={['Ctrl', 'K']} />
</Button>
```

### 4. 跨平台支持

同时支持 Windows 和 Mac：

```tsx
useHotkey({
  keys: ['ctrl+k', 'cmd+k'],
  callback: () => openSearch(),
});
```

### 5. 避免冲突

避免使用浏览器内置快捷键：

- ✅ 推荐：Ctrl + K（搜索）
- ❌ 避免：Ctrl + F（浏览器查找）
- ❌ 避免：Ctrl + D（书签）

## 浏览器兼容性

### 支持的浏览器

- Chrome 90+
- Firefox 88+
- Safari 14+
- Edge 90+

### 注意事项

1. 部分快捷键可能被浏览器占用
2. 移动设备不支持键盘快捷键
3. 确保提供非键盘的操作方式

## 演示页面

完整的使用示例请参考 `pages/HotkeysDemo/index.tsx`，包含：

- 快捷键操作演示
- 已注册快捷键列表
- 草稿保存功能
- 搜索弹窗
- 新建咨询弹窗

## 性能优化

1. **事件委托**：所有快捷键通过一个全局事件监听器处理
2. **优先级排序**：快捷键按优先级排序，快速匹配
3. **防抖处理**：避免频繁触发快捷键回调
4. **输入检测**：智能判断是否在输入框中，避免干扰

## 无障碍性

1. 快捷键帮助面板可以通过键盘访问
2. 支持屏幕阅读器
3. 遵循 WAI-ARIA 规范
4. 提供非键盘的操作方式
