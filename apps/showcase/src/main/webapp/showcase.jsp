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
<p>The Struts Showcase demonstrates using Struts tags as well as validations, and so forth</p>

<p>

    <%-- THIS LIST IS MAINTAINED IN MAIN.JSP TO CREATE THE MENU BAR -- EDIT THERE AND COPY HERE --%>

    <ul>
        <li><a href="<s:url value="/showcase.jsp"/>">Home</a></li>
        <li><a href="<s:url action="index" namespace="/config-browser" includeParams="none" />">Config Browser</a></li>
        <li><a href="<s:url value="/ajax/index.jsp"/>">AJAX</a></li>
        <li><a href="<s:url action="actionChain1!input" namespace="/actionchaining"  includeParams="none" />">Action Chaining</a></li>
        <li><a href="<s:url value="/conversion/index.jsp"/>">Conversion</a></li>
        <li><a href="<s:url value="/empmanager/index.jsp"/>">CRUD</a></li>
        <li><a href="<s:url value="/wait/index.jsp"/>">Execute & Wait</a></li>
        <li><a href="<s:url value="/filedownload/index.jsp"/>">File Download</a></li>
        <li><a href="<s:url value="/fileupload" />">File Upload</a></li>
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
        These examples are under development.
    </p>
    <ul>
        <li>AJAX / Remote Forms</li>
        <li><a href="<s:url value="/chat/index.jsp"/>">Chat (AJAX)</a>
        <li><a href="<s:url action="guess" namespace="/continuations" />">Continuations</a></li>
        <li>Conversion<ul>
                <li>Address</li>
                <li>Java 5 Enum</li>
            </ul>
        </li>
        <li>Tags / UI Tags<ul>
            <li>UI Example</li>
            <li>UI Example (Velocity)</li>
        </ul>
        </li>
    </ul>

</p>

</body>
</html>
