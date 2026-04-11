import { useState, useEffect } from 'react';
import { Card, Upload, Button, List, Tag, Alert, Timeline, Row, Col, Progress, Modal, Form, Input, Select, Space, Divider, Statistic, message, Empty } from 'antd';
import { UploadOutlined, FileTextOutlined, CheckCircleOutlined, WarningOutlined, DeleteOutlined, LinkOutlined, EyeOutlined, SearchOutlined } from '@ant-design/icons';
import type { UploadProps } from 'antd';
import type { CSSProperties } from 'react';

const { Option } = Select;
const { TextArea } = Input;
const { Dragger } = Upload;

interface Evidence {
  id: string;
  name: string;
  type: string;
  source: string;
  date: string;
  description: string;
  reliability: 'HIGH' | 'MEDIUM' | 'LOW';
  relevance: number;
  status: 'PENDING' | 'VERIFIED' | 'REJECTED';
  relatedEvidence: string[];
}

interface EvidenceRelation {
  from: string;
  to: string;
  relationship: string;
  strength: 'STRONG' | 'MEDIUM' | 'WEAK';
}

interface AnalysisResult {
  totalEvidence: number;
  verifiedCount: number;
  rejectedCount: number;
  pendingCount: number;
  averageRelevance: number;
  mainFactChain: string[];
  criticalEvidence: Evidence[];
  contradictions: {
    evidence1: string;
    evidence2: string;
    description: string;
    severity: 'HIGH' | 'MEDIUM' | 'LOW';
  }[];
}

const EvidencePage: React.FC = () => {
  const [evidenceList, setEvidenceList] = useState<Evidence[]>([]);
  const [loading, setLoading] = useState(false);
  const [analyzing, setAnalyzing] = useState(false);
  const [analysisResult, setAnalysisResult] = useState<AnalysisResult | null>(null);
  const [modalVisible, setModalVisible] = useState(false);
  const [relationModalVisible, setRelationModalVisible] = useState(false);
  const [selectedEvidence, setSelectedEvidence] = useState<Evidence | null>(null);
  const [form] = Form.useForm();
  const [relationForm] = Form.useForm();

  const uploadProps: UploadProps = {
    name: 'file',
    multiple: true,
    accept: '.pdf,.jpg,.png,.doc,.docx',
    onChange(info) {
      // 模拟添加证据
      const newEvidence: Evidence = {
        id: Date.now().toString(),
        name: info.file.name,
        type: info.file.type.includes('pdf') ? 'PDF' : info.file.type.includes('image') ? '图片' : '文档',
        source: '上传',
        date: new Date().toISOString().split('T')[0],
        description: '',
        reliability: 'MEDIUM',
        relevance: 75,
        status: 'PENDING',
        relatedEvidence: [],
      };
      setEvidenceList([...evidenceList, newEvidence]);
      message.success('证据添加成功');
    },
    beforeUpload(file) {
      return false;
    },
  };

  const handleAnalyze = async () => {
    if (evidenceList.length === 0) {
      message.warning('请先上传证据');
      return;
    }

    setAnalyzing(true);
    // 模拟AI分析
    await new Promise(resolve => setTimeout(resolve, 2000));

    const result: AnalysisResult = {
      totalEvidence: evidenceList.length,
      verifiedCount: Math.floor(evidenceList.length * 0.6),
      rejectedCount: Math.floor(evidenceList.length * 0.1),
      pendingCount: evidenceList.length - Math.floor(evidenceList.length * 0.7),
      averageRelevance: 78,
      mainFactChain: ['合同签订 → 货物交付 → 验收确认 → 付款延迟 → 违约通知'],
      criticalEvidence: evidenceList.slice(0, 2).map(e => ({ ...e, relevance: 95 })),
      contradictions: evidenceList.length > 2 ? [
        {
          evidence1: evidenceList[0].name,
          evidence2: evidenceList[1].name,
          description: '两份证据对交货时间的表述存在差异',
          severity: 'HIGH',
        },
        {
          evidence1: evidenceList[1].name,
          evidence2: evidenceList[2].name,
          description: '付款金额不一致',
          severity: 'MEDIUM',
        },
      ] : [],
    };

    setAnalysisResult(result);
    setAnalyzing(false);
    message.success('证据分析完成');
  };

  const handleUpdateStatus = (id: string, status: 'VERIFIED' | 'REJECTED' | 'PENDING') => {
    setEvidenceList(evidenceList.map(e => 
      e.id === id ? { ...e, status } : e
    ));
    message.success('状态更新成功');
  };

  const handleViewEvidence = (evidence: Evidence) => {
    setSelectedEvidence(evidence);
    setModalVisible(true);
  };

  const handleDeleteEvidence = (id: string) => {
    Modal.confirm({
      title: '确认删除',
      content: '确定要删除这个证据吗？',
      onOk: () => {
        setEvidenceList(evidenceList.filter(e => e.id !== id));
        message.success('删除成功');
      },
    });
  };

  const handleAddRelation = () => {
    relationForm.validateFields().then(values => {
      setEvidenceList(evidenceList.map(e => 
        e.id === values.from ? 
          { ...e, relatedEvidence: [...e.relatedEvidence, values.to] } : 
          e.id === values.to ?
          { ...e, relatedEvidence: [...e.relatedEvidence, values.from] } :
          e
      ));
      setRelationModalVisible(false);
      relationForm.resetFields();
      message.success('关联关系添加成功');
    });
  };

  const getReliabilityColor = (reliability: string) => {
    switch(reliability) {
      case 'HIGH': return { color: '#52c41a', text: '高' };
      case 'MEDIUM': return { color: '#faad14', text: '中' };
      case 'LOW': return { color: '#ff4d4f', text: '低' };
      default: return { color: '#d9d9d9', text: '未知' };
    }
  };

  const getStatusColor = (status: string) => {
    switch(status) {
      case 'VERIFIED': return 'success';
      case 'REJECTED': return 'error';
      case 'PENDING': return 'default';
      default: return 'default';
    }
  };

  const getStatusText = (status: string) => {
    switch(status) {
      case 'VERIFIED': return '已验证';
      case 'REJECTED': return '已拒绝';
      case 'PENDING': return '待审核';
      default: return '未知';
    }
  };

  return (
    <div style={styles.container}>
      <div style={styles.header}>
        <h2><FileTextOutlined /> 证据分析</h2>
        <p>上传证据材料，AI自动分析证据关联性、可靠性和事实链条</p>
      </div>

      <Card>
        <Row gutter={16}>
          <Col span={18}>
            <Space style={{ marginBottom: 16 }}>
              <Upload {...uploadProps}>
                <Button icon={<UploadOutlined />}>上传证据</Button>
              </Upload>
              <Button 
                type="primary" 
                icon={<SearchOutlined />} 
                onClick={handleAnalyze}
                loading={analyzing}
                disabled={evidenceList.length === 0}
              >
                开始分析
              </Button>
              <Button 
                icon={<LinkOutlined />} 
                onClick={() => setRelationModalVisible(true)}
                disabled={evidenceList.length < 2}
              >
                添加关联
              </Button>
            </Space>
          </Col>
          <Col span={6} style={{ textAlign: 'right' }}>
            <Statistic 
              title="证据总数" 
              value={evidenceList.length} 
              suffix="份"
              valueStyle={{ color: '#1890ff' }}
            />
          </Col>
        </Row>

        <Divider />

        {analyzing && (
          <div style={styles.analyzing}>
            <Progress percent={60} status="active" />
            <p style={{ textAlign: 'center', color: '#999' }}>正在分析证据关联性和可靠性...</p>
          </div>
        )}

        {analysisResult && (
          <Alert
            message="分析完成"
            description={
              <Space>
                <span>已验证 {analysisResult.verifiedCount} 份，</span>
                <span>已拒绝 {analysisResult.rejectedCount} 份，</span>
                <span>待审核 {analysisResult.pendingCount} 份</span>
              </Space>
            }
            type="success"
            showIcon
            style={{ marginBottom: 24 }}
          />
        )}

        <Row gutter={16} style={{ marginBottom: 24 }}>
          <Col span={6}>
            <Card size="small">
              <Statistic
                title="证据总数"
                value={analysisResult?.totalEvidence || evidenceList.length}
                valueStyle={{ color: '#1890ff' }}
              />
            </Card>
          </Col>
          <Col span={6}>
            <Card size="small">
              <Statistic
                title="已验证"
                value={analysisResult?.verifiedCount || 0}
                valueStyle={{ color: '#52c41a' }}
              />
            </Card>
          </Col>
          <Col span={6}>
            <Card size="small">
              <Statistic
                title="已拒绝"
                value={analysisResult?.rejectedCount || 0}
                valueStyle={{ color: '#ff4d4f' }}
              />
            </Card>
          </Col>
          <Col span={6}>
            <Card size="small">
              <Statistic
                title="平均关联度"
                value={analysisResult?.averageRelevance || 0}
                suffix="%"
                valueStyle={{ color: '#faad14' }}
              />
            </Card>
          </Col>
        </Row>

        {analysisResult && analysisResult.mainFactChain.length > 0 && (
          <Card title="事实链条分析" style={{ marginBottom: 24 }} size="small">
            <Timeline
              items={analysisResult.mainFactChain.map((fact, index) => ({
                color: index === 0 ? 'green' : 'blue',
                children: (
                  <div>
                    <p style={{ marginBottom: 4 }}>{fact}</p>
                    {index === 0 && <Tag color="green">起始点</Tag>}
                  </div>
                ),
              }))}
            />
          </Card>
        )}

        {analysisResult && analysisResult.criticalEvidence.length > 0 && (
          <Card title="关键证据" style={{ marginBottom: 24 }} size="small">
            <List
              size="small"
              dataSource={analysisResult.criticalEvidence}
              renderItem={(evidence) => (
                <List.Item>
                  <List.Item.Meta
                    avatar={<CheckCircleOutlined style={{ fontSize: 24, color: '#52c41a' }} />}
                    title={evidence.name}
                    description={
                      <Space>
                        <Tag color="red">关键证据</Tag>
                        <Tag>关联度: {evidence.relevance}%</Tag>
                      </Space>
                    }
                  />
                </List.Item>
              )}
            />
          </Card>
        )}

        {analysisResult && analysisResult.contradictions.length > 0 && (
          <Card title="证据冲突分析" style={{ marginBottom: 24 }} size="small">
            <List
              size="small"
              dataSource={analysisResult.contradictions}
              renderItem={(contradiction) => (
                <List.Item>
                  <List.Item.Meta
                    avatar={<WarningOutlined style={{ fontSize: 24, color: contradiction.severity === 'HIGH' ? '#ff4d4f' : '#faad14' }} />}
                    title={contradiction.description}
                    description={
                      <div>
                        <p style={{ marginBottom: 4 }}>
                          <Tag color="blue">{contradiction.evidence1}</Tag>
                          <WarningOutlined /> <Tag color="blue">{contradiction.evidence2}</Tag>
                        </p>
                        <Tag color={contradiction.severity === 'HIGH' ? 'red' : 'orange'}>
                          严重程度: {contradiction.severity === 'HIGH' ? '高' : '中'}
                        </Tag>
                      </div>
                    }
                  />
                </List.Item>
              )}
            />
          </Card>
        )}

        <Divider />

        <h3>证据列表</h3>
        
        {evidenceList.length === 0 ? (
          <Empty description="暂无证据，请上传证据材料" />
        ) : (
          <List
            dataSource={evidenceList}
            renderItem={(evidence) => {
              const reliability = getReliabilityColor(evidence.reliability);
              return (
                <List.Item
                  actions={[
                    <Button 
                      type="link" 
                      icon={<EyeOutlined />} 
                      onClick={() => handleViewEvidence(evidence)}
                    >
                      查看
                    </Button>,
                    <Button 
                      type="link" 
                      icon={<DeleteOutlined />} 
                      danger
                      onClick={() => handleDeleteEvidence(evidence.id)}
                    >
                      删除
                    </Button>,
                  ]}
                >
                  <List.Item.Meta
                    avatar={<FileTextOutlined style={{ fontSize: 32, color: '#1890ff' }} />}
                    title={
                      <Space>
                        <span>{evidence.name}</span>
                        <Tag>{evidence.type}</Tag>
                        <Tag color={getStatusColor(evidence.status)}>
                          {getStatusText(evidence.status)}
                        </Tag>
                      </Space>
                    }
                    description={
                      <div>
                        <Space>
                          <Tag color={reliability.color}>
                            可靠度: {reliability.text}
                          </Tag>
                          <Tag>关联度: {evidence.relevance}%</Tag>
                          <span style={{ color: '#999' }}>{evidence.date}</span>
                        </Space>
                        {evidence.relatedEvidence.length > 0 && (
                          <div style={{ marginTop: 8 }}>
                            <Tag color="blue">关联证据: {evidence.relatedEvidence.length} 份</Tag>
                          </div>
                        )}
                      </div>
                    }
                  />
                </List.Item>
              );
            }}
          />
        )}
      </Card>

      {/* 证据详情弹窗 */}
      <Modal
        title="证据详情"
        open={modalVisible}
        onCancel={() => setModalVisible(false)}
        footer={[
          <Button key="verify" type="primary" onClick={() => handleUpdateStatus(selectedEvidence?.id || '', 'VERIFIED')}>
            标记已验证
          </Button>,
          <Button key="reject" danger onClick={() => handleUpdateStatus(selectedEvidence?.id || '', 'REJECTED')}>
            标记已拒绝
          </Button>,
          <Button key="close" onClick={() => setModalVisible(false)}>
            关闭
          </Button>,
        ]}
        width={800}
      >
        {selectedEvidence && (
          <Form layout="vertical">
            <Row gutter={16}>
              <Col span={12}>
                <Form.Item label="证据名称">
                  <Input value={selectedEvidence.name} />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item label="证据类型">
                  <Select value={selectedEvidence.type}>
                    <Option value="PDF">PDF文档</Option>
                    <Option value="图片">图片</Option>
                    <Option value="文档">Word文档</Option>
                    <Option value="其他">其他</Option>
                  </Select>
                </Form.Item>
              </Col>
            </Row>
            <Row gutter={16}>
              <Col span={12}>
                <Form.Item label="证据来源">
                  <Input value={selectedEvidence.source} />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item label="日期">
                  <Input value={selectedEvidence.date} />
                </Form.Item>
              </Col>
            </Row>
            <Form.Item label="可靠度">
              <Select value={selectedEvidence.reliability}>
                <Option value="HIGH">高</Option>
                <Option value="MEDIUM">中</Option>
                <Option value="LOW">低</Option>
              </Select>
            </Form.Item>
            <Form.Item label="关联度">
              <Progress percent={selectedEvidence.relevance} />
            </Form.Item>
            <Form.Item label="描述">
              <TextArea rows={4} value={selectedEvidence.description} placeholder="描述证据的内容和目的" />
            </Form.Item>
          </Form>
        )}
      </Modal>

      {/* 添加关联弹窗 */}
      <Modal
        title="添加证据关联"
        open={relationModalVisible}
        onOk={handleAddRelation}
        onCancel={() => {
          setRelationModalVisible(false);
          relationForm.resetFields();
        }}
        width={600}
      >
        <Form form={relationForm} layout="vertical">
          <Form.Item
            name="from"
            label="证据1"
            rules={[{ required: true, message: '请选择证据' }]}
          >
            <Select placeholder="选择第一个证据">
              {evidenceList.map(e => (
                <Option key={e.id} value={e.id}>{e.name}</Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item
            name="to"
            label="证据2"
            rules={[{ required: true, message: '请选择证据' }]}
          >
            <Select placeholder="选择第二个证据">
              {evidenceList.map(e => (
                <Option key={e.id} value={e.id}>{e.name}</Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item name="relationship" label="关联关系">
            <Select placeholder="选择关联类型">
              <Option value="support">支持关系</Option>
              <Option value="contradict">矛盾关系</Option>
              <Option value="complement">互补关系</Option>
              <Option value="causation">因果关系</Option>
            </Select>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

const styles: Record<string, CSSProperties> = {
  container: {
    padding: 24,
  },
  header: {
    marginBottom: 24,
  },
  analyzing: {
    padding: 24,
    background: '#f5f5f5',
    borderRadius: 4,
    marginBottom: 24,
  },
};

export default EvidencePage;