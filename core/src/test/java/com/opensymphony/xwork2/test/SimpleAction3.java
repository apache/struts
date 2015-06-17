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
package com.opensymphony.xwork2.test;

import com.opensymphony.xwork2.SimpleAction;
import com.opensymphony.xwork2.util.Bar;


/**
 * Extend SimpleAction to test class hierarchy traversal.
 *
 * @author Mark Woon
 */
public class SimpleAction3 extends SimpleAction implements DataAware {

    private Bar bar;
    private String data;


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
}
