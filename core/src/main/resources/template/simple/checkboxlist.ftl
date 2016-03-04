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
<#assign itemCount = 0/>
<#if parameters.list??>
<@s.iterator value="parameters.list">
    <#assign itemCount = itemCount + 1/>
    <#if parameters.listKey??>
        <#assign itemKey = stack.findValue(parameters.listKey)/>
        <#assign itemKeyStr = stack.findString(parameters.listKey)/>
    <#else>
        <#assign itemKey = stack.findValue('top')/>
        <#assign itemKeyStr = stack.findString('top')>
    </#if>
    <#if parameters.listLabelKey??>
    <#-- checks the valueStack for the 'valueKey.' The valueKey is then looked-up in the locale 
       file for it's localized value.  This is then used as a label -->
        <#assign itemValue = struts.getText(stack.findString(parameters.listLabelKey))/>
    <#elseif parameters.listValue??>
        <#assign itemValue = stack.findString(parameters.listValue)!""/>
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
<input type="checkbox" name="${parameters.name?html}" value="${itemKeyStr?html}"<#rt/>
    <#if parameters.id?has_content>
       id="${parameters.id?html}-${itemCount}"<#rt/>
    <#else>
       id="${parameters.name?html}-${itemCount}"<#rt/>
    </#if>
    <#if tag.contains(parameters.nameValue, itemKey)>
       checked="checked"<#rt/>
    </#if>
    <#if parameters.disabled!false>
       disabled="disabled"<#rt/>
    </#if>
    <#if itemCssClass! != "">
     class="${itemCssClass?html}"<#rt/>
    <#else>
        <#if parameters.cssClass?has_content>
     class="${parameters.cssClass?html}"<#rt/>
        </#if>
    </#if>
    <#if itemCssStyle! != "">
     style="${itemCssStyle?html}"<#rt/>
    <#else>
        <#if parameters.cssStyle?has_content>
     style="${parameters.cssStyle?html}"<#rt/>
        </#if>
    </#if>
    <#if itemTitle! != "">
     title="${itemTitle?html}"<#rt/>
    <#else>
        <#if parameters.title?has_content>
     title="${parameters.title?html}"<#rt/>
        </#if>
    </#if>
    <#include "/${parameters.templateDir}/${parameters.expandTheme}/css.ftl" />
    <#include "/${parameters.templateDir}/${parameters.expandTheme}/scripting-events.ftl" />
    <#include "/${parameters.templateDir}/${parameters.expandTheme}/common-attributes.ftl" />
    <#include "/${parameters.templateDir}/${parameters.expandTheme}/dynamic-attributes.ftl" />
        />
<label<#rt/> 
    <#if parameters.id?has_content>
        for="${parameters.id?html}-${itemCount}"<#rt/>
    <#else>
        for="${parameters.name?html}-${itemCount}"<#rt/>
    </#if>
        class="checkboxLabel">${itemValue?html}</label>
</@s.iterator>
    <#else>
    &nbsp;
</#if>
<input type="hidden" id="__multiselect_${parameters.id?html}" name="__multiselect_${parameters.name?html}"
       value=""<#rt/>
<#if parameters.disabled!false>
       disabled="disabled"<#rt/>
</#if>
 />