import { Routes, Route } from 'react-router-dom';
import { lazy, Suspense } from 'react';
import { Alert, Spin } from 'antd';
import {
  WarningOutlined,
} from '@ant-design/icons';
import './App.css';
import MainLayout from './components/MainLayout';
import AIChat from './components/AIChat';
import AnimatedRoutes from './components/Animations/AnimatedRoutes';
import { HotkeyProvider, HotkeyHelp } from './components/Hotkeys';
import AppHotkeys from './components/Hotkeys/AppHotkeys';

// 路由懒加载
const Defense = lazy(() => import('./pages/Defense'));
const JudgeProfile = lazy(() => import('./pages/JudgeProfile'));
const Consult = lazy(() => import('./pages/Consult'));
const Document = lazy(() => import('./pages/Document'));
const CaseSearch = lazy(() => import('./pages/CaseSearch'));
const Contract = lazy(() => import('./pages/Contract'));
const Analysis = lazy(() => import('./pages/Analysis'));
const Evidence = lazy(() => import('./pages/Evidence'));
const Compliance = lazy(() => import('./pages/Compliance'));
const IntentRouter = lazy(() => import('./pages/IntentRouter'));
const ThemePreview = lazy(() => import('./pages/ThemePreview'));

// 加载中组件
const LoadingFallback = () => (
  <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', padding: '100px 0' }}>
    <Spin size="large" tip="加载中..." />
  </div>
);

function App() {
  const renderAIChat = () => {
    return null; // AI聊天已移至首页主内容区域
  };

  const renderMainContent = () => {
    return (
      <Suspense fallback={<LoadingFallback />}>
        <AnimatedRoutes type="slide-up" duration={300}>
          <Routes>
            <Route path="/" element={
              <div>
                <h2>欢迎使用律法先锋法律AI助手</h2>
                <Alert
                  message="重要声明"
                  description={
                    <div>
                      <p><strong>本平台提供的所有法律咨询服务均由AI驱动，仅供用户参考，不构成正式的法律意见或建议。</strong></p>
                      <p>• AI生成内容可能存在错误或遗漏，请谨慎使用</p>
                      <p>• 涉及重要法律事务时，请咨询专业律师</p>
                      <p>• 本平台不对使用AI生成内容造成的任何损失承担责任</p>
                    </div>
                  }
                  type="warning"
                  showIcon
                  icon={<WarningOutlined />}
                  style={{ marginBottom: 24 }}
                />
                <AIChat />
              </div>
            } />

            <Route path="/consult" element={<Consult />} />
            <Route path="/document" element={<Document />} />
            <Route path="/defense" element={<Defense />} />
            <Route path="/case" element={<CaseSearch />} />
            <Route path="/judge-profile" element={<JudgeProfile />} />
            <Route path="/contract" element={<Contract />} />
            <Route path="/analysis" element={<Analysis />} />
            <Route path="/evidence" element={<Evidence />} />
            <Route path="/compliance" element={<Compliance />} />
            <Route path="/intent-router" element={<IntentRouter />} />
            <Route path="/theme-preview" element={<ThemePreview />} />

            <Route path="*" element={
              <div>
                <h2>页面开发中</h2>
                <Alert
                  message="功能开发中"
                  description="该功能正在开发中，敬请期待"
                  type="info"
                  showIcon
                />
              </div>
            } />
          </Routes>
        </AnimatedRoutes>
      </Suspense>
    );
  };

  return (
    <MainLayout
      leftContent={<div>左侧功能菜单已集成到布局中</div>}
      mainContent={renderMainContent()}
      rightContent={null}
    />
  );
}

export default App;
