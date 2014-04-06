<%
    request.setAttribute("decorator", "none");
    response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
    response.setHeader("Pragma","no-cache"); //HTTP 1.0
    response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
%>
<p>
    <b>Calling methods</b>
</p>
<p>
  OGNL follows Java's syntax to execute a method.
</p>
<p>To execute the <i>getTitle()</i> method on the <i>book</i> object type:</p>
<p>
    <i id="example">
        book.getTitle() 
    </i>
</p>
<p>
    on the OGNL console and hit enter.  <a href="#" onclick="execOgnl()">Do it for me</a>
</p>
<br/>
<p>
    <a href="#" onclick="window.open('http://www.ognl.org/2.6.9/Documentation/html/LanguageGuide/methods.html')">[More details]</a>
</p>