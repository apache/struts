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
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.security.AcceptedPatternsChecker;
import com.opensymphony.xwork2.security.ExcludedPatternsChecker;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.Cookie;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * <!-- START SNIPPET: description -->
 *
 * The aim of this intercepter is to set values in the stack/action based on cookie name/value
 * of interest. <p/>
 *
 * If an asterisk is present in cookiesName parameter, it will be assume that
 * all cookies name are to be injected into struts' action, even though
 * cookiesName is comma-separated by other values, e.g. (cookie1,*,cookie2). <p/>
 *
 * If cookiesName is left empty it will assume that no cookie will be injected
 * into Struts' action. <p/>
 *
 * If an asterisk is present in cookiesValue parameter, it will assume that all
 * cookies name irrespective of its value will be injected into Struts' action so
 * long as the cookie name matches those specified in cookiesName parameter.<p/>
 *
 * If cookiesValue is left empty it will assume that all cookie that match the cookieName
 * parameter will be injected into Struts' action.<p/>
 *
 * The action could implement {@link CookiesAware} in order to have a {@link Map}
 * of filtered cookies set into it. <p/>
 *
 * <!-- END SNIPPET: description -->
 *
 *
 * <!-- START SNIPPET: parameters -->
 *
 * <ul>
 *      <li>cookiesName (mandatory) - Name of cookies to be injected into the action. If more
 *                                                                 than one cookie name is desired it could be comma-separated.
 *                                                                 If all cookies name is desired, it could simply be *, an asterik.
 *                                                                 When many cookies name are comma-separated either of the cookie
 *                                                        that match the name in the comma-separated list will be qualified.</li>
 *     <li>cookiesValue (mandatory) - Value of cookies that if its name matches cookieName attribute
 *                                                         and its value matched this, will be injected into Struts'
 *                                                         action. If more than one cookie name is desired it could be
 *                                                         comma-separated. If left empty, it will assume any value would
 *                                                         be ok. If more than one value is specified (comma-separated)
 *                                                         it will assume a match if either value is matched.</li>
 *     <li>acceptCookieNames (optional) - Pattern used to check if name of cookie matches the provided patter, to </li>
 * </ul>
 *
 * <!-- END SNIPPET: parameters -->
 *
 *
 * <!-- START SNIPPET: extending -->
 *
 * <ul>
 *     <li>
 *         populateCookieValueIntoStack - this method will decide if this cookie value is qualified
 *         to be populated into the value stack (hence into the action itself)
 *     </li>
 *     <li>
 *         injectIntoCookiesAwareAction - this method will inject selected cookies (as a java.util.Map)
 *         into action that implements {@link CookiesAware}.
 *     </li>
 * </ul>
 *
 * <!-- END SNIPPET: extending -->
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 *
 * &lt;!--
 *   This example will inject cookies named either 'cookie1' or 'cookie2' whose
 *   value could be either 'cookie1value' or 'cookie2value' into Struts' action.
 * --&gt;
 * &lt;action ... &gt;
 *    &lt;interceptor-ref name="cookie"&gt;
 *        &lt;param name="cookiesName"&gt;cookie1, cookie2&lt;/param&gt;
 *        &lt;param name="cookiesValue"&gt;cookie1value, cookie2value&lt;/param&gt;
 *    &lt;/interceptor-ref&gt;
 *    ....
 * &lt;/action&gt;
 *
 *
 * &lt;!--
 *      This example will inject cookies named either 'cookie1' or 'cookie2'
 *     regardless of their value into Struts' action.
 * --&gt;
 * &lt;action ... &gt;
 *   &lt;interceptor-ref name="cookie"&gt;
 *      &lt;param name="cookiesName"&gt;cookie1, cookie2&lt;/param&gt;
 *      &lt;param name="cookiesValue"&gt;*&lt;/param&gt;
 *   &lt;interceptor-ref&gt;
 *   ...
 * &lt;/action&gt;
 *
 *
 * &lt;!--
 *      This example will inject cookies named either 'cookie1' with value
 *      'cookie1value' or 'cookie2' with value 'cookie2value' into Struts'
 *      action.
 * --&gt;
 * &lt;action ... &gt;
 *   &lt;interceptor-ref name="cookie"&gt;
 *      &lt;param name="cookiesName"&gt;cookie1&lt;/param&gt;
 *      &lt;param name="cookiesValue"&gt;cookie1value&lt;/param&gt;
 *   &lt;/interceptor-ref&gt;
 *   &lt;interceptor-ref name="cookie"&gt;
 *      &lt;param name="cookiesName"&lt;cookie2&lt;/param&gt;
 *     &lt;param name="cookiesValue"&gt;cookie2value&lt;/param&gt;
 *   &lt;/interceptor-ref&gt;
 *   ....
 * &lt;/action&gt;
 *
 * &lt;!--
 *    This example will inject any cookies regardless of its value into
 *    Struts' action.
 *  --&gt;
 * &lt;action ... &gt;
 *   &lt;interceptor-ref name="cookie"&gt;
 *      &lt;param name="cookiesName"&gt;*&lt;/param&gt;
 *      &lt;param name="cookiesValue"&gt;*&lt;/param&gt;
 *   &lt;/interceptor-ref&gt;
 *    ...
 * &lt;/action&gt;
 *
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @see CookiesAware
 */
public class CookieInterceptor extends AbstractInterceptor {

    private static final long serialVersionUID = 4153142432948747305L;

    private static final Logger LOG = LoggerFactory.getLogger(CookieInterceptor.class);

    private static final String ACCEPTED_PATTERN = "[a-zA-Z0-9\\.\\]\\[_'\\s]+";

    private Set<String> cookiesNameSet = Collections.emptySet();
    private Set<String> cookiesValueSet = Collections.emptySet();

    private ExcludedPatternsChecker excludedPatternsChecker;
    private AcceptedPatternsChecker acceptedPatternsChecker;

    @Inject
    public void setExcludedPatternsChecker(ExcludedPatternsChecker excludedPatternsChecker) {
        this.excludedPatternsChecker = excludedPatternsChecker;
    }

    @Inject
    public void setAcceptedPatternsChecker(AcceptedPatternsChecker acceptedPatternsChecker) {
        this.acceptedPatternsChecker = acceptedPatternsChecker;
        this.acceptedPatternsChecker.setAcceptedPatterns(ACCEPTED_PATTERN);
    }

    /**
     * Set the <code>cookiesName</code> which if matched will allow the cookie
     * to be injected into action, could be comma-separated string.
     *
     * @param cookiesName
     */
    public void setCookiesName(String cookiesName) {
        if (cookiesName != null)
            this.cookiesNameSet = TextParseUtil.commaDelimitedStringToSet(cookiesName);
    }

    /**
     * Set the <code>cookiesValue</code> which if matched (together with matching
     * cookiesName) will caused the cookie to be injected into action, could be
     * comma-separated string.
     *
     * @param cookiesValue
     */
    public void setCookiesValue(String cookiesValue) {
        if (cookiesValue != null)
            this.cookiesValueSet = TextParseUtil.commaDelimitedStringToSet(cookiesValue);
    }

    /**
     * Set the <code>acceptCookieNames</code> pattern of allowed names of cookies
     * to protect against remote command execution vulnerability.
     *
     * @param commaDelimitedPattern is used to check cookie name against, can set of comma delimited patterns
     */
    public void setAcceptCookieNames(String commaDelimitedPattern) {
        acceptedPatternsChecker.setAcceptedPatterns(commaDelimitedPattern);
    }

    public String intercept(ActionInvocation invocation) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("start interception");
        }

        // contains selected cookies
        final Map<String, String> cookiesMap = new LinkedHashMap<String, String>();

        Cookie[] cookies = ServletActionContext.getRequest().getCookies();
        if (cookies != null) {
            final ValueStack stack = ActionContext.getContext().getValueStack();

            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                String value = cookie.getValue();

                if (isAcceptableName(name)) {
                    if (cookiesNameSet.contains("*")) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("contains cookie name [*] in configured cookies name set, cookie with name [" + name + "] with value [" + value + "] will be injected");
                        }
                        populateCookieValueIntoStack(name, value, cookiesMap, stack);
                    } else if (cookiesNameSet.contains(cookie.getName())) {
                        populateCookieValueIntoStack(name, value, cookiesMap, stack);
                    }
                } else {
                    LOG.warn("Cookie name [#0] with value [#1] was rejected!", name, value);
                }
            }
        }

        // inject the cookiesMap, even if we don't have any cookies
        injectIntoCookiesAwareAction(invocation.getAction(), cookiesMap);

        return invocation.invoke();
    }

    /**
     * Checks if name of Cookie doesn't contain vulnerable code
     *
     * @param name of Cookie
     * @return true|false
     */
    protected boolean isAcceptableName(String name) {
        return !isExcluded(name) && isAccepted(name);
    }

    /**
     * Checks if name/value of Cookie is acceptable
     *
     * @param name of Cookie
     * @return true|false
     */
    protected boolean isAccepted(String name) {
        AcceptedPatternsChecker.IsAccepted accepted = acceptedPatternsChecker.isAccepted(name);
        if (accepted.isAccepted()) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Cookie [#0] matches acceptedPattern [#1]", name, accepted.getAcceptedPattern());
            }
            return true;
        }
        if (LOG.isTraceEnabled()) {
            LOG.trace("Cookie [#0] doesn't match acceptedPattern [#1]", name, accepted.getAcceptedPattern());
        }
        return false;
    }

    /**
     * Checks if name/value of Cookie is excluded
     *
     * @param name of Cookie
     * @return true|false
     */
    protected boolean isExcluded(String name) {
        ExcludedPatternsChecker.IsExcluded excluded = excludedPatternsChecker.isExcluded(name);
        if (excluded.isExcluded()) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Cookie [#0] matches excludedPattern [#1]", name, excluded.getExcludedPattern());
            }
            return true;
        }
        if (LOG.isTraceEnabled()) {
            LOG.trace("Cookie [#0] doesn't match excludedPattern [#1]", name, excluded.getExcludedPattern());
        }
        return false;
    }

    /**
     * Hook that populate cookie value into value stack (hence the action)
     * if the criteria is satisfied (if the cookie value matches with those configured).
     *
     * @param cookieName
     * @param cookieValue
     * @param cookiesMap
     * @param stack
     */
    protected void populateCookieValueIntoStack(String cookieName, String cookieValue, Map<String, String> cookiesMap, ValueStack stack) {
        if (cookiesValueSet.isEmpty() || cookiesValueSet.contains("*")) {
            // If the interceptor is configured to accept any cookie value
            // OR
            // no cookiesValue is defined, so as long as the cookie name match
            // we'll inject it into Struts' action
            if (LOG.isDebugEnabled()) {
                if (cookiesValueSet.isEmpty())
                    LOG.debug("no cookie value is configured, cookie with name ["+cookieName+"] with value ["+cookieValue+"] will be injected");
                else if (cookiesValueSet.contains("*"))
                    LOG.debug("interceptor is configured to accept any value, cookie with name ["+cookieName+"] with value ["+cookieValue+"] will be injected");
            }
            cookiesMap.put(cookieName, cookieValue);
            stack.setValue(cookieName, cookieValue);
        }
        else {
            // if cookiesValues is specified, the cookie's value must match before we
            // inject them into Struts' action
            if (cookiesValueSet.contains(cookieValue)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("both configured cookie name and value matched, cookie ["+cookieName+"] with value ["+cookieValue+"] will be injected");
                }

                cookiesMap.put(cookieName, cookieValue);
                stack.setValue(cookieName, cookieValue);
            }
        }
    }

    /**
     * Hook that set the <code>cookiesMap</code> into action that implements
     * {@link CookiesAware}.
     *
     * @param action
     * @param cookiesMap
     */
    protected void injectIntoCookiesAwareAction(Object action, Map<String, String> cookiesMap) {
        if (action instanceof CookiesAware) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("action ["+action+"] implements CookiesAware, injecting cookies map ["+cookiesMap+"]");
            }
            ((CookiesAware)action).setCookiesMap(cookiesMap);
        }
    }
}
