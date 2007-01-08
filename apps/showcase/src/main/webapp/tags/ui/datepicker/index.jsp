<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <title>Ajax Examples</title>

    <!--// START SNIPPET: common-include-->
    <jsp:include page="/ajax/commonInclude.jsp"/>
    <!--// END SNIPPET: common-include-->

</head>

<body>
<table>
<tr><td>
<s:dropdowndatetimepicker value="2006-10-31" toggleType="wipe" toggleDuration="300" name="test"/>
</td><td>
toggleType="wipe"
</td></tr>
<tr><td>
<s:dropdowndatetimepicker value="2006-07-22" toggleType="explode" toggleDuration="500"/>
</td><td>
toggleType="explode"
</td></tr>
<tr><td>
<s:dropdowndatetimepicker value="2006-06-30" toggleType="fade" toggleDuration="500"/>
</td><td>
toggleType="fade"
</td></tr>
<tr><td>
<s:dropdowndatetimepicker  name="dddp1" value="today" />
</td><td>
With value="today"
</td></tr>
<tr><td>
<s:dropdowndatetimepicker  name="dddp2" language="en-us" />
</td><td>
US format, empty
</td></tr>
<tr><td>
<s:dropdowndatetimepicker  name="dddp3" value="2006-06-26" language="en-us" />
</td><td>
US format with initial date of 2006-06-26
</td></tr>
<tr><td>
<s:dropdowndatetimepicker  name="dddp5" value="1969-04-25" displayFormat="dd/MM/yyyy" />
</td><td>
With initial date of 1969-04-25 and a custom format dd/MM/yyyy
</td></tr>
<tr><td>
<s:dropdowndatetimepicker  name="dddp7" value="2006-06-28" language="de" />
</td><td>
In German
</td></tr>
<tr><td>
<s:dropdowndatetimepicker  name="dddp8" value="2006-06-28" language="nl" />
</td><td>
In Dutch
</td></tr>
<tr><td>
<s:dropdowndatetimepicker  name="dddp12" value="2006-06-26" formatLength="long" language="en-us" />
</td><td>
US format with initial date of 2006-06-26 and long formatting (parse not supported)
</td></tr>
<tr><td>
<s:dropdowndatetimepicker  name="dddp13" value="2006-06-26" formatLength="long" language="de" />
</td><td>
German format with initial date of 2006-06-26 and long formatting (parse not supported)
</table>
<s:include value="../footer.jsp"/>
</body>
</html>
