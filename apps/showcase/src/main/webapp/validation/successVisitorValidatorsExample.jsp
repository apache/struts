<%-- 
	successVisitorValidatorsExample.jsp
	
	@author tm_jee
	@version $Date: 2005/12/22 09:18:00 $ $Id: successVisitorValidatorsExample.jsp,v 1.1 2005/12/22 09:18:00 tmjee Exp $
--%>



<%@taglib uri="/struts-action" prefix="saf" %>

<html>
	<head><title>Showcase - Validation - SuccessVisitorValidatorsExameple</title></head>
	<body>
		<h1>Success !</h1>
		<table>
			<tr>
				<td>User Name:</td>
				<td><saf:property value="user.name" /></td>
			</tr>
			<tr>
				<td>User Age:</td>			
				<td><saf:property value="user.age" /></td>
			</tr>
			<tr>
				<td>User Birthday:</td>
				<td><saf:property value="user.birthday" /></td>
			</tr>
		</table>
		
		<saf:include value="footer.jsp" />
		
	</body>
</html>

