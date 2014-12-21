/*
 * Copyright 2002-2003,2009 The Apache Software Foundation.
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
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.inject.Inject;

import java.util.*;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class SimpleAction extends ActionSupport {

    public static final String COMMAND_RETURN_CODE = "com.opensymphony.xwork2.SimpleAction.CommandInvoked";


    private ArrayList someList = new ArrayList();
    private Date date = new Date();
    private Properties settings = new Properties();
    private String blah;
    private String name;
    private TestBean bean = new TestBean();
    private boolean throwException;
    private int bar;
    private int baz;
    private int foo;
    private long longFoo;
    private short shortFoo;
    private double percentage;
    private Map<Integer,String> indexedProps = new HashMap<Integer,String>();

    private String aliasSource;
    private String aliasDest;
    private Map<String,String> protectedMap = new HashMap<String,String>();
    private Map<String,String> existingMap = new HashMap<String,String>();
    
    public static boolean resultCalled;


    public SimpleAction() {
        resultCalled = false;
        existingMap.put("existingKey", "value");
    }
    
    public Map<String,String> getTheProtectedMap() {
        return protectedMap;
    }
    
    protected Map<String,String> getTheSemiProtectedMap() {
        return protectedMap;
    }

    public void setExistingMap(Map<String,String> map) {
        this.existingMap = map;
    }

    public Map<String,String> getTheExistingMap() {
        return existingMap;
    }


    public void setBar(int bar) {
        this.bar = bar;
    }

    public int getBar() {
        return bar;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public void setBaz(int baz) {
        this.baz = baz;
    }

    public int getBaz() {
        return baz;
    }

    public void setBean(TestBean bean) {
        this.bean = bean;
    }

    public TestBean getBean() {
        return bean;
    }

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
        boolean[] b = new boolean[]{true, false, false, true};

        return b;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setFoo(int foo) {
        this.foo = foo;
    }

    public int getFoo() {
        return foo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setSettings(Properties settings) {
        this.settings = settings;
    }

    public Properties getSettings() {
        return settings;
    }


    public String getAliasDest() {
        return aliasDest;
    }

    public void setAliasDest(String aliasDest) {
        this.aliasDest = aliasDest;
    }

    public String getAliasSource() {
        return aliasSource;
    }

    public void setAliasSource(String aliasSource) {
        this.aliasSource = aliasSource;
    }


    public void setSomeList(ArrayList someList) {
        this.someList = someList;
    }

    public ArrayList getSomeList() {
        return someList;
    }
    
    public String getIndexedProp(int index) {
    	return indexedProps.get(index);
    }
    
    public void setIndexedProp(int index, String val) {
    	indexedProps.put(index, val);
    }
    

    public void setThrowException(boolean   throwException) {
        this.throwException = throwException;
    }

    public String commandMethod() throws Exception {
        return COMMAND_RETURN_CODE;
    }
    
    public Result resultAction() throws Exception {
    	return new Result() {
            public Configuration configuration;

            @Inject
            public void setConfiguration(Configuration config) {
                this.configuration = config;
            }
            public void execute(ActionInvocation invocation) throws Exception {
                if (configuration != null)
                    resultCalled = true;
            }
    	    
    	};
    }

    public String exceptionMethod() throws Exception {
        if (throwException) {
            throw new Exception("We're supposed to throw this");
        }

        return "OK";
    }

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
    
    public String doInput() throws Exception {
        return INPUT;
    }

    public String doWith() throws Exception {
        return "with";
    }

    public long getLongFoo() {
        return longFoo;
    }


    public void setLongFoo(long longFoo) {
        this.longFoo = longFoo;
    }


    public short getShortFoo() {
        return shortFoo;
    }


    public void setShortFoo(short shortFoo) {
        this.shortFoo = shortFoo;
    }
}
