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
package com.opensymphony.xwork2.mock;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;

/**
 * Mock for a {@link Result}.
 *
 * @author Mike
 * @author Rainer Hermanns
 */
public class MockResult implements Result {

    public static final String DEFAULT_PARAM = "foo";

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        return o instanceof MockResult;
    }

    public void execute(ActionInvocation invocation) throws Exception {
        // no op
    }

    @Override
    public int hashCode() {
        return 10;
    }

    public void setFoo(String foo) {
        // no op
    }

}
