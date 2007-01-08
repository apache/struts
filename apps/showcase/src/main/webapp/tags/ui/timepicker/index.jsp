<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <title>UI Tags Example</title>
    <s:head />
</head>
<body>
<table>
<s:datetimepicker label="toggleType='wipe'" type="time" value="10:30" toggleType="wipe" toggleDuration="300"/>
<s:datetimepicker label="toggleType='explode'" type="time" value="13:00" toggleType="explode" toggleDuration="500"/>
<s:datetimepicker label="toggleType='fade'" type="time" value="13:00" toggleType="fade" toggleDuration="500"/>
<s:datetimepicker label="With value='today'" type="time" name="dddp1" value="today" />
<s:datetimepicker label="US format, empty" type="time" name="dddp2" language="en-us" />
<s:datetimepicker label="In German" type="time" name="dddp7"  language="de" />
<s:datetimepicker label="In Dutch" type="time" name="dddp8"  language="nl" />
</table>
</body>
</html>
