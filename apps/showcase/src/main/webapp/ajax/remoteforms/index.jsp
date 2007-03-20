<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
    <title>Ajax Examples</title>
    <s:head theme="ajax"/>

    <script language="JavaScript" type="text/javascript">
        function doSomething() {
            alert('you can manipulate the form before it gets submitted');
            return true;
        }
    </script>

</head>

<body>


Remote form replacing another div:<br/>

<div id='two' style="border: 1px solid yellow;"><b>initial content</b></div>
<s:form
        id='theForm2'
        cssStyle="border: 1px solid green;"
        action='AjaxRemoteForm'
        method='post'
        theme="ajax">

    <input type='text' name='data' value='Struts User'>

    <s:submit value="GO2" theme="ajax" targets="two"/>

</s:form>


Remote form replacing the forms content:<br/>
<s:form
        id='theForm3'
        cssStyle="border: 1px solid green;"
        action='AjaxRemoteForm'
        method='post'
        theme="ajax">

    <input type='text' name='data' value='Struts User'>

    <s:submit value="GO3" theme="ajax" targets="theForm3"/>

</s:form>

Remote form evaluating suplied JS on completion:<br/>
<s:form
        id='theForm4'
        cssStyle="border: 1px solid green;"
        action='AjaxRemoteForm'
        method='post'
        theme="ajax">

    <input type='text' name='data' value='Struts User'>

    <s:submit value="GO4" theme="ajax"/>

</s:form>

Remote form replacing the forms content after confirming results:<br/>
<s:form
        id='theForm5'
        cssStyle="border: 1px solid green;"
        action='AjaxRemoteForm'
        method='post'
        theme="ajax">

    <input type='text' name='data' value='Struts User'>

    <s:submit value="GO3" theme="ajax" targets="theForm5" />

</s:form>

Remote form replacing the forms content after running a function:<br/>
<s:form
        id='theForm6'
        cssStyle="border: 1px solid green;"
        action='AjaxRemoteForm'
        method='post'
        theme="ajax">

    <input type='text' name='data' value='Struts User'>

    <s:submit value="GO3" theme="ajax" targets="theForm6" />

</s:form>

A form with no remote submit (so should not be ajaxified):<br/>
<s:form
        id='theForm7'
        cssStyle="border: 1px solid green;"
        action='AjaxRemoteForm'
        method='post'
        theme="ajax">

    <input type='text' name='data' value='Struts User'>

    <s:submit value="Go AWAY" targets="theForm7" />

</s:form>

</body>
</html>
