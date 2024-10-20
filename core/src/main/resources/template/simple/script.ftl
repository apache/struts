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
<script<#rt/>
<#if attributes.async?has_content && attributes.async == "true">
 async<#rt/>
</#if>
<#if attributes.charset?has_content>
 charset="${attributes.charset}"<#rt/>
</#if>
<#if attributes.defer?has_content && attributes.defer=="true">
 defer<#rt/>
</#if>
<#if attributes.type?has_content>
 type="${attributes.type}"<#rt/>
</#if>
<#if attributes.src?has_content>
 src="${attributes.src}"<#rt/>
</#if>
<#if attributes.referrerpolicy?has_content>
 referrerpolicy="${attributes.referrerpolicy}"<#rt/>
</#if>
<#if attributes.nomodule?has_content && attributes.nomodule=="true">
 nomodule<#rt/>
</#if>
<#if attributes.integrity?has_content>
 integrity="${attributes.integrity}"<#rt/>
</#if>
<#if attributes.crossorigin?has_content>
 crossorigin="${attributes.crossorigin}"<#rt/>
</#if>
<#include "/${attributes.templateDir}/${attributes.expandTheme}/common-attributes.ftl" />
<#include "/${attributes.templateDir}/${attributes.expandTheme}/dynamic-attributes.ftl" />
<#include "/${attributes.templateDir}/${attributes.expandTheme}/nonce.ftl" />>
