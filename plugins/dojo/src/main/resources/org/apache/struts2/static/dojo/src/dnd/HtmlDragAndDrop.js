/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.dnd.HtmlDragAndDrop");
dojo.require("dojo.dnd.HtmlDragManager");
dojo.require("dojo.dnd.DragAndDrop");
dojo.require("dojo.html.*");
dojo.require("dojo.html.display");
dojo.require("dojo.html.util");
dojo.require("dojo.html.selection");
dojo.require("dojo.html.iframe");
dojo.require("dojo.lang.extras");
dojo.require("dojo.lfx.*");
dojo.require("dojo.event.*");
dojo.declare("dojo.dnd.HtmlDragSource", dojo.dnd.DragSource, {dragClass:"", onDragStart:function () {
	var dragObj = new dojo.dnd.HtmlDragObject(this.dragObject, this.type);
	if (this.dragClass) {
		dragObj.dragClass = this.dragClass;
	}
	if (this.constrainToContainer) {
		dragObj.constrainTo(this.constrainingContainer || this.domNode.parentNode);
	}
	return dragObj;
}, setDragHandle:function (node) {
	node = dojo.byId(node);
	dojo.dnd.dragManager.unregisterDragSource(this);
	this.domNode = node;
	dojo.dnd.dragManager.registerDragSource(this);
}, setDragTarget:function (node) {
	this.dragObject = node;
}, constrainTo:function (container) {
	this.constrainToContainer = true;
	if (container) {
		this.constrainingContainer = container;
	}
}, onSelected:function () {
	for (var i = 0; i < this.dragObjects.length; i++) {
		dojo.dnd.dragManager.selectedSources.push(new dojo.dnd.HtmlDragSource(this.dragObjects[i]));
	}
}, addDragObjects:function (el) {
	for (var i = 0; i < arguments.length; i++) {
		this.dragObjects.push(dojo.byId(arguments[i]));
	}
}}, function (node, type) {
	node = dojo.byId(node);
	this.dragObjects = [];
	this.constrainToContainer = false;
	if (node) {
		this.domNode = node;
		this.dragObject = node;
		this.type = (type) || (this.domNode.nodeName.toLowerCase());
		dojo.dnd.DragSource.prototype.reregister.call(this);
	}
});
dojo.declare("dojo.dnd.HtmlDragObject", dojo.dnd.DragObject, {dragClass:"", opacity:0.5, createIframe:true, disableX:false, disableY:false, createDragNode:function () {
	var node = this.domNode.cloneNode(true);
	if (this.dragClass) {
		dojo.html.addClass(node, this.dragClass);
	}
	if (this.opacity < 1) {
		dojo.html.setOpacity(node, this.opacity);
	}
	var ltn = node.tagName.toLowerCase();
	var isTr = (ltn == "tr");
	if ((isTr) || (ltn == "tbody")) {
		var doc = this.domNode.ownerDocument;
		var table = doc.createElement("table");
		if (isTr) {
			var tbody = doc.createElement("tbody");
			table.appendChild(tbody);
			tbody.appendChild(node);
		} else {
			table.appendChild(node);
		}
		var tmpSrcTr = ((isTr) ? this.domNode : this.domNode.firstChild);
		var tmpDstTr = ((isTr) ? node : node.firstChild);
		var domTds = tmpSrcTr.childNodes;
		var cloneTds = tmpDstTr.childNodes;
		for (var i = 0; i < domTds.length; i++) {
			if ((cloneTds[i]) && (cloneTds[i].style)) {
				cloneTds[i].style.width = dojo.html.getContentBox(domTds[i]).width + "px";
			}
		}
		node = table;
	}
	if ((dojo.render.html.ie55 || dojo.render.html.ie60) && this.createIframe) {
		with (node.style) {
			top = "0px";
			left = "0px";
		}
		var outer = document.createElement("div");
		outer.appendChild(node);
		this.bgIframe = new dojo.html.BackgroundIframe(outer);
		outer.appendChild(this.bgIframe.iframe);
		node = outer;
	}
	node.style.zIndex = 999;
	return node;
}, onDragStart:function (e) {
	dojo.html.clearSelection();
	this.scrollOffset = dojo.html.getScroll().offset;
	this.dragStartPosition = dojo.html.getAbsolutePosition(this.domNode, true);
	this.dragOffset = {y:this.dragStartPosition.y - e.pageY, x:this.dragStartPosition.x - e.pageX};
	this.dragClone = this.createDragNode();
	this.containingBlockPosition = this.domNode.offsetParent ? dojo.html.getAbsolutePosition(this.domNode.offsetParent, true) : {x:0, y:0};
	if (this.constrainToContainer) {
		this.constraints = this.getConstraints();
	}
	with (this.dragClone.style) {
		position = "absolute";
		top = this.dragOffset.y + e.pageY + "px";
		left = this.dragOffset.x + e.pageX + "px";
	}
	dojo.body().appendChild(this.dragClone);
	dojo.event.topic.publish("dragStart", {source:this});
}, getConstraints:function () {
	if (this.constrainingContainer.nodeName.toLowerCase() == "body") {
		var viewport = dojo.html.getViewport();
		var width = viewport.width;
		var height = viewport.height;
		var scroll = dojo.html.getScroll().offset;
		var x = scroll.x;
		var y = scroll.y;
	} else {
		var content = dojo.html.getContentBox(this.constrainingContainer);
		width = content.width;
		height = content.height;
		x = this.containingBlockPosition.x + dojo.html.getPixelValue(this.constrainingContainer, "padding-left", true) + dojo.html.getBorderExtent(this.constrainingContainer, "left");
		y = this.containingBlockPosition.y + dojo.html.getPixelValue(this.constrainingContainer, "padding-top", true) + dojo.html.getBorderExtent(this.constrainingContainer, "top");
	}
	var mb = dojo.html.getMarginBox(this.domNode);
	return {minX:x, minY:y, maxX:x + width - mb.width, maxY:y + height - mb.height};
}, updateDragOffset:function () {
	var scroll = dojo.html.getScroll().offset;
	if (scroll.y != this.scrollOffset.y) {
		var diff = scroll.y - this.scrollOffset.y;
		this.dragOffset.y += diff;
		this.scrollOffset.y = scroll.y;
	}
	if (scroll.x != this.scrollOffset.x) {
		var diff = scroll.x - this.scrollOffset.x;
		this.dragOffset.x += diff;
		this.scrollOffset.x = scroll.x;
	}
}, onDragMove:function (e) {
	this.updateDragOffset();
	var x = this.dragOffset.x + e.pageX;
	var y = this.dragOffset.y + e.pageY;
	if (this.constrainToContainer) {
		if (x < this.constraints.minX) {
			x = this.constraints.minX;
		}
		if (y < this.constraints.minY) {
			y = this.constraints.minY;
		}
		if (x > this.constraints.maxX) {
			x = this.constraints.maxX;
		}
		if (y > this.constraints.maxY) {
			y = this.constraints.maxY;
		}
	}
	this.setAbsolutePosition(x, y);
	dojo.event.topic.publish("dragMove", {source:this});
}, setAbsolutePosition:function (x, y) {
	if (!this.disableY) {
		this.dragClone.style.top = y + "px";
	}
	if (!this.disableX) {
		this.dragClone.style.left = x + "px";
	}
}, onDragEnd:function (e) {
	switch (e.dragStatus) {
	  case "dropSuccess":
		dojo.html.removeNode(this.dragClone);
		this.dragClone = null;
		break;
	  case "dropFailure":
		var startCoords = dojo.html.getAbsolutePosition(this.dragClone, true);
		var endCoords = {left:this.dragStartPosition.x + 1, top:this.dragStartPosition.y + 1};
		var anim = dojo.lfx.slideTo(this.dragClone, endCoords, 300);
		var dragObject = this;
		dojo.event.connect(anim, "onEnd", function (e) {
			dojo.html.removeNode(dragObject.dragClone);
			dragObject.dragClone = null;
		});
		anim.play();
		break;
	}
	dojo.event.topic.publish("dragEnd", {source:this});
}, constrainTo:function (container) {
	this.constrainToContainer = true;
	if (container) {
		this.constrainingContainer = container;
	} else {
		this.constrainingContainer = this.domNode.parentNode;
	}
}}, function (node, type) {
	this.domNode = dojo.byId(node);
	this.type = type;
	this.constrainToContainer = false;
	this.dragSource = null;
	dojo.dnd.DragObject.prototype.register.call(this);
});
dojo.declare("dojo.dnd.HtmlDropTarget", dojo.dnd.DropTarget, {vertical:false, onDragOver:function (e) {
	if (!this.accepts(e.dragObjects)) {
		return false;
	}
	this.childBoxes = [];
	for (var i = 0, child; i < this.domNode.childNodes.length; i++) {
		child = this.domNode.childNodes[i];
		if (child.nodeType != dojo.html.ELEMENT_NODE) {
			continue;
		}
		var pos = dojo.html.getAbsolutePosition(child, true);
		var inner = dojo.html.getBorderBox(child);
		this.childBoxes.push({top:pos.y, bottom:pos.y + inner.height, left:pos.x, right:pos.x + inner.width, height:inner.height, width:inner.width, node:child});
	}
	return true;
}, _getNodeUnderMouse:function (e) {
	for (var i = 0, child; i < this.childBoxes.length; i++) {
		with (this.childBoxes[i]) {
			if (e.pageX >= left && e.pageX <= right && e.pageY >= top && e.pageY <= bottom) {
				return i;
			}
		}
	}
	return -1;
}, createDropIndicator:function () {
	this.dropIndicator = document.createElement("div");
	with (this.dropIndicator.style) {
		position = "absolute";
		zIndex = 999;
		if (this.vertical) {
			borderLeftWidth = "1px";
			borderLeftColor = "black";
			borderLeftStyle = "solid";
			height = dojo.html.getBorderBox(this.domNode).height + "px";
			top = dojo.html.getAbsolutePosition(this.domNode, true).y + "px";
		} else {
			borderTopWidth = "1px";
			borderTopColor = "black";
			borderTopStyle = "solid";
			width = dojo.html.getBorderBox(this.domNode).width + "px";
			left = dojo.html.getAbsolutePosition(this.domNode, true).x + "px";
		}
	}
}, onDragMove:function (e, dragObjects) {
	var i = this._getNodeUnderMouse(e);
	if (!this.dropIndicator) {
		this.createDropIndicator();
	}
	var gravity = this.vertical ? dojo.html.gravity.WEST : dojo.html.gravity.NORTH;
	var hide = false;
	if (i < 0) {
		if (this.childBoxes.length) {
			var before = (dojo.html.gravity(this.childBoxes[0].node, e) & gravity);
			if (before) {
				hide = true;
			}
		} else {
			var before = true;
		}
	} else {
		var child = this.childBoxes[i];
		var before = (dojo.html.gravity(child.node, e) & gravity);
		if (child.node === dragObjects[0].dragSource.domNode) {
			hide = true;
		} else {
			var currentPosChild = before ? (i > 0 ? this.childBoxes[i - 1] : child) : (i < this.childBoxes.length - 1 ? this.childBoxes[i + 1] : child);
			if (currentPosChild.node === dragObjects[0].dragSource.domNode) {
				hide = true;
			}
		}
	}
	if (hide) {
		this.dropIndicator.style.display = "none";
		return;
	} else {
		this.dropIndicator.style.display = "";
	}
	this.placeIndicator(e, dragObjects, i, before);
	if (!dojo.html.hasParent(this.dropIndicator)) {
		dojo.body().appendChild(this.dropIndicator);
	}
}, placeIndicator:function (e, dragObjects, boxIndex, before) {
	var targetProperty = this.vertical ? "left" : "top";
	var child;
	if (boxIndex < 0) {
		if (this.childBoxes.length) {
			child = before ? this.childBoxes[0] : this.childBoxes[this.childBoxes.length - 1];
		} else {
			this.dropIndicator.style[targetProperty] = dojo.html.getAbsolutePosition(this.domNode, true)[this.vertical ? "x" : "y"] + "px";
		}
	} else {
		child = this.childBoxes[boxIndex];
	}
	if (child) {
		this.dropIndicator.style[targetProperty] = (before ? child[targetProperty] : child[this.vertical ? "right" : "bottom"]) + "px";
		if (this.vertical) {
			this.dropIndicator.style.height = child.height + "px";
			this.dropIndicator.style.top = child.top + "px";
		} else {
			this.dropIndicator.style.width = child.width + "px";
			this.dropIndicator.style.left = child.left + "px";
		}
	}
}, onDragOut:function (e) {
	if (this.dropIndicator) {
		dojo.html.removeNode(this.dropIndicator);
		delete this.dropIndicator;
	}
}, onDrop:function (e) {
	this.onDragOut(e);
	var i = this._getNodeUnderMouse(e);
	var gravity = this.vertical ? dojo.html.gravity.WEST : dojo.html.gravity.NORTH;
	if (i < 0) {
		if (this.childBoxes.length) {
			if (dojo.html.gravity(this.childBoxes[0].node, e) & gravity) {
				return this.insert(e, this.childBoxes[0].node, "before");
			} else {
				return this.insert(e, this.childBoxes[this.childBoxes.length - 1].node, "after");
			}
		}
		return this.insert(e, this.domNode, "append");
	}
	var child = this.childBoxes[i];
	if (dojo.html.gravity(child.node, e) & gravity) {
		return this.insert(e, child.node, "before");
	} else {
		return this.insert(e, child.node, "after");
	}
}, insert:function (e, refNode, position) {
	var node = e.dragObject.domNode;
	if (position == "before") {
		return dojo.html.insertBefore(node, refNode);
	} else {
		if (position == "after") {
			return dojo.html.insertAfter(node, refNode);
		} else {
			if (position == "append") {
				refNode.appendChild(node);
				return true;
			}
		}
	}
	return false;
}}, function (node, types) {
	if (arguments.length == 0) {
		return;
	}
	this.domNode = dojo.byId(node);
	dojo.dnd.DropTarget.call(this);
	if (types && dojo.lang.isString(types)) {
		types = [types];
	}
	this.acceptedTypes = types || [];
	dojo.dnd.dragManager.registerDropTarget(this);
});

