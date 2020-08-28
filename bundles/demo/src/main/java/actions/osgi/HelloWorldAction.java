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
package actions.osgi;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Actions;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.ResultPath;

@Namespace("/osgi")
@ResultPath("/content/osgi")
public class HelloWorldAction extends ActionSupport {
    private Message message = new Message("Default non-null message");

    @Override
    @Actions({
        @Action(value="hello-convention", results={@Result(name="success", type="freemarker", location="/content/osgi/hello-convention.ftl")}),
        @Action(value="/osgi/hello-convention", results={@Result(name="success", type="freemarker", location="/content/osgi/hello-convention.ftl")})
    })
    public String execute() {
        return SUCCESS;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public String getSimpleMessage() {
        return "Hello!!!";
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{message:");
        sb.append(message != null ? message.getText() : "null");
        sb.append("}");
        return sb.toString();
    }
}