<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
/*
 * $Id: pom.xml 559206 2007-07-24 21:01:18Z apetrelli $
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
--%>
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
