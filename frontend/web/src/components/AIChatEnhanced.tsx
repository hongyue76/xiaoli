import { useState, useRef, useEffect } from 'react';
import { Input, Button, message, Select, Space } from 'antd';
import {
  SendOutlined,
  ThunderboltOutlined,
  HistoryOutlined,
} from '@ant-design/icons';
import { ProgressBar, LoadingButton } from './LoadingStates';
import ChatBubble from '../Chat/ChatBubble';
import QuickActions, { QuickQuestion } from '../Chat/QuickActions';
import VoiceInteraction from '../Chat/VoiceInteraction';
import ChatHistoryList, { ChatHistoryItem } from '../Chat/ChatHistoryList';
import { useChatHistory } from '../Chat/ChatHistory';
import './AIChatEnhanced.css';

const { TextArea } = Input;

export interface ChatMessage {
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

const AIChatEnhanced: React.FC = () => {
  const [messages, setMessages] = useState<ChatMessage[]>([
    {
      id: '1',
      role: 'assistant',
      content: '您好！我是律法先锋AI助手，可以帮您解答法律问题。请选择AI模型后开始提问。',
      timestamp: new Date(),
      metadata: {
        hasFollowUp: true,
      },
    },
  ]);
  const [inputValue, setInputValue] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [progressPercent, setProgressPercent] = useState(0);
  const [progressStep, setProgressStep] = useState(0);
  const [aiModel, setAiModel] = useState<string>('backend');
  const [showQuickActions, setShowQuickActions] = useState(true);
  const [lastMessageId, setLastMessageId] = useState<string>('');
  const [historyVisible, setHistoryVisible] = useState(false);
  const [currentHistoryId, setCurrentHistoryId] = useState<string>('');
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const { createHistory, updateHistory, deleteHistory } = useChatHistory();

  // 常用问题
  const quickQuestions: QuickQuestion[] = [
    { id: 'q1', text: '什么是违约金？如何计算？', category: '合同法', isFavorite: true },
    { id: 'q2', text: '离婚诉讼需要准备哪些材料？', category: '婚姻家庭', isFavorite: false },
    { id: 'q3', text: '劳动仲裁的时效是多久？', category: '劳动法', isFavorite: true },
    { id: 'q4', text: '如何申请工伤认定？', category: '社会保障', isFavorite: false },
    { id: 'q5', text: '交通事故赔偿标准是什么？', category: '侵权法', isFavorite: false },
    { id: 'q6', text: '房屋买卖合同需要注意什么？', category: '房产法律', isFavorite: true },
  ];

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const callDeepSeekAPI = async (question: string) => {
    try {
      const response = await fetch('https://api.deepseek.com/v1/chat/completions', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer sk-ab6f6d5baa9349c6a735df32f9cc4f16',
        },
        body: JSON.stringify({
          model: 'deepseek-chat',
          messages: [
            {
              role: 'system',
              content: '你是一个专业的法律AI助手，擅长解答各类法律问题。请用专业、准确、易懂的语言回答用户的问题，并使用Markdown格式排版，适当引用法条。'
            },
            {
              role: 'user',
              content: question
            }
          ],
          temperature: 0.7,
          max_tokens: 2000,
        }),
      });

      const data = await response.json();
      return data.choices[0]?.message?.content || '抱歉，无法获取回复。';
    } catch (error) {
      console.error('DeepSeek API调用失败:', error);
      throw error;
    }
  };

  const handleSend = async (content?: string) => {
    const text = content || inputValue;
    if (!text.trim()) return;

    const userMessage: ChatMessage = {
      id: Date.now().toString(),
      role: 'user',
      content: text,
      timestamp: new Date(),
    };

    setMessages([...messages, userMessage]);
    const question = text;
    setInputValue('');
    setIsLoading(true);
    setProgressPercent(0);
    setProgressStep(0);
    setShowQuickActions(false);

    // 添加打字指示器
    const typingMessage: ChatMessage = {
      id: (Date.now() + 1).toString(),
      role: 'assistant',
      content: '',
      timestamp: new Date(),
      isTyping: true,
    };
    setMessages(prev => [...prev, typingMessage]);

    // 模拟进度条动画
    const progressInterval = setInterval(() => {
      setProgressPercent(prev => {
        const newPercent = prev + Math.random() * 15 + 5;
        return newPercent > 85 ? 85 : newPercent;
      });
      setProgressStep(prev => {
        const newStep = prev + (Math.random() > 0.7 ? 1 : 0);
        return newStep > 2 ? 2 : newStep;
      });
    }, 800);

    try {
      let aiResponse = '';

      if (aiModel === 'real') {
        aiResponse = await callDeepSeekAPI(question);
      } else if (aiModel === 'backend') {
        // 使用相对路径，通过vite代理转发到后端
        const response = await fetch('/api/consult/chat', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            messages: [{ role: 'user', content: question }],
          }),
        });

        if (response.ok) {
          const data = await response.json();
          aiResponse = data.answer || data || '抱歉，没有收到回复。';
        } else {
          throw new Error('后端API调用失败');
        }
      }

      // 完成进度
      setProgressPercent(100);
      setProgressStep(3);

      setTimeout(() => {
        const aiMessage: ChatMessage = {
          id: (Date.now() + 2).toString(),
          role: 'assistant',
          content: aiResponse,
          timestamp: new Date(),
          metadata: {
            hasFollowUp: true,
            canCopy: true,
            sources: [
              { title: '《民法典》', url: '#' },
              { title: '《合同法》', url: '#' },
            ],
          },
        };

        setMessages(prev => {
          const newMessages = prev.filter(m => !m.isTyping);
          return [...newMessages, aiMessage];
        });
        setLastMessageId(aiMessage.id);
        setIsLoading(false);
        setShowQuickActions(true);

        // 保存到历史记录
        saveToHistory();
      }, 500);
    } catch (error: any) {
      console.error('API调用失败:', error);
      message.error(error.message || '请求失败，请稍后重试');

      const errorMessage: ChatMessage = {
        id: (Date.now() + 2).toString(),
        role: 'assistant',
        content: '抱歉，发生了错误。请尝试切换AI模型或稍后重试。如果使用真实AI，请确保已配置API密钥。',
        timestamp: new Date(),
      };

      setMessages(prev => {
        const newMessages = prev.filter(m => !m.isTyping);
        return [...newMessages, errorMessage];
      });
      setIsLoading(false);
      setShowQuickActions(true);
    } finally {
      clearInterval(progressInterval);
    }
  };

  const handleQuickQuestion = (question: QuickQuestion) => {
    handleSend(question.text);
  };

  const handleCopy = (content: string) => {
    navigator.clipboard.writeText(content);
    message.success('已复制到剪贴板');
  };

  const handleFollowUp = (content: string) => {
    handleSend(content);
  };

  const handleLike = (messageId: string, type: 'like' | 'dislike') => {
    console.log(`Message ${messageId} ${type}d`);
  };

  // 保存历史记录
  const saveToHistory = () => {
    // 过滤掉初始欢迎消息和打字指示器
    const messagesToSave = messages.filter(m => !m.isTyping && m.id !== '1');
    if (messagesToSave.length > 0) {
      if (currentHistoryId) {
        // 更新现有历史记录
        updateHistory(currentHistoryId, { messages: messagesToSave });
      } else {
        // 创建新历史记录
        const newHistory = createHistory(messagesToSave, aiModel);
        if (newHistory) {
          setCurrentHistoryId(newHistory.id);
        }
      }
    }
  };

  // 选择历史记录
  const handleSelectHistory = (history: ChatHistoryItem) => {
    setMessages(history.messages);
    setCurrentHistoryId(history.id);
    setHistoryVisible(false);
    setLastMessageId(history.messages[history.messages.length - 1]?.id || '');
    message.success('已加载历史记录');
  };

  // 新建对话
  const handleNewChat = () => {
    setMessages([
      {
        id: '1',
        role: 'assistant',
        content: '您好！我是律法先锋AI助手，可以帮您解答法律问题。请选择AI模型后开始提问。',
        timestamp: new Date(),
        metadata: {
          hasFollowUp: true,
        },
      },
    ]);
    setCurrentHistoryId('');
    setShowQuickActions(true);
    setLastMessageId('');
  };


  const handleVoiceInput = (text: string) => {
    setInputValue(text);
  };

  const handleToggleFavorite = (questionId: string) => {
    console.log(`Toggle favorite: ${questionId}`);
  };

  return (
    <div className="ai-chat-enhanced">
      {/* 模型选择 */}
      <div className="model-selector">
        <Space>
          <span className="selector-label">AI模型：</span>
          <Select
            value={aiModel}
            onChange={setAiModel}
            style={{ width: 200 }}
          >
            <Select.Option value="real">DeepSeek AI</Select.Option>
          </Select>
          <Button
            icon={<HistoryOutlined />}
            onClick={() => setHistoryVisible(true)}
          >
            历史记录
          </Button>
          {currentHistoryId && (
            <Button onClick={handleNewChat}>
              新建对话
            </Button>
          )}
        </Space>
      </div>

      {/* 聊天消息区域 */}
      <div className="chat-messages">
        {messages.map((message) => (
          <ChatBubble
            key={message.id}
            message={message}
            onCopy={handleCopy}
            onFollowUp={handleFollowUp}
            onLike={handleLike}
          />
        ))}
        {isLoading && (
          <ProgressBar
            current={progressStep}
            percent={progressPercent}
            status="active"
            showSteps={true}
            showPercent={true}
            type="default"
          />
        )}
        <div ref={messagesEndRef} />
      </div>

      {/* 快捷操作 */}
      {showQuickActions && !isLoading && (
        <QuickActions
          questions={quickQuestions}
          onQuestionClick={handleQuickQuestion}
          onCopy={handleCopy}
          onFollowUp={handleFollowUp}
          onLike={handleLike}
          onToggleFavorite={handleToggleFavorite}
          showFollowUp={!!lastMessageId && messages[messages.length - 1]?.role === 'assistant'}
          followUpContent={messages[messages.length - 1]?.content}
          messageId={lastMessageId}
        />
      )}

      {/* 语音交互 */}
      <VoiceInteraction
        onVoiceInput={handleVoiceInput}
        disabled={isLoading}
      />

      {/* 输入区域 */}
      <div className="chat-input">
        <TextArea
          value={inputValue}
          onChange={(e) => setInputValue(e.target.value)}
          placeholder="请输入您的法律问题，或使用语音输入..."
          autoSize={{ minRows: 3, maxRows: 5 }}
          onPressEnter={(e) => {
            if (e.shiftKey) return;
            e.preventDefault();
            handleSend();
          }}
          className="chat-textarea"
        />
        <LoadingButton
          type="primary"
          icon={<SendOutlined />}
          onClick={() => handleSend()}
          loading={isLoading}
          disabled={!inputValue.trim()}
          className="send-button"
        >
          发送
        </LoadingButton>
      </div>

      {/* 提示 */}
      <div className="chat-hint">
        <span className="hint-text">
          💡 提示：按 Enter 发送，Shift+Enter 换行 | 支持语音输入和朗读 | 点击追问深入了解 | 自动保存历史记录
        </span>
      </div>

      {/* 历史记录抽屉 */}
      <ChatHistoryList
        visible={historyVisible}
        onClose={() => setHistoryVisible(false)}
        onSelectHistory={handleSelectHistory}
        currentHistoryId={currentHistoryId}
      />
    </div>
  );
};

export default AIChatEnhanced;
