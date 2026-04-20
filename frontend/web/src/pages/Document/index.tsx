import { useState, useEffect } from 'react';
import { Card, Row, Col, Tag, Button, Modal, Form, Input, Select, Spin, message, Empty } from 'antd';
import { FileTextOutlined, PlusOutlined, EditOutlined, DownloadOutlined } from '@ant-design/icons';
import { documentAPI } from '@/services/api';
import EmptyState from '@/components/EmptyState';
import type { CSSProperties } from 'react';

const { Option } = Select;
const { TextArea } = Input;

interface Template {
  id: number;
  name: string;
  templateType: string;
  caseType: string;
  description: string;
  usageCount: number;
}

export default function DocumentPage() {
  const [templates, setTemplates] = useState<Template[]>([]);
  const [loading, setLoading] = useState(false);
  const [generateModalVisible, setGenerateModalVisible] = useState(false);
  const [selectedTemplate, setSelectedTemplate] = useState<Template | null>(null);
  const [form] = Form.useForm();

  useEffect(() => {
    loadTemplates();
  }, []);

  const loadTemplates = async () => {
    setLoading(true);
    try {
      // 尝试API调用，如果失败则使用模拟数据
      const data = await documentAPI.getAllTemplates();
      setTemplates(data || []);
    } catch (error) {
      console.log('API调用失败，使用静态数据:', error);
      setTemplates([
        {
          id: 1,
          name: '民事起诉状',
          templateType: 'PLAINTIFF',
          caseType: '民事纠纷',
          description: '适用于民事诉讼的起诉状模板，包含原告被告信息、诉讼请求、事实与理由等',
          usageCount: 128,
        },
        {
          id: 2,
          name: '答辩状',
          templateType: 'DEFENSE',
          caseType: '民事纠纷',
          description: '适用于民事案件被告的答辩状模板，支持答辩要点、证据反驳等',
          usageCount: 96,
        },
        {
          id: 3,
          name: '上诉状',
          templateType: 'APPEAL',
          caseType: '上诉案件',
          description: '适用于不服一审判决的上诉状模板',
          usageCount: 64,
        },
      ]);
    } finally {
      setLoading(false);
    }
  };

  const handleGenerate = (template: Template) => {
    setSelectedTemplate(template);
    setGenerateModalVisible(true);
    form.resetFields();
  };

  const handleSubmit = async () => {
    if (!selectedTemplate) return;

    try {
      const values = await form.validateFields();
      const result = await documentAPI.generate({
        templateId: selectedTemplate.id,
        data: values,
        aiAssist: true,
      });
      message.success('文书生成成功');
      setGenerateModalVisible(false);
    } catch (error: any) {
      message.error(error.message || '生成失败');
    }
  };

  const templateTypeColors: Record<string, string> = {
    PLAINTIFF: 'blue',
    DEFENSE: 'green',
    APPEAL: 'orange',
  };

  return (
    <div style={styles.container}>
      <div style={styles.header}>
        <h2>法律文书生成</h2>
        <p>选择模板，填写信息，AI智能生成法律文书</p>
      </div>

      {loading ? (
        <div style={styles.loading}>
          <Spin size="large" />
        </div>
      ) : templates.length === 0 ? (
        <Empty description="暂无模板" image={Empty.PRESENTED_IMAGE_SIMPLE} />
      ) : (
        <Row gutter={[16, 16]}>
          {templates.map((template) => (
            <Col xs={24} sm={12} md={8} lg={6} key={template.id}>
              <Card
                hoverable
                style={styles.card}
                cover={
                  <div style={styles.cardCover}>
                    <FileTextOutlined style={{ fontSize: 48, color: '#1890ff' }} />
                  </div>
                }
                actions={[
                  <Button
                    type="link"
                    icon={<PlusOutlined />}
                    onClick={() => handleGenerate(template)}
                  >
                    生成文书
                  </Button>,
                ]}
              >
                <Card.Meta
                  title={template.name}
                  description={
                    <div>
                      <Tag color={templateTypeColors[template.templateType] || 'default'}>
                        {template.templateType}
                      </Tag>
                      <p style={{ marginTop: 8, color: '#999', fontSize: 12 }}>
                        {template.description}
                      </p>
                      <p style={{ color: '#ccc', fontSize: 12 }}>
                        已使用 {template.usageCount} 次
                      </p>
                    </div>
                  }
                />
              </Card>
            </Col>
          ))}
        </Row>
      )}

      <Modal
        title={`生成文书 - ${selectedTemplate?.name}`}
        open={generateModalVisible}
        onOk={handleSubmit}
        onCancel={() => setGenerateModalVisible(false)}
        width={600}
        okText="生成"
        cancelText="取消"
      >
        <Form form={form} layout="vertical">
          <Form.Item label="文书标题" name="title" rules={[{ required: true, message: '请输入文书标题' }]}>
            <Input placeholder="请输入文书标题" />
          </Form.Item>

          <Form.Item label="原告/申请人" name="plaintiff">
            <Input placeholder="请输入原告/申请人姓名" />
          </Form.Item>

          <Form.Item label="被告/被申请人" name="defendant">
            <Input placeholder="请输入被告/被申请人姓名" />
          </Form.Item>

          <Form.Item label="诉讼请求" name="claims">
            <TextArea rows={3} placeholder="请输入诉讼请求" />
          </Form.Item>

          <Form.Item label="事实与理由" name="facts">
            <TextArea rows={5} placeholder="请输入事实与理由" />
          </Form.Item>

          <Form.Item label="证据清单" name="evidence">
            <TextArea rows={3} placeholder="请输入证据清单" />
          </Form.Item>

          <Form.Item label="管辖法院" name="court">
            <Input placeholder="请输入管辖法院" />
          </Form.Item>

          <Form.Item label="日期" name="date">
            <Input placeholder="请输入日期" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}

const styles: Record<string, CSSProperties> = {
  container: {
    padding: 24,
  },
  header: {
    marginBottom: 24,
  },
  loading: {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    minHeight: 200,
  },
  card: {
    height: '100%',
  },
  cardCover: {
    height: 120,
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    background: '#f5f8fa',
  },
};
