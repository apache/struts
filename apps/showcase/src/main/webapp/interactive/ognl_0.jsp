<%
    request.setAttribute("decorator", "none");
    response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
    response.setHeader("Pragma","no-cache"); //HTTP 1.0
    response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
%>
<p>
    <b>Accessing properties</b>
</p>
<p>
    The framework uses a standard naming context to evaluate OGNL expressions. 
    The top level object dealing with OGNL is a Map (usually referred as a context map or context).
    OGNL has a notion of there being a root (or default) object within the context. 
    In OGNL expressions, the properties of the root object can be referenced without any special "marker" notion.
    References to other objects are marked with a pound sign (#).
    
    In this example (and in your JSP pages) the last action executed will be on the top of the stack. 
</p>
<p>    
    <a href="#" onclick="selectClassSrcTab()">This action</a> is available on the third tab above. 
    To access the <i>name</i> field type:
</p>
<p>
    <i id="example">
        name
    </i>
</p>
<p>
    on the OGNL console and hit enter. <a href="#" onclick="execOgnl()">Do it for me</a>
</p>