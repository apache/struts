<%@ taglib prefix="ww" uri="/webwork" %>
<html>
    <head><title>Token Examples</title></head>

<body>
    <h1>Token Example 3</h1>

    <b>Example 3:</b> This example illustrates a situation where you can transfer money from
    one account to another. We use the token to prevent double posts so the transfer only
    happens once. This example uses the token session based interceptor and redirect after post.
    <p/>

    <br/>Balance of source account: <ww:property value="#session.balanceSource"/>
    <br/>Balance of destination account: <ww:property value="#session.balanceDestination"/>
    <p/>

    <ww:form action="transfer3">
        <ww:token/>
        <ww:textfield label="Amount" name="amount" required="true" value="300"/>
        <ww:submit value="Transfer money"/>
    </ww:form>

</body>
</html>
