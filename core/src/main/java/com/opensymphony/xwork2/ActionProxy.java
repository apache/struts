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
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.config.entities.ActionConfig;

@Deprecated
public interface ActionProxy extends org.apache.struts2.ActionProxy {

    @Override
    ActionInvocation getInvocation();

    static ActionProxy adapt(org.apache.struts2.ActionProxy actualProxy) {
        if (actualProxy instanceof ActionProxy) {
            return (ActionProxy) actualProxy;
        }
        return actualProxy != null ? new LegacyAdapter(actualProxy) : null;
    }

    class LegacyAdapter implements ActionProxy {

        private final org.apache.struts2.ActionProxy adaptee;

        private LegacyAdapter(org.apache.struts2.ActionProxy adaptee) {
            this.adaptee = adaptee;
        }

        @Override
        public Object getAction() {
            return adaptee.getAction();
        }

        @Override
        public String getActionName() {
            return adaptee.getActionName();
        }

        @Override
        public ActionConfig getConfig() {
            return adaptee.getConfig();
        }

        @Override
        public void setExecuteResult(boolean executeResult) {
            adaptee.setExecuteResult(executeResult);
        }

        @Override
        public boolean getExecuteResult() {
            return adaptee.getExecuteResult();
        }

        @Override
        public ActionInvocation getInvocation() {
            return ActionInvocation.adapt(adaptee.getInvocation());
        }

        @Override
        public String getNamespace() {
            return adaptee.getNamespace();
        }

        @Override
        public String execute() throws Exception {
            return adaptee.execute();
        }

        @Override
        public String getMethod() {
            return adaptee.getMethod();
        }

        @Override
        public boolean isMethodSpecified() {
            return adaptee.isMethodSpecified();
        }
    }
}
