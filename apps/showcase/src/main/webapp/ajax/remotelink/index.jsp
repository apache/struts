<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/tags" %>

<html>
<head>
    <title>Ajax Examples</title>
    <jsp:include page="/ajax/commonInclude.jsp"/>
</head>

<body>

One Component:
<s:div
        id="one"
        cssStyle="border: 1px solid yellow;"
        href="/AjaxTest.action"
        theme="ajax"
        listenTopics="mylink1_click"
        delay="1000">Initial Content</s:div>

<br/><br/>

Two Component:
<s:div
        id="two"
        cssStyle="border: 1px solid yellow;"
        href="/AjaxTest.action"
        theme="ajax"
        listenTopics="mylink1_click,mylink2_click"
        delay="1000">Initial Content</s:div>
<br/><br/>

Three Component:
<s:div
        id="three"
        cssStyle="border: 1px solid yellow;"
        href="/AjaxTest.action"
        theme="ajax"
        listenTopics="mylink2_click"
        delay="1000">Initial Content</s:div>
<br/><br/>

Fourth Component:
<s:div
       id="four"
       theme="ajax"
       cssStyle="border: 1px solid yellow;"
       href="/AjaxTest.action"
       listenTopics="myLink3_click"
       delay="0"
       updateFreq="0">Initial Content</s:div>
<br/><br/>


<s:url id="remoteLink" value   ="/AjaxRemoteLink.action" />
<s:url id="testLink" value   ="/AjaxTest.action" />

Remote link 1 updating "One Component" and "Two Component"<br/>

<s:a
        id="link1"
        theme="ajax"
        href="%{remoteLink}"
        notifyTopics="mylink1_click"
        showErrorTransportText="true"
        errorText="An Error ocurred">Update</s:a>
<br/><br/>

Remote link 2 updating "Two Component" and "Three Component"<br/>
<s:a
        id="link2"
        theme="ajax"
        href="%{remoteLink}"
        notifyTopics="mylink2_click"
        showErrorTransportText="true"
        errorText="An Error ocurred">Update</s:a>
<br/><br/>

Remote DIV that is not connected to any remote links:
<s:div
        id="five"
        cssStyle="border: 1px solid yellow;"
        href="/AjaxTest.action"
        theme="ajax"
        delay="1000">Initial Content</s:div>
<br/><br/>

A Remote link that doesn't trigger any remote DIV updates<br/>

<s:a
        id="link3"
        theme="ajax"
        href="%{remoteLink}"
        showErrorTransportText="true"
        errorText="An Error ocurred">Update
</s:a>
<br/><br/>

A Remote link that will update "Fourth Component"
<s:a 
        id="link4"
        theme="ajax"
        href="%{remoteLink}"
        notifyTopics="myLink3_click"
        showErrorTransportText="true"
        errorText="An Error Ocurred">Update</s:a>
<br/><br/>

<s:include value="../footer.jsp"/>

</body>
</html>
