<%--
	successNonFieldValidatorsExample.jsp
	
	@author tm_jee
	@version $Date: 2005/12/22 09:17:59 $ $Id: successNonFieldValidatorsExample.jsp,v 1.1 2005/12/22 09:17:59 tmjee Exp $
--%>


<%@taglib uri="/struts-action" prefix="saf" %>

<html>
	<head><title>Showcase - Validation - SuccessNonFieldValidatorsExample</title></head>
	<body>
		<h1>Success !</h1>
		<table>
			<tr>
				<td>Some Text: </td>
				<td><saf:property value="someText" /></td>
			</tr>
			<tr>
				<td>Some Text Retyped: </td>
				<td><saf:property value="someTextRetype" /></td>
			</tr>
			<tr>
				<td>Some Text Retyped Again: </td>
				<td><saf:property value="someTextRetypeAgain" /></td>
			</tr>
		</table>
		
		<saf:include value="footer.jsp" />
	</body>
</html>

