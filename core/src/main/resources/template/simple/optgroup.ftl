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
<#if parameters.optGroupInternalListUiBeanList??>
<#assign optGroupInternalListUiBeans=parameters.optGroupInternalListUiBeanList />
<#list optGroupInternalListUiBeans as optGroupInternalListUiBean>
<optgroup<#rt>
	<#if optGroupInternalListUiBean.parameters.label?has_content>
 label="${optGroupInternalListUiBean.parameters.label}"<#rt>
	</#if>
	<#if optGroupInternalListUiBean.parameters.disabled!false>
 disabled="disabled"<#rt>
	</#if>
<#include "/${parameters.templateDir}/${parameters.expandTheme}/dynamic-attributes.ftl" />
>
<#list optGroupInternalListUiBean.parameters.list as optGroupBean>
<#assign trash=stack.push(optGroupBean) />
	<#assign tmpKey=stack.findValue(optGroupInternalListUiBean.parameters.listKey) />
	<#assign tmpValue=stack.findValue(optGroupInternalListUiBean.parameters.listValue) />
	<#assign tmpKeyStr = tmpKey.toString() />
	<#assign optGroupItemCssClass = ''/>
	<#if optGroupInternalListUiBean.parameters.listCssClass??>
		<#assign optGroupItemCssClass= stack.findString(optGroupInternalListUiBean.parameters.listCssClass)!''/>
	</#if>
	<#assign optGroupItemCssStyle = ''/>
	<#if optGroupInternalListUiBean.parameters.listCssStyle??>
		<#assign optGroupItemCssStyle= stack.findString(optGroupInternalListUiBean.parameters.listCssStyle)!''/>
	</#if>
	<#assign optGroupItemTitle = ''/>
	<#if optGroupInternalListUiBean.parameters.listTitle??>
		<#assign optGroupItemTitle= stack.findString(optGroupInternalListUiBean.parameters.listTitle)!''/>
	</#if>
	<option value="${tmpKeyStr}"<#rt>
	<#if tag.contains(parameters.nameValue, tmpKey) == true>
	selected="selected"<#rt>
	</#if>
	<#if optGroupItemCssClass?has_content>
	class="${optGroupItemCssClass}"<#rt/>
	</#if>
	<#if optGroupItemCssStyle?has_content>
	style="${optGroupItemCssStyle}"<#rt/>
	</#if>
	<#if optGroupItemTitle?has_content>
	title="${optGroupItemTitle}"<#rt/>
	</#if>
	>${tmpValue}<#t>
	</option><#lt>
<#assign trash=stack.pop() />
</#list>
</optgroup>
</#list>
</#if>