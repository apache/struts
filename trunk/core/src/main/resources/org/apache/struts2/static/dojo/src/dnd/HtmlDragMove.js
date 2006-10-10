/*
	Copyright (c) 2004-2005, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.dnd.HtmlDragMove");
dojo.provide("dojo.dnd.HtmlDragMoveSource");
dojo.provide("dojo.dnd.HtmlDragMoveObject");
dojo.require("dojo.dnd.*");

dojo.dnd.HtmlDragMoveSource = function(node, type){
	dojo.dnd.HtmlDragSource.call(this, node, type);
}

dojo.inherits(dojo.dnd.HtmlDragMoveSource, dojo.dnd.HtmlDragSource);

dojo.lang.extend(dojo.dnd.HtmlDragMoveSource, {
	onDragStart: function(){
		var dragObj =  new dojo.dnd.HtmlDragMoveObject(this.dragObject, this.type);

		if (this.constrainToContainer) {
			dragObj.constrainTo(this.constrainingContainer);
		}
		return dragObj;
	}
});

dojo.dnd.HtmlDragMoveObject = function(node, type){
	dojo.dnd.HtmlDragObject.call(this, node, type);
}

dojo.inherits(dojo.dnd.HtmlDragMoveObject, dojo.dnd.HtmlDragObject);

dojo.lang.extend(dojo.dnd.HtmlDragMoveObject, {
	onDragEnd: function(e){
		delete this.dragClone;
	},
	
	onDragStart: function(e){
		dojo.html.clearSelection();
		
		this.dragClone = this.domNode;

		this.scrollOffset = {
			top: dojo.html.getScrollTop(), // document.documentElement.scrollTop,
			left: dojo.html.getScrollLeft() // document.documentElement.scrollLeft
		};

		this.dragStartPosition = {top: dojo.style.getAbsoluteY(this.domNode) ,
			left: dojo.style.getAbsoluteX(this.domNode) };
		
		this.dragOffset = {top: this.dragStartPosition.top - e.clientY,
			left: this.dragStartPosition.left - e.clientX};

		if (this.domNode.parentNode.nodeName.toLowerCase() == 'body') {
			this.parentPosition = {top: 0, left: 0};
		} else {
			this.parentPosition = {top: dojo.style.getAbsoluteY(this.domNode.parentNode, true),
				left: dojo.style.getAbsoluteX(this.domNode.parentNode,true)};
		}

		this.dragClone.style.position = "absolute";

		if (this.constrainToContainer) {
			this.constraints = this.getConstraints();
		}
	}

});
