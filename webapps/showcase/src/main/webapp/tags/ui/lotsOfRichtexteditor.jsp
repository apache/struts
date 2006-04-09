<%@taglib prefix="saf" uri="/struts-action" %>

<html>
<head>
	<title>Showcase - Tags - UI Tags - Rich Text Editor </title>
	<saf:head />
</head>
<body>
	<saf:form action="lotsOfRichtexteditorSubmit" method="post" namespace="/tags/ui">
		<saf:richtexteditor 
			toolbarCanCollapse="false"
			width="700"
			label="Description 1" 
			name="description1" 
			/>
			
		<saf:richtexteditor 
			toolbarCanCollapse="false"
			width="700"
			label="Description 2"
			name="description2" />
			
		<saf:richtexteditor
			width="700"
			label="Description 3"
			name="description3" />
			
		<saf:richtexteditor
			width="700"
			label="Description 4"
			name="description4" />
			
		<saf:submit />
	</saf:form>
</body>
</html>
