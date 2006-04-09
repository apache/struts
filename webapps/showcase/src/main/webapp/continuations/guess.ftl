<!-- START SNIPPET: example -->
<html>
<head>
    <title></title>
</head>

<body>
<#list actionMessages as msg>
    ${msg}
</#list>

<@ww.form action="guess" method="post">
    <@ww.textfield label="Guess" name="guess"/>
    <@ww.submit value="Guess"/>
</@ww.form>
</body>
</html>
<!-- END SNIPPET: example -->
