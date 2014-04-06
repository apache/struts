<%
    request.setAttribute("decorator", "none");
    response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
    response.setHeader("Pragma","no-cache"); //HTTP 1.0
    response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
%>
<p>
    <b>String Attributes</b>
</p>
<p>
    Some tag attributes are expected to be Strings in which case String literals
    can be passed as the value, like the <i>href</i> attribute in the <i>a</i> tag.
</p>
<p>
    <i>
        &lt;s:a href=&quot;http://struts.apache.org/&quot; /&gt; 
    </i>
</p>
<p>
   If the value that you want to use in one of these string literal attributes is stored on the Value Stack,
   then the <i>%{#name}</i> syntax (alternative syntax) needs to be used. Assuming there is a value
   with the name "url" stored on the stack:
</p>
<p>
    <i>
        &lt;s:a href=&quot;%{#url}&quot; /&gt;
    </i>
</p>
<p>
  will create an anchor and use the value of "url" for the <i>href</i> attribute.
</p>
<p>
    <b>Value Attributes</b>
</p>
<p>
    Other attributes expect an object as their value(not an string literal). In these attributes you can specify
    the name of a variable stored on the Value Stack, and the tag will look it up and use it. Like the
    <i>value</i> attribute in the <i>property</i> tag. Assuming there is an object stored on the Value Stack with
    the name "movie", then:
</p>
<p>
    <i>
        &lt;s:property value=&quot;movie&quot; /&gt;
    </i>
</p>
<p>
  will print the value to the page. To pass an String literal to an attribute that expects a value use the <i>%{'string'}</i>
  notation.
</p>
<p>
  If you don't remember if an attribute expects an string literal or a value, you can always use the <i>%{value}</i> notation:
</p>
<p>
    <i>
        &lt;s:a href=&quot;%{'http://struts.apache.org/'}&quot; /&gt;
        <br />
        &lt;s:property value=&quot;%{#movie}&quot; /&gt;
    </i>
</p>
<p>
    <a href="#" onclick="window.open('http://struts.apache.org/2.x/docs/tag-syntax.html')">[More details]</a>
</p>