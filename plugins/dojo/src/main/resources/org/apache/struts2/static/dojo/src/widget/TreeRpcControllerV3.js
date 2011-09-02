/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.TreeRpcControllerV3");
dojo.require("dojo.event.*");
dojo.require("dojo.json");
dojo.require("dojo.io.*");
dojo.require("dojo.widget.TreeLoadingControllerV3");
dojo.widget.defineWidget("dojo.widget.TreeRpcControllerV3", dojo.widget.TreeLoadingControllerV3, {extraRpcOnEdit:false, doMove:function (child, newParent, index, sync) {
	var params = {child:this.getInfo(child), childTree:this.getInfo(child.tree), oldParent:this.getInfo(child.parent), oldParentTree:this.getInfo(child.parent.tree), newParent:this.getInfo(newParent), newParentTree:this.getInfo(newParent.tree), newIndex:index};
	var deferred = this.runRpc({url:this.getRpcUrl("move"), sync:sync, params:params});
	var _this = this;
	var args = arguments;
	deferred.addCallback(function () {
		dojo.widget.TreeBasicControllerV3.prototype.doMove.apply(_this, args);
	});
	return deferred;
}, prepareDetach:function (node, sync) {
	var deferred = this.startProcessing(node);
	return deferred;
}, finalizeDetach:function (node) {
	this.finishProcessing(node);
}, doDetach:function (node, sync) {
	var params = {node:this.getInfo(node), tree:this.getInfo(node.tree)};
	var deferred = this.runRpc({url:this.getRpcUrl("detach"), sync:sync, params:params});
	var _this = this;
	var args = arguments;
	deferred.addCallback(function () {
		dojo.widget.TreeBasicControllerV3.prototype.doDetach.apply(_this, args);
	});
	return deferred;
}, requestEditConfirmation:function (node, action, sync) {
	if (!this.extraRpcOnEdit) {
		return dojo.Deferred.prototype.makeCalled();
	}
	var _this = this;
	var deferred = this.startProcessing(node);
	var params = {node:this.getInfo(node), tree:this.getInfo(node.tree)};
	deferred.addCallback(function () {
		return _this.runRpc({url:_this.getRpcUrl(action), sync:sync, params:params});
	});
	deferred.addBoth(function (r) {
		_this.finishProcessing(node);
		return r;
	});
	return deferred;
}, editLabelSave:function (node, newContent, sync) {
	var deferred = this.startProcessing(node);
	var _this = this;
	var params = {node:this.getInfo(node), tree:this.getInfo(node.tree), newContent:newContent};
	deferred.addCallback(function () {
		return _this.runRpc({url:_this.getRpcUrl("editLabelSave"), sync:sync, params:params});
	});
	deferred.addBoth(function (r) {
		_this.finishProcessing(node);
		return r;
	});
	return deferred;
}, editLabelStart:function (node, sync) {
	if (!this.canEditLabel(node)) {
		return false;
	}
	var _this = this;
	if (!this.editor.isClosed()) {
		var deferred = this.editLabelFinish(this.editor.saveOnBlur, sync);
		deferred.addCallback(function () {
			return _this.editLabelStart(node, sync);
		});
		return deferred;
	}
	var deferred = this.requestEditConfirmation(node, "editLabelStart", sync);
	deferred.addCallback(function () {
		_this.doEditLabelStart(node);
	});
	return deferred;
}, editLabelFinish:function (save, sync) {
	var _this = this;
	var node = this.editor.node;
	var deferred = dojo.Deferred.prototype.makeCalled();
	if (!save && !node.isPhantom) {
		deferred = this.requestEditConfirmation(this.editor.node, "editLabelFinishCancel", sync);
	}
	if (save) {
		if (node.isPhantom) {
			deferred = this.sendCreateChildRequest(node.parent, node.getParentIndex(), {title:this.editor.getContents()}, sync);
		} else {
			deferred = this.editLabelSave(node, this.editor.getContents(), sync);
		}
	}
	deferred.addCallback(function (server_data) {
		_this.doEditLabelFinish(save, server_data);
	});
	deferred.addErrback(function (r) {
		_this.doEditLabelFinish(false);
		return false;
	});
	return deferred;
}, createAndEdit:function (parent, index, sync) {
	var data = {title:parent.tree.defaultChildTitle};
	if (!this.canCreateChild(parent, index, data)) {
		return false;
	}
	if (!this.editor.isClosed()) {
		var deferred = this.editLabelFinish(this.editor.saveOnBlur, sync);
		deferred.addCallback(function () {
			return _this.createAndEdit(parent, index, sync);
		});
		return deferred;
	}
	var _this = this;
	var deferred = this.prepareCreateChild(parent, index, data, sync);
	deferred.addCallback(function () {
		var child = _this.makeDefaultNode(parent, index);
		child.isPhantom = true;
		return child;
	});
	deferred.addBoth(function (r) {
		_this.finalizeCreateChild(parent, index, data, sync);
		return r;
	});
	deferred.addCallback(function (child) {
		var d = _this.exposeCreateChild(parent, index, data, sync);
		d.addCallback(function () {
			return child;
		});
		return d;
	});
	deferred.addCallback(function (child) {
		_this.doEditLabelStart(child);
		return child;
	});
	return deferred;
}, prepareDestroyChild:function (node, sync) {
	var deferred = this.startProcessing(node);
	return deferred;
}, finalizeDestroyChild:function (node) {
	this.finishProcessing(node);
}, doDestroyChild:function (node, sync) {
	var params = {node:this.getInfo(node), tree:this.getInfo(node.tree)};
	var deferred = this.runRpc({url:this.getRpcUrl("destroyChild"), sync:sync, params:params});
	var _this = this;
	var args = arguments;
	deferred.addCallback(function () {
		dojo.widget.TreeBasicControllerV3.prototype.doDestroyChild.apply(_this, args);
	});
	return deferred;
}, sendCreateChildRequest:function (parent, index, data, sync) {
	var params = {tree:this.getInfo(parent.tree), parent:this.getInfo(parent), index:index, data:data};
	var deferred = this.runRpc({url:this.getRpcUrl("createChild"), sync:sync, params:params});
	return deferred;
}, doCreateChild:function (parent, index, data, sync) {
	if (dojo.lang.isUndefined(data.title)) {
		data.title = parent.tree.defaultChildTitle;
	}
	var deferred = this.sendCreateChildRequest(parent, index, data, sync);
	var _this = this;
	var args = arguments;
	deferred.addCallback(function (server_data) {
		dojo.lang.mixin(data, server_data);
		return dojo.widget.TreeBasicControllerV3.prototype.doCreateChild.call(_this, parent, index, data);
	});
	return deferred;
}, doClone:function (child, newParent, index, deep, sync) {
	var params = {child:this.getInfo(child), oldParent:this.getInfo(child.parent), oldParentTree:this.getInfo(child.parent.tree), newParent:this.getInfo(newParent), newParentTree:this.getInfo(newParent.tree), index:index, deep:deep ? true : false, tree:this.getInfo(child.tree)};
	var deferred = this.runRpc({url:this.getRpcUrl("clone"), sync:sync, params:params});
	var _this = this;
	var args = arguments;
	deferred.addCallback(function () {
		dojo.widget.TreeBasicControllerV3.prototype.doClone.apply(_this, args);
	});
	return deferred;
}});

