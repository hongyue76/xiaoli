import { useState } from 'react';
import { Card, Steps, Timeline, Descriptions, Statistic, Row, Col, Tag, Alert, Progress, Button, message, Spin, DatePicker, Select, Space, Form, Input } from 'antd';
import { FileTextOutlined, CheckCircleOutlined, WarningOutlined, CloseCircleOutlined, ExclamationCircleOutlined, ArrowRightOutlined } from '@ant-design/icons';
import type { CSSProperties } from 'react';
import dayjs from 'dayjs';

const { Option } = Select;
const { RangePicker } = DatePicker;
const { TextArea } = Input;

interface CaseRisk {
  type: 'HIGH' | 'MEDIUM' | 'LOW';
  title: string;
  description: string;
  evidence?: string;
  suggestion: string;
}

interface TimelineNode {
  title: string;
  date: string;
  status: 'pending' | 'processing' | 'finished';
  details: string[];
}

interface CaseAnalysis {
  basicInfo: {
    caseName: string;
    caseType: string;
    caseNo: string;
    court: string;
    plaintiff: string;
    defendant: string;
    filingDate: string;
    estimatedDuration: number;
  };
  riskAssessment: {
    overallRisk: 'HIGH' | 'MEDIUM' | 'LOW';
    riskScore: number;
    risks: CaseRisk[];
  };
  proceduralFlow: TimelineNode[];
  strategicAdvice: {
    plaintiffAdvice: string[];
    defendantAdvice: string[];
    commonAdvice: string[];
  };
  costEstimate: {
    attorneyFees: number;
    litigationCosts: number;
    otherCosts: number;
    total: number;
  };
}

const AnalysisPage: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [currentStep, setCurrentStep] = useState(0);
  const [analysis, setAnalysis] = useState<CaseAnalysis | null>(null);
  const [form] = Form.useForm();

  const handleAnalyze = async (values: any) => {
    setLoading(true);
    setCurrentStep(1);

    // 模拟AI分析过程
    setTimeout(() => {
      const mockAnalysis: CaseAnalysis = {
        basicInfo: {
          caseName: '买卖合同纠纷',
          caseType: '民事诉讼',
          caseNo: '未分配',
          court: '待选择',
          plaintiff: values.plaintiff || '原告A',
          defendant: values.defendant || '被告B',
          filingDate: values.dateRange?.[0]?.format('YYYY-MM-DD') || '2024-01-15',
          estimatedDuration: 6,
        },
        riskAssessment: {
          overallRisk: 'MEDIUM',
          riskScore: 72,
          risks: [
            {
              type: 'HIGH',
              title: '合同效力风险',
              description: '合同可能存在无效或可撤销的情形，如意思表示不真实、违反法律强制性规定等。',
              evidence: '合同第5条约定不明确，可能构成重大误解',
              suggestion: '建议重新审查合同条款，确认双方真实意思表示，必要时进行公证或见证。',
            },
            {
              type: 'MEDIUM',
              title: '管辖权风险',
              description: '本案可能涉及多个有管辖权的法院，选择不当可能导致程序拖延。',
              evidence: '合同履行地、被告所在地不一致',
              suggestion: '建议根据案件具体情况选择最有利的管辖法院，必要时申请管辖权异议。',
            },
            {
              type: 'MEDIUM',
              title: '证据不足风险',
              description: '关键证据可能缺失或难以取得，影响胜诉概率。',
              evidence: '缺乏有效的交货单据和质量检验报告',
              suggestion: '建议立即收集和固定相关证据，必要时申请证据保全。',
            },
            {
              type: 'LOW',
              title: '诉讼时效风险',
              description: '可能超过法律规定的诉讼时效期限。',
              evidence: '最后一次交易发生在2023年6月',
              suggestion: '建议尽快提起诉讼，避免超过3年诉讼时效。',
            },
          ],
        },
        proceduralFlow: [
          {
            title: '立案审查',
            date: '预计1-2天',
            status: 'pending',
            details: ['提交起诉状及证据材料', '法院进行立案审查', '决定是否立案'],
          },
          {
            title: '送达程序',
            date: '预计3-7天',
            status: 'pending',
            details: ['法院向被告送达起诉状', '被告提出答辩状', '确定开庭时间'],
          },
          {
            title: '庭前准备',
            date: '预计7-15天',
            status: 'pending',
            details: ['证据交换', '庭前会议', '确定争议焦点'],
          },
          {
            title: '开庭审理',
            date: '预计30分钟至2小时',
            status: 'pending',
            details: ['法庭调查', '法庭辩论', '最后陈述', '法庭调解'],
          },
          {
            title: '判决与执行',
            date: '预计15-30天',
            status: 'pending',
            details: ['法院作出判决', '判决生效', '申请强制执行'],
          },
        ],
        strategicAdvice: {
          plaintiffAdvice: [
            '重点举证证明合同关系和违约事实',
            '注意收集和保存交易证据，如合同、送货单、发票、付款凭证等',
            '考虑诉前保全措施，防止被告转移财产',
          ],
          defendantAdvice: [
            '审查合同条款的合法性和有效性',
            '对对方主张进行逐一反驳',
            '考虑反诉或提出反请求',
          ],
          commonAdvice: [
            '密切关注诉讼时效，及时主张权利',
            '选择有利的管辖法院和法官',
            '合理运用证据规则，确保证据链完整',
            '做好调解准备，争取庭外和解',
          ],
        },
        costEstimate: {
          attorneyFees: 15000,
          litigationCosts: 8000,
          otherCosts: 5000,
          total: 28000,
        },
      };
      setAnalysis(mockAnalysis);
      setCurrentStep(2);
      setLoading(false);
    }, 2000);
  };

  const handleReset = () => {
    form.resetFields();
    setAnalysis(null);
    setCurrentStep(0);
  };

  const getRiskColor = (type: string) => {
    switch (type) {
      case 'HIGH': return { color: '#ff4d4f', text: '高风险', icon: <CloseCircleOutlined /> };
      case 'MEDIUM': return { color: '#faad14', text: '中风险', icon: <WarningOutlined /> };
      case 'LOW': return { color: '#52c41a', text: '低风险', icon: <CheckCircleOutlined /> };
      default: return { color: '#d9d9d9', text: '未知', icon: <ExclamationCircleOutlined /> };
    }
  };

  return (
    <div style={styles.container}>
      <div style={styles.header}>
        <h2><FileTextOutlined /> 案件分析</h2>
        <p>基于AI的智能案件分析系统，提供风险评估、流程预测和策略建议</p>
      </div>

      <Card style={{ marginBottom: 24 }}>
        <Steps current={currentStep} style={{ marginBottom: 32 }}>
          <Steps.Step title="信息录入" description="填写案件基本信息" />
          <Steps.Step title="AI分析" description="智能分析案件风险" />
          <Steps.Step title="报告生成" description="查看分析报告" />
        </Steps>

        {currentStep === 0 && (
          <Form form={form} layout="vertical" onFinish={handleAnalyze}>
            <Row gutter={16}>
              <Col span={12}>
                <Form.Item label="案件名称" name="caseName" rules={[{ required: true }]}>
                  <Input placeholder="例如：买卖合同纠纷案" />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item label="案件类型" name="caseType" rules={[{ required: true }]}>
                  <Select placeholder="选择案件类型">
                    <Option value="CONTRACT_DISPUTE">合同纠纷</Option>
                    <Option value="TORT_DISPUTE">侵权纠纷</Option>
                    <Option value="LABOR_DISPUTE">劳动争议</Option>
                    <Option value="MARRIAGE_FAMILY">婚姻家庭</Option>
                    <Option value="PROPERTY_DISPUTE">财产纠纷</Option>
                  </Select>
                </Form.Item>
              </Col>
            </Row>
            <Row gutter={16}>
              <Col span={12}>
                <Form.Item label="原告/申请人" name="plaintiff" rules={[{ required: true }]}>
                  <Input placeholder="原告名称" />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item label="被告/被申请人" name="defendant" rules={[{ required: true }]}>
                  <Input placeholder="被告名称" />
                </Form.Item>
              </Col>
            </Row>
            <Form.Item label="案件描述" name="description" rules={[{ required: true }]}>
              <TextArea rows={4} placeholder="简要描述案件经过和争议焦点" />
            </Form.Item>
            <Form.Item label="关键事实" name="keyFacts">
              <TextArea rows={4} placeholder="列出关键事实和时间节点" />
            </Form.Item>
            <Form.Item label="时间范围">
              <RangePicker style={{ width: '100%' }} />
            </Form.Item>
            <Form.Item>
              <Space>
                <Button type="primary" htmlType="submit" loading={loading} icon={<CheckCircleOutlined />}>
                  开始分析
                </Button>
                <Button onClick={handleReset}>重置</Button>
              </Space>
            </Form.Item>
          </Form>
        )}

        {currentStep === 1 && (
          <div style={styles.loadingArea}>
            <Spin size="large" />
            <div style={{ marginTop: 16 }}>
              <h3>AI正在分析案件...</h3>
              <Progress percent={60} status="active" />
              <p style={{ color: '#999' }}>分析合同风险、评估案件胜率、制定诉讼策略</p>
            </div>
          </div>
        )}

        {currentStep === 2 && analysis && (
          <div>
            <Button onClick={handleReset} style={{ marginBottom: 24 }}>
              分析新案件
            </Button>

            <Alert
              message={`综合风险评分：${analysis.riskAssessment.riskScore}分`}
              description={
                <Space>
                  <span>风险等级：</span>
                  <Tag color={getRiskColor(analysis.riskAssessment.overallRisk).color}>
                    {getRiskColor(analysis.riskAssessment.overallRisk).icon}
                    {getRiskColor(analysis.riskAssessment.overallRisk).text}
                  </Tag>
                  <span>预计审理时间：{analysis.basicInfo.estimatedDuration}个月</span>
                </Space>
              }
              type={analysis.riskAssessment.overallRisk === 'HIGH' ? 'error' : analysis.riskAssessment.overallRisk === 'MEDIUM' ? 'warning' : 'success'}
              showIcon
              style={{ marginBottom: 24 }}
            />

            <Row gutter={16}>
              <Col span={8}>
                <Card title="案件基本信息" style={{ height: '100%' }}>
                  <Descriptions column={1} size="small">
                    <Descriptions.Item label="案件名称">{analysis.basicInfo.caseName}</Descriptions.Item>
                    <Descriptions.Item label="案件类型">{analysis.basicInfo.caseType}</Descriptions.Item>
                    <Descriptions.Item label="原告">{analysis.basicInfo.plaintiff}</Descriptions.Item>
                    <Descriptions.Item label="被告">{analysis.basicInfo.defendant}</Descriptions.Item>
                    <Descriptions.Item label="立案日期">{analysis.basicInfo.filingDate}</Descriptions.Item>
                    <Descriptions.Item label="预计时长">{analysis.basicInfo.estimatedDuration}个月</Descriptions.Item>
                  </Descriptions>
                </Card>
              </Col>

              <Col span={8}>
                <Card title="风险分布" style={{ height: '100%' }}>
                  <Row gutter={16}>
                    <Col span={8}>
                      <Statistic
                        title="高风险"
                        value={analysis.riskAssessment.risks.filter(r => r.type === 'HIGH').length}
                        valueStyle={{ color: '#ff4d4f' }}
                      />
                    </Col>
                    <Col span={8}>
                      <Statistic
                        title="中风险"
                        value={analysis.riskAssessment.risks.filter(r => r.type === 'MEDIUM').length}
                        valueStyle={{ color: '#faad14' }}
                      />
                    </Col>
                    <Col span={8}>
                      <Statistic
                        title="低风险"
                        value={analysis.riskAssessment.risks.filter(r => r.type === 'LOW').length}
                        valueStyle={{ color: '#52c41a' }}
                      />
                    </Col>
                  </Row>
                  <Progress
                    percent={analysis.riskAssessment.riskScore}
                    status={analysis.riskAssessment.overallRisk === 'HIGH' ? 'exception' : analysis.riskAssessment.overallRisk === 'MEDIUM' ? 'normal' : 'success'}
                    style={{ marginTop: 16 }}
                  />
                </Card>
              </Col>

              <Col span={8}>
                <Card title="费用估算" style={{ height: '100%' }}>
                  <Descriptions column={1} size="small">
                    <Descriptions.Item label="律师费">
                      <span style={{ color: '#ff4d4f' }}>¥{analysis.costEstimate.attorneyFees.toLocaleString()}</span>
                    </Descriptions.Item>
                    <Descriptions.Item label="诉讼费">
                      ¥{analysis.costEstimate.litigationCosts.toLocaleString()}
                    </Descriptions.Item>
                    <Descriptions.Item label="其他费用">
                      ¥{analysis.costEstimate.otherCosts.toLocaleString()}
                    </Descriptions.Item>
                    <Descriptions.Item label="总计">
                      <strong style={{ fontSize: 16, color: '#ff4d4f' }}>
                        ¥{analysis.costEstimate.total.toLocaleString()}
                      </strong>
                    </Descriptions.Item>
                  </Descriptions>
                </Card>
              </Col>
            </Row>

            <Card title="风险详情分析" style={{ marginTop: 16 }}>
              <List
                dataSource={analysis.riskAssessment.risks}
                renderItem={(risk) => (
                  <List.Item key={risk.title}>
                    <List.Item.Meta
                      title={
                        <Space>
                          {getRiskColor(risk.type).icon}
                          <span style={{ fontSize: 16 }}>{risk.title}</span>
                          <Tag color={getRiskColor(risk.type).color}>
                            {getRiskColor(risk.type).text}
                          </Tag>
                        </Space>
                      }
                      description={
                        <div>
                          <p style={{ color: '#666', marginBottom: 8 }}>{risk.description}</p>
                          {risk.evidence && (
                            <Alert
                              message={`证据：${risk.evidence}`}
                              type="info"
                              style={{ marginBottom: 8 }}
                            />
                          )}
                          <Alert
                            message={`建议：${risk.suggestion}`}
                            type="success"
                          />
                        </div>
                      }
                    />
                  </List.Item>
                )}
              />
            </Card>

            <Card title="案件流程预测" style={{ marginTop: 16 }}>
              <Timeline
                items={analysis.proceduralFlow.map((node, index) => ({
                  color: index === 0 ? 'blue' : 'gray',
                  children: (
                    <div>
                      <p style={{ marginBottom: 8 }}>
                        <strong>{node.title}</strong>
                        <Tag style={{ marginLeft: 8 }}>{node.date}</Tag>
                      </p>
                      <ul style={{ color: '#666', paddingLeft: 20, margin: 0 }}>
                        {node.details.map((detail, i) => (
                          <li key={i}>{detail}</li>
                        ))}
                      </ul>
                    </div>
                  ),
                }))}
              />
            </Card>

            <Row gutter={16} style={{ marginTop: 16 }}>
              <Col span={8}>
                <Card title={<><CheckCircleOutlined style={{ color: '#1890ff' }} /> 原告策略建议</>} size="small">
                  <ul style={{ paddingLeft: 20, color: '#666' }}>
                    {analysis.strategicAdvice.plaintiffAdvice.map((item, index) => (
                      <li key={index}>{item}</li>
                    ))}
                  </ul>
                </Card>
              </Col>
              <Col span={8}>
                <Card title={<><WarningOutlined style={{ color: '#faad14' }} /> 被告策略建议</>} size="small">
                  <ul style={{ paddingLeft: 20, color: '#666' }}>
                    {analysis.strategicAdvice.defendantAdvice.map((item, index) => (
                      <li key={index}>{item}</li>
                    ))}
                  </ul>
                </Card>
              </Col>
              <Col span={8}>
                <Card title={<><ExclamationCircleOutlined style={{ color: '#52c41a' }} /> 通用建议</>} size="small">
                  <ul style={{ paddingLeft: 20, color: '#666' }}>
                    {analysis.strategicAdvice.commonAdvice.map((item, index) => (
                      <li key={index}>{item}</li>
                    ))}
                  </ul>
                </Card>
              </Col>
            </Row>

            <Alert
              message="温馨提示"
              description="本分析结果由AI生成，仅供参考。实际案件处理请咨询专业律师。"
              type="info"
              showIcon
              style={{ marginTop: 24 }}
            />
          </div>
        )}
      </Card>
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
  loadingArea: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'center',
    minHeight: 300,
  },
};

export default AnalysisPage;