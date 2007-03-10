<script type="text/javascript">
  dojo.require("dojo.widget.TabContainer");
  dojo.require("dojo.widget.LinkPane");
  dojo.require("dojo.widget.ContentPane");
</script>

<div dojoType="TabContainer"
  <#if parameters.cssStyle?if_exists != "">
    style="${parameters.cssStyle?html}"<#rt/>
  </#if>
  <#if parameters.id?if_exists != "">
    id="${parameters.id?html}"<#rt/>
  </#if>
  <#if parameters.cssClass?if_exists != "">
    class="${parameters.cssClass?html}"<#rt/>
  </#if>
  <#if parameters.selectedTab?if_exists != "">
    selectedTab="${parameters.selectedTab?html}"<#rt/>
  </#if>
  <#if parameters.labelPosition?if_exists != "">
    labelPosition="${parameters.labelPosition?html}"<#rt/>
  </#if>
  <#if parameters.closeButton?if_exists != "">
    closeButton="${parameters.closeButton?html}"<#rt/>
  </#if>
  <#if parameters.doLayout?exists>
    doLayout="${parameters.doLayout?string?html}"<#rt/>
  </#if>
  <#if parameters.templateCssPath?exists>
	templateCssPath="<@s.url value='${parameters.templateCssPath}' encode="false" includeParams='none'/>"
  </#if>
>
