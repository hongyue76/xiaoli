import { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useHotkey, useHotkeys } from '@/components/Hotkeys';
import { message, Modal } from 'antd';
import { SearchOutlined, PlusOutlined, SaveOutlined } from '@ant-design/icons';

/**
 * 应用级快捷键
 */
export const AppHotkeys: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [modals, setModals] = useState<{ [key: string]: boolean }>({});
  const [searchVisible, setSearchVisible] = useState(false);

  // 显示搜索快捷键
  useHotkey({
    keys: ['ctrl+k', 'cmd+k'],
    description: '打开搜索',
    priority: 10,
    callback: () => {
      console.log('打开搜索');
      // 触发全局搜索事件，其他组件可以监听
      window.dispatchEvent(new CustomEvent('open-search'));
      message.info('按 Ctrl + K 打开搜索');
    },
  });

  // 新建咨询快捷键
  useHotkey({
    keys: ['ctrl+n', 'cmd+n'],
    description: '新建咨询',
    priority: 10,
    callback: () => {
      console.log('新建咨询');
      navigate('/consult');
      message.success('已跳转到咨询页面');
    },
  });

  // 保存草稿快捷键
  useHotkey({
    keys: ['ctrl+s', 'cmd+s'],
    description: '保存草稿',
    priority: 10,
    callback: (event) => {
      console.log('保存草稿');
      // 触发保存草稿事件
      window.dispatchEvent(new CustomEvent('save-draft'));
      message.success('草稿已保存');
    },
  });

  // 关闭弹窗快捷键（Esc）
  useHotkey({
    keys: ['escape'],
    description: '关闭弹窗',
    priority: 5,
    callback: () => {
      console.log('关闭弹窗');
      // 关闭所有 Ant Design Modal
      const modals = document.querySelectorAll('.ant-modal-root');
      const closeButtons = document.querySelectorAll('.ant-modal-close');
      
      if (closeButtons.length > 0) {
        (closeButtons[0] as HTMLElement).click();
      }
      
      // 触发自定义关闭事件
      window.dispatchEvent(new CustomEvent('close-modal'));
    },
  });

  // 查看快捷键帮助
  const { showHelp } = useHotkeys();
  
  useHotkey({
    keys: ['?'],
    description: '查看快捷键帮助',
    priority: 20,
    callback: () => {
      console.log('快捷键帮助');
      showHelp();
    },
  });

  // 返回首页
  useHotkey({
    keys: ['ctrl+h', 'cmd+h'],
    description: '返回首页',
    priority: 5,
    callback: () => {
      console.log('返回首页');
      navigate('/');
    },
  });

  // 刷新页面
  useHotkey({
    keys: ['ctrl+r', 'cmd+r'],
    description: '刷新页面',
    priority: 3,
    callback: () => {
      console.log('刷新页面');
      window.location.reload();
    },
  });

  return null;
};

/**
 * 快捷键提示组件
 */
export const ShortcutHint: React.FC<{
  keys: string[];
  description: string;
  direction?: 'top' | 'bottom' | 'left' | 'right';
}> = ({ keys, description, direction = 'top' }) => {
  const formatKey = (key: string) => {
    return key
      .replace(/ctrl/g, 'Ctrl')
      .replace(/cmd/g, '⌘')
      .replace(/alt/g, 'Alt')
      .replace(/shift/g, 'Shift')
      .replace(/escape/g, 'Esc')
      .replace(/\+/g, ' + ');
  };

  const keyString = Array.isArray(keys) ? keys.join(' / ') : keys;

  return (
    <div
      className="shortcut-hint"
      data-tooltip={description}
      style={{
        position: 'relative',
        display: 'inline-flex',
        alignItems: 'center',
        gap: 4,
      }}
    >
      {Array.isArray(keys) ? keys.map((key, index) => (
        <React.Fragment key={index}>
          <span className="shortcut-key">
            {formatKey(key)}
          </span>
          {index < keys.length - 1 && <span> + </span>}
        </React.Fragment>
      )) : (
        <span className="shortcut-key">
          {formatKey(keys)}
        </span>
      )}
    </div>
  );
};

/**
 * 快捷键显示组件
 */
export const ShortcutDisplay: React.FC<{
  keys: string[];
  compact?: boolean;
}> = ({ keys, compact = false }) => {
  const formatKey = (key: string) => {
    return key
      .replace(/ctrl/g, 'Ctrl')
      .replace(/cmd/g, '⌘')
      .replace(/alt/g, 'Alt')
      .replace(/shift/g, 'Shift')
      .replace(/escape/g, 'Esc')
      .replace(/\+/g, '');
  };

  const keyList = Array.isArray(keys) ? keys[0].split('+') : [keys];

  return (
    <span className={`shortcut-combo ${compact ? 'compact' : ''}`}>
      {keyList.map((key, index) => (
        <span key={index} className={`shortcut-key ${key.length > 1 ? 'modifier' : ''}`}>
          {formatKey(key)}
        </span>
      ))}
    </span>
  );
};

export default AppHotkeys;
