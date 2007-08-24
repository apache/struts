/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.ContentPane");
dojo.require("dojo.widget.*");
dojo.require("dojo.io.*");
dojo.require("dojo.widget.HtmlWidget");
dojo.require("dojo.string");
dojo.require("dojo.string.extras");
dojo.require("dojo.html.style");
dojo.widget.defineWidget("dojo.widget.ContentPane", dojo.widget.HtmlWidget, function () {
	this._styleNodes = [];
	this._onLoadStack = [];
	this._onUnloadStack = [];
	this._callOnUnload = false;
	this._ioBindObj;
	this.scriptScope;
	this.bindArgs = {};
}, {isContainer:true, adjustPaths:true, href:"", extractContent:true, parseContent:true, cacheContent:true, preload:false, refreshOnShow:false, handler:"", executeScripts:false, scriptSeparation:true, loadingMessage:"Loading...", isLoaded:false, postCreate:function (args, frag, parentComp) {
	if (this.handler !== "") {
		this.setHandler(this.handler);
	}
	if (this.isShowing() || this.preload) {
		this.loadContents();
	}
}, show:function () {
	if (this.refreshOnShow) {
		this.refresh();
	} else {
		this.loadContents();
	}
	dojo.widget.ContentPane.superclass.show.call(this);
}, refresh:function () {
	this.isLoaded = false;
	this.loadContents();
}, loadContents:function () {
	if (this.isLoaded) {
		return;
	}
	if (dojo.lang.isFunction(this.handler)) {
		this._runHandler();
	} else {
		if (this.href != "") {
			this._downloadExternalContent(this.href, this.cacheContent && !this.refreshOnShow);
		}
	}
}, setUrl:function (url) {
	this.href = url;
	this.isLoaded = false;
	if (this.preload || this.isShowing()) {
		this.loadContents();
	}
}, abort:function () {
	var bind = this._ioBindObj;
	if (!bind || !bind.abort) {
		return;
	}
	bind.abort();
	delete this._ioBindObj;
}, _downloadExternalContent:function (url, useCache) {
	this.abort();
	this._handleDefaults(this.loadingMessage, "onDownloadStart");
	var self = this;
	this._ioBindObj = dojo.io.bind(this._cacheSetting({url:url, mimetype:"text/html", handler:function (type, data, xhr) {
		delete self._ioBindObj;
		if (type == "load") {
			self.onDownloadEnd.call(self, url, data);
		} else {
			var e = {responseText:xhr.responseText, status:xhr.status, statusText:xhr.statusText, responseHeaders:xhr.getAllResponseHeaders(), text:"Error loading '" + url + "' (" + xhr.status + " " + xhr.statusText + ")"};
			self._handleDefaults.call(self, e, "onDownloadError");
			self.onLoad();
		}
	}}, useCache));
}, _cacheSetting:function (bindObj, useCache) {
	for (var x in this.bindArgs) {
		if (dojo.lang.isUndefined(bindObj[x])) {
			bindObj[x] = this.bindArgs[x];
		}
	}
	if (dojo.lang.isUndefined(bindObj.useCache)) {
		bindObj.useCache = useCache;
	}
	if (dojo.lang.isUndefined(bindObj.preventCache)) {
		bindObj.preventCache = !useCache;
	}
	if (dojo.lang.isUndefined(bindObj.mimetype)) {
		bindObj.mimetype = "text/html";
	}
	return bindObj;
}, onLoad:function (e) {
	this._runStack("_onLoadStack");
	this.isLoaded = true;
}, onUnLoad:function (e) {
	dojo.deprecated(this.widgetType + ".onUnLoad, use .onUnload (lowercased load)", 0.5);
}, onUnload:function (e) {
	this._runStack("_onUnloadStack");
	delete this.scriptScope;
	if (this.onUnLoad !== dojo.widget.ContentPane.prototype.onUnLoad) {
		this.onUnLoad.apply(this, arguments);
	}
}, _runStack:function (stName) {
	var st = this[stName];
	var err = "";
	var scope = this.scriptScope || window;
	for (var i = 0; i < st.length; i++) {
		try {
			st[i].call(scope);
		}
		catch (e) {
			err += "\n" + st[i] + " failed: " + e.description;
		}
	}
	this[stName] = [];
	if (err.length) {
		var name = (stName == "_onLoadStack") ? "addOnLoad" : "addOnUnLoad";
		this._handleDefaults(name + " failure\n " + err, "onExecError", "debug");
	}
}, addOnLoad:function (obj, func) {
	this._pushOnStack(this._onLoadStack, obj, func);
}, addOnUnload:function (obj, func) {
	this._pushOnStack(this._onUnloadStack, obj, func);
}, addOnUnLoad:function () {
	dojo.deprecated(this.widgetType + ".addOnUnLoad, use addOnUnload instead. (lowercased Load)", 0.5);
	this.addOnUnload.apply(this, arguments);
}, _pushOnStack:function (stack, obj, func) {
	if (typeof func == "undefined") {
		stack.push(obj);
	} else {
		stack.push(function () {
			obj[func]();
		});
	}
}, destroy:function () {
	this.onUnload();
	dojo.widget.ContentPane.superclass.destroy.call(this);
}, onExecError:function (e) {
}, onContentError:function (e) {
}, onDownloadError:function (e) {
}, onDownloadStart:function (e) {
}, onDownloadEnd:function (url, data) {
	data = this.splitAndFixPaths(data, url);
	this.setContent(data);
}, _handleDefaults:function (e, handler, messType) {
	if (!handler) {
		handler = "onContentError";
	}
	if (dojo.lang.isString(e)) {
		e = {text:e};
	}
	if (!e.text) {
		e.text = e.toString();
	}
	e.toString = function () {
		return this.text;
	};
	if (typeof e.returnValue != "boolean") {
		e.returnValue = true;
	}
	if (typeof e.preventDefault != "function") {
		e.preventDefault = function () {
			this.returnValue = false;
		};
	}
	this[handler](e);
	if (e.returnValue) {
		switch (messType) {
		  case true:
		  case "alert":
			alert(e.toString());
			break;
		  case "debug":
			dojo.debug(e.toString());
			break;
		  default:
			if (this._callOnUnload) {
				this.onUnload();
			}
			this._callOnUnload = false;
			if (arguments.callee._loopStop) {
				dojo.debug(e.toString());
			} else {
				arguments.callee._loopStop = true;
				this._setContent(e.toString());
			}
		}
	}
	arguments.callee._loopStop = false;
}, splitAndFixPaths:function (s, url) {
	var titles = [], scripts = [], tmp = [];
	var match = [], requires = [], attr = [], styles = [];
	var str = "", path = "", fix = "", tagFix = "", tag = "", origPath = "";
	if (!url) {
		url = "./";
	}
	if (s) {
		var regex = /<title[^>]*>([\s\S]*?)<\/title>/i;
		while (match = regex.exec(s)) {
			titles.push(match[1]);
			s = s.substring(0, match.index) + s.substr(match.index + match[0].length);
		}
		if (this.adjustPaths) {
			var regexFindTag = /<[a-z][a-z0-9]*[^>]*\s(?:(?:src|href|style)=[^>])+[^>]*>/i;
			var regexFindAttr = /\s(src|href|style)=(['"]?)([\w()\[\]\/.,\\'"-:;#=&?\s@]+?)\2/i;
			var regexProtocols = /^(?:[#]|(?:(?:https?|ftps?|file|javascript|mailto|news):))/;
			while (tag = regexFindTag.exec(s)) {
				str += s.substring(0, tag.index);
				s = s.substring((tag.index + tag[0].length), s.length);
				tag = tag[0];
				tagFix = "";
				while (attr = regexFindAttr.exec(tag)) {
					path = "";
					origPath = attr[3];
					switch (attr[1].toLowerCase()) {
					  case "src":
					  case "href":
						if (regexProtocols.exec(origPath)) {
							path = origPath;
						} else {
							path = (new dojo.uri.Uri(url, origPath).toString());
						}
						break;
					  case "style":
						path = dojo.html.fixPathsInCssText(origPath, url);
						break;
					  default:
						path = origPath;
					}
					fix = " " + attr[1] + "=" + attr[2] + path + attr[2];
					tagFix += tag.substring(0, attr.index) + fix;
					tag = tag.substring((attr.index + attr[0].length), tag.length);
				}
				str += tagFix + tag;
			}
			s = str + s;
		}
		regex = /(?:<(style)[^>]*>([\s\S]*?)<\/style>|<link ([^>]*rel=['"]?stylesheet['"]?[^>]*)>)/i;
		while (match = regex.exec(s)) {
			if (match[1] && match[1].toLowerCase() == "style") {
				styles.push(dojo.html.fixPathsInCssText(match[2], url));
			} else {
				if (attr = match[3].match(/href=(['"]?)([^'">]*)\1/i)) {
					styles.push({path:attr[2]});
				}
			}
			s = s.substring(0, match.index) + s.substr(match.index + match[0].length);
		}
		var regex = /<script([^>]*)>([\s\S]*?)<\/script>/i;
		var regexSrc = /src=(['"]?)([^"']*)\1/i;
		var regexDojoJs = /.*(\bdojo\b\.js(?:\.uncompressed\.js)?)$/;
		var regexInvalid = /(?:var )?\bdjConfig\b(?:[\s]*=[\s]*\{[^}]+\}|\.[\w]*[\s]*=[\s]*[^;\n]*)?;?|dojo\.hostenv\.writeIncludes\(\s*\);?/g;
		var regexRequires = /dojo\.(?:(?:require(?:After)?(?:If)?)|(?:widget\.(?:manager\.)?registerWidgetPackage)|(?:(?:hostenv\.)?setModulePrefix|registerModulePath)|defineNamespace)\((['"]).*?\1\)\s*;?/;
		while (match = regex.exec(s)) {
			if (this.executeScripts && match[1]) {
				if (attr = regexSrc.exec(match[1])) {
					if (regexDojoJs.exec(attr[2])) {
						dojo.debug("Security note! inhibit:" + attr[2] + " from  being loaded again.");
					} else {
						scripts.push({path:attr[2]});
					}
				}
			}
			if (match[2]) {
				var sc = match[2].replace(regexInvalid, "");
				if (!sc) {
					continue;
				}
				while (tmp = regexRequires.exec(sc)) {
					requires.push(tmp[0]);
					sc = sc.substring(0, tmp.index) + sc.substr(tmp.index + tmp[0].length);
				}
				if (this.executeScripts) {
					scripts.push(sc);
				}
			}
			s = s.substr(0, match.index) + s.substr(match.index + match[0].length);
		}
		if (this.extractContent) {
			match = s.match(/<body[^>]*>\s*([\s\S]+)\s*<\/body>/im);
			if (match) {
				s = match[1];
			}
		}
		if (this.executeScripts && this.scriptSeparation) {
			var regex = /(<[a-zA-Z][a-zA-Z0-9]*\s[^>]*?\S=)((['"])[^>]*scriptScope[^>]*>)/;
			var regexAttr = /([\s'";:\(])scriptScope(.*)/;
			str = "";
			while (tag = regex.exec(s)) {
				tmp = ((tag[3] == "'") ? "\"" : "'");
				fix = "";
				str += s.substring(0, tag.index) + tag[1];
				while (attr = regexAttr.exec(tag[2])) {
					tag[2] = tag[2].substring(0, attr.index) + attr[1] + "dojo.widget.byId(" + tmp + this.widgetId + tmp + ").scriptScope" + attr[2];
				}
				str += tag[2];
				s = s.substr(tag.index + tag[0].length);
			}
			s = str + s;
		}
	}
	return {"xml":s, "styles":styles, "titles":titles, "requires":requires, "scripts":scripts, "url":url};
}, _setContent:function (cont) {
	this.destroyChildren();
	for (var i = 0; i < this._styleNodes.length; i++) {
		if (this._styleNodes[i] && this._styleNodes[i].parentNode) {
			this._styleNodes[i].parentNode.removeChild(this._styleNodes[i]);
		}
	}
	this._styleNodes = [];
	try {
		var node = this.containerNode || this.domNode;
		while (node.firstChild) {
			dojo.html.destroyNode(node.firstChild);
		}
		if (typeof cont != "string") {
			node.appendChild(cont);
		} else {
			node.innerHTML = cont;
		}
	}
	catch (e) {
		e.text = "Couldn't load content:" + e.description;
		this._handleDefaults(e, "onContentError");
	}
}, setContent:function (data) {
	this.abort();
	if (this._callOnUnload) {
		this.onUnload();
	}
	this._callOnUnload = true;
	if (!data || dojo.html.isNode(data)) {
		this._setContent(data);
		this.onResized();
		this.onLoad();
	} else {
		if (typeof data.xml != "string") {
			this.href = "";
			data = this.splitAndFixPaths(data);
		}
		this._setContent(data.xml);
		for (var i = 0; i < data.styles.length; i++) {
			if (data.styles[i].path) {
				this._styleNodes.push(dojo.html.insertCssFile(data.styles[i].path, dojo.doc(), false, true));
			} else {
				this._styleNodes.push(dojo.html.insertCssText(data.styles[i]));
			}
		}
		if (this.parseContent) {
			for (var i = 0; i < data.requires.length; i++) {
				try {
					eval(data.requires[i]);
				}
				catch (e) {
					e.text = "ContentPane: error in package loading calls, " + (e.description || e);
					this._handleDefaults(e, "onContentError", "debug");
				}
			}
		}
		var _self = this;
		function asyncParse() {
			if (_self.executeScripts) {
				_self._executeScripts(data.scripts);
			}
			if (_self.parseContent) {
				var node = _self.containerNode || _self.domNode;
				var parser = new dojo.xml.Parse();
				var frag = parser.parseElement(node, null, true);
				dojo.widget.getParser().createSubComponents(frag, _self);
			}
			_self.onResized();
			_self.onLoad();
		}
		if (dojo.hostenv.isXDomain && data.requires.length) {
			dojo.addOnLoad(asyncParse);
		} else {
			asyncParse();
		}
	}
}, setHandler:function (handler) {
	var fcn = dojo.lang.isFunction(handler) ? handler : window[handler];
	if (!dojo.lang.isFunction(fcn)) {
		this._handleDefaults("Unable to set handler, '" + handler + "' not a function.", "onExecError", true);
		return;
	}
	this.handler = function () {
		return fcn.apply(this, arguments);
	};
}, _runHandler:function () {
	var ret = true;
	if (dojo.lang.isFunction(this.handler)) {
		this.handler(this, this.domNode);
		ret = false;
	}
	this.onLoad();
	return ret;
}, _executeScripts:function (scripts) {
	var self = this;
	var tmp = "", code = "";
	for (var i = 0; i < scripts.length; i++) {
		if (scripts[i].path) {
			dojo.io.bind(this._cacheSetting({"url":scripts[i].path, "load":function (type, scriptStr) {
				dojo.lang.hitch(self, tmp = ";" + scriptStr);
			}, "error":function (type, error) {
				error.text = type + " downloading remote script";
				self._handleDefaults.call(self, error, "onExecError", "debug");
			}, "mimetype":"text/plain", "sync":true}, this.cacheContent));
			code += tmp;
		} else {
			code += scripts[i];
		}
	}
	try {
		if (this.scriptSeparation) {
			delete this.scriptScope;
			this.scriptScope = new (new Function("_container_", code + "; return this;"))(self);
		} else {
			var djg = dojo.global();
			if (djg.execScript) {
				djg.execScript(code);
			} else {
				var djd = dojo.doc();
				var sc = djd.createElement("script");
				sc.appendChild(djd.createTextNode(code));
				(this.containerNode || this.domNode).appendChild(sc);
			}
		}
	}
	catch (e) {
		e.text = "Error running scripts from content:\n" + e.description;
		this._handleDefaults(e, "onExecError", "debug");
	}
}});

