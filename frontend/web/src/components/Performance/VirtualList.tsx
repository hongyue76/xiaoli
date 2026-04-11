import React, { useState, useRef, useEffect, useCallback } from 'react';
import './VirtualList.css';

interface VirtualListProps<T> {
  /**
   * 数据源
   */
  data: T[];
  /**
   * 列表项渲染函数
   */
  renderItem: (item: T, index: number) => React.ReactNode;
  /**
   * 每个列表项的高度（固定高度）
   */
  itemHeight: number;
  /**
   * 列表容器的总高度
   */
  height: number;
  /**
   * 额外渲染的缓冲项数量（上下各渲染多少个不可见的项）
   */
  bufferCount?: number;
  /**
   * 滚动到指定索引
   */
  scrollToIndex?: number;
  /**
   * 滚动到指定索引的回调
   */
  onScroll?: (scrollTop: number) => void;
  /**
   * 是否启用平滑滚动
   */
  smooth?: boolean;
  /**
   * 列表项的唯一标识键名（用于优化渲染）
   */
  keyExtractor?: (item: T, index: number) => string | number;
}

/**
 * 虚拟滚动列表组件
 *
 * 功能特点：
 * 1. 只渲染可视区域内的列表项，大幅减少 DOM 节点数量
 * 2. 支持固定高度列表项（性能最优）
 * 3. 支持滚动到指定位置
 * 4. 上下缓冲渲染，避免滚动时出现白屏
 * 5. 性能优化：使用 useCallback 和 useMemo 减少不必要的重渲染
 *
 * 适用场景：
 * - 长列表（1000+ 条数据）
 * - 列表项高度固定
 * - 需要高性能滚动
 *
 * 使用示例：
 * ```tsx
 * <VirtualList
 *   data={items}
 *   renderItem={(item, index) => <div key={item.id}>{item.name}</div>}
 *   itemHeight={80}
 *   height={600}
 *   bufferCount={3}
 * />
 * ```
 */
const VirtualList = <T,>({
  data,
  renderItem,
  itemHeight,
  height,
  bufferCount = 3,
  scrollToIndex,
  onScroll,
  smooth = true,
  keyExtractor,
}: VirtualListProps<T>) => {
  const containerRef = useRef<HTMLDivElement>(null);
  const [scrollTop, setScrollTop] = useState(0);

  // 计算可视区域可以显示多少个列表项
  const visibleCount = Math.ceil(height / itemHeight);

  // 计算实际需要渲染的列表项范围
  const { startIndex, endIndex } = React.useMemo(() => {
    const start = Math.max(0, Math.floor(scrollTop / itemHeight) - bufferCount);
    const end = Math.min(
      data.length,
      Math.ceil((scrollTop + height) / itemHeight) + bufferCount
    );
    return { startIndex: start, endIndex: end };
  }, [scrollTop, itemHeight, height, bufferCount, data.length]);

  // 计算总高度
  const totalHeight = data.length * itemHeight;

  // 计算偏移量
  const offsetY = startIndex * itemHeight;

  // 获取需要渲染的数据
  const visibleData = data.slice(startIndex, endIndex);

  // 滚动事件处理
  const handleScroll = useCallback(
    (e: React.UIEvent<HTMLDivElement>) => {
      const newScrollTop = e.currentTarget.scrollTop;
      setScrollTop(newScrollTop);
      onScroll?.(newScrollTop);
    },
    [onScroll]
  );

  // 滚动到指定索引
  useEffect(() => {
    if (scrollToIndex !== undefined && containerRef.current) {
      const targetScrollTop = scrollToIndex * itemHeight;
      if (smooth) {
        containerRef.current.scrollTo({
          top: targetScrollTop,
          behavior: 'smooth',
        });
      } else {
        containerRef.current.scrollTop = targetScrollTop;
      }
    }
  }, [scrollToIndex, itemHeight, smooth]);

  return (
    <div
      ref={containerRef}
      className="virtual-list-container"
      style={{ height, overflow: 'auto' }}
      onScroll={handleScroll}
    >
      <div
        className="virtual-list-content"
        style={{ height: totalHeight, position: 'relative' }}
      >
        <div
          className="virtual-list-items"
          style={{
            position: 'absolute',
            top: offsetY,
            width: '100%',
          }}
        >
          {visibleData.map((item, index) => {
            const actualIndex = startIndex + index;
            const key = keyExtractor
              ? keyExtractor(item, actualIndex)
              : actualIndex;
            return (
              <div
                key={key}
                className="virtual-list-item"
                style={{
                  height: itemHeight,
                  display: 'flex',
                  alignItems: 'center',
                }}
              >
                {renderItem(item, actualIndex)}
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
};

export default VirtualList;
