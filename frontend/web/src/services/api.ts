import axios, { AxiosRequestConfig, AxiosResponse } from 'axios';

const request = axios.create({
  baseURL: '/api',
  timeout: 30000,
});

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 添加token
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器 - 修正支持两种常见响应格式
request.interceptors.response.use(
  (response: AxiosResponse) => {
    const { code, data } = response.data;
    // 兼容 code=0 和 code=200 两种情况
    if (code === 0 || code === 200) {
      return data;
    }
    return Promise.reject(new Error(response.data.message || '请求失败'));
  },
  (error) => {
    return Promise.reject(error);
  }
);

export default request;

// 法律咨询API
export const consultAPI = {
  // 智能问答
  chat: (data: { messages: any[] }) => request.post('/consult/chat', data),

  // 简单问答
  ask: (question: string) => request.get('/consult/ask', { params: { question } }),

  // 获取咨询历史
  getHistory: (params: { userId?: number; current?: number; size?: number }) =>
    request.get('/consult/history', { params }),

  // 获取会话详情
  getConversation: (id: number) => request.get(`/consult/conversation/${id}`),

  // 获取消息列表
  getMessages: (conversationId: number) => request.get(`/consult/messages/${conversationId}`),

  // 获取咨询分类
  getCategories: () => request.get('/consult/categories'),
};

// 文书生成API
export const documentAPI = {
  // 获取模板列表
  getTemplates: (params: { caseType?: string; templateType?: string; current?: number; size?: number }) =>
    request.get('/document/templates', { params }),

  // 获取所有模板
  getAllTemplates: () => request.get('/document/templates/all'),

  // 获取模板详情
  getTemplateDetail: (id: number) => request.get(`/document/templates/${id}`),

  // 生成文书
  generate: (data: { templateId: number; caseId?: number; data: any; aiAssist?: boolean }) =>
    request.post('/document/generate', data),

  // 获取文书详情
  getDetail: (id: number) => request.get(`/document/${id}`),

  // 获取我的文书
  getMyDocuments: (params: { userId: number; status?: string; current?: number; size?: number }) =>
    request.get('/document/my', { params }),

  // 更新文书
  update: (id: number, content: string) => request.put(`/document/${id}`, content),

  // 删除文书
  delete: (id: number) => request.delete(`/document/${id}`),

  // 导出PDF
  exportPdf: (id: number) => request.get(`/document/${id}/export`),

  // 获取文书类型
  getTypes: () => request.get('/document/types'),
};

// 案例检索API
export const caseAPI = {
  // 案例检索
  search: (data: {
    keyword?: string;
    caseType?: string;
    court?: string;
    year?: number;
    caseStatus?: string;
    sortBy?: string;
    current?: number;
    size?: number;
    semantic?: boolean;
  }) => request.post('/case/search', data),

  // 获取案例详情
  getDetail: (id: number) => request.get(`/case/${id}`),

  // 获取相似案例
  getSimilar: (id: number, limit?: number) => request.get(`/case/${id}/similar`, { params: { limit } }),

  // 获取热门案例
  getHotCases: (params: { caseType?: string; limit?: number }) => request.get('/case/hot', { params }),

  // 获取案件类型
  getTypes: () => request.get('/case/types'),

  // 获取案件子类型
  getSubTypes: (type: string) => request.get(`/case/types/${type}`),

  // 得理法搜案例检索
  searchByDelilegal: (data: {
    keywords?: string[];
    longText?: string;
    courtLevelArr?: string[];
    judgementTypeArr?: string[];
  }) => request.post('/case/delilegal/search', data),

  // 得理法搜法规检索
  searchLaws: (data: {
    keywords: string[];
    fieldName: 'title' | 'semantic';
  }) => request.post('/case/delilegal/law', data),
};

// 意图路由API
export const intentRouterAPI = {
  // 分析用户意图
  analyzeIntent: (question: string) => request.post('/intent-router/analyze', { question }),

  // 带检索的智能问答
  chatWithRetrieval: (data: { question: string; userId?: number; sessionId?: string }) =>
    request.post('/intent-router/chat', data),
};
