
<%@taglib prefix="s" uri="/tags" %>

<html>
<head>
    <title>Showcase - Freemarker</title>
</head>
<body>
    
    <ul>
        <li>
            <s:url id="url" action="customFreemarkerManagerDemo" namespace="/freemarker" />
            <s:a href="%{#url}">Demo of usage of a Custom Freemarker Manager</s:a>
        </li>
    </ul>

</body>
</html>


