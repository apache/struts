/*
 * Copyright 2002-2006,2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.util.LocalizedTextUtil;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import java.util.Locale;
import java.util.Map;

/**
 * <!-- START SNIPPET: description -->
 * <p/>
 * An interceptor that handles setting the locale specified in a session as the locale for the current action request.
 * In addition, this interceptor will look for a specific HTTP request parameter and set the locale to whatever value is
 * provided. This means that this interceptor can be used to allow for your application to dynamically change the locale
 * for the user's session or, alternatively, only for the current request (since XWork 2.1.3).
 * This is very useful for applications that require multi-lingual support and want the user to
 * be able to set his or her language preference at any point. The locale parameter is removed during the execution of
 * this interceptor, ensuring that properties aren't set on an action (such as request_locale) that have no typical
 * corresponding setter in your action.
 * <p/>
 * <p/>For example, using the default parameter name, a request to <b>foo.action?request_locale=en_US</b>, then the
 * locale for US English is saved in the user's session and will be used for all future requests.
 * <p/>
 * <!-- END SNIPPET: description -->
 * <p/>
 * <p/> <u>Interceptor parameters:</u>
 * <p/>
 * <!-- START SNIPPET: parameters -->
 * <p/>
 * <ul>
 * <p/>
 * <li>parameterName (optional) - the name of the HTTP request parameter that dictates the locale to switch to and save
 * in the session. By default this is <b>request_locale</b></li>
 * <p/>
 * <li>requestOnlyParameterName (optional) - the name of the HTTP request parameter that dictates the locale to switch to
 * for the current request only, without saving it in the session. By default this is <b>request_only_locale</b></li>
 * <p/>
 * <li>attributeName (optional) - the name of the session key to store the selected locale. By default this is
 * <b>WW_TRANS_I18N_LOCALE</b></li>
 * <p/>
 * </ul>
 * <p/>
 * <!-- END SNIPPET: parameters -->
 * <p/>
 * <p/> <u>Extending the interceptor:</u>
 * <p/>
 * <p/>
 * <p/>
 * <!-- START SNIPPET: extending -->
 * <p/>
 * There are no known extensions points for this interceptor.
 * <p/>
 * <!-- END SNIPPET: extending -->
 * <p/>
 * <p/> <u>Example code:</u>
 * <p/>
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;interceptor-ref name="i18n"/&gt;
 *     &lt;interceptor-ref name="basicStack"/&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Aleksei Gopachenko
 */
public class I18nInterceptor extends AbstractInterceptor {
    protected static final Logger LOG = LoggerFactory.getLogger(I18nInterceptor.class);

    public static final String DEFAULT_SESSION_ATTRIBUTE = "WW_TRANS_I18N_LOCALE";
    public static final String DEFAULT_PARAMETER = "request_locale";
    public static final String DEFAULT_REQUESTONLY_PARAMETER = "request_only_locale";

    protected String parameterName = DEFAULT_PARAMETER;
    protected String requestOnlyParameterName = DEFAULT_REQUESTONLY_PARAMETER;
    protected String attributeName = DEFAULT_SESSION_ATTRIBUTE;

    public I18nInterceptor() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("new I18nInterceptor()");
        }
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public void setRequestOnlyParameterName(String requestOnlyParameterName) {
        this.requestOnlyParameterName = requestOnlyParameterName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("intercept '"
                    + invocation.getProxy().getNamespace() + "/"
                    + invocation.getProxy().getActionName() + "' { ");
        }
        //get requested locale
        Map<String, Object> params = invocation.getInvocationContext().getParameters();

        boolean storeInSession = true;
        Object requested_locale = findLocaleParameter(params, parameterName);
        if (requested_locale == null) {
            requested_locale = findLocaleParameter(params, requestOnlyParameterName);
            if (requested_locale != null) {
                storeInSession = false;
            }
        }

        //save it in session
        Map<String, Object> session = invocation.getInvocationContext().getSession();

        Locale locale = null;
        if (requested_locale != null) {
            locale = (requested_locale instanceof Locale) ?
                    (Locale) requested_locale : LocalizedTextUtil.localeFromString(requested_locale.toString(), null);
            if (locale != null && LOG.isDebugEnabled()) {
                LOG.debug("applied request locale=" + locale);
            }
        }
        if (session != null) {
            synchronized (session) {
                if (locale == null) {
                    storeInSession = false;
                    // check session for saved locale
                    Object sessionLocale = session.get(attributeName);
                    if (sessionLocale != null && sessionLocale instanceof Locale) {
                        locale = (Locale) sessionLocale;
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("applied session locale=" + locale);
                        }
                    } else {
                        // no overriding locale definition found, stay with current invocation (=browser) locale
                        locale = invocation.getInvocationContext().getLocale();
                        if (locale != null && LOG.isDebugEnabled()) {
                            LOG.debug("applied invocation context locale=" + locale);
                        }
                    }
                }
                if (storeInSession) {
                    session.put(attributeName, locale);
                }
            }
        }
        saveLocale(invocation, locale);

        if (LOG.isDebugEnabled()) {
            LOG.debug("before Locale=" + invocation.getStack().findValue("locale"));
        }

        final String result = invocation.invoke();
        if (LOG.isDebugEnabled()) {
            LOG.debug("after Locale=" + invocation.getStack().findValue("locale"));
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("intercept } ");
        }

        return result;
    }

    private Object findLocaleParameter(Map<String, Object> params, String parameterName) {
        Object requested_locale = params.remove(parameterName);
        if (requested_locale != null && requested_locale.getClass().isArray()
                && ((Object[]) requested_locale).length == 1) {
            requested_locale = ((Object[]) requested_locale)[0];

            if (LOG.isDebugEnabled()) {
                LOG.debug("requested_locale=" + requested_locale);
            }
        }
        return requested_locale;
    }

    /**
     * Save the given locale to the ActionInvocation.
     *
     * @param invocation The ActionInvocation.
     * @param locale     The locale to save.
     */
    protected void saveLocale(ActionInvocation invocation, Locale locale) {
        invocation.getInvocationContext().setLocale(locale);
    }

}
