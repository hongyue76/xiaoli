# 腾讯元器 SDK for Java

基于腾讯元器智能体API的Java SDK，提供法律领域的智能对话和文本处理能力。

## 功能特性

- 智能对话生成
- 文本嵌入向量提取
- 法律文书润色
- 案例分析理解
- 合同条款审查

## 快速开始

### 添加Maven依赖

```xml
<dependency>
    <groupId>com.xiaoli.ai</groupId>
    <artifactId>xiaoli-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 配置

```yaml
tencent:
  yuanqi:
    base-url: https://api.yuanqi.tencent.com
    assistant-id: your-assistant-id
    token: your-token
```

> **注意**: 该SDK已更新为使用腾讯元器智能体API

### 使用示例

```java
import com.xiaoli.ai.sdk.XiaoliClient;
import com.xiaoli.ai.sdk.config.XiaoliConfig;
import com.xiaoli.ai.sdk.model.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Resource
    private XiaoliClient xiaoliClient;

    public void chat() {
        // 简单对话
        ChatRequest request = ChatRequest.builder()
                .message("您好，我想咨询一下离婚诉讼的程序")
                .temperature(0.7)
                .build();
        
        ChatResponse response = xiaoliClient.chat(request);
        System.out.println(response.getContent());
    }

    public void embedding() {
        // 文本向量化
        EmbeddingRequest request = EmbeddingRequest.builder()
                .text("合同违约责任的认定")
                .build();
        
        EmbeddingResponse response = xiaoliClient.embedding(request);
        float[] vector = response.getEmbedding();
    }

    public void legalConsult() {
        // 法律咨询
        LegalConsultRequest request = LegalConsultRequest.builder()
                .question("夫妻共同债务如何认定？")
                .caseType(CaseType.DIVORCE)
                .build();
        
        LegalConsultResponse response = xiaoliClient.legalConsult(request);
    }

    public void documentGenerate() {
        // 文书生成
        DocumentGenerateRequest request = DocumentGenerateRequest.builder()
                .docType(DocumentType.PLAINTIFF)
                .caseInfo(caseInfo)
                .build();
        
        DocumentGenerateResponse response = xiaoliClient.generateDocument(request);
    }
}
```

## API列表

### 对话生成
- `chat(ChatRequest)` - 通用对话
- `legalConsult(LegalConsultRequest)` - 法律咨询
- `caseAnalysis(CaseAnalysisRequest)` - 案件分析

### 文档处理
- `generateDocument(DocumentGenerateRequest)` - 文书生成
- `reviewContract(ReviewContractRequest)` - 合同审查
- `polishDocument(PolishDocumentRequest)` - 文书润色

### 向量处理
- `embedding(EmbeddingRequest)` - 文本向量化
- `semanticSearch(SemanticSearchRequest)` - 语义检索

### 语音处理
- `speechToText(SpeechToTextRequest)` - 语音转文字
- `textToSpeech(TextToSpeechRequest)` - 文字转语音

## 配置说明

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| xiaoli.api.key | API密钥 | - |
| xiaoli.api.base-url | API地址 | https://api.xiaoli.ai |
| xiaoli.api.model | 使用模型 | xiaoli-legal |
| xiaoli.api.timeout | 请求超时(ms) | 30000 |
| xiaoli.api.max-retries | 最大重试次数 | 3 |

## 错误处理

```java
try {
    ChatResponse response = xiaoliClient.chat(request);
} catch (XiaoliException e) {
    switch (e.getErrorCode()) {
        case "INVALID_API_KEY":
            // API密钥无效
            break;
        case "RATE_LIMIT":
            // 请求频率超限
            break;
        case "MODEL_NOT_FOUND":
            // 模型不存在
            break;
        default:
            // 其他错误
    }
}
```

## 许可证

MIT License
