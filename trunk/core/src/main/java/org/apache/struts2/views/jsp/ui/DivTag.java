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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.Div;

import com.opensymphony.xwork2.util.ValueStack;

public class DivTag extends AbstractClosingTag {
	
	private static final long serialVersionUID = 5309231035916461758L;
	
	protected String href;
    protected String updateFreq;
    protected String delay="1";
    protected String loadingText;
    protected String errorText;
    protected String showErrorTransportText;
    protected String listenTopics;
    protected String afterLoading;

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Div(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();

        Div div = (Div) component;
        div.setHref(href);
        div.setUpdateFreq(updateFreq);
        div.setDelay(delay);
        div.setLoadingText(loadingText);
        div.setErrorText(errorText);
        div.setShowErrorTransportText(showErrorTransportText);
        div.setListenTopics(listenTopics);
        div.setAfterLoading(afterLoading);
    }

    public void setHref(String href) {
        this.href = href;
    }

    public void setUpdateFreq(String updateFreq) {
        this.updateFreq = updateFreq;
    }

    public void setDelay(String delay) {
        this.delay = delay;
    }

    public void setLoadingText(String loadingText) {
        this.loadingText = loadingText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    public void setShowErrorTransportText(String showErrorTransportText) {
        this.showErrorTransportText = showErrorTransportText;
    }

    public void setListenTopics(String listenTopics) {
        this.listenTopics = listenTopics;
    }

    public void setAfterLoading(String afterLoading) {
        this.afterLoading = afterLoading;
    }
}
