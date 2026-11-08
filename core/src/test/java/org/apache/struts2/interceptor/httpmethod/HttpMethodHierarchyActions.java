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
package org.apache.struts2.interceptor.httpmethod;

import org.apache.struts2.ActionSupport;
import org.apache.struts2.interceptor.httpmethod.HttpPost;

public class HttpMethodHierarchyActions {
    public static final java.util.concurrent.atomic.AtomicInteger RAN = new java.util.concurrent.atomic.AtomicInteger();

    public static class ExecutePost extends ActionSupport {
        @HttpPost
        @Override
        public String execute() { RAN.incrementAndGet(); return SUCCESS; }
    }
    public static class NamedPost extends ActionSupport {
        @HttpPost
        public String save() { RAN.incrementAndGet(); return SUCCESS; }
    }
    @HttpPost
    public static class ClassPost extends ActionSupport {
        @Override
        public String execute() { RAN.incrementAndGet(); return SUCCESS; }
    }
    public static class SubOfClassPost extends ClassPost { }
    public static class BaseMethodPost extends ActionSupport {
        @HttpPost
        public String save() { RAN.incrementAndGet(); return SUCCESS; }
    }
    public static class SubOverride extends BaseMethodPost {
        @Override
        public String save() { RAN.incrementAndGet(); return SUCCESS; }
    }

    public interface MethodPostMarker {
        @HttpPost
        default String save() { HttpMethodHierarchyActions.RAN.incrementAndGet(); return ActionSupport.SUCCESS; }
    }
    public static class ImplementsMethodMarker extends ActionSupport implements MethodPostMarker { }
    public static class SubOfImplementsMethodMarker extends ImplementsMethodMarker { }

    @HttpPost
    public interface ClassPostMarker { }
    public static class ImplementsClassMarker extends ActionSupport implements ClassPostMarker {
        @Override
        public String execute() { RAN.incrementAndGet(); return SUCCESS; }
    }
    public static class SubOfImplementsClassMarker extends ImplementsClassMarker { }
}
