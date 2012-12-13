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

package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.struts2.util.MakeIterator;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;
import org.apache.struts2.views.jsp.IteratorStatus;

import java.io.Writer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * <p>Iterator will iterate over a value. An iterable value can be any of: java.util.Collection, java.util.Iterator,
 * java.util.Enumeration, java.util.Map, or an array.</p> <p/> <!-- END SNIPPET: javadoc -->
 *
 * <!-- START SNIPPET: params -->
 *
 * <ul>
 *
 * <li>status (String) - if specified, an instance of IteratorStatus will be pushed into stack upon each iteration</li>
 *
 * <li>value (Object) - the source to iterate over, must be iteratable, else the object itself will be put into a
 * newly created List (see MakeIterator#convert(Object)</li>
 *
 * <li>id (String) - if specified the current iteration object will be place with this id in Struts stack's context
 * scope</li>
 *
 * <li>begin (Integer) - if specified the iteration will start on that index</li>
 *
 * <li>end (Integer) - if specified the iteration will end on that index(inclusive)</li>
 *
 * <li>step (Integer) - if specified the iteration index will be increased by this value on each iteration. It can be a negative
 * value, in which case 'begin' must be greater than 'end'</li>
 *
 * </ul>
 *
 * <!-- END SNIPPET: params -->
 *
 * <!-- START SNIPPET: example1description -->
 *
 * <p>The following example retrieves the value of the getDays() method of the current object on the value stack and
 * uses it to iterate over. The &lt;s:property/&gt; tag prints out the current value of the iterator.</p>
 *
 * <!-- END SNIPPET: example1description -->
 *
 * <pre>
 * <!-- START SNIPPET: example1code -->
 * &lt;s:iterator value="days"&gt;
 *   &lt;p&gt;day is: &lt;s:property/&gt;&lt;/p&gt;
 * &lt;/s:iterator&gt;
 * <!-- END SNIPPET: example1code -->
 * </pre>
 *
 *
 * <!-- START SNIPPET: example2description -->
 *
 * <p>The following example uses a {@link Bean} tag and places it into the ActionContext. The iterator tag will retrieve
 * that object from the ActionContext and then calls its getDays() method as above. The status attribute is also used to
 * create an {@link IteratorStatus} object, which in this example, its odd() method is used to alternate row
 * colours:</p>
 *
 * <!-- END SNIPPET: example2description -->
 *
 *
 * <pre>
 * <!-- START SNIPPET: example2code -->
 *
 * &lt;s:bean name="org.apache.struts2.example.IteratorExample" var="it"&gt;
 *   &lt;s:param name="day" value="'foo'"/&gt;
 *   &lt;s:param name="day" value="'bar'"/&gt;
 * &lt;/s:bean&gt;
 * <p/>
 * &lt;table border="0" cellspacing="0" cellpadding="1"&gt;
 * &lt;tr&gt;
 *   &lt;th&gt;Days of the week&lt;/th&gt;
 * &lt;/tr&gt;
 * <p/>
 * &lt;s:iterator value="#it.days" status="rowstatus"&gt;
 *   &lt;tr&gt;
 *     &lt;s:if test="#rowstatus.odd == true"&gt;
 *       &lt;td style="background: grey"&gt;&lt;s:property/&gt;&lt;/td&gt;
 *     &lt;/s:if&gt;
 *     &lt;s:else&gt;
 *       &lt;td&gt;&lt;s:property/&gt;&lt;/td&gt;
 *     &lt;/s:else&gt;
 *   &lt;/tr&gt;
 * &lt;/s:iterator&gt;
 * &lt;/table&gt;
 *
 * <!-- END SNIPPET: example2code -->
 * </pre>
 *
 * <!--START SNIPPET: example3description -->
 *
 * <p> The next example will further demonstrate the use of the status attribute, using a DAO obtained from the action
 * class through OGNL, iterating over groups and their users (in a security context). The last() method indicates if the
 * current object is the last available in the iteration, and if not, we need to separate the users using a comma: </p>
 *
 * <!-- END SNIPPET: example3description -->
 *
 * <pre>
 * <!-- START SNIPPET: example3code -->
 *
 *  &lt;s:iterator value="groupDao.groups" status="groupStatus"&gt;
 *      &lt;tr class="&lt;s:if test="#groupStatus.odd == true "&gt;odd&lt;/s:if&gt;&lt;s:else&gt;even&lt;/s:else&gt;"&gt;
 *          &lt;td&gt;&lt;s:property value="name" /&gt;&lt;/td&gt;
 *          &lt;td&gt;&lt;s:property value="description" /&gt;&lt;/td&gt;
 *          &lt;td&gt;
 *              &lt;s:iterator value="users" status="userStatus"&gt;
 *                  &lt;s:property value="fullName" /&gt;&lt;s:if test="!#userStatus.last"&gt;,&lt;/s:if&gt;
 *              &lt;/s:iterator&gt;
 *          &lt;/td&gt;
 *      &lt;/tr&gt;
 *  &lt;/s:iterator&gt;
 *
 * <!-- END SNIPPET: example3code -->
 * </pre>
 * <p>
 *
 * <!-- START SNIPPET: example4description -->
 *
 * </p> The next example iterates over a an action collection and passes every iterator value to another action. The
 * trick here lies in the use of the '[0]' operator. It takes the current iterator value and passes it on to the edit
 * action. Using the '[0]' operator has the same effect as using &lt;s:property /&gt;. (The latter, however, does not
 * work from inside the param tag). </p>
 *
 * <!-- END SNIPPET: example4description -->
 *
 * <pre>
 * <!-- START SNIPPET: example4code -->
 *
 *      &lt;s:action name="entries" var="entries"/&gt;
 *      &lt;s:iterator value="#entries.entries" &gt;
 *          &lt;s:property value="name" /&gt;
 *          &lt;s:property /&gt;
 *          &lt;s:push value="..."&gt;
 *              &lt;s:action name="edit" var="edit" &gt;
 *                  &lt;s:param name="entry" value="[0]" /&gt;
 *              &lt;/s:action&gt;
 *          &lt;/push&gt;
 *      &lt;/s:iterator&gt;
 *
 * <!-- END SNIPPET: example4code -->
 * </pre>
 *
 * <!-- START SNIPPET: example5description -->
 *
 * </p>A loop that iterates 5 times
 *
 * <!-- END SNIPPET: example5description -->
 *
 * <pre>
 * <!-- START SNIPPET: example5code -->
 *
 * &lt;s:iterator var="counter" begin="1" end="5" &gt;
 *    &lt;!-- current iteration value (1, ... 5) --&gt;
 *    &lt;s:property value="top" /&gt;
 * &lt;/s:iterator&gt;
 *
 * <!-- END SNIPPET: example5code -->
 * </pre>
 *
 * <!-- START SNIPPET: example6description -->
 *
 * </p>Another way to create a simple loop, similar to JSTL's 
 * &lt;c:forEach begin="..." end="..." ...&gt; is to use some 
 * OGNL magic, which provides some under-the-covers magic to 
 * make 0-n loops trivial. This example also loops five times.
 *
 * <!-- END SNIPPET: example6description -->
 *
 * <pre>
 * <!-- START SNIPPET: example6code -->
 *
 * &lt;s:iterator status="stat" value="(5).{ #this }" &gt;
 *    &lt;s:property value="#stat.count" /&gt; &lt;!-- Note that "count" is 1-based, "index" is 0-based. --&gt;
 * &lt;/s:iterator&gt;
 *
 * <!-- END SNIPPET: example6code -->
 * </pre>
 *
 *  <!-- START SNIPPET: example7description -->
 *
 * </p>A loop that iterates over a partial list
 *
 * <!-- END SNIPPET: example7description -->
 *
 * <pre>
 * <!-- START SNIPPET: example7code -->
 *
 * &lt;s:iterator value="{1,2,3,4,5}" begin="2" end="4" &gt;
 *    &lt;!-- current iteration value (2,3,4) --&gt;
 *    &lt;s:property value="top" /&gt;
 * &lt;/s:iterator&gt;
 *
 * <!-- END SNIPPET: example7code -->
 * </pre>
 */
@StrutsTag(name="iterator", tldTagClass="org.apache.struts2.views.jsp.IteratorTag", description="Iterate over a iterable value")
public class IteratorComponent extends ContextBean {
    private static final Logger LOG = LoggerFactory.getLogger(IteratorComponent.class);

    protected Iterator iterator;
    protected IteratorStatus status;
    protected Object oldStatus;
    protected IteratorStatus.StatusState statusState;
    protected String statusAttr;
    protected String value;
    protected String beginStr;
    protected Integer begin;
    protected String endStr;
    protected Integer end;
    protected String stepStr;
    protected Integer step;

    public IteratorComponent(ValueStack stack) {
        super(stack);
    }

    public boolean start(Writer writer) {
        //Create an iterator status if the status attribute was set.
        if (statusAttr != null) {
            statusState = new IteratorStatus.StatusState();
            status = new IteratorStatus(statusState);
        }

        if (beginStr != null)
            begin = (Integer) findValue(beginStr,  Integer.class);

        if (endStr != null)
            end = (Integer) findValue(endStr,  Integer.class);

        if (stepStr != null)
            step = (Integer) findValue(stepStr,  Integer.class);

        ValueStack stack = getStack();

        if (value == null && begin == null && end == null) {
            value = "top";
        }
        Object iteratorTarget = findValue(value);
        iterator = MakeIterator.convert(iteratorTarget);

        if (begin != null) {
            //default step to 1
            if (step == null)
                step = 1;

            if (iterator == null) {
                //classic for loop from 'begin' to 'end'
                iterator = new CounterIterator(begin, end, step, null);
            } else if (iterator != null) {
                //only arrays and lists are supported
                if (iteratorTarget.getClass().isArray()) {
                    Object[] values = (Object[]) iteratorTarget;
                    if (end == null)
                        end = step > 0 ? values.length - 1 : 0;
                    iterator = new CounterIterator(begin, end, step, Arrays.asList(values));
                } else if (iteratorTarget instanceof List) {
                    List values = (List) iteratorTarget;
                    if (end == null)
                        end = step > 0 ? values.size() - 1 : 0;
                    iterator = new CounterIterator(begin, end, step, values);
                } else {
                    //so the iterator is not based on an array or a list
                    LOG.error("Incorrect use of the iterator tag. When 'begin' is set, 'value' must be" +
                            " an Array or a List, or not set at all. 'begin', 'end' and 'step' will be ignored");
                }
            }
        }

        // get the first
        if ((iterator != null) && iterator.hasNext()) {
            Object currentValue = iterator.next();
            stack.push(currentValue);

            String var = getVar();

            if ((var != null) && (currentValue != null)) {
                //pageContext.setAttribute(id, currentValue);
                //pageContext.setAttribute(id, currentValue, PageContext.REQUEST_SCOPE);
                putInContext(currentValue);
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
        ValueStack stack = getStack();
        if (iterator != null) {
            stack.pop();
        }

        if (iterator!=null && iterator.hasNext()) {
            Object currentValue = iterator.next();
            stack.push(currentValue);

            putInContext(currentValue);

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

    static class CounterIterator implements Iterator<Object> {
        private int step;
        private int end;
        private int currentIndex;
        private List<Object> values;

        CounterIterator(Integer begin, Integer end, Integer step, List<Object> values) {
            this.end = end;
            if (step != null)
                this.step = step;
            this.currentIndex = begin - this.step;
            this.values = values;
        }

        public boolean hasNext() {
            int next = peekNextIndex();
            return step > 0 ? next <= end : next >= end;
        }

        public Object next() {
            if (hasNext()) {
                int nextIndex = peekNextIndex();
                currentIndex += step;
                return values != null ? values.get(nextIndex) : nextIndex;
            } else {
                throw new IndexOutOfBoundsException("Index " + ( currentIndex + step) + " must be less than or equal to " + end);
            }
        }

        private int peekNextIndex() {
           return currentIndex + step;
        }

        public void remove() {
            throw new UnsupportedOperationException("Values cannot be removed from this iterator");
        }
    }

    @StrutsTagAttribute(description="If specified, an instanceof IteratorStatus will be pushed into stack upon each iteration",
        type="Boolean", defaultValue="false")
    public void setStatus(String status) {
        this.statusAttr = status;
    }

    @StrutsTagAttribute(description="the iteratable source to iterate over, else an the object itself will be put into a newly created List")
    public void setValue(String value) {
        this.value = value;
    }

    @StrutsTagAttribute(description="if specified the iteration will start on that index", type="Integer", defaultValue="0")
    public void setBegin(String begin) {
        this.beginStr = begin;
    }

    @StrutsTagAttribute(description="if specified the iteration will end on that index(inclusive)", type="Integer",
            defaultValue="Size of the 'values' List or array, or 0 if 'step' is negative")
    public void setEnd(String end) {
        this.endStr = end;
    }

    @StrutsTagAttribute(description="if specified the iteration index will be increased by this value on each iteration. It can be a negative " +
            "value, in which case 'begin' must be greater than 'end'", type="Integer", defaultValue="1")
    public void setStep(String step) {
        this.stepStr = step;
    }
}
