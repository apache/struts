<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
    <title>Ajax Examples</title>
    <jsp:include page="/ajax/commonInclude.jsp"/>
</head>

<script type="text/javascript">
   function before() {alert("before request");}
   function after() {alert("after request");}
   function handler(widget, node) {
     alert('I will handle this myself!');
	 dojo.byId(widget.targetsArray[0]).innerHTML = "Done";
   }
</script>

<body>

<div id="t1">Div 1</div>

<br/>

<div id="t2">Div 2</div>

<br/><br/>

<s:a  id="link1"
        theme="ajax"
        href="/AjaxTest.action"
		targets="t1,t2">Update 'Div 1' and 'Div 2'</s:a>

<br/><br/>

<s:a  id="link2"
        theme="ajax"
        href="/AjaxNoUrl.jsp"
		errorText="Error Loading"
		targets="t1">Try to update 'Div 1', use custom error message</s:a>
		
<br/><br/>

<s:a  id="link3"
        theme="ajax"
        href="/AjaxTest.action"
		loadingText="Loading!!!"
		beforeLoading="before()"
		afterLoading="after()"
		targets="t1">Update 'Div 1', use custom loading message, execute javascript functions before and after the request is made</s:a>

<br/><br/>

<s:a  id="link4"
        theme="ajax"
        href="/Test3.action"
		executeScripts="true"
		targets="t2">Update 'Div 2' and execute returned javascript </s:a>

<br/><br/>

<s:a  id="link5"
        theme="ajax"
        href="/AjaxTest.action"
		handler="handler"
		targets="t2">Update 'Div 2' using a custom handler </s:a>


<br/><br/>

<label for="textInput">Text to be echoed</label>

<form id="form">
  <input type=textbox name="data">
</form>

<br/><br/>

<s:a  id="link6"
        theme="ajax"
        href="/AjaxTest.action"
		targets="t2"
		formId="form"
		>Update 'Div 2' with the content of the textbox </s:a>

		
<br/><br/>
	
<s:include value="../footer.jsp"/>

</body>
</html>
