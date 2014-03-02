
<table class="table">
<tr class="tableHeader">
	<td>Operation</td>
	<td>Name</td>
	<td>Description</td>
	<td>Date Created</td>
</tr>
<@s.iterator id="room" value="%{availableRooms}" status="stat">
<tr class="tableContent">
	<#if stat.isOdd()>
	<td class="tableOperationColumnOdd">
	<#else>
	<td class="tableOperationColumnEven">
	</#if>
	<@s.url id="url" action="enterRoom" namespace="/chat">
		<@s.param name="roomName" value="%{#room.name}" />
	</@s.url>
	<@s.a href="%{url}">Enter</@s.a>
	</td>
	<#if stat.odd>
	<td class="tableNameColumnOdd">
	<#else>
	<td class="tableNameColumnEven">
	</#if>
	<@s.property value="%{#room.name}" />
	</td>
	<#if stat.odd>
	<td class="tableDescriptionColumnOdd">
	<#else>
	<td class="tableDescriptionColumnEven">
	</#if>
	<@s.property value="%{#room.description}" />
	</td>
	<#if stat.odd>
	<td class="tableDateCreatedColumnOdd">
	<#else>
	<td class="tableDateCreateColumnEven">
	</#if>
	<@s.property value="%{#room.creationDate}" />
	</td>
</tr>
</@s.iterator>
</table>
