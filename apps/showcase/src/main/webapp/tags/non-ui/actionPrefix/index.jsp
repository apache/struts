
<%@taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
<title>Showcase - Tags - Non-Ui - Action Prefix</title>
</head>
<body>
    <ul><s:url id="url" action="actionPrefixExampleUsingFreemarker" namespace="/tags/non-ui/actionPrefix" />
    <s:a href="%{#url}">Action Prefix Example (Freemarker)</s:a></ul>
</body>
</html>

