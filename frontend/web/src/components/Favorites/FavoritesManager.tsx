import { useState, useEffect } from 'react';
import {
  Modal,
  Input,
  Select,
  Tree,
  Button,
  Space,
  Popconfirm,
  Tag,
  Empty,
  message,
} from 'antd';
import {
  FolderOutlined,
  FolderOpenOutlined,
  FileTextOutlined,
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  StarFilled,
} from '@ant-design/icons';
import './FavoritesManager.css';

const { Search } = Input;
const { Option } = Select;

export interface FavoriteFolder {
  id: string;
  name: string;
  parentId: string | null;
  children?: FavoriteFolder[];
}

export interface FavoriteItem {
  id: string;
  title: string;
  type: 'law' | 'case' | 'document';
  content: string;
  folderId: string;
  tags: string[];
  createdAt: Date;
  updatedAt: Date;
}

const FAVORITES_STORAGE_KEY = 'favorites_data';

export const useFavorites = () => {
  const [folders, setFolders] = useState<FavoriteFolder[]>([
    { id: 'root', name: '全部收藏', parentId: null },
    { id: 'laws', name: '常用法条', parentId: 'root' },
    { id: 'cases', name: '典型案例', parentId: 'root' },
    { id: 'documents', name: '生成的文书', parentId: 'root' },
  ]);
  const [items, setItems] = useState<FavoriteItem[]>([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    loadFavorites();
  }, []);

  const loadFavorites = () => {
    try {
      setLoading(true);
      const saved = localStorage.getItem(FAVORITES_STORAGE_KEY);
      if (saved) {
        const data = JSON.parse(saved);
        setFolders(data.folders || folders);
        setItems(
          (data.items || []).map((item: any) => ({
            ...item,
            createdAt: new Date(item.createdAt),
            updatedAt: new Date(item.updatedAt),
          }))
        );
      }
    } catch (error) {
      console.error('加载收藏夹失败:', error);
      message.error('加载收藏夹失败');
    } finally {
      setLoading(false);
    }
  };

  const saveFavorites = (newFolders: FavoriteFolder[], newItems: FavoriteItem[]) => {
    try {
      const data = {
        folders: newFolders,
        items: newItems,
      };
      localStorage.setItem(FAVORITES_STORAGE_KEY, JSON.stringify(data));
      setFolders(newFolders);
      setItems(newItems);
    } catch (error) {
      console.error('保存收藏夹失败:', error);
      message.error('保存收藏夹失败');
    }
  };

  const addFolder = (name: string, parentId: string = 'root') => {
    const newFolder: FavoriteFolder = {
      id: Date.now().toString(),
      name,
      parentId,
    };
    saveFavorites([...folders, newFolder], items);
    message.success('文件夹创建成功');
  };

  const renameFolder = (id: string, name: string) => {
    const newFolders = folders.map(f =>
      f.id === id ? { ...f, name } : f
    );
    saveFavorites(newFolders, items);
    message.success('文件夹重命名成功');
  };

  const deleteFolder = (id: string) => {
    // 递归删除子文件夹
    const deleteFolderRecursive = (folderId: string) => {
      const childFolders = folders.filter(f => f.parentId === folderId);
      childFolders.forEach(cf => deleteFolderRecursive(cf.id));
    };

    deleteFolderRecursive(id);

    // 删除文件夹及其所有子文件夹
    const newFolders = folders.filter(f => {
      if (f.id === id) return false;
      // 检查是否是子文件夹
      let currentFolder = f;
      while (currentFolder.parentId) {
        if (currentFolder.parentId === id) return false;
        currentFolder = folders.find(fo => fo.id === currentFolder.parentId)!;
      }
      return true;
    });

    // 删除文件夹下的所有项目
    const deleteFolderItems = (folderId: string) => {
      const childFolders = folders.filter(f => f.parentId === folderId);
      childFolders.forEach(cf => deleteFolderItems(cf.id));
    };

    const itemsToDelete: string[] = [id];
    deleteFolderItems(id);

    const newItems = items.filter(item => !itemsToDelete.includes(item.folderId));
    saveFavorites(newFolders, newItems);
    message.success('文件夹删除成功');
  };

  const addItem = (item: Omit<FavoriteItem, 'id' | 'createdAt' | 'updatedAt'>) => {
    const newItem: FavoriteItem = {
      ...item,
      id: Date.now().toString(),
      createdAt: new Date(),
      updatedAt: new Date(),
    };
    saveFavorites(folders, [...items, newItem]);
    message.success('收藏成功');
  };

  const updateItem = (id: string, updates: Partial<FavoriteItem>) => {
    const newItems = items.map(i =>
      i.id === id ? { ...i, ...updates, updatedAt: new Date() } : i
    );
    saveFavorites(folders, newItems);
  };

  const deleteItem = (id: string) => {
    const newItems = items.filter(i => i.id !== id);
    saveFavorites(folders, newItems);
    message.success('已取消收藏');
  };

  const moveItem = (itemId: string, targetFolderId: string) => {
    const newItems = items.map(i =>
      i.id === itemId ? { ...i, folderId: targetFolderId, updatedAt: new Date() } : i
    );
    saveFavorites(folders, newItems);
    message.success('移动成功');
  };

  const searchItems = (keyword: string, folderId?: string) => {
    let filteredItems = items;

    if (folderId && folderId !== 'root') {
      // 获取文件夹及其所有子文件夹
      const getFolderIds = (fid: string): string[] => {
        const ids = [fid];
        folders.filter(f => f.parentId === fid).forEach(f => {
          ids.push(...getFolderIds(f.id));
        });
        return ids;
      };

      const folderIds = getFolderIds(folderId);
      filteredItems = items.filter(i => folderIds.includes(i.folderId));
    }

    if (keyword.trim()) {
      const lowerKeyword = keyword.toLowerCase();
      filteredItems = filteredItems.filter(i =>
        i.title.toLowerCase().includes(lowerKeyword) ||
        i.content.toLowerCase().includes(lowerKeyword) ||
        i.tags.some(t => t.toLowerCase().includes(lowerKeyword))
      );
    }

    return filteredItems;
  };

  const getItemsByFolder = (folderId: string) => {
    return items.filter(i => i.folderId === folderId);
  };

  const getItemsByType = (type: FavoriteItem['type']) => {
    return items.filter(i => i.type === type);
  };

  return {
    folders,
    items,
    loading,
    addFolder,
    renameFolder,
    deleteFolder,
    addItem,
    updateItem,
    deleteItem,
    moveItem,
    searchItems,
    getItemsByFolder,
    getItemsByType,
    loadFavorites,
  };
};

interface FavoritesManagerProps {
  visible: boolean;
  onClose: () => void;
}

const FavoritesManager: React.FC<FavoritesManagerProps> = ({ visible, onClose }) => {
  const {
    folders,
    items,
    addFolder,
    renameFolder,
    deleteFolder,
    addItem,
    deleteItem,
    moveItem,
    searchItems,
    getItemsByFolder,
  } = useFavorites();

  const [selectedFolderId, setSelectedFolderId] = useState<string>('root');
  const [searchKeyword, setSearchKeyword] = useState('');
  const [newFolderModalVisible, setNewFolderModalVisible] = useState(false);
  const [newFolderName, setNewFolderName] = useState('');
  const [addItemModalVisible, setAddItemModalVisible] = useState(false);
  const [newItemTitle, setNewItemTitle] = useState('');
  const [newItemContent, setNewItemContent] = useState('');
  const [newItemTags, setNewItemTags] = useState<string[]>([]);
  const [inputTag, setInputTag] = useState('');

  const filteredItems = searchItems(searchKeyword, selectedFolderId);

  const handleAddFolder = () => {
    if (!newFolderName.trim()) {
      message.warning('请输入文件夹名称');
      return;
    }
    addFolder(newFolderName, selectedFolderId);
    setNewFolderName('');
    setNewFolderModalVisible(false);
  };

  const handleAddItem = () => {
    if (!newItemTitle.trim()) {
      message.warning('请输入标题');
      return;
    }
    addItem({
      title: newItemTitle,
      type: 'law',
      content: newItemContent,
      folderId: selectedFolderId === 'root' ? 'laws' : selectedFolderId,
      tags: newItemTags,
    });
    setNewItemTitle('');
    setNewItemContent('');
    setNewItemTags([]);
    setAddItemModalVisible(false);
  };

  const handleAddTag = () => {
    if (inputTag && !newItemTags.includes(inputTag)) {
      setNewItemTags([...newItemTags, inputTag]);
      setInputTag('');
    }
  };

  const handleRemoveTag = (tagToRemove: string) => {
    setNewItemTags(newItemTags.filter(tag => tag !== tagToRemove));
  };

  const buildTreeData = () => {
    const buildNode = (folderId: string): any => {
      const folder = folders.find(f => f.id === folderId);
      if (!folder) return null;

      return {
        title: folder.name,
        key: folder.id,
        icon: selectedFolderId === folderId ? <FolderOpenOutlined /> : <FolderOutlined />,
        children: folders
          .filter(f => f.parentId === folderId)
          .map(f => buildNode(f.id))
          .filter(Boolean),
      };
    };

    return [buildNode('root')].filter(Boolean);
  };

  const treeData = buildTreeData();

  return (
    <Modal
      title={
        <div className="favorites-modal-title">
          <StarFilled className="title-icon" />
          <span>收藏夹管理</span>
        </div>
      }
      open={visible}
      onCancel={onClose}
      footer={null}
      width={900}
      className="favorites-modal"
    >
      <div className="favorites-content">
        {/* 左侧文件夹树 */}
        <div className="favorites-sidebar">
          <div className="sidebar-header">
            <Search
              placeholder="搜索收藏..."
              allowClear
              value={searchKeyword}
              onChange={(e) => setSearchKeyword(e.target.value)}
              className="favorites-search"
            />
            <Button
              type="primary"
              icon={<PlusOutlined />}
              onClick={() => setNewFolderModalVisible(true)}
              className="add-folder-button"
            >
              新建文件夹
            </Button>
          </div>
          <Tree
            showIcon
            treeData={treeData}
            selectedKeys={[selectedFolderId]}
            onSelect={(keys) => setSelectedFolderId(keys[0] as string)}
            className="folder-tree"
          />
        </div>

        {/* 右侧内容列表 */}
        <div className="favorites-main">
          <div className="main-header">
            <Space>
              <Button
                icon={<PlusOutlined />}
                onClick={() => setAddItemModalVisible(true)}
              >
                添加收藏
              </Button>
            </Space>
          </div>
          <div className="items-list">
            {filteredItems.length > 0 ? (
              filteredItems.map(item => (
                <div key={item.id} className="favorite-item">
                  <div className="item-header">
                    <FileTextOutlined className="item-type-icon" />
                    <span className="item-title">{item.title}</span>
                    <Space>
                      <Select
                        value={item.folderId}
                        size="small"
                        style={{ width: 120 }}
                        onChange={(folderId) => moveItem(item.id, folderId)}
                      >
                        {folders.filter(f => f.id !== 'root').map(f => (
                          <Option key={f.id} value={f.id}>{f.name}</Option>
                        ))}
                      </Select>
                      <Popconfirm
                        title="确认删除"
                        description="确定要删除这个收藏吗？"
                        onConfirm={() => deleteItem(item.id)}
                        okText="删除"
                        cancelText="取消"
                      >
                        <Button
                          type="text"
                          size="small"
                          danger
                          icon={<DeleteOutlined />}
                        />
                      </Popconfirm>
                    </Space>
                  </div>
                  <div className="item-content">{item.content}</div>
                  <div className="item-footer">
                    <Space size={[4, 8]} wrap>
                      {item.tags.map(tag => (
                        <Tag key={tag} color="blue">{tag}</Tag>
                      ))}
                    </Space>
                    <span className="item-date">
                      {item.createdAt.toLocaleDateString('zh-CN')}
                    </span>
                  </div>
                </div>
              ))
            ) : (
              <Empty description="暂无收藏" />
            )}
          </div>
        </div>
      </div>

      {/* 新建文件夹模态框 */}
      <Modal
        title="新建文件夹"
        open={newFolderModalVisible}
        onOk={handleAddFolder}
        onCancel={() => {
          setNewFolderModalVisible(false);
          setNewFolderName('');
        }}
      >
        <Input
          placeholder="请输入文件夹名称"
          value={newFolderName}
          onChange={(e) => setNewFolderName(e.target.value)}
          onPressEnter={handleAddFolder}
        />
      </Modal>

      {/* 添加收藏模态框 */}
      <Modal
        title="添加收藏"
        open={addItemModalVisible}
        onOk={handleAddItem}
        onCancel={() => {
          setAddItemModalVisible(false);
          setNewItemTitle('');
          setNewItemContent('');
          setNewItemTags([]);
        }}
        width={600}
      >
        <Space direction="vertical" style={{ width: '100%' }} size="middle">
          <Input
            placeholder="标题"
            value={newItemTitle}
            onChange={(e) => setNewItemTitle(e.target.value)}
          />
          <Input.TextArea
            placeholder="内容"
            value={newItemContent}
            onChange={(e) => setNewItemContent(e.target.value)}
            autoSize={{ minRows: 4, maxRows: 8 }}
          />
          <div>
            <Space size={8} wrap>
              {newItemTags.map(tag => (
                <Tag
                  key={tag}
                  closable
                  onClose={() => handleRemoveTag(tag)}
                >
                  {tag}
                </Tag>
              ))}
              <Input
                size="small"
                style={{ width: 100 }}
                value={inputTag}
                onChange={(e) => setInputTag(e.target.value)}
                onPressEnter={handleAddTag}
                placeholder="添加标签"
              />
            </Space>
          </div>
        </Space>
      </Modal>
    </Modal>
  );
};

export default FavoritesManager;
