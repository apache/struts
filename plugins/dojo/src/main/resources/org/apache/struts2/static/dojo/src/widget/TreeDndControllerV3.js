/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.TreeDndControllerV3");
dojo.require("dojo.dnd.TreeDragAndDropV3");
dojo.require("dojo.experimental");
dojo.experimental("Tree drag'n'drop' has lots of problems/bugs, it requires dojo drag'n'drop overhaul to work, probably in 0.5");
dojo.widget.defineWidget("dojo.widget.TreeDndControllerV3", [dojo.widget.HtmlWidget, dojo.widget.TreeCommon], function () {
	this.dragSources = {};
	this.dropTargets = {};
	this.listenedTrees = {};
}, {listenTreeEvents:["afterChangeTree", "beforeTreeDestroy", "afterAddChild"], listenNodeFilter:function (elem) {
	return elem instanceof dojo.widget.Widget;
}, initialize:function (args) {
	this.treeController = dojo.lang.isString(args.controller) ? dojo.widget.byId(args.controller) : args.controller;
	if (!this.treeController) {
		dojo.raise("treeController must be declared");
	}
}, onBeforeTreeDestroy:function (message) {
	this.unlistenTree(message.source);
}, onAfterAddChild:function (message) {
	this.listenNode(message.child);
}, onAfterChangeTree:function (message) {
	if (!message.oldTree) {
		return;
	}
	if (!message.newTree || !this.listenedTrees[message.newTree.widgetId]) {
		this.processDescendants(message.node, this.listenNodeFilter, this.unlistenNode);
	}
	if (!this.listenedTrees[message.oldTree.widgetId]) {
		this.processDescendants(message.node, this.listenNodeFilter, this.listenNode);
	}
}, listenNode:function (node) {
	if (!node.tree.DndMode) {
		return;
	}
	if (this.dragSources[node.widgetId] || this.dropTargets[node.widgetId]) {
		return;
	}
	var source = null;
	var target = null;
	if (!node.actionIsDisabled(node.actions.MOVE)) {
		var source = this.makeDragSource(node);
		this.dragSources[node.widgetId] = source;
	}
	var target = this.makeDropTarget(node);
	this.dropTargets[node.widgetId] = target;
}, makeDragSource:function (node) {
	return new dojo.dnd.TreeDragSourceV3(node.contentNode, this, node.tree.widgetId, node);
}, makeDropTarget:function (node) {
	return new dojo.dnd.TreeDropTargetV3(node.contentNode, this.treeController, node.tree.DndAcceptTypes, node);
}, unlistenNode:function (node) {
	if (this.dragSources[node.widgetId]) {
		dojo.dnd.dragManager.unregisterDragSource(this.dragSources[node.widgetId]);
		delete this.dragSources[node.widgetId];
	}
	if (this.dropTargets[node.widgetId]) {
		dojo.dnd.dragManager.unregisterDropTarget(this.dropTargets[node.widgetId]);
		delete this.dropTargets[node.widgetId];
	}
}});

