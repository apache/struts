<%@ taglib prefix="saf" uri="/struts-action" %>
<html>
<head>
</head>

<body>
    <p style="border: 1px solid silver; padding: 5px; background: #ffd; text-align: center;">
      Double post. Struts intercepted this request and prevents the action from executing again.
    </p>

    <p/>
    Click here to <saf:url id="back" value="/token"/><saf:a href="%{back}">return</saf:a>.

</body>
</html>
