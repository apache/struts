<#if (actionMessages?exists && actionMessages?size > 0)>
	<ul>
		<#list actionMessages as message>
			<li><span class="actionMessage">${message}</span></li>
		</#list>
	</ul>
</#if>
