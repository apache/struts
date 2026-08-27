/*
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
package org.apache.struts2.ognl.accessor;

import org.apache.struts2.ActionContext;
import org.apache.struts2.XWorkTestCase;
import org.apache.struts2.util.ValueStack;
import org.apache.struts2.util.reflection.ReflectionContextState;

public class XWorkMethodAccessorTest extends XWorkTestCase {

    public void testDenyMethodExecutionBlocksArgumentTakingGetterThatIsNotAnIndexedProperty() {
        Bean bean = new Bean();
        ValueStack vs = ActionContext.getContext().getValueStack();
        vs.push(bean);
        ReflectionContextState.setDenyMethodExecution(vs.getContext(), true);

        vs.findValue("getAttack('PWNED')");

        assertNull("getAttack(String) is not an indexed property accessor and must not be"
                + " executed while method execution is denied", bean.attackArgument);
    }

    /**
     * Note the name: OGNL classifies this pair as {@code INDEXED_PROPERTY_OBJECT}, not {@code _INT}, because
     * {@code findObjectIndexedPropertyDescriptors} overwrites the {@code java.beans} descriptor whenever it
     * finds a matching get/set pair. {@link #testDenyMethodExecutionAllowsReadOnlyIntIndexedPropertyAccessor()}
     * is what covers the {@code _INT} branch.
     */
    public void testDenyMethodExecutionAllowsIndexedPropertyAccessorDeclaredOverAnIntIndex() {
        Bean bean = new Bean();
        ValueStack vs = ActionContext.getContext().getValueStack();
        vs.push(bean);
        ReflectionContextState.setDenyMethodExecution(vs.getContext(), true);

        Object value = vs.findValue("getItem(1)");

        assertEquals("indexed property accessors must keep working while method execution is denied",
                "item1", value);
    }

    public void testDenyMethodExecutionAllowsObjectIndexedPropertyAccessor() {
        Bean bean = new Bean();
        ValueStack vs = ActionContext.getContext().getValueStack();
        vs.push(bean);
        ReflectionContextState.setDenyMethodExecution(vs.getContext(), true);

        Object value = vs.findValue("getKeyed('k')");

        assertEquals("object indexed property accessors must keep working while method execution is denied",
                "keyedk", value);
    }

    /**
     * A read-only indexed property is the one shape that reaches {@code INDEXED_PROPERTY_INT}: with no
     * matching setter, OGNL leaves the {@code java.beans} {@code IndexedPropertyDescriptor} in place.
     */
    public void testDenyMethodExecutionAllowsReadOnlyIntIndexedPropertyAccessor() {
        ReadOnlyIndexedBean bean = new ReadOnlyIndexedBean();
        ValueStack vs = ActionContext.getContext().getValueStack();
        vs.push(bean);
        ReflectionContextState.setDenyMethodExecution(vs.getContext(), true);

        Object value = vs.findValue("getItem(1)");

        assertEquals("a read-only indexed property accessor must keep working while method execution is denied",
                "item1", value);
    }

    /**
     * The property name alone does not identify the method that will run. This bean really does declare the
     * indexed pair getItem(int)/setItem(int, String), so the property is indexed - but the one-argument call
     * below dispatches to the unrelated getItem(String) overload, because the argument types choose the
     * method and the caller chooses the arguments.
     */
    public void testDenyMethodExecutionBlocksAnOverloadOfAnIndexedAccessor() {
        OverloadedIndexedBean bean = new OverloadedIndexedBean();
        ValueStack vs = ActionContext.getContext().getValueStack();
        vs.push(bean);
        ReflectionContextState.setDenyMethodExecution(vs.getContext(), true);

        vs.findValue("getItem('PWNED')");

        assertNull("an overload sharing an indexed accessor's name must not be executed while method"
                + " execution is denied", bean.overloadArgument);
    }

    /**
     * The direction matters too: a read-only indexed property must not legitimise an unrelated two-argument
     * setter that merely shares its name.
     */
    public void testDenyMethodExecutionBlocksUnrelatedSetterNamedAfterAReadOnlyIndexedProperty() {
        ReadOnlyIndexedBean bean = new ReadOnlyIndexedBean();
        ValueStack vs = ActionContext.getContext().getValueStack();
        vs.push(bean);
        ReflectionContextState.setDenyMethodExecution(vs.getContext(), true);

        vs.findValue("setItem('PWNED', 'x')");

        assertNull("a two-argument setter is not the accessor of a read-only indexed property and must not"
                + " be executed while method execution is denied", bean.setterArgument);
    }

    public void testDenyMethodExecutionBlocksBareGetAccessor() {
        Bean bean = new Bean();
        ValueStack vs = ActionContext.getContext().getValueStack();
        vs.push(bean);
        ReflectionContextState.setDenyMethodExecution(vs.getContext(), true);

        vs.findValue("get('PWNED')");

        assertNull("a map style get(String) is not an indexed property accessor and must not be"
                + " executed while method execution is denied", bean.bareGetArgument);
    }

    public void testArgumentTakingGetterIsExecutedWhenMethodExecutionIsNotDenied() {
        Bean bean = new Bean();
        ValueStack vs = ActionContext.getContext().getValueStack();
        vs.push(bean);

        vs.findValue("getAttack('PWNED')");

        assertEquals("outside parameter binding the deny flag is unset and methods still execute",
                "PWNED", bean.attackArgument);
    }

    public static class Bean {
        private String attackArgument;
        private String bareGetArgument;

        /**
         * Named exactly "get", so there is no property name left once the prefix is removed.
         */
        public String get(String key) {
            this.bareGetArgument = key;
            return "irrelevant";
        }

        /**
         * Not a JavaBeans property: takes an argument and has no matching setter, so it is not an
         * indexed property accessor either.
         */
        public String getAttack(String argument) {
            this.attackArgument = argument;
            return "irrelevant";
        }

        public String getItem(int index) {
            return "item" + index;
        }

        public void setItem(int index, String value) {
            // present so that the pair forms an indexed property
        }

        public String getKeyed(String key) {
            return "keyed" + key;
        }

        public void setKeyed(String key, String value) {
            // present so that the pair forms an indexed property
        }
    }

    public static class ReadOnlyIndexedBean {
        private String setterArgument;

        public String getItem(int index) {
            return "item" + index;
        }

        /**
         * Not the indexed setter of {@code item} - that would be {@code setItem(int, String)}. It only shares
         * the name and the two-argument shape.
         */
        public void setItem(String key, String value) {
            this.setterArgument = key;
        }
    }

    public static class OverloadedIndexedBean {
        private String overloadArgument;

        public String getItem(int index) {
            return "item" + index;
        }

        public void setItem(int index, String value) {
            // present so that the pair forms an indexed property
        }

        public String getItem(String key) {
            this.overloadArgument = key;
            return "irrelevant";
        }
    }
}
