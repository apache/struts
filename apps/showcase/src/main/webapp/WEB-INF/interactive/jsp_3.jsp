<%
    request.setAttribute("decorator", "none");
    response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
    response.setHeader("Pragma","no-cache"); //HTTP 1.0
    response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
%>
<p>
    <b><i>set</i> tag</b>
</p>
<p>
    The <i>set</i> tag sets the variable with the name specified in the <i>name</i> attribute to 
    the value specified in the <i>value</i> attribute in the scope
    entered in the <i>scope</i> attribute. The available scopes are:
    <ul>
        <li>application - application scope according to servlet spec</li>
        <li>session - session scope according to servlet spec</li>
        <li>request - request scope according to servlet spec</li>
        <li>page - page scope according to servlet sepc</li>
        <li>action - the value will be set in the request scope and Struts' action context</li>
        
    </ul>
</p>
<p>    
    This example sets <i>favouriteBand</i> in the request scope to the first element of the <i>bands</i> property:
</p>
<p>
    <pre id="example0">
        &lt;s:set name="favouriteBand" value="bands[0]" /&gt;
        &lt;s:property value="#favouriteBand" /&gt;
    </pre>
</p>
<p>
    <a href="#" onclick="execJSP('example0')">Do it for me</a>
</p>
<p>
    <b><i>url</i> tag</b>
</p>
<p>
    The <i>url</i> tag is used to build urls (who would have guessed!). To build an url mapping to
    an action, set the <i>namespace</i> and <i>action</i> attributes. The url will be stored under
    the name specified in the <i>id</i> attribute. <b>url tag uses the <i>id</i> attribute while 
    the <i>set</i> tag uses name</b>. To specify a value (no action lookup), just use the <i>value</i>
    attribute. <i>param</i> tags can be nested inside the <i>url</i> tag to add parameters to the url.
</p>
<p>    
    First link creates a url that maps to an action, second one creates a url to google, passing one parameter:
</p>
<p>
    <pre id="example1">
        &lt;s:url id="evalAction" namespace="/nodecorate" action="jspEval" /&gt;
        &lt;s:a href="%{#evalAction}" &gt;Eval&lt;/s:a&gt;
        
        &lt;s:url id="google" value="http://www.google.com" &gt;
            &lt;s:param name="q" value="%{'Struts 2'}" /&gt; 
        &lt;/s:url&gt;
        &lt;s:a href="%{#google}" &gt;Eval&lt;/s:a&gt;
    </pre>
</p>
<p>
   <a href="#" onclick="execJSP('example1')">Do it for me</a>
</p>
<p>
    <a href="#" onclick="window.open('http://struts.apache.org/2.x/docs/set.html')">[More on the <i>set</i> tag]</a>
    <a href="#" onclick="window.open('http://struts.apache.org/2.x/docs/url.html')">[More on the <i>url</i> tag]</a>
</p>