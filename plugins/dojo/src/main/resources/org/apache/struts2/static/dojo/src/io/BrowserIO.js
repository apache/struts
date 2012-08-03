/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.io.BrowserIO");
dojo.require("dojo.io.common");
dojo.require("dojo.lang.array");
dojo.require("dojo.lang.func");
dojo.require("dojo.string.extras");
dojo.require("dojo.dom");
dojo.require("dojo.undo.browser");
if (!dj_undef("window")) {
	dojo.io.checkChildrenForFile = function (node) {
		var hasFile = false;
		var inputs = node.getElementsByTagName("input");
		dojo.lang.forEach(inputs, function (input) {
			if (hasFile) {
				return;
			}
			if (input.getAttribute("type") == "file") {
				hasFile = true;
			}
		});
		return hasFile;
	};
	dojo.io.formHasFile = function (formNode) {
		return dojo.io.checkChildrenForFile(formNode);
	};
	dojo.io.updateNode = function (node, urlOrArgs) {
		node = dojo.byId(node);
		var args = urlOrArgs;
		if (dojo.lang.isString(urlOrArgs)) {
			args = {url:urlOrArgs};
		}
		args.mimetype = "text/html";
		args.load = function (t, d, e) {
			while (node.firstChild) {
				dojo.dom.destroyNode(node.firstChild);
			}
			node.innerHTML = d;
		};
		dojo.io.bind(args);
	};
	dojo.io.formFilter = function (node) {
		var type = (node.type || "").toLowerCase();
		return !node.disabled && node.name && !dojo.lang.inArray(["file", "submit", "image", "reset", "button"], type);
	};
	dojo.io.encodeForm = function (formNode, encoding, formFilter) {
		if ((!formNode) || (!formNode.tagName) || (!formNode.tagName.toLowerCase() == "form")) {
			dojo.raise("Attempted to encode a non-form element.");
		}
		if (!formFilter) {
			formFilter = dojo.io.formFilter;
		}
		var enc = /utf/i.test(encoding || "") ? encodeURIComponent : dojo.string.encodeAscii;
		var values = [];
		for (var i = 0; i < formNode.elements.length; i++) {
			var elm = formNode.elements[i];
			if (!elm || elm.tagName.toLowerCase() == "fieldset" || !formFilter(elm)) {
				continue;
			}
			var name = enc(elm.name);
			var type = elm.type.toLowerCase();
			if (type == "select-multiple") {
				for (var j = 0; j < elm.options.length; j++) {
					if (elm.options[j].selected) {
						values.push(name + "=" + enc(elm.options[j].value));
					}
				}
			} else {
				if (dojo.lang.inArray(["radio", "checkbox"], type)) {
					if (elm.checked) {
						values.push(name + "=" + enc(elm.value));
					}
				} else {
					values.push(name + "=" + enc(elm.value));
				}
			}
		}
		var inputs = formNode.getElementsByTagName("input");
		for (var i = 0; i < inputs.length; i++) {
			var input = inputs[i];
			if (input.type.toLowerCase() == "image" && input.form == formNode && formFilter(input)) {
				var name = enc(input.name);
				values.push(name + "=" + enc(input.value));
				values.push(name + ".x=0");
				values.push(name + ".y=0");
			}
		}
		return values.join("&") + "&";
	};
	dojo.io.FormBind = function (args) {
		this.bindArgs = {};
		if (args && args.formNode) {
			this.init(args);
		} else {
			if (args) {
				this.init({formNode:args});
			}
		}
	};
	dojo.lang.extend(dojo.io.FormBind, {form:null, bindArgs:null, clickedButton:null, init:function (args) {
		var form = dojo.byId(args.formNode);
		if (!form || !form.tagName || form.tagName.toLowerCase() != "form") {
			throw new Error("FormBind: Couldn't apply, invalid form");
		} else {
			if (this.form == form) {
				return;
			} else {
				if (this.form) {
					throw new Error("FormBind: Already applied to a form");
				}
			}
		}
		dojo.lang.mixin(this.bindArgs, args);
		this.form = form;
		this.connect(form, "onsubmit", "submit");
		for (var i = 0; i < form.elements.length; i++) {
			var node = form.elements[i];
			if (node && node.type && dojo.lang.inArray(["submit", "button"], node.type.toLowerCase())) {
				this.connect(node, "onclick", "click");
			}
		}
		var inputs = form.getElementsByTagName("input");
		for (var i = 0; i < inputs.length; i++) {
			var input = inputs[i];
			if (input.type.toLowerCase() == "image" && input.form == form) {
				this.connect(input, "onclick", "click");
			}
		}
	}, onSubmit:function (form) {
		return true;
	}, submit:function (e) {
		e.preventDefault();
		if (this.onSubmit(this.form)) {
			dojo.io.bind(dojo.lang.mixin(this.bindArgs, {formFilter:dojo.lang.hitch(this, "formFilter")}));
		}
	}, click:function (e) {
		var node = e.currentTarget;
		if (node.disabled) {
			return;
		}
		this.clickedButton = node;
	}, formFilter:function (node) {
		var type = (node.type || "").toLowerCase();
		var accept = false;
		if (node.disabled || !node.name) {
			accept = false;
		} else {
			if (dojo.lang.inArray(["submit", "button", "image"], type)) {
				if (!this.clickedButton) {
					this.clickedButton = node;
				}
				accept = node == this.clickedButton;
			} else {
				accept = !dojo.lang.inArray(["file", "submit", "reset", "button"], type);
			}
		}
		return accept;
	}, connect:function (srcObj, srcFcn, targetFcn) {
		if (dojo.evalObjPath("dojo.event.connect")) {
			dojo.event.connect(srcObj, srcFcn, this, targetFcn);
		} else {
			var fcn = dojo.lang.hitch(this, targetFcn);
			srcObj[srcFcn] = function (e) {
				if (!e) {
					e = window.event;
				}
				if (!e.currentTarget) {
					e.currentTarget = e.srcElement;
				}
				if (!e.preventDefault) {
					e.preventDefault = function () {
						window.event.returnValue = false;
					};
				}
				fcn(e);
			};
		}
	}});
	dojo.io.XMLHTTPTransport = new function () {
		var _this = this;
		var _cache = {};
		this.useCache = false;
		this.preventCache = false;
		function getCacheKey(url, query, method) {
			return url + "|" + query + "|" + method.toLowerCase();
		}
		function addToCache(url, query, method, http) {
			_cache[getCacheKey(url, query, method)] = http;
		}
		function getFromCache(url, query, method) {
			return _cache[getCacheKey(url, query, method)];
		}
		this.clearCache = function () {
			_cache = {};
		};
		function doLoad(kwArgs, http, url, query, useCache) {
			if (((http.status >= 200) && (http.status < 300)) || (http.status == 304) || (http.status == 1223) || (location.protocol == "file:" && (http.status == 0 || http.status == undefined)) || (location.protocol == "chrome:" && (http.status == 0 || http.status == undefined))) {
				var ret;
				if (kwArgs.method.toLowerCase() == "head") {
					var headers = http.getAllResponseHeaders();
					ret = {};
					ret.toString = function () {
						return headers;
					};
					var values = headers.split(/[\r\n]+/g);
					for (var i = 0; i < values.length; i++) {
						var pair = values[i].match(/^([^:]+)\s*:\s*(.+)$/i);
						if (pair) {
							ret[pair[1]] = pair[2];
						}
					}
				} else {
					if (kwArgs.mimetype == "text/javascript") {
						try {
							ret = dj_eval(http.responseText);
						}
						catch (e) {
							dojo.debug(e);
							dojo.debug(http.responseText);
							ret = null;
						}
					} else {
						if (kwArgs.mimetype.substr(0, 9) == "text/json" || kwArgs.mimetype.substr(0, 16) == "application/json") {
							try {
								ret = dj_eval("(" + kwArgs.jsonFilter(http.responseText) + ")");
							}
							catch (e) {
								dojo.debug(e);
								dojo.debug(http.responseText);
								ret = false;
							}
						} else {
							if ((kwArgs.mimetype == "application/xml") || (kwArgs.mimetype == "text/xml")) {
								ret = http.responseXML;
								if (!ret || typeof ret == "string" || !http.getResponseHeader("Content-Type")) {
									ret = dojo.dom.createDocumentFromText(http.responseText);
								}
							} else {
								ret = http.responseText;
							}
						}
					}
				}
				if (useCache) {
					addToCache(url, query, kwArgs.method, http);
				}
				kwArgs[(typeof kwArgs.load == "function") ? "load" : "handle"]("load", ret, http, kwArgs);
			} else {
				var errObj = new dojo.io.Error("XMLHttpTransport Error: " + http.status + " " + http.statusText);
				kwArgs[(typeof kwArgs.error == "function") ? "error" : "handle"]("error", errObj, http, kwArgs);
			}
		}
		function setHeaders(http, kwArgs) {
			if (kwArgs["headers"]) {
				for (var header in kwArgs["headers"]) {
					if (header.toLowerCase() == "content-type" && !kwArgs["contentType"]) {
						kwArgs["contentType"] = kwArgs["headers"][header];
					} else {
						http.setRequestHeader(header, kwArgs["headers"][header]);
					}
				}
			}
		}
		this.inFlight = [];
		this.inFlightTimer = null;
		this.startWatchingInFlight = function () {
			if (!this.inFlightTimer) {
				this.inFlightTimer = setTimeout("dojo.io.XMLHTTPTransport.watchInFlight();", 10);
			}
		};
		this.watchInFlight = function () {
			var now = null;
			if (!dojo.hostenv._blockAsync && !_this._blockAsync) {
				for (var x = this.inFlight.length - 1; x >= 0; x--) {
					try {
						var tif = this.inFlight[x];
						if (!tif || tif.http._aborted || !tif.http.readyState) {
							this.inFlight.splice(x, 1);
							continue;
						}
						if (4 == tif.http.readyState) {
							this.inFlight.splice(x, 1);
							doLoad(tif.req, tif.http, tif.url, tif.query, tif.useCache);
						} else {
							if (tif.startTime) {
								if (!now) {
									now = (new Date()).getTime();
								}
								if (tif.startTime + (tif.req.timeoutSeconds * 1000) < now) {
									if (typeof tif.http.abort == "function") {
										tif.http.abort();
									}
									this.inFlight.splice(x, 1);
									tif.req[(typeof tif.req.timeout == "function") ? "timeout" : "handle"]("timeout", null, tif.http, tif.req);
								}
							}
						}
					}
					catch (e) {
						try {
							var errObj = new dojo.io.Error("XMLHttpTransport.watchInFlight Error: " + e);
							tif.req[(typeof tif.req.error == "function") ? "error" : "handle"]("error", errObj, tif.http, tif.req);
						}
						catch (e2) {
							dojo.debug("XMLHttpTransport error callback failed: " + e2);
						}
					}
				}
			}
			clearTimeout(this.inFlightTimer);
			if (this.inFlight.length == 0) {
				this.inFlightTimer = null;
				return;
			}
			this.inFlightTimer = setTimeout("dojo.io.XMLHTTPTransport.watchInFlight();", 10);
		};
		var hasXmlHttp = dojo.hostenv.getXmlhttpObject() ? true : false;
		this.canHandle = function (kwArgs) {
			var mlc = kwArgs["mimetype"].toLowerCase() || "";
			return hasXmlHttp && ((dojo.lang.inArray(["text/plain", "text/html", "application/xml", "text/xml", "text/javascript"], mlc)) || (mlc.substr(0, 9) == "text/json" || mlc.substr(0, 16) == "application/json")) && !(kwArgs["formNode"] && dojo.io.formHasFile(kwArgs["formNode"]));
		};
		this.multipartBoundary = "45309FFF-BD65-4d50-99C9-36986896A96F";
		this.bind = function (kwArgs) {
			if (!kwArgs["url"]) {
				if (!kwArgs["formNode"] && (kwArgs["backButton"] || kwArgs["back"] || kwArgs["changeUrl"] || kwArgs["watchForURL"]) && (!djConfig.preventBackButtonFix)) {
					dojo.deprecated("Using dojo.io.XMLHTTPTransport.bind() to add to browser history without doing an IO request", "Use dojo.undo.browser.addToHistory() instead.", "0.4");
					dojo.undo.browser.addToHistory(kwArgs);
					return true;
				}
			}
			var url = kwArgs.url;
			var query = "";
			if (kwArgs["formNode"]) {
				var ta = kwArgs.formNode.getAttribute("action");
				if ((ta) && (!kwArgs["url"])) {
					url = ta;
				}
				var tp = kwArgs.formNode.getAttribute("method");
				if ((tp) && (!kwArgs["method"])) {
					kwArgs.method = tp;
				}
				query += dojo.io.encodeForm(kwArgs.formNode, kwArgs.encoding, kwArgs["formFilter"]);
			}
			if (url.indexOf("#") > -1) {
				dojo.debug("Warning: dojo.io.bind: stripping hash values from url:", url);
				url = url.split("#")[0];
			}
			if (kwArgs["file"]) {
				kwArgs.method = "post";
			}
			if (!kwArgs["method"]) {
				kwArgs.method = "get";
			}
			if (kwArgs.method.toLowerCase() == "get") {
				kwArgs.multipart = false;
			} else {
				if (kwArgs["file"]) {
					kwArgs.multipart = true;
				} else {
					if (!kwArgs["multipart"]) {
						kwArgs.multipart = false;
					}
				}
			}
			if (kwArgs["backButton"] || kwArgs["back"] || kwArgs["changeUrl"]) {
				dojo.undo.browser.addToHistory(kwArgs);
			}
			var content = kwArgs["content"] || {};
			if (kwArgs.sendTransport) {
				content["dojo.transport"] = "xmlhttp";
			}
			do {
				if (kwArgs.postContent) {
					query = kwArgs.postContent;
					break;
				}
				if (content) {
					query += dojo.io.argsFromMap(content, kwArgs.encoding);
				}
				if (kwArgs.method.toLowerCase() == "get" || !kwArgs.multipart) {
					break;
				}
				var t = [];
				if (query.length) {
					var q = query.split("&");
					for (var i = 0; i < q.length; ++i) {
						if (q[i].length) {
							var p = q[i].split("=");
							t.push("--" + this.multipartBoundary, "Content-Disposition: form-data; name=\"" + p[0] + "\"", "", p[1]);
						}
					}
				}
				if (kwArgs.file) {
					if (dojo.lang.isArray(kwArgs.file)) {
						for (var i = 0; i < kwArgs.file.length; ++i) {
							var o = kwArgs.file[i];
							t.push("--" + this.multipartBoundary, "Content-Disposition: form-data; name=\"" + o.name + "\"; filename=\"" + ("fileName" in o ? o.fileName : o.name) + "\"", "Content-Type: " + ("contentType" in o ? o.contentType : "application/octet-stream"), "", o.content);
						}
					} else {
						var o = kwArgs.file;
						t.push("--" + this.multipartBoundary, "Content-Disposition: form-data; name=\"" + o.name + "\"; filename=\"" + ("fileName" in o ? o.fileName : o.name) + "\"", "Content-Type: " + ("contentType" in o ? o.contentType : "application/octet-stream"), "", o.content);
					}
				}
				if (t.length) {
					t.push("--" + this.multipartBoundary + "--", "");
					query = t.join("\r\n");
				}
			} while (false);
			var async = kwArgs["sync"] ? false : true;
			var preventCache = kwArgs["preventCache"] || (this.preventCache == true && kwArgs["preventCache"] != false);
			var useCache = kwArgs["useCache"] == true || (this.useCache == true && kwArgs["useCache"] != false);
			if (!preventCache && useCache) {
				var cachedHttp = getFromCache(url, query, kwArgs.method);
				if (cachedHttp) {
					doLoad(kwArgs, cachedHttp, url, query, false);
					return;
				}
			}
			var http = dojo.hostenv.getXmlhttpObject(kwArgs);
			var received = false;
			if (async) {
				var startTime = this.inFlight.push({"req":kwArgs, "http":http, "url":url, "query":query, "useCache":useCache, "startTime":kwArgs.timeoutSeconds ? (new Date()).getTime() : 0});
				this.startWatchingInFlight();
			} else {
				_this._blockAsync = true;
			}
			if (kwArgs.method.toLowerCase() == "post") {
				if (!kwArgs.user) {
					http.open("POST", url, async);
				} else {
					http.open("POST", url, async, kwArgs.user, kwArgs.password);
				}
				setHeaders(http, kwArgs);
				http.setRequestHeader("Content-Type", kwArgs.multipart ? ("multipart/form-data; boundary=" + this.multipartBoundary) : (kwArgs.contentType || "application/x-www-form-urlencoded"));
				try {
					http.send(query);
				}
				catch (e) {
					if (typeof http.abort == "function") {
						http.abort();
					}
					doLoad(kwArgs, {status:404}, url, query, useCache);
				}
			} else {
				var tmpUrl = url;
				if (query != "") {
					tmpUrl += (tmpUrl.indexOf("?") > -1 ? "&" : "?") + query;
				}
				if (preventCache) {
					tmpUrl += (dojo.string.endsWithAny(tmpUrl, "?", "&") ? "" : (tmpUrl.indexOf("?") > -1 ? "&" : "?")) + "dojo.preventCache=" + new Date().valueOf();
				}
				if (!kwArgs.user) {
					http.open(kwArgs.method.toUpperCase(), tmpUrl, async);
				} else {
					http.open(kwArgs.method.toUpperCase(), tmpUrl, async, kwArgs.user, kwArgs.password);
				}
				setHeaders(http, kwArgs);
				try {
					http.send(null);
				}
				catch (e) {
					if (typeof http.abort == "function") {
						http.abort();
					}
					doLoad(kwArgs, {status:404}, url, query, useCache);
				}
			}
			if (!async) {
				doLoad(kwArgs, http, url, query, useCache);
				_this._blockAsync = false;
			}
			kwArgs.abort = function () {
				try {
					http._aborted = true;
				}
				catch (e) {
				}
				return http.abort();
			};
			return;
		};
		dojo.io.transports.addTransport("XMLHTTPTransport");
	};
}

