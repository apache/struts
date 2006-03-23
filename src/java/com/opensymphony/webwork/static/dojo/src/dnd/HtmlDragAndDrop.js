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
	onDragStart: function(){
		return new dojo.dnd.HtmlDragObject(this.dragObject, this.type);
	},
	setDragHandle: function(node){
		node = dojo.byId(node);
		dojo.dnd.dragManager.unregisterDragSource(this);
		this.domNode = node;
		dojo.dnd.dragManager.registerDragSource(this);
	},
	setDragTarget: function(node){
		this.dragObject = node;
	}
});

dojo.dnd.HtmlDragObject = function(node, type){
	node = dojo.byId(node);
	this.type = type;
	this.domNode = node;
}

dojo.lang.extend(dojo.dnd.HtmlDragObject, {  
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
		
		// set up for dragging
		with(this.dragClone.style){
			position = "absolute";
			top = this.dragOffset.top + e.clientY + "px";
			left = this.dragOffset.left + e.clientX + "px";
		}
		dojo.style.setOpacity(this.dragClone, 0.5);
		dojo.html.body().appendChild(this.dragClone);
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
		this.dragClone.style.top = this.dragOffset.top + e.clientY + "px";
		this.dragClone.style.left = this.dragOffset.left + e.clientX + "px";
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
		if(!dojo.lang.inArray(this.acceptedTypes, "*")){ // wildcard
			for (var i = 0; i < e.dragObjects.length; i++) {
				if (!dojo.lang.inArray(this.acceptedTypes,
					e.dragObjects[i].type)) { return false; }
			}
		}
		
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
	
	onDragMove: function(e){
		var i = this._getNodeUnderMouse(e);
		
		if(!this.dropIndicator){
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
		}

		with(this.dropIndicator.style){
			if (i < 0) {
				if (this.childBoxes.length) {
					top = ((dojo.html.gravity(this.childBoxes[0].node, e) & dojo.html.gravity.NORTH)
						? this.childBoxes[0].top : this.childBoxes[this.childBoxes.length - 1].bottom) + "px";
				} else {
					top = dojo.style.getAbsoluteY(this.domNode) + "px";
				}
			} else {
				var child = this.childBoxes[i];
				top = ((dojo.html.gravity(child.node, e) & dojo.html.gravity.NORTH)
					? child.top : child.bottom) + "px";
			}
		}
		
		if (!this.dropIndicator.parentNode) {
			dojo.html.body().appendChild(this.dropIndicator);
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
					return dojo.dom.insertBefore(e.dragObject.domNode, 
						this.childBoxes[0].node);
				} else {
					return dojo.dom.insertAfter(e.dragObject.domNode, 
						this.childBoxes[this.childBoxes.length - 1].node);
				}
			}
			this.domNode.appendChild(e.dragObject.domNode);
			return	true;
		}
		
		var child = this.childBoxes[i];
		if (dojo.html.gravity(child.node, e) & dojo.html.gravity.NORTH) {
			return dojo.dom.insertBefore(e.dragObject.domNode, child.node);
		} else {
			return dojo.dom.insertAfter(e.dragObject.domNode, child.node);
		}
	}
});
