<%@taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
<title>Showcase - Tags - Non UI Tag - MergeIterator Tag</title>
</head>
<body>

    <s:generator var="iterator1" val="%{iteratorValue1}" separator="," />
    <s:generator var="iterator2" val="%{iteratorValue2}" separator="," />
    
    <s:merge var="mergedIterator">
        <s:param value="%{#attr.iterator1}" />
        <s:param value="%{#attr.iterator2}" />
    </s:merge>
    
    <s:iterator value="%{#mergedIterator}">
        <s:property /><br/>
    </s:iterator>

    <s:url value="%{'/tags/non-ui/'}" var="url" /><s:a href="%{#url}">Back To Non-UI Demo</s:a>
    <s:url value="%{'/'}" var="url" /><s:a href="%{#url}">Back To Showcase</s:a>

</body>
</html>
