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

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * Create a optgroup component which needs to resides within a select tag.
 *
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/>
 *
 * <!-- START SNIPPET: notice -->
 *
 * This component is to be used within a  Select component.
 *
 * <!-- END SNIPPET: notice -->
 *
 * <p/>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 *
 * &lt;s:select label="My Selection"
 *            name="mySelection"
 *            value="%{'POPEYE'}"
 *            list="%{#{'SUPERMAN':'Superman', 'SPIDERMAN':'spiderman'}}"&gt;
 *    &lt;s:optgroup label="Adult"
 *                 list="%{#{'SOUTH_PARK':'South Park'}}" /&gt;
 *    &lt;s:optgroup label="Japanese"
 *                 list="%{#{'POKEMON':'pokemon','DIGIMON':'digimon','SAILORMOON':'Sailormoon'}}" /&gt;
 * &lt;/s:select&gt;
 *
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 */
@StrutsTag(
    name="optgroup",
    tldTagClass="org.apache.struts2.views.jsp.ui.OptGroupTag",
    description="Renders a Select Tag's OptGroup Tag")
public class OptGroup extends Component {

    public static final String INTERNAL_LIST_UI_BEAN_LIST_PARAMETER_KEY = "optGroupInternalListUiBeanList";

    private static Logger LOG = LoggerFactory.getLogger(OptGroup.class);

    protected HttpServletRequest req;
    protected HttpServletResponse res;

    protected ListUIBean internalUiBean;

    public OptGroup(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        super(stack);
        this.req = req;
        this.res = res;
        internalUiBean = new ListUIBean(stack, req, res) {
            protected String getDefaultTemplate() {
                return "empty";
            }
        };
    }
    
    @Inject
    public void setContainer(Container container) {
        container.inject(internalUiBean);
    }

    public boolean end(Writer writer, String body) {
        Select select = (Select) findAncestor(Select.class);
        if (select == null) {
            LOG.error("incorrect use of OptGroup component, this component must be used within a Select component",
                    new IllegalStateException("incorrect use of OptGroup component, this component must be used within a Select component"));
            return false;
        }
        internalUiBean.start(writer);
        internalUiBean.end(writer, body);

        List listUiBeans = (List) select.getParameters().get(INTERNAL_LIST_UI_BEAN_LIST_PARAMETER_KEY);
        if (listUiBeans == null) {
            listUiBeans = new ArrayList();
        }
        listUiBeans.add(internalUiBean);
        select.addParameter(INTERNAL_LIST_UI_BEAN_LIST_PARAMETER_KEY, listUiBeans);

        return false;
    }

    @StrutsTagAttribute(description="Set the label attribute")
    public void setLabel(String label) {
        internalUiBean.setLabel(label);
    }

    @StrutsTagAttribute(description="Set the disable attribute.")
    public void setDisabled(String disabled) {
        internalUiBean.setDisabled(disabled);
    }

    @StrutsTagAttribute(description="Set the list attribute.")
    public void setList(String list) {
        internalUiBean.setList(list);
    }

    @StrutsTagAttribute(description="Set the listKey attribute.")
    public void setListKey(String listKey) {
        internalUiBean.setListKey(listKey);
    }

    @StrutsTagAttribute(description="Set the listValue attribute.")
    public void setListValue(String listValue) {
        internalUiBean.setListValue(listValue);
    }
}
