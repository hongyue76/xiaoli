import { message } from 'antd';
import { useCallback, useState } from 'react';

export interface ErrorConfig {
  /** Toast 提示时长（毫秒） */
  toastDuration?: number;
  /** 是否自动降级到缓存数据 */
  enableFallback?: boolean;
  /** 最大重试次数 */
  maxRetries?: number;
  /** 重试延迟（毫秒） */
  retryDelay?: number;
  /** 自定义错误处理 */
  customErrorHandler?: (error: any) => void;
}

export interface FallbackData<T> {
  data: T | null;
  isFallback: boolean;
  timestamp: number;
}

const defaultConfig: Required<ErrorConfig> = {
  toastDuration: 3000,
  enableFallback: true,
  maxRetries: 3,
  retryDelay: 1000,
  customErrorHandler: undefined,
};

/**
 * 错误处理 Hook - 提供统一的错误处理、Toast 提示和降级方案
 */
export const useErrorHandler = (config: ErrorConfig = {}) => {
  const finalConfig = { ...defaultConfig, ...config };
  const [retryCount, setRetryCount] = useState(0);
  const [isRetrying, setIsRetrying] = useState(false);

  /**
   * 显示错误 Toast 提示
   */
  const showErrorToast = useCallback((errorMessage: string, duration?: number) => {
    message.error(errorMessage, duration ?? finalConfig.toastDuration);
  }, [finalConfig.toastDuration]);

  /**
   * 显示警告 Toast 提示
   */
  const showWarningToast = useCallback((warningMessage: string, duration?: number) => {
    message.warning(warningMessage, duration ?? finalConfig.toastDuration);
  }, [finalConfig.toastDuration]);

  /**
   * 显示信息 Toast 提示
   */
  const showInfoToast = useCallback((infoMessage: string, duration?: number) => {
    message.info(infoMessage, duration ?? finalConfig.toastDuration);
  }, [finalConfig.toastDuration]);

  /**
   * 处理网络错误
   */
  const handleNetworkError = useCallback((error: any) => {
    console.error('网络错误:', error);
    
    const errorMessage = error?.response?.data?.message || 
                         error?.message || 
                         '网络连接失败，请检查您的网络';
    
    showErrorToast(errorMessage);

    if (finalConfig.customErrorHandler) {
      finalConfig.customErrorHandler(error);
    }
  }, [showErrorToast, finalConfig.customErrorHandler]);

  /**
   * 处理接口异常
   */
  const handleAPIError = useCallback((error: any, fallbackData?: any) => {
    console.error('API 错误:', error);

    let errorMessage = '请求失败，请稍后重试';
    let errorType: 'network' | 'timeout' | 'server' | 'permission' | 'notFound' = 'server';

    // 判断错误类型
    if (error?.name === 'AbortError' || error?.code === 'ECONNABORTED') {
      errorType = 'timeout';
      errorMessage = '请求超时，请稍后重试';
    } else if (!error?.response && error?.message === 'Network Error') {
      errorType = 'network';
      errorMessage = '网络连接失败';
    } else if (error?.response?.status === 401) {
      errorType = 'permission';
      errorMessage = '未授权，请重新登录';
    } else if (error?.response?.status === 403) {
      errorType = 'permission';
      errorMessage = '没有权限访问该资源';
    } else if (error?.response?.status === 404) {
      errorType = 'notFound';
      errorMessage = '请求的资源不存在';
    } else if (error?.response?.status >= 500) {
      errorType = 'server';
      errorMessage = '服务器错误，请稍后重试';
    } else if (error?.response?.data?.message) {
      errorMessage = error.response.data.message;
    }

    showErrorToast(errorMessage);

    // 如果启用降级且有缓存数据，返回缓存数据
    if (finalConfig.enableFallback && fallbackData) {
      console.log('使用缓存数据作为降级方案');
      showWarningToast('显示缓存数据');
      return fallbackData;
    }

    if (finalConfig.customErrorHandler) {
      finalConfig.customErrorHandler({ ...error, errorType, errorMessage });
    }

    return null;
  }, [showErrorToast, showWarningToast, finalConfig.enableFallback, finalConfig.customErrorHandler]);

  /**
   * 带重试的异步操作
   */
  const executeWithRetry = useCallback(async <T,>(
    fn: () => Promise<T>,
    options?: {
      onSuccess?: (data: T) => void;
      onError?: (error: any) => void;
      onFinally?: () => void;
    }
  ): Promise<T | null> => {
    setIsRetrying(true);
    
    try {
      const result = await fn();
      setRetryCount(0);
      
      if (options?.onSuccess) {
        options.onSuccess(result);
      }
      
      return result;
    } catch (error) {
      if (retryCount < finalConfig.maxRetries) {
        console.log(`重试 ${retryCount + 1}/${finalConfig.maxRetries}...`);
        await new Promise(resolve => setTimeout(resolve, finalConfig.retryDelay));
        setRetryCount(prev => prev + 1);
        return executeWithRetry(fn, options);
      }
      
      setRetryCount(0);
      handleNetworkError(error);
      
      if (options?.onError) {
        options.onError(error);
      }
      
      return null;
    } finally {
      setIsRetrying(false);
      if (options?.onFinally) {
        options.onFinally();
      }
    }
  }, [retryCount, finalConfig, handleNetworkError, setIsRetrying]);

  /**
   * 安全执行异步操作（不抛出错误）
   */
  const safeExecute = useCallback(async <T,>(
    fn: () => Promise<T>,
    fallback?: T | null
  ): Promise<T | null> => {
    try {
      return await fn();
    } catch (error) {
      handleAPIError(error, fallback);
      return fallback ?? null;
    }
  }, [handleAPIError]);

  /**
   * 重置重试计数
   */
  const resetRetryCount = useCallback(() => {
    setRetryCount(0);
  }, []);

  /**
   * 存储缓存数据
   */
  const setCache = useCallback(<T,>(key: string, data: T, ttl = 3600000) => {
    const cacheData: FallbackData<T> = {
      data,
      isFallback: false,
      timestamp: Date.now(),
    };
    localStorage.setItem(`cache_${key}`, JSON.stringify({
      data: cacheData,
      expireAt: Date.now() + ttl,
    }));
  }, []);

  /**
   * 获取缓存数据
   */
  const getCache = useCallback(<T,>(key: string): FallbackData<T> | null => {
    try {
      const cacheStr = localStorage.getItem(`cache_${key}`);
      if (!cacheStr) return null;

      const cache = JSON.parse(cacheStr);
      if (cache.expireAt && Date.now() > cache.expireAt) {
        localStorage.removeItem(`cache_${key}`);
        return null;
      }

      return {
        ...cache.data,
        isFallback: true,
      };
    } catch (error) {
      console.error('读取缓存失败:', error);
      return null;
    }
  }, []);

  /**
   * 清除缓存
   */
  const clearCache = useCallback((key?: string) => {
    if (key) {
      localStorage.removeItem(`cache_${key}`);
    } else {
      // 清除所有缓存
      Object.keys(localStorage)
        .filter(k => k.startsWith('cache_'))
        .forEach(k => localStorage.removeItem(k));
    }
  }, []);

  /**
   * 带缓存的异步操作
   */
  const executeWithCache = useCallback(async <T,>(
    key: string,
    fn: () => Promise<T>,
    ttl = 3600000
  ): Promise<T | null> => {
    try {
      // 尝试获取缓存
      const cached = getCache<T>(key);
      if (cached?.data) {
        console.log('使用缓存数据:', key);
        showInfoToast('显示缓存数据');
        return cached.data;
      }

      // 执行请求
      const result = await fn();
      
      // 存储缓存
      if (result) {
        setCache(key, result, ttl);
      }
      
      return result;
    } catch (error) {
      // 出错时尝试使用缓存
      const cached = getCache<T>(key);
      if (cached?.data && finalConfig.enableFallback) {
        showWarningToast('请求失败，显示缓存数据');
        return cached.data;
      }
      
      handleAPIError(error, cached?.data);
      return cached?.data ?? null;
    }
  }, [getCache, setCache, showInfoToast, showWarningToast, finalConfig.enableFallback, handleAPIError]);

  return {
    retryCount,
    isRetrying,
    showErrorToast,
    showWarningToast,
    showInfoToast,
    handleNetworkError,
    handleAPIError,
    executeWithRetry,
    safeExecute,
    resetRetryCount,
    setCache,
    getCache,
    clearCache,
    executeWithCache,
  };
};

/**
 * 高阶组件 - 包装组件添加错误处理
 */
export const withErrorHandler = <P extends object>(
  WrappedComponent: React.ComponentType<P>,
  config: ErrorConfig = {}
) => {
  return (props: P) => {
    const errorHandlers = useErrorHandler(config);
    return <WrappedComponent {...props} {...errorHandlers} />;
  };
};

/**
 * 错误边界组件
 */
export class ErrorBoundary extends React.Component<
  {
    children: React.ReactNode;
    fallback?: React.ReactNode;
    onError?: (error: Error, errorInfo: React.ErrorInfo) => void;
  },
  { hasError: boolean; error: Error | null }
> {
  constructor(props: any) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error: Error) {
    return { hasError: true, error };
  }

  componentDidCatch(error: Error, errorInfo: React.ErrorInfo) {
    console.error('错误边界捕获错误:', error, errorInfo);
    
    if (this.props.onError) {
      this.props.onError(error, errorInfo);
    }
  }

  render() {
    if (this.state.hasError) {
      return this.props.fallback || (
        <div style={{ padding: 48, textAlign: 'center' }}>
          <h3>组件渲染出错</h3>
          <p>{this.state.error?.message}</p>
        </div>
      );
    }

    return this.props.children;
  }
}
