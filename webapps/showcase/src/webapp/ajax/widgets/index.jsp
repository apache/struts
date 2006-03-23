<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="ww" uri="/webwork" %>

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
<ww:form id="form1" action="AjaxRemoteForm" method="post">
    <ww:textarea name="data" theme="ajax" cols="50" rows="10" value="Test Data 1" />
    <ww:submit value="Submit"/>
</ww:form>
<br/>

Configured Editor configuration:<br/>
<ww:form id="form2" action="AjaxRemoteForm" method="post">
    <ww:textarea id="editor2" name="data" theme="ajax" cols="50" rows="10" value="Test Data 2">
        <ww:param name="editorControls">textGroup;|;justifyGroup;|;listGroup;indentGroup</ww:param>
    </ww:textarea>
    <ww:submit value="Submit"/>
</ww:form>
<br/>

<ww:include value="../footer.jsp"/>

</body>
</html>
