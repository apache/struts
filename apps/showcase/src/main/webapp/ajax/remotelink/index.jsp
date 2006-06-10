<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="saf" uri="/struts-action" %>

<html>
<head>
    <title>Ajax Examples</title>
    <jsp:include page="/ajax/commonInclude.jsp"/>
</head>

<body>

One Component:
<saf:div
        id="one"
        cssStyle="border: 1px solid yellow;"
        href="/AjaxTest.action"
        theme="ajax"
        listenTopics="mylink1_click"
        delay="1000">Initial Content</saf:div>

<br/><br/>

Two Component:
<saf:div
        id="two"
        cssStyle="border: 1px solid yellow;"
        href="/AjaxTest.action"
        theme="ajax"
        listenTopics="mylink1_click,mylink2_click"
        delay="1000">Initial Content</saf:div>
<br/><br/>

Three Component:
<saf:div
        id="three"
        cssStyle="border: 1px solid yellow;"
        href="/AjaxTest.action"
        theme="ajax"
        listenTopics="mylink2_click"
        delay="1000">Initial Content</saf:div>
<br/><br/>

Fourth Component:
<saf:div
	   id="four"
	   theme="ajax"
	   cssStyle="border: 1px solid yellow;"
	   href="/AjaxTest.action"
	   listenTopics="myLink3_click"
	   delay="0"
	   updateFreq="0">Initial Content</saf:div>
<br/><br/>


<saf:url id="remoteLink" value   ="/AjaxRemoteLink.action" />
<saf:url id="testLink" value   ="/AjaxTest.action" />

Remote link 1 updating "One Component" and "Two Component"<br/>

<saf:a
        id="link1"
        theme="ajax"
        href="%{remoteLink}"
        notifyTopics="mylink1_click"
        showErrorTransportText="true"
        errorText="An Error ocurred">Update</saf:a>
<br/><br/>

Remote link 2 updating "Two Component" and "Three Component"<br/>
<saf:a
        id="link2"
        theme="ajax"
        href="%{remoteLink}"
        notifyTopics="mylink2_click"
        showErrorTransportText="true"
        errorText="An Error ocurred">Update</saf:a>
<br/><br/>

Remote DIV that is not connected to any remote links:
<saf:div
        id="five"
        cssStyle="border: 1px solid yellow;"
        href="/AjaxTest.action"
        theme="ajax"
        delay="1000">Initial Content</saf:div>
<br/><br/>

A Remote link that doesn't trigger any remote DIV updates<br/>

<saf:a
        id="link3"
        theme="ajax"
        href="%{remoteLink}"
        showErrorTransportText="true"
        errorText="An Error ocurred">Update
</saf:a>
<br/><br/>

A Remote link that will update "Fourth Component"
<saf:a 
		id="link4"
		theme="ajax"
		href="%{remoteLink}"
		notifyTopics="myLink3_click"
		showErrorTransportText="true"
		errorText="An Error Ocurred">Update</saf:a>
<br/><br/>

<saf:include value="../footer.jsp"/>

</body>
</html>
