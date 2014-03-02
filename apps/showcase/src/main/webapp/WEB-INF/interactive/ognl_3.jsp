<%
    request.setAttribute("decorator", "none");
    response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
    response.setHeader("Pragma","no-cache"); //HTTP 1.0
    response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
%>
<p>
    <b>Accessing properties inside Maps</b>
</p>
<p>
    To access properties inside maps, use the brackets "[]" operators with the desired key. The action
    class has a map of Book objects in the field <i>books</i>.
</p>
<p>
    To access the book with key "Iliad" in the <i>books</i> map type:
</p>
<p>
    <i id="example0">
        books['Iliad']
    </i>
</p>
<p>
    on the OGNL console and hit enter. <a href="#" onclick="execOgnl('example0')">Do it for me</a>
</p>
<p>If the key does not have spaces in it, you can access an element in the map, using the dot "." operator.</p>
<p>
    To access the book with key "Iliad" in the <i>books</i> map type:
</p>
<p>
    <i id="example1">
        books.Iliad
    </i>
</p>
<p>
    on the OGNL console and hit enter. <a href="#" onclick="execOgnl('example1')">Do it for me</a>
</p>
<p>
    Note that the object returned is of type Book. If you want to access one of its properties, you can do so using the dot
    "." operator as you did before.</p>
<p>
    To access the <i>author</i> property of the book with key "Iliad" in the <i>books</i> map type:
</p>
<p>
    <i id="example2">
        books['Iliad'].author
    </i>
</p>
<p>
    on the OGNL console and hit enter. <a href="#" onclick="execOgnl('example2')">Do it for me</a>
</p>
<br/>
<p>
    <a href="#" onclick="window.open('http://www.ognl.org/2.6.9/Documentation/html/LanguageGuide/indexing.html#N10184')">[More details]</a>
</p>
