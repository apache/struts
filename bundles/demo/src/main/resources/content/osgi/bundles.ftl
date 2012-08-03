<html>
    <head>
        <title>OSGi integration</title>
    </head>
    <body>
        This action was mapped by <b>Convention</b>, and shows how to get access to the <b>BundleContext</b>
        and registered services
        <br />
        Spring Application Contexts: ${applicationContextsCount!}
        <br>
        Bundles List:
        <ul>
            <#list bundles as bundle>
                <li>${bundle.symbolicName!}</li>
            </#list>
        </ul>
    </body>
</html>