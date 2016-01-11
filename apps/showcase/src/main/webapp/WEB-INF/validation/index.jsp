<%--
    index.jsp

    @author tm_jee
    @version $Date$ $Id$
--%>

<%@taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
	<title>Struts2 Showcase - Validation</title>
</head>
<body>

<div class="page-header">
	<h1>Validation Examples</h1>
</div>

<div class="container-fluid">
	<div class="row-fluid">
		<div class="span12">

			<s:url var="quizBasic" namespace="/validation" action="quizBasic" method="input"/>
			<s:url var="quizClient" namespace="/validation" action="quizClient" method="input"/>
			<s:url var="quizClientCss" namespace="/validation" action="quizClientCss" method="input"/>
			<s:url var="quizAjax" namespace="/validation" action="quizAjax" method="input"/>
			<s:url var="fieldValidatorUrl" action="showFieldValidatorsExamples" namespace="/validation"/>
			<s:url var="nonFieldValidatorUrl" action="showNonFieldValidatorsExamples" namespace="/validation"/>
			<s:url var="visitorValidatorUrl" action="showVisitorValidatorsExamples" namespace="/validation"/>
			<s:url var="clientSideValidationUrl" action="clientSideValidationExample" namespace="/validation"/>
			<s:url var="backToShowcase" action="showcase" namespace="/"/>
			<s:url var="storeMessageAcrossRequestExample" namespace="/validation" action="storeErrorsAcrossRequestExample"/>
			<s:url var="ajaxFormSubmitAction" namespace="/validation" action="ajaxFormSubmit!input"/>

			<ul>
				<li><s:a href="%{fieldValidatorUrl}">Field Validators</s:a></li>
				<li><s:a href="%{clientSideValidationUrl}">Field Validators with client-side JavaScript</s:a></li>
				<li><s:a href="%{nonFieldValidatorUrl}">Non Field Validator</s:a></li>
				<li><s:a href="%{storeMessageAcrossRequestExample}">Store across request using MessageStoreInterceptor (Example)</s:a></li>
				<li><s:a href="%{quizAjax}">Validation (ajax)</s:a></li>
				<li><s:a href="%{quizBasic}">Validation (basic)</s:a></li>
				<li><s:a href="%{quizClient}">Validation (client)</s:a></li>
				<li><s:a href="%{quizClientCss}">Validation (client using css_xhtml theme)</s:a></li>
				<li><s:a href="%{visitorValidatorUrl}">Visitor Validator</s:a></li>
				<li><s:a href="%{ajaxFormSubmitAction}">AJAX Form Submit</s:a></li>
			</ul>
		</div>
	</div>
</div>
</body>
</html>

