<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<!-- START SNIPPET: ajaxValidation -->

<html>
<head>
	<title>Struts2 Showcase - Validation - Ajax</title>
	<sx:head cache="true" compressed="true"/>
</head>

<s:url var="url" namespace="/validation" action="quizAjax"/>

<body>

<div class="page-header">
	<h1>AJAX validation Example</h1>
</div>

<div class="container-fluid">
	<div class="row-fluid">
		<div class="span12">
			<h5>1.Use AJAX validation, and AJAX request (ajaxAfterValidation="true"), if validation succeeds</h5>

			<div id="response" class="well">
				Response goes here
			</div>

			<br/>

			<s:form method="post" theme="xhtml" namespace="/nodecorate" action="quizAjax" id="form">
				<s:textfield label="Name" name="name"/>
				<s:textfield label="Age" name="age"/>
				<s:textfield label="Favorite color" name="answer"/>
				<sx:submit
						validate="true"
						ajaxAfterValidation="true"
						targets="response"
						showLoadingText="false"
						cssClass="btn btn-primary"/>
			</s:form>

			<sx:a
					formId="form"
					validate="true"
					ajaxAfterValidation="true"
					targets="response"
					showLoadingText="false"
					cssClass="btn btn-primary">Submit Form with a link</sx:a>
			<br/><br/>

			<input type="checkbox" id="checkbox">Submit Form listening to an event on an element (check to submit)
			<sx:bind
					id="bind"
					formId="form"
					sources="checkbox"
					validate="true"
					events="onchange"
					ajaxAfterValidation="true"
					targets="response"
					showLoadingText="false"/>

			<br/><br/><br/><br/>

			<h5>2.Use AJAX validation, and regular request (ajaxAfterValidation="false", default), if validation
				succeeds</h5>
			<br/>

			<s:form method="post" theme="xhtml" namespace="/nodecorate" action="quizAjax" id="form2">
				<s:textfield label="Name" name="name"/>
				<s:textfield label="Age" name="age"/>
				<s:textfield label="Favorite color" name="answer"/>
				<sx:submit
						validate="true"
						ajaxAfterValidation="false"
						showLoadingText="false"
						cssClass="btn btn-primary"/>
			</s:form>

			<sx:a
					formId="form2"
					validate="true"
					ajaxAfterValidation="false"
					showLoadingText="false"
					cssClass="btn btn-primary">Submit Form with a link</sx:a>
			<br/><br/>

			<input type="checkbox" id="checkbox2">Submit Form listening to an event on an element (check to submit)
			<sx:bind
					formId="form2"
					sources="checkbox2"
					validate="true"
					events="onchange"
					ajaxAfterValidation="false"
					showLoadingText="false"/>


			<s:include value="footer.jsp"/>
		</div>
	</div>
</div>
</body>
</html>
<!-- END SNIPPET: ajaxValidation -->
