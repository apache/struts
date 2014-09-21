package org.apache.struts2.interceptor.httpmethod;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.util.AnnotationUtils;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Interceptor is used to control with what http methods action can be called,
 * if request with not allowed method was performed, {@link #badRequestResultName}
 * will be returned or if action implements {@link HttpMethodAware}
 * and {@link HttpMethodAware#getBadRequestResultName()} returns non-null result name,
 * thus value will be used instead.
 * <p/>
 * To limit allowed http methods, annotate action class with {@link AllowedHttpMethod} and specify
 * which methods are allowed. You can also use shorter versions {@link HttpGet}, {@link HttpPost}
 * and {@link HttpGetOrPost}
 *
 * @see HttpMethodAware
 * @see HttpMethod
 * @see AllowedHttpMethod
 * @see HttpGet
 * @see HttpPost
 * @see HttpGetOrPost
 * @since 2.3.18
 */
public class HttpMethodInterceptor extends AbstractInterceptor {

    public static final Class[] HTTP_METHOD_ANNOTATIONS = {
            AllowedHttpMethod.class,
            HttpPost.class,
            HttpGet.class,
            HttpGetOrPost.class
    };

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
            ((HttpMethodAware) (action)).setMethod(HttpMethod.parse(request.getMethod()));
        }
        if (invocation.getProxy().isMethodSpecified()) {
            Method method = action.getClass().getMethod(invocation.getProxy().getMethod(), new Class[0]);
            if (AnnotationUtils.isAnnotatedBy(method, HTTP_METHOD_ANNOTATIONS)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Action's method #0 annotated with #1, checking if request #2 meets allowed methods!",
                            invocation.getProxy().getMethod(), AllowedHttpMethod.class.getSimpleName(), request.getMethod());
                }
                return doIntercept(invocation, method);
            }
        } else if (AnnotationUtils.isAnnotatedBy(action.getClass(), HTTP_METHOD_ANNOTATIONS)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Action #0 annotated with #1, checking if request #2 meets allowed methods!",
                        action, AllowedHttpMethod.class.getSimpleName(), request.getMethod());
            }
            return doIntercept(invocation, action.getClass());
        }
        return invocation.invoke();
    }

    protected String doIntercept(ActionInvocation invocation, AnnotatedElement element) throws Exception {
        List<HttpMethod> allowedMethods = readAllowedMethods(element);
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpMethod requestedMethod = HttpMethod.parse(request.getMethod());
        if (allowedMethods.contains(requestedMethod)) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Request method #0 matches allowed methods #1, continuing invocation!", requestedMethod, allowedMethods);
            }
            return invocation.invoke();
        } else {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Request method #0 doesn't match allowed methods #1, continuing invocation!", requestedMethod, allowedMethods);
            }
            return getBadRequestResultName(invocation);
        }
    }

    protected List<HttpMethod> readAllowedMethods(AnnotatedElement element) {
        List<HttpMethod> allowedMethods = new ArrayList<HttpMethod>();
        if (AnnotationUtils.isAnnotatedBy(element, AllowedHttpMethod.class)) {
            allowedMethods.addAll(Arrays.asList(element.getAnnotation(AllowedHttpMethod.class).value()));
        }
        if (AnnotationUtils.isAnnotatedBy(element, HttpGet.class)) {
            allowedMethods.addAll(Arrays.asList(element.getAnnotation(HttpGet.class).value()));
        }
        if (AnnotationUtils.isAnnotatedBy(element, HttpPost.class)) {
            allowedMethods.addAll(Arrays.asList(element.getAnnotation(HttpPost.class).value()));
        }
        if (AnnotationUtils.isAnnotatedBy(element, HttpPut.class)) {
            allowedMethods.addAll(Arrays.asList(element.getAnnotation(HttpPut.class).value()));
        }
        if (AnnotationUtils.isAnnotatedBy(element, HttpDelete.class)) {
            allowedMethods.addAll(Arrays.asList(element.getAnnotation(HttpDelete.class).value()));
        }
        if (AnnotationUtils.isAnnotatedBy(element, HttpGetOrPost.class)) {
            allowedMethods.addAll(Arrays.asList(element.getAnnotation(HttpGetOrPost.class).value()));
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
