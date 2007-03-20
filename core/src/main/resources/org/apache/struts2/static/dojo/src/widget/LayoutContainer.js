/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.LayoutContainer");

dojo.require("dojo.widget.*");
dojo.require("dojo.widget.html.layout");

// summary
//	Provides Delphi-style panel layout semantics.
//
// details
//	A LayoutContainer is a box with a specified size (like style="width: 500px; height: 500px;"),
//	that contains children widgets marked with "layoutAlign" of "left", "right", "bottom", "top", and "client".
//	It takes it's children marked as left/top/bottom/right, and lays them out along the edges of the box,
//	and then it takes the child marked "client" and puts it into the remaining space in the middle.
//
//  Left/right positioning is similar to CSS's "float: left" and "float: right",
//	and top/bottom positioning would be similar to "float: top" and "float: bottom", if there were such
//	CSS.
//
//	Note that there can only be one client element, but there can be multiple left, right, top,
//	or bottom elements.
//
// usage
//	<style>
//		html, body{ height: 100%; width: 100%; }
//	</style>
//	<div dojoType="LayoutContainer" style="width: 100%; height: 100%">
//		<div dojoType="ContentPane" layoutAlign="top">header text</div>
//		<div dojoType="ContentPane" layoutAlign="left" style="width: 200px;">table of contents</div>
//		<div dojoType="ContentPane" layoutAlign="client">client area</div>
//	</div>
dojo.widget.defineWidget(
	"dojo.widget.LayoutContainer",
	dojo.widget.HtmlWidget,
{
	isContainer: true,

	// String
	//	- If the value is "top-bottom", then LayoutContainer will first position the "top" and "bottom" aligned elements,
	//	to and then put the left and right aligned elements in the remaining space, between the top and the bottom elements.
	//	It aligns the client element at the very end, in the remaining space.
	//
	//	- If the value is "left-right", then it first positions the "left" and "right" elements, and then puts the
	//	"top" and "bottom" elements in the remaining space, between the left and the right elements.
	//	It aligns the client element at the very end, in the remaining space.
	//
	//	- If the value is "none", then it will lay out each child in the natural order the children occur in.
	//	Basically each child is laid out into the "remaining space", where "remaining space" is initially
	//	the content area of this widget, but is reduced to a smaller rectangle each time a child is added.
	//	
	layoutChildPriority: 'top-bottom',

	postCreate: function(){
		dojo.widget.html.layout(this.domNode, this.children, this.layoutChildPriority);
	},

	addChild: function(child, overrideContainerNode, pos, ref, insertIndex){
		dojo.widget.LayoutContainer.superclass.addChild.call(this, child, overrideContainerNode, pos, ref, insertIndex);
		dojo.widget.html.layout(this.domNode, this.children, this.layoutChildPriority);
	},

	removeChild: function(pane){
		dojo.widget.LayoutContainer.superclass.removeChild.call(this,pane);
		dojo.widget.html.layout(this.domNode, this.children, this.layoutChildPriority);
	},

	onResized: function(){
		dojo.widget.html.layout(this.domNode, this.children, this.layoutChildPriority);
	},

	show: function(){
		// If this node was created while display=="none" then it
		// hasn't been laid out yet.  Do that now.
		this.domNode.style.display="";
		this.checkSize();
		this.domNode.style.display="none";
		this.domNode.style.visibility="";

		dojo.widget.LayoutContainer.superclass.show.call(this);
	}
});

// This argument can be specified for the children of a LayoutContainer.
// Since any widget can be specified as a LayoutContainer child, mix it
// into the base widget class.  (This is a hack, but it's effective.)
dojo.lang.extend(dojo.widget.Widget, {
	layoutAlign: 'none'
});
