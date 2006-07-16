<html>
<head>
	<title>Example 4</title>
	<@saf.head theme="ajax" debug="false" />
	<link rel="stylesheet" type="text/css" href="<@saf.url value="/struts/tabs.css"/>" />
</head>
<body>
	<@saf.url id="panel1url" action="panel1" namespace="/nodecorate" includeContext="false" />
	<@saf.url id="panel2url" action="panel2" namespace="/nodecorate" includeContext="false"/>
	<@saf.url id="panel3url" action="panel3" namespace="/nodecorate" includeContext="false"/>
	<@saf.tabbedPanel id="tabbedpanel" >
		<@saf.panel id="panel1" tabName="Panel1" remote="true" href="%{#panel1url}" theme="ajax" /> 
		<@saf.panel id="panel2" tabName="Panel2" remote="true" href="%{#panel2url}" theme="ajax"  />
		<@saf.panel id="panel3" tabName="Panel3" remote="true" href="%{#panel3url}" theme="ajax" />
	</@saf.tabbedPanel>
</body>
</html>

