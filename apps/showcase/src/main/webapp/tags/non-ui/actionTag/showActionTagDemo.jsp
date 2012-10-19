<%@taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
	<title>Struts2 Showcase - Non-Ui Tag - Action Tag </title>
</head>
<body>

<div class="page-header">
	<h1>Non-Ui Tag - Action Tag</h1>
</div>

<div class="container-fluid">
	<div class="row-fluid">
		<div class="span12">

			<div class="well">
				<h2> This is Not - Included by the Action Tag</h2>
			</div>


			<!-- lets include the first page many times -->
			<div class="well">
				<s:action name="includePage" namespace="/tags/non-ui/actionTag" executeResult="true" />
				<s:action name="includePage" namespace="/tags/non-ui/actionTag" executeResult="true" />
				<s:action name="includePage" namespace="/tags/non-ui/actionTag" executeResult="true" />
			</div>


			<!-- lets include the second page many times -->
			<div class="well">
				<s:action name="includePage2" namespace="/tags/non-ui/actionTag" executeResult="true" />
				<s:action name="includePage2" namespace="/tags/non-ui/actionTag" executeResult="true" />
				<s:action name="includePage2" namespace="/tags/non-ui/actionTag" executeResult="true" />
			</div>


			<!--  lets include the third page many time -->
			<div class="well">
				<s:action name="includePage3" namespace="/tags/non-ui/actionTag" executeResult="true" />
				<s:action name="includePage3" namespace="/tags/non-ui/actionTag" executeResult="true" />
				<s:action name="includePage3" namespace="/tags/non-ui/actionTag" executeResult="true" />
			</div>


			<s:url var="url" action="lookAtSource" namespace="/tags/non-ui/actionTag" />
			<s:a href="%{#url}" cssClass="btn btn-info">Source</s:a>
		</div>
	</div>
</div>
</body>
</html>

