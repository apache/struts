<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
	<title>Struts2 Showcase - Execute and Wait Examples - Example 1</title>
</head>

<body>
<div class="page-header">
	<h1>Execute and Wait Examples - Example 1</h1>
</div>


<div class="container-fluid">
	<div class="row-fluid">
		<div class="span12" style="text-align: center;">

			<p><b>Example 1:</b> In the form below enter how long time to simulate the process should take.</p>

			<s:form action="longProcess1">
				<s:textfield label="Time (millis)" name="time" required="true" value="7000"/>
				<s:submit value="submit" cssClass="btn btn-primary"/>
			</s:form>

		</div>
	</div>
</div>
</body>
</html>
