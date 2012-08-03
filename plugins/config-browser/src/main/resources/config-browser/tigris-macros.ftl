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
<#-- Display related macros -->

<#macro startPage pageTitle>
	<#include "page-header.ftl">
</#macro>

<#macro endPage>
	<#include "page-footer.ftl">
</#macro>

<#macro startTabs>
	<div class="tabs" id="tabs">
		<table cellpadding="3" cellspacing="0" border="0">
			<tr>
</#macro>

<#macro endTabs>
			</tr>
		</table>
	</div>
</#macro>

<#macro tab name, url, isSelected>
	<#if isSelected == "true">
		<th><a href="${url}">${name}</a></th>
		<#else>
		<td><a href="${url}">${name}</a></td>
	</#if>
</#macro>

<#-- Highlights every other row-->
<#macro indexedRows count>
	<#if (count%2>0)>
		<tr class="a">
 	<#else>
 		<tr class="b">
 	</#if>
</#macro>
<#-- Convenience method-->
<#macro rows count>
	<#call indexedRows count="${count}">
</#macro>
<#-- User feedback macros -->
<#macro error(text)>
	<p class="errormark"><strong>${text}</strong></p>
</#macro>

<#macro done(text)>
	<p class="donemark"><em>${text}</em></p>
</#macro>
<#macro info(text)>
	<p class="infomark">${text}</em></p>
</#macro>

<#macro warn(text)>
	<p class="warningmark"><strong>${text}</strong></p>
</#macro>
<#macro errorMessage(caption, message)>
	<div class="errormessage">
  		<p>
			<strong>${caption}</strong></p>
			${message}
  		</p>
	</div>
</#macro>

<#macro errorMessageMultiple(caption, message, errors)>
	<div class="errormessage">
  		<p>
			<strong>${caption}</strong></p>
			${message}
            <#if errors?exists>
                <@s.iterator id="e" value="errors">
                    <#assign e = stack.findString('top') />
                    <#call error(e)>
                </@s.iterator>
            </#if>
        </p>
	</div>
</#macro>

<#macro warningMessage(caption, message)>
	<div class="warningmessage">
  		<p>
			<strong>${caption}</strong></p>
			${message}
  		</p>
	</div>
</#macro>

<#macro infoMessage(caption, message)>
	<div class="infomessage">
  		<p>
			<strong>${caption}</strong></p>
			${message}
  	</p>
	</div>
</#macro>

<#macro doneMessage(caption, message)>
	<div class="donemessage">
  		<p>
			<strong>${caption}</strong></p>
		${message}
  	</p>
	</div>
</#macro>

