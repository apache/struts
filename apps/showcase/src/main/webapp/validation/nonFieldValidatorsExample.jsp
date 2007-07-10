<%-- 
    nonFieldValidatorsExample.jsp
    
    @author tm_jee
    @version $Date$ $Id$
--%>


<%@taglib prefix="s" uri="/struts-tags" %>

<html>
    <head>
        <title>Showcase - Validation - Non Field Validator Example</title>
        <s:url var="siteCss" value="/validation/validationExamplesStyles.css" includeContext="true" />
        <s:head />
        <!-- link rel="stylesheet" type="text/css" href='<s:property value="%{siteCss}" />'-->
    </head>
    <body>
    
       
       <!-- START SNIPPET: nonFieldValidatorsExample -->
        <s:actionerror />
    
        <s:form method="POST" action="submitNonFieldValidatorsExamples" namespace="/validation">
            <s:textfield name="someText" label="Some Text" />
            <s:textfield name="someTextRetype" label="Retype Some Text" />  
            <s:textfield name="someTextRetypeAgain" label="Retype Some Text Again" />
            <s:submit label="Submit" />
        </s:form>
        
        
        <!--  END SNIPPET: nonFieldValidatorsExample -->
        
        
        <s:include value="footer.jsp" />
    </body>
</html>

