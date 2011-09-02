<%-- 
    successVisitorValidatorsExample.jsp
    
    @author tm_jee
    @version $Date$ $Id$
--%>



<%@taglib prefix="s" uri="/struts-tags" %>

<html>
    <head><title>Showcase - Validation - SuccessVisitorValidatorsExameple</title></head>
    <body>
        <h1>Success !</h1>
        <table>
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
        
        <s:include value="footer.jsp" />
        
    </body>
</html>

