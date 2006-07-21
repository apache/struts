<%@ taglib prefix="s" uri="/tags" %>

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
                    <s:url id="helloURL" action="helloMatrix" />
                    <s:a href="%{helloURL}">Enter the matrix</s:a>
                </p>
            </fieldset>
        </p>
    </body>
</html>
