<%@ taglib prefix="ww" uri="/webwork" %>
<html>
    <head><title>Token Examples (double post)</title></head>

<body>
    <h1>Token Examples</h1>

    The transfer is done at <ww:text name="token.transfer.time"><ww:param value="#session.time"/></ww:text>

    <br/>New balance of source account: <ww:property value="#session.balanceSource"/>
    <br/>New balance of destination account: <ww:property value="#session.balanceDestination"/>

    <p/>
    Try using the browser back button and submit the form again. This should result in a double post
    that WebWork should intercept and handle accordingly.
    <p/>
    For example 3 (session token) you should notice that the date/time stays the same. This interceptor
    catches that this is a double post but doens't display the double post page, but just renders the
    web page result from the first post. 

    <p/>
    Click here to <ww:url id="back" value="/token"/><ww:a href="%{back}">return</ww:a>.

</body>
</html>
