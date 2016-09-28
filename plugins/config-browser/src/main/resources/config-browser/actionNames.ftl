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
<#include "tigris-macros.ftl">
<@startPage pageTitle="Actions in namespace"/>
<h3>Actions in <#if namespace == ""> default namespace <#else> ${namespace?html} </#if></h3>
<table>
	<tr>
		<td>
			<ul>
			<#list actionNames as name>
                <@s.url id="showConfig" action="showConfig" includeParams="none">
                    <@s.param name="namespace">${namespace}</@s.param>
                    <@s.param name="actionName">${name}</@s.param>
                </@s.url>
                <li><a href="${showConfig}">${name}</a></li>
			</#list>
			</ul>
		</td>
		<td>
			<!-- Placeholder for namespace graph -->
		</td>
	</tr>
</table>
<@endPage />
