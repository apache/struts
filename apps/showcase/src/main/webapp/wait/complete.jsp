<%@ taglib prefix="saf" uri="/struts-action" %>
<html>
    <head><title>Execute and Wait Examples</title></head>

<body>
    <h1>The process is complete</h1>

    <b>We have processed your request.</b>
    <p/>
    Click here to <saf:url id="back" value="/wait"/><saf:a href="%{back}">return</saf:a>.

</body>
</html>
