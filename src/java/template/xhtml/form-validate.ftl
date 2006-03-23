<#if parameters.validate?default(false) == true>
	<script src="${base}/webwork/xhtml/validation.js"></script>
	<#if parameters.onsubmit?exists>
		${tag.addParameter('onsubmit', "${parameters.onsubmit}; customOnsubmit(); return validateForm_${parameters.id}();")}
	<#else>
		${tag.addParameter('onsubmit', "customOnsubmit(); return validateForm_${parameters.id}();")}
	</#if>
</#if>
