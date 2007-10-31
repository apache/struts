<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<html>
<head>
    <title>UI Tags Example</title>
    <sx:head extraLocales="en-us,nl-nl,de-de"/>
</head>
<body>
<table>
<sx:datetimepicker label="toggleType='wipe'" type="time" value="%{'10:30'}" toggleType="wipe" toggleDuration="300"/>
<sx:datetimepicker label="toggleType='explode'" type="time" value="%{'13:00'}" toggleType="explode" toggleDuration="500"/>
<sx:datetimepicker label="toggleType='fade'" type="time" value="%{'13:00'}" toggleType="fade" toggleDuration="500"/>
<sx:datetimepicker label="With value='today'" name="dddp4" type="time" value="%{'today'}" />
<sx:datetimepicker label="US format, empty" name="dddp5" type="time" language="en-us" />
<sx:datetimepicker label="US format, 13:00 hours" name="dddp6" type="time" value="%{'13:00'}" language="en-us" />
<sx:datetimepicker label="In German" name="dddp7" type="time" value="%{'13:00'}" language="de" />
<sx:datetimepicker label="In Dutch" name="dddp8" type="time" value="%{'13:00'}" language="nl" />
</table>
</body>
</html>
