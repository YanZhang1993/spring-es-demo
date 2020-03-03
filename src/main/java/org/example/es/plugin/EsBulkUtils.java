package org.example.es.plugin;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.ActiveShardCount;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.security.RefreshPolicy;
import org.elasticsearch.common.unit.TimeValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * 类描述：批量操作类
 * <p>
 * <pre>
 * -------------History------------------
 *   DATE                     AUTHOR         VERSION        DESCRIPTION
 *   2019年04月11日 15:28      chennengcheng       V01.00.001		  新增内容
 * </pre>
 *
 * @author <a href="mailto:chennengcheng@zhangkongapp.com">陈能成</a>
 */
public class EsBulkUtils {

    private EsBulkUtils() {
    }

    /**
     * 批量新增
     *
     * @param client
     * @param indexRequests
     * @param refreshPolicy WAIT_UNTIL (等待自动刷新)
     * @return
     */
    public static boolean bulkRequest(RestHighLevelClient client, List<IndexRequest> indexRequests, WriteRequest.RefreshPolicy refreshPolicy) {
        BulkRequest bulkRequest = getBulkRequest(indexRequests, refreshPolicy);
        return bulkData(bulkRequest, client);
    }

    /**
     * 批量新增 (异步)
     *
     * @param client
     * @param indexRequests
     * @param refreshPolicy WAIT_UNTIL (等待自动刷新)
     * @return
     */
    public static void asyncBulkRequest(RestHighLevelClient client, List<IndexRequest> indexRequests, WriteRequest.RefreshPolicy refreshPolicy) {
        BulkRequest bulkRequest = getBulkRequest(indexRequests, refreshPolicy);
        asyncBulkData(bulkRequest, client);
    }

    private static BulkRequest getBulkRequest(List<IndexRequest> indexRequests, WriteRequest.RefreshPolicy refreshPolicy) {
        BulkRequest request = new BulkRequest();
        indexRequests.forEach(request::add);
        request.setRefreshPolicy(refreshPolicy);
        return request;
    }

    /**
     * 检查结果集是否有效
     *
     * @param t
     * @param function1
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> String checkSearchValid(T t, Function<T, R> function1) {
        R r = function1.apply(t);
        if (Objects.nonNull(r)) {
            return JSONObject.toJSONString(r);
        }

        return "";
    }

    /**
     * 批量处理结果 返回
     *
     * @param request
     * @param client
     */
    private static boolean bulkData(BulkRequest request, RestHighLevelClient client) {
        try {
            BulkResponse bulkResponse = client.bulk(request, RequestOptions.DEFAULT);
            for (BulkItemResponse bulkItemResponse : bulkResponse) {

                if (Objects.nonNull(bulkItemResponse.getFailure())) {
                    continue;
                }

                DocWriteResponse itemResponse = bulkItemResponse.getResponse();
                if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.INDEX
                        || bulkItemResponse.getOpType() == DocWriteRequest.OpType.CREATE) {
                    IndexResponse indexResponse = (IndexResponse) itemResponse;
                } else if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.UPDATE) {
                    UpdateResponse updateResponse = (UpdateResponse) itemResponse;
                } else if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.DELETE) {
                    DeleteResponse deleteResponse = (DeleteResponse) itemResponse;
                }

            }
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    private static void asyncBulkData(BulkRequest request, RestHighLevelClient client) {
        client.bulkAsync(request, RequestOptions.DEFAULT, new ActionListener<BulkResponse>() {
            @Override
            public void onResponse(BulkResponse bulkItemResponses) {

            }

            @Override
            public void onFailure(Exception e) {
            }
        });
    }

    /**
     * 通过ID批量删除请求
     *
     * @param client
     * @param ids
     * @param clazz
     */
    public static void deleteByIds(RestHighLevelClient client, List<Long> ids, Class clazz) {
        List<DeleteRequest> deleteRequests = new ArrayList<>();
        for (Long id : ids) {
            deleteRequests.add(new DeleteRequest(IndexBuilder.getIndexName(clazz), IndexBuilder.getType(clazz), id.toString()));
        }

        BulkRequest request = new BulkRequest();
        for (DeleteRequest deleteRequest : deleteRequests) {
            request.add(deleteRequest);
        }
        request.timeout(TimeValue.timeValueMinutes(2));
        request.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        request.waitForActiveShards(ActiveShardCount.ALL);
        try {
            client.bulk(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
