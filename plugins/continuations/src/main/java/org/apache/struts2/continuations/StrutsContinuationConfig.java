/*
 * $Id: URL.java 474191 2006-11-13 08:30:40Z mrdon $
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
package org.apache.struts2.continuations;

import com.uwyn.rife.continuations.ContinuationConfig;

/**
 * RIFE Continuation configuration.
 */
public class StrutsContinuationConfig extends ContinuationConfig {
    public static final String CONTINUE_PARAM = "__continue";
    public static final String CONTINUE_KEY = "__continue";

    public String getContinuableClassInternalName() {
        return "com.opensymphony.xwork2.ActionSupport";
    }

    public String getContinuableInterfaceInternalName() {
        return "com.opensymphony.xwork2.Action";
    }

    public String getEntryMethod() {
        return "execute()Ljava/lang/String;";
    }

    public String getContinuableClassOrInterfaceName() {
        return "com.opensymphony.xwork2.ActionSupport";
    }

    public String getPauseSignature() {
        return "(Ljava/lang/String;)V";
    }
}
