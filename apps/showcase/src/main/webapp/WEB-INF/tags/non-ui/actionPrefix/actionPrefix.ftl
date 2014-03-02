<html>
<head>
	<title>Struts2 Showcase - Non UI Tags - Action Prefix (Freemarker)</title>
</head>
<body>
<div class="page-header">
	<h1>Non Ui Tag - Action Prefix (Freemarker)</h1>
</div>

<div class="container-fluid">
	<div class="row-fluid">
		<div class="span12">

			<p>You have come to this page because you used an <strong>action</strong> prefix.<p/>

			<p>The text you've entered is ${text?default('')?html}<p/>

			<@s.a href="javascript:history.back();" cssClass="btn btn-info"><i class="icon icon-arrow-left"></i> Back</@s.a>
		</div>
	</div>
</div>
</body>
</html>


