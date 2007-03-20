<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

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

    dojo.event.topic.subscribe("/after", function(data, type, e){
      alert('inside a topic event. type='+type);
      //data : text returned
      //type : "before", "load" or "error"
      //e    : request object
    });

</script>

<input type=button value="refresh" onclick="controller.refresh()">
<input type=button value="start timer" onclick="controller.start()">
<input type=button value="stop timer" onclick="controller.stop()">

<s:url id="ajaxTest" value="/AjaxTest.action" />

<s:div
        id="once"
        theme="ajax"
        cssStyle="border: 1px solid yellow;"
        href="%{ajaxTest}"
        listenTopics="/refresh"
		startTimerListenTopics="/startTimer"
		stopTimerListenTopics="/stopTimer"
		updateFreq="10000"
		autoStart="false"
		afterLoading="alert('after request')"
        notifyTopics="/after"
		>
    Initial Content</s:div>

<s:include value="../footer.jsp"/>

</body>
</html>
