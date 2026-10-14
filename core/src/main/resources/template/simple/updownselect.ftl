<#--
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
-->
<#if !stack.findValue("#optiontransferselect_js_included")??><#t/>
	<@s.script src="${base}${attributes.staticContentPath}/optiontransferselect.js" /><#t/>
	<#assign temporaryVariable = stack.setValue("#optiontransferselect_js_included", "true") /><#t/>
</#if><#t/>
<table>
<tr><td>
<#include "/${templateDir}/simple/select.ftl" /><#t/>
</td></tr>
<tr><td>
<#if attributes.allowMoveUp!true><#t/>
	<#assign defMoveUpLabel="${attributes.moveUpLabel!'^'}" /><#t/>
	<#if attributes.headerKey??><#t/>
		&nbsp;<input type="button" value="${defMoveUpLabel}" data-struts-updownselect="moveUp" data-struts-target="${attributes.id}" data-struts-header-key="${attributes.headerKey}" />&nbsp;
	<#else><#t/>
		&nbsp;<input type="button" value="${defMoveUpLabel}" data-struts-updownselect="moveUp" data-struts-target="${attributes.id}" />&nbsp;
	</#if><#t/>
</#if><#t/>
<#if attributes.allowMoveDown!true><#t/>
	<#assign defMoveDownLabel="${attributes.moveDownLabel!'v'}" /><#t/>
	<#if attributes.headerKey??><#t/>
		&nbsp;<input type="button" value="${defMoveDownLabel}" data-struts-updownselect="moveDown" data-struts-target="${attributes.id}" data-struts-header-key="${attributes.headerKey}" />&nbsp;
	<#else><#t/>
		&nbsp;<input type="button" value="${defMoveDownLabel}" data-struts-updownselect="moveDown" data-struts-target="${attributes.id}" />&nbsp;
	</#if><#t/>
</#if><#t/>
<#if attributes.allowSelectAll!true><#t/>
	<#assign defSelectAllLabel="${attributes.selectAllLabel!'*'}" /><#t/>
	<#if attributes.headerKey??><#t/>
		&nbsp;<input type="button" value="${defSelectAllLabel}" data-struts-updownselect="selectAll" data-struts-target="${attributes.id}" data-struts-header-key="${attributes.headerKey}" />&nbsp;
	<#else><#t/>
		&nbsp;<input type="button" value="${defSelectAllLabel}" data-struts-updownselect="selectAll" data-struts-target="${attributes.id}" />&nbsp;
	</#if><#t/>
</#if><#t/>
</td></tr>
</table>
