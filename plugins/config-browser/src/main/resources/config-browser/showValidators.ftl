<!-- Validators -->
<table width="100%">
    <tr><th>Field</th><th>Type</th><th>&nbsp;</th></tr>
    <#assign row = 0>
    	<#if validators?exists>
        <#foreach i in validators>       
        <tr <#if i_index%2 gt 0>class="b"<#else>class="a"</#if>>
        	<td>${i.fieldName}</td>
            <td>${action.stripPackage(i.class)}</td>
            <td>
            <a href="#" onClick="window.open('validatorDetails.${extension}?clazz=${clazz}&context=${context}&selected=${row}', 'Validator Details', 'resizable=yes,toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,width=640,height=480');">details</a>
            </td>
        </tr>
        <#assign row = row + 1>
        </#foreach>
        </#if>
</table>
