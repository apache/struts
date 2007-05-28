<script language="JavaScript" type="text/javascript">
	dojo.addOnLoad(function() {
		dojo.widget.createWidget("struts:BindEvent", {
		    <#if parameters.sources?if_exists != "">
                "sources": "${parameters.sources?html}",<#rt/>
            </#if>
            <#if parameters.events?if_exists != "">
                "events": "${parameters.events?html}",<#rt/>
            </#if>
			<#if parameters.id?if_exists != "">
			  	"id": "${parameters.id?html}",<#rt/>
		    </#if>
		    <#if parameters.formId?if_exists != "">
			  	"formId": "${parameters.formId?html}",<#rt/>
		    </#if>
			<#if parameters.formFilter?if_exists != "">
			  	"formFilter": "${parameters.formFilter?html}",<#rt/>
			</#if>
			<#if parameters.href?if_exists != "">
			  	"href": "${parameters.href}",<#rt/>
			</#if>
			<#if parameters.loadingText?if_exists != "">
			    "loadingText" : "${parameters.loadingText?html}",<#rt/>
		    </#if>
			<#if parameters.errorText?if_exists != "">
			    "errorText" : "${parameters.errorText?html}",<#rt/>
			</#if>
			<#if parameters.executeScripts?exists>
			    "executeScripts": ${parameters.executeScripts?string?html},<#rt/>
			</#if>
			<#if parameters.listenTopics?if_exists != "">
			    "listenTopics": "${parameters.listenTopics?html}",<#t/>
			</#if>
			<#if parameters.notifyTopics?if_exists != "">
			    "notifyTopics": "${parameters.notifyTopics?html}",<#t/>
			</#if>
			<#if parameters.beforeNotifyTopics?if_exists != "">
			    "beforeNotifyTopics": "${parameters.beforeNotifyTopics?html}",<#t/>
		    </#if>
		    <#if parameters.afterNotifyTopics?if_exists != "">
		        "afterNotifyTopics": "${parameters.afterNotifyTopics?html}",<#t/>
    		</#if>
     		<#if parameters.errorNotifyTopics?if_exists != "">
		        "errorNotifyTopics": "${parameters.errorNotifyTopics?html}",<#t/>
     		</#if>
			<#if parameters.targets?if_exists != "">
			    "targets": "${parameters.targets?html}",<#t/>
			</#if>
			<#if parameters.indicator?if_exists != "">
			    "indicator": "${parameters.indicator?html}",<#t/>
			</#if>
			<#if parameters.showErrorTransportText?exists>
			    "showError": ${parameters.showErrorTransportText?string?html},<#t/>
			</#if>
			<#if parameters.showLoadingText?exists>
			    "showLoading": ${parameters.showLoadingText?string?html},<#t/>
			</#if>
			<#if parameters.handler?if_exists != "">
			    "handler": "${parameters.handler?html}",<#t/>
		    </#if>
		    <#if parameters.highlightColor?if_exists != "">
			    "highlightColor" : "${parameters.highlightColor?html}",<#t/>
			</#if>
			<#if parameters.highlightDuration?if_exists != "">
			    "highlightDuration" : ${parameters.highlightDuration?html},<#t/>
			</#if>
			<#if parameters.validate?exists>
			    "validate": ${parameters.validate?string?html},<#t/>
			<#else>
			    "validate": false,
			</#if>
			<#if parameters.ajaxAfterValidation?exists>
			    "ajaxAfterValidation": ${parameters.ajaxAfterValidation?string?html},<#t/>
			<#else>
			    "ajaxAfterValidation": false,    
			</#if>
			<#if parameters.separateScripts?exists>
                "scriptSeparation": ${parameters.separateScripts?string?html},<#rt/>
            </#if>
		});
	});
</script>