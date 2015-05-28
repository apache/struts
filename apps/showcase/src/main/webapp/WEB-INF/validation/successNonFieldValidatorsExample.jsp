<%--
    successNonFieldValidatorsExample.jsp
    
    @author tm_jee
    @version $Date$ $Id$
--%>


<%@taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
	<title>Struts2 Showcase - Validation - Success Non Field Validators Example</title>
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
                    <td>Some Text: </td>
                    <td><s:property value="someText" /></td>
                </tr>
                <tr>
                    <td>Some Text Retyped: </td>
                    <td><s:property value="someTextRetype" /></td>
                </tr>
                <tr>
                    <td>Some Text Retyped Again: </td>
                    <td><s:property value="someTextRetypeAgain" /></td>
                </tr>
            </table>
		</div>
	</div>
</div>
</body>
</html>
