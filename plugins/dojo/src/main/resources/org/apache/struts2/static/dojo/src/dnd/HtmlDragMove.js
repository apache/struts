/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.dnd.HtmlDragMove");
dojo.require("dojo.dnd.*");
dojo.declare("dojo.dnd.HtmlDragMoveSource", dojo.dnd.HtmlDragSource, {onDragStart:function () {
	var dragObj = new dojo.dnd.HtmlDragMoveObject(this.dragObject, this.type);
	if (this.constrainToContainer) {
		dragObj.constrainTo(this.constrainingContainer);
	}
	return dragObj;
}, onSelected:function () {
	for (var i = 0; i < this.dragObjects.length; i++) {
		dojo.dnd.dragManager.selectedSources.push(new dojo.dnd.HtmlDragMoveSource(this.dragObjects[i]));
	}
}});
dojo.declare("dojo.dnd.HtmlDragMoveObject", dojo.dnd.HtmlDragObject, {onDragStart:function (e) {
	dojo.html.clearSelection();
	this.dragClone = this.domNode;
	if (dojo.html.getComputedStyle(this.domNode, "position") != "absolute") {
		this.domNode.style.position = "relative";
	}
	var left = parseInt(dojo.html.getComputedStyle(this.domNode, "left"));
	var top = parseInt(dojo.html.getComputedStyle(this.domNode, "top"));
	this.dragStartPosition = {x:isNaN(left) ? 0 : left, y:isNaN(top) ? 0 : top};
	this.scrollOffset = dojo.html.getScroll().offset;
	this.dragOffset = {y:this.dragStartPosition.y - e.pageY, x:this.dragStartPosition.x - e.pageX};
	this.containingBlockPosition = {x:0, y:0};
	if (this.constrainToContainer) {
		this.constraints = this.getConstraints();
	}
	dojo.event.connect(this.domNode, "onclick", this, "_squelchOnClick");
}, onDragEnd:function (e) {
}, setAbsolutePosition:function (x, y) {
	if (!this.disableY) {
		this.domNode.style.top = y + "px";
	}
	if (!this.disableX) {
		this.domNode.style.left = x + "px";
	}
}, _squelchOnClick:function (e) {
	dojo.event.browser.stopEvent(e);
	dojo.event.disconnect(this.domNode, "onclick", this, "_squelchOnClick");
}});

