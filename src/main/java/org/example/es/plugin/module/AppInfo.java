package org.example.es.plugin.module;


import lombok.Getter;
import lombok.Setter;
import org.example.es.plugin.annotations.Document;
import org.example.es.plugin.annotations.FieldType;
import org.example.es.plugin.annotations.Id;
import org.example.es.plugin.annotations.Property;

/**
 * 类描述：
 * <p>
 * <pre>
 * -------------History------------------
 *   DATE                      AUTHOR         VERSION             DESCRIPTION
 *  2019-05-11                    Sen            V1.0		  新增内容
 * </pre>
 *
 * @author <a href="mailto:chennengcheng@zhangkongapp.com">陈能成</a>
 */
@Getter
@Setter
@Document(indexName = "index_app_info", type = "type_app_info", shards= 1, replicas= 0, refreshInterval = "2s")
public class AppInfo {

    @Id
    private Long id;

    @Property(type = FieldType.Nested)
    private App app;
}
