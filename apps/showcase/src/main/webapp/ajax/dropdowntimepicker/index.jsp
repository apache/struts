<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<head>
    <title>Ajax Examples</title>

    <!--// START SNIPPET: common-include-->
    <jsp:include page="/ajax/commonInclude.jsp"/>
    <!--// END SNIPPET: common-include-->

</head>

<body>
toggleTye="wipe":
	<s:dropdowndatetimepicker type="time" value="10:30" toggleType="wipe" toggleDuration="300"/> 
<br/>

toggleTye="explode": 
	<s:dropdowndatetimepicker type="time" value="13:00" toggleType="explode" toggleDuration="500"/> 
<br/>

toggleTye="fade":
	<s:dropdowndatetimepicker type="time" value="13:00" toggleType="fade" toggleDuration="500"/>
<br/>

With value="today":
	<s:dropdowndatetimepicker type="time" name="dddp1" value="today" />
<br/>

US format, empty 
	<s:dropdowndatetimepicker  type="time" name="dddp2" language="en-us" />
<br/>

In German:
	<s:dropdowndatetimepicker type="time" name="dddp7"  language="de" />
<br/>

In Dutch: 
	<s:dropdowndatetimepicker type="time" name="dddp8"  language="nl" />
<br/>
Value sent as RFC3339
	<s:dropdowndatetimepicker type="time" name="dddp14" saveFormat="rfc" value="01:00" language="en-us" />
<br/>

Value sent as time since Unix Epoch
	<s:dropdowndatetimepicker type="time" name="dddp16" saveFormat="posix" value="3:45" language="en-us" />
<br/>


</body>
</html>
