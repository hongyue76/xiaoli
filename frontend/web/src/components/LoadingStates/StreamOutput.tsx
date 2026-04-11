/**
 * 流式输出组件
 * 模拟AI生成内容的分段展示效果
 */

import React, { useState, useEffect, useRef } from 'react';
import { Typography, Card, Space } from 'antd';
import { LoadingOutlined, CheckCircleOutlined } from '@ant-design/icons';
import './StreamOutput.css';

const { Text, Paragraph } = Typography;

interface StreamChunk {
  id: string;
  content: string;
  timestamp: Date;
  status: 'pending' | 'streaming' | 'completed';
}

interface StreamOutputProps {
  content: string;
  speed?: number; // 打字速度（字符/秒）
  onComplete?: () => void;
  showCursor?: boolean;
}

/**
 * 流式输出组件
 */
const StreamOutput: React.FC<StreamOutputProps> = ({
  content,
  speed = 20,
  onComplete,
  showCursor = true,
}) => {
  const [displayedContent, setDisplayedContent] = useState<string>('');
  const [isStreaming, setIsStreaming] = useState<boolean>(true);
  const streamRef = useRef<number | null>(null);

  useEffect(() => {
    setDisplayedContent('');
    setIsStreaming(true);

    let currentIndex = 0;
    const intervalTime = 1000 / speed;

    const streamInterval = () => {
      if (currentIndex < content.length) {
        setDisplayedContent(prev => {
          const newContent = content.substring(0, currentIndex + 1);
          currentIndex++;
          return newContent;
        });
        streamRef.current = window.setTimeout(streamInterval, intervalTime);
      } else {
        setIsStreaming(false);
        onComplete?.();
      }
    };

    streamInterval();

    return () => {
      if (streamRef.current) {
        clearTimeout(streamRef.current);
      }
    };
  }, [content, speed, onComplete]);

  return (
    <div className="stream-output">
      <Card className="stream-card">
        <div className="stream-header">
          {isStreaming ? (
            <LoadingOutlined className="stream-icon" spin />
          ) : (
            <CheckCircleOutlined className="stream-icon completed" />
          )}
          <Text className="stream-status">
            {isStreaming ? 'AI正在生成...' : '生成完成'}
          </Text>
        </div>

        <div className="stream-content">
          <Paragraph className={`stream-text ${isStreaming ? 'streaming' : 'completed'}`}>
            {displayedContent}
            {showCursor && isStreaming && <span className="stream-cursor">|</span>}
          </Paragraph>
        </div>

        {isStreaming && (
          <div className="stream-footer">
            <Space>
              <LoadingOutlined spin />
              <Text type="secondary" style={{ fontSize: 12 }}>
                正在生成内容...
              </Text>
            </Space>
          </div>
        )}
      </Card>
    </div>
  );
};

/**
 * 分段流式输出组件
 */
interface Segment {
  id: string;
  title?: string;
  content: string;
  delay?: number; // 延迟显示（毫秒）
}

interface SegmentedStreamOutputProps {
  segments: Segment[];
  speed?: number;
  onComplete?: () => void;
}

const SegmentedStreamOutput: React.FC<SegmentedStreamOutputProps> = ({
  segments,
  speed = 20,
  onComplete,
}) => {
  const [activeSegmentIndex, setActiveSegmentIndex] = useState<number>(0);
  const [segmentStates, setSegmentStates] = useState<
    Array<{ displayed: string; isStreaming: boolean }>
  >(
    segments.map(() => ({ displayed: '', isStreaming: false }))
  );

  useEffect(() => {
    if (activeSegmentIndex >= segments.length) {
      onComplete?.();
      return;
    }

    const segment = segments[activeSegmentIndex];
    const startTime = Date.now();

    // 等待延迟
    const delay = segment.delay || 0;
    if (delay > 0) {
      const delayTimeout = setTimeout(() => {
        startStreaming(segment, activeSegmentIndex);
      }, delay);
      return () => clearTimeout(delayTimeout);
    } else {
      startStreaming(segment, activeSegmentIndex);
    }
  }, [activeSegmentIndex]);

  const startStreaming = (segment: Segment, index: number) => {
    let currentIndex = 0;
    const intervalTime = 1000 / speed;

    const streamInterval = () => {
      if (currentIndex < segment.content.length) {
        setSegmentStates(prev => {
          const newState = [...prev];
          newState[index] = {
            ...newState[index],
            displayed: segment.content.substring(0, currentIndex + 1),
            isStreaming: true,
          };
          return newState;
        });
        currentIndex++;
        setTimeout(streamInterval, intervalTime);
      } else {
        setSegmentStates(prev => {
          const newState = [...prev];
          newState[index] = {
            ...newState[index],
            isStreaming: false,
          };
          return newState;
        });
        setActiveSegmentIndex(index + 1);
      }
    };

    streamInterval();
  };

  return (
    <div className="segmented-stream-output">
      {segments.map((segment, index) => {
        const state = segmentStates[index] || { displayed: '', isStreaming: false };
        const isPending = index > activeSegmentIndex;

        return (
          <Card
            key={segment.id}
            className={`segment-card ${
              state.isStreaming ? 'streaming' : ''
            } ${isPending ? 'pending' : ''}`}
          >
            {segment.title && (
              <div className="segment-title">
                <Text strong>{segment.title}</Text>
                {state.isStreaming && (
                  <LoadingOutlined className="segment-icon" spin />
                )}
                {!state.isStreaming && !isPending && (
                  <CheckCircleOutlined className="segment-icon completed" />
                )}
              </div>
            )}

            <Paragraph className="segment-content">
              {isPending ? (
                <span className="pending-placeholder">等待生成...</span>
              ) : (
                <>
                  {state.displayed}
                  {state.isStreaming && <span className="stream-cursor">|</span>}
                </>
              )}
            </Paragraph>
          </Card>
        );
      })}
    </div>
  );
};

export default StreamOutput;
export { SegmentedStreamOutput };
