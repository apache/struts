<%@ taglib prefix="saf" uri="/struts-action" %>

<html>
    <head>
        <title>Welcome</title>
    </head>
    <body>
        <h1>Welcome to the starter webapp</h1>

        <p>
            <fieldset>
                <legend>Example action</legend>
                <p>
                    <saf:url id="helloURL" action="helloMatrix" />
                    <saf:a href="%{helloURL}">Enter the matrix</saf:a>
                </p>
            </fieldset>
        </p>
    </body>
</html>
