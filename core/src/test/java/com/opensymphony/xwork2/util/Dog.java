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
package com.opensymphony.xwork2.util;

import java.io.Serializable;


/**
 * @author <a href="mailto:plightbo@cisco.com">Pat Lightbody</a>
 * @author $Author$
 * @version $Revision$
 */
public class Dog implements Serializable {

    public static final String SCIENTIFIC_NAME = "Canine";


    Cat hates;
    String name;
    int[] childAges;
    boolean male;
    int age;
    static String deity;


    public void setAge(int age) {
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    public void setChildAges(int[] childAges) {
        this.childAges = childAges;
    }

    public int[] getChildAges() {
        return childAges;
    }

    public void setException(String blah) throws Exception {
        throw new Exception("This is expected");
    }

    public String getException() throws Exception {
        throw new Exception("This is expected");
    }

    public void setHates(Cat hates) {
        this.hates = hates;
    }

    public Cat getHates() {
        return hates;
    }

    public void setMale(boolean male) {
        this.male = male;
    }

    public boolean isMale() {
        return male;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    public static String getDeity() {
        return deity;
    }

    public static void setDeity(String deity) {
        Dog.deity = deity;
    }

    public int computeDogYears() {
        return age * 7;
    }

    public int multiplyAge(int by) {
        return age * by;
    }

    /**
     * @return null
     */
    public Integer nullMethod() {
        return null;
    }

    /**
     * a method which is safe to call with a null argument
     *
     * @param arg the Boolean to return
     * @return arg, if it is not null, or Boolean.TRUE if arg is null
     */
    public Boolean nullSafeMethod(Boolean arg) {
        return (arg == null) ? Boolean.TRUE : arg;
    }

    public void getBite() {
        throw new RuntimeException("wuf wuf");
    }
}
