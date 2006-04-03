/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.views.jsp.ui;

import java.util.Locale;

import org.apache.struts.action2.views.jsp.AbstractUITagTest;
import com.opensymphony.xwork.ActionContext;

/**
 * RichTextEditor test case.
 * 
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class RichTextEditorTagTest extends AbstractUITagTest {

	public void testBasic() throws Exception {
		RichTextEditorTag tag = new RichTextEditorTag();
		tag.setPageContext(pageContext);
		tag.setLabel("myLabel");
		tag.setId("myId");
		tag.setName("myName");
        tag.setDefaultLanguage("en");

		tag.doStartTag();
		tag.doEndTag();
		
		verify(RichTextEditorTagTest.class.getResource("richtexteditor-1.txt"));
	}
	
	public void testWithSomeAttributeSet() throws Exception {
		RichTextEditorTag tag = new RichTextEditorTag();
		tag.setPageContext(pageContext);
		tag.setLabel("myLabel");
		tag.setId("myId");
		tag.setName("myName");
		tag.setBasePath("/some/base/path/");
		tag.setWidth("100%");
		tag.setHeight("100%");
		tag.setToolbarSet("MyToolBarSet");
		tag.setCheckBrowser("true");
		tag.setDisplayError("true");
		tag.setValue("some text to be displayed");
		tag.setCustomConfigurationsPath("myCustom/Configuration/Path");
		tag.setEditorAreaCSS("/some/editor/area/css");
		tag.setBaseHref("/base/href");
		tag.setSkinPath("/skin/path");
		tag.setPluginsPath("/plugins/path");
		tag.setFullPage("true");
		tag.setDebug("true");
		tag.setAutoDetectLanguage("true");
		tag.setDefaultLanguage("en");
		tag.setContentLangDirection("ltr");
		tag.setEnableXHTML("true");
		tag.setEnableSourceXHTML("true");
		tag.setFillEmptyBlocks("true");
		tag.setFormatSource("true");
		tag.setFormatOutput("true");
		tag.setFormatIndentator("\\t\\t");
		tag.setGeckoUseSPAN("true");
		tag.setStartupFocus("false");
		tag.setForcePasteAsPlainText("true");
		tag.setForceSimpleAmpersand("true");
		tag.setTabSpaces("\\t");
		tag.setUseBROnCarriageReturn("true");
		tag.setToolbarStartExpanded("true");
		tag.setToolbarCanCollapse("true");
		tag.setFontNames("Arial;Comic Sans MS");
		tag.setFontColors("000000,993300,333300");
		tag.setFontSizes("1/xx-small;2/x-small");
		tag.setFontFormats("p;div;pre;address");
		tag.setStylesXmlPath("/styles/xml/path");
		tag.setTemplatesXmlPath("/templates/xml/path");
		tag.setLinkBrowserURL("/link/browser/url");
		tag.setImageBrowserURL("/image/browser/url");
		tag.setFlashBrowserURL("/flash/browser/url");
		tag.setLinkUploadURL("/link/upload/url");
		tag.setImageUploadURL("/image/upload/url");
		tag.setFlashUploadURL("/flash/upload/url");
		tag.setAllowImageBrowse("true");
		tag.setAllowLinkBrowse("true");
		tag.setAllowFlashBrowse("true");
		tag.setAllowImageUpload("false");
		tag.setAllowLinkUpload("false");
		tag.setAllowFlashUpload("false");
		tag.setLinkUploadAllowedExtension("*.*");
		tag.setLinkUploadDeniedExtension("*.*");
		tag.setImageUploadAllowedExtension(".(jpg|gif|jpeg|png)$");
		tag.setImageUploadDeniedExtension(".(jpg|gif|jpeg|png)$");
		tag.setFlashUploadAllowedExtension(".(swf|fla)$");
		tag.setFlashUploadDeniedExtension(".(swf|fla)$");
		tag.setSmileyPath("/smiley/path");
		tag.setSmileyImages("['regular_smile.gif','sad_smile.gif']");
		
		tag.doStartTag();
		tag.doEndTag();
		
		verify(RichTextEditorTagTest.class.getResource("richtexteditor-2.txt"));
	}
	
	public void testWithSomeAttributeSetFromStack() throws Exception {
		stack.getContext().put("mySomeText", "This Is Some Text From The Stack");
		RichTextEditorTag tag = new RichTextEditorTag();
		tag.setPageContext(pageContext);
		tag.setLabel("myLabel");
		tag.setId("myId");
		tag.setName("myName");
		tag.setBasePath("/some/base/path/");
		tag.setWidth("100%");
		tag.setHeight("100%");
		tag.setToolbarSet("MyToolBarSet");
		tag.setCheckBrowser("true");
		tag.setDisplayError("true");
		tag.setValue("%{#mySomeText}");
        tag.setDefaultLanguage("en");

		tag.doStartTag();
		tag.doEndTag();
		
		verify(RichTextEditorTagTest.class.getResource("richtexteditor-3.txt"));
	}
	
    public void testChangeLocale() throws Exception {
        ActionContext.getContext().setLocale(Locale.FRENCH);
        
        RichTextEditorTag tag = new RichTextEditorTag();
        tag.setPageContext(pageContext);
        tag.setLabel("myLabel");
        tag.setId("myId");
        tag.setName("myName");
        
        tag.doStartTag();
        tag.doEndTag();
        
        verify(RichTextEditorTagTest.class.getResource("richtexteditor-4.txt"));
    }
}
