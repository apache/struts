<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

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

    dojo.event.topic.subscribe("/alltopics", function(data, type, e){
      alert('inside a topic event. type='+type);
      //data : text returned
      //type : "before", "load" or "error"
      //e    : request object
   });
</script>

<body>

<div id="t1">Div 1</div>
<s:url var="ajaxTest" value="/AjaxTest.action" />

<br/><br/>

A submit button, that highlights (blue color) its targets
<sx:submit type="submit" value="submit" targets="t1" href="%{ajaxTest}" highlightColor="blue"/>

<br/><br/>

A submit button, with an indicator
<img id="indicator" src="${pageContext.request.contextPath}/images/indicator.gif" alt="Loading..." style="display:none"/>
<sx:submit id="submit2" type="submit" value="submit" targets="t1" href="%{ajaxTest}" indicator="indicator"/>

<br/><br/>

A submit button, with "notifyTopics"
<sx:submit type="submit" value="submit" targets="t1" href="%{ajaxTest}" notifyTopics="/alltopics"/>

<br/><br/>

Use an image as submit
<s:url value="/images/struts-power.gif" var="imgUrl" />
<sx:submit type="image" label="Alt Text" targets="t1"
  src="%{imgUrl}" href="%{ajaxTest}" />
<br/><br/>

<label for="textInput">Text to be echoed</label>
<br/><br/>

Use a button as submit (custom text)
<s:form id="form" action="AjaxTest">
  <input type=textbox name="data">
  <sx:submit type="button" label="Update Content" targets="t1"  id="ajaxbtn"/>
</s:form>

<br/><br/>

<s:include value="../footer.jsp"/>

</body>
</html>
