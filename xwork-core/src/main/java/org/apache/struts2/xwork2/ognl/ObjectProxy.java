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

package com.opensymphony.xwork2.ognl;

/**
 * An Object to use within OGNL to proxy other Objects
 * usually Collections that you set in a different place
 * on the ValueStack but want to retain the context information
 * about where they previously were.
 *
 * @author Gabe
 */
public class ObjectProxy {
    private Object value;
    private Class lastClassAccessed;
    private String lastPropertyAccessed;

    public Class getLastClassAccessed() {
        return lastClassAccessed;
    }

    public void setLastClassAccessed(Class lastClassAccessed) {
        this.lastClassAccessed = lastClassAccessed;
    }

    public String getLastPropertyAccessed() {
        return lastPropertyAccessed;
    }

    public void setLastPropertyAccessed(String lastPropertyAccessed) {
        this.lastPropertyAccessed = lastPropertyAccessed;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
