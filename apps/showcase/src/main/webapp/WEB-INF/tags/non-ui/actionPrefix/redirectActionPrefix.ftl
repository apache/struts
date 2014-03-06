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

			<p>You have come to this page because you used an 'redirect-action' prefix.<p/>
	
			<p>Because this is a <strong>redirect-action</strong>, the text will be lost, due to a redirection
			implies a new request being issued from the client.<p/>

			The text you've enter is ${text?default('')?html}<p/>

			<@s.a href="javascript:history.back();" cssClass="btn btn-info"><i class="icon icon-arrow-left"></i> Back</@s.a>
		</div>
	</div>
</div>
</body>
</html>


