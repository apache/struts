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
    On the OGNL demo you learned how to access values from the Value Stack using OGNL expressions.
    The <i>property</i> tag is used to print to the page, the result of an OGNL expression. The expression
    is specified in the <i>value</i> attribute.
</p>
<p>To print the value of the expression <i>name</i> to the page type:
<p>
    <i id="example">
        &lt;s:property value=&quot;name&quot; /&gt;
    </i>
</p>
<p>
    on the JSP console and hit enter. <a href="#" onclick="evalJSP()">Do it for me</a>
</p>