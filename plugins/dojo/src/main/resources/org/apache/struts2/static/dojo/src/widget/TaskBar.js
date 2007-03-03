/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.TaskBar");

dojo.require("dojo.widget.*");
dojo.require("dojo.widget.FloatingPane");
dojo.require("dojo.widget.HtmlWidget");
dojo.require("dojo.event.*");
dojo.require("dojo.html.selection");

// summary
//	Widget used internally by the TaskBar;
//	shows an icon associated w/a floating pane
dojo.widget.defineWidget(
	"dojo.widget.TaskBarItem",
	dojo.widget.HtmlWidget,
{
	// String
	//	path of icon for associated floating pane
	iconSrc: '',
	
	// String
	//	name of associated floating pane
	caption: 'Untitled',

	// String
	//	widget id of associated floating pane
	widgetId: "",

	templatePath: dojo.uri.dojoUri("src/widget/templates/TaskBarItemTemplate.html"),
	templateCssPath: dojo.uri.dojoUri("src/widget/templates/TaskBar.css"),

	fillInTemplate: function() {
		if (this.iconSrc) {
			var img = document.createElement("img");
			img.src = this.iconSrc;
			this.domNode.appendChild(img);
		}
		this.domNode.appendChild(document.createTextNode(this.caption));
		dojo.html.disableSelection(this.domNode);
	},

	postCreate: function() {
		this.window=dojo.widget.getWidgetById(this.windowId);
		this.window.explodeSrc = this.domNode;
		dojo.event.connect(this.window, "destroy", this, "destroy")
	},

	onClick: function() {
		this.window.toggleDisplay();
	}
});

// summary:
//	Displays an icon for each associated floating pane, like Windows task bar
dojo.widget.defineWidget(
	"dojo.widget.TaskBar",
	dojo.widget.FloatingPane,
	function(){
		this._addChildStack = [];
	},
{
	// TODO: this class extends floating pane merely to get the shadow;
	//	it should extend HtmlWidget and then just call the shadow code directly
	resizable: false,
	titleBarDisplay: "none",

	addChild: function(/*Widget*/ child) {
		// summary: add taskbar item for specified FloatingPane
		// TODO: this should not be called addChild(), as that has another meaning.
		if(!this.containerNode){ 
			this._addChildStack.push(child);
		}else if(this._addChildStack.length > 0){
			var oarr = this._addChildStack;
			this._addChildStack = [];
			dojo.lang.forEach(oarr, this.addChild, this);
		}
		var tbi = dojo.widget.createWidget("TaskBarItem",
			{	windowId: child.widgetId, 
				caption: child.title, 
				iconSrc: child.iconSrc
			});
		dojo.widget.TaskBar.superclass.addChild.call(this,tbi);
	}
});
