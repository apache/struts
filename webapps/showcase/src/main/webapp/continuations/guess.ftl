<!-- START SNIPPET: example -->
<html>
<head>
    <title></title>
</head>

<body>
<#list actionMessages as msg>
    ${msg}
</#list>

<@saf.form action="guess" method="post">
    <@saf.textfield label="Guess" name="guess"/>
    <@saf.submit value="Guess"/>
</@saf.form>
</body>
</html>
<!-- END SNIPPET: example -->
