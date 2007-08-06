<#--
/*
 * $Id: pom.xml 559206 2007-07-24 21:01:18Z apetrelli $
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

<table class="table">
<tr class="tableHeader">
	<td>Operation</td>
	<td>Name</td>
	<td>Description</td>
	<td>Date Created</td>
</tr>
<@s.iterator id="room" value="%{availableRooms}" status="stat">
<tr class="tableContent">
	<#if stat.isOdd()>
	<td class="tableOperationColumnOdd">
	<#else>
	<td class="tableOperationColumnEven">
	</#if>
	<@s.url id="url" action="enterRoom" namespace="/chat">
		<@s.param name="roomName" value="%{#room.name}" />
	</@s.url>
	<@s.a href="%{url}">Enter</@s.a>
	</td>
	<#if stat.odd>
	<td class="tableNameColumnOdd">
	<#else>
	<td class="tableNameColumnEven">
	</#if>
	<@s.property value="%{#room.name}" />
	</td>
	<#if stat.odd>
	<td class="tableDescriptionColumnOdd">
	<#else>
	<td class="tableDescriptionColumnEven">
	</#if>
	<@s.property value="%{#room.description}" />
	</td>
	<#if stat.odd>
	<td class="tableDateCreatedColumnOdd">
	<#else>
	<td class="tableDateCreateColumnEven">
	</#if>
	<@s.property value="%{#room.creationDate}" />
	</td>
</tr>
</@s.iterator>
</table>
