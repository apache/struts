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
<#if !stack.findValue("#inputtransferselect_js_included")??><#t/>
	<@s.script src="${base}${attributes.staticContentPath}/inputtransferselect.js"/>
	<#assign temporaryVariable = stack.setValue("#inputtransferselect_js_included", "true") /><#t/>
</#if><#t/>
<table>
<tr>
<td>
<#if attributes.leftTitle??><#t/>
	<label for="leftTitle">${attributes.leftTitle}</label><br />
</#if><#t/>

<input type="text"<#rt/>
 name="${attributes.name!""}_input"<#rt/>
<#if attributes.disabled!false>
 disabled="disabled"<#rt/>
</#if>
<#if attributes.readonly!false>
 readonly="readonly"<#rt/>
</#if>
<#if attributes.tabindex?has_content>
 tabindex="${attributes.tabindex}"<#rt/>
</#if>
<#if attributes.id?has_content>
 id="${attributes.id}_input"<#rt/>
</#if>
<#if attributes.cssClass?has_content>
 class="${attributes.cssClass}"<#rt/>
</#if>
<#if attributes.cssStyle?has_content>
 style="${attributes.cssStyle}"<#rt/>
</#if>
<#if attributes.title?has_content>
 title="${attributes.title}"<#rt/>
</#if>
<#include "/${attributes.templateDir}/${attributes.expandTheme}/scripting-events.ftl" />
<#include "/${attributes.templateDir}/${attributes.expandTheme}/common-attributes.ftl" />
<#include "/${attributes.templateDir}/${attributes.expandTheme}/prefixed-dynamic-attributes.ftl" />
<@prefixedDynamicAttributes prefix="input-"/>
/>


</td>
<td class="tdTransferSelect">
	<#assign addLabel=(attributes.addLabel!"->") /><#t/>
	<input type="button"
		<#if attributes.buttonCssClass?has_content><#t/>
		 class="${attributes.buttonCssClass}"
		</#if><#t/>
		<#if attributes.buttonCssStyle?has_content>
		 style="${attributes.buttonCssStyle}"
		</#if><#t/>
		 value="${addLabel}" onclick="addOption(document.getElementById('${attributes.id}_input'), document.getElementById('${attributes.id}'))" /><br /><br />
	<#t/>
	<#assign removeLabel=(attributes.removeLabel!"<-") /><#t/>
	<input type="button"
  		<#if attributes.buttonCssClass?has_content><#t/>
		 class="${attributes.buttonCssClass}"
		</#if><#t/>
		<#if attributes.buttonCssStyle?has_content>
		 style="${attributes.buttonCssStyle}"
		</#if><#t/>
		 value="${removeLabel}" onclick="removeOptions(document.getElementById('${attributes.id}'))" /><br /><br />
	<#t/>
	<#assign removeAllLabel=(attributes.removeAllLabel!"<<--") /><#t/>
	<input type="button"
	    		<#if attributes.buttonCssClass?has_content><#t/>
		 class="${attributes.buttonCssClass}"
		</#if><#t/>
		<#if attributes.buttonCssStyle?has_content>
		 style="${attributes.buttonCssStyle}"
		</#if><#t/>
		 value="${removeAllLabel}" onclick="removeAllOptions(document.getElementById('${attributes.id}'))" /><br /><br />
</td>
<td>
<#if attributes.rightTitle?has_content><#t/>
	<label for="rightTitle">${attributes.rightTitle}</label><br />
</#if><#t/>
<#global dynamic_attributes_ignore = "input-"/>
<#include "/${attributes.templateDir}/simple/select.ftl" />
<#if attributes.allowUpDown!true>
<input type="button"
<#if attributes.headerKey?has_content>
	onclick="moveOptionDown(document.getElementById('${attributes.id}'), 'key', '${attributes.headerKey}');"
<#else>
	onclick="moveOptionDown(document.getElementById('${attributes.id}'), 'key', '');"
</#if>
<#if attributes.downLabel?has_content>
	value="${attributes.downLabel}"
</#if>
/>
<input type="button"
<#if attributes.headerKey?has_content>
	onclick="moveOptionUp(document.getElementById('${attributes.id}'), 'key', '${attributes.headerKey}');"
<#else>
	onclick="moveOptionUp(document.getElementById('${attributes.id}'), 'key', '');"
</#if>
<#if attributes.upLabel?has_content>
	value="${attributes.upLabel}"
</#if>
/>
</#if>
</td>
</tr>
</table>
