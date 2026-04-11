# AI 对话体验优化 - 快速开始

## 安装依赖

```bash
cd frontend/web
npm install react-markdown react-syntax-highlighter
npm install @types/react-syntax-highlighter --save-dev
```

## 快速使用

### 方式一：使用增强版 AIChat 组件

```tsx
import AIChatEnhanced from './components/AIChatEnhanced';

function App() {
  return (
    <div style={{ height: '100vh', padding: '20px' }}>
      <AIChatEnhanced />
    </div>
  );
}
```

### 方式二：单独使用组件

```tsx
import { ChatBubble, QuickActions, VoiceInteraction } from './components/Chat';

function App() {
  const [messages, setMessages] = useState<ChatMessage[]>([]);

  return (
    <div>
      {/* 对话消息 */}
      {messages.map(msg => (
        <ChatBubble
          key={msg.id}
          message={msg}
          onCopy={handleCopy}
          onFollowUp={handleFollowUp}
          onLike={handleLike}
        />
      ))}

      {/* 快捷操作 */}
      <QuickActions
        questions={quickQuestions}
        onQuestionClick={handleQuestionClick}
      />

      {/* 语音交互 */}
      <VoiceInteraction
        onVoiceInput={handleVoiceInput}
      />
    </div>
  );
}
```

## 查看演示

在路由配置中添加：

```tsx
import AIChatDemo from './pages/AIChatDemo';

<Routes>
  <Route path="/ai-chat-demo" element={<AIChatDemo />} />
</Routes>
```

访问 `http://localhost:3000/ai-chat-demo` 查看演示。

## 功能列表

✅ **对话气泡**
- 用户消息右对齐（蓝色）
- AI 消息左对齐（白色）
- 头像和身份标识
- 打字动画效果

✅ **快捷操作**
- 常用问题推荐
- "追问"按钮
- "复制答案"、"点赞/点踩"

✅ **富文本展示**
- Markdown 渲染
- 法条引用高亮
- 结构化内容（列表、表格）

✅ **语音交互**
- 语音输入按钮
- TTS 朗读答案
- 声纹可视化

## API 参考

### ChatBubble

```typescript
interface ChatMessage {
  id: string;
  role: 'user' | 'assistant';
  content: string;
  timestamp: Date;
  avatar?: string;
  isTyping?: boolean;
  metadata?: {
    hasFollowUp?: boolean;
    canCopy?: boolean;
    sources?: Array<{ title: string; url: string }>;
  };
}

interface ChatBubbleProps {
  message: ChatMessage;
  onFollowUp?: (content: string) => void;
  onCopy?: (content: string) => void;
  onLike?: (messageId: string, type: 'like' | 'dislike') => void;
}
```

### QuickActions

```typescript
interface QuickQuestion {
  id: string;
  text: string;
  category?: string;
  isFavorite?: boolean;
}

interface QuickActionsProps {
  questions: QuickQuestion[];
  onQuestionClick?: (question: QuickQuestion) => void;
  onCopy?: (content: string) => void;
  onFollowUp?: (content: string) => void;
  onLike?: (messageId: string, type: 'like' | 'dislike') => void;
  onToggleFavorite?: (questionId: string) => void;
  showFollowUp?: boolean;
  followUpContent?: string;
  messageId?: string;
}
```

### VoiceInteraction

```typescript
interface VoiceInteractionProps {
  onVoiceInput?: (text: string) => void;
  onTTSStart?: () => void;
  onTTSEnd?: () => void;
  disabled?: boolean;
}
```

## 浏览器兼容性

| 功能 | Chrome | Firefox | Safari | Edge |
|------|--------|---------|--------|------|
| 所有功能 | ✅ 25+ | ✅ | ✅ 14+ | ✅ 79+ |

**注意：**
- 语音识别不支持 Firefox
- 建议使用最新版本 Chrome 或 Edge 获得最佳体验

## 响应式断点

- 576px: 手机
- 768px: 平板
- 992px: 小型桌面
- 1200px: 中型桌面
- 1400px: 大型桌面

## 文件结构

```
frontend/web/src/
├── components/
│   ├── Chat/
│   │   ├── ChatBubble.tsx
│   │   ├── ChatBubble.css
│   │   ├── QuickActions.tsx
│   │   ├── QuickActions.css
│   │   ├── VoiceInteraction.tsx
│   │   ├── VoiceInteraction.css
│   │   └── index.ts
│   ├── AIChatEnhanced.tsx
│   └── AIChatEnhanced.css
├── pages/
│   ├── AIChatDemo.tsx
│   └── AIChatDemo.css
```

## 常见问题

### Q: 语音识别不工作？

A: 请确保：
1. 浏览器支持 Web Speech API（推荐 Chrome）
2. 已允许麦克风权限
3. 使用 HTTPS 或 localhost

### Q: Markdown 渲染显示异常？

A: 请确保已安装依赖：
```bash
npm install react-markdown react-syntax-highlighter
```

### Q: 如何自定义样式？

A: 可以通过覆盖 CSS 类自定义样式，例如：
```css
/* 自定义用户消息颜色 */
.user-message .message-bubble {
  background: linear-gradient(135deg, #your-color 0%, #your-color 100%);
}
```

### Q: 如何禁用某些功能？

A: 通过 props 控制：
```tsx
<VoiceInteraction disabled={true} /> {/* 禁用语音交互 */}
<QuickActions showFollowUp={false} /> {/* 不显示追问 */}
```

## 下一步

- 查看详细文档：`AI_CHAT_EXPERIENCE_OPTIMIZATION.md`
- 查看演示页面：`/ai-chat-demo`
- 根据需求自定义组件

## 支持

如有问题，请查看详细文档或联系开发团队。
