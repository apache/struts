<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
	<title>Struts2 Showcase - Execute and Wait Examples - Example 2</title>
</head>

<body>
<div class="page-header">
	<h1>Execute and Wait Examples - Example 2</h1>
</div>


<div class="container-fluid">
	<div class="row-fluid">
		<div class="span12">

			<p><b>Example 2:</b> As example 1 but uses a delay of 2000 millis before the wait page is shown. Try simulating with
				a value of 500 millis to see that no wait page is shown at all.</p>

			<s:form action="longProcess2">
				<s:textfield label="Time (millis)" name="time" required="true" value="8000"/>
				<s:submit value="submit" cssClass="btn btn-primary"/>
			</s:form>

		</div>
	</div>
</div>
</body>
</html>
