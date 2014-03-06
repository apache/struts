<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <title>AJAX-based remote DIV tag</title>
    <%@ include file="/WEB-INF/ajax/commonInclude.jsp" %>
</head>

<body>

<h2>Examples</h2>

<p>
    <ol>
        <li>
            <a href="example1.jsp">A simple DIV that refreshes only once</a>
        </li>

		<li>
            <a href="example10.jsp">A simple DIV that uses a custom handler</a>
        </li>

        <li>
            <a href="example2.jsp?url=/AjaxTest.action">A simple DIV that updates every 2 seconds, with indicator</a>
        </li>

        <li>
            <a href="example4.jsp">A simple DIV that updates every 5 seconds with loading text and reloading text and delay</a>
        </li>

        <li>
            <a href="example5.jsp">A simple DIV's that cannot contact the server, with fixed error message</a>
        </li>


        <li>
            <a href="example7.jsp">A div that calls the server, and JS in the resulting page is executed</a>
        </li>

        <li>
            <a href="example8.jsp">A div that will listen to events to refresh and start/stop autoupdate, and gets highlighted in red (when it loads)</a>
        </li>

		<li>
            <a href="example9.jsp">A div that will listen to events to refresh and start/stop autoupdate, publish notifyTopics</a>
        </li>

    </ol>


</p>

<s:include value="../footer.jsp"/>

</body>
</html>
