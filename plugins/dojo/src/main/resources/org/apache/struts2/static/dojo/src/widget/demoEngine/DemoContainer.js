/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.demoEngine.DemoContainer");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.HtmlWidget");
dojo.require("dojo.widget.demoEngine.DemoPane");
dojo.require("dojo.widget.demoEngine.SourcePane");
dojo.require("dojo.widget.TabContainer");
dojo.widget.defineWidget("my.widget.demoEngine.DemoContainer", dojo.widget.HtmlWidget, {templateString:"<div dojoAttachPoint=\"domNode\">\n\t<table width=\"100%\" cellspacing=\"0\" cellpadding=\"5\">\n\t\t<tbody>\n\t\t\t<tr dojoAttachPoint=\"headerNode\">\n\t\t\t\t<td dojoAttachPoint=\"returnNode\" valign=\"middle\" width=\"1%\">\n\t\t\t\t\t<img dojoAttachPoint=\"returnImageNode\" dojoAttachEvent=\"onclick: returnToDemos\"/>\n\t\t\t\t</td>\n\t\t\t\t<td>\n\t\t\t\t\t<h1 dojoAttachPoint=\"demoNameNode\"></h1>\n\t\t\t\t\t<p dojoAttachPoint=\"summaryNode\"></p>\n\t\t\t\t</td>\n\t\t\t\t<td dojoAttachPoint=\"tabControlNode\" valign=\"middle\" align=\"right\" nowrap>\n\t\t\t\t\t<span dojoAttachPoint=\"sourceButtonNode\" dojoAttachEvent=\"onclick: showSource\">source</span>\n\t\t\t\t\t<span dojoAttachPoint=\"demoButtonNode\" dojoAttachEvent=\"onclick: showDemo\">demo</span>\n\t\t\t\t</td>\n\t\t\t</tr>\n\t\t\t<tr>\n\t\t\t\t<td colspan=\"3\">\n\t\t\t\t\t<div dojoAttachPoint=\"tabNode\">\n\t\t\t\t\t</div>\n\t\t\t\t</td>\n\t\t\t</tr>\n\t\t</tbody>\n\t</table>\n</div>\n", templateCssString:".demoContainer{\n\twidth: 100%;\n\theight: 100%;\n\tpadding: 0px;\n\tmargin: 0px;\n}\n\n.demoContainer .return {\n\tcursor: pointer;\n}\n\n.demoContainer span {\n\tmargin-right: 10px;\n\tcursor: pointer;\n}\n\n.demoContainer .selected {\n\tborder-bottom: 5px solid #95bfff;\n}\n\n.demoContainer table {\n\tbackground: #f5f5f5;\n\twidth: 100%;\n\theight: 100%;\n}\n\n.demoContainerTabs {\n\twidth: 100%;\n\theight: 400px;\n}\n\n.demoContainerTabs .dojoTabLabels-top {\n\tdisplay: none;\n}\n\n.demoContainerTabs .dojoTabPaneWrapper {\n\tborder: 0px;\n}\n\n", templateCssPath:dojo.uri.moduleUri("dojo.widget", "demoEngine/templates/DemoContainer.css"), postCreate:function () {
	dojo.html.addClass(this.domNode, this.domNodeClass);
	dojo.html.addClass(this.tabNode, this.tabClass);
	dojo.html.addClass(this.returnImageNode, this.returnClass);
	this.returnImageNode.src = this.returnImage;
	this.tabContainer = dojo.widget.createWidget("TabContainer", {}, this.tabNode);
	this.demoTab = dojo.widget.createWidget("DemoPane", {});
	this.tabContainer.addChild(this.demoTab);
	this.sourceTab = dojo.widget.createWidget("SourcePane", {});
	this.tabContainer.addChild(this.sourceTab);
	dojo.html.setOpacity(this.domNode, 0);
	dojo.html.hide(this.domNode);
}, loadDemo:function (url) {
	this.demoTab.setHref(url);
	this.sourceTab.setHref(url);
	this.showDemo();
}, setName:function (name) {
	dojo.html.removeChildren(this.demoNameNode);
	this.demoNameNode.appendChild(document.createTextNode(name));
}, setSummary:function (summary) {
	dojo.html.removeChildren(this.summaryNode);
	this.summaryNode.appendChild(document.createTextNode(summary));
}, showSource:function () {
	dojo.html.removeClass(this.demoButtonNode, this.selectedButtonClass);
	dojo.html.addClass(this.sourceButtonNode, this.selectedButtonClass);
	this.tabContainer.selectTab(this.sourceTab);
}, showDemo:function () {
	dojo.html.removeClass(this.sourceButtonNode, this.selectedButtonClass);
	dojo.html.addClass(this.demoButtonNode, this.selectedButtonClass);
	this.tabContainer.selectTab(this.demoTab);
}, returnToDemos:function () {
	dojo.debug("Return To Demos");
}, show:function () {
	dojo.html.setOpacity(this.domNode, 1);
	dojo.html.show(this.domNode);
	this.tabContainer.checkSize();
}}, "", function () {
	dojo.debug("DemoPane Init");
	this.domNodeClass = "demoContainer";
	this.tabContainer = "";
	this.sourceTab = "";
	this.demoTab = "";
	this.headerNode = "";
	this.returnNode = "";
	this.returnImageNode = "";
	this.returnImage = "images/dojoDemos.gif";
	this.returnClass = "return";
	this.summaryNode = "";
	this.demoNameNode = "";
	this.tabControlNode = "";
	this.tabNode = "";
	this.tabClass = "demoContainerTabs";
	this.sourceButtonNode = "";
	this.demoButtonNode = "";
	this.selectedButtonClass = "selected";
});

