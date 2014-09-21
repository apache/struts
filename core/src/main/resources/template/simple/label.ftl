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
<label<#rt/>
<#if parameters.id?has_content>
 id="${parameters.id?html}"<#rt/>
</#if>
<#if parameters.cssClass?has_content>
 class="${parameters.cssClass?html}"<#rt/>
</#if>
<#if parameters.cssStyle?has_content>
 style="${parameters.cssStyle?html}"<#rt/>
</#if>
<#if parameters.title?has_content>
 title="${parameters.title?html}"<#rt/>
</#if>
<#if parameters.for?has_content>
 for="${parameters.for?html}"<#rt/>
</#if>
<#include "/${parameters.templateDir}/${parameters.expandTheme}/common-attributes.ftl" />
<#include "/${parameters.templateDir}/${parameters.expandTheme}/dynamic-attributes.ftl" />
><#rt/>
<#if parameters.nameValue??>
<@s.property value="parameters.nameValue"/><#t/>
</#if>
</label>