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
package org.apache.struts2.factory;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.factory.ResultFactory;
import org.apache.struts2.StrutsInternalTestCase;
import com.opensymphony.xwork2.result.ParamNameAwareResult;

import java.util.HashMap;
import java.util.Map;

public class StrutsResultFactoryTest extends StrutsInternalTestCase {

    public void testAcceptParams() throws Exception {
        // given
        initDispatcherWithConfigs("struts-default.xml");
        StrutsResultFactory builder = (StrutsResultFactory) container.getInstance(ResultFactory.class);

        Map<String, String> params = new HashMap<String, String>();
        params.put("accept", "ok");
        params.put("reject", "bad");
        ResultConfig config = new ResultConfig.Builder("struts", MyResult.class.getName()).addParams(params).build();
        Map<String, Object> context = new HashMap<String, Object>();

        // when
        Result result = builder.buildResult(config, context);

        // then
        assertEquals("ok", ((MyResult)result).getAccept());
        assertEquals("ok", ((MyResult)result).getReject());
    }

    public void testUseCustomResultBuilder() throws Exception {
        // given
        initDispatcherWithConfigs("struts-default.xml,struts-object-factory-result-builder.xml");

        // when
        ResultFactory actual = container.getInstance(ResultFactory.class);

        // then
        assertTrue(actual instanceof MyResultFactory);
    }

    public static class MyResult implements Result, ParamNameAwareResult {

        private String accept;
        private String reject = "ok";

        public boolean acceptableParameterName(String name, String value) {
            return "accept".equals(name);
        }

        public void execute(ActionInvocation invocation) throws Exception {

        }

        public String getAccept() {
            return accept;
        }

        public void setAccept(String accept) {
            this.accept = accept;
        }

        public String getReject() {
            return reject;
        }

        public void setReject(String reject) {
            this.reject = reject;
        }
    }

}
