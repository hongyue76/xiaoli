import React, { useState, useEffect } from 'react';
import { Button, Card, Space, Tag, Tooltip, message } from 'antd';
import { ThunderboltOutlined, StarOutlined, CopyOutlined, LikeOutlined, DislikeOutlined } from '@ant-design/icons';
import './QuickActions.css';

export interface QuickQuestion {
  id: string;
  text: string;
  category?: string;
  isFavorite?: boolean;
}

interface QuickActionsProps {
  questions: QuickQuestion[];
  onQuestionClick?: (question: QuickQuestion) => void;
  onCopy?: (content: string) => void;
  onFollowUp?: (content: string) => void;
  onLike?: (messageId: string, type: 'like' | 'dislike') => void;
  onToggleFavorite?: (questionId: string) => void;
  showFollowUp?: boolean;
  followUpContent?: string;
  messageId?: string;
}

const QuickActions: React.FC<QuickActionsProps> = ({
  questions,
  onQuestionClick,
  onCopy,
  onFollowUp,
  onLike,
  onToggleFavorite,
  showFollowUp = false,
  followUpContent,
  messageId,
}) => {
  const [categories, setCategories] = useState<string[]>([]);
  const [selectedCategory, setSelectedCategory] = useState<string>('全部');

  useEffect(() => {
    const uniqueCategories = ['全部', ...new Set(questions.map(q => q.category).filter(Boolean) as string[])];
    setCategories(uniqueCategories);
  }, [questions]);

  const filteredQuestions = selectedCategory === '全部'
    ? questions
    : questions.filter(q => q.category === selectedCategory);

  const handleQuestionClick = (question: QuickQuestion) => {
    onQuestionClick?.(question);
  };

  const handleCopy = () => {
    if (followUpContent) {
      navigator.clipboard.writeText(followUpContent);
      message.success('已复制到剪贴板');
      onCopy?.(followUpContent);
    }
  };

  const handleFollowUp = () => {
    if (followUpContent) {
      onFollowUp?.(`能否详细说明一下"${followUpContent.slice(0, 20)}..."的内容？`);
    }
  };

  const handleLike = (type: 'like' | 'dislike') => {
    if (messageId) {
      onLike?.(messageId, type);
      message.success(type === 'like' ? '感谢您的点赞！' : '感谢您的反馈，我们会改进！');
    }
  };

  const handleToggleFavorite = (questionId: string, e: React.MouseEvent) => {
    e.stopPropagation();
    onToggleFavorite?.(questionId);
  };

  return (
    <div className="quick-actions">
      {/* 追问操作 */}
      {showFollowUp && followUpContent && (
        <Card className="follow-up-actions" bordered={false}>
          <Space direction="vertical" size="middle" style={{ width: '100%' }}>
            <div className="follow-up-header">
              <ThunderboltOutlined className="follow-up-icon" />
              <span className="follow-up-title">快捷操作</span>
            </div>
            <Space size="small" wrap>
              <Button
                type="primary"
                size="small"
                icon={<ThunderboltOutlined />}
                onClick={handleFollowUp}
              >
                追问详情
              </Button>
              <Button
                size="small"
                icon={<CopyOutlined />}
                onClick={handleCopy}
              >
                复制答案
              </Button>
              <Button
                size="small"
                icon={<LikeOutlined />}
                onClick={() => handleLike('like')}
              >
                点赞
              </Button>
              <Button
                size="small"
                icon={<DislikeOutlined />}
                onClick={() => handleLike('dislike')}
              >
                点踩
              </Button>
            </Space>
          </Space>
        </Card>
      )}

      {/* 常用问题推荐 */}
      {questions.length > 0 && (
        <Card className="quick-questions" bordered={false} title="常用问题">
          {/* 分类标签 */}
          {categories.length > 1 && (
            <div className="category-tags">
              <Space size="small" wrap>
                {categories.map((category) => (
                  <Tag
                    key={category}
                    color={selectedCategory === category ? 'blue' : 'default'}
                    onClick={() => setSelectedCategory(category)}
                    className="category-tag"
                  >
                    {category}
                  </Tag>
                ))}
              </Space>
            </div>
          )}

          {/* 问题列表 */}
          <div className="question-list">
            <Space direction="vertical" size="small" style={{ width: '100%' }}>
              {filteredQuestions.map((question) => (
                <div
                  key={question.id}
                  className="question-item"
                  onClick={() => handleQuestionClick(question)}
                >
                  <Button
                    type="text"
                    block
                    className="question-button"
                  >
                    <div className="question-content">
                      <span className="question-text">{question.text}</span>
                      {question.category && (
                        <Tag className="question-tag" size="small">
                          {question.category}
                        </Tag>
                      )}
                    </div>
                    <Tooltip title={question.isFavorite ? '取消收藏' : '收藏'}>
                      <StarOutlined
                        className={`favorite-icon ${question.isFavorite ? 'active' : ''}`}
                        onClick={(e) => handleToggleFavorite(question.id, e)}
                      />
                    </Tooltip>
                  </Button>
                </div>
              ))}
            </Space>
          </div>

          {/* 空状态 */}
          {filteredQuestions.length === 0 && (
            <div className="empty-state">
              <span>该分类下暂无问题</span>
            </div>
          )}
        </Card>
      )}
    </div>
  );
};

export default QuickActions;
