<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
	<title>Struts2 Showcase - Token Examples - Example 1</title>
</head>

<body>
<div class="page-header">
	<h1>Token Examples - Example 1</h1>
</div>


<div class="container-fluid">
	<div class="row-fluid">
		<div class="span12">

			<p>
			<b>Example 1:</b> This example illustrates a situation where you can transfer money from
			one account to another. We use the token to prevent double posts so the transfer only
			happens once.
			<p/>

			<p>
			<br/>Balance of source account: <s:property value="#session.balanceSource"/>
			<br/>Balance of destination account: <s:property value="#session.balanceDestination"/>
			<p/>

			<s:form action="transfer">
				<s:token/>
				<s:textfield label="Amount" name="amount" required="true" value="100"/>
				<s:submit value="Transfer money" cssClass="btn btn-primary"/>
			</s:form>

		</div>
	</div>
</div>
</body>
</html>


<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
    <head><title>Token Examples</title></head>

<body>
    <h1>Token Example 1</h1>


</body>
</html>
