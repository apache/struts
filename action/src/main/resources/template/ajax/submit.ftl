<#if parameters.resultDivId?exists || parameters.onLoadJS?exists>
<#include "/${parameters.templateDir}/ajax/submit-ajax.ftl" />
${tag.addFormParameter("ajaxSubmit", "false")}
<#else>
<#include "/${parameters.templateDir}/xhtml/submit.ftl" />
</#if>
