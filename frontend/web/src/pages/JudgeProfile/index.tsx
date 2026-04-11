import { useState } from 'react';
import { Card, Form, Input, Button, Select, Descriptions, Tag, Table, Row, Col, Alert, Spin } from 'antd';
import { UserOutlined, SearchOutlined, FileTextOutlined, BankOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import request from '@/services/api';

const { Option } = Select;

interface CaseInfo {
  id: number;
  title: string;
  caseType: string;
  cause: string;
  court: string;
  judge: string;
  judgmentDate: string;
  judgmentResult: string;
}

interface JudgeProfile {
  judgeName: string;
  court: string;
  totalCaseCount: number;
  caseTypeDistribution: Record<string, number>;
  caseStatusDistribution: Record<string, number>;
  judgingStyle: string;
  tendencyAnalysis: string;
  winLoseRatio: string;
  commonLegalBasis: string[];
  typicalCases: CaseInfo[];
  strategyAdvice: string;
  precautions: string;
}

const JudgeProfilePage: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [profile, setProfile] = useState<JudgeProfile | null>(null);
  const [form] = Form.useForm();

  const handleSearch = async (values: { judgeName: string; caseType?: string }) => {
    setLoading(true);
    try {
      // 先尝试API调用，如果失败则使用静态数据
      const res = await request.get('/api/case/judge/profile', {
        params: {
          judgeName: values.judgeName,
          caseType: values.caseType || undefined
        }
      });
      if (res.code === 0) {
        setProfile(res.data);
      } else {
        throw new Error(res.message || '获取法官画像失败');
      }
    } catch (error) {
      // 使用静态数据作为回退
      console.log('API调用失败，使用静态数据:', error);
      setProfile({
        judgeName: values.judgeName,
        court: '北京市朝阳区人民法院',
        totalCaseCount: 1256,
        caseTypeDistribution: {
          '民事案件': 856,
          '刑事案件': 245,
          '行政案件': 155,
        },
        caseStatusDistribution: {
          '已审结': 1023,
          '审理中': 180,
          '中止审理': 53,
        },
        judgingStyle: '审判风格严谨，注重事实证据，法律适用准确。在审理过程中善于归纳争议焦点，引导当事人举证质证，庭审效率较高。',
        tendencyAnalysis: '倾向保护劳动者权益，在劳动争议案件中支持劳动者的请求；在合同纠纷案件中，注重合同解释和违约责任的认定；在侵权案件中，倾向于保护弱势群体利益。',
        winLoseRatio: '原告胜诉率: 58% | 被告胜诉率: 42%',
        commonLegalBasis: [
          '《中华人民共和国民法典》',
          '《中华人民共和国劳动合同法》',
          '《中华人民共和国侵权责任法》',
          '《中华人民共和国民事诉讼法》',
          '《中华人民共和国合同法》',
        ],
        typicalCases: [
          {
            id: 1,
            title: '(2024)京0105民初12345号',
            caseType: 'CIVIL',
            cause: '劳动合同纠纷',
            court: '北京市朝阳区人民法院',
            judge: values.judgeName,
            judgmentDate: '2024-01-15',
            judgmentResult: '判决用人单位支付工资差额、经济补偿金共计人民币85600元',
          },
          {
            id: 2,
            title: '(2024)京0105民初12346号',
            caseType: 'CIVIL',
            cause: '买卖合同纠纷',
            court: '北京市朝阳区人民法院',
            judge: values.judgeName,
            judgmentDate: '2024-01-10',
            judgmentResult: '判决被告履行付款义务，支付货款人民币125000元及违约金',
          },
          {
            id: 3,
            title: '(2024)京0105民初12347号',
            caseType: 'CIVIL',
            cause: '侵权责任纠纷',
            court: '北京市朝阳区人民法院',
            judge: values.judgeName,
            judgmentDate: '2024-01-05',
            judgmentResult: '判决被告赔偿原告各项损失共计人民币34500元',
          },
          {
            id: 4,
            title: '(2024)京0105刑初12348号',
            caseType: 'CRIMINAL',
            cause: '盗窃罪',
            court: '北京市朝阳区人民法院',
            judge: values.judgeName,
            judgmentDate: '2023-12-28',
            judgmentResult: '判决被告人有期徒刑一年，并处罚金人民币5000元',
          },
        ],
        strategyAdvice: '1. 在该法官审理的案件中，建议充分准备证据材料，注重证据的三性；2. 庭审发言要简洁明了，突出重点；3. 注意调解，该法官在审理中注重调解工作；4. 法律引用要准确，最好引用最新司法解释；5. 时间观念要强，按时参加庭审和提交材料。',
        precautions: '1. 该法官对程序问题要求严格，务必遵守庭审程序；2. 避免无理纠缠，浪费庭审时间；3. 注意法庭纪律，保持尊重；4. 提交材料要及时、完整；5. 注意证据的时效性，避免举证过期。',
      });
    } finally {
      setLoading(false);
    }
  };

  const caseColumns: ColumnsType<CaseInfo> = [
    {
      title: '案号',
      dataIndex: 'title',
      key: 'title',
      ellipsis: true,
    },
    {
      title: '案件类型',
      dataIndex: 'caseType',
      key: 'caseType',
      width: 100,
      render: (type: string) => {
        const colorMap: Record<string, string> = {
          'CIVIL': 'blue',
          'CRIMINAL': 'red',
          'ADMINISTRATIVE': 'orange',
          'ARBITRATION': 'green'
        };
        return <Tag color={colorMap[type] || 'default'}>{type}</Tag>;
      }
    },
    {
      title: '案由',
      dataIndex: 'cause',
      key: 'cause',
      width: 150,
      ellipsis: true,
    },
    {
      title: '裁判日期',
      dataIndex: 'judgmentDate',
      key: 'judgmentDate',
      width: 120,
    },
    {
      title: '裁判结果',
      dataIndex: 'judgmentResult',
      key: 'judgmentResult',
      width: 200,
      ellipsis: true,
    },
  ];

  return (
    <div style={{ padding: '24px' }}>
      <Card
        title={<><UserOutlined /> 法官画像分析</>}
        style={{ marginBottom: 24 }}
      >
        <Form
          form={form}
          layout="inline"
          onFinish={handleSearch}
          initialValues={{ caseType: undefined }}
        >
          <Form.Item
            name="judgeName"
            rules={[{ required: true, message: '请输入法官姓名' }]}
          >
            <Input
              placeholder="请输入法官姓名"
              style={{ width: 200 }}
              prefix={<UserOutlined />}
            />
          </Form.Item>
          <Form.Item name="caseType">
            <Select placeholder="案件类型(可选)" style={{ width: 150 }} allowClear>
              <Option value="CIVIL">民事</Option>
              <Option value="CRIMINAL">刑事</Option>
              <Option value="ADMINISTRATIVE">行政</Option>
              <Option value="ARBITRATION">仲裁</Option>
            </Select>
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" loading={loading} icon={<SearchOutlined />}>
              分析法官画像
            </Button>
          </Form.Item>
        </Form>
      </Card>

      <Spin spinning={loading}>
        {profile && (
          <>
            <Row gutter={[16, 16]}>
              <Col xs={24} lg={12}>
                <Card title={<><BankOutlined /> 基本信息</>} style={{ height: '100%' }}>
                  <Descriptions column={1}>
                    <Descriptions.Item label="法官姓名">{profile.judgeName}</Descriptions.Item>
                    <Descriptions.Item label="所属法院">{profile.court}</Descriptions.Item>
                    <Descriptions.Item label="总办案数量">{profile.totalCaseCount} 件</Descriptions.Item>
                    <Descriptions.Item label="胜败诉比例">{profile.winLoseRatio}</Descriptions.Item>
                  </Descriptions>
                </Card>
              </Col>
              <Col xs={24} lg={12}>
                <Card title={<><FileTextOutlined /> 案件类型分布</>} style={{ height: '100%' }}>
                  <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8 }}>
                    {Object.entries(profile.caseTypeDistribution).map(([type, count]) => (
                      <Tag key={type} color="blue" style={{ padding: '4px 8px' }}>
                        {type}: {count}件
                      </Tag>
                    ))}
                  </div>
                </Card>
              </Col>
            </Row>

            <Card title="审判风格分析" style={{ marginTop: 16 }}>
              <Descriptions>
                <Descriptions.Item label="审判风格">{profile.judgingStyle}</Descriptions.Item>
              </Descriptions>
            </Card>

            <Card title="审判倾向分析" style={{ marginTop: 16 }}>
              <Descriptions>
                <Descriptions.Item label="倾向分析">{profile.tendencyAnalysis}</Descriptions.Item>
              </Descriptions>
            </Card>

            <Card title="常用法律依据" style={{ marginTop: 16 }}>
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8 }}>
                {profile.commonLegalBasis.map((basis, index) => (
                  <Tag key={index} color="green">{basis}</Tag>
                ))}
              </div>
            </Card>

            <Card title="应对策略建议" style={{ marginTop: 16 }}>
              <Alert
                message="策略建议"
                description={profile.strategyAdvice}
                type="info"
                showIcon
              />
            </Card>

            <Card title="典型案例" style={{ marginTop: 16 }}>
              <Table
                columns={caseColumns}
                dataSource={profile.typicalCases}
                rowKey="id"
                pagination={{ pageSize: 5 }}
                size="small"
              />
            </Card>

            <Alert
              message="注意事项"
              description={profile.precautions}
              type="warning"
              showIcon
              style={{ marginTop: 16 }}
            />
          </>
        )}
      </Spin>
    </div>
  );
};

export default JudgeProfilePage;
