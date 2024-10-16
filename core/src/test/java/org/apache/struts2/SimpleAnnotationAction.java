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

import org.apache.struts2.validator.annotations.DateRangeFieldValidator;
import org.apache.struts2.validator.annotations.DoubleRangeFieldValidator;
import org.apache.struts2.validator.annotations.EmailValidator;
import org.apache.struts2.validator.annotations.ExpressionValidator;
import org.apache.struts2.validator.annotations.IntRangeFieldValidator;
import org.apache.struts2.validator.annotations.RequiredFieldValidator;
import org.apache.struts2.validator.annotations.RequiredStringValidator;
import org.apache.struts2.validator.annotations.StringLengthFieldValidator;
import org.apache.struts2.validator.annotations.UrlValidator;
import org.apache.struts2.validator.annotations.Validations;
import org.apache.struts2.validator.annotations.ValidatorType;
import org.apache.struts2.interceptor.parameter.StrutsParameter;

import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;


/**
 * Simple Test Action for annotation processing.
 */
public class SimpleAnnotationAction extends ActionSupport {
    //~ Static fields/initializers /////////////////////////////////////////////

    public static final String COMMAND_RETURN_CODE = "org.apache.struts2.SimpleAnnotationAction.CommandInvoked";

    //~ Instance fields ////////////////////////////////////////////////////////

    private ArrayList<String> someList = new ArrayList<>();
    private Date date = new Date();
    private Properties settings = new Properties();
    private String blah;
    private String name;
    private AnnotatedTestBean bean = new AnnotatedTestBean();
    private boolean throwException;
    private int bar;
    private int baz;
    private int foo;
    private double percentage;

    private String aliasSource;
    private String aliasDest;

    //~ Constructors ///////////////////////////////////////////////////////////

    public SimpleAnnotationAction() {
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    @RequiredFieldValidator(type = ValidatorType.FIELD, message = "You must enter a value for bar.")
    @IntRangeFieldValidator(type = ValidatorType.FIELD, min = "6", max = "10", message = "bar must be between ${min} and ${max}, current value is ${bar}.")
    @StrutsParameter
    public void setBar(int bar) {
        this.bar = bar;
    }

    public int getBar() {
        return bar;
    }

    @IntRangeFieldValidator(min = "0", key = "baz.range", message = "Could not find baz.range!")
    @StrutsParameter
    public void setBaz(int baz) {
        this.baz = baz;
    }

    public int getBaz() {
        return baz;
    }

    public double getPercentage() {
        return percentage;
    }

    @DoubleRangeFieldValidator(minInclusive = "0.123", key = "baz.range", message = "Could not find percentage.range!")
    @StrutsParameter
    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public void setBean(AnnotatedTestBean bean) {
        this.bean = bean;
    }

    @StrutsParameter(depth = 2)
    public AnnotatedTestBean getBean() {
        return bean;
    }

    @StrutsParameter
    public void setBlah(String blah) {
        this.blah = blah;
    }

    public String getBlah() {
        return blah;
    }

    public Boolean getBool(String b) {
        return new Boolean(b);
    }

    public boolean[] getBools() {
        return new boolean[] {true, false, false, true};
    }

    @DateRangeFieldValidator(min = "12/22/2002", max = "12/25/2002", message = "The date must be between 12-22-2002 and 12-25-2002.")
    @StrutsParameter
    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    @StrutsParameter
    public void setFoo(int foo) {
        this.foo = foo;
    }

    public int getFoo() {
        return foo;
    }

    @StrutsParameter
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setSettings(Properties settings) {
        this.settings = settings;
    }

    @StrutsParameter(depth = 1)
    public Properties getSettings() {
        return settings;
    }

    public String getAliasDest() {
        return aliasDest;
    }

    @StrutsParameter
    public void setAliasDest(String aliasDest) {
        this.aliasDest = aliasDest;
    }

    public String getAliasSource() {
        return aliasSource;
    }

    @StrutsParameter
    public void setAliasSource(String aliasSource) {
        this.aliasSource = aliasSource;
    }

    @StrutsParameter
    public void setSomeList(ArrayList<String> someList) {
        this.someList = someList;
    }

    public ArrayList<String> getSomeList() {
        return someList;
    }

    @StrutsParameter
    public void setThrowException(boolean throwException) {
        this.throwException = throwException;
    }

    public String commandMethod() throws Exception {
        return COMMAND_RETURN_CODE;
    }

    public String exceptionMethod() throws Exception {
        if (throwException) {
            throw new Exception("We're supposed to throw this");
        }

        return "OK";
    }

    @Validations(
            requiredFields =
                    {@RequiredFieldValidator(type = ValidatorType.SIMPLE, fieldName = "customfield", message = "You must enter a value for field.")},
            requiredStrings =
                    {@RequiredStringValidator(type = ValidatorType.SIMPLE, fieldName = "stringisrequired", message = "You must enter a value for string.")},
            emails =
                    { @EmailValidator(type = ValidatorType.SIMPLE, fieldName = "emailaddress", message = "You must enter a value for email.")},
            urls =
                    { @UrlValidator(type = ValidatorType.SIMPLE, fieldName = "hreflocation", message = "You must enter a value for email.")},
            stringLengthFields =
                    {@StringLengthFieldValidator(type = ValidatorType.SIMPLE, trim = true, minLength="10" , maxLength = "12", fieldName = "needstringlength", message = "You must enter a stringlength.")},
            intRangeFields =
                    { @IntRangeFieldValidator(type = ValidatorType.SIMPLE, fieldName = "intfield", min = "6", max = "10", message = "bar must be between ${min} and ${max}, current value is ${bar}.")},
            dateRangeFields =
                    {@DateRangeFieldValidator(type = ValidatorType.SIMPLE, fieldName = "datefield", min = "-1", max = "99", message = "bar must be between ${min} and ${max}, current value is ${bar}.")},
            expressions = {
                @ExpressionValidator(expression = "foo &gt; 1", message = "Foo must be greater than Bar 1. Foo = ${foo}, Bar = ${bar}."),
                @ExpressionValidator(expression = "foo &gt; 2", message = "Foo must be greater than Bar 2. Foo = ${foo}, Bar = ${bar}."),
                @ExpressionValidator(expression = "foo &gt; 3", message = "Foo must be greater than Bar 3. Foo = ${foo}, Bar = ${bar}."),
                @ExpressionValidator(expression = "foo &gt; 4", message = "Foo must be greater than Bar 4. Foo = ${foo}, Bar = ${bar}."),
                @ExpressionValidator(expression = "foo &gt; 5", message = "Foo must be greater than Bar 5. Foo = ${foo}, Bar = ${bar}.")
    }
    )
    @Override
    public String execute() throws Exception {
        if (foo == bar) {
            return ERROR;
        }

        baz = foo + bar;

        name = "HelloWorld";
        settings.put("foo", "bar");
        settings.put("black", "white");

        someList.add("jack");
        someList.add("bill");
        someList.add("kerry");

        return SUCCESS;
    }
}
