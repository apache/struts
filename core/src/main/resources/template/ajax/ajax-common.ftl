  <#if parameters.id?if_exists != "">
  	id="${parameters.id?html}"<#rt/>
  </#if>
  <#if parameters.formId?if_exists != "">
  	formId="${parameters.formId?html}"<#rt/>
  </#if>
  <#if parameters.formFilter?if_exists != "">
  	formFilter="${parameters.formFilter?html}"<#rt/>
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
  <#if parameters.label?if_exists != "">
    label="${parameters.label?html}"<#rt/>
  </#if>
  <#if parameters.title?if_exists != "">
    title="${parameters.title?html}"<#rt/>
  </#if>
  <#if parameters.name?if_exists != "">
  	name="${parameters.name?html}"<#rt/>
  </#if>
  <#if parameters.href?if_exists != "">
  	href="${parameters.href}"<#rt/>
  </#if>
  <#if parameters.loadingText?if_exists != "">
    loadingText="${parameters.loadingText?html}"<#rt/>
  </#if>
  <#if parameters.errorText?if_exists != "">
    errorText="${parameters.errorText?html}"<#rt/>
  </#if>
  <#if parameters.executeScripts?exists>
    executeScripts="${parameters.executeScripts?string?html}"<#rt/>
  </#if>
  <#if parameters.listenTopics?if_exists != "">
    listenTopics="${parameters.listenTopics?html}"<#rt/>
  </#if>
   <#if parameters.notifyTopics?if_exists != "">
    notifyTopics="${parameters.notifyTopics?html}"<#rt/>
  </#if>
  <#if parameters.beforeLoading?if_exists != "">
    beforeLoading="${parameters.beforeLoading?html}"<#rt/>
  </#if>
  <#if parameters.afterLoading?if_exists != "">
    afterLoading="${parameters.afterLoading?html}"<#rt/>
  </#if>
  <#if parameters.targets?if_exists != "">
    targets="${parameters.targets?html}"<#rt/>
  </#if>
  <#if parameters.handler?if_exists != "">
    handler="${parameters.handler?html}"<#rt/>
  </#if>
  <#if parameters.indicator?if_exists != "">
    indicator="${parameters.indicator?html}"<#rt/>
  </#if>
  <#if parameters.showErrorTransportText?exists>
    showError="${parameters.showErrorTransportText?string?html}"<#rt/>
  </#if>
  <#if parameters.showLoadingText?exists>
    showLoading="${parameters.showLoadingText?string?html}"<#rt/>
  </#if>
