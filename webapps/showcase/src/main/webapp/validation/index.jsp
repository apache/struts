<%--
	index.jsp

	@author tm_jee
	@version $Date: 2006/01/13 16:23:44 $ $Id: index.jsp,v 1.4 2006/01/13 16:23:44 rainerh Exp $
--%>

<%@taglib uri="/webwork" prefix="ww" %>

<html>
	<head>
		<title>Showcase - Validation</title>
	</head>
	<body>
		<h1>Validation Examples</h1>
		
		<ww:url id="quizBasic" namespace="/validation" action="quizBasic" method="input"/>
        <ww:url id="quizClient" namespace="/validation" action="quizClient" method="input"/>
        <ww:url id="quizClientCss" namespace="/validation" action="quizClientCss" method="input"/>
		<ww:url id="quizAjax" namespace="/validation" action="quizAjax" method="input"/>
		<ww:url id="fieldValidatorUrl" action="showFieldValidatorsExamples" namespace="/validation" />
		<ww:url id="nonFieldValidatorUrl" action="showNonFieldValidatorsExamples" namespace="/validation" />
		<ww:url id="visitorValidatorUrl" action="showVisitorValidatorsExamples" namespace="/validation" />
		<ww:url id="clientSideValidationUrl" action="clientSideValidationExample" namespace="/validation" />
		<ww:url id="backToShowcase" action="showcase" namespace="/" />
		
		<ul>
			<li><ww:a href="%{quizBasic}">Validation (basic)</ww:a></li>
            <li><ww:a href="%{quizClient}">Validation (client)</ww:a></li>
            <li><ww:a href="%{quizClientCss}">Validation (client using css_xhtml theme)</ww:a></li>
        	<li><ww:a href="%{quizAjax}">Validation (ajax)</ww:a></li>
			<li><ww:a href="%{fieldValidatorUrl}">Field Validators</ww:a></li>
			<li><ww:a href="%{nonFieldValidatorUrl}">Non Field Validator</ww:a></li>
			<li><ww:a href="%{visitorValidatorUrl}">Visitor Validator</ww:a></li>
			<li><ww:a href="%{clientSideValidationUrl}">Client side validation using JavaScript</ww:a></li>
			<li><ww:a href="%{backToShowcase}">Back To Showcase</ww:a>
		</ul>
	</body>
</html>

