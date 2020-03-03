package org.example;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
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

        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery(FILED, "部落冲突");
        QueryStringQueryBuilder queryStringQueryBuilder = QueryBuilders.queryStringQuery("部落冲突");
        queryStringQueryBuilder.defaultField("searchInfo.appAnalyzerName");
        queryStringQueryBuilder.queryName("部落冲突");


        DisMaxQueryBuilder disMaxQueryBuilder = QueryBuilders.disMaxQuery()
                .add(QueryBuilders.termQuery(FILED, "部落冲突"))
                .add(QueryBuilders.termQuery(FILED, "部落冲"))
                .add(QueryBuilders.termQuery(FILED, "部落"))
                .boost(1.2f)
                .tieBreaker(0.7f);


        String name ="部落冲突";
        String regex = "[^a-zA-Z0-9\\u4E00-\\u9FA5]";
        name = name.replaceAll(regex, "").toLowerCase();
//        NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("searchInfo", QueryBuilders.termQuery("searchInfo.appAnalyzerName", name), ScoreMode.None);
//        nestedQueryBuilder.query().queryName();

//        boolBuilder.must(nestedQueryBuilder);

        SearchSourceBuilder searchSourceBuilder = EsUtils.listByQueryBuild(matchQueryBuilder, "");
        SearchRequest searchRequest = EsUtils.getSearchRequest(searchSourceBuilder,
                "index_app_info_v4_test", IndexBuilder.getType(AppInfo.class));

        SearchResponse response = EsUtils.getSearchResponse(restHighLevelClient, searchRequest);

        List<AppInfo> appInfoList = EsUtils.hitResultToObj(response.getHits(), AppInfo.class);
        for (AppInfo appInfo : appInfoList) {
            System.out.println(JSON.toJSONString(appInfo));
        }


    }
}
