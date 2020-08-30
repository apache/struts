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
package org.apache.struts2.views.jsp.ui;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;
import org.apache.struts2.components.Link;
import org.apache.struts2.components.Script;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see Link
 */
public class LinkTag extends AbstractUITag {

    protected String href;
    protected String hreflang;
    protected String rel;
    protected String media;
    protected String referrerpolicy;
    protected String sizes;
    protected String crossorigin;
    protected String type;
    protected String as;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Link(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();
        Link link = ((Link) component);
        link.setHref(href);
        link.setHreflang(hreflang);
        link.setRel(rel);
        link.setDisabled(disabled);
        link.setMedia(media);
        link.setReferrerpolicy(referrerpolicy);
        link.setSizes(sizes);
        link.setCrossorigin(crossorigin);
        link.setType(type);
        link.setAs(as);
        link.setTitle(title);
    }

    public void setHref(String href) {
        this.href = href;
    }

    public void setHreflang(String hreflang) {
        this.hreflang = hreflang;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    public void setSizes(String sizes) {
        this.sizes = sizes;
    }

    public void setCrossorigin(String crossorigin) {
        this.crossorigin = crossorigin;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAs(String as) {
        this.as = as;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public void setReferrerpolicy(String referrerpolicy) {
        this.referrerpolicy = referrerpolicy;
    }
}
