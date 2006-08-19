<html>
    <head><title>Token Examples</title></head>

<body>
    <h1>Token Example 4</h1>

    <b>Example 4:</b> This example illustrates a situation where you can transfer money from
    one account to another. We use the token to prevent double posts so the transfer only
    happens once. This page is rendered using freemarker. See the xwork-token.xml where
    we must also use the createSession interceptor to be sure that a HttpSession exists
    when freemarker renders this webpage, otherwise the @s.token tag causes an exception
    while rendering the page.
    <p/>

    <br/>Balance of source account: <@s.property value="#session.balanceSource"/>
    <br/>Balance of destination account: <@s.property value="#session.balanceDestination"/>
    <p/>

    <@s.form action="transfer4">
        <@s.token/>
        <@s.textfield label="Amount" name="amount" required="true" value="400"/>
        <@s.submit value="Transfer money"/>
    </@s.form>

</body>
</html>
