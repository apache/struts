<#--
/*
 * $Id: form.ftl 590812 2007-10-31 20:32:54Z apetrelli $
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
<#if (parameters.validate!false == false)><#rt/>
    <#if parameters.onsubmit?has_content><#rt/>
        ${tag.addParameter('onsubmit', "${parameters.onsubmit}") }
    </#if>
</#if>
<form<#rt/>
<#if parameters.id?has_content>
 id="${parameters.id}"<#rt/>
</#if>
<#if parameters.name?has_content>
 name="${parameters.name}"<#rt/>
</#if>
<#if parameters.onsubmit?has_content>
 onsubmit="<#outputformat 'JavaScript'>${parameters.onsubmit}</#outputformat>"<#rt/>
</#if>
<#if parameters.onreset?has_content>
 onreset="<#outputformat 'JavaScript'>${parameters.onreset}</#outputformat>"<#rt/>
</#if>
<#if parameters.action?has_content>
 action="${parameters.action}"<#rt/>
</#if>
<#if parameters.target?has_content>
 target="${parameters.target}"<#rt/>
</#if>
<#if parameters.method?has_content>
 method="${parameters.method}"<#rt/>
<#else>
 method="post"<#rt/>
</#if>
<#if parameters.enctype?has_content>
 enctype="${parameters.enctype}"<#rt/>
</#if>
<#if parameters.cssClass?has_content>
 class="${parameters.cssClass}"<#rt/>
</#if>
<#if parameters.cssStyle?has_content>
 style="${parameters.cssStyle}"<#rt/>
</#if>
<#if parameters.title?has_content>
 title="${parameters.title}"<#rt/>
</#if>
<#if parameters.acceptcharset?has_content>
 accept-charset="${parameters.acceptcharset}"<#rt/>
</#if>
<#include "/${parameters.templateDir}/${parameters.expandTheme}/dynamic-attributes.ftl" />