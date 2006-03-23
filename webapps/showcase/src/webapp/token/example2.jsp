<%@ taglib prefix="ww" uri="/webwork" %>
<html>
    <head><title>Token Examples</title></head>

<body>
    <h1>Token Example 2</h1>

    <b>Example 2:</b> This example illustrates a situation where you can transfer money from
    one account to another. We use the token to prevent double posts so the transfer only
    happens once. This action will redirect after you have submitted the form.
    <p/>

    <br/>Balance of source account: <ww:property value="#session.balanceSource"/>
    <br/>Balance of destination account: <ww:property value="#session.balanceDestination"/>
    <p/>

    <ww:form action="transfer2">
        <ww:token/>
        <ww:textfield label="Amount" name="amount" required="true" value="200"/>
        <ww:submit value="Transfer money"/>
    </ww:form>

</body>
</html>
