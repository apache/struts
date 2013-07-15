/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsStatics;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.Map;

/**
 * <!-- START SNIPPET: description -->
 * This interceptor extends the original xwork i18n interceptor
 * and adds functionality to support cookies.
 *
 * <!-- END SNIPPET: description -->
 *
 * <!-- START SNIPPET: parameters -->
  * <p/>
  * <ul>
  * <p/>
  * <li>parameterName (optional) - the name of the HTTP request parameter that dictates the locale to switch to and save
  * in the session. By default this is <b>request_locale</b></li>
  * <p/>
  * <li>requestCookieParameterName (optional) - the name of the HTTP request parameter that dictates the locale to switch to
  * and save in a cookien. By default this is <b>request_cookie_locale</b></li>
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
  *
  * <!-- START SNIPPET: example -->
  * &lt;interceptor name="i18nCookie" class="org.apache.struts2.interceptor.I18nInterceptor"/&gt;
  *
  * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
  *     &lt;interceptor-ref name="i18nCookie"/&gt;
  *     &lt;interceptor-ref name="basicStack"/&gt;
  *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
  * &lt;/action&gt;
  * <!-- END SNIPPET: example -->
  */
public class I18nInterceptor extends com.opensymphony.xwork2.interceptor.I18nInterceptor {
    private static final long serialVersionUID = 4587460933182760358L;

    public static final String DEFAULT_COOKIE_ATTRIBUTE = DEFAULT_SESSION_ATTRIBUTE;

    public static final String COOKIE_STORAGE = "cookie";

    public static final String DEFAULT_COOKIE_PARAMETER = "request_cookie_locale";
    protected String requestCookieParameterName = DEFAULT_COOKIE_PARAMETER;

    protected class CookieLocaleFinder extends LocaleFinder {
        protected CookieLocaleFinder(ActionInvocation invocation) {
            super(invocation);
        }

        @Override
        protected void find() {
            //get requested locale
            Map<String, Object> params = actionInvocation.getInvocationContext().getParameters();
            storage = Storage.SESSION.toString();

            requestedLocale = findLocaleParameter(params, parameterName);

            if (requestedLocale != null) {
                return;
            }

            requestedLocale = findLocaleParameter(params, requestCookieParameterName);
            if (requestedLocale != null) {
                storage = COOKIE_STORAGE;
                return;
            }

            requestedLocale = findLocaleParameter(params, requestOnlyParameterName);
            if (requestedLocale != null) {
                storage = Storage.NONE.toString();
            }

        }
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("intercept '#0/#1' {",
                invocation.getProxy().getNamespace(), invocation.getProxy().getActionName());
        }

        LocaleFinder localeFinder = new CookieLocaleFinder(invocation);
        Locale locale = getLocaleFromParam(localeFinder.getRequestedLocale());
        locale = storeLocale(invocation, locale, localeFinder.getStorage());
        saveLocale(invocation, locale);

        if (LOG.isDebugEnabled()) {
            LOG.debug("before Locale=#0", invocation.getStack().findValue("locale"));
        }

        final String result = invocation.invoke();

        if (LOG.isDebugEnabled()) {
            LOG.debug("after Locale=#0", invocation.getStack().findValue("locale"));
            LOG.debug("intercept } ");
        }

        return result;
    }

    @Override
    protected Locale storeLocale(ActionInvocation invocation, Locale locale, String storage) {
        if (COOKIE_STORAGE.equals(storage)) {
            ActionContext ac = invocation.getInvocationContext();
            HttpServletResponse response = (HttpServletResponse) ac.get(StrutsStatics.HTTP_RESPONSE);

            Cookie cookie = new Cookie(DEFAULT_COOKIE_ATTRIBUTE, locale.toString());
            cookie.setMaxAge(1209600); // two weeks
            response.addCookie(cookie);

            storage = Storage.SESSION.toString();
        }

        return super.storeLocale(invocation, locale, storage);
    }

    @Override
    protected Locale readStoredLocale(ActionInvocation invocation, Map<String, Object> session) {
        Locale locale = this.readStoredLocalFromSession(invocation, session);

        if (locale != null) {
            return locale;
        }

        Cookie[] cookies = ServletActionContext.getRequest().getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (DEFAULT_COOKIE_ATTRIBUTE.equals(cookie.getName())) {
                    return getLocaleFromParam(cookie.getValue());
                }
            }
        }

        return this.readStoredLocalFromCurrentInvocation(invocation);
    }

    public void setRequestCookieParameterName(String requestCookieParameterName) {
        this.requestCookieParameterName = requestCookieParameterName;
    }
}
