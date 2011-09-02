<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
</head>

<body>
    <p style="border: 1px solid silver; padding: 5px; background: #ffd; text-align: center;">
      Double post. Struts intercepted this request and prevents the action from executing again.
    </p>

    <p/>
    Click here to <s:url var="back" value="/token"/><s:a href="%{back}">return</s:a>.

</body>
</html>
