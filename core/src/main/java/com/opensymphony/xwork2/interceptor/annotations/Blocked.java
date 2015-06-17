package com.opensymphony.xwork2.interceptor.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares that the given field should NOT be able to be mutated through
 * a HttpRequest parameter.
 *
 * @author martin.gilday
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Blocked {

}
