<script language="JavaScript" type="text/javascript">
    // Dojo configuration
    djConfig = {
        isDebug: ${parameters.debug?default(false)?string},
        bindEncoding: "${parameters.encoding}",
        <#if parameters.baseRelativePath?if_exists != "">
          baseRelativePath: "<@s.url value='${parameters.baseRelativePath}' includeParams='none' encode='false' />",
          baseScriptUri: "<@s.url value='${parameters.baseRelativePath}' includeParams='none' encode='false' />",
        <#else>
          baseRelativePath: "<@s.url value='/struts/dojo/' includeParams='none' encode='false' />",
          baseScriptUri: "<@s.url value='/struts/dojo/' includeParams='none' encode='false' />",
        </#if>  
        <#if parameters.locale?if_exists != "">
          locale: "${parameters.locale}",
        </#if>
        <#if parameters.extraLocales?exists>
          extraLocale: [
        	<#list parameters.extraLocales as locale>
        		"${locale}",
        	</#list>
          ]
        </#if>
    };
</script>

<#if parameters.compressed?default(true)>
  <#assign dojoFile="dojo.js">
<#else>
  <#assign dojoFile="dojo.js.uncompressed.js">
</#if>

<#if parameters.cache?default(false)>
  <#assign profile="struts_">
<#else>
  <#assign profile="">
</#if>           

<#if parameters.baseRelativePath?if_exists != "">
  <script language="JavaScript" type="text/javascript"
        src="<@s.url value='${parameters.baseRelativePath}/${profile}${dojoFile}' includeParams='none' encode='false'  />"></script>
<#else>
  <script language="JavaScript" type="text/javascript"
        src="<@s.url value='/struts/dojo/${profile}${dojoFile}' includeParams='none' encode='false'  />"></script>
</#if>  

<script language="JavaScript" type="text/javascript"
        src="<@s.url value='/struts/ajax/dojoRequire.js' includeParams='none' encode='false'  />"></script>
<script language="JavaScript" type="text/javascript"
        src="<@s.url value='/struts/CommonFunctions.js' includeParams='none' encode='false'/>"></script>
