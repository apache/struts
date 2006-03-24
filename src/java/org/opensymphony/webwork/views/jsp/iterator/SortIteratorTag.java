/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.jsp.iterator;

import com.opensymphony.webwork.util.MakeIterator;
import com.opensymphony.webwork.util.SortIteratorFilter;
import com.opensymphony.webwork.views.jsp.WebWorkBodyTagSupport;

import javax.servlet.jsp.JspException;

import java.util.Comparator;


/**
 * <!-- START SNIPPET: javadoc -->
 *
 * <b>NOTE: JSP-TAG</b>
 * 
 * <p>A Tag that sorts a List using a Comparator both passed in as the tag attribute.
 * If 'id' attribute is specified, the sorted list will be placed into the PageContext
 * attribute using the key specified by 'id'. The sorted list will ALWAYS be
 * pushed into the stack and poped at the end of this tag.</p>
 *
 * <!-- END SNIPPET: javadoc -->
 *
 *
 * <!-- START SNIPPET: params -->
 *
 * <ul>
 * 		<li>id (String) - if specified, the sorted iterator will be place with this id under page context</li>
 * 		<li>source (Object) - the source for the sort to take place (should be iteratable) else JspException will be thrown</li>
 * 		<li>comparator* (Object) - the comparator used to do sorting (should be a type of Comparator or its decendent) else JspException will be thrown</li>
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
 * &lt;ww:sort comparator="myComparator" source="myList"&gt;
 *      &lt;ww:iterator&gt;
 * 		&lt;!-- do something with each sorted elements --&gt;
 * 		&lt;ww:property value="..." /&gt;
 *      &lt;/ww:iterator&gt;
 * &lt;/ww:sort&gt;
 *
 * USAGE 2:
 * &lt;ww:sort id="mySortedList" comparator="myComparator" source="myList" /&gt;
 *
 * &lt;%
 *    Iterator sortedIterator = (Iterator) pageContext.getAttribute("mySortedList");
 *    for (Iterator i = sortedIterator; i.hasNext(); ) {
 *    	// do something with each of the sorted elements
 *    }
 * %&gt;
 *
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 *
 * @see com.opensymphony.webwork.util.SortIteratorFilter
 * @author Rickard Oberg (rickard@dreambean.com)
 * @author tm_jee (tm_jee(at)yahoo.co.uk)
 *
 * @ww.tag name="sort" tld-body-content="JSP"
 * description="Sort a List using a Comparator both passed in as the tag attribute."
 */
public class SortIteratorTag extends WebWorkBodyTagSupport {

	private static final long serialVersionUID = -7835719609764092235L;

	String comparatorAttr;
    String sourceAttr;

    SortIteratorFilter sortIteratorFilter = null;

    /**
     * @ww.tagattribute required="true" type="java.util.Comparator"
     * description="The comparator to use"
     */
    public void setComparator(String comparator) {
        comparatorAttr = comparator;
    }

    /**
     * @ww.tagattribute required="false"
     * description="The iterable source to sort"
     */
    public void setSource(String source) {
        sourceAttr = source;
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
        if (getId() != null && getId().length() > 0) {
        	pageContext.setAttribute(getId(), sortIteratorFilter);
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
