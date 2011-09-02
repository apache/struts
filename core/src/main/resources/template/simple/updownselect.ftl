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
<#if !stack.findValue("#optiontransferselect_js_included")??><#t/>
	<script type="text/javascript" src="<@s.url value="/struts/optiontransferselect.js" encode='false' includeParams='none'/>"></script>
	<#assign temporaryVariable = stack.setValue("#optiontransferselect_js_included", "true") /><#t/>
</#if><#t/>
<table>
<tr><td>
<#include "/${templateDir}/simple/select.ftl" /><#t/>
</td></tr>
<tr><td>
<#if parameters.allowMoveUp?default(true)><#t/>
	<#assign defMoveUpLabel="${parameters.moveUpLabel?default('^')}" /><#t/>
	<#if parameters.headerKey??><#t/>
		&nbsp;<input type="button" value="${defMoveUpLabel}" onclick="moveOptionUp(document.getElementById('${parameters.id}'), 'key', '${parameters.headerKey}');" />&nbsp;
	<#else><#t/>
		&nbsp;<input type="button" value="${defMoveUpLabel}" onclick="moveOptionUp(document.getElementById('${parameters.id}'), 'key', '');" />&nbsp;
	</#if><#t/>
</#if><#t/>
<#if parameters.allowMoveDown?default(true)><#t/>
	<#assign defMoveDownLabel="${parameters.moveDownLabel?default('v')}" /><#t/>
	<#if parameters.headerKey??><#t/>
		&nbsp;<input type="button" value="${defMoveDownLabel}" onclick="moveOptionDown(document.getElementById('${parameters.id}'), 'key', '${parameters.headerKey}');" />&nbsp;
	<#else><#t/>
		&nbsp;<input type="button" value="${defMoveDownLabel}" onclick="moveOptionDown(document.getElementById('${parameters.id}'), 'key', '');" />&nbsp;
	</#if><#t/>
</#if><#t/>
<#if parameters.allowSelectAll?default(true)><#t/>
	<#assign defSelectAllLabel="${parameters.selectAllLabel?default('*')}" /><#t/>
	<#if parameters.headerKey??><#t/>
		&nbsp;<input type="button" value="${defSelectAllLabel}" onclick="selectAllOptionsExceptSome(document.getElementById('${parameters.id}'), 'key', '${parameters.headerKey}');" />&nbsp;
	<#else><#t/>
		&nbsp;<input type="button" value="${defSelectAllLabel}" onclick="selectAllOptions(document.getElementById('${parameters.id}'), 'key', '');" />&nbsp;
	</#if><#t/>
</#if><#t/>
</td></tr>
</table>