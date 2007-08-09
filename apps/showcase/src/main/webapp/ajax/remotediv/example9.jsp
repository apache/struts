<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<html>
<head>
    <title>Ajax Examples</title>
    <jsp:include page="/ajax/commonInclude.jsp"/>
</head>

<body>

<script>
	var controller = {
		refresh : function() {},
		start : function() {},
		stop : function() {}
	};

	dojo.event.topic.registerPublisher("/refresh", controller, "refresh");
	dojo.event.topic.registerPublisher("/startTimer", controller, "start");
	dojo.event.topic.registerPublisher("/stopTimer", controller, "stop");

    dojo.event.topic.subscribe("/before", function(data, type, e){
      alert('inside a topic event. before request');
      //data : source element id
      //type : "before" 
      //e    : request object
    });
    
    dojo.event.topic.subscribe("/after", function(data, type, e){
      alert('inside a topic event. after request');
      //data : text returned
      //type : "load" 
      //e    : undefined
    });

</script>

<input type=button value="refresh" onclick="controller.refresh()">
<input type=button value="start timer" onclick="controller.start()">
<input type=button value="stop timer" onclick="controller.stop()">

<s:url var="ajaxTest" value="/AjaxTest.action" />

<sx:div
        id="div1"
        cssStyle="border: 1px solid yellow;"
        href="%{ajaxTest}"
        listenTopics="/refresh"
		startTimerListenTopics="/startTimer"
		stopTimerListenTopics="/stopTimer"
		updateFreq="10000"
		autoStart="false"
        beforeNotifyTopics="/before"
        afterNotifyTopics="/after"
		>
    Initial Content</sx:div>

<s:include value="../footer.jsp"/>

</body>
</html>
