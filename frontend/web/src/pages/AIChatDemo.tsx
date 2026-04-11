import React from 'react';
import { Card, Typography, Divider, Space, Tag } from 'antd';
import AIChatEnhanced from '../components/AIChatEnhanced';
import './AIChatDemo.css';

const { Title, Paragraph, Text } = Typography;

const AIChatDemo: React.FC = () => {
  return (
    <div className="ai-chat-demo">
      <div className="demo-header">
        <Title level={2}>AI 对话体验优化演示</Title>
        <Paragraph type="secondary">
          展示优化后的 AI 对话功能，包括对话气泡、快捷操作、富文本展示和语音交互
        </Paragraph>
      </div>

      <div className="demo-features">
        <Space size="large" wrap>
          <Tag color="blue" className="feature-tag">对话气泡</Tag>
          <Tag color="green" className="feature-tag">快捷操作</Tag>
          <Tag color="orange" className="feature-tag">富文本展示</Tag>
          <Tag color="purple" className="feature-tag">语音交互</Tag>
          <Tag color="cyan" className="feature-tag">Markdown 渲染</Tag>
          <Tag color="red" className="feature-tag">法条高亮</Tag>
          <Tag color="gold" className="feature-tag">历史记录</Tag>
        </Space>
      </div>

      <Divider />

      <div className="demo-content">
        <Card
          title="AI 法律助手对话"
          bordered={false}
          className="chat-card"
        >
          <div className="chat-container">
            <AIChatEnhanced />
          </div>
        </Card>
      </div>

      <Divider />

      <div className="demo-description">
        <Title level={4}>功能说明</Title>
        <div className="feature-list">
          <Card size="small" className="feature-card">
            <Title level={5}>对话气泡</Title>
            <Paragraph>
              • 用户消息右对齐（蓝色），AI 消息左对齐（白色）
              <br />
              • 添加头像和身份标识
              <br />
              • 打字动画效果
            </Paragraph>
          </Card>

          <Card size="small" className="feature-card">
            <Title level={5}>快捷操作</Title>
            <Paragraph>
              • 常用问题推荐（支持分类和收藏）
              <br />
              • "追问"按钮（基于上一条回答）
              <br />
              • "复制答案"、"点赞/点踩"
            </Paragraph>
          </Card>

          <Card size="small" className="feature-card">
            <Title level={5}>富文本展示</Title>
            <Paragraph>
              • 法条引用高亮（红色背景）
              <br />
              • 结构化内容（列表、表格）
              <br />
              • Markdown 完整渲染（标题、代码块、引用等）
            </Paragraph>
          </Card>

          <Card size="small" className="feature-card">
            <Title level={5}>语音交互</Title>
            <Paragraph>
              • 语音输入按钮（支持实时声纹可视化）
              <br />
              • TTS 朗读答案（支持暂停/继续）
              <br />
              • 音量调节控制
            </Paragraph>
          </Card>

          <Card size="small" className="feature-card">
            <Title level={5}>历史记录</Title>
            <Paragraph>
              • 自动保存对话历史（localStorage）
              <br />
              • 支持搜索、收藏、删除
              <br />
              • 导出/导入历史记录（JSON 格式）
            </Paragraph>
          </Card>
        </div>
      </div>
    </div>
  );
};

export default AIChatDemo;
