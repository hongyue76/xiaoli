import React from 'react';
import { Button, Dropdown, Space } from 'antd';
import {
  SunOutlined,
  MoonOutlined,
  BgColorsOutlined,
  CheckOutlined,
} from '@ant-design/icons';
import { useTheme } from '../../hooks/useTheme';
import './ThemeToggle.css';

const ThemeToggle: React.FC = () => {
  const { theme, setTheme, toggleTheme } = useTheme();

  const themes = [
    { key: 'light', label: '浅色模式', icon: <SunOutlined /> },
    { key: 'dark', label: '深色模式', icon: <MoonOutlined /> },
    { key: 'auto', label: '跟随系统', icon: <BgColorsOutlined /> },
  ];

  const currentThemeIcon = theme === 'dark' ? <MoonOutlined /> : <SunOutlined />;

  const menuItems = themes.map(t => ({
    key: t.key,
    label: (
      <Space className="theme-menu-item">
        {t.icon}
        <span>{t.label}</span>
        {theme === t.key && <CheckOutlined className="check-icon" />}
      </Space>
    ),
    onClick: () => setTheme(t.key as any),
  }));

  return (
    <Dropdown
      menu={{ items: menuItems }}
      placement="bottomRight"
      trigger={['click']}
    >
      <Button
        type="text"
        icon={currentThemeIcon}
        className="theme-toggle-button"
        onClick={toggleTheme}
      >
        切换主题
      </Button>
    </Dropdown>
  );
};

export default ThemeToggle;
