<%
    request.setAttribute("decorator", "none");
    response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
    response.setHeader("Pragma","no-cache"); //HTTP 1.0
    response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
%>
<p>
    <b>Accessing properties inside Arrays</b>
</p>
<p>
    To access properties inside arrays, use the brackets "[]" operators with the desired index(starting from 0). The action
    class has an array of String in the field <i>bands</i>.
</p>
<p>
    To access the second element in the <i>bands</i> array type:
</p>
<p>
    <i id="example0">
        bands[1]
    </i>
</p>
<p>
    on the OGNL console and hit enter. <a href="#" onclick="execOgnl('example0')">Do it for me</a>
</p>
<p>
    <b>Accessing properties inside Lists</b>
</p>
<p>Lists can be accessed on the same way. The action class has a List of String on the field <i>movies</i>.</p>
<p>
    To access the first element in the <i>movies</i> list type:
</p>
<p>
    <i id="example1">
        movies[0]
    </i>
</p>
<p>
    on the OGNL console and hit enter.  <a href="#" onclick="execOgnl('example1')">Do it for me</a>
</p>
<br/>
<p>
    <a href="#" onclick="window.open('http://www.ognl.org/2.6.9/Documentation/html/LanguageGuide/indexing.html#N10184')">[More details]</a>
</p>
