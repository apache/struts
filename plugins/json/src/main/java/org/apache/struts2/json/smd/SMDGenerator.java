package org.apache.struts2.json.smd;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.json.JSONUtil;
import org.apache.struts2.json.annotations.SMD;
import org.apache.struts2.json.annotations.SMDMethod;
import org.apache.struts2.json.annotations.SMDMethodParameter;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Pattern;

public class SMDGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(SMDGenerator.class);

    // rootObject is based on OGNL expression (action by default)
    private Object rootObject;
    private List<Pattern> excludeProperties;
    private boolean ignoreInterfaces;

    public SMDGenerator(Object root, List<Pattern> excludeProperties, boolean ignoreInterfaces) {
        this.rootObject = root;
        this.excludeProperties = excludeProperties;
        this.ignoreInterfaces = ignoreInterfaces;
    }

    public org.apache.struts2.json.smd.SMD generate(ActionInvocation actionInvocation) {
        ActionContext actionContext = actionInvocation.getInvocationContext();
        HttpServletRequest request = (HttpServletRequest) actionContext.get(StrutsStatics.HTTP_REQUEST);

        Class clazz = rootObject.getClass();
        org.apache.struts2.json.smd.SMD smd = new org.apache.struts2.json.smd.SMD();
        // URL
        smd.setServiceUrl(request.getRequestURI());

        // customize SMD
        org.apache.struts2.json.annotations.SMD smdAnnotation = (SMD) clazz.getAnnotation(SMD.class);
        if (smdAnnotation != null) {
            smd.setObjectName(smdAnnotation.objectName());
            smd.setServiceType(smdAnnotation.serviceType());
            smd.setVersion(smdAnnotation.version());
        }

        // get public methods
        Method[] methods = JSONUtil.listSMDMethods(clazz, ignoreInterfaces);

        for (Method method : methods) {
            processAnnotatedMethod(smd, method);
        }
        return smd;

    }

    private void processAnnotatedMethod(org.apache.struts2.json.smd.SMD smd, Method method) {
        SMDMethod smdMethodAnnotation = method.getAnnotation(SMDMethod.class);
        // SMDMethod annotation is required
        if (shouldProcessMethod(method, smdMethodAnnotation)) {
            String methodName = readMethodName(method, smdMethodAnnotation);
            org.apache.struts2.json.smd.SMDMethod smdMethod = new org.apache.struts2.json.smd.SMDMethod(methodName);
            smd.addSMDMethod(smdMethod);

            // find params for this method
            processMethodsParameters(method, smdMethod);

        } else if(LOG.isDebugEnabled()) {
            LOG.debug("Ignoring property " + method.getName());
        }
    }

    private void processMethodsParameters(Method method, org.apache.struts2.json.smd.SMDMethod smdMethod) {
        int parametersCount = method.getParameterTypes().length;
        if (parametersCount > 0) {

            Annotation[][] parameterAnnotations = method.getParameterAnnotations();

            for (int i = 0; i < parametersCount; i++) {
                processParameter(smdMethod, parameterAnnotations[i], i);
            }
        }
    }

    private void processParameter(org.apache.struts2.json.smd.SMDMethod smdMethod, Annotation[] parameterAnnotation, int i) {
        // are you ever going to pick shorter names? nope
        SMDMethodParameter smdMethodParameterAnnotation = getSMDMethodParameterAnnotation(parameterAnnotation);
        String paramName = buildParamName(i, smdMethodParameterAnnotation);
        smdMethod.addSMDMethodParameter(new org.apache.struts2.json.smd.SMDMethodParameter(paramName));
    }

    private String buildParamName(int i, SMDMethodParameter smdMethodParameterAnnotation) {
        return smdMethodParameterAnnotation != null ? smdMethodParameterAnnotation.name() : "p" + i;
    }

    private String readMethodName(Method method, SMDMethod smdMethodAnnotation) {
        return smdMethodAnnotation.name().length() == 0 ? method.getName() : smdMethodAnnotation.name();
    }

    private boolean shouldProcessMethod(Method method, SMDMethod smdMethodAnnotation) {
        return ((smdMethodAnnotation != null) && !this.shouldExcludeProperty(method.getName()));
    }

    private boolean shouldExcludeProperty(String expr) {
        if (this.excludeProperties != null) {
            for (Pattern pattern : this.excludeProperties) {
                if (pattern.matcher(expr).matches())
                    return true;
            }
        }
        return false;
    }

    /**
     * Find an SMDethodParameter annotation on this array
     */
    private org.apache.struts2.json.annotations.SMDMethodParameter getSMDMethodParameterAnnotation(
            Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof org.apache.struts2.json.annotations.SMDMethodParameter)
                return (org.apache.struts2.json.annotations.SMDMethodParameter) annotation;
        }

        return null;
    }

}
