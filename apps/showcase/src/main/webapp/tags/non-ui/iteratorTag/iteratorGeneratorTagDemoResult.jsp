<%@taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
<title>Showcase - Tag - Non Ui Tag - Iterator Generator Tag Demo</title>
</head>
<body>

    <s:generator val="%{value}" separator="%{separator}" count="%{count}">
        <s:iterator value="%{top}">
            <s:property /><br/>
        </s:iterator>
    </s:generator>

    <s:url value="%{'/tags/non-ui/'}" var="url" /><s:a href="%{#url}">Back To Non-UI Demo</s:a>
    <s:url value="%{'/'}" var="url" /><s:a href="%{#url}">Back To Showcase</s:a>

</body>
</html>
