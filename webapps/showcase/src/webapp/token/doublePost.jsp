<%@ taglib prefix="ww" uri="/webwork" %>
<html>
<head>
</head>

<body>
    <p style="border: 1px solid silver; padding: 5px; background: #ffd; text-align: center;">
      Double post. WebWork intercepted this request and prevents the action from executing again.
    </p>

    <p/>
    Click here to <ww:url id="back" value="/token"/><ww:a href="%{back}">return</ww:a>.

</body>
</html>
