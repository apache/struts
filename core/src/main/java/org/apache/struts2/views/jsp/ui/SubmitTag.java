/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
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
package org.apache.struts2.views.jsp.ui;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.Submit;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see Submit
 */
public class SubmitTag extends AbstractUITag {
	
	private static final long serialVersionUID = 2179281109958301343L;
	
	protected String action;
    protected String method;
    protected String align;
    protected String resultDivId;
    protected String onLoadJS;
    protected String notifyTopics;
    protected String listenTopics;
    protected String preInvokeJS;
    protected String type;
    protected String src;

    public Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Submit(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();

        Submit submit = ((Submit) component);
        submit.setAction(action);
        submit.setMethod(method);
        submit.setAlign(align);
        submit.setResultDivId(resultDivId);
        submit.setOnLoadJS(onLoadJS);
        submit.setNotifyTopics(notifyTopics);
        submit.setListenTopics(listenTopics);
        submit.setPreInvokeJS(preInvokeJS);
        submit.setType(type);
        submit.setSrc(src);
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setAlign(String align) {
        this.align = align;
    }

    public void setResultDivId(String resultDivId) {
        this.resultDivId = resultDivId;
    }

    public void setOnLoadJS(String onLoadJS) {
        this.onLoadJS = onLoadJS;
    }

    public void setNotifyTopics(String notifyTopics) {
        this.notifyTopics = notifyTopics;
    }

    public void setListenTopics(String listenTopics) {
        this.listenTopics = listenTopics;
    }

    public void setPreInvokeJS(String preInvokeJS) {
        this.preInvokeJS = preInvokeJS;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }
}
