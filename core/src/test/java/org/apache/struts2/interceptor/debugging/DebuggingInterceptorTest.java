/*
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
package org.apache.struts2.interceptor.debugging;

import org.apache.struts2.ActionContext;
import org.apache.struts2.StrutsJUnit4InternalTestCase;
import org.apache.struts2.TestAction;
import org.apache.struts2.dispatcher.DispatcherConstants;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.dispatcher.RequestMap;
import org.apache.struts2.dispatcher.SessionMap;
import org.apache.struts2.mock.MockActionInvocation;
import org.apache.struts2.ognl.ThreadAllowlist;
import org.apache.struts2.util.ValueStack;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.assertj.core.util.Maps;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class DebuggingInterceptorTest extends StrutsJUnit4InternalTestCase {

    private DebuggingInterceptor interceptor;
    private MockActionInvocation invocation;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private ActionContext context;
    private TestAction action;

    @Test
    public void noDevMode() throws Exception {
        interceptor.intercept(invocation);
        assertThat(invocation.getResultCode()).isEqualTo("mock");
        assertThat(response.getContentAsString()).isEmpty();
    }

    @Test
    public void debugXml() throws Exception {
        interceptor.setDevMode("true");
        context.withParameters(HttpParameters.create(Maps.newHashMap("debug", "xml")).build());

        interceptor.intercept(invocation);

        assertThat(response.getContentAsString()).isEqualToIgnoringWhitespace("""
                <debug>
                  <parameters/>
                  <context/>
                  <request/>
                  <session/>
                  <valueStack>
                    <value>
                      <action>
                        <actionErrors/>
                        <actionMessages/>
                        <class>class org.apache.struts2.TestAction</class>
                        <fieldErrors/>
                        <locale>
                          <ISO3Country>USA</ISO3Country>
                          <ISO3Language>eng</ISO3Language>
                          <class>class java.util.Locale</class>
                          <country>US</country>
                          <displayCountry>United States</displayCountry>
                          <displayLanguage>English</displayLanguage>
                          <displayName>English (United States)</displayName>
                          <displayScript></displayScript>
                          <displayVariant></displayVariant>
                          <extensionKeys/>
                          <language>en</language>
                          <script></script>
                          <unicodeLocaleAttributes/>
                          <unicodeLocaleKeys/>
                          <variant></variant>
                        </locale>
                        <status>
                          <class>class org.apache.struts2.SomeEnum</class>
                          <declaringClass>class org.apache.struts2.SomeEnum</declaringClass>
                          <displayName>completed</displayName>
                          <name>COMPLETED</name>
                        </status>
                        <statusList>
                          <value>
                            <class>class org.apache.struts2.SomeEnum</class>
                            <declaringClass>class org.apache.struts2.SomeEnum</declaringClass>
                            <displayName>init</displayName>
                            <name>INIT</name>
                          </value>
                          <value>
                            <class>class org.apache.struts2.SomeEnum</class>
                            <declaringClass>class org.apache.struts2.SomeEnum</declaringClass>
                            <displayName>completed</displayName>
                            <name>COMPLETED</name>
                          </value>
                        </statusList>
                        <texts>
                          <baseBundleName>org.apache.struts2.TestAction</baseBundleName>
                          <class>class java.util.PropertyResourceBundle</class>
                          <keys>
                            <class>class sun.util.ResourceBundleEnumeration</class>
                          </keys>
                          <locale>
                            <ISO3Country></ISO3Country>
                            <ISO3Language></ISO3Language>
                            <class>class java.util.Locale</class>
                            <country></country>
                            <displayCountry></displayCountry>
                            <displayLanguage></displayLanguage>
                            <displayName></displayName>
                            <displayScript></displayScript>
                            <displayVariant></displayVariant>
                            <extensionKeys/>
                            <language></language>
                            <script></script>
                            <unicodeLocaleAttributes/>
                            <unicodeLocaleKeys/>
                            <variant></variant>
                          </locale>
                        </texts>
                      </action>
                      <org.apache.struts2.util.OgnlValueStack.MAP_IDENTIFIER_KEY></org.apache.struts2.util.OgnlValueStack.MAP_IDENTIFIER_KEY>
                    </value>
                    <value>
                      <class>class org.apache.struts2.text.DefaultTextProvider</class>
                    </value>
                  </valueStack>
                </debug>
                """);
    }

    @Test
    public void debugXmlWithConsole() throws Exception {
        interceptor.setDevMode("true");
        context.withParameters(HttpParameters.create(Maps.newHashMap("debug", "console")).build());
        interceptor.setEnableXmlWithConsole(true);

        interceptor.intercept(invocation);

        assertThat(response.getContentAsString()).isEqualToIgnoringWhitespace("""
                <!DOCTYPE html>
                <html>
                <head>
                <script>
                      var baseUrl = "/static";
                      window.open(baseUrl+"/webconsole.html", 'OGNL Console','width=500,height=450,status=no,toolbar=no,menubar=no');
                </script>
                </head>
                <body>
                <pre>
                    &amp;lt;debug&amp;gt;
                  &amp;lt;parameters/&amp;gt;
                  &amp;lt;context/&amp;gt;
                  &amp;lt;request/&amp;gt;
                  &amp;lt;session/&amp;gt;
                  &amp;lt;valueStack&amp;gt;
                    &amp;lt;value&amp;gt;
                      &amp;lt;action&amp;gt;
                        &amp;lt;actionErrors/&amp;gt;
                        &amp;lt;actionMessages/&amp;gt;
                        &amp;lt;class&amp;gt;class org.apache.struts2.TestAction&amp;lt;/class&amp;gt;
                        &amp;lt;fieldErrors/&amp;gt;
                        &amp;lt;locale&amp;gt;
                          &amp;lt;ISO3Country&amp;gt;USA&amp;lt;/ISO3Country&amp;gt;
                          &amp;lt;ISO3Language&amp;gt;eng&amp;lt;/ISO3Language&amp;gt;
                          &amp;lt;class&amp;gt;class java.util.Locale&amp;lt;/class&amp;gt;
                          &amp;lt;country&amp;gt;US&amp;lt;/country&amp;gt;
                          &amp;lt;displayCountry&amp;gt;United States&amp;lt;/displayCountry&amp;gt;
                          &amp;lt;displayLanguage&amp;gt;English&amp;lt;/displayLanguage&amp;gt;
                          &amp;lt;displayName&amp;gt;English (United States)&amp;lt;/displayName&amp;gt;
                          &amp;lt;displayScript&amp;gt;&amp;lt;/displayScript&amp;gt;
                          &amp;lt;displayVariant&amp;gt;&amp;lt;/displayVariant&amp;gt;
                          &amp;lt;extensionKeys/&amp;gt;
                          &amp;lt;language&amp;gt;en&amp;lt;/language&amp;gt;
                          &amp;lt;script&amp;gt;&amp;lt;/script&amp;gt;
                          &amp;lt;unicodeLocaleAttributes/&amp;gt;
                          &amp;lt;unicodeLocaleKeys/&amp;gt;
                          &amp;lt;variant&amp;gt;&amp;lt;/variant&amp;gt;
                        &amp;lt;/locale&amp;gt;
                        &amp;lt;status&amp;gt;
                          &amp;lt;class&amp;gt;class org.apache.struts2.SomeEnum&amp;lt;/class&amp;gt;
                          &amp;lt;declaringClass&amp;gt;class org.apache.struts2.SomeEnum&amp;lt;/declaringClass&amp;gt;
                          &amp;lt;displayName&amp;gt;completed&amp;lt;/displayName&amp;gt;
                          &amp;lt;name&amp;gt;COMPLETED&amp;lt;/name&amp;gt;
                        &amp;lt;/status&amp;gt;
                        &amp;lt;statusList&amp;gt;
                          &amp;lt;value&amp;gt;
                            &amp;lt;class&amp;gt;class org.apache.struts2.SomeEnum&amp;lt;/class&amp;gt;
                            &amp;lt;declaringClass&amp;gt;class org.apache.struts2.SomeEnum&amp;lt;/declaringClass&amp;gt;
                            &amp;lt;displayName&amp;gt;init&amp;lt;/displayName&amp;gt;
                            &amp;lt;name&amp;gt;INIT&amp;lt;/name&amp;gt;
                          &amp;lt;/value&amp;gt;
                          &amp;lt;value&amp;gt;
                            &amp;lt;class&amp;gt;class org.apache.struts2.SomeEnum&amp;lt;/class&amp;gt;
                            &amp;lt;declaringClass&amp;gt;class org.apache.struts2.SomeEnum&amp;lt;/declaringClass&amp;gt;
                            &amp;lt;displayName&amp;gt;completed&amp;lt;/displayName&amp;gt;
                            &amp;lt;name&amp;gt;COMPLETED&amp;lt;/name&amp;gt;
                          &amp;lt;/value&amp;gt;
                        &amp;lt;/statusList&amp;gt;
                        &amp;lt;texts&amp;gt;
                          &amp;lt;baseBundleName&amp;gt;org.apache.struts2.TestAction&amp;lt;/baseBundleName&amp;gt;
                          &amp;lt;class&amp;gt;class java.util.PropertyResourceBundle&amp;lt;/class&amp;gt;
                          &amp;lt;keys&amp;gt;
                            &amp;lt;class&amp;gt;class sun.util.ResourceBundleEnumeration&amp;lt;/class&amp;gt;
                          &amp;lt;/keys&amp;gt;
                          &amp;lt;locale&amp;gt;
                            &amp;lt;ISO3Country&amp;gt;&amp;lt;/ISO3Country&amp;gt;
                            &amp;lt;ISO3Language&amp;gt;&amp;lt;/ISO3Language&amp;gt;
                            &amp;lt;class&amp;gt;class java.util.Locale&amp;lt;/class&amp;gt;
                            &amp;lt;country&amp;gt;&amp;lt;/country&amp;gt;
                            &amp;lt;displayCountry&amp;gt;&amp;lt;/displayCountry&amp;gt;
                            &amp;lt;displayLanguage&amp;gt;&amp;lt;/displayLanguage&amp;gt;
                            &amp;lt;displayName&amp;gt;&amp;lt;/displayName&amp;gt;
                            &amp;lt;displayScript&amp;gt;&amp;lt;/displayScript&amp;gt;
                            &amp;lt;displayVariant&amp;gt;&amp;lt;/displayVariant&amp;gt;
                            &amp;lt;extensionKeys/&amp;gt;
                            &amp;lt;language&amp;gt;&amp;lt;/language&amp;gt;
                            &amp;lt;script&amp;gt;&amp;lt;/script&amp;gt;
                            &amp;lt;unicodeLocaleAttributes/&amp;gt;
                            &amp;lt;unicodeLocaleKeys/&amp;gt;
                            &amp;lt;variant&amp;gt;&amp;lt;/variant&amp;gt;
                          &amp;lt;/locale&amp;gt;
                        &amp;lt;/texts&amp;gt;
                      &amp;lt;/action&amp;gt;
                      &amp;lt;org.apache.struts2.util.OgnlValueStack.MAP_IDENTIFIER_KEY&amp;gt;&amp;lt;/org.apache.struts2.util.OgnlValueStack.MAP_IDENTIFIER_KEY&amp;gt;
                    &amp;lt;/value&amp;gt;
                    &amp;lt;value&amp;gt;
                      &amp;lt;class&amp;gt;class org.apache.struts2.text.DefaultTextProvider&amp;lt;/class&amp;gt;
                    &amp;lt;/value&amp;gt;
                  &amp;lt;/valueStack&amp;gt;
                &amp;lt;/debug&amp;gt;
                </pre>
                </body>
                </html>
                """);
    }

    @Test
    public void debugConsole() throws Exception {
        interceptor.setDevMode("true");
        context.withParameters(HttpParameters.create(Maps.newHashMap("debug", "console")).build());

        interceptor.intercept(invocation);

        assertThat(response.getContentAsString()).isEqualToIgnoringWhitespace("""
                <!DOCTYPE html>
                <html>
                <head>
                <script>
                      var baseUrl = "/static";
                      window.open(baseUrl+"/webconsole.html", 'OGNL Console','width=500,height=450,status=no,toolbar=no,menubar=no');
                </script>
                </head>
                <body>
                <pre>
                
                </pre>
                </body>
                </html>
                """);
    }

    @Test
    public void debugCommand() throws Exception {
        interceptor.setDevMode("true");
        Map<String, Object> params = new HashMap<>() {{
            put("debug", "command");
            put("expression", "1+1");
        }};
        context.withParameters(HttpParameters.create(params).build());

        interceptor.intercept(invocation);

        assertThat(response.getContentAsString()).isEqualToIgnoringWhitespace("2");
    }

    @Test
    public void debugBrowser() throws Exception {
        interceptor.setDevMode("true");
        context.withParameters(HttpParameters.create(Maps.newHashMap("debug", "browser")).build());

        interceptor.intercept(invocation);
        invocation.invoke();

        assertThat(response.getContentAsString()).isEqualToIgnoringWhitespace("""
                <!DOCTYPE html>
                <html lang="en">
                    <style>
                        .debugTable {
                            border-style: solid;
                            border-width: 1px;
                        }
                
                        .debugTable td {
                            border-style: solid;
                            border-width: 1px;
                        }
                
                        .nameColumn {
                            background-color:#CCDDFF;
                        }
                
                        .valueColumn {
                            background-color: #CCFFCC;
                        }
                
                        .nullValue {
                            background-color: #FF0000;
                        }
                
                        .typeColumn {
                            background-color: white;
                        }
                
                        .emptyCollection {
                            background-color: #EEEEEE;
                        }
                    </style>
                
                    <script>
                        function expand(src, path) {
                          let baseUrl = location.href;
                          const i = baseUrl.indexOf('&object=');
                          baseUrl = (i > 0 ? baseUrl.substring(0, i) : baseUrl) + "&object=" + path;
                          if (baseUrl.indexOf("decorate") < 0) {
                             baseUrl += "&decorate=false";
                          }
                
                          const request = new XMLHttpRequest();
                          request.open('GET', baseUrl, true);
                          request.onreadystatechange = function() {
                            if (this.readyState === 4) {
                              if (this.status >= 200 && this.status < 400) {
                                const div = document.createElement('div');
                                console.log(this.responseText);
                                div.innerHTML = this.responseText;
                                src.parentNode.appendChild(div);
                
                                src.innerHTML = "Collapse";
                                const oldOnclick = src.onclick;
                                src.onclick = function() {
                                  src.innerHTML = "Expand";
                                  src.parentNode.removeChild(div);
                                  src.onclick = oldOnclick;
                                };
                              }
                            }
                          };
                          request.send();
                        }
                    </script>
                
                <body>
                    <table class="debugTable">
                  <tr>
                    <td class="nameColumn">container</td>
                    <td class="valueColumn">There is no read method for container</td>
                    <td class="typeColumn">java.lang.String</td>
                  </tr>
                  <tr>
                    <td class="nameColumn">foo</td>
                    <td class="nullValue">null</td>
                    <td class="nullValue">unknown</td>
                  </tr>
                  <tr>
                    <td class="nameColumn">intList</td>
                    <td class="nullValue">null</td>
                    <td class="nullValue">unknown</td>
                  </tr>
                  <tr>
                    <td class="nameColumn">locale</td>
                    <td class="valueColumn">
                      <a onclick="expand(this, 'action[&quot;locale&quot;]')" href="javascript://nop/">Expand</a>
                    </td>
                    <td class="typeColumn">java.util.Locale</td>
                  </tr>
                  <tr>
                    <td class="nameColumn">result</td>
                    <td class="nullValue">null</td>
                    <td class="nullValue">unknown</td>
                  </tr>
                  <tr>
                    <td class="nameColumn">collection2</td>
                    <td class="nullValue">null</td>
                    <td class="nullValue">unknown</td>
                  </tr>
                  <tr>
                    <td class="nameColumn">someBool</td>
                    <td class="nullValue">null</td>
                    <td class="nullValue">unknown</td>
                  </tr>
                  <tr>
                    <td class="nameColumn">array</td>
                    <td class="nullValue">null</td>
                    <td class="nullValue">unknown</td>
                  </tr>
                  <tr>
                    <td class="nameColumn">fooInt</td>
                    <td class="nullValue">null</td>
                    <td class="nullValue">unknown</td>
                  </tr>
                  <tr>
                    <td class="nameColumn">id</td>
                    <td class="nullValue">null</td>
                    <td class="nullValue">unknown</td>
                  </tr>
                  <tr>
                    <td class="nameColumn">map</td>
                    <td class="nullValue">null</td>
                    <td class="nullValue">unknown</td>
                  </tr>
                  <tr>
                    <td class="nameColumn">actionErrors</td>
                    <td class="emptyCollection">empty</td>
                    <td class="typeColumn">java.util.LinkedList</td>
                  </tr>
                  <tr>
                    <td class="nameColumn">objectArray</td>
                    <td class="nullValue">null</td>
                    <td class="nullValue">unknown</td>
                  </tr>
                  <tr>
                    <td class="nameColumn">fieldErrors</td>
                    <td class="emptyCollection">empty</td>
                    <td class="typeColumn">java.util.LinkedHashMap</td>
                  </tr>
                  <tr>
                    <td class="nameColumn">collection</td>
                    <td class="nullValue">null</td>
                    <td class="nullValue">unknown</td>
                  </tr>
                  <tr>
                    <td class="nameColumn">floatNumber</td>
                    <td class="nullValue">null</td>
                    <td class="nullValue">unknown</td>
                  </tr>
                  <tr>
                    <td class="nameColumn">list</td>
                    <td class="nullValue">null</td>
                    <td class="nullValue">unknown</td>
                  </tr>
                  <tr>
                    <td class="nameColumn">enumList</td>
                    <td class="nullValue">null</td>
                    <td class="nullValue">unknown</td>
                  </tr>
                  <tr>
                    <td class="nameColumn">actionMessages</td>
                    <td class="emptyCollection">empty</td>
                    <td class="typeColumn">java.util.LinkedList</td>
                  </tr>
                  <tr>
                    <td class="nameColumn">statusList</td>
                    <td class="valueColumn">
                      <a onclick="expand(this, 'action[&quot;statusList&quot;]')" href="javascript://nop/">Expand</a>
                    </td>
                    <td class="typeColumn">java.util.Arrays$ArrayList</td>
                  </tr>
                  <tr>
                    <td class="nameColumn">texts</td>
                    <td class="valueColumn">
                      <a onclick="expand(this, 'action[&quot;texts&quot;]')" href="javascript://nop/">Expand</a>
                    </td>
                    <td class="typeColumn">java.util.PropertyResourceBundle</td>
                  </tr>
                  <tr>
                    <td class="nameColumn">list3</td>
                    <td class="nullValue">null</td>
                    <td class="nullValue">unknown</td>
                  </tr>
                  <tr>
                    <td class="nameColumn">list2</td>
                    <td class="nullValue">null</td>
                    <td class="nullValue">unknown</td>
                  </tr>
                  <tr>
                    <td class="nameColumn">user</td>
                    <td class="nullValue">null</td>
                    <td class="nullValue">unknown</td>
                  </tr>
                  <tr>
                    <td class="nameColumn">status</td>
                    <td class="valueColumn">
                      <a onclick="expand(this, 'action[&quot;status&quot;]')" href="javascript://nop/">Expand</a>
                    </td>
                    <td class="typeColumn">org.apache.struts2.SomeEnum</td>
                  </tr>
                </table>
                </body>
                </html>
                """);
    }

    @Test
    public void allowlist() throws Exception {
        interceptor.setDevMode("true");
        context.withParameters(HttpParameters.create(Maps.newHashMap("debug", "browser")).build());

        assertThat(container.getInstance(ThreadAllowlist.class))
                .extracting(ThreadAllowlist::getAllowlist).asInstanceOf(InstanceOfAssertFactories.SET)
                .isEmpty();

        interceptor.intercept(invocation);
        invocation.invoke();

        assertThat(container.getInstance(ThreadAllowlist.class))
                .extracting(ThreadAllowlist::getAllowlist).asInstanceOf(InstanceOfAssertFactories.SET)
                .contains(
                        org.apache.struts2.interceptor.ValidationAware.class,
                        org.apache.struts2.Validateable.class,
                        org.apache.struts2.action.Action.class,
                        org.apache.struts2.text.TextProvider.class,
                        org.apache.struts2.ActionSupport.class,
                        org.apache.struts2.locale.LocaleProvider.class,
                        org.apache.struts2.TestAction.class
                );
    }

    @Before
    public void before() {
        request = new MockHttpServletRequest();
        request.setSession(new MockHttpSession());
        response = new MockHttpServletResponse();

        ValueStack valueStack = dispatcher.getValueStackFactory().createValueStack();

        context = valueStack.getActionContext()
                .withServletContext(servletContext)
                .withServletRequest(request)
                .withServletResponse(response)
                .withSession(new SessionMap(request))
                .with(DispatcherConstants.REQUEST, new RequestMap(request));

        interceptor = container.inject(DebuggingInterceptor.class);
        interceptor.init();

        invocation = new MockActionInvocation();
        invocation.setResultCode("mock");
        invocation.setInvocationContext(context);
        action = new TestAction();
        invocation.setAction(action);
        invocation.setStack(valueStack);

        valueStack.set("action", invocation.getAction());

        context = context.withActionInvocation(invocation).bind();
    }

}