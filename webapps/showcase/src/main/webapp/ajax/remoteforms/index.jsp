<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="saf" uri="/struts-action" %>

<html>
<head>
    <title>Ajax Examples</title>
    <jsp:include page="/ajax/commonInclude.jsp"/>

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
<saf:form
        id='theForm2'
        cssStyle="border: 1px solid green;"
        action='AjaxRemoteForm'
        method='post'
        theme="ajax">

    <input type='text' name='data' value='Struts User'>

    <saf:submit value="GO2" theme="ajax" resultDivId="two"/>

</saf:form>


Remote form replacing the forms content:<br/>
<saf:form
        id='theForm3'
        cssStyle="border: 1px solid green;"
        action='AjaxRemoteForm'
        method='post'
        theme="ajax">

    <input type='text' name='data' value='Struts User'>

    <saf:submit value="GO3" theme="ajax" resultDivId="theForm3"/>

</saf:form>

Remote form evaluating suplied JS on completion:<br/>
<saf:form
        id='theForm4'
        cssStyle="border: 1px solid green;"
        action='AjaxRemoteForm'
        method='post'
        theme="ajax">

    <input type='text' name='data' value='Struts User'>

    <saf:submit value="GO4" theme="ajax" onLoadJS="alert('form submitted');"/>

</saf:form>

Remote form replacing the forms content after confirming results:<br/>
<saf:form
        id='theForm5'
        cssStyle="border: 1px solid green;"
        action='AjaxRemoteForm'
        method='post'
        theme="ajax">

    <input type='text' name='data' value='Struts User'>

    <saf:submit value="GO3" theme="ajax" resultDivId="theForm5" preInvokeJS="confirm('sure?');"/>

</saf:form>

Remote form replacing the forms content after running a function:<br/>
<saf:form
        id='theForm6'
        cssStyle="border: 1px solid green;"
        action='AjaxRemoteForm'
        method='post'
        theme="ajax">

    <input type='text' name='data' value='Struts User'>

    <saf:submit value="GO3" theme="ajax" resultDivId="theForm6" preInvokeJS="doSomething();"/>

</saf:form>

A form with no remote submit (so should not be ajaxified):<br/>
<saf:form
        id='theForm7'
        cssStyle="border: 1px solid green;"
        action='AjaxRemoteForm'
        method='post'
        theme="ajax">

    <input type='text' name='data' value='Struts User'>

    <saf:submit value="Go AWAY" resultDivId="theForm7" />

</saf:form>

<saf:include value="../footer.jsp"/>

</body>
</html>
