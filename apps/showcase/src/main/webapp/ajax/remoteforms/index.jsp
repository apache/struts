<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<html>
<head>
    <title>Ajax Examples</title>
    <s:head theme="ajax"/>

    <script language="JavaScript" type="text/javascript">
        dojo.event.topic.subscribe("/beforeSubmit", function(data, type, e) {
            alert('you can manipulate the form before it gets submitted. To cancel the submit event set e.cancel=true');
            e.cancel = true;
        });
    </script>

</head>

<body>

<div id='two' style="border: 1px solid yellow;"><b>initial content</b></div>


<br /><br />
Remote form replacing another div:<br/>
<sx:form
        id='theForm2'
        cssStyle="border: 1px solid green;"
        action='AjaxRemoteForm'
        method='post'>

    <input type='text' name='data' value='Struts User'>

    <sx:submit value="GO2" targets="two"/>

</sx:form>

<br /><br />
Remote form replacing the forms content:<br/>
<sx:form
        id='theForm3'
        cssStyle="border: 1px solid green;"
        action='AjaxRemoteForm'
        method='post'>

    <input type='text' name='data' value='Struts User'>

    <sx:submit value="GO3" targets="theForm3"/>

</sx:form>

<br /><br />
Remote form evaluating suplied JS on completion:<br/>
<sx:form
        id='theForm4'
        cssStyle="border: 1px solid green;"
        action='Test3'
        method='post'>

    <input type='text' name='data' value='Struts User'>

    <sx:submit value="GO4" executeScripts="true"/>

</sx:form>

<br /><br />
Remote form whose submit is cancelled:<br/>
<sx:form
        id='theForm5'
        cssStyle="border: 1px solid green;"
        action='AjaxRemoteForm'
        method='post'>

    <input type='text' name='data' value='Struts User'>

    <sx:submit value="GO5" targets="theForm5" beforeNotifyTopics="/beforeSubmit"/>

</sx:form>

<br /><br />
A form with no remote submit (so should not be ajaxified):<br/>
<s:form
        id='theForm7'
        cssStyle="border: 1px solid green;"
        action='AjaxRemoteForm'
        method='post'>

    <input type='text' name='data' value='Struts User'>

    <s:submit value="Go AWAY"  />

</s:form>

</body>
</html>
