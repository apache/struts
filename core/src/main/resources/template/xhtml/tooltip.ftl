<#if parameters.tooltip?exists><#t/>
      <img 
      <#if parameters.tooltipIconPath?exists><#t/>
      	src='<@s.url value="${parameters.tooltipIconPath}" includeParams="none" encode="false" />'
      <#else><#t/>
      	src='<@s.url value="/struts/tooltip.gif" includeParams="none" encode="false" />'
      </#if><#t/>
      alt="${parameters.tooltip?html}" 
      <#if parameters.jsTooltipEnabled?default('false') == 'true'>
          onmouseover="domTT_activate(this, event, 'content', '${parameters.tooltip}'<#t/> 
          <#if parameters.tooltipDelay?exists><#t/>
          	<#t/>,'delay', '${parameters.tooltipDelay}'<#t/>
          </#if><#t/>
          <#t/>,'styleClass', '${parameters.tooltipCssClass?default("StrutsTTClassic")}'<#t/>
          <#t/>)" />
      <#else>
      	title="${parameters.tooltip?html}"/>
     </#if>
</#if><#t/>
