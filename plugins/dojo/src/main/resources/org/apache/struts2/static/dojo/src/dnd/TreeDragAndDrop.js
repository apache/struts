/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.dnd.TreeDragAndDrop");
dojo.require("dojo.dnd.HtmlDragAndDrop");
dojo.require("dojo.lang.func");
dojo.require("dojo.lang.array");
dojo.require("dojo.lang.extras");
dojo.require("dojo.html.layout");
dojo.dnd.TreeDragSource = function (node, syncController, type, treeNode) {
	this.controller = syncController;
	this.treeNode = treeNode;
	dojo.dnd.HtmlDragSource.call(this, node, type);
};
dojo.inherits(dojo.dnd.TreeDragSource, dojo.dnd.HtmlDragSource);
dojo.lang.extend(dojo.dnd.TreeDragSource, {onDragStart:function () {
	var dragObject = dojo.dnd.HtmlDragSource.prototype.onDragStart.call(this);
	dragObject.treeNode = this.treeNode;
	dragObject.onDragStart = dojo.lang.hitch(dragObject, function (e) {
		this.savedSelectedNode = this.treeNode.tree.selector.selectedNode;
		if (this.savedSelectedNode) {
			this.savedSelectedNode.unMarkSelected();
		}
		var result = dojo.dnd.HtmlDragObject.prototype.onDragStart.apply(this, arguments);
		var cloneGrid = this.dragClone.getElementsByTagName("img");
		for (var i = 0; i < cloneGrid.length; i++) {
			cloneGrid.item(i).style.backgroundImage = "url()";
		}
		return result;
	});
	dragObject.onDragEnd = function (e) {
		if (this.savedSelectedNode) {
			this.savedSelectedNode.markSelected();
		}
		return dojo.dnd.HtmlDragObject.prototype.onDragEnd.apply(this, arguments);
	};
	return dragObject;
}, onDragEnd:function (e) {
	var res = dojo.dnd.HtmlDragSource.prototype.onDragEnd.call(this, e);
	return res;
}});
dojo.dnd.TreeDropTarget = function (domNode, controller, type, treeNode) {
	this.treeNode = treeNode;
	this.controller = controller;
	dojo.dnd.HtmlDropTarget.apply(this, [domNode, type]);
};
dojo.inherits(dojo.dnd.TreeDropTarget, dojo.dnd.HtmlDropTarget);
dojo.lang.extend(dojo.dnd.TreeDropTarget, {autoExpandDelay:1500, autoExpandTimer:null, position:null, indicatorStyle:"2px black solid", showIndicator:function (position) {
	if (this.position == position) {
		return;
	}
	this.hideIndicator();
	this.position = position;
	if (position == "before") {
		this.treeNode.labelNode.style.borderTop = this.indicatorStyle;
	} else {
		if (position == "after") {
			this.treeNode.labelNode.style.borderBottom = this.indicatorStyle;
		} else {
			if (position == "onto") {
				this.treeNode.markSelected();
			}
		}
	}
}, hideIndicator:function () {
	this.treeNode.labelNode.style.borderBottom = "";
	this.treeNode.labelNode.style.borderTop = "";
	this.treeNode.unMarkSelected();
	this.position = null;
}, onDragOver:function (e) {
	var accepts = dojo.dnd.HtmlDropTarget.prototype.onDragOver.apply(this, arguments);
	if (accepts && this.treeNode.isFolder && !this.treeNode.isExpanded) {
		this.setAutoExpandTimer();
	}
	return accepts;
}, accepts:function (dragObjects) {
	var accepts = dojo.dnd.HtmlDropTarget.prototype.accepts.apply(this, arguments);
	if (!accepts) {
		return false;
	}
	var sourceTreeNode = dragObjects[0].treeNode;
	if (dojo.lang.isUndefined(sourceTreeNode) || !sourceTreeNode || !sourceTreeNode.isTreeNode) {
		dojo.raise("Source is not TreeNode or not found");
	}
	if (sourceTreeNode === this.treeNode) {
		return false;
	}
	return true;
}, setAutoExpandTimer:function () {
	var _this = this;
	var autoExpand = function () {
		if (dojo.dnd.dragManager.currentDropTarget === _this) {
			_this.controller.expand(_this.treeNode);
		}
	};
	this.autoExpandTimer = dojo.lang.setTimeout(autoExpand, _this.autoExpandDelay);
}, getDNDMode:function () {
	return this.treeNode.tree.DNDMode;
}, getAcceptPosition:function (e, sourceTreeNode) {
	var DNDMode = this.getDNDMode();
	if (DNDMode & dojo.widget.Tree.prototype.DNDModes.ONTO && !(!this.treeNode.actionIsDisabled(dojo.widget.TreeNode.prototype.actions.ADDCHILD) && sourceTreeNode.parent !== this.treeNode && this.controller.canMove(sourceTreeNode, this.treeNode))) {
		DNDMode &= ~dojo.widget.Tree.prototype.DNDModes.ONTO;
	}
	var position = this.getPosition(e, DNDMode);
	if (position == "onto" || (!this.isAdjacentNode(sourceTreeNode, position) && this.controller.canMove(sourceTreeNode, this.treeNode.parent))) {
		return position;
	} else {
		return false;
	}
}, onDragOut:function (e) {
	this.clearAutoExpandTimer();
	this.hideIndicator();
}, clearAutoExpandTimer:function () {
	if (this.autoExpandTimer) {
		clearTimeout(this.autoExpandTimer);
		this.autoExpandTimer = null;
	}
}, onDragMove:function (e, dragObjects) {
	var sourceTreeNode = dragObjects[0].treeNode;
	var position = this.getAcceptPosition(e, sourceTreeNode);
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
}, getPosition:function (e, DNDMode) {
	var node = dojo.byId(this.treeNode.labelNode);
	var mousey = e.pageY || e.clientY + dojo.body().scrollTop;
	var nodey = dojo.html.getAbsolutePosition(node).y;
	var height = dojo.html.getBorderBox(node).height;
	var relY = mousey - nodey;
	var p = relY / height;
	var position = "";
	if (DNDMode & dojo.widget.Tree.prototype.DNDModes.ONTO && DNDMode & dojo.widget.Tree.prototype.DNDModes.BETWEEN) {
		if (p <= 0.3) {
			position = "before";
		} else {
			if (p <= 0.7) {
				position = "onto";
			} else {
				position = "after";
			}
		}
	} else {
		if (DNDMode & dojo.widget.Tree.prototype.DNDModes.BETWEEN) {
			if (p <= 0.5) {
				position = "before";
			} else {
				position = "after";
			}
		} else {
			if (DNDMode & dojo.widget.Tree.prototype.DNDModes.ONTO) {
				position = "onto";
			}
		}
	}
	return position;
}, getTargetParentIndex:function (sourceTreeNode, position) {
	var index = position == "before" ? this.treeNode.getParentIndex() : this.treeNode.getParentIndex() + 1;
	if (this.treeNode.parent === sourceTreeNode.parent && this.treeNode.getParentIndex() > sourceTreeNode.getParentIndex()) {
		index--;
	}
	return index;
}, onDrop:function (e) {
	var position = this.position;
	this.onDragOut(e);
	var sourceTreeNode = e.dragObject.treeNode;
	if (!dojo.lang.isObject(sourceTreeNode)) {
		dojo.raise("TreeNode not found in dragObject");
	}
	if (position == "onto") {
		return this.controller.move(sourceTreeNode, this.treeNode, 0);
	} else {
		var index = this.getTargetParentIndex(sourceTreeNode, position);
		return this.controller.move(sourceTreeNode, this.treeNode.parent, index);
	}
}});
dojo.dnd.TreeDNDController = function (treeController) {
	this.treeController = treeController;
	this.dragSources = {};
	this.dropTargets = {};
};
dojo.lang.extend(dojo.dnd.TreeDNDController, {listenTree:function (tree) {
	dojo.event.topic.subscribe(tree.eventNames.createDOMNode, this, "onCreateDOMNode");
	dojo.event.topic.subscribe(tree.eventNames.moveFrom, this, "onMoveFrom");
	dojo.event.topic.subscribe(tree.eventNames.moveTo, this, "onMoveTo");
	dojo.event.topic.subscribe(tree.eventNames.addChild, this, "onAddChild");
	dojo.event.topic.subscribe(tree.eventNames.removeNode, this, "onRemoveNode");
	dojo.event.topic.subscribe(tree.eventNames.treeDestroy, this, "onTreeDestroy");
}, unlistenTree:function (tree) {
	dojo.event.topic.unsubscribe(tree.eventNames.createDOMNode, this, "onCreateDOMNode");
	dojo.event.topic.unsubscribe(tree.eventNames.moveFrom, this, "onMoveFrom");
	dojo.event.topic.unsubscribe(tree.eventNames.moveTo, this, "onMoveTo");
	dojo.event.topic.unsubscribe(tree.eventNames.addChild, this, "onAddChild");
	dojo.event.topic.unsubscribe(tree.eventNames.removeNode, this, "onRemoveNode");
	dojo.event.topic.unsubscribe(tree.eventNames.treeDestroy, this, "onTreeDestroy");
}, onTreeDestroy:function (message) {
	this.unlistenTree(message.source);
}, onCreateDOMNode:function (message) {
	this.registerDNDNode(message.source);
}, onAddChild:function (message) {
	this.registerDNDNode(message.child);
}, onMoveFrom:function (message) {
	var _this = this;
	dojo.lang.forEach(message.child.getDescendants(), function (node) {
		_this.unregisterDNDNode(node);
	});
}, onMoveTo:function (message) {
	var _this = this;
	dojo.lang.forEach(message.child.getDescendants(), function (node) {
		_this.registerDNDNode(node);
	});
}, registerDNDNode:function (node) {
	if (!node.tree.DNDMode) {
		return;
	}
	var source = null;
	var target = null;
	if (!node.actionIsDisabled(node.actions.MOVE)) {
		var source = new dojo.dnd.TreeDragSource(node.labelNode, this, node.tree.widgetId, node);
		this.dragSources[node.widgetId] = source;
	}
	var target = new dojo.dnd.TreeDropTarget(node.labelNode, this.treeController, node.tree.DNDAcceptTypes, node);
	this.dropTargets[node.widgetId] = target;
}, unregisterDNDNode:function (node) {
	if (this.dragSources[node.widgetId]) {
		dojo.dnd.dragManager.unregisterDragSource(this.dragSources[node.widgetId]);
		delete this.dragSources[node.widgetId];
	}
	if (this.dropTargets[node.widgetId]) {
		dojo.dnd.dragManager.unregisterDropTarget(this.dropTargets[node.widgetId]);
		delete this.dropTargets[node.widgetId];
	}
}});

