<script language="JavaScript" type="text/javascript">
    // Dojo configuration
    djConfig = {
        baseRelativePath: "<@saf.url includeParams='none' value='/struts/dojo' encode='false'/>",
        isDebug: ${parameters.debug},
        bindEncoding: "${parameters.encoding}",
        debugAtAllCosts: true // not needed, but allows the Venkman debugger to work with the includes
    };
</script>
<script language="JavaScript" type="text/javascript"
        src="<@saf.url includeParams='none' value='/struts/dojo/dojo.js' encode='false'/>"></script>
<script language="JavaScript" type="text/javascript"
        src="<@saf.url includeParams='none' value='/struts/simple/dojoRequire.js' encode='false'/>"></script>
