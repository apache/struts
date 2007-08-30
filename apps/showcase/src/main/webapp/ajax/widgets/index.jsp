<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<html>
<head>
    <title>Ajax Widgets</title>
    <jsp:include page="/ajax/commonInclude.jsp"/>
</head>

<body>

<br/>
NOTES:
<ul>
    <li>Make sure that there is a 'value' attribute in the textarea with the content for the editor</li>
    <li>This is experimental</li>
</ul>


Default Editor configuration:<br/>
<s:form id="form1" action="AjaxRemoteForm" method="post">
    <sx:textarea name="data" cols="50" rows="10" value="Test Data 1" />
    <s:submit value="Submit"/>
</s:form>
<br/>

Configured Editor configuration:<br/>  
<s:form id="form2" action="AjaxRemoteForm" method="post">
    <sx:textarea id="editor2" name="data" cols="50" rows="10" value="Test Data 2">
        <s:param name="editorControls">textGroup;|;justifyGroup;|;listGroup;|;indentGroup</s:param>
    </sx:textarea>
    <s:submit value="Submit"/>
</s:form>
<br/>

<s:include value="../footer.jsp"/>

</body>
</html>
