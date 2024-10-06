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
<#setting number_format="#.#####">
<select<#rt/>
 name="${(attributes.name!"")}"<#rt/>
<#if attributes.get("size")?has_content>
 size="${attributes.get("size")}"<#rt/>
</#if>
<#if attributes.disabled!false>
 disabled="disabled"<#rt/>
</#if>
<#if attributes.tabindex?has_content>
 tabindex="${attributes.tabindex}"<#rt/>
</#if>
<#if attributes.id?has_content>
 id="${attributes.id}"<#rt/>
</#if>
<#include "/${attributes.templateDir}/${attributes.expandTheme}/css.ftl" />
<#if attributes.title?has_content>
 title="${attributes.title}"<#rt/>
</#if>
<#if attributes.multiple!false>
 multiple="multiple"<#rt/>
</#if>
<#include "/${attributes.templateDir}/${attributes.expandTheme}/scripting-events.ftl" />
<#include "/${attributes.templateDir}/${attributes.expandTheme}/common-attributes.ftl" />
<#include "/${attributes.templateDir}/${attributes.expandTheme}/dynamic-attributes.ftl" />
>
<#if attributes.headerKey?? && attributes.headerValue??>
    <option value="${attributes.headerKey}"<#rt/>
    <#if tag.contains(attributes.nameValue, attributes.headerKey) == true>
    selected="selected"<#rt/>
    </#if>
    >${attributes.headerValue}</option>
</#if>
<#if attributes.emptyOption!false>
    <option value=""></option>
</#if>
<@s.iterator value="attributes.list">
        <#if attributes.listKey??>
            <#if stack.findValue(attributes.listKey)??>
              <#assign itemKey = stack.findValue(attributes.listKey)/>
              <#assign itemKeyStr = stack.findString(attributes.listKey)/>
            <#else>
              <#assign itemKey = ''/>
              <#assign itemKeyStr = ''/>
            </#if>
        <#else>
            <#assign itemKey = stack.findValue('top')/>
            <#assign itemKeyStr = stack.findString('top')>
        </#if>
        <#if attributes.listValueKey??>
          <#-- checks the valueStack for the 'valueKey.' The valueKey is then looked-up in the locale file for it's
             localized value.  This is then used as a label -->
          <#assign valueKey = stack.findString(attributes.listValueKey)!'' />
          <#if valueKey?has_content>
              <#assign itemValue = struts.getText(valueKey) />
          <#else>
              <#assign itemValue = attributes.listValueKey />
          </#if>
        <#elseif attributes.listValue??>
            <#if stack.findString(attributes.listValue)??>
              <#assign itemValue = stack.findString(attributes.listValue)/>
            <#else>
              <#assign itemValue = ''/>
            </#if>
        <#else>
            <#assign itemValue = stack.findString('top')/>
        </#if>
        <#if attributes.listCssClass??>
            <#if stack.findString(attributes.listCssClass)??>
              <#assign itemCssClass= stack.findString(attributes.listCssClass)/>
            <#else>
              <#assign itemCssClass = ''/>
            </#if>
        </#if>
        <#if attributes.listCssStyle??>
            <#if stack.findString(attributes.listCssStyle)??>
              <#assign itemCssStyle= stack.findString(attributes.listCssStyle)/>
            <#else>
              <#assign itemCssStyle = ''/>
            </#if>
        </#if>
        <#if attributes.listTitle??>
            <#if stack.findString(attributes.listTitle)??>
              <#assign itemTitle= stack.findString(attributes.listTitle)/>
            <#else>
              <#assign itemTitle = ''/>
            </#if>
        </#if>
    <option value="${itemKeyStr}"<#rt/>
        <#if tag.contains(attributes.nameValue, itemKey) == true>
 selected="selected"<#rt/>
        </#if>
        <#if itemCssClass?has_content>
 class="${itemCssClass}"<#rt/>
        </#if>
        <#if itemCssStyle?has_content>
 style="${itemCssStyle}"<#rt/>
        </#if>
        <#if itemTitle?has_content>
 title="${itemTitle}"<#rt/>
        </#if>
    >${itemValue}</option><#lt/>
</@s.iterator>
<#include "/${attributes.templateDir}/${attributes.expandTheme}/optgroup.ftl" />
</select>

<#if attributes.multiple!false>
  <#if (attributes.id?? && attributes.name??)>
    <input type="hidden" id="__multiselect_${attributes.id}" name="__multiselect_${attributes.name}" value=""<#rt/>
  </#if>
  <#if (attributes.id?? && !attributes.name??)>
    <input type="hidden" id="__multiselect_${attributes.id}" name="__multiselect_${attributes.id}" value=""<#rt/>
  </#if>
  <#if ( !attributes.id?? && attributes.name??)>
    <input type="hidden" id="__multiselect_${attributes.id}" name="__multiselect_${attributes.id}" value=""<#rt/>
  </#if>
   <#if ( !attributes.id?? && !attributes.name??)>
     <input type="hidden" id="" name="" value="" <#rt/>
  </#if>

<#if attributes.disabled!false>
 disabled="disabled"<#rt/>
</#if>
 />
</#if>
