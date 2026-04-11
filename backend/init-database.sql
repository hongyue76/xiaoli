-- 律法先锋数据库初始化脚本
-- 创建数据库
CREATE DATABASE xiaoli_legal;

-- 创建用户
CREATE USER xiaoli WITH PASSWORD 'Xiaoli@2024';

-- 授权
GRANT ALL PRIVILEGES ON DATABASE xiaoli_legal TO xiaoli;

-- 切换到数据库
\c xiaoli_legal

-- 咨询服务表
CREATE TABLE IF NOT EXISTS consult_conversations (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    title VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS consult_messages (
    id BIGSERIAL PRIMARY KEY,
    conversation_id BIGINT,
    role VARCHAR(50),
    content TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (conversation_id) REFERENCES consult_conversations(id)
);

-- 案例检索服务表
CREATE TABLE IF NOT EXISTS case_info (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(500),
    case_no VARCHAR(100),
    case_type VARCHAR(50),
    cause VARCHAR(200),
    court VARCHAR(200),
    judge VARCHAR(100),
    judgment_date DATE,
    case_status VARCHAR(50),
    summary TEXT,
    dispute_focus TEXT,
    ruling_idea TEXT,
    judgment_result TEXT,
    legal_basis TEXT,
    view_count INTEGER DEFAULT 0,
    score DECIMAL(5,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 文书生成服务表
CREATE TABLE IF NOT EXISTS document_templates (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    case_type VARCHAR(50),
    template_type VARCHAR(50),
    content TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS document_records (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    template_id BIGINT,
    case_id BIGINT,
    content TEXT,
    status VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (template_id) REFERENCES document_templates(id)
);

-- 合同审查服务表
CREATE TABLE IF NOT EXISTS contract_reviews (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    contract_name VARCHAR(255),
    contract_type VARCHAR(100),
    content TEXT,
    review_result TEXT,
    status VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 证据分析服务表
CREATE TABLE IF NOT EXISTS evidence_records (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    case_id BIGINT,
    evidence_type VARCHAR(100),
    description TEXT,
    analysis_result TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 合规检查服务表
CREATE TABLE IF NOT EXISTS compliance_checks (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    company_name VARCHAR(255),
    check_type VARCHAR(100),
    check_result TEXT,
    status VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 插入示例案例数据
INSERT INTO case_info (title, case_no, case_type, cause, court, judge, judgment_date, case_status, summary, dispute_focus, ruling_idea, judgment_result, legal_basis, view_count, score) VALUES
('某科技公司诉某员工劳动争议案', '(2024)京0105民初12345号', 'CIVIL', '劳动合同纠纷', '北京市朝阳区人民法院', '张法官', '2024-01-15', 'FINAL', '本案涉及解除劳动合同的合法性问题，用人单位以员工违反公司规章制度为由解除劳动合同，员工主张违法解除。法院对用人单位解除行为的合法性进行了详细审查。', '解除劳动合同是否合法', '用人单位单方解除劳动合同需符合法定情形，并履行告知义务。本案中，用人单位未能充分证明员工存在严重违反规章制度的行为，且未履行告知义务，构成违法解除。', '判决用人单位支付违法解除劳动合同赔偿金人民币85600元', '《劳动合同法》第三十九条、第四十八条', 1234, 95.00),
('某公司买卖合同纠纷案', '(2024)京0105民初12346号', 'CIVIL', '买卖合同纠纷', '北京市朝阳区人民法院', '李法官', '2024-01-10', 'FINAL', '原告向被告购买设备，被告交付的设备存在质量问题，原告要求解除合同并赔偿损失。', '货物质量是否符合约定', '当事人应当按照约定全面履行自己的义务。出卖人交付的标的物不符合质量要求的，买受人可以请求承担违约责任。', '判决被告返还货款人民币125000元，支付违约金人民币25000元', '《民法典》第五百零九条、第五百七十七条、第五百八十二条', 987, 92.00),
('道路交通事故责任纠纷案', '(2024)京0105民初12347号', 'CIVIL', '侵权责任纠纷', '北京市朝阳区人民法院', '王法官', '2024-01-05', 'FINAL', '张某驾驶车辆与李某驾驶的车辆发生追尾事故，造成两车受损及李某受伤。交警认定张某负主要责任，李某负次要责任。', '事故责任如何划分，赔偿数额如何确定', '交通事故责任应当按照过错原则确定。造成人身损害的，应当赔偿医疗费、护理费、交通费、营养费、住院伙食补助费等合理费用。', '判决保险公司在交强险范围内赔偿120000元，超出部分由张某承担70%，李某承担30%', '《道路交通安全法》第七十六条，《民法典》第一千一百七十九条', 876, 88.00),
('房屋租赁合同纠纷案', '(2024)京0105民初12348号', 'CIVIL', '租赁合同纠纷', '北京市朝阳区人民法院', '赵法官', '2024-01-03', 'FINAL', '承租人因工作调动提前退租，出租人要求承担违约责任。承租人主张符合合同约定的解除条件。', '提前退租是否构成违约', '当事人可以约定一方解除合同的条件。解除合同的条件成就时，解除权人可以解除合同，但应当通知对方。', '判决解除租赁合同，承租人支付相当于一个月租金的违约金', '《民法典》第五百六十二条、第五百六十三条', 765, 85.00),
('建设工程施工合同纠纷案', '(2024)京0105民初12349号', 'CIVIL', '建设工程合同纠纷', '北京市朝阳区人民法院', '孙法官', '2024-01-02', 'FINAL', '承包人完成工程后，发包人未按约定支付工程款。承包人要求支付工程款及利息。', '工程款数额如何确定，利息如何计算', '发包人未按照约定支付价款的，承包人可以催告发包人在合理期限内支付价款。发包人逾期不支付的，应当按照约定支付违约金。', '判决发包人支付工程款人民币2560000元，并支付利息', '《民法典》第七百九十九条、第八百零七条', 654, 82.00);

-- 插入示例文档模板
INSERT INTO document_templates (name, case_type, template_type, content) VALUES
('劳动合同纠纷起诉状', 'CIVIL', 'complaint', '# 劳动合同纠纷起诉状模板\n\n原告：[姓名]\n被告：[公司名称]\n\n诉讼请求：\n1. 请求判决被告支付违法解除劳动合同赔偿金人民币[金额]元；\n2. 请求被告承担本案诉讼费用。\n\n事实与理由：\n[案情描述]\n\n此致\n[法院名称]\n\n具状人：[签名]\n日期：[日期]'),
('买卖合同纠纷起诉状', 'CIVIL', 'complaint', '# 买卖合同纠纷起诉状模板\n\n原告：[公司名称]\n被告：[公司名称]\n\n诉讼请求：\n1. 请求判决被告返还货款人民币[金额]元；\n2. 请求判决被告支付违约金人民币[金额]元；\n3. 请求被告承担本案诉讼费用。\n\n事实与理由：\n[案情描述]\n\n此致\n[法院名称]\n\n具状人：[签名]\n日期：[日期]');

-- 创建索引

-- ==================== 咨询表索引 ====================

-- 用户查询历史记录：用户ID + 创建时间 联合索引
CREATE INDEX IF NOT EXISTS idx_consult_user_created 
ON consult_conversations(user_id, created_at DESC);

-- 咨询会话创建时间索引
CREATE INDEX IF NOT EXISTS idx_consult_created_at 
ON consult_conversations(created_at DESC);

-- 消息创建时间索引
CREATE INDEX IF NOT EXISTS idx_consult_message_created 
ON consult_messages(created_at DESC);

-- ==================== 案例表索引 ====================

-- 案例类型索引
CREATE INDEX IF NOT EXISTS idx_case_case_type ON case_info(case_type);

-- 案例状态索引
CREATE INDEX IF NOT EXISTS idx_case_case_status ON case_info(case_status);

-- 案例创建时间索引
CREATE INDEX IF NOT EXISTS idx_case_created_at ON case_info(created_at DESC);

-- 法院索引：用于按法院筛选案例
CREATE INDEX IF NOT EXISTS idx_case_court ON case_info(court);

-- 法官索引：用于按法官筛选案例
CREATE INDEX IF NOT EXISTS idx_case_judge ON case_info(judge);

-- ==================== 文书表索引 ====================

-- 文书记录：用户ID + 状态 联合索引
CREATE INDEX IF NOT EXISTS idx_document_user_status 
ON document_records(user_id, status);

-- 文书创建时间索引
CREATE INDEX IF NOT EXISTS idx_document_created_at 
ON document_records(created_at DESC);

-- ==================== 合同审查表索引 ====================

-- 合同审查：用户ID + 状态 联合索引
CREATE INDEX IF NOT EXISTS idx_contract_user_status 
ON contract_reviews(user_id, status);

-- 合同审查创建时间索引
CREATE INDEX IF NOT EXISTS idx_contract_created_at 
ON contract_reviews(created_at DESC);

-- ==================== 证据表索引 ====================

-- 证据记录：用户ID + 创建时间 联合索引
CREATE INDEX IF NOT EXISTS idx_evidence_user_created 
ON evidence_records(user_id, created_at DESC);

-- ==================== 合规检查表索引 ====================

-- 合规检查：用户ID + 状态 联合索引
CREATE INDEX IF NOT EXISTS idx_compliance_user_status 
ON compliance_checks(user_id, status);
