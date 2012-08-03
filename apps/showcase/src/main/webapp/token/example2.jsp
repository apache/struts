<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
    <head><title>Token Examples</title></head>

<body>
    <h1>Token Example 2</h1>

    <b>Example 2:</b> This example illustrates a situation where you can transfer money from
    one account to another. We use the token to prevent double posts so the transfer only
    happens once. This action will redirect after you have submitted the form.
    <p/>

    <br/>Balance of source account: <s:property value="#session.balanceSource"/>
    <br/>Balance of destination account: <s:property value="#session.balanceDestination"/>
    <p/>

    <s:form action="transfer2">
        <s:token/>
        <s:textfield label="Amount" name="amount" required="true" value="200"/>
        <s:submit value="Transfer money"/>
    </s:form>

</body>
</html>
