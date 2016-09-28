<#--
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
-->
<#include "tigris-macros.ftl"/>
<@startPage pageTitle="Action information"/>
<h3>Action information - ${actionName}</h3>


<table>
	<tr><td>Action name:</td><td>${actionName}</td></tr>
	<tr><td>Namespace:</td><td> ${namespace}</td></tr>
	<tr><td>Action class:</td><td> ${config.className}</td></tr>
	<tr><td>Action method:</td><td> <#if config.methodName??>${config.methodName}</#if></td></tr>
	<tr><td>Parameters:</td><td> <#list config.params?keys as p>
		${p}
	</#list></td></tr>

	<tr><td>Default location:</td><td> <a href="${base}${namespace}/${actionName}<#if extension != ''>.${extension}</#if>">
		${base}${namespace}/${actionName}<#if extension != "">.${extension}</#if>
	</a>
	</td></tr>
</table>

<!-- URLTag is faulty -->
<@s.url var="url" action="showConfig" includeParams="none">
    <@s.param name="namespace">${namespace?html}</@s.param>
    <@s.param name="actionName">${actionName?html}</@s.param>
</@s.url>
<#assign url = url + "&amp;detailView=">
<!-- Set all to false -->
<#assign detailsSelected = false>
<#assign exceptionsSelected = false>
<#assign interceptorsSelected = false>
<#assign propertiesSelected = false>
<#assign validatorsSelected = false>
<!-- Set selected to true -->

<#if detailView == "results">
	<#assign detailsSelected = true>
<#elseif detailView == "exceptions">
	<#assign exceptionsSelected = true>
<#elseif detailView == "interceptors">
	<#assign interceptorsSelected = true>
<#elseif detailView == "properties">
	<#assign propertiesSelected = true>
<#else>
	<#assign validatorsSelected = true>
</#if>

<@startTabs/>
	<#call tab name="Results" url="${url}results" isSelected="${detailsSelected?string}"/>
	<#call tab name="Exception Mappings" url="${url}exceptions" isSelected="${exceptionsSelected?string}"/>
	<#call tab name="Interceptors" url="${url}interceptors" isSelected="${interceptorsSelected?string}"/>
	<#call tab name="Properties" url="${url}properties" isSelected="${propertiesSelected?string}"/>
	<#call tab name="Validators" url="${url}validators" isSelected="${validatorsSelected?string}"/>
<@endTabs/>

<#if detailsSelected>	<!-- Action results -->
    <table width="100%">
    	<tr><th>Name</th><th>Type</th><th>Parameters</th></tr>
    	<#assign count=config.results?size>
    	<#list config.results.values() as r>
    		<tr <#if r_index%2 gt 0>class="b"<#else>class="a"</#if>>
    		<td>${r.name}</td>
    		<td>${r.className}</td>
    		<td>
    		<#list r.params.keySet() as p>
    			${p} = ${r.params[p]}<br>
    		</#list>
    		</td>
    		</tr>
    	</#list>
    </table>

<#elseif exceptionsSelected>	<!-- Action exception mappings -->
    <table width="100%">
        <tr><th>Name</th><th>Exception Class Name</th><th>Result</th><th>Parameters</th></tr>
        <#list config.exceptionMappings as e>
        	<tr <#if e_index%2 gt 0>class="b"<#else>class="a"</#if>>
    			<td>${e.name}</td>
    			<td>${e.exceptionClassName}</td>
    			<td>${e.result}</td>
    		    <td>
    		        <#list e.params.keySet() as p>
    			        ${p} = ${e.params[p]}<br>
    		        </#list>
    		    </td>
    		</tr>
    	</#list>
    </table>

<#elseif interceptorsSelected>	<!-- Action interceptors -->
    <table width="100%">
        <tr><th>Name</th><th>Type</th></tr>
        <#list config.interceptors as i>
        	<tr <#if i_index%2 gt 0>class="b"<#else>class="a"</#if>>
    			<td>${action.stripPackage(i.interceptor.class)}</td>
    			<td>${i.interceptor.class.name}</td>
    		</tr>
    	</#list>
    </table>
<#elseif propertiesSelected>
	<table width="100%">
        <tr><th>Name</th><th>Type</th></tr>
        <#list properties as prop>
        	<tr <#if prop_index%2 gt 0>class="b"<#else>class="a"</#if>>
    			<td>${prop.name}</td>
    			<td>${prop.propertyType.name}</td>
    		</tr>
    	</#list>
    </table>
<#else>
    <@s.action name="showValidators" executeResult="true">
        <@s.param name="clazz" value="'${config.className}'"/>
        <@s.param name="context" value="'${namespace}'"/>
    </@s.action>
</#if>

<#call endPage>
