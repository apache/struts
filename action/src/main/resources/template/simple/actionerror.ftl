<#if (actionErrors?exists && actionErrors?size > 0)>
	<ul>
	<#list actionErrors as error>
		<li><span class="errorMessage">${error}</span></li>
	</#list>
	</ul>
</#if>
