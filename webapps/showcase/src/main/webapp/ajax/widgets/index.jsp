<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="saf" uri="/struts-action" %>

<html>
<head>
    <title>Ajax Widgets</title>
    <jsp:include page="/ajax/commonInclude.jsp"/>
</head>

<body>
NOTES:
<ul>
    <li>Make sure that there is a 'value' attribute in the textarea with the content for the editor</li>
    <li>This is experimental</li>
</ul>

Default Editor configuration:<br/>
<saf:form id="form1" action="AjaxRemoteForm" method="post">
    <saf:textarea name="data" theme="ajax" cols="50" rows="10" value="Test Data 1" />
    <saf:submit value="Submit"/>
</saf:form>
<br/>

Configured Editor configuration:<br/>
<saf:form id="form2" action="AjaxRemoteForm" method="post">
    <saf:textarea id="editor2" name="data" theme="ajax" cols="50" rows="10" value="Test Data 2">
        <saf:param name="editorControls">textGroup;|;justifyGroup;|;listGroup;indentGroup</saf:param>
    </saf:textarea>
    <saf:submit value="Submit"/>
</saf:form>
<br/>

<saf:include value="../footer.jsp"/>

</body>
</html>
