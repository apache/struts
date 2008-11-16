/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.PageContainer");

dojo.require("dojo.lang.func");
dojo.require("dojo.widget.*");
dojo.require("dojo.event.*");
dojo.require("dojo.html.selection");

// A PageContainer is a container that has multiple children, but shows only
// one child at a time (like looking at the pages in a book one by one).
//
// Publishes topics <widgetId>-addChild, <widgetId>-removeChild, and <widgetId>-selectChild
//
// Can be base class for container, Wizard, Show, etc.
dojo.widget.defineWidget("dojo.widget.PageContainer", dojo.widget.HtmlWidget, {
	isContainer: true,

	// Boolean
	//  if true, change the size of my currently displayed child to match my size
	doLayout: true,

	templateString: "<div dojoAttachPoint='containerNode'></div>",

	// String
	//   id of the initially shown page
	selectedChild: "",

	fillInTemplate: function(args, frag) {
		// Copy style info from input node to output node
		var source = this.getFragNodeRef(frag);
		dojo.html.copyStyle(this.domNode, source);
		dojo.widget.PageContainer.superclass.fillInTemplate.apply(this, arguments);
	},

	postCreate: function(args, frag) {
		if(this.children.length){
			// Setup each page panel
			dojo.lang.forEach(this.children, this._setupChild, this);

			// Figure out which child to initially display
			var initialChild;
			if(this.selectedChild){
				this.selectChild(this.selectedChild);
			}else{
				for(var i=0; i<this.children.length; i++){
					if(this.children[i].selected){
						this.selectChild(this.children[i]);
						break;
					}
				}
				if(!this.selectedChildWidget){
					this.selectChild(this.children[0]);
				}
			}
		}
	},

	addChild: function(child){
		dojo.widget.PageContainer.superclass.addChild.apply(this, arguments);
		this._setupChild(child);

		// in case the page labels have overflowed from one line to two lines
		this.onResized();

		// if this is the first child, then select it
		if(!this.selectedChildWidget){
			this.selectChild(child);
		}
	},

	_setupChild: function(page){
		// Summary: Add the given child to this page container

		page.hide();

		// publish the addChild event for panes added via addChild(), and the original panes too
		dojo.event.topic.publish(this.widgetId+"-addChild", page);
	},

	removeChild: function(/* Widget */page){
		dojo.widget.PageContainer.superclass.removeChild.apply(this, arguments);

		// If we are being destroyed than don't run the code below (to select another page), because we are deleting
		// every page one by one
		if(this._beingDestroyed){ return; }

		// this will notify any tablists to remove a button; do this first because it may affect sizing
		dojo.event.topic.publish(this.widgetId+"-removeChild", page);

		if (this.selectedChildWidget === page) {
			this.selectedChildWidget = undefined;
			if (this.children.length > 0) {
				this.selectChild(this.children[0], true);
			}
		}
	},

	selectChild: function(/* Widget */ page, /* Widget */ callingWidget){
		// summary
		//	Show the given widget (which must be one of my children)
		page = dojo.widget.byId(page);
		this.correspondingPageButton = callingWidget;

		// Deselect old page and select new one
		if(this.selectedChildWidget){
			this._hideChild(this.selectedChildWidget);
		}
		this.selectedChildWidget = page;
		this._showChild(page);
		page.isFirstChild = (page == this.children[0]);
		page.isLastChild = (page == this.children[this.children.length-1]);
		dojo.event.topic.publish(this.widgetId+"-selectChild", page);
	},

	forward: function(){
		// Summary: advance to next page
		var index = dojo.lang.find(this.children, this.selectedChildWidget);
		this.selectChild(this.children[index+1]);
	},

	back: function(){
		// Summary: go back to previous page
		var index = dojo.lang.find(this.children, this.selectedChildWidget);
		this.selectChild(this.children[index-1]);
	},

	onResized: function(){
		// Summary: called when any page is shown, to make it fit the container correctly
		if(this.doLayout && this.selectedChildWidget){
			with(this.selectedChildWidget.domNode.style){
				top = dojo.html.getPixelValue(this.containerNode, "padding-top", true);
				left = dojo.html.getPixelValue(this.containerNode, "padding-left", true);
			}
			var content = dojo.html.getContentBox(this.containerNode);
			this.selectedChildWidget.resizeTo(content.width, content.height);
		}
	},

	_showChild: function(page) {
		// size the current page (in case this is the first time it's being shown, or I have been resized)
		if(this.doLayout){
			var content = dojo.html.getContentBox(this.containerNode);
			page.resizeTo(content.width, content.height);
		}

		page.selected=true;
		page.show();
	},

	_hideChild: function(page) {
		page.selected=false;
		page.hide();
	},

	closeChild: function(page) {
		// summary
		//	callback when user clicks the [X] to remove a page
		//	if onClose() returns true then remove and destroy the childd
		var remove = page.onClose(this, page);
		if(remove) {
			this.removeChild(page);
			// makes sure we can clean up executeScripts in ContentPane onUnLoad
			page.destroy();
		}
	},

	destroy: function(){
		this._beingDestroyed = true;
		dojo.event.topic.destroy(this.widgetId+"-addChild");
		dojo.event.topic.destroy(this.widgetId+"-removeChild");
		dojo.event.topic.destroy(this.widgetId+"-selectChild");
		dojo.widget.PageContainer.superclass.destroy.apply(this, arguments);
	}
});


// PageController - set of buttons to select the page in a page list
// When intialized, the PageController monitors the container, and whenever a page is
// added or deleted updates itself accordingly.
dojo.widget.defineWidget(
    "dojo.widget.PageController",
    dojo.widget.HtmlWidget,
	{
		templateString: "<span wairole='tablist' dojoAttachEvent='onKey'></span>",
		isContainer: true,

		// String
		//	the id of the page container that I point to
		containerId: "",

		// String
		//	the name of the button widget to create to correspond to each page
		buttonWidget: "PageButton",

		// String
		//	Class name to apply to the top dom node
		"class": "dojoPageController",

		fillInTemplate: function() {
			dojo.html.addClass(this.domNode, this["class"]);  // "class" is a reserved word in JS
			dojo.widget.wai.setAttr(this.domNode, "waiRole", "role", "tablist");
		},

		postCreate: function(){
			this.pane2button = {};		// mapping from panes to buttons

			// If children have already been added to the page container then create buttons for them
			var container = dojo.widget.byId(this.containerId);
			if(container){
				dojo.lang.forEach(container.children, this.onAddChild, this);
			}

			dojo.event.topic.subscribe(this.containerId+"-addChild", this, "onAddChild");
			dojo.event.topic.subscribe(this.containerId+"-removeChild", this, "onRemoveChild");
			dojo.event.topic.subscribe(this.containerId+"-selectChild", this, "onSelectChild");
		},

		destroy: function(){
			dojo.event.topic.unsubscribe(this.containerId+"-addChild", this, "onAddChild");
			dojo.event.topic.unsubscribe(this.containerId+"-removeChild", this, "onRemoveChild");
			dojo.event.topic.unsubscribe(this.containerId+"-selectChild", this, "onSelectChild");
			dojo.widget.PageController.superclass.destroy.apply(this, arguments);
		},

		onAddChild: function(/* Widget */ page){
			// summary
			//   Called whenever a page is added to the container.
			//   Create button corresponding to the page.
			var button = dojo.widget.createWidget(this.buttonWidget,
				{
					label: page.label,
					closeButton: page.closable
				});
			this.addChild(button);
			this.domNode.appendChild(button.domNode);
			this.pane2button[page]=button;
			page.controlButton = button;	// this value might be overwritten if two tabs point to same container

			var _this = this;
			dojo.event.connect(button, "onClick", function(){ _this.onButtonClick(page); });
			dojo.event.connect(button, "onCloseButtonClick", function(){ _this.onCloseButtonClick(page); });
		},

		onRemoveChild: function(/* Widget */ page){
			// summary
			//   Called whenever a page is removed from the container.
			//   Remove the button corresponding to the page.
			if(this._currentChild == page){ this._currentChild = null; }
			var button = this.pane2button[page];
			if(button){
				button.destroy();
			}
			this.pane2button[page] = null;
		},

		onSelectChild: function(/*Widget*/ page){
			// Summary
			//	Called when a page has been selected in the PageContainer, either by me or by another PageController
			if(this._currentChild){
				var oldButton=this.pane2button[this._currentChild];
				oldButton.clearSelected();
			}
			var newButton=this.pane2button[page];
			newButton.setSelected();
			this._currentChild=page;
		},

		onButtonClick: function(/*Widget*/ page){
			// summary
			//   Called whenever one of my child buttons is pressed in an attempt to select a page
			var container = dojo.widget.byId(this.containerId);	// TODO: do this via topics?
			container.selectChild(page, false, this);
		},

		onCloseButtonClick: function(/*Widget*/ page){
			// summary
			//   Called whenever one of my child buttons [X] is pressed in an attempt to close a page
			var container = dojo.widget.byId(this.containerId);
			container.closeChild(page);
		},

		onKey: function(evt){
			// summary:
			//   Handle keystrokes on the page list, for advancing to next/previous button

			if( (evt.keyCode == evt.KEY_RIGHT_ARROW)||
				(evt.keyCode == evt.KEY_LEFT_ARROW) ){
				var current = 0;
				var next = null;	// the next button to focus on
				
				// find currently focused button in children array
				var current = dojo.lang.find(this.children, this.pane2button[this._currentChild]);
				
				// pick next button to focus on
				if(evt.keyCode == evt.KEY_RIGHT_ARROW){
					next = this.children[ (current+1) % this.children.length ]; 
				}else{ // is LEFT_ARROW
					next = this.children[ (current+ (this.children.length-1)) % this.children.length ];
				}
				
				dojo.event.browser.stopEvent(evt);
				next.onClick();
			}
		}
	}
);

// PageButton (the thing you click to select or delete a page)
dojo.widget.defineWidget("dojo.widget.PageButton", dojo.widget.HtmlWidget,
{
	templateString: "<span class='item'>" +
						"<span dojoAttachEvent='onClick' dojoAttachPoint='titleNode' class='selectButton'>${this.label}</span>" +
						"<span dojoAttachEvent='onClick:onCloseButtonClick' class='closeButton'>[X]</span>" +
					"</span>",

	// String
	//  Name to print on the button
	label: "foo",
	
	// Boolean
	//	true iff we should also print a close icon to destroy corresponding page
	closeButton: false,

	onClick: function(){
		// summary
		//  Basically this is the attach point PageController listens to, to select the page
		this.focus();
	},

	onCloseButtonMouseOver: function(){
		// summary
		//	The close button changes color a bit when you mouse over	
		dojo.html.addClass(this.closeButtonNode, "closeHover");
	},

	onCloseButtonMouseOut: function(){
		// summary
		// 	Revert close button to normal color on mouse out
		dojo.html.removeClass(this.closeButtonNode, "closeHover");
	},

	onCloseButtonClick: function(evt){
		// summary
		//	Handle clicking the close button for this tab
	},
	
	setSelected: function(){
		// summary
		//	This is run whenever the page corresponding to this button has been selected
		dojo.html.addClass(this.domNode, "current");
		this.titleNode.setAttribute("tabIndex","0");
	},
	
	clearSelected: function(){
		// summary
		//	This function is run whenever the page corresponding to this button has been deselected (and another page has been shown)
		dojo.html.removeClass(this.domNode, "current");
		this.titleNode.setAttribute("tabIndex","-1");
	},

	focus: function(){
		// summary
		//	This will focus on the this button (for accessibility you need to do this when the button is selected)
		if(this.titleNode.focus){	// mozilla 1.7 doesn't have focus() func
			this.titleNode.focus();
		}
	}
});

// These arguments can be specified for the children of a PageContainer.
// Since any widget can be specified as a PageContainer child, mix them
// into the base widget class.  (This is a hack, but it's effective.)
dojo.lang.extend(dojo.widget.Widget, {
	label: "",
	selected: false,	// is this tab currently selected?
	closable: false,	// true if user can close this tab pane
	onClose: function(){ return true; }	// callback if someone tries to close the child, child will be closed if func returns true
});
