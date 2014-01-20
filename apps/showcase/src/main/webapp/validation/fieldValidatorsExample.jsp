<%-- 
   fieldValidatorExample.jsp
   
   @author tm_jee
   @version $Date$ $Id$
--%>

<%@taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
    <title>Struts2 Showcase - Validation - Field Validators Example</title>
    <s:head theme="xhtml"/>
</head>
<body>

<div class="page-header">
    <h1>Field Validation Examples</h1>
</div>

<div class="container-fluid">
    <div class="row-fluid">
        <div class="span12">

            <!-- START SNIPPET: fieldValidatorsExample -->

            <h3>All Field Errors Will Appear Here</h3>
            <s:fielderror/>
            <hr/>

            <h3>Field Error due to 'Required String Validator Field' Will Appear Here</h3>
            <s:fielderror>
                <s:param value="%{'requiredStringValidatorField'}"/>
            </s:fielderror>
            <hr/>

            <h3>Field Error due to 'String Length Validator Field' Will Appear Here</h3>
            <s:fielderror>
                <s:param>stringLengthValidatorField</s:param>
            </s:fielderror>
            <hr/>

            <s:form action="submitFieldValidatorsExamples" namespace="/validation" method="POST" theme="xhtml">
                <s:textfield label="Required Validator Field" name="requiredValidatorField"/>
                <s:textfield label="Required String Validator Field" name="requiredStringValidatorField"/>
                <s:textfield label="Integer Validator Field" name="integerValidatorField"/>
                <s:textfield label="Date Validator Field" name="dateValidatorField"/>
                <s:textfield label="Email Validator Field" name="emailValidatorField"/>
                <s:textfield label="URL Validator Field" name="urlValidatorField"/>
                <s:textfield label="String Length Validator Field" name="stringLengthValidatorField"/>
                <s:textfield label="Regex Validator Field" name="regexValidatorField"/>
                <s:textfield label="Field Expression Validator Field" name="fieldExpressionValidatorField"/>
                <s:submit label="Submit" cssClass="btn btn-primary"/>
            </s:form>

            <!-- END SNIPPET: fieldValidatorsExample -->

            <s:include value="footer.jsp"/>
        </div>
    </div>
</div>
</body>
</html>
