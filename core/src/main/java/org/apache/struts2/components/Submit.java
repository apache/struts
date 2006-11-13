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
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;s:submit value="%{'Submit'}" /&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * <pre>
 * <!-- START SNIPPET: example2 -->
 * Render an image submit:
 * &lt;s:submit type="image" value="%{'Submit'}" label="Submit the form" src="submit.gif"/&gt;
 * <!-- END SNIPPET: example2 -->
 * </pre>
 *
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
 *      <li>resultDivId</li>
 *      <li>notifyTopics</li>
 *      <li>onLoadJS</li>
 *      <li>preInvokeJS</li>
 * </ul>
 * The remote form has three basic modes of use, using the resultDivId,
 * the notifyTopics, or the onLoadJS. You can mix and match any combination of
 * them to get your desired result. All of these examples are contained in the
 * Ajax example webapp. Lets go through some scenarios to see how you might use it:
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
 *
 * <!-- START SNIPPET: ajxExDescription2 -->
 * Notify other controls(divs) of a change. Using an pub-sub model you can
 * notify others that your control changed and they can take the appropriate action.
 * Most likely they will execute some action to refresh. The notifyTopics does this
 * for you. You can have many topic names in a comma delimited list.
 * eg: notifyTopics="newPerson, dataChanged" .
 * Here is an example of this approach:
 * <!-- END SNIPPET: ajxExDescription2 -->
 *
 * <pre>
 * <!-- START SNIPPET: ajxExample2 -->
 * &lt;s:form id="frm1" action="newPersonWithXMLResult" theme="ajax"  &gt;
 *     &lt;s:textfield label="Name" name="person.name" value="person.name" size="20" required="true" /&gt;
 *     &lt;s:submit id="submitBtn" value="Save" theme="ajax"  cssClass="primary"  notifyTopics="personUpdated, systemWorking" /&gt;
 * &lt;/s:form &gt;
 *
 * &lt;s:div href="/listPeople.action" theme="ajax" errorText="error opps"
 *         loadingText="loading..." id="cart-body" &gt;
 *     &lt;s:action namespace="" name="listPeople" executeResult="true" /&gt;
 * &lt;/s:div&gt;
 * <!-- END SNIPPET: ajxExample2 -->
 * </pre>
 *
 * <!-- START SNIPPET: ajxExDescription3 -->
 * Massage the results with JavaScript. Say that your result returns some h
 * appy XML and you want to parse it and do lots of cool things with it.
 * The way to do this is with a onLoadJS handler. Here you provide the name of
 * a JavaScript function to be called back with the result and the event type.
 * The only key is that you must use the variable names 'data' and 'type' when
 * defining the callback. For example: onLoadJS="myFancyDancyFunction(data, type)".
 * While I talked about XML in this example, your not limited to XML, the data in
 * the callback will be exactly whats returned as your result.
 * Here is an example of this approach:
 * <!-- END SNIPPET: ajxExDescription3 -->
 *
 * <pre>
 * <!-- START SNIPPET: ajxExample3 -->
 * &lt;script language="JavaScript" type="text/javascript"&gt;
 *     function doGreatThings(data, type) {
 *         //Do whatever with your returned fragment...
 *         //Perhapps.... if xml...
 *               var xml = dojo.xml.domUtil.createDocumentFromText(data);
 *               var people = xml.getElementsByTagName("person");
 *               for(var i = 0;i < people.length; i ++){
 *                   var person = people[i];
 *                   var name = person.getAttribute("name")
 *                   var id = person.getAttribute("id")
 *                   alert('Thanks dude. Person: ' + name + ' saved great!!!');
 *               }
 *
 *     }
 * &lt;/script&gt;
 *
 * &lt;s:form id="frm1" action="newPersonWithXMLResult" theme="ajax"  &gt;
 *     &lt;s:textfield label="Name" name="person.name" value="person.name" size="20" required="true" /&gt;
 *     &lt;s:submit id="submitBtn" value="Save" theme="ajax"  cssClass="primary"  onLoadJS="doGreatThings(data, type)" /&gt;
 * &lt;/s:form&gt;
 * <!-- END SNIPPET: ajxExample3 -->
 * </pre>
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
