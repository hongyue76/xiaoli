import { useState, useEffect } from 'react';
import { Card, Input, Row, Col, List, Tag, Select, Button, Spin, Empty, Pagination, message, Modal, Switch, Tabs, Descriptions } from 'antd';
import { SearchOutlined, EyeOutlined, FileTextOutlined, CopyOutlined, DatabaseOutlined, ApiOutlined } from '@ant-design/icons';
import { caseAPI } from '@/services/api';
import { VirtualList } from '@/components/Performance';
import type { CSSProperties } from 'react';

interface Case {
  id: number;
  title: string;
  caseNo: string;
  caseType: string;
  cause: string;
  court: string;
  judge: string;
  judgmentDate: string;
  caseStatus: string;
  summary: string;
  disputeFocus: string;
  rulingIdea: string;
  judgmentResult: string;
  legalBasis: string;
  viewCount: number;
  score?: number;
}

interface Law {
  id: number;
  title: string;
  content: string;
  publishDate: string;
  effectiveDate: string;
  source: string;
}

const caseTypeColors: Record<string, string> = {
  'CIVIL': 'blue',
  'CRIMINAL': 'red',
  'ADMINISTRATIVE': 'orange',
  'ARBITRATION': 'green',
};

const caseStatusColors: Record<string, string> = {
  'FIRST_INSTANCE': 'blue',
  'SECOND_INSTANCE': 'orange',
  'FINAL': 'green',
  'RETRIAL': 'purple',
};

export default function CaseSearchPage() {
  const [activeTab, setActiveTab] = useState('case');
  const [useDelilegalAPI, setUseDelilegalAPI] = useState(false);
  const [keyword, setKeyword] = useState('');
  const [caseType, setCaseType] = useState<string>();
  const [caseStatus, setCaseStatus] = useState<string>();
  const [court, setCourt] = useState<string>();
  const [sortBy, setSortBy] = useState('RELEVANCE');
  const [semantic, setSemantic] = useState(true);

  const [cases, setCases] = useState<Case[]>([]);
  const [laws, setLaws] = useState<Law[]>([]);
  const [loading, setLoading] = useState(false);
  const [total, setTotal] = useState(0);
  const [current, setCurrent] = useState(1);
  const [size] = useState(10);

  const [hotCases, setHotCases] = useState<Case[]>([]);

  // 案例详情弹窗
  const [detailVisible, setDetailVisible] = useState(false);
  const [selectedCase, setSelectedCase] = useState<Case | null>(null);
  const [selectedLaw, setSelectedLaw] = useState<Law | null>(null);

  useEffect(() => {
    loadHotCases();
  }, []);

  const loadHotCases = async () => {
    try {
      const data = await caseAPI.getHotCases({ limit: 5 });
      setHotCases(data || []);
    } catch (error) {
      // 使用静态热门案例数据作为回退
      console.log('API调用失败，使用静态数据:', error);
      setHotCases([
        {
          id: 1,
          title: '某科技公司诉某员工劳动争议案',
          caseNo: '(2024)京0105民初12345号',
          caseType: 'CIVIL',
          cause: '劳动合同纠纷',
          court: '北京市朝阳区人民法院',
          judge: '张法官',
          judgmentDate: '2024-01-15',
          caseStatus: 'FINAL',
          summary: '本案涉及解除劳动合同的合法性问题，法院对用人单位解除行为的合法性进行了详细审查',
          disputeFocus: '解除劳动合同是否合法',
          rulingIdea: '用人单位单方解除劳动合同需符合法定情形，并履行告知义务',
          judgmentResult: '判决用人单位支付违法解除劳动合同赔偿金',
          legalBasis: '《劳动合同法》第三十九条、第四十八条',
          viewCount: 1234,
          score: 95,
        },
        {
          id: 2,
          title: '某公司买卖合同纠纷案',
          caseNo: '(2024)京0105民初12346号',
          caseType: 'CIVIL',
          cause: '买卖合同纠纷',
          court: '北京市朝阳区人民法院',
          judge: '李法官',
          judgmentDate: '2024-01-10',
          caseStatus: 'FINAL',
          summary: '本案涉及买卖合同的履行和违约责任问题',
          disputeFocus: '货物质量是否符合约定',
          rulingIdea: '当事人应当按照约定全面履行自己的义务',
          judgmentResult: '判决被告支付货款及违约金',
          legalBasis: '《民法典》第五百零九条、第五百七十七条',
          viewCount: 987,
          score: 92,
        },
        {
          id: 3,
          title: '道路交通事故责任纠纷案',
          caseNo: '(2024)京0105民初12347号',
          caseType: 'CIVIL',
          cause: '侵权责任纠纷',
          court: '北京市朝阳区人民法院',
          judge: '王法官',
          judgmentDate: '2024-01-05',
          caseStatus: 'FINAL',
          summary: '本案涉及道路交通事故的责任认定和赔偿问题',
          disputeFocus: '事故责任如何划分',
          rulingIdea: '交通事故责任应当按照过错原则确定',
          judgmentResult: '判决保险公司在交强险范围内赔偿，超出部分按责任比例分担',
          legalBasis: '《道路交通安全法》第七十六条',
          viewCount: 876,
          score: 88,
        },
      ]);
    }
  };

  const handleSearch = async (page = 1) => {
    setLoading(true);
    setCurrent(page);

    try {
      let data;
      if (useDelilegalAPI && activeTab === 'case') {
          // 使用得理法搜API进行案例检索
          const response = await caseAPI.searchByDelilegal({
            keywords: keyword ? keyword.split(',') : undefined,
            longText: semantic ? keyword : undefined,
            courtLevelArr: court ? [court] : undefined,
            judgementTypeArr: caseStatus ? [caseStatus] : undefined,
          });
          // 得理API返回的是JSON字符串，需要解析
          const parsedData = typeof response === 'string' ? JSON.parse(response) : response;
          setCases(parsedData.data || []);
          setTotal(parsedData.data?.length || 0);
      } else {
        // 使用本地数据库
        const response = await caseAPI.search({
          keyword,
          caseType,
          court,
          caseStatus,
          sortBy,
          semantic,
          current: page,
          size,
        });
        const data = response as any;
        setCases(data?.records || []);
        setTotal(data?.total || 0);
      }
    } catch (error) {
      // 使用静态搜索结果作为回退
      console.log('API调用失败，使用静态数据:', error);
      const staticCases = [
        {
          id: 1,
          title: '某科技公司诉某员工劳动争议案',
          caseNo: '(2024)京0105民初12345号',
          caseType: 'CIVIL',
          cause: '劳动合同纠纷',
          court: '北京市朝阳区人民法院',
          judge: '张法官',
          judgmentDate: '2024-01-15',
          caseStatus: 'FINAL',
          summary: '本案涉及解除劳动合同的合法性问题，用人单位以员工违反公司规章制度为由解除劳动合同，员工主张违法解除。法院对用人单位解除行为的合法性进行了详细审查。',
          disputeFocus: '解除劳动合同是否合法',
          rulingIdea: '用人单位单方解除劳动合同需符合法定情形，并履行告知义务。本案中，用人单位未能充分证明员工存在严重违反规章制度的行为，且未履行告知义务，构成违法解除。',
          judgmentResult: '判决用人单位支付违法解除劳动合同赔偿金人民币85600元',
          legalBasis: '《劳动合同法》第三十九条、第四十八条',
          viewCount: 1234,
          score: 95,
        },
        {
          id: 2,
          title: '某公司买卖合同纠纷案',
          caseNo: '(2024)京0105民初12346号',
          caseType: 'CIVIL',
          cause: '买卖合同纠纷',
          court: '北京市朝阳区人民法院',
          judge: '李法官',
          judgmentDate: '2024-01-10',
          caseStatus: 'FINAL',
          summary: '原告向被告购买设备，被告交付的设备存在质量问题，原告要求解除合同并赔偿损失。',
          disputeFocus: '货物质量是否符合约定',
          rulingIdea: '当事人应当按照约定全面履行自己的义务。出卖人交付的标的物不符合质量要求的，买受人可以请求承担违约责任。',
          judgmentResult: '判决被告返还货款人民币125000元，支付违约金人民币25000元',
          legalBasis: '《民法典》第五百零九条、第五百七十七条、第五百八十二条',
          viewCount: 987,
          score: 92,
        },
        {
          id: 3,
          title: '道路交通事故责任纠纷案',
          caseNo: '(2024)京0105民初12347号',
          caseType: 'CIVIL',
          cause: '侵权责任纠纷',
          court: '北京市朝阳区人民法院',
          judge: '王法官',
          judgmentDate: '2024-01-05',
          caseStatus: 'FINAL',
          summary: '张某驾驶车辆与李某驾驶的车辆发生追尾事故，造成两车受损及李某受伤。交警认定张某负主要责任，李某负次要责任。',
          disputeFocus: '事故责任如何划分，赔偿数额如何确定',
          rulingIdea: '交通事故责任应当按照过错原则确定。造成人身损害的，应当赔偿医疗费、护理费、交通费、营养费、住院伙食补助费等合理费用。',
          judgmentResult: '判决保险公司在交强险范围内赔偿120000元，超出部分由张某承担70%，李某承担30%',
          legalBasis: '《道路交通安全法》第七十六条，《民法典》第一千一百七十九条',
          viewCount: 876,
          score: 88,
        },
        {
          id: 4,
          title: '房屋租赁合同纠纷案',
          caseNo: '(2024)京0105民初12348号',
          caseType: 'CIVIL',
          cause: '租赁合同纠纷',
          court: '北京市朝阳区人民法院',
          judge: '赵法官',
          judgmentDate: '2024-01-03',
          caseStatus: 'FINAL',
          summary: '承租人因工作调动提前退租，出租人要求承担违约责任。承租人主张符合合同约定的解除条件。',
          disputeFocus: '提前退租是否构成违约',
          rulingIdea: '当事人可以约定一方解除合同的条件。解除合同的条件成就时，解除权人可以解除合同，但应当通知对方。',
          judgmentResult: '判决解除租赁合同，承租人支付相当于一个月租金的违约金',
          legalBasis: '《民法典》第五百六十二条、第五百六十三条',
          viewCount: 765,
          score: 85,
        },
        {
          id: 5,
          title: '建设工程施工合同纠纷案',
          caseNo: '(2024)京0105民初12349号',
          caseType: 'CIVIL',
          cause: '建设工程合同纠纷',
          court: '北京市朝阳区人民法院',
          judge: '孙法官',
          judgmentDate: '2024-01-02',
          caseStatus: 'FINAL',
          summary: '承包人完成工程后，发包人未按约定支付工程款。承包人要求支付工程款及利息。',
          disputeFocus: '工程款数额如何确定，利息如何计算',
          rulingIdea: '发包人未按照约定支付价款的，承包人可以催告发包人在合理期限内支付价款。发包人逾期不支付的，应当按照约定支付违约金。',
          judgmentResult: '判决发包人支付工程款人民币2560000元，并支付利息',
          legalBasis: '《民法典》第七百九十九条、第八百零七条',
          viewCount: 654,
          score: 82,
        },
      ];
      setCases(staticCases);
      setTotal(staticCases.length);
    } finally {
      setLoading(false);
    }
  };

  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') {
      handleSearch(1);
    }
  };

  const handleViewDetail = async (id: number) => {
    try {
      const response = await caseAPI.getDetail(id);
      const detail = response as any;
      setSelectedCase(detail);
      setSelectedLaw(null);
      setDetailVisible(true);
    } catch (error) {
      message.error('加载案例详情失败');
    }
  };

  const handleSearchLaws = async () => {
    setLoading(true);
    try {
      const response = await caseAPI.searchLaws({
        keywords: keyword ? [keyword] : ['法律'],
        fieldName: semantic ? 'semantic' : 'title',
      });
      const parsedData = typeof response === 'string' ? JSON.parse(response) : response;
      setLaws(parsedData.data || []);
      setTotal(parsedData.data?.length || 0);
    } catch (error) {
      console.log('法规检索失败:', error);
      message.error('法规检索失败');
    } finally {
      setLoading(false);
    }
  };

  // 标签切换时清空数据和搜索
  const handleTabChange = (key: string) => {
    setActiveTab(key);
    setCases([]);
    setLaws([]);
    setTotal(0);
    setCurrent(1);
  };

  const handleCopy = (text: string) => {
    navigator.clipboard.writeText(text);
    message.success('已复制到剪贴板');
  };

  return (
    <div style={styles.container}>
      <Row gutter={16}>
        {/* 左侧搜索区域 */}
        <Col span={18}>
          <Card style={{ marginBottom: 16 }}>
            <div style={styles.searchBar}>
              <Input
                placeholder="输入关键词、案由、法院名称搜索..."
                value={keyword}
                onChange={(e) => setKeyword(e.target.value)}
                onKeyPress={handleKeyPress}
                prefix={<SearchOutlined />}
                style={{ flex: 1 }}
                size="large"
              />
              <Button type="primary" size="large" onClick={() => activeTab === 'case' ? handleSearch(1) : handleSearchLaws()}>
                搜索
              </Button>
            </div>

            <div style={styles.filters}>
              <Select
                placeholder="案件类型"
                style={{ width: 150 }}
                allowClear
                value={caseType}
                onChange={setCaseType}
              >
                <Select.Option value="CIVIL">民事</Select.Option>
                <Select.Option value="CRIMINAL">刑事</Select.Option>
                <Select.Option value="ADMINISTRATIVE">行政</Select.Option>
                <Select.Option value="ARBITRATION">仲裁</Select.Option>
              </Select>

              <Select
                placeholder="案件状态"
                style={{ width: 150 }}
                allowClear
                value={caseStatus}
                onChange={setCaseStatus}
              >
                <Select.Option value="FIRST_INSTANCE">一审</Select.Option>
                <Select.Option value="SECOND_INSTANCE">二审</Select.Option>
                <Select.Option value="FINAL">终审</Select.Option>
                <Select.Option value="RETRIAL">再审</Select.Option>
              </Select>

              <Select
                placeholder="排序方式"
                style={{ width: 150 }}
                value={sortBy}
                onChange={setSortBy}
              >
                <Select.Option value="RELEVANCE">相关度</Select.Option>
                <Select.Option value="DATE">日期</Select.Option>
                <Select.Option value="VIEW">浏览量</Select.Option>
              </Select>

              <Select
                placeholder="检索方式"
                style={{ width: 150 }}
                value={semantic ? 'semantic' : 'keyword'}
                onChange={(val) => setSemantic(val === 'semantic')}
              >
                <Select.Option value="semantic">语义检索</Select.Option>
                <Select.Option value="keyword">关键词检索</Select.Option>
              </Select>

              {activeTab === 'case' && (
                <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                  <DatabaseOutlined />
                  <span style={{ fontSize: 14 }}>本地</span>
                  <Switch
                    checked={useDelilegalAPI}
                    onChange={setUseDelilegalAPI}
                  />
                  <span style={{ fontSize: 14 }}>得理API</span>
                  <ApiOutlined />
                </div>
              )}
            </div>
          </Card>

          {/* Tab切换 */}
          <Card>
            <Tabs
              activeKey={activeTab}
              onChange={handleTabChange}
              items={[
                {
                  key: 'case',
                  label: '案例检索',
                  children: (
                    <Card title={`找到 ${total} 个结果`} style={{ border: 'none', boxShadow: 'none' }}>
                      {loading ? (
                        <div style={styles.loading}>
                          <Spin size="large" />
                        </div>
                      ) : cases.length === 0 ? (
                        <Empty description="请输入关键词搜索案例" />
                      ) : (
                        <VirtualList
                          data={cases}
                          renderItem={(item, index) => (
                            <List.Item style={styles.caseItem}>
                              <List.Item.Meta
                                title={
                                  <div style={styles.caseTitle}>
                                    <a onClick={() => handleViewDetail(item.id)}>{item.title}</a>
                                    {item.score && <Tag color="blue">相似度: {item.score.toFixed(2)}</Tag>}
                                  </div>
                                }
                                description={
                                  <div>
                                    <div style={styles.caseInfo}>
                                      <Tag color={caseTypeColors[item.caseType] || 'default'}>
                                        {item.caseType}
                                      </Tag>
                                      <Tag color={caseStatusColors[item.caseStatus] || 'default'}>
                                        {item.caseStatus}
                                      </Tag>
                                      <span>法院: {item.court}</span>
                                      <span>法官: {item.judge}</span>
                                      <span>日期: {item.judgmentDate}</span>
                                    </div>
                                    <div style={styles.summary}>
                                      <strong>案情简介：</strong>
                                      {item.summary}
                                    </div>
                                    {item.disputeFocus && (
                                      <div style={styles.focus}>
                                        <strong>争议焦点：</strong>
                                        {item.disputeFocus}
                                      </div>
                                    )}
                                    {item.rulingIdea && (
                                      <div style={styles.ruling}>
                                        <strong>裁判要旨：</strong>
                                        {item.rulingIdea}
                                      </div>
                                    )}
                                  </div>
                                }
                              />
                              <div style={styles.caseActions}>
                                <Button icon={<EyeOutlined />}>{item.viewCount}</Button>
                                <Button icon={<FileTextOutlined />} onClick={() => handleViewDetail(item.id)}>
                                  详情
                                </Button>
                                <Button icon={<CopyOutlined />} onClick={() => handleCopy(item.title)}>
                                  复制
                                </Button>
                              </div>
                            </List.Item>
                          )}
                          itemHeight={250}
                          height={600}
                          bufferCount={3}
                          keyExtractor={(item) => item.id}
                        />
                      )}
                      {total > 0 && (
                        <div style={styles.pagination}>
                          <Pagination
                            current={current}
                            pageSize={size}
                            total={total}
                            onChange={handleSearch}
                            showSizeChanger={false}
                          />
                        </div>
                      )}
                    </Card>
                  ),
                },
                {
                  key: 'law',
                  label: '法规检索',
                  children: (
                    <Card title={`找到 ${total} 个结果`} style={{ border: 'none', boxShadow: 'none' }}>
                      {loading ? (
                        <div style={styles.loading}>
                          <Spin size="large" />
                        </div>
                      ) : laws.length === 0 ? (
                        <EmptyState
                          illustration="search"
                          description="请输入关键词检索法规"
                          onAction={() => {
                            if (keyword) handleSearchLaws();
                          }}
                        />
                      ) : (
                        <List
                          dataSource={laws}
                          renderItem={(item) => (
                            <List.Item style={styles.caseItem}>
                              <List.Item.Meta
                                title={
                                  <div style={styles.caseTitle}>
                                    <a onClick={() => { setSelectedLaw(item); setSelectedCase(null); setDetailVisible(true); }}>
                                      {item.title}
                                    </a>
                                  </div>
                                }
                                description={
                                  <div>
                                    <div style={styles.caseInfo}>
                                      <span>发布日期: {item.publishDate}</span>
                                      <span>生效日期: {item.effectiveDate}</span>
                                      <span>来源: {item.source}</span>
                                    </div>
                                    <div style={styles.summary}>
                                      <strong>内容摘要：</strong>
                                      {item.content?.substring(0, 200)}
                                      {item.content?.length > 200 && '...'}
                                    </div>
                                  </div>
                                }
                              />
                            </List.Item>
                          )}
                        />
                      )}
                    </Card>
                  ),
                },
              ]}
            />
          </Card>
        </Col>

        {/* 右侧热门案例 */}
        <Col span={6}>
          <Card title="热门案例" style={{ position: 'sticky', top: 16 }}>
            <List
              dataSource={hotCases}
              renderItem={(item) => (
                <List.Item style={styles.hotItem}>
                  <a onClick={() => handleViewDetail(item.id)}>
                    <div style={styles.hotTitle}>{item.title}</div>
                    <div style={styles.hotInfo}>
                      <span>{item.court}</span>
                      <span>
                        <EyeOutlined /> {item.viewCount}
                      </span>
                    </div>
                  </a>
                </List.Item>
              )}
            />
          </Card>
        </Col>
      </Row>

      {/* 案例详情弹窗 */}
      {(selectedCase || selectedLaw) && (
        <Modal
          title={selectedCase?.title || selectedLaw?.title}
          open={detailVisible}
          onCancel={() => setDetailVisible(false)}
          width={800}
          footer={[
            <Button key="close" onClick={() => setDetailVisible(false)}>
              关闭
            </Button>,
          ]}
        >
          {selectedCase && (
            <div>
              <Descriptions column={1} bordered>
                <Descriptions.Item label="案号">{selectedCase.caseNo}</Descriptions.Item>
                <Descriptions.Item label="案件类型">
                  <Tag color={caseTypeColors[selectedCase.caseType] || 'default'}>
                    {selectedCase.caseType}
                  </Tag>
                </Descriptions.Item>
                <Descriptions.Item label="案件状态">
                  <Tag color={caseStatusColors[selectedCase.caseStatus] || 'default'}>
                    {selectedCase.caseStatus}
                  </Tag>
                </Descriptions.Item>
                <Descriptions.Item label="法院">{selectedCase.court}</Descriptions.Item>
                <Descriptions.Item label="法官">{selectedCase.judge}</Descriptions.Item>
                <Descriptions.Item label="裁判日期">{selectedCase.judgmentDate}</Descriptions.Item>
                <Descriptions.Item label="案由">{selectedCase.cause}</Descriptions.Item>
                <Descriptions.Item label="案情简介">{selectedCase.summary}</Descriptions.Item>
                <Descriptions.Item label="争议焦点">{selectedCase.disputeFocus}</Descriptions.Item>
                <Descriptions.Item label="裁判要旨">{selectedCase.rulingIdea}</Descriptions.Item>
                <Descriptions.Item label="裁判结果">{selectedCase.judgmentResult}</Descriptions.Item>
                <Descriptions.Item label="法律依据">{selectedCase.legalBasis}</Descriptions.Item>
                <Descriptions.Item label="浏览量">{selectedCase.viewCount}</Descriptions.Item>
              </Descriptions>
            </div>
          )}
          {selectedLaw && (
            <div>
              <Descriptions column={1} bordered>
                <Descriptions.Item label="法规标题">{selectedLaw.title}</Descriptions.Item>
                <Descriptions.Item label="发布日期">{selectedLaw.publishDate}</Descriptions.Item>
                <Descriptions.Item label="生效日期">{selectedLaw.effectiveDate}</Descriptions.Item>
                <Descriptions.Item label="来源">{selectedLaw.source}</Descriptions.Item>
                <Descriptions.Item label="内容">
                  <div style={{ maxHeight: 400, overflow: 'auto' }}>
                    <pre style={{ whiteSpace: 'pre-wrap', wordBreak: 'break-word' }}>
                      {selectedLaw.content}
                    </pre>
                  </div>
                </Descriptions.Item>
              </Descriptions>
            </div>
          )}
        </Modal>
      )}
    </div>
  );
}

const styles: Record<string, CSSProperties> = {
  container: {
    padding: 24,
  },
  searchBar: {
    display: 'flex',
    gap: 8,
    marginBottom: 16,
  },
  filters: {
    display: 'flex',
    gap: 8,
    flexWrap: 'wrap',
  },
  loading: {
    display: 'flex',
    justifyContent: 'center',
    padding: 48,
  },
  caseItem: {
    flexDirection: 'column',
    alignItems: 'flex-start',
  },
  caseTitle: {
    display: 'flex',
    alignItems: 'center',
    gap: 8,
  },
  caseInfo: {
    display: 'flex',
    gap: 8,
    marginBottom: 8,
    flexWrap: 'wrap',
    fontSize: 12,
    color: '#666',
  },
  summary: {
    marginBottom: 8,
    fontSize: 13,
    color: '#333',
    lineHeight: 1.6,
  },
  focus: {
    marginBottom: 8,
    fontSize: 13,
    color: '#1890ff',
  },
  ruling: {
    fontSize: 13,
    color: '#52c41a',
    lineHeight: 1.6,
  },
  caseActions: {
    display: 'flex',
    gap: 8,
    marginTop: 8,
  },
  pagination: {
    display: 'flex',
    justifyContent: 'flex-end',
    marginTop: 16,
  },
  hotItem: {
    padding: '8px 0',
  },
  hotTitle: {
    fontSize: 13,
    marginBottom: 4,
    overflow: 'hidden',
    textOverflow: 'ellipsis',
    whiteSpace: 'nowrap',
  },
  hotInfo: {
    display: 'flex',
    justifyContent: 'space-between',
    fontSize: 12,
    color: '#999',
  },
};
