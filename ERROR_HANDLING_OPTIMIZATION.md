# 错误处理优化文档

## 概述

错误处理优化提供了完整的网络错误和接口异常处理方案，包括友好错误展示、Toast 提示、自动重连和缓存降级。

## 核心组件

### 1. ErrorState 组件

提供美观的错误状态展示，支持自动重连机制。

#### 特性

- **6 种错误类型插画**：network、timeout、server、permission、notFound、default
- **预设文案**：每种错误类型都有默认的标题和描述
- **自动重连倒计时**：显示秒数倒计时，自动重试
- **手动重试按钮**：用户可以手动点击重试
- **入场动画**：fadeInUp 动画
- **插画震动效果**：错误的视觉反馈

#### 使用示例

```tsx
import { ErrorState } from '@/components/ErrorState';

<ErrorState
  errorType="network"
  showAutoRetry
  autoRetryInterval={5}
  onRetry={handleRetry}
/>
```

#### 参数说明

```typescript
interface ErrorStateProps {
  errorType?: 'network' | 'timeout' | 'server' | 'permission' | 'notFound' | 'default';
  title?: string;           // 自定义标题
  description?: string;     // 自定义描述
  retryText?: string;        // 自定义重试按钮文本
  onRetry?: () => void;      // 重试回调
  showAutoRetry?: boolean;   // 是否显示自动重连倒计时
  autoRetryInterval?: number;// 自动重连间隔（秒）
  extra?: React.ReactNode;   // 额外内容
  compact?: boolean;         // 紧凑模式
  style?: React.CSSProperties;
  className?: string;
}
```

### 2. useErrorHandler Hook

提供统一的错误处理能力。

#### 功能

1. **Toast 提示**
   - `showErrorToast(errorMessage, duration)` - 显示错误提示（3秒）
   - `showWarningToast(warningMessage, duration)` - 显示警告提示
   - `showInfoToast(infoMessage, duration)` - 显示信息提示

2. **错误处理**
   - `handleNetworkError(error)` - 处理网络错误
   - `handleAPIError(error, fallbackData)` - 处理 API 错误，支持降级到缓存数据

3. **重试机制**
   - `executeWithRetry(fn, options)` - 带重试的异步操作
   - `safeExecute(fn, fallback)` - 安全执行，不抛出错误

4. **缓存管理**
   - `setCache(key, data, ttl)` - 存储缓存
   - `getCache(key)` - 获取缓存
   - `clearCache(key)` - 清除缓存
   - `executeWithCache(key, fn, ttl)` - 带缓存的异步操作

#### 使用示例

```tsx
import { useErrorHandler } from '@/components/ErrorHandling';

function MyComponent() {
  const errorHandler = useErrorHandler({
    toastDuration: 3000,
    enableFallback: true,
    maxRetries: 3,
    retryDelay: 1000,
  });

  const loadData = async () => {
    // 使用带重试的请求
    const data = await errorHandler.executeWithRetry(
      async () => {
        const response = await api.getData();
        return response;
      },
      {
        onSuccess: (data) => {
          console.log('成功:', data);
        },
        onError: (error) => {
          console.log('失败:', error);
        },
      }
    );
  };

  const loadDataWithCache = async () => {
    // 使用带缓存的请求
    const data = await errorHandler.executeWithCache(
      'myData',
      () => api.getData(),
      3600000 // 缓存 1 小时
    );
  };

  const handleAPIError = async () => {
    try {
      await api.call();
    } catch (error) {
      // 自动显示 Toast，并支持降级
      const fallbackData = { cached: true };
      return errorHandler.handleAPIError(error, fallbackData);
    }
  };
}
```

#### 配置选项

```typescript
interface ErrorConfig {
  toastDuration?: number;      // Toast 提示时长（毫秒），默认 3000
  enableFallback?: boolean;    // 是否启用降级方案，默认 true
  maxRetries?: number;         // 最大重试次数，默认 3
  retryDelay?: number;         // 重试延迟（毫秒），默认 1000
  customErrorHandler?: (error: any) => void; // 自定义错误处理
}
```

### 3. withErrorHandler 高阶组件

包装组件添加错误处理能力。

```tsx
import { withErrorHandler } from '@/components/ErrorHandling';

const EnhancedComponent = withErrorHandler(
  MyComponent,
  { toastDuration: 2000 }
);
```

### 4. ErrorBoundary 错误边界

捕获 React 组件渲染错误。

```tsx
import { ErrorBoundary } from '@/components/ErrorHandling';

<ErrorBoundary
  fallback={
    <ErrorState
      errorType="server"
      title="组件渲染出错"
      onRetry={() => window.location.reload()}
    />
  }
  onError={(error, errorInfo) => {
    console.error('错误:', error, errorInfo);
  }}
>
  <MyComponent />
</ErrorBoundary>
```

### 5. API 错误处理封装

在 `services/apiWithErrorHandling.ts` 中提供带自动重试和错误处理的 API 封装。

#### 特性

1. **自动重试**：网络错误或 5xx 错误自动重试 3 次
2. **指数退避**：重试延迟逐次增加（1s、2s、3s）
3. **请求拦截**：自动添加 token 和请求 ID
4. **响应拦截**：统一处理 401、403、404 等状态码

#### 使用示例

```tsx
import { createRequest } from '@/services/apiWithErrorHandling';

function MyComponent() {
  const errorHandler = useErrorHandler();
  const request = createRequest(errorHandler);

  const getData = async () => {
    // 普通 GET 请求
    const data = await request.get('/api/data');

    // 带缓存的 GET 请求
    const cachedData = await request.getWithCache('/api/data', 3600000);

    // 带重试的请求
    const result = await request.withRetry(
      () => apiClient.post('/api/action', data),
      {
        onSuccess: () => message.success('成功'),
        onError: () => message.error('失败'),
      }
    );
  };
}
```

## 实际应用

### 案例搜索页面

```tsx
// pages/CaseSearch/index.tsx
import { useErrorHandler } from '@/components/ErrorHandling';

function CaseSearchPage() {
  const errorHandler = useErrorHandler({
    toastDuration: 3000,
    enableFallback: true,
    maxRetries: 3,
  });

  const loadHotCases = async () => {
    // 使用带缓存和重试的请求
    const data = await errorHandler.executeWithCache(
      'hotCases',
      () => caseAPI.getHotCases({ limit: 5 }),
      300000 // 缓存 5 分钟
    );
    setHotCases(data || []);
  };

  const handleSearch = async (page = 1) => {
    setLoading(true);
    
    // 使用带重试的请求
    await errorHandler.executeWithRetry(
      async () => {
        const response = await caseAPI.search({...});
        setCases(response?.records || []);
      },
      {
        onError: (error) => {
          // 出错时使用静态数据作为降级方案
          const staticCases = [...];
          setCases(staticCases);
        },
        onFinally: () => {
          setLoading(false);
        },
      }
    );
  };
}
```

### 网络错误页面

```tsx
function NetworkErrorPage() {
  const [error, setError] = useState(true);

  return (
    <div>
      {error && (
        <ErrorState
          errorType="network"
          showAutoRetry
          autoRetryInterval={5}
          onRetry={() => {
            message.success('重连成功');
            setError(false);
          }}
        />
      )}
    </div>
  );
}
```

## 错误类型说明

| 错误类型 | 插画颜色 | 触发条件 | 默认文案 |
|---------|---------|---------|---------|
| network | 红色 | 网络连接失败 | "网络连接失败" |
| timeout | 橙色 | 请求超时 | "请求超时" |
| server | 蓝色 | 5xx 服务器错误 | "服务器错误" |
| permission | 紫色 | 401/403 权限错误 | "没有权限" |
| notFound | 绿色 | 404 资源未找到 | "资源未找到" |
| default | 灰色 | 其他错误 | "出现错误" |

## Toast 提示规范

1. **错误提示**：红色，3 秒自动消失
   ```tsx
   errorHandler.showErrorToast('操作失败');
   ```

2. **警告提示**：橙色，用于降级场景
   ```tsx
   errorHandler.showWarningToast('显示缓存数据');
   ```

3. **信息提示**：蓝色，用于缓存命中
   ```tsx
   errorHandler.showInfoToast('显示缓存数据');
   ```

## 降级方案

### 1. 静态数据降级

当 API 失败时，使用预设的静态数据：

```tsx
const loadData = async () => {
  try {
    const response = await api.getData();
    setData(response);
  } catch (error) {
    // 使用静态数据
    setData(staticData);
    errorHandler.showWarningToast('显示缓存数据');
  }
};
```

### 2. 缓存数据降级

使用 localStorage 缓存的数据：

```tsx
const loadData = async () => {
  const data = await errorHandler.executeWithCache(
    'myData',
    () => api.getData(),
    3600000
  );
  
  // 如果请求失败，自动返回缓存数据
  if (data) {
    setData(data);
  }
};
```

### 3. 离线数据降级

使用 Service Worker 缓存的数据：

```tsx
const loadData = async () => {
  try {
    const response = await fetch('/api/data');
    return response.json();
  } catch (error) {
    // 从 Service Worker 获取缓存
    const cached = await caches.match('/api/data');
    return cached ? await cached.json() : null;
  }
};
```

## 自动重连机制

### 1. 倒计时重试

显示倒计时，自动重试：

```tsx
<ErrorState
  errorType="network"
  showAutoRetry
  autoRetryInterval={5}
  onRetry={handleRetry}
/>
```

### 2. 手动重试

用户手动点击重试：

```tsx
<ErrorState
  errorType="server"
  onRetry={async () => {
    await loadData();
  }}
/>
```

### 3. 自动指数退避

后端自动重试，延迟递增：

```tsx
const data = await errorHandler.executeWithRetry(
  async () => api.getData(),
  { maxRetries: 5, retryDelay: 1000 }
);
```

## 最佳实践

### 1. 错误分类

根据错误类型选择合适的展示方式：

- **网络错误**：使用 ErrorState + 自动重连
- **API 错误**：使用 Toast 提示 + 降级方案
- **表单错误**：使用表单内联提示
- **全局错误**：使用 ErrorBoundary

### 2. 降级优先级

优先级从高到低：

1. 用户输入的数据
2. localStorage 缓存
3. SessionStorage 缓存
4. 预设静态数据
5. 空状态展示

### 3. 重试策略

- **用户操作**：手动重试，不自动重试
- **数据加载**：自动重试 3 次
- **批量操作**：不重试，记录失败项
- **定时任务**：自动重试直到成功

### 4. 日志记录

所有错误都应该记录到日志：

```tsx
const handleAPIError = (error: any) => {
  // 记录错误日志
  console.error('[API Error]', error);
  
  // 发送到错误监控服务
  trackError(error);
  
  // 显示用户友好的提示
  message.error('操作失败，请稍后重试');
};
```

## 性能优化

1. **缓存 TTL**：根据数据更新频率设置合理的过期时间
2. **请求去重**：相同的请求应该去重，避免重复调用
3. **错误节流**：相同的错误提示应该节流，避免频繁弹出
4. **懒加载**：错误处理模块按需加载

## 注意事项

1. **不要过度重试**：避免无限重试导致服务器压力
2. **不要暴露敏感信息**：错误信息应该对用户友好
3. **提供明确的反馈**：让用户知道发生了什么
4. **保持一致性**：所有错误处理遵循相同规范

## 演示页面

完整的使用示例请参考 `pages/ErrorHandlingDemo/index.tsx`，包含：

- 网络错误演示
- 接口异常演示
- 缓存降级演示
- 自动重试演示
- 错误边界演示
