<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <title>TimePicker tag</title>
    <%@ include file="/ajax/commonInclude.jsp" %>
</head>

<body>

<h2>Examples</h2>

Normal English-US timepicker:
<s:timepicker language="en-us"/>

<br>
Set time to current time (useDefaultTime="true")
<s:timepicker id="tp0" useDefaultTime="true"/>

<br>
Set minutes to current time (useDefaultTime="true" and useDefaultMinutes="true")
<s:timepicker id="tp1" useDefaultTime="true" useDefaultMinutes="true"/>

<br>
Set time to 9:43(value="9:43")
<s:timepicker id="tp2" value="9:43"/>

<br>
Set time to 1:00PM(value="13:00")
<s:timepicker id="tp3" value="13:00"/>

<br>
<s:include value="../footer.jsp"/>

</body>
</html>
