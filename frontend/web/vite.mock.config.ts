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
    // 静态数据模式：禁用代理，让前端使用内置的静态数据
    proxy: {},
  },
  build: {
    outDir: 'dist',
    sourcemap: false,
  },
});
