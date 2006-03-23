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
<#include "/${parameters.templateDir}/simple/submit.ftl" />
<#if parameters.labelposition?default("top") == 'top'>
</div> <#t/>
<#else>
</span> <#t/>
</#if>
