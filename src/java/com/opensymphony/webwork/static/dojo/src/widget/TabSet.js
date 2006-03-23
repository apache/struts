/*
	Copyright (c) 2004-2005, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.TabSet");
dojo.provide("dojo.widget.html.TabSet");
dojo.provide("dojo.widget.Tab");
dojo.provide("dojo.widget.html.Tab");

dojo.require("dojo.widget.*");
dojo.require("dojo.widget.LayoutPane");
dojo.require("dojo.event.*");
dojo.require("dojo.html");
dojo.require("dojo.style");

//////////////////////////////////////////
// TabSet -- a set of Tabs
//////////////////////////////////////////
dojo.widget.html.TabSet = function() {
	dojo.widget.html.LayoutPane.call(this);
}
dojo.inherits(dojo.widget.html.TabSet, dojo.widget.html.LayoutPane);

dojo.lang.extend(dojo.widget.html.TabSet, {

	// Constructor arguments
	labelPosition: "top",
	useVisibility: false,		// true-->use visibility:hidden instead of display:none

	widgetType: "TabSet",


	templateCssPath: dojo.uri.dojoUri("src/widget/templates/HtmlTabSet.css"),

	selectedTab: "",		// currently selected tab's widgetId, later widget

	fillInTemplate: function(args, frag) {
		dojo.widget.html.TabSet.superclass.fillInTemplate.call(this, args, frag);
		
		// TODO: prevent multiple includes of the same CSS file, when there are multiple
		// TabSets on the same screen.
		dojo.style.insertCssFile(this.templateCssPath);
		dojo.html.prependClass(this.domNode, "dojoTabSet");

		// Create panel to hold the tab labels (as a <ul> with special formatting)
		// TODO: set "bottom" css tag if label is on bottom
		this.filterAllowed('labelPosition', ['top', 'bottom']);
		this.labelPanel = dojo.widget.fromScript("LayoutPane", {layoutAlign: this.labelPosition});
		this.ul = document.createElement("ul");
		dojo.html.addClass(this.ul, "tabs");
		dojo.html.addClass(this.ul, this.labelPosition);
		this.labelPanel.domNode.appendChild(this.ul);
		this.addPane(this.labelPanel);
	},

	registerChild: function(child, insertionIndex){
		// registerChild will be called for each tab, and also for the
		// top pane (layoutAlign="top") that holds all the tab labels

		dojo.widget.html.TabSet.superclass.registerChild.call(this, child, insertionIndex);

		if ( child.widgetType == "Tab" ){
			this.ul.appendChild(child.li);
	
			if (this.selectedTab==child.widgetId || child.selected) {
				this.onSelected(child);
			} else {
				child.hide();
			}
		}
	},

	onSelected: function(tab) {
		// Deselect old tab and select new one
		if (this.selectedTab && this.selectedTab.widgetId) {
			this.selectedTab.hide();
		}
		this.selectedTab = tab;		// becomes widget rather than string
		tab.show();
	},
	
	onResized: function() {
		// If none of the tabs were specified as selected, catch that here
		// and just select the first one
		if ( !this.selectedTab.widgetId ) {
			this.onSelected(this.children[0]);
		}
		dojo.widget.html.TabSet.superclass.onResized.call(this);
	}
});
dojo.widget.tags.addParseTreeHandler("dojo:TabSet");

//////////////////////////////////////////////////////
// Tab - a single tab
//////////////////////////////////////////////////////
dojo.widget.html.Tab = function() {
	dojo.widget.html.LayoutPane.call(this);
}
dojo.inherits(dojo.widget.html.Tab, dojo.widget.html.LayoutPane);

dojo.lang.extend(dojo.widget.html.Tab, {
	widgetType: "Tab",
	
	label: "",
	url: "inline",
	handler: "none",
	selected: false,	// is this tab currently selected?
	
	fillInTemplate: function(args, frag) {
		this.layoutAlign = "client";
		dojo.widget.html.Tab.superclass.fillInTemplate.call(this, args, frag);
		dojo.html.prependClass(this.domNode, "dojoTabPanel");

		// Create label
		this.li = document.createElement("li");
		var span = document.createElement("span");
		span.innerHTML = this.label;
		this.li.appendChild(span);
		dojo.event.connect(this.li, "onclick", this, "onSelected");
	},
	
	onSelected: function() {
		this.parent.onSelected(this);
	},
	
	show: function() {
		dojo.html.addClass(this.li, "current");
		this.selected=true;
		if ( this.parent.useVisibility && !dojo.render.html.ie ) {
			this.domNode.style.visibility="visible";
		} else {
			dojo.widget.html.Tab.superclass.show.call(this);
		}
	},

	hide: function() {
		dojo.html.removeClass(this.li, "current");
		this.selected=false;
		if( this.parent.useVisibility ){
			this.domNode.style.visibility="hidden";
		}else{
			dojo.widget.html.Tab.superclass.hide.call(this);
		}
	}	
});
dojo.widget.tags.addParseTreeHandler("dojo:Tab");

