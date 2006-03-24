/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.jsp.iterator;

import com.opensymphony.webwork.util.SubsetIteratorFilter;
import com.opensymphony.webwork.util.SubsetIteratorFilter.Decider;
import com.opensymphony.webwork.views.jsp.WebWorkBodyTagSupport;

import javax.servlet.jsp.JspException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * <!-- START SNIPPET: javadoc -->
 * <b>NOTE: JSP-TAG</b>
 * 
 * <p>A tag that takes an iterator and outputs a subset of it. It delegates to
 * {@link com.opensymphony.webwork.util.SubsetIteratorFilter} internally to
 * perform the subset functionality.</p>
 * <!-- END SNIPPET: javadoc -->
 *
 * <!-- START SNIPPET: params -->
 * <ul>
 * 		<li>count (Object) - Indicate the number of entries to be in the resulting subset iterator</li>
 * 		<li>source* (Object) - Indicate the source of which the resulting subset iterator is to be derived base on</li>
 * 		<li>start (Object) - Indicate the starting index (eg. first entry is 0) of entries in the source to be available as the first entry in the resulting subset iterator</li>
 * 		<li>decider (Object) - Extension to plug-in a decider to determine if that particular entry is to be included in the resulting subset iterator</li>
 * 		<li>id (String) - Indicate the pageContext attribute id to store the resultant subset iterator in</li>
 * </ul>
 * <!-- END SNIPPET: params -->
 *
 *
 * <pre>
 * <!-- START SNIPPET: action -->
 * public class MySubsetTagAction extends ActionSupport {
 *      public String execute() throws Exception {
 *		   l = new ArrayList();
 *		   l.add(new Integer(1));
 *		   l.add(new Integer(2));
 *		   l.add(new Integer(3));
 *		   l.add(new Integer(4));
 *		   l.add(new Integer(5));
 *		   return "done";
 *	    }
 *
 *
 *	    public Integer[] getMyArray() {
 *		   return a;
 *	    }
 *
 *	    public List getMyList() {
 *		   return l;
 *	     }
 *
 *      public Decider getMyDecider() {
 *		return new Decider() {
 *			public boolean decide(Object element) throws Exception {
 *				int i = ((Integer)element).intValue();
 *				return (((i % 2) == 0)?true:false);
 *			}
 *		};
 *		}
 *	}
 * <!-- END SNIPPET: action -->
 * </pre>
 *
 *
 * <pre>
 * <!-- START SNIPPET: example1 -->
 * &lt;!-- A: List basic --&gt;
 *    &lt;ww:subset source="myList"&gt;
 *	     &lt;ww:iterator&gt;
 *		    &lt;ww:property /&gt;
 *	     &lt;/ww:iterator&gt;
 *    &lt;/ww:subset&gt;
 * <!-- END SNIPPET: example1 -->
 * </pre>
 *
 * <pre>
 * <!-- START SNIPPET: example2 -->
 * &lt;!-- B: List with count --&gt;
 *    &lt;ww:subset source="myList" count="3"&gt;
 * 	     &lt;ww:iterator&gt;
 * 		     &lt;ww:property /&gt;
 * 	     &lt;/ww:iterator&gt;
 *     &lt;/ww:subset&gt;
 * <!-- END SNIPPET: example2 -->
 * </pre>
 *
 * <pre>
 * <!-- START SNIPPET: example3 -->
 * &lt;!--  C: List with start -->
 *      &lt;ww:subset source="myList" count="13" start="3"&gt;
 * 	       &lt;ww:iterator&gt;
 * 		     &lt;ww:property /&gt;
 * 	       &lt;/ww:iterator&gt;
 *      &lt;/ww:subset&gt;
 * <!-- END SNIPPET: example3 -->
 * </pre>
 *
 * <pre>
 * <!-- START SNIPPET: example4 -->
 * &lt;!--  D: List with id --&gt;
 *      &lt;ww:subset id="mySubset" source="myList" count="13" start="3" /&gt;
 *      &lt;%
 * 	        Iterator i = (Iterator) pageContext.getAttribute("mySubset");
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
 *      &lt;ww:subset source="myList" decider="myDecider"&gt;
 * 	           &lt;ww:iterator&gt;
 *		            &lt;ww:property /&gt;
 *	           &lt;/ww:iterator&gt;
 *      &lt;/ww:subset&gt;
 * <!-- END SNIPPET: example5 -->
 * </pre>
 *
 *
 * @author Rickard �berg (rickard@dreambean.com)
 * @author tm_jee ( tm_jee(at)yahoo.co.uk )
 * @ww.tag name="subset" tld-body-content="JSP"
 * description="Takes an iterator and outputs a subset of it"
 */
public class SubsetIteratorTag extends WebWorkBodyTagSupport {

	private static final long serialVersionUID = -6252696081713080102L;

	private static final Log _log = LogFactory.getLog(SubsetIteratorTag.class);

    String countAttr;
    String sourceAttr;
    String startAttr;
    String deciderAttr;

    SubsetIteratorFilter subsetIteratorFilter = null;


    /**
     * @ww.tagattribute required="false" type="Integer"
     * description="Indicate the number of entries to be in the resulting subset iterator"
     */
    public void setCount(String count) {
        countAttr = count;
    }

    /**
     * @ww.tagattribute required="false"
     * description="Indicate the source of which the resulting subset iterator is to be derived base on"
     */
    public void setSource(String source) {
        sourceAttr = source;
    }

    /**
     * @ww.tagattribute required="false" type="Integer"
     * description="Indicate the starting index (eg. first entry is 0) of entries in the source to be available as the first entry in the resulting subset iterator"
     */
    public void setStart(String start) {
        startAttr = start;
    }

    /**
     * @ww.tagattribute required="false" type="com.opensymphony.webwork.util.SubsetIteratorFilter.Decider"
     * description="Extension to plug-in a decider to determine if that particular entry is to be included in the resulting subset iterator"
     */
    public void setDecider(String decider) {
    	deciderAttr = decider;
    }


    public int doStartTag() throws JspException {

        // source
        Object source = null;
        if (sourceAttr == null && sourceAttr.length() <= 0) {
        	source = findValue("top");
        } else {
            source = findValue(sourceAttr);
        }

        // count
        int count = -1;
    	if (countAttr != null && countAttr.length() > 0) {
    		Object countObj = findValue(countAttr);
    		if (countObj instanceof Integer) {
    			count = ((Integer)countObj).intValue();
    		}
    		else if (countObj instanceof Float) {
    			count = ((Float)countObj).intValue();
    		}
    		else if (countObj instanceof Long) {
    			count = ((Long)countObj).intValue();
    		}
    		else if (countObj instanceof Double) {
    			count = ((Long)countObj).intValue();
    		}
    		else if (countObj instanceof String) {
    			try {
    				count = Integer.parseInt((String)countObj);
    			}
    			catch(NumberFormatException e) {
    				_log.warn("unable to convert count attribute ["+countObj+"] to number, ignore count attribute", e);
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
    			start = ((Long)startObj).intValue();
    		}
    		else if (startObj instanceof String) {
    			try {
    				start = Integer.parseInt((String)startObj);
    			}
    			catch(NumberFormatException e) {
    				_log.warn("unable to convert count attribute ["+startObj+"] to number, ignore count attribute", e);
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
        if (getId() != null) {
        	pageContext.setAttribute(getId(), subsetIteratorFilter);
        }

        return EVAL_BODY_INCLUDE;
    }

    public int doEndTag() throws JspException {

    	getStack().pop();

    	subsetIteratorFilter = null;

    	return EVAL_PAGE;
    }
}
