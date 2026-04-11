import axios from 'axios';

// 使用相对路径，通过 vite 代理转发
const INTENT_ROUTER_BASE_URL = '/api';

export interface Intent {
  type: 'PROFESSIONAL' | 'GENERAL' | 'CHAT' | 'WEATHER' | 'SUMMARY' | 'AMBIGUOUS';
  needSearch: boolean;
  confidence: number;
  reason: string;
  originalQuestion: string;
}

export interface RouteRequest {
  question: string;
  context?: Record<string, any>;
}

export interface RouteResponse {
  success: boolean;
  intentType?: Intent['type'];
  processType?: string;
  answer?: string;
  searchResults?: string;
  errorMessage?: string;
  duration?: number;
}

/**
 * 意图路由服务 - 前端调用封装
 */
export const intentRouterService = {
  /**
   * 分析用户意图
   */
  async analyzeIntent(question: string): Promise<{ success: boolean; intent?: Intent; errorMessage?: string }> {
    try {
      const response = await axios.post<{ success: boolean; intent?: Intent }>(
        `${INTENT_ROUTER_BASE_URL}/intent-router/analyze`,
        { question }
      );
      
      console.log('意图分析响应:', response.data);
      return response.data;
    } catch (error: any) {
      console.error('意图分析请求失败:', error);
      
      return {
        success: false,
        errorMessage: error.response?.data?.errorMessage || error.message || '请求失败',
      };
    }
  },

  /**
   * 路由问题（智能问答）
   */
  async routeQuestion(question: string): Promise<RouteResponse> {
    const startTime = Date.now();
    
    try {
      const response = await axios.post<RouteResponse>(
        `${INTENT_ROUTER_BASE_URL}/intent-router/chat`,
        { question }
      );
      
      response.data.duration = Date.now() - startTime;
      
      console.log('意图路由响应:', response.data);
      return response.data;
    } catch (error: any) {
      console.error('意图路由请求失败:', error);
      
      return {
        success: false,
        errorMessage: error.response?.data?.errorMessage || error.message || '请求失败',
      };
    }
  },

  /**
   * 判断是否为专业法律问题
   */
  isProfessionalQuestion(intent: Intent | undefined): boolean {
    return intent?.type === 'PROFESSIONAL' || false;
  },

  /**
   * 判断是否需要检索
   */
  requiresSearch(intent: Intent | undefined): boolean {
    return intent?.needSearch || false;
  },

  /**
   * 获取意图类型描述
   */
  getIntentDescription(intent: Intent | undefined): string {
    const descriptions: Record<string, string> = {
      PROFESSIONAL: '专业法律问题',
      GENERAL: '通用问题',
      CHAT: '日常闲聊',
      WEATHER: '天气查询',
      SUMMARY: '内容总结',
      AMBIGUOUS: '意图不明确',
    };
    return intent ? descriptions[intent.type] : '未知';
  },
};

export default intentRouterService;
