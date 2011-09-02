/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.demoEngine.DemoPane");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.HtmlWidget");
dojo.widget.defineWidget("my.widget.demoEngine.DemoPane", dojo.widget.HtmlWidget, {templateString:"<div dojoAttachPoint=\"domNode\">\n\t<iframe dojoAttachPoint=\"demoNode\"></iframe>\n</div>\n", templateCssString:".demoPane {\n\twidth: 100%;\n\theight: 100%;\n\tpadding: 0px;\n\tmargin: 0px;\n\toverflow: hidden;\n}\n\n.demoPane iframe {\n\twidth: 100%;\n\theight: 100%;\n\tborder: 0px;\n\tborder: none;\n\toverflow: auto;\n\tpadding: 0px;\n\tmargin:0px;\n\tbackground: #ffffff;\n}\n", templateCssPath:dojo.uri.moduleUri("dojo.widget", "demoEngine/templates/DemoPane.css"), postCreate:function () {
	dojo.html.addClass(this.domNode, this.domNodeClass);
	dojo.debug("PostCreate");
	this._launchDemo();
}, _launchDemo:function () {
	dojo.debug("Launching Demo");
	dojo.debug(this.demoNode);
	this.demoNode.src = this.href;
}, setHref:function (url) {
	this.href = url;
	this._launchDemo();
}}, "", function () {
	dojo.debug("DemoPane Init");
	this.domNodeClass = "demoPane";
	this.demoNode = "";
	this.href = "";
});

