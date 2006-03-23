<%@ taglib prefix="ww" uri="/webwork" %>

<html>
    <head>
        <title>Enter the Matrix</title>
    </head>
    <body>
        <h1>Enter the Matrix sample</h1>

        <p>
            <fieldset>
                <legend>Matrix Form</legend>
                <ww:form name="helloWorldForm" action="helloMatrixSubmit">
                    <ww:textfield name="hello" label="Enter your name" />
                    <ww:submit />
                </ww:form>
            </fieldset>
        </p>
    </body>
</html>
