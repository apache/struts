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

</script>
<form id="form">
	<label for="textInput">Text to be echoed</label>
	<input type=textbox id="textInput" name="data">
</form>

<br/><br/>

<input type=button value="refresh" onclick="controller.refresh()">
<input type=button value="stop timer" onclick="controller.stop()">
<input type=button value="start timer" onclick="controller.start()">

<s:url id="ajaxTest" value="/AjaxTest.action" />

<s:div
        id="once"
        theme="ajax"
        cssStyle="border: 1px solid yellow;"
        href="%{ajaxTest}"
        loadingText="Loading..."
		listenTopics="/refresh"
		startTimerListenTopics="/startTimer"
		stopTimerListenTopics="/stopTimer"
		updateFreq="3000"
		autoStart="true"
		formId="form"
		>
    Initial Content</s:div>

<s:include value="../footer.jsp"/>

</body>
</html>
