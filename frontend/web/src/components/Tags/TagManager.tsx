import { useState, useEffect } from 'react';
import { Tag, Space, Input, Button, Popconfirm, Modal, message, Empty } from 'antd';
import {
  TagOutlined,
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
} from '@ant-design/icons';
import './TagManager.css';

export interface TagItem {
  id: string;
  name: string;
  color: string;
  count: number;
  createdAt: Date;
}

const TAGS_STORAGE_KEY = 'tags_data';

const TAG_COLORS = [
  'blue',
  'green',
  'orange',
  'red',
  'purple',
  'cyan',
  'magenta',
  'lime',
  'gold',
  'geekblue',
];

export const useTagManager = () => {
  const [tags, setTags] = useState<TagItem[]>([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    loadTags();
  }, []);

  const loadTags = () => {
    try {
      setLoading(true);
      const saved = localStorage.getItem(TAGS_STORAGE_KEY);
      if (saved) {
        const parsed = JSON.parse(saved);
        setTags(
          parsed.map((t: any) => ({
            ...t,
            createdAt: new Date(t.createdAt),
          }))
        );
      }
    } catch (error) {
      console.error('加载标签失败:', error);
      message.error('加载标签失败');
    } finally {
      setLoading(false);
    }
  };

  const saveTags = (newTags: TagItem[]) => {
    try {
      localStorage.setItem(TAGS_STORAGE_KEY, JSON.stringify(newTags));
      setTags(newTags);
    } catch (error) {
      console.error('保存标签失败:', error);
      message.error('保存标签失败');
    }
  };

  const addTag = (name: string, color?: string) => {
    const existingTag = tags.find(t => t.name === name);
    if (existingTag) {
      message.warning('标签已存在');
      return null;
    }

    const newTag: TagItem = {
      id: Date.now().toString(),
      name,
      color: color || TAG_COLORS[tags.length % TAG_COLORS.length],
      count: 0,
      createdAt: new Date(),
    };
    saveTags([...tags, newTag]);
    message.success('标签创建成功');
    return newTag;
  };

  const updateTag = (id: string, updates: Partial<TagItem>) => {
    // 检查名称是否重复
    if (updates.name) {
      const existingTag = tags.find(t => t.name === updates.name && t.id !== id);
      if (existingTag) {
        message.warning('标签名称已存在');
        return;
      }
    }

    const newTags = tags.map(t =>
      t.id === id ? { ...t, ...updates } : t
    );
    saveTags(newTags);
    message.success('标签更新成功');
  };

  const deleteTag = (id: string) => {
    const newTags = tags.filter(t => t.id !== id);
    saveTags(newTags);
    message.success('标签删除成功');
  };

  const incrementTagCount = (tagName: string) => {
    const newTags = tags.map(t =>
      t.name === tagName ? { ...t, count: t.count + 1 } : t
    );
    saveTags(newTags);
  };

  const decrementTagCount = (tagName: string) => {
    const newTags = tags.map(t =>
      t.name === tagName ? { ...t, count: Math.max(0, t.count - 1) } : t
    );
    saveTags(newTags);
  };

  const searchTags = (keyword: string) => {
    if (!keyword.trim()) return tags;
    const lowerKeyword = keyword.toLowerCase();
    return tags.filter(t =>
      t.name.toLowerCase().includes(lowerKeyword)
    );
  };

  const getPopularTags = (limit: number = 10) => {
    return [...tags]
      .sort((a, b) => b.count - a.count)
      .slice(0, limit);
  };

  const getRecentTags = (limit: number = 10) => {
    return [...tags]
      .sort((a, b) => b.createdAt.getTime() - a.createdAt.getTime())
      .slice(0, limit);
  };

  return {
    tags,
    loading,
    addTag,
    updateTag,
    deleteTag,
    incrementTagCount,
    decrementTagCount,
    searchTags,
    getPopularTags,
    getRecentTags,
    loadTags,
  };
};

interface TagManagerProps {
  visible: boolean;
  onClose: () => void;
  onTagSelect?: (tag: TagItem) => void;
}

const TagManager: React.FC<TagManagerProps> = ({ visible, onClose, onTagSelect }) => {
  const {
    tags,
    addTag,
    updateTag,
    deleteTag,
    getPopularTags,
    getRecentTags,
  } = useTagManager();

  const [searchKeyword, setSearchKeyword] = useState('');
  const [newTagModalVisible, setNewTagModalVisible] = useState(false);
  const [editTagModalVisible, setEditTagModalVisible] = useState(false);
  const [newTagName, setNewTagName] = useState('');
  const [newTagColor, setNewTagColor] = useState('blue');
  const [editingTag, setEditingTag] = useState<TagItem | null>(null);

  const filteredTags = tags.filter(t =>
    t.name.toLowerCase().includes(searchKeyword.toLowerCase())
  );

  const popularTags = getPopularTags(5);
  const recentTags = getRecentTags(5);

  const handleAddTag = () => {
    if (!newTagName.trim()) {
      message.warning('请输入标签名称');
      return;
    }
    addTag(newTagName, newTagColor);
    setNewTagName('');
    setNewTagColor('blue');
    setNewTagModalVisible(false);
  };

  const handleEditTag = () => {
    if (!editingTag) return;
    updateTag(editingTag.id, {
      name: newTagName,
      color: newTagColor,
    });
    setNewTagName('');
    setNewTagColor('blue');
    setEditTagModalVisible(false);
    setEditingTag(null);
  };

  const openEditModal = (tag: TagItem) => {
    setEditingTag(tag);
    setNewTagName(tag.name);
    setNewTagColor(tag.color);
    setEditTagModalVisible(true);
  };

  return (
    <Modal
      title={
        <div className="tag-modal-title">
          <TagOutlined className="title-icon" />
          <span>标签管理</span>
        </div>
      }
      open={visible}
      onCancel={onClose}
      footer={[
        <Button key="close" onClick={onClose}>
          关闭
        </Button>,
        <Button
          key="add"
          type="primary"
          icon={<PlusOutlined />}
          onClick={() => setNewTagModalVisible(true)}
        >
          新建标签
        </Button>,
      ]}
      width={800}
      className="tag-manager-modal"
    >
      <div className="tag-manager-content">
        {/* 搜索 */}
        <div className="tag-search">
          <Input.Search
            placeholder="搜索标签..."
            allowClear
            value={searchKeyword}
            onChange={(e) => setSearchKeyword(e.target.value)}
          />
        </div>

        {/* 热门标签 */}
        {popularTags.length > 0 && (
          <div className="tag-section">
            <h3 className="section-title">热门标签</h3>
            <Space size={[8, 8]} wrap>
              {popularTags.map(tag => (
                <Tag
                  key={tag.id}
                  color={tag.color}
                  className="tag-item"
                  onClick={() => onTagSelect?.(tag)}
                >
                  {tag.name} ({tag.count})
                </Tag>
              ))}
            </Space>
          </div>
        )}

        {/* 最近标签 */}
        {recentTags.length > 0 && (
          <div className="tag-section">
            <h3 className="section-title">最近使用</h3>
            <Space size={[8, 8]} wrap>
              {recentTags.map(tag => (
                <Tag
                  key={tag.id}
                  color={tag.color}
                  className="tag-item"
                  onClick={() => onTagSelect?.(tag)}
                >
                  {tag.name}
                </Tag>
              ))}
            </Space>
          </div>
        )}

        {/* 所有标签 */}
        <div className="tag-section">
          <h3 className="section-title">
            全部标签 ({filteredTags.length})
          </h3>
          {filteredTags.length > 0 ? (
            <Space size={[8, 8]} wrap>
              {filteredTags.map(tag => (
                <Tag
                  key={tag.id}
                  color={tag.color}
                  closable
                  onClose={(e) => {
                    e.preventDefault();
                    deleteTag(tag.id);
                  }}
                  closeIcon={
                    <Space size={4}>
                      <EditOutlined onClick={() => openEditModal(tag)} />
                      <DeleteOutlined />
                    </Space>
                  }
                  className="tag-item"
                  onClick={() => onTagSelect?.(tag)}
                >
                  {tag.name} ({tag.count})
                </Tag>
              ))}
            </Space>
          ) : (
            <Empty
              description={searchKeyword ? '未找到匹配的标签' : '暂无标签'}
              image={Empty.PRESENTED_IMAGE_SIMPLE}
            />
          )}
        </div>
      </div>

      {/* 新建标签模态框 */}
      <Modal
        title="新建标签"
        open={newTagModalVisible}
        onOk={handleAddTag}
        onCancel={() => {
          setNewTagModalVisible(false);
          setNewTagName('');
          setNewTagColor('blue');
        }}
      >
        <Space direction="vertical" style={{ width: '100%' }} size="middle">
          <Input
            placeholder="标签名称"
            value={newTagName}
            onChange={(e) => setNewTagName(e.target.value)}
            onPressEnter={handleAddTag}
          />
          <div>
            <span style={{ marginBottom: 8, display: 'block' }}>选择颜色:</span>
            <Space size={8} wrap>
              {TAG_COLORS.map(color => (
                <div
                  key={color}
                  className={`color-option ${newTagColor === color ? 'selected' : ''}`}
                  onClick={() => setNewTagColor(color)}
                  style={{ backgroundColor: getTagColorHex(color) }}
                />
              ))}
            </Space>
          </div>
        </Space>
      </Modal>

      {/* 编辑标签模态框 */}
      <Modal
        title="编辑标签"
        open={editTagModalVisible}
        onOk={handleEditTag}
        onCancel={() => {
          setEditTagModalVisible(false);
          setEditingTag(null);
          setNewTagName('');
          setNewTagColor('blue');
        }}
      >
        <Space direction="vertical" style={{ width: '100%' }} size="middle">
          <Input
            placeholder="标签名称"
            value={newTagName}
            onChange={(e) => setNewTagName(e.target.value)}
          />
          <div>
            <span style={{ marginBottom: 8, display: 'block' }}>选择颜色:</span>
            <Space size={8} wrap>
              {TAG_COLORS.map(color => (
                <div
                  key={color}
                  className={`color-option ${newTagColor === color ? 'selected' : ''}`}
                  onClick={() => setNewTagColor(color)}
                  style={{ backgroundColor: getTagColorHex(color) }}
                />
              ))}
            </Space>
          </div>
        </Space>
      </Modal>
    </Modal>
  );
};

// 获取标签颜色的十六进制值
const getTagColorHex = (color: string) => {
  const colorMap: Record<string, string> = {
    blue: '#1890ff',
    green: '#52c41a',
    orange: '#faad14',
    red: '#ff4d4f',
    purple: '#722ed1',
    cyan: '#13c2c2',
    magenta: '#eb2f96',
    lime: '#a0d911',
    gold: '#faad14',
    geekblue: '#2f54eb',
  };
  return colorMap[color] || '#1890ff';
};

export default TagManager;
