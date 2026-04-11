# AI 对话历史记录功能文档

## 功能概述

AI 对话历史记录功能允许用户保存、管理、搜索和导出对话历史,使用 localStorage 进行本地存储。

## 核心功能

### 1. 自动保存

- **自动保存**: 每次对话完成后自动保存到本地存储
- **最多保存**: 限制最多保存 100 条历史记录
- **智能过滤**: 自动过滤初始欢迎消息和打字指示器

### 2. 历史记录管理

- **查看历史**: 点击"历史记录"按钮打开侧边栏
- **新建对话**: 加载历史记录后可以点击"新建对话"开始新会话
- **当前标记**: 高亮显示当前加载的历史记录

### 3. 搜索功能

- **关键词搜索**: 支持搜索标题和消息内容
- **实时搜索**: 输入时即时显示匹配结果
- **分类筛选**: 支持按"全部"和"收藏"分类查看

### 4. 收藏功能

- **收藏历史**: 点击星标图标收藏/取消收藏
- **收藏列表**: 在"收藏"标签页查看所有收藏的历史记录
- **快速访问**: 收藏的对话优先显示

### 5. 导出/导入

- **单条导出**: 导出单条历史记录为 JSON 文件
- **批量导出**: 导出所有历史记录为 JSON 文件
- **导入功能**: 从 JSON 文件导入历史记录

### 6. 删除功能

- **单条删除**: 删除单条历史记录（带确认提示）
- **清空全部**: 清空所有历史记录（带二次确认）

## 组件结构

### 1. useChatHistory Hook

**文件**: `frontend/web/src/components/Chat/ChatHistory.tsx`

提供历史记录管理的核心功能:

```typescript
const {
  histories,           // 所有历史记录
  loading,             // 加载状态
  createHistory,        // 创建历史记录
  updateHistory,        // 更新历史记录
  deleteHistory,        // 删除历史记录
  clearAllHistories,    // 清空所有历史记录
  toggleFavorite,       // 切换收藏状态
  getHistory,          // 获取单条历史记录
  searchHistories,      // 搜索历史记录
  getFavoriteHistories, // 获取收藏的历史记录
  exportHistory,       // 导出单条历史记录
  exportAllHistories,  // 导出所有历史记录
  importHistory,       // 导入历史记录
  loadHistories,       // 重新加载历史记录
} = useChatHistory();
```

### 2. ChatHistoryList 组件

**文件**: `frontend/web/src/components/Chat/ChatHistoryList.tsx`

历史记录列表侧边栏组件:

```typescript
interface ChatHistoryListProps {
  visible: boolean;              // 是否可见
  onClose: () => void;           // 关闭回调
  onSelectHistory: (history: ChatHistoryItem) => void; // 选择历史记录回调
  currentHistoryId?: string;      // 当前历史记录 ID
}
```

### 3. 数据结构

```typescript
export interface ChatHistoryItem {
  id: string;                   // 唯一标识
  title: string;                // 标题（第一条用户消息的前50个字符）
  messages: ChatMessage[];      // 消息列表
  createdAt: Date;             // 创建时间
  updatedAt: Date;             // 更新时间
  isFavorite?: boolean;        // 是否收藏
  model?: string;             // 使用的 AI 模型
}
```

## 使用示例

### 基础使用

```tsx
import AIChatEnhanced from './components/AIChatEnhanced';

function App() {
  return (
    <div>
      <AIChatEnhanced />
    </div>
  );
}
```

### 自定义使用

```tsx
import { useChatHistory, ChatHistoryList } from './components/Chat';

function CustomApp() {
  const { histories, deleteHistory, toggleFavorite } = useChatHistory();
  const [historyVisible, setHistoryVisible] = useState(false);
  const [currentHistoryId, setCurrentHistoryId] = useState('');

  const handleSelectHistory = (history: ChatHistoryItem) => {
    setCurrentHistoryId(history.id);
    // 加载历史记录的消息
  };

  return (
    <div>
      <Button onClick={() => setHistoryVisible(true)}>
        查看历史记录
      </Button>

      <ChatHistoryList
        visible={historyVisible}
        onClose={() => setHistoryVisible(false)}
        onSelectHistory={handleSelectHistory}
        currentHistoryId={currentHistoryId}
      />
    </div>
  );
}
```

## API 参考

### useChatHistory

#### 创建历史记录

```typescript
createHistory(messages: ChatMessage[], model?: string): ChatHistoryItem | null
```

**参数**:
- `messages`: 消息列表
- `model`: AI 模型类型（可选）

**返回**: 新创建的历史记录对象，如果没有用户消息则返回 null

#### 更新历史记录

```typescript
updateHistory(id: string, updates: Partial<ChatHistoryItem>): void
```

**参数**:
- `id`: 历史记录 ID
- `updates`: 要更新的字段

#### 删除历史记录

```typescript
deleteHistory(id: string): void
```

**参数**:
- `id`: 历史记录 ID

#### 搜索历史记录

```typescript
searchHistories(keyword: string): ChatHistoryItem[]
```

**参数**:
- `keyword`: 搜索关键词

**返回**: 匹配的历史记录列表

#### 导出历史记录

```typescript
exportHistory(id: string): void
exportAllHistories(): void
```

**参数**:
- `id` (仅 exportHistory): 历史记录 ID

#### 导入历史记录

```typescript
importHistory(file: File): Promise<ChatHistoryItem>
```

**参数**:
- `file`: JSON 文件

**返回**: Promise, 解析后返回导入的历史记录

## 本地存储

### 存储键

```typescript
const HISTORY_STORAGE_KEY = 'ai_chat_history';
```

### 数据格式

```json
{
  "id": "1234567890",
  "title": "什么是违约金？如何计算？",
  "messages": [
    {
      "id": "1",
      "role": "user",
      "content": "什么是违约金？",
      "timestamp": "2026-03-30T10:00:00.000Z"
    },
    {
      "id": "2",
      "role": "assistant",
      "content": "违约金是指...",
      "timestamp": "2026-03-30T10:00:01.000Z"
    }
  ],
  "createdAt": "2026-03-30T10:00:00.000Z",
  "updatedAt": "2026-03-30T10:00:01.000Z",
  "isFavorite": false,
  "model": "simulated"
}
```

## 样式定制

### 历史记录抽屉

```css
.history-drawer {
  z-index: 1001;
}

.history-drawer .ant-drawer-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
```

### 历史记录项

```css
.history-item {
  padding: 12px 16px;
  margin-bottom: 8px;
  background: #fff;
  border-radius: 8px;
  border: 1px solid #e8e8e8;
  cursor: pointer;
  transition: all 0.3s;
}

.history-item:hover {
  border-color: #1890ff;
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.1);
  transform: translateX(4px);
}

.history-item.selected {
  border-color: #1890ff;
  background: #e6f7ff;
}
```

## 性能优化

1. **限制存储数量**: 最多保存 100 条历史记录
2. **懒加载**: 列表项使用虚拟滚动（待实现）
3. **防抖搜索**: 搜索输入防抖处理
4. **缓存优化**: 使用 localStorage 缓存，避免重复请求

## 浏览器兼容性

| 功能 | Chrome | Firefox | Safari | Edge |
|------|--------|---------|--------|------|
| localStorage | ✅ | ✅ | ✅ | ✅ |
| JSON 导出/导入 | ✅ | ✅ | ✅ | ✅ |
| 所有功能 | ✅ | ✅ | ✅ | ✅ |

## 常见问题

### Q: 历史记录会丢失吗？

A: 历史记录保存在浏览器的 localStorage 中，如果清除浏览器数据会丢失。建议定期导出备份。

### Q: 如何迁移历史记录到另一台设备？

A: 使用"导出全部"功能导出 JSON 文件，然后在另一台设备上导入。

### Q: 历史记录占用多少空间？

A: 每条历史记录约 1-10 KB（取决于消息数量），100 条历史记录约 1 MB。

### Q: 如何删除所有历史记录？

A: 在历史记录抽屉中点击"清空"按钮，确认后即可删除所有历史记录。

### Q: 导出的 JSON 文件可以修改吗？

A: 可以导出后手动编辑 JSON 文件，然后重新导入。但请注意保持正确的数据格式。

## 未来优化方向

1. **云同步**: 支持多设备同步历史记录
2. **标签管理**: 为历史记录添加自定义标签
3. **智能分组**: 按主题、时间自动分组
4. **搜索增强**: 支持高级搜索（日期范围、模型类型等）
5. **分享功能**: 生成分享链接，分享给其他用户
6. **备份恢复**: 定期自动备份，一键恢复

## 相关文件

- `frontend/web/src/components/Chat/ChatHistory.tsx` - 历史记录管理 Hook
- `frontend/web/src/components/Chat/ChatHistoryList.tsx` - 历史记录列表组件
- `frontend/web/src/components/Chat/ChatHistoryList.css` - 历史记录列表样式
- `frontend/web/src/components/AIChatEnhanced.tsx` - 集成历史记录功能的聊天组件

## 总结

历史记录功能为 AI 对话提供了完整的会话管理能力，包括自动保存、搜索、收藏、导出导入等功能，极大地提升了用户体验和对话的可追溯性。
