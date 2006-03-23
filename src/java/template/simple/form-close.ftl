</form>


<script>
	function customOnsubmit() {
	<#-- 
	   Code that will auto-select optiontransferselect elements upon containing form submission
	--><#t/>
	<#if (parameters.optiontransferselectIds?if_exists?size > 0)><#t/>
		// Code that will auto-select optiontransferselect elements upon containing form submission
		<#assign selectTagIds = parameters.optiontransferselectIds.keySet()/><#t/>
		<#list selectTagIds as tmpId><#t/>
			// auto-select optiontrasferselect (left side) with id ${tmpId}
			var selectObj = document.getElementById("${tmpId}");
			selectAllOptions(selectObj);
			selectUnselectMatchingOptions(selectObj, null, "unselect", false, "key");
			<#if parameters.optiontransferselectIds.get(tmpId)?exists><#t/>
				<#assign selectTagHeaderKey = parameters.optiontransferselectIds.get(tmpId).toString()/><#t/>
				selectUnselectMatchingOptions(selectObj, "${selectTagHeaderKey}", "unselect", false, "key");
			</#if><#t/>
		</#list><#t/>
	</#if><#t/>
	<#if (parameters.optiontransferselectDoubleIds?if_exists?size > 0) ><#t/>		
		<#assign doubleSelectTagIds = parameters.optiontransferselectDoubleIds.keySet()/><#t/>
		<#list doubleSelectTagIds as tmpDoubleId><#t/>
			// auto-select optiontransferselect (right side) with id ${tmpDoubleId}
			var doubleSelectObj = document.getElementById("${tmpDoubleId}");
			selectAllOptions(doubleSelectObj);
			selectUnselectMatchingOptions(doubleSelectObj, null, "unselect", false, "key");
			<#if parameters.optiontransferselectDoubleIds.get(tmpDoubleId)?exists><#t/>
				<#assign doubleSelectTagHeaderKey = parameters.optiontransferselectDoubleIds.get(tmpDoubleId)/><#t/>
				selectUnselectMatchingOptions(doubleSelectObj, "${doubleSelectTagHeaderKey}", "unselect", false, "key");
			</#if><#t/>		
		</#list><#t/>
	</#if><#t/>
	
	<#--
	   Code that will auto select updownselect elements upon its containing form submission
	--><#t/>
	<#if (parameters.updownselectIds?if_exists?size > 0)><#t/>
		// Code that will auto select updownselect elements upon its containing form submission
		<#assign updownselectTagIds = parameters.updownselectIds.keySet() /><#t/>
		<#list updownselectTagIds as tmpUpdownselectTagId><#t/>
			var updownselectObj = document.getElementById("${tmpUpdownselectTagId}");
			selectAllOptions(updownselectObj);
			selectUnselectMatchingOptions(updownselectObj, null, "unselect", false, "key");
			<#if parameters.updownselectIds.get(tmpUpdownselectTagId)?exists><#t/>
				<#assign updownselectHeaderKey = parameters.updownselectIds.get(tmpUpdownselectTagId) /><#t/>
				selectUnselectMatchingOptions(updownselectObj, "${updownselectHeaderKey}", "unselect", false, "key");
			</#if><#t/>
		</#list><#t/>
	</#if><#t/>
	}
</script>


<#-- 
 Code that will add javascript needed for tooltips
--><#t/>
<#if parameters.hasTooltip?default(false)><#t/>
	<#lt/><!-- javascript that is needed for tooltips -->
	<#lt/><script language="JavaScript" type="text/javascript" src="<@ww.url value='/webwork/tooltip/wz_tooltip.js' encode='false' />"></script>
</#if><#t/>
