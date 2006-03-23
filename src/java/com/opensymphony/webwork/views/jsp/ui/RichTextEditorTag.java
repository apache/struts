/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.jsp.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.opensymphony.webwork.components.Component;
import com.opensymphony.webwork.components.RichTextEditor;
import com.opensymphony.xwork.util.OgnlValueStack;

/**
 * WebWork's RichTextEditor Jsp Tag.
 * 
 * @author tm_jee
 * @version $Date: 2006/02/17 15:17:33 $ $Id: RichTextEditorTag.java,v 1.1 2006/02/17 15:17:33 tmjee Exp $
 */
public class RichTextEditorTag extends AbstractUITag {

	private static final long serialVersionUID = -3780294902103996827L;

	private String checkBrowser;
	private String displayError;
	private String basePath = null;
	private String toolbarSet = null;
	private String width = null;
	private String height = null;
	private String customConfigurationsPath = null;
	private String editorAreaCSS = null;
	private String baseHref = null;
	private String skinPath = null;
	private String pluginsPath = null;
	private String fullPage = null;
	private String debug = null;	
	private String autoDetectLanguage = null;
	private String defaultLanguage = null;
	private String contentLangDirection = null;
	private String enableXHTML = null;
	private String enableSourceXHTML = null;
	private String fillEmptyBlocks = null;
	private String formatSource = null;
	private String formatOutput = null;
	private String formatIndentator = null;
	private String geckoUseSPAN = null;
	private String startupFocus = null;
	private String forcePasteAsPlainText = null;
	private String forceSimpleAmpersand = null;
	private String tabSpaces = null;
	private String useBROnCarriageReturn = null;
	private String toolbarStartExpanded = null;
	private String toolbarCanCollapse = null;
	private String fontColors = null;
	private String fontNames = null;
	private String fontSizes = null;
	private String fontFormats = null;
	private String stylesXmlPath = null;
	private String templatesXmlPath = null;
	private String linkBrowserURL = null;
	private String imageBrowserURL = null;
	private String flashBrowserURL = null;
	private String linkUploadURL = null;
	private String imageUploadURL = null;
	private String flashUploadURL = null;
	
	private String allowImageBrowse;
	private String allowLinkBrowse;
	private String allowFlashBrowse;
	private String allowImageUpload;
	private String allowLinkUpload;
	private String allowFlashUpload;
	private String linkUploadAllowedExtension;
	private String linkUploadDeniedExtension;
	private String imageUploadAllowedExtension;
	private String imageUploadDeniedExtension;
	private String flashUploadAllowedExtension;
	private String flashUploadDeniedExtension;
	private String smileyPath;
	private String smileyImages;
	
	
	public Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
		return new RichTextEditor(stack, req, res);
	}

	protected void populateParams() {
		super.populateParams();
		
		RichTextEditor richTextEditor = (RichTextEditor) component;
		
		richTextEditor.setCheckBrowser(checkBrowser);
		richTextEditor.setDisplayError(displayError);
		richTextEditor.setBasePath(basePath);
		richTextEditor.setToolbarSet(toolbarSet);
		richTextEditor.setWidth(width);
		richTextEditor.setHeight(height);
		richTextEditor.setCustomConfigurationsPath(customConfigurationsPath);
		richTextEditor.setEditorAreaCSS(editorAreaCSS);
		richTextEditor.setBaseHref(baseHref);
		richTextEditor.setSkinPath(skinPath);
		richTextEditor.setPluginsPath(pluginsPath);
		richTextEditor.setFullPage(fullPage);
		richTextEditor.setDebug(debug);
		richTextEditor.setAutoDetectLanguage(autoDetectLanguage);
		richTextEditor.setDefaultLanguage(defaultLanguage);
		richTextEditor.setContentLangDirection(contentLangDirection);
		richTextEditor.setEnableXHTML(enableXHTML);
		richTextEditor.setEnableSourceXHTML(enableSourceXHTML);
		richTextEditor.setFillEmptyBlocks(fillEmptyBlocks);
		richTextEditor.setFormatSource(formatSource);
		richTextEditor.setFormatOutput(formatOutput);
		richTextEditor.setFormatIndentator(formatIndentator);
		richTextEditor.setGeckoUseSPAN(geckoUseSPAN);
		richTextEditor.setStartupFocus(startupFocus);
		richTextEditor.setForcePasteAsPlainText(forcePasteAsPlainText);
		richTextEditor.setForceSimpleAmpersand(forceSimpleAmpersand);
		richTextEditor.setTabSpaces(tabSpaces);
		richTextEditor.setUseBROnCarriageReturn(useBROnCarriageReturn);
		richTextEditor.setToolbarStartExpanded(toolbarStartExpanded);
		richTextEditor.setToolbarCanCollapse(toolbarCanCollapse);
		richTextEditor.setFontColors(fontColors);
		richTextEditor.setFontSizes(fontSizes);
		richTextEditor.setFontNames(fontNames);
		richTextEditor.setFontFormats(fontFormats);
		richTextEditor.setStylesXmlPath(stylesXmlPath);
		richTextEditor.setTemplatesXmlPath(templatesXmlPath);
		richTextEditor.setLinkBrowserURL(linkBrowserURL);
		richTextEditor.setImageBrowserURL(imageBrowserURL);
		richTextEditor.setFlashBrowserURL(flashBrowserURL);
		richTextEditor.setLinkUploadURL(linkUploadURL);
		richTextEditor.setImageUploadURL(imageUploadURL);
		richTextEditor.setFlashUploadURL(flashUploadURL);
		
		richTextEditor.setAllowImageBrowse(allowImageBrowse);
		richTextEditor.setAllowLinkBrowse(allowLinkBrowse);
		richTextEditor.setAllowFlashBrowse(allowFlashBrowse);
		richTextEditor.setAllowLinkUpload(allowLinkUpload);
		richTextEditor.setAllowImageUpload(allowImageUpload);
		richTextEditor.setAllowFlashUpload(allowFlashUpload);
		richTextEditor.setLinkUploadAllowedExtension(linkUploadAllowedExtension);
		richTextEditor.setLinkUploadDeniedExtension(linkUploadDeniedExtension);
		richTextEditor.setImageUploadAllowedExtension(imageUploadAllowedExtension);
		richTextEditor.setImageUploadDeniedExtension(imageUploadDeniedExtension);
		richTextEditor.setFlashUploadAllowedExtension(flashUploadAllowedExtension);
		richTextEditor.setFlashUploadDeniedExtension(flashUploadDeniedExtension);
		richTextEditor.setSmileyPath(smileyPath);
		richTextEditor.setSmileyImages(smileyImages);
		
		
	}

	public String getAutoDetectLanguage() {
		return autoDetectLanguage;
	}

	public void setAutoDetectLanguage(String autoDetectLanguage) {
		this.autoDetectLanguage = autoDetectLanguage;
	}

	public String getBaseHref() {
		return baseHref;
	}

	public void setBaseHref(String baseHref) {
		this.baseHref = baseHref;
	}

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public String getCheckBrowser() {
		return checkBrowser;
	}

	public void setCheckBrowser(String checkBrowser) {
		this.checkBrowser = checkBrowser;
	}

	public String getContentLangDirection() {
		return contentLangDirection;
	}

	public void setContentLangDirection(String contentLangDirection) {
		this.contentLangDirection = contentLangDirection;
	}

	public String getCustomConfigurationsPath() {
		return customConfigurationsPath;
	}

	public void setCustomConfigurationsPath(String customConfigurationsPath) {
		this.customConfigurationsPath = customConfigurationsPath;
	}

	public String getDebug() {
		return debug;
	}

	public void setDebug(String debug) {
		this.debug = debug;
	}

	public String getDefaultLanguage() {
		return defaultLanguage;
	}

	public void setDefaultLanguage(String defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
	}

	public String getDisplayError() {
		return displayError;
	}

	public void setDisplayError(String displayError) {
		this.displayError = displayError;
	}

	public String getEditorAreaCSS() {
		return editorAreaCSS;
	}

	public void setEditorAreaCSS(String editorAreaCSS) {
		this.editorAreaCSS = editorAreaCSS;
	}

	public String getEnableSourceXHTML() {
		return enableSourceXHTML;
	}

	public void setEnableSourceXHTML(String enableSourceXHTML) {
		this.enableSourceXHTML = enableSourceXHTML;
	}

	public String getEnableXHTML() {
		return enableXHTML;
	}

	public void setEnableXHTML(String enableXHTML) {
		this.enableXHTML = enableXHTML;
	}

	public String getFillEmptyBlocks() {
		return fillEmptyBlocks;
	}

	public void setFillEmptyBlocks(String fillEmptyBlocks) {
		this.fillEmptyBlocks = fillEmptyBlocks;
	}

	public String getFlashBrowserURL() {
		return flashBrowserURL;
	}

	public void setFlashBrowserURL(String flashBrowserURL) {
		this.flashBrowserURL = flashBrowserURL;
	}

	public String getFlashUploadURL() {
		return flashUploadURL;
	}

	public void setFlashUploadURL(String flashUploadURL) {
		this.flashUploadURL = flashUploadURL;
	}

	public String getFontColors() {
		return fontColors;
	}

	public void setFontColors(String fontColors) {
		this.fontColors = fontColors;
	}

	public String getFontFormats() {
		return fontFormats;
	}

	public void setFontFormats(String fontFormats) {
		this.fontFormats = fontFormats;
	}

	public String getFontNames() {
		return fontNames;
	}

	public void setFontNames(String fontNames) {
		this.fontNames = fontNames;
	}

	public String getFontSizes() {
		return fontSizes;
	}

	public void setFontSizes(String fontSizes) {
		this.fontSizes = fontSizes;
	}

	public String getForcePasteAsPlainText() {
		return forcePasteAsPlainText;
	}

	public void setForcePasteAsPlainText(String forcePasteAsPlainText) {
		this.forcePasteAsPlainText = forcePasteAsPlainText;
	}

	public String getForceSimpleAmpersand() {
		return forceSimpleAmpersand;
	}

	public void setForceSimpleAmpersand(String forceSimpleAmpersand) {
		this.forceSimpleAmpersand = forceSimpleAmpersand;
	}

	public String getFormatIndentator() {
		return formatIndentator;
	}

	public void setFormatIndentator(String formatIndentator) {
		this.formatIndentator = formatIndentator;
	}

	public String getFormatOutput() {
		return formatOutput;
	}

	public void setFormatOutput(String formatOutput) {
		this.formatOutput = formatOutput;
	}

	public String getFormatSource() {
		return formatSource;
	}

	public void setFormatSource(String formatSource) {
		this.formatSource = formatSource;
	}

	public String getFullPage() {
		return fullPage;
	}

	public void setFullPage(String fullPage) {
		this.fullPage = fullPage;
	}

	public String getGeckoUseSPAN() {
		return geckoUseSPAN;
	}

	public void setGeckoUseSPAN(String geckoUseSPAN) {
		this.geckoUseSPAN = geckoUseSPAN;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getImageBrowserURL() {
		return imageBrowserURL;
	}

	public void setImageBrowserURL(String imageBrowserURL) {
		this.imageBrowserURL = imageBrowserURL;
	}

	public String getImageUploadURL() {
		return imageUploadURL;
	}

	public void setImageUploadURL(String imageUploadURL) {
		this.imageUploadURL = imageUploadURL;
	}

	public String getLinkBrowserURL() {
		return linkBrowserURL;
	}

	public void setLinkBrowserURL(String linkBrowserURL) {
		this.linkBrowserURL = linkBrowserURL;
	}

	public String getLinkUploadURL() {
		return linkUploadURL;
	}

	public void setLinkUploadURL(String linkUploadURL) {
		this.linkUploadURL = linkUploadURL;
	}

	public String getPluginsPath() {
		return pluginsPath;
	}

	public void setPluginsPath(String pluginsPath) {
		this.pluginsPath = pluginsPath;
	}

	public String getSkinPath() {
		return skinPath;
	}

	public void setSkinPath(String skinPath) {
		this.skinPath = skinPath;
	}

	public String getStartupFocus() {
		return startupFocus;
	}

	public void setStartupFocus(String startupFocus) {
		this.startupFocus = startupFocus;
	}

	public String getStylesXmlPath() {
		return stylesXmlPath;
	}

	public void setStylesXmlPath(String stylesXmlPath) {
		this.stylesXmlPath = stylesXmlPath;
	}

	public String getTabSpaces() {
		return tabSpaces;
	}

	public void setTabSpaces(String tabSpaces) {
		this.tabSpaces = tabSpaces;
	}

	public String getToolbarCanCollapse() {
		return toolbarCanCollapse;
	}

	public void setToolbarCanCollapse(String toolbarCanCollapse) {
		this.toolbarCanCollapse = toolbarCanCollapse;
	}

	public String getToolbarSet() {
		return toolbarSet;
	}

	public void setToolbarSet(String toolbarSet) {
		this.toolbarSet = toolbarSet;
	}

	public String getToolbarStartExpanded() {
		return toolbarStartExpanded;
	}

	public void setToolbarStartExpanded(String toolbarStartExpanded) {
		this.toolbarStartExpanded = toolbarStartExpanded;
	}

	public String getUseBROnCarriageReturn() {
		return useBROnCarriageReturn;
	}

	public void setUseBROnCarriageReturn(String useBROnCarriageReturn) {
		this.useBROnCarriageReturn = useBROnCarriageReturn;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getAllowFlashBrowse() {
		return allowFlashBrowse;
	}

	public void setAllowFlashBrowse(String allowFlashBrowse) {
		this.allowFlashBrowse = allowFlashBrowse;
	}

	public String getAllowFlashUpload() {
		return allowFlashUpload;
	}

	public void setAllowFlashUpload(String allowFlashUpload) {
		this.allowFlashUpload = allowFlashUpload;
	}

	public String getAllowImageBrowse() {
		return allowImageBrowse;
	}

	public void setAllowImageBrowse(String allowImageBrowse) {
		this.allowImageBrowse = allowImageBrowse;
	}

	public String getAllowImageUpload() {
		return allowImageUpload;
	}

	public void setAllowImageUpload(String allowImageUpload) {
		this.allowImageUpload = allowImageUpload;
	}

	public String getAllowLinkBrowse() {
		return allowLinkBrowse;
	}

	public void setAllowLinkBrowse(String allowLinkBrowse) {
		this.allowLinkBrowse = allowLinkBrowse;
	}

	public String getAllowLinkUpload() {
		return allowLinkUpload;
	}

	public void setAllowLinkUpload(String allowLinkUpload) {
		this.allowLinkUpload = allowLinkUpload;
	}

	public String getFlashUploadAllowedExtension() {
		return flashUploadAllowedExtension;
	}

	public void setFlashUploadAllowedExtension(String flashUploadAllowedExtension) {
		this.flashUploadAllowedExtension = flashUploadAllowedExtension;
	}

	public String getFlashUploadDeniedExtension() {
		return flashUploadDeniedExtension;
	}

	public void setFlashUploadDeniedExtension(String flashUploadDeniedExtension) {
		this.flashUploadDeniedExtension = flashUploadDeniedExtension;
	}

	public String getImageUploadAllowedExtension() {
		return imageUploadAllowedExtension;
	}

	public void setImageUploadAllowedExtension(String imageUploadAllowedExtension) {
		this.imageUploadAllowedExtension = imageUploadAllowedExtension;
	}

	public String getImageUploadDeniedExtension() {
		return imageUploadDeniedExtension;
	}

	public void setImageUploadDeniedExtension(String imageUploadDeniedExtension) {
		this.imageUploadDeniedExtension = imageUploadDeniedExtension;
	}

	public String getLinkUploadAllowedExtension() {
		return linkUploadAllowedExtension;
	}

	public void setLinkUploadAllowedExtension(String linkUploadAllowedExtension) {
		this.linkUploadAllowedExtension = linkUploadAllowedExtension;
	}

	public String getLinkUploadDeniedExtension() {
		return linkUploadDeniedExtension;
	}

	public void setLinkUploadDeniedExtension(String linkUploadDeniedExtension) {
		this.linkUploadDeniedExtension = linkUploadDeniedExtension;
	}

	public String getSmileyImages() {
		return smileyImages;
	}

	public void setSmileyImages(String smileyImages) {
		this.smileyImages = smileyImages;
	}

	public String getSmileyPath() {
		return smileyPath;
	}

	public void setSmileyPath(String smileyPath) {
		this.smileyPath = smileyPath;
	}

	public String getTemplatesXmlPath() {
		return templatesXmlPath;
	}

	public void setTemplatesXmlPath(String templatesXmlPath) {
		this.templatesXmlPath = templatesXmlPath;
	}
	
	
	
}
