<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <title>UI Tags Example</title>
    <s:head />
</head>
<body>
<table>
<s:datetimepicker label="toggleType='wipe'" value="2006-10-31" toggleType="wipe" toggleDuration="300" name="test"/>
<s:datetimepicker label="toggleType='explode'" value="2006-07-22" toggleType="explode" toggleDuration="500"/>
<s:datetimepicker label="toggleType='fade'" value="2006-06-30" toggleType="fade" toggleDuration="500"/>
<s:datetimepicker label="With value='today'"  name="dddp1" value="today" />
<s:datetimepicker label="US format, empty" name="dddp2" language="en-us" />
<s:datetimepicker label="US format with initial date of 2006-06-26" name="dddp3" value="2006-06-26" language="en-us" />
<s:datetimepicker label="With initial date of 1969-04-25 and a custom format dd/MM/yyyy" name="dddp5" value="25/04/1969" displayFormat="dd/MM/yyyy" />
<s:datetimepicker label="In German" name="dddp7" value="2006-06-28" language="de" />
<s:datetimepicker label="In Dutch"  name="dddp8" value="2006-06-28" language="nl" />
<s:datetimepicker label="US format with initial date of 2006-06-26 and long formatting (parse not supported)" name="dddp12" value="2006-06-26" formatLength="long" language="en-us" />
<s:datetimepicker label="German format with initial date of 2006-06-26 and long formatting (parse not supported)" name="dddp13" value="2006-06-26" formatLength="long" language="de" />
</table>
</body>
</html>
