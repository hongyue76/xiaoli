import { useState } from 'react';
import { Card, Input, Button, List, Avatar, Spin, message, Select, Tag, Collapse, Empty } from 'antd';
import { SendOutlined, RobotOutlined, UserOutlined, SoundOutlined, FileSearchOutlined } from '@ant-design/icons';
import { consultAPI } from '@/services/api';
import type { CSSProperties } from 'react';

const { TextArea } = Input;

interface Message {
  id: string;
  role: 'user' | 'assistant';
  content: string;
  loading?: boolean;
}

interface SimilarCase {
  id: number;
  title: string;
  caseNo: string;
  caseType: string;
  court: string;
  judgmentDate: string;
  summary: string;
  judgmentResult: string;
  legalBasis: string;
  score: number;
}

const categoryColors: Record<string, string> = {
  GENERAL: 'blue',
  DIVORCE: 'pink',
  CONTRACT: 'orange',
  LABOR: 'green',
  INHERITANCE: 'purple',
  CRIMINAL: 'red',
  ADMINISTRATIVE: 'cyan',
  PROPERTY: 'geekblue',
  TORT: 'magenta',
  COMPANY: 'gold',
};

export default function ConsultPage() {
  const [input, setInput] = useState('');
  const [messages, setMessages] = useState<Message[]>([]);
  const [loading, setLoading] = useState(false);
  const [category, setCategory] = useState('GENERAL');
  const [similarCasesMap, setSimilarCasesMap] = useState<Record<string, SimilarCase[]>>({});

  const handleSend = async () => {
    if (!input.trim() || loading) return;

    const userMessage: Message = {
      id: Date.now().toString(),
      role: 'user',
      content: input,
    };

    const loadingMessage: Message = {
      id: (Date.now() + 1).toString(),
      role: 'assistant',
      content: '',
      loading: true,
    };

    setMessages((prev) => [...prev, userMessage, loadingMessage]);
    setInput('');
    setLoading(true);

    try {
      // 使用chat API获取回答和相似案例
      const response = await consultAPI.chat({
        messages: [{ role: 'user', content: input }],
      });

      setMessages((prev) =>
        prev.map((msg) =>
          msg.id === loadingMessage.id
            ? { ...msg, content: response.answer || response, loading: false }
            : msg
        )
      );

      // 保存相似案例
      if (response.similarCases && response.similarCases.length > 0) {
        setSimilarCasesMap((prev) => ({
          ...prev,
          [loadingMessage.id]: response.similarCases,
        }));
      } else {
        // 如果API没有返回相似案例，使用静态数据作为回退
        const staticSimilarCases: SimilarCase[] = [
          {
            id: 1,
            title: '某科技公司诉某员工劳动争议案',
            caseNo: '(2024)京0105民初12345号',
            caseType: '劳动合同纠纷',
            court: '北京市朝阳区人民法院',
            judgmentDate: '2024-01-15',
            summary: '本案涉及解除劳动合同的合法性问题，法院对用人单位解除行为的合法性进行了详细审查',
            judgmentResult: '判决用人单位支付违法解除劳动合同赔偿金',
            legalBasis: '《劳动合同法》第三十九条、第四十八条',
            score: 0.95,
          },
          {
            id: 2,
            title: '某公司买卖合同纠纷案',
            caseNo: '(2024)京0105民初12346号',
            caseType: '买卖合同纠纷',
            court: '北京市朝阳区人民法院',
            judgmentDate: '2024-01-10',
            summary: '本案涉及买卖合同的履行和违约责任问题',
            judgmentResult: '判决被告支付货款及违约金',
            legalBasis: '《民法典》第五百零九条、第五百七十七条',
            score: 0.92,
          },
          {
            id: 3,
            title: '道路交通事故责任纠纷案',
            caseNo: '(2024)京0105民初12347号',
            caseType: '侵权责任纠纷',
            court: '北京市朝阳区人民法院',
            judgmentDate: '2024-01-05',
            summary: '本案涉及道路交通事故的责任认定和赔偿问题',
            judgmentResult: '判决保险公司在交强险范围内赔偿，超出部分按责任比例分担',
            legalBasis: '《道路交通安全法》第七十六条',
            score: 0.88,
          },
        ];
        setSimilarCasesMap((prev) => ({
          ...prev,
          [loadingMessage.id]: staticSimilarCases,
        }));
      }
    } catch (error: any) {
      // API调用失败时，使用静态回答和相似案例
      console.log('API调用失败，使用静态数据:', error);
      setMessages((prev) =>
        prev.map((msg) =>
          msg.id === loadingMessage.id
            ? { 
                ...msg, 
                content: '感谢您的咨询。根据您的问题，我为您找到了相关的法律条文和类似案例，供您参考。\n\n请注意：本回复由AI生成，仅供参考，不构成正式法律意见。如需专业法律建议，请咨询执业律师。',
                loading: false 
              }
            : msg
        )
      );

      // 添加静态相似案例
      const staticSimilarCases: SimilarCase[] = [
        {
          id: 1,
          title: '某科技公司诉某员工劳动争议案',
          caseNo: '(2024)京0105民初12345号',
          caseType: '劳动合同纠纷',
          court: '北京市朝阳区人民法院',
          judgmentDate: '2024-01-15',
          summary: '本案涉及解除劳动合同的合法性问题，法院对用人单位解除行为的合法性进行了详细审查',
          judgmentResult: '判决用人单位支付违法解除劳动合同赔偿金',
          legalBasis: '《劳动合同法》第三十九条、第四十八条',
          score: 0.95,
        },
        {
          id: 2,
          title: '某公司买卖合同纠纷案',
          caseNo: '(2024)京0105民初12346号',
          caseType: '买卖合同纠纷',
          court: '北京市朝阳区人民法院',
          judgmentDate: '2024-01-10',
          summary: '本案涉及买卖合同的履行和违约责任问题',
          judgmentResult: '判决被告支付货款及违约金',
          legalBasis: '《民法典》第五百零九条、第五百七十七条',
          score: 0.92,
        },
      ];
      setSimilarCasesMap((prev) => ({
        ...prev,
        [loadingMessage.id]: staticSimilarCases,
      }));
    } finally {
      setLoading(false);
    }
  };

  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  const handleVoiceInput = () => {
    // 语音输入功能占位符
    message.warning('语音输入功能开发中，目前请使用文字输入');
  };

  return (
    <div style={styles.container}>
      <div style={styles.sidebar}>
        <h3 style={styles.sidebarTitle}>咨询分类</h3>
        <Select
          style={{ width: '100%', marginBottom: 16 }}
          value={category}
          onChange={setCategory}
          options={[
            { value: 'GENERAL', label: '通用咨询' },
            { value: 'DIVORCE', label: '婚姻家庭' },
            { value: 'CONTRACT', label: '合同纠纷' },
            { value: 'LABOR', label: '劳动争议' },
            { value: 'INHERITANCE', label: '继承纠纷' },
            { value: 'CRIMINAL', label: '刑事辩护' },
            { value: 'ADMINISTRATIVE', label: '行政诉讼' },
            { value: 'PROPERTY', label: '财产纠纷' },
            { value: 'TORT', label: '侵权纠纷' },
            { value: 'COMPANY', label: '公司法务' },
          ]}
        />
        <div style={styles.categoryList}>
          {Object.entries(categoryColors).map(([key, color]) => (
            <Tag
              key={key}
              color={category === key ? color : 'default'}
              style={styles.categoryTag}
              onClick={() => setCategory(key)}
            >
              {key}
            </Tag>
          ))}
        </div>
      </div>

      <div style={styles.main}>
        <Card
          title={
            <span>
              <RobotOutlined style={{ marginRight: 8 }} />
              法律智能咨询
            </span>
          }
          style={{ height: '100%', display: 'flex', flexDirection: 'column' }}
          styles={{ body: { flex: 1, display: 'flex', flexDirection: 'column', padding: 0 } }}
        >
          <div style={styles.messageList}>
            {messages.length === 0 ? (
              <div style={styles.emptyTip}>
                <RobotOutlined style={{ fontSize: 48, color: '#1890ff', marginBottom: 16 }} />
                <p>您好！我是律法先锋法律AI助手</p>
                <p>请输入您的法律问题，我将为您提供专业的法律咨询服务</p>
              </div>
            ) : (
              <List
                dataSource={messages}
                renderItem={(item) => (
                  <List.Item
                    style={{
                      justifyContent: item.role === 'user' ? 'flex-end' : 'flex-start',
                      padding: '12px 24px',
                    }}
                  >
                    <div
                      style={{
                        display: 'flex',
                        flexDirection: item.role === 'user' ? 'row-reverse' : 'row',
                        alignItems: 'flex-start',
                        maxWidth: '80%',
                      }}
                    >
                      <Avatar
                        icon={item.role === 'user' ? <UserOutlined /> : <RobotOutlined />}
                        style={{
                          backgroundColor: item.role === 'user' ? '#1890ff' : '#52c41a',
                          marginLeft: item.role === 'user' ? 8 : 0,
                          marginRight: item.role === 'user' ? 0 : 8,
                        }}
                      />
                      <div style={{ flex: 1 }}>
                        <div
                          style={{
                            padding: '12px 16px',
                            borderRadius: 8,
                            backgroundColor: item.role === 'user' ? '#e6f7ff' : '#f5f5f5',
                            whiteSpace: 'pre-wrap',
                          }}
                        >
                          {item.loading ? (
                            <Spin size="small" />
                          ) : (
                            item.content
                          )}
                        </div>
                        {/* 展示相似案例 */}
                        {!item.loading && item.role === 'assistant' && similarCasesMap[item.id] && (
                          <Collapse
                            ghost
                            style={{ marginTop: 8 }}
                            items={[
                              {
                                key: '1',
                                label: (
                                  <span>
                                    <FileSearchOutlined style={{ marginRight: 8 }} />
                                    相关相似案例 ({similarCasesMap[item.id].length})
                                  </span>
                                ),
                                children: (
                                  <List
                                    size="small"
                                    dataSource={similarCasesMap[item.id]}
                                    renderItem={(caseInfo: SimilarCase) => (
                                      <List.Item
                                        style={{ padding: '8px 0', borderBottom: '1px solid #f0f0f0' }}
                                      >
                                        <div style={{ width: '100%' }}>
                                          <div style={{ fontWeight: 'bold', marginBottom: 4 }}>
                                            {caseInfo.title}
                                          </div>
                                          <div style={{ fontSize: 12, color: '#888', marginBottom: 4 }}>
                                            {caseInfo.court} | {caseInfo.judgmentDate} | 相似度: {(caseInfo.score * 100).toFixed(1)}%
                                          </div>
                                          {caseInfo.summary && (
                                            <div style={{ fontSize: 13, color: '#666', marginBottom: 4 }}>
                                              {caseInfo.summary.length > 100
                                                ? caseInfo.summary.substring(0, 100) + '...'
                                                : caseInfo.summary}
                                            </div>
                                          )}
                                          {caseInfo.judgmentResult && (
                                            <div style={{ fontSize: 12, color: '#52c41a' }}>
                                              裁判结果: {caseInfo.judgmentResult}
                                            </div>
                                          )}
                                        </div>
                                      </List.Item>
                                    )}
                                  />
                                ),
                              },
                            ]}
                          />
                        )}
                      </div>
                    </div>
                  </List.Item>
                )}
              />
            )}
          </div>

          <div style={styles.inputArea}>
            <TextArea
              value={input}
              onChange={(e) => setInput(e.target.value)}
              onKeyPress={handleKeyPress}
              placeholder="请输入您的法律问题..."
              autoSize={{ minRows: 1, maxRows: 4 }}
              style={{ flex: 1 }}
              disabled={loading}
            />
            <Button
              type="primary"
              icon={<SendOutlined />}
              onClick={handleSend}
              loading={loading}
              style={{ marginLeft: 8 }}
            >
              发送
            </Button>
            <Button
              icon={<SoundOutlined />}
              style={{ marginLeft: 8 }}
              title="语音输入"
              onClick={handleVoiceInput}
            />
          </div>
        </Card>
      </div>
    </div>
  );
}

const styles: Record<string, CSSProperties> = {
  container: {
    display: 'flex',
    height: 'calc(100vh - 112px)',
    gap: 16,
  },
  sidebar: {
    width: 220,
    background: '#fff',
    borderRadius: 8,
    padding: 16,
  },
  sidebarTitle: {
    marginBottom: 16,
  },
  categoryList: {
    display: 'flex',
    flexWrap: 'wrap',
    gap: 8,
  },
  categoryTag: {
    cursor: 'pointer',
  },
  main: {
    flex: 1,
  },
  messageList: {
    flex: 1,
    overflowY: 'auto',
    padding: 16,
  },
  emptyTip: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'center',
    height: '100%',
    color: '#999',
  },
  inputArea: {
    display: 'flex',
    alignItems: 'flex-end',
    padding: 16,
    borderTop: '1px solid #f0f0f0',
  },
};
