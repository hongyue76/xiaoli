import React, { useState, useEffect } from 'react';
import { Card, Button, Drawer, Collapse, Badge, Avatar, Dropdown, Breadcrumb, Modal, message, Popover, Tooltip } from 'antd';
import { useNavigate, useLocation } from 'react-router-dom';
import {
  MessageOutlined,
  FileTextOutlined,
  SearchOutlined,
  HomeOutlined,
  UserOutlined,
  SettingOutlined,
  FileProtectOutlined,
  TeamOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  CloseOutlined,
  RobotOutlined,
  FileSearchOutlined,
  ThunderboltOutlined,
  BellOutlined,
  StarOutlined,
  StarFilled,
  ExperimentOutlined,
  LogoutOutlined,
  CrownOutlined,
} from '@ant-design/icons';
import './MainLayout.css';

interface MainLayoutProps {
  leftContent: React.ReactNode;
  mainContent: React.ReactNode;
  rightContent?: React.ReactNode;
}

const MainLayout: React.FC<MainLayoutProps> = ({
  leftContent,
  mainContent,
  rightContent,
}) => {
  const navigate = useNavigate();
  const location = useLocation();
  const [leftCollapsed, setLeftCollapsed] = useState(false);
  const [rightCollapsed, setRightCollapsed] = useState(false);
  const [leftDrawerVisible, setLeftDrawerVisible] = useState(false);
  const [rightDrawerVisible, setRightDrawerVisible] = useState(false);
  const [notifications, setNotifications] = useState([
    { id: 1, message: '您有一条新的法律咨询回复', time: '5分钟前', read: false },
    { id: 2, message: '案例检索结果已更新', time: '1小时前', read: false },
    { id: 3, message: '系统维护通知', time: '昨天', read: true },
  ]);
  const [notificationVisible, setNotificationVisible] = useState(false);
  const [favorites, setFavorites] = useState<string[]>([]);
  const [collapsedGroups, setCollapsedGroups] = useState<Record<string, boolean>>({});

  // 菜单分组
  const menuGroups = [
    {
      title: '咨询工具',
      icon: <MessageOutlined />,
      items: [
        { key: '/consult', icon: <MessageOutlined />, label: '法律咨询' },
        { key: '/case', icon: <SearchOutlined />, label: '案例检索' },
        { key: '/judge-profile', icon: <TeamOutlined />, label: '法官画像' },
      ]
    },
    {
      title: '文书工具',
      icon: <FileTextOutlined />,
      items: [
        { key: '/document', icon: <FileTextOutlined />, label: '文书生成' },
        { key: '/defense', icon: <FileProtectOutlined />, label: '答辩书编辑' },
        { key: '/contract', icon: <FileTextOutlined />, label: '合同审查' },
      ]
    },
    {
      title: '分析工具',
      icon: <ThunderboltOutlined />,
      items: [
        { key: '/analysis', icon: <ThunderboltOutlined />, label: '案件分析' },
        { key: '/evidence', icon: <FileSearchOutlined />, label: '证据分析' },
        { key: '/compliance', icon: <FileProtectOutlined />, label: '企业合规' },
      ]
    },
    {
      title: '系统功能',
      icon: <ExperimentOutlined />,
      items: [
        { key: '/intent-router', icon: <ExperimentOutlined />, label: '智能路由' },
      ]
    },
  ];

  // 扁平化菜单项用于导航
  const menuItems = menuGroups.flatMap(group => group.items);

  const toggleFavorite = (key: string) => {
    setFavorites(prev => {
      if (prev.includes(key)) {
        message.success('已取消收藏');
        return prev.filter(k => k !== key);
      } else {
        message.success('已添加到快捷收藏');
        return [...prev, key];
      }
    });
  };

  // 面包屑路径映射
  const getBreadcrumbItems = () => {
    const pathMap: Record<string, string> = {
      '/': '首页',
      '/consult': '法律咨询',
      '/document': '文书生成',
      '/defense': '答辩书编辑',
      '/case': '案例检索',
      '/judge-profile': '法官画像',
      '/contract': '合同审查',
      '/analysis': '案件分析',
      '/evidence': '证据分析',
      '/compliance': '企业合规',
      '/intent-router': '智能路由',
    };
    return pathMap[location.pathname] || '其他';
  };

  const toggleGroupCollapse = (groupTitle: string) => {
    setCollapsedGroups(prev => ({
      ...prev,
      [groupTitle]: !prev[groupTitle]
    }));
  };

  const handleNotificationClick = (id: number) => {
    setNotifications(prev =>
      prev.map(notif =>
        notif.id === id ? { ...notif, read: true } : notif
      )
    );
  };

  const clearNotifications = () => {
    setNotifications([]);
    message.success('已清除所有通知');
  };

  const userMenuItems = [
    {
      key: 'profile',
      icon: <UserOutlined />,
      label: '个人中心',
    },
    {
      key: 'settings',
      icon: <SettingOutlined />,
      label: '设置',
    },
    {
      type: 'divider' as const,
    },
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: '退出登录',
      danger: true,
    },
  ];

  const rightMenuItems = [
    {
      key: 'ai-chat',
      icon: <RobotOutlined />,
      label: 'AI 对话',
      defaultOpen: true,
    },
    {
      key: 'history',
      icon: <FileTextOutlined />,
      label: '历史记录',
    },
    {
      key: 'templates',
      icon: <FileTextOutlined />,
      label: '文档模板',
    },
  ];

  return (
    <div className="main-layout">
      {/* 顶部导航栏 */}
      <header className="main-header">
        <div className="header-left">
          <Button
            type="text"
            icon={leftCollapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
            onClick={() => setLeftCollapsed(!leftCollapsed)}
            className="collapse-btn"
          />
          <div className="logo-wrapper">
            <CrownOutlined className="logo-icon" />
            <h1 className="logo">律法先锋</h1>
          </div>
        </div>

        <div className="header-center">
          <Breadcrumb className="header-breadcrumb" items={[
            { title: <HomeOutlined /> },
            { title: getBreadcrumbItems() }
          ]} />
        </div>

        <div className="header-right">
          <Popover
            placement="bottomRight"
            title={
              <div className="notification-header">
                <span>通知</span>
                <Button
                  type="link"
                  size="small"
                  onClick={clearNotifications}
                  disabled={notifications.length === 0}
                >
                  全部已读
                </Button>
              </div>
            }
            content={
              <div className="notification-list">
                {notifications.length === 0 ? (
                  <div className="notification-empty">暂无通知</div>
                ) : (
                  notifications.map((notif) => (
                    <div
                      key={notif.id}
                      className={`notification-item ${notif.read ? 'read' : 'unread'}`}
                      onClick={() => handleNotificationClick(notif.id)}
                    >
                      <div className="notification-content">{notif.message}</div>
                      <div className="notification-time">{notif.time}</div>
                    </div>
                  ))
                )}
              </div>
            }
            trigger="click"
            open={notificationVisible}
            onOpenChange={setNotificationVisible}
          >
            <Badge count={notifications.filter(n => !n.read).length} overflowCount={99}>
              <Button type="text" icon={<BellOutlined />} className="header-icon-btn">
                通知
              </Button>
            </Badge>
          </Popover>

          <Dropdown menu={{ items: userMenuItems }} placement="bottomRight">
            <div className="user-avatar-wrapper">
              <Avatar size="default" icon={<UserOutlined />} className="user-avatar" />
              <span className="user-name">用户</span>
            </div>
          </Dropdown>
        </div>
      </header>

      {/* 主体内容区域 */}
      <div className="main-content-wrapper">
        {/* 左侧边栏 - 功能模块 */}
        {!leftCollapsed && (
          <aside className="left-sidebar">
            <div className="sidebar-header">
              <span>功能菜单</span>
              <Button
                type="text"
                size="small"
                icon={<CloseOutlined />}
                onClick={() => setLeftCollapsed(true)}
                className="close-sidebar-btn"
              />
            </div>

            {/* 快捷收藏 */}
            {favorites.length > 0 && (
              <div className="favorites-section">
                <div className="section-title">
                  <StarFilled className="section-icon" />
                  <span>快捷收藏</span>
                </div>
                <div className="favorites-list">
                  {menuItems.filter(item => favorites.includes(item.key)).map((item) => (
                    <div
                      key={item.key}
                      className={`menu-item favorite-item ${location.pathname === item.key ? 'active' : ''}`}
                      onClick={() => {
                        message.info(`正在跳转到${item.label}...`);
                        navigate(item.key);
                      }}
                    >
                      <span className="menu-icon">{item.icon}</span>
                      <span className="menu-label">{item.label}</span>
                    </div>
                  ))}
                </div>
              </div>
            )}

            {/* 分组菜单 */}
            <div className="menu-groups">
              {menuGroups.map((group) => (
                <div key={group.title} className="menu-group">
                  <div
                    className="menu-group-header"
                    onClick={() => toggleGroupCollapse(group.title)}
                  >
                    <span className="group-icon">{group.icon}</span>
                    <span className="group-title">{group.title}</span>
                    <Button
                      type="text"
                      size="small"
                      icon={collapsedGroups[group.title] ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
                      className="group-collapse-btn"
                    />
                  </div>
                  {!collapsedGroups[group.title] && (
                    <div className="menu-group-items">
                      {group.items.map((item) => (
                        <div
                          key={item.key}
                          className={`menu-item ${location.pathname === item.key ? 'active' : ''}`}
                          onClick={() => {
                            message.info(`正在跳转到${item.label}...`);
                            navigate(item.key);
                          }}
                        >
                          <span className="menu-icon">{item.icon}</span>
                          <span className="menu-label">{item.label}</span>
                          <Button
                            type="text"
                            size="small"
                            icon={favorites.includes(item.key) ? <StarFilled /> : <StarOutlined />}
                            onClick={(e) => {
                              e.stopPropagation();
                              toggleFavorite(item.key);
                            }}
                            className={`favorite-btn ${favorites.includes(item.key) ? 'favorited' : ''}`}
                          />
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              ))}
            </div>
          </aside>
        )}

        {/* 中间主内容区域 */}
        <main className="main-center">
          <div className="center-content">
            {mainContent}
          </div>
        </main>

        {/* 右侧边栏 - AI对话和其他工具 */}
        {!rightCollapsed && (
          <aside className="right-sidebar">
            <div className="sidebar-header">
              <div className="sidebar-title">
                <RobotOutlined className="ai-icon-small" />
                <span>AI 助手</span>
              </div>
              <div className="header-actions">
                <Button
                  type="text"
                  size="small"
                  icon={<MenuFoldOutlined />}
                  onClick={() => setRightCollapsed(true)}
                  className="collapse-sidebar-btn"
                />
              </div>
            </div>
            <div className="sidebar-content">
              <Collapse
                defaultActiveKey={['ai-chat']}
                ghost
                className="sidebar-collapse"
                items={[
                  {
                    key: 'ai-chat',
                    label: (
                      <div className="panel-header">
                        <RobotOutlined />
                        <span>AI 对话</span>
                      </div>
                    ),
                    children: rightContent || (
                      <div className="ai-chat-placeholder">
                        <RobotOutlined className="ai-icon" />
                        <p>AI 助手准备就绪</p>
                        <p className="hint">您可以随时与AI助手对话</p>
                      </div>
                    )
                  },
                  {
                    key: 'history',
                    label: (
                      <div className="panel-header">
                        <FileTextOutlined />
                        <span>历史记录</span>
                      </div>
                    ),
                    children: (
                      <div className="history-list">
                        <p className="empty-hint">暂无历史记录</p>
                      </div>
                    )
                  },
                  {
                    key: 'templates',
                    label: (
                      <div className="panel-header">
                        <FileTextOutlined />
                        <span>文档模板</span>
                      </div>
                    ),
                    children: (
                      <div className="template-list">
                        <p className="empty-hint">暂无模板</p>
                      </div>
                    )
                  }
                ]}
              />
            </div>
          </aside>
        )}
      </div>

      {/* 左侧抽屉 - 收起时显示 */}
      <Drawer
        title={
          <div className="drawer-title">
            <span>功能菜单</span>
          </div>
        }
        placement="left"
        open={leftDrawerVisible}
        onClose={() => setLeftDrawerVisible(false)}
        width={280}
        styles={{
          body: { padding: 0 },
        }}
      >
        <div className="drawer-content">
          {menuGroups.map((group) => (
            <div key={group.title} className="drawer-menu-group">
              <div className="drawer-group-header">
                <span className="group-icon">{group.icon}</span>
                <span className="group-title">{group.title}</span>
              </div>
              <div className="drawer-menu-list">
                {group.items.map((item) => (
                  <div
                    key={item.key}
                    className={`drawer-menu-item ${location.pathname === item.key ? 'active' : ''}`}
                    onClick={() => {
                      navigate(item.key);
                      setLeftDrawerVisible(false);
                    }}
                  >
                    <span className="menu-icon">{item.icon}</span>
                    <span className="menu-label">{item.label}</span>
                  </div>
                ))}
              </div>
            </div>
          ))}
        </div>
      </Drawer>

      {/* 右侧抽屉 - 收起时显示 */}
      <Drawer
        title="AI助手"
        placement="right"
        open={rightDrawerVisible}
        onClose={() => setRightDrawerVisible(false)}
        width={300}
        styles={{
          body: { padding: 0 },
        }}
      >
        <Collapse
          defaultActiveKey={['ai-chat']}
          ghost
          items={rightMenuItems.map((item) => ({
            key: item.key,
            label: (
              <div className="panel-header">
                {item.icon}
                <span>{item.label}</span>
              </div>
            ),
            children: (
              <div className="drawer-content">
                <p className="empty-hint">暂无内容</p>
              </div>
            )
          }))}
        />
      </Drawer>

      {/* 左侧收起时的浮动按钮 */}
      {leftCollapsed && (
        <Button
          type="primary"
          icon={<MenuUnfoldOutlined />}
          onClick={() => setLeftDrawerVisible(true)}
          className="left-float-btn"
        >
          菜单
        </Button>
      )}

      {/* 右侧收起时的浮动按钮 */}
      {rightCollapsed && (
        <Button
          type="primary"
          icon={<MenuUnfoldOutlined />}
          onClick={() => setRightDrawerVisible(true)}
          className="right-float-btn"
        >
          AI
        </Button>
      )}
    </div>
  );
};

export default MainLayout;

// 导出子组件供其他页面使用
export { default as ContentCard } from './ContentCard';
export { default as ResponsiveGrid } from './ResponsiveGrid';
