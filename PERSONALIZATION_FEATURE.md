# 个性化功能文档

## 功能概述

本文档详细说明了 AI 法律助手平台的个性化功能实现，包括暗黑模式、收藏夹管理、咨询记录时间轴、标签分类系统和工作台自定义。

## 一、暗黑模式切换

### 功能特性

- **三种模式**: 浅色模式、深色模式、跟随系统
- **自动保存**: 主题设置自动保存到 localStorage
- **系统跟随**: 自动检测系统主题偏好
- **平滑过渡**: 切换时平滑的动画效果

### 文件结构

```
frontend/web/src/
├── hooks/
│   └── useTheme.ts              # 主题管理 Hook
├── components/
│   ├── Theme/
│   │   ├── ThemeToggle.tsx      # 主题切换组件
│   │   └── ThemeToggle.css     # 主题切换样式
│   └── Theme/
│       └── index.ts             # 组件导出
└── styles/
    └── theme.css               # 全局主题变量
```

### 使用示例

```tsx
import { useTheme } from './hooks/useTheme';
import ThemeToggle from './components/Theme';

function App() {
  const { theme, isDark, setTheme, toggleTheme } = useTheme();

  return (
    <div>
      <ThemeToggle />
      {/* 应用根据 isDark 显示不同的样式 */}
    </div>
  );
}
```

### CSS 变量

```css
:root {
  /* 浅色模式变量 */
  --bg-primary: #ffffff;
  --text-primary: #262626;
  /* ... */
}

[data-theme='dark'] {
  /* 深色模式变量 */
  --bg-primary: #1f1f1f;
  --text-primary: rgba(255, 255, 255, 0.85);
  /* ... */
}
```

## 二、收藏夹管理

### 功能特性

- **文件夹管理**: 支持创建、重命名、删除文件夹
- **层级结构**: 支持多级文件夹嵌套
- **分类收藏**: 按类型分类（法条、案例、文书）
- **标签系统**: 为收藏项添加标签
- **搜索功能**: 搜索标题、内容和标签
- **移动管理**: 将收藏项移动到不同文件夹
- **导出导入**: JSON 格式导出/导入

### 数据结构

```typescript
interface FavoriteFolder {
  id: string;
  name: string;
  parentId: string | null;
  children?: FavoriteFolder[];
}

interface FavoriteItem {
  id: string;
  title: string;
  type: 'law' | 'case' | 'document';
  content: string;
  folderId: string;
  tags: string[];
  createdAt: Date;
  updatedAt: Date;
}
```

### 使用示例

```tsx
import FavoritesManager, { useFavorites } from './components/Favorites';

function FavoritesPage() {
  const {
    folders,
    items,
    addFolder,
    addItem,
    searchItems,
  } = useFavorites();

  return (
    <FavoritesManager
      visible={visible}
      onClose={() => setVisible(false)}
    />
  );
}
```

### 预设文件夹

- 全部收藏（根目录）
- 常用法条
- 典型案例
- 生成的文书

## 三、咨询记录时间轴

### 功能特性

- **时间轴展示**: 按时间顺序展示咨询记录
- **状态管理**: 进行中、已完成、已归档
- **文书生成**: 标记是否生成文书
- **分类筛选**: 按分类筛选记录
- **时间筛选**: 按日期范围筛选
- **关键词搜索**: 搜索标题和消息内容
- **归档功能**: 将完成的记录归档
- **统计信息**: 显示问答数量、使用的模型

### 数据结构

```typescript
interface ConsultationRecord extends ChatHistoryItem {
  status: 'pending' | 'completed' | 'archived';
  documentGenerated?: boolean;
  category?: string;
}
```

### 使用示例

```tsx
import ConsultationTimeline, { useConsultationTimeline } from './components/Consultation';

function ConsultationPage() {
  const {
    records,
    filterRecords,
    getRecordsByStatus,
  } = useConsultationTimeline();

  const handleSelectRecord = (record: ConsultationRecord) => {
    // 加载选中的记录
  };

  return (
    <ConsultationTimeline
      visible={visible}
      onClose={() => setVisible(false)}
      onSelectRecord={handleSelectRecord}
    />
  );
}
```

### 筛选功能

- **状态筛选**: 全部、进行中、已完成、已归档
- **分类筛选**: 按自定义分类筛选
- **日期筛选**: 选择开始和结束日期
- **关键词搜索**: 搜索标题和消息内容

## 四、标签分类系统

### 功能特性

- **标签管理**: 创建、编辑、删除标签
- **颜色选择**: 10 种预设颜色
- **使用统计**: 记录每个标签的使用次数
- **热门标签**: 显示最常用的标签
- **最近使用**: 显示最近使用的标签
- **搜索功能**: 按名称搜索标签
- **智能推荐**: 根据使用频率推荐标签

### 数据结构

```typescript
interface TagItem {
  id: string;
  name: string;
  color: string;
  count: number;
  createdAt: Date;
}
```

### 预设颜色

- blue（蓝色）
- green（绿色）
- orange（橙色）
- red（红色）
- purple（紫色）
- cyan（青色）
- magenta（品红）
- lime（酸橙）
- gold（金色）
- geekblue（极客蓝）

### 使用示例

```tsx
import TagManager, { useTagManager } from './components/Tags';

function TagsPage() {
  const {
    tags,
    addTag,
    updateTag,
    deleteTag,
    getPopularTags,
  } = useTagManager();

  const handleTagSelect = (tag: TagItem) => {
    // 使用选中的标签
  };

  return (
    <TagManager
      visible={visible}
      onClose={() => setVisible(false)}
      onTagSelect={handleTagSelect}
    />
  );
}
```

### 标签统计

- **热门标签**: 按使用次数排序
- **最近使用**: 按创建时间排序
- **使用计数**: 自动递增/递减

## 五、工作台自定义

### 功能特性

- **模块管理**: 启用/禁用功能模块
- **置顶功能**: 将常用模块置顶
- **拖拽排序**: 通过按钮上下移动模块
- **分类展示**: 按功能分类展示模块
- **视图切换**: 查看全部或仅查看置顶
- **重置功能**: 重置为默认配置

### 预设模块

| ID | 标题 | 分类 | 默认状态 |
|-----|--------|--------|-----------|
| ai-consult | AI 咨询 | consult | 启用 |
| case-analysis | 案例分析 | consult | 启用 |
| document-gen | 文书生成 | document | 启用 |
| case-search | 案例检索 | case | 启用 |
| law-search | 法条检索 | tool | 启用 |
| favorites | 收藏夹 | tool | 启用 |
| history | 历史记录 | tool | 启用 |
| statistics | 数据统计 | tool | 禁用 |

### 数据结构

```typescript
interface WorkspaceModule {
  id: string;
  title: string;
  icon: string;
  enabled: boolean;
  pinned: boolean;
  order: number;
  category: 'consult' | 'document' | 'case' | 'tool';
}
```

### 使用示例

```tsx
import WorkspaceCustomizer, { useWorkspaceCustomizer } from './components/Workspace';

function WorkspacePage() {
  const {
    modules,
    toggleModule,
    togglePin,
    getEnabledModules,
  } = useWorkspaceCustomizer();

  return (
    <WorkspaceCustomizer
      visible={visible}
      onClose={() => setVisible(false)}
    />
  );
}
```

### 模块分类

- **consult**: 咨询类功能（AI 咨询、案例分析）
- **document**: 文书类功能（文书生成）
- **case**: 案例类功能（案例检索）
- **tool**: 工具类功能（法条检索、收藏夹、历史记录、数据统计）

## 本地存储

### 存储键

| 功能 | 存储键 | 数据类型 |
|------|---------|---------|
| 主题 | app_theme | string |
| 收藏夹 | favorites_data | JSON |
| 咨询记录 | consultation_records | JSON |
| 标签 | tags_data | JSON |
| 工作台 | workspace_settings | JSON |

## 样式定制

### CSS 变量

使用 CSS 变量轻松自定义主题：

```css
/* 浅色模式 */
:root {
  --bg-primary: #ffffff;
  --text-primary: #262626;
  --primary-color: #1890ff;
}

/* 深色模式 */
[data-theme='dark'] {
  --bg-primary: #1f1f1f;
  --text-primary: rgba(255, 255, 255, 0.85);
  --primary-color: #177ddc;
}
```

### 响应式断点

- 576px: 手机
- 768px: 平板
- 1200px: 桌面

## 性能优化

1. **本地存储缓存**: 使用 localStorage 减少网络请求
2. **懒加载**: 列表项按需加载
3. **防抖搜索**: 搜索输入防抖处理
4. **虚拟滚动**: 长列表使用虚拟滚动（待实现）
5. **动画优化**: 使用 CSS transform 而非 top/left

## 浏览器兼容性

| 功能 | Chrome | Firefox | Safari | Edge |
|------|--------|---------|--------|------|
| 暗黑模式 | ✅ | ✅ | ✅ | ✅ |
| 收藏夹 | ✅ | ✅ | ✅ | ✅ |
| 时间轴 | ✅ | ✅ | ✅ | ✅ |
| 标签系统 | ✅ | ✅ | ✅ | ✅ |
| 工作台自定义 | ✅ | ✅ | ✅ | ✅ |

## 常见问题

### Q: 如何切换暗黑模式？

A: 点击顶部的主题切换按钮，选择浅色、深色或跟随系统模式。

### Q: 收藏夹最多能保存多少？

A: 理论上没有限制，但受 localStorage 容量限制（通常 5-10 MB）。

### Q: 如何归档咨询记录？

A: 在咨询记录时间轴中，点击记录的"归档"标签即可。

### Q: 标签可以重命名吗？

A: 可以，在标签管理中点击标签的编辑图标，修改名称和颜色。

### Q: 如何恢复默认工作台设置？

A: 在工作台自定义中点击"重置工作台"按钮。

## 未来优化方向

1. **云同步**: 所有个性化设置云端同步
2. **拖拽排序**: 工作台模块支持拖拽排序
3. **主题市场**: 提供更多主题选择
4. **智能推荐**: AI 推荐标签和分类
5. **批量操作**: 批量删除、批量移动
6. **快捷键**: 为常用功能添加快捷键

## 文件列表

### 新增文件

1. `frontend/web/src/hooks/useTheme.ts` - 主题管理 Hook
2. `frontend/web/src/components/Theme/ThemeToggle.tsx` - 主题切换组件
3. `frontend/web/src/components/Theme/ThemeToggle.css` - 主题切换样式
4. `frontend/web/src/components/Theme/index.ts` - 主题组件导出
5. `frontend/web/src/components/Favorites/FavoritesManager.tsx` - 收藏夹管理
6. `frontend/web/src/components/Favorites/FavoritesManager.css` - 收藏夹样式
7. `frontend/web/src/components/Favorites/index.ts` - 收藏夹导出
8. `frontend/web/src/components/Consultation/ConsultationTimeline.tsx` - 咨询记录时间轴
9. `frontend/web/src/components/Consultation/ConsultationTimeline.css` - 咨询记录样式
10. `frontend/web/src/components/Consultation/index.ts` - 咨询记录导出
11. `frontend/web/src/components/Tags/TagManager.tsx` - 标签管理
12. `frontend/web/src/components/Tags/TagManager.css` - 标签管理样式
13. `frontend/web/src/components/Tags/index.ts` - 标签管理导出
14. `frontend/web/src/components/Workspace/WorkspaceCustomizer.tsx` - 工作台自定义
15. `frontend/web/src/components/Workspace/WorkspaceCustomizer.css` - 工作台自定义样式
16. `frontend/web/src/components/Workspace/index.ts` - 工作台自定义导出
17. `frontend/web/src/styles/theme.css` - 全局主题变量

## 总结

个性化功能为用户提供了完整的自定义能力：

1. **暗黑模式**: 三种模式切换，自动保存
2. **收藏夹管理**: 文件夹分类、标签系统、搜索功能
3. **咨询记录时间轴**: 状态管理、筛选搜索、归档功能
4. **标签分类系统**: 颜色选择、使用统计、智能推荐
5. **工作台自定义**: 模块启用、置顶排序、分类展示

所有功能均使用 TypeScript 开发，类型安全，支持响应式设计，代码质量高。
