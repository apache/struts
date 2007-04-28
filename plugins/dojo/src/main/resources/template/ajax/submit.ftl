<#if parameters.parentTheme?default('') == 'xhtml'>
  <tr>
      <td colspan="2"><div <#rt/>
  <#if parameters.align?exists>
      align="${parameters.align?html}"<#t/>
  </#if>
  ><#t/>
<#elseif parameters.parentTheme?default('') == 'css_xhtml'>
  <#if parameters.labelposition?default("top") == 'top'>
    <div <#rt/>
  <#else>
    <span <#rt/>
  </#if>
  <#if parameters.align?exists>
    align="${parameters.align?html}"<#t/>
  </#if>
  <#if parameters.id?exists>
    id="wwctrl_${parameters.id}"<#rt/>
  </#if>
  ><#t/>
</#if>

<#if parameters.type?exists && parameters.type=="button">
  <input type="button" dojoType="struts:Bind" events="onclick"<#rt/>
  <#include "/${parameters.templateDir}/ajax/ajax-common.ftl"/>
  <#include "/${parameters.templateDir}/simple/scripting-events.ftl"/>
  <#include "/${parameters.templateDir}/simple/common-attributes.ftl" />
  <#if parameters.label?if_exists != "">
     value="${parameters.label?html}"<#rt/>
  </#if>
 />
<#else>
  <#if parameters.type?exists && parameters.type=="image">
    <input type="image" dojoType="struts:Bind" events="onclick"<#rt/>
    <#if parameters.label?if_exists != "">
     alt="${parameters.label?html}"<#rt/>
    </#if>
    <#if parameters.src?if_exists != "">
     src="${parameters.src?html}"<#rt/>
    </#if>
  <#else>
    <input type="submit" dojoType="struts:Bind" events="onclick"<#rt/>
  </#if>
    <#if parameters.nameValue?if_exists != "">
     value="${parameters.nameValue?html}"<#rt/>
    </#if>
    <#if parameters.value?if_exists != "">
     value="${parameters.value?html}"<#rt/>
    </#if>
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
    <#include "/${parameters.templateDir}/ajax/ajax-common.ftl"/>
    <#include "/${parameters.templateDir}/simple/scripting-events.ftl" />
    <#include "/${parameters.templateDir}/simple/common-attributes.ftl" />
  />
</#if>
<#if parameters.parentTheme?default('') == 'xhtml'>
  </div><#t/>
  <#include "/${parameters.templateDir}/xhtml/controlfooter.ftl" />
<#elseif parameters.parentTheme?default('') == 'css_xhtml'>
  <#if parameters.labelposition?default("top") == 'top'>
    </div> <#t/>
  <#else>
    </span> <#t/>
  </#if>  
</#if>