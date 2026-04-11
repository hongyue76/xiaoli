import { useState, useEffect } from 'react';
import { message } from 'antd';
import { ChatMessage } from './ChatBubble';

export interface ChatHistoryItem {
  id: string;
  title: string;
  messages: ChatMessage[];
  createdAt: Date;
  updatedAt: Date;
  isFavorite?: boolean;
  model?: string;
}

const HISTORY_STORAGE_KEY = 'ai_chat_history';
const MAX_HISTORY_SIZE = 100; // 最多保存100条历史记录

export const useChatHistory = () => {
  const [histories, setHistories] = useState<ChatHistoryItem[]>([]);
  const [loading, setLoading] = useState(false);

  // 从 localStorage 加载历史记录
  useEffect(() => {
    loadHistories();
  }, []);

  const loadHistories = () => {
    try {
      setLoading(true);
      const saved = localStorage.getItem(HISTORY_STORAGE_KEY);
      if (saved) {
        const parsed = JSON.parse(saved);
        const parsedHistories = parsed.map((h: any) => ({
          ...h,
          createdAt: new Date(h.createdAt),
          updatedAt: new Date(h.updatedAt),
          messages: h.messages.map((m: any) => ({
            ...m,
            timestamp: new Date(m.timestamp),
          })),
        }));
        setHistories(parsedHistories);
      }
    } catch (error) {
      console.error('加载历史记录失败:', error);
      message.error('加载历史记录失败');
    } finally {
      setLoading(false);
    }
  };

  const saveHistories = (newHistories: ChatHistoryItem[]) => {
    try {
      // 限制历史记录数量
      const limitedHistories = newHistories.slice(0, MAX_HISTORY_SIZE);
      localStorage.setItem(HISTORY_STORAGE_KEY, JSON.stringify(limitedHistories));
      setHistories(limitedHistories);
    } catch (error) {
      console.error('保存历史记录失败:', error);
      message.error('保存历史记录失败');
    }
  };

  const createHistory = (messages: ChatMessage[], model: string = 'simulated') => {
    // 只在有用户消息时才创建历史记录
    const userMessages = messages.filter(m => m.role === 'user');
    if (userMessages.length === 0) return null;

    const firstUserMessage = userMessages[0];
    const title = firstUserMessage.content.slice(0, 50) + (firstUserMessage.content.length > 50 ? '...' : '');

    const newHistory: ChatHistoryItem = {
      id: Date.now().toString(),
      title,
      messages,
      createdAt: new Date(),
      updatedAt: new Date(),
      model,
    };

    saveHistories([newHistory, ...histories]);
    return newHistory;
  };

  const updateHistory = (id: string, updates: Partial<ChatHistoryItem>) => {
    const updatedHistories = histories.map(h =>
      h.id === id ? { ...h, ...updates, updatedAt: new Date() } : h
    );
    saveHistories(updatedHistories);
  };

  const deleteHistory = (id: string) => {
    const updatedHistories = histories.filter(h => h.id !== id);
    saveHistories(updatedHistories);
    message.success('已删除历史记录');
  };

  const clearAllHistories = () => {
    saveHistories([]);
    message.success('已清空所有历史记录');
  };

  const toggleFavorite = (id: string) => {
    const history = histories.find(h => h.id === id);
    if (history) {
      const newFavoriteStatus = !history.isFavorite;
      updateHistory(id, { isFavorite: newFavoriteStatus });
      message.success(newFavoriteStatus ? '已收藏' : '已取消收藏');
    }
  };

  const getHistory = (id: string) => {
    return histories.find(h => h.id === id);
  };

  const searchHistories = (keyword: string) => {
    if (!keyword.trim()) return histories;
    const lowerKeyword = keyword.toLowerCase();
    return histories.filter(h =>
      h.title.toLowerCase().includes(lowerKeyword) ||
      h.messages.some(m => m.content.toLowerCase().includes(lowerKeyword))
    );
  };

  const getFavoriteHistories = () => {
    return histories.filter(h => h.isFavorite);
  };

  const exportHistory = (id: string) => {
    const history = getHistory(id);
    if (!history) {
      message.error('未找到历史记录');
      return;
    }

    try {
      const exportData = {
        title: history.title,
        createdAt: history.createdAt.toISOString(),
        messages: history.messages,
      };

      const blob = new Blob([JSON.stringify(exportData, null, 2)], { type: 'application/json' });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `chat-history-${history.id}.json`;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      URL.revokeObjectURL(url);
      message.success('导出成功');
    } catch (error) {
      console.error('导出失败:', error);
      message.error('导出失败');
    }
  };

  const exportAllHistories = () => {
    try {
      const exportData = {
        exportDate: new Date().toISOString(),
        totalHistories: histories.length,
        histories: histories.map(h => ({
          title: h.title,
          createdAt: h.createdAt.toISOString(),
          updatedAt: h.updatedAt.toISOString(),
          messages: h.messages,
        })),
      };

      const blob = new Blob([JSON.stringify(exportData, null, 2)], { type: 'application/json' });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `all-chat-histories-${Date.now()}.json`;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      URL.revokeObjectURL(url);
      message.success('导出成功');
    } catch (error) {
      console.error('导出失败:', error);
      message.error('导出失败');
    }
  };

  const importHistory = (file: File) => {
    return new Promise<ChatHistoryItem>((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = (e) => {
        try {
          const content = e.target?.result as string;
          const data = JSON.parse(content);

          // 支持单条历史记录导入
          if (data.title && data.messages) {
            const newHistory: ChatHistoryItem = {
              id: Date.now().toString(),
              title: data.title || '导入的对话',
              messages: data.messages.map((m: any) => ({
                ...m,
                timestamp: new Date(m.timestamp),
              })),
              createdAt: data.createdAt ? new Date(data.createdAt) : new Date(),
              updatedAt: new Date(),
            };
            saveHistories([newHistory, ...histories]);
            message.success('导入成功');
            resolve(newHistory);
          } else {
            reject(new Error('无效的文件格式'));
          }
        } catch (error) {
          console.error('导入失败:', error);
          message.error('导入失败');
          reject(error);
        }
      };
      reader.onerror = () => {
        message.error('文件读取失败');
        reject(new Error('文件读取失败'));
      };
      reader.readAsText(file);
    });
  };

  return {
    histories,
    loading,
    createHistory,
    updateHistory,
    deleteHistory,
    clearAllHistories,
    toggleFavorite,
    getHistory,
    searchHistories,
    getFavoriteHistories,
    exportHistory,
    exportAllHistories,
    importHistory,
    loadHistories,
  };
};
