package org.example.es.plugin.module;

import lombok.Getter;
import lombok.Setter;
import org.example.es.plugin.annotations.FieldType;
import org.example.es.plugin.annotations.Property;

/**
 * 类描述：t_app
 * -------------History------------------
 * DATE                 AUTHOR         VERSION        DESCRIPTION
 * 2019-05-11 16:38:31 CodeGenerator         v1.0        自动生成
 *
 * @author <a href="mailto:CodeGenerator@zhangkongapp.com">CodeGenerator</a>
 */


@Getter
@Setter
public class App {

    @Property(type = FieldType.Long)
    private Long id;

    @Property(type = FieldType.Text, analyzer = "charSplit")
    private String name;

}