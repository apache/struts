<%
    request.setAttribute("decorator", "none");
    response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
    response.setHeader("Pragma","no-cache"); //HTTP 1.0
    response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
%>
<p>
    <b>Accessing properites</b>
</p>
<p>
   Object that are not on the top of the Value Stack are accessed using the "#name" notation. 
   Some objects are always pushed into the stack by Struts, like:
</p>
<ul>
    <li>#application</li>
    <li>#session</li>
    <li>#request</li>
    <li>#parameters</li>
</ul>
<p>To see the value os the parameters type</p>
<p>
    <i id="example">
        #parameters
    </i>
</p>
<p>
    on the OGNL console and it enter.  <a href="#" onclick="execOgnl()">Do it for me</a>
</p>