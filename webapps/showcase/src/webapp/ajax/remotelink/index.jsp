<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="ww" uri="/webwork" %>

<html>
<head>
    <title>Ajax Examples</title>
    <jsp:include page="/ajax/commonInclude.jsp"/>
</head>

<body>

One Component:
<ww:div
        id="one"
        cssStyle="border: 1px solid yellow;"
        href="/AjaxTest.action"
        theme="ajax"
        listenTopics="mylink1_click"
        delay="1000">Initial Content</ww:div>

<br/><br/>

Two Component:
<ww:div
        id="two"
        cssStyle="border: 1px solid yellow;"
        href="/AjaxTest.action"
        theme="ajax"
        listenTopics="mylink1_click,mylink2_click"
        delay="1000">Initial Content</ww:div>
<br/><br/>

Three Component:
<ww:div
        id="three"
        cssStyle="border: 1px solid yellow;"
        href="/AjaxTest.action"
        theme="ajax"
        listenTopics="mylink2_click"
        delay="1000">Initial Content</ww:div>
<br/><br/>

<ww:url id="remoteLink" value   ="/AjaxRemoteLink.action" />
<ww:url id="testLink" value   ="/AjaxTest.action" />

Remote link 1 updating "One Component" and "Two Component"<br/>

<ww:a
        id="link1"
        theme="ajax"
        href="%{remoteLink}"
        notifyTopics="mylink1_click"
        showErrorTransportText="true"
        errorText="An Error ocurred">Update</ww:a>
<br/><br/>

Remote link 2 updating "Two Component" and "Three Component"<br/>
<ww:a
        id="link2"
        theme="ajax"
        href="%{remoteLink}"
        notifyTopics="mylink2_click"
        showErrorTransportText="true"
        errorText="An Error ocurred">Update</ww:a>
<br/><br/>

Remote DIV that is not connected to any remote links:
<ww:div
        id="four"
        cssStyle="border: 1px solid yellow;"
        href="/AjaxTest.action"
        theme="ajax"
        delay="1000">Initial Content</ww:div>
<br/><br/>

A Remote link that doesn't trigger any remote DIV updates<br/>

<ww:a
        id="link3"
        theme="ajax"
        href="%{remoteLink}"
        showErrorTransportText="true"
        errorText="An Error ocurred">Update
</ww:a>
<br/><br/>

<ww:include value="../footer.jsp"/>

</body>
</html>
