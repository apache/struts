<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<html>
<head>
    <title>Ajax examples - tabbled panel</title>

    <jsp:include page="/ajax/commonInclude.jsp"/>
</head>

<body>

    <sx:tabbedpanel id="test2" cssStyle="width: 500px; height: 300px;" doLayout="true">
        <sx:div label="test1"  >
          I'm a Tab!!!
        </sx:div >
        <sx:div  id="middle" label="test2"   >
          I'm the other Tab!!!
        </sx:div >
    </sx:tabbedpanel>
				  
<s:include value="../footer.jsp"/>

</body>
</html>
