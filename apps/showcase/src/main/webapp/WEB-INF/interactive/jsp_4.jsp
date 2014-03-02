<%
    request.setAttribute("decorator", "none");
    response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
    response.setHeader("Pragma","no-cache"); //HTTP 1.0
    response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
%>
<p>
    <b>More on JSP tags</b>
</p>
<p>
  Struts 2 provides many more tags which you can learn about 
  <a href="#" onclick="window.open('http://cwiki.apache.org/confluence/display/WW/Tag+Reference')">here</a>
</p>
<br/>
You can keep playing with the JSP console or 
<a href="#" onclick="startOgnl()">Start OGNL Interactive Demo</a>