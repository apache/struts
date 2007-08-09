
<table class="table">
<tr class="tableHeader">
	<td class="tableSenderColumn">Sender</td>
	<td class="tableDateColumn">Date</td>
	<td class="tableMessageColumn">Message</td>
</tr>
<@s.iterator id="message" value="%{messagesAvailableInRoom}" status="stat">
<tr class="tableContent">
	<#if stat.odd>
	<td class="tableSenderColumnOdd"> 
	<#else>
	<td clas="tableSenderColumnEven">
	</#if>
		<@s.property value="%{#message.creator.name}" />
	</td>
	<#if stat.odd>
	<td class="tableDateColumnOdd">
	<#else>
	<td class="tableDateColumnEven">
	</#if>
		<@s.property value="%{#message.creationDate}" />
	</td>
	<#if stat.odd>
	<td class="tableMessageColumnOdd">
	<#else>
	<td class="tableMessageColumnEven">
	</#if>
		<@s.property value="%{#message.message}" />
	</td>
</tr>
</@s.iterator>
</table>
