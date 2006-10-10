<#if parameters.optGroupInternalListUiBeanList?exists>
<#assign optGroupInternalListUiBeans=parameters.optGroupInternalListUiBeanList />
<#list optGroupInternalListUiBeans as optGroupInternalListUiBean>
<optgroup 
	<#if optGroupInternalListUiBean.parameters.label?exists>
	label="${optGroupInternalListUiBean.parameters.label}"
	</#if>
	<#if optGroupInternalListUiBean.parameters.disabled?default(false)>
	disabled="disabled"
	</#if>
>

<#list optGroupInternalListUiBean.parameters.list as optGroupBean>
<#assign trash=stack.push(optGroupBean) />
	<#assign tmpKey=stack.findValue(optGroupInternalListUiBean.parameters.listKey) />
	<#assign tmpValue=stack.findValue(optGroupInternalListUiBean.parameters.listValue) />
	<option value="${tmpKey}"
	<#if tag.contains(parameters.nameValue, tmpKey) == true>
	selected="selected"
	</#if>
	>${tmpValue}
	</option>
<#assign trash=stack.pop() />
</#list>
</optgroup>
</#list>
</#if>
