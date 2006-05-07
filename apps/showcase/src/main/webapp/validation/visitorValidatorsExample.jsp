<%-- 
	visitorValidatorsExample.jsp
	
	@author tm_jee
	@version $Date: 2005/12/24 09:22:28 $ $Id: visitorValidatorsExample.jsp,v 1.3 2005/12/24 09:22:28 tmjee Exp $
--%>


<%@taglib uri="/struts-action" prefix="saf" %>

<html>
<head><title>Showcase - Validation - VisitorValidatorsExample </title>
		<saf:url id="siteCss" value="/validation/validationExamplesStyles.css" includeContext="true" />
		<saf:head />
		<!-- link rel="stylesheet" type="text/css" href='<saf:property value="%{siteCss}" />' -->
</head>
<body>

	<!-- START SNIPPET: visitorValidatorsExample -->
	
	<saf:fielderror />

	<saf:form method="POST" action="submitVisitorValidatorsExamples" namespace="/validation">
		<saf:textfield name="user.name" label="User Name" />
		<saf:textfield name="user.age" label="User Age" />
		<saf:textfield name="user.birthday" label="Birthday" />
		<saf:submit label="Submit" />
	</saf:form>
	
	<!--  END SNIPPET: visitorValidatorsExample -->
	
	
	<saf:include value="footer.jsp" />
</body>
</html>

