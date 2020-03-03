package org.example;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpHost;
import org.apache.lucene.queries.function.FunctionQuery;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.lucene.search.function.CombineFunction;
import org.elasticsearch.common.lucene.search.function.FunctionScoreQuery;
import org.elasticsearch.index.query.*;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilder;
import org.elasticsearch.index.query.functionscore.WeightBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.example.es.plugin.EsUtils;
import org.example.es.plugin.IndexBuilder;
import org.example.es.plugin.es.ElasticClientDecorator;
import org.example.es.plugin.es.ElasticsProperties;
import org.example.es.plugin.module.AppInfo;

import java.util.List;

/**
 * Hello world!
 *
 */
public class App 
{

    private final static String FILED = "searchInfo.appAnalyzerName";
    public static void main( String[] args ){
        ElasticsProperties elasticsProperties = new ElasticsProperties();
        elasticsProperties.setClusterNodes("es-cn-v0h15do5q000am0sk.public.elasticsearch.aliyuncs.com");
        elasticsProperties.setPassword("zhangkongElastic#");
        elasticsProperties.setPort(9200);
        elasticsProperties.setUserName("elastic");
        RestHighLevelClient restHighLevelClient = new ElasticClientDecorator(
                new HttpHost(elasticsProperties.getClusterNodes(), elasticsProperties.getPort()),
                elasticsProperties.getUserName(), elasticsProperties.getPassword()).getRestHighLevelClient();


//        SearchSourceBuilder searchSourceBuilder = EsUtils.listByQueryBuild(getNestedQueryBuilder(), "");

        SearchSourceBuilder searchSourceBuilder = EsUtils.pageByQueryBuild(getFunctionScoreQueryBuilder(), 1, 100,"");


        SearchRequest searchRequest = EsUtils.getSearchRequest(searchSourceBuilder,
                "index_app_info_v4_test", IndexBuilder.getType(AppInfo.class));


        SearchResponse response = EsUtils.getSearchResponse(restHighLevelClient, searchRequest);

        List<AppInfo> appInfoList = EsUtils.hitResultToObj(response.getHits(), AppInfo.class);
        for (AppInfo appInfo : appInfoList) {
            System.out.println(JSON.toJSONString(appInfo));
        }


    }

    public static FunctionScoreQueryBuilder getFunctionScoreQueryBuilder() {
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();

        FunctionScoreQueryBuilder.FilterFunctionBuilder[] filterFunctionBuilders = new FunctionScoreQueryBuilder.FilterFunctionBuilder[3];
        ScoreFunctionBuilder<WeightBuilder> scoreFunctionBuilder = new WeightBuilder();
        scoreFunctionBuilder.setWeight(5000);
        NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("searchInfo", QueryBuilders.termQuery(FILED, "植物大战僵尸2天空之城"), ScoreMode.None);
        FunctionScoreQueryBuilder.FilterFunctionBuilder category = new FunctionScoreQueryBuilder.FilterFunctionBuilder(nestedQueryBuilder, scoreFunctionBuilder);
        filterFunctionBuilders[0] = category;

        ScoreFunctionBuilder<WeightBuilder> scoreFunctionBuilder1 = new WeightBuilder();
        scoreFunctionBuilder1.setWeight(30);
        NestedQueryBuilder nestedQueryBuilder1 = QueryBuilders.nestedQuery("searchInfo", QueryBuilders.termQuery(FILED, "植物大战僵尸2"), ScoreMode.None);
        FunctionScoreQueryBuilder.FilterFunctionBuilder category1 = new FunctionScoreQueryBuilder.FilterFunctionBuilder(nestedQueryBuilder1, scoreFunctionBuilder);
        filterFunctionBuilders[1] = category1;

        ScoreFunctionBuilder<WeightBuilder> scoreFunctionBuilder2 = new WeightBuilder();
        scoreFunctionBuilder2.setWeight(1);
        NestedQueryBuilder nestedQueryBuilder2 = QueryBuilders.nestedQuery("searchInfo", QueryBuilders.termQuery(FILED, "植物大战"), ScoreMode.None);
        FunctionScoreQueryBuilder.FilterFunctionBuilder category2 = new FunctionScoreQueryBuilder.FilterFunctionBuilder(nestedQueryBuilder2, scoreFunctionBuilder);
        filterFunctionBuilders[2] = category2;

        FunctionScoreQueryBuilder query = QueryBuilders.functionScoreQuery(filterFunctionBuilders)
                .scoreMode(FunctionScoreQuery.ScoreMode.SUM).boostMode(CombineFunction.SUM);
        return query;
    }

    public static BoolQueryBuilder getQueryStringBuilder() {
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        QueryStringQueryBuilder queryStringQueryBuilder = QueryBuilders.queryStringQuery("部落冲突");
        queryStringQueryBuilder.defaultField(FILED);
        NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("searchInfo", queryStringQueryBuilder, ScoreMode.None);
        boolBuilder.must(nestedQueryBuilder);
        return boolBuilder;
    }

    public static BoolQueryBuilder getNestedQueryBuilder() {
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        BoolQueryBuilder nestedBoolQueryBuilder = QueryBuilders.boolQuery();
        nestedBoolQueryBuilder.must(QueryBuilders.matchQuery(FILED, "部落冲突"));

        String name ="部落冲突";
        String regex = "[^a-zA-Z0-9\\u4E00-\\u9FA5]";
        name = name.replaceAll(regex, "").toLowerCase();
        NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("searchInfo", nestedBoolQueryBuilder, ScoreMode.None);
        nestedQueryBuilder.query().queryName();
        boolBuilder.must(nestedQueryBuilder);
        return boolBuilder;
    }
}
