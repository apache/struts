<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
    <head><title>Token Examples (double post)</title></head>

<body>
    <h1>Token Examples</h1>

    The transfer is done at <s:text name="token.transfer.time"><s:param value="#session.time"/></s:text>

    <br/>New balance of source account: <s:property value="#session.balanceSource"/>
    <br/>New balance of destination account: <s:property value="#session.balanceDestination"/>

    <p/>
    Try using the browser back button and submit the form again. This should result in a double post
    that Struts should intercept and handle accordingly.
    <p/>
    For example 3 (session token) you should notice that the date/time stays the same. This interceptor
    catches that this is a double post but doens't display the double post page, but just renders the
    web page result from the first post. 

    <p/>
    Click here to <s:url var="back" value="/token"/><s:a href="%{back}">return</s:a>.

</body>
</html>
