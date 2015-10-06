package com.opensymphony.xwork2.interceptor.annotations;


import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.interceptor.ParameterFilterInterceptor;
import com.opensymphony.xwork2.interceptor.ParametersInterceptor;
import com.opensymphony.xwork2.util.AnnotationUtils;
import org.apache.struts2.dispatcher.HttpParameters;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Annotation based version of {@link ParameterFilterInterceptor}.
 *
 * <p>
 * This {@link Interceptor} must be placed in the stack before the {@link ParametersInterceptor}
 * When a parameter matches a field that is marked {@link Blocked} then it is removed from
 * the parameter map.
 * </p>
 * <p>
 * If an {@link Action} class is marked with {@link BlockByDefault} then all parameters are
 * removed unless a field on the Action exists and is marked with {@link Allowed}
 * </p>
 *
 * @author martin.gilday
 */
public class AnnotationParameterFilterInterceptor extends AbstractInterceptor {

    /* (non-Javadoc)
      * @see com.opensymphony.xwork2.interceptor.AbstractInterceptor#intercept(com.opensymphony.xwork2.ActionInvocation)
      */
    @Override public String intercept(ActionInvocation invocation) throws Exception {

        final Object action = invocation.getAction();
        HttpParameters parameters = invocation.getInvocationContext().getParameters();

        Object model = invocation.getStack().peek();
        if (model == action) {
            model = null;
        }

        boolean blockByDefault = action.getClass().isAnnotationPresent(BlockByDefault.class);
        List<Field> annotatedFields = new ArrayList<>();

        if (blockByDefault) {
            AnnotationUtils.addAllFields(Allowed.class, action.getClass(), annotatedFields);
            if (model != null) {
                AnnotationUtils.addAllFields(Allowed.class, model.getClass(), annotatedFields);
            }

            for (String paramName : parameters.getNames()) {
                boolean allowed = false;

                for (Field field : annotatedFields) {
                    //TODO only matches exact field names.  need to change to it matches start of ognl expression
                    //i.e take param name up to first . (period) and match against that
                    if (field.getName().equals(paramName)) {
                        allowed = true;
                        break;
                    }
                }

                if (!allowed) {
                    parameters = parameters.remove(paramName);
                }
            }
        } else {
            AnnotationUtils.addAllFields(Blocked.class, action.getClass(), annotatedFields);
            if (model != null) {
                AnnotationUtils.addAllFields(Blocked.class, model.getClass(), annotatedFields);
            }

            for (String paramName : parameters.getNames()) {
                for (Field field : annotatedFields) {
                    //TODO only matches exact field names.  need to change to it matches start of ognl expression
                    //i.e take param name up to first . (period) and match against that
                    if (field.getName().equals(paramName)) {
                        parameters = parameters.remove(paramName);
                    }
                }
            }
        }

        invocation.getInvocationContext().setParameters(parameters);

        return invocation.invoke();
    }

}
