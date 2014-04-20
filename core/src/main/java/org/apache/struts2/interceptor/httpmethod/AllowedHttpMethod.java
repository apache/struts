package org.apache.struts2.interceptor.httpmethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation to limit with what http method action or action's method can be called
 *
 * @see HttpMethodInterceptor
 * @since 2.3.18
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AllowedHttpMethod {

    HttpMethod[] value() default {};

}
