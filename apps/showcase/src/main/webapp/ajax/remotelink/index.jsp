<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<html>
<head>
    <title>Ajax Examples</title>
    <jsp:include page="/ajax/commonInclude.jsp"/>
</head>

<script type="text/javascript">
   function handler(widget, node) {
     alert('I will handle this myself!');
	 dojo.byId(widget.targetsArray[0]).innerHTML = "Done";
   }

   dojo.event.topic.subscribe("/after", function(data, type, e){
      alert('inside a topic event. type='+type);
      //data : text returned
      //type : "before", "load" or "error"
      //e    : request object
   });
</script>

<body>

<div id="t1">Div 1</div>

<br/>

<div id="t2">Div 2</div>

<br/><br/>

<s:url id="ajaxTest" value="/AjaxTest.action" />
<s:url id="test3" value="/Test3.action" />

<br/><br/>

<sx:a
        href="%{ajaxTest}"
        indicator="indicator"
		targets="t1,t2" notifyTopics="/after" >Update 'Div 1' and 'Div 2', publish topic '/after', use indicator</sx:a>
<img id="indicator" src="${pageContext.request.contextPath}/images/indicator.gif" alt="Loading..." style="display:none"/>

<br/><br/>

<sx:a  id="link2"
        href="/AjaxNoUrl.jsp"
		errorText="Error Loading"
		targets="t1">Try to update 'Div 1', use custom error message</sx:a>

<br/><br/>

<sx:a  id="link3"
        href="%{ajaxTest}"
		loadingText="Loading!!!"
		targets="t1">Update 'Div 1', use custom loading message</sx:a>

<br/><br/>

<sx:a  id="link4"
        href="%{test3}"
		executeScripts="true"
		targets="t2">Update 'Div 2' and execute returned javascript </sx:a>

<br/><br/>

<sx:a  id="link5"
        href="%{ajaxTest}"
		handler="handler"
		targets="t2">Update 'Div 2' using a custom handler </sx:a>


<br/><br/>

<label for="textInput">Text to be echoed</label>

<form id="form">
  <input type=textbox name="data">
</form>

<br/><br/>

<sx:a  id="link6"
        href="%{ajaxTest}"
		targets="t2"
		formId="form"
		>Update 'Div 2' with the content of the textbox </sx:a>


<br/><br/>

<s:include value="../footer.jsp"/>

</body>
</html>
