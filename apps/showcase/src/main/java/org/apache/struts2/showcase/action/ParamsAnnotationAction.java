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
package org.apache.struts2.showcase.action;

import org.apache.struts2.ActionSupport;
import org.apache.struts2.interceptor.parameter.StrutsParameter;
import org.apache.struts2.showcase.model.MyDto;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;

/**
 * This class supports {@link com.atlassian.confluence.stateless.webdriver.selenium3.security.StrutsParametersTest}
 * which prevents critical security regressions. Do NOT modify without understanding the motivation behind the tests and
 * the implications of any changes.
 */
public class ParamsAnnotationAction extends ActionSupport {

    @StrutsParameter
    public String varToPrint;

    public String publicField = "no";

    @StrutsParameter
    public String publicFieldAnnotated = "no";

    private String privateField = "no";

    public int[] publicArray = new int[]{0};

    @StrutsParameter(depth = 1)
    public int[] publicArrayAnnotated = new int[]{0};

    public List<String> publicList = new ArrayList<>(singletonList("no"));

    @StrutsParameter(depth = 1)
    public List<String> publicListAnnotated = new ArrayList<>(singletonList("no"));

    private List<String> privateList = new ArrayList<>(singletonList("no"));

    public Map<String, String> publicMap = new HashMap<>(singletonMap("key", "no"));

    @StrutsParameter(depth = 1)
    public Map<String, String> publicMapAnnotated = new HashMap<>(singletonMap("key", "no"));

    public MyDto publicMyDto = new MyDto();

    @StrutsParameter(depth = 2)
    public MyDto publicMyDtoAnnotated = new MyDto();

    @StrutsParameter(depth = 1)
    public MyDto publicMyDtoAnnotatedDepthOne = new MyDto();

    private MyDto privateMyDto = new MyDto();

    public void setPrivateFieldMethod(String privateField) {
        this.privateField = privateField;
    }

    @StrutsParameter
    public void setPrivateFieldMethodAnnotated(String privateField) {
        this.privateField = privateField;
    }

    public List<String> getPrivateListMethod() {
        return privateList;
    }

    @StrutsParameter(depth = 1)
    public List<String> getPrivateListMethodAnnotated() {
        return privateList;
    }

    public MyDto getUnsafeMethodMyDto() {
        return privateMyDto;
    }

    @StrutsParameter(depth = 2)
    public MyDto getSafeMethodMyDto() {
        return privateMyDto;
    }

    @StrutsParameter(depth = 1)
    public MyDto getSafeMethodMyDtoDepthOne() {
        return privateMyDto;
    }

    public String renderVarToPrint() throws ReflectiveOperationException {
        if (varToPrint == null) {
            return "null";
        }
        Field field = this.getClass().getDeclaredField(varToPrint);
        field.setAccessible(true);
        try {
            return String.format("%s{%s}", varToPrint,
                    field.getType().isArray() ? stringifyArray(field.get(this)) : field.get(this));
        } finally {
            field.setAccessible(false);
        }
    }

    private String stringifyArray(Object array) {
        switch (array.getClass().getComponentType().getName()) {
            case "int":
                return Arrays.toString((int[]) array);
            default:
                return "TODO";
        }
    }
}
