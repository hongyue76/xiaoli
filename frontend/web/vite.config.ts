import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'path';

export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  server: {
    port: 3000,
    proxy: {
      // 法律咨询 (18081)
      '/api/consult': {
        target: 'http://localhost:18081',
        changeOrigin: true,
      },
      // 文书生成 (18082)
      '/api/document': {
        target: 'http://localhost:18082',
        changeOrigin: true,
      },
      // 案例检索 (18083)
      '/api/case': {
        target: 'http://localhost:18083',
        changeOrigin: true,
      },
      // 合同审查 (18084)
      '/api/contract': {
        target: 'http://localhost:18084',
        changeOrigin: true,
      },
      // 案件分析 (18085)
      '/api/analysis': {
        target: 'http://localhost:18085',
        changeOrigin: true,
      },
      // 司法决策 (18086)
      '/api/decision': {
        target: 'http://localhost:18086',
        changeOrigin: true,
      },
      // 企业合规 (18087)
      '/api/compliance': {
        target: 'http://localhost:18087',
        changeOrigin: true,
      },
      // 证据分析 (18088)
      '/api/evidence': {
        target: 'http://localhost:18088',
        changeOrigin: true,
      },
      // 语音服务 (18089)
      '/api/speech': {
        target: 'http://localhost:18089',
        changeOrigin: true,
      },
      // 认证服务
      '/api/auth': {
        target: 'http://localhost:18081',
        changeOrigin: true,
      },
      // 公共API
      '/api/public': {
        target: 'http://localhost:18081',
        changeOrigin: true,
      },
      // 意图路由
      '/api/intent-router': {
        target: 'http://localhost:18081',
        changeOrigin: true,
      },
    },
  },
  build: {
    outDir: 'dist',
    sourcemap: false,
  },
});
