<%
    request.setAttribute("decorator", "none");
    response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
    response.setHeader("Pragma","no-cache"); //HTTP 1.0
    response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
%>
<p><b>Creating arrays</b></p>
<p>
    OGNL follows Java syntax to create arrys.
</p>
<p>
    Create an array of integers:
</p>
<p>
    <i id="example0">
        new int[] {0, 1, 2}
    </i>
</p>
<p>
    on the OGNL console and hit enter. <a href="#" onclick="execOgnl('example0')">Do it for me</a>
</p>
<p><b>Creating lists</b></p>
<p>
    To create a list, enclose a list of comma separated expression in a pair of braces.
</p>
<p>
    Create a list of Strings:
</p>
<p>
    <i id="example1">
        {'Is', 'there', 'any', 'body', 'out', 'there?'}
    </i>
</p>
<p>
    on the OGNL console and hit enter. <a href="#" onclick="execOgnl('example1')">Do it for me</a>
</p>
<p><b>Creating maps</b></p>
<p>
    To create a map, use the syntax #@MAP_TYPE@{key:value}.
</p>
<p>
    Create a LinkedHashMap:
</p>
<p>
    <i id="example2">
        #@java.util.LinkedHashMap@{'name': 'John Galt', 'job' : 'Engineer'}
    </i>
</p>
<p>
    on the OGNL console and hit enter. <a href="#" onclick="execOgnl('example2')">Do it for me</a>
</p>
<br/>
<p>
    <a href="#" onclick="window.open('http://www.ognl.org/2.6.9/Documentation/html/LanguageGuide/collectionConstruction.html#listConstruction')">[More details]</a>
</p>