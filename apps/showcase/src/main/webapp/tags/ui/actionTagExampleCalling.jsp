<%@taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
<title>Showcase - Tags - UI Tags - Action Tag</title>
</head>
<body>
  <b>Example 1:</b>
  This example calls an action and includes the output on the page
  <p id="example1" style="background-color:yellow;">
    <s:action namespace="/tags/ui" name="actionTagExample" executeResult="true"/>
  </p>
</body>
</html>