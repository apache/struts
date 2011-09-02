/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.TreeSelectorV3");
dojo.require("dojo.widget.HtmlWidget");
dojo.require("dojo.widget.TreeCommon");
dojo.widget.defineWidget("dojo.widget.TreeSelectorV3", [dojo.widget.HtmlWidget, dojo.widget.TreeCommon], function () {
	this.eventNames = {};
	this.listenedTrees = {};
	this.selectedNodes = [];
	this.lastClicked = {};
}, {listenTreeEvents:["afterTreeCreate", "afterCollapse", "afterChangeTree", "afterDetach", "beforeTreeDestroy"], listenNodeFilter:function (elem) {
	return elem instanceof dojo.widget.Widget;
}, allowedMulti:true, dblselectTimeout:300, eventNamesDefault:{select:"select", deselect:"deselect", dblselect:"dblselect"}, onAfterTreeCreate:function (message) {
	var tree = message.source;
	dojo.event.browser.addListener(tree.domNode, "onclick", dojo.lang.hitch(this, this.onTreeClick));
	if (dojo.render.html.ie) {
		dojo.event.browser.addListener(tree.domNode, "ondblclick", dojo.lang.hitch(this, this.onTreeDblClick));
	}
	dojo.event.browser.addListener(tree.domNode, "onKey", dojo.lang.hitch(this, this.onKey));
}, onKey:function (e) {
	if (!e.key || e.ctrkKey || e.altKey) {
		return;
	}
	switch (e.key) {
	  case e.KEY_ENTER:
		var node = this.domElement2TreeNode(e.target);
		if (node) {
			this.processNode(node, e);
		}
	}
}, onAfterChangeTree:function (message) {
	if (!message.oldTree && message.node.selected) {
		this.select(message.node);
	}
	if (!message.newTree || !this.listenedTrees[message.newTree.widgetId]) {
		if (this.selectedNode && message.node.children) {
			this.deselectIfAncestorMatch(message.node);
		}
	}
}, initialize:function (args) {
	for (var name in this.eventNamesDefault) {
		if (dojo.lang.isUndefined(this.eventNames[name])) {
			this.eventNames[name] = this.widgetId + "/" + this.eventNamesDefault[name];
		}
	}
}, onBeforeTreeDestroy:function (message) {
	this.unlistenTree(message.source);
}, onAfterCollapse:function (message) {
	this.deselectIfAncestorMatch(message.source);
}, onTreeDblClick:function (event) {
	this.onTreeClick(event);
}, checkSpecialEvent:function (event) {
	return event.shiftKey || event.ctrlKey;
}, onTreeClick:function (event) {
	var node = this.domElement2TreeNode(event.target);
	if (!node) {
		return;
	}
	var checkLabelClick = function (domElement) {
		return domElement === node.labelNode;
	};
	if (this.checkPathCondition(event.target, checkLabelClick)) {
		this.processNode(node, event);
	}
}, processNode:function (node, event) {
	if (node.actionIsDisabled(node.actions.SELECT)) {
		return;
	}
	if (dojo.lang.inArray(this.selectedNodes, node)) {
		if (this.checkSpecialEvent(event)) {
			this.deselect(node);
			return;
		}
		var _this = this;
		var i = 0;
		var selectedNode;
		while (this.selectedNodes.length > i) {
			selectedNode = this.selectedNodes[i];
			if (selectedNode !== node) {
				this.deselect(selectedNode);
				continue;
			}
			i++;
		}
		var wasJustClicked = this.checkRecentClick(node);
		eventName = wasJustClicked ? this.eventNames.dblselect : this.eventNames.select;
		if (wasJustClicked) {
			eventName = this.eventNames.dblselect;
			this.forgetLastClicked();
		} else {
			eventName = this.eventNames.select;
			this.setLastClicked(node);
		}
		dojo.event.topic.publish(eventName, {node:node});
		return;
	}
	this.deselectIfNoMulti(event);
	this.setLastClicked(node);
	this.select(node);
}, forgetLastClicked:function () {
	this.lastClicked = {};
}, setLastClicked:function (node) {
	this.lastClicked.date = new Date();
	this.lastClicked.node = node;
}, checkRecentClick:function (node) {
	var diff = new Date() - this.lastClicked.date;
	if (this.lastClicked.node && diff < this.dblselectTimeout) {
		return true;
	} else {
		return false;
	}
}, deselectIfNoMulti:function (event) {
	if (!this.checkSpecialEvent(event) || !this.allowedMulti) {
		this.deselectAll();
	}
}, deselectIfAncestorMatch:function (ancestor) {
	var _this = this;
	dojo.lang.forEach(this.selectedNodes, function (node) {
		var selectedNode = node;
		node = node.parent;
		while (node && node.isTreeNode) {
			if (node === ancestor) {
				_this.deselect(selectedNode);
				return;
			}
			node = node.parent;
		}
	});
}, onAfterDetach:function (message) {
	this.deselectIfAncestorMatch(message.child);
}, select:function (node) {
	var index = dojo.lang.find(this.selectedNodes, node, true);
	if (index >= 0) {
		return;
	}
	this.selectedNodes.push(node);
	dojo.event.topic.publish(this.eventNames.select, {node:node});
}, deselect:function (node) {
	var index = dojo.lang.find(this.selectedNodes, node, true);
	if (index < 0) {
		return;
	}
	this.selectedNodes.splice(index, 1);
	dojo.event.topic.publish(this.eventNames.deselect, {node:node});
}, deselectAll:function () {
	while (this.selectedNodes.length) {
		this.deselect(this.selectedNodes[0]);
	}
}});

