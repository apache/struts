/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.RadioGroup");

dojo.require("dojo.lang.common");
dojo.require("dojo.event.browser");
dojo.require("dojo.html.selection");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.HtmlWidget");

// summary:
// 	Widget that provides useful/common functionality that may be desirable
// 	when interacting with ul/ol html lists.
//	
// The core behaviour of the lists this widget manages is expected to be determined
// by the css class names defined: 
// 	
// 	 "radioGroup" - Applied to main ol or ul 
//	 "selected"	- Applied to the currently selected li, if any.
//   "itemContent" - Applied to the content contained in a li, this widget embeds a span 
//					within each <li></li> to contain the contents of the li.
// This widget was mostly developed under supervision/guidance from Tom Trenka.
dojo.widget.defineWidget(
	"dojo.widget.RadioGroup", 
	dojo.widget.HtmlWidget,
	function(){
		//	summary
		//	Initializes all properties for the widget.
		
		// Node: Currently selected li, if any
		this.selectedItem=null;
		// Node array: Array of li nodes being managed by widget
		this.items=[];
		// String array: List of optional ids specifying which li's should be selected by default
		this.selected=[];
		
		// String: Css class applied to main ol or ul, value is "radioGroup"
		this.groupCssClass="radioGroup";
		// String: Css class applied to the currently selected li, if any. value of "selected"
		this.selectedCssClass="selected";
		// String: Css class Applied to the content contained in a li, this widget embeds a span 
		// within each <li></li> to contain the contents of the li. value is "itemContent"
		this.itemContentCssClass="itemContent";
	},
	{
		isContainer:false,
		templatePath: null,
		templateCssPath: null,
		
		postCreate:function(){
			// summary: Parses content of widget and sets up the default state of any 
			// default selections / etc. The onSelect function will also be fired for any
			// default selections.
			this.parseStructure();
			dojo.html.addClass(this.domNode, this.groupCssClass);
			this.setupChildren();
			
			dojo.event.browser.addListener(this.domNode, "onclick", dojo.lang.hitch(this, "onSelect"));
			if (this.selectedItem){
				this.selectItem(this.selectedItem);
			}
		},
		
		parseStructure:function() {
			// summary: Sets local radioGroup and items properties, also validates
		    // that domNode contains an expected list.
		    // 
		    // Exception raised if a ul or ol node can't be found in this widgets domNode.
			if(this.domNode.tagName.toLowerCase() != "ul" 
				&& this.domNode.tagName.toLowerCase() != "ol") {
				dojo.raise("RadioGroup: Expected ul or ol content.");
				return;
			}
			
			this.items=[];	//	reset the items.
			var nl=this.domNode.getElementsByTagName("li");
			for (var i=0; i<nl.length; i++){
				if(nl[i].parentNode==this.domNode){
					this.items.push(nl[i]);
				}
			}
		},
		
		add:function(node){
			// summary: Allows the app to add a node on the fly, finishing up
		    // the setup so that we don't need to deal with it on a
		    // widget-wide basis.
			if(node.parentNode!=this.domNode){
				this.domNode.appendChild(node);
			}
			this.items.push(node);
			this.setup(node);
		},
		
		remove:function(node){
			// summary: Removes the specified node from this group, if it exists.
			var idx=-1;
			for(var i=0; i<this.items.length; i++){
				if(this.items[i]==node){
					idx=i;
					break;
				}
			}
			if(idx<0) {return;}
			this.items.splice(idx,1);
			node.parentNode.removeChild(node);
		},
		
		clear:function(){
			// summary: Removes all items in this list
			for(var i=0; i<this.items.length; i++){
				this.domNode.removeChild(this.items[i]);
			}
			this.items=[];
		},
		
		clearSelections:function(){
			// summary: Clears any selected items from being selected
			for(var i=0; i<this.items.length; i++){
				dojo.html.removeClass(this.items[i], this.selectedCssClass);
			}
			this.selectedItem=null;
		},
		
		setup:function(node){
			var span = document.createElement("span");
			dojo.html.disableSelection(span);
			dojo.html.addClass(span, this.itemContentCssClass);
			dojo.dom.moveChildren(node, span);
			node.appendChild(span);
			
			if (this.selected.length > 0) {
				var uid = dojo.html.getAttribute(node, "id");
				if (uid && uid == this.selected){
					this.selectedItem = node;
				}
			}
			dojo.event.browser.addListener(node, "onclick", dojo.lang.hitch(this, "onItemSelect"));
			if (dojo.html.hasAttribute(node, "onitemselect")) {
				var tn = dojo.lang.nameAnonFunc(new Function(dojo.html.getAttribute(node, "onitemselect")), 
												this);
				dojo.event.browser.addListener(node, "onclick", dojo.lang.hitch(this, tn));
			}
		},
		
		setupChildren:function(){
			for (var i=0; i<this.items.length; i++){
				this.setup(this.items[i]);
			}
		},
		
		selectItem:function(node, event, nofire){
			// summary: Sets the selectedItem to passed in node, applies
			// css selection class on new item
			if(this.selectedItem){
				dojo.html.removeClass(this.selectedItem, this.selectedCssClass);
			}
			
			this.selectedItem = node;
			dojo.html.addClass(this.selectedItem, this.selectedCssClass);
			
			// if this is the result of an event, stop here.
			if (!dj_undef("currentTarget", event)){
				return;
			}
			
			//	if there's no nofire flag, passed when this is nailed internally.
			if(!nofire){
				if(dojo.render.html.ie){
					this.selectedItem.fireEvent("onclick");
				}else{
					var e = document.createEvent("MouseEvents");
					e.initEvent("click", true, false);
					this.selectedItem.dispatchEvent(e);
				}
			}
		},
		
		getValue:function() {
			// summary: Gets the currently selected item, if any.
			return this.selectedItem; /*Node*/
		},
		
		onSelect:function(e) { 
			// summary: When the ul or ol contained by this widget is selected this function
			// is fired. A good function to listen to via dojo.event.connect. 
		},
		
		onItemSelect:function(e) {
			// summary: when an individual li is selected
			if (!dj_undef("currentTarget", e)){
				this.selectItem(e.currentTarget, e);
			}
		}
	}
);
