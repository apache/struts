<#if !stack.findValue("#inputtransferselect_js_included")?exists><#t/>
	<script type="text/javascript" src="<@s.url value="/struts/inputtransferselect.js" encode='false' includeParams='none'/>"></script>
	<#assign temporaryVariable = stack.setValue("#inputtransferselect_js_included", "true") /><#t/>
</#if><#t/>
<table border="0">
<tr>
<td>
<#if parameters.leftTitle?exists><#t/>
	<label for="leftTitle">${parameters.leftTitle}</label><br />
</#if><#t/>


<input type="text"<#rt/>
 name="${parameters.name?default("")?html}_input"<#rt/>
<#if parameters.disabled?default(false)>
 disabled="disabled"<#rt/>
</#if>
<#if parameters.readonly?default(false)>
 readonly="readonly"<#rt/>
</#if>
<#if parameters.tabindex?exists>
 tabindex="${parameters.tabindex?html}"<#rt/>
</#if>
<#if parameters.id?exists>
 id="${parameters.id?html}_input"<#rt/>
</#if>
<#if parameters.cssClass?exists>
 class="${parameters.cssClass?html}"<#rt/>
</#if>
<#if parameters.cssStyle?exists>
 style="${parameters.cssStyle?html}"<#rt/>
</#if>
<#if parameters.title?exists>
 title="${parameters.title?html}"<#rt/>
</#if>
<#include "/${parameters.templateDir}/simple/scripting-events.ftl" />
<#include "/${parameters.templateDir}/simple/common-attributes.ftl" />
/>


</td>
<td valign="middle" align="center">
	<#assign addLabel=parameters.addLabel?default("->")?html /><#t/>
	<input type="button"
		<#if parameters.buttonCssClass?exists><#t/>
		 class="${parameters.buttonCssClass?html}"
		</#if><#t/>
		<#if parameters.buttonCssStyle?exists>
		 style="${parameters.buttonCssStyle?html}"
		</#if><#t/>
		 value="${addLabel}" onclick="addOption(document.getElementById('${parameters.id?html}_input'), document.getElementById('${parameters.id?html}'))" /><br /><br />
	<#t/>
	<#assign removeLabel=parameters.removeLabel?default("<-")?html /><#t/>
	<input type="button"
  		<#if parameters.buttonCssClass?exists><#t/>
		 class="${parameters.buttonCssClass?html}"
		</#if><#t/>
		<#if parameters.buttonCssStyle?exists>
		 style="${parameters.buttonCssStyle?html}"
		</#if><#t/>
		 value="${removeLabel}" onclick="removeOptions(document.getElementById('${parameters.id?html}'))" /><br /><br />
	<#t/>
	<#assign removeAllLabel=parameters.removeAllLabel?default("<<--")?html /><#t/>
	<input type="button"
	    		<#if parameters.buttonCssClass?exists><#t/>
		 class="${parameters.buttonCssClass?html}"
		</#if><#t/>
		<#if parameters.buttonCssStyle?exists>
		 style="${parameters.buttonCssStyle?html}"
		</#if><#t/>
		 value="${removeAllLabel}" onclick="removeAllOptions(document.getElementById('${parameters.id?html}'))" /><br /><br />
</td>
<td>
<#if parameters.rightTitle?exists><#t/>
	<label for="rightTitle">${parameters.rightTitle}</label><br />
</#if><#t/>
<#include "/${parameters.templateDir}/simple/select.ftl" />
<#if parameters.allowUpDown?default(true)>
<input type="button" 
<#if parameters.headerKey?exists>
	onclick="moveOptionDown(document.getElementById('${parameters.id}'), 'key', '${parameters.headerKey}');"
<#else>
	onclick="moveOptionDown(document.getElementById('${parameters.id}'), 'key', '');"
</#if>
<#if parameters.downLabel?exists>
	value="${parameters.downLabel?html}"
</#if>
/>
<input type="button" 
<#if parameters.headerKey?exists>
	onclick="moveOptionUp(document.getElementById('${parameters.id}'), 'key', '${parameters.headerKey}');"
<#else>
	onclick="moveOptionUp(document.getElementById('${parameters.id}'), 'key', '');"
</#if>
<#if parameters.upLabel?exists>
	value="${parameters.upLabel?html}"
</#if>
/>
</#if>
</td>
</tr>
</table>

