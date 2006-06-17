
<table class="table">
<tr class="tableHeader">
	<td class="tableSenderColumn">Sender</td>
	<td class="tableDateColumn">Date</td>
	<td class="tableMessageColumn">Message</td>
</tr>
<@saf.iterator id="message" value="%{messagesAvailableInRoom}" status="stat">
<tr class="tableContent">
	<#if stat.odd>
	<td class="tableSenderColumnOdd"> 
	<#else>
	<td clas="tableSenderColumnEven">
	</#if>
		<@saf.property value="%{#message.creator.name}" />
	</td>
	<#if stat.odd>
	<td class="tableDateColumnOdd">
	<#else>
	<td class="tableDateColumnEven">
	</#if>
		<@saf.property value="%{#message.creationDate}" />
	</td>
	<#if stat.odd>
	<td class="tableMessageColumnOdd">
	<#else>
	<td class="tableMessageColumnEven">
	</#if>
		<@saf.property value="%{#message.message}" />
	</td>
</tr>
</@saf.iterator>
</table>
