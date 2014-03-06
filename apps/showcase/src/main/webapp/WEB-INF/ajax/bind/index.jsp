<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<html>
<head>
    <title>Bind Examples</title>
    <jsp:include page="/WEB-INF/ajax/commonInclude.jsp"/>
</head>

<script type="text/javascript">
   dojo.event.topic.subscribe("/before", function(event, widget){
      alert('inside a topic event. before request');
      //event: set event.cancel = true, to cancel request
      //widget: widget that published the topic
   });
   
   dojo.event.topic.subscribe("/after", function(data, request, widget){
      alert('inside a topic event. after request');
      //data : text returned from request
      //request: XMLHttpRequest object
      //widget: widget that published the topic
   });
</script>

<body>

<div id="div1">Div 1</div>
<s:url var="ajaxTest" value="/AjaxTest.action" />


<br/><br/>
<p>
    1. Attach to "onclick" event on button. Update content of Div 1. Use with indicator.
    <img id="indicator" src="${pageContext.request.contextPath}/images/indicator.gif" alt="Loading..." style="display:none"/>
    <sx:bind href="%{#ajaxTest}" sources="button" targets="div1" events="onclick" indicator="indicator" />
     <br/>
    <s:submit theme="simple" type="submit" value="submit" id="button"/>
</p>
<br/><br/>
<p>
    2. Attach to "onmouseover", and "onclick" event on Area below and update content of Div1, highlight targets with green color
    <sx:bind id="ex2" href="%{#ajaxTest}" sources="div2" targets="div1" events="onmouseover,onclick" highlightColor="green"/>
    <div id="div2" style="width: 300px; height: 50px; border: 1px solid black">
        Mouse Over or Click Here!
    </div>
</p>
<br/><br/>
<p>
    3. Attach to "onkeydown" event on Textbox below update content of Div1. Publish topics.
    <sx:bind id="ex4" href="%{#ajaxTest}" sources="txt1" targets="div1" events="onkeydown" beforeNotifyTopics="/before" afterNotifyTopics="/after" />
    <br/>
    <s:textfield id="txt1"/>
</p>


<br/><br/>
<s:include value="../footer.jsp"/>

</body>
</html>