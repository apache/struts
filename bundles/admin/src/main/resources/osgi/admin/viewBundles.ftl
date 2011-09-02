<html>
    <head>
        <title>OSGi Bundles</title>

        <link rel="stylesheet" type="text/css" href="<@s.url value="/static/css/main.css" />" />
        <link rel="stylesheet" type="text/css" href="<@s.url value="/static/css/redmond/jquery-ui-1.7.1.custom.css" />" />

        <script src="<@s.url value="/static/js/jquery-1.3.2.min.js" />"></script>
        <script src="<@s.url value="/static/js/jquery-ui-1.7.1.custom.min.js" />"></script>
    </head>
<body>

<div class="menu">
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
<table class="properties" style="clear:both; width:700px">
    <thead>
        <tr>
            <th>Name</th>
            <th>Status</th>
            <th>Struts Bundle</th>
            <th>Actions</th>
        </tr>
    </thead>
    <tbody>
        <#list bundles as bundle>
        <tr>
            <td>
                <a href="bundle_${bundle.symbolicName}!view.action">${bundle.symbolicName}</a>
            </td>
            <td>${action.getBundleState(bundle)}</td>
            <td>${action.isStrutsEnabled(bundle)?string("yes", "no")}</td>
            <td style="width:200px">
                <#if action.isAllowedAction(bundle, "start")>
                <a href="bundle_${bundle.symbolicName}!start.action" class="ui-state-default ui-corner-all fg-button-small fg-button-icon-left">
                    <span class="ui-icon ui-icon-play"></span>
                    Start
                </a>
                </#if>

                <#if action.isAllowedAction(bundle, "stop")>
                <a href="bundle_${bundle.symbolicName}!stop.action" class="ui-state-default ui-corner-all fg-button-small fg-button-icon-left">
                    <span class="ui-icon ui-icon-stop"></span>
                    Stop
                </a>
                </#if>

                <#if action.isAllowedAction(bundle, "update")>
                <a href="bundle_${bundle.symbolicName}!update.action" class="ui-state-default ui-corner-all fg-button-small fg-button-icon-left">
                    <span class="ui-icon ui-icon-refresh"></span>
                    Update
                </a>
                </#if>
            </td>
        </tr>
        </#list>
    </tbody>
</table>
</body>
</html>
