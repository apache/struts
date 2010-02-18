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
package com.opensymphony.xwork2.test;

import com.opensymphony.xwork2.AnnotatedTestBean;
import com.opensymphony.xwork2.conversion.annotations.Conversion;
import com.opensymphony.xwork2.conversion.annotations.TypeConversion;
import com.opensymphony.xwork2.util.Bar;
import com.opensymphony.xwork2.util.Cat;


/**
 * Extend TestBean to test class hierarchy traversal.
 *
 * @author Mark Woon
 * @author Rainer Hermanns
 */
@Conversion()
public class AnnotationTestBean2 extends AnnotatedTestBean implements AnnotationDataAware {

    private Bar bar;
    private String data;
    private Cat cat;


    public void setBarObj(Bar b) {
        bar = b;
    }

    public Bar getBarObj() {
        return bar;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public Cat getCat() {
        return cat;
    }

    @TypeConversion(
            key = "cat", converter = "com.opensymphony.xwork2.conversion.impl.FooBarConverter"
    )
    public void setCat(Cat cat) {
        this.cat = cat;
    }
}
