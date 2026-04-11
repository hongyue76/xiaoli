/**
 * 响应式栅格布局组件
 */

import React, { ReactNode } from 'react';
import { Row, Col } from 'antd';
import './ResponsiveGrid.css';

interface ResponsiveGridProps {
  children: ReactNode;
  gutter?: number | [number, number];
  cols?: {
    xs?: number;
    sm?: number;
    md?: number;
    lg?: number;
    xl?: number;
    xxl?: number;
  };
  className?: string;
}

const ResponsiveGrid: React.FC<ResponsiveGridProps> = ({
  children,
  gutter = [16, 16],
  cols = { xs: 24, sm: 12, md: 8, lg: 6, xl: 6, xxl: 4 },
  className = '',
}) => {
  return (
    <Row gutter={gutter} className={`responsive-grid ${className}`}>
      {React.Children.map(children, (child) => (
        <Col
          xs={cols.xs}
          sm={cols.sm}
          md={cols.md}
          lg={cols.lg}
          xl={cols.xl}
          xxl={cols.xxl}
          className="grid-item"
        >
          {child}
        </Col>
      ))}
    </Row>
  );
};

export default ResponsiveGrid;
