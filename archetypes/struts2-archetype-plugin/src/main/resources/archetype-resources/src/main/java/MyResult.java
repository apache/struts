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
package $package;

import org.apache.struts2.ServletActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;

import javax.servlet.http.HttpServletResponse;
import java.io.Writer;


/**
 * An example result that simply returns "hello".
 *
 */
public class MyResult implements Result {

    /**
     * Executes the result. 
     *
     * @param invocation an encapsulation of the action execution state.
     * @throws Exception if an error occurs when writing the text to the servlet output stream.
     */
    public void execute(ActionInvocation invocation) throws Exception {

        HttpServletResponse response = ServletActionContext.getResponse();
        Writer writer = response.getWriter();
        writer.write("Hello");
        writer.flush();
    }
}
