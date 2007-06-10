<%
    request.setAttribute("decorator", "none");
    response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
    response.setHeader("Pragma","no-cache"); //HTTP 1.0
    response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
%>
<p>
    <b>Expressions</b>
</p>
<p>
  OGNL supports expressions using primitive values.
</p>
<p>Example 1:</p>
<p>
    <i id="example">
        (6 - 2)/2 
    </i>
</p>
<p>
    on the OGNL console and it enter.  <a href="#" onclick="execOgnl()">Do it for me</a>
</p>