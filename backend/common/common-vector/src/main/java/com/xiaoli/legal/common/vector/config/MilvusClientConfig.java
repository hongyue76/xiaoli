package com.xiaoli.legal.common.vector.config;

import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Milvus客户端配置
 */
@Configuration
@ConditionalOnProperty(prefix = "milvus", name = "enabled", havingValue = "true")
public class MilvusClientConfig {

    private static final Logger log = LoggerFactory.getLogger(MilvusClientConfig.class);

    @Bean
    @ConditionalOnMissingBean
    public MilvusServiceClient milvusClient(MilvusProperties properties) {
        log.info("初始化Milvus客户端 {}:{}", properties.getHost(), properties.getPort());

        ConnectParam.Builder builder = ConnectParam.newBuilder()
                .withHost(properties.getHost())
                .withPort(properties.getPort())
                .withConnectTimeout(properties.getTimeout(), java.util.concurrent.TimeUnit.SECONDS);

        if (properties.getUsername() != null && properties.getPassword() != null) {
            builder.withAuthorization(properties.getUsername(), properties.getPassword());
        }

        return new MilvusServiceClient(builder.build());
    }
}
