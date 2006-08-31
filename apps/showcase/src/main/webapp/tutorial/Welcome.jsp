<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
  <head><title>Tutorial</title></head>
  <body>

  <p>
    This is the companion source code to the
    <a href="http://cwiki.apache.org/WW/step-by-step.html">
        Tutorial.</a>
  </p>

      <h2>Hello World</h2>

      <ul>
          <li>
              <s:url id="url" action="HelloWorld" />
              <s:a href="%{url}">Hello World!</s:a>
          </li>
      </ul>

      <h2>Understanding Actions</h2>

      <ul>
          <li>
              <s:url id="url" value="HelloName.html" />
              <s:a href="%{url}">Hello Name</s:a>
          </li>
          <li>
              <s:url id="url" value="HelloName2.html" />
              <s:a href="%{url}">Hello Name (2)</s:a>
          </li>
      </ul>

  </body>
</html>