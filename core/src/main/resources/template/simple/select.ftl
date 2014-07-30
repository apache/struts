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
<#setting number_format="#.#####">
<select<#rt/>
 name="${(parameters.name!"")?html}"<#rt/>
<#if parameters.get("size")?has_content>
 size="${parameters.get("size")?html}"<#rt/>
</#if>
<#if parameters.disabled!false>
 disabled="disabled"<#rt/>
</#if>
<#if parameters.tabindex?has_content>
 tabindex="${parameters.tabindex?html}"<#rt/>
</#if>
<#if parameters.id?has_content>
 id="${parameters.id?html}"<#rt/>
</#if>
<#include "/${parameters.templateDir}/${parameters.expandTheme}/css.ftl" />
<#if parameters.title?has_content>
 title="${parameters.title?html}"<#rt/>
</#if>
<#if parameters.multiple!false>
 multiple="multiple"<#rt/>
</#if>
<#include "/${parameters.templateDir}/${parameters.expandTheme}/scripting-events.ftl" />
<#include "/${parameters.templateDir}/${parameters.expandTheme}/common-attributes.ftl" />
<#include "/${parameters.templateDir}/${parameters.expandTheme}/dynamic-attributes.ftl" />
>
<#if parameters.headerKey?? && parameters.headerValue??>
    <option value="${parameters.headerKey?html}"
    <#if tag.contains(parameters.nameValue, parameters.headerKey) == true>
    selected="selected"
    </#if>
    >${parameters.headerValue?html}</option>
</#if>
<#if parameters.emptyOption!false>
    <option value=""></option>
</#if>
<@s.iterator value="parameters.list">
        <#if parameters.listKey??>
            <#if stack.findValue(parameters.listKey)??>
              <#assign itemKey = stack.findValue(parameters.listKey)/>
              <#assign itemKeyStr = stack.findString(parameters.listKey)/>
            <#else>
              <#assign itemKey = ''/>
              <#assign itemKeyStr = ''/>
            </#if>
        <#else>
            <#assign itemKey = stack.findValue('top')/>
            <#assign itemKeyStr = stack.findString('top')>
        </#if>
        <#if parameters.listValueKey??>
          <#-- checks the valueStack for the 'valueKey.' The valueKey is then looked-up in the locale file for it's 
             localized value.  This is then used as a label -->
          <#assign valueKey = stack.findString(parameters.listValueKey) />
          <#if valueKey??>
              <#assign itemValue = struts.getText(valueKey) />
          <#else>
              <#assign itemValue = parameters.listValueKey />
          </#if>
        <#elseif parameters.listValue??>
            <#if stack.findString(parameters.listValue)??>
              <#assign itemValue = stack.findString(parameters.listValue)/>
            <#else>
              <#assign itemValue = ''/>
            </#if>
        <#else>
            <#assign itemValue = stack.findString('top')/>
        </#if>
        <#if parameters.listCssClass??>
            <#if stack.findString(parameters.listCssClass)??>
              <#assign itemCssClass= stack.findString(parameters.listCssClass)/>
            <#else>
              <#assign itemCssClass = ''/>
            </#if>
        </#if>
        <#if parameters.listCssStyle??>
            <#if stack.findString(parameters.listCssStyle)??>
              <#assign itemCssStyle= stack.findString(parameters.listCssStyle)/>
            <#else>
              <#assign itemCssStyle = ''/>
            </#if>
        </#if>
        <#if parameters.listTitle??>
            <#if stack.findString(parameters.listTitle)??>
              <#assign itemTitle= stack.findString(parameters.listTitle)/>
            <#else>
              <#assign itemTitle = ''/>
            </#if>
        </#if>
    <option value="${itemKeyStr?html}"<#rt/>
        <#if tag.contains(parameters.nameValue, itemKey) == true>
 selected="selected"<#rt/>
        </#if>
        <#if itemCssClass?has_content>
 class="${itemCssClass?html}"<#rt/>
        </#if>
        <#if itemCssStyle?has_content>
 style="${itemCssStyle?html}"<#rt/>
        </#if>
        <#if itemTitle?has_content>
 title="${itemTitle?html}"<#rt/>
        </#if>
    >${itemValue?html}</option><#lt/>
</@s.iterator>

<#include "/${parameters.templateDir}/${parameters.expandTheme}/optgroup.ftl" />

</select>

<#if parameters.multiple!false>
  <#if (parameters.id?? && parameters.name??)>
    <input type="hidden" id="__multiselect_${parameters.id?html}" name="__multiselect_${parameters.name?html}" value=""<#rt/>
  </#if>
  <#if (parameters.id?? && !parameters.name??)>
    <input type="hidden" id="__multiselect_${parameters.id?html}" name="__multiselect_${parameters.id?html}" value=""<#rt/>
  </#if>
  <#if ( !parameters.id?? && parameters.name??)>
    <input type="hidden" id="__multiselect_${parameters.id?html}" name="__multiselect_${parameters.id?html}" value=""<#rt/>
  </#if>
   <#if ( !parameters.id?? && !parameters.name??)>
     <input type="hidden" id="" name="" value="" <#rt/>
  </#if>
  
<#if parameters.disabled!false>
 disabled="disabled"<#rt/>
</#if>
 />
</#if>
