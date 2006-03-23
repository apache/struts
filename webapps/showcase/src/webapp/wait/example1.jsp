<%@ taglib prefix="ww" uri="/webwork" %>
<html>
    <head><title>Execute and Wait Examples</title></head>

<body>
    <h1>Execute and Wait Example 1</h1>
    
    <b>Example 1:</b> In the form below enter how long time to simulate the process should take.

    <ww:form action="longProcess1">
        <ww:textfield label="Time (millis)" name="time" required="true" value="7000"/>
        <ww:submit value="submit"/>
    </ww:form>

</body>
</html>
