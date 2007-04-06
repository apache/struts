<script language="JavaScript" type="text/javascript">
	dojo.addOnLoad(function() {
		dojo.widget.createWidget("struts:BindEvent", {
			"sources": "${parameters.sources?html}",
			"events": "${parameters.events?html}",
			<#if parameters.id?if_exists != "">
			  	"id": "${parameters.id?html}",
		    </#if>
		    <#if parameters.formId?if_exists != "">
			  	"formId": "${parameters.formId?html}",
		    </#if>
			<#if parameters.formFilter?if_exists != "">
			  	"formFilter": "${parameters.formFilter?html}",
			</#if>
			<#if parameters.href?if_exists != "">
			  	"href": "${parameters.href}",
			</#if>
			<#if parameters.loadingText?if_exists != "">
			    "loadingText" : "${parameters.loadingText?html}",
		    </#if>
			<#if parameters.errorText?if_exists != "">
			    "errorText" : "${parameters.errorText?html}",
			</#if>
			<#if parameters.executeScripts?exists>
			    "executeScripts": "${parameters.executeScripts?string?html}",
			</#if>
			<#if parameters.listenTopics?if_exists != "">
			    "listenTopics": "${parameters.listenTopics?html}",
			</#if>
			<#if parameters.notifyTopics?if_exists != "">
			    "notifyTopics": "${parameters.notifyTopics?html}",
			</#if>
			<#if parameters.targets?if_exists != "">
			    "targets": "${parameters.targets?html}",
			</#if>
			<#if parameters.indicator?if_exists != "">
			    "indicator": "${parameters.indicator?html}",
			</#if>
			<#if parameters.showErrorTransportText?exists>
			    "showError": "${parameters.showErrorTransportText?string?html}",
			</#if>
			<#if parameters.handler?if_exists != "">
			    "handler": "${parameters.handler?html}"
		    </#if>
		});
	});
</script>