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

import org.apache.struts2.util.IteratorGenerator;
import org.apache.struts2.util.IteratorGenerator.Converter;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;
import org.apache.struts2.views.jsp.StrutsBodyTagSupport;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;


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
 *      <li>val* (Object) - the source to be parsed into an iterator </li>
 *      <li>count (Object) - the max number (Integer, Float, Double, Long, String) entries to be in the iterator</li>
 *      <li>separator (String) - the separator to be used in separating the <i>val</i> into entries of the iterator</li>
 *      <li>var (String) - the name to store the resultant iterator into page context, if such name is supplied</li>
 *      <li>converter (Object) - the converter (must extends off IteratorGenerator.Converter interface) to convert the String entry parsed from <i>val</i> into an object</li>
 * </ul>
 * <!-- END SNIPPET: params -->
 *
 *
 * <!-- START SNIPPET: example -->
 * Example One:
 * <pre>
 * Generate a simple iterator
 * &lt;s:generator val="%{'aaa,bbb,ccc,ddd,eee'}"&gt;
 *  &lt;s:iterator&gt;
 *      &lt;s:property /&gt;&lt;br/&gt;
 *  &lt;/s:iterator&gt;
 * &lt;/s:generator&gt;
 * </pre>
 * This generates an iterator and print it out using the iterator tag.
 *
 * Example Two:
 * <pre>
 * Generate an iterator with count attribute
 * &lt;s:generator val="%{'aaa,bbb,ccc,ddd,eee'}" count="3"&gt;
 *  &lt;s:iterator&gt;
 *      &lt;s:property /&gt;&lt;br/&gt;
 *  &lt;/s:iterator&gt;
 * &lt;/s:generator&gt;
 * </pre>
 * This generates an iterator, but only 3 entries will be available in the iterator
 * generated, namely aaa, bbb and ccc respectively because count attribute is set to 3
 *
 * Example Three:
 * <pre>
 * Generate an iterator with var attribute
 * &lt;s:generator val="%{'aaa,bbb,ccc,ddd,eee'}" count="4" separator="," var="myAtt" /&gt;
 * &lt;%
 *  Iterator i = (Iterator) pageContext.getAttribute("myAtt");
 *  while(i.hasNext()) {
 *      String s = (String) i.next(); %>
 *      &lt;%=s%&gt; &lt;br/&gt;
 * &lt;%    }
 * %&gt;
 * </pre>
 * This generates an iterator and put it in the PageContext under the key as specified
 * by the var attribute.
 *
 *
 * Example Four:
 * <pre>
 * Generate an iterator with comparator attribute
 * &lt;s:generator val="%{'aaa,bbb,ccc,ddd,eee'}" converter="%{myConverter}"&gt;
 *  &lt;s:iterator&gt;
 *      &lt;s:property /&gt;&lt;br/&gt;
 *  &lt;/s:iterator&gt;
 * &lt;/s:generator&gt;
 *
 *
 * public class GeneratorTagAction extends ActionSupport {
 *
 *   ....
 *
 *   public Converter getMyConverter() {
 *      return new Converter() {
 *          public Object convert(String value) throws Exception {
 *              return "converter-"+value;
 *          }
 *      };
 *   }
 *
 *   ...
 *
 * }
 * </pre>
 * This will generate an iterator with each entries decided by the converter supplied. With
 * this converter, it simply add "converter-" to each entries.
 * <!-- END SNIPPET: example -->
 *
 * @see org.apache.struts2.util.IteratorGenerator
 */
@StrutsTag(name="generator", tldTagClass="org.apache.struts2.views.jsp.iterator.IteratorGeneratorTag",
        description="Generate an iterator for a iterable source.")
public class IteratorGeneratorTag extends StrutsBodyTagSupport {

    private static final long serialVersionUID = 2968037295463973936L;

    public static final String DEFAULT_SEPARATOR = ",";

    private static final Logger LOG = LoggerFactory.getLogger(IteratorGeneratorTag.class);

    String countAttr;
    String separatorAttr;
    String valueAttr;
    String converterAttr;
    String var;
    IteratorGenerator iteratorGenerator = null;

    @StrutsTagAttribute(type="Integer",description="The max number entries to be in the iterator")
    public void setCount(String count) {
        countAttr = count;
    }

    /**
     * @s.tagattribute required="true" type="String"
     * description="the separator to be used in separating the <i>val</i> into entries of the iterator"
     */
    @StrutsTagAttribute(required=true, description="The separator to be used in separating the <i>val</i> into entries of the iterator")
    public void setSeparator(String separator) {
        separatorAttr = separator;
    }

    /**
     * @s.tagattribute required="true"
     * description="the source to be parsed into an iterator"
     */
    @StrutsTagAttribute(required=true, description="The source to be parsed into an iterator")
    public void setVal(String val) {
        valueAttr = val;
    }

    @StrutsTagAttribute(type="org.apache.struts2.util.IteratorGenerator.Converter",
            description="The converter to convert the String entry parsed from <i>val</i> into an object")
    public void setConverter(String aConverter) {
        converterAttr = aConverter;
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

        // Push resulting iterator on stack and put into
        // stack context if we have a "var" specified.
        getStack().push(iteratorGenerator);
        if (var != null && var.length() > 0) {
            getStack().getContext().put(var, iteratorGenerator);
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
