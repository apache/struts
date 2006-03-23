<%@ taglib prefix="ww" uri="/webwork" %>
<html>
<head>
    <title>AJAX-based remote DIV tag</title>
    <%@ include file="/ajax/commonInclude.jsp" %>
</head>

<body>

<h2>Examples</h2>

<p>
    <ol>
        <li>
            <a href="example1.jsp">A simple DIV that refreshes only once</a>
        </li>

        <li>
            <a href="example2.jsp?url=/AjaxTest.action">A simple DIV that updates every 2 seconds</a>
        </li>

        <li>
            <a href="example3.jsp?period=3000">A simple DIV that obtains the update freq (3 secs) from the value
                stack/action</a>
        </li>

        <li>
            <a href="example4.jsp">A simple DIV that updates every 5 seconds with loading text and reloading text</a>
        </li>

        <li>
            <a href="example5.jsp">A simple DIV's that cannot contact the server</a>
        </li>

        <li>
            <a href="example6.jsp">A simple DIV's that cannot contact the server and displays the transport error
                message</a>
        </li>

        <li>
            <a href="example7.jsp">A div that calls the server, and JS in the resulting page is executed</a>
        </li>

    </ol>


</p>

<ww:include value="../footer.jsp"/>

</body>
</html>
