import { useState, useEffect } from 'react';
import { Card, Row, Col, Button, Modal, Form, Input, Select, Spin, message, Empty, Tabs, Divider, List, Tag, Space, Popconfirm, Timeline, Badge, Statistic } from 'antd';
import { FileTextOutlined, PlusOutlined, EditOutlined, DeleteOutlined, SaveOutlined, DownloadOutlined, FileProtectOutlined, HistoryOutlined, SwapOutlined, RollbackOutlined } from '@ant-design/icons';
import { documentAPI } from '@/services/api';
import type { CSSProperties } from 'react';

const { Option } = Select;
const { TextArea } = Input;

interface DefenseDocument {
  id?: number;
  title: string;
  caseName: string;
  caseType: string;
  plaintiff: string;
  defendant: string;
  court: string;
  caseNo: string;
  
  // 答辩要点
  defensePoints: string[];
  
  // 事实与理由
  factsAndReasons: string;
  
  // 证据反驳
  evidenceRefutations: EvidenceRefutation[];
  
  // 法律依据
  legalBasis: LegalBasisItem[];
  
  // 辩论意见
  argumentOpinion: string;
  
  // 结尾
  conclusion: string;
  
  status: 'draft' | 'completed';
  createdAt?: string;
  updatedAt?: string;
}

interface EvidenceRefutation {
  key: string;
  evidenceName: string;
  plaintiffClaim: string;
  refutationContent: string;
}

interface LegalBasisItem {
  key: string;
  lawName: string;
  article: string;
  content: string;
}

interface DocumentRecord {
  id: number;
  title: string;
  caseName: string;
  templateType: string;
  status: string;
  createdAt: string;
}

interface VersionRecord {
  id: number;
  documentId: number;
  version: number;
  description: string;
  changeType: string;
  diffSummary: string;
  createTime: string;
  contentPreview: string;
}

interface DiffLine {
  oldLineNo: number | null;
  newLineNo: number | null;
  oldContent: string | null;
  newContent: string | null;
  type: 'ADD' | 'DELETE' | 'MODIFY' | 'CONTEXT';
}

interface CompareResult {
  documentId: number;
  oldVersion: number;
  newVersion: number;
  diffType: string;
  summary: string;
  diffLines: DiffLine[];
  stats: {
    addedLines: number;
    deletedLines: number;
    modifiedLines: number;
    unchangedLines: number;
  };
}

const caseTypes = [
  { value: 'CONTRACT_DISPUTE', label: '合同纠纷' },
  { value: 'LABOR_DISPUTE', label: '劳动争议' },
  { value: 'TORT', label: '侵权纠纷' },
  { value: 'MARRIAGE', label: '婚姻家庭' },
  { value: 'PROPERTY_DISPUTE', label: '财产纠纷' },
  { value: 'OTHER', label: '其他' },
];

export default function DefensePage() {
  const [documents, setDocuments] = useState<DocumentRecord[]>([]);
  const [loading, setLoading] = useState(false);
  const [editorVisible, setEditorVisible] = useState(false);
  const [currentDocument, setCurrentDocument] = useState<DefenseDocument | null>(null);
  const [activeTab, setActiveTab] = useState('1');
  const [form] = Form.useForm();

  // 版本管理相关状态
  const [versionHistoryVisible, setVersionHistoryVisible] = useState(false);
  const [compareVisible, setCompareVisible] = useState(false);
  const [versionHistory, setVersionHistory] = useState<VersionRecord[]>([]);
  const [selectedVersions, setSelectedVersions] = useState<[number | null, number | null]>([null, null]);
  const [compareResult, setCompareResult] = useState<CompareResult | null>(null);
  const [currentContent, setCurrentContent] = useState('');

  useEffect(() => {
    loadMyDocuments();
  }, []);

  const loadMyDocuments = async () => {
    setLoading(true);
    try {
      const data = await documentAPI.getMyDocuments({ userId: 1, current: 1, size: 20 });
      setDocuments(data?.records || []);
    } catch (error) {
      setDocuments([]);
    } finally {
      setLoading(false);
    }
  };

  const handleNewDocument = () => {
    setCurrentDocument({
      title: '',
      caseName: '',
      caseType: '',
      plaintiff: '',
      defendant: '',
      court: '',
      caseNo: '',
      defensePoints: [],
      factsAndReasons: '',
      evidenceRefutations: [],
      legalBasis: [],
      argumentOpinion: '',
      conclusion: '',
      status: 'draft',
    });
    setCurrentContent('');
    setEditorVisible(true);
    setActiveTab('1');
  };

  const handleEditDocument = (doc: DocumentRecord) => {
    // 模拟加载文档详情
    const docContent = `答辩人：被告B
原告：原告A
案号：(2024)京0105民初12345号

答辩要点：
1. 对合同效力的答辩
2. 对违约责任的答辩

事实与理由：
答辩人认为原告的诉讼请求缺乏事实和法律依据...

证据反驳：
1. 合同原件 - 证明双方存在合同关系 - 对合同真实性无异议，但该合同已解除

法律依据：
《民法典》第五百六十七条 - 合同的权利义务关系终止

辩论意见：
综上所述，请求法院驳回原告的全部诉讼请求。`;

    setCurrentContent(docContent);
    setCurrentDocument({
      id: doc.id,
      title: doc.title,
      caseName: doc.caseName || '',
      caseType: 'CONTRACT_DISPUTE',
      plaintiff: '原告A',
      defendant: '被告B',
      court: '北京市朝阳区人民法院',
      caseNo: '(2024)京0105民初12345号',
      defensePoints: ['对合同效力的答辩', '对违约责任的答辩'],
      factsAndReasons: '答辩人认为原告的诉讼请求缺乏事实和法律依据...',
      evidenceRefutations: [
        {
          key: '1',
          evidenceName: '合同原件',
          plaintiffClaim: '证明双方存在合同关系',
          refutationContent: '对合同真实性无异议，但该合同已解除...',
        },
      ],
      legalBasis: [
        {
          key: '1',
          lawName: '《中华人民共和国民法典》',
          article: '第五百六十七条',
          content: '合同的权利义务关系终止...',
        },
      ],
      argumentOpinion: '综上所述，请求法院驳回原告的全部诉讼请求。',
      conclusion: '此致\n北京市朝阳区人民法院\n\n答辩人（签名）：\n日期：',
      status: 'draft',
    });
    setEditorVisible(true);
    setActiveTab('1');
  };

  const handleSave = async () => {
    try {
      const values = await form.validateFields();
      // 构建完整内容用于版本保存
      const fullContent = buildDocumentContent(values);
      
      // 调用API保存文档（这里简化处理，实际应该调用后端API）
      // await documentAPI.update(currentDocument?.id || 0, fullContent);
      
      // 模拟保存版本
      message.success('答辩书保存成功');
      setEditorVisible(false);
      loadMyDocuments();
    } catch (error) {
      message.error('保存失败');
    }
  };

  const buildDocumentContent = (values: any): string => {
    const { title, caseName, plaintiff, defendant, court, caseNo, factsAndReasons, argumentOpinion, conclusion } = values;
    return `答辩人：${defendant || ''}
原告：${plaintiff || ''}
案号：${caseNo || ''}

案件名称：${caseName || ''}

${currentDocument?.defensePoints?.map((p, i) => `${i + 1}. ${p}`).join('\n') || ''}

事实与理由：
${factsAndReasons || ''}

${currentDocument?.evidenceRefutations?.map((e, i) => 
  `${i + 1}. ${e.evidenceName} - ${e.plaintiffClaim} - ${e.refutationContent}`
).join('\n') || ''}

${currentDocument?.legalBasis?.map((l, i) => 
  `${l.lawName} ${l.article} - ${l.content}`
).join('\n') || ''}

辩论意见：
${argumentOpinion || ''}

结尾：
${conclusion || ''}`;
  };

  const handleExport = async () => {
    if (!currentDocument?.id && !currentDocument?.title) {
      message.warning('请先输入文档信息');
      return;
    }

    try {
      const values = await form.validateFields();
      const fullContent = buildDocumentContent(values);

      // 模拟导出PDF - 在没有后端的情况下生成文本文件
      const blob = new Blob([fullContent], { type: 'text/plain;charset=utf-8' });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `${currentDocument?.title || '答辩书'}.txt`);
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);

      message.success('导出成功（模拟导出为TXT文件）');
    } catch (error: any) {
      message.error(error.message || '导出失败，请稍后重试');
    }
  };

  // 版本管理
  const handleShowVersionHistory = async () => {
    if (!currentDocument?.id) {
      message.warning('请先保存文档');
      return;
    }
    setVersionHistoryVisible(true);
    // 模拟加载版本历史
    setVersionHistory([
      { id: 1, documentId: currentDocument.id, version: 3, description: '当前版本', changeType: 'MANUAL', diffSummary: '+5行-2行', createTime: new Date().toISOString(), contentPreview: '...' },
      { id: 2, documentId: currentDocument.id, version: 2, description: '修改答辩要点', changeType: 'MANUAL', diffSummary: '+3行', createTime: new Date(Date.now() - 3600000).toISOString(), contentPreview: '...' },
      { id: 3, documentId: currentDocument.id, version: 1, description: '初始版本', changeType: 'MANUAL', diffSummary: '初始版本', createTime: new Date(Date.now() - 7200000).toISOString(), contentPreview: '...' },
    ]);
  };

  const handleCompareVersions = () => {
    if (!selectedVersions[0] || !selectedVersions[1]) {
      message.warning('请选择两个版本进行比对');
      return;
    }
    
    // 模拟比对结果
    setCompareResult({
      documentId: currentDocument?.id || 0,
      oldVersion: selectedVersions[0],
      newVersion: selectedVersions[1],
      diffType: 'MODIFIED',
      summary: '新增3行，删除1行，修改2行',
      diffLines: [
        { oldLineNo: 1, newLineNo: 1, oldContent: '答辩人：被告B', newContent: '答辩人：被告B（公司）', type: 'MODIFY' },
        { oldLineNo: 2, newLineNo: 2, oldContent: '原告：原告A', newContent: '原告：原告A', type: 'CONTEXT' },
        { oldLineNo: null, newLineNo: 3, oldContent: null, newContent: '案件名称：合同纠纷案', type: 'ADD' },
        { oldLineNo: 5, newLineNo: 6, oldContent: '1. 对合同效力的答辩', newContent: '1. 对合同效力的答辩', type: 'CONTEXT' },
        { oldLineNo: 6, newLineNo: null, oldContent: '2. 对违约责任的答辩', newContent: null, type: 'DELETE' },
      ],
      stats: { addedLines: 3, deletedLines: 1, modifiedLines: 2, unchangedLines: 10 },
    });
    setCompareVisible(true);
  };

  const handleRollback = (version: number) => {
    Modal.confirm({
      title: '确认回滚',
      content: `确定要回滚到版本${version}吗？当前内容将被保存为新版本。`,
      onOk: () => {
        message.success(`已回滚到版本${version}`);
        setVersionHistoryVisible(false);
      },
    });
  };

  const addDefensePoint = () => {
    if (!currentDocument) return;
    const newDoc = {
      ...currentDocument,
      defensePoints: [...currentDocument.defensePoints, ''],
    };
    setCurrentDocument(newDoc);
  };

  const updateDefensePoint = (index: number, value: string) => {
    if (!currentDocument) return;
    const newPoints = [...currentDocument.defensePoints];
    newPoints[index] = value;
    setCurrentDocument({ ...currentDocument, defensePoints: newPoints });
  };

  const removeDefensePoint = (index: number) => {
    if (!currentDocument) return;
    const newPoints = currentDocument.defensePoints.filter((_, i) => i !== index);
    setCurrentDocument({ ...currentDocument, defensePoints: newPoints });
  };

  const addEvidenceRefutation = () => {
    if (!currentDocument) return;
    const newRefutations = [
      ...currentDocument.evidenceRefutations,
      { key: Date.now().toString(), evidenceName: '', plaintiffClaim: '', refutationContent: '' },
    ];
    setCurrentDocument({ ...currentDocument, evidenceRefutations: newRefutations });
  };

  const updateEvidenceRefutation = (key: string, field: keyof EvidenceRefutation, value: string) => {
    if (!currentDocument) return;
    const newRefutations = currentDocument.evidenceRefutations.map((item) =>
      item.key === key ? { ...item, [field]: value } : item
    );
    setCurrentDocument({ ...currentDocument, evidenceRefutations: newRefutations });
  };

  const removeEvidenceRefutation = (key: string) => {
    if (!currentDocument) return;
    const newRefutations = currentDocument.evidenceRefutations.filter((item) => item.key !== key);
    setCurrentDocument({ ...currentDocument, evidenceRefutations: newRefutations });
  };

  const addLegalBasis = () => {
    if (!currentDocument) return;
    const newLegalBasis = [
      ...currentDocument.legalBasis,
      { key: Date.now().toString(), lawName: '', article: '', content: '' },
    ];
    setCurrentDocument({ ...currentDocument, legalBasis: newLegalBasis });
  };

  const updateLegalBasis = (key: string, field: keyof LegalBasisItem, value: string) => {
    if (!currentDocument) return;
    const newLegalBasis = currentDocument.legalBasis.map((item) =>
      item.key === key ? { ...item, [field]: value } : item
    );
    setCurrentDocument({ ...currentDocument, legalBasis: newLegalBasis });
  };

  const removeLegalBasis = (key: string) => {
    if (!currentDocument) return;
    const newLegalBasis = currentDocument.legalBasis.filter((item) => item.key !== key);
    setCurrentDocument({ ...currentDocument, legalBasis: newLegalBasis });
  };

  const getDiffLineStyle = (type: string): CSSProperties => {
    switch (type) {
      case 'ADD': return { backgroundColor: '#e6ffec', color: '#28a745' };
      case 'DELETE': return { backgroundColor: '#ffebe9', color: '#d73a49' };
      case 'MODIFY': return { backgroundColor: '#fff8c4', color: '#b08800' };
      default: return {};
    }
  };

  return (
    <div style={styles.container}>
      <div style={styles.header}>
        <h2><FileProtectOutlined /> 答辩书编辑</h2>
        <p>专业律师答辩书编辑工具，支持答辩要点、证据反驳、法律依据等功能</p>
        <Space>
          <Button type="primary" icon={<PlusOutlined />} onClick={handleNewDocument}>
            新建答辩书
          </Button>
          {currentDocument?.id && (
            <Button icon={<HistoryOutlined />} onClick={handleShowVersionHistory}>
              版本历史
            </Button>
          )}
        </Space>
      </div>

      {loading ? (
        <div style={styles.loading}>
          <Spin size="large" />
        </div>
      ) : documents.length === 0 ? (
        <Empty description="暂无答辩书，点击上方按钮创建" />
      ) : (
        <List
          grid={{ gutter: 16, xs: 1, sm: 2, md: 3, lg: 4 }}
          dataSource={documents}
          renderItem={(item) => (
            <List.Item>
              <Card
                hoverable
                actions={[
                  <Button type="link" icon={<EditOutlined />} onClick={() => handleEditDocument(item)}>
                    编辑
                  </Button>,
                  <Button type="link" icon={<DownloadOutlined />} onClick={handleExport}>
                    导出
                  </Button>,
                ]}
              >
                <Card.Meta
                  avatar={<FileTextOutlined style={{ fontSize: 32, color: '#52c41a' }} />}
                  title={item.title}
                  description={
                    <div>
                      <Tag color="blue">{item.caseName || '未指定案件'}</Tag>
                      <p style={{ marginTop: 8 }}>创建时间：{item.createdAt}</p>
                    </div>
                  }
                />
              </Card>
            </List.Item>
          )}
        />
      )}

      <Modal
        title={currentDocument?.id ? '编辑答辩书' : '新建答辩书'}
        open={editorVisible}
        onOk={handleSave}
        onCancel={() => setEditorVisible(false)}
        width={900}
        okText="保存"
        cancelText="关闭"
      >
        <Form form={form} layout="vertical" initialValues={currentDocument || {}}>
          <Tabs activeKey={activeTab} onChange={setActiveTab}>
            <Tabs.TabPane tab="案件信息" key="1">
              <Row gutter={16}>
                <Col span={12}>
                  <Form.Item label="答辩书标题" name="title" rules={[{ required: true }]}>
                    <Input placeholder="例如：关于某某案答辩状" />
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item label="案件名称" name="caseName">
                    <Input placeholder="案件名称" />
                  </Form.Item>
                </Col>
              </Row>
              <Row gutter={16}>
                <Col span={8}>
                  <Form.Item label="案件类型" name="caseType">
                    <Select placeholder="选择案件类型">
                      {caseTypes.map((type) => (
                        <Option key={type.value} value={type.value}>
                          {type.label}
                        </Option>
                      ))}
                    </Select>
                  </Form.Item>
                </Col>
                <Col span={8}>
                  <Form.Item label="原告/上诉人" name="plaintiff">
                    <Input placeholder="原告姓名" />
                  </Form.Item>
                </Col>
                <Col span={8}>
                  <Form.Item label="被告/被上诉人" name="defendant">
                    <Input placeholder="被告姓名" />
                  </Form.Item>
                </Col>
              </Row>
              <Row gutter={16}>
                <Col span={12}>
                  <Form.Item label="管辖法院" name="court">
                    <Input placeholder="管辖法院" />
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item label="案号" name="caseNo">
                    <Input placeholder="案号" />
                  </Form.Item>
                </Col>
              </Row>
            </Tabs.TabPane>

            <Tabs.TabPane tab="答辩要点" key="2">
              <Divider>答辩要点</Divider>
              <p style={{ color: '#666', marginBottom: 16 }}>列出主要的答辩观点和理由</p>
              {currentDocument?.defensePoints.map((point, index) => (
                <Space key={index} style={{ display: 'flex', marginBottom: 8 }} align="start">
                  <Tag>{index + 1}</Tag>
                  <Input
                    value={point}
                    onChange={(e) => updateDefensePoint(index, e.target.value)}
                    placeholder="输入答辩要点"
                    style={{ width: 500 }}
                  />
                  <Button type="text" danger icon={<DeleteOutlined />} onClick={() => removeDefensePoint(index)} />
                </Space>
              ))}
              <Button type="dashed" onClick={addDefensePoint} icon={<PlusOutlined />} style={{ width: '100%', marginTop: 8 }}>
                添加答辩要点
              </Button>
            </Tabs.TabPane>

            <Tabs.TabPane tab="事实与理由" key="3">
              <Form.Item label="事实与理由" name="factsAndReasons">
                <TextArea rows={15} placeholder="详细陈述答辩的事实与理由..." />
              </Form.Item>
              <div style={styles.helpText}>
                <h4>写作提示：</h4>
                <ul>
                  <li>针对原告主张逐一进行反驳</li>
                  <li>阐明答辩人认为的事实真相</li>
                  <li>说明答辩的法律依据</li>
                </ul>
              </div>
            </Tabs.TabPane>

            <Tabs.TabPane tab="证据反驳" key="4">
              <Divider>证据反驳</Divider>
              <p style={{ color: '#666', marginBottom: 16 }}>对原告提交的证据进行逐一反驳</p>
              {currentDocument?.evidenceRefutations.map((item) => (
                <Card key={item.key} size="small" style={{ marginBottom: 16 }}>
                  <Row gutter={16}>
                    <Col span={8}>
                      <label>证据名称：</label>
                      <Input
                        value={item.evidenceName}
                        onChange={(e) => updateEvidenceRefutation(item.key, 'evidenceName', e.target.value)}
                        placeholder="原告提交的证据名称"
                      />
                    </Col>
                    <Col span={8}>
                      <label>原告主张：</label>
                      <Input
                        value={item.plaintiffClaim}
                        onChange={(e) => updateEvidenceRefutation(item.key, 'plaintiffClaim', e.target.value)}
                        placeholder="原告想证明的内容"
                      />
                    </Col>
                    <Col span={8}>
                      <label>反驳内容：</label>
                      <Input
                        value={item.refutationContent}
                        onChange={(e) => updateEvidenceRefutation(item.key, 'refutationContent', e.target.value)}
                        placeholder="针对该证据的反驳"
                      />
                      <Button
                        type="link"
                        danger
                        size="small"
                        icon={<DeleteOutlined />}
                        onClick={() => removeEvidenceRefutation(item.key)}
                        style={{ marginTop: 4 }}
                      >
                        删除
                      </Button>
                    </Col>
                  </Row>
                </Card>
              ))}
              <Button type="dashed" onClick={addEvidenceRefutation} icon={<PlusOutlined />} style={{ width: '100%' }}>
                添加证据反驳
              </Button>
            </Tabs.TabPane>

            <Tabs.TabPane tab="法律依据" key="5">
              <Divider>法律依据</Divider>
              <p style={{ color: '#666', marginBottom: 16 }}>列明支持答辩意见的法律条文</p>
              {currentDocument?.legalBasis.map((item) => (
                <Card key={item.key} size="small" style={{ marginBottom: 16 }}>
                  <Row gutter={16}>
                    <Col span={8}>
                      <label>法律名称：</label>
                      <Input
                        value={item.lawName}
                        onChange={(e) => updateLegalBasis(item.key, 'lawName', e.target.value)}
                        placeholder="例如：民法典"
                      />
                    </Col>
                    <Col span={4}>
                      <label>条款：</label>
                      <Input
                        value={item.article}
                        onChange={(e) => updateLegalBasis(item.key, 'article', e.target.value)}
                        placeholder="第几条"
                      />
                    </Col>
                    <Col span={10}>
                      <label>内容：</label>
                      <Input
                        value={item.content}
                        onChange={(e) => updateLegalBasis(item.key, 'content', e.target.value)}
                        placeholder="法条内容"
                      />
                      <Button
                        type="link"
                        danger
                        size="small"
                        icon={<DeleteOutlined />}
                        onClick={() => removeLegalBasis(item.key)}
                        style={{ marginTop: 4 }}
                      >
                        删除
                      </Button>
                    </Col>
                  </Row>
                </Card>
              ))}
              <Button type="dashed" onClick={addLegalBasis} icon={<PlusOutlined />} style={{ width: '100%' }}>
                添加法律依据
              </Button>
            </Tabs.TabPane>

            <Tabs.TabPane tab="辩论意见" key="6">
              <Form.Item label="辩论意见" name="argumentOpinion">
                <TextArea rows={8} placeholder="总结辩论意见，请求法院支持..." />
              </Form.Item>
              <Form.Item label="结尾" name="conclusion">
                <TextArea rows={6} placeholder="此致 xxx人民法院&#10;&#10;答辩人：&#10;日期：" />
              </Form.Item>
            </Tabs.TabPane>
          </Tabs>
        </Form>
      </Modal>

      {/* 版本历史弹窗 */}
      <Modal
        title={<><HistoryOutlined /> 版本历史</>}
        open={versionHistoryVisible}
        onCancel={() => setVersionHistoryVisible(false)}
        width={800}
        footer={[
          <Button key="compare" type="primary" icon={<SwapOutlined />} onClick={handleCompareVersions}>
            比对选中版本
          </Button>,
          <Button key="close" onClick={() => setVersionHistoryVisible(false)}>
            关闭
          </Button>,
        ]}
      >
        <Row gutter={16} style={{ marginBottom: 16 }}>
          <Col span={12}>
            <Select
              style={{ width: '100%' }}
              placeholder="选择旧版本"
              onChange={(v) => setSelectedVersions([v, selectedVersions[1]])}
            >
              {versionHistory.map(v => (
                <Option key={v.version} value={v.version}>
                  V{v.version} - {v.description} ({v.diffSummary})
                </Option>
              ))}
            </Select>
          </Col>
          <Col span={12}>
            <Select
              style={{ width: '100%' }}
              placeholder="选择新版本"
              onChange={(v) => setSelectedVersions([selectedVersions[0], v])}
            >
              {versionHistory.map(v => (
                <Option key={v.version} value={v.version}>
                  V{v.version} - {v.description} ({v.diffSummary})
                </Option>
              ))}
            </Select>
          </Col>
        </Row>
        <Timeline>
          {versionHistory.map((version, index) => (
            <Timeline.Item
              key={version.id}
              color={index === 0 ? 'green' : 'blue'}
            >
              <Card size="small" style={{ marginBottom: 8 }}>
                <Row justify="space-between" align="middle">
                  <Col>
                    <Space>
                      <Badge count={version.version} style={{ backgroundColor: index === 0 ? '#52c41a' : '#1890ff' }} />
                      <strong>{version.description}</strong>
                      <Tag color={version.changeType === 'MANUAL' ? 'blue' : 'orange'}>
                        {version.changeType === 'MANUAL' ? '手动保存' : '自动保存'}
                      </Tag>
                    </Space>
                    <p style={{ margin: '8px 0', color: '#666' }}>
                      {version.diffSummary} | {new Date(version.createTime).toLocaleString()}
                    </p>
                  </Col>
                  <Col>
                    <Button
                      type="link"
                      icon={<RollbackOutlined />}
                      onClick={() => handleRollback(version.version)}
                    >
                      回滚
                    </Button>
                  </Col>
                </Row>
              </Card>
            </Timeline.Item>
          ))}
        </Timeline>
      </Modal>

      {/* 版本比对弹窗 */}
      <Modal
        title={<><SwapOutlined /> 版本比对结果</>}
        open={compareVisible}
        onCancel={() => setCompareVisible(false)}
        width={900}
        footer={[
          <Button key="close" onClick={() => setCompareVisible(false)}>
            关闭
          </Button>,
        ]}
      >
        {compareResult && (
          <>
            <Row gutter={16} style={{ marginBottom: 16 }}>
              <Col span={6}>
                <Statistic
                  title="新增"
                  value={compareResult.stats.addedLines}
                  valueStyle={{ color: '#28a745' }}
                />
              </Col>
              <Col span={6}>
                <Statistic
                  title="删除"
                  value={compareResult.stats.deletedLines}
                  valueStyle={{ color: '#d73a49' }}
                />
              </Col>
              <Col span={6}>
                <Statistic
                  title="修改"
                  value={compareResult.stats.modifiedLines}
                  valueStyle={{ color: '#b08800' }}
                />
              </Col>
              <Col span={6}>
                <Statistic
                  title="未变"
                  value={compareResult.stats.unchangedLines}
                  valueStyle={{ color: '#666' }}
                />
              </Col>
            </Row>
            <Divider>差异详情</Divider>
            <div style={styles.diffContainer}>
              {compareResult.diffLines.map((line, index) => (
                <div key={index} style={{ ...styles.diffLine, ...getDiffLineStyle(line.type) }}>
                  <span style={{ width: 40, display: 'inline-block', textAlign: 'right', marginRight: 8, color: '#999' }}>
                    {line.oldLineNo || ''}
                  </span>
                  <span style={{ width: 40, display: 'inline-block', textAlign: 'right', marginRight: 8, color: '#999' }}>
                    {line.newLineNo || ''}
                  </span>
                  <span style={{ width: 20, display: 'inline-block', fontWeight: 'bold' }}>
                    {line.type === 'ADD' ? '+' : line.type === 'DELETE' ? '-' : line.type === 'MODIFY' ? '~' : ' '}
                  </span>
                  <span>
                    {line.type === 'DELETE' ? line.oldContent : line.newContent}
                  </span>
                </div>
              ))}
            </div>
          </>
        )}
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
  helpText: {
    background: '#f5f8fa',
    padding: 16,
    borderRadius: 4,
    marginTop: 16,
  },
  diffContainer: {
    maxHeight: 500,
    overflow: 'auto',
    border: '1px solid #d9d9d9',
    borderRadius: 4,
    fontFamily: 'monospace',
    fontSize: 12,
  },
  diffLine: {
    padding: '2px 8px',
    borderBottom: '1px solid #f0f0f0',
    whiteSpace: 'pre-wrap',
    wordBreak: 'break-all',
  },
};
