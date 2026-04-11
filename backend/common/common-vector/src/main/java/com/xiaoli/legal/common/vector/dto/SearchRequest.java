package com.xiaoli.legal.common.vector.dto;

import java.util.List;
import java.util.Map;

/**
 * 向量搜索请求
 */
public class SearchRequest {

    /**
     * 集合名称
     */
    private String collectionName;

    /**
     * 查询向量
     */
    private float[] vector;

    /**
     * 向量字段名
     */
    private String vectorField = "vector";

    /**
     * 输出字段
     */
    private String[] outputFields;

    /**
     * 查询条件
     */
    private String filter;

    /**
     * 返回数量
     */
    private Integer limit = 10;

    /**
     * 搜索参数
     */
    private String searchParams;

    // Getters and Setters
    public String getCollectionName() { return collectionName; }
    public void setCollectionName(String collectionName) { this.collectionName = collectionName; }

    public float[] getVector() { return vector; }
    public void setVector(float[] vector) { this.vector = vector; }

    public String getVectorField() { return vectorField; }
    public void setVectorField(String vectorField) { this.vectorField = vectorField; }

    public String[] getOutputFields() { return outputFields; }
    public void setOutputFields(String[] outputFields) { this.outputFields = outputFields; }

    public String getFilter() { return filter; }
    public void setFilter(String filter) { this.filter = filter; }

    public Integer getLimit() { return limit; }
    public void setLimit(Integer limit) { this.limit = limit; }

    public String getSearchParams() { return searchParams; }
    public void setSearchParams(String searchParams) { this.searchParams = searchParams; }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String collectionName;
        private float[] vector;
        private String vectorField = "vector";
        private String[] outputFields;
        private String filter;
        private Integer limit = 10;
        private String searchParams;

        public Builder collectionName(String collectionName) { this.collectionName = collectionName; return this; }
        public Builder vector(float[] vector) { this.vector = vector; return this; }
        public Builder vectorField(String vectorField) { this.vectorField = vectorField; return this; }
        public Builder outputFields(String[] outputFields) { this.outputFields = outputFields; return this; }
        public Builder filter(String filter) { this.filter = filter; return this; }
        public Builder limit(Integer limit) { this.limit = limit; return this; }
        public Builder searchParams(String searchParams) { this.searchParams = searchParams; return this; }

        public SearchRequest build() {
            SearchRequest request = new SearchRequest();
            request.setCollectionName(collectionName);
            request.setVector(vector);
            request.setVectorField(vectorField);
            request.setOutputFields(outputFields);
            request.setFilter(filter);
            request.setLimit(limit);
            request.setSearchParams(searchParams);
            return request;
        }
    }
}
