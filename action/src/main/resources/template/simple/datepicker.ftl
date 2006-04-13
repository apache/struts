<script type="text/javascript">
    dojo.require("dojo.widget.html.DatePicker");
    dojo.require("struts.widgets.*");
</script>

<#if parameters.readonly?exists>
    <#include "/${parameters.templateDir}/simple/text.ftl" />
<#else>
    <div dojoType="dropdowncontainer"
        dateIconPath="<@saf.url includeParams='none' value='/struts/dojo/struts/widgets/dateIcon.gif' encode='false'/>"
        <#if parameters.dateFormat?exists>
            dateFormat="${parameters.dateFormat}"
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
        <#if parameters.dateFormat?exists>
            dateFormat="${parameters.dateFormat}"
        </#if>
        <#if parameters.get("size")?exists>
             inputWidth="${parameters.get("size")?html}"
        </#if>
    >
        <#include "/${parameters.templateDir}/simple/text.ftl" />
    </div>
</#if>
