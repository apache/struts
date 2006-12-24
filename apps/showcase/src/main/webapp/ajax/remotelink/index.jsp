<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

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

<s:a
        theme="ajax"
        href="%{ajaxTest}"
        indicator="indicator"
		targets="t1,t2" notifyTopics="/after" >Update 'Div 1' and 'Div 2', publish topic '/after', use indicator</s:a>
<img id="indicator" src="${pageContext.request.contextPath}/images/indicator.gif" alt="Loading..." style="display:none"/>

<br/><br/>

<s:a  id="link2"
        theme="ajax"
        href="/AjaxNoUrl.jsp"
		errorText="Error Loading"
		targets="t1">Try to update 'Div 1', use custom error message</s:a>

<br/><br/>

<s:a  id="link3"
        theme="ajax"
        href="%{ajaxTest}"
		loadingText="Loading!!!"
		targets="t1">Update 'Div 1', use custom loading message</s:a>

<br/><br/>

<s:a  id="link4"
        theme="ajax"
        href="%{test3}"
		executeScripts="true"
		targets="t2">Update 'Div 2' and execute returned javascript </s:a>

<br/><br/>

<s:a  id="link5"
        theme="ajax"
        href="%{ajaxTest}"
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
        href="%{ajaxTest}"
		targets="t2"
		formId="form"
		>Update 'Div 2' with the content of the textbox </s:a>


<br/><br/>

<s:include value="../footer.jsp"/>

</body>
</html>
