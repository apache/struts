<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
    <title>Ajax Examples</title>
    <jsp:include page="/ajax/commonInclude.jsp"/>
</head>

<script type="text/javascript">
   function handler(widget, node) {
     alert('I will handle this myself!');
	 node.innerHTML = "Done";
   }
</script>

<s:url id="ajaxTest" value="/AjaxTest.action" />

<body>
<s:div
        id="once"
        theme="ajax"
        cssStyle="border: 1px solid yellow;"
        href="%{ajaxTest}"
		handler="handler">
    Initial Content</s:div>

<s:include value="../footer.jsp"/>

</body>
</html>
