package com.xiaoli.legal.common.vector.dto;

import java.util.List;
import java.util.Map;

/**
 * 向量搜索结果
 */
public class SearchResult {

    /**
     * 结果列表
     */
    private List<SearchResultItem> results;

    /**
     * 总数
     */
    private Long total;

    // Getters and Setters
    public List<SearchResultItem> getResults() { return results; }
    public void setResults(List<SearchResultItem> results) { this.results = results; }

    public Long getTotal() { return total; }
    public void setTotal(Long total) { this.total = total; }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private List<SearchResultItem> results;
        private Long total;

        public Builder results(List<SearchResultItem> results) { this.results = results; return this; }
        public Builder total(Long total) { this.total = total; return this; }

        public SearchResult build() {
            SearchResult result = new SearchResult();
            result.setResults(results);
            result.setTotal(total);
            return result;
        }
    }

    /**
     * 搜索结果项
     */
    public static class SearchResultItem {
        /**
         * ID
         */
        private Object id;

        /**
         * 距离/相似度
         */
        private Float distance;

        /**
         * 字段数据
         */
        private Map<String, Object> fields;

        // Getters and Setters
        public Object getId() { return id; }
        public void setId(Object id) { this.id = id; }

        public Float getDistance() { return distance; }
        public void setDistance(Float distance) { this.distance = distance; }

        public Map<String, Object> getFields() { return fields; }
        public void setFields(Map<String, Object> fields) { this.fields = fields; }

        // Builder
        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private Object id;
            private Float distance;
            private Map<String, Object> fields;

            public Builder id(Object id) { this.id = id; return this; }
            public Builder distance(Float distance) { this.distance = distance; return this; }
            public Builder fields(Map<String, Object> fields) { this.fields = fields; return this; }

            public SearchResultItem build() {
                SearchResultItem item = new SearchResultItem();
                item.setId(id);
                item.setDistance(distance);
                item.setFields(fields);
                return item;
            }
        }
    }
}
