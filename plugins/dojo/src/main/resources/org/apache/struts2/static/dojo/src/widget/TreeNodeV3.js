/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.TreeNodeV3");
dojo.require("dojo.html.*");
dojo.require("dojo.event.*");
dojo.require("dojo.io.*");
dojo.require("dojo.widget.TreeWithNode");
dojo.widget.defineWidget("dojo.widget.TreeNodeV3", [dojo.widget.HtmlWidget, dojo.widget.TreeWithNode], function () {
	this.actionsDisabled = [];
	this.object = {};
}, {tryLazyInit:true, actions:{MOVE:"MOVE", DETACH:"DETACH", EDIT:"EDIT", ADDCHILD:"ADDCHILD", SELECT:"SELECT"}, labelClass:"", contentClass:"", expandNode:null, labelNode:null, nodeDocType:"", selected:false, getnodeDocType:function () {
	return this.nodeDocType;
}, cloneProperties:["actionsDisabled", "tryLazyInit", "nodeDocType", "objectId", "object", "title", "isFolder", "isExpanded", "state"], clone:function (deep) {
	var ret = new this.constructor();
	for (var i = 0; i < this.cloneProperties.length; i++) {
		var prop = this.cloneProperties[i];
		ret[prop] = dojo.lang.shallowCopy(this[prop], true);
	}
	if (this.tree.unsetFolderOnEmpty && !deep && this.isFolder) {
		ret.isFolder = false;
	}
	ret.toggleObj = this.toggleObj;
	dojo.widget.manager.add(ret);
	ret.tree = this.tree;
	ret.buildRendering({}, {});
	ret.initialize({}, {});
	if (deep && this.children.length) {
		for (var i = 0; i < this.children.length; i++) {
			var child = this.children[i];
			if (child.clone) {
				ret.children.push(child.clone(deep));
			} else {
				ret.children.push(dojo.lang.shallowCopy(child, deep));
			}
		}
		ret.setChildren();
	}
	return ret;
}, markProcessing:function () {
	this.markProcessingSavedClass = dojo.html.getClass(this.expandNode);
	dojo.html.setClass(this.expandNode, this.tree.classPrefix + "ExpandLoading");
}, unmarkProcessing:function () {
	dojo.html.setClass(this.expandNode, this.markProcessingSavedClass);
}, buildRendering:function (args, fragment, parent) {
	if (args.tree) {
		this.tree = dojo.lang.isString(args.tree) ? dojo.widget.manager.getWidgetById(args.tree) : args.tree;
	} else {
		if (parent && parent.tree) {
			this.tree = parent.tree;
		}
	}
	if (!this.tree) {
		dojo.raise("Can't evaluate tree from arguments or parent");
	}
	this.domNode = this.tree.nodeTemplate.cloneNode(true);
	this.expandNode = this.domNode.firstChild;
	this.contentNode = this.domNode.childNodes[1];
	this.labelNode = this.contentNode.firstChild;
	if (this.labelClass) {
		dojo.html.addClass(this.labelNode, this.labelClass);
	}
	if (this.contentClass) {
		dojo.html.addClass(this.contentNode, this.contentClass);
	}
	this.domNode.widgetId = this.widgetId;
	this.labelNode.innerHTML = this.title;
}, isTreeNode:true, object:{}, title:"", isFolder:null, contentNode:null, expandClass:"", isExpanded:false, containerNode:null, getInfo:function () {
	var info = {widgetId:this.widgetId, objectId:this.objectId, index:this.getParentIndex()};
	return info;
}, setFolder:function () {
	this.isFolder = true;
	this.viewSetExpand();
	if (!this.containerNode) {
		this.viewAddContainer();
	}
	dojo.event.topic.publish(this.tree.eventNames.afterSetFolder, {source:this});
}, initialize:function (args, frag, parent) {
	if (args.isFolder) {
		this.isFolder = true;
	}
	if (this.children.length || this.isFolder) {
		this.setFolder();
	} else {
		this.viewSetExpand();
	}
	for (var i = 0; i < this.actionsDisabled.length; i++) {
		this.actionsDisabled[i] = this.actionsDisabled[i].toUpperCase();
	}
	dojo.event.topic.publish(this.tree.eventNames.afterChangeTree, {oldTree:null, newTree:this.tree, node:this});
}, unsetFolder:function () {
	this.isFolder = false;
	this.viewSetExpand();
	dojo.event.topic.publish(this.tree.eventNames.afterUnsetFolder, {source:this});
}, insertNode:function (parent, index) {
	if (!index) {
		index = 0;
	}
	if (index == 0) {
		dojo.html.prependChild(this.domNode, parent.containerNode);
	} else {
		dojo.html.insertAfter(this.domNode, parent.children[index - 1].domNode);
	}
}, updateTree:function (newTree) {
	if (this.tree === newTree) {
		return;
	}
	var oldTree = this.tree;
	dojo.lang.forEach(this.getDescendants(), function (elem) {
		elem.tree = newTree;
	});
	if (oldTree.classPrefix != newTree.classPrefix) {
		var stack = [this.domNode];
		var elem;
		var reg = new RegExp("(^|\\s)" + oldTree.classPrefix, "g");
		while (elem = stack.pop()) {
			for (var i = 0; i < elem.childNodes.length; i++) {
				var childNode = elem.childNodes[i];
				if (childNode.nodeDocType != 1) {
					continue;
				}
				dojo.html.setClass(childNode, dojo.html.getClass(childNode).replace(reg, "$1" + newTree.classPrefix));
				stack.push(childNode);
			}
		}
	}
	var message = {oldTree:oldTree, newTree:newTree, node:this};
	dojo.event.topic.publish(this.tree.eventNames.afterChangeTree, message);
	dojo.event.topic.publish(newTree.eventNames.afterChangeTree, message);
}, addedTo:function (parent, index, dontPublishEvent) {
	if (this.tree !== parent.tree) {
		this.updateTree(parent.tree);
	}
	if (parent.isTreeNode) {
		if (!parent.isFolder) {
			parent.setFolder();
			parent.state = parent.loadStates.LOADED;
		}
	}
	var siblingsCount = parent.children.length;
	this.insertNode(parent, index);
	this.viewAddLayout();
	if (siblingsCount > 1) {
		if (index == 0 && parent.children[1] instanceof dojo.widget.Widget) {
			parent.children[1].viewUpdateLayout();
		}
		if (index == siblingsCount - 1 && parent.children[siblingsCount - 2] instanceof dojo.widget.Widget) {
			parent.children[siblingsCount - 2].viewUpdateLayout();
		}
	} else {
		if (parent.isTreeNode) {
			parent.viewSetHasChildren();
		}
	}
	if (!dontPublishEvent) {
		var message = {child:this, index:index, parent:parent};
		dojo.event.topic.publish(this.tree.eventNames.afterAddChild, message);
	}
}, createSimple:function (args, parent) {
	if (args.tree) {
		var tree = args.tree;
	} else {
		if (parent) {
			var tree = parent.tree;
		} else {
			dojo.raise("createSimple: can't evaluate tree");
		}
	}
	tree = dojo.widget.byId(tree);
	var treeNode = new tree.defaultChildWidget();
	for (var x in args) {
		treeNode[x] = args[x];
	}
	treeNode.toggleObj = dojo.lfx.toggle[treeNode.toggle.toLowerCase()] || dojo.lfx.toggle.plain;
	dojo.widget.manager.add(treeNode);
	treeNode.buildRendering(args, {}, parent);
	treeNode.initialize(args, {}, parent);
	if (treeNode.parent) {
		delete dojo.widget.manager.topWidgets[treeNode.widgetId];
	}
	return treeNode;
}, viewUpdateLayout:function () {
	this.viewRemoveLayout();
	this.viewAddLayout();
}, viewAddContainer:function () {
	this.containerNode = this.tree.containerNodeTemplate.cloneNode(true);
	this.domNode.appendChild(this.containerNode);
}, viewAddLayout:function () {
	if (this.parent["isTree"]) {
		dojo.html.setClass(this.domNode, dojo.html.getClass(this.domNode) + " " + this.tree.classPrefix + "IsRoot");
	}
	if (this.isLastChild()) {
		dojo.html.setClass(this.domNode, dojo.html.getClass(this.domNode) + " " + this.tree.classPrefix + "IsLast");
	}
}, viewRemoveLayout:function () {
	dojo.html.removeClass(this.domNode, this.tree.classPrefix + "IsRoot");
	dojo.html.removeClass(this.domNode, this.tree.classPrefix + "IsLast");
}, viewGetExpandClass:function () {
	if (this.isFolder) {
		return this.isExpanded ? "ExpandOpen" : "ExpandClosed";
	} else {
		return "ExpandLeaf";
	}
}, viewSetExpand:function () {
	var expand = this.tree.classPrefix + this.viewGetExpandClass();
	var reg = new RegExp("(^|\\s)" + this.tree.classPrefix + "Expand\\w+", "g");
	dojo.html.setClass(this.domNode, dojo.html.getClass(this.domNode).replace(reg, "") + " " + expand);
	this.viewSetHasChildrenAndExpand();
}, viewGetChildrenClass:function () {
	return "Children" + (this.children.length ? "Yes" : "No");
}, viewSetHasChildren:function () {
	var clazz = this.tree.classPrefix + this.viewGetChildrenClass();
	var reg = new RegExp("(^|\\s)" + this.tree.classPrefix + "Children\\w+", "g");
	dojo.html.setClass(this.domNode, dojo.html.getClass(this.domNode).replace(reg, "") + " " + clazz);
	this.viewSetHasChildrenAndExpand();
}, viewSetHasChildrenAndExpand:function () {
	var clazz = this.tree.classPrefix + "State" + this.viewGetChildrenClass() + "-" + this.viewGetExpandClass();
	var reg = new RegExp("(^|\\s)" + this.tree.classPrefix + "State[\\w-]+", "g");
	dojo.html.setClass(this.domNode, dojo.html.getClass(this.domNode).replace(reg, "") + " " + clazz);
}, viewUnfocus:function () {
	dojo.html.removeClass(this.labelNode, this.tree.classPrefix + "LabelFocused");
}, viewFocus:function () {
	dojo.html.addClass(this.labelNode, this.tree.classPrefix + "LabelFocused");
}, viewEmphasize:function () {
	dojo.html.clearSelection(this.labelNode);
	dojo.html.addClass(this.labelNode, this.tree.classPrefix + "NodeEmphasized");
}, viewUnemphasize:function () {
	dojo.html.removeClass(this.labelNode, this.tree.classPrefix + "NodeEmphasized");
}, detach:function () {
	if (!this.parent) {
		return;
	}
	var parent = this.parent;
	var index = this.getParentIndex();
	this.doDetach.apply(this, arguments);
	dojo.event.topic.publish(this.tree.eventNames.afterDetach, {child:this, parent:parent, index:index});
}, doDetach:function () {
	var parent = this.parent;
	if (!parent) {
		return;
	}
	var index = this.getParentIndex();
	this.viewRemoveLayout();
	dojo.widget.DomWidget.prototype.removeChild.call(parent, this);
	var siblingsCount = parent.children.length;
	if (siblingsCount > 0) {
		if (index == 0) {
			parent.children[0].viewUpdateLayout();
		}
		if (index == siblingsCount) {
			parent.children[siblingsCount - 1].viewUpdateLayout();
		}
	} else {
		if (parent.isTreeNode) {
			parent.viewSetHasChildren();
		}
	}
	if (this.tree.unsetFolderOnEmpty && !parent.children.length && parent.isTreeNode) {
		parent.unsetFolder();
	}
	this.parent = null;
}, destroy:function () {
	dojo.event.topic.publish(this.tree.eventNames.beforeNodeDestroy, {source:this});
	this.detach();
	return dojo.widget.HtmlWidget.prototype.destroy.apply(this, arguments);
}, expand:function () {
	if (this.isExpanded) {
		return;
	}
	if (this.tryLazyInit) {
		this.setChildren();
		this.tryLazyInit = false;
	}
	this.isExpanded = true;
	this.viewSetExpand();
	this.showChildren();
}, collapse:function () {
	if (!this.isExpanded) {
		return;
	}
	this.isExpanded = false;
	this.hideChildren();
}, hideChildren:function () {
	this.tree.toggleObj.hide(this.containerNode, this.tree.toggleDuration, this.explodeSrc, dojo.lang.hitch(this, "onHideChildren"));
}, showChildren:function () {
	this.tree.toggleObj.show(this.containerNode, this.tree.toggleDuration, this.explodeSrc, dojo.lang.hitch(this, "onShowChildren"));
}, onShowChildren:function () {
	this.onShow();
	dojo.event.topic.publish(this.tree.eventNames.afterExpand, {source:this});
}, onHideChildren:function () {
	this.viewSetExpand();
	this.onHide();
	dojo.event.topic.publish(this.tree.eventNames.afterCollapse, {source:this});
}, setTitle:function (title) {
	var oldTitle = this.title;
	this.labelNode.innerHTML = this.title = title;
	dojo.event.topic.publish(this.tree.eventNames.afterSetTitle, {source:this, oldTitle:oldTitle});
}, toString:function () {
	return "[" + this.widgetType + ", " + this.title + "]";
}});

