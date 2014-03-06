<%
    request.setAttribute("decorator", "none");
    response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
    response.setHeader("Pragma","no-cache"); //HTTP 1.0
    response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
%>
<p>
    <b>Accessing properties on the stack</b>
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
<p>To see the value of the first parameter type:</p>
<p>
    <i id="example">
        #parameters['debug'][0]
    </i>
</p>
<p>
    on the OGNL console and hit enter.  <a href="#" onclick="execOgnl()">Do it for me</a>
</p>
<br/>
<p>
    <a href="#" onclick="window.open('http://struts.apache.org/2.x/docs/ognl.html')">[More details]</a>
</p>