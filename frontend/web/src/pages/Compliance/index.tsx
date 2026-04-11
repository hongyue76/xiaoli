import { useState, useEffect } from 'react';
import { Card, Button, List, Tag, Alert, Progress, Row, Col, Form, Input, Select, Space, Divider, Statistic, Timeline, Modal, message, Empty, Spin, Descriptions } from 'antd';
import { CheckCircleOutlined, WarningOutlined, CloseCircleOutlined, FileTextOutlined, SafetyOutlined, AuditOutlined, DownloadOutlined, ExclamationCircleOutlined } from '@ant-design/icons';
import type { CSSProperties } from 'react';

const { Option } = Select;
const { TextArea } = Input;

interface ComplianceCheckItem {
  id: string;
  name: string;
  category: string;
  description: string;
  status: 'PASSED' | 'WARNING' | 'FAILED' | 'PENDING';
  riskLevel: 'HIGH' | 'MEDIUM' | 'LOW';
  details: string;
  suggestion: string;
  relatedLaws: string[];
}

interface ComplianceReport {
  company: string;
  checkDate: string;
  overallScore: number;
  overallStatus: 'EXCELLENT' | 'GOOD' | 'WARNING' | 'CRITICAL';
  totalItems: number;
  passedCount: number;
  warningCount: number;
  failedCount: number;
  pendingCount: number;
  categories: {
    name: string;
    score: number;
    items: ComplianceCheckItem[];
  }[];
  recommendations: string[];
  upcomingDeadlines: {
    task: string;
    deadline: string;
    priority: 'HIGH' | 'MEDIUM' | 'LOW';
  }[];
}

const CompliancePage: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [checking, setChecking] = useState(false);
  const [report, setReport] = useState<ComplianceReport | null>(null);
  const [detailModalVisible, setDetailModalVisible] = useState(false);
  const [selectedItem, setSelectedItem] = useState<ComplianceCheckItem | null>(null);
  const [form] = Form.useForm();

  const mockReport: ComplianceReport = {
    company: '示例科技有限公司',
    checkDate: new Date().toISOString().split('T')[0],
    overallScore: 72,
    overallStatus: 'WARNING',
    totalItems: 25,
    passedCount: 15,
    warningCount: 6,
    failedCount: 2,
    pendingCount: 2,
    categories: [
      {
        name: '公司治理',
        score: 85,
        items: [
          {
            id: '1',
            name: '公司章程',
            category: '公司治理',
            description: '公司章程是否符合最新法律法规要求',
            status: 'PASSED',
            riskLevel: 'LOW',
            details: '公司章程内容完整，符合《公司法》相关规定',
            suggestion: '无需改进',
            relatedLaws: ['《中华人民共和国公司法》'],
          },
          {
            id: '2',
            name: '股东会制度',
            category: '公司治理',
            description: '股东会召开程序和决议方式是否规范',
            status: 'WARNING',
            riskLevel: 'MEDIUM',
            details: '部分股东会会议记录不够详细',
            suggestion: '建议完善股东会会议记录，保存完整的决议文件',
            relatedLaws: ['《中华人民共和国公司法》'],
          },
          {
            id: '3',
            name: '董事会运作',
            category: '公司治理',
            description: '董事会决策程序和职责履行情况',
            status: 'PASSED',
            riskLevel: 'LOW',
            details: '董事会运作规范，决策程序合法',
            suggestion: '继续保持现有管理制度',
            relatedLaws: ['《中华人民共和国公司法》'],
          },
        ],
      },
      {
        name: '劳动用工',
        score: 68,
        items: [
          {
            id: '4',
            name: '劳动合同',
            category: '劳动用工',
            description: '劳动合同的签订和内容是否合规',
            status: 'WARNING',
            riskLevel: 'MEDIUM',
            details: '部分员工劳动合同未及时更新',
            suggestion: '建议对劳动合同进行全面审查，确保内容完整、及时签订',
            relatedLaws: ['《中华人民共和国劳动合同法》'],
          },
          {
            id: '5',
            name: '社会保险',
            category: '劳动用工',
            description: '社会保险缴纳是否足额、及时',
            status: 'FAILED',
            riskLevel: 'HIGH',
            details: '存在部分员工社保缴纳基数不足的情况',
            suggestion: '立即补缴社保，确保缴纳基数符合规定',
            relatedLaws: ['《中华人民共和国社会保险法》'],
          },
          {
            id: '6',
            name: '加班管理',
            category: '劳动用工',
            description: '加班工资计算和工时管理是否合规',
            status: 'PASSED',
            riskLevel: 'LOW',
            details: '加班工资计算正确，工时记录完整',
            suggestion: '继续保持规范管理',
            relatedLaws: ['《中华人民共和国劳动法》'],
          },
        ],
      },
      {
        name: '财税合规',
        score: 75,
        items: [
          {
            id: '7',
            name: '税务申报',
            category: '财税合规',
            description: '各项税费申报是否及时、准确',
            status: 'PASSED',
            riskLevel: 'LOW',
            details: '税务申报及时，账务处理规范',
            suggestion: '继续保持现有管理制度',
            relatedLaws: ['《中华人民共和国税收征收管理法》'],
          },
          {
            id: '8',
            name: '发票管理',
            category: '财税合规',
            description: '发票开具和管理是否规范',
            status: 'WARNING',
            riskLevel: 'MEDIUM',
            details: '部分发票备注栏填写不完整',
            suggestion: '加强发票审核，确保信息完整准确',
            relatedLaws: ['《中华人民共和国发票管理办法》'],
          },
        ],
      },
      {
        name: '知识产权',
        score: 80,
        items: [
          {
            id: '9',
            name: '商标管理',
            category: '知识产权',
            description: '商标注册和使用是否规范',
            status: 'PASSED',
            riskLevel: 'LOW',
            details: '商标注册及时，使用规范',
            suggestion: '定期检查商标有效期',
            relatedLaws: ['《中华人民共和国商标法》'],
          },
          {
            id: '10',
            name: '商业秘密保护',
            category: '知识产权',
            description: '商业秘密保护措施是否完善',
            status: 'WARNING',
            riskLevel: 'MEDIUM',
            details: '部分商业秘密保护制度需要更新',
            suggestion: '完善商业秘密保护制度，加强员工保密培训',
            relatedLaws: ['《中华人民共和国反不正当竞争法》'],
          },
        ],
      },
      {
        name: '数据安全',
        score: 65,
        items: [
          {
            id: '11',
            name: '数据收集',
            category: '数据安全',
            description: '个人数据收集是否合法合规',
            status: 'FAILED',
            riskLevel: 'HIGH',
            details: '部分数据收集未充分告知用户',
            suggestion: '更新隐私政策，完善用户告知机制',
            relatedLaws: ['《中华人民共和国个人信息保护法》'],
          },
          {
            id: '12',
            name: '数据存储',
            category: '数据安全',
            description: '数据存储安全措施是否到位',
            status: 'WARNING',
            riskLevel: 'MEDIUM',
            details: '数据备份策略需要优化',
            suggestion: '完善数据备份策略，确保数据安全',
            relatedLaws: ['《中华人民共和国数据安全法》'],
          },
        ],
      },
    ],
    recommendations: [
      '立即补缴不足的社保费用，避免法律风险',
      '完善劳动合同管理制度，确保合同及时签订和更新',
      '更新隐私政策和用户协议，符合个人信息保护法要求',
      '完善商业秘密保护制度，加强员工保密意识培训',
      '优化数据备份策略，确保数据安全存储',
      '加强发票审核管理，确保信息完整准确',
      '定期开展合规培训，提高员工合规意识',
    ],
    upcomingDeadlines: [
      {
        task: '2024年度工商年报公示',
        deadline: '2024-06-30',
        priority: 'HIGH',
      },
      {
        task: '营业执照续期',
        deadline: '2024-12-31',
        priority: 'HIGH',
      },
      {
        task: '税务年度审计',
        deadline: '2024-05-31',
        priority: 'MEDIUM',
      },
      {
        task: '商标续费',
        deadline: '2024-08-15',
        priority: 'MEDIUM',
      },
    ],
  };

  const handleCheck = async () => {
    setLoading(true);
    setChecking(true);

    // 模拟合规检查过程
    await new Promise(resolve => setTimeout(resolve, 2000));

    setReport(mockReport);
    setLoading(false);
    setChecking(false);
    message.success('合规检查完成');
  };

  const handleReset = () => {
    form.resetFields();
    setReport(null);
  };

  const handleViewDetail = (item: ComplianceCheckItem) => {
    setSelectedItem(item);
    setDetailModalVisible(true);
  };

  const handleDownloadReport = () => {
    message.success('合规报告下载成功');
  };

  const getStatusColor = (status: string) => {
    switch(status) {
      case 'PASSED': return 'success';
      case 'WARNING': return 'warning';
      case 'FAILED': return 'error';
      case 'PENDING': return 'default';
      default: return 'default';
    }
  };

  const getStatusText = (status: string) => {
    switch(status) {
      case 'PASSED': return '通过';
      case 'WARNING': return '警告';
      case 'FAILED': return '失败';
      case 'PENDING': return '待检查';
      default: return '未知';
    }
  };

  const getRiskIcon = (level: string) => {
    switch(level) {
      case 'HIGH': return <CloseCircleOutlined style={{ color: '#ff4d4f' }} />;
      case 'MEDIUM': return <WarningOutlined style={{ color: '#faad14' }} />;
      case 'LOW': return <CheckCircleOutlined style={{ color: '#52c41a' }} />;
      default: return <ExclamationCircleOutlined style={{ color: '#999' }} />;
    }
  };

  const getOverallStatusConfig = (status: string) => {
    switch(status) {
      case 'EXCELLENT': return { color: '#52c41a', text: '优秀', icon: <CheckCircleOutlined /> };
      case 'GOOD': return { color: '#1890ff', text: '良好', icon: <CheckCircleOutlined /> };
      case 'WARNING': return { color: '#faad14', text: '警告', icon: <WarningOutlined /> };
      case 'CRITICAL': return { color: '#ff4d4f', text: '严重', icon: <CloseCircleOutlined /> };
      default: return { color: '#d9d9d9', text: '未知', icon: <ExclamationCircleOutlined /> };
    }
  };

  return (
    <div style={styles.container}>
      <div style={styles.header}>
        <h2><SafetyOutlined /> 企业合规检查</h2>
        <p>全面的企业合规AI智能检查系统，覆盖公司治理、劳动用工、财税合规等多个领域</p>
      </div>

      <Card>
        {!report ? (
          <div>
            <Form form={form} layout="vertical" onFinish={handleCheck}>
              <Row gutter={16}>
                <Col span={12}>
                  <Form.Item label="企业名称" name="companyName" rules={[{ required: true }]}>
                    <Input placeholder="请输入企业名称" />
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item label="统一社会信用代码" name="creditCode" rules={[{ required: true }]}>
                    <Input placeholder="请输入统一社会信用代码" />
                  </Form.Item>
                </Col>
              </Row>
              <Row gutter={16}>
                <Col span={12}>
                  <Form.Item label="所属行业" name="industry">
                    <Select placeholder="选择所属行业">
                      <Option value="TECHNOLOGY">科技行业</Option>
                      <Option value="FINANCE">金融行业</Option>
                      <Option value="MANUFACTURING">制造业</Option>
                      <Option value="TRADE">贸易行业</Option>
                      <Option value="SERVICE">服务行业</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item label="企业规模" name="scale">
                    <Select placeholder="选择企业规模">
                      <Option value="SMALL">小型企业</Option>
                      <Option value="MEDIUM">中型企业</Option>
                      <Option value="LARGE">大型企业</Option>
                    </Select>
                  </Form.Item>
                </Col>
              </Row>
              <Form.Item label="检查范围">
                <Select mode="multiple" placeholder="选择检查范围（可多选）">
                  <Option value="GOVERNANCE">公司治理</Option>
                  <Option value="LABOR">劳动用工</Option>
                  <Option value="TAX">财税合规</Option>
                  <Option value="INTELLECTUAL">知识产权</Option>
                  <Option value="DATA">数据安全</Option>
                  <Option value="CONTRACT">合同管理</Option>
                </Select>
              </Form.Item>
              <Form.Item>
                <Space>
                  <Button type="primary" htmlType="submit" loading={loading} icon={<AuditOutlined />}>
                    开始检查
                  </Button>
                  <Button onClick={handleReset}>重置</Button>
                </Space>
              </Form.Item>
            </Form>

            {checking && (
              <div style={styles.checking}>
                <Progress percent={60} status="active" />
                <p style={{ textAlign: 'center', color: '#999', marginTop: 16 }}>
                  正在进行合规检查...
                </p>
                <Timeline
                  items={[
                    { color: 'green', children: '加载企业基本信息' },
                    { color: 'green', children: '检查公司治理合规性' },
                    { color: 'blue', children: '检查劳动用工合规性...' },
                    { color: 'gray', children: '检查财税合规性' },
                    { color: 'gray', children: '检查知识产权合规性' },
                    { color: 'gray', children: '生成合规报告' },
                  ]}
                />
              </div>
            )}
          </div>
        ) : (
          <div>
            <Space style={{ marginBottom: 24 }}>
              <Button onClick={handleReset}>重新检查</Button>
              <Button type="primary" icon={<DownloadOutlined />} onClick={handleDownloadReport}>
                下载报告
              </Button>
            </Space>

            <Alert
              message={
                <Space>
                  {getOverallStatusConfig(report.overallStatus).icon}
                  <span>合规等级：{getOverallStatusConfig(report.overallStatus).text}</span>
                  <span>| 综合评分：{report.overallScore}分</span>
                </Space>
              }
              description={`共检查 ${report.totalItems} 项，通过 ${report.passedCount} 项，警告 ${report.warningCount} 项，失败 ${report.failedCount} 项`}
              type={report.overallStatus === 'EXCELLENT' || report.overallStatus === 'GOOD' ? 'success' : report.overallStatus === 'WARNING' ? 'warning' : 'error'}
              showIcon
              style={{ marginBottom: 24 }}
            />

            <Row gutter={16} style={{ marginBottom: 24 }}>
              <Col span={6}>
                <Card size="small">
                  <Statistic
                    title="通过"
                    value={report.passedCount}
                    valueStyle={{ color: '#52c41a' }}
                    suffix={`/ ${report.totalItems}`}
                  />
                </Card>
              </Col>
              <Col span={6}>
                <Card size="small">
                  <Statistic
                    title="警告"
                    value={report.warningCount}
                    valueStyle={{ color: '#faad14' }}
                    suffix={`/ ${report.totalItems}`}
                  />
                </Card>
              </Col>
              <Col span={6}>
                <Card size="small">
                  <Statistic
                    title="失败"
                    value={report.failedCount}
                    valueStyle={{ color: '#ff4d4f' }}
                    suffix={`/ ${report.totalItems}`}
                  />
                </Card>
              </Col>
              <Col span={6}>
                <Card size="small">
                  <Statistic
                    title="综合评分"
                    value={report.overallScore}
                    suffix="分"
                    valueStyle={{ 
                      color: report.overallScore >= 80 ? '#52c41a' : report.overallScore >= 60 ? '#faad14' : '#ff4d4f' 
                    }}
                  />
                </Card>
              </Col>
            </Row>

            <Row gutter={16} style={{ marginBottom: 24 }}>
              {report.categories.map((category, index) => (
                <Col span={12} key={index}>
                  <Card
                    title={
                      <Space>
                        <FileTextOutlined />
                        {category.name}
                      </Space>
                    }
                    size="small"
                  >
                    <Progress
                      percent={category.score}
                      status={category.score >= 80 ? 'success' : category.score >= 60 ? 'normal' : 'exception'}
                    />
                    <div style={{ marginTop: 12 }}>
                      {category.items.map((item) => (
                        <div
                          key={item.id}
                          style={{
                            padding: '8px 12px',
                            background: '#f5f5f5',
                            borderRadius: 4,
                            marginBottom: 8,
                            cursor: 'pointer',
                          }}
                          onClick={() => handleViewDetail(item)}
                        >
                          <Space>
                            {getRiskIcon(item.riskLevel)}
                            <span>{item.name}</span>
                            <Tag color={getStatusColor(item.status)}>{getStatusText(item.status)}</Tag>
                          </Space>
                        </div>
                      ))}
                    </div>
                  </Card>
                </Col>
              ))}
            </Row>

            <Card title="改进建议" style={{ marginBottom: 24 }} size="small">
              <List
                dataSource={report.recommendations}
                renderItem={(recommendation, index) => (
                  <List.Item>
                    <CheckCircleOutlined style={{ color: '#52c41a', marginRight: 8 }} />
                    {recommendation}
                  </List.Item>
                )}
              />
            </Card>

            <Card title="重要截止日期" size="small">
              <List
                dataSource={report.upcomingDeadlines}
                renderItem={(deadline, index) => (
                  <List.Item>
                    <List.Item.Meta
                      avatar={
                        <Tag color={deadline.priority === 'HIGH' ? 'red' : deadline.priority === 'MEDIUM' ? 'orange' : 'blue'}>
                          {deadline.priority === 'HIGH' ? '紧急' : deadline.priority === 'MEDIUM' ? '重要' : '一般'}
                        </Tag>
                      }
                      title={deadline.task}
                      description={`截止日期：${deadline.deadline}`}
                    />
                  </List.Item>
                )}
              />
            </Card>

            <Alert
              message="温馨提示"
              description="本合规检查结果由AI生成，仅供参考。实际合规管理请咨询专业法律顾问。"
              type="info"
              showIcon
              style={{ marginTop: 24 }}
            />
          </div>
        )}
      </Card>

      {/* 详情弹窗 */}
      <Modal
        title="合规检查详情"
        open={detailModalVisible}
        onCancel={() => setDetailModalVisible(false)}
        footer={[
          <Button key="close" onClick={() => setDetailModalVisible(false)}>
            关闭
          </Button>,
        ]}
        width={800}
      >
        {selectedItem && (
          <div>
            <Space style={{ marginBottom: 16 }}>
              {getRiskIcon(selectedItem.riskLevel)}
              <span style={{ fontSize: 18 }}>{selectedItem.name}</span>
              <Tag color={getStatusColor(selectedItem.status)}>
                {getStatusText(selectedItem.status)}
              </Tag>
              <Tag color={selectedItem.riskLevel === 'HIGH' ? 'red' : selectedItem.riskLevel === 'MEDIUM' ? 'orange' : 'green'}>
                风险等级：{selectedItem.riskLevel === 'HIGH' ? '高' : selectedItem.riskLevel === 'MEDIUM' ? '中' : '低'}
              </Tag>
            </Space>

            <Descriptions column={1}>
              <Descriptions.Item label="检查项目">{selectedItem.description}</Descriptions.Item>
              <Descriptions.Item label="检查结果">
                <Alert
                  message={selectedItem.details}
                  type={selectedItem.status === 'PASSED' ? 'success' : selectedItem.status === 'FAILED' ? 'error' : 'warning'}
                  showIcon
                />
              </Descriptions.Item>
              <Descriptions.Item label="改进建议">
                <Alert message={selectedItem.suggestion} type="info" showIcon />
              </Descriptions.Item>
              <Descriptions.Item label="相关法律依据">
                <Space wrap>
                  {selectedItem.relatedLaws.map((law, index) => (
                    <Tag key={index} color="blue">{law}</Tag>
                  ))}
                </Space>
              </Descriptions.Item>
            </Descriptions>
          </div>
        )}
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
  checking: {
    padding: 32,
    background: '#f5f5f5',
    borderRadius: 4,
  },
};

export default CompliancePage;