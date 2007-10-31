<%@taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
<title>Insert title here</title>
</head>
<body>

<table border="1">
<s:iterator value="upload" status="stat">
<tr>
	<td>File <s:property value="%{#stat.index}" /></td>
	<td><s:property value="%{upload[#stat.index]}" /></td>
</tr>
</s:iterator>
</table>


<table border="1">
<s:iterator value="uploadFileName" status="stat">
<tr>
	<td>File Name <s:property value="%{#stat.index}" /></td>
	<td><s:property value="%{uploadFileName[#stat.index]}" /></td>
</tr>	
</s:iterator>
</table>

<table border="1">
<s:iterator value="uploadContentType" status="stat">
<tr>
	<td>Content Type <s:property value="%{#stat.index}" /></td>
	<td><s:property value="%{uploadContentType[#stat.index]}" /></td>
</tr>
</s:iterator>
</table>

</body>
</html>