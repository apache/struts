<#include "tigris-macros.ftl">
<#assign hideNav = true>
<#call startPage pageTitle="Validator Details"/>
<table>
<tr><td>Validated Class:</td><td>${action.stripPackage(clazz)}</td></tr>
<tr><td>Context:</td><td>${context}</td></tr>
<tr><td>Validator Number:</td><td>${selected}</td></tr>
<tr><td>Validator Type:</td><td>${action.stripPackage(selectedValidator.class)}</td></tr>
</table>
<table width="100%" title="Properties">
    <tr><th>Name</th><th>Value</th><th>Type</th></tr>
    <#foreach prop in properties>
    	<tr <#if prop_index%2 gt 0>class="b"<#else>class="a"</#if>>
            <td>${prop.name}</td>
            <td><#if prop.value?exists> ${prop.value?string} <#else> <b>null</b> </#if></td>
            <td>${prop.type.name}</td>
        </tr>
    </#foreach></table>
<#call endPage>
