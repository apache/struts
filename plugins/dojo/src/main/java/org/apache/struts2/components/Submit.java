/*
 * $Id: Submit.java 508285 2007-02-16 02:42:24Z musachy $
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

import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;
import org.apache.struts2.views.annotations.StrutsTagSkipInheritance;

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
 *      <li>listenTopics</li>
 *      <li>notifyTopics</li>
 *      <li>executeScripts</li>
 *      <li>loadingText</li>
 *      <li>listenTopics</li>
 *      <li>handler</li>
 *      <li>formId</li>
 *      <li>formFilter</li>
 *      <li>targets</li>
 *      <li>showErrorTransportText</li>
 *      <li>targets</li>
 *      <li>indicator</li>
 * </ul>
 * 'resultDivId' Deprecated. Use targets.</p>
 * 'targets' is a list of element ids whose content will be updated with the
 * text returned from request.<p/>
 * 'errorText' is the text that will be displayed when there is an error making the request.<p/>
 * 'onLoadJS' Deprecated. Use 'notifyTopics'.<p/>
 * 'preInvokeJS' Deprecated. Use 'notifyTopics'.<p/>
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
 * 'listenTopics' comma separated list of topics names, that will trigger a request
 * 'indicator' element to be shown while the request executing
 * 'showErrorTransportText': whether errors should be displayed (on 'targets')</p>
 * 'showLoadingText' show loading text on targets</p>
 * 'notifyTopics' comma separated list of topics names, that will be published. Three parameters are passed:<p/>
 * <ul>
 *      <li>data: html or json object when type='load' or type='error'</li>
 *      <li>type: 'before' before the request is made, 'load' when the request succeeds, or 'error' when it fails</li>
 *      <li>request: request javascript object, when type='load' or type='error'</li>
 * </ul>
 * <!-- END SNIPPET: ajaxJavadoc -->
 *
 * <!-- START SNIPPET: ajxExDescription1 -->
 * Show the results in another div. If you want your results to be shown in
 * a div, use the resultDivId where the id is the id of the div you want them
 * shown in. This is an inner HTML approah. Your results get jammed into
 * the div for you. Here is a sample of this approach:
 * <!-- END SNIPPET: ajxExDescription1 -->
 *
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
 */
@StrutsTag(name="submit", tldTagClass="org.apache.struts2.views.jsp.ui.SubmitTag", description="Render a submit button")
@StrutsTagSkipInheritance
public class Submit extends FormButton implements RemoteUICallBean {
    
    private static final Log LOG = LogFactory.getLog(Submit.class);
    
    final public static String TEMPLATE = "submit";

    protected String href;
    protected String errorText;
    protected String executeScripts;
    protected String loadingText;
    protected String listenTopics;
    protected String handler;
    protected String formId;
    protected String formFilter;
    protected String src;
    protected String notifyTopics;
    protected String showErrorTransportText;
    protected String indicator;
    protected String showLoadingText;
    //these two are called "preInvokeJS" and "onLoadJS" on the tld
    //Names changed here to keep some consistency
    protected String beforeLoading;
    protected String afterLoading;

    //this one is called "resultDivId" on the tld
    protected String targets;

    public Submit(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    public void evaluateParams() {
        if ((key == null) && (value == null)) {
            value = "Submit";
        }

        if (((key != null)) && (value == null)) {
            this.value = "%{getText('"+key +"')}";
        }

        super.evaluateParams();
    }

    public void evaluateExtraParams() {
        super.evaluateExtraParams();

        if (href != null)
            addParameter("href", findString(href));
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
        if (listenTopics != null)
            addParameter("listenTopics", findString(listenTopics));
        if (notifyTopics != null)
            addParameter("notifyTopics", findString(notifyTopics));
        if (handler != null)
            addParameter("handler", findString(handler));
        if (formId != null)
            addParameter("formId", findString(formId));
        if (formFilter != null)
            addParameter("formFilter", findString(formFilter));
        if (src != null)
            addParameter("src", findString(src));
        if (indicator != null)
            addParameter("indicator", findString(indicator));
        if (targets != null)
            addParameter("targets", findString(targets));
        if (showLoadingText != null)
            addParameter("showLoadingText", findString(showLoadingText));
    }

    /**
     * Indicate whether the concrete button supports the type "image".
     *
     * @return <tt>true</tt> to indicate type image is supported.
     */
    protected boolean supportsImageType() {
        return true;
    }
    
    /**
     * Overrides to be able to render body in a template rather than always before the template
     */
    public boolean end(Writer writer, String body) {
        evaluateParams();
        try {
            addParameter("body", body);
            
            mergeTemplate(writer, buildTemplateName(template, getDefaultTemplate()));
        } catch (Exception e) {
            LOG.error("error when rendering", e);
        }
        finally {
            popComponentStack();
        }

        return false;
    }

    @StrutsTagAttribute(description="Topic that will trigger the remote call")
    public void setListenTopics(String listenTopics) {
        this.listenTopics = listenTopics;
    }

    @StrutsTagAttribute(description="The theme to use for the element. <b>This tag will usually use the ajax theme.</b>")
    public void setTheme(String theme) {
        super.setTheme(theme);
    }

    @StrutsTagAttribute(description="The URL to call to obtain the content. Note: If used with ajax context, the value must be set as an url tag value.")
    public void setHref(String href) {
        this.href = href;
    }

    @StrutsTagAttribute(description="The text to display to the user if the is an error fetching the content")
    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    @StrutsTagAttribute(name="onLoadJS", description="Deprecated. Use 'notifyTopics'. Javascript code execute after reload")
    public void setAfterLoading(String afterLoading) {
        this.afterLoading = afterLoading;
    }


    @StrutsTagAttribute(name="preInvokeJS", description="Deprecated. Use 'notifyTopics'. Javascript code execute before reload")
    public void setBeforeLoading(String beforeLoading) {
        this.beforeLoading = beforeLoading;
    }

    @StrutsTagAttribute(description="Javascript code in the fetched content will be executed", type="Boolean", defaultValue="false")
    public void setExecuteScripts(String executeScripts) {
        this.executeScripts = executeScripts;
    }

    @StrutsTagAttribute(description="Text to be shown while content is being fetched", defaultValue="Loading...")
    public void setLoadingText(String loadingText) {
        this.loadingText = loadingText;
    }

    @StrutsTagAttribute(description="Javascript function name that will make the request")
    public void setHandler(String handler) {
        this.handler = handler;
    }

    @StrutsTagAttribute(description="Function name used to filter the fields of the form.")
    public void setFormFilter(String formFilter) {
        this.formFilter = formFilter;
    }

    @StrutsTagAttribute(description="Form id whose fields will be serialized and passed as parameters")
    public void setFormId(String formId) {
        this.formId = formId;
    }

    @StrutsTagAttribute(description="Supply an image src for <i>image</i> type submit button. Will have no effect for types <i>input</i> and <i>button</i>.")
    public void setSrc(String src) {
        this.src = src;
    }

    @StrutsTagAttribute(description="Comma delimited list of ids of the elements whose content will be updated")
    public void setTargets(String targets) {
        this.targets = targets;
    }


    @StrutsTagAttribute(description="Topics that will published when the remote call completes")
    public void setNotifyTopics(String notifyTopics) {
        this.notifyTopics = notifyTopics;
    }


    @StrutsTagAttribute(description="Set whether errors will be shown or not", type="Boolean", defaultValue="true")
    public void setShowErrorTransportText(String showErrorTransportText) {
        this.showErrorTransportText = showErrorTransportText;
    }


    @StrutsTagAttribute(description="Set indicator")
    public void setIndicator(String indicator) {
        this.indicator = indicator;
    }

    @StrutsTagAttribute(description="Show loading text on targets", type="Boolean", defaultValue="true")
    public void setShowLoadingText(String showLoadingText) {
        this.showLoadingText = showLoadingText;
    }
}
