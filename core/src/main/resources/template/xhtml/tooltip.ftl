<#if parameters.tooltip?exists><#t/>
	  <#assign tooltipHashCode=parameters.tooltip.hashCode() />
      <img id="_tt${tooltipHashCode?string('#')}"
      <#if parameters.tooltipIcon?exists><#t/>
      	src='<@s.url value="${parameters.tooltipIcon}" includeParams="none" encode="false" />'
      <#else><#t/>
      	src='<@s.url value="/struts/dojo/struts/tooltip.gif" includeParams="none" encode="false" />'
      </#if><#t/>
      	alt="${parameters.tooltip?html}" 
      	title="${parameters.tooltip?html}" />
      <#if parameters.jsTooltipEnabled?default('false') == 'true'>
      <span dojoType="tooltip" connectId="_tt${tooltipHashCode?string('#')}"
          <#if parameters.tooltipToggle?exists><#t/>
        	<#t/>toggle="${parameters.tooltipToggle}"<#t/>
          </#if><#t/>
          <#if parameters.tooltipToggleDuration?exists><#t/>
        	<#t/>toggleDuration="${parameters.tooltipToggleDuration}"<#t/>
          </#if><#t/>
          <#if parameters.tooltipDelay?exists><#t/>
          	<#t/>delay="${parameters.tooltipDelay}"<#t/>
          </#if><#t/>
          caption="${parameters.tooltip}"></span>
     </#if>
</#if><#t/>
