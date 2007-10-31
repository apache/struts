<%@taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
<title>Showcase - Fileupload</title>
</head>
<body>

<ul>
	<li>
		<s:url var="url" action="upload" namespace="/fileupload" />
		<s:a href="%{#url}">Single File Upload</s:a>
	</li>
	<li>
		<s:url var="url" action="multipleUploadUsingList" namespace="/fileupload" />
		<s:a href="%{#url}">Multiple File Upload (List)</s:a>
	
	</li>
	<li>
		<s:url var="url" action="multipleUploadUsingArray" namespace="/fileupload" />
		<s:a href="%{#url}">Multiple File Upload (Array)</s:a>
	</li>
</ul>

</body>
</html>