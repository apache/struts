<%
    request.setAttribute("decorator", "none");
    response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
    response.setHeader("Pragma","no-cache"); //HTTP 1.0
    response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
%>
<p>
    <b>Static fields</b>
</p>
<p>
   Static fields can easily be accessed in OGNL expressions using the notation 
   <i>@some.package.ClassName@PROPERTY_NAME</i> for properties, and <i>@some.package.ClassName@METHOD_NAME()</i>
   for methods.
</p>
<p>To access the value of the <i>CONSTANT</i> field in the ExampleAction type:</p>
<p>
    <i id="example0">
        @org.apache.struts2.showcase.action.ExampleAction@CONSTANT
    </i>
</p>
<p>
    on the OGNL console and it enter.  <a href="#" onclick="execOgnl('example0')">Do it for me</a>
</p>
<p>To execute the static method <i>getCurrentDate</i> in the ExampleAction type:</p>
<p>
    <i id="example1">
        @org.apache.struts2.showcase.action.ExampleAction@getCurrentDate()
    </i>
</p>
<p>
    on the OGNL console and it enter.  <a href="#" onclick="execOgnl('example1')">Do it for me</a>
</p>