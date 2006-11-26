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
<p>The Struts Showcase demonstrates the usages of all Struts tags as well as validations, and so forth</p>

<p>

    <%-- THIS LIST IS MAINTAINED IN MAIN.JSP TO CREATE THE MENU BAR -- EDIT THERE AND COPY HERE --%>

    <ul>
    <li><a href="<s:url action="index" namespace="/config-browser" includeParams="none" />">Config Browser</a></li>
    <li><a href="<s:url action="guess" namespace="/continuations" />">Continuations</a></li>
    <li><a href="<s:url value="/tags/index.jsp"/>">Tags</a></li>
    <li><a href="<s:url action="upload" namespace="/fileupload" includeParams="none"/>">File Upload</a></li>
    <li><a href="<s:url value="/empmanager/index.jsp"/>">CRUD</a></li>
    <li><a href="<s:url value="/person/index.jsp"/>">Person Manager</a></li>
    <li><a href="<s:url value="/validation/index.jsp"/>">Validation</a></li>
    <li><a href="<s:url value="/ajax/index.jsp"/>">AJAX</a></li>
    <li><a href="<s:url action="actionChain1!input" namespace="/actionchaining"  includeParams="none" />">Action Chaining</a></li>
    <li><a href="<s:url value="/wait/index.jsp"/>">Execute & Wait</a></li>
    <li><a href="<s:url value="/token/index.jsp"/>">Token</a></li>
    <li><a href="<s:url value="/filedownload/index.jsp"/>">File Download</a></li>
    <li><a href="<s:url value="/conversion/index.jsp"/>">Conversion</a></li>
    <li><a href="<s:url value="/jsf/index.jsp"/>">JSF</a></li>
    <li><a href="<s:url value="/freemarker/index.jsp"/>">Freemarker</a>
    <li><a href="<s:url value="/chat/index.jsp"/>">Chat (AJAX)</a>
    <li><a href="<s:url action="hangmanMenu" namespace="/hangman"/>">Hangman</a></li>
    <li><a href="<s:url value="/fileupload" />">Fileupload</a></li>
    <li><a href="<s:url value="/tiles/index.action" />">Tiles</a></li>
    <li class="last"><a href="<s:url value="/help.jsp"/>">Help</a></li>
    <ul>

    </ul>
</p>

</body>
</html>
