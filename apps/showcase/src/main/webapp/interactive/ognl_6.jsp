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
<p>Arithmetic:</p>
<p>
    <i id="example0">
        (6 - 2)/2
    </i>
</p>
<p>
    on the OGNL console and hit enter.  <a href="#" onclick="execOgnl('example0')">Do it for me</a>
</p>
<p>Logical:</p>
<p>
    <i id="example1">
        (true || false) and true
    </i>
</p>
<p>
    on the OGNL console and hit enter.  <a href="#" onclick="execOgnl('example1')">Do it for me</a>
</p>
<p>Equality:</p>
<p>
    <i id="example2">
        'a' == 'a'
    </i>
</p>
<p>
    on the OGNL console and hit enter.  <a href="#" onclick="execOgnl('example2')">Do it for me</a>
</p>
<p>
    OGNL supports many more operators and expressions, see <a href="#" onclick="window.open('http://www.ognl.org/2.6.9/Documentation/html/LanguageGuide/apa.html#operators')">[Operators Reference]</a>
    for more details.
</p>