<#include "tigris-macros.ftl">
<@startPage pageTitle="Actions in namespace"/>
<h3>Actions in <#if namespace == ""> default namespace <#else> ${namespace} </#if></h3>
<table>
	<tr>
		<td>
			<ul>
			<#list actionNames as name>
                <@s.url id="showConfig" action="showConfig" includeParams="none">
                    <@s.param name="namespace">${namespace}</@s.param>
                    <@s.param name="actionName">${name}</@s.param>
                </@s.url>
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
