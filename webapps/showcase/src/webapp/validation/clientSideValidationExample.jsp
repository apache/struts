<%--
   fieldValidatorExample.jsp

   @author tm_jee
   @version $Date: 2006/01/13 16:23:41 $ $Id: clientSideValidationExample.jsp,v 1.1 2006/01/13 16:23:41 rainerh Exp $
--%>

<%@taglib prefix="ww" uri="/webwork" %>

<html>
	<head>
		<title>Showcase - Validation - Field Validators Example</title>
		<ww:url id="siteCss" includeContext="true" value="/validation/validationExamplesStyles.css" />
		<ww:head />
		<!--  link rel="stylesheet" type="text/css" href='<ww:property value="%{siteCss}" />'-->
	</head>
	<body>

	<!-- START SNIPPET: fieldValidatorsExample -->

		<h3>All Field Errors Will Appear Here</h3>
		<ww:fielderror />
		<hr/>

		<h3>Field Error due to 'Required String Validator Field' Will Appear Here</h3>
		<ww:fielderror>
			<ww:param value="%{'requiredStringValidatorField'}" />
		</ww:fielderror>
		<hr/>

		<h3>Field Error due to 'String Length Validator Field' Will Appear Here</h3>
		<ww:fielderror>
			<ww:param>stringLengthValidatorField</ww:param>
		</ww:fielderror>
		<hr/>

		<ww:form action="submitClientSideValidationExample" namespace="/validation" method="POST" validate="true">
			<ww:textfield label="Required Validator Field" name="requiredValidatorField" />
			<ww:textfield label="Required String Validator Field" name="requiredStringValidatorField" />
			<ww:textfield label="Integer Validator Field" name="integerValidatorField" />
			<ww:textfield label="Date Validator Field" name="dateValidatorField" />
			<ww:textfield label="Email Validator Field" name="emailValidatorField" />
            <ww:textfield label="URL Validator Field" name="urlValidatorField" />
            <ww:textfield label="String Length Validator Field" name="stringLengthValidatorField" />
			<ww:textfield label="Regex Validator Field" name="regexValidatorField"/>
			<ww:textfield label="Field Expression Validator Field" name="fieldExpressionValidatorField" />
			<ww:submit label="Submit" />
		</ww:form>

    <!-- END SNIPPET: fieldValidatorsExample -->


		<ww:include value="footer.jsp" />
	</body>
</html>
