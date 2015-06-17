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
package com.opensymphony.xwork2;

/**
 * ModelDrivenAnnotationAction
 *
 * @author Jason Carreira
 * @author Rainer Hermanns
 *         Created Apr 8, 2003 6:27:29 PM
 */
public class ModelDrivenAnnotationAction extends ActionSupport implements ModelDriven {

    private String foo;
    private AnnotatedTestBean model = new AnnotatedTestBean();


    public void setFoo(String foo) {
        this.foo = foo;
    }

    public String getFoo() {
        return foo;
    }

    /**
     * @return the model to be pushed onto the ValueStack after the Action itself
     */
    public Object getModel() {
        return model;
    }
}
