/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.jsp.iterator;

import com.opensymphony.webwork.util.IteratorGenerator;
import com.opensymphony.webwork.util.IteratorGenerator.Converter;
import com.opensymphony.webwork.views.jsp.WebWorkBodyTagSupport;

import javax.servlet.jsp.JspException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * <!-- START SNIPPET: javadoc -->
 * <b>NOTE: JSP-TAG</b>
 * 
 * <p>Generate an iterator based on the val attribute supplied.</P>
 * 
 * <b>NOTE:</b> The generated iterator will <b>ALWAYS</b> be pushed into the top of the stack, and poped
 * at the end of the tag.
 * <!-- END SNIPPET: javadoc -->
 *
 * <!-- START SNIPPET: params -->
 * <ul>
 * 		<li>val* (Object) - the source to be parsed into an iterator </li>
 * 		<li>count (Object) - the max number (Integer, Float, Double, Long, String) entries to be in the iterator</li>
 * 		<li>separator (String) - the separator to be used in separating the <i>val</i> into entries of the iterator</li>
 *  	<li>id (String) - the id to store the resultant iterator into page context, if such id is supplied</li>
 *  	<li>converter (Object) - the converter (must extends off IteratorGenerator.Converter interface) to convert the String entry parsed from <i>val</i> into an object</li>
 * </ul>
 * <!-- END SNIPPET: params -->
 *
 *
 * <!-- START SNIPPET: example -->
 * Example One:
 * <pre>
 * Generate a simple iterator
 * &lt;ww:generator val="%{'aaa,bbb,ccc,ddd,eee'}"&gt;
 *	&lt;ww:iterator&gt;
 *		&lt;ww:property /&gt;&lt;br/&gt;
 *	&lt;/ww:iterator&gt;
 * &lt;/ww:generator&gt;
 * </pre>
 * This generates an iterator and print it out using the iterator tag.
 *
 * Example Two:
 * <pre>
 * Generate an iterator with count attribute
 * &lt;ww:generator val="%{'aaa,bbb,ccc,ddd,eee'}" count="3"&gt;
 *	&lt;ww:iterator&gt;
 *		&lt;ww:property /&gt;&lt;br/&gt;
 *	&lt;/ww:iterator&gt;
 * &lt;/ww:generator&gt;
 * </pre>
 * This generates an iterator, but only 3 entries will be available in the iterator
 * generated, namely aaa, bbb and ccc respectively because count attribute is set to 3
 *
 * Example Three:
 * <pre>
 * Generate an iterator with id attribute
 * &lt;ww:generator val="%{'aaa,bbb,ccc,ddd,eee'}" count="4" separator="," id="myAtt" /&gt;
 * &lt;%
 * 	Iterator i = (Iterator) pageContext.getAttribute("myAtt");
 * 	while(i.hasNext()) {
 * 		String s = (String) i.next(); %>
 * 		&lt;%=s%&gt; &lt;br/&gt;
 * &lt;% 	}
 * %&gt;
 * </pre>
 * This generates an iterator and put it in the PageContext under the key as specified
 * by the id attribute.
 *
 *
 * Example Four:
 * <pre>
 * Generate an iterator with comparator attribute
 * &lt;ww:generator val="%{'aaa,bbb,ccc,ddd,eee'}" converter="%{myConverter}"&gt;
 *	&lt;ww:iterator&gt;
 * 		&lt;ww:property /&gt;&lt;br/&gt;
 * 	&lt;/ww:iterator&gt;
 * &lt;/ww:generator&gt;
 *
 *
 * public class GeneratorTagAction extends ActionSupport {
 *
 *   ....
 *
 *	 public Converter getMyConverter() {
 *		return new Converter() {
 *			public Object convert(String value) throws Exception {
 *				return "converter-"+value;
 *			}
 *		};
 *	 }
 *
 *   ...
 *
 * }
 * </pre>
 * This will generate an iterator with each entries decided by the converter supplied. With
 * this converter, it simply add "converter-" to each entries.
 * <!-- END SNIPPET: example -->
 *
 * @see com.opensymphony.webwork.util.IteratorGenerator
 * @author Rickard �berg (rickard@dreambean.com)
 * @author tm_jee ( tm_jee(at)yahoo.co.uk )
 * @version $Revision: 1.14 $
 *
 * @ww.tag name="generator" tld-body-content="JSP"
 * description="Generate an iterator for a iterable source."
 */
public class IteratorGeneratorTag extends WebWorkBodyTagSupport {

	private static final long serialVersionUID = 2968037295463973936L;

	public static final String DEFAULT_SEPARATOR = ",";

	private static final Log _log = LogFactory.getLog(IteratorGeneratorTag.class);

    String countAttr;
    String separatorAttr;
    String valueAttr;
    String converterAttr;

    IteratorGenerator iteratorGenerator = null;

    /**
     * @ww.tagattribute required="false" type="Integer"
     * description="the max number entries to be in the iterator"
     */
    public void setCount(String count) {
        countAttr = count;
    }

    /**
     * @ww.tagattribute required="true" type="String"
     * description="the separator to be used in separating the <i>val</i> into entries of the iterator"
     */
    public void setSeparator(String separator) {
        separatorAttr = separator;
    }

    /**
     * @ww.tagattribute required="true"
     * description="the source to be parsed into an iterator"
     */
    public void setVal(String val) {
        valueAttr = val;
    }

    /**
     * @ww.tagattribute required="false" type="com.opensymphony.webwork.util.IteratorGenerator.Converter"
     * description="the converter to convert the String entry parsed from <i>val</i> into an object"
     */
    public void setConverter(String aConverter) {
    	converterAttr = aConverter;
    }

    /**
     * @ww.tagattribute required="false" type="String"
     * description="the id to store the resultant iterator into page context, if such id is supplied"
     */
    public void setId(String string) {
        super.setId(string);
    }

    public int doStartTag() throws JspException {

    	// value
    	Object value = findValue(valueAttr);

    	// separator
    	String separator = DEFAULT_SEPARATOR;
    	if (separatorAttr != null && separatorAttr.length() > 0) {
    		separator = findString(separatorAttr);
    	}

    	// TODO: maybe this could be put into an Util class, or there is already one?
    	// count
    	int count = 0;
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

    	// converter
    	Converter converter = null;
    	if (converterAttr != null && converterAttr.length() > 0) {
    		converter = (Converter) findValue(converterAttr);
    	}


    	iteratorGenerator = new IteratorGenerator();
    	iteratorGenerator.setValues(value);
    	iteratorGenerator.setCount(count);
    	iteratorGenerator.setSeparator(separator);
    	iteratorGenerator.setConverter(converter);

    	iteratorGenerator.execute();



    	// push resulting iterator into stack
    	getStack().push(iteratorGenerator);
    	if (getId() != null && getId().length() > 0) {
    		// if an id is specified, we have the resulting iterator set into
    		// the pageContext attribute as well
    		pageContext.setAttribute(getId(), iteratorGenerator);
    	}

        return EVAL_BODY_INCLUDE;
    }

    public int doEndTag() throws JspException {
    	// pop resulting iterator from stack at end tag
    	getStack().pop();
    	iteratorGenerator = null; // clean up

    	return EVAL_PAGE;
    }
}
