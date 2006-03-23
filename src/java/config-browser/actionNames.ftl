<#include "tigris-macros.ftl">
<@startPage pageTitle="Actions in namespace"/>
<h3>Actions in <#if namespace == ""> default namespace <#else> ${namespace} </#if></h3>
<table>
	<tr>
		<td>
			<ul>
			<#list actionNames as name>
                <@ww.url id="showConfig" action="showConfig" includeParams="none">
                    <@ww.param name="namespace">${namespace}</@ww.param>
                    <@ww.param name="actionName">${name}</@ww.param>
                </@ww.url>
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
