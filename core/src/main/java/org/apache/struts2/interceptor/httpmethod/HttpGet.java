package org.apache.struts2.interceptor.httpmethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation to allow call action or action's method via GET request only
 *
 * @see HttpMethodInterceptor
 * @since 2.3.18
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpGet {

    HttpMethod[] value() default { HttpMethod.GET };

}
