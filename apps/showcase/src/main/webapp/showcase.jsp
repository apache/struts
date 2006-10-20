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
<h1>Showcase samples</h1>

<p>The given examples will demonstrate the usages of all Struts tags as well as validations etc.</p>

<p>
    <ul>
        <!-- config-browser -->
        <li><s:url id="url" namespace="/config-browser" action="index"/><s:a href="%{url}">Configuration browser (Great for development!)</s:a></li>

        <!-- continuation -->
        <li><s:url id="url" namespace="/continuations" action="guess"/><s:a href="%{url}">Continuations Example</s:a></li>
        
        <!-- tags -->
        <li><s:url id="url" value="/tags"/><s:a href="%{url}">Tags Examples</s:a></li>
        
        <!-- fileupload -->
        <li><s:url id="url" value="/fileupload" /><s:a href="%{url}">File Upload Example</s:a></li>

        <!-- crud -->
        <li><s:url id="url" value="/empmanager"/><s:a href="%{url}">CRUD Examples</s:a></li>
        
        <!-- person manager sample -->
        <li><s:url id="url" value="/person"/><s:a href="%{url}">PersonManager Sample</s:a></li>

        <!-- validation -->
        <li><s:url id="url" value="/validation"/><s:a href="%{url}">Validation Examples</s:a></li>

        <!-- ajax -->
        <li><s:url id="url" value="/ajax"/><s:a href="%{url}">AJAX Examples</s:a></li>
        
        <!-- action chaining -->
        <li><s:url id="url" namespace="actionchaining" action="actionChain1" method="input" /><s:a href="%{url}">Action Chaining Example</s:a></li>

        <!-- execute and wait -->
        <li><s:url id="url" value="/wait"/><s:a href="%{url}">Execute and Wait Examples</s:a></li>

        <!-- token -->
        <li><s:url id="url" value="/token"/><s:a href="%{url}">Token Examples (double post)</s:a></li>

        <!-- filedownload -->
        <li><s:url id="url" value="/filedownload"/><s:a href="%{url}">File Download Example</s:a></li>
        
        <!-- model-driven -->
        <li><s:url id="url" action="modelDriven" namespace="/modelDriven" method="input"/><s:a href="%{url}">Model Driven Example</s:a>
        
        <!-- conversion -->
        <li><s:url id="url" value="/conversion" /><s:a href="%{url}">Conversion Example</s:a></li>
        
        <!--  freemarker -->
        <li><s:url id="url" value="/freemarker" /><s:a href="%{#url}">Freemarker Example</s:a></li>
        
        <!--  JavaServer Faces -->
        <li><s:url id="url" value="/jsf" /><s:a href="%{#url}">JavaServer Faces Example</s:a></li>
        
        <!--  Integration -->
        <li><s:url id="url" action="editGangster" namespace="/integration"/><s:a href="%{#url}">Struts 1.3 Integration Example</s:a></li>
        
        <!--  Chat (AJAX) Example -->
        <li><s:url id="url" value="/chat" /><s:a href="%{#url}">Chat (AJAX) Example</s:a></li>
        
        <!--  Hangman (AJAX and Non AJAX) Example -->
        <li><s:url id="url" action="hangmanMenu" namespace="/hangman"/><s:a href="%{#url}">Hangman (AJAX and Non AJAX) Example</s:a>

    </ul>
</p>

</body>
</html>
