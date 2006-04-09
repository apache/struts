<div
    <#if parameters.id?exists>               id="${parameters.id?html}"         </#if>
    <#if parameters.name?exists>             name="${parameters.name?html}"         </#if>
    <#if parameters.cssClass?exists>         class="${parameters.cssClass?html}"    </#if>
    <#if parameters.cssStyle?exists>         style="${parameters.cssStyle?html}"    </#if>
    <#if parameters.title?exists>            title="${parameters.title?html}"<#rt/>
        </#if>
<#include "/${parameters.templateDir}/simple/scripting-events.ftl" />
>
