/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.components;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.webwork.components.Param.UnnamedParametric;
import com.opensymphony.webwork.util.MakeIterator;
import com.opensymphony.webwork.util.MergeIteratorFilter;
import com.opensymphony.xwork.util.OgnlValueStack;

/**
 * <!-- START SNIPPET: javadoc -->
 * <p>Component for MergeIteratorTag, which job is to merge iterators and successive 
 * call to the merged iterator will cause each merge iterator to have a chance to 
 * expose its element, subsequently next call will allow the next iterator to expose 
 * its element. Once the last iterator is done exposing its element, the first iterator
 * is allowed to do so again (unless it is exhausted of entries).</P>
 * 
 * <p>Internally the task are delegated to MergeIteratorFilter</p>
 * 
 * <p>Example if there are 3 lists being merged, each list have 3 entries, the following will
 * be the logic.</P>
 * <ol>
 * 		<li>Display first element of the first list</li>
 * 		<li>Display first element of the second list</li>
 * 		<li>Display first element of the third list</li>
 *      <li>Display second element of the first list</li>
 *      <li>Display second element of the second list</li>
 *      <li>Display second element of the third list</li>
 *      <li>Display third element of the first list</li>
 *      <li>Display thrid element of the second list</li>
 *      <li>Display third element of the thrid list</li>
 * </ol>
 * <!-- END SNIPPET: javadoc -->
 * 
 * <!-- START SNIPPET: params -->
 * <ul>
 * 			<li>id (String) - the id where the resultant merged iterator will be stored in the stack's context</li>
 * </ul>
 * <!-- END SNIPPET: params -->
 * 
 * 
 * <!-- START SNIPPET: javacode -->
 * public class MergeIteratorTagAction extends ActionSupport {
 *
 *	private List myList1;
 *	private List myList2;
 *	private List myList3;
 *	
 *	public List getMyList1() {
 *		return myList1;
 *	}
 *	
 *	public List getMyList2() {
 *		return myList2;
 *	}
 *	
 *	public List getMyList3() {
 *		return myList3;
 *	}
 *	
 *	
 *	public String execute() throws Exception {
 *		
 *		myList1 = new ArrayList();
 *		myList1.add("1");
 *		myList1.add("2");
 *		myList1.add("3");
 *		
 *		myList2 = new ArrayList();
 *		myList2.add("a");
 *		myList2.add("b");
 *		myList2.add("c");
 *		
 *		myList3 = new ArrayList();
 *		myList3.add("A");
 *		myList3.add("B");
 *		myList3.add("C");
 *		
 *		return "done";
 *	}
 * }
 * <!-- END SNIPPET: javacode -->
 *
 * <!-- START SNIPPET: example -->
 * &lt;ww:merge id="myMergedIterator1"&gt;
 *		&lt;ww:param value="%{myList1}" /&gt;
 *		&lt;ww:param value="%{myList2}" /&gt;
 *		&lt;ww:param value="%{myList3}" /&gt;
 * &lt;/ww:merge&gt;
 * &lt;ww:iterator value="%{#myMergedIterator1}"&gt;
 *		&lt;ww:property /&gt;
 * &lt;/ww:iterator&gt;
 * <!-- END SNIPPET: example -->
 *
 * <!-- START SNIPPET: description -->
 * This wil generate "1aA2bB3cC".
 * <!-- START SNIPPET: description -->
 *
 * @author tm_jee (tm_jee (at) yahoo.co.uk)
 * @version $Date: 2006/03/18 16:14:40 $ $Id: MergeIterator.java,v 1.10 2006/03/18 16:14:40 rgielen Exp $
 * @see com.opensymphony.webwork.util.MergeIteratorFilter
 * @see com.opensymphony.webwork.views.jsp.iterator.MergeIteratorTag
 *
 * @ww.tag name="merge" tld-body-content="JSP" tld-tag-class="com.opensymphony.webwork.views.jsp.iterator.MergeIteratorTag"
 * description="Merge the values of a list of iterators into one iterator"
 */
public class MergeIterator extends Component implements UnnamedParametric {
	
	private static final Log _log = LogFactory.getLog(MergeIterator.class);
	
	private MergeIteratorFilter mergeIteratorFilter = null;
	private List _parameters;

	public MergeIterator(OgnlValueStack stack) {
		super(stack);
	}
	
	public boolean start(Writer writer) {
		
		mergeIteratorFilter = new MergeIteratorFilter();
		_parameters = new ArrayList();

        return super.start(writer);
    }
	
	public boolean end(Writer writer, String body) {
		
		for (Iterator parametersIterator = _parameters.iterator(); parametersIterator.hasNext(); ) {
			Object iteratorEntryObj = parametersIterator.next();
			if (! MakeIterator.isIterable(iteratorEntryObj)) {
				_log.warn("param with value resolved as "+iteratorEntryObj+" cannot be make as iterator, it will be ignored and hence will not appear in the merged iterator");
				continue;
			}
			mergeIteratorFilter.setSource(MakeIterator.convert(iteratorEntryObj));
		}
		
		mergeIteratorFilter.execute();

		// if id exists, we put it in the stack's context
		if (getId() != null && getId().length() > 0) {
			getStack().getContext().put(getId(), mergeIteratorFilter);
		}
		
		mergeIteratorFilter = null;

        return super.end(writer, body);
    }

    /**
     * the id where the resultant merged iterator will be stored in the stack's context
     * @ww.tagattribute required="false"
     */
    public void setId(String id) {
        super.setId(id);
    }

    // == UnnamedParametric interface implementation ---------------------
	public void addParameter(Object value) {
		_parameters.add(value);
	}
}
