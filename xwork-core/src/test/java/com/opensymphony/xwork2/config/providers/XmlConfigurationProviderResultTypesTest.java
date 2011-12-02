/*
 * Copyright 2002-2003,2009 The Apache Software Foundation.
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
package com.opensymphony.xwork2.config.providers;

import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.config.entities.ResultTypeConfig;
import com.opensymphony.xwork2.mock.MockResult;

import java.util.Map;


/**
 * Test XmlConfigurationProvider's <result-types> ... </result-types>
 * 
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class XmlConfigurationProviderResultTypesTest extends ConfigurationTestBase {

	public void testPlainResultTypesParams() throws Exception {
		ConfigurationProvider configurationProvider = buildConfigurationProvider("com/opensymphony/xwork2/config/providers/xwork-test-result-types.xml");
		
		PackageConfig packageConfig = configuration.getPackageConfig("xworkResultTypesTestPackage1");
		Map resultTypesConfigMap = packageConfig.getResultTypeConfigs();
		
		assertEquals(resultTypesConfigMap.size(), 2);
		assertTrue(resultTypesConfigMap.containsKey("result1"));
		assertTrue(resultTypesConfigMap.containsKey("result2"));
		assertFalse(resultTypesConfigMap.containsKey("result3"));
		
		ResultTypeConfig result1ResultTypeConfig = (ResultTypeConfig) resultTypesConfigMap.get("result1");
		Map result1ParamsMap = result1ResultTypeConfig.getParams();
		ResultTypeConfig result2ResultTypeConfig = (ResultTypeConfig) resultTypesConfigMap.get("result2");
		Map result2ParamsMap = result2ResultTypeConfig.getParams();
		
		assertEquals(result1ResultTypeConfig.getName(), "result1");
		assertEquals(result1ResultTypeConfig.getClazz(), MockResult.class.getName());
		assertEquals(result2ResultTypeConfig.getName(), "result2");
		assertEquals(result2ResultTypeConfig.getClazz(), MockResult.class.getName());
		assertEquals(result1ParamsMap.size(), 3);
		assertEquals(result2ParamsMap.size(), 2);
		assertTrue(result1ParamsMap.containsKey("param1"));
		assertTrue(result1ParamsMap.containsKey("param2"));
		assertTrue(result1ParamsMap.containsKey("param3"));
		assertFalse(result1ParamsMap.containsKey("param4"));
		assertTrue(result2ParamsMap.containsKey("paramA"));
		assertTrue(result2ParamsMap.containsKey("paramB"));
		assertFalse(result2ParamsMap.containsKey("paramC"));
		assertEquals(result1ParamsMap.get("param1"), "value1");
		assertEquals(result1ParamsMap.get("param2"), "value2");
		assertEquals(result1ParamsMap.get("param3"), "value3");
		assertEquals(result2ParamsMap.get("paramA"), "valueA");
		assertEquals(result2ParamsMap.get("paramB"), "valueB");
	}
	
	public void testInheritedResultTypesParams() throws Exception {
		ConfigurationProvider configurationProvider = buildConfigurationProvider("com/opensymphony/xwork2/config/providers/xwork-test-result-types.xml");
		
		PackageConfig packageConfig = configuration.getPackageConfig("xworkResultTypesTestPackage2");
		Map actionConfigMap = packageConfig.getActionConfigs();
		
		
		ActionConfig action1ActionConfig = (ActionConfig) actionConfigMap.get("action1");
		ActionConfig action2ActionConfig = (ActionConfig) actionConfigMap.get("action2");
		
		ResultConfig action1Result = (ResultConfig) action1ActionConfig.getResults().get("success");
		ResultConfig action2Result = (ResultConfig) action2ActionConfig.getResults().get("success");
		
		assertEquals(action1Result.getName(), "success");
		assertEquals(action1Result.getClassName(), "com.opensymphony.xwork2.mock.MockResult");
		assertEquals(action1Result.getName(), "success");
		assertEquals(action1Result.getClassName(), "com.opensymphony.xwork2.mock.MockResult");
		
		Map action1ResultMap = action1Result.getParams();
		Map action2ResultMap = action2Result.getParams();
		
		assertEquals(action1ResultMap.size(), 5);
		assertTrue(action1ResultMap.containsKey("param1"));
		assertTrue(action1ResultMap.containsKey("param2"));
		assertTrue(action1ResultMap.containsKey("param3"));
		assertTrue(action1ResultMap.containsKey("param10"));
		assertTrue(action1ResultMap.containsKey("param11"));
		assertFalse(action1ResultMap.containsKey("param12"));
		assertEquals(action1ResultMap.get("param1"), "newValue1");
		assertEquals(action1ResultMap.get("param2"), "value2");
		assertEquals(action1ResultMap.get("param3"), "newValue3");
		assertEquals(action1ResultMap.get("param10"), "value10");
		assertEquals(action1ResultMap.get("param11"), "value11");
		
		assertEquals(action2ResultMap.size(), 3);
		assertTrue(action2ResultMap.containsKey("paramA"));
		assertTrue(action2ResultMap.containsKey("paramB"));
		assertTrue(action2ResultMap.containsKey("paramZ"));
		assertFalse(action2ResultMap.containsKey("paramY"));
		assertEquals(action2ResultMap.get("paramA"), "valueA");
		assertEquals(action2ResultMap.get("paramB"), "newValueB");
		assertEquals(action2ResultMap.get("paramZ"), "valueZ");
		
		
	}
}


