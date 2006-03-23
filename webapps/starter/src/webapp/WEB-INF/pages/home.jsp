<%@ taglib prefix="ww" uri="/webwork" %>

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
                    <ww:url id="helloURL" action="helloMatrix" />
                    <ww:a href="%{helloURL}">Enter the matrix</ww:a>
                </p>
            </fieldset>
        </p>
    </body>
</html>
