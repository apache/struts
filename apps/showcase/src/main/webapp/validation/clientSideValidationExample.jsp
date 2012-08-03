<%--
   fieldValidatorExample.jsp

   @author tm_jee
   @version $Date$ $Id$
--%>

<%@taglib prefix="s" uri="/struts-tags" %>

<html>
    <head>
        <title>Showcase - Validation - Field Validators Example</title>
        <s:url var="siteCss" includeContext="true" value="/validation/validationExamplesStyles.css" />
        <s:head />
        <!--  link rel="stylesheet" type="text/css" href='<s:property value="%{siteCss}" />'-->
    </head>
    <body>

    <!-- START SNIPPET: fieldValidatorsExample -->

        <h3>All Field Errors Will Appear Here</h3>
        <s:fielderror />
        <hr/>

        <h3>Field Error due to 'Required String Validator Field' Will Appear Here</h3>
        <s:fielderror>
            <s:param value="%{'requiredStringValidatorField'}" />
        </s:fielderror>
        <hr/>

        <h3>Field Error due to 'String Length Validator Field' Will Appear Here</h3>
        <s:fielderror>
            <s:param>stringLengthValidatorField</s:param>
        </s:fielderror>
        <hr/>

        <s:form action="submitClientSideValidationExample" namespace="/validation" method="POST" validate="true">
            <s:textfield label="Required Validator Field" name="requiredValidatorField" />
            <s:textfield label="Required String Validator Field" name="requiredStringValidatorField" />
            <s:textfield label="Integer Validator Field" name="integerValidatorField" />
            <s:textfield label="Date Validator Field" name="dateValidatorField" />
            <s:textfield label="Email Validator Field" name="emailValidatorField" />
            <s:textfield label="URL Validator Field" name="urlValidatorField" />
            <s:textfield label="String Length Validator Field" name="stringLengthValidatorField" />
            <s:textfield label="Regex Validator Field" name="regexValidatorField"/>
            <s:textfield label="Field Expression Validator Field" name="fieldExpressionValidatorField" />
            <s:submit label="Submit" />
        </s:form>

    <!-- END SNIPPET: fieldValidatorsExample -->


        <s:include value="footer.jsp" />
    </body>
</html>
