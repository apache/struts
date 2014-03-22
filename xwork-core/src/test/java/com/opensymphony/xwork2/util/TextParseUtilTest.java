/*
 * Copyright 2002-2006,2009 The Apache Software Foundation.
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
package com.opensymphony.xwork2.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.junit.Assert;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.XWorkTestCase;

/**
 * Unit test of {@link TextParseUtil}.
 *
 * @author plightbo
 * @author tm_jee
 *
 * @version $Date$ $Id$
 */
public class TextParseUtilTest extends XWorkTestCase {


	public void testTranslateVariablesWithEvaluator() throws Exception {
		ValueStack stack = ActionContext.getContext().getValueStack();
		stack.push(new Object() {
			public String getMyVariable() {
				return "My Variable ";
			}
		});

		TextParseUtil.ParsedValueEvaluator evaluator = new TextParseUtil.ParsedValueEvaluator() {
			public Object evaluate(String parsedValue) {
				return parsedValue.toString()+"Something";
			}
		};

		String result = TextParseUtil.translateVariables("Hello ${myVariable}", stack, evaluator);

		assertEquals(result, "Hello My Variable Something");
	}

    public void testTranslateVariables() {
        ValueStack stack = ActionContext.getContext().getValueStack();

        Object s = TextParseUtil.translateVariables("foo: ${{1, 2, 3}}, bar: %{1}", stack);
        assertEquals("foo: [1, 2, 3], bar: 1", s);

        s = TextParseUtil.translateVariables("foo: %{{1, 2, 3}}, bar: %{1}", stack);
        assertEquals("foo: [1, 2, 3], bar: 1", s);

        s = TextParseUtil.translateVariables("foo: %{{1, 2, 3}}, bar: %{1}", stack);
        assertEquals("foo: [1, 2, 3], bar: 1", s);

        s = TextParseUtil.translateVariables("foo: ${#{1 : 2, 3 : 4}}, bar: ${1}", stack);
        assertEquals("foo: {1=2, 3=4}, bar: 1", s);

        s = TextParseUtil.translateVariables("foo: %{#{1 : 2, 3 : 4}}, bar: %{1}", stack);
        assertEquals("foo: {1=2, 3=4}, bar: 1", s);

        s = TextParseUtil.translateVariables("foo: 1}", stack);
        assertEquals("foo: 1}", s);

        s = TextParseUtil.translateVariables("foo: {1}", stack);
        assertEquals("foo: {1}", s);

        s = TextParseUtil.translateVariables("foo: ${1", stack);
        assertEquals("foo: ${1", s);

        s = TextParseUtil.translateVariables("foo: %{1", stack);
        assertEquals("foo: %{1", s);

        s =  TextParseUtil.translateVariables('$', "${{1, 2, 3}}", stack, Object.class);
        assertNotNull(s);
        assertTrue("List not returned when parsing a 'pure' list", s instanceof List);
        assertEquals(((List)s).size(), 3);

        s = TextParseUtil.translateVariables('$', "${#{'key1':'value1','key2':'value2','key3':'value3'}}", stack, Object.class);
        assertNotNull(s);
        assertTrue("Map not returned when parsing a 'pure' map", s instanceof Map);
        assertEquals(((Map)s).size(), 3);

        s =  TextParseUtil.translateVariables('$', "${1} two ${3}", stack, Object.class);
        assertEquals("1 two 3", s);

        s = TextParseUtil.translateVariables('$', "count must be between ${123} and ${456}, current value is ${98765}.", stack, Object.class);
        assertEquals("count must be between 123 and 456, current value is 98765.", s);
    }

    public void testNestedExpression() throws Exception {
        ValueStack stack = ActionContext.getContext().getValueStack();
        stack.push(new HashMap<String, Object>() {{ put("foo", "${%{1+1}}"); }});
        String s = TextParseUtil.translateVariables("${foo}", stack);
        assertEquals("${%{1+1}}", s);
        stack.pop();
    }

    public void testMixedOpenChars() throws Exception {
        ValueStack stack = ActionContext.getContext().getValueStack();
        stack.push(new HashMap<String, Object>() {{ put("foo", "bar"); }});
        String s = TextParseUtil.translateVariables("${foo}-%{foo}", stack);
        assertEquals("bar-bar", s);
        s = TextParseUtil.translateVariables("%{foo}-${foo}", stack);
        assertEquals("%{foo}-bar", s); // this is bad, but it is the only way not to double evaluate passed expression
        stack.pop();
    }

    public void testCommaDelimitedStringToSet() {
        assertEquals(0, TextParseUtil.commaDelimitedStringToSet("").size());
        assertEquals(new HashSet<String>(Arrays.asList("foo", "bar", "tee")),
                TextParseUtil.commaDelimitedStringToSet(" foo, bar,tee"));
    }

    public void testTranslateVariablesOpenChar() {
        // just a quick test to see if the open char works
        // most test are done the methods above
        ValueStack stack = ActionContext.getContext().getValueStack();

        Object s = TextParseUtil.translateVariables('$', "foo: ${{1, 2, 3}}, bar: ${1}", stack);
        assertEquals("foo: [1, 2, 3], bar: 1", s);

        Object s2 = TextParseUtil.translateVariables('#', "foo: #{{1, 2, 3}}, bar: #{1}", stack);
        assertEquals("foo: [1, 2, 3], bar: 1", s2);
    }

    public void testTranslateNoVariables() {
        ValueStack stack = ActionContext.getContext().getValueStack();

        Object s = TextParseUtil.translateVariables('$', "foo: ${}", stack);
        assertEquals("foo: ", s);
    }

    public void testTranslateVariablesNoRecursive() {
        ValueStack stack = ActionContext.getContext().getValueStack();
        stack.push(new HashMap<String, Object>() {{ put("foo", "${1+1}"); }});

        Object s = TextParseUtil.translateVariables('$', "foo: ${foo}", stack, String.class, null, 1);
        assertEquals("foo: ${1+1}", s);
    }

    public void testTranslateVariablesRecursive() {
        ValueStack stack = ActionContext.getContext().getValueStack();
        stack.push(new HashMap<String, Object>() {{ put("foo", "${1+1}"); put("bar", "${${1+2}}"); }});

        Object s = TextParseUtil.translateVariables('$', "foo: ${foo}", stack, String.class, null, 2);
        assertEquals("foo: 2", s);

        s = TextParseUtil.translateVariables('$', "foo: ${bar}", stack, String.class, null, 1);
        assertEquals("foo: ${${1+2}}", s);
    }

    public void testTranslateVariablesWithNull() {
        // given
        ValueStack stack = ActionContext.getContext().getValueStack();
        stack.push(new HashMap<String, Object>() {{ put("foo", null); }});

        TextParseUtil.ParsedValueEvaluator evaluator = new TextParseUtil.ParsedValueEvaluator() {
            public Object evaluate(String parsedValue) {
                return parsedValue;
            }
        };

        // when
        Object s = TextParseUtil.translateVariables('$', "foo: ${foo}", stack, String.class, evaluator, 2);

        // then
        assertEquals("foo: ", s);
    }

    public void testTranslateVariablesCollection() {
        ValueStack stack = ActionContext.getContext().getValueStack();
        final List<String> list = new ArrayList<String>() {{
            add("val 1");
            add("val 2");
        }};
        stack.push(new HashMap<String, Object>() {{ put("list", list); }});

        Collection<String> collection = TextParseUtil.translateVariablesCollection("${list}", stack, true, null);

        Assert.assertNotNull(collection);
        Assert.assertEquals(2, collection.size());
    }

    public void testTranslateVariablesCollectionWithExpressions() {
        ValueStack stack = ActionContext.getContext().getValueStack();
        final List<String> list = new ArrayList<String>() {{
            add("${val1}");
            add("%{val2}");
        }};
        stack.push(new HashMap<String, Object>() {{ put("list", list); put("val1", 1); put("val2", "Value 2"); }});

        Collection<String> collection = TextParseUtil.translateVariablesCollection("${list}", stack, true, null);

        Assert.assertNotNull(collection);
        Assert.assertEquals(2, collection.size());

        // if this starts passing, probably an double evaluation expression vulnerability was introduced
        // carefully review changes as this can affect users and allows break in intruders
        Assert.assertEquals("${val1}", collection.toArray()[0]);
        Assert.assertEquals("%{val2}", collection.toArray()[1]);
    }

}
