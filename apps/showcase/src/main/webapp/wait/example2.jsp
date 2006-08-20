<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
    <head><title>Execute and Wait Examples</title></head>

<body>
    <h1>Execute and Wait Example 2</h1>

    <b>Example 2:</b> As example 1 but uses a delay of 2000 millis before the wait page is shown. Try simulating with
    a value of 500 millis to see that no wait page is shown at all.

    <s:form action="longProcess2">
        <s:textfield label="Time (millis)" name="time" required="true" value="8000"/>
        <s:submit value="submit"/>
    </s:form>

</body>
</html>
