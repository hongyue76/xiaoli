import { useState, useEffect } from 'react';
import { Card, Timeline, Tag, Space, Empty, Input, Select, DatePicker, message } from 'antd';
import {
  ClockCircleOutlined,
  FileTextOutlined,
  SearchOutlined,
  FilterOutlined,
  CalendarOutlined,
} from '@ant-design/icons';
import { ChatHistoryItem } from '../Chat/ChatHistory';
import './ConsultationTimeline.css';

const { RangePicker } = DatePicker;
const { Option } = Select;

export interface ConsultationRecord extends ChatHistoryItem {
  status: 'pending' | 'completed' | 'archived';
  documentGenerated?: boolean;
  category?: string;
}

const CONSULTATION_STORAGE_KEY = 'consultation_records';

export const useConsultationTimeline = () => {
  const [records, setRecords] = useState<ConsultationRecord[]>([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    loadRecords();
  }, []);

  const loadRecords = () => {
    try {
      setLoading(true);
      const saved = localStorage.getItem(CONSULTATION_STORAGE_KEY);
      if (saved) {
        const parsed = JSON.parse(saved);
        setRecords(
          parsed.map((r: any) => ({
            ...r,
            createdAt: new Date(r.createdAt),
            updatedAt: new Date(r.updatedAt),
            messages: r.messages.map((m: any) => ({
              ...m,
              timestamp: new Date(m.timestamp),
            })),
          }))
        );
      }
    } catch (error) {
      console.error('加载咨询记录失败:', error);
      message.error('加载咨询记录失败');
    } finally {
      setLoading(false);
    }
  };

  const saveRecords = (newRecords: ConsultationRecord[]) => {
    try {
      localStorage.setItem(CONSULTATION_STORAGE_KEY, JSON.stringify(newRecords));
      setRecords(newRecords);
    } catch (error) {
      console.error('保存咨询记录失败:', error);
      message.error('保存咨询记录失败');
    }
  };

  const addRecord = (history: ChatHistoryItem, category?: string) => {
    const newRecord: ConsultationRecord = {
      ...history,
      status: 'completed',
      documentGenerated: false,
      category,
    };
    saveRecords([newRecord, ...records]);
    return newRecord;
  };

  const updateRecordStatus = (id: string, status: ConsultationRecord['status']) => {
    const newRecords = records.map(r =>
      r.id === id ? { ...r, status, updatedAt: new Date() } : r
    );
    saveRecords(newRecords);
    message.success('状态更新成功');
  };

  const updateRecordCategory = (id: string, category: string) => {
    const newRecords = records.map(r =>
      r.id === id ? { ...r, category, updatedAt: new Date() } : r
    );
    saveRecords(newRecords);
    message.success('分类更新成功');
  };

  const setDocumentGenerated = (id: string, generated: boolean) => {
    const newRecords = records.map(r =>
      r.id === id ? { ...r, documentGenerated: generated, updatedAt: new Date() } : r
    );
    saveRecords(newRecords);
    message.success(generated ? '文书生成成功' : '文书标记已更新');
  };

  const archiveRecord = (id: string) => {
    updateRecordStatus(id, 'archived');
  };

  const deleteRecord = (id: string) => {
    const newRecords = records.filter(r => r.id !== id);
    saveRecords(newRecords);
    message.success('记录删除成功');
  };

  const filterRecords = (filters: {
    keyword?: string;
    status?: ConsultationRecord['status'];
    category?: string;
    dateRange?: [Date, Date];
  }) => {
    let filtered = records;

    if (filters.keyword) {
      const keyword = filters.keyword.toLowerCase();
      filtered = filtered.filter(r =>
        r.title.toLowerCase().includes(keyword) ||
        r.messages.some(m => m.content.toLowerCase().includes(keyword))
      );
    }

    if (filters.status) {
      filtered = filtered.filter(r => r.status === filters.status);
    }

    if (filters.category) {
      filtered = filtered.filter(r => r.category === filters.category);
    }

    if (filters.dateRange) {
      const [start, end] = filters.dateRange;
      filtered = filtered.filter(r =>
        r.createdAt >= start && r.createdAt <= end
      );
    }

    return filtered;
  };

  const getRecordsByStatus = (status: ConsultationRecord['status']) => {
    return records.filter(r => r.status === status);
  };

  const getRecordsByCategory = (category: string) => {
    return records.filter(r => r.category === category);
  };

  const getCategories = () => {
    const categories = new Set(records.map(r => r.category).filter(Boolean) as string[]);
    return Array.from(categories);
  };

  return {
    records,
    loading,
    addRecord,
    updateRecordStatus,
    updateRecordCategory,
    setDocumentGenerated,
    archiveRecord,
    deleteRecord,
    filterRecords,
    getRecordsByStatus,
    getRecordsByCategory,
    getCategories,
    loadRecords,
  };
};

interface ConsultationTimelineProps {
  onSelectRecord?: (record: ConsultationRecord) => void;
}

const ConsultationTimeline: React.FC<ConsultationTimelineProps> = ({ onSelectRecord }) => {
  const {
    records,
    filterRecords,
    getCategories,
    archiveRecord,
    deleteRecord,
  } = useConsultationTimeline();

  const [searchKeyword, setSearchKeyword] = useState('');
  const [selectedStatus, setSelectedStatus] = useState<string>('all');
  const [selectedCategory, setSelectedCategory] = useState<string>('all');
  const [selectedDateRange, setSelectedDateRange] = useState<[Date, Date] | null>(null);

  const categories = getCategories();
  const filteredRecords = filterRecords({
    keyword: searchKeyword,
    status: selectedStatus !== 'all' ? selectedStatus as ConsultationRecord['status'] : undefined,
    category: selectedCategory !== 'all' ? selectedCategory : undefined,
    dateRange: selectedDateRange || undefined,
  });

  const handleResetFilters = () => {
    setSearchKeyword('');
    setSelectedStatus('all');
    setSelectedCategory('all');
    setSelectedDateRange(null);
  };

  const formatDate = (date: Date) => {
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  const getStatusColor = (status: ConsultationRecord['status']) => {
    const colorMap = {
      pending: 'orange',
      completed: 'green',
      archived: 'default',
    };
    return colorMap[status];
  };

  const getStatusText = (status: ConsultationRecord['status']) => {
    const textMap = {
      pending: '进行中',
      completed: '已完成',
      archived: '已归档',
    };
    return textMap[status];
  };

  const renderTimelineItem = (record: ConsultationRecord) => {
    const userMessages = record.messages.filter(m => m.role === 'user');
    const aiMessages = record.messages.filter(m => m.role === 'assistant');

    return (
      <Timeline.Item
        key={record.id}
        dot={<ClockCircleOutlined className="timeline-dot" />}
        color="blue"
      >
        <Card
          size="small"
          className="timeline-card"
          onClick={() => onSelectRecord?.(record)}
        >
          <div className="card-header">
            <Space>
              <FileTextOutlined className="file-icon" />
              <span className="record-title">{record.title}</span>
            </Space>
            <Space size={4}>
              <Tag color={getStatusColor(record.status)}>
                {getStatusText(record.status)}
              </Tag>
              {record.documentGenerated && (
                <Tag color="purple">已生成文书</Tag>
              )}
              {record.category && (
                <Tag color="cyan">{record.category}</Tag>
              )}
            </Space>
          </div>
          <div className="card-content">
            <div className="record-info">
              <span className="info-item">
                📝 {userMessages.length} 个问题
              </span>
              <span className="info-item">
                💬 {aiMessages.length} 个回答
              </span>
              <span className="info-item">
                🤖 {record.model || '模拟'}
              </span>
            </div>
          </div>
          <div className="card-footer">
            <span className="record-time">{formatDate(record.updatedAt)}</span>
            <Space size={4}>
              {record.status === 'completed' && (
                <Tag
                  color="blue"
                  style={{ cursor: 'pointer' }}
                  onClick={(e) => {
                    e.stopPropagation();
                    archiveRecord(record.id);
                  }}
                >
                  归档
                </Tag>
              )}
              <Tag
                color="red"
                style={{ cursor: 'pointer' }}
                onClick={(e) => {
                  e.stopPropagation();
                  deleteRecord(record.id);
                }}
              >
                删除
              </Tag>
            </Space>
          </div>
        </Card>
      </Timeline.Item>
    );
  };

  return (
    <Card
      title={
        <div className="timeline-header">
          <ClockCircleOutlined className="header-icon" />
          <span>咨询记录时间轴</span>
          <Tag color="blue" className="count-badge">
            {filteredRecords.length} 条记录
          </Tag>
        </div>
      }
      bordered={false}
      className="consultation-timeline"
    >
      {/* 筛选工具栏 */}
      <div className="filter-toolbar">
        <Space size="middle" wrap>
          <Input
            placeholder="搜索记录..."
            prefix={<SearchOutlined />}
            allowClear
            value={searchKeyword}
            onChange={(e) => setSearchKeyword(e.target.value)}
            style={{ width: 200 }}
          />
          <Select
            placeholder="状态"
            value={selectedStatus}
            onChange={setSelectedStatus}
            style={{ width: 120 }}
            allowClear
          >
            <Option value="all">全部状态</Option>
            <Option value="pending">进行中</Option>
            <Option value="completed">已完成</Option>
            <Option value="archived">已归档</Option>
          </Select>
          <Select
            placeholder="分类"
            value={selectedCategory}
            onChange={setSelectedCategory}
            style={{ width: 120 }}
            allowClear
          >
            <Option value="all">全部分类</Option>
            {categories.map(cat => (
              <Option key={cat} value={cat}>{cat}</Option>
            ))}
          </Select>
          <RangePicker
            value={selectedDateRange}
            onChange={(dates) => {
              if (dates && dates[0] && dates[1]) {
                setSelectedDateRange([dates[0].toDate(), dates[1].toDate()]);
              } else {
                setSelectedDateRange(null);
              }
            }}
            placeholder={['开始日期', '结束日期']}
          />
          {searchKeyword || selectedStatus !== 'all' || selectedCategory !== 'all' || selectedDateRange ? (
            <Tag
              icon={<FilterOutlined />}
              closable
              onClose={handleResetFilters}
              color="blue"
            >
              清除筛选
            </Tag>
          ) : null}
        </Space>
      </div>

      {/* 时间轴 */}
      {filteredRecords.length > 0 ? (
        <Timeline
          mode="left"
          className="timeline-list"
          items={filteredRecords.map(record => ({
            children: renderTimelineItem(record),
          }))}
        />
      ) : (
        <Empty
          description={searchKeyword || selectedStatus !== 'all' ? '未找到匹配的记录' : '暂无咨询记录'}
          className="timeline-empty"
        />
      )}
    </Card>
  );
};

export default ConsultationTimeline;
