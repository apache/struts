<%@taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>
<html>
<head>
	<title>Struts2 Showcase - UI Tags - Datepicker Tag</title>
	<sx:head extraLocales="en-us,nl-nl,de-de" />
</head>
<body>
<div class="page-header">
	<h1>UI Tags - Datepicker Tag</h1>
</div>

<div class="container-fluid">
	<div class="row-fluid">
		<div class="span12">
			<table>
				<sx:datetimepicker label="toggleType='wipe'" value="%{'2006-10-31'}" toggleType="wipe" toggleDuration="300" name="test"/>
				<sx:datetimepicker label="toggleType='explode'" value="%{'2006-07-22'}" toggleType="explode" toggleDuration="500" id="dp2"/>
				<sx:datetimepicker label="toggleType='fade'" value="%{'2006-06-30'}" toggleType="fade" toggleDuration="500"/>
				<sx:datetimepicker label="With value='today'"  name="dddp1" value="%{'today'}" />
				<sx:datetimepicker label="US format, empty" name="dddp2" language="en-us" />
				<sx:datetimepicker label="US format with initial date of 2006-06-26" name="dddp3" value="%{'2006-06-26'}" language="en-us" />
				<sx:datetimepicker label="With initial date of 1969-04-25 and a custom format dd/MM/yyyy" name="dddp5" value="%{'25/04/1969'}" displayFormat="dd/MM/yyyy" />
				<sx:datetimepicker label="In German" name="dddp7" value="%{'2006-06-28'}" language="de-de" />
				<sx:datetimepicker label="In Dutch"  name="dddp8" value="%{'2006-06-28'}" language="nl-nl" />
				<sx:datetimepicker label="US format with initial date of 2006-06-26 and long formatting (parse not supported)" name="dddp12" value="%{'2006-06-26'}" formatLength="long" language="en-us" />
				<sx:datetimepicker label="German format with initial date of 2006-06-26 and long formatting (parse not supported)" name="dddp13" value="%{'2006-06-26'}" formatLength="long" language="de" />
			</table>
		</div>
	</div>
</div>
</body>
</html>
