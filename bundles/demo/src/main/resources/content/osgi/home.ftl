<html>
    <head>
        <title>OSGi Demo Bundle</title>
    </head>
    <body>
        This demo contains actions that will be packaged into an OSGi bundle and loaded by the Struts 2 OSGi plugin.
        <br />
        <ul>
            <li><@s.a namespace="/osgi" action="hello-convention">Action mapped by the Convention plugin, with FreeMarker result</@s.a></li>
            <li><@s.a namespace="/osgi" action="hello-freemarker">Action mapped by XML, with FreeMarker result</@s.a></li>
            <li><@s.a namespace="/osgi" action="hello-velocity">Action mapped by XML, with Velocity result</@s.a></li>
        </ul>
    </body>
</html>