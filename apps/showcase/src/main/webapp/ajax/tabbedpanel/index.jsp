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
        <li><a href="example2.jsp">A local tabbed panel width fixed size (doLayout="true")</a></li>
        <li><a href="example4.jsp">A Local tabbed panel with disabled tabs</a></li>
        <li><a href="example6.jsp">A Local tabbed panel that publishes topics when tabs are selected(before and after)</a></li>
        <li><a href="example3.jsp">A remote (href != "") and local tabbed panel</a></li>
        <li><a href="example1.jsp">Various remote and local tabbed panels (with enclosed tabbed pannels) with layout (doLayout="false")</a></li>
        <li><a href="example5.jsp">A local tabbed panel width fixed size (doLayout="true") with close button on the tab pane (closable="true" on tabs), and tabs on the bottom (labelposition="bottom")</a></li>
    </ol>


</p>

<s:include value="../footer.jsp"/>

</body>
</html>
