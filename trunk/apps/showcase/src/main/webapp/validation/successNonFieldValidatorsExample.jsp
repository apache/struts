<%--
    successNonFieldValidatorsExample.jsp
    
    @author tm_jee
    @version $Date$ $Id$
--%>


<%@taglib prefix="s" uri="/struts-tags" %>

<html>
    <head><title>Showcase - Validation - SuccessNonFieldValidatorsExample</title></head>
    <body>
        <h1>Success !</h1>
        <table>
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
        
        <s:include value="footer.jsp" />
    </body>
</html>

