<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
	<title>Struts2 Showcase - Validation - Store Errors Across Request Example</title>
	<s:head/>
</head>

<body>

<div class="page-header">
	<h1>Store Errors Across Request Example</h1>
</div>

<div class="container-fluid">
	<div class="row-fluid">
		<div class="span12">

			<p>
				This is an example demonstrating the use of MessageStoreInterceptor.
				When this form is submited a redirect is issue both when there's a validation
				error or not. Normally, when a redirect is issue the action messages / errors and
				field errors stored in the action will be lost (due to an action lives
				only as long as a request). With a MessageStoreInterceptor in place and
				configured, the action errors / messages / field errors will be store and
				remains retrieveable even after a redirect.
			</p>

			<table border="1">
				<tr><td>ActionMessages: </td><td></td><s:actionmessage/></tr>
				<tr><td>ActionErrors: </td><td><s:actionerror/></td></tr>
			</table>

			<p>
				<s:form action="submitApplication" namespace="/validation">
					<s:textfield name="name" label="Name" />
					<s:textfield name="age" label="Age" />
					<s:submit cssClass="btn btn-primary"/>
					<s:submit action="cancelApplication" value="%{'Cancel'}" cssClass="btn btn-danger"/>
				</s:form>
			</p>
			<p>
				Try submitting with an invalid age value,
				and note that the browser location changes,
				but validation messages are retained.
				Because of the redirect,
				the input values are not retained.
			</p>

			<s:include value="footer.jsp"/>
		</div>
	</div>
</div>
</body>
</html>
