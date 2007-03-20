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

    dojo.event.topic.subscribe("/after", function(data, type, e){
      alert('inside a topic event. type='+type);
      //data : text returned
      //type : "before", "load" or "error"
      //e    : request object
   });
</script>

<body>

<div id="t1">Div 1</div>
<s:url id="ajaxTest" value="/AjaxTest.action" />


<br/><br/>

A submit button, with an indicator
<img id="indicator" src="${pageContext.request.contextPath}/images/indicator.gif" alt="Loading..." style="display:none"/>
<s:submit type="submit" theme="ajax" value="submit" targets="t1" href="%{ajaxTest}" align="left" indicator="indicator"/>

<br/><br/>

A submit button, with "notifyTopics"
<s:submit type="submit" theme="ajax" value="submit" targets="t1" href="%{ajaxTest}" align="left" notifyTopics="/after"/>

<br/><br/>

Use an image as submit

<s:submit type="image" theme="ajax" label="Alt Text" targets="t1"
  src="${pageContext.request.contextPath}/images/struts-power.gif" href="%{ajaxTest}" align="left" />
<br/><br/>

<label for="textInput">Text to be echoed</label>
<br/><br/>

Use a button as submit (custom text)
<s:form id="form" action="AjaxTest">
  <input type=textbox name="data">
  <s:submit type="button" theme="ajax" label="Update Content" targets="t1"  id="ajaxbtn"/>
</s:form>

<br/><br/>

<s:include value="../footer.jsp"/>

</body>
</html>
