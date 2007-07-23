<div id="${parameters.id}" dojoType="ContentPane"
  <#if parameters.title?if_exists != "">
    label="${parameters.title?html}"<#rt/>
  </#if>
  <#if parameters.href?if_exists != "">
    href="${parameters.href}"<#rt/>
  </#if>
  <#if parameters.cssStyle?if_exists != "">
    style="${parameters.cssStyle?html}"<#rt/>
  </#if>
  <#if parameters.cssClass?if_exists != "">
    class="${parameters.cssClass?html}"<#rt/>
  </#if>
  <#if parameters.closeButton?if_exists != "">
    closeButton="${parameters.closeButton?html}"<#rt/>
  </#if>
  <#if parameters.refreshOnShow?if_exists != "">
    refreshOnShow="${parameters.refreshOnShow?html}"<#rt/>
  </#if>
>