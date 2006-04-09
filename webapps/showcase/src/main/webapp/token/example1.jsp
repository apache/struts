<%@ taglib prefix="saf" uri="/struts-action" %>
<html>
    <head><title>Token Examples</title></head>

<body>
    <h1>Token Example 1</h1>

    <b>Example 1:</b> This example illustrates a situation where you can transfer money from
    one account to another. We use the token to prevent double posts so the transfer only
    happens once.
    <p/>

    <br/>Balance of source account: <saf:property value="#session.balanceSource"/>
    <br/>Balance of destination account: <saf:property value="#session.balanceDestination"/>
    <p/>

    <saf:form action="transfer">
        <saf:token/>
        <saf:textfield label="Amount" name="amount" required="true" value="100"/>
        <saf:submit value="Transfer money"/>
    </saf:form>

</body>
</html>
