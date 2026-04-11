package com.xiaoli.legal.common.vector.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Milvus配置
 */
@Configuration
@ConfigurationProperties(prefix = "milvus")
public class MilvusProperties {

    /**
     * 是否启用
     */
    private boolean enabled = false;

    /**
     * Host
     */
    private String host = "localhost";

    /**
     * Port
     */
    private Integer port = 19530;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 超时时间(秒)
     */
    private Integer timeout = 30;

    /**
     * 默认向量维度
     */
    private Integer dimension = 1536;

    /**
     * 索引类型
     */
    private String indexType = "IVF_FLAT";

    /**
     * 度量类型
     */
    private String metricType = "COSINE";

    /**
     * 聚类数量
     */
    private Integer nlist = 1024;

    /**
     * 搜索参数
     */
    private String searchParams = "{\"nprobe\": 10}";

    // Getters and Setters
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }

    public Integer getPort() { return port; }
    public void setPort(Integer port) { this.port = port; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Integer getTimeout() { return timeout; }
    public void setTimeout(Integer timeout) { this.timeout = timeout; }

    public Integer getDimension() { return dimension; }
    public void setDimension(Integer dimension) { this.dimension = dimension; }

    public String getIndexType() { return indexType; }
    public void setIndexType(String indexType) { this.indexType = indexType; }

    public String getMetricType() { return metricType; }
    public void setMetricType(String metricType) { this.metricType = metricType; }

    public Integer getNlist() { return nlist; }
    public void setNlist(Integer nlist) { this.nlist = nlist; }

    public String getSearchParams() { return searchParams; }
    public void setSearchParams(String searchParams) { this.searchParams = searchParams; }
}
