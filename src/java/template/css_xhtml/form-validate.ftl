<#if parameters.validate?default(false) == true>
<script src="${base}/webwork/css_xhtml/validation.js" type="text/javascript"></script>
    <#if parameters.onsubmit?exists>
        ${tag.addParameter('onsubmit', "${parameters.onsubmit}; customOnsubmit(); return validateForm_${parameters.id}();")}
    <#else>
        ${tag.addParameter('onsubmit', "customOnsubmit(); return validateForm_${parameters.id}();")}
    </#if>
</#if>
