<%-- 
	nonFieldValidatorsExample.jsp
	
	@author tm_jee
	@version $Date: 2005/12/24 09:22:28 $ $Id: nonFieldValidatorsExample.jsp,v 1.3 2005/12/24 09:22:28 tmjee Exp $
--%>


<%@taglib prefix="saf" uri="/struts-action" %>

<html>
	<head>
		<title>Showcase - Validation - Non Field Validator Example</title>
		<saf:url id="siteCss" value="/validation/validationExamplesStyles.css" includeContext="true" />
		<saf:head />
		<!-- link rel="stylesheet" type="text/css" href='<saf:property value="%{siteCss}" />'-->
	</head>
	<body>
	
	   
	   <!-- START SNIPPET: nonFieldValidatorsExample -->
		<saf:actionerror />
	
		<saf:form method="POST" action="submitNonFieldValidatorsExamples" namespace="/validation">
			<saf:textfield name="someText" label="Some Text" />
			<saf:textfield name="someTextRetype" label="Retype Some Text" />  
			<saf:textfield name="someTextRetypeAgain" label="Retype Some Text Again" />
			<saf:submit label="Submit" />
		</saf:form>
		
		
		<!--  END SNIPPET: nonFieldValidatorsExample -->
		
		
		<saf:include value="footer.jsp" />
	</body>
</html>

