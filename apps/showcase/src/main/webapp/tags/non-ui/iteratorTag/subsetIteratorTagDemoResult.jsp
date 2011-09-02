<%@taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
<title>Showcase - Tags - Non UI Tags - Subset Tag</title>
</head>
<body>

    <s:generator var="iterator" val="%{iteratorValue}" separator="," />

    <s:subset count="%{count}" start="%{start}" source="%{#attr.iterator}" >
        <s:iterator>
            <s:property /><br/>
        </s:iterator>
    </s:subset>

    <s:url value="%{'/tags/non-ui/'}" var="url" /><s:a href="%{#url}">Back To Non-UI Demo</s:a>
    <s:url value="%{'/'}" var="url" /><s:a href="%{#url}">Back To Showcase</s:a>

</body>
</html>
