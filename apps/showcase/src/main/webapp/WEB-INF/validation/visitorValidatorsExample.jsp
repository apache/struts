<%-- 
    visitorValidatorsExample.jsp
    
    @author tm_jee
    @version $Date$ $Id$
--%>


<%@taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
	<title>Struts2 Showcase - Validation - Visitor Validators Example</title>
	<s:head/>
</head>
<body>

<div class="page-header">
	<h1>Visitor Validators Example</h1>
</div>

<div class="container-fluid">
	<div class="row-fluid">
		<div class="span12">

			<!-- START SNIPPET: visitorValidatorsExample -->

			<s:fielderror cssClass="alert alert-error"/>

			<s:form method="POST" action="submitVisitorValidatorsExamples" namespace="/validation">
				<s:textfield name="user.name" label="User Name"/>
				<s:textfield name="user.age" label="User Age"/>
				<s:textfield name="user.birthday" label="Birthday"/>
				<s:submit label="Submit" cssClass="btn btn-primary"/>
			</s:form>

			<!--  END SNIPPET: visitorValidatorsExample -->

			<s:include value="footer.jsp"/>
		</div>
	</div>
</div>
</body>
</html>
