<!-- START SNIPPET: example -->
<html>
<head>
    <title></title>
</head>

<body>
<#list actionMessages as msg>
    ${msg}
</#list>

<@s.form action="guess" method="post">
    <@s.textfield label="Guess" name="guess"/>
    <@s.submit value="Guess"/>
</@s.form>
</body>
</html>
<!-- END SNIPPET: example -->
