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
package org.apache.struts2;

import org.apache.struts2.action.Action;
import org.apache.struts2.util.ValueStack;
import org.apache.struts2.validator.annotations.RequiredFieldValidator;
import org.apache.struts2.validator.annotations.RequiredStringValidator;
import org.apache.struts2.validator.annotations.Validations;
import org.apache.struts2.validator.annotations.ValidatorType;
import org.apache.struts2.interceptor.parameter.StrutsParameter;
import org.apache.struts2.views.jsp.ui.User;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestAction extends ActionSupport {

    private static final long serialVersionUID = -8891365561914451494L;

    private Collection collection;
    private Collection collection2;
    private Map map;
    private String foo;
    private Integer fooInt;
    private String result;
    private User user;
    private String[] array;
    private Object[] objectArray;
    private String[][] list;
    private List list2;
    private List list3;
    private SomeEnum status = SomeEnum.COMPLETED;
    private Float floatNumber;
    private Long id;
    private List<SomeEnum> enumList;
    private List<Integer> intList;
    private Boolean someBool;

    private final Map<String, String> texts = new HashMap<>();

    /**
     * Define a text resource within this action that will be returned by the getText methods
     * here before delegating to the default TextProvider call
     */
    public void setText(String key, String value) {
        this.texts.put(key, value);
    }

    /**
     * Returns the test value if defined otherwise delegates to the default TextProvider
     */
    public String getText(String key) {
        if (this.texts.containsKey(key)) {
            return this.texts.get(key);
        }
        return super.getText(key);
    }

    /**
     * This is the method invoked by the {@link org.apache.struts2.util.TextProviderHelper}.
     * Returns the test value if defined otherwise delegates to the default TextProvider
     */
    public String getText(String key, String defaultValue, List<?> args, ValueStack stack) {
        if (this.texts.containsKey(key)) {
            return this.texts.get(key);
        } else {
            return super.getText(key, defaultValue, args, stack);
        }
    }

    public Collection getCollection() {
        return collection;
    }

    @StrutsParameter
    public void setCollection(Collection collection) {
        this.collection = collection;
    }

    public Map getMap() {
        return map;
    }

    @StrutsParameter
    public void setMap(Map map) {
        this.map = map;
    }

    public String getFoo() {
        return foo;
    }

    @StrutsParameter
    public void setFoo(String foo) {
        this.foo = foo;
    }

    public String getResult() {
        return result;
    }

    @StrutsParameter
    public void setResult(String result) {
        this.result = result;
    }

    public User getUser() {
        return user;
    }

    @StrutsParameter
    public void setUser(User user) {
        this.user = user;
    }

    public String[] getArray() {
        return array;
    }

    @StrutsParameter
    public void setArray(String[] array) {
        this.array = array;
    }

    public Object[] getObjectArray() {
        return objectArray;
    }

    @StrutsParameter
    public void setObjectArray(Object[] arrayObject) {
        this.objectArray = arrayObject;
    }

    public String[][] getList() {
        return list;
    }

    @StrutsParameter
    public void setList(String[][] list) {
        this.list = list;
    }

    public List getList2() {
        return list2;
    }

    @StrutsParameter
    public void setList2(List list2) {
        this.list2 = list2;
    }

    @StrutsParameter
    public void setList3(List list) {
        this.list3 = list;
    }

    public List getList3() {
        return this.list3;
    }

    public Collection getCollection2() {
        return this.collection2;
    }

    @StrutsParameter
    public void setCollection2(Collection collection) {
        this.collection2 = collection;
    }

    public Integer getFooInt() {
        return fooInt;
    }

    @StrutsParameter
    public void setFooInt(Integer fooInt) {
        this.fooInt = fooInt;
    }

    @Override
    public String execute() throws Exception {
        if (result == null) {
            result = Action.SUCCESS;
        }

        return result;
    }

    @Validations(
        requiredFields = {
            @RequiredFieldValidator(type = ValidatorType.SIMPLE, fieldName = "status", message = "You must enter a value for field.")
        },
        requiredStrings = {
            @RequiredStringValidator(type = ValidatorType.SIMPLE, fieldName = "result", message = "You must enter a value for field.")
        }
    )
    public String annotatedExecute1() throws Exception {
        return Action.SUCCESS;
    }

    @Validations(
        requiredFields = {
            @RequiredFieldValidator(type = ValidatorType.SIMPLE, fieldName = "status", message = "You must enter a value for field.")
        }
    )
    public String annotatedExecute2() throws Exception {
        return Action.SUCCESS;
    }

    public String executeThrowsException() throws Exception {
        throw new StrutsException("something went wrong!");
    }

    public String doInput() throws Exception {
        return INPUT;
    }

    public SomeEnum getStatus() {
        return status;
    }

    @StrutsParameter
    public void setStatus(SomeEnum status) {
        this.status = status;
    }

    public List<SomeEnum> getStatusList() {
        return Arrays.asList(SomeEnum.values());
    }

    public Float getFloatNumber() {
        return floatNumber;
    }

    @StrutsParameter
    public void setFloatNumber(Float floatNumber) {
        this.floatNumber = floatNumber;
    }

    public Long getId() {
        return id;
    }

    @StrutsParameter
    public void setId(Long id) {
        this.id = id;
    }

    public List<SomeEnum> getEnumList() {
        return enumList;
    }

    @StrutsParameter
    public void setEnumList(List<SomeEnum> enumList) {
        this.enumList = enumList;
    }

    public List<Integer> getIntList() {
        return intList;
    }

    @StrutsParameter
    public void setIntList(List<Integer> intList) {
        this.intList = intList;
    }

    public Boolean getSomeBool() {
        return someBool;
    }

    @StrutsParameter
    public void setSomeBool(Boolean someBool) {
        this.someBool = someBool;
    }
}
