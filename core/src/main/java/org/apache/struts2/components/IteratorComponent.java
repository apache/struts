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
package org.apache.struts2.components;

import org.apache.struts2.util.MakeIterator;
import org.apache.struts2.views.jsp.IteratorStatus;
import com.opensymphony.xwork.util.OgnlValueStack;

import java.io.Writer;
import java.util.Iterator;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * <p>Iterator will iterate over a value. An iterable value can be either of: java.util.Collection, java.util.Iterator,
 * java.util.Enumeration, java.util.Map, array.</p> <p/> <!-- END SNIPPET: javadoc -->
 *
 * <!-- START SNIPPET: params -->
 *
 * <ul>
 *
 * <li>status (String) - if specified, an instanceof IteratorStatus will be pushed into stack upon each iteration</li>
 *
 * <li>value (Object) - the source to iterate over, must be iteratable, else an the object itself will be put into a
 * newly created List (see MakeIterator#convert(Object)</li>
 *
 * <li>id (String) - if specified the current iteration object will be place with this id in Struts stack's context
 * scope</li>
 *
 * </ul>
 *
 * <!-- END SNIPPET: params -->
 *
 * <!-- START SNIPPET: example1description -->
 *
 * <p>The following example retrieves the value of the getDays() method of the current object on the value stack and
 * uses it to iterate over. The &lt;a:property/&gt; tag prints out the current value of the iterator.</p>
 *
 * <!-- END SNIPPET: example1description -->
 *
 * <pre>
 * <!-- START SNIPPET: example1code -->
 * &lt;a:iterator value="days"&gt;
 *   &lt;p&gt;day is: &lt;a:property/&gt;&lt;/p&gt;
 * &lt;/a:iterator&gt;
 * <!-- END SNIPPET: example1code -->
 * </pre>
 *
 *
 * <!-- START SNIPPET: example2description -->
 *
 * <p>The following example uses a {@link Bean} tag and places it into the ActionContext. The iterator tag will retrieve
 * that object from the ActionContext and then calls its getDays() method as above. The status attribute is also used to
 * create a {@link IteratorStatus} object, which in this example, its odd() method is used to alternate row
 * colours:</p>
 *
 * <!-- END SNIPPET: example2description -->
 * 
 * 
 * <pre>
 * <!-- START SNIPPET: example2code -->
 * 
 * &lt;a:bean name="org.apache.struts2.example.IteratorExample" id="it"&gt;
 *   &lt;a:param name="day" value="'foo'"/&gt;
 *   &lt;a:param name="day" value="'bar'"/&gt;
 * &lt;/a:bean&gt;
 * <p/>
 * &lt;table border="0" cellspacing="0" cellpadding="1"&gt;
 * &lt;tr&gt;
 *   &lt;th&gt;Days of the week&lt;/th&gt;
 * &lt;/tr&gt;
 * <p/>
 * &lt;a:iterator value="#it.days" status="rowstatus"&gt;
 *   &lt;tr&gt;
 *     &lt;a:if test="#rowstatus.odd == true"&gt;
 *       &lt;td style="background: grey"&gt;&lt;a:property/&gt;&lt;/td&gt;
 *     &lt;/a:if&gt;
 *     &lt;a:else&gt;
 *       &lt;td&gt;&lt;a:property/&gt;&lt;/td&gt;
 *     &lt;/a:else&gt;
 *   &lt;/tr&gt;
 * &lt;/a:iterator&gt;
 * &lt;/table&gt;
 * 
 * <!-- END SNIPPET: example2code -->
 * </pre>
 *
 * <!--START SNIPPET: example3description -->
 *
 * <p> The next example will further demonstrate the use of the status attribute, using a DAO obtained from the action
 * class through OGNL, iterating over groups and their users (in a security context). The last() method indicates if the
 * current object is the last available in the iteration, and if not, we need to seperate the users using a comma: </p>
 *
 * <!-- END SNIPPET: example3description -->
 *
 * <pre>
 * <!-- START SNIPPET: example3code -->
 * 
 * 	&lt;saf:iterator value="groupDao.groups" status="groupStatus"&gt;
 * 		&lt;tr class="&lt;saf:if test="#groupStatus.odd == true "&gt;odd&lt;/saf:if&gt;&lt;saf:else&gt;even&lt;/saf:else&gt;"&gt;
 * 			&lt;td&gt;&lt;saf:property value="name" /&gt;&lt;/td&gt;
 * 			&lt;td&gt;&lt;saf:property value="description" /&gt;&lt;/td&gt;
 * 			&lt;td&gt;
 * 				&lt;saf:iterator value="users" status="userStatus"&gt;
 * 					&lt;saf:property value="fullName" /&gt;&lt;saf:if test="!#userStatus.last"&gt;,&lt;/saf:if&gt;
 * 				&lt;/saf:iterator&gt;
 * 			&lt;/td&gt;
 * 		&lt;/tr&gt;
 * 	&lt;/saf:iterator&gt;
 * 
 * <!-- END SNIPPET: example3code -->
 * </pre>
 * <p>
 *
 * <!-- START SNIPPET: example4description -->
 *
 * </p> The next example iterates over a an action collection and passes every iterator value to another action. The
 * trick here lies in the use of the '[0]' operator. It takes the current iterator value and passes it on to the edit
 * action. Using the '[0]' operator has the same effect as using &gt;a:property /&gt;. (The latter, however, does not
 * work from inside the param tag). </p>
 *
 * <!-- END SNIPPET: example4description -->
 *
 * <pre>
 * <!-- START SNIPPET: example4code -->
 * 
 * 		&lt;a:action name="entries" id="entries"/&gt;
 * 		&lt;a:iterator value="#entries.entries" &gt;
 * 			&lt;a:property value="name" /&gt;
 * 			&lt;a:property /&gt;
 * 			&lt;a:push value="..."&gt;
 * 				&lt;a:action name="edit" id="edit" &gt;
 * 					&lt;a:param name="entry" value="[0]" /&gt;
 * 				&lt;/a:action&gt;
 * 			&lt;/push&gt;
 * 		&lt;/a:iterator&gt;
 * 
 * <!-- END SNIPPET: example4code -->
 * </pre>
 * 
 * <!-- START SNIPPET: example5description -->
 * 
 * </p>To simulate a simple loop with iterator tag, the following could be done. 
 * It does the loop 5 times.
 * 
 * <!-- END SNIPPET: example5description -->
 * 
 * <pre>
 * <!-- START SNIPPET: example5code -->
 * 
 * &lt;a:iterator status="stat" value="{1,2,3,4,5}" &gt;
 *    &lt;!-- grab the index (start with 0 ... ) --&gt;
 *    &lt;a:property value="#stat.index" /&gt;
 *    
 *    &lt;!-- grab the top of the stack which should be the --&gt;
 *    &lt;!-- current iteration value (0, 1, ... 5) --&gt;
 *    &lt;a:property value="top" /&gt;
 * &lt;/a:iterator&gt;
 * 
 * <!-- END SNIPPET: example5code -->
 * </pre>
 * 
 * @a2.tag name="iterator" tld-body-content="JSP" tld-tag-class="org.apache.struts2.views.jsp.IteratorTag"
 * description="Iterate over a iterable value"
 */
public class IteratorComponent extends Component {
    protected Iterator iterator;
    protected IteratorStatus status;
    protected Object oldStatus;
    protected IteratorStatus.StatusState statusState;
    protected String statusAttr;
    protected String value;

    public IteratorComponent(OgnlValueStack stack) {
        super(stack);
    }

    public boolean start(Writer writer) {
        //Create an iterator status if the status attribute was set.
        if (statusAttr != null) {
            statusState = new IteratorStatus.StatusState();
            status = new IteratorStatus(statusState);
        }

        OgnlValueStack stack = getStack();

        if (value == null) {
            value = "top";
        }
        iterator = MakeIterator.convert(findValue(value));
        
        // get the first
        if ((iterator != null) && iterator.hasNext()) {
            Object currentValue = iterator.next();
            stack.push(currentValue);

            String id = getId();
            
            if ((id != null) && (currentValue != null)) {
                //pageContext.setAttribute(id, currentValue);
                //pageContext.setAttribute(id, currentValue, PageContext.REQUEST_SCOPE);
            	stack.getContext().put(id, currentValue);
            }

            // Status object
            if (statusAttr != null) {
                statusState.setLast(!iterator.hasNext());
                oldStatus = stack.getContext().get(statusAttr);
                stack.getContext().put(statusAttr, status);
            }

            return true;
        } else {
        	super.end(writer, "");
            return false;
        }
    }

    public boolean end(Writer writer, String body) {
        OgnlValueStack stack = getStack();
        if (iterator != null) {
            stack.pop();
        }

        if (iterator!=null && iterator.hasNext()) {
            Object currentValue = iterator.next();
            stack.push(currentValue);

            String id = getId();

            if ((id != null) && (currentValue != null)) {
                //pageContext.setAttribute(id, currentValue);
                //pageContext.setAttribute(id, currentValue, PageContext.REQUEST_SCOPE);
            	stack.getContext().put(id, currentValue);
            }

            // Update status
            if (status != null) {
                statusState.next(); // Increase counter
                statusState.setLast(!iterator.hasNext());
            }

            return true;
        } else {
            // Reset status object in case someone else uses the same name in another iterator tag instance
            if (status != null) {
                if (oldStatus == null) {
                    stack.getContext().put(statusAttr, null);
                } else {
                    stack.getContext().put(statusAttr, oldStatus);
                }
            }
            super.end(writer, "");
            return false;
        }
    }

    /**
     * if specified, an instanceof IteratorStatus will be pushed into stack upon each iteration
     * @a2.tagattribute required="false" type="Boolean" default="false"
     */
    public void setStatus(String status) {
        this.statusAttr = status;
    }

    /**
     * the iteratable source to iterate over, else an the object itself will be put into a newly created List
     * @a2.tagattribute required="false"
     */
    public void setValue(String value) {
        this.value = value;
    }

}
