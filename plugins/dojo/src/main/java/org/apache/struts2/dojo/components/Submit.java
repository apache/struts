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

package org.apache.struts2.dojo.components;

import java.io.Writer;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Form;
import org.apache.struts2.components.FormButton;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;
import org.apache.struts2.views.annotations.StrutsTagSkipInheritance;

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * <!-- START SNIPPET: javadoc -->
 * Renders a submit button that can submit a form asynchronously.
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
 * <p>Examples</p>
 * <!-- START SNIPPET: example1 -->
 * &lt;sx:submit value="%{'Submit'}" /&gt;
 * <!-- END SNIPPET: example1 -->
 *
 * <!-- START SNIPPET: example2 -->
 * &lt;sx:submit type="image" value="%{'Submit'}" label="Submit the form" src="submit.gif"/&gt;
 * <!-- END SNIPPET: example2 -->

 * <!-- START SNIPPET: example3 -->
 * &lt;sx:submit type="button" value="%{'Submit'}" label="Submit the form"/&gt;
 * <!-- END SNIPPET: example3 -->
 *
 * <!-- START SNIPPET: example4 -->
 * &lt;div id="div1"&gt;Div 1&lt;/div&gt;
 * &lt;s:url id="ajaxTest" value="/AjaxTest.action"/&gt;
 *
 * &lt;sx:submit id="link1" href="%{ajaxTest}" target="div1" /&gt;
 * <!-- END SNIPPET: example4 -->
 *
 * <!-- START SNIPPET: example5 -->
 * &lt;s:form id="form" action="AjaxTest"&gt;
 *      &lt;input type="textbox" name="data"&gt;
 *      &lt;sx:submit /&gt;
 * &lt;/s:form&gt;
 * <!-- END SNIPPET: example5 -->
 *
 * <!-- START SNIPPET: example6 -->
 * &lt;s:form id="form" action="AjaxTest"&gt;
 *      &lt;input type="textbox" name="data"&gt;
 * &lt;/s:form&gt;
 *
 * &lt;sx:submit formId="form" /&gt;
 * <!-- END SNIPPET: example6 -->
 *
 * <!-- START SNIPPET: example7 -->
 * &lt;script type="text/javascript"&gt;
 * dojo.event.topic.subscribe("/before", function(event, widget){
 *     alert('inside a topic event. before request');
 *     //event: set event.cancel = true, to cancel request
 *     //widget: widget that published the topic
 * });
 * &lt;/script&gt;
 *
 * &lt;sx:submit beforeNotifyTopics="/before" /&gt;
 * <!-- END SNIPPET: example7 -->
 *
 * <!-- START SNIPPET: example8 -->
 * &lt;script type="text/javascript"&gt;
 * dojo.event.topic.subscribe("/after", function(data, request, widget){
 *     alert('inside a topic event. after request');
 *     //data : text returned from request(the html)
 *     //request: XMLHttpRequest object
 *     //widget: widget that published the topic
 * });
 * &lt;/script&gt;
 *
 * &lt;sx:submit afterNotifyTopics="/after" highlightColor="red" href="%{#ajaxTest}" /&gt;
 * <!-- END SNIPPET: example8 -->
 *
 * <!-- START SNIPPET: example9 -->
 * &lt;script type="text/javascript"&gt;
 * dojo.event.topic.subscribe("/error", function(error, request, widget){
 *     alert('inside a topic event. on error');
 *     //error : error object (error.message has the error message)
 *     //request: XMLHttpRequest object
 *     //widget: widget that published the topic
 * });
 * &lt;/script&gt;
 *
 * &lt;img id="ind1" src="${pageContext.request.contextPath}/images/indicator.gif" style="display:none"/&gt;
 * &lt;sx:submit errorNotifyTopics="/error" indicator="ind1" href="%{#ajaxTest}" /&gt;
 * <!-- END SNIPPET: example9 -->
 */
@StrutsTag(name="submit", tldTagClass="org.apache.struts2.dojo.views.jsp.ui.SubmitTag", description="Render a submit button")
public class Submit extends FormButton implements RemoteBean {

    private static final Logger LOG = LoggerFactory.getLogger(Submit.class);
    private final static transient Random RANDOM = new Random();

    final public static String OPEN_TEMPLATE = "submit";
    final public static String TEMPLATE = "submit-close";

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
    protected String targets;
    protected String beforeNotifyTopics;
    protected String afterNotifyTopics;
    protected String errorNotifyTopics;
    protected String highlightColor;
    protected String highlightDuration;
    protected String validate;
    protected String ajaxAfterValidation;
    protected String separateScripts;
    protected String transport;
    protected String parseContent;

    public Submit(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    @Override
    public String getDefaultOpenTemplate() {
        return OPEN_TEMPLATE;
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
        if (showLoadingText != null)
            addParameter("showLoadingText", findString(showLoadingText));
        if (beforeNotifyTopics != null)
            addParameter("beforeNotifyTopics", findString(beforeNotifyTopics));
        if (afterNotifyTopics != null)
            addParameter("afterNotifyTopics", findString(afterNotifyTopics));
        if (errorNotifyTopics != null)
            addParameter("errorNotifyTopics", findString(errorNotifyTopics));
        if (highlightColor != null)
            addParameter("highlightColor", findString(highlightColor));
        if (highlightDuration != null)
            addParameter("highlightDuration", findString(highlightDuration));
        if (separateScripts != null)
            addParameter("separateScripts", findValue(separateScripts, Boolean.class));
        if (transport != null)
            addParameter("transport", findString(transport));
        if (parseContent != null)
            addParameter("parseContent", findValue(parseContent, Boolean.class));

        Boolean validateValue = false;
        if (validate != null) {
            validateValue = (Boolean) findValue(validate, Boolean.class);
            addParameter("validate", validateValue);
        }

        Form form = (Form) findAncestor(Form.class);
        if (form != null)
            addParameter("parentTheme", form.getTheme());

        if (ajaxAfterValidation != null)
            addParameter("ajaxAfterValidation", findValue(ajaxAfterValidation, Boolean.class));

        // generate a random ID if not explicitly set and not parsing the content
        Boolean parseContent = (Boolean)stack.getContext().get(Head.PARSE_CONTENT);
        boolean generateId = (parseContent != null ? !parseContent : true);

        addParameter("pushId", generateId);
        if ((this.id == null || this.id.length() == 0) && generateId) {
            // resolves Math.abs(Integer.MIN_VALUE) issue reported by FindBugs
            // http://findbugs.sourceforge.net/bugDescriptions.html#RV_ABSOLUTE_VALUE_OF_RANDOM_INT
            int nextInt = RANDOM.nextInt();
            nextInt = nextInt == Integer.MIN_VALUE ? Integer.MAX_VALUE : Math.abs(nextInt);
            this.id = "widget_" + String.valueOf(nextInt);
            addParameter("id", this.id);
        }
    }

    @Override
    @StrutsTagSkipInheritance
    public void setTheme(String theme) {
        super.setTheme(theme);
    }

    @Override
    public String getTheme() {
        return "ajax";
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

    @StrutsTagAttribute(description="The URL to call to obtain the content. Note: If used with ajax context, the value must be set as an url tag value.")
    public void setHref(String href) {
        this.href = href;
    }

    @StrutsTagAttribute(description="The text to display to the user if the is an error fetching the content")
    public void setErrorText(String errorText) {
        this.errorText = errorText;
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

    @StrutsTagAttribute(description="Comma delimmited list of topics that will published before and after the request, and on errors")
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

    @StrutsTagAttribute(description="Show loading text on targets", type="Boolean", defaultValue="false")
    public void setShowLoadingText(String showLoadingText) {
        this.showLoadingText = showLoadingText;
    }

    @StrutsTagAttribute(description="The css class to use for element")
    public void setCssClass(String cssClass) {
        super.setCssClass(cssClass);
    }

    @StrutsTagAttribute(description="The css style to use for element")
    public void setCssStyle(String cssStyle) {
        super.setCssStyle(cssStyle);
    }

    @StrutsTagAttribute(description="The id to use for the element")
    public void setId(String id) {
        super.setId(id);
    }

    @StrutsTagAttribute(description="The name to set for element")
    public void setName(String name) {
        super.setName(name);
    }

    @StrutsTagAttribute(description="The type of submit to use. Valid values are <i>input</i>, " +
        "<i>button</i> and <i>image</i>.", defaultValue="input")
    public void setType(String type) {
        super.setType(type);
    }

    @StrutsTagAttribute(description="Preset the value of input element.")
    public void setValue(String value) {
        super.setValue(value);
    }

    @StrutsTagAttribute(description="Label expression used for rendering a element specific label")
    public void setLabel(String label) {
        super.setLabel(label);
    }

    @StrutsTagAttribute(description="Comma delimmited list of topics that will published after the request(if the request succeeds)")
    public void setAfterNotifyTopics(String afterNotifyTopics) {
        this.afterNotifyTopics = afterNotifyTopics;
    }

    @StrutsTagAttribute(description="Comma delimmited list of topics that will published before the request")
    public void setBeforeNotifyTopics(String beforeNotifyTopics) {
        this.beforeNotifyTopics = beforeNotifyTopics;
    }

    @StrutsTagAttribute(description="Comma delimmited list of topics that will published after the request(if the request fails)")
    public void setErrorNotifyTopics(String errorNotifyTopics) {
        this.errorNotifyTopics = errorNotifyTopics;
    }

    @StrutsTagAttribute(description = "Color used to perform a highlight effect on the elements specified in the 'targets' attribute",
        defaultValue = "none")
    public void setHighlightColor(String highlightColor) {
        this.highlightColor = highlightColor;
    }

    @StrutsTagAttribute(description = "Duration of highlight effect in milliseconds. Only valid if 'highlightColor' attribute is set",
        defaultValue = "1000")
    public void setHighlightDuration(String highlightDuration) {
        this.highlightDuration = highlightDuration;
    }

    @StrutsTagAttribute(description = "Perform Ajax validation. 'ajaxValidation' interceptor must be applied to action", type="Boolean",
        defaultValue = "false")
    public void setValidate(String validate) {
        this.validate = validate;
    }

    @StrutsTagAttribute(description = "Make an asynchronous request if validation succeeds. Only valid if 'validate' is 'true'", type="Boolean",
        defaultValue = "false")
    public void setAjaxAfterValidation(String ajaxAfterValidation) {
        this.ajaxAfterValidation = ajaxAfterValidation;
    }

    @StrutsTagSkipInheritance
    public void setAction(String action) {
        super.setAction(action);
    }

    @StrutsTagAttribute(description="Run scripts in a separate scope, unique for each tag", defaultValue="true")
    public void setSeparateScripts(String separateScripts) {
        this.separateScripts = separateScripts;
    }

    @StrutsTagAttribute(description="Transport used by Dojo to make the request", defaultValue="XMLHTTPTransport")
    public void setTransport(String transport) {
        this.transport = transport;
    }

    @StrutsTagAttribute(description="Parse returned HTML for Dojo widgets", defaultValue="true", type="Boolean")
    public void setParseContent(String parseContent) {
        this.parseContent = parseContent;
    }
}
