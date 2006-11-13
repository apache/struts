<div dojoType="struts:BindDiv"
  <#if parameters.delay?exists>
    delay="${parameters.delay?c}"<#rt/>
  </#if>
  <#if parameters.updateInterval?exists>
    updateInterval="${parameters.updateInterval?c}"<#rt/>
  </#if>
  <#if parameters.autoStart?exists>
    autoStart="${parameters.autoStart?string?html}"<#rt/>
  </#if>
  <#if parameters.startTimerListenTopic?if_exists != "">
    startTimerListenTopic="${parameters.startTimerListenTopic?html}"<#rt/>
  </#if>
  <#if parameters.stopTimerListenTopic?if_exists != "">
    stopTimerListenTopic="${parameters.stopTimerListenTopic?html}"<#rt/>
  </#if>
  <#include "/${parameters.templateDir}/ajax/ajax-common.ftl" />
  <#include "/${parameters.templateDir}/simple/scripting-events.ftl" />
>
