<script type="text/javascript">
    dojo.require("dojo.widget.html.DatePicker");
    dojo.require("struts.widgets.*");
</script>

<#if parameters.readonly?default(false)>
    <#include "/${parameters.templateDir}/simple/text.ftl" />
<#else>
<#--
    <div dojoType="dropdowncontainer"
        dateIconPath="<@s.url includeParams='none' value='/struts/dojo/struts/widgets/dateIcon.gif' encode='false'/>"
        <#if parameters.format?exists>
            dateFormat="${parameters.format}"
        </#if>
        <#if parameters.dateIconPath?exists>
            dateIconPath="${parameters.dateIconPath}"
        </#if>
        <#if parameters.currentDate?exists>
            currentDate="${parameters.currentDate}"
        </#if>
        <#if parameters.currentDate?exists>
            templatePath="${parameters.templatePath}"
        </#if>
        <#if parameters.templateCssPath?exists>
            templateCssPath="${parameters.templateCssPath}"
        </#if>
        <#if parameters.get("size")?exists>
             inputWidth="${parameters.get("size")?html}"
        </#if>
    >
        <#include "/${parameters.templateDir}/simple/text.ftl" />
    </div>
-->
	<div dojoType="dropdowndatepicker"
        <#if parameters.format?exists>
            dateFormat="${parameters.format}"
        </#if>
        <#if parameters.dateIconPath?exists>
            iconPath="${parameters.dateIconPath}"
        <#else>
        	iconPath="<@s.url includeParams='none' value='/struts/dojo/struts/widgets/dateIcon.gif' encode='false' includeParams="none" />"
        </#if>
        <#if parameters.nameValue?exists>
        	value="${parameters.nameValue?html}"
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
