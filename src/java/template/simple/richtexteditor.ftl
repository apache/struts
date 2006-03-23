<#if !stack.findValue('#richtexteditor_js_included')?exists><#t/>
	<script type="text/javascript" src="<@ww.url value='/webwork/richtexteditor/fckeditor.js' encode='false' />"></script>
	<#assign tmpVariable = stack.setValue('#richtexteditor_js_included', 'true') /><#t/>
</#if><#t/>
<script>
	var oFCKeditor_${parameters.id} = new FCKeditor( '${parameters.name}' ) ;
	<#-- basePath --><#t/>
	<#if parameters.basePath?exists><#t/>
		oFCKeditor_${parameters.id}.BasePath = '${parameters.basePath}' ;
	<#else><#t/>
		oFCKeditor_${parameters.id}.BasePath = '<@ww.url value="/webwork/richtexteditor/" />' ;
	</#if><#t/>
	<#-- height --><#t/>
	<#if parameters.height?exists><#t/>
		oFCKeditor_${parameters.id}.Height	= '${parameters.height}' ;
	</#if><#t/>
	<#-- width --><#t/>
	<#if parameters.width?exists><#lt/>
		oFCKeditor_${parameters.id}.Width = '${parameters.width}' ;
	</#if><#t/>
	<#-- toolbarSet --><#t/>
	<#if parameters.toolbarSet?exists><#t/>
		oFCKeditor_${parameters.id}.ToolbarSet = '${parameters.toolbarSet}' ;
	</#if><#t/>
	<#-- checkBrowser --><#t/>
	<#if parameters.checkBrowser?exists><#t/>
		oFCKeditor_${parameters.id}.CheckBrowser = '${parameters.checkBrowser}' ;
	</#if><#t/>
	<#-- displayError --><#t/>
	<#if parameters.displayError?exists><#t/>
		oFCKeditor_${parameters.id}.DisplayError = '${parameters.displayError}' ;
	</#if><#t/>
	<#-- value --><#t/>
	<@ww.set name="tmpVal" value="parameters.nameValue" /><#t/>
	<#if (stack.findValue('#tmpVal')?has_content)><#t/>
		oFCKeditor_${parameters.id}.Value = '<@ww.property escape="false" value="parameters.nameValue" />' ;
	</#if><#t/>
	<#-- customConfigurationsPath --><#t/>
	<#if parameters.customConfigurationsPath?exists><#t/>
		oFCKeditor_${parameters.id}.Config['CustomConfigurationsPath'] = '${parameters.customConfigurationsPath}' ;
	</#if><#t/>
	<#-- editorAreaCSS --><#t/>
	<#if parameters.editorAreaCss?exists><#t/>
		oFCKeditor_${parameters.id}.Config['EditorAreaCSS'] = '<@ww.url value=parameters.editorAreaCss?string />' ;
	</#if><#t/>
	<#-- baseHref --><#t/>
	<#if parameters.baseHref?exists><#t/>
			oFCKeditor_${parameters.id}.Config['BaseHref'] = '${parameters.baseHref}' ;
	</#if><#t/>
	<#-- skinPath --><#t/>
	<#if parameters.skinPath?exists><#t/>
		oFCKeditor_${parameters.id}.Config['SkinPath'] = '${parameters.skinPath}' ;
	</#if><#t/>
	<#-- pluginsPath --><#t/>
	<#if parameters.pluginsPath?exists><#t/>
		oFCKeditor_${parameters.id}.Config['PluginsPath'] = '${parameters.pluginsPath}' ;
	</#if><#t/>
	<#-- fullPage --><#t/>
	<#if parameters.fullPage?exists><#t/>
		oFCKeditor_${parameters.id}.Config['FullPage'] = '${parameters.fullPage}' ;
	</#if><#t/>
	<#-- debug --><#t/>
	<#if parameters.debug?exists><#t/>
		oFCKeditor_${parameters.id}.Config['Debug'] = '${parameters.debug}' ;
	</#if><#t/>
	<#-- autoDetectLanguage --><#t/>
	<#if parameters.autoDetectLanguage?exists><#t/>
		oFCKeditor_${parameters.id}.Config['AutoDetectLanguage'] = '${parameters.autoDetectLanguage}' ;
	</#if><#t/>
	<#-- defaultLanguage --><#t/>
	<#if parameters.defaultLanguage?exists><#t/>
		oFCKeditor_${parameters.id}.Config['DefaultLanguage'] = '${parameters.defaultLanguage}' ;
	</#if><#t/>
	<#-- contentLanguageDirection --><#t/>
	<#if parameters.contentLangDirection?exists><#t/>
		oFCKeditor_${parameters.id}.Config['ContentLangDirection'] = '${parameters.contentLangDirection}' ;
	</#if><#t/>
	<#-- enableXHTML  --><#t/>
	<#if parameters.enableXHTML?exists><#t/>
		oFCKeditor_${parameters.id}.Config['EnableXHTML'] = '${parameters.enableXHTML}' ;
	</#if><#t/>
	<#-- enableSourceXHTML --><#t/>
	<#if parameters.enableSourceXHTML?exists><#t/>
		oFCKeditor_${parameters.id}.Config['EnableSourceXHTML'] = '${parameters.enableSourceXHTML}' ;
	</#if><#t/>
	<#-- fillEmptyBlocks --><#t/>
	<#if parameters.fillEmptyBlocks?exists><#t/>
		oFCKeditor_${parameters.id}.Config['FillEmptyBlocks'] = '${parameters.fillEmptyBlocks}' ;
	</#if><#t/>
	<#-- formatSource --><#t/>
	<#if parameters.formatSource?exists><#t/>
		oFCKeditor_${parameters.id}.Config['FormatSource'] = '${parameters.formatSource}' ;
	</#if><#t/>
	<#-- formatOutput --><#t/>
	<#if parameters.formatOutput?exists><#t/>
		oFCKeditor_${parameters.id}.Config['FormatOutput'] = '${parameters.formatOutput}' ;
	</#if><#t/>
	<#-- formatIndentator --><#t/>
	<#if parameters.formatIndentator?exists><#t/>
		oFCKeditor_${parameters.id}.Config['FormatIndentator'] = '${parameters.formatIndentator}' ;
	</#if><#t/>
	<#-- geckoUseSPAN --><#t/>
	<#if parameters.geckoUseSPAN?exists><#t/>
		oFCKeditor_${parameters.id}.Config['GeckoUseSPAN'] = '${parameters.geckoUseSPAN}' ;
	</#if><#t/>
	<#-- startupFocus --><#t/>
	<#if parameters.startupFocus?exists><#t/>
		oFCKeditor_${parameters.id}.Config['StartupFocus'] = '${parameters.startupFocus}' ;
	</#if><#t/>
	<#-- forcePasteAsPlainText --><#t/>
	<#if parameters.forcePasteAsPlainText?exists><#t/>
		oFCKeditor_${parameters.id}.Config['ForcePasteAsPlainText'] = '${parameters.forcePasteAsPlainText}' ;
	</#if><#t/>
	<#-- forceSimpleAmpersand --><#t/>
	<#if parameters.forceSimpleAmpersand?exists><#t/>
		oFCKeditor_${parameters.id}.Config['ForceSimpleAmpersand'] = '${parameters.forceSimpleAmpersand}' ;
	</#if><#t/>
	<#-- tabSpaces --><#t/>
	<#if parameters.tabSpaces?exists><#t/>
		oFCKeditor_${parameters.id}.Config['TabSpaces'] = '${parameters.tabSpaces}' ;	
	</#if><#t/>
	<#-- useBROnCarriageReturn --><#t/>
	<#if parameters.useBROnCarriageReturn?exists><#t/>
		oFCKeditor_${parameters.id}.Config['UseBROnCarriageReturn'] = '${parameters.useBROnCarriageReturn}' ;
	</#if><#t/>
	<#-- toolbarStartExpanded --><#t/>
	<#if parameters.toolbarStartExpanded?exists><#t/>
		oFCKeditor_${parameters.id}.Config['ToolbarStartExpanded'] = '${parameters.toolbarStartExpanded}' ;
	</#if><#t/>
	<#-- toolbarCanCollapse --><#t/>
	<#if parameters.toolbarCanCollapse?exists><#t/>
		oFCKeditor_${parameters.id}.Config['ToolbarCanCollapse'] = '${parameters.toolbarCanCollapse}' ;
	</#if><#t/>
	<#-- fontColors --><#t/>
	<#if parameters.fontColors?exists><#t/>
		oFCKeditor_${parameters.id}.Config['FontColors'] = '${parameters.fontColors}' ;
	</#if><#t/>
	<#-- fontNames --><#t/>
	<#if parameters.fontNames?exists><#t/>
		oFCKeditor_${parameters.id}.Config['FontNames'] = '${parameters.fontNames}' ;
	</#if><#t/>
	<#-- fontSizes --><#t/>
	<#if parameters.fontSizes?exists><#t/>
		oFCKeditor_${parameters.id}.Config['FontSizes'] = '${parameters.fontSizes}' ;
	</#if><#t/>
	<#-- fontFormats --><#t/>
	<#if parameters.fontFormats?exists><#t/>
		oFCKeditor_${parameters.id}.Config['FontFormats'] = '${parameters.fontFormats}' ;
	</#if><#t/>
	<#-- stylesXmlPath --><#t/>
	<#if parameters.stylesXmlPath?exists><#t/>
		oFCKeditor_${parameters.id}.Config['StylesXmlPath'] = '<@ww.url value=parameters.stylesXmlPath?string />' ;
	</#if><#t/>
	<#-- templatesXmlPath --><#t/>
	<#if parameters.templatesXmlPath?exists><#t/>
		oFCKeditor_${parameters.id}.Config['TemplatesXmlPath'] = '<@ww.url value=parameters.templatesXmlPath?string />' ;
	</#if><#t/>
	<#-- linkBrowserURL --><#t/>
	<#if parameters.linkBrowserURL?exists><#t/>
		oFCKeditor_${parameters.id}.Config['LinkBrowserURL'] = '<@ww.url value=parameters.linkBrowserURL?string />' ;
	</#if><#t/>
	<#-- imageBrowserURL --><#t/>
	<#if parameters.imageBrowserURL?exists><#t/>
		oFCKeditor_${parameters.id}.Config['ImageBrowserURL'] = '<@ww.url value=parameters.imageBrowserURL?string />' ;
	</#if><#t/>
	<#-- flashBrowserURL --><#t/>
	<#if parameters.flashBrowserURL?exists><#t/>
		oFCKeditor_${parameters.id}.Config['FlashBrowserURL'] = '<@ww.url value=parameters.flashBrowserURL?string />' ;
	</#if><#t/>
	<#-- linkUploadURL --><#t/>
	<#if parameters.linkUploadURL?exists><#t/>
		oFCKeditor_${parameters.id}.Config['LinkUploadURL'] = '<@ww.url value=parameters.linkUploadURL?string />' ;
	</#if><#t/>
	<#-- imageUploadURL --><#t/>
	<#if parameters.imageUploadURL?exists><#t/>
		oFCKeditor_${parameters.id}.Config['ImageUploadURL'] = '<@ww.url value=parameters.imageUploadURL?string />' ;
	</#if><#t/>
	<#-- flashUploadURL --><#t/>
	<#if parameters.flashUploadURL?exists><#t/>
		oFCKeditor_${parameters.id}.Config['FlashUploadURL'] = '<@ww.url value=parameters.flashUploadURL?string />' ;
	</#if><#t/>
	<#-- allowImageBrowse --><#t/>
	<#if parameters.allowImageBrowse?exists><#t/>
		oFCKeditor_${parameters.id}.Config['ImageBrowser'] = '<@ww.url value=parameters.allowImageBrowse?string />' ;
	</#if><#t/>
	<#-- allowLinkBrowse --><#t/>
	<#if parameters.allowLinkBrowse?exists><#t/>
		oFCKeditor_${parameters.id}.Config['LinkBrowser'] = '<@ww.url value=parameters.allowLinkBrowse?string />' ;
	</#if><#t/>
	<#-- allowFlashBrowse --><#t/>
	<#if parameters.allowFlashBrowse?exists><#t/>
		oFCKeditor_${parameters.id}.Config['FlashBrowser'] = '${parameters.allowFlashBrowse}' ;
	</#if><#t/>
	<#-- allowImageUpload --><#t/>
	<#if parameters.allowImageUpload?exists><#t/>
		oFCKeditor_${parameters.id}.Config['ImageUpload'] = '${parameters.allowImageUpload}' ;
	</#if><#t/>
	<#-- allowLinkUpload --><#t/>
	<#if parameters.allowLinkUpload?exists><#t/>
		oFCKeditor_${parameters.id}.Config['LinkUpload'] = '${parameters.allowLinkUpload}' ;
	</#if><#t/>
	<#-- allowFlashUpload --><#t/>
	<#if parameters.allowFlashUpload?exists><#t/>
		oFCKeditor_${parameters.id}.Config['FlashUpload'] = '${parameters.allowFlashUpload}' ;
	</#if><#t/>
	<#-- linkUploadAllowedExtension --><#t/>
	<#if parameters.linkUploadAllowedExtension?exists><#t/>
		oFCKeditor_${parameters.id}.Config['LinkUploadAllowedExtensions'] = '${parameters.linkUploadAllowedExtension}' ;
	</#if><#t/>
	<#-- linkUploadDeniedExtension --><#t/>
	<#if parameters.linkUploadDeniedExtension?exists><#t/>
		oFCKeditor_${parameters.id}.Config['LinkUploadDeniedExtensions'] = '${parameters.linkUploadDeniedExtension}' ;
	</#if><#t/>
	<#-- imageUploadAllowedExtension --><#t/>
	<#if parameters.imageUploadAllowedExtension?exists><#t/>
		oFCKeditor_${parameters.id}.Config['ImageUploadAllowedExtensions'] = '${parameters.imageUploadAllowedExtension}' ;
	</#if><#t/>
	<#-- imageUploadDeniedExtension --><#t/>
	<#if parameters.imageUploadDeniedExtension?exists><#t/>
		oFCKeditor_${parameters.id}.Config['ImageUploadDeniedExtensions'] = '${parameters.imageUploadDeniedExtension}' ;
	</#if><#t/>
	<#-- flashUploadAllowedExtension --><#t/>
	<#if parameters.flashUploadAllowedExtension?exists><#t/>
		oFCKeditor_${parameters.id}.Config['FlashUploadAllowedExtensions'] = '${parameters.flashUploadAllowedExtension}' ;
	</#if><#t/>
	<#-- flashUploadDeniedExtension --><#t/>
	<#if parameters.flashUploadDeniedExtension?exists><#t/>
		oFCKeditor_${parameters.id}.Config['FlashUploadDeniedExtensions'] = '${parameters.flashUploadDeniedExtension}' ;
	</#if><#t/>
	<#-- smileyPath --><#t/>
	<#if parameters.smileyPath?exists><#t/>
		oFCKeditor_${parameters.id}.Config['SmileyPath'] = '<@ww.url value=parameters.smileyPath?string />' ;
	</#if><#t/>
	<#-- smileyImages --><#t/>
	<#if parameters.smileyImages?exists><#t/>
		oFCKeditor_${parameters.id}.Config['SmileyImages'] = "${parameters.smileyImages}" ;
	</#if><#t/>
	oFCKeditor_${parameters.id}.Create() ;
</script>
