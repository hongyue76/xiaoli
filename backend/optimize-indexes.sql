-- =====================================================
-- 索引优化脚本
-- 提升数据库查询性能
-- =====================================================

-- 注意：执行此脚本前请先执行 init-database.sql 或 deployment/docker/init/sql/init.sql

-- =====================================================
-- 1. 咨询记录表索引优化
-- =====================================================

-- 用户查询历史记录：用户ID + 创建时间 联合索引
-- 使用场景：用户查看自己的咨询历史，按时间倒序排列
CREATE INDEX IF NOT EXISTS idx_consult_user_created 
ON consult_conversations(user_id, create_time DESC);

-- 消息查询：会话ID索引（已存在，保持）
-- CREATE INDEX IF NOT EXISTS idx_consult_conversation_id 
-- ON consult_messages(conversation_id);

-- 消息创建时间索引：用于按时间查询消息
CREATE INDEX IF NOT EXISTS idx_consult_message_created 
ON consult_messages(create_time DESC);

-- 会话状态索引：用于筛选活跃会话
CREATE INDEX IF NOT EXISTS idx_consult_status 
ON consult_conversations(status);


-- =====================================================
-- 2. 法律文书表索引优化
-- =====================================================

-- 文书类型和状态联合索引：用于按类型和状态筛选
-- 使用场景：用户筛选特定类型和状态的文书
CREATE INDEX IF NOT EXISTS idx_document_type_status 
ON legal_document(template_id, status);

-- 用户文书查询：用户ID + 创建时间索引
-- 使用场景：用户查看自己的文书历史
CREATE INDEX IF NOT EXISTS idx_document_user_created 
ON legal_document(user_id, create_time DESC);

-- 文书标题索引：用于全文搜索（如果需要模糊匹配）
CREATE INDEX IF NOT EXISTS idx_document_title 
ON legal_document(title);


-- =====================================================
-- 3. 案例表索引优化
-- =====================================================

-- 法院和法官索引：用于案例检索
-- 使用场景：按法院或法官筛选案例
CREATE INDEX IF NOT EXISTS idx_case_court 
ON legal_case(court_level);

-- 案例类型和案件日期联合索引：用于按类型和时间筛选
CREATE INDEX IF NOT EXISTS idx_case_type_date 
ON legal_case(case_type, case_date DESC);

-- 案号唯一索引（已存在，保持）
-- CREATE UNIQUE INDEX IF NOT EXISTS idx_case_number 
-- ON legal_case(case_number);

-- 判决结果索引：用于按结果筛选
CREATE INDEX IF NOT EXISTS idx_case_result 
ON legal_case(judgment_result);


-- =====================================================
-- 4. 用户表索引优化
-- =====================================================

-- 手机号唯一索引：用于手机号登录验证
CREATE UNIQUE INDEX IF NOT EXISTS idx_user_phone 
ON sys_user(phone) WHERE phone IS NOT NULL;

-- 邮箱唯一索引：用于邮箱登录验证
CREATE UNIQUE INDEX IF NOT EXISTS idx_user_email 
ON sys_user(email) WHERE email IS NOT NULL;

-- 用户名唯一索引（已存在，保持）
-- CREATE UNIQUE INDEX IF NOT EXISTS idx_user_username 
-- ON sys_user(username);

-- 用户状态索引：用于筛选活跃用户
CREATE INDEX IF NOT EXISTS idx_user_status 
ON sys_user(status);


-- =====================================================
-- 5. 合同表索引优化
-- =====================================================

-- 合同类型索引：用于按类型筛选
CREATE INDEX IF NOT EXISTS idx_contract_type_user 
ON contract(contract_type, user_id);

-- 合同状态索引：用于按状态筛选
CREATE INDEX IF NOT EXISTS idx_contract_status_date 
ON contract(status, create_time DESC);

-- 合同金额索引：用于按金额范围查询
CREATE INDEX IF NOT EXISTS idx_contract_amount 
ON contract(amount);


-- =====================================================
-- 6. 案件分析表索引优化
-- =====================================================

-- 用户分析历史：用户ID + 状态索引
CREATE INDEX IF NOT EXISTS idx_analysis_user_status 
ON case_analysis(user_id, status);

-- 案件ID索引：用于关联查询
CREATE INDEX IF NOT EXISTS idx_analysis_case 
ON case_analysis(case_id);


-- =====================================================
-- 7. 证据表索引优化
-- =====================================================

-- 用户证据查询：用户ID + 证据类型索引
CREATE INDEX IF NOT EXISTS idx_evidence_user_type 
ON evidence(user_id, evidence_type);

-- 案件ID索引：用于关联查询
CREATE INDEX IF NOT EXISTS idx_evidence_case 
ON evidence(case_id);


-- =====================================================
-- 8. 合规审查表索引优化
-- =====================================================

-- 公司审查历史：公司ID + 状态索引
CREATE INDEX IF NOT EXISTS idx_compliance_company_status 
ON compliance_review(company_id, status);

-- 审查类型索引：用于按类型筛选
CREATE INDEX IF NOT EXISTS idx_compliance_type 
ON compliance_review(review_type);


-- =====================================================
-- 9. 裁判参考表索引优化
-- =====================================================

-- 案例类型索引：用于相似案例检索
CREATE INDEX IF NOT EXISTS idx_judgment_type 
ON judgment_reference(case_type);

-- 相似度得分索引：用于按相似度排序
CREATE INDEX IF NOT EXISTS idx_judgment_similarity 
ON judgment_reference(similarity_score DESC);


-- =====================================================
-- 10. 企业表索引优化
-- =====================================================

-- 企业名称索引：用于企业搜索
CREATE INDEX IF NOT EXISTS idx_company_name 
ON company(name);

-- 风险等级索引：用于按风险等级筛选
CREATE INDEX IF NOT EXISTS idx_company_risk_level 
ON company(risk_level);


-- =====================================================
-- 11. 语音会话表索引优化
-- =====================================================

-- 用户会话查询：用户ID + 状态索引
CREATE INDEX IF NOT EXISTS idx_speech_user_status 
ON speech_session(user_id, status);

-- 会话ID索引（已存在，保持）
-- CREATE UNIQUE INDEX IF NOT EXISTS idx_speech_session_id 
-- ON speech_session(session_id);


-- =====================================================
-- 索引使用统计查询
-- =====================================================

-- 查看所有索引
SELECT 
    schemaname,
    tablename,
    indexname,
    indexdef
FROM pg_indexes
WHERE schemaname = 'public'
ORDER BY tablename, indexname;

-- 查看索引大小
SELECT 
    schemaname,
    tablename,
    indexname,
    pg_size_pretty(pg_relation_size(indexrelid)) AS index_size
FROM pg_stat_user_indexes
WHERE schemaname = 'public'
ORDER BY pg_relation_size(indexrelid) DESC;

-- 查看索引使用情况（需要运行一段时间后查询）
SELECT 
    schemaname,
    tablename,
    indexname,
    idx_scan AS index_scans,
    idx_tup_read AS tuples_read,
    idx_tup_fetch AS tuples_fetched
FROM pg_stat_user_indexes
WHERE schemaname = 'public'
ORDER BY idx_scan DESC;


-- =====================================================
-- 索引维护建议
-- =====================================================

-- 定期分析表（建议每周执行一次）
-- ANALYZE;

-- 重建索引（建议每月执行一次，在业务低峰期）
-- REINDEX TABLE legal_case;
-- REINDEX TABLE contract;
-- REINDEX TABLE legal_document;

-- 查看膨胀的索引
SELECT 
    schemaname,
    tablename,
    indexname,
    pg_size_pretty(pg_relation_size(indexrelid)) AS index_size,
    pg_size_pretty(pg_relation_size(indexrelid) - pg_relation_size(indrelid)) AS wasted_space
FROM pg_stat_user_indexes
WHERE schemaname = 'public'
    AND pg_relation_size(indexrelid) - pg_relation_size(indrelid) > 1000000
ORDER BY wasted_space DESC;


-- =====================================================
-- 性能优化提示
-- =====================================================

-- 1. 联合索引字段顺序很重要，将选择性高的字段放在前面
-- 2. 索引不是越多越好，过多的索引会影响写入性能
-- 3. 定期监控索引使用情况，删除未使用的索引
-- 4. 对于大表，考虑使用部分索引（WHERE 条件）
-- 5. 对于文本搜索，考虑使用 PostgreSQL 的全文索引功能

-- 示例：创建部分索引
-- CREATE INDEX idx_active_users ON sys_user(username) WHERE status = 'ACTIVE';

-- 示例：创建全文索引
-- CREATE INDEX idx_document_fulltext ON legal_document USING gin(to_tsvector('chinese', title || ' ' || content));


SELECT '索引优化完成！' AS result;
