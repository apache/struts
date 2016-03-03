package org.apache.struts2.convention.annotation;

import com.opensymphony.xwork2.config.entities.ActionConfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <!-- START SNIPPET: javadoc -->
 * <p>
 * This annotation allows actions to specify allowed action methods
 * to limit access to any other public action's methods
 * </p>
 *
 * <p>
 * This annotation can be used directly on Action classes or
 * in the <strong>package-info.java</strong> class in order
 * to specify global allowed methods for all sub-packages.
 * </p>
 * <!-- END SNIPPET: javadoc -->
 */
@Target({ElementType.TYPE, ElementType.PACKAGE})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface AllowedMethods {

    String[] value() default ActionConfig.DEFAULT_METHOD;

}
