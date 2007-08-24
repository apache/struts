/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.Tree");
dojo.require("dojo.widget.*");
dojo.require("dojo.event.*");
dojo.require("dojo.io.*");
dojo.require("dojo.widget.HtmlWidget");
dojo.require("dojo.widget.TreeNode");
dojo.require("dojo.html.common");
dojo.require("dojo.html.selection");
dojo.widget.defineWidget("dojo.widget.Tree", dojo.widget.HtmlWidget, function () {
	this.eventNames = {};
	this.tree = this;
	this.DNDAcceptTypes = [];
	this.actionsDisabled = [];
}, {widgetType:"Tree", eventNamesDefault:{createDOMNode:"createDOMNode", treeCreate:"treeCreate", treeDestroy:"treeDestroy", treeClick:"treeClick", iconClick:"iconClick", titleClick:"titleClick", moveFrom:"moveFrom", moveTo:"moveTo", addChild:"addChild", removeNode:"removeNode", expand:"expand", collapse:"collapse"}, isContainer:true, DNDMode:"off", lockLevel:0, strictFolders:true, DNDModes:{BETWEEN:1, ONTO:2}, DNDAcceptTypes:"", templateCssString:"\n.dojoTree {\n\tfont: caption;\n\tfont-size: 11px;\n\tfont-weight: normal;\n\toverflow: auto;\n}\n\n\n.dojoTreeNodeLabelTitle {\n\tpadding-left: 2px;\n\tcolor: WindowText;\n}\n\n.dojoTreeNodeLabel {\n\tcursor:hand;\n\tcursor:pointer;\n}\n\n.dojoTreeNodeLabelTitle:hover {\n\ttext-decoration: underline;\n}\n\n.dojoTreeNodeLabelSelected {\n\tbackground-color: Highlight;\n\tcolor: HighlightText;\n}\n\n.dojoTree div {\n\twhite-space: nowrap;\n}\n\n.dojoTree img, .dojoTreeNodeLabel img {\n\tvertical-align: middle;\n}\n\n", templateCssPath:dojo.uri.moduleUri("dojo.widget", "templates/images/Tree/Tree.css"), templateString:"<div class=\"dojoTree\"></div>", isExpanded:true, isTree:true, objectId:"", controller:"", selector:"", menu:"", expandLevel:"", blankIconSrc:dojo.uri.moduleUri("dojo.widget", "templates/images/Tree/treenode_blank.gif"), gridIconSrcT:dojo.uri.moduleUri("dojo.widget", "templates/images/Tree/treenode_grid_t.gif"), gridIconSrcL:dojo.uri.moduleUri("dojo.widget", "templates/images/Tree/treenode_grid_l.gif"), gridIconSrcV:dojo.uri.moduleUri("dojo.widget", "templates/images/Tree/treenode_grid_v.gif"), gridIconSrcP:dojo.uri.moduleUri("dojo.widget", "templates/images/Tree/treenode_grid_p.gif"), gridIconSrcC:dojo.uri.moduleUri("dojo.widget", "templates/images/Tree/treenode_grid_c.gif"), gridIconSrcX:dojo.uri.moduleUri("dojo.widget", "templates/images/Tree/treenode_grid_x.gif"), gridIconSrcY:dojo.uri.moduleUri("dojo.widget", "templates/images/Tree/treenode_grid_y.gif"), gridIconSrcZ:dojo.uri.moduleUri("dojo.widget", "templates/images/Tree/treenode_grid_z.gif"), expandIconSrcPlus:dojo.uri.moduleUri("dojo.widget", "templates/images/Tree/treenode_expand_plus.gif"), expandIconSrcMinus:dojo.uri.moduleUri("dojo.widget", "templates/images/Tree/treenode_expand_minus.gif"), expandIconSrcLoading:dojo.uri.moduleUri("dojo.widget", "templates/images/Tree/treenode_loading.gif"), iconWidth:18, iconHeight:18, showGrid:true, showRootGrid:true, actionIsDisabled:function (action) {
	var _this = this;
	return dojo.lang.inArray(_this.actionsDisabled, action);
}, actions:{ADDCHILD:"ADDCHILD"}, getInfo:function () {
	var info = {widgetId:this.widgetId, objectId:this.objectId};
	return info;
}, initializeController:function () {
	if (this.controller != "off") {
		if (this.controller) {
			this.controller = dojo.widget.byId(this.controller);
		} else {
			dojo.require("dojo.widget.TreeBasicController");
			this.controller = dojo.widget.createWidget("TreeBasicController", {DNDController:(this.DNDMode ? "create" : ""), dieWithTree:true});
		}
		this.controller.listenTree(this);
	} else {
		this.controller = null;
	}
}, initializeSelector:function () {
	if (this.selector != "off") {
		if (this.selector) {
			this.selector = dojo.widget.byId(this.selector);
		} else {
			dojo.require("dojo.widget.TreeSelector");
			this.selector = dojo.widget.createWidget("TreeSelector", {dieWithTree:true});
		}
		this.selector.listenTree(this);
	} else {
		this.selector = null;
	}
}, initialize:function (args, frag) {
	var _this = this;
	for (name in this.eventNamesDefault) {
		if (dojo.lang.isUndefined(this.eventNames[name])) {
			this.eventNames[name] = this.widgetId + "/" + this.eventNamesDefault[name];
		}
	}
	for (var i = 0; i < this.actionsDisabled.length; i++) {
		this.actionsDisabled[i] = this.actionsDisabled[i].toUpperCase();
	}
	if (this.DNDMode == "off") {
		this.DNDMode = 0;
	} else {
		if (this.DNDMode == "between") {
			this.DNDMode = this.DNDModes.ONTO | this.DNDModes.BETWEEN;
		} else {
			if (this.DNDMode == "onto") {
				this.DNDMode = this.DNDModes.ONTO;
			}
		}
	}
	this.expandLevel = parseInt(this.expandLevel);
	this.initializeSelector();
	this.initializeController();
	if (this.menu) {
		this.menu = dojo.widget.byId(this.menu);
		this.menu.listenTree(this);
	}
	this.containerNode = this.domNode;
}, postCreate:function () {
	this.createDOMNode();
}, createDOMNode:function () {
	dojo.html.disableSelection(this.domNode);
	for (var i = 0; i < this.children.length; i++) {
		this.children[i].parent = this;
		var node = this.children[i].createDOMNode(this, 0);
		this.domNode.appendChild(node);
	}
	if (!this.showRootGrid) {
		for (var i = 0; i < this.children.length; i++) {
			this.children[i].expand();
		}
	}
	dojo.event.topic.publish(this.eventNames.treeCreate, {source:this});
}, destroy:function () {
	dojo.event.topic.publish(this.tree.eventNames.treeDestroy, {source:this});
	return dojo.widget.HtmlWidget.prototype.destroy.apply(this, arguments);
}, addChild:function (child, index) {
	var message = {child:child, index:index, parent:this, domNodeInitialized:child.domNodeInitialized};
	this.doAddChild.apply(this, arguments);
	dojo.event.topic.publish(this.tree.eventNames.addChild, message);
}, doAddChild:function (child, index) {
	if (dojo.lang.isUndefined(index)) {
		index = this.children.length;
	}
	if (!child.isTreeNode) {
		dojo.raise("You can only add TreeNode widgets to a " + this.widgetType + " widget!");
		return;
	}
	if (this.isTreeNode) {
		if (!this.isFolder) {
			this.setFolder();
		}
	}
	var _this = this;
	dojo.lang.forEach(child.getDescendants(), function (elem) {
		elem.tree = _this.tree;
	});
	child.parent = this;
	if (this.isTreeNode) {
		this.state = this.loadStates.LOADED;
	}
	if (index < this.children.length) {
		dojo.html.insertBefore(child.domNode, this.children[index].domNode);
	} else {
		this.containerNode.appendChild(child.domNode);
		if (this.isExpanded && this.isTreeNode) {
			this.showChildren();
		}
	}
	this.children.splice(index, 0, child);
	if (child.domNodeInitialized) {
		var d = this.isTreeNode ? this.depth : -1;
		child.adjustDepth(d - child.depth + 1);
		child.updateIconTree();
	} else {
		child.depth = this.isTreeNode ? this.depth + 1 : 0;
		child.createDOMNode(child.tree, child.depth);
	}
	var prevSibling = child.getPreviousSibling();
	if (child.isLastChild() && prevSibling) {
		prevSibling.updateExpandGridColumn();
	}
}, makeBlankImg:function () {
	var img = document.createElement("img");
	img.style.width = this.iconWidth + "px";
	img.style.height = this.iconHeight + "px";
	img.src = this.blankIconSrc;
	img.style.verticalAlign = "middle";
	return img;
}, updateIconTree:function () {
	if (!this.isTree) {
		this.updateIcons();
	}
	for (var i = 0; i < this.children.length; i++) {
		this.children[i].updateIconTree();
	}
}, toString:function () {
	return "[" + this.widgetType + " ID:" + this.widgetId + "]";
}, move:function (child, newParent, index) {
	var oldParent = child.parent;
	var oldTree = child.tree;
	this.doMove.apply(this, arguments);
	var newParent = child.parent;
	var newTree = child.tree;
	var message = {oldParent:oldParent, oldTree:oldTree, newParent:newParent, newTree:newTree, child:child};
	dojo.event.topic.publish(oldTree.eventNames.moveFrom, message);
	dojo.event.topic.publish(newTree.eventNames.moveTo, message);
}, doMove:function (child, newParent, index) {
	child.parent.doRemoveNode(child);
	newParent.doAddChild(child, index);
}, removeNode:function (child) {
	if (!child.parent) {
		return;
	}
	var oldTree = child.tree;
	var oldParent = child.parent;
	var removedChild = this.doRemoveNode.apply(this, arguments);
	dojo.event.topic.publish(this.tree.eventNames.removeNode, {child:removedChild, tree:oldTree, parent:oldParent});
	return removedChild;
}, doRemoveNode:function (child) {
	if (!child.parent) {
		return;
	}
	var parent = child.parent;
	var children = parent.children;
	var index = child.getParentIndex();
	if (index < 0) {
		dojo.raise("Couldn't find node " + child + " for removal");
	}
	children.splice(index, 1);
	dojo.html.removeNode(child.domNode);
	if (parent.children.length == 0 && !parent.isTree) {
		parent.containerNode.style.display = "none";
	}
	if (index == children.length && index > 0) {
		children[index - 1].updateExpandGridColumn();
	}
	if (parent instanceof dojo.widget.Tree && index == 0 && children.length > 0) {
		children[0].updateExpandGrid();
	}
	child.parent = child.tree = null;
	return child;
}, markLoading:function () {
}, unMarkLoading:function () {
}, lock:function () {
	!this.lockLevel && this.markLoading();
	this.lockLevel++;
}, unlock:function () {
	if (!this.lockLevel) {
		dojo.raise("unlock: not locked");
	}
	this.lockLevel--;
	!this.lockLevel && this.unMarkLoading();
}, isLocked:function () {
	var node = this;
	while (true) {
		if (node.lockLevel) {
			return true;
		}
		if (node instanceof dojo.widget.Tree) {
			break;
		}
		node = node.parent;
	}
	return false;
}, flushLock:function () {
	this.lockLevel = 0;
	this.unMarkLoading();
}});

