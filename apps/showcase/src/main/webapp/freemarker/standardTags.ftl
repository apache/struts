<html>
<head>
	<title>Struts2 Showcase - Freemarker - Standard Struts Freemarker Tags</title>
</head>
<body>

<div class="page-header">
	<h1>Standard Struts Freemarker Tags</h1>
</div>

<div class="container-fluid">
	<div class="row-fluid">
		<div class="span12">

			<@s.form action="test">
	            <@s.textfield label="Name" name="name"/>
	            <@s.select label="Birth Month" headerValue="Select Month" list="months" />
			</@s.form>

		</div>
	</div>
</div>
</body>
</html>
