<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
    <title>Ajax examples - tabbled panel</title>

    <jsp:include page="/ajax/commonInclude.jsp"/>
    <link rel="stylesheet" type="text/css" href="<s:url value="/struts/tabs.css"/>">
    
    
</head>

<body>

				<s:tabbedPanel id="test2" theme="simple" cssStyle="width: 500px; height: 300px;" doLayout="true">
                      <s:div id="left" label="test1" theme="ajax" >
                          I'm a Tab!!!
                      </s:div >
                      <s:div  id="middle" label="test2"  theme="ajax" >
                          I'm the other Tab!!!
                      </s:div >
                     
                  </s:tabbedPanel>
				  
				

<s:include value="../footer.jsp"/>

</body>
</html>
