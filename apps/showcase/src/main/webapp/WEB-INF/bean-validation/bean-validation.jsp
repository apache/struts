<%@taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <title>Struts2 Showcase - Validation - Bean Validation Example</title>
    <s:head theme="xhtml"/>
</head>
<body>

<div class="page-header">
    <h1>Bean Validation Examples</h1>
</div>

<div class="container-fluid">
    <div class="row">
        <div class="col-md-12">

            <!-- START SNIPPET: beanValidatationExample -->

            <h3>All Action Errors Will Appear Here</h3>
            <s:actionerror/>
            <hr/>

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

            <s:form action="bean-validation-example" namespace="/bean-validation" method="POST" theme="xhtml">
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

            <!-- END SNIPPET: beanValidatationExample -->
        </div>
    </div>
</div>
</body>
</html>
