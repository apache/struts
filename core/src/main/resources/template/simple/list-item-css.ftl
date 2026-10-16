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
<#assign hasFieldErrors = attributes.name?? && fieldErrors?? && fieldErrors.get(attributes.name)??/>
<#assign hasErrorClass = hasFieldErrors && attributes.cssErrorClass??/>
<#assign hasErrorStyle = hasFieldErrors && attributes.cssErrorStyle??/>
<#if itemCssClass?has_content>
    <#assign listItemClass = itemCssClass/>
<#else>
    <#assign listItemClass = attributes.cssClass!''/>
</#if>
<#if hasErrorClass>
    <#assign listItemClass = (listItemClass + ' ' + attributes.cssErrorClass)?trim/>
</#if>
<#if listItemClass?has_content>
 class="${listItemClass}"<#rt/>
</#if>
<#if hasErrorStyle>
 style="${attributes.cssErrorStyle}"<#rt/>
<#elseif itemCssStyle?has_content>
 style="${itemCssStyle}"<#rt/>
<#elseif attributes.cssStyle?has_content && !hasErrorClass>
 style="${attributes.cssStyle}"<#rt/>
</#if>
