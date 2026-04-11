-- 法律AI助手数据库初始化脚本
-- 创建数据库后执行此脚本

-- 开启扩展
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ==================== 用户相关表 ====================

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    real_name VARCHAR(50),
    role VARCHAR(20) DEFAULT 'USER',
    status VARCHAR(20) DEFAULT 'ACTIVE',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==================== 法律咨询相关表 ====================

-- 咨询会话表
CREATE TABLE IF NOT EXISTS consult_conversation (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    title VARCHAR(200),
    session_type VARCHAR(30) DEFAULT 'CONSULT',
    status VARCHAR(20) DEFAULT 'ACTIVE',
    message_count INT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 咨询消息表
CREATE TABLE IF NOT EXISTS consult_message (
    id BIGSERIAL PRIMARY KEY,
    conversation_id BIGINT NOT NULL,
    role VARCHAR(20) NOT NULL,
    content TEXT NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (conversation_id) REFERENCES consult_conversation(id)
);

-- ==================== 法律文书相关表 ====================

-- 文书模板表
CREATE TABLE IF NOT EXISTS document_template (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    category VARCHAR(50),
    content TEXT,
    description TEXT,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 法律文书表
CREATE TABLE IF NOT EXISTS legal_document (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    title VARCHAR(200),
    content TEXT,
    template_id BIGINT,
    case_id BIGINT,
    status VARCHAR(20) DEFAULT 'DRAFT',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (template_id) REFERENCES document_template(id)
);

-- ==================== 案例相关表 ====================

-- 法律案例表
CREATE TABLE IF NOT EXISTS legal_case (
    id BIGSERIAL PRIMARY KEY,
    case_number VARCHAR(100) UNIQUE,
    case_type VARCHAR(50),
    title VARCHAR(200),
    court_level VARCHAR(20),
    judgment_result VARCHAR(500),
    case_facts TEXT,
    judgment_reason TEXT,
    legal_basis TEXT,
    key_points TEXT,
    sentence VARCHAR(200),
    amount_involved DECIMAL(15,2),
    case_date DATE,
    publish_date DATE,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==================== 合同相关表 ====================

-- 合同表
CREATE TABLE IF NOT EXISTS contract (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    title VARCHAR(200),
    contract_type VARCHAR(50),
    content TEXT,
    party_a VARCHAR(200),
    party_b VARCHAR(200),
    amount DECIMAL(15,2),
    sign_date DATE,
    status VARCHAR(20) DEFAULT 'DRAFT',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 合同问题表
CREATE TABLE IF NOT EXISTS contract_issue (
    id BIGSERIAL PRIMARY KEY,
    contract_id BIGINT NOT NULL,
    issue_type VARCHAR(50),
    severity VARCHAR(20),
    description TEXT,
    suggestion TEXT,
    clause_content TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (contract_id) REFERENCES contract(id)
);

-- ==================== 案件分析相关表 ====================

-- 案件分析表
CREATE TABLE IF NOT EXISTS case_analysis (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    case_id BIGINT,
    case_description TEXT,
    facts TEXT,
    analysis_result TEXT,
    legal_basis TEXT,
    strategy TEXT,
    risk_assessment TEXT,
    status VARCHAR(20) DEFAULT 'PENDING',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==================== 证据分析相关表 ====================

-- 证据表
CREATE TABLE IF NOT EXISTS evidence (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    case_id BIGINT,
    evidence_name VARCHAR(200),
    evidence_type VARCHAR(50),
    file_path VARCHAR(500),
    description TEXT,
    submit_date DATE,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 证据分析表
CREATE TABLE IF NOT EXISTS evidence_analysis (
    id BIGSERIAL PRIMARY KEY,
    evidence_id BIGINT NOT NULL,
    authenticity VARCHAR(20),
    legitimacy VARCHAR(20),
    relevance VARCHAR(20),
    proof_strength VARCHAR(20),
    analysis_content TEXT,
    opinion TEXT,
    suggestion TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (evidence_id) REFERENCES evidence(id)
);

-- ==================== 司法决策相关表 ====================

-- 案件决策表
CREATE TABLE IF NOT EXISTS case_decision (
    id BIGSERIAL PRIMARY KEY,
    case_id BIGINT,
    case_type VARCHAR(50),
    case_description TEXT,
    sentencing_suggestion TEXT,
    sentence_min_months INT,
    sentence_max_months INT,
    probation_suggestion BOOLEAN,
    fine_suggestion VARCHAR(200),
    trial_prediction VARCHAR(200),
    win_probability DECIMAL(5,4),
    confidence_level DECIMAL(5,4),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 裁判参考表
CREATE TABLE IF NOT EXISTS judgment_reference (
    id BIGSERIAL PRIMARY KEY,
    case_number VARCHAR(100),
    case_type VARCHAR(50),
    court_level VARCHAR(20),
    judgment_result VARCHAR(200),
    sentence VARCHAR(200),
    judgment_date DATE,
    key_facts TEXT,
    judgment_reason TEXT,
    legal_basis TEXT,
    key_points TEXT,
    similarity_score DECIMAL(5,4),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==================== 企业合规相关表 ====================

-- 企业表
CREATE TABLE IF NOT EXISTS company (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    credit_code VARCHAR(50),
    company_type VARCHAR(50),
    industry VARCHAR(50),
    registered_capital DECIMAL(15,2),
    business_scope TEXT,
    registered_address VARCHAR(500),
    contact_person VARCHAR(50),
    contact_phone VARCHAR(20),
    email VARCHAR(100),
    risk_level VARCHAR(20) DEFAULT 'LOW',
    compliance_score INT DEFAULT 100,
    last_check_time TIMESTAMP,
    next_check_time TIMESTAMP,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 合规审查表
CREATE TABLE IF NOT EXISTS compliance_review (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL,
    review_type VARCHAR(30),
    title VARCHAR(200),
    scope TEXT,
    result TEXT,
    risk_level VARCHAR(20),
    risk_score INT,
    issue_count INT DEFAULT 0,
    serious_issue_count INT DEFAULT 0,
    suggestion_count INT DEFAULT 0,
    issues TEXT,
    suggestions TEXT,
    status VARCHAR(20) DEFAULT 'PENDING',
    reviewer VARCHAR(50),
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (company_id) REFERENCES company(id)
);

-- 合规风险表
CREATE TABLE IF NOT EXISTS compliance_risk (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL,
    risk_type VARCHAR(50),
    risk_name VARCHAR(200),
    description TEXT,
    risk_level VARCHAR(20),
    risk_score INT,
    impact_scope VARCHAR(100),
    potential_consequences TEXT,
    probability DECIMAL(5,4),
    recommended_actions TEXT,
    responsible_dept VARCHAR(50),
    responsible_person VARCHAR(50),
    deadline TIMESTAMP,
    remediation_status VARCHAR(20) DEFAULT 'PENDING',
    remark TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (company_id) REFERENCES company(id)
);

-- ==================== 语音对话相关表 ====================

-- 语音会话表
CREATE TABLE IF NOT EXISTS speech_session (
    id BIGSERIAL PRIMARY KEY,
    session_id VARCHAR(64) NOT NULL UNIQUE,
    user_id BIGINT,
    session_type VARCHAR(30),
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    duration INT,
    message_count INT DEFAULT 0,
    asr_error_count INT DEFAULT 0,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 语音消息表
CREATE TABLE IF NOT EXISTS speech_message (
    id BIGSERIAL PRIMARY KEY,
    session_id BIGINT NOT NULL,
    direction VARCHAR(10),
    asr_text TEXT,
    tts_text TEXT,
    audio_url VARCHAR(500),
    synthesized_audio_url VARCHAR(500),
    audio_duration DECIMAL(10,2),
    confidence DECIMAL(5,4),
    success BOOLEAN DEFAULT TRUE,
    error_message VARCHAR(500),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES speech_session(id)
);

-- ==================== 索引 ====================

-- ==================== 用户表索引 ====================

-- 手机号唯一索引：用于手机号登录验证
CREATE UNIQUE INDEX IF NOT EXISTS idx_user_phone 
ON sys_user(phone) WHERE phone IS NOT NULL;

-- 邮箱唯一索引：用于邮箱登录验证
CREATE UNIQUE INDEX IF NOT EXISTS idx_user_email 
ON sys_user(email) WHERE email IS NOT NULL;

-- 用户状态索引：用于筛选活跃用户
CREATE INDEX IF NOT EXISTS idx_user_status 
ON sys_user(status);

-- ==================== 咨询表索引 ====================

-- 用户查询历史记录：用户ID + 创建时间 联合索引
CREATE INDEX IF NOT EXISTS idx_consult_user_created 
ON consult_conversation(user_id, create_time DESC);

-- 会话状态索引：用于筛选活跃会话
CREATE INDEX IF NOT EXISTS idx_consult_status 
ON consult_conversation(status);

-- 消息创建时间索引：用于按时间查询消息
CREATE INDEX IF NOT EXISTS idx_consult_message_created 
ON consult_message(create_time DESC);

-- ==================== 法律文书表索引 ====================

-- 文书类型和状态联合索引：用于按类型和状态筛选
CREATE INDEX IF NOT EXISTS idx_document_type_status 
ON legal_document(template_id, status);

-- 用户文书查询：用户ID + 创建时间索引
CREATE INDEX IF NOT EXISTS idx_document_user_created 
ON legal_document(user_id, create_time DESC);

-- 文书标题索引：用于全文搜索
CREATE INDEX IF NOT EXISTS idx_document_title 
ON legal_document(title);

-- ==================== 案例表索引 ====================

-- 案例类型和案件日期联合索引：用于按类型和时间筛选
CREATE INDEX IF NOT EXISTS idx_case_type_date 
ON legal_case(case_type, case_date DESC);

-- 法院等级索引：用于按法院筛选
CREATE INDEX IF NOT EXISTS idx_case_court_level 
ON legal_case(court_level);

-- 判决结果索引：用于按结果筛选
CREATE INDEX IF NOT EXISTS idx_case_result 
ON legal_case(judgment_result);

-- 案例表原有索引
CREATE INDEX IF NOT EXISTS idx_case_type ON legal_case(case_type);
CREATE INDEX IF NOT EXISTS idx_case_date ON legal_case(case_date);
CREATE INDEX IF NOT EXISTS idx_case_court ON legal_case(court_level);

-- ==================== 合同表索引 ====================

-- 合同类型和用户ID联合索引
CREATE INDEX IF NOT EXISTS idx_contract_type_user 
ON contract(contract_type, user_id);

-- 合同状态索引：用于按状态筛选
CREATE INDEX IF NOT EXISTS idx_contract_status_date 
ON contract(status, create_time DESC);

-- 合同金额索引：用于按金额范围查询
CREATE INDEX IF NOT EXISTS idx_contract_amount 
ON contract(amount);

-- 合同表原有索引
CREATE INDEX IF NOT EXISTS idx_contract_type ON contract(contract_type);
CREATE INDEX IF NOT EXISTS idx_contract_status ON contract(status);

-- ==================== 案件分析表索引 ====================

-- 用户分析历史：用户ID + 状态索引
CREATE INDEX IF NOT EXISTS idx_analysis_user_status 
ON case_analysis(user_id, status);

-- 案件ID索引：用于关联查询
CREATE INDEX IF NOT EXISTS idx_analysis_case 
ON case_analysis(case_id);

-- ==================== 证据表索引 ====================

-- 用户证据查询：用户ID + 证据类型索引
CREATE INDEX IF NOT EXISTS idx_evidence_user_type 
ON evidence(user_id, evidence_type);

-- 案件ID索引：用于关联查询
CREATE INDEX IF NOT EXISTS idx_evidence_case 
ON evidence(case_id);

-- ==================== 企业表索引 ====================

-- 企业名称索引：用于企业搜索
CREATE INDEX IF NOT EXISTS idx_company_name 
ON company(name);

-- 风险等级索引：用于按风险等级筛选
CREATE INDEX IF NOT EXISTS idx_company_risk_level 
ON company(risk_level);

-- 企业表原有索引
CREATE INDEX IF NOT EXISTS idx_company_industry ON company(industry);
CREATE INDEX IF NOT EXISTS idx_company_risk ON company(risk_level);

-- ==================== 合规审查表索引 ====================

-- 公司审查历史：公司ID + 状态索引
CREATE INDEX IF NOT EXISTS idx_compliance_company_status 
ON compliance_review(company_id, status);

-- 审查类型索引：用于按类型筛选
CREATE INDEX IF NOT EXISTS idx_compliance_type 
ON compliance_review(review_type);

-- ==================== 裁判参考表索引 ====================

-- 案例类型索引：用于相似案例检索
CREATE INDEX IF NOT EXISTS idx_judgment_type 
ON judgment_reference(case_type);

-- 相似度得分索引：用于按相似度排序
CREATE INDEX IF NOT EXISTS idx_judgment_similarity 
ON judgment_reference(similarity_score DESC);

-- ==================== 语音会话表索引 ====================

-- 用户会话查询：用户ID + 状态索引
CREATE INDEX IF NOT EXISTS idx_speech_user_status 
ON speech_session(user_id, status);

-- 消息原有索引
CREATE INDEX IF NOT EXISTS idx_consult_conv ON consult_message(conversation_id);
CREATE INDEX IF NOT EXISTS idx_speech_session ON speech_message(session_id);

-- 插入默认管理员用户
INSERT INTO sys_user (username, password, role, real_name)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt.RNxu', 'ADMIN', '系统管理员')
ON CONFLICT (username) DO NOTHING;

-- 插入默认文书模板
INSERT INTO document_template (name, category, description) VALUES
('民事起诉状', '民事诉讼', '用于向人民法院提起民事诉讼的官方文书'),
('民事答辩状', '民事诉讼', '被告针对原告起诉进行答辩的文书'),
('劳动合同', '合同协议', '规范劳动关系的合同文本'),
('房屋租赁合同', '合同协议', '房屋租赁双方权利义务的合同文本'),
('授权委托书', '法律文书', '委托他人代理法律事务的文书');

-- 插入示例案例
INSERT INTO legal_case (case_number, case_type, title, court_level, judgment_result, case_facts, judgment_reason, legal_basis)
VALUES 
('(2024)民初字第1234号', '民事纠纷', '合同违约纠纷案', '基层人民法院', '判令被告支付原告违约金10万元', 
 '原被告签订合同后，被告未按约定履行义务', '被告行为构成违约，应承担违约责任', '《中华人民共和国合同法》第一百零七条'),
('(2024)刑初字第567号', '刑事案件', '盗窃罪案', '基层人民法院', '判处有期徒刑六个月', 
 '被告人秘密窃取他人财物', '被告人行为构成盗窃罪', '《中华人民共和国刑法》第二百六十四条');

SELECT 'Database initialized successfully!' AS result;
