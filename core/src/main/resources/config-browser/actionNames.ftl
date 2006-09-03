<#include "tigris-macros.ftl">
<@startPage pageTitle="Actions in namespace"/>
<h3>Actions in <#if namespace == ""> default namespace <#else> ${namespace} </#if></h3>
<table>
	<tr>
		<td>
			<ul>
			<#list actionNames as name>
                <@saf.url id="showConfig" action="showConfig" includeParams="none">
                    <@saf.param name="namespace">${namespace}</@saf.param>
                    <@saf.param name="actionName">${name}</@saf.param>
                </@saf.url>
                <li><a href="${showConfig}">${name}</a></li>
			</#list>
			</ul>
		</td>
		<td>
			<!-- Placeholder for namespace graph -->
		</td>
	</tr>
</table>
<@endPage />
