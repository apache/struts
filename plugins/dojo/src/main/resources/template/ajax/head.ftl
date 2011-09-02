<#--
/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
-->
<script language="JavaScript" type="text/javascript">
    // Dojo configuration
    djConfig = {
        isDebug: ${parameters.debug?default(false)?string},
        bindEncoding: "${parameters.encoding}"
        <#if parameters.baseRelativePath?if_exists != "">
          ,baseRelativePath: "<@s.url value='${parameters.baseRelativePath}' includeParams='none' encode='false' />"
          ,baseScriptUri: "<@s.url value='${parameters.baseRelativePath}' includeParams='none' encode='false' />"
        <#else>
          ,baseRelativePath: "${base}/struts/dojo/"
          ,baseScriptUri: "${base}/struts/dojo/"
        </#if>  
        <#if parameters.locale?if_exists != "">
          ,locale: "${parameters.locale}"
        </#if>
        <#if parameters.extraLocales?exists>
          ,extraLocale: [
        	<#list parameters.extraLocales as locale>
        		"${locale}"<#if locale_has_next>,</#if>
        	</#list>
          ]
        </#if>
         ,parseWidgets : ${parameters.parseContent?string}
        
    };
</script>

<#if parameters.compressed?default(true)>
  <#assign dojoFile="dojo.js">
<#else>
  <#assign dojoFile="dojo.js.uncompressed.js">
</#if>

<#if parameters.cache?default(true)>
  <#assign profile="struts_">
<#else>
  <#assign profile="">
</#if>           

<#if parameters.baseRelativePath?if_exists != "">
  <script language="JavaScript" type="text/javascript"
        src="<@s.url value='${parameters.baseRelativePath}/${profile}${dojoFile}' includeParams='none' encode='false'  />"></script>
<#else>
  <script language="JavaScript" type="text/javascript"
        src="${base}/struts/dojo/${profile}${dojoFile}"></script>
</#if>  

<script language="JavaScript" type="text/javascript"
        src="${base}/struts/ajax/dojoRequire.js"></script>
<#if parameters.debug?default(false)>
<script language="JavaScript" type="text/javascript">
    dojo.hostenv.writeIncludes(true);
</script>     
</#if>        
<link rel="stylesheet" href="${base}/struts/xhtml/styles.css" type="text/css"/>

<script language="JavaScript" src="${base}/struts/utils.js" type="text/javascript"></script>
<script language="JavaScript" src="${base}/struts/xhtml/validation.js" type="text/javascript"></script>
<script language="JavaScript" src="${base}/struts/css_xhtml/validation.js" type="text/javascript"></script>