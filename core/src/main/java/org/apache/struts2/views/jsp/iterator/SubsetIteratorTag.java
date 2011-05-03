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

package org.apache.struts2.views.jsp.iterator;

import javax.servlet.jsp.JspException;

import org.apache.struts2.util.SubsetIteratorFilter;
import org.apache.struts2.util.SubsetIteratorFilter.Decider;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;
import org.apache.struts2.views.jsp.StrutsBodyTagSupport;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;


/**
 * <!-- START SNIPPET: javadoc -->
 * <b>NOTE: JSP-TAG</b>
 *
 * <p>A tag that takes an iterator and outputs a subset of it. It delegates to
 * {@link org.apache.struts2.util.SubsetIteratorFilter} internally to
 * perform the subset functionality.</p>
 * <!-- END SNIPPET: javadoc -->
 *
 * <!-- START SNIPPET: params -->
 * <ul>
 *      <li>count (Object) - Indicate the number of entries to be in the resulting subset iterator</li>
 *      <li>source* (Object) - Indicate the source of which the resulting subset iterator is to be derived base on</li>
 *      <li>start (Object) - Indicate the starting index (eg. first entry is 0) of entries in the source to be available as the first entry in the resulting subset iterator</li>
 *      <li>decider (Object) - Extension to plug-in a decider to determine if that particular entry is to be included in the resulting subset iterator</li>
 *      <li>var (String) - Indicate the pageContext attribute name to store the resultant subset iterator in</li>
 * </ul>
 * <!-- END SNIPPET: params -->
 *
 *
 * <pre>
 * <!-- START SNIPPET: action -->
 * public class MySubsetTagAction extends ActionSupport {
 *      public String execute() throws Exception {
 *         l = new ArrayList();
 *         l.add(new Integer(1));
 *         l.add(new Integer(2));
 *         l.add(new Integer(3));
 *         l.add(new Integer(4));
 *         l.add(new Integer(5));
 *         return "done";
 *      }
 *
 *
 *      public Integer[] getMyArray() {
 *         return a;
 *      }
 *
 *      public List getMyList() {
 *         return l;
 *       }
 *
 *      public Decider getMyDecider() {
 *      return new Decider() {
 *          public boolean decide(Object element) throws Exception {
 *              int i = ((Integer)element).intValue();
 *              return (((i % 2) == 0)?true:false);
 *          }
 *      };
 *      }
 *  }
 * <!-- END SNIPPET: action -->
 * </pre>
 *
 *
 * <pre>
 * <!-- START SNIPPET: example1 -->
 * &lt;!-- s: List basic --&gt;
 *    &lt;s:subset source="myList"&gt;
 *       &lt;s:iterator&gt;
 *          &lt;s:property /&gt;
 *       &lt;/s:iterator&gt;
 *    &lt;/s:subset&gt;
 * <!-- END SNIPPET: example1 -->
 * </pre>
 *
 * <pre>
 * <!-- START SNIPPET: example2 -->
 * &lt;!-- B: List with count --&gt;
 *    &lt;s:subset source="myList" count="3"&gt;
 *       &lt;s:iterator&gt;
 *           &lt;s:property /&gt;
 *       &lt;/s:iterator&gt;
 *     &lt;/s:subset&gt;
 * <!-- END SNIPPET: example2 -->
 * </pre>
 *
 * <pre>
 * <!-- START SNIPPET: example3 -->
 * &lt;!--  C: List with start -->
 *      &lt;s:subset source="myList" count="13" start="3"&gt;
 *         &lt;s:iterator&gt;
 *           &lt;s:property /&gt;
 *         &lt;/s:iterator&gt;
 *      &lt;/s:subset&gt;
 * <!-- END SNIPPET: example3 -->
 * </pre>
 *
 * <pre>
 * <!-- START SNIPPET: example4 -->
 * &lt;!--  D: List with var --&gt;
 *      &lt;s:subset var="mySubset" source="myList" count="13" start="3" /&gt;
 *      &lt;%
 *          Iterator i = (Iterator) pageContext.getAttribute("mySubset");
 *          while(i.hasNext()) {
 *      %&gt;
 *      &lt;%=i.next() %&gt;
 *      &lt;%  } %&gt;
 * <!-- END SNIPPET: example4 -->
 * </pre>
 *
 * <pre>
 * <!-- START SNIPPET: example5 -->
 *  &lt;!--  D: List with Decider --&gt;
 *      &lt;s:subset source="myList" decider="myDecider"&gt;
 *             &lt;s:iterator&gt;
 *                  &lt;s:property /&gt;
 *             &lt;/s:iterator&gt;
 *      &lt;/s:subset&gt;
 * <!-- END SNIPPET: example5 -->
 * </pre>
 *
 *
 * @s.tag name="subset" tld-body-content="JSP"
 * description="Takes an iterator and outputs a subset of it"
 */
@StrutsTag(name="subset", tldTagClass="org.apache.struts2.views.jsp.iterator.SubsetIteratorTag",
        description="Takes an iterator and outputs a subset of it.")
public class SubsetIteratorTag extends StrutsBodyTagSupport {

    private static final long serialVersionUID = -6252696081713080102L;

    private static final Logger LOG = LoggerFactory.getLogger(SubsetIteratorTag.class);

    String countAttr;
    String sourceAttr;
    String startAttr;
    String deciderAttr;
    String var;
    SubsetIteratorFilter subsetIteratorFilter = null;


    @StrutsTagAttribute(type="Integer", description="Indicate the number of entries to be in the resulting subset iterator")
    public void setCount(String count) {
        countAttr = count;
    }

    @StrutsTagAttribute(description="Indicate the source of which the resulting subset iterator is to be derived base on")
    public void setSource(String source) {
        sourceAttr = source;
    }

    /**
     * @s.tagattribute required="false" type="Integer"
     * description="Indicate the starting index (eg. first entry is 0) of entries in the source to be available as the first entry in the resulting subset iterator"
     */
    @StrutsTagAttribute(type="Integer",
            description="Indicate the starting index (eg. first entry is 0) of entries in the source to be available as the first entry in the resulting subset iterator")
    public void setStart(String start) {
        startAttr = start;
    }

    @StrutsTagAttribute(type="org.apache.struts2.util.SubsetIteratorFilter.Decider",
            description="Extension to plug-in a decider to determine if that particular entry is to be included in the resulting subset iterator")
    public void setDecider(String decider) {
        deciderAttr = decider;
    }

    @StrutsTagAttribute(description="Deprecated. Use 'var' instead")
    public void setId(String string) {
        setVar(string);
    }

    @StrutsTagAttribute(description="The name to store the resultant iterator into page context, if such name is supplied")
    public void setVar(String var) {
        this.var = var;
    }

    public int doStartTag() throws JspException {

        // source
        Object source = null;
        if (sourceAttr == null || sourceAttr.length() == 0) {
            source = findValue("top");
        } else {
            source = findValue(sourceAttr);
        }

        // count
        int count = -1;
        if (countAttr != null && countAttr.length() > 0) {
            Object countObj = findValue(countAttr);
            if (countObj instanceof Number) {
                count = ((Number)countObj).intValue();
            }
            else if (countObj instanceof String) {
                try {
                    count = Integer.parseInt((String)countObj);
                }
                catch(NumberFormatException e) {
                    if (LOG.isWarnEnabled()) {
                	LOG.warn("unable to convert count attribute ["+countObj+"] to number, ignore count attribute", e);
                    }
                }
            }
        }

        // start
        int start = 0;
        if (startAttr != null && startAttr.length() > 0) {
            Object startObj = findValue(startAttr);
            if (startObj instanceof Integer) {
                start = ((Integer)startObj).intValue();
            }
            else if (startObj instanceof Float) {
                start = ((Float)startObj).intValue();
            }
            else if (startObj instanceof Long) {
                start = ((Long)startObj).intValue();
            }
            else if (startObj instanceof Double) {
                start = ((Double)startObj).intValue();
            }
            else if (startObj instanceof String) {
                try {
                    start = Integer.parseInt((String)startObj);
                }
                catch(NumberFormatException e) {
                    if (LOG.isWarnEnabled()) {
                	LOG.warn("unable to convert count attribute ["+startObj+"] to number, ignore count attribute", e);
                    }
                }
            }
        }

        // decider
        Decider decider = null;
        if (deciderAttr != null && deciderAttr.length() > 0) {
            Object deciderObj = findValue(deciderAttr);
            if (! (deciderObj instanceof Decider)) {
                throw new JspException("decider found from stack ["+deciderObj+"] does not implement "+Decider.class);
            }
            decider = (Decider) deciderObj;
        }


        subsetIteratorFilter = new SubsetIteratorFilter();
        subsetIteratorFilter.setCount(count);
        subsetIteratorFilter.setDecider(decider);
        subsetIteratorFilter.setSource(source);
        subsetIteratorFilter.setStart(start);
        subsetIteratorFilter.execute();

        getStack().push(subsetIteratorFilter);
        if (var != null && var.length() > 0) {
            pageContext.setAttribute(var, subsetIteratorFilter);
        }

        return EVAL_BODY_INCLUDE;
    }

    public int doEndTag() throws JspException {

        getStack().pop();

        subsetIteratorFilter = null;

        return EVAL_PAGE;
    }
}
