<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
    <head><title>Execute and Wait Examples</title></head>

<body>
    <h1>The process is complete</h1>

    <b>We have processed your request.</b>
    <p/>
    Click here to <s:url id="back" value="/wait"/><s:a href="%{back}">return</s:a>.

</body>
</html>
