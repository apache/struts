/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts.action2.components;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.struts.action2.components.Param.UnnamedParametric;
import org.apache.struts.action2.util.AppendIteratorFilter;
import org.apache.struts.action2.util.MakeIterator;
import com.opensymphony.xwork.util.OgnlValueStack;

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
 * 		<li>First Entry of the First Iterator</li>
 * 		<li>Second Entry of the First Iterator</li>
 * 		<li>Third Entry of the First Iterator</li>
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
 * 		<li>id (String) - the id of which if supplied will have the resultant
 *                        appended iterator stored under in the stack's context</li>
 * </ul>
 * <!-- END SNIPPET: params -->
 *
 *
 * <!-- START SNIPPET: code -->
 * public class AppendIteratorTagAction extends ActionSupport {
 *
 *	private List myList1;
 *	private List myList2;
 *	private List myList3;
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
 *
 *	public List getMyList1() { return myList1; }
 *	public List getMyList2() { return myList2; }
 *	public List getMyList3() { return myList3; }
 *}
 * <!-- END SNIPPET: code -->
 *
 * <!-- START SNIPPET: example -->
 * &lt;a:append id="myAppendIterator"&gt;
 *		&lt;a:param value="%{myList1}" /&gt;
 *		&lt;a:param value="%{myList2}" /&gt;
 *		&lt;a:param value="%{myList3}" /&gt;
 * &lt;/a:append&gt;
 * &lt;a:iterator value="%{#myAppendIterator}"&gt;
 *		&lt;a:property /&gt;
 * &lt;/a:iterator&gt;
 * <!-- END SNIPPET: example -->
 *
 *
 * @author tm_jee
 * @version $Date$ $Id$
 * @see org.apache.struts.action2.util.AppendIteratorFilter
 * @see org.apache.struts.action2.views.jsp.iterator.AppendIteratorTag
 *
 * @a2.tag name="append" tld-body-content="JSP" tld-tag-class="org.apache.struts.action2.views.jsp.iterator.AppendIteratorTag"
 * description="Append the values of a list of iterators to one iterator"
 */
public class AppendIterator extends Component implements UnnamedParametric {

	private static final Log _log = LogFactory.getLog(AppendIterator.class);
	
	private AppendIteratorFilter appendIteratorFilter= null;
	private List _parameters;
	
	public AppendIterator(OgnlValueStack stack) {
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
				_log.warn("param with value resolved as "+iteratorEntryObj+" cannot be make as iterator, it will be ignored and hence will not appear in the merged iterator");
				continue;
			}
			appendIteratorFilter.setSource(MakeIterator.convert(iteratorEntryObj));
		}
		
		appendIteratorFilter.execute();
		
		if (getId() != null && getId().length() > 0) {
			getStack().getContext().put(getId(), appendIteratorFilter);
		}
		
		appendIteratorFilter = null;

        return super.end(writer, body);
    }

	// UnnamedParametric implementation --------------------------------------
	public void addParameter(Object value) {
		_parameters.add(value);
	}

    /**
     * the id of which if supplied will have the resultant appended iterator stored under in the stack's context
     * @a2.tagattribute required="false"
     */
    public void setId(String id) {
        super.setId(id);
    }
}


