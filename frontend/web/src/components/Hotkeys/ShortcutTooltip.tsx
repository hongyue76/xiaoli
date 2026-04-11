import React, { useState, useEffect } from 'react';
import { Tooltip } from 'antd';
import { ShortcutDisplay } from './AppHotkeys';
import './HotkeyHelp.css';

/**
 * 带快捷键提示的 Tooltip
 */
export const ShortcutTooltip: React.FC<{
  children: React.ReactNode;
  keys: string[];
  description?: string;
  placement?: 'top' | 'bottom' | 'left' | 'right' | 'topLeft' | 'topRight' | 'bottomLeft' | 'bottomRight';
}> = ({ children, keys, description, placement = 'top' }) => {
  const [visible, setVisible] = useState(false);

  const title = (
    <div>
      {description && <div style={{ marginBottom: 4 }}>{description}</div>}
      <ShortcutDisplay keys={keys} />
    </div>
  );

  return (
    <Tooltip
      title={title}
      placement={placement}
      visible={visible}
      onVisibleChange={setVisible}
    >
      {children}
    </Tooltip>
  );
};

/**
 * 快捷键指示器 - 在按钮旁边显示快捷键
 */
export const ShortcutIndicator: React.FC<{
  keys: string[];
  style?: React.CSSProperties;
  className?: string;
}> = ({ keys, style, className }) => {
  return (
    <div 
      className={`shortcut-indicator ${className || ''}`}
      style={{
        display: 'inline-flex',
        alignItems: 'center',
        gap: 2,
        fontSize: 11,
        color: '#999',
        ...style,
      }}
    >
      <ShortcutDisplay keys={keys} compact />
    </div>
  );
};

/**
 * 快捷键徽章 - 在按钮上显示快捷键
 */
export const ShortcutBadge: React.FC<{
  keys: string[];
  position?: 'top-right' | 'top-left' | 'bottom-right' | 'bottom-left';
}> = ({ keys, position = 'top-right' }) => {
  const positionStyles: Record<string, React.CSSProperties> = {
    'top-right': { top: -6, right: -6 },
    'top-left': { top: -6, left: -6 },
    'bottom-right': { bottom: -6, right: -6 },
    'bottom-left': { bottom: -6, left: -6 },
  };

  return (
    <div
      style={{
        position: 'absolute',
        ...positionStyles[position],
        padding: '2px 6px',
        background: 'rgba(0, 0, 0, 0.7)',
        color: 'white',
        borderRadius: 4,
        fontSize: 10,
        fontWeight: 500,
        fontFamily: 'Monaco, Menlo, Courier New, monospace',
        zIndex: 10,
      }}
    >
      <ShortcutDisplay keys={keys} compact />
    </div>
  );
};
