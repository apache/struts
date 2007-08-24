/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.TreeLoadingControllerV3");
dojo.require("dojo.widget.TreeBasicControllerV3");
dojo.require("dojo.event.*");
dojo.require("dojo.json");
dojo.require("dojo.io.*");
dojo.require("dojo.Deferred");
dojo.require("dojo.DeferredList");
dojo.declare("dojo.Error", Error, function (message, extra) {
	this.message = message;
	this.extra = extra;
	this.stack = (new Error()).stack;
});
dojo.declare("dojo.CommunicationError", dojo.Error, function () {
	this.name = "CommunicationError";
});
dojo.declare("dojo.LockedError", dojo.Error, function () {
	this.name = "LockedError";
});
dojo.declare("dojo.FormatError", dojo.Error, function () {
	this.name = "FormatError";
});
dojo.declare("dojo.RpcError", dojo.Error, function () {
	this.name = "RpcError";
});
dojo.widget.defineWidget("dojo.widget.TreeLoadingControllerV3", dojo.widget.TreeBasicControllerV3, {RpcUrl:"", RpcActionParam:"action", preventCache:true, checkValidRpcResponse:function (type, obj) {
	if (type != "load") {
		var extra = {};
		for (var i = 1; i < arguments.length; i++) {
			dojo.lang.mixin(extra, arguments[i]);
		}
		return new dojo.CommunicationError(obj, extra);
	}
	if (typeof obj != "object") {
		return new dojo.FormatError("Wrong server answer format " + (obj && obj.toSource ? obj.toSource() : obj) + " type " + (typeof obj), obj);
	}
	if (!dojo.lang.isUndefined(obj.error)) {
		return new dojo.RpcError(obj.error, obj);
	}
	return false;
}, getDeferredBindHandler:function (deferred) {
	return dojo.lang.hitch(this, function (type, obj) {
		var error = this.checkValidRpcResponse.apply(this, arguments);
		if (error) {
			deferred.errback(error);
			return;
		}
		deferred.callback(obj);
	});
}, getRpcUrl:function (action) {
	if (this.RpcUrl == "local") {
		var dir = document.location.href.substr(0, document.location.href.lastIndexOf("/"));
		var localUrl = dir + "/local/" + action;
		return localUrl;
	}
	if (!this.RpcUrl) {
		dojo.raise("Empty RpcUrl: can't load");
	}
	var url = this.RpcUrl;
	if (url.indexOf("/") != 0) {
		var protocol = document.location.href.replace(/:\/\/.*/, "");
		var prefix = document.location.href.substring(protocol.length + 3);
		if (prefix.lastIndexOf("/") != prefix.length - 1) {
			prefix = prefix.replace(/\/[^\/]+$/, "/");
		}
		if (prefix.lastIndexOf("/") != prefix.length - 1) {
			prefix = prefix + "/";
		}
		url = protocol + "://" + prefix + url;
	}
	return url + (url.indexOf("?") > -1 ? "&" : "?") + this.RpcActionParam + "=" + action;
}, loadProcessResponse:function (node, result) {
	if (!dojo.lang.isArray(result)) {
		throw new dojo.FormatError("loadProcessResponse: Not array loaded: " + result);
	}
	node.setChildren(result);
}, runRpc:function (kw) {
	var _this = this;
	var deferred = new dojo.Deferred();
	dojo.io.bind({url:kw.url, handle:this.getDeferredBindHandler(deferred), mimetype:"text/javascript", preventCache:this.preventCache, sync:kw.sync, content:{data:dojo.json.serialize(kw.params)}});
	return deferred;
}, loadRemote:function (node, sync) {
	var _this = this;
	var params = {node:this.getInfo(node), tree:this.getInfo(node.tree)};
	var deferred = this.runRpc({url:this.getRpcUrl("getChildren"), sync:sync, params:params});
	deferred.addCallback(function (res) {
		return _this.loadProcessResponse(node, res);
	});
	return deferred;
}, batchExpandTimeout:0, recurseToLevel:function (widget, level, callFunc, callObj, skipFirst, sync) {
	if (level == 0) {
		return;
	}
	if (!skipFirst) {
		var deferred = callFunc.call(callObj, widget, sync);
	} else {
		var deferred = dojo.Deferred.prototype.makeCalled();
	}
	var _this = this;
	var recurseOnExpand = function () {
		var children = widget.children;
		var deferreds = [];
		for (var i = 0; i < children.length; i++) {
			deferreds.push(_this.recurseToLevel(children[i], level - 1, callFunc, callObj, sync));
		}
		return new dojo.DeferredList(deferreds);
	};
	deferred.addCallback(recurseOnExpand);
	return deferred;
}, expandToLevel:function (nodeOrTree, level, sync) {
	return this.recurseToLevel(nodeOrTree, nodeOrTree.isTree ? level + 1 : level, this.expand, this, nodeOrTree.isTree, sync);
}, loadToLevel:function (nodeOrTree, level, sync) {
	return this.recurseToLevel(nodeOrTree, nodeOrTree.isTree ? level + 1 : level, this.loadIfNeeded, this, nodeOrTree.isTree, sync);
}, loadAll:function (nodeOrTree, sync) {
	return this.loadToLevel(nodeOrTree, Number.POSITIVE_INFINITY, sync);
}, expand:function (node, sync) {
	var _this = this;
	var deferred = this.startProcessing(node);
	deferred.addCallback(function () {
		return _this.loadIfNeeded(node, sync);
	});
	deferred.addCallback(function (res) {
		dojo.widget.TreeBasicControllerV3.prototype.expand(node);
		return res;
	});
	deferred.addBoth(function (res) {
		_this.finishProcessing(node);
		return res;
	});
	return deferred;
}, loadIfNeeded:function (node, sync) {
	var deferred;
	if (node.state == node.loadStates.UNCHECKED && node.isFolder && !node.children.length) {
		deferred = this.loadRemote(node, sync);
	} else {
		deferred = new dojo.Deferred();
		deferred.callback();
	}
	return deferred;
}, runStages:function (check, prepare, make, finalize, expose, args) {
	var _this = this;
	if (check && !check.apply(this, args)) {
		return false;
	}
	var deferred = dojo.Deferred.prototype.makeCalled();
	if (prepare) {
		deferred.addCallback(function () {
			return prepare.apply(_this, args);
		});
	}
	if (make) {
		deferred.addCallback(function () {
			var res = make.apply(_this, args);
			return res;
		});
	}
	if (finalize) {
		deferred.addBoth(function (res) {
			finalize.apply(_this, args);
			return res;
		});
	}
	if (expose) {
		deferred.addCallback(function (res) {
			expose.apply(_this, args);
			return res;
		});
	}
	return deferred;
}, startProcessing:function (nodesArray) {
	var deferred = new dojo.Deferred();
	var nodes = dojo.lang.isArray(nodesArray) ? nodesArray : arguments;
	for (var i = 0; i < nodes.length; i++) {
		if (nodes[i].isLocked()) {
			deferred.errback(new dojo.LockedError("item locked " + nodes[i], nodes[i]));
			return deferred;
		}
		if (nodes[i].isTreeNode) {
			nodes[i].markProcessing();
		}
		nodes[i].lock();
	}
	deferred.callback();
	return deferred;
}, finishProcessing:function (nodesArray) {
	var nodes = dojo.lang.isArray(nodesArray) ? nodesArray : arguments;
	for (var i = 0; i < nodes.length; i++) {
		if (!nodes[i].hasLock()) {
			continue;
		}
		nodes[i].unlock();
		if (nodes[i].isTreeNode) {
			nodes[i].unmarkProcessing();
		}
	}
}, refreshChildren:function (nodeOrTree, sync) {
	return this.runStages(null, this.prepareRefreshChildren, this.doRefreshChildren, this.finalizeRefreshChildren, this.exposeRefreshChildren, arguments);
}, prepareRefreshChildren:function (nodeOrTree, sync) {
	var deferred = this.startProcessing(nodeOrTree);
	nodeOrTree.destroyChildren();
	nodeOrTree.state = nodeOrTree.loadStates.UNCHECKED;
	return deferred;
}, doRefreshChildren:function (nodeOrTree, sync) {
	return this.loadRemote(nodeOrTree, sync);
}, finalizeRefreshChildren:function (nodeOrTree, sync) {
	this.finishProcessing(nodeOrTree);
}, exposeRefreshChildren:function (nodeOrTree, sync) {
	nodeOrTree.expand();
}, move:function (child, newParent, index) {
	return this.runStages(this.canMove, this.prepareMove, this.doMove, this.finalizeMove, this.exposeMove, arguments);
}, doMove:function (child, newParent, index) {
	child.tree.move(child, newParent, index);
	return true;
}, prepareMove:function (child, newParent, index, sync) {
	var deferred = this.startProcessing(newParent);
	deferred.addCallback(dojo.lang.hitch(this, function () {
		return this.loadIfNeeded(newParent, sync);
	}));
	return deferred;
}, finalizeMove:function (child, newParent) {
	this.finishProcessing(newParent);
}, prepareCreateChild:function (parent, index, data, sync) {
	var deferred = this.startProcessing(parent);
	deferred.addCallback(dojo.lang.hitch(this, function () {
		return this.loadIfNeeded(parent, sync);
	}));
	return deferred;
}, finalizeCreateChild:function (parent) {
	this.finishProcessing(parent);
}, prepareClone:function (child, newParent, index, deep, sync) {
	var deferred = this.startProcessing(child, newParent);
	deferred.addCallback(dojo.lang.hitch(this, function () {
		return this.loadIfNeeded(newParent, sync);
	}));
	return deferred;
}, finalizeClone:function (child, newParent) {
	this.finishProcessing(child, newParent);
}});

