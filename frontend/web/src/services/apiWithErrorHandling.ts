import axios, { AxiosRequestConfig, AxiosError } from 'axios';
import { useErrorHandler } from '@/components/ErrorHandling';

// 创建 axios 实例 - 使用相对路径，通过 vite 代理转发
const apiClient = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 请求拦截器
apiClient.interceptors.request.use(
  (config) => {
    // 添加 token
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    // 添加请求 ID 用于追踪
    config.headers['X-Request-ID'] = Date.now().toString();
    
    console.log(`[API Request] ${config.method?.toUpperCase()} ${config.url}`, config.data);
    
    return config;
  },
  (error) => {
    console.error('[API Request Error]', error);
    return Promise.reject(error);
  }
);

// 响应拦截器 - 自动重试机制
let retryCount = 0;
const MAX_RETRY_COUNT = 3;

apiClient.interceptors.response.use(
  (response) => {
    retryCount = 0; // 重置重试计数
    console.log(`[API Response] ${response.config.url}`, response.data);
    return response;
  },
  async (error: AxiosError) => {
    const config = error.config as any;
    
    if (!config) {
      return Promise.reject(error);
    }

    console.error(`[API Error] ${config.url}`, error);

    // 网络错误或 5xx 错误自动重试
    const shouldRetry =
      (!error.response && error.message === 'Network Error') ||
      (error.response?.status && error.response.status >= 500);

    if (shouldRetry && retryCount < MAX_RETRY_COUNT) {
      retryCount++;
      console.log(`[API Retry] ${retryCount}/${MAX_RETRY_COUNT} - ${config.url}`);
      
      // 延迟重试
      await new Promise(resolve => setTimeout(resolve, 1000 * retryCount));
      
      return apiClient(config);
    }

    // 重置重试计数
    retryCount = 0;

    // 401 未授权
    if (error.response?.status === 401) {
      console.log('[Auth] 未授权，清除 token');
      localStorage.removeItem('token');
      window.location.href = '/login';
    }

    return Promise.reject(error);
  }
);

/**
 * 带错误处理的 API 请求方法
 */
export const createRequest = <T = any>(
  errorHandler: ReturnType<typeof useErrorHandler>
) => {
  return {
    get: async <T = any>(url: string, config?: AxiosRequestConfig): Promise<T | null> => {
      try {
        const response = await apiClient.get<T>(url, config);
        return response.data;
      } catch (error: any) {
        return errorHandler.handleAPIError(error);
      }
    },

    post: async <T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T | null> => {
      try {
        const response = await apiClient.post<T>(url, data, config);
        return response.data;
      } catch (error: any) {
        return errorHandler.handleAPIError(error);
      }
    },

    put: async <T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T | null> => {
      try {
        const response = await apiClient.put<T>(url, data, config);
        return response.data;
      } catch (error: any) {
        return errorHandler.handleAPIError(error);
      }
    },

    delete: async <T = any>(url: string, config?: AxiosRequestConfig): Promise<T | null> => {
      try {
        const response = await apiClient.delete<T>(url, config);
        return response.data;
      } catch (error: any) {
        return errorHandler.handleAPIError(error);
      }
    },

    // 带缓存的 GET 请求
    getWithCache: async <T = any>(
      url: string,
      ttl: number = 3600000,
      config?: AxiosRequestConfig
    ): Promise<T | null> => {
      const cacheKey = url;
      return errorHandler.executeWithCache(cacheKey, () => apiClient.get<T>(url, config).then(r => r.data), ttl);
    },

    // 带重试的请求
    withRetry: async <T = any>(
      fn: () => Promise<T>,
      options?: {
        onSuccess?: (data: T) => void;
        onError?: (error: any) => void;
        onFinally?: () => void;
      }
    ): Promise<T | null> => {
      return errorHandler.executeWithRetry(fn, options);
    },
  };
};

export default apiClient;
