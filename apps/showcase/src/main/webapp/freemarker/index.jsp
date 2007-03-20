
<%@taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
    <title>Showcase - Freemarker</title>
</head>
<body>
    
    <ul>
        <li>
            <s:url id="url" action="customFreemarkerManagerDemo" namespace="/freemarker" />
            <s:a href="%{#url}">Demo of usage of a Custom Freemarker Manager</s:a>
            <p/>
            <s:url id="url" action="standardTags" namespace="/freemarker" />
            <s:a href="%{#url}">Demo of Standard Struts Freemarker Tags</s:a>
        </li>
    </ul>

</body>
</html>


