package org.apache.struts2.interceptor.httpmethod;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.util.AnnotationUtils;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Interceptor is used to control with what http methods action can be called,
 * if request with not allowed method was performed, {@link #badRequestResultName}
 * will be returned or if action implements {@link HttpMethodAware}
 * and {@link HttpMethodAware#getBadRequestResultName()} returns non-null result name,
 * thus value will be used instead.
 *
 * To limit allowed http methods, annotate action class with {@link AllowedMethod} and specify
 * which methods are allowed. You can also use shorter versions {@link GetOnly}, {@link PostOnly}
 * and {@link GetPostOnly}
 *
 * @see HttpMethodAware
 * @see HttpMethod
 * @see AllowedMethod
 * @see GetOnly
 * @see PostOnly
 * @see GetPostOnly
 *
 * @since 2.3.18
 */
public class HttpMethodInterceptor extends AbstractInterceptor {

    public static final Class[] HTTP_METHOD_ANNOTATIONS = { AllowedMethod.class, PostOnly.class, GetOnly.class, GetPostOnly.class };

    private static final Logger LOG = LoggerFactory.getLogger(HttpMethodInterceptor.class);

    private String badRequestResultName = "bad-request";

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();
        HttpServletRequest request = ServletActionContext.getRequest();
        if (action instanceof HttpMethodAware) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Action #0 implements #1, setting request method #3",
                        action, HttpMethodAware.class.getSimpleName(), request.getMethod());
            }
            ((HttpMethodAware) (action)).setMethod(HttpMethod.valueOf(request.getMethod()));
        }
        if (AnnotationUtils.isAnnotatedBy(action.getClass(), HTTP_METHOD_ANNOTATIONS)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Action #0 annotated with #1, checking if request #2 meets allowed methods!",
                        action, AllowedMethod.class.getSimpleName(), request.getMethod());
            }
            return doIntercept(invocation);
        }
        return invocation.invoke();
    }

    protected String doIntercept(ActionInvocation invocation) throws Exception {
        List<HttpMethod> allowedMethods = readAllowedMethods(invocation.getAction().getClass());
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpMethod requestedMethod = HttpMethod.valueOf(request.getMethod());
        if (allowedMethods.contains(requestedMethod)) {
            if(LOG.isTraceEnabled()) {
                LOG.trace("Request method #0 matches allowed methods #1, continuing invocation!", requestedMethod, allowedMethods);
            }
            return invocation.invoke();
        } else {
            if(LOG.isTraceEnabled()) {
                LOG.trace("Request method #0 doesn't match allowed methods #1, continuing invocation!", requestedMethod, allowedMethods);
            }
            return getBadRequestResultName(invocation);
        }
    }

    protected List<HttpMethod> readAllowedMethods(Class<? extends Object> klass) {
        List<HttpMethod> allowedMethods = Collections.emptyList();
        if (AnnotationUtils.isAnnotatedBy(klass, AllowedMethod.class)) {
            allowedMethods = Arrays.asList(klass.getAnnotation(AllowedMethod.class).value());
        }
        if (AnnotationUtils.isAnnotatedBy(klass, GetOnly.class)) {
            allowedMethods = Arrays.asList(klass.getAnnotation(GetOnly.class).value());
        }
        if (AnnotationUtils.isAnnotatedBy(klass, PostOnly.class)) {
            allowedMethods = Arrays.asList(klass.getAnnotation(PostOnly.class).value());
        }
        if (AnnotationUtils.isAnnotatedBy(klass, GetPostOnly.class)) {
            allowedMethods = Arrays.asList(klass.getAnnotation(GetPostOnly.class).value());
        }
        return Collections.unmodifiableList(allowedMethods);
    }

    protected String getBadRequestResultName(ActionInvocation invocation) {
        Object action = invocation.getAction();
        String resultName = badRequestResultName;
        if (action instanceof HttpMethodAware) {
            String actionResultName = ((HttpMethodAware) action).getBadRequestResultName();
            if (actionResultName != null) {
                resultName = actionResultName;
            }
        }
        if (LOG.isTraceEnabled()) {
            LOG.trace("Bad request result name is #0", resultName);
        }
        return resultName;
    }

    public void setBadRequestResultName(String badRequestResultName) {
        this.badRequestResultName = badRequestResultName;
    }

}
