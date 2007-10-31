<%
    request.setAttribute("decorator", "none");
    response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
    response.setHeader("Pragma","no-cache"); //HTTP 1.0
    response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
%>
<p>
    <b>More on OGNL</b>
</p>
<p>
  There are a lot of OGNL features that we have not covered on this short tutorial. 
</p>
<br/>
<p>
    To learn more see the <a href="#" onclick="window.open('http://www.ognl.org/2.6.9/Documentation/html/LanguageGuide/index.html')">[OGNL Documentation]</a>
</p>
You can keep playing with the OGNL console or 
<a href="#" onclick="startJSP()">Start JSP Interactive Demo</a>