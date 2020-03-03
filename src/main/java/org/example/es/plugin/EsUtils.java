package org.example.es.plugin;


import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.NestedSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 类描述：
 * <p>
 * <pre>
 * -------------History------------------
 *   DATE                      AUTHOR         VERSION             DESCRIPTION
 *  2019-05-30                    Sen            V1.0		  新增内容
 * </pre>
 *
 * @author <a href="mailto:chennengcheng@zhangkongapp.com">陈能成</a>
 */
public class EsUtils {
    /**
     * @return
     * @description: 搜索 公共调用
     * @params
     * @creator chennengcheng
     * @creatime 2019/4/15
     * @modify
     */
    public static SearchSourceBuilder listByQueryBuild(QueryBuilder queryBuilder, String includes) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(queryBuilder);
        if (StringUtils.isNotBlank(includes)) {
            sourceBuilder.fetchSource(includes.split(","), null);
        }

        return sourceBuilder;
    }

    /**
     * @return
     * @description: 搜索 公共调用
     * @params
     * @creator chennengcheng
     * @creatime 2019/4/15
     */
    public static SearchSourceBuilder pageByQueryBuild(QueryBuilder queryBuilder, int pageNum, int pageSize, String includes) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        if (Objects.nonNull(queryBuilder)) {
            sourceBuilder.query(queryBuilder);
        }

        sourceBuilder.from((pageNum - 1) * pageSize);
        sourceBuilder.size(pageSize);
        if (StringUtils.isNotBlank(includes)) {
            sourceBuilder.fetchSource(includes.trim().split(","), null);
        }

        return sourceBuilder;
    }

    /**
     * 排序字段
     *
     * @return
     */
    public static FieldSortBuilder sortField() {
        NestedSortBuilder nestedSortBuilder = new NestedSortBuilder("searchInfo");
        return SortBuilders.fieldSort("searchInfo.createTime").setNestedSort(nestedSortBuilder).order(SortOrder.DESC);
    }

    /**
     * 排序字段
     *
     * @param field
     * @return
     */
    public static FieldSortBuilder sortField(String field, SortOrder order) {
        FieldSortBuilder fsb = SortBuilders.fieldSort(field);
        fsb.order(order);
        return fsb;
    }

    /**
     * 排序字段
     *
     * @param field
     * @return
     */
    public static FieldSortBuilder includesField(String field) {
        FieldSortBuilder fsb = SortBuilders.fieldSort(field);
        fsb.order(SortOrder.DESC);
        return fsb;
    }

    /**
     * @return
     * @description: 搜索 返回 response
     * @params
     * @creator chennengcheng
     * @creatime 2019/4/15
     * @modify
     */
    public static SearchResponse getSearchResponse(RestHighLevelClient restHighLevelClient, SearchRequest searchRequest) {
        try {
            return restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            return new SearchResponse();
        }
    }

    /**
     * @return
     * @description: 设置索引和Type
     * @params
     * @creator chennengcheng
     * @creatime 2019/4/15
     * @modify
     */
    public static SearchRequest getSearchRequest(SearchSourceBuilder sourceBuilder, String indexName, String typeName) {
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.types(typeName);
        searchRequest.source(sourceBuilder);
        return searchRequest;
    }

    /**
     * 范围查询
     *
     * @param start
     * @param end
     * @param fileName
     */
    public static RangeQueryBuilder rangeQuery(Long start, Long end, String fileName) {
        if (Objects.isNull(start) && Objects.isNull(end)) {
            return null;
        }

        RangeQueryBuilder rangBuilder = QueryBuilders.rangeQuery(fileName);
        if (Objects.nonNull(start)) {
            rangBuilder.gte(start);
        }

        if (Objects.nonNull(end)) {
            rangBuilder.lte(end);
        }

        return rangBuilder;
    }

    public static String getResultDataIndexOne(SearchHits shs) {
        if (Objects.isNull(shs) || shs.totalHits == 0) {
            return "{}";
        }

        return shs.getHits()[0].getSourceAsString();
    }

    public static String getResultData(SearchHits shs) {
        if (Objects.isNull(shs) || shs.totalHits == 0) {
            return "[]";
        }

        return "[".concat(Arrays.stream(shs.getHits()).map(SearchHit::getSourceAsString).collect(Collectors.joining(","))).concat("]");
    }

    /**
     * 结果集转化为 实体
     *
     * @param shs
     * @return
     */
    public static <T> List<T> hitResultToObj(SearchHits shs, Class clazz) {
        List<T> resultData = new ArrayList<>();
        if (Objects.isNull(shs) || shs.totalHits == 0) {
            return new ArrayList<>();
        }

        for (SearchHit hit : shs) {
            T object = JSONObject.parseObject(hit.getSourceAsString(), (Type) clazz);
            resultData.add(object);
        }

        return resultData;
    }
}
