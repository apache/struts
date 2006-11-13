<script type="text/javascript">
    dojo.require("dojo.widget.DatePicker");
</script>

<div dojoType="datepicker"
  <#if parameters.id?if_exists != "">
    id="${parameters.id?html}"<#rt/>
  </#if>
  <#if parameters.value?if_exists != "">
    value="${parameters.value?html}"<#rt/>
  </#if>
  <#if parameters.language?if_exists != "">
    lang="${parameters.language?html}"<#rt/>
  </#if>
  <#if parameters.name?if_exists != "">
    name="${parameters.name?html}"<#rt/>
  </#if>
  <#if parameters.displayWeeks?if_exists != "">
    displayWeeks="${parameters.displayWeeks?html}"<#rt/>
  </#if>
  <#if parameters.adjustWeeks?exists>
    adjustWeeks="${parameters.adjustWeeks?string?html}"<#rt/>
  </#if>
  <#if parameters.startDate?if_exists != "">
    startDate="${parameters.startDate?html}"<#rt/>
  </#if>
  <#if parameters.endDate?if_exists != "">
    endDate="${parameters.endDate?html}"<#rt/>
  </#if>
  <#if parameters.weekStartsOn?if_exists != "">
    weekStartsOn="${parameters.weekStartsOn?html}"<#rt/>
  </#if>
  <#if parameters.staticDisplay?exists>
    staticDisplay="${parameters.staticDisplay?string?html}"<#rt/>
  </#if>
  <#if parameters.dayWidth?if_exists != "">
    dayWidth="${parameters.dayWidth?html}"<#rt/>
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
  <#include "/${parameters.templateDir}/simple/scripting-events.ftl" />
></div>