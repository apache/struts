<%@taglib prefix="saf" uri="/struts-action" %>

<html>
<head>
<title>Showcase - Non-Ui Tag - Action Tag </title>
</head>
<body>

<div style="margin: 5px; border: solid 1px; ">
<h1> This is Not - Included by the Action Tag</h1>
</div>


<!-- lets include the first page many times -->
<div style="margin: 5px; border: solid 1px; ">
<saf:action name="includePage" namespace="/tags/non-ui/actionTag" executeResult="true" />
<saf:action name="includePage" namespace="/tags/non-ui/actionTag" executeResult="true" />
<saf:action name="includePage" namespace="/tags/non-ui/actionTag" executeResult="true" />
</div>


<!-- lets include the second page many times -->
<div style="margin: 5px; border: solid 1px; ">
<saf:action name="includePage2" namespace="/tags/non-ui/actionTag" executeResult="true" />
<saf:action name="includePage2" namespace="/tags/non-ui/actionTag" executeResult="true" />
<saf:action name="includePage2" namespace="/tags/non-ui/actionTag" executeResult="true" />
</div>


<!--  lets include the third page many time -->
<div style="margin: 5px; margin: 5px; border: solid 1px; ">
<saf:action name="includePage3" namespace="/tags/non-ui/actionTag" executeResult="true" />
<saf:action name="includePage3" namespace="/tags/non-ui/actionTag" executeResult="true" />
<saf:action name="includePage3" namespace="/tags/non-ui/actionTag" executeResult="true" />
</div>


<saf:url id="url" action="lookAtSource" namespace="/tags/non-ui/actionTag" />
<saf:a href="%{#url}">Source</saf:a>

</body>
</html>

