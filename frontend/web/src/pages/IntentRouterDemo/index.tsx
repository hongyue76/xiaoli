import React, { useState } from 'react';
import { Card, Row, Col, Input, Button, Tag, Space, Typography, List, Divider, Alert, Switch } from 'antd';
import { SendOutlined, BulbOutlined, RobotOutlined, SearchOutlined, CheckCircleOutlined, WarningOutlined } from '@ant-design/icons';
import intentRouterService from '@/services/intentRouterService';
import type { Intent, RouteResponse } from '@/services/intentRouterService';
import './IntentRouterDemo.css';

const { Title, Paragraph, Text } = Typography;

export default function IntentRouterDemoPage() {
  const [inputValue, setInputValue] = useState('');
  const [loading, setLoading] = useState(false);
  const [history, setHistory] = useState<{ question: string; response: RouteResponse }[]>([]);
  const [autoTest, setAutoTest] = useState(false);

  const handleSend = async () => {
    if (!inputValue.trim() || loading) return;

    setLoading(true);
    
    try {
      const response = await intentRouterService.routeQuestion(inputValue);
      
      setHistory(prev => [{ question: inputValue, response }, ...prev]);
      setInputValue('');

    } catch (error: any) {
      console.error('请求失败:', error);
    } finally {
      setLoading(false);
    }
  };

  // 预设测试问题
  const testQuestions = [
    {
      type: '专业法律',
      category: 'PROFESSIONAL',
      questions: [
        '劳动合同纠纷怎么处理？',
        '交通事故赔偿标准是什么？',
        '合同违约金怎么计算？',
        '刑事辩护需要注意什么？',
        '公司合规风险有哪些？',
      ],
    },
    {
      type: '通用问题',
      category: 'GENERAL',
      questions: [
        '生命的意义是什么？',
        '什么是人工智能？',
        '如何提高学习效率？',
        '什么是云计算？',
      ],
    },
    {
      type: '闲聊',
      category: 'CHAT',
      questions: [
        '你好',
        '在吗？',
        '很高兴认识你',
        '最近怎么样？',
      ],
    },
    {
      type: '天气/查询',
      category: 'WEATHER',
      questions: [
        '今天天气怎么样？',
        '现在几点了？',
        '今天是几号？',
      ],
    },
    {
      type: '内容总结',
      category: 'SUMMARY',
      questions: [
        '帮我总结这段话',
        '提炼一下这段内容的要点',
        '生成这段文字的摘要',
      ],
    },
  ];

  const runAutoTest = async () => {
    setAutoTest(true);
    setHistory([]);

    for (const category of testQuestions) {
      for (const question of category.questions) {
        const response = await intentRouterService.routeQuestion(question);
        setHistory(prev => [{ question, response }, ...prev]);
        await new Promise(resolve => setTimeout(resolve, 500)); // 延迟避免过快
      }
    }
    setAutoTest(false);
  };

  const getIntentTagColor = (type: string) => {
    const colors: Record<string, string> = {
      PROFESSIONAL: 'red',
      GENERAL: 'blue',
      CHAT: 'green',
      WEATHER: 'orange',
      SUMMARY: 'purple',
    };
    return colors[type] || 'default';
  };

  const getProcessTypeBadge = (processType?: string) => {
    if (!processType) return null;
    
    if (processType === 'PROFESSIONAL_WITH_SEARCH') {
      return <Tag color="blue" icon={<SearchOutlined />}>专业+检索</Tag>;
    } else if (processType === 'GENERAL') {
      return <Tag color="green" icon={<BulbOutlined />}>通用</Tag>;
    } else if (processType === 'ERROR') {
      return <Tag color="red">错误</Tag>;
    }
    return null;
  };

  return (
    <div style={{ padding: 24 }}>
      <Title level={2}>
        <RobotOutlined /> 意图路由演示
      </Title>

      <Paragraph>
        通过 DeepSeek API 智能识别用户意图，自动路由到专业法律处理或通用回答。
      </Paragraph>

      <Row gutter={[16, 16]}>
        {/* 左侧：输入和测试 */}
        <Col span={12}>
          <Card title="测试问题" style={{ height: '100%' }}>
            <Space direction="vertical" style={{ width: '100%' }} size="large">
              <Input.TextArea
                value={inputValue}
                onChange={(e) => setInputValue(e.target.value)}
                placeholder="输入测试问题..."
                autoSize={{ minRows: 3, maxRows: 6 }}
              />
              
              <Button
                type="primary"
                icon={<SendOutlined />}
                onClick={handleSend}
                loading={loading}
                disabled={!inputValue.trim()}
                block
              >
                发送测试
              </Button>

              <Divider />

              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <Text strong>自动批量测试</Text>
                <Switch checked={autoTest} onChange={setAutoTest} />
              </div>

              <Button
                onClick={runAutoTest}
                loading={autoTest}
                disabled={autoTest}
                block
              >
                运行所有预设测试
              </Button>
            </Space>
          </Card>
        </Col>

        {/* 右侧：预设问题 */}
        <Col span={12}>
          <Card title="预设测试问题" style={{ height: '100%', overflow: 'auto' }}>
            <Space direction="vertical" style={{ width: '100%' }} size="middle">
              {testQuestions.map((category, idx) => (
                <div key={idx} style={{ marginBottom: 16 }}>
                  <Text strong style={{ display: 'block', marginBottom: 8 }}>
                    {category.type}
                  </Text>
                  <Space size={[4, 8]} wrap>
                    {category.questions.map((q, qIdx) => (
                      <Button
                        key={qIdx}
                        size="small"
                        onClick={() => {
                          setInputValue(q);
                        }}
                      >
                        {q}
                      </Button>
                    ))}
                  </Space>
                </div>
              ))}
            </Space>
          </Card>
        </Col>
      </Row>

      {/* 历史记录 */}
      {history.length > 0 && (
        <Card
          title={
            <Space>
              <span>测试历史</span>
              <Tag color="blue">{history.length} 条</Tag>
            </Space>
          }
          style={{ marginTop: 24 }}
        >
          <List
            dataSource={history}
            renderItem={(item, idx) => (
              <List.Item
                style={{
                  flexDirection: 'column',
                  alignItems: 'flex-start',
                }}
              >
                <div style={{ width: '100%', marginBottom: 8 }}>
                  <Text strong>问题 {history.length - idx}：</Text>
                  <div style={{ 
                    padding: '8px 12px',
                    background: '#f5f5f5',
                    borderRadius: 4,
                    marginTop: 4,
                  }}>
                    {item.question}
                  </div>
                </div>

                <div style={{ width: '100%', padding: '12px', background: '#f0f5ff', borderRadius: 4 }}>
                  <Space direction="vertical" size="small" style={{ width: '100%' }}>
                    <Space>
                      <Text strong>意图类型：</Text>
                      {item.response.intentType && (
                        <Tag color={getIntentTagColor(item.response.intentType)}>
                          {item.response.intentType}
                        </Tag>
                      )}
                    </Space>

                    <Space>
                      <Text strong>处理方式：</Text>
                      {getProcessTypeBadge(item.response.processType)}
                    </Space>

                    {item.response.duration && (
                      <Text type="secondary">耗时：{item.response.duration}ms</Text>
                    )}

                    {!item.response.success && item.response.errorMessage && (
                      <Alert
                        message={item.response.errorMessage}
                        type="error"
                        size="small"
                      />
                    )}
                  </Space>

                  <Divider style={{ margin: '8px 0' }} />

                  {item.response.answer && (
                    <div style={{ 
                      background: 'white',
                      padding: '12px',
                      borderRadius: 4,
                      borderLeft: '3px solid #1890ff',
                    }}>
                      <Text type="secondary">AI回答：</Text>
                      <Paragraph style={{ marginTop: 8, marginBottom: 0 }}>
                        {item.response.answer.length > 200 
                          ? item.response.answer.substring(0, 200) + '...' 
                          : item.response.answer}
                      </Paragraph>
                    </div>
                  )}

                  {item.response.searchResults && (
                    <Alert
                      message="检索到相关法律信息"
                      description={
                        <details>
                          <summary>查看检索结果</summary>
                          <pre style={{
                            maxHeight: 150,
                            overflow: 'auto',
                            background: '#f5f5f5',
                            padding: 8,
                            marginTop: 8,
                            borderRadius: 4,
                          }}>
                            {JSON.stringify(JSON.parse(item.response.searchResults), null, 2)}
                          </pre>
                        </details>
                      }
                      type="info"
                      showIcon
                    />
                  )}
                </div>
              </List.Item>
            )}
          />
        </Card>
      )}

      {/* 说明卡片 */}
      <Card title="路由规则说明" style={{ marginTop: 24 }}>
        <Space direction="vertical" size="middle" style={{ width: '100%' }}>
          <Alert
            message="专业法律问题"
            description="包含法律关键词且涉及具体场景的问题，会调用得理API检索案例和法规，再生成严谨的专业回答。"
            type="error"
            showIcon
          />

          <Alert
            message="通用问题"
            description="日常闲聊、常识性提问、天气查询等，直接由通用大模型自由回答，无需检索。"
            type="success"
            showIcon
          />

          <Alert
            message="其他场景"
            description="内容总结、天气时间查询等，根据具体意图采用相应处理方式。"
            type="info"
            showIcon
          />
        </Space>
      </Card>
    </div>
  );
}
