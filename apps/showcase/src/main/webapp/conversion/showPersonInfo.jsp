<%@taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Showcase - Conversion - Populate Object into Struts action List</title>
</head>
<body>

<s:iterator value="persons" status="status">
    <s:label label="%{'SET '+#status.index+' Name'}" value="%{name}" /><br/>
    <s:label label="%{'SET '+#status.index+' Age'}" value="%{age}" /><br/>
</s:iterator>

</body>
</html>

