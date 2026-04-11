import React, { useState, useEffect } from 'react';
import { Card, Button, Input, Modal, Form, Row, Col, Divider, Space, Typography, List, Tag } from 'antd';
import { SearchOutlined, PlusOutlined, SaveOutlined, CloseOutlined, KeyboardOutlined, HomeOutlined, ReloadOutlined } from '@ant-design/icons';
import { useHotkey, useHotkeys } from '@/components/Hotkeys';
import { ShortcutDisplay, ShortcutIndicator } from '@/components/Hotkeys/AppHotkeys';
import { ShortcutTooltip } from '@/components/Hotkeys/ShortcutTooltip';

const { Title, Text, Paragraph } = Typography;

export default function HotkeysDemoPage() {
  const [searchModalVisible, setSearchModalVisible] = useState(false);
  const [consultModalVisible, setConsultModalVisible] = useState(false);
  const [draftSaved, setDraftSaved] = useState(false);
  const [savedContent, setSavedContent] = useState('');
  const [form] = Form.useForm();
  const { hotkeys } = useHotkeys();

  // 搜索功能
  useHotkey({
    keys: ['ctrl+k', 'cmd+k'],
    description: '打开搜索',
    priority: 15,
    callback: () => {
      setSearchModalVisible(true);
    },
  });

  // 新建咨询
  useHotkey({
    keys: ['ctrl+n', 'cmd+n'],
    description: '新建咨询',
    priority: 15,
    callback: () => {
      setConsultModalVisible(true);
      form.resetFields();
    },
  });

  // 保存草稿
  useHotkey({
    keys: ['ctrl+s', 'cmd+s'],
    description: '保存草稿',
    priority: 15,
    callback: () => {
      const values = form.getFieldsValue();
      setSavedContent(JSON.stringify(values, null, 2));
      setDraftSaved(true);
      setTimeout(() => setDraftSaved(false), 2000);
    },
  });

  // 关闭弹窗
  useHotkey({
    keys: ['escape'],
    description: '关闭弹窗',
    priority: 20,
    callback: () => {
      if (searchModalVisible) setSearchModalVisible(false);
      if (consultModalVisible) setConsultModalVisible(false);
    },
  });

  // 监听自定义事件
  useEffect(() => {
    const handleOpenSearch = () => setSearchModalVisible(true);
    const handleSaveDraft = () => {
      const values = form.getFieldsValue();
      setSavedContent(JSON.stringify(values, null, 2));
      setDraftSaved(true);
      setTimeout(() => setDraftSaved(false), 2000);
    };
    const handleCloseModal = () => {
      setSearchModalVisible(false);
      setConsultModalVisible(false);
    };

    window.addEventListener('open-search', handleOpenSearch);
    window.addEventListener('save-draft', handleSaveDraft);
    window.addEventListener('close-modal', handleCloseModal);

    return () => {
      window.removeEventListener('open-search', handleOpenSearch);
      window.removeEventListener('save-draft', handleSaveDraft);
      window.removeEventListener('close-modal', handleCloseModal);
    };
  }, [form]);

  return (
    <div style={{ padding: 24 }}>
      <Title level={2}>
        <KeyboardOutlined /> 快捷键演示
      </Title>
      
      <Paragraph>
        按下 <Text keyboard>?</Text> 查看所有快捷键
      </Paragraph>

      <Row gutter={[16, 16]}>
        {/* 快捷键操作区 */}
        <Col span={12}>
          <Card title="快捷键操作" style={{ height: '100%' }}>
            <Space direction="vertical" style={{ width: '100%' }} size="large">
              {/* 搜索 */}
              <div>
                <Text strong>打开搜索</Text>
                <div style={{ marginTop: 8 }}>
                  <ShortcutTooltip
                    keys={['Ctrl', 'K']}
                    description="点击或按 Ctrl+K 打开搜索"
                  >
                    <Button type="primary" icon={<SearchOutlined />} onClick={() => setSearchModalVisible(true)}>
                      打开搜索
                    </Button>
                  </ShortcutTooltip>
                  <ShortcutIndicator keys={['Ctrl', 'K']} style={{ marginLeft: 12 }} />
                </div>
              </div>

              {/* 新建咨询 */}
              <div>
                <Text strong>新建咨询</Text>
                <div style={{ marginTop: 8 }}>
                  <ShortcutTooltip
                    keys={['Ctrl', 'N']}
                    description="点击或按 Ctrl+N 新建咨询"
                  >
                    <Button type="primary" icon={<PlusOutlined />} onClick={() => {
                      setConsultModalVisible(true);
                      form.resetFields();
                    }}>
                      新建咨询
                    </Button>
                  </ShortcutTooltip>
                  <ShortcutIndicator keys={['Ctrl', 'N']} style={{ marginLeft: 12 }} />
                </div>
              </div>

              {/* 保存草稿 */}
              <div>
                <Text strong>保存草稿</Text>
                <div style={{ marginTop: 8 }}>
                  <ShortcutTooltip
                    keys={['Ctrl', 'S']}
                    description="点击或按 Ctrl+S 保存草稿"
                  >
                    <Button icon={<SaveOutlined />} onClick={() => {
                      const values = form.getFieldsValue();
                      setSavedContent(JSON.stringify(values, null, 2));
                      setDraftSaved(true);
                      setTimeout(() => setDraftSaved(false), 2000);
                    }}>
                      {draftSaved ? '已保存 ✓' : '保存草稿'}
                    </Button>
                  </ShortcutTooltip>
                  <ShortcutIndicator keys={['Ctrl', 'S']} style={{ marginLeft: 12 }} />
                </div>
              </div>

              {/* 关闭弹窗 */}
              <div>
                <Text strong>关闭弹窗</Text>
                <div style={{ marginTop: 8 }}>
                  <Text type="secondary">按 Esc 键关闭所有弹窗</Text>
                  <ShortcutIndicator keys={['Esc']} style={{ marginLeft: 12 }} />
                </div>
              </div>
            </Space>
          </Card>
        </Col>

        {/* 已注册的快捷键 */}
        <Col span={12}>
          <Card title="已注册的快捷键" style={{ height: '100%' }}>
            <List
              dataSource={hotkeys}
              renderItem={(hotkey) => {
                const keys = Array.isArray(hotkey.keys) ? hotkey.keys : [hotkey.keys];
                return (
                  <List.Item>
                    <div style={{ display: 'flex', alignItems: 'center', gap: 12, width: '100%' }}>
                      <ShortcutDisplay keys={keys} />
                      <Text style={{ flex: 1 }}>{hotkey.description}</Text>
                      {hotkey.disabled && <Tag color="red">禁用</Tag>}
                    </div>
                  </List.Item>
                );
              }}
            />
          </Card>
        </Col>
      </Row>

      <Divider />

      {/* 草稿保存区 */}
      <Card title="草稿内容">
        <Form form={form} layout="vertical">
          <Form.Item label="咨询问题" name="question">
            <Input.TextArea
              rows={4}
              placeholder="请输入您的法律咨询问题..."
            />
          </Form.Item>
          <Form.Item label="备注" name="notes">
            <Input.TextArea
              rows={2}
              placeholder="添加备注信息..."
            />
          </Form.Item>
        </Form>

        {savedContent && (
          <div style={{ marginTop: 16 }}>
            <Text strong>已保存的草稿：</Text>
            <pre style={{
              background: '#f5f5f5',
              padding: 12,
              borderRadius: 4,
              marginTop: 8,
              overflow: 'auto',
            }}>
              {savedContent}
            </pre>
          </div>
        )}
      </Card>

      {/* 搜索弹窗 */}
      <Modal
        title="搜索"
        open={searchModalVisible}
        onCancel={() => setSearchModalVisible(false)}
        footer={[
          <Button key="close" icon={<CloseOutlined />} onClick={() => setSearchModalVisible(false)}>
            关闭 (Esc)
          </Button>,
        ]}
      >
        <Input
          placeholder="搜索内容..."
          prefix={<SearchOutlined />}
          size="large"
          autoFocus
        />
        <div style={{ marginTop: 16 }}>
          <Text type="secondary">搜索功能演示</Text>
        </div>
      </Modal>

      {/* 新建咨询弹窗 */}
      <Modal
        title="新建咨询"
        open={consultModalVisible}
        onCancel={() => setConsultModalVisible(false)}
        onOk={() => {
          const values = form.getFieldsValue();
          setSavedContent(JSON.stringify(values, null, 2));
          setDraftSaved(true);
          setTimeout(() => setDraftSaved(false), 2000);
          setConsultModalVisible(false);
        }}
        okText="提交"
        cancelText="取消 (Esc)"
      >
        <Form form={form} layout="vertical">
          <Form.Item label="咨询问题" name="consultation" rules={[{ required: true, message: '请输入咨询问题' }]}>
            <Input.TextArea
              rows={4}
              placeholder="请输入您的法律咨询问题..."
              autoFocus
            />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}
