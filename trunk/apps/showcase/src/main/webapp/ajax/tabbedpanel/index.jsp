<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <title>Tabbed Panes</title>
    <%@ include file="/ajax/commonInclude.jsp" %>
</head>

<body>

<h2>Examples</h2>

<p>
    <ol>
        <li><a href="example2.jsp">A local tabbed panel</a></li>
        <li><a href="example3.jsp">A remote and local tabbed panel</a></li>
        <li><a href="example1.jsp">Various remote and local tabbed panels (with enclosed tabbed pannels)</a></li>
        <li>
            <s:url id="url" action="example4" />
            <s:a href="%{#url}">Only remove tabbed panel</s:a>
        </li>
        <li>
        	<s:url id="url" value="/ajax/tabbedpanel/example5.jsp" />
        	<s:a href="%{#url}">Remote form validation inside tabbed panel</s:a>
        </li>
    </ol>


</p>

<s:include value="../footer.jsp"/>

</body>
</html>
