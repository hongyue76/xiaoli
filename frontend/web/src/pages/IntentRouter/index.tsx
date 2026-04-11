import { useState } from 'react';
import { Card, Typography, Tabs, Tag, Space, Alert, Button, Row, Col, Statistic } from 'antd';
import {
  ExperimentOutlined,
  ThunderboltOutlined,
  RobotOutlined,
  CheckCircleOutlined,
  RocketOutlined,
  InfoCircleOutlined,
} from '@ant-design/icons';
import IntentRouterChat from '../../components/IntentRouterChat';
import './IntentRouter.css';

const { Title, Paragraph, Text } = Typography;

const IntentRouter: React.FC = () => {
  const [activeTab, setActiveTab] = useState('demo');

  const items = [
    {
      key: 'demo',
      label: '智能对话演示',
      children: <IntentRouterChat />,
    },
    {
      key: 'guide',
      label: '功能说明',
      children: renderGuide(),
    },
    {
      key: 'architecture',
      label: '架构设计',
      children: renderArchitecture(),
    },
  ];

  return (
    <div className="intent-router-page">
      <div className="page-header">
        <Title level={2}>
          <RocketOutlined /> 意图识别 + 双引擎路由
        </Title>
        <Paragraph>
          智能识别用户意图，自动选择最佳AI引擎（规则引擎或AI大模型），
          在响应速度和回答质量之间取得最佳平衡。
        </Paragraph>
      </div>

      <Row gutter={16} style={{ marginBottom: 24 }}>
        <Col span={8}>
          <Card bordered={false} className="stat-card">
            <Statistic
              title="支持意图类型"
              value={8}
              suffix="种"
              prefix={<ExperimentOutlined />}
              valueStyle={{ color: '#3f8600' }}
            />
          </Card>
        </Col>
        <Col span={8}>
          <Card bordered={false} className="stat-card">
            <Statistic
              title="AI引擎类型"
              value={2}
              suffix="种"
              prefix={<RobotOutlined />}
              valueStyle={{ color: '#1890ff' }}
            />
          </Card>
        </Col>
        <Col span={8}>
          <Card bordered={false} className="stat-card">
            <Statistic
              title="平均响应时间"
              value={1.2}
              suffix="秒"
              prefix={<ThunderboltOutlined />}
              valueStyle={{ color: '#cf1322' }}
            />
          </Card>
        </Col>
      </Row>

      <Alert
        message="核心功能"
        description={
          <Space direction="vertical" size="small">
            <div><CheckCircleOutlined /> <strong>意图识别</strong> - 自动识别8种用户意图类型</div>
            <div><CheckCircleOutlined /> <strong>智能路由</strong> - 根据复杂度和置信度选择引擎</div>
            <div><CheckCircleOutlined /> <strong>双引擎架构</strong> - 规则引擎（快）+ LLM引擎（准）</div>
            <div><CheckCircleOutlined /> <strong>透明反馈</strong> - 展示意图识别和引擎选择过程</div>
          </Space>
        }
        type="info"
        showIcon
        style={{ marginBottom: 24 }}
      />

      <Card>
        <Tabs
          activeKey={activeTab}
          onChange={setActiveTab}
          items={items}
        />
      </Card>
    </div>
  );
};

function renderGuide() {
  return (
    <div className="guide-content">
      <Title level={3}>功能说明</Title>

      <Card title="支持的意图类型" style={{ marginBottom: 16 }}>
        <Space wrap>
          <Tag color="blue">法律咨询</Tag>
          <Tag color="green">案例检索</Tag>
          <Tag color="purple">文书生成</Tag>
          <Tag color="orange">合同审查</Tag>
          <Tag color="cyan">案件分析</Tag>
          <Tag color="magenta">证据分析</Tag>
          <Tag color="volcano">司法决策</Tag>
          <Tag color="geekblue">合规检查</Tag>
        </Space>
      </Card>

      <Card title="双引擎对比" style={{ marginBottom: 16 }}>
        <Row gutter={16}>
          <Col span={12}>
            <Card
              size="small"
              title={
                <Space>
                  <ThunderboltOutlined />
                  <Text>规则引擎</Text>
                </Space>
              }
            >
              <Paragraph>
                <Text strong>特点：</Text><br />
                • 响应速度快（~500ms）<br />
                • 成本低<br />
                • 适合结构化任务<br />
                • 支持模板生成
              </Paragraph>
              <Paragraph>
                <Text strong>适用场景：</Text><br />
                • 案例检索<br />
                • 文书生成<br />
                • 简单查询
              </Paragraph>
            </Card>
          </Col>
          <Col span={12}>
            <Card
              size="small"
              title={
                <Space>
                  <ExperimentOutlined />
                  <Text>AI大模型</Text>
                </Space>
              }
            >
              <Paragraph>
                <Text strong>特点：</Text><br />
                • 理解能力强<br />
                • 生成质量高<br />
                • 支持复杂推理<br />
                • 个性化回答
              </Paragraph>
              <Paragraph>
                <Text strong>适用场景：</Text><br />
                • 法律咨询<br />
                • 合同审查<br />
                • 案件分析
              </Paragraph>
            </Card>
          </Col>
        </Row>
      </Card>

      <Card title="路由策略">
        <Paragraph>
          系统会根据以下因素智能选择引擎：
        </Paragraph>
        <ol>
          <li><strong>意图类型</strong> - 不同意图有默认引擎</li>
          <li><strong>查询复杂度</strong> - 评估查询复杂程度</li>
          <li><strong>置信度</strong> - 低置信度使用LLM提高准确率</li>
          <li><strong>成本考虑</strong> - 平衡速度和质量</li>
        </ol>
      </Card>
    </div>
  );
}

function renderArchitecture() {
  return (
    <div className="architecture-content">
      <Title level={3}>架构设计</Title>

      <Card title="系统架构" style={{ marginBottom: 16 }}>
        <pre style={{ background: '#f5f5f5', padding: 16, borderRadius: 8 }}>
{`用户查询
    ↓
意图识别服务 (IntentRecognitionService)
    ↓
    ├─ 识别意图类型
    ├─ 计算置信度
    └─ 提取关键词
    ↓
引擎路由服务 (EngineRouterService)
    ↓
    ├─ 评估查询复杂度
    ├─ 计算切换阈值
    └─ 选择最佳引擎
    ↓
    ├─→ 规则引擎 (RuleEngineProcessor)
    │       ├─ 模板化响应
    │       ├─ 关键词匹配
    │       └─ 快速检索
    │
    └─→ LLM引擎 (LLMEngineProcessor)
            ├─ DeepSeek API
            ├─ 流式生成
            └─ 上下文理解
    ↓
统一响应 (IntentRouterService)`}
        </pre>
      </Card>

      <Card title="核心模块" style={{ marginBottom: 16 }}>
        <Row gutter={16}>
          <Col span={8}>
            <Card size="small" title="意图识别层">
              <Paragraph>
                <Text code>IntentRecognitionService</Text>
                <br /><br />
                • 关键词匹配<br />
                • 正则表达式<br />
                • 置信度评分<br />
                • 备选意图
              </Paragraph>
            </Card>
          </Col>
          <Col span={8}>
            <Card size="small" title="引擎路由层">
              <Paragraph>
                <Text code>EngineRouterService</Text>
                <br /><br />
                • 复杂度评估<br />
                • 阈值计算<br />
                • 引擎选择<br />
                • 混合模式
              </Paragraph>
            </Card>
          </Col>
          <Col span={8}>
            <Card size="small" title="响应生成层">
              <Paragraph>
                <Text code>Rule/LLM Processor</Text>
                <br /><br />
                • 模板生成<br />
                • API调用<br />
                • 流式输出<br />
                • 错误降级
              </Paragraph>
            </Card>
          </Col>
        </Row>
      </Card>

      <Card title="技术栈">
        <Row gutter={16}>
          <Col span={12}>
            <Paragraph>
              <Text strong>后端：</Text><br />
              • Spring Boot 3.x<br />
              • Intent Recognition Service<br />
              • Engine Router Service<br />
              • DeepSeek API Integration
            </Paragraph>
          </Col>
          <Col span={12}>
            <Paragraph>
              <Text strong>前端：</Text><br />
              • React 18 + TypeScript<br />
              • Ant Design 5.x<br />
              • 实时意图展示<br />
              • 引擎选择可视化
            </Paragraph>
          </Col>
        </Row>
      </Card>
    </div>
  );
}

export default IntentRouter;
