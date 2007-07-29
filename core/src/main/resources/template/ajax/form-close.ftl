<#--
/*
 * $Id: pom.xml 560558 2007-07-28 15:47:10Z apetrelli $
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

<#if (parameters.customOnsubmitEnabled?default(false))>
<script type="text/javascript">
<#-- 
  Enable auto-select of optiontransferselect tag's entries upon containing form's 
  submission.
-->
dojo.require("dojo.event.connect");
<#if (parameters.optiontransferselectIds?if_exists?size > 0)>
	var containingForm = document.getElementById("${parameters.id}");
	<#assign selectObjIds = parameters.optiontransferselectIds.keySet() />
	<#list selectObjIds as selectObjectId>
		dojo.event.connect(containingForm, "onsubmit", 
			function(evt) {
				var selectObj = document.getElementById("${selectObjectId}");
				<#if parameters.optiontransferselectIds.get(selectObjectId)?exists>
					<#assign selectTagHeaderKey = parameters.optiontransferselectIds.get(selectObjectId)/>
					selectAllOptionsExceptSome(selectObj, "key", "${selectTagHeaderKey}");
				<#else>
					selectAllOptionsExceptSome(selectObj, "key", "");
				</#if>
			});
	</#list>
</#if>
<#if (parameters.inputtransferselectIds?if_exists?size > 0)>
	var containingForm = document.getElementById("${parameters.id}");
	<#assign selectObjIds = parameters.inputtransferselectIds.keySet() />
	<#list selectObjIds as selectObjectId>
		dojo.event.connect(containingForm, "onsubmit",
			function(evt) {
				var selectObj = document.getElementById("${selectObjectId}");
				<#if parameters.inputtransferselectIds.get(selectObjectId)?exists>
					<#assign selectTagHeaderKey = parameters.inputtransferselectIds.get(selectObjectId)/>
					selectAllOptionsExceptSome(selectObj, "key", "${selectTagHeaderKey}");
				<#else>
					selectAllOptionsExceptSome(selectObj, "key", "");
				</#if>
			});
	</#list>
</#if>
<#if (parameters.optiontransferselectDoubleIds?if_exists?size > 0)>
	var containingForm = document.getElementById("${parameters.id}");
	<#assign selectDoubleObjIds = parameters.optiontransferselectDoubleIds.keySet() />
	<#list selectDoubleObjIds as selectObjId>
		dojo.event.connect(containingForm, "onsubmit", 
			function(evt) {
				var selectObj = document.getElementById("${selectObjId}");
				<#if parameters.optiontransferselectDoubleIds.get(selectObjId)?exists>
					<#assign selectTagHeaderKey = parameters.optiontransferselectDoubleIds.get(selectObjId)/>
					selectAllOptionsExceptSome(selectObj, "key", "${selectTagHeaderKey}");
				<#else>
					selectAllOptionsExceptSome(selectObj, "key", "");
				</#if>
			});
	</#list>
</#if>


<#--
	Enable auto-select of all elements of updownselect tag upon its containing form
	submission
-->
<#if (parameters.updownselectIds?if_exists?size > 0)>
	var containingForm = document.getElementById("${parameters.id}");
	<#assign tmpIds = parameters.updownselectIds.keySet() />
	<#list tmpIds as tmpId>
		dojo.event.connect(containingForm, "onsubmit", 
			function(evt) {
				var updownselectObj = document.getElementById("${tmpId}");
				<#if parameters.updownselectIds.get(tmpId)?exists>
					<#assign tmpHeaderKey = parameters.updownselectIds.get(tmpId) />
					selectAllOptionsExceptSome(updownselectObj, "key", "${tmpHeaderKey}");
				<#else>
					selectAllOptionsExceptSome(updownselectObj, "key", "");
				</#if>
			});
	</#list>
</#if>
</script>
</#if>

<#-- 
 Code that will add javascript needed for tooltips
--><#t/>
	<#lt/><!-- javascript that is needed for tooltips -->
	<#lt/><script type="text/javascript">dojo.require("dojo.widget.Tooltip");dojo.require("dojo.fx.html");</script>

</table>
</form>
