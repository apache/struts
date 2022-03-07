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
<#function acceptKey(key)>
  <#if dynamic_attributes_ignore??>
    <#return !key?starts_with(dynamic_attributes_ignore) >
  <#else>
    <#return true>
  </#if>
</#function>
<#if (parameters.dynamicAttributes?? && parameters.dynamicAttributes?size > 0)><#rt/>
<#assign aKeys = parameters.dynamicAttributes.keySet()><#rt/>
<#list aKeys?filter(acceptKey) as aKey><#rt/>
<#assign keyValue = parameters.dynamicAttributes.get(aKey)/>
<#if keyValue?is_string>
  <#if evaluate_dynamic_attributes!false == true>
    <#assign value = struts.translateVariables(keyValue)!keyValue/><#rt/>
  <#else>
    <#assign value = keyValue/><#rt/>
  </#if>
<#else>
  <#assign value = keyValue?string/>
</#if>
 ${aKey}="${value}"<#rt/>
</#list><#rt/>
</#if><#rt/>
