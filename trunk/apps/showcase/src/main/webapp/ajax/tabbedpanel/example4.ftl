<html>
<head>
	<title>Example 4</title>
	<@s.head theme="ajax" debug="false" />
	<link rel="stylesheet" type="text/css" href="<@s.url value="/struts/tabs.css"/>" />
</head>
<body>
	<@s.url id="panel1url" action="panel1" namespace="/nodecorate" includeContext="false" />
	<@s.url id="panel2url" action="panel2" namespace="/nodecorate" includeContext="false"/>
	<@s.url id="panel3url" action="panel3" namespace="/nodecorate" includeContext="false"/>
	<@s.tabbedPanel id="tabbedpanel" >
		<@s.panel id="panel1" tabName="Panel1" remote="true" href="%{#panel1url}" theme="ajax" />
		<@s.panel id="panel2" tabName="Panel2" remote="true" href="%{#panel2url}" theme="ajax"  />
		<@s.panel id="panel3" tabName="Panel3" remote="true" href="%{#panel3url}" theme="ajax" />
	</@s.tabbedPanel>
</body>
</html>

