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
package org.apache.struts2.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.views.util.UrlHelper;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * <!-- START SNIPPET: javadoc -->
 * Render a submit button. The submit tag is used together with the form tag to provide asynchronous form submissions.
 * The submit can have three different types of rendering:
 * <ul>
 * <li>input: renders as html &lt;input type="submit"...&gt;</li>
 * <li>image: renders as html &lt;input type="image"...&gt;</li>
 * <li>button: renders as html &lt;button type="submit"...&gt;</li>
 * </ul>
 * Please note that the button type has advantages by adding the possibility to seperate the submitted value from the
 * text shown on the button face, but has issues with Microsoft Internet Explorer at least up to 6.0
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;s:submit value="%{'Submit'}" /&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 * <pre>
 * <!-- START SNIPPET: example2 -->
 * Render an image submit:
 * &lt;s:submit type="image" value="%{'Submit'}" label="Submit the form" src="submit.gif"/&gt;
 * <!-- END SNIPPET: example2 -->
 * </pre>
 * <pre>
 * <!-- START SNIPPET: example3 -->
 * Render an button submit:
 * &lt;s:submit type="button" value="%{'Submit'}" label="Submit the form"/&gt;
 * <!-- END SNIPPET: example3 -->
 * </pre>
 *
 * <!-- START SNIPPET: ajaxJavadoc -->
 * <B>THE FOLLOWING IS ONLY VALID WHEN AJAX IS CONFIGURED</B>
 * <ul>
 *      <li>href</li>
 *      <li>errorText</li>
 *      <li>afterLoading</li>
 *      <li>beforeLoading</li>
 *      <li>executeScripts</li>
 *      <li>loadingText</li>
 *      <li>refreshListenTopic</li>
 *      <li>handler</li>
 *      <li>formId</li>
 *      <li>formFilter</li>
 *      <li>targets</li>
 * </ul>
 * 'targets' is a list of element ids whose content will be updated with the
 * text returned from request.<p/>
 * 'errorText' is the text that will be displayed when there is an error making the request.<p/>
 * 'afterLoading' is the name of a function that will be called after the request.<p/>
 * 'beforeLoading' is the name of a function that will be called before the request.<p/>
 * 'executeScripts' if set to true will execute javascript sections in the returned text.<p/>
 * 'loadingText' is the text that will be displayed on the 'targets' elements while making the
 * request.<p/>
 * 'handler' is the name of the function that will take care of making the AJAX request. Dojo's widget
 * and dom node are passed as parameters).<p/>
 * 'formId' is the id of the html form whose fields will be seralized and passed as parameters
 * in the request.<p/>
 * 'formFilter' is the name of a function which will be used to filter the fields that will be
 * seralized. This function takes as a parameter the element and returns true if the element
 * should be included.<p/>
 * 'refreshListenTopic' is the topic that forces an update
 * <pre>
 * <!-- START SNIPPET: ajxExample1 -->
 * Remote form replacing another div:
 * &lt;div id='two' style="border: 1px solid yellow;"&gt;Initial content&lt;/div&gt;
 * &lt;s:form
 *       id='theForm2'
 *       cssStyle="border: 1px solid green;"
 *       action='/AjaxRemoteForm.action'
 *       method='post'
 *       theme="ajax"&gt;
 *
 *   &lt;input type='text' name='data' value='Struts User' /&gt;
 *   &lt;s:submit value="GO2" theme="ajax" resultDivId="two" /&gt;
 *
 * &lt;/s:form &gt;
 * <!-- END SNIPPET: ajxExample1 -->
 * </pre>
 *
 *
 * @s.tag name="submit" tld-body-content="JSP" tld-tag-class="org.apache.struts2.views.jsp.ui.SubmitTag"
 * description="Render a submit button"
 */
public class Submit extends FormButton implements RemoteUICallBean{
    final public static String TEMPLATE = "submit";

    protected String href;
    protected String errorText;
    protected String afterLoading;
    protected String beforeLoading;
    protected String executeScripts;
    protected String loadingText;
    protected String refreshListenTopic;
    protected String handler;
    protected String formId;
    protected String formFilter;
    protected String src;
    protected String targets;

    public Submit(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    public void evaluateParams() {
        if (value == null) {
            value = "Submit";
        }
        super.evaluateParams();
    }

    public void evaluateExtraParams() {
        super.evaluateExtraParams();

        if (href != null)
            addParameter("href", UrlHelper.buildUrl(findString(href), request, response, null));
        if (errorText != null)
            addParameter("errorText", findString(errorText));
        if (loadingText != null)
            addParameter("loadingText", findString(loadingText));
        if (afterLoading != null)
            addParameter("afterLoading", findString(afterLoading));
        if (beforeLoading != null)
            addParameter("beforeLoading", findString(beforeLoading));
        if (executeScripts != null)
            addParameter("executeScripts", findValue(executeScripts, Boolean.class));
        if (refreshListenTopic != null)
            addParameter("refreshListenTopic", findValue(refreshListenTopic, String.class));
        if (handler != null)
            addParameter("handler", findString(handler));
        if (formId != null)
            addParameter("formId", findString(formId));
        if (formFilter != null)
            addParameter("formFilter", findString(formFilter));
        if (src != null)
            addParameter("src", findString(src));
        if (targets != null)
            addParameter("targets", findString(targets));
    }

    /**
     * Indicate whether the concrete button supports the type "image".
     *
     * @return <tt>true</tt> to indicate type image is supported.
     */
    protected boolean supportsImageType() {
        return true;
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.components.RemoteUICallBean#setRefreshListenTopic(java.lang.String)
     */
    public void setRefreshListenTopic(String refreshListenTopic) {
        this.refreshListenTopic = refreshListenTopic;
    }

    /**
     * The theme to use for the element. <b>This tag will usually use the ajax theme.</b>
     * @s.tagattribute required="false" type="String"
     */
    public void setTheme(String theme) {
        super.setTheme(theme);
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.components.RemoteUICallBean#setHref(java.lang.String)
     */
    public void setHref(String href) {
        this.href = href;
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.components.RemoteUICallBean#setErrorText(java.lang.String)
     */
    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.components.RemoteUICallBean#setAfterLoading(java.lang.String)
     */
    public void setAfterLoading(String afterLoading) {
        this.afterLoading = afterLoading;
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.components.RemoteUICallBean#setBeforeLoading(java.lang.String)
     */
    public void setBeforeLoading(String beforeLoading) {
        this.beforeLoading = beforeLoading;
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.components.RemoteUICallBean#setExecuteScripts(java.lang.String)
     */
    public void setExecuteScripts(String executeScripts) {
        this.executeScripts = executeScripts;
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.components.RemoteUICallBean#setLoadingText(java.lang.String)
     */
    public void setLoadingText(String loadingText) {
        this.loadingText = loadingText;
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.components.RemoteUICallBean#setHandler(java.lang.String)
     */
    public void setHandler(String handler) {
        this.handler = handler;
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.components.RemoteUICallBean#setFormFilter(java.lang.String)
     */
    public void setFormFilter(String formFilter) {
        this.formFilter = formFilter;
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.components.RemoteUICallBean#setFormId(java.lang.String)
     */
    public void setFormId(String formId) {
        this.formId = formId;
    }

    /**
     * Supply an image src for <i>image</i> type submit button. Will have no effect for types <i>input</i> and <i>button</i>.
     * @s.tagattribute required="false"
     */
    public void setSrc(String src) {
        this.src = src;
    }

    /**
     * Comma delimited list of ids of the elements whose content will be updated
     * @s.tagattribute required="false" type="String"
     */
    public void setTargets(String targets) {
        this.targets = targets;
    }
}
