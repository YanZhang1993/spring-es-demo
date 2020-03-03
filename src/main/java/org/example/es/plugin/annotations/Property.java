package org.example.es.plugin.annotations;

import java.lang.annotation.*;

/**
 * 类描述：属性
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
@Inherited
public @interface Property {

    FieldType type() default FieldType.Auto;

    boolean index() default true;

    DateFormat format() default DateFormat.none;

    String pattern() default "";

    boolean store() default false;

    boolean fielddata() default false;

    String searchAnalyzer() default "";

    String analyzer() default "";

    String normalizer() default "";

    String[] ignoreFields() default {};

    boolean includeInParent() default false;

    String[] copyTo() default {};
}
