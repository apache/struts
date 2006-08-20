<%@ taglib prefix="s" uri="/struts-tags" %>

<html>
    <head>
        <title>Enter the Matrix</title>
    </head>
    <body>
        <h1>Enter the Matrix sample</h1>

        <p>
            <fieldset>
                <legend>Matrix Form</legend>
                <s:form name="helloWorldForm" action="helloMatrixSubmit">
                    <s:textfield name="hello" label="Enter your name" />
                    <s:submit />
                </s:form>
            </fieldset>
        </p>
    </body>
</html>
