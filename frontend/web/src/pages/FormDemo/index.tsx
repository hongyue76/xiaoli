/**
 * 表单交互优化演示页面
 */

import React, { useState } from 'react';
import { Card, Row, Col, Typography, Divider, Form, Space, Button, Select } from 'antd';
import {
  SmartInput,
  StepWizard,
  DragDropUpload,
} from '../../components/Form';
import './index.css';

const { Title, Paragraph, Text } = Typography;

/**
 * 案由自动补全选项
 */
const caseTypeOptions = [
  { value: '劳动争议', label: '劳动争议', description: '劳动合同、工资、社保等纠纷' },
  { value: '合同纠纷', label: '合同纠纷', description: '买卖、租赁、服务合同纠纷' },
  { value: '侵权责任', label: '侵权责任', description: '人身损害、财产损害等' },
  { value: '婚姻家庭', label: '婚姻家庭', description: '离婚、抚养、赡养等' },
  { value: '知识产权', label: '知识产权', description: '专利、商标、著作权纠纷' },
];

/**
 * 法院名称自动补全选项
 */
const courtOptions = [
  { value: '北京市海淀区人民法院', label: '北京市海淀区人民法院' },
  { value: '北京市朝阳区人民法院', label: '北京市朝阳区人民法院' },
  { value: '上海市浦东新区人民法院', label: '上海市浦东新区人民法院' },
  { value: '广州市天河区人民法院', label: '广州市天河区人民法院' },
  { value: '深圳市南山区人民法院', label: '深圳市南山区人民法院' },
];

const FormDemo: React.FC = () => {
  const [form] = Form.useForm();

  /**
   * 步骤引导示例
   */
  const steps = [
    {
      key: 'basic',
      title: '基本信息',
      description: '填写案件基本信息',
      content: (
        <div className="step-content">
          <Space direction="vertical" style={{ width: '100%' }} size="large">
            <SmartInput
              type="autocomplete"
              label="案件名称"
              placeholder="请输入案件名称"
              options={[
                { value: '张三诉李四劳动争议案', label: '张三诉李四劳动争议案' },
                { value: '王五诉赵六合同纠纷案', label: '王五诉赵六合同纠纷案' },
              ]}
              rules={[{ required: true, message: '请输入案件名称' }]}
            />

            <SmartInput
              type="autocomplete"
              label="案由"
              placeholder="请选择或输入案由"
              options={caseTypeOptions}
              rules={[{ required: true, message: '请选择案由' }]}
            />

            <SmartInput
              type="autocomplete"
              label="审理法院"
              placeholder="请选择或输入法院名称"
              options={courtOptions}
              rules={[{ required: true, message: '请选择审理法院' }]}
            />

            <SmartInput
              type="text"
              label="案件编号"
              placeholder="请输入案件编号"
              rules={[
                { required: true, message: '请输入案件编号' },
                { pattern: /^\d{4}-\d{4}-\d+-\d+$/, message: '格式：2024-0101-民-001' }
              ]}
              hint="格式：2024-0101-民-001"
            />
          </Space>
        </div>
      ),
      estimatedTime: 2,
    },
    {
      key: 'parties',
      title: '当事人信息',
      description: '填写原告和被告信息',
      content: (
        <div className="step-content">
          <Space direction="vertical" style={{ width: '100%' }} size="large">
            <SmartInput
              type="text"
              label="原告姓名"
              placeholder="请输入原告姓名"
              rules={[{ required: true, message: '请输入原告姓名' }]}
            />

            <SmartInput
              type="text"
              label="原告联系方式"
              placeholder="请输入联系方式"
              rules={[
                { required: true, message: '请输入联系方式' },
                { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码' }
              ]}
              hint="格式：11位手机号码"
            />

            <SmartInput
              type="text"
              label="被告姓名"
              placeholder="请输入被告姓名"
              rules={[{ required: true, message: '请输入被告姓名' }]}
            />

            <SmartInput
              type="text"
              label="被告联系方式"
              placeholder="请输入联系方式（如已知）"
              rules={[
                { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码' }
              ]}
            />
          </Space>
        </div>
      ),
      estimatedTime: 3,
    },
    {
      key: 'evidence',
      title: '证据材料',
      description: '上传相关证据材料',
      content: (
        <div className="step-content">
          <DragDropUpload
            accept=".pdf,.jpg,.jpeg,.png,.doc,.docx"
            maxSize={10}
            maxCount={20}
            multiple={true}
            preview={true}
            showProgress={true}
          />
        </div>
      ),
      estimatedTime: 5,
    },
  ];

  return (
    <div className="form-demo">
      <div className="demo-header">
        <Title level={2}>表单交互优化演示</Title>
        <Paragraph>
          本页面展示了智能输入、步骤引导和拖拽上传等表单交互优化功能。
        </Paragraph>
      </div>

      {/* 智能输入组件 */}
      <section className="demo-section">
        <Title level={3}>1. 智能输入 (SmartInput)</Title>
        <Paragraph>
          支持自动补全、实时校验、友好提示，避免红色警告，改用黄色提示。
        </Paragraph>

        <Row gutter={[16, 16]}>
          <Col span={12}>
            <Card title="文本输入 - 实时校验" size="small">
              <Space direction="vertical" style={{ width: '100%' }}>
                <SmartInput
                  type="text"
                  label="手机号码"
                  placeholder="请输入手机号码"
                  rules={[
                    { required: true, message: '手机号码为必填项' },
                    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码' }
                  ]}
                  hint="格式：11位手机号码"
                />

                <SmartInput
                  type="text"
                  label="身份证号"
                  placeholder="请输入身份证号"
                  rules={[
                    { required: true, message: '身份证号为必填项' },
                    { min: 18, message: '身份证号至少18位' }
                  ]}
                />
              </Space>
            </Card>
          </Col>

          <Col span={12}>
            <Card title="自动补全 - 案由/法院" size="small">
              <Space direction="vertical" style={{ width: '100%' }}>
                <SmartInput
                  type="autocomplete"
                  label="案由"
                  placeholder="请选择或输入案由"
                  options={caseTypeOptions}
                  rules={[{ required: true, message: '请选择案由' }]}
                />

                <SmartInput
                  type="autocomplete"
                  label="审理法院"
                  placeholder="请选择或输入法院名称"
                  options={courtOptions}
                  rules={[{ required: true, message: '请选择审理法院' }]}
                />
              </Space>
            </Card>
          </Col>

          <Col span={12}>
            <Card title="选择框 - 类型选择" size="small">
              <Space direction="vertical" style={{ width: '100%' }}>
                <SmartInput
                  type="select"
                  label="案件类型"
                  placeholder="请选择案件类型"
                  selectOptions={[
                    { value: 'civil', label: '民事案件' },
                    { value: 'criminal', label: '刑事案件' },
                    { value: 'administrative', label: '行政案件' },
                  ]}
                  rules={[{ required: true, message: '请选择案件类型' }]}
                />
              </Space>
            </Card>
          </Col>
        </Row>
      </section>

      <Divider />

      {/* 步骤引导 */}
      <section className="demo-section">
        <Title level={3}>2. 步骤引导 (StepWizard)</Title>
        <Paragraph>
          复杂流程分步骤，每步显示预计用时，支持上一步/下一步。
        </Paragraph>

        <Card>
          <StepWizard
            steps={steps}
            onFinish={(data) => {
              console.log('表单完成', data);
              alert('表单提交成功！');
            }}
            onCancel={() => {
              console.log('取消');
            }}
            showEstimatedTime={true}
            showProgress={true}
            allowSkip={false}
          />
        </Card>
      </section>

      <Divider />

      {/* 拖拽上传 */}
      <section className="demo-section">
        <Title level={3}>3. 拖拽上传 (DragDropUpload)</Title>
        <Paragraph>
          支持文件拖拽、预览缩略图、上传进度可视化。
        </Paragraph>

        <DragDropUpload
          accept=".pdf,.jpg,.jpeg,.png,.doc,.docx"
          maxSize={10}
          maxCount={20}
          multiple={true}
          preview={true}
          showProgress={true}
        />
      </section>
    </div>
  );
};

export default FormDemo;
