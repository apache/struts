<script language="JavaScript" type="text/javascript">
    // Dojo configuration
    djConfig = {
        baseRelativePath: "<@s.url includeParams='none' value='/struts/dojo' includeParams="none" encode='false'/>",
        isDebug: ${parameters.debug?default(false)},
        bindEncoding: "${parameters.encoding}",
        debugAtAllCosts: true // not needed, but allows the Venkman debugger to work with the includes
    };
</script>
<script language="JavaScript" type="text/javascript"
        src="<@s.url value='/struts/dojo/dojo.js' includeParams='none' encode='false'  />"></script>
<script language="JavaScript" type="text/javascript"
        src="<@s.url value='/struts/ajax/dojoRequire.js' includeParams='none' encode='false'  />"></script>
<script language="JavaScript" type="text/javascript"
        src="<@s.url value='/struts/CommonFunctions.js' includeParams='none' encode='false'/>"></script>
