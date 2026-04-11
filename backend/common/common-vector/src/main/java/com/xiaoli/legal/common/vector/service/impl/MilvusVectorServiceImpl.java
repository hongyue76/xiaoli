package com.xiaoli.legal.common.vector.service.impl;

import com.xiaoli.legal.common.vector.config.MilvusProperties;
import com.xiaoli.legal.common.vector.dto.SearchRequest;
import com.xiaoli.legal.common.vector.dto.SearchResult;
import com.xiaoli.legal.common.vector.service.VectorService;
import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.DataType;
import io.milvus.grpc.DescribeCollectionResponse;
import io.milvus.grpc.MutationResult;
import io.milvus.grpc.SearchResults;
import io.milvus.param.*;
import io.milvus.param.collection.*;
import io.milvus.param.dml.DeleteParam;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.QueryParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.param.index.CreateIndexParam;
import io.milvus.response.SearchResultsWrapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Milvus向量服务实现
 */
@Service
public class MilvusVectorServiceImpl implements VectorService {

    private static final Logger log = LoggerFactory.getLogger(MilvusVectorServiceImpl.class);

    private final MilvusServiceClient milvusClient;
    private final MilvusProperties properties;

    public MilvusVectorServiceImpl(MilvusServiceClient milvusClient, MilvusProperties properties) {
        this.milvusClient = milvusClient;
        this.properties = properties;
    }

    @PostConstruct
    public void init() {
        log.info("Milvus向量服务初始化完成");
    }

    @Override
    public SearchResult search(SearchRequest request) {
        List<String> outputFields = request.getOutputFields() != null
                ? Arrays.asList(request.getOutputFields())
                : Collections.singletonList("*");

        SearchParam.Builder builder = SearchParam.newBuilder()
                .withCollectionName(request.getCollectionName())
                .withVectorFieldName(request.getVectorField())
                .withTopK(request.getLimit())
                .withVectors(Collections.singletonList(request.getVector()))
                .withOutFields(outputFields);

        if (request.getFilter() != null && !request.getFilter().isEmpty()) {
            builder.withExpr(request.getFilter());
        }

        if (request.getSearchParams() != null) {
            builder.withParams(request.getSearchParams());
        }

        SearchParam searchParam = builder.build();

        R<SearchResults> response = milvusClient.search(searchParam);

        if (response.getStatus() != R.Status.Success.getCode()) {
            throw new RuntimeException("向量搜索失败: " + response.getMessage());
        }

        SearchResults results = response.getData();
        SearchResultsWrapper wrapper = new SearchResultsWrapper(results.getResults());
        
        List<SearchResult.SearchResultItem> items = new ArrayList<>();

        // Get ID scores - the method takes a single int parameter for the offset
        List<SearchResultsWrapper.IDScore> resultList = wrapper.getIDScore(0);
        if (resultList != null) {
            for (SearchResultsWrapper.IDScore idsScore : resultList) {
                Map<String, Object> fields = new HashMap<>();
                for (String fieldName : outputFields) {
                    if (!"*".equals(fieldName)) {
                        Object fieldData = wrapper.getFieldData(fieldName, 0);
                        if (fieldData != null) {
                            fields.put(fieldName, fieldData);
                        }
                    }
                }
                // Use reflection to access private fields since SDK uses Lombok internally
                Object idObj = null;
                Float scoreObj = null;
                try {
                    java.lang.reflect.Field idField = SearchResultsWrapper.IDScore.class.getDeclaredField("id");
                    idField.setAccessible(true);
                    idObj = idField.get(idsScore);
                    java.lang.reflect.Field scoreField = SearchResultsWrapper.IDScore.class.getDeclaredField("score");
                    scoreField.setAccessible(true);
                    scoreObj = (Float) scoreField.get(idsScore);
                } catch (Exception e) {
                    log.warn("Failed to access IDScore fields via reflection", e);
                    continue;
                }
                SearchResult.SearchResultItem item = SearchResult.SearchResultItem.builder()
                        .id(idObj != null ? idObj.toString() : "")
                        .distance(scoreObj != null ? scoreObj : 0.0f)
                        .fields(fields)
                        .build();
                items.add(item);
            }
        }

        SearchResult result = SearchResult.builder()
                .results(items)
                .total((long) items.size())
                .build();

        log.debug("向量搜索完成, collection: {}, 结果数量: {}", request.getCollectionName(), items.size());
        return result;
    }

    @Override
    public List<SearchResult> batchSearch(List<SearchRequest> requests) {
        return requests.parallelStream()
                .map(this::search)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public void insert(String collectionName, List<float[]> vectors, List<Map<String, Object>> data) {
        if (vectors == null || vectors.isEmpty()) {
            return;
        }

        List<InsertParam.Field> fields = new ArrayList<>();

        // ID字段
        List<Object> ids = new ArrayList<>();
        for (int i = 0; i < vectors.size(); i++) {
            ids.add(UUID.randomUUID().toString());
        }
        fields.add(new InsertParam.Field("id", ids));

        // 向量字段
        List<Float> vectorList = new ArrayList<>();
        for (float[] vector : vectors) {
            for (float v : vector) {
                vectorList.add(v);
            }
        }
        fields.add(new InsertParam.Field("vector", vectorList));

        // 其他字段
        if (data != null && !data.isEmpty()) {
            Set<String> keys = data.get(0).keySet();
            for (String key : keys) {
                List<Object> values = new ArrayList<>();
                for (Map<String, Object> item : data) {
                    values.add(item.get(key));
                }
                fields.add(new InsertParam.Field(key, values));
            }
        }

        InsertParam insertParam = InsertParam.newBuilder()
                .withCollectionName(collectionName)
                .withFields(fields)
                .build();

        R<MutationResult> response = milvusClient.insert(insertParam);

        if (response.getStatus() != R.Status.Success.getCode()) {
            throw new RuntimeException("向量插入失败: " + response.getMessage());
        }

        log.info("向量插入完成, collection: {}, 数量: {}", collectionName, vectors.size());
    }

    @Override
    public void delete(String collectionName, List<Object> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        StringBuilder exprBuilder = new StringBuilder("id in [");
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) exprBuilder.append(",");
            exprBuilder.append("\"").append(ids.get(i)).append("\"");
        }
        exprBuilder.append("]");

        DeleteParam deleteParam = DeleteParam.newBuilder()
                .withCollectionName(collectionName)
                .withExpr(exprBuilder.toString())
                .build();

        R<MutationResult> response = milvusClient.delete(deleteParam);

        if (response.getStatus() != R.Status.Success.getCode()) {
            throw new RuntimeException("向量删除失败: " + response.getMessage());
        }

        log.info("向量删除完成, collection: {}, 数量: {}", collectionName, ids.size());
    }

    @Override
    public void createCollection(String collectionName, int dimension, String description) {
        // 检查是否已存在
        DescribeCollectionParam describeParam = DescribeCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .build();

        R<DescribeCollectionResponse> describeResponse = milvusClient.describeCollection(describeParam);

        if (describeResponse.getStatus() == R.Status.Success.getCode()) {
            log.info("集合已存在: {}", collectionName);
            return;
        }

        // 创建集合
        FieldType fieldType = FieldType.newBuilder()
                .withName("id")
                .withDataType(DataType.VarChar)
                .withMaxLength(256)
                .withPrimaryKey(true)
                .withAutoID(false)
                .build();

        FieldType vectorFieldType = FieldType.newBuilder()
                .withName("vector")
                .withDataType(DataType.FloatVector)
                .withDimension(dimension)
                .build();

        CreateCollectionParam createParam = CreateCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .withDescription(description)
                .addFieldType(fieldType)
                .addFieldType(vectorFieldType)
                .build();

        R<RpcStatus> response = milvusClient.createCollection(createParam);

        if (response.getStatus() != R.Status.Success.getCode()) {
            throw new RuntimeException("创建集合失败: " + response.getMessage());
        }

        log.info("创建集合成功: {}", collectionName);
    }

    @Override
    public void dropCollection(String collectionName) {
        DropCollectionParam dropParam = DropCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .build();

        R<RpcStatus> response = milvusClient.dropCollection(dropParam);

        if (response.getStatus() != R.Status.Success.getCode()) {
            throw new RuntimeException("删除集合失败: " + response.getMessage());
        }

        log.info("删除集合成功: {}", collectionName);
    }

    @Override
    public void createIndex(String collectionName, String fieldName) {
        CreateIndexParam indexParam = CreateIndexParam.newBuilder()
                .withCollectionName(collectionName)
                .withFieldName(fieldName)
                .withIndexType(IndexType.valueOf(properties.getIndexType()))
                .withMetricType(MetricType.valueOf(properties.getMetricType()))
                .withExtraParam(properties.getSearchParams())
                .build();

        R<RpcStatus> response = milvusClient.createIndex(indexParam);

        if (response.getStatus() != R.Status.Success.getCode()) {
            throw new RuntimeException("创建索引失败: " + response.getMessage());
        }

        log.info("创建索引成功: {}", collectionName);
    }

    @Override
    public void loadCollection(String collectionName) {
        LoadCollectionParam loadParam = LoadCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .build();

        R<RpcStatus> response = milvusClient.loadCollection(loadParam);

        if (response.getStatus() != R.Status.Success.getCode()) {
            throw new RuntimeException("加载集合失败: " + response.getMessage());
        }

        log.info("加载集合成功: {}", collectionName);
    }

    @Override
    public void releaseCollection(String collectionName) {
        ReleaseCollectionParam releaseParam = ReleaseCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .build();

        R<RpcStatus> response = milvusClient.releaseCollection(releaseParam);

        if (response.getStatus() != R.Status.Success.getCode()) {
            throw new RuntimeException("释放集合失败: " + response.getMessage());
        }

        log.info("释放集合成功: {}", collectionName);
    }
}
