<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
/*
 * $Id$
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

<script type="text/javascript">
   function handler(widget, node) {
     alert('I will handle this myself!');
	 dojo.byId(widget.targetsArray[0]).innerHTML = "Done";
   }

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
   
   dojo.event.topic.subscribe("/error", function(error, request, widget){
      alert('inside a topic event. on error');
      //error : error object (error.message has the error message)
      //request: XMLHttpRequest object
      //widget: widget that published the topic
   });
   
   dojo.event.topic.subscribe("/topics", function(data, type, e){
      alert('inside a topic event. type='+type);
      //data : text returned
      //type : "before", "load", "error"
      //e    : request object
   });
</script>

<body>

<div id="t1">Div 1</div>

<br/>

<div id="t2">Div 2</div>

<br/><br/>

<s:url var="ajaxTest" value="/AjaxTest.action" />
<s:url var="test3" value="/Test3.action" />

<sx:a  id="link0"
        href="%{#ajaxTest}"
        targets="t1"
        highlightColor="red"
        highlightDuration="2000">Update 'Div 1' and use red highligh to notify user of changed content</sx:a>

<br/><br/>

<sx:a   id="link1"
        href="%{#ajaxTest}"
        indicator="indicator"
		targets="t1,t2" 
        beforeNotifyTopics="/before"
        afterNotifyTopics="/after" >Update 'Div 1' and 'Div 2', publish topic '/before' and '/after', use indicator</sx:a>
<img id="indicator" src="${pageContext.request.contextPath}/images/indicator.gif" alt="Loading..." style="display:none"/>

<br/><br/>

<sx:a  id="link2"
        href="/AjaxNoUrl.jsp"
		errorText="Error Loading"
		targets="t1"
        errorNotifyTopics="/error">Try to update 'Div 1', publish '/error', use custom error message</sx:a>

<br/><br/>

<sx:a  id="link3"
        href="%{#ajaxTest}"
		loadingText="Loading!!!"
        showLoadingText="true"
		targets="t1">Update 'Div 1', use custom loading message</sx:a>

<br/><br/>

<sx:a  id="link4"
        href="%{#test3}"
		executeScripts="true"
		targets="t2">Update 'Div 2' and execute returned javascript </sx:a>

<br/><br/>

<sx:a  id="link5"
        href="%{#ajaxTest}"
		handler="handler"
		targets="t2">Update 'Div 2' using a custom handler </sx:a>


<br/><br/>

<label for="textInput">Text to be echoed</label>

<form id="form">
  <input type=textbox name="data">
</form>

<br/><br/>

<sx:a  id="link6"
        href="%{#ajaxTest}"
		targets="t2"
		formId="form"
		>Update 'Div 2' with the content of the textbox </sx:a>


<br/><br/>

<s:include value="../footer.jsp"/>

</body>
</html>
