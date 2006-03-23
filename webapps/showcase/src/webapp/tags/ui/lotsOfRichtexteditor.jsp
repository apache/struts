<%@taglib prefix="ww" uri="/webwork" %>

<html>
<head>
	<title>Showcase - Tags - UI Tags - Rich Text Editor </title>
	<ww:head />
</head>
<body>
	<ww:form action="lotsOfRichtexteditorSubmit" method="post" namespace="/tags/ui">
		<ww:richtexteditor 
			toolbarCanCollapse="false"
			width="700"
			label="Description 1" 
			name="description1" 
			/>
			
		<ww:richtexteditor 
			toolbarCanCollapse="false"
			width="700"
			label="Description 2"
			name="description2" />
			
		<ww:richtexteditor
			width="700"
			label="Description 3"
			name="description3" />
			
		<ww:richtexteditor
			width="700"
			label="Description 4"
			name="description4" />
			
		<ww:submit />
	</ww:form>
</body>
</html>
