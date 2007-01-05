<input type="hidden"
  <#if parameters.nameValue?if_exists != "">
    value="${parameters.nameValue?html}"<#rt/>
  </#if>
  <#if parameters.name?if_exists != "">
    name="${parameters.name?html}"<#rt/>
    id="struts_${parameters.name?html}"<#rt/>
  </#if>
>
<div dojoType="struts:TimePicker"
  <#if parameters.id?if_exists != "">
    id="${parameters.id?html}"<#rt/>
  </#if>
  <#if parameters.name?if_exists != "">
    inputId="struts_${parameters.name?html}"<#rt/>
  </#if>
  <#if parameters.nameValue?if_exists != "">
    storedTime="${parameters.nameValue?html}"<#rt/>
  </#if>
  <#if parameters.language?if_exists != "">
    lang="${parameters.language?html}"<#rt/>
  </#if>
  <#if parameters.name?if_exists != "">
    name="${parameters.name?html}"<#rt/>
  </#if>
  <#if parameters.tabindex?if_exists != "">
    tabindex="${parameters.tabindex?html}"<#rt/>
  </#if>
  <#if parameters.cssClass?if_exists != "">
    class="${parameters.cssClass?html}"<#rt/>
  </#if>
  <#if parameters.cssStyle?if_exists != "">
    style="${parameters.cssStyle?html}"<#rt/>
  </#if>
  <#if parameters.useDefaultMinutes?exists>
    useDefaultMinutes="${parameters.useDefaultMinutes?string}"<#rt/>
  </#if>
  <#if parameters.useDefaultTime?exists>
    useDefaultTime="${parameters.useDefaultTime?string}"<#rt/>
  </#if>
  <#include "/${parameters.templateDir}/simple/scripting-events.ftl" />
></div>