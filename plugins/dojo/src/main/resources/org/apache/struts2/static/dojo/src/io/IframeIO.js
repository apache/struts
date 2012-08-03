/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.io.IframeIO");
dojo.require("dojo.io.BrowserIO");
dojo.require("dojo.uri.*");
dojo.io.createIFrame = function (fname, onloadstr, uri) {
	if (window[fname]) {
		return window[fname];
	}
	if (window.frames[fname]) {
		return window.frames[fname];
	}
	var r = dojo.render.html;
	var cframe = null;
	var turi = uri;
	if (!turi) {
		if (djConfig["useXDomain"] && !djConfig["dojoIframeHistoryUrl"]) {
			dojo.debug("dojo.io.createIFrame: When using cross-domain Dojo builds," + " please save iframe_history.html to your domain and set djConfig.dojoIframeHistoryUrl" + " to the path on your domain to iframe_history.html");
		}
		turi = (djConfig["dojoIframeHistoryUrl"] || dojo.uri.moduleUri("dojo", "../iframe_history.html")) + "#noInit=true";
	}
	var ifrstr = ((r.ie) && (dojo.render.os.win)) ? "<iframe name=\"" + fname + "\" src=\"" + turi + "\" onload=\"" + onloadstr + "\">" : "iframe";
	cframe = document.createElement(ifrstr);
	with (cframe) {
		name = fname;
		setAttribute("name", fname);
		id = fname;
	}
	dojo.body().appendChild(cframe);
	window[fname] = cframe;
	with (cframe.style) {
		if (!r.safari) {
			position = "absolute";
		}
		left = top = "0px";
		height = width = "1px";
		visibility = "hidden";
	}
	if (!r.ie) {
		dojo.io.setIFrameSrc(cframe, turi, true);
		cframe.onload = new Function(onloadstr);
	}
	return cframe;
};
dojo.io.IframeTransport = new function () {
	var _this = this;
	this.currentRequest = null;
	this.requestQueue = [];
	this.iframeName = "dojoIoIframe";
	this.fireNextRequest = function () {
		try {
			if ((this.currentRequest) || (this.requestQueue.length == 0)) {
				return;
			}
			var cr = this.currentRequest = this.requestQueue.shift();
			cr._contentToClean = [];
			var fn = cr["formNode"];
			var content = cr["content"] || {};
			if (cr.sendTransport) {
				content["dojo.transport"] = "iframe";
			}
			if (fn) {
				if (content) {
					for (var x in content) {
						if (!fn[x]) {
							var tn;
							if (dojo.render.html.ie) {
								tn = document.createElement("<input type='hidden' name='" + x + "' value='" + content[x] + "'>");
								fn.appendChild(tn);
							} else {
								tn = document.createElement("input");
								fn.appendChild(tn);
								tn.type = "hidden";
								tn.name = x;
								tn.value = content[x];
							}
							cr._contentToClean.push(x);
						} else {
							fn[x].value = content[x];
						}
					}
				}
				if (cr["url"]) {
					cr._originalAction = fn.getAttribute("action");
					fn.setAttribute("action", cr.url);
				}
				if (!fn.getAttribute("method")) {
					fn.setAttribute("method", (cr["method"]) ? cr["method"] : "post");
				}
				cr._originalTarget = fn.getAttribute("target");
				fn.setAttribute("target", this.iframeName);
				fn.target = this.iframeName;
				fn.submit();
			} else {
				var query = dojo.io.argsFromMap(this.currentRequest.content);
				var tmpUrl = cr.url + (cr.url.indexOf("?") > -1 ? "&" : "?") + query;
				dojo.io.setIFrameSrc(this.iframe, tmpUrl, true);
			}
		}
		catch (e) {
			this.iframeOnload(e);
		}
	};
	this.canHandle = function (kwArgs) {
		return ((dojo.lang.inArray(["text/plain", "text/html", "text/javascript", "text/json", "application/json"], kwArgs["mimetype"])) && (dojo.lang.inArray(["post", "get"], kwArgs["method"].toLowerCase())) && (!((kwArgs["sync"]) && (kwArgs["sync"] == true))));
	};
	this.bind = function (kwArgs) {
		if (!this["iframe"]) {
			this.setUpIframe();
		}
		this.requestQueue.push(kwArgs);
		this.fireNextRequest();
		return;
	};
	this.setUpIframe = function () {
		this.iframe = dojo.io.createIFrame(this.iframeName, "dojo.io.IframeTransport.iframeOnload();");
	};
	this.iframeOnload = function (errorObject) {
		if (!_this.currentRequest) {
			_this.fireNextRequest();
			return;
		}
		var req = _this.currentRequest;
		if (req.formNode) {
			var toClean = req._contentToClean;
			for (var i = 0; i < toClean.length; i++) {
				var key = toClean[i];
				if (dojo.render.html.safari) {
					var fNode = req.formNode;
					for (var j = 0; j < fNode.childNodes.length; j++) {
						var chNode = fNode.childNodes[j];
						if (chNode.name == key) {
							var pNode = chNode.parentNode;
							pNode.removeChild(chNode);
							break;
						}
					}
				} else {
					var input = req.formNode[key];
					req.formNode.removeChild(input);
					req.formNode[key] = null;
				}
			}
			if (req["_originalAction"]) {
				req.formNode.setAttribute("action", req._originalAction);
			}
			if (req["_originalTarget"]) {
				req.formNode.setAttribute("target", req._originalTarget);
				req.formNode.target = req._originalTarget;
			}
		}
		var contentDoc = function (iframe_el) {
			var doc = iframe_el.contentDocument || ((iframe_el.contentWindow) && (iframe_el.contentWindow.document)) || ((iframe_el.name) && (document.frames[iframe_el.name]) && (document.frames[iframe_el.name].document)) || null;
			return doc;
		};
		var value;
		var success = false;
		if (errorObject) {
			this._callError(req, "IframeTransport Request Error: " + errorObject);
		} else {
			var ifd = contentDoc(_this.iframe);
			try {
				var cmt = req.mimetype;
				if ((cmt == "text/javascript") || (cmt == "text/json") || (cmt == "application/json")) {
					var js = ifd.getElementsByTagName("textarea")[0].value;
					if (cmt == "text/json" || cmt == "application/json") {
						js = "(" + js + ")";
					}
					value = dj_eval(js);
				} else {
					if (cmt == "text/html") {
						value = ifd;
					} else {
						value = ifd.getElementsByTagName("textarea")[0].value;
					}
				}
				success = true;
			}
			catch (e) {
				this._callError(req, "IframeTransport Error: " + e);
			}
		}
		try {
			if (success && dojo.lang.isFunction(req["load"])) {
				req.load("load", value, req);
			}
		}
		catch (e) {
			throw e;
		}
		finally {
			_this.currentRequest = null;
			_this.fireNextRequest();
		}
	};
	this._callError = function (req, message) {
		var errObj = new dojo.io.Error(message);
		if (dojo.lang.isFunction(req["error"])) {
			req.error("error", errObj, req);
		}
	};
	dojo.io.transports.addTransport("IframeTransport");
};

