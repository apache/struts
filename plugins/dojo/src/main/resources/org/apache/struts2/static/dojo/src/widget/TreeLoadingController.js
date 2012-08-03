/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.TreeLoadingController");
dojo.require("dojo.widget.TreeBasicController");
dojo.require("dojo.event.*");
dojo.require("dojo.json");
dojo.require("dojo.io.*");
dojo.widget.defineWidget("dojo.widget.TreeLoadingController", dojo.widget.TreeBasicController, {RPCUrl:"", RPCActionParam:"action", RPCErrorHandler:function (type, obj, evt) {
	alert("RPC Error: " + (obj.message || "no message"));
}, preventCache:true, getRPCUrl:function (action) {
	if (this.RPCUrl == "local") {
		var dir = document.location.href.substr(0, document.location.href.lastIndexOf("/"));
		var localUrl = dir + "/" + action;
		return localUrl;
	}
	if (!this.RPCUrl) {
		dojo.raise("Empty RPCUrl: can't load");
	}
	return this.RPCUrl + (this.RPCUrl.indexOf("?") > -1 ? "&" : "?") + this.RPCActionParam + "=" + action;
}, loadProcessResponse:function (node, result, callObj, callFunc) {
	if (!dojo.lang.isUndefined(result.error)) {
		this.RPCErrorHandler("server", result.error);
		return false;
	}
	var newChildren = result;
	if (!dojo.lang.isArray(newChildren)) {
		dojo.raise("loadProcessResponse: Not array loaded: " + newChildren);
	}
	for (var i = 0; i < newChildren.length; i++) {
		newChildren[i] = dojo.widget.createWidget(node.widgetType, newChildren[i]);
		node.addChild(newChildren[i]);
	}
	node.state = node.loadStates.LOADED;
	if (dojo.lang.isFunction(callFunc)) {
		callFunc.apply(dojo.lang.isUndefined(callObj) ? this : callObj, [node, newChildren]);
	}
}, getInfo:function (obj) {
	return obj.getInfo();
}, runRPC:function (kw) {
	var _this = this;
	var handle = function (type, data, evt) {
		if (kw.lock) {
			dojo.lang.forEach(kw.lock, function (t) {
				t.unlock();
			});
		}
		if (type == "load") {
			kw.load.call(this, data);
		} else {
			this.RPCErrorHandler(type, data, evt);
		}
	};
	if (kw.lock) {
		dojo.lang.forEach(kw.lock, function (t) {
			t.lock();
		});
	}
	dojo.io.bind({url:kw.url, handle:dojo.lang.hitch(this, handle), mimetype:"text/json", preventCache:_this.preventCache, sync:kw.sync, content:{data:dojo.json.serialize(kw.params)}});
}, loadRemote:function (node, sync, callObj, callFunc) {
	var _this = this;
	var params = {node:this.getInfo(node), tree:this.getInfo(node.tree)};
	this.runRPC({url:this.getRPCUrl("getChildren"), load:function (result) {
		_this.loadProcessResponse(node, result, callObj, callFunc);
	}, sync:sync, lock:[node], params:params});
}, expand:function (node, sync, callObj, callFunc) {
	if (node.state == node.loadStates.UNCHECKED && node.isFolder) {
		this.loadRemote(node, sync, this, function (node, newChildren) {
			this.expand(node, sync, callObj, callFunc);
		});
		return;
	}
	dojo.widget.TreeBasicController.prototype.expand.apply(this, arguments);
}, doMove:function (child, newParent, index) {
	if (newParent.isTreeNode && newParent.state == newParent.loadStates.UNCHECKED) {
		this.loadRemote(newParent, true);
	}
	return dojo.widget.TreeBasicController.prototype.doMove.apply(this, arguments);
}, doCreateChild:function (parent, index, data, callObj, callFunc) {
	if (parent.state == parent.loadStates.UNCHECKED) {
		this.loadRemote(parent, true);
	}
	return dojo.widget.TreeBasicController.prototype.doCreateChild.apply(this, arguments);
}});

