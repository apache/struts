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
package com.opensymphony.xwork2.spring;

import com.opensymphony.xwork2.Action;

/**
 * @author Mike
 */
public class ExternalReferenceAction implements Action 
{
    private Foo foo;
    private Bar bar;

    public String execute() throws Exception {
        return SUCCESS;
    }

    /**
     * @return Returns the foo.
     */
    public Foo getFoo() {
        return foo;
    }

    /**
     * @param foo
     *            The foo to set.
     */
    public void setFoo(Foo foo) {
        this.foo = foo;
    }

    /**
     * @return Returns the bar.
     */
    public Bar getBar() {
        return bar;
    }

    /**
     * @param bar
     *            The bar to set.
     */
    public void setBar(Bar bar) {
        this.bar = bar;
    }
}
