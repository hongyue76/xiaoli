import { useState } from 'react';
import { Card, Upload, Button, Steps, Alert, List, Tag, Progress, message, Row, Col, Statistic, Divider, Empty } from 'antd';
import { UploadOutlined, FileTextOutlined, CheckCircleOutlined, WarningOutlined, DownloadOutlined, CloseCircleOutlined } from '@ant-design/icons';
import EmptyState from '@/components/EmptyState';
import type { UploadProps } from 'antd';
import type { CSSProperties } from 'react';

const { Dragger } = Upload;

interface RiskItem {
  type: 'HIGH' | 'MEDIUM' | 'LOW';
  content: string;
  location: string;
  suggestion?: string;
}

interface ReviewResult {
  fileName: string;
  fileSize: number;
  overallRisk: 'HIGH' | 'MEDIUM' | 'LOW';
  risks: RiskItem[];
  suggestions: string[];
  score: number;
}

const ContractPage: React.FC = () => {
  const [currentStep, setCurrentStep] = useState(0);
  const [fileList, setFileList] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [reviewResult, setReviewResult] = useState<ReviewResult | null>(null);

  const uploadProps: UploadProps = {
    name: 'file',
    multiple: false,
    fileList,
    onChange(info) {
      const { fileList } = info;
      setFileList(fileList.slice(-1)); // 只保留最后一个文件
    },
    beforeUpload(file) {
      const isValidType = ['application/pdf', 'application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'].includes(file.type);
      if (!isValidType) {
        message.error('只能上传PDF、Word文档!');
        return false;
      }
      const isLt10M = file.size / 1024 / 1024 < 10;
      if (!isLt10M) {
        message.error('文件大小不能超过10MB!');
        return false;
      }
      return false; // 阻止自动上传
    },
    onRemove() {
      setFileList([]);
      setReviewResult(null);
      setCurrentStep(0);
    },
  };

  const handleReview = async () => {
    if (fileList.length === 0) {
      message.warning('请先上传合同文件');
      return;
    }

    setLoading(true);
    // 模拟AI审查过程
    await new Promise(resolve => setTimeout(resolve, 2000));
    
    // 模拟审查结果
    setReviewResult({
      fileName: fileList[0].name,
      fileSize: fileList[0].size,
      overallRisk: 'MEDIUM',
      score: 72,
      risks: [
        {
          type: 'HIGH',
          content: '合同中缺少明确的争议解决条款，未指定管辖法院或仲裁机构',
          location: '第12条',
          suggestion: '建议增加明确的争议解决条款，约定具体的管辖法院或仲裁机构',
        },
        {
          type: 'HIGH',
          content: '违约责任条款过于笼统，缺乏具体的违约行为界定和赔偿标准',
          location: '第15条',
          suggestion: '建议细化违约责任，明确具体违约情形及相应的赔偿计算方式',
        },
        {
          type: 'MEDIUM',
          content: '合同生效条件表述不够清晰，可能存在歧义',
          location: '第18条',
          suggestion: '建议明确合同生效的具体条件和时间点',
        },
        {
          type: 'MEDIUM',
          content: '保密条款适用范围过宽，可能影响正常业务开展',
          location: '第10条',
          suggestion: '建议明确保密信息的具体范围和例外情形',
        },
        {
          type: 'LOW',
          content: '合同份数及效力条款存在冗余表述',
          location: '第20条',
          suggestion: '建议简化合同份数条款表述',
        },
      ],
      suggestions: [
        '建议完善争议解决机制，明确管辖法院或仲裁机构',
        '建议细化违约责任条款，提高可执行性',
        '建议审查合同主体的资质和履约能力',
        '建议明确合同变更、解除的条件和程序',
        '建议增加通知送达条款，确保各方沟通渠道畅通',
      ],
    });
    
    setLoading(false);
    setCurrentStep(2);
  };

  const handleReset = () => {
    setFileList([]);
    setReviewResult(null);
    setCurrentStep(0);
  };

  const handleDownloadReport = () => {
    message.success('审查报告下载成功');
  };

  const getRiskColor = (type: string) => {
    switch(type) {
      case 'HIGH': return { color: '#ff4d4f', icon: <CloseCircleOutlined />, label: '高风险' };
      case 'MEDIUM': return { color: '#faad14', icon: <WarningOutlined />, label: '中风险' };
      case 'LOW': return { color: '#52c41a', icon: <CheckCircleOutlined />, label: '低风险' };
      default: return { color: '#d9d9d9', icon: null, label: '未知' };
    }
  };

  return (
    <div style={styles.container}>
      <div style={styles.header}>
        <h2><FileTextOutlined /> 智能合同审查</h2>
        <p>上传合同文件，AI自动识别条款风险，提供专业的法律建议</p>
      </div>

      <Card>
        <Steps current={currentStep} style={{ marginBottom: 32 }}>
          <Steps.Step title="上传合同" description="选择需要审查的合同文件" />
          <Steps.Step title="AI审查" description="智能分析合同条款风险" />
          <Steps.Step title="查看报告" description="查看审查结果和建议" />
        </Steps>

        {currentStep === 0 && (
          <div style={styles.uploadArea}>
            <Dragger {...uploadProps} style={{ padding: '40px 24px' }}>
              <p className="ant-upload-drag-icon" style={{ fontSize: 48, color: '#1890ff' }}>
                <UploadOutlined />
              </p>
              <p className="ant-upload-text">点击或拖拽文件到此区域上传</p>
              <p className="ant-upload-hint">
                支持 PDF、Word 格式，文件大小不超过 10MB
              </p>
            </Dragger>

            <div style={{ textAlign: 'center', marginTop: 24 }}>
              <Button 
                type="primary" 
                size="large" 
                icon={<UploadOutlined />}
                onClick={handleReview}
                disabled={fileList.length === 0}
                loading={loading}
              >
                开始审查
              </Button>
            </div>

            <Alert
              message="温馨提示"
              description="AI审查结果仅供参考，不构成正式法律意见。重要合同建议咨询专业律师。"
              type="info"
              showIcon
              style={{ marginTop: 24 }}
            />
          </div>
        )}

        {currentStep === 1 && (
          <div style={styles.loadingArea}>
            <div style={{ textAlign: 'center' }}>
              <Progress type="circle" percent={100} status="active" />
              <h3 style={{ marginTop: 24 }}>正在审查合同...</h3>
              <p style={{ color: '#999' }}>AI正在分析合同条款，识别潜在风险</p>
            </div>
          </div>
        )}

        {currentStep === 2 && !reviewResult && (
          <EmptyState
            illustration="contract"
            title="审查完成"
            description="请重新上传合同进行审查"
            actionText="重新审查"
            onAction={handleReset}
          />
        )}

        {currentStep === 2 && reviewResult && (
          <div>
            <Row gutter={16} style={{ marginBottom: 24 }}>
              <Col span={8}>
                <Statistic
                  title="综合评分"
                  value={reviewResult.score}
                  suffix="分"
                  valueStyle={{ color: reviewResult.overallRisk === 'HIGH' ? '#ff4d4f' : reviewResult.overallRisk === 'MEDIUM' ? '#faad14' : '#52c41a' }}
                />
              </Col>
              <Col span={8}>
                <Statistic
                  title="风险等级"
                  value={reviewResult.overallRisk === 'HIGH' ? '高风险' : reviewResult.overallRisk === 'MEDIUM' ? '中风险' : '低风险'}
                  valueStyle={{ color: reviewResult.overallRisk === 'HIGH' ? '#ff4d4f' : reviewResult.overallRisk === 'MEDIUM' ? '#faad14' : '#52c41a' }}
                />
              </Col>
              <Col span={8}>
                <Statistic
                  title="识别风险"
                  value={reviewResult.risks.length}
                  suffix="项"
                />
              </Col>
            </Row>

            <Alert
              message={reviewResult.overallRisk === 'HIGH' ? '发现高风险问题，建议仔细审查' : reviewResult.overallRisk === 'MEDIUM' ? '发现部分风险问题，建议关注' : '合同整体风险较低'}
              type={reviewResult.overallRisk === 'HIGH' ? 'error' : reviewResult.overallRisk === 'MEDIUM' ? 'warning' : 'success'}
              showIcon
              style={{ marginBottom: 24 }}
            />

            <Divider>风险清单</Divider>

            <List
              dataSource={reviewResult.risks}
              renderItem={(risk, index) => {
                const { color, icon, label } = getRiskColor(risk.type);
                return (
                  <List.Item key={index}>
                    <Card style={{ width: '100%' }}>
                      <List.Item.Meta
                        avatar={
                          <div style={{ color, fontSize: 24 }}>
                            {icon}
                          </div>
                        }
                        title={
                          <div>
                            <Tag color={color} style={{ marginRight: 8 }}>
                              {label}
                            </Tag>
                            <span>{risk.content}</span>
                          </div>
                        }
                        description={
                          <div>
                            <p style={{ marginBottom: 8 }}>📍 位置：{risk.location}</p>
                            {risk.suggestion && (
                              <Alert
                                message="建议"
                                description={risk.suggestion}
                                type="info"
                                showIcon
                                style={{ marginTop: 8 }}
                              />
                            )}
                          </div>
                        }
                      />
                    </Card>
                  </List.Item>
                );
              }}
            />

            <Divider>改进建议</Divider>

            <List
              dataSource={reviewResult.suggestions}
              renderItem={(suggestion, index) => (
                <List.Item key={index}>
                  <CheckCircleOutlined style={{ color: '#52c41a', marginRight: 8 }} />
                  {suggestion}
                </List.Item>
              )}
            />

            <div style={{ textAlign: 'center', marginTop: 32 }}>
              <Button type="primary" icon={<DownloadOutlined />} onClick={handleDownloadReport}>
                下载审查报告
              </Button>
              <Button style={{ marginLeft: 16 }} onClick={handleReset}>
                审查其他合同
              </Button>
            </div>
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
  uploadArea: {
    minHeight: 400,
  },
  loadingArea: {
    minHeight: 400,
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
  },
};

export default ContractPage;
