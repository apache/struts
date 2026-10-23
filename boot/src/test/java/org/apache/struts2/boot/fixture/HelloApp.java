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
package org.apache.struts2.boot.fixture;

import org.apache.struts2.boot.ActionRegistry;
import org.apache.struts2.boot.StrutsBootApplication;

import static org.apache.struts2.boot.HttpMethods.POST;

public class HelloApp implements StrutsBootApplication {

    @Override
    public void actions(ActionRegistry actions) {
        actions.action("hello", HelloAction.class).success("hello.ftl");
        actions.action("boom", ThrowingAction.class).success("hello.ftl");
        actions.namespace("/admin", admin -> {
            admin.action("users", UserAction.class).method("list").success("admin/users.ftl");
            admin.action("saveUser", UserAction.class).method("save").httpMethods(POST).redirectAction("success", "users");
        });
    }
}
