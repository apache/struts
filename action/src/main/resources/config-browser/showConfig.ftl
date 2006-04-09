<#include "tigris-macros.ftl"/>
<@startPage pageTitle="Action information"/>
<h3>Action information - ${actionName}</h3>


<table>
	<tr><td>Action name:</td><td>${actionName}</td></tr>
	<tr><td>Namespace:</td><td> ${namespace}</td></tr>
	<tr><td>Action class:</td><td> ${config.className}</td></tr>
	<tr><td>Action method:</td><td> <#if config.methodName?exists>${config.methodName}</#if></td></tr>
	<tr><td>Parameters:</td><td> <#list config.params?keys as p>
		${p}
	</#list></td></tr>

	<tr><td>Default location:</td><td> <a href="${base}${namespace}/${actionName}.${extension}">
		${base}${namespace}/${actionName}.${extension}
	</a>
	</td></tr>
</table>

<!-- URLTag is faulty -->
<@ww.url id="url" action="showConfig" includeParams="none">
    <@ww.param name="namespace">${namespace}</@ww.param>
    <@ww.param name="actionName">${actionName}</@ww.param>
</@ww.url>
<#assign url = url + "&amp;detailView=">
<!-- Set all to false -->
<#assign detailsSelected = false>
<#assign exceptionsSelected = false>
<#assign interceptorsSelected = false>
<#assign propertiesSelected = false>
<#assign validatorsSelected = false>
<!-- Set selected to true -->

<#if detailView == "results">
	<#assign detailsSelected = true>
<#elseif detailView == "exceptions">
	<#assign exceptionsSelected = true>
<#elseif detailView == "interceptors">
	<#assign interceptorsSelected = true>
<#elseif detailView == "properties">
	<#assign propertiesSelected = true>
<#else>
	<#assign validatorsSelected = true>
</#if>

<@startTabs/>
	<#call tab name="Results" url="${url}results" isSelected="${detailsSelected?string}"/>
	<#call tab name="Exception Mappings" url="${url}exceptions" isSelected="${exceptionsSelected?string}"/>
	<#call tab name="Interceptors" url="${url}interceptors" isSelected="${interceptorsSelected?string}"/>
	<#call tab name="Properties" url="${url}properties" isSelected="${propertiesSelected?string}"/>
	<#call tab name="Validators" url="${url}validators" isSelected="${validatorsSelected?string}"/>
<@endTabs/>

<#if detailsSelected>	<!-- Action results -->
    <table width="100%">
    	<tr><th>Name</th><th>Type</th><th>Parameters</th></tr>
    	<#assign count=config.results?size>
    	<#list config.results.values() as r>
    		<tr <#if r_index%2 gt 0>class="b"<#else>class="a"</#if>>
    		<td>${r.name}</td>
    		<td>${r.className}</td>
    		<td>
    		<#list r.params.keySet() as p>
    			${p} = ${r.params[p]}<br>
    		</#list>
    		</td>
    		</tr>
    	</#list>
    </table>

<#elseif exceptionsSelected>	<!-- Action exception mappings -->
    <table width="100%">
        <tr><th>Name</th><th>Exception Class Name</th><th>Result</th><th>Parameters</th></tr>
        <#list config.exceptionMappings as e>
        	<tr <#if e_index%2 gt 0>class="b"<#else>class="a"</#if>>
    			<td>${e.name}</td>
    			<td>${e.exceptionClassName}</td>
    			<td>${e.result}</td>
    		    <td>
    		        <#list e.params.keySet() as p>
    			        ${p} = ${e.params[p]}<br>
    		        </#list>
    		    </td>
    		</tr>
    	</#list>
    </table>

<#elseif interceptorsSelected>	<!-- Action interceptors -->
    <table width="100%">
        <tr><th>Name</th><th>Type</th></tr>
        <#list config.interceptors as i>
        	<tr <#if i_index%2 gt 0>class="b"<#else>class="a"</#if>>
    			<td>${action.stripPackage(i.interceptor.class)}</td>
    			<td>${i.interceptor.class.name}</td>
    		</tr>
    	</#list>
    </table>
<#elseif propertiesSelected>
	<table width="100%">
        <tr><th>Name</th><th>Type</th></tr>
        <#list properties as prop>
        	<tr <#if prop_index%2 gt 0>class="b"<#else>class="a"</#if>>
    			<td>${prop.name}</td>
    			<td>${prop.propertyType.name}</td>
    		</tr>
    	</#list>
    </table>
<#else>
    <@ww.action name="showValidators" executeResult="true">
        <@ww.param name="clazz" value="'${config.className}'"/>
        <@ww.param name="context" value="'${namespace}'"/>
    </@ww.action>
</#if>

<#call endPage>
