<%--
    successFieldValidatorsExample.jsp

    @author tm_jee
    @version $Date$ $Id$
--%>

<%@taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
	<title>Struts2 Showcase - Validation - Success Client Side Validators Example</title>
	<s:head/>
</head>
<body>

<div class="page-header">
	<h1>Success !</h1>
</div>

<div class="container-fluid">
	<div class="row">
		<div class="col-md-12">

        <table class="table table-striped table-bordered table-hover table-condensed">
            <tr>
                <td>Required Validator Field:</td>
                <td><s:property value="requiredValidatorField" /></td>
            </tr>
            <tr>
                <td>Required String Validator Field:</td>
                <td><s:property value="requiredStringValidatorField" /></td>
            </tr>
            <tr>
                <td>Integer Validator Field: </td>
                <td><s:property value="integerValidatorField" /></td>
            </tr>
            <tr>
                <td>Date Validator Field: </td>
                <td><s:property value="dateValidatorField" /></td>
            </tr>
            <tr>
                <td>Email Validator Field: </td>
                <td><s:property value="emailValidatorField" /></td>
            </tr>
            <tr>
                <td>URL Validator Field: </td>
                <td><s:property value="urlValidatorField" /></td>
            </tr>
            <tr>
                <td>String Length Validator Field: </td>
                <td><s:property value="stringLengthValidatorField" /></td>
            </tr>
            <tr>
                <td>Regex Validator Field: <s:property value="regexValidatorField" /></td>
                <td>Field Expression Validator Field: <s:property value="fieldExpressionValidatorField" /></td>
            </tr>
        </table>
		</div>
	</div>
</div>
</body>
</html>
