<tr>
    <td colspan="2"><div <#rt/>
<#if parameters.align?exists>
    align="${parameters.align?html}"<#t/>
</#if>
><#t/>
<#if parameters.type?exists && parameters.type=="button">
  <input type="button" dojoType="struts:Bind" event="onclick"<#rt/>
  <#include "/${parameters.templateDir}/ajax/ajax-common.ftl"/>
  <#include "/${parameters.templateDir}/simple/scripting-events.ftl"/>
  <#include "/${parameters.templateDir}/simple/common-attributes.ftl" />
  <#if parameters.label?if_exists != "">
     value="${parameters.label?html}"<#rt/>
  </#if>
 />
<#else>
  <#if parameters.type?exists && parameters.type=="image">
    <input type="image" dojoType="struts:Bind" event="onclick"<#rt/>
    <#if parameters.label?if_exists != "">
     alt="${parameters.label?html}"<#rt/>
    </#if>
    <#if parameters.src?if_exists != "">
     src="${parameters.src?html}"<#rt/>
    </#if>
  <#else>
    <input type="submit" dojoType="struts:Bind" event="onclick"<#rt/>
  </#if>
    <#if parameters.nameValue?if_exists != "">
     value="${parameters.nameValue?html}"<#rt/>
    </#if>
    <#if parameters.value?if_exists != "">
     value="${parameters.value?html}"<#rt/>
    </#if>
    <#include "/${parameters.templateDir}/ajax/ajax-common.ftl"/>
    <#include "/${parameters.templateDir}/simple/scripting-events.ftl" />
    <#include "/${parameters.templateDir}/simple/common-attributes.ftl" />
  />
</#if>

</div><#t/>
<#include "/${parameters.templateDir}/xhtml/controlfooter.ftl" />
