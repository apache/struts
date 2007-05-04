<div dojoType="struts:StrutsTabContainer"
  <#if parameters.cssStyle?if_exists != "">
    style="${parameters.cssStyle?html}"<#rt/>
  </#if>
  <#if parameters.id?if_exists != "">
    id="${parameters.id?html}"<#rt/>
  </#if>
  <#if parameters.cssClass?if_exists != "">
    class="${parameters.cssClass?html}"<#rt/>
  </#if>
  <#if parameters.selectedTab?if_exists != "">
    selectedTab="${parameters.selectedTab?html}"<#rt/>
  </#if>
  <#if parameters.labelPosition?if_exists != "">
    labelPosition="${parameters.labelPosition?html}"<#rt/>
  </#if>
  <#if parameters.closeButton?if_exists != "">
    closeButton="${parameters.closeButton?html}"<#rt/>
  </#if>
  <#if parameters.doLayout?exists>
    doLayout="${parameters.doLayout?string?html}"<#rt/>
  </#if>
  <#if parameters.label?if_exists != "">
    label="${parameters.label?html}"<#rt/>
  </#if>
  <#if parameters.templateCssPath?exists>
	templateCssPath="<@s.url value='${parameters.templateCssPath}' encode="false" includeParams='none'/>"
  </#if>
  <#if parameters.beforeSelectTabNotifyTopics?if_exists != "">
    beforeSelectTabNotifyTopics="${parameters.beforeSelectTabNotifyTopics?html}"<#rt/>
  </#if>
  <#if parameters.afterSelectTabNotifyTopics?if_exists != "">
    afterSelectTabNotifyTopics="${parameters.afterSelectTabNotifyTopics?html}"<#rt/>
  </#if>
  <#include "/${parameters.templateDir}/simple/scripting-events.ftl" />
  <#include "/${parameters.templateDir}/simple/common-attributes.ftl" />
>
