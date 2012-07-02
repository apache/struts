<%-- 
    showcase.jsp
    
    @version $Date$ $Id$
--%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <title>Showcase</title>
    <s:head theme="simple"/>
</head>

<body>
    <blockquote>
        <p>
            The Struts Showcase demonstrates a variety of use cases and tag usages.
            Essentially, the application exercises various framework features in isolation.
            <em>The Showcase is <strong>not</strong> meant as a "best practices" example.</em>
        </p>
        <ul>
            <li>
                For more "by example" solutions,
                see the <a href="http://struts.apache.org/2.x/docs/cookbook.html">Struts Cookbook</a> pages.
            </li>
        </ul>
    </blockquote>
    <p>
    <%-- THIS LIST IS MAINTAINED IN WEB-INF/decorators/main.jsp TO CREATE THE MENU BAR -- EDIT THERE AND COPY HERE --%>
    <ul>
        <li><a href="<s:url value="/showcase.jsp"/>">Home</a></li>        
        <li><a href="<s:url value="/interactive/index.jsp" />">Interactive demo of OGNL and JSP tags</a></li>   
        <li><a href="<s:url value="/ajax/index.jsp" />">Ajax plugin for Struts</a></li>
        <li><a href="<s:url value="/chat/index.jsp"/>">Ajax Chat</a>
        <li><a href="<s:url action="actionChain1!input" namespace="/actionchaining"  includeParams="none" />">Action Chaining</a></li>
        <li><a href="<s:url action="index" namespace="/config-browser" includeParams="none" />">Config Browser</a></li>
        <li><a href="<s:url value="/conversion/index.jsp"/>">Conversion</a></li>
        <li><a href="<s:url value="/empmanager/index.jsp"/>">CRUD</a></li>
        <li><a href="<s:url value="/wait/index.jsp"/>">Execute & Wait</a></li>
        <li><a href="<s:url value="/filedownload/index.jsp"/>">File Download</a></li>
        <li><a href="<s:url value="/fileupload/index.jsp" />">File Upload</a></li>
        <li><a href="<s:url value="/freemarker/index.jsp"/>">Freemarker</a>
        <li><a href="<s:url action="hangmanMenu" namespace="/hangman"/>">Hangman</a></li>
        <li><a href="<s:url value="/jsf/index.jsp"/>">JavaServer Faces</a></li>
        <li><a href="<s:url value="/person/index.jsp"/>">Person Manager</a></li>
        <li><a href="<s:url value="/tags/index.jsp"/>">Tags</a></li>
        <li><a href="<s:url value="/tiles/index.action" />">Tiles</a></li>
        <li><a href="<s:url value="/token/index.jsp"/>">Token</a></li>
        <li><a href="<s:url value="/validation/index.jsp"/>">Validation</a></li>
        <li class="last"><a href="<s:url value="/help.jsp"/>">Help</a></li>
     </ul>

    <h2>Sandbox</h2>
    <p>
        These examples are under development and may not be fully operational.
    </p>
    <ul>
        <li><a href="<s:url action="guess" namespace="/continuations" />">Continuations</a></li>
    </ul>

</p>

</body>
</html>
