package org.example.es.plugin.annotations;

import java.lang.annotation.*;

/**
 * 类描述：多属性
 * <p>
 * <pre>
 * -------------History------------------
 *   DATE                     AUTHOR         VERSION        DESCRIPTION
 *   2019年04月11日 15:28      chennengcheng       V01.00.001		  新增内容
 * </pre>
 *
 * @author <a href="mailto:chennengcheng@zhangkongapp.com">陈能成</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface MultiField {

    public Property mainField();

    public InnerField[] otherFields() default {};
}
