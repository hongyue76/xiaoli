const express = require('express');
const cors = require('cors');
const { Pool } = require('pg');

const app = express();
const PORT = process.env.PORT || 3000;

// 数据库配置
let pool;
try {
    pool = new Pool({
        host: process.env.DB_HOST || 'localhost',
        port: process.env.DB_PORT || 5432,
        database: process.env.DB_NAME || 'xiaoli_legal',
        user: process.env.DB_USER || 'xiaoli',
        password: process.env.DB_PASSWORD || 'Xiaoli@2024',
        max: 10,
        idleTimeoutMillis: 30000,
        connectionTimeoutMillis: 5000,
    });
} catch (e) {
    console.log('数据库连接失败，使用模拟模式');
}

app.use(cors());
app.use(express.json());

// 首页
app.get('/', (req, res) => {
    res.json({
        name: '律法先锋 API',
        version: '1.0.0',
        description: '法律AI智能助手平台',
        endpoints: {
            health: 'GET /api/health',
            consult: 'POST /api/consult/chat',
            case: 'POST /api/case/search',
            law: 'POST /api/case/delilegal/law',
            document: 'POST /api/document/generate'
        }
    });
});

// 健康检查
app.get('/api/health', async (req, res) => {
    let dbStatus = 'disconnected';
    try {
        if (pool) {
            await pool.query('SELECT 1');
            dbStatus = 'connected';
        }
    } catch (e) {
        dbStatus = 'error: ' + e.message;
    }
    
    res.json({ 
        status: 'ok', 
        service: '律法先锋 API',
        version: '1.0.0',
        database: dbStatus,
        timestamp: new Date().toISOString()
    });
});

// 法律咨询
app.post('/api/consult/chat', async (req, res) => {
    try {
        const { messages } = req.body;
        const lastMessage = messages?.[messages.length - 1]?.content || '';
        
        // 模拟AI回复
        const response = {
            code: 200,
            data: {
                answer: `您好！我是律法先锋法律AI助手。

针对您的问题："${lastMessage}"

根据相关法律规定，建议您：

1. **收集证据**：保留相关合同、聊天记录、凭证等
2. **了解权利**：查阅相关法律法规
3. **协商解决**：首先尝试与对方协商
4. **法律途径**：如协商不成，可考虑诉讼或仲裁

⚠️ 免责声明：本回答仅供参考，不构成正式法律意见。如需专业法律帮助，请咨询执业律师。

如有其他问题，欢迎继续咨询！`,
                similarCases: []
            }
        };
        
        res.json(response);
    } catch (error) {
        res.status(500).json({ 
            code: 500, 
            message: '服务错误，请稍后重试' 
        });
    }
});

// 案例检索
app.post('/api/case/search', async (req, res) => {
    const { keyword, page = 1, pageSize = 10 } = req.body;
    
    // 尝试从数据库查询
    if (pool) {
        try {
            const result = await pool.query(
                'SELECT id, title, case_no, court, judge_date FROM cases WHERE title LIKE $1 LIMIT $2 OFFSET $3',
                [`%${keyword || ''}%`, pageSize, (page - 1) * pageSize]
            );
            return res.json({
                code: 200,
                data: {
                    total: result.rows.length,
                    list: result.rows,
                    page,
                    pageSize
                }
            });
        } catch (e) {
            console.log('案例查询失败:', e.message);
        }
    }
    
    res.json({
        code: 200,
        data: {
            total: 0,
            list: [],
            message: '请配置得理法搜API获取完整案例数据'
        }
    });
});

// 法规检索
app.post('/api/case/delilegal/law', async (req, res) => {
    const { keywords } = req.body;
    res.json({
        code: 200,
        data: {
            message: '法规检索功能',
            keywords: keywords,
            list: []
        }
    });
});

// 获取法规详情
app.get('/api/case/delilegal/law/detail', async (req, res) => {
    const { lawId, merge } = req.query;
    res.json({
        code: 200,
        data: {
            lawId: lawId,
            lawTitle: '中华人民共和国相关法律',
            lawDetailContent: '（实际使用时调用得理法搜API获取完整法规内容）',
            merge: merge === 'true'
        }
    });
});

// 文书生成
app.post('/api/document/generate', async (req, res) => {
    const { type, data } = req.body;
    res.json({
        code: 200,
        data: {
            message: '文书生成功能演示',
            type: type,
            content: '请配置AI服务获取完整文书内容'
        }
    });
});

// 启动服务器
app.listen(PORT, '0.0.0.0', () => {
    console.log(`律法先锋 API 服务已启动: http://0.0.0.0:${PORT}`);
    console.log(`健康检查: http://0.0.0.0:${PORT}/api/health`);
    console.log(`数据库: ${process.env.DB_HOST || 'localhost'}:${process.env.DB_PORT || 5432}`);
});
