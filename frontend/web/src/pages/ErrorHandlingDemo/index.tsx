import { useState } from 'react';
import { Card, Button, Space, Divider, message } from 'antd';
import { 
  useErrorHandler, 
  withErrorHandler, 
  ErrorBoundary 
} from '@/components/ErrorHandling';
import { ErrorState } from '@/components/ErrorState';

/**
 * 演示组件：网络错误处理
 */
const NetworkErrorDemo: React.FC = () => {
  const errorHandler = useErrorHandler();
  const [error, setError] = useState(false);

  const simulateNetworkError = () => {
    setError(true);
    setTimeout(() => setError(false), 3000);
  };

  return (
    <Card title="网络错误演示">
      <Space direction="vertical" style={{ width: '100%' }}>
        <Button type="primary" onClick={simulateNetworkError}>
          模拟网络错误
        </Button>
        <Button onClick={() => errorHandler.handleNetworkError(new Error('Network Error'))}>
          触发错误处理
        </Button>
        
        {error && (
          <ErrorState
            errorType="network"
            showAutoRetry
            autoRetryInterval={5}
            onRetry={() => {
              message.success('重试成功');
              setError(false);
            }}
          />
        )}
      </Space>
    </Card>
  );
};

/**
 * 演示组件：接口异常处理
 */
const APIErrorDemo: React.FC = () => {
  const errorHandler = useErrorHandler();
  const [showTimeoutError, setShowTimeoutError] = useState(false);
  const [showServerError, setShowServerError] = useState(false);

  const simulateTimeoutError = () => {
    setShowTimeoutError(true);
    setTimeout(() => setShowTimeoutError(false), 3000);
  };

  const simulateServerError = () => {
    setShowServerError(true);
    setTimeout(() => setShowServerError(false), 3000);
  };

  const handleAPIError = async () => {
    // 模拟 API 错误并显示 Toast
    errorHandler.handleAPIError(new Error('API Error'), { cachedData: '缓存数据' });
  };

  return (
    <Card title="接口异常演示">
      <Space direction="vertical" style={{ width: '100%' }}>
        <Button onClick={simulateTimeoutError}>模拟超时错误</Button>
        <Button onClick={simulateServerError}>模拟服务器错误</Button>
        <Button type="primary" onClick={handleAPIError}>
          触发 API 错误处理
        </Button>
        
        {showTimeoutError && (
          <ErrorState
            errorType="timeout"
            onRetry={() => {
              message.success('重试成功');
              setShowTimeoutError(false);
            }}
          />
        )}
        
        {showServerError && (
          <ErrorState
            errorType="server"
            onRetry={() => {
              message.success('重试成功');
              setShowServerError(false);
            }}
          />
        )}
      </Space>
    </Card>
  );
};

/**
 * 演示组件：缓存降级
 */
const CacheFallbackDemo: React.FC = () => {
  const errorHandler = useErrorHandler();
  const [data, setData] = useState<any>(null);
  const [loading, setLoading] = useState(false);

  const fetchDataWithCache = async () => {
    setLoading(true);
    try {
      // 模拟 API 请求
      await new Promise((_, reject) => {
        setTimeout(() => reject(new Error('API Error')), 500);
      });
    } catch (error) {
      // 使用缓存的降级数据
      const cached = errorHandler.getCache<any>('demo_data');
      if (cached?.data) {
        setData(cached.data);
        errorHandler.showWarningToast('使用缓存数据');
      } else {
        // 设置一些测试数据到缓存
        const testData = { id: 1, name: '测试数据', timestamp: Date.now() };
        errorHandler.setCache('demo_data', testData);
        setData(testData);
        errorHandler.showInfoToast('已存储到缓存');
      }
    } finally {
      setLoading(false);
    }
  };

  const clearCache = () => {
    errorHandler.clearCache('demo_data');
    setData(null);
    message.success('缓存已清除');
  };

  return (
    <Card title="缓存降级演示">
      <Space direction="vertical" style={{ width: '100%' }}>
        <Button type="primary" onClick={fetchDataWithCache} loading={loading}>
          获取数据（带缓存）
        </Button>
        <Button onClick={clearCache}>清除缓存</Button>
        
        {data && (
          <div style={{ padding: 16, background: '#f5f5f5', borderRadius: 8 }}>
            <pre>{JSON.stringify(data, null, 2)}</pre>
          </div>
        )}
      </Space>
    </Card>
  );
};

/**
 * 演示组件：自动重试
 */
const AutoRetryDemo: React.FC = () => {
  const errorHandler = useErrorHandler();
  const [retrying, setRetrying] = useState(false);
  const [success, setSuccess] = useState(false);

  const simulateRetry = async () => {
    setRetrying(true);
    setSuccess(false);
    
    await errorHandler.executeWithRetry(
      async () => {
        // 模拟随机成功/失败
        const shouldFail = Math.random() > 0.5;
        if (shouldFail) {
          throw new Error('Random failure');
        }
        return { success: true };
      },
      {
        onSuccess: () => {
          setSuccess(true);
          message.success('操作成功！');
        },
        onError: () => {
          message.error('操作失败');
        },
        onFinally: () => {
          setRetrying(false);
        },
      }
    );
  };

  return (
    <Card title="自动重试演示">
      <Space direction="vertical" style={{ width: '100%' }}>
        <Button type="primary" onClick={simulateRetry} loading={retrying}>
          执行操作（随机失败）
        </Button>
        
        {retrying && <div style={{ color: '#1890ff' }}>正在重试... {errorHandler.retryCount}</div>}
        {success && <div style={{ color: '#52c41a' }}>操作成功！</div>}
      </Space>
    </Card>
  );
};

/**
 * 演示组件：错误边界
 */
const ErrorBoundaryDemo: React.FC = () => {
  const [throwError, setThrowError] = useState(false);

  if (throwError) {
    throw new Error('这是一个测试错误');
  }

  return (
    <Card title="错误边界演示">
      <Space>
        <Button danger onClick={() => setThrowError(true)}>
          触发渲染错误
        </Button>
      </Space>
    </Card>
  );
};

export default function ErrorHandlingDemoPage() {
  return (
    <div style={{ padding: 24 }}>
      <h1>错误处理优化演示</h1>
      
      <Space direction="vertical" style={{ width: '100%' }} size="large">
        <NetworkErrorDemo />
        <APIErrorDemo />
        <CacheFallbackDemo />
        <AutoRetryDemo />
        
        <Divider>错误边界演示</Divider>
        
        <ErrorBoundary
          fallback={
            <ErrorState
              errorType="server"
              title="组件渲染出错"
              description="抱歉，页面加载失败，请刷新重试"
              onRetry={() => window.location.reload()}
            />
          }
        >
          <ErrorBoundaryDemo />
        </ErrorBoundary>
      </Space>
    </div>
  );
}
