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
</form>

<#if (attributes.customOnsubmitEnabled??)>
<@s.script>
<#--
  Enable auto-select of optiontransferselect tag's entries upon containing form's
  submission.
-->
<#if (attributes.optiontransferselectIds!?size > 0)>
	var containingForm = document.getElementById("${attributes.id?js_string}");
	<#assign selectObjIds = attributes.optiontransferselectIds.keySet() />
	<#list selectObjIds as selectObjectId>
		StrutsUtils.addEventListener(containingForm, "submit",
			function(evt) {
				var selectObj = document.getElementById("${selectObjectId?js_string}");
				<#if attributes.optiontransferselectIds.get(selectObjectId)??>
					<#assign selectTagHeaderKey = attributes.optiontransferselectIds.get(selectObjectId)/>
					selectAllOptionsExceptSome(selectObj, "key", "${selectTagHeaderKey?js_string}");
				<#else>
					selectAllOptionsExceptSome(selectObj, "key", "");
				</#if>
			}, true);
	</#list>
</#if>
<#if (attributes.inputtransferselectIds!?size > 0)>
	var containingForm = document.getElementById("${attributes.id?js_string}");
	<#assign selectObjIds = attributes.inputtransferselectIds.keySet() />
	<#list selectObjIds as selectObjectId>
		StrutsUtils.addEventListener(containingForm, "submit",
			function(evt) {
				var selectObj = document.getElementById("${selectObjectId?js_string}");
				<#if attributes.inputtransferselectIds.get(selectObjectId)??>
					<#assign selectTagHeaderKey = attributes.inputtransferselectIds.get(selectObjectId)/>
					selectAllOptionsExceptSome(selectObj, "key", "${selectTagHeaderKey?js_string}");
				<#else>
					selectAllOptionsExceptSome(selectObj, "key", "");
				</#if>
			}, true);
	</#list>
</#if>
<#if (attributes.optiontransferselectDoubleIds!?size > 0)>
	var containingForm = document.getElementById("${attributes.id?js_string}");
	<#assign selectDoubleObjIds = attributes.optiontransferselectDoubleIds.keySet() />
	<#list selectDoubleObjIds as selectObjId>
		StrutsUtils.addEventListener(containingForm, "submit",
			function(evt) {
				var selectObj = document.getElementById("${selectObjId?js_string}");
				<#if attributes.optiontransferselectDoubleIds.get(selectObjId)??>
					<#assign selectTagHeaderKey = attributes.optiontransferselectDoubleIds.get(selectObjId)/>
					selectAllOptionsExceptSome(selectObj, "key", "${selectTagHeaderKey?js_string}");
				<#else>
					selectAllOptionsExceptSome(selectObj, "key", "");
				</#if>
			}, true);
	</#list>
</#if>


<#--
	Enable auto-select of all elements of updownselect tag upon its containing form
	submission
-->
<#if (attributes.updownselectIds!?size > 0)>
	var containingForm = document.getElementById("${attributes.id?js_string}");
	<#assign tmpIds = attributes.updownselectIds.keySet() />
	<#list tmpIds as tmpId>
		StrutsUtils.addEventListener(containingForm, "submit",
			function(evt) {
				var updownselectObj = document.getElementById("${tmpId?js_string}");
				<#if attributes.updownselectIds.get(tmpId)??>
					<#assign tmpHeaderKey = attributes.updownselectIds.get(tmpId) />
					selectAllOptionsExceptSome(updownselectObj, "key", "${tmpHeaderKey?js_string}");
				<#else>
					selectAllOptionsExceptSome(updownselectObj, "key", "");
				</#if>
			}, true);
	</#list>
</#if>
</@s.script>
</#if>

<#include "/${attributes.templateDir}/${attributes.expandTheme}/form-close-tooltips.ftl" />
