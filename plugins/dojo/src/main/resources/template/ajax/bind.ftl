<#assign firstField=true >
<script language="JavaScript" type="text/javascript">
	dojo.addOnLoad(function() {
		dojo.widget.createWidget("struts:BindEvent", {
		    <#if parameters.sources?if_exists != "">
		        <#assign firstField=false ><#t/>
                "sources": "${parameters.sources?html}"<#t/>
            </#if>
            <#if parameters.events?if_exists != "">
                <#if !firstField>,</#if><#t/>
                <#assign firstField=false ><#t/>
                "events": "${parameters.events?html}"<#t/>
            </#if>
			<#if parameters.id?if_exists != "">
			    <#if !firstField>,</#if><#t/>
                <#assign firstField=false ><#t/>
			  	"id": "${parameters.id?html}"<#t/>
		    </#if>
		    <#if parameters.formId?if_exists != "">
		        <#if !firstField>,</#if><#t/>
                <#assign firstField=false ><#t/>
			  	"formId": "${parameters.formId?html}"<#t/>
		    </#if>
			<#if parameters.formFilter?if_exists != "">
			    <#if !firstField>,</#if><#t/>
                <#assign firstField=false ><#t/>
			  	"formFilter": "${parameters.formFilter?html}"<#t/>
			</#if>
			<#if parameters.href?if_exists != "">
			    <#if !firstField>,</#if><#t/>
                <#assign firstField=false ><#t/>
			  	"href": "${parameters.href}"<#t/>
			</#if>
			<#if parameters.loadingText?if_exists != "">
			    <#if !firstField>,</#if><#t/>
                <#assign firstField=false ><#t/>
			    "loadingText" : "${parameters.loadingText?html}"<#t/>
		    </#if>
			<#if parameters.errorText?if_exists != "">
			    <#if !firstField>,</#if><#t/>
                <#assign firstField=false ><#t/>
			    "errorText" : "${parameters.errorText?html}"<#t/>
			</#if>
			<#if parameters.executeScripts?exists>
			    <#if !firstField>,</#if><#t/>
                <#assign firstField=false ><#t/>
			    "executeScripts": ${parameters.executeScripts?string?html}<#t/>
			</#if>
			<#if parameters.listenTopics?if_exists != "">
			    <#if !firstField>,</#if><#t/>
                <#assign firstField=false ><#t/>
			    "listenTopics": "${parameters.listenTopics?html}"<#t/>
			</#if>
			<#if parameters.notifyTopics?if_exists != "">
			    <#if !firstField>,</#if><#t/>
                <#assign firstField=false ><#t/>
			    "notifyTopics": "${parameters.notifyTopics?html}"<#t/>
			</#if>
			<#if parameters.beforeNotifyTopics?if_exists != "">
			    <#if !firstField>,</#if><#t/>
                <#assign firstField=false ><#t/>
			    "beforeNotifyTopics": "${parameters.beforeNotifyTopics?html}"<#t/>
		    </#if>
		    <#if parameters.afterNotifyTopics?if_exists != "">
		        <#if !firstField>,</#if><#t/>
                <#assign firstField=false ><#t/>
		        "afterNotifyTopics": "${parameters.afterNotifyTopics?html}"<#t/>
    		</#if>
     		<#if parameters.errorNotifyTopics?if_exists != "">
     		    <#if !firstField>,</#if><#t/>
                <#assign firstField=false ><#t/>
		        "errorNotifyTopics": "${parameters.errorNotifyTopics?html}"<#t/>
     		</#if>
			<#if parameters.targets?if_exists != "">
			    <#if !firstField>,</#if><#t/>
                <#assign firstField=false ><#t/>
			    "targets": "${parameters.targets?html}"<#t/>
			</#if>
			<#if parameters.indicator?if_exists != "">
			    <#if !firstField>,</#if><#t/>
                <#assign firstField=false ><#t/>
			    "indicator": "${parameters.indicator?html}"<#t/>
			</#if>
			<#if parameters.showErrorTransportText?exists>
			    <#if !firstField>,</#if><#t/>
                <#assign firstField=false ><#t/>
			    "showError": ${parameters.showErrorTransportText?string?html}<#t/>
			</#if>
			<#if parameters.showLoadingText?exists>
			    <#if !firstField>,</#if><#t/>
                <#assign firstField=false ><#t/>
			    "showLoading": ${parameters.showLoadingText?string?html}<#t/>
			</#if>
			<#if parameters.handler?if_exists != "">
			    <#if !firstField>,</#if><#t/>
                <#assign firstField=false ><#t/>
			    "handler": "${parameters.handler?html}"<#t/>
		    </#if>
		    <#if parameters.highlightColor?if_exists != "">
		        <#if !firstField>,</#if><#t/>
                <#assign firstField=false ><#t/>
			    "highlightColor" : "${parameters.highlightColor?html}"<#t/>
			</#if>
			<#if parameters.highlightDuration?if_exists != "">
			    <#if !firstField>,</#if><#t/>
                <#assign firstField=false ><#t/>
			    "highlightDuration" : ${parameters.highlightDuration?html}<#t/>
			</#if>
			<#if parameters.validate?exists>
			    <#if !firstField>,</#if><#t/>
                <#assign firstField=false ><#t/>
			    "validate": ${parameters.validate?string?html}<#t/>
			<#else>
			    <#if !firstField>,</#if><#t/>
                <#assign firstField=false ><#t/>
			    "validate": false
			</#if>
			<#if parameters.ajaxAfterValidation?exists>
			    <#if !firstField>,</#if><#t/>
                <#assign firstField=false ><#t/>
			    "ajaxAfterValidation": ${parameters.ajaxAfterValidation?string?html}<#t/>
			<#else>
			    <#if !firstField>,</#if><#t/>
                <#assign firstField=false ><#t/>
			    "ajaxAfterValidation": false
			</#if>
			<#if parameters.separateScripts?exists>
			    <#if !firstField>,</#if><#t/>
                <#assign firstField=false ><#t/>
                "scriptSeparation": ${parameters.separateScripts?string?html}<#t/>
            </#if>
            <#if parameters.transport?if_exists != "">
                <#if !firstField>,</#if><#t/>
                <#assign firstField=false ><#t/>
                "transport": "${parameters.transport?html}"<#t/>
            </#if>
		});
	});
</script>