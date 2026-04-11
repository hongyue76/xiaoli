/**
 * 加载状态组件使用示例页面
 */

import React, { useState } from 'react';
import { Card, Row, Col, Typography, Space, Divider, Button, Input } from 'antd';
import {
  SkeletonLoader,
  ProgressBar,
  LoadingButton,
  StreamOutput,
  SegmentedStreamOutput,
} from '../../components/LoadingStates';
import './index.css';

const { Title, Text, Paragraph } = Typography;

const LoadingStatesDemo: React.FC = () => {
  const [streamContent, setStreamContent] = useState<string>('');
  const [showStream, setShowStream] = useState<boolean>(false);

  const handleStreamStart = () => {
    const content = `这是一个流式输出示例。AI助手正在逐步生成内容，模拟真实的AI响应过程。

**第一步**：分析用户问题
- 理解问题意图
- 提取关键信息
- 确定回答方向

**第二步**：检索相关知识
- 查阅相关法条
- 搜索类似案例
- 整理法律要点

**第三步**：生成完整回复
- 组织逻辑结构
- 撰写详细内容
- 提供专业建议

流式输出可以让用户实时看到生成过程，提升用户体验。`;

    setStreamContent(content);
    setShowStream(true);
  };

  const segments = [
    {
      id: '1',
      title: '问题分析',
      content: '根据您的问题，我进行了深入分析。这是一个关于劳动纠纷的典型案例，涉及劳动合同解除和经济补偿问题。',
      delay: 0,
    },
    {
      id: '2',
      title: '法律依据',
      content: '根据《劳动合同法》第四十六条、第四十七条规定，用人单位依法解除劳动合同的，应当向劳动者支付经济补偿。',
      delay: 3000,
    },
    {
      id: '3',
      title: '处理建议',
      content: '建议您：1. 收集劳动合同、工资单等证据；2. 与用人单位协商；3. 协商不成可申请劳动仲裁。',
      delay: 6000,
    },
  ];

  return (
    <div className="loading-states-demo">
      <div className="demo-header">
        <Title level={2}>加载状态组件展示</Title>
        <Paragraph>
          本页面展示了所有加载状态组件的使用方法，包括骨架屏、进度条、加载按钮和流式输出。
        </Paragraph>
      </div>

      {/* 骨架屏 */}
      <section className="demo-section">
        <Title level={3}>1. 骨架屏 (Skeleton)</Title>
        <Paragraph>
          骨架屏用于在内容加载时提供视觉反馈，避免页面空白。支持列表、卡片、文本、头像等多种类型。
        </Paragraph>

        <Row gutter={[16, 16]} style={{ marginBottom: 16 }}>
          <Col span={24}>
            <Card title="列表骨架" size="small">
              <SkeletonLoader type="list" count={3} avatar={true} />
            </Card>
          </Col>
        </Row>

        <Row gutter={[16, 16]} style={{ marginBottom: 16 }}>
          <Col span={24}>
            <Card title="卡片骨架" size="small">
              <SkeletonLoader type="card" count={6} cols={3} />
            </Card>
          </Col>
        </Row>

        <Row gutter={[16, 16]} style={{ marginBottom: 16 }}>
          <Col span={12}>
            <Card title="文本骨架" size="small">
              <SkeletonLoader type="text" rows={5} />
            </Card>
          </Col>
          <Col span={12}>
            <Card title="输入框骨架" size="small">
              <SkeletonLoader type="input" count={4} />
            </Card>
          </Col>
        </Row>
      </section>

      <Divider />

      {/* 进度条 */}
      <section className="demo-section">
        <Title level={3}>2. 进度反馈 (Progress)</Title>
        <Paragraph>
          进度条用于显示AI生成内容时的进度，包含有趣的等待文案和步骤指示。
        </Paragraph>

        <Row gutter={[16, 16]} style={{ marginBottom: 16 }}>
          <Col span={12}>
            <Card title="默认进度条" size="small">
              <ProgressBar
                current={1}
                percent={35}
                status="active"
                showSteps={true}
                showPercent={true}
                type="default"
              />
            </Card>
          </Col>
          <Col span={12}>
            <Card title="线型进度条" size="small">
              <ProgressBar
                current={2}
                percent={65}
                status="active"
                type="line"
              />
            </Card>
          </Col>
        </Row>

        <Row gutter={[16, 16]} style={{ marginBottom: 16 }}>
          <Col span={12}>
            <Card title="圆形进度条" size="small">
              <ProgressBar
                percent={50}
                status="active"
                type="circle"
              />
            </Card>
          </Col>
          <Col span={12}>
            <Card title="仪表盘进度条" size="small">
              <ProgressBar
                current={1}
                percent={75}
                status="active"
                type="dashboard"
                showSteps={true}
              />
            </Card>
          </Col>
        </Row>
      </section>

      <Divider />

      {/* 加载按钮 */}
      <section className="demo-section">
        <Title level={3}>3. 加载按钮 (Loading Button)</Title>
        <Paragraph>
          加载按钮支持防重复提交，点击后显示loading状态，避免用户重复点击。
        </Paragraph>

        <Card size="small">
          <Space size="large" wrap>
            <LoadingButton
              type="primary"
              onClick={async () => {
                await new Promise(resolve => setTimeout(resolve, 2000));
                console.log('Primary button clicked');
              }}
              loadingText="处理中..."
            >
              主要按钮
            </LoadingButton>

            <LoadingButton
              type="default"
              onClick={async () => {
                await new Promise(resolve => setTimeout(resolve, 2000));
                console.log('Default button clicked');
              }}
              loadingText="加载中..."
            >
              默认按钮
            </LoadingButton>

            <LoadingButton
              type="dashed"
              onClick={async () => {
                await new Promise(resolve => setTimeout(resolve, 2000));
                console.log('Dashed button clicked');
              }}
              loadingText="等待中..."
            >
              虚线按钮
            </LoadingButton>

            <LoadingButton
              danger
              type="primary"
              onClick={async () => {
                await new Promise(resolve => setTimeout(resolve, 2000));
                console.log('Danger button clicked');
              }}
              loadingText="删除中..."
            >
              危险按钮
            </LoadingButton>
          </Space>
        </Card>
      </section>

      <Divider />

      {/* 流式输出 */}
      <section className="demo-section">
        <Title level={3}>4. 流式输出 (Stream Output)</Title>
        <Paragraph>
          流式输出模拟AI生成内容的分段展示效果，让用户实时看到生成过程。
        </Paragraph>

        <Card size="small" style={{ marginBottom: 16 }}>
          <Space direction="vertical" style={{ width: '100%' }}>
            <Button
              type="primary"
              onClick={handleStreamStart}
              disabled={showStream}
            >
              开始流式输出演示
            </Button>

            {showStream && (
              <StreamOutput
                content={streamContent}
                speed={30}
                onComplete={() => console.log('Stream complete')}
              />
            )}
          </Space>
        </Card>

        <Card title="分段流式输出" size="small">
          <SegmentedStreamOutput
            segments={segments}
            speed={25}
            onComplete={() => console.log('All segments complete')}
          />
        </Card>
      </section>
    </div>
  );
};

export default LoadingStatesDemo;
