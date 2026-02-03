// 文件路径: mis-common/src/main/java/edu/scau/mis/common/annotation/AutoFill.java
package edu.scau.mis.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，用于标记需要自动填充字段的Mapper方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {
    // 你还可以添加一个枚举来区分是 INSERT 还是 UPDATE
    // OperationType type();
    boolean isInsert() default true;
}
