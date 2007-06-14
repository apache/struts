<#include "/${parameters.templateDir}/css_xhtml/control-close.ftl" />
<#include "/${parameters.templateDir}/simple/form-close.ftl" />
<#include "/${parameters.templateDir}/xhtml/form-close-validate.ftl" />
<#if parameters.focusElement?if_exists != "">
<script type="text/javascript">
    StrutsUtils.addOnLoad(function() {
        var element = document.getElementById("${parameters.focusElement?html}");
        if(element) {
            element.focus();
        }
    });
</script>
</#if>
