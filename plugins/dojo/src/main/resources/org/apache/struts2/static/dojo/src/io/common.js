/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.io.common");
dojo.require("dojo.string");
dojo.require("dojo.lang.extras");
dojo.io.transports = [];
dojo.io.hdlrFuncNames = ["load", "error", "timeout"];
dojo.io.Request = function (url, mimetype, transport, changeUrl) {
	if ((arguments.length == 1) && (arguments[0].constructor == Object)) {
		this.fromKwArgs(arguments[0]);
	} else {
		this.url = url;
		if (mimetype) {
			this.mimetype = mimetype;
		}
		if (transport) {
			this.transport = transport;
		}
		if (arguments.length >= 4) {
			this.changeUrl = changeUrl;
		}
	}
};
dojo.lang.extend(dojo.io.Request, {url:"", mimetype:"text/plain", method:"GET", content:undefined, transport:undefined, changeUrl:undefined, formNode:undefined, sync:false, bindSuccess:false, useCache:false, preventCache:false, jsonFilter:function (value) {
	if ((this.mimetype == "text/json-comment-filtered") || (this.mimetype == "application/json-comment-filtered")) {
		var cStartIdx = value.indexOf("/*");
		var cEndIdx = value.lastIndexOf("*/");
		if ((cStartIdx == -1) || (cEndIdx == -1)) {
			dojo.debug("your JSON wasn't comment filtered!");
			return "";
		}
		return value.substring(cStartIdx + 2, cEndIdx);
	}
	dojo.debug("please consider using a mimetype of text/json-comment-filtered to avoid potential security issues with JSON endpoints");
	return value;
}, load:function (type, data, transportImplementation, kwArgs) {
}, error:function (type, error, transportImplementation, kwArgs) {
}, timeout:function (type, empty, transportImplementation, kwArgs) {
}, handle:function (type, data, transportImplementation, kwArgs) {
}, timeoutSeconds:0, abort:function () {
}, fromKwArgs:function (kwArgs) {
	if (kwArgs["url"]) {
		kwArgs.url = kwArgs.url.toString();
	}
	if (kwArgs["formNode"]) {
		kwArgs.formNode = dojo.byId(kwArgs.formNode);
	}
	if (!kwArgs["method"] && kwArgs["formNode"] && kwArgs["formNode"].method) {
		kwArgs.method = kwArgs["formNode"].method;
	}
	if (!kwArgs["handle"] && kwArgs["handler"]) {
		kwArgs.handle = kwArgs.handler;
	}
	if (!kwArgs["load"] && kwArgs["loaded"]) {
		kwArgs.load = kwArgs.loaded;
	}
	if (!kwArgs["changeUrl"] && kwArgs["changeURL"]) {
		kwArgs.changeUrl = kwArgs.changeURL;
	}
	kwArgs.encoding = dojo.lang.firstValued(kwArgs["encoding"], djConfig["bindEncoding"], "");
	kwArgs.sendTransport = dojo.lang.firstValued(kwArgs["sendTransport"], djConfig["ioSendTransport"], false);
	var isFunction = dojo.lang.isFunction;
	for (var x = 0; x < dojo.io.hdlrFuncNames.length; x++) {
		var fn = dojo.io.hdlrFuncNames[x];
		if (kwArgs[fn] && isFunction(kwArgs[fn])) {
			continue;
		}
		if (kwArgs["handle"] && isFunction(kwArgs["handle"])) {
			kwArgs[fn] = kwArgs.handle;
		}
	}
	dojo.lang.mixin(this, kwArgs);
}});
dojo.io.Error = function (msg, type, num) {
	this.message = msg;
	this.type = type || "unknown";
	this.number = num || 0;
};
dojo.io.transports.addTransport = function (name) {
	this.push(name);
	this[name] = dojo.io[name];
};
dojo.io.bind = function (request) {
	if (!(request instanceof dojo.io.Request)) {
		try {
			request = new dojo.io.Request(request);
		}
		catch (e) {
			dojo.debug(e);
		}
	}
	var tsName = "";
	if (request["transport"]) {
		tsName = request["transport"];
		if (!this[tsName]) {
			dojo.io.sendBindError(request, "No dojo.io.bind() transport with name '" + request["transport"] + "'.");
			return request;
		}
		if (!this[tsName].canHandle(request)) {
			dojo.io.sendBindError(request, "dojo.io.bind() transport with name '" + request["transport"] + "' cannot handle this type of request.");
			return request;
		}
	} else {
		for (var x = 0; x < dojo.io.transports.length; x++) {
			var tmp = dojo.io.transports[x];
			if ((this[tmp]) && (this[tmp].canHandle(request))) {
				tsName = tmp;
				break;
			}
		}
		if (tsName == "") {
			dojo.io.sendBindError(request, "None of the loaded transports for dojo.io.bind()" + " can handle the request.");
			return request;
		}
	}
	this[tsName].bind(request);
	request.bindSuccess = true;
	return request;
};
dojo.io.sendBindError = function (request, message) {
	if ((typeof request.error == "function" || typeof request.handle == "function") && (typeof setTimeout == "function" || typeof setTimeout == "object")) {
		var errorObject = new dojo.io.Error(message);
		setTimeout(function () {
			request[(typeof request.error == "function") ? "error" : "handle"]("error", errorObject, null, request);
		}, 50);
	} else {
		dojo.raise(message);
	}
};
dojo.io.queueBind = function (request) {
	if (!(request instanceof dojo.io.Request)) {
		try {
			request = new dojo.io.Request(request);
		}
		catch (e) {
			dojo.debug(e);
		}
	}
	var oldLoad = request.load;
	request.load = function () {
		dojo.io._queueBindInFlight = false;
		var ret = oldLoad.apply(this, arguments);
		dojo.io._dispatchNextQueueBind();
		return ret;
	};
	var oldErr = request.error;
	request.error = function () {
		dojo.io._queueBindInFlight = false;
		var ret = oldErr.apply(this, arguments);
		dojo.io._dispatchNextQueueBind();
		return ret;
	};
	dojo.io._bindQueue.push(request);
	dojo.io._dispatchNextQueueBind();
	return request;
};
dojo.io._dispatchNextQueueBind = function () {
	if (!dojo.io._queueBindInFlight) {
		dojo.io._queueBindInFlight = true;
		if (dojo.io._bindQueue.length > 0) {
			dojo.io.bind(dojo.io._bindQueue.shift());
		} else {
			dojo.io._queueBindInFlight = false;
		}
	}
};
dojo.io._bindQueue = [];
dojo.io._queueBindInFlight = false;
dojo.io.argsFromMap = function (map, encoding, last) {
	var enc = /utf/i.test(encoding || "") ? encodeURIComponent : dojo.string.encodeAscii;
	var mapped = [];
	var control = new Object();
	for (var name in map) {
		var domap = function (elt) {
			var val = enc(name) + "=" + enc(elt);
			mapped[(last == name) ? "push" : "unshift"](val);
		};
		if (!control[name]) {
			var value = map[name];
			if (dojo.lang.isArray(value)) {
				dojo.lang.forEach(value, domap);
			} else {
				domap(value);
			}
		}
	}
	return mapped.join("&");
};
dojo.io.setIFrameSrc = function (iframe, src, replace) {
	try {
		var r = dojo.render.html;
		if (!replace) {
			if (r.safari) {
				iframe.location = src;
			} else {
				frames[iframe.name].location = src;
			}
		} else {
			var idoc;
			if (r.ie) {
				idoc = iframe.contentWindow.document;
			} else {
				if (r.safari) {
					idoc = iframe.document;
				} else {
					idoc = iframe.contentWindow;
				}
			}
			if (!idoc) {
				iframe.location = src;
				return;
			} else {
				idoc.location.replace(src);
			}
		}
	}
	catch (e) {
		dojo.debug(e);
		dojo.debug("setIFrameSrc: " + e);
	}
};

