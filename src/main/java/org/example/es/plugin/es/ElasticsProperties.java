package org.example.es.plugin.es;

import lombok.Data;

/**
 * 类描述：ES 配置类
 * <p>
 * <pre>
 * -------------History------------------
 *   DATE                     AUTHOR         VERSION        DESCRIPTION
 *   2018年03月09日 15:28      Bluesky       V01.00.001		  新增内容
 * </pre>
 *
 * @author <a href="mailto:huangbingxin@zhangkongapp.com">黄兵新</a>
 */
@Data
public class ElasticsProperties {

    /**
     * 名称
     */
    private String clusterName;

    /**
     * 节点
     */
    private String clusterNodes;

    /**
     * 端口号
     */
    private Integer port;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String password;


}
