<%
    request.setAttribute("decorator", "none");
    response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
    response.setHeader("Pragma","no-cache"); //HTTP 1.0
    response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
%>
<p>
    <b>Accessing nested properties</b>
</p>
<p>
    To access nested properties, use the dot "." operator to concatenate the property names. The action
    class has a <i>book</i> field, with <i>title</i> and <i>author</i> fields.
</p>
<p>
    To access the name of the book type:
</p>
<p>
    <i id="example">
        book.title
    </i>
</p>
<p>
    on the OGNL console and hit enter. <a href="#" onclick="execOgnl()">Do it for me</a>
</p>
<br/>
<p>
    <a href="#" onclick="window.open('http://www.ognl.org/2.6.9/Documentation/html/LanguageGuide/properties.html')">[More details]</a>
</p>