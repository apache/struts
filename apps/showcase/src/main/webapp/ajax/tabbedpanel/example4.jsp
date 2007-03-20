<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
	<title>Example 4</title>
	<jsp:include page="/ajax/commonInclude.jsp"/>
	<link rel="stylesheet" type="text/css" href="<s:url value="/struts/tabs.css"/>" />
</head>
<body>
	<s:url id="panel1url" action="panel1" namespace="/nodecorate" />
	<s:url id="panel2url" action="panel2" namespace="/nodecorate" />
	<s:url id="panel3url" action="panel3" namespace="/nodecorate" />
	<s:tabbedPanel id="tabbedpanel" >
		<s:div id="panel1" label="Panel1" href="%{#panel1url}" theme="ajax" />
		<s:div id="panel2" label="Panel2" href="%{#panel2url}" theme="ajax"  />
		<s:div id="panel3" label="Panel3" href="%{#panel3url}" theme="ajax" />
	</s:tabbedPanel>
</body>
</html>

