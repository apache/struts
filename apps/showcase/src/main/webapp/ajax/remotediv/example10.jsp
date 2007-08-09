<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

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

<s:url var="ajaxTest" value="/AjaxTest.action" />

<body>
<sx:div
        id="once"
        cssStyle="border: 1px solid yellow;"
        href="%{ajaxTest}"
		handler="handler">
    Initial Content</sx:div>

<s:include value="../footer.jsp"/>

</body>
</html>
