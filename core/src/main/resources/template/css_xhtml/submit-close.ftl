<#include "/${parameters.templateDir}/simple/submit-close.ftl" />
<#if !parameters.labelposition?? && (parameters.form.labelposition)??>
<#assign labelpos = parameters.form.labelposition/>
<#elseif parameters.labelposition??>
<#assign labelpos = parameters.labelposition/>
</#if>
<#if labelpos?default("top") == 'top'>
</div> <#t/>
<#else>
</span> <#t/>
</#if>
