<#--
/*
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
<script nonce="${parameters.nonce}"<#rt/>
<#if parameters.async?has_content>
 name="${parameters.async}"<#rt/>
</#if>
<#if parameters.charset?has_content>
 name="${parameters.charset}"<#rt/>
</#if>
<#if parameters.defer?has_content>
 name="${parameters.defer}"<#rt/>
</#if>
<#if parameters.src?has_content>
 name="${parameters.src}"<#rt/>
</#if>
<#if parameters.type?has_content>
 name="${parameters.type}"<#rt/>
</#if>
<#if parameters.name?has_content>
 name="${parameters.name}"<#rt/>
</#if>
<#if parameters.referrerpolicy?has_content>
 name="${parameters.referrerpolicy}"<#rt/>
</#if>
<#if parameters.nomodule?has_content>
 name="${parameters.nomodule}"<#rt/>
</#if>
<#if parameters.integrity?has_content>
 name="${parameters.integrity}"<#rt/>
</#if>
<#if parameters.crossorigin?has_content>
 name="${parameters.crossorigin}"<#rt/>
</#if>

<#include "/${parameters.templateDir}/${parameters.expandTheme}/common-attributes.ftl" />
<#include "/${parameters.templateDir}/${parameters.expandTheme}/dynamic-attributes.ftl" />
>