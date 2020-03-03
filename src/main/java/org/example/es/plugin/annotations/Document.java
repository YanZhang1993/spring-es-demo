package org.example.es.plugin.annotations;


import java.lang.annotation.*;

/**
 * 类描述：Document
 * <p>
 * <pre>
 * -------------History------------------
 *   DATE                     AUTHOR         VERSION        DESCRIPTION
 *   2019年04月11日 15:28      chennengcheng       V01.00.001		  新增内容
 * </pre>
 *
 * @author <a href="mailto:chennengcheng@zhangkongapp.com">陈能成</a>
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Document {

    /**
     * 索引名称
     */
    String indexName();

    /**
     * 索引类型
     */
    String type() default "";

    /**
     * 分片数
     */
    short shards() default 5;

    /**
     * 副本数
     */
    short replicas() default 1;

    /**
     * 刷新间隔
     */
    String refreshInterval() default "1s";
}
