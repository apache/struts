<%-- 
	showcase.jsp
	
	@version $Date: 2006/03/20 16:04:09 $ $Id: showcase.jsp,v 1.17 2006/03/20 16:04:09 tmjee Exp $
--%>

<%@ taglib uri="/struts-action" prefix="saf" %>
<html>
<head>
    <title>Showcase</title>
    <saf:head theme="simple"/>
</head>

<body>
<h1>Showcase samples</h1>

<p>The given examples will demonstrate the usages of all Struts tags as well as validations etc.</p>

<p>
	<ul>
        <!-- config-browser -->
        <li><saf:url id="url" namespace="/config-browser" action="index"/><saf:a href="%{url}">Configuration browser (Great for development!)</saf:a></li>

		<!-- continuation -->
		<li><saf:url id="url" namespace="/continuations" action="guess"/><saf:a href="%{url}">Continuations Example</saf:a></li>
		
		<!-- tags -->
		<li><saf:url id="url" value="/tags"/><saf:a href="%{url}">Tags Examples</saf:a></li>
		
		<!-- fileupload -->
		<li><saf:url id="url" namespace="/fileupload" action="upload"/><saf:a href="%{url}">File Upload Example</saf:a></li>

		<!-- crud -->
		<li><saf:url id="url" value="/empmanager"/><saf:a href="%{url}">CRUD Examples</saf:a></li>
		
		<!-- person manager sample -->
		<li><saf:url id="url" value="/person"/><saf:a href="%{url}">PersonManager Sample</saf:a></li>

        <!-- validation -->
        <li><saf:url id="url" value="/validation"/><saf:a href="%{url}">Validation Examples</saf:a></li>

        <!-- ajax -->
        <li><saf:url id="url" value="/ajax"/><saf:a href="%{url}">AJAX Examples</saf:a></li>
        
        <!-- action chaining -->
		<li><saf:url id="url" namespace="actionchaining" action="actionChain1" method="input" /><saf:a href="%{url}">Action Chaining Example</saf:a></li>

        <!-- execute and wait -->
        <li><saf:url id="url" value="/wait"/><saf:a href="%{url}">Execute and Wait Examples</saf:a></li>

        <!-- token -->
        <li><saf:url id="url" value="/token"/><saf:a href="%{url}">Token Examples (double post)</saf:a></li>

        <!-- filedownload -->
        <li><saf:url id="url" value="/filedownload"/><saf:a href="%{url}">File Download Example</saf:a></li>
        
        <!-- model-driven -->
        <li><saf:url id="url" action="modelDriven" namespace="/modelDriven" method="input"/><saf:a href="%{url}">Model Driven Example</saf:a>
        
        <!-- conversion -->
        <li><saf:url id="url" value="/conversion" /><saf:a href="%{url}">Conversion Example</saf:a></li>
        
        <!--  freemarker -->
        <li><saf:url id="url" value="/freemarker" /><saf:a href="%{#url}">Freemarker Example</saf:a></li>
        
        <!--  JavaServer Faces -->
        <li><saf:url id="url" value="/jsf" /><saf:a href="%{#url}">JavaServer Faces Example</saf:a></li>
    </ul>
</p>

</body>
</html>
