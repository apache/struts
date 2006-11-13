<script type="text/javascript">
    dojo.require("dojo.widget.TimePicker");
</script>

<div dojoType="timepicker"
  <#if parameters.id?if_exists != "">
    id="${parameters.id?html}"<#rt/>
  </#if>
  <#if parameters.value?if_exists != "">
    storedTime="todayT${parameters.value?html}"<#rt/>
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