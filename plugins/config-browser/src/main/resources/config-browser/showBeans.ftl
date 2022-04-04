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
<@startPage pageTitle="Struts Beans"/>
<h3>Struts Beans</h3>

<table width="100%">
	<tr>
		<th>Type</th>
		<th>Alias</th>
		<th>Implementation</th>
		<th>Constant Name</th>
	</tr>
	<#list beans.entrySet() as entry>
		<#list entry.value as b>
			<tr <#if b_index==0>class="a"<#else>class="b"</#if>>
			<td><#if b_index==0>${entry.key}</#if></td>
			<td>${b.alias}</td>
			<td>${b.impl}</td>
			<td><#if b_index==0>${b.constant}</#if></td>
			</tr>
		</#list>
	</#list>
</table>

<@endPage />
