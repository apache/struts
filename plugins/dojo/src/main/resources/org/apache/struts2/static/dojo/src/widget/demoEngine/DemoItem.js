/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.demoEngine.DemoItem");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.HtmlWidget");
dojo.widget.defineWidget("my.widget.demoEngine.DemoItem", dojo.widget.HtmlWidget, {templateString:"<div dojoAttachPoint=\"domNode\">\n\t<div dojoAttachPoint=\"summaryBoxNode\">\n\t\t<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">\n\t\t\t<tbody>\n\t\t\t\t<tr>\n\t\t\t\t\t<td dojoAttachPoint=\"screenshotTdNode\" valign=\"top\" width=\"1%\">\n\t\t\t\t\t\t<img dojoAttachPoint=\"thumbnailImageNode\" dojoAttachEvent=\"onclick: onSelectDemo\" />\n\t\t\t\t\t</td>\n\t\t\t\t\t<td dojoAttachPoint=\"summaryContainerNode\" valign=\"top\">\n\t\t\t\t\t\t<h1 dojoAttachPoint=\"nameNode\">\n\t\t\t\t\t\t</h1>\n\t\t\t\t\t\t<div dojoAttachPoint=\"summaryNode\">\n\t\t\t\t\t\t\t<p dojoAttachPoint=\"descriptionNode\"></p>\n\t\t\t\t\t\t\t<div dojoAttachPoint=\"viewDemoLinkNode\"><img dojoAttachPoint=\"viewDemoImageNode\"/ dojoAttachEvent=\"onclick: onSelectDemo\"></div>\n\t\t\t\t\t\t</div>\n\t\t\t\t\t</td>\n\t\t\t\t</tr>\n\t\t\t</tbody>\n\t\t</table>\n\t</div>\n</div>\n", templateCssString:".demoItemSummaryBox {\n\tbackground: #efefef;\n\tborder:1px solid #dae3ee;\n}\n\n.demoItemScreenshot {\n\tpadding:0.65em;\n\twidth:175px;\n\tborder-right:1px solid #fafafa;\n\ttext-align:center;\n\tcursor: pointer;\n}\n\n.demoItemWrapper{\n\tmargin-bottom:1em;\n}\n\n.demoItemWrapper a:link, .demoItemWrapper a:visited {\n\tcolor:#a6238f;\n\ttext-decoration:none;\n}\n\n.demoItemSummaryContainer {\n\tborder-left:1px solid #ddd;\n}\n\n.demoItemSummaryContainer h1 {\n\tbackground-color:#e8e8e8;\n\tborder-bottom: 1px solid #e6e6e6;\n\tcolor:#738fb9;\n\tmargin:1px;\n\tpadding:0.5em;\n\tfont-family:\"Lucida Grande\", \"Tahoma\", serif;\n\tfont-size:1.25em;\n\tfont-weight:normal;\n}\n\n.demoItemSummaryContainer h1 .packageSummary {\n\tdisplay:block;\n\tcolor:#000;\n\tfont-size:10px;\n\tmargin-top:2px;\n}\n\n.demoItemSummaryContainer .demoItemSummary{\n\tpadding:1em;\n}\n\n.demoItemSummaryContainer .demoItemSummary p {\n\tfont-size:0.85em;\n\tpadding:0;\n\tmargin:0;\n}\n\n.demoItemView {\n\ttext-align:right;\n\tcursor: pointer;\n}\n", templateCssPath:dojo.uri.moduleUri("dojo.widget", "demoEngine/templates/DemoItem.css"), postCreate:function () {
	dojo.html.addClass(this.domNode, this.domNodeClass);
	dojo.html.addClass(this.summaryBoxNode, this.summaryBoxClass);
	dojo.html.addClass(this.screenshotTdNode, this.screenshotTdClass);
	dojo.html.addClass(this.summaryContainerNode, this.summaryContainerClass);
	dojo.html.addClass(this.summaryNode, this.summaryClass);
	dojo.html.addClass(this.viewDemoLinkNode, this.viewDemoLinkClass);
	this.nameNode.appendChild(document.createTextNode(this.name));
	this.descriptionNode.appendChild(document.createTextNode(this.description));
	this.thumbnailImageNode.src = this.thumbnail;
	this.thumbnailImageNode.name = this.name;
	this.viewDemoImageNode.src = this.viewDemoImage;
	this.viewDemoImageNode.name = this.name;
}, onSelectDemo:function () {
}}, "", function () {
	this.demo = "";
	this.domNodeClass = "demoItemWrapper";
	this.summaryBoxNode = "";
	this.summaryBoxClass = "demoItemSummaryBox";
	this.nameNode = "";
	this.thumbnailImageNode = "";
	this.viewDemoImageNode = "";
	this.screenshotTdNode = "";
	this.screenshotTdClass = "demoItemScreenshot";
	this.summaryContainerNode = "";
	this.summaryContainerClass = "demoItemSummaryContainer";
	this.summaryNode = "";
	this.summaryClass = "demoItemSummary";
	this.viewDemoLinkNode = "";
	this.viewDemoLinkClass = "demoItemView";
	this.descriptionNode = "";
	this.name = "Some Demo";
	this.description = "This is the description of this demo.";
	this.thumbnail = "images/test_thumb.gif";
	this.viewDemoImage = "images/viewDemo.png";
});

