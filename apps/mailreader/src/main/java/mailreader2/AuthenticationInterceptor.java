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
package mailreader2;

import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Action;
import java.util.Map;
import org.apache.struts.apps.mailreader.dao.User;

public class AuthenticationInterceptor implements Interceptor  {

    public void destroy () {}

    public void init() {}

    public String intercept(ActionInvocation actionInvocation) throws Exception {

        Map session = actionInvocation.getInvocationContext().getSession();

        User user = (User) session.get(Constants.USER_KEY);

        boolean isAuthenticated = (null!=user) && (null!=user.getDatabase());

        if (!isAuthenticated) {
            return Action.LOGIN;            
        }
        else {
            return actionInvocation.invoke();
        }

    }
}
