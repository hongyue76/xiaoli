package com.xiaoli.legal.common.vector.service;

import com.xiaoli.legal.common.vector.dto.SearchRequest;
import com.xiaoli.legal.common.vector.dto.SearchResult;

import java.util.List;
import java.util.Map;

/**
 * 向量服务接口
 */
public interface VectorService {

    /**
     * 搜索
     *
     * @param request 请求
     * @return 结果
     */
    SearchResult search(SearchRequest request);

    /**
     * 批量搜索
     *
     * @param requests 请求列表
     * @return 结果列表
     */
    List<SearchResult> batchSearch(List<SearchRequest> requests);

    /**
     * 插入向量
     *
     * @param collectionName 集合名
     * @param vectors        向量列表
     * @param data           关联数据
     */
    void insert(String collectionName, List<float[]> vectors, List<Map<String, Object>> data);

    /**
     * 删除向量
     *
     * @param collectionName 集合名
     * @param ids            ID列表
     */
    void delete(String collectionName, List<Object> ids);

    /**
     * 创建集合
     *
     * @param collectionName 集合名
     * @param dimension      维度
     * @param description   描述
     */
    void createCollection(String collectionName, int dimension, String description);

    /**
     * 删除集合
     *
     * @param collectionName 集合名
     */
    void dropCollection(String collectionName);

    /**
     * 创建索引
     *
     * @param collectionName 集合名
     * @param fieldName      字段名
     */
    void createIndex(String collectionName, String fieldName);

    /**
     * 加载集合到内存
     *
     * @param collectionName 集合名
     */
    void loadCollection(String collectionName);

    /**
     * 释放集合
     *
     * @param collectionName 集合名
     */
    void releaseCollection(String collectionName);
}
