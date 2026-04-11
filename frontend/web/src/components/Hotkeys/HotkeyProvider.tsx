import React, { useEffect, createContext, useContext, useCallback, useState } from 'react';
import { message } from 'antd';

export interface HotkeyAction {
  /** 快捷键组合，例如 'ctrl+k', 'cmd+k', 'escape', '?' */
  keys: string | string[];
  /** 动作描述 */
  description: string;
  /** 回调函数 */
  callback: (event: KeyboardEvent) => void;
  /** 是否禁用 */
  disabled?: boolean;
  /** 优先级，数字越大优先级越高 */
  priority?: number;
}

export interface HotkeyContextType {
  /** 注册快捷键 */
  registerHotkey: (action: HotkeyAction) => () => void;
  /** 所有已注册的快捷键 */
  hotkeys: HotkeyAction[];
  /** 是否显示帮助面板 */
  helpVisible: boolean;
  /** 切换帮助面板显示 */
  toggleHelp: () => void;
  /** 打开帮助面板 */
  showHelp: () => void;
  /** 关闭帮助面板 */
  hideHelp: () => void;
}

const HotkeyContext = createContext<HotkeyContextType | null>(null);

/**
 * 快捷键上下文 Provider
 */
export const HotkeyProvider: React.FC<{
  children: React.ReactNode;
  /** 是否启用快捷键 */
  enabled?: boolean;
}> = ({ children, enabled = true }) => {
  const [hotkeys, setHotkeys] = useState<HotkeyAction[]>([]);
  const [helpVisible, setHelpVisible] = useState(false);

  const registerHotkey = useCallback((action: HotkeyAction) => {
    const id = Date.now() + Math.random();
    setHotkeys(prev => [...prev, { ...action, id }].sort((a, b) => (b.priority || 0) - (a.priority || 0)));
    
    return () => {
      setHotkeys(prev => prev.filter(h => (h as any).id !== id));
    };
  }, []);

  const toggleHelp = useCallback(() => {
    setHelpVisible(prev => !prev);
  }, []);

  const showHelp = useCallback(() => {
    setHelpVisible(true);
  }, []);

  const hideHelp = useCallback(() => {
    setHelpVisible(false);
  }, []);

  // 全局键盘事件监听
  useEffect(() => {
    if (!enabled) return;

    const handleKeyDown = (event: KeyboardEvent) => {
      // 忽略在输入框中的快捷键
      const target = event.target as HTMLElement;
      const isInput = target.tagName === 'INPUT' ||
                     target.tagName === 'TEXTAREA' ||
                     target.isContentEditable;

      // 获取按下的键
      const pressedKey = event.key.toLowerCase();
      const modifierKey = event.ctrlKey ? 'ctrl' : event.metaKey ? 'cmd' : '';
      const altKey = event.altKey ? 'alt' : '';
      const shiftKey = event.shiftKey ? 'shift' : '';

      // 构建快捷键字符串
      const pressedCombo = [modifierKey, altKey, shiftKey, pressedKey]
        .filter(Boolean)
        .join('+');

      // 遍历所有注册的快捷键
      for (const hotkey of hotkeys) {
        if (hotkey.disabled) continue;

        const keys = Array.isArray(hotkey.keys) ? hotkey.keys : [hotkey.keys];
        const keyCombos = keys.map(key => key.toLowerCase().replace(/ /g, '+'));

        // 检查是否匹配
        if (keyCombos.includes(pressedCombo)) {
          // 如果在输入框中，只有 Escape 可以触发
          if (isInput && pressedCombo !== 'escape') {
            continue;
          }

          event.preventDefault();
          hotkey.callback(event);
          return;
        }
      }
    };

    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [enabled, hotkeys]);

  const value: HotkeyContextType = {
    registerHotkey,
    hotkeys,
    helpVisible,
    toggleHelp,
    showHelp,
    hideHelp,
  };

  return (
    <HotkeyContext.Provider value={value}>
      {children}
    </HotkeyContext.Provider>
  );
};

/**
 * 使用快捷键 Hook
 */
export const useHotkeys = () => {
  const context = useContext(HotkeyContext);
  if (!context) {
    throw new Error('useHotkeys must be used within HotkeyProvider');
  }
  return context;
};

/**
 * 注册快捷键 Hook
 */
export const useHotkey = (action: HotkeyAction, deps: React.DependencyList = []) => {
  const context = useContext(HotkeyContext);
  
  useEffect(() => {
    if (!context) return;
    if (action.disabled) return;
    
    return context.registerHotkey(action);
  }, [context, action, ...deps]);
};

/**
 * 快捷键帮助面板组件
 */
import { Modal, List, Tag, Typography } from 'antd';
import { KeyboardOutlined } from '@ant-design/icons';
import './HotkeyHelp.css';

const { Text } = Typography;

export const HotkeyHelp: React.FC = () => {
  const { helpVisible, hideHelp, hotkeys } = useHotkeys();

  // 按类别分组快捷键
  const groupedHotkeys = hotkeys.reduce((acc, hotkey) => {
    const keys = Array.isArray(hotkey.keys) ? hotkey.keys : [hotkey.keys];
    const keyString = keys[0].toUpperCase();
    
    if (!acc[keyString]) {
      acc[keyString] = [];
    }
    acc[keyString].push(hotkey);
    
    return acc;
  }, {} as Record<string, HotkeyAction[]>);

  const formatKey = (key: string) => {
    return key
      .replace(/ctrl/g, 'Ctrl')
      .replace(/cmd/g, '⌘')
      .replace(/alt/g, 'Alt')
      .replace(/shift/g, 'Shift')
      .replace(/escape/g, 'Esc')
      .replace(/\+/g, ' + ');
  };

  return (
    <Modal
      title={
        <span style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
          <KeyboardOutlined />
          快捷键帮助
        </span>
      }
      open={helpVisible}
      onCancel={hideHelp}
      footer={null}
      width={600}
    >
      <List
        dataSource={Object.entries(groupedHotkeys)}
        renderItem(([key, actions]) => (
          <List.Item className="hotkey-help-item">
            <div style={{ display: 'flex', alignItems: 'center', gap: 16, width: '100%' }}>
              <Tag color="blue" className="hotkey-tag">
                {formatKey(key)}
              </Tag>
              <Text type="secondary">
                {actions.map(a => a.description).join(' / ')}
              </Text>
            </div>
          </List.Item>
        ))}
      </List>
      
      <div style={{ marginTop: 24, textAlign: 'center', color: '#999' }}>
        <Text type="secondary">
          💡 提示：在输入框中，除了 Esc 外的快捷键会被禁用
        </Text>
      </div>
    </Modal>
  );
};
