/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.TreeBasicController");
dojo.require("dojo.event.*");
dojo.require("dojo.json");
dojo.require("dojo.io.*");
dojo.widget.defineWidget("dojo.widget.TreeBasicController", dojo.widget.HtmlWidget, {widgetType:"TreeBasicController", DNDController:"", dieWithTree:false, initialize:function (args, frag) {
	if (this.DNDController == "create") {
		dojo.require("dojo.dnd.TreeDragAndDrop");
		this.DNDController = new dojo.dnd.TreeDNDController(this);
	}
}, listenTree:function (tree) {
	dojo.event.topic.subscribe(tree.eventNames.createDOMNode, this, "onCreateDOMNode");
	dojo.event.topic.subscribe(tree.eventNames.treeClick, this, "onTreeClick");
	dojo.event.topic.subscribe(tree.eventNames.treeCreate, this, "onTreeCreate");
	dojo.event.topic.subscribe(tree.eventNames.treeDestroy, this, "onTreeDestroy");
	if (this.DNDController) {
		this.DNDController.listenTree(tree);
	}
}, unlistenTree:function (tree) {
	dojo.event.topic.unsubscribe(tree.eventNames.createDOMNode, this, "onCreateDOMNode");
	dojo.event.topic.unsubscribe(tree.eventNames.treeClick, this, "onTreeClick");
	dojo.event.topic.unsubscribe(tree.eventNames.treeCreate, this, "onTreeCreate");
	dojo.event.topic.unsubscribe(tree.eventNames.treeDestroy, this, "onTreeDestroy");
}, onTreeDestroy:function (message) {
	var tree = message.source;
	this.unlistenTree(tree);
	if (this.dieWithTree) {
		this.destroy();
	}
}, onCreateDOMNode:function (message) {
	var node = message.source;
	if (node.expandLevel > 0) {
		this.expandToLevel(node, node.expandLevel);
	}
}, onTreeCreate:function (message) {
	var tree = message.source;
	var _this = this;
	if (tree.expandLevel) {
		dojo.lang.forEach(tree.children, function (child) {
			_this.expandToLevel(child, tree.expandLevel - 1);
		});
	}
}, expandToLevel:function (node, level) {
	if (level == 0) {
		return;
	}
	var children = node.children;
	var _this = this;
	var handler = function (node, expandLevel) {
		this.node = node;
		this.expandLevel = expandLevel;
		this.process = function () {
			for (var i = 0; i < this.node.children.length; i++) {
				var child = node.children[i];
				_this.expandToLevel(child, this.expandLevel);
			}
		};
	};
	var h = new handler(node, level - 1);
	this.expand(node, false, h, h.process);
}, onTreeClick:function (message) {
	var node = message.source;
	if (node.isLocked()) {
		return false;
	}
	if (node.isExpanded) {
		this.collapse(node);
	} else {
		this.expand(node);
	}
}, expand:function (node, sync, callObj, callFunc) {
	node.expand();
	if (callFunc) {
		callFunc.apply(callObj, [node]);
	}
}, collapse:function (node) {
	node.collapse();
}, canMove:function (child, newParent) {
	if (child.actionIsDisabled(child.actions.MOVE)) {
		return false;
	}
	if (child.parent !== newParent && newParent.actionIsDisabled(newParent.actions.ADDCHILD)) {
		return false;
	}
	var node = newParent;
	while (node.isTreeNode) {
		if (node === child) {
			return false;
		}
		node = node.parent;
	}
	return true;
}, move:function (child, newParent, index) {
	if (!this.canMove(child, newParent)) {
		return false;
	}
	var result = this.doMove(child, newParent, index);
	if (!result) {
		return result;
	}
	if (newParent.isTreeNode) {
		this.expand(newParent);
	}
	return result;
}, doMove:function (child, newParent, index) {
	child.tree.move(child, newParent, index);
	return true;
}, canRemoveNode:function (child) {
	if (child.actionIsDisabled(child.actions.REMOVE)) {
		return false;
	}
	return true;
}, removeNode:function (node, callObj, callFunc) {
	if (!this.canRemoveNode(node)) {
		return false;
	}
	return this.doRemoveNode(node, callObj, callFunc);
}, doRemoveNode:function (node, callObj, callFunc) {
	node.tree.removeNode(node);
	if (callFunc) {
		callFunc.apply(dojo.lang.isUndefined(callObj) ? this : callObj, [node]);
	}
}, canCreateChild:function (parent, index, data) {
	if (parent.actionIsDisabled(parent.actions.ADDCHILD)) {
		return false;
	}
	return true;
}, createChild:function (parent, index, data, callObj, callFunc) {
	if (!this.canCreateChild(parent, index, data)) {
		return false;
	}
	return this.doCreateChild.apply(this, arguments);
}, doCreateChild:function (parent, index, data, callObj, callFunc) {
	var widgetType = data.widgetType ? data.widgetType : "TreeNode";
	var newChild = dojo.widget.createWidget(widgetType, data);
	parent.addChild(newChild, index);
	this.expand(parent);
	if (callFunc) {
		callFunc.apply(callObj, [newChild]);
	}
	return newChild;
}});

