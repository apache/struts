<script type="text/javascript">
    dojo.require("struts.widgets.*");
</script>

<#if parameters.readonly?exists>
    <#include "/${parameters.templateDir}/simple/text.ftl" />
<#else>
	<div dojoType="dropdowntimepicker"
		useDefaultTime="false"
        <#if parameters.format?exists>
            timeFormat="${parameters.format}"
        </#if>
        <#if parameters.nameValue?exists>
        	value="${parameters.nameValue?html}"
        </#if>
        <#if parameters.timeIconPath?exists>
            iconPath="${parameters.timeIconPath}"
        <#else>
        	iconPath="<@s.url includeParams='none' value='/struts/dojo/struts/widgets/timeIcon.gif' encode='false' includeParams="none" />"
        </#if>
        <#if parameters.templatePath?exists>
            templatePath="${parameters.templatePath}"
        </#if>
        <#if parameters.templateCssPath?exists>
            templateCssPath="${parameters.templateCssPath}"
        </#if>
        <#if parameters.get("size")?exists>
             inputWidth="${parameters.get("size")?string?html}"
        </#if>
    >
        <#include "/${parameters.templateDir}/simple/text.ftl" />
    </div>
</#if>