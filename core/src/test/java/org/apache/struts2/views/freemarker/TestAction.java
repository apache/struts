/*
 * $Id$
 *
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

package org.apache.struts2.views.freemarker;

import java.util.ArrayList;
import java.util.List;

import com.opensymphony.xwork2.ActionSupport;


/**
 */
public class TestAction extends ActionSupport {

    private static final long serialVersionUID = -762413863731432302L;

    public TestAction() {
        super();
    }


    public List getBeanList() {
        List list = new ArrayList();
        list.add(new TestBean("one", "1"));
        list.add(new TestBean("two", "2"));
        list.add(new TestBean("three", "3"));

        return list;
    }

    public List getStringList() {
        List list = new ArrayList();
        list.add("one");
        list.add("two");
        list.add("three");

        return list;
    }
}
