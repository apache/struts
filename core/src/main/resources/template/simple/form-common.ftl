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
<#if (attributes.validate!false == false)><#rt/>
    <#if attributes.onsubmit?has_content><#rt/>
        ${tag.addParameter('onsubmit', "${attributes.onsubmit}") }
    </#if>
</#if>
<form<#rt/>
<#if attributes.id?has_content>
 id="${attributes.id}"<#rt/>
</#if>
<#if attributes.name?has_content>
 name="${attributes.name}"<#rt/>
</#if>
<#if attributes.onsubmit?has_content>
 onsubmit="<#outputformat 'JavaScript'>${attributes.onsubmit}</#outputformat>"<#rt/>
</#if>
<#if attributes.onreset?has_content>
 onreset="<#outputformat 'JavaScript'>${attributes.onreset}</#outputformat>"<#rt/>
</#if>
<#if attributes.action?has_content>
 action="${attributes.action}"<#rt/>
</#if>
<#if attributes.target?has_content>
 target="${attributes.target}"<#rt/>
</#if>
<#if attributes.method?has_content>
 method="${attributes.method}"<#rt/>
<#else>
 method="post"<#rt/>
</#if>
<#if attributes.enctype?has_content>
 enctype="${attributes.enctype}"<#rt/>
</#if>
<#if attributes.cssClass?has_content>
 class="${attributes.cssClass}"<#rt/>
</#if>
<#if attributes.cssStyle?has_content>
 style="${attributes.cssStyle}"<#rt/>
</#if>
<#if attributes.title?has_content>
 title="${attributes.title}"<#rt/>
</#if>
<#if attributes.acceptcharset?has_content>
 accept-charset="${attributes.acceptcharset}"<#rt/>
</#if>
<#include "/${attributes.templateDir}/${attributes.expandTheme}/dynamic-attributes.ftl" />