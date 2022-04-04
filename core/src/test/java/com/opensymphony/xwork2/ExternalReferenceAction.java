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
/*
 * Created on Nov 11, 2003
 *
 * To change the template for this generated file go to Window - Preferences -
 * Java - Code Generation - Code and Comments
 */
package com.opensymphony.xwork2;

public class ExternalReferenceAction implements Action {

    private Foo foo;


    /**
     * @param foo The foo to set.
     */
    public void setFoo(Foo foo) {
        this.foo = foo;
    }

    /**
     * @return Returns the foo.
     */
    public Foo getFoo() {
        return foo;
    }

    public String execute() throws Exception {
        return SUCCESS;
    }
}
