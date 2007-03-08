<#--include "/${parameters.templateDir}/xhtml/controlheader-core.ftl" /-->
<#if parameters.form?exists && parameters.form.validate?default(false) == true>
	<#-- can't mutate the data model in freemarker -->
    <#if parameters.onblur?exists>
        ${tag.addParameter('onblur', "validate(this);${parameters.onblur}")}
    <#else>
        ${tag.addParameter('onblur', "validate(this);")}
    </#if>
</#if>
    