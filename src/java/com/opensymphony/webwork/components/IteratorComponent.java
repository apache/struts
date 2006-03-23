package com.opensymphony.webwork.components;

import com.opensymphony.webwork.util.MakeIterator;
import com.opensymphony.webwork.views.jsp.IteratorStatus;
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
 * <li>id (String) - if specified the current iteration object will be place with this id in webwork stack's context
 * scope</li>
 *
 * </ul>
 *
 * <!-- END SNIPPET: params -->
 *
 * <!-- START SNIPPET: example1description -->
 *
 * <p>The following example retrieves the value of the getDays() method of the current object on the value stack and
 * uses it to iterate over. The &lt;ww:property/&gt; tag prints out the current value of the iterator.</p>
 *
 * <!-- END SNIPPET: example1description -->
 *
 * <pre>
 * <!-- START SNIPPET: example1code -->
 * &lt;ww:iterator value="days"&gt;
 *   &lt;p&gt;day is: &lt;ww:property/&gt;&lt;/p&gt;
 * &lt;/ww:iterator&gt;
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
 * &lt;ww:bean name="com.opensymphony.webwork.example.IteratorExample" id="it"&gt;
 *   &lt;ww:param name="day" value="'foo'"/&gt;
 *   &lt;ww:param name="day" value="'bar'"/&gt;
 * &lt;/ww:bean&gt;
 * <p/>
 * &lt;table border="0" cellspacing="0" cellpadding="1"&gt;
 * &lt;tr&gt;
 *   &lt;th&gt;Days of the week&lt;/th&gt;
 * &lt;/tr&gt;
 * <p/>
 * &lt;ww:iterator value="#it.days" status="rowstatus"&gt;
 *   &lt;tr&gt;
 *     &lt;ww:if test="#rowstatus.odd == true"&gt;
 *       &lt;td style="background: grey"&gt;&lt;ww:property/&gt;&lt;/td&gt;
 *     &lt;/ww:if&gt;
 *     &lt;ww:else&gt;
 *       &lt;td&gt;&lt;ww:property/&gt;&lt;/td&gt;
 *     &lt;/ww:else&gt;
 *   &lt;/tr&gt;
 * &lt;/ww:iterator&gt;
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
 * 	&lt;webwork:iterator value="groupDao.groups" status="groupStatus"&gt;
 * 		&lt;tr class="&lt;webwork:if test="#groupStatus.odd == true "&gt;odd&lt;/webwork:if&gt;&lt;webwork:else&gt;even&lt;/webwork:else&gt;"&gt;
 * 			&lt;td&gt;&lt;webwork:property value="name" /&gt;&lt;/td&gt;
 * 			&lt;td&gt;&lt;webwork:property value="description" /&gt;&lt;/td&gt;
 * 			&lt;td&gt;
 * 				&lt;webwork:iterator value="users" status="userStatus"&gt;
 * 					&lt;webwork:property value="fullName" /&gt;&lt;webwork:if test="!#userStatus.last"&gt;,&lt;/webwork:if&gt;
 * 				&lt;/webwork:iterator&gt;
 * 			&lt;/td&gt;
 * 		&lt;/tr&gt;
 * 	&lt;/webwork:iterator&gt;
 * 
 * <!-- END SNIPPET: example3code -->
 * </pre>
 * <p>
 *
 * <!-- START SNIPPET: example4description -->
 *
 * </p> The next example iterates over a an action collection and passes every iterator value to another action. The
 * trick here lies in the use of the '[0]' operator. It takes the current iterator value and passes it on to the edit
 * action. Using the '[0]' operator has the same effect as using &gt;ww:property /&gt;. (The latter, however, does not
 * work from inside the param tag). </p>
 *
 * <!-- END SNIPPET: example4description -->
 *
 * <pre>
 * <!-- START SNIPPET: example4code -->
 * 
 * 		&lt;ww:action name="entries" id="entries"/&gt;
 * 		&lt;ww:iterator value="#entries.entries" &gt;
 * 			&lt;ww:property value="name" /&gt;
 * 			&lt;ww:property /&gt;
 * 			&lt;ww:push value="..."&gt;
 * 				&lt;ww:action name="edit" id="edit" &gt;
 * 					&lt;ww:param name="entry" value="[0]" /&gt;
 * 				&lt;/ww:action&gt;
 * 			&lt;/push&gt;
 * 		&lt;/ww:iterator&gt;
 * 
 * <!-- END SNIPPET: example4code -->
 * </pre>
 *
 * @author $Author: rgielen $
 * @author Rick Salsa (rsal@mb.sympatico.ca)
 * @author tm_jee ( tm_jee(at)yahoo.co.uk )
 * @version $Revision: 1.11 $
 * @ww.tag name="iterator" tld-body-content="JSP" tld-tag-class="com.opensymphony.webwork.views.jsp.IteratorTag"
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
     * @ww.tagattribute required="false" type="Boolean" default="false"
     */
    public void setStatus(String status) {
        this.statusAttr = status;
    }

    /**
     * the iteratable source to iterate over, else an the object itself will be put into a newly created List
     * @ww.tagattribute required="false"
     */
    public void setValue(String value) {
        this.value = value;
    }

}
