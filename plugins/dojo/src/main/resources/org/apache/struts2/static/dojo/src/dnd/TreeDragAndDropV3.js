/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.dnd.TreeDragAndDropV3");
dojo.require("dojo.dnd.HtmlDragAndDrop");
dojo.require("dojo.lang.func");
dojo.require("dojo.lang.array");
dojo.require("dojo.lang.extras");
dojo.require("dojo.Deferred");
dojo.require("dojo.html.layout");
dojo.dnd.TreeDragSourceV3 = function (node, syncController, type, treeNode) {
	this.controller = syncController;
	this.treeNode = treeNode;
	dojo.dnd.HtmlDragSource.call(this, node, type);
};
dojo.inherits(dojo.dnd.TreeDragSourceV3, dojo.dnd.HtmlDragSource);
dojo.dnd.TreeDropTargetV3 = function (domNode, controller, type, treeNode) {
	this.treeNode = treeNode;
	this.controller = controller;
	dojo.dnd.HtmlDropTarget.call(this, domNode, type);
};
dojo.inherits(dojo.dnd.TreeDropTargetV3, dojo.dnd.HtmlDropTarget);
dojo.lang.extend(dojo.dnd.TreeDropTargetV3, {autoExpandDelay:1500, autoExpandTimer:null, position:null, indicatorStyle:"2px black groove", showIndicator:function (position) {
	if (this.position == position) {
		return;
	}
	this.hideIndicator();
	this.position = position;
	var node = this.treeNode;
	node.contentNode.style.width = dojo.html.getBorderBox(node.labelNode).width + "px";
	if (position == "onto") {
		node.contentNode.style.border = this.indicatorStyle;
	} else {
		if (position == "before") {
			node.contentNode.style.borderTop = this.indicatorStyle;
		} else {
			if (position == "after") {
				node.contentNode.style.borderBottom = this.indicatorStyle;
			}
		}
	}
}, hideIndicator:function () {
	this.treeNode.contentNode.style.borderBottom = "";
	this.treeNode.contentNode.style.borderTop = "";
	this.treeNode.contentNode.style.border = "";
	this.treeNode.contentNode.style.width = "";
	this.position = null;
}, onDragOver:function (e) {
	var accepts = dojo.dnd.HtmlDropTarget.prototype.onDragOver.apply(this, arguments);
	if (accepts && this.treeNode.isFolder && !this.treeNode.isExpanded) {
		this.setAutoExpandTimer();
	}
	if (accepts) {
		this.cacheNodeCoords();
	}
	return accepts;
}, accepts:function (dragObjects) {
	var accepts = dojo.dnd.HtmlDropTarget.prototype.accepts.apply(this, arguments);
	if (!accepts) {
		return false;
	}
	for (var i = 0; i < dragObjects.length; i++) {
		var sourceTreeNode = dragObjects[i].treeNode;
		if (sourceTreeNode === this.treeNode) {
			return false;
		}
	}
	return true;
}, setAutoExpandTimer:function () {
	var _this = this;
	var autoExpand = function () {
		if (dojo.dnd.dragManager.currentDropTarget === _this) {
			_this.controller.expand(_this.treeNode);
			dojo.dnd.dragManager.cacheTargetLocations();
		}
	};
	this.autoExpandTimer = dojo.lang.setTimeout(autoExpand, _this.autoExpandDelay);
}, getAcceptPosition:function (e, dragObjects) {
	var DndMode = this.treeNode.tree.DndMode;
	if (DndMode & dojo.widget.TreeV3.prototype.DndModes.ONTO && this.treeNode.actionIsDisabledNow(this.treeNode.actions.ADDCHILD)) {
		DndMode &= ~dojo.widget.TreeV3.prototype.DndModes.ONTO;
	}
	var position = this.getPosition(e, DndMode);
	if (position == "onto") {
		return position;
	}
	for (var i = 0; i < dragObjects.length; i++) {
		var source = dragObjects[i].dragSource;
		if (source.treeNode && this.isAdjacentNode(source.treeNode, position)) {
			continue;
		}
		if (!this.controller.canMove(source.treeNode ? source.treeNode : source, this.treeNode.parent)) {
			return false;
		}
	}
	return position;
}, onDropEnd:function (e) {
	this.clearAutoExpandTimer();
	this.hideIndicator();
}, onDragOut:function (e) {
	this.clearAutoExpandTimer();
	this.hideIndicator();
}, clearAutoExpandTimer:function () {
	if (this.autoExpandTimer) {
		clearTimeout(this.autoExpandTimer);
		this.autoExpandTimer = null;
	}
}, onDragMove:function (e, dragObjects) {
	var position = this.getAcceptPosition(e, dragObjects);
	if (position) {
		this.showIndicator(position);
	}
}, isAdjacentNode:function (sourceNode, position) {
	if (sourceNode === this.treeNode) {
		return true;
	}
	if (sourceNode.getNextSibling() === this.treeNode && position == "before") {
		return true;
	}
	if (sourceNode.getPreviousSibling() === this.treeNode && position == "after") {
		return true;
	}
	return false;
}, cacheNodeCoords:function () {
	var node = this.treeNode.contentNode;
	this.cachedNodeY = dojo.html.getAbsolutePosition(node).y;
	this.cachedNodeHeight = dojo.html.getBorderBox(node).height;
}, getPosition:function (e, DndMode) {
	var mousey = e.pageY || e.clientY + dojo.body().scrollTop;
	var relY = mousey - this.cachedNodeY;
	var p = relY / this.cachedNodeHeight;
	var position = "";
	if (DndMode & dojo.widget.TreeV3.prototype.DndModes.ONTO && DndMode & dojo.widget.TreeV3.prototype.DndModes.BETWEEN) {
		if (p <= 0.33) {
			position = "before";
		} else {
			if (p <= 0.66 || this.treeNode.isExpanded && this.treeNode.children.length && !this.treeNode.isLastChild()) {
				position = "onto";
			} else {
				position = "after";
			}
		}
	} else {
		if (DndMode & dojo.widget.TreeV3.prototype.DndModes.BETWEEN) {
			if (p <= 0.5 || this.treeNode.isExpanded && this.treeNode.children.length && !this.treeNode.isLastChild()) {
				position = "before";
			} else {
				position = "after";
			}
		} else {
			if (DndMode & dojo.widget.TreeV3.prototype.DndModes.ONTO) {
				position = "onto";
			}
		}
	}
	return position;
}, getTargetParentIndex:function (source, position) {
	var index = position == "before" ? this.treeNode.getParentIndex() : this.treeNode.getParentIndex() + 1;
	if (source.treeNode && this.treeNode.parent === source.treeNode.parent && this.treeNode.getParentIndex() > source.treeNode.getParentIndex()) {
		index--;
	}
	return index;
}, onDrop:function (e) {
	var position = this.position;
	var source = e.dragObject.dragSource;
	var targetParent, targetIndex;
	if (position == "onto") {
		targetParent = this.treeNode;
		targetIndex = 0;
	} else {
		targetIndex = this.getTargetParentIndex(source, position);
		targetParent = this.treeNode.parent;
	}
	var r = this.getDropHandler(e, source, targetParent, targetIndex)();
	return r;
}, getDropHandler:function (e, source, targetParent, targetIndex) {
	var handler;
	var _this = this;
	handler = function () {
		var result;
		if (source.treeNode) {
			result = _this.controller.move(source.treeNode, targetParent, targetIndex, true);
		} else {
			if (dojo.lang.isFunction(source.onDrop)) {
				source.onDrop(targetParent, targetIndex);
			}
			var treeNode = source.getTreeNode();
			if (treeNode) {
				result = _this.controller.createChild(targetParent, targetIndex, treeNode, true);
			} else {
				result = true;
			}
		}
		if (result instanceof dojo.Deferred) {
			var isSuccess = result.fired == 0;
			if (!isSuccess) {
				_this.handleDropError(source, targetParent, targetIndex, result);
			}
			return isSuccess;
		} else {
			return result;
		}
	};
	return handler;
}, handleDropError:function (source, parent, index, result) {
	dojo.debug("TreeDropTargetV3.handleDropError: DND error occured");
	dojo.debugShallow(result);
}});

