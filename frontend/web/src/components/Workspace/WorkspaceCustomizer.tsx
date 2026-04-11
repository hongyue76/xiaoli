import { useState, useEffect } from 'react';
import { Card, Switch, Space, Button, Empty, message } from 'antd';
import {
  StarOutlined,
  ArrowUpOutlined,
  ArrowDownOutlined,
  SettingOutlined,
  AppstoreOutlined,
} from '@ant-design/icons';
import './WorkspaceCustomizer.css';

export interface WorkspaceModule {
  id: string;
  title: string;
  icon: string;
  enabled: boolean;
  pinned: boolean;
  order: number;
  category: 'consult' | 'document' | 'case' | 'tool';
}

const WORKSPACE_STORAGE_KEY = 'workspace_settings';

const DEFAULT_MODULES: WorkspaceModule[] = [
  {
    id: 'ai-consult',
    title: 'AI 咨询',
    icon: '🤖',
    enabled: true,
    pinned: true,
    order: 0,
    category: 'consult',
  },
  {
    id: 'case-analysis',
    title: '案例分析',
    icon: '📊',
    enabled: true,
    pinned: false,
    order: 1,
    category: 'consult',
  },
  {
    id: 'document-gen',
    title: '文书生成',
    icon: '📝',
    enabled: true,
    pinned: true,
    order: 2,
    category: 'document',
  },
  {
    id: 'case-search',
    title: '案例检索',
    icon: '🔍',
    enabled: true,
    pinned: false,
    order: 3,
    category: 'case',
  },
  {
    id: 'law-search',
    title: '法条检索',
    icon: '📚',
    enabled: true,
    pinned: false,
    order: 4,
    category: 'tool',
  },
  {
    id: 'favorites',
    title: '收藏夹',
    icon: '⭐',
    enabled: true,
    pinned: false,
    order: 5,
    category: 'tool',
  },
  {
    id: 'history',
    title: '历史记录',
    icon: '📜',
    enabled: true,
    pinned: false,
    order: 6,
    category: 'tool',
  },
  {
    id: 'statistics',
    title: '数据统计',
    icon: '📈',
    enabled: false,
    pinned: false,
    order: 7,
    category: 'tool',
  },
];

export const useWorkspaceCustomizer = () => {
  const [modules, setModules] = useState<WorkspaceModule[]>(DEFAULT_MODULES);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    loadModules();
  }, []);

  const loadModules = () => {
    try {
      setLoading(true);
      const saved = localStorage.getItem(WORKSPACE_STORAGE_KEY);
      if (saved) {
        const parsed = JSON.parse(saved);
        setModules(parsed);
      }
    } catch (error) {
      console.error('加载工作台设置失败:', error);
      message.error('加载工作台设置失败');
    } finally {
      setLoading(false);
    }
  };

  const saveModules = (newModules: WorkspaceModule[]) => {
    try {
      localStorage.setItem(WORKSPACE_STORAGE_KEY, JSON.stringify(newModules));
      setModules(newModules);
    } catch (error) {
      console.error('保存工作台设置失败:', error);
      message.error('保存工作台设置失败');
    }
  };

  const toggleModule = (id: string) => {
    const newModules = modules.map(m =>
      m.id === id ? { ...m, enabled: !m.enabled } : m
    );
    saveModules(newModules);
    message.success('模块状态已更新');
  };

  const togglePin = (id: string) => {
    const newModules = modules.map(m =>
      m.id === id ? { ...m, pinned: !m.pinned } : m
    );
    saveModules(newModules);
    message.success('置顶状态已更新');
  };

  const moveUp = (id: string) => {
    const index = modules.findIndex(m => m.id === id);
    if (index <= 0) return;

    const newModules = [...modules];
    [newModules[index - 1], newModules[index]] = [newModules[index], newModules[index - 1]];
    newModules[index - 1].order = index - 1;
    newModules[index].order = index;

    saveModules(newModules);
    message.success('顺序已调整');
  };

  const moveDown = (id: string) => {
    const index = modules.findIndex(m => m.id === id);
    if (index >= modules.length - 1) return;

    const newModules = [...modules];
    [newModules[index], newModules[index + 1]] = [newModules[index + 1], newModules[index]];
    newModules[index].order = index;
    newModules[index + 1].order = index + 1;

    saveModules(newModules);
    message.success('顺序已调整');
  };

  const getEnabledModules = () => {
    return modules.filter(m => m.enabled).sort((a, b) => a.order - b.order);
  };

  const getPinnedModules = () => {
    return modules
      .filter(m => m.enabled && m.pinned)
      .sort((a, b) => a.order - b.order);
  };

  const getModulesByCategory = (category: WorkspaceModule['category']) => {
    return modules.filter(m => m.category === category && m.enabled);
  };

  const resetModules = () => {
    saveModules(DEFAULT_MODULES);
    message.success('工作台已重置');
  };

  return {
    modules,
    loading,
    toggleModule,
    togglePin,
    moveUp,
    moveDown,
    getEnabledModules,
    getPinnedModules,
    getModulesByCategory,
    resetModules,
    loadModules,
  };
};

interface WorkspaceCustomizerProps {
  visible: boolean;
  onClose: () => void;
}

const WorkspaceCustomizer: React.FC<WorkspaceCustomizerProps> = ({ visible, onClose }) => {
  const {
    modules,
    toggleModule,
    togglePin,
    moveUp,
    moveDown,
    resetModules,
  } = useWorkspaceCustomizer();

  const [viewMode, setViewMode] = useState<'all' | 'pinned'>('pinned');

  const filteredModules = viewMode === 'pinned'
    ? modules.filter(m => m.enabled && m.pinned)
    : modules;

  const getCategoryTitle = (category: WorkspaceModule['category']) => {
    const titles = {
      consult: '咨询',
      document: '文书',
      case: '案例',
      tool: '工具',
    };
    return titles[category];
  };

  const groupedModules = filteredModules.reduce((acc, module) => {
    if (!acc[module.category]) {
      acc[module.category] = [];
    }
    acc[module.category].push(module);
    return acc;
  }, {} as Record<string, WorkspaceModule[]>);

  return (
    <div className={`workspace-customizer ${visible ? 'visible' : ''}`}>
      <Card
        title={
          <div className="customizer-header">
            <AppstoreOutlined className="header-icon" />
            <span>工作台自定义</span>
            <Space>
              <Button
                size="small"
                type={viewMode === 'pinned' ? 'primary' : 'default'}
                onClick={() => setViewMode('pinned')}
              >
                置顶
              </Button>
              <Button
                size="small"
                type={viewMode === 'all' ? 'primary' : 'default'}
                onClick={() => setViewMode('all')}
              >
                全部
              </Button>
            </Space>
          </div>
        }
        bordered={false}
        className="customizer-card"
      >
        <Space direction="vertical" style={{ width: '100%' }} size="large">
          {Object.entries(groupedModules).map(([category, mods]) => (
            <div key={category} className="category-section">
              <h3 className="category-title">{getCategoryTitle(category as WorkspaceModule['category'])}</h3>
              <Space direction="vertical" style={{ width: '100%' }} size="small">
                {mods
                  .sort((a, b) => a.order - b.order)
                  .map((module, index) => (
                    <Card
                      key={module.id}
                      size="small"
                      className="module-card"
                    >
                      <div className="module-content">
                        <Space className="module-left">
                          <span className="module-icon">{module.icon}</span>
                          <span className="module-title">{module.title}</span>
                        </Space>
                        <Space className="module-actions" size={4}>
                          <Button
                            type="text"
                            size="small"
                            icon={<StarOutlined className={module.pinned ? 'active' : ''} />}
                            onClick={() => togglePin(module.id)}
                            title={module.pinned ? '取消置顶' : '置顶'}
                          />
                          <Switch
                            size="small"
                            checked={module.enabled}
                            onChange={() => toggleModule(module.id)}
                            checkedChildren="启用"
                            unCheckedChildren="禁用"
                          />
                          <Space size={2}>
                            <Button
                              type="text"
                              size="small"
                              icon={<ArrowUpOutlined />}
                              onClick={() => moveUp(module.id)}
                              disabled={index === 0}
                            />
                            <Button
                              type="text"
                              size="small"
                              icon={<ArrowDownOutlined />}
                              onClick={() => moveDown(module.id)}
                              disabled={index === mods.length - 1}
                            />
                          </Space>
                        </Space>
                      </div>
                    </Card>
                  ))}
              </Space>
            </div>
          ))}
          {filteredModules.length === 0 && (
            <Empty
              description="暂无模块"
              image={Empty.PRESENTED_IMAGE_SIMPLE}
            />
          )}
        </Space>
        <div className="customizer-footer">
          <Button
            icon={<SettingOutlined />}
            onClick={resetModules}
          >
            重置工作台
          </Button>
        </div>
      </Card>
    </div>
  );
};

export default WorkspaceCustomizer;
