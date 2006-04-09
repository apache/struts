<%--
   fieldValidatorExample.jsp

   @author tm_jee
   @version $Date: 2006/01/13 16:23:41 $ $Id: clientSideValidationExample.jsp,v 1.1 2006/01/13 16:23:41 rainerh Exp $
--%>

<%@taglib prefix="saf" uri="/struts-action" %>

<html>
	<head>
		<title>Showcase - Validation - Field Validators Example</title>
		<saf:url id="siteCss" includeContext="true" value="/validation/validationExamplesStyles.css" />
		<saf:head />
		<!--  link rel="stylesheet" type="text/css" href='<saf:property value="%{siteCss}" />'-->
	</head>
	<body>

	<!-- START SNIPPET: fieldValidatorsExample -->

		<h3>All Field Errors Will Appear Here</h3>
		<saf:fielderror />
		<hr/>

		<h3>Field Error due to 'Required String Validator Field' Will Appear Here</h3>
		<saf:fielderror>
			<saf:param value="%{'requiredStringValidatorField'}" />
		</saf:fielderror>
		<hr/>

		<h3>Field Error due to 'String Length Validator Field' Will Appear Here</h3>
		<saf:fielderror>
			<saf:param>stringLengthValidatorField</saf:param>
		</saf:fielderror>
		<hr/>

		<saf:form action="submitClientSideValidationExample" namespace="/validation" method="POST" validate="true">
			<saf:textfield label="Required Validator Field" name="requiredValidatorField" />
			<saf:textfield label="Required String Validator Field" name="requiredStringValidatorField" />
			<saf:textfield label="Integer Validator Field" name="integerValidatorField" />
			<saf:textfield label="Date Validator Field" name="dateValidatorField" />
			<saf:textfield label="Email Validator Field" name="emailValidatorField" />
            <saf:textfield label="URL Validator Field" name="urlValidatorField" />
            <saf:textfield label="String Length Validator Field" name="stringLengthValidatorField" />
			<saf:textfield label="Regex Validator Field" name="regexValidatorField"/>
			<saf:textfield label="Field Expression Validator Field" name="fieldExpressionValidatorField" />
			<saf:submit label="Submit" />
		</saf:form>

    <!-- END SNIPPET: fieldValidatorsExample -->


		<saf:include value="footer.jsp" />
	</body>
</html>
