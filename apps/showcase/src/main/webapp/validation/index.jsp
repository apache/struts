<%--
	index.jsp

	@author tm_jee
	@version $Date: 2006/01/13 16:23:44 $ $Id: index.jsp,v 1.4 2006/01/13 16:23:44 rainerh Exp $
--%>

<%@taglib uri="/struts-action" prefix="saf" %>

<html>
	<head>
		<title>Showcase - Validation</title>
	</head>
	<body>
		<h1>Validation Examples</h1>
		
		<saf:url id="quizBasic" namespace="/validation" action="quizBasic" method="input"/>
        <saf:url id="quizClient" namespace="/validation" action="quizClient" method="input"/>
        <saf:url id="quizClientCss" namespace="/validation" action="quizClientCss" method="input"/>
		<saf:url id="quizAjax" namespace="/validation" action="quizAjax" method="input"/>
		<saf:url id="fieldValidatorUrl" action="showFieldValidatorsExamples" namespace="/validation" />
		<saf:url id="nonFieldValidatorUrl" action="showNonFieldValidatorsExamples" namespace="/validation" />
		<saf:url id="visitorValidatorUrl" action="showVisitorValidatorsExamples" namespace="/validation" />
		<saf:url id="clientSideValidationUrl" action="clientSideValidationExample" namespace="/validation" />
		<saf:url id="backToShowcase" action="showcase" namespace="/" />
		
		<ul>
			<li><saf:a href="%{quizBasic}">Validation (basic)</saf:a></li>
            <li><saf:a href="%{quizClient}">Validation (client)</saf:a></li>
            <li><saf:a href="%{quizClientCss}">Validation (client using css_xhtml theme)</saf:a></li>
        	<li><saf:a href="%{quizAjax}">Validation (ajax)</saf:a></li>
			<li><saf:a href="%{fieldValidatorUrl}">Field Validators</saf:a></li>
			<li><saf:a href="%{nonFieldValidatorUrl}">Non Field Validator</saf:a></li>
			<li><saf:a href="%{visitorValidatorUrl}">Visitor Validator</saf:a></li>
			<li><saf:a href="%{clientSideValidationUrl}">Client side validation using JavaScript</saf:a></li>
			<li><saf:a href="%{backToShowcase}">Back To Showcase</saf:a>
		</ul>
	</body>
</html>

