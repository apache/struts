<%@ taglib prefix="saf" uri="/struts-action" %>

<html>
    <head>
        <title>Enter the Matrix</title>
    </head>
    <body>
        <h1>Enter the Matrix sample</h1>

        <p>
            <fieldset>
                <legend>Matrix Form</legend>
                <saf:form name="helloWorldForm" action="helloMatrixSubmit">
                    <saf:textfield name="hello" label="Enter your name" />
                    <saf:submit />
                </saf:form>
            </fieldset>
        </p>
    </body>
</html>
