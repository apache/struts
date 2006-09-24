/*
	Copyright (c) 2004-2005, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.dnd.HtmlDragAndDrop");
dojo.provide("dojo.dnd.HtmlDragSource");
dojo.provide("dojo.dnd.HtmlDropTarget");
dojo.provide("dojo.dnd.HtmlDragObject");

dojo.require("dojo.dnd.HtmlDragManager");
dojo.require("dojo.animation.*");
dojo.require("dojo.dom");
dojo.require("dojo.style");
dojo.require("dojo.html");
dojo.require("dojo.lang");

dojo.dnd.HtmlDragSource = function(node, type){
	node = dojo.byId(node);
	this.constrainToContainer = false;
	if(node){
		this.domNode = node;
		this.dragObject = node;

		// register us
		dojo.dnd.DragSource.call(this);

		// set properties that might have been clobbered by the mixin
		this.type = type||this.domNode.nodeName.toLowerCase();
	}

}

dojo.lang.extend(dojo.dnd.HtmlDragSource, {
	dragClass: "", // CSS classname(s) applied to node when it is being dragged

	onDragStart: function(){
		var dragObj = new dojo.dnd.HtmlDragObject(this.dragObject, this.type, this.dragClass);

		if (this.constrainToContainer) {
			dragObj.constrainTo(this.constrainingContainer);
		}

		return dragObj;
	},
	setDragHandle: function(node){
		node = dojo.byId(node);
		dojo.dnd.dragManager.unregisterDragSource(this);
		this.domNode = node;
		dojo.dnd.dragManager.registerDragSource(this);
	},
	setDragTarget: function(node){
		this.dragObject = node;
	},

	constrainTo: function(container) {
		this.constrainToContainer = true;

		if (container) {
			this.constrainingContainer = container;
		} else {
			this.constrainingContainer = this.domNode.parentNode;
		}
	}
});

dojo.dnd.HtmlDragObject = function(node, type, dragClass){
	this.domNode = dojo.byId(node);
	this.type = type;
	if(dragClass) { this.dragClass = dragClass; }
	this.constrainToContainer = false;
}

dojo.lang.extend(dojo.dnd.HtmlDragObject, {  
	dragClass: "",
	opacity: 0.5,

	// if true, node will not move in X and/or Y direction
	disableX: false,
	disableY: false,

	/**
	 * Creates a clone of this node and replaces this node with the clone in the
	 * DOM tree. This is done to prevent the browser from selecting the textual
	 * content of the node. This node is then set to opaque and drags around as
	 * the intermediate representation.
	 */
	onDragStart: function(e){
		dojo.html.clearSelection();
		
		this.scrollOffset = {
			top: dojo.html.getScrollTop(), // document.documentElement.scrollTop,
			left: dojo.html.getScrollLeft() // document.documentElement.scrollLeft
		};
	
		this.dragStartPosition = {top: dojo.style.getAbsoluteY(this.domNode, true) + this.scrollOffset.top,
			left: dojo.style.getAbsoluteX(this.domNode, true) + this.scrollOffset.left};
		

		this.dragOffset = {top: this.dragStartPosition.top - e.clientY,
			left: this.dragStartPosition.left - e.clientX};

		this.dragClone = this.domNode.cloneNode(true);
		//this.domNode.parentNode.replaceChild(this.dragClone, this.domNode);


 		if ((this.domNode.parentNode.nodeName.toLowerCase() == 'body') || (dojo.style.getComputedStyle(this.domNode.parentNode,"position") == "static")) {
			this.parentPosition = {top: 0, left: 0};
		} else {
			this.parentPosition = {top: dojo.style.getAbsoluteY(this.domNode.parentNode, true),
				left: dojo.style.getAbsoluteX(this.domNode.parentNode,true)};
		}
	
		if (this.constrainToContainer) {
			this.constraints = this.getConstraints();
		}

		// set up for dragging
		with(this.dragClone.style){
			position = "absolute";
			top = this.dragOffset.top + e.clientY + "px";
			left = this.dragOffset.left + e.clientX + "px";
		}

		if(this.dragClass) { dojo.html.addClass(this.dragClone, this.dragClass); }
		dojo.style.setOpacity(this.dragClone, this.opacity);
		dojo.html.body().appendChild(this.dragClone);
	},

	getConstraints: function() {

		if (this.constrainingContainer.nodeName.toLowerCase() == 'body') {
			width = dojo.html.getViewportWidth();
			height = dojo.html.getViewportHeight();
			padLeft = 0;
			padTop = 0;
		} else {
			width = dojo.style.getContentWidth(this.constrainingContainer);
			height = dojo.style.getContentHeight(this.constrainingContainer);	
			padLeft = dojo.style.getPixelValue(this.constrainingContainer, "padding-left", true);
			padTop = dojo.style.getPixelValue(this.constrainingContainer, "padding-top", true);
		}

		return {
			minX: padLeft,
			minY: padTop,
			maxX: padLeft+width - dojo.style.getOuterWidth(this.domNode),
			maxY: padTop+height - dojo.style.getOuterHeight(this.domNode) 
		}
	},

	updateDragOffset: function() {
		var sTop = dojo.html.getScrollTop(); // document.documentElement.scrollTop;
		var sLeft = dojo.html.getScrollLeft(); // document.documentElement.scrollLeft;
		if(sTop != this.scrollOffset.top) {
			var diff = sTop - this.scrollOffset.top;
			this.dragOffset.top += diff;
			this.scrollOffset.top = sTop;
		}
	},
	
	/** Moves the node to follow the mouse */
	onDragMove: function(e){
		this.updateDragOffset();
		var x = this.dragOffset.left + e.clientX - this.parentPosition.left;
		var y = this.dragOffset.top + e.clientY - this.parentPosition.top;

		if (this.constrainToContainer) {
			if (x < this.constraints.minX) { x = this.constraints.minX; }
			if (y < this.constraints.minY) { y = this.constraints.minY; }
			if (x > this.constraints.maxX) { x = this.constraints.maxX; }
			if (y > this.constraints.maxY) { y = this.constraints.maxY; }
		}

		if(!this.disableY) { this.dragClone.style.top = y + "px"; }
		if(!this.disableX) { this.dragClone.style.left = x + "px"; }
	},

	/**
	 * If the drag operation returned a success we reomve the clone of
	 * ourself from the original position. If the drag operation returned
	 * failure we slide back over to where we came from and end the operation
	 * with a little grace.
	 */
	onDragEnd: function(e){
		switch(e.dragStatus){

			case "dropSuccess":
				dojo.dom.removeNode(this.dragClone);
				this.dragClone = null;
				break;
		
			case "dropFailure": // slide back to the start
				var startCoords = [dojo.style.getAbsoluteX(this.dragClone), 
							dojo.style.getAbsoluteY(this.dragClone)];
				// offset the end so the effect can be seen
				var endCoords = [this.dragStartPosition.left + 1,
					this.dragStartPosition.top + 1];
	
				// animate
				var line = new dojo.math.curves.Line(startCoords, endCoords);
				var anim = new dojo.animation.Animation(line, 300, 0, 0);
				var dragObject = this;
				dojo.event.connect(anim, "onAnimate", function(e) {
					dragObject.dragClone.style.left = e.x + "px";
					dragObject.dragClone.style.top = e.y + "px";
				});
				dojo.event.connect(anim, "onEnd", function (e) {
					// pause for a second (not literally) and disappear
					dojo.lang.setTimeout(dojo.dom.removeNode, 200,
						dragObject.dragClone);
				});
				anim.play();
				break;
		}
	},

	constrainTo: function(container) {
		this.constrainToContainer=true;
		if (container) {
			this.constrainingContainer = container;
		} else {
			this.constrainingContainer = this.domNode.parentNode;
		}
	}
});

dojo.dnd.HtmlDropTarget = function(node, types){
	if (arguments.length == 0) { return; }
	node = dojo.byId(node);
	this.domNode = node;
	dojo.dnd.DropTarget.call(this);
	this.acceptedTypes = types || [];
}
dojo.inherits(dojo.dnd.HtmlDropTarget, dojo.dnd.DropTarget);

dojo.lang.extend(dojo.dnd.HtmlDropTarget, {  
	onDragOver: function(e){
		if(!this.accepts(e.dragObjects)){ return false; }
		
		// cache the positions of the child nodes
		this.childBoxes = [];
		for (var i = 0, child; i < this.domNode.childNodes.length; i++) {
			child = this.domNode.childNodes[i];
			if (child.nodeType != dojo.dom.ELEMENT_NODE) { continue; }
			var top = dojo.style.getAbsoluteY(child);
			var bottom = top + dojo.style.getInnerHeight(child);
			var left = dojo.style.getAbsoluteX(child);
			var right = left + dojo.style.getInnerWidth(child);
			this.childBoxes.push({top: top, bottom: bottom,
				left: left, right: right, node: child});
		}
		
		// TODO: use dummy node
		
		return true;
	},
	
	_getNodeUnderMouse: function(e){
		var mousex = e.pageX || e.clientX + dojo.html.body().scrollLeft;
		var mousey = e.pageY || e.clientY + dojo.html.body().scrollTop;

		// find the child
		for (var i = 0, child; i < this.childBoxes.length; i++) {
			with (this.childBoxes[i]) {
				if (mousex >= left && mousex <= right &&
					mousey >= top && mousey <= bottom) { return i; }
			}
		}
		
		return -1;
	},

	createDropIndicator: function() {
		this.dropIndicator = document.createElement("div");
		with (this.dropIndicator.style) {
			position = "absolute";
			zIndex = 1;
			borderTopWidth = "1px";
			borderTopColor = "black";
			borderTopStyle = "solid";
			width = dojo.style.getInnerWidth(this.domNode) + "px";
			left = dojo.style.getAbsoluteX(this.domNode) + "px";
		}
	},
	
	onDragMove: function(e, dragObjects){
		var i = this._getNodeUnderMouse(e);
		
		if(!this.dropIndicator){
			this.createDropIndicator();
		}

		if(i < 0) {
			if(this.childBoxes.length) {
				var before = (dojo.html.gravity(this.childBoxes[0].node, e) & dojo.html.gravity.NORTH);
			} else {
				var before = true;
			}
		} else {
			var child = this.childBoxes[i];
			var before = (dojo.html.gravity(child.node, e) & dojo.html.gravity.NORTH);
		}
		this.placeIndicator(e, dragObjects, i, before);

		if(!dojo.html.hasParent(this.dropIndicator)) {
			dojo.html.body().appendChild(this.dropIndicator);
		}
	},

	placeIndicator: function(e, dragObjects, boxIndex, before) {
		with(this.dropIndicator.style){
			if (boxIndex < 0) {
				if (this.childBoxes.length) {
					top = (before ? this.childBoxes[0].top
						: this.childBoxes[this.childBoxes.length - 1].bottom) + "px";
				} else {
					top = dojo.style.getAbsoluteY(this.domNode) + "px";
				}
			} else {
				var child = this.childBoxes[boxIndex];
				top = (before ? child.top : child.bottom) + "px";
			}
		}
	},

	onDragOut: function(e) {
		dojo.dom.removeNode(this.dropIndicator);
		delete this.dropIndicator;
	},
	
	/**
	 * Inserts the DragObject as a child of this node relative to the
	 * position of the mouse.
	 *
	 * @return true if the DragObject was inserted, false otherwise
	 */
	onDrop: function(e){
		this.onDragOut(e);
		
		var i = this._getNodeUnderMouse(e);

		if (i < 0) {
			if (this.childBoxes.length) {
				if (dojo.html.gravity(this.childBoxes[0].node, e) & dojo.html.gravity.NORTH) {
					return this.insert(e, this.childBoxes[0].node, "before");
				} else {
					return this.insert(e, this.childBoxes[this.childBoxes.length-1].node, "after");
				}
			}
			return this.insert(e, this.domNode, "append");
		}
		
		var child = this.childBoxes[i];
		if (dojo.html.gravity(child.node, e) & dojo.html.gravity.NORTH) {
			return this.insert(e, child.node, "before");
		} else {
			return this.insert(e, child.node, "after");
		}
	},

	insert: function(e, refNode, position) {
		var node = e.dragObject.domNode;

		if(position == "before") {
			return dojo.html.insertBefore(node, refNode);
		} else if(position == "after") {
			return dojo.html.insertAfter(node, refNode);
		} else if(position == "append") {
			refNode.appendChild(node);
			return true;
		}

		return false;
	}
});
