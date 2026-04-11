# 律法先锋 - 法律AI前端应用

基于腾讯元器智能体构建的法律人工智能助手前端，提供法律咨询、文书生成、案例检索、合同审查等全方位法律服务。

## 项目说明

本项目是**律法先锋法律AI智能平台**的前端应用，采用React + TypeScript + Ant Design构建，为用户提供以下核心功能：

- **法律咨询** - 智能法律问答，提供婚姻家庭、合同纠纷、劳动争议等专业法律咨询
- **法律文书自动生成** - 起诉状、答辩状、代理词等法律文书智能生成
- **案例智能检索** - 基于向量数据库的语义化案例检索
- **合同条款审查** - 合同风险识别、条款合规性检查
- **案件分析** - 案件要素提取、法律适用分析、诉讼策略建议
- **证据材料分析** - 证据三性分析、证明力评估
- **法官画像分析** - 分析法官过往案例、审判风格、应对策略建议
- **答辩书编辑** - 支持版本管理、差异比对的答辩书编辑功能
- **法律维权指导** - 提供详细的维权流程和步骤指引

## 项目结构

```
frontend/
└── web/           # Web管理后台 (React + Ant Design) ✅ 已实现
    ├── src/
    │   ├── pages/           # 页面组件
    │   ├── components/      # 公共组件
    │   ├── services/        # API服务
    │   ├── hooks/           # 自定义Hooks
    │   └── utils/           # 工具函数
    └── package.json
```

## 技术选型

### Web管理后台 (已实现)
- React 18 + TypeScript
- Ant Design Pro 组件库
- UmiJS 企业级框架
- Zustand 状态管理
- React Query 服务端状态管理
- Dumi 组件文档

### 移动端H5 (规划中)
- Vue 3 + TypeScript
- Vant 4 移动端组件库
- Pinia 状态管理
- Vite 构建工具

### 桌面客户端 (规划中)
- Electron
- Vue 3 + TypeScript
- Tauri (可选)

## 功能模块

### 客户端页面

| 模块 | 路由 | 说明 |
|------|------|------|
| 首页 | / | 欢迎页、快捷入口 |
| 法律咨询 | /consult | AI法律问答 |
| 文书生成 | /document | 智能文书生成 |
| 案例检索 | /case-search | 案例智能检索 |
| 合同审查 | /contract | 合同风险审查 |
| 案件管理 | /case | 案件信息管理 |
| 证据分析 | /evidence | 证据材料分析 |
| 知识库 | /knowledge | 法律法规查询 |
| 个人中心 | /profile | 用户信息管理 |
| 系统设置 | /settings | 系统配置 |

### 移动端页面 (规划中)

| 模块 | 路由 | 说明 |
|------|------|------|
| 首页 | /home | 底部导航首页 |
| 咨询 | /consult | 语音/文字咨询 |
| 文书 | /document | 快速文书生成 |
| 我的 | /profile | 个人中心 |

## 快速开始

### Web前端 (已实现)

```bash
cd frontend/web
npm install
npm run dev
```

### 移动端H5 (规划中)

```bash
# 等待实现
cd frontend/mobile
npm install
npm run dev
```

### 桌面客户端 (规划中)

```bash
# 等待实现
cd frontend/desktop
npm install
npm run electron:dev
```

## 核心组件

### AI对话组件

```tsx
// components/LegalChat/index.tsx
import { useState } from 'react';
import { ChatInput, MessageList, VoiceInput } from '@/components';

interface Message {
  id: string;
  role: 'user' | 'assistant';
  content: string;
  type: 'text' | 'voice' | 'document';
}

export const LegalChat = () => {
  const [messages, setMessages] = useState<Message[]>([]);
  const [loading, setLoading] = useState(false);

  const handleSend = async (content: string) => {
    setLoading(true);
    try {
      const response = await legalConsult({ question: content });
      setMessages(prev => [...prev, response]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="legal-chat">
      <MessageList messages={messages} />
      <ChatInput onSend={handleSend} loading={loading} />
      <VoiceInput onResult={handleSend} />
    </div>
  );
};
```

### 语音对话组件

```tsx
// components/VoiceChat/index.tsx
import { useVoice } from '@/hooks/useVoice';
import { VoiceIndicator } from './VoiceIndicator';

export const VoiceChat = () => {
  const { 
    isRecording, 
    startRecording, 
    stopRecording,
    audioLevel 
  } = useVoice();

  const handleVoiceInput = async () => {
    if (isRecording) {
      const audioData = await stopRecording();
      const text = await speechToText(audioData);
      // 发送文字到AI
    } else {
      startRecording();
    }
  };

  return (
    <div className="voice-chat">
      <VoiceIndicator level={audioLevel} recording={isRecording} />
      <button onClick={handleVoiceInput}>
        {isRecording ? '结束录音' : '开始对话'}
      </button>
    </div>
  );
};
```

### 文书生成组件

```tsx
// components/DocumentGenerator/index.tsx
import { useState } from 'react';
import { 
  TemplateSelect, 
  FormFill, 
  DocumentPreview,
  DownloadButton 
} from './components';

export const DocumentGenerator = () => {
  const [step, setStep] = useState(1);
  const [template, setTemplate] = useState(null);
  const [formData, setFormData] = useState({});
  const [generatedDoc, setGeneratedDoc] = useState(null);

  const handleGenerate = async () => {
    const doc = await generateDocument({
      templateId: template.id,
      data: formData
    });
    setGeneratedDoc(doc);
  };

  return (
    <div className="document-generator">
      <Steps current={step}>
        <Step title="选择模板" />
        <Step title="填写信息" />
        <Step title="生成文档" />
      </Steps>
      
      {step === 1 && <TemplateSelect onSelect={setTemplate} />}
      {step === 2 && <FormFill template={template} onChange={setFormData} />}
      {step === 3 && (
        <DocumentPreview 
          document={generatedDoc}
          onDownload={handleDownload}
        />
      )}
    </div>
  );
};
```

## API调用

```typescript
// services/legal.ts
import { request } from '@umijs/max';

export const legalConsult = (params: {
  question: string;
  caseType?: string;
}) => {
  return request('/api/consult', {
    method: 'POST',
    data: params,
  });
};

export const generateDocument = (params: {
  templateId: string;
  data: Record<string, any>;
}) => {
  return request('/api/document/generate', {
    method: 'POST',
    data: params,
  });
};

export const searchCases = (params: {
  keyword: string;
  caseType?: string;
  court?: string;
}) => {
  return request('/api/case/search', {
    method: 'GET',
    params,
  });
};

export const reviewContract = (data: FormData) => {
  return request('/api/contract/review', {
    method: 'POST',
    data,
    headers: { 'Content-Type': 'multipart/form-data' },
  });
};

export const speechToText = (file: File) => {
  const formData = new FormData();
  formData.append('file', file);
  return request('/api/speech/to-text', {
    method: 'POST',
    data: formData,
  });
};

export const textToSpeech = (params: { text: string; voice?: string }) => {
  return request('/api/speech/to-voice', {
    method: 'POST',
    params,
    responseType: 'blob',
  });
};
```

## 样式规范

```css
/* 主题色 */
:root {
  --primary-color: #1890ff;
  --success-color: #52c41a;
  --warning-color: #faad14;
  --error-color: #f5222d;
  --text-color: #333333;
  --bg-color: #f5f5f5;
}

/* 组件样式 */
.legal-card {
  background: #ffffff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}
```
