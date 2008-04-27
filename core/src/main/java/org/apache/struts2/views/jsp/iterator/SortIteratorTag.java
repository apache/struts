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

import java.util.Comparator;

import javax.servlet.jsp.JspException;

import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;
import org.apache.struts2.util.MakeIterator;
import org.apache.struts2.util.SortIteratorFilter;
import org.apache.struts2.views.jsp.StrutsBodyTagSupport;


/**
 * <!-- START SNIPPET: javadoc -->
 *
 * <b>NOTE: JSP-TAG</b>
 *
 * <p>A Tag that sorts a List using a Comparator both passed in as the tag attribute.
 * If 'var' attribute is specified, the sorted list will be placed into the PageContext
 * attribute using the key specified by 'var'. The sorted list will ALWAYS be
 * pushed into the stack and poped at the end of this tag.</p>
 *
 * <!-- END SNIPPET: javadoc -->
 *
 *
 * <!-- START SNIPPET: params -->
 *
 * <ul>
 *      <li>id (String) - if specified, the sorted iterator will be place with this id under page context</li>
 *      <li>source (Object) - the source for the sort to take place (should be iteratable) else JspException will be thrown</li>
 *      <li>comparator* (Object) - the comparator used to do sorting (should be a type of Comparator or its decendent) else JspException will be thrown</li>
 * </ul>
 *
 * <!-- END SNIPPET: params -->
 *
 *
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 *
 * USAGE 1:
 * &lt;s:sort comparator="myComparator" source="myList"&gt;
 *      &lt;s:iterator&gt;
 *      &lt;!-- do something with each sorted elements --&gt;
 *      &lt;s:property value="..." /&gt;
 *      &lt;/s:iterator&gt;
 * &lt;/s:sort&gt;
 *
 * USAGE 2:
 * &lt;s:sort var="mySortedList" comparator="myComparator" source="myList" /&gt;
 *
 * &lt;%
 *    Iterator sortedIterator = (Iterator) pageContext.getAttribute("mySortedList");
 *    for (Iterator i = sortedIterator; i.hasNext(); ) {
 *      // do something with each of the sorted elements
 *    }
 * %&gt;
 *
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 *
 * @see org.apache.struts2.util.SortIteratorFilter
 *
 * @s.tag name="sort" tld-body-content="JSP"
 * description="Sort a List using a Comparator both passed in as the tag attribute."
 */
@StrutsTag(name="sort", tldTagClass="org.apache.struts2.views.jsp.iterator.SortIteratorTag", 
        description="Sort a List using a Comparator both passed in as the tag attribute.")
public class SortIteratorTag extends StrutsBodyTagSupport {

    private static final long serialVersionUID = -7835719609764092235L;

    String comparatorAttr;
    String sourceAttr;
    String var;

    SortIteratorFilter sortIteratorFilter = null;

    @StrutsTagAttribute(required=true,type="java.util.Comparator", description="The comparator to use")
    public void setComparator(String comparator) {
        comparatorAttr = comparator;
    }

    @StrutsTagAttribute(description="The iterable source to sort")
    public void setSource(String source) {
        sourceAttr = source;
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
        // Source
        Object srcToSort;
        if (sourceAttr == null) {
            srcToSort = findValue("top");
        } else {
            srcToSort = findValue(sourceAttr);
        }
        if (! MakeIterator.isIterable(srcToSort)) { // see if source is Iteratable
            throw new JspException("source ["+srcToSort+"] is not iteratable");
        }

        // Comparator
        Object comparatorObj = findValue(comparatorAttr);
        if (! (comparatorObj instanceof Comparator)) {
            throw new JspException("comparator ["+comparatorObj+"] does not implements Comparator interface");
        }
        Comparator c = (Comparator) findValue(comparatorAttr);

        // SortIteratorFilter
        sortIteratorFilter = new SortIteratorFilter();
        sortIteratorFilter.setComparator(c);
        sortIteratorFilter.setSource(srcToSort);
        sortIteratorFilter.execute();

        // push sorted iterator into stack, so nexted tag have access to it
        getStack().push(sortIteratorFilter);
        if (var != null && var.length() > 0) {
            pageContext.setAttribute(var, sortIteratorFilter);
        }

        return EVAL_BODY_INCLUDE;
    }

    public int doEndTag() throws JspException {
        int returnVal =  super.doEndTag();

        // pop sorted list from stack at the end of tag
        getStack().pop();
        sortIteratorFilter = null;

        return returnVal;
    }
}
