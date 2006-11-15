<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<head>
    <title>Ajax Examples</title>

    <!--// START SNIPPET: common-include-->
    <jsp:include page="/ajax/commonInclude.jsp"/>
    <!--// END SNIPPET: common-include-->

</head>

<body>
<p>toggleTye="wipe":</p>
	<input dojoType="dropdowndatepicker" value="2006-10-31" containerToggle="wipe" containerToggleDuration="300">
<p>toggleTye="explode":</p>
	<input dojoType="dropdowndatepicker" value="2006-07-22" containerToggle="explode" containerToggleDuration="500">
<p>toggleTye="fade":</p>
	<input dojoType="dropdowndatepicker" value="2006-06-30" containerToggle="fade" containerToggleDuration="500">
<p>With value="today"</p>
	<s:dropdowndatetimepicker  name="dddp1" value="today" />
<p>US format, empty</p>
	<s:dropdowndatetimepicker  name="dddp2" language="en-us" />
<p>US format with initial date of 2006-06-26</p>
	<s:dropdowndatetimepicker  name="dddp3" value="2006-06-26" language="en-us" />
<p>With initial date of 1969-04-25 and a custom format dd/MM/yyyy</p>
	<s:dropdowndatetimepicker  name="dddp5" value="1969-04-25" displayFormat="dd/MM/yyyy" />
<p>In German:</p>
	<s:dropdowndatetimepicker  name="dddp7" value="2006-06-28" language="de" />
<p>In Dutch:</p>
	<s:dropdowndatetimepicker  name="dddp8" value="2006-06-28" language="nl" />
<p>US format with initial date of 2006-06-26 and long formatting (parse not supported)</p>
	<s:dropdowndatetimepicker  name="dddp12" value="2006-06-26" formatLength="long" language="en-us" />
<p>German format with initial date of 2006-06-26 and long formatting (parse not supported)</p>
	<s:dropdowndatetimepicker  name="dddp13" value="2006-06-26" formatLength="long" language="de" />
<p>Value sent as RFC3339</p>
	<s:dropdowndatetimepicker  name="dddp14" saveFormat="rfc" value="2006-06-26" language="en-us" />
<p>Custom save format: yyyy!dd!mm</p>
	<s:dropdowndatetimepicker  name="dddp15" saveFormat="yyyy!dd!MM" value="2006-06-26" language="en-us" />
<p>Value sent as time since Unix Epoch</p>
	<s:dropdowndatetimepicker  name="dddp16" saveFormat="posix" value="2006-06-26" language="en-us" />

<s:include value="../footer.jsp"/>
</body>
</html>
