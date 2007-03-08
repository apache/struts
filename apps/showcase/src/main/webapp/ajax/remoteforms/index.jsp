<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

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
<sx:form
        id='theForm2'
        cssStyle="border: 1px solid green;"
        action='AjaxRemoteForm'
        method='post'>

    <input type='text' name='data' value='Struts User'>

    <sx:submit value="GO2" theme="ajax" targets="two"/>

</sx:form>


Remote form replacing the forms content:<br/>
<sx:form
        id='theForm3'
        cssStyle="border: 1px solid green;"
        action='AjaxRemoteForm'
        method='post'>

    <input type='text' name='data' value='Struts User'>

    <sx:submit value="GO3" targets="theForm3"/>

</sx:form>

Remote form evaluating suplied JS on completion:<br/>
<sx:form
        id='theForm4'
        cssStyle="border: 1px solid green;"
        action='AjaxRemoteForm'
        method='post'>

    <input type='text' name='data' value='Struts User'>

    <sx:submit value="GO4" />

</sx:form>

Remote form replacing the forms content after confirming results:<br/>
<sx:form
        id='theForm5'
        cssStyle="border: 1px solid green;"
        action='AjaxRemoteForm'
        method='post'>

    <input type='text' name='data' value='Struts User'>

    <sx:submit value="GO3" targets="theForm5" />

</sx:form>

Remote form replacing the forms content after running a function:<br/>
<sx:form
        id='theForm6'
        cssStyle="border: 1px solid green;"
        action='AjaxRemoteForm'
        method='post'>

    <input type='text' name='data' value='Struts User'>

    <sx:submit value="GO3" targets="theForm6" />

</sx:form>

A form with no remote submit (so should not be ajaxified):<br/>
<sx:form
        id='theForm7'
        cssStyle="border: 1px solid green;"
        action='AjaxRemoteForm'
        method='post'>

    <input type='text' name='data' value='Struts User'>

    <sx:submit value="Go AWAY" targets="theForm7" />

</sx:form>

</body>
</html>
