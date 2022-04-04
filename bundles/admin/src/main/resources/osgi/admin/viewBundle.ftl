<html>
    <head>
        <title>${bundle.symbolicName!}</title>

        <link rel="stylesheet" type="text/css" href="<@s.url value="/static/css/main.css" />" />
        <link rel="stylesheet" type="text/css" href="<@s.url value="/static/css/redmond/jquery-ui-1.7.1.custom.css" />" />

        <script src="<@s.url value="/static/js/jquery-1.3.2.min.js" />"></script>
        <script src="<@s.url value="/static/js/jquery-ui-1.7.1.custom.min.js" />"></script>

        <script type="text/javascript">
            $(function() {
                $("#tabs").tabs();
            });
        </script>

    </head>
<body>

<div class="menu">
    <div style="float:left;">
        <#if action.isAllowedAction(bundle, "start")>
        <a href="bundle_${bundle.symbolicName}!start.action" class="ui-state-default ui-corner-all fg-button fg-button-icon-left">
            <span class="ui-icon ui-icon-play"></span>
            Start
        </a>
        </#if>

        <#if action.isAllowedAction(bundle, "stop")>
        <a href="bundle_${bundle.symbolicName}!stop.action" class="ui-state-default ui-corner-all fg-button fg-button-icon-left">
            <span class="ui-icon ui-icon-stop"></span>
            Stop
        </a>
        </#if>

        <#if action.isAllowedAction(bundle, "update")>
        <a href="bundle_${bundle.symbolicName}!update.action" class="ui-state-default ui-corner-all fg-button fg-button-icon-left">
            <span class="ui-icon ui-icon-refresh"></span>
            Update
        </a>
        </#if>
    </div>

    <div  style="float:right;">
        <@s.url var="bundlesUrl" namespace="/osgi/admin" action="bundles" includeParams="none" />
        <@s.url var="osgiShellUrl" namespace="/osgi/admin" action="shell" includeParams="none" />
        <a href="${bundlesUrl}" class="ui-state-default ui-corner-all fg-button fg-button-icon-left">
            <span class="ui-icon ui-icon-bullet"></span>
            Bundles
        </a>
        <a href="${osgiShellUrl}" class="ui-state-default ui-corner-all fg-button fg-button-icon-left">
            <span class="ui-icon ui-icon-transferthick-e-w"></span>
            OSGi Shell
        </a>
    </div>    
</div>

<@s.actionerror />

<div id="tabs" class="tabs">
    <ul>
        <li><a href="#tabs-1">OSGi Metadata</a></li>
        <li><a href="#tabs-2">Resgistered Services</a></li>
        <li><a href="#tabs-3">Services in Use</a></li>
        <li><a href="#tabs-4">Packages</a></li>
        <li><a href="#tabs-5">Headers</a></li>
    </ul>
    <div id="tabs-1">
        <table class="properties">
            <tr>
                <td class="name">Id</td>
                <td>${bundle.bundleId!}</td>
            </tr>
            <tr>
                <td class="name">Name</td>
                <td>${bundle.symbolicName!}</td>
            </tr>
            <tr>
                <td class="name">Location</td>
                <td>${bundle.location!}</td>
            </tr>
            <tr>
                <td class="name">Status</td>
                <td>${action.getBundleState(bundle)}</td>
            </tr>
        </table>
    </div>
    <div id="tabs-2">
        <#list (bundle.registeredServices)! as service>
            <table class="properties">
                <#list (service.propertyKeys)! as key >
                    <tr>
                        <td class="name">${key}</td>
                        <td>${action.displayProperty(service.getProperty(key))}</td>
                    </tr>
                </#list>
            </table>
            <br/>
        </#list>
    </div>
    <div id="tabs-3">
        <#list (bundle.servicesInUse)! as service>
            <table class="properties">
                <#list (service.propertyKeys)! as key >
                    <tr>
                        <td class="name">${key}</td>
                        <td>${action.displayProperty(service.getProperty(key))!}</td>
                    </tr>
                </#list>
            </table>
            <br/>
        </#list>
    </div>
    <div id="tabs-4">
        <#list packages! as pkg>
            <table class="properties">
                <tr>
                    <td class="name">Name</td>
                    <td>${pkg.name}</td>
                </tr>
                <tr>
                    <td class="name">Actions</td>
                    <td>
                        <ul>
                            <#list (pkg.actionConfigs.keySet())! as name >
                                <li>${name}</li>
                            </#list>
                        </ul>
                    </td>
                </tr>
            </table>
            <br/>
        </#list>
    </div>
    <div id="tabs-5">
        <table class="properties">
        <#list headerKeys as header>
            <tr>
                <td class="name">${header}</td>
                <td><div class="propertyValue">${bundle.headers.get(header)}</div> </td>
            </tr>
        </#list>
        </table>
    </div>
</div>
</body>
</html>