import { Card, Button, Input, Tag, Space, Typography, Divider, Row, Col, Alert, Tabs, Progress } from 'antd';
import {
  CheckCircleOutlined,
  ExperimentOutlined,
  ThunderboltOutlined,
  StarOutlined,
} from '@ant-design/icons';
import './ThemePreview.css';

const { Title, Paragraph, Text } = Typography;

const ThemePreview: React.FC = () => {
  return (
    <div className="theme-preview-page">
      <div className="preview-header">
        <Title level={2}>🎨 律法先锋主题预览</Title>
        <Paragraph>法律专业配色方案展示</Paragraph>
      </div>

      <Tabs defaultActiveKey="colors" items={[
        {
          key: 'colors',
          label: '配色方案',
          children: renderColorScheme(),
        },
        {
          key: 'components',
          label: '组件样式',
          children: renderComponents(),
        },
        {
          key: 'gradients',
          label: '渐变效果',
          children: renderGradients(),
        },
      ]} />
    </div>
  );
};

function renderColorScheme() {
  return (
    <div className="color-scheme">
      <Row gutter={16}>
        <Col span={24}>
          <Card title="主色调 - 深蓝色系列" style={{ marginBottom: 16 }}>
            <Space wrap>
              <div className="color-swatch">
                <div className="color-box primary" />
                <Text>深蓝 #003a8c</Text>
              </div>
              <div className="color-swatch">
                <div className="color-box primary-light" />
                <Text>浅蓝 #096dd9</Text>
              </div>
              <div className="color-swatch">
                <div className="color-box primary-lighter" />
                <Text>更浅蓝 #40a9ff</Text>
              </div>
              <div className="color-swatch">
                <div className="color-box primary-dark" />
                <Text>更深蓝 #002766</Text>
              </div>
            </Space>
          </Card>
        </Col>

        <Col span={24}>
          <Card title="辅助色 - 金色/橙色" style={{ marginBottom: 16 }}>
            <Space wrap>
              <div className="color-swatch">
                <div className="color-box gold" />
                <Text>金色 #faad14</Text>
              </div>
              <div className="color-swatch">
                <div className="color-box gold-light" />
                <Text>浅金 #ffc53d</Text>
              </div>
              <div className="color-swatch">
                <div className="color-box gold-dark" />
                <Text>深金 #d48806</Text>
              </div>
            </Space>
          </Card>
        </Col>

        <Col span={24}>
          <Card title="功能色">
            <Space wrap>
              <div className="color-swatch">
                <div className="color-box success" />
                <Text>成功 #52c41a</Text>
              </div>
              <div className="color-swatch">
                <div className="color-box warning" />
                <Text>警告 #faad14</Text>
              </div>
              <div className="color-swatch">
                <div className="color-box error" />
                <Text>错误 #f5222d</Text>
              </div>
              <div className="color-swatch">
                <div className="color-box info" />
                <Text>信息 #1890ff</Text>
              </div>
            </Space>
          </Card>
        </Col>
      </Row>
    </div>
  );
}

function renderComponents() {
  return (
    <div className="components-preview">
      <Row gutter={16}>
        <Col span={12}>
          <Card title="按钮样式" style={{ marginBottom: 16 }}>
            <Space direction="vertical" style={{ width: '100%' }}>
              <Button type="primary" size="large" icon={<CheckCircleOutlined />}>
                主按钮（深蓝渐变）
              </Button>
              <Button type="default" size="large">
                默认按钮
              </Button>
              <Button className="btn-gold" size="large" icon={<StarOutlined />}>
                金色强调按钮
              </Button>
            </Space>
          </Card>
        </Col>

        <Col span={12}>
          <Card title="标签样式" style={{ marginBottom: 16 }}>
            <Space wrap>
              <Tag color="blue">规则引擎</Tag>
              <Tag color="purple" icon={<ExperimentOutlined />}>AI大模型</Tag>
              <Tag color="gold" icon={<StarOutlined />}>金色标签</Tag>
              <Tag color="green">成功</Tag>
              <Tag color="orange">警告</Tag>
              <Tag color="red">错误</Tag>
            </Space>
          </Card>
        </Col>

        <Col span={24}>
          <Card title="输入框样式" style={{ marginBottom: 16 }}>
            <Space direction="vertical" style={{ width: '100%' }}>
              <Input size="large" placeholder="默认输入框" />
              <Input size="large" placeholder="带图标的输入框" prefix={<SearchOutlined />} />
              <Input.TextArea rows={4} placeholder="文本域" />
            </Space>
          </Card>
        </Col>

        <Col span={24}>
          <Card title="警告框样式">
            <Space direction="vertical" style={{ width: '100%' }}>
              <Alert
                message="信息提示"
                description="这是一个信息提示框"
                type="info"
                showIcon
              />
              <Alert
                message="警告提示"
                description="这是一个警告提示框"
                type="warning"
                showIcon
              />
              <Alert
                message="成功提示"
                description="这是一个成功提示框"
                type="success"
                showIcon
              />
            </Space>
          </Card>
        </Col>
      </Row>
    </div>
  );
}

function renderGradients() {
  return (
    <div className="gradients-preview">
      <Row gutter={16}>
        <Col span={12}>
          <Card title="主色渐变" style={{ marginBottom: 16 }}>
            <div className="gradient-card primary-gradient">
              <Title level={4} style={{ color: '#fff' }}>主色渐变</Title>
              <Paragraph style={{ color: 'rgba(255,255,255,0.9)' }}>
                linear-gradient(135deg, #003a8c 0%, #096dd9 100%)
              </Paragraph>
            </div>
          </Card>
        </Col>

        <Col span={12}>
          <Card title="金色渐变" style={{ marginBottom: 16 }}>
            <div className="gradient-card gold-gradient">
              <Title level={4} style={{ color: '#fff' }}>金色渐变</Title>
              <Paragraph style={{ color: 'rgba(255,255,255,0.9)' }}>
                linear-gradient(135deg, #faad14 0%, #ffc53d 100%)
              </Paragraph>
            </div>
          </Card>
        </Col>

        <Col span={12}>
          <Card title="蓝金渐变" style={{ marginBottom: 16 }}>
            <div className="gradient-card blue-gold-gradient">
              <Title level={4} style={{ color: '#fff' }}>蓝金渐变</Title>
              <Paragraph style={{ color: 'rgba(255,255,255,0.9)' }}>
                linear-gradient(135deg, #003a8c 0%, #faad14 100%)
              </Paragraph>
            </div>
          </Card>
        </Col>

        <Col span={12}>
          <Card title="微妙渐变">
            <div className="gradient-card subtle-gradient">
              <Title level={4}>微妙渐变</Title>
              <Paragraph>
                linear-gradient(135deg, #f5f7fa 0%, #ffffff 100%)
              </Paragraph>
            </div>
          </Card>
        </Col>
      </Row>
    </div>
  );
}

export default ThemePreview;
