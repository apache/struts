<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
    <head><title>Execute and Wait Examples</title></head>

<body>
    <h1>Execute and Wait Example 1</h1>
    
    <b>Example 1:</b> In the form below enter how long time to simulate the process should take.

    <s:form action="longProcess1">
        <s:textfield label="Time (millis)" name="time" required="true" value="7000"/>
        <s:submit value="submit"/>
    </s:form>

</body>
</html>
