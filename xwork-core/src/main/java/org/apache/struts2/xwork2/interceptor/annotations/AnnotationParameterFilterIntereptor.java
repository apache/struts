package org.apache.struts2.xwork2.interceptor.annotations;


import org.apache.struts2.xwork2.Action;
import org.apache.struts2.xwork2.ActionInvocation;
import org.apache.struts2.xwork2.interceptor.AbstractInterceptor;
import org.apache.struts2.xwork2.interceptor.Interceptor;
import org.apache.struts2.xwork2.interceptor.ParameterFilterInterceptor;
import org.apache.struts2.xwork2.interceptor.ParametersInterceptor;
import org.apache.struts2.xwork2.util.AnnotationUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Annotation based version of {@link ParameterFilterInterceptor}.
 * <p/>
 * This {@link Interceptor} must be placed in the stack before the {@link ParametersInterceptor}
 * When a parameter matches a field that is marked {@link Blocked} then it is removed from
 * the parameter map.
 * <p/>
 * If an {@link Action} class is marked with {@link BlockByDefault} then all parameters are
 * removed unless a field on the Action exists and is marked with {@link Allowed}
 *
 * @author martin.gilday
 */
public class AnnotationParameterFilterIntereptor extends AbstractInterceptor {

    /* (non-Javadoc)
      * @see org.apache.struts2.xwork2.interceptor.AbstractInterceptor#intercept(org.apache.struts2.xwork2.ActionInvocation)
      */
    @Override public String intercept(ActionInvocation invocation) throws Exception {

        final Object action = invocation.getAction();
        Map<String, Object> parameters = invocation.getInvocationContext().getParameters();

        Object model = invocation.getStack().peek();
        if (model == action) {
            model = null;
        }

        boolean blockByDefault = action.getClass().isAnnotationPresent(BlockByDefault.class);
        List<Field> annotatedFields = new ArrayList<Field>();
        HashSet<String> paramsToRemove = new HashSet<String>();

        if (blockByDefault) {
            AnnotationUtils.addAllFields(Allowed.class, action.getClass(), annotatedFields);
            if (model != null) {
                AnnotationUtils.addAllFields(Allowed.class, model.getClass(), annotatedFields);
            }

            for (String paramName : parameters.keySet()) {
                boolean allowed = false;

                for (Field field : annotatedFields) {
                    //TODO only matches exact field names.  need to change to it matches start of ognl expression
                    //i.e take param name up to first . (period) and match against that
                    if (field.getName().equals(paramName)) {
                        allowed = true;
                    }
                }

                if (!allowed) {
                    paramsToRemove.add(paramName);
                }
            }
        } else {
            AnnotationUtils.addAllFields(Blocked.class, action.getClass(), annotatedFields);
            if (model != null) {
                AnnotationUtils.addAllFields(Blocked.class, model.getClass(), annotatedFields);
            }

            for (String paramName : parameters.keySet()) {

                for (Field field : annotatedFields) {
                    //TODO only matches exact field names.  need to change to it matches start of ognl expression
                    //i.e take param name up to first . (period) and match against that
                    if (field.getName().equals(paramName)) {
                        paramsToRemove.add(paramName);
                    }
                }
            }
        }

        for (String aParamsToRemove : paramsToRemove) {
            parameters.remove(aParamsToRemove);
        }

        return invocation.invoke();
    }

}
