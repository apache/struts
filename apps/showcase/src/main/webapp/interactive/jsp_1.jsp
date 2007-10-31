<%
    request.setAttribute("decorator", "none");
    response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
    response.setHeader("Pragma","no-cache"); //HTTP 1.0
    response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
%>
<p>
    <b>Print property value, using the <i>property tag</i></b>
</p>
<p>
    On the OGNL demo you learned how to access values from the Value Stack using OGNL expressions.
    The <i>property</i> tag is used to print to the page the result of an OGNL expression. The expression
    is specified in the <i>value</i> attribute.
</p>
<p>To print the value of the expression <i>name</i> to the page type:
<p>
    <i id="example0">
        &lt;s:property value=&quot;name&quot; /&gt;
    </i>
</p>
<p>
    on the JSP console and hit enter. <a href="#" onclick="execJSP('example0')">Do it for me</a>
</p>
<p>
    As you saw in the OGNL demo, to print a property of an object that is not on top of the stack,
    use the <i>#object.property</i> notation.
</p>
<p>To print the value for the key "struts.view_uri" in <i>request</i> to the page type:
<p>
    <i id="example1">
        &lt;s:property value=&quot;#request['struts.view_uri']&quot; /&gt;
    </i>
</p>
<p>
    on the JSP console and hit enter. <a href="#" onclick="execJSP('example1')">Do it for me</a>
</p>
<p>
    <b>Print property value, using the <i>$</i> operator</b>
</p>
<p>Use the <i>${name}</i> notation to print values from the Value Stack to the page.
<p>To print the value of the expression <i>name</i> to the page type:
<p>
    <i id="example2">
        &#36;{name}
    </i>
</p>
<p>
    on the JSP console and hit enter. <a href="#" onclick="execJSP('example2')">Do it for me</a>
</p>
<p>
    <a href="#" onclick="window.open('http://struts.apache.org/2.x/docs/property.html')">[More details]</a>
</p>
