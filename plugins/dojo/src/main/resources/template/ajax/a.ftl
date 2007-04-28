<a dojoType="struts:BindAnchor"
  <#if parameters.validate?exists>
    validate="${parameters.validate?string?html}"<#rt/>
  <#else>
    validate="false"<#rt/>  
  </#if>
  <#if parameters.ajaxAfterValidation?exists>
    ajaxAfterValidation="${parameters.ajaxAfterValidation?string?html}"<#rt/>
  <#else>
    ajaxAfterValidation="false"  
  </#if>
  <#include "/${parameters.templateDir}/ajax/ajax-common.ftl" />
  <#include "/${parameters.templateDir}/simple/scripting-events.ftl" />
  <#include "/${parameters.templateDir}/simple/common-attributes.ftl" />
>
