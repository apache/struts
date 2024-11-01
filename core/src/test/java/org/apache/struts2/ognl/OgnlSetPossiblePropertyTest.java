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
package org.apache.struts2.ognl;

import org.apache.struts2.XWorkTestCase;
import org.apache.struts2.ognl.OgnlValueStack;
import org.apache.struts2.util.ValueStackFactory;
import ognl.OgnlRuntime;
import org.apache.struts2.StrutsConstants;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotEquals;

public class OgnlSetPossiblePropertyTest extends XWorkTestCase {
    private OgnlValueStack vs;

    public <T> T setUpClass(Class<T> holderClass) throws Exception {
        Map<String, String> properties = new HashMap<>();
        properties.put(StrutsConstants.STRUTS_EXCLUDED_CLASSES, holderClass.getName() + "$ExcludedField");
        loadButSet(properties);
        vs = (OgnlValueStack) container.getInstance(ValueStackFactory.class).createValueStack();

        T nonExcludedHolder = holderClass.getDeclaredConstructor().newInstance();
        vs.push(nonExcludedHolder);

        return nonExcludedHolder;
    }

    public void testSetFieldValueDontAssignWhenHolderClassAndFieldClassHaveOnlyPublicFields() throws Exception {
        /* Case: to test setFieldValue without having set method
         *
         *  NonExcludedHolder class
         *   - field: public
         *  ExcludeField class
         *   - field: public
         */
        HolderWithPublicField holder = setUpClass(HolderWithPublicField.class);
        vs.setValue("excludedField.excludedFieldString", "EXPLOITED");

        assertNotEquals("EXPLOITED", holder.excludedField.excludedFieldString);
    }

    public void testSetMethodValueDontAssignWhenHolderAndFieldClassWithPublicMethodsAndPrivateFields() throws Exception {
        /* Case: to test setMethodValue, so to make fields as private
         *
         *  NonExcludedHolder class
         *   - field: private
         *   - method: public
         *  ExcludeField class
         *   - field: private
         *   - method: public
         */
        HolderWithPublicMethod holder = setUpClass(HolderWithPublicMethod.class);
        vs.setValue("excludedField.excludedFieldString", "EXPLOITED");

        assertNotEquals("EXPLOITED", holder.excludedField.excludedFieldString);
    }

    public void testSetFieldValueDontAssignWhenHolderClassWithGetMethodAndFieldClassWithPublicField() throws Exception {
        /* Case: to test setFieldValue when holder get method is public and field class set method is private so fallback to set field
         *
         *  NonExcludedHolder class
         *   - field: private
         *   - method: public
         *  ExcludeField class
         *   - field: public
         *   - method: private
         */
        HolderWhoseFieldWithPrivateMethod holder = setUpClass(HolderWhoseFieldWithPrivateMethod.class);
        vs.setValue("excludedField.excludedFieldString", "EXPLOITED");

        assertNotEquals("EXPLOITED", holder.excludedField.excludedFieldString);
    }

    public void testSetMethodValueDontAssignWhenHolderClassWithGetMethodAndFieldClassWithPublicMethod() throws Exception {
        /* Case: to test setMethodValue when holder get method is public and field class field is private so only call to set method
         *
         *  NonExcludedHolder class
         *   - field: private
         *   - method: public
         *  ExcludeField class
         *   - field: private
         *   - method: public
         */
        HolderWhoseFieldWithPublicMethod holder = setUpClass(HolderWhoseFieldWithPublicMethod.class);
        vs.setValue("excludedField.excludedFieldString", "EXPLOITED");

        assertNotEquals("EXPLOITED", holder.excludedField.excludedFieldString);
    }

    public void testWriteMethodValueDontAssignWhenWriteMethodIsNotAccessible() throws Exception {
        /* Case: to test invoke method from getWriteMethod when holder get method is public and field class field / set method is private so fallback to write method
         *
         *  NonExcludedHolder class
         *   - field: private
         *   - method: public
         *  ExcludeField class
         *   - field: private
         *   - set method: private
         *   - write method: public
         */
        HolderWhoseFieldWithPublicWriteMethod holder = setUpClass(HolderWhoseFieldWithPublicWriteMethod.class);
        Method writeMethod = OgnlRuntime.getWriteMethod(HolderWhoseFieldWithPublicWriteMethod.ExcludedField.class, "excludedFieldString");
        vs.setValue("excludedField.excludedFieldString", "EXPLOITED");

        assertEquals("setexcludedfieldstring", writeMethod.getName());
        assertNotEquals("EXPLOITED", holder.excludedField.excludedFieldString);
    }

    public void testWriteMethodValueDontAssignWhenPublicSetterDifferentFieldName() throws Exception {
        /* Case: to test invoke method from getWriteMethod when holder get method is public and field class field / set method is of different name
         *
         *  NonExcludedHolder class
         *   - field: private
         *   - method: public
         *  ExcludeField class
         *   - field: private
         *   - set method: public (but not matching with field name)
         */
        HolderWhoseFieldWithPublicSetterDifferentFieldName holder = setUpClass(HolderWhoseFieldWithPublicSetterDifferentFieldName.class);
        vs.setValue("excludedField.excludedFieldString", "EXPLOITED");

        assertNotEquals("EXPLOITED", holder.excludedField.excludedFieldStringInternal);
    }


    public static class HolderWithPublicField {
        public ExcludedField excludedField = new ExcludedField();

        public static class ExcludedField {
            public String excludedFieldString = "defaultValue";
        }
    }

    public static class HolderWithPublicMethod {
        private ExcludedField excludedField = new ExcludedField();

        public ExcludedField getExcludedField() {
            return excludedField;
        }

        public static class ExcludedField {
            private String excludedFieldString = "defaultValue";

            public void setExcludedFieldString(String value) {
                this.excludedFieldString = value;
            }
        }
    }

    public static class HolderWhoseFieldWithPrivateMethod {
        private ExcludedField excludedField = new ExcludedField();


        public ExcludedField getExcludedField() {
            return excludedField;
        }

        public static class ExcludedField {
            public String excludedFieldString = "defaultValue";

            private void setExcludedFieldString(String value) {
                this.excludedFieldString = value;
            }
        }
    }

    public static class HolderWhoseFieldWithPublicMethod {
        private ExcludedField excludedField = new ExcludedField();


        public ExcludedField getExcludedField() {
            return excludedField;
        }

        public static class ExcludedField {
            public String excludedFieldString = "defaultValue";

            private void setExcludedFieldString(String value) {
                this.excludedFieldString = value;
            }
        }
    }

    public static class HolderWhoseFieldWithPublicWriteMethod {
        private ExcludedField excludedField = new ExcludedField();


        public ExcludedField getExcludedField() {
            return excludedField;
        }

        public static class ExcludedField {
            private String excludedFieldString = "defaultValue";

            private void setExcludedFieldString(String value) {
                this.excludedFieldString = value;
            }

            public void setexcludedfieldstring(String value) {
                this.excludedFieldString = value;
            }
        }
    }

    public static class HolderWhoseFieldWithPublicSetterDifferentFieldName {
        private ExcludedField excludedField = new ExcludedField();

        public ExcludedField getExcludedField() {
            return excludedField;
        }

        public static class ExcludedField {
            private String excludedFieldStringInternal = "defaultValue";

            public void setExcludedFieldString(String value) {
                this.excludedFieldStringInternal = value;
            }
        }
    }
}
