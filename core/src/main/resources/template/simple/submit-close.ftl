<#if parameters.type?? && parameters.type=="button">
<#if (parameters.body)?default("")?length gt 0>${parameters.body}<#elseif parameters.label??><@s.property value="parameters.label"/><#rt/></#if>
</button>
<#else>
${parameters.body}<#rt/>
</#if>
<#include "/${parameters.templateDir}/${parameters.expandTheme}/action-handler.ftl"/>
