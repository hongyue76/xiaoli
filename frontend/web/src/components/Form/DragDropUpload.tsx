/**
 * 拖拽上传组件
 * 支持文件拖拽、预览缩略图、上传进度可视化
 */

import React, { useState, useRef, useCallback } from 'react';
import { Upload, message, Progress, Card, Image, Space, Typography, Button } from 'antd';
import {
  InboxOutlined,
  FileOutlined,
  PictureOutlined,
  FileTextOutlined,
  DeleteOutlined,
  EyeOutlined,
  DownloadOutlined,
} from '@ant-design/icons';
import type { UploadProps, UploadFile } from 'antd';
import './DragDropUpload.css';

const { Dragger } = Upload;
const { Text } = Typography;

interface DragDropUploadProps {
  accept?: string;
  maxSize?: number; // MB
  maxCount?: number;
  multiple?: boolean;
  onUpload?: (files: File[]) => Promise<void>;
  onRemove?: (file: File) => void;
  preview?: boolean;
  showProgress?: boolean;
  customRequest?: (options: any) => void;
  disabled?: boolean;
}

/**
 * 文件类型图标
 */
const getFileIcon = (fileType: string) => {
  if (fileType.startsWith('image/')) {
    return <PictureOutlined className="file-icon image" />;
  } else if (fileType.includes('pdf')) {
    return <FileTextOutlined className="file-icon pdf" />;
  } else {
    return <FileOutlined className="file-icon default" />;
  }
};

/**
 * 格式化文件大小
 */
const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return '0 B';
  const k = 1024;
  const sizes = ['B', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return Math.round((bytes / Math.pow(k, i)) * 100) / 100 + ' ' + sizes[i];
};

/**
 * 模拟上传进度
 */
const simulateUpload = (
  file: UploadFile,
  onProgress: (percent: number) => void,
  onSuccess: () => void,
  onError: (error: string) => void
) => {
  let progress = 0;
  const interval = setInterval(() => {
    progress += Math.random() * 20;
    if (progress >= 100) {
      progress = 100;
      clearInterval(interval);
      onSuccess();
    }
    onProgress(Math.min(progress, 100));
  }, 300);

  return () => clearInterval(interval);
};

/**
 * 拖拽上传组件
 */
const DragDropUpload: React.FC<DragDropUploadProps> = ({
  accept = '.pdf,.doc,.docx,.jpg,.jpeg,.png,.gif',
  maxSize = 10,
  maxCount = 10,
  multiple = true,
  onUpload,
  onRemove,
  preview = true,
  showProgress = true,
  customRequest,
  disabled = false,
}) => {
  const [fileList, setFileList] = useState<UploadFile[]>([]);
  const [uploading, setUploading] = useState(false);
  const [uploadProgress, setUploadProgress] = useState<Record<string, number>>({});

  /**
   * 上传前验证
   */
  const beforeUpload = (file: File) => {
    // 验证文件类型
    const acceptTypes = accept.split(',').map(type => type.trim());
    const fileExtension = '.' + file.name.split('.').pop()?.toLowerCase();
    const isValidType = acceptTypes.some(type => type === '*' || type === fileExtension);

    if (!isValidType) {
      message.error(`文件类型不支持，仅支持：${accept}`);
      return false;
    }

    // 验证文件大小
    const isValidSize = file.size <= maxSize * 1024 * 1024;
    if (!isValidSize) {
      message.error(`文件大小不能超过 ${maxSize}MB`);
      return false;
    }

    return true;
  };

  /**
   * 处理文件变化
   */
  const handleChange: UploadProps['onChange'] = ({ fileList: newFileList }) => {
    setFileList(newFileList);
  };

  /**
   * 处理文件上传
   */
  const handleUpload = async () => {
    if (fileList.length === 0) {
      message.warning('请先选择文件');
      return;
    }

    setUploading(true);
    const files = fileList.map(file => file.originFileObj as File);

    try {
      // 模拟上传进度
      const cancelUploads: Array<() => void> = [];

      fileList.forEach(file => {
        if (!file.originFileObj) return;

        const cancelUpload = simulateUpload(
          file,
          (percent) => {
            setUploadProgress(prev => ({
              ...prev,
              [file.uid]: percent,
            }));
          },
          () => {
            setUploadProgress(prev => ({
              ...prev,
              [file.uid]: 100,
            }));
          },
          (error) => {
            message.error(`${file.name} 上传失败：${error}`);
          }
        );
        cancelUploads.push(cancelUpload);
      });

      // 等待所有上传完成
      await new Promise(resolve => setTimeout(resolve, 3000));

      // 调用自定义上传函数
      if (onUpload) {
        await onUpload(files);
      }

      message.success('文件上传成功');
      setFileList([]);
      setUploadProgress({});
    } catch (error: any) {
      message.error('上传失败：' + (error.message || '未知错误'));
    } finally {
      setUploading(false);
    }
  };

  /**
   * 处理文件删除
   */
  const handleRemove = (file: UploadFile) => {
    const newFileList = fileList.filter(item => item.uid !== file.uid);
    setFileList(newFileList);
    onRemove?.(file.originFileObj as File);
  };

  /**
   * 预览文件
   */
  const handlePreview = (file: UploadFile) => {
    if (file.type?.startsWith('image/')) {
      // 图片预览
      const url = URL.createObjectURL(file.originFileObj as File);
      const img = new Image();
      img.src = url;
      const newWindow = window.open('');
      if (newWindow) {
        newWindow.document.write(img.outerHTML);
      }
    } else {
      // 文件预览提示
      message.info('此文件类型不支持预览');
    }
  };

  /**
   * 下载文件
   */
  const handleDownload = (file: UploadFile) => {
    const url = URL.createObjectURL(file.originFileObj as File);
    const link = document.createElement('a');
    link.href = url;
    link.download = file.name;
    link.click();
    URL.revokeObjectURL(url);
  };

  return (
    <div className="drag-drop-upload">
      {/* 拖拽上传区域 */}
      <Card className="upload-card" title="文件上传">
        <Dragger
          accept={accept}
          multiple={multiple}
          fileList={fileList}
          beforeUpload={beforeUpload}
          onChange={handleChange}
          customRequest={customRequest}
          disabled={disabled || uploading}
          showUploadList={false}
          className="upload-dragger"
        >
          <p className="ant-upload-drag-icon">
            <InboxOutlined />
          </p>
          <p className="ant-upload-text">点击或拖拽文件到此区域上传</p>
          <p className="ant-upload-hint">
            支持单个或批量上传，文件大小不超过 {maxSize}MB
          </p>
          <p className="ant-upload-hint">
            支持格式：{accept}
          </p>
        </Dragger>

        {/* 文件列表 */}
        {fileList.length > 0 && (
          <div className="file-list">
            <div className="file-list-header">
              <Text strong>已选择 {fileList.length} 个文件</Text>
            </div>
            <div className="file-list-content">
              {fileList.map(file => {
                const progress = uploadProgress[file.uid] || 0;
                return (
                  <div key={file.uid} className="file-item">
                    <div className="file-info">
                      <div className="file-icon-wrapper">
                        {preview && file.type?.startsWith('image/') ? (
                          <Image
                            src={URL.createObjectURL(file.originFileObj as File)}
                            alt={file.name}
                            className="file-preview"
                            preview={false}
                          />
                        ) : (
                          getFileIcon(file.type || '')
                        )}
                      </div>
                      <div className="file-details">
                        <Text className="file-name" ellipsis={{ tooltip: file.name }}>
                          {file.name}
                        </Text>
                        <Text type="secondary" className="file-size">
                          {formatFileSize(file.size)}
                        </Text>
                      </div>
                    </div>

                    {/* 上传进度 */}
                    {showProgress && progress > 0 && progress < 100 && (
                      <Progress
                        percent={progress}
                        size="small"
                        strokeColor={{
                          '0%': '#003a8c',
                          '100%': '#faad14',
                        }}
                      />
                    )}

                    {/* 操作按钮 */}
                    <Space className="file-actions">
                      {preview && (
                        <Button
                          type="text"
                          size="small"
                          icon={<EyeOutlined />}
                          onClick={() => handlePreview(file)}
                          disabled={uploading}
                        >
                          预览
                        </Button>
                      )}
                      <Button
                        type="text"
                        size="small"
                        icon={<DownloadOutlined />}
                        onClick={() => handleDownload(file)}
                        disabled={uploading}
                      >
                        下载
                      </Button>
                      <Button
                        type="text"
                        size="small"
                        danger
                        icon={<DeleteOutlined />}
                        onClick={() => handleRemove(file)}
                        disabled={uploading}
                      >
                        删除
                      </Button>
                    </Space>
                  </div>
                );
              })}
            </div>

            {/* 上传按钮 */}
            <div className="upload-actions">
              <Button
                type="primary"
                onClick={handleUpload}
                loading={uploading}
                disabled={fileList.length === 0}
                size="large"
              >
                {uploading ? '上传中...' : `上传 ${fileList.length} 个文件`}
              </Button>
            </div>
          </div>
        )}
      </Card>
    </div>
  );
};

export default DragDropUpload;
