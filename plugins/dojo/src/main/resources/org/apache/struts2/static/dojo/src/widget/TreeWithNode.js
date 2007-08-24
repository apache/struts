/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.require("dojo.lang.declare");
dojo.provide("dojo.widget.TreeWithNode");
dojo.declare("dojo.widget.TreeWithNode", null, function () {
}, {loadStates:{UNCHECKED:"UNCHECKED", LOADING:"LOADING", LOADED:"LOADED"}, state:"UNCHECKED", objectId:"", isContainer:true, lockLevel:0, lock:function () {
	this.lockLevel++;
}, unlock:function () {
	if (!this.lockLevel) {
		dojo.raise(this.widgetType + " unlock: not locked");
	}
	this.lockLevel--;
}, expandLevel:0, loadLevel:0, hasLock:function () {
	return this.lockLevel > 0;
}, isLocked:function () {
	var node = this;
	while (true) {
		if (node.lockLevel) {
			return true;
		}
		if (!node.parent || node.isTree) {
			break;
		}
		node = node.parent;
	}
	return false;
}, flushLock:function () {
	this.lockLevel = 0;
}, actionIsDisabled:function (action) {
	var disabled = false;
	if (dojo.lang.inArray(this.actionsDisabled, action)) {
		disabled = true;
	}
	if (this.isTreeNode) {
		if (!this.tree.allowAddChildToLeaf && action == this.actions.ADDCHILD && !this.isFolder) {
			disabled = true;
		}
	}
	return disabled;
}, actionIsDisabledNow:function (action) {
	return this.actionIsDisabled(action) || this.isLocked();
}, setChildren:function (childrenArray) {
	if (this.isTreeNode && !this.isFolder) {
		this.setFolder();
	} else {
		if (this.isTreeNode) {
			this.state = this.loadStates.LOADED;
		}
	}
	var hadChildren = this.children.length > 0;
	if (hadChildren && childrenArray) {
		this.destroyChildren();
	}
	if (childrenArray) {
		this.children = childrenArray;
	}
	var hasChildren = this.children.length > 0;
	if (this.isTreeNode && hasChildren != hadChildren) {
		this.viewSetHasChildren();
	}
	for (var i = 0; i < this.children.length; i++) {
		var child = this.children[i];
		if (!(child instanceof dojo.widget.Widget)) {
			child = this.children[i] = this.tree.createNode(child);
			var childWidgetCreated = true;
		} else {
			var childWidgetCreated = false;
		}
		if (!child.parent) {
			child.parent = this;
			if (this.tree !== child.tree) {
				child.updateTree(this.tree);
			}
			child.viewAddLayout();
			this.containerNode.appendChild(child.domNode);
			var message = {child:child, index:i, parent:this, childWidgetCreated:childWidgetCreated};
			delete dojo.widget.manager.topWidgets[child.widgetId];
			dojo.event.topic.publish(this.tree.eventNames.afterAddChild, message);
		}
		if (this.tree.eagerWidgetInstantiation) {
			dojo.lang.forEach(this.children, function (child) {
				child.setChildren();
			});
		}
	}
}, doAddChild:function (child, index) {
	return this.addChild(child, index, true);
}, addChild:function (child, index, dontPublishEvent) {
	if (dojo.lang.isUndefined(index)) {
		index = this.children.length;
	}
	if (!child.isTreeNode) {
		dojo.raise("You can only add TreeNode widgets to a " + this.widgetType + " widget!");
		return;
	}
	this.children.splice(index, 0, child);
	child.parent = this;
	child.addedTo(this, index, dontPublishEvent);
	delete dojo.widget.manager.topWidgets[child.widgetId];
}, onShow:function () {
	this.animationInProgress = false;
}, onHide:function () {
	this.animationInProgress = false;
}});

