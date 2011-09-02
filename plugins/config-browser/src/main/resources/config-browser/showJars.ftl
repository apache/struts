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
<@startPage pageTitle="Jars and Struts Plugins"/>
<h3>Jars and Struts Plugins</h3>

<table width="100%">
	<tr>
		<th>Artifact ID</th>
		<th>Group ID</th>
		<th>Version</th>
	</tr>
	<#list jarPoms as pom>
		<tr <#if pom_index%2 gt 0>class="b"<#else>class="a"</#if>>
		<td>${pom.artifactId}</td>
		<td>${pom.groupId}</td>
		<td>${pom.version}</td>
		</tr>
	</#list>
</table>

<br />
<h4> Discovered plugin XML</h4>
<ul>
	<#list pluginsLoaded as url>
		<li>${url}</li>
	</#list>
</ul>

<@endPage />
