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
		<th><a href="${url}">${name}</h></td>
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
                <@ww.iterator id="e" value="errors">
                    <#assign e = stack.findString('top') />
                    <#call error(e)>
                </@ww.iterator>
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

