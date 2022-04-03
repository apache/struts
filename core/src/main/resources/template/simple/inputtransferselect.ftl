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
	<script type="text/javascript" src="<@s.url value="${parameters.staticContentPath}/inputtransferselect.js" encode='false' includeParams='none'/>" <#include "/${parameters.templateDir}/simple/nonce.ftl" /> ></script>
	<#assign temporaryVariable = stack.setValue("#inputtransferselect_js_included", "true") /><#t/>
</#if><#t/>
<table>
<tr>
<td>
<#if parameters.leftTitle??><#t/>
	<label for="leftTitle">${parameters.leftTitle}</label><br />
</#if><#t/>

<input type="text"<#rt/>
 name="${parameters.name!""}_input"<#rt/>
<#if parameters.disabled!false>
 disabled="disabled"<#rt/>
</#if>
<#if parameters.readonly!false>
 readonly="readonly"<#rt/>
</#if>
<#if parameters.tabindex?has_content>
 tabindex="${parameters.tabindex}"<#rt/>
</#if>
<#if parameters.id?has_content>
 id="${parameters.id}_input"<#rt/>
</#if>
<#if parameters.cssClass?has_content>
 class="${parameters.cssClass}"<#rt/>
</#if>
<#if parameters.cssStyle?has_content>
 style="${parameters.cssStyle}"<#rt/>
</#if>
<#if parameters.title?has_content>
 title="${parameters.title}"<#rt/>
</#if>
<#include "/${parameters.templateDir}/${parameters.expandTheme}/scripting-events.ftl" />
<#include "/${parameters.templateDir}/${parameters.expandTheme}/common-attributes.ftl" />
<#include "/${parameters.templateDir}/${parameters.expandTheme}/prefixed-dynamic-attributes.ftl" />
<@prefixedDynamicAttributes prefix="input-"/>
/>


</td>
<td class="tdTransferSelect">
	<#assign addLabel=(parameters.addLabel!"->") /><#t/>
	<input type="button"
		<#if parameters.buttonCssClass?has_content><#t/>
		 class="${parameters.buttonCssClass}"
		</#if><#t/>
		<#if parameters.buttonCssStyle?has_content>
		 style="${parameters.buttonCssStyle}"
		</#if><#t/>
		 value="${addLabel}" onclick="addOption(document.getElementById('${parameters.id}_input'), document.getElementById('${parameters.id}'))" /><br /><br />
	<#t/>
	<#assign removeLabel=(parameters.removeLabel!"<-") /><#t/>
	<input type="button"
  		<#if parameters.buttonCssClass?has_content><#t/>
		 class="${parameters.buttonCssClass}"
		</#if><#t/>
		<#if parameters.buttonCssStyle?has_content>
		 style="${parameters.buttonCssStyle}"
		</#if><#t/>
		 value="${removeLabel}" onclick="removeOptions(document.getElementById('${parameters.id}'))" /><br /><br />
	<#t/>
	<#assign removeAllLabel=(parameters.removeAllLabel!"<<--") /><#t/>
	<input type="button"
	    		<#if parameters.buttonCssClass?has_content><#t/>
		 class="${parameters.buttonCssClass}"
		</#if><#t/>
		<#if parameters.buttonCssStyle?has_content>
		 style="${parameters.buttonCssStyle}"
		</#if><#t/>
		 value="${removeAllLabel}" onclick="removeAllOptions(document.getElementById('${parameters.id}'))" /><br /><br />
</td>
<td>
<#if parameters.rightTitle?has_content><#t/>
	<label for="rightTitle">${parameters.rightTitle}</label><br />
</#if><#t/>
<#global dynamic_attributes_ignore = "input-"/>
<#include "/${parameters.templateDir}/simple/select.ftl" />
<#if parameters.allowUpDown!true>
<input type="button"
<#if parameters.headerKey?has_content>
	onclick="moveOptionDown(document.getElementById('${parameters.id}'), 'key', '${parameters.headerKey}');"
<#else>
	onclick="moveOptionDown(document.getElementById('${parameters.id}'), 'key', '');"
</#if>
<#if parameters.downLabel?has_content>
	value="${parameters.downLabel}"
</#if>
/>
<input type="button"
<#if parameters.headerKey?has_content>
	onclick="moveOptionUp(document.getElementById('${parameters.id}'), 'key', '${parameters.headerKey}');"
<#else>
	onclick="moveOptionUp(document.getElementById('${parameters.id}'), 'key', '');"
</#if>
<#if parameters.upLabel?has_content>
	value="${parameters.upLabel}"
</#if>
/>
</#if>
</td>
</tr>
</table>
