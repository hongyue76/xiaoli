import React, { useState, useEffect } from 'react';
import { List, Input, Space, Button, Popconfirm, Empty, Tag, Drawer, Tabs, Badge } from 'antd';
import {
  HistoryOutlined,
  SearchOutlined,
  StarOutlined,
  StarFilled,
  DeleteOutlined,
  ClearOutlined,
  DownloadOutlined,
  UploadOutlined,
  CloseOutlined,
  FilterOutlined,
} from '@ant-design/icons';
import { useChatHistory, ChatHistoryItem } from './ChatHistory';
import EmptyState from '@/components/EmptyState';
import './ChatHistoryList.css';

const { Search } = Input;
const { TabPane } = Tabs;

interface ChatHistoryListProps {
  visible: boolean;
  onClose: () => void;
  onSelectHistory: (history: ChatHistoryItem) => void;
  currentHistoryId?: string;
}

const ChatHistoryList: React.FC<ChatHistoryListProps> = ({
  visible,
  onClose,
  onSelectHistory,
  currentHistoryId,
}) => {
  const {
    histories,
    loading,
    searchHistories,
    getFavoriteHistories,
    deleteHistory,
    clearAllHistories,
    toggleFavorite,
    exportHistory,
    exportAllHistories,
    importHistory,
  } = useChatHistory();

  const [activeTab, setActiveTab] = useState('all');
  const [searchKeyword, setSearchKeyword] = useState('');
  const [filteredHistories, setFilteredHistories] = useState<ChatHistoryItem[]>([]);
  const [importing, setImporting] = useState(false);

  useEffect(() => {
    if (activeTab === 'all') {
      setFilteredHistories(searchHistories(searchKeyword));
    } else if (activeTab === 'favorites') {
      setFilteredHistories(getFavoriteHistories().filter(h =>
        h.title.toLowerCase().includes(searchKeyword.toLowerCase()) ||
        h.messages.some(m => m.content.toLowerCase().includes(searchKeyword.toLowerCase()))
      ));
    }
  }, [activeTab, searchKeyword, histories]);

  const handleSearch = (value: string) => {
    setSearchKeyword(value);
  };

  const handleDelete = (id: string, e: React.MouseEvent) => {
    e.stopPropagation();
    deleteHistory(id);
  };

  const handleToggleFavorite = (id: string, e: React.MouseEvent) => {
    e.stopPropagation();
    toggleFavorite(id);
  };

  const handleExport = (id: string, e: React.MouseEvent) => {
    e.stopPropagation();
    exportHistory(id);
  };

  const handleExportAll = () => {
    exportAllHistories();
  };

  const handleClearAll = () => {
    clearAllHistories();
    setSearchKeyword('');
  };

  const handleImport = () => {
    const input = document.createElement('input');
    input.type = 'file';
    input.accept = 'application/json';
    input.onchange = async (e) => {
      const file = (e.target as HTMLInputElement).files?.[0];
      if (file) {
        setImporting(true);
        try {
          await importHistory(file);
          setSearchKeyword('');
        } catch (error) {
          console.error('导入失败:', error);
        } finally {
          setImporting(false);
        }
      }
    };
    input.click();
  };

  const formatTime = (date: Date) => {
    const now = new Date();
    const diff = now.getTime() - date.getTime();
    const days = Math.floor(diff / (1000 * 60 * 60 * 24));

    if (days === 0) {
      return '今天';
    } else if (days === 1) {
      return '昨天';
    } else if (days < 7) {
      return `${days}天前`;
    } else {
      return date.toLocaleDateString('zh-CN', {
        month: '2-digit',
        day: '2-digit',
      });
    }
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

  const renderHistoryItem = (history: ChatHistoryItem) => {
    const isSelected = history.id === currentHistoryId;
    const userMessages = history.messages.filter(m => m.role === 'user');
    const aiMessages = history.messages.filter(m => m.role === 'assistant');

    return (
      <List.Item
        key={history.id}
        className={`history-item ${isSelected ? 'selected' : ''}`}
        onClick={() => onSelectHistory(history)}
        actions={[
          <Button
            type="text"
            size="small"
            icon={history.isFavorite ? <StarFilled /> : <StarOutlined />}
            onClick={(e) => handleToggleFavorite(history.id, e)}
            className={history.isFavorite ? 'favorite-btn active' : 'favorite-btn'}
          />,
          <Button
            type="text"
            size="small"
            icon={<DownloadOutlined />}
            onClick={(e) => handleExport(history.id, e)}
          />,
          <Popconfirm
            title="确认删除"
            description="确定要删除这条历史记录吗？"
            onConfirm={(e) => handleDelete(history.id, e as any)}
            okText="删除"
            cancelText="取消"
          >
            <Button
              type="text"
              size="small"
              danger
              icon={<DeleteOutlined />}
            />
          </Popconfirm>,
        ]}
      >
        <List.Item.Meta
          avatar={
            <div className="history-avatar">
              <HistoryOutlined />
            </div>
          }
          title={
            <div className="history-title-row">
              <span className="history-title">{history.title}</span>
              <Space size={4}>
                <Tag color="blue" size="small">{userMessages.length} 问</Tag>
                <Tag color="green" size="small">{aiMessages.length} 答</Tag>
              </Space>
            </div>
          }
          description={
            <div className="history-description">
              <span className="history-time">{formatTime(history.updatedAt)}</span>
              {history.model && (
                <Tag color="purple" size="small">{history.model}</Tag>
              )}
            </div>
          }
        />
      </List.Item>
    );
  };

  return (
    <Drawer
      title={
        <div className="history-drawer-title">
          <HistoryOutlined className="title-icon" />
          <span>对话历史</span>
          <Badge count={histories.length} showZero className="history-count-badge" />
        </div>
      }
      placement="right"
      onClose={onClose}
      visible={visible}
      width={400}
      className="history-drawer"
    >
      <div className="history-content">
        {/* 搜索和操作栏 */}
        <div className="history-toolbar">
          <Search
            placeholder="搜索历史记录..."
            allowClear
            value={searchKeyword}
            onChange={(e) => setSearchKeyword(e.target.value)}
            onSearch={handleSearch}
            className="history-search"
            prefix={<SearchOutlined />}
          />
          <Space className="history-actions">
            <Button
              size="small"
              icon={<UploadOutlined />}
              onClick={handleImport}
              loading={importing}
            >
              导入
            </Button>
            <Button
              size="small"
              icon={<DownloadOutlined />}
              onClick={handleExportAll}
            >
              导出全部
            </Button>
            {histories.length > 0 && (
              <Popconfirm
                title="确认清空"
                description="确定要清空所有历史记录吗？此操作不可恢复。"
                onConfirm={handleClearAll}
                okText="清空"
                cancelText="取消"
                okButtonProps={{ danger: true }}
              >
                <Button
                  size="small"
                  danger
                  icon={<ClearOutlined />}
                >
                  清空
                </Button>
              </Popconfirm>
            )}
          </Space>
        </div>

        {/* 分类标签 */}
        <Tabs
          activeKey={activeTab}
          onChange={setActiveTab}
          className="history-tabs"
        >
          <TabPane
            tab={
              <span>
                全部
                <Badge count={histories.length} size="small" offset={[4, 0]} />
              </span>
            }
            key="all"
          >
            <List
              loading={loading}
              dataSource={filteredHistories}
              renderItem={renderHistoryItem}
              locale={{
                emptyText: (
                  <Empty
                    image={Empty.PRESENTED_IMAGE_SIMPLE}
                    description={searchKeyword ? '未找到匹配的历史记录' : '暂无历史记录'}
                  />
                ),
              }}
              className="history-list"
            />
          </TabPane>
          <TabPane
            tab={
              <span>
                收藏
                <Badge count={getFavoriteHistories().length} size="small" offset={[4, 0]} />
              </span>
            }
            key="favorites"
          >
            <List
              loading={loading}
              dataSource={filteredHistories}
              renderItem={renderHistoryItem}
              locale={{
                emptyText: (
                  <Empty
                    image={Empty.PRESENTED_IMAGE_SIMPLE}
                    description={searchKeyword ? '未找到匹配的收藏' : '暂无收藏'}
                  />
                ),
              }}
              className="history-list"
            />
          </TabPane>
        </Tabs>
      </div>
    </Drawer>
  );
};

export default ChatHistoryList;
