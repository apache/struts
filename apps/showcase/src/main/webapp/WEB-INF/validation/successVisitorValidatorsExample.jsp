<%-- 
    successVisitorValidatorsExample.jsp
    
    @author tm_jee
    @version $Date$ $Id$
--%>


<%@taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
	<title>Struts2 Showcase - Validation - Success Visitor Validators Exameple</title>
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
                    <td>User Name:</td>
                    <td><s:property value="user.name" /></td>
                </tr>
                <tr>
                    <td>User Age:</td>
                    <td><s:property value="user.age" /></td>
                </tr>
                <tr>
                    <td>User Birthday:</td>
                    <td><s:property value="user.birthday" /></td>
                </tr>
            </table>

		</div>
	</div>
</div>
</body>
</html>
