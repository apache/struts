<%@taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
<title>Showcase - Tag - Non UI Tag - AppendIterator Tag</title>
</head>
<body>

    <s:generator id="iterator1" separator="," val="%{iteratorValue1}" />
    <s:generator id="iterator2" separator="," val="%{iteratorValue2}" />
    
    <s:append id="appendedIterator">
        <s:param value="%{#attr.iterator1}" />
        <s:param value="%{#attr.iterator2}" />
    </s:append>
    
    <s:iterator value="#appendedIterator">
        <s:property /><br/>
    </s:iterator>
    
    <s:url value="%{'/tags/non-ui/'}" id="url" /><s:a href="%{#url}">Back To Non-UI Demo</s:a>
    <s:url value="%{'/'}" id="url" /><s:a href="%{#url}">Back To Showcase</s:a>

</body>
</html>
