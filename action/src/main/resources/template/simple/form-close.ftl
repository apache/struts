</form>

<#if (parameters.customOnsubmitEnabled?if_exists)>
<script>
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
				selectAllOptions(selectObj);
				selectUnselectMatchingOptions(selectObj, null, "unselect", false, "key");
				<#if parameters.optiontransferselectIds.get(selectObjectId)?exists>
					<#assign selectTagHeaderKey = parameters.optiontransferselectIds.get(selectObjectId)/>
					selectUnselectMatchingOptions(selectObj, "${selectTagHeaderKey}", "unselect", false, "key");
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
				selectAllOptions(selectObj);
				selectUnselectMatchingOptions(selectObj, null, "unselect", false, "key");
				<#if parameters.optiontransferselectDoubleIds.get(selectObjId)?exists>
					<#assign selectTagHeaderKey = parameters.optiontransferselectDoubleIds.get(selectObjId)/>
					selectUnselectMatchingOptions(selectObj, "${selectTagHeaderKey}", "unselect", false, "key");
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
				selectAllOptions(updownselectObj);
				selectUnselectMatchingOptions(updownselectObj, null, "unselect", false, "key");
				<#if parameters.updownselectIds.get(tmpId)?exists>
					<#assign tmpHeaderKey = parameters.updownselectIds.get(tmpId) />
					selectUnselectMatchingOptions(updownselectObj, "${tmpHeaderKey}", "unselect", false, "key");
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
	<#lt/><script language="JavaScript" type="text/javascript">dojo.require("dojo.widget.html.Tooltip");dojo.require("dojo.fx.html");</script>
