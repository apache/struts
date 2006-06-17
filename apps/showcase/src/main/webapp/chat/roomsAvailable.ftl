
<table class="table">
<tr class="tableHeader">
	<td>Operation</td>
	<td>Name</td>
	<td>Description</td>
	<td>Date Created</td>
</tr>
<@saf.iterator id="room" value="%{availableRooms}" status="stat">
<tr class="tableContent">
	<#if stat.isOdd()>
	<td class="tableOperationColumnOdd">
	<#else>
	<td class="tableOperationColumnEven">
	</#if>
	<@saf.url id="url" action="enterRoom" namespace="/chat">
		<@saf.param name="roomName" value="%{#room.name}" />
	</@saf.url>
	<@saf.a href="%{url}">Enter</@saf.a>
	</td>
	<#if stat.odd>
	<td class="tableNameColumnOdd">
	<#else>
	<td class="tableNameColumnEven">
	</#if>
	<@saf.property value="%{#room.name}" />
	</td>
	<#if stat.odd>
	<td class="tableDescriptionColumnOdd">
	<#else>
	<td class="tableDescriptionColumnEven">
	</#if>
	<@saf.property value="%{#room.description}" />
	</td>
	<#if stat.odd>
	<td class="tableDateCreatedColumnOdd">
	<#else>
	<td class="tableDateCreateColumnEven">
	</#if>
	<@saf.property value="%{#room.creationDate}" />
	</td>
</tr>
</@saf.iterator>
</table>
