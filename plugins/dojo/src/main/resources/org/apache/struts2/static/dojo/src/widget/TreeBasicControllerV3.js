/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.TreeBasicControllerV3");
dojo.require("dojo.event.*");
dojo.require("dojo.json");
dojo.require("dojo.io.*");
dojo.require("dojo.widget.TreeCommon");
dojo.require("dojo.widget.TreeNodeV3");
dojo.require("dojo.widget.TreeV3");
dojo.widget.defineWidget("dojo.widget.TreeBasicControllerV3", [dojo.widget.HtmlWidget, dojo.widget.TreeCommon], function () {
	this.listenedTrees = {};
}, {listenTreeEvents:["afterSetFolder", "afterTreeCreate", "beforeTreeDestroy"], listenNodeFilter:function (elem) {
	return elem instanceof dojo.widget.Widget;
}, editor:null, initialize:function (args) {
	if (args.editor) {
		this.editor = dojo.widget.byId(args.editor);
		this.editor.controller = this;
	}
}, getInfo:function (elem) {
	return elem.getInfo();
}, onBeforeTreeDestroy:function (message) {
	this.unlistenTree(message.source);
}, onAfterSetFolder:function (message) {
	if (message.source.expandLevel > 0) {
		this.expandToLevel(message.source, message.source.expandLevel);
	}
	if (message.source.loadLevel > 0) {
		this.loadToLevel(message.source, message.source.loadLevel);
	}
}, _focusNextVisible:function (nodeWidget) {
	if (nodeWidget.isFolder && nodeWidget.isExpanded && nodeWidget.children.length > 0) {
		returnWidget = nodeWidget.children[0];
	} else {
		while (nodeWidget.isTreeNode && nodeWidget.isLastChild()) {
			nodeWidget = nodeWidget.parent;
		}
		if (nodeWidget.isTreeNode) {
			var returnWidget = nodeWidget.parent.children[nodeWidget.getParentIndex() + 1];
		}
	}
	if (returnWidget && returnWidget.isTreeNode) {
		this._focusLabel(returnWidget);
		return returnWidget;
	}
}, _focusPreviousVisible:function (nodeWidget) {
	var returnWidget = nodeWidget;
	if (!nodeWidget.isFirstChild()) {
		var previousSibling = nodeWidget.parent.children[nodeWidget.getParentIndex() - 1];
		nodeWidget = previousSibling;
		while (nodeWidget.isFolder && nodeWidget.isExpanded && nodeWidget.children.length > 0) {
			returnWidget = nodeWidget;
			nodeWidget = nodeWidget.children[nodeWidget.children.length - 1];
		}
	} else {
		nodeWidget = nodeWidget.parent;
	}
	if (nodeWidget && nodeWidget.isTreeNode) {
		returnWidget = nodeWidget;
	}
	if (returnWidget && returnWidget.isTreeNode) {
		this._focusLabel(returnWidget);
		return returnWidget;
	}
}, _focusZoomIn:function (nodeWidget) {
	var returnWidget = nodeWidget;
	if (nodeWidget.isFolder && !nodeWidget.isExpanded) {
		this.expand(nodeWidget);
	} else {
		if (nodeWidget.children.length > 0) {
			nodeWidget = nodeWidget.children[0];
		}
	}
	if (nodeWidget && nodeWidget.isTreeNode) {
		returnWidget = nodeWidget;
	}
	if (returnWidget && returnWidget.isTreeNode) {
		this._focusLabel(returnWidget);
		return returnWidget;
	}
}, _focusZoomOut:function (node) {
	var returnWidget = node;
	if (node.isFolder && node.isExpanded) {
		this.collapse(node);
	} else {
		node = node.parent;
	}
	if (node && node.isTreeNode) {
		returnWidget = node;
	}
	if (returnWidget && returnWidget.isTreeNode) {
		this._focusLabel(returnWidget);
		return returnWidget;
	}
}, onFocusNode:function (e) {
	var node = this.domElement2TreeNode(e.target);
	if (node) {
		node.viewFocus();
		dojo.event.browser.stopEvent(e);
	}
}, onBlurNode:function (e) {
	var node = this.domElement2TreeNode(e.target);
	if (!node) {
		return;
	}
	var labelNode = node.labelNode;
	labelNode.setAttribute("tabIndex", "-1");
	node.viewUnfocus();
	dojo.event.browser.stopEvent(e);
	node.tree.domNode.setAttribute("tabIndex", "0");
}, _focusLabel:function (node) {
	var lastFocused = node.tree.lastFocused;
	var labelNode;
	if (lastFocused && lastFocused.labelNode) {
		labelNode = lastFocused.labelNode;
		dojo.event.disconnect(labelNode, "onblur", this, "onBlurNode");
		labelNode.setAttribute("tabIndex", "-1");
		dojo.html.removeClass(labelNode, "TreeLabelFocused");
	}
	labelNode = node.labelNode;
	labelNode.setAttribute("tabIndex", "0");
	node.tree.lastFocused = node;
	dojo.html.addClass(labelNode, "TreeLabelFocused");
	dojo.event.connectOnce(labelNode, "onblur", this, "onBlurNode");
	dojo.event.connectOnce(labelNode, "onfocus", this, "onFocusNode");
	labelNode.focus();
}, onKey:function (e) {
	if (!e.key || e.ctrkKey || e.altKey) {
		return;
	}
	var nodeWidget = this.domElement2TreeNode(e.target);
	if (!nodeWidget) {
		return;
	}
	var treeWidget = nodeWidget.tree;
	if (treeWidget.lastFocused && treeWidget.lastFocused.labelNode) {
		nodeWidget = treeWidget.lastFocused;
	}
	switch (e.key) {
	  case e.KEY_TAB:
		if (e.shiftKey) {
			treeWidget.domNode.setAttribute("tabIndex", "-1");
		}
		break;
	  case e.KEY_RIGHT_ARROW:
		this._focusZoomIn(nodeWidget);
		dojo.event.browser.stopEvent(e);
		break;
	  case e.KEY_LEFT_ARROW:
		this._focusZoomOut(nodeWidget);
		dojo.event.browser.stopEvent(e);
		break;
	  case e.KEY_UP_ARROW:
		this._focusPreviousVisible(nodeWidget);
		dojo.event.browser.stopEvent(e);
		break;
	  case e.KEY_DOWN_ARROW:
		this._focusNextVisible(nodeWidget);
		dojo.event.browser.stopEvent(e);
		break;
	}
}, onFocusTree:function (e) {
	if (!e.currentTarget) {
		return;
	}
	try {
		var treeWidget = this.getWidgetByNode(e.currentTarget);
		if (!treeWidget || !treeWidget.isTree) {
			return;
		}
		var nodeWidget = this.getWidgetByNode(treeWidget.domNode.firstChild);
		if (nodeWidget && nodeWidget.isTreeNode) {
			if (treeWidget.lastFocused && treeWidget.lastFocused.isTreeNode) {
				nodeWidget = treeWidget.lastFocused;
			}
			this._focusLabel(nodeWidget);
		}
	}
	catch (e) {
	}
}, onAfterTreeCreate:function (message) {
	var tree = message.source;
	dojo.event.browser.addListener(tree.domNode, "onKey", dojo.lang.hitch(this, this.onKey));
	dojo.event.browser.addListener(tree.domNode, "onmousedown", dojo.lang.hitch(this, this.onTreeMouseDown));
	dojo.event.browser.addListener(tree.domNode, "onclick", dojo.lang.hitch(this, this.onTreeClick));
	dojo.event.browser.addListener(tree.domNode, "onfocus", dojo.lang.hitch(this, this.onFocusTree));
	tree.domNode.setAttribute("tabIndex", "0");
	if (tree.expandLevel) {
		this.expandToLevel(tree, tree.expandLevel);
	}
	if (tree.loadLevel) {
		this.loadToLevel(tree, tree.loadLevel);
	}
}, onTreeMouseDown:function (e) {
}, onTreeClick:function (e) {
	var domElement = e.target;
	var node = this.domElement2TreeNode(domElement);
	if (!node || !node.isTreeNode) {
		return;
	}
	var checkExpandClick = function (el) {
		return el === node.expandNode;
	};
	if (this.checkPathCondition(domElement, checkExpandClick)) {
		this.processExpandClick(node);
	}
	this._focusLabel(node);
}, processExpandClick:function (node) {
	if (node.isExpanded) {
		this.collapse(node);
	} else {
		this.expand(node);
	}
}, batchExpandTimeout:20, expandAll:function (nodeOrTree) {
	return this.expandToLevel(nodeOrTree, Number.POSITIVE_INFINITY);
}, collapseAll:function (nodeOrTree) {
	var _this = this;
	var filter = function (elem) {
		return (elem instanceof dojo.widget.Widget) && elem.isFolder && elem.isExpanded;
	};
	if (nodeOrTree.isTreeNode) {
		this.processDescendants(nodeOrTree, filter, this.collapse);
	} else {
		if (nodeOrTree.isTree) {
			dojo.lang.forEach(nodeOrTree.children, function (c) {
				_this.processDescendants(c, filter, _this.collapse);
			});
		}
	}
}, expandToNode:function (node, withSelected) {
	n = withSelected ? node : node.parent;
	s = [];
	while (!n.isExpanded) {
		s.push(n);
		n = n.parent;
	}
	dojo.lang.forEach(s, function (n) {
		n.expand();
	});
}, expandToLevel:function (nodeOrTree, level) {
	dojo.require("dojo.widget.TreeTimeoutIterator");
	var _this = this;
	var filterFunc = function (elem) {
		var res = elem.isFolder || elem.children && elem.children.length;
		return res;
	};
	var callFunc = function (node, iterator) {
		_this.expand(node, true);
		iterator.forward();
	};
	var iterator = new dojo.widget.TreeTimeoutIterator(nodeOrTree, callFunc, this);
	iterator.setFilter(filterFunc);
	iterator.timeout = this.batchExpandTimeout;
	iterator.setMaxLevel(nodeOrTree.isTreeNode ? level - 1 : level);
	return iterator.start(nodeOrTree.isTreeNode);
}, getWidgetByNode:function (node) {
	var widgetId;
	var newNode = node;
	while (!(widgetId = newNode.widgetId)) {
		newNode = newNode.parentNode;
		if (newNode == null) {
			break;
		}
	}
	if (widgetId) {
		return dojo.widget.byId(widgetId);
	} else {
		if (node == null) {
			return null;
		} else {
			return dojo.widget.manager.byNode(node);
		}
	}
}, expand:function (node) {
	if (node.isFolder) {
		node.expand();
	}
}, collapse:function (node) {
	if (node.isFolder) {
		node.collapse();
	}
}, canEditLabel:function (node) {
	if (node.actionIsDisabledNow(node.actions.EDIT)) {
		return false;
	}
	return true;
}, editLabelStart:function (node) {
	if (!this.canEditLabel(node)) {
		return false;
	}
	if (!this.editor.isClosed()) {
		this.editLabelFinish(this.editor.saveOnBlur);
	}
	this.doEditLabelStart(node);
}, editLabelFinish:function (save) {
	this.doEditLabelFinish(save);
}, doEditLabelStart:function (node) {
	if (!this.editor) {
		dojo.raise(this.widgetType + ": no editor specified");
	}
	this.editor.open(node);
}, doEditLabelFinish:function (save, server_data) {
	if (!this.editor) {
		dojo.raise(this.widgetType + ": no editor specified");
	}
	var node = this.editor.node;
	var editorTitle = this.editor.getContents();
	this.editor.close(save);
	if (save) {
		var data = {title:editorTitle};
		if (server_data) {
			dojo.lang.mixin(data, server_data);
		}
		if (node.isPhantom) {
			var parent = node.parent;
			var index = node.getParentIndex();
			node.destroy();
			dojo.widget.TreeBasicControllerV3.prototype.doCreateChild.call(this, parent, index, data);
		} else {
			var title = server_data && server_data.title ? server_data.title : editorTitle;
			node.setTitle(title);
		}
	} else {
		if (node.isPhantom) {
			node.destroy();
		}
	}
}, makeDefaultNode:function (parent, index) {
	var data = {title:parent.tree.defaultChildTitle};
	return dojo.widget.TreeBasicControllerV3.prototype.doCreateChild.call(this, parent, index, data);
}, runStages:function (check, prepare, make, finalize, expose, args) {
	if (check && !check.apply(this, args)) {
		return false;
	}
	if (prepare && !prepare.apply(this, args)) {
		return false;
	}
	var result = make.apply(this, args);
	if (finalize) {
		finalize.apply(this, args);
	}
	if (!result) {
		return result;
	}
	if (expose) {
		expose.apply(this, args);
	}
	return result;
}});
dojo.lang.extend(dojo.widget.TreeBasicControllerV3, {createAndEdit:function (parent, index) {
	var data = {title:parent.tree.defaultChildTitle};
	if (!this.canCreateChild(parent, index, data)) {
		return false;
	}
	var child = this.doCreateChild(parent, index, data);
	if (!child) {
		return false;
	}
	this.exposeCreateChild(parent, index, data);
	child.isPhantom = true;
	if (!this.editor.isClosed()) {
		this.editLabelFinish(this.editor.saveOnBlur);
	}
	this.doEditLabelStart(child);
}});
dojo.lang.extend(dojo.widget.TreeBasicControllerV3, {canClone:function (child, newParent, index, deep) {
	return true;
}, clone:function (child, newParent, index, deep) {
	return this.runStages(this.canClone, this.prepareClone, this.doClone, this.finalizeClone, this.exposeClone, arguments);
}, exposeClone:function (child, newParent) {
	if (newParent.isTreeNode) {
		this.expand(newParent);
	}
}, doClone:function (child, newParent, index, deep) {
	var cloned = child.clone(deep);
	newParent.addChild(cloned, index);
	return cloned;
}});
dojo.lang.extend(dojo.widget.TreeBasicControllerV3, {canDetach:function (child) {
	if (child.actionIsDisabledNow(child.actions.DETACH)) {
		return false;
	}
	return true;
}, detach:function (node) {
	return this.runStages(this.canDetach, this.prepareDetach, this.doDetach, this.finalizeDetach, this.exposeDetach, arguments);
}, doDetach:function (node, callObj, callFunc) {
	node.detach();
}});
dojo.lang.extend(dojo.widget.TreeBasicControllerV3, {canDestroyChild:function (child) {
	if (child.parent && !this.canDetach(child)) {
		return false;
	}
	return true;
}, destroyChild:function (node) {
	return this.runStages(this.canDestroyChild, this.prepareDestroyChild, this.doDestroyChild, this.finalizeDestroyChild, this.exposeDestroyChild, arguments);
}, doDestroyChild:function (node) {
	node.destroy();
}});
dojo.lang.extend(dojo.widget.TreeBasicControllerV3, {canMoveNotANode:function (child, parent) {
	if (child.treeCanMove) {
		return child.treeCanMove(parent);
	}
	return true;
}, canMove:function (child, newParent) {
	if (!child.isTreeNode) {
		return this.canMoveNotANode(child, newParent);
	}
	if (child.actionIsDisabledNow(child.actions.MOVE)) {
		return false;
	}
	if (child.parent !== newParent && newParent.actionIsDisabledNow(newParent.actions.ADDCHILD)) {
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
	return this.runStages(this.canMove, this.prepareMove, this.doMove, this.finalizeMove, this.exposeMove, arguments);
}, doMove:function (child, newParent, index) {
	child.tree.move(child, newParent, index);
	return true;
}, exposeMove:function (child, newParent) {
	if (newParent.isTreeNode) {
		this.expand(newParent);
	}
}});
dojo.lang.extend(dojo.widget.TreeBasicControllerV3, {canCreateChild:function (parent, index, data) {
	if (parent.actionIsDisabledNow(parent.actions.ADDCHILD)) {
		return false;
	}
	return true;
}, createChild:function (parent, index, data) {
	if (!data) {
		data = {title:parent.tree.defaultChildTitle};
	}
	return this.runStages(this.canCreateChild, this.prepareCreateChild, this.doCreateChild, this.finalizeCreateChild, this.exposeCreateChild, [parent, index, data]);
}, prepareCreateChild:function () {
	return true;
}, finalizeCreateChild:function () {
}, doCreateChild:function (parent, index, data) {
	var newChild = parent.tree.createNode(data);
	parent.addChild(newChild, index);
	return newChild;
}, exposeCreateChild:function (parent) {
	return this.expand(parent);
}});

