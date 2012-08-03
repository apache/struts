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
import java.util.Iterator;
import java.util.List;

import org.apache.struts2.components.Param.UnnamedParametric;
import org.apache.struts2.util.AppendIteratorFilter;
import org.apache.struts2.util.MakeIterator;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * <!-- START SNIPPET: javadoc -->
 * <p>Component for AppendIteratorTag, which jobs is to append iterators to form an
 * appended iterator whereby entries goes from one iterator to another after each
 * respective iterator is exhausted of entries.</p>
 *
 * <p>For example, if there are 3 iterator appended (each iterator has 3 entries),
 * the following will be how the appended iterator entries will be arranged:</p>
 *
 * <ol>
 *      <li>First Entry of the First Iterator</li>
 *      <li>Second Entry of the First Iterator</li>
 *      <li>Third Entry of the First Iterator</li>
 *      <li>First Entry of the Second Iterator</li>
 *      <li>Second Entry of the Second Iterator</li>
 *      <li>Third Entry of the Second Iterator</li>
 *      <li>First Entry of the Third Iterator</li>
 *      <li>Second Entry of the Third Iterator</li>
 *      <li>Third Entry of the Third ITerator</li>
 * </ol>
 * <!-- END SNIPPET: javadoc -->
 *
 * <!-- START SNIPPET: params -->
 * <ul>
 *      <li>id (String) - the id of which if supplied will have the resultant
 *                        appended iterator stored under in the stack's context</li>
 * </ul>
 * <!-- END SNIPPET: params -->
 *
 *
 * <!-- START SNIPPET: code -->
 * public class AppendIteratorTagAction extends ActionSupport {
 *
 *  private List myList1;
 *  private List myList2;
 *  private List myList3;
 *
 *
 *  public String execute() throws Exception {
 *
 *      myList1 = new ArrayList();
 *      myList1.add("1");
 *      myList1.add("2");
 *      myList1.add("3");
 *
 *      myList2 = new ArrayList();
 *      myList2.add("a");
 *      myList2.add("b");
 *      myList2.add("c");
 *
 *      myList3 = new ArrayList();
 *      myList3.add("A");
 *      myList3.add("B");
 *      myList3.add("C");
 *
 *      return "done";
 *  }
 *
 *  public List getMyList1() { return myList1; }
 *  public List getMyList2() { return myList2; }
 *  public List getMyList3() { return myList3; }
 *}
 * <!-- END SNIPPET: code -->
 *
 * <!-- START SNIPPET: example -->
 * &lt;s:append var="myAppendIterator"&gt;
 *      &lt;s:param value="%{myList1}" /&gt;
 *      &lt;s:param value="%{myList2}" /&gt;
 *      &lt;s:param value="%{myList3}" /&gt;
 * &lt;/s:append&gt;
 * &lt;s:iterator value="%{#myAppendIterator}"&gt;
 *      &lt;s:property /&gt;
 * &lt;/s:iterator&gt;
 * <!-- END SNIPPET: example -->
 *
 *
 * @see org.apache.struts2.util.AppendIteratorFilter
 * @see org.apache.struts2.views.jsp.iterator.AppendIteratorTag
 *
 */
@StrutsTag(name="append", tldTagClass="org.apache.struts2.views.jsp.iterator.AppendIteratorTag", description="Append the values of a list of iterators to one iterator")
public class AppendIterator extends ContextBean implements UnnamedParametric {

    private static final Logger LOG = LoggerFactory.getLogger(AppendIterator.class);

    private AppendIteratorFilter appendIteratorFilter= null;
    private List _parameters;

    public AppendIterator(ValueStack stack) {
        super(stack);
    }

    public boolean start(Writer writer) {
        _parameters = new ArrayList();
        appendIteratorFilter = new AppendIteratorFilter();

        return super.start(writer);
    }

    public boolean end(Writer writer, String body) {

        for (Iterator paramEntries = _parameters.iterator(); paramEntries.hasNext(); ) {

            Object iteratorEntryObj = paramEntries.next();
            if (! MakeIterator.isIterable(iteratorEntryObj)) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("param with value resolved as "+iteratorEntryObj+" cannot be make as iterator, it will be ignored and hence will not appear in the merged iterator");
                }
                continue;
            }
            appendIteratorFilter.setSource(MakeIterator.convert(iteratorEntryObj));
        }

        appendIteratorFilter.execute();

        putInContext(appendIteratorFilter);

        appendIteratorFilter = null;

        return super.end(writer, body);
    }

    // UnnamedParametric implementation --------------------------------------
    public void addParameter(Object value) {
        _parameters.add(value);
    }

    @StrutsTagAttribute(description="The name of which if supplied will have the resultant appended iterator stored under in the stack's context")
    public void setVar(String var) {
        super.setVar(var);
    }
}


