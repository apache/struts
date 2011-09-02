/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.RichText");
dojo.require("dojo.widget.*");
dojo.require("dojo.html.*");
dojo.require("dojo.html.layout");
dojo.require("dojo.html.selection");
dojo.require("dojo.event.*");
dojo.require("dojo.string.extras");
dojo.require("dojo.uri.Uri");
dojo.require("dojo.Deferred");
if (!djConfig["useXDomain"] || djConfig["allowXdRichTextSave"]) {
	if (dojo.hostenv.post_load_) {
		(function () {
			var savetextarea = dojo.doc().createElement("textarea");
			savetextarea.id = "dojo.widget.RichText.savedContent";
			savetextarea.style = "display:none;position:absolute;top:-100px;left:-100px;height:3px;width:3px;overflow:hidden;";
			dojo.body().appendChild(savetextarea);
		})();
	} else {
		try {
			dojo.doc().write("<textarea id=\"dojo.widget.RichText.savedContent\" " + "style=\"display:none;position:absolute;top:-100px;left:-100px;height:3px;width:3px;overflow:hidden;\"></textarea>");
		}
		catch (e) {
		}
	}
}
dojo.widget.defineWidget("dojo.widget.RichText", dojo.widget.HtmlWidget, function () {
	this.contentPreFilters = [];
	this.contentPostFilters = [];
	this.contentDomPreFilters = [];
	this.contentDomPostFilters = [];
	this.editingAreaStyleSheets = [];
	if (dojo.render.html.moz) {
		this.contentPreFilters.push(this._fixContentForMoz);
	}
	this._keyHandlers = {};
	if (dojo.Deferred) {
		this.onLoadDeferred = new dojo.Deferred();
	}
}, {inheritWidth:false, focusOnLoad:false, saveName:"", styleSheets:"", _content:"", height:"", minHeight:"1em", isClosed:true, isLoaded:false, useActiveX:false, relativeImageUrls:false, _SEPARATOR:"@@**%%__RICHTEXTBOUNDRY__%%**@@", onLoadDeferred:null, fillInTemplate:function () {
	dojo.event.topic.publish("dojo.widget.RichText::init", this);
	this.open();
	dojo.event.connect(this, "onKeyPressed", this, "afterKeyPress");
	dojo.event.connect(this, "onKeyPress", this, "keyPress");
	dojo.event.connect(this, "onKeyDown", this, "keyDown");
	dojo.event.connect(this, "onKeyUp", this, "keyUp");
	this.setupDefaultShortcuts();
}, setupDefaultShortcuts:function () {
	var ctrl = this.KEY_CTRL;
	var exec = function (cmd, arg) {
		return arguments.length == 1 ? function () {
			this.execCommand(cmd);
		} : function () {
			this.execCommand(cmd, arg);
		};
	};
	this.addKeyHandler("b", ctrl, exec("bold"));
	this.addKeyHandler("i", ctrl, exec("italic"));
	this.addKeyHandler("u", ctrl, exec("underline"));
	this.addKeyHandler("a", ctrl, exec("selectall"));
	this.addKeyHandler("s", ctrl, function () {
		this.save(true);
	});
	this.addKeyHandler("1", ctrl, exec("formatblock", "h1"));
	this.addKeyHandler("2", ctrl, exec("formatblock", "h2"));
	this.addKeyHandler("3", ctrl, exec("formatblock", "h3"));
	this.addKeyHandler("4", ctrl, exec("formatblock", "h4"));
	this.addKeyHandler("\\", ctrl, exec("insertunorderedlist"));
	if (!dojo.render.html.ie) {
		this.addKeyHandler("Z", ctrl, exec("redo"));
	}
}, events:["onBlur", "onFocus", "onKeyPress", "onKeyDown", "onKeyUp", "onClick"], open:function (element) {
	if (this.onLoadDeferred.fired >= 0) {
		this.onLoadDeferred = new dojo.Deferred();
	}
	var h = dojo.render.html;
	if (!this.isClosed) {
		this.close();
	}
	dojo.event.topic.publish("dojo.widget.RichText::open", this);
	this._content = "";
	if ((arguments.length == 1) && (element["nodeName"])) {
		this.domNode = element;
	}
	if ((this.domNode["nodeName"]) && (this.domNode.nodeName.toLowerCase() == "textarea")) {
		this.textarea = this.domNode;
		var html = this._preFilterContent(this.textarea.value);
		this.domNode = dojo.doc().createElement("div");
		dojo.html.copyStyle(this.domNode, this.textarea);
		var tmpFunc = dojo.lang.hitch(this, function () {
			with (this.textarea.style) {
				display = "block";
				position = "absolute";
				left = top = "-1000px";
				if (h.ie) {
					this.__overflow = overflow;
					overflow = "hidden";
				}
			}
		});
		if (h.ie) {
			setTimeout(tmpFunc, 10);
		} else {
			tmpFunc();
		}
		if (!h.safari) {
			dojo.html.insertBefore(this.domNode, this.textarea);
		}
		if (this.textarea.form) {
			dojo.event.connect("before", this.textarea.form, "onsubmit", dojo.lang.hitch(this, function () {
				this.textarea.value = this.getEditorContent();
			}));
		}
		var editor = this;
		dojo.event.connect(this, "postCreate", function () {
			dojo.html.insertAfter(editor.textarea, editor.domNode);
		});
	} else {
		var html = this._preFilterContent(dojo.string.trim(this.domNode.innerHTML));
	}
	if (html == "") {
		html = "&nbsp;";
	}
	var content = dojo.html.getContentBox(this.domNode);
	this._oldHeight = content.height;
	this._oldWidth = content.width;
	this._firstChildContributingMargin = this._getContributingMargin(this.domNode, "top");
	this._lastChildContributingMargin = this._getContributingMargin(this.domNode, "bottom");
	this.savedContent = html;
	this.domNode.innerHTML = "";
	this.editingArea = dojo.doc().createElement("div");
	this.domNode.appendChild(this.editingArea);
	if ((this.domNode["nodeName"]) && (this.domNode.nodeName == "LI")) {
		this.domNode.innerHTML = " <br>";
	}
	if (this.saveName != "" && (!djConfig["useXDomain"] || djConfig["allowXdRichTextSave"])) {
		var saveTextarea = dojo.doc().getElementById("dojo.widget.RichText.savedContent");
		if (saveTextarea.value != "") {
			var datas = saveTextarea.value.split(this._SEPARATOR);
			for (var i = 0; i < datas.length; i++) {
				var data = datas[i].split(":");
				if (data[0] == this.saveName) {
					html = data[1];
					datas.splice(i, 1);
					break;
				}
			}
		}
		dojo.event.connect("before", window, "onunload", this, "_saveContent");
	}
	if (h.ie70 && this.useActiveX) {
		dojo.debug("activeX in ie70 is not currently supported, useActiveX is ignored for now.");
		this.useActiveX = false;
	}
	if (this.useActiveX && h.ie) {
		var self = this;
		setTimeout(function () {
			self._drawObject(html);
		}, 0);
	} else {
		if (h.ie || this._safariIsLeopard() || h.opera) {
			this.iframe = dojo.doc().createElement("iframe");
			this.iframe.src = "javascript:void(0)";
			this.editorObject = this.iframe;
			with (this.iframe.style) {
				border = "0";
				width = "100%";
			}
			this.iframe.frameBorder = 0;
			this.editingArea.appendChild(this.iframe);
			this.window = this.iframe.contentWindow;
			this.document = this.window.document;
			this.document.open();
			this.document.write("<html><head><style>body{margin:0;padding:0;border:0;overflow:hidden;}</style></head><body><div></div></body></html>");
			this.document.close();
			this.editNode = this.document.body.firstChild;
			this.editNode.contentEditable = true;
			with (this.iframe.style) {
				if (h.ie70) {
					if (this.height) {
						height = this.height;
					}
					if (this.minHeight) {
						minHeight = this.minHeight;
					}
				} else {
					height = this.height ? this.height : this.minHeight;
				}
			}
			var formats = ["p", "pre", "address", "h1", "h2", "h3", "h4", "h5", "h6", "ol", "div", "ul"];
			var localhtml = "";
			for (var i in formats) {
				if (formats[i].charAt(1) != "l") {
					localhtml += "<" + formats[i] + "><span>content</span></" + formats[i] + ">";
				} else {
					localhtml += "<" + formats[i] + "><li>content</li></" + formats[i] + ">";
				}
			}
			with (this.editNode.style) {
				position = "absolute";
				left = "-2000px";
				top = "-2000px";
			}
			this.editNode.innerHTML = localhtml;
			var node = this.editNode.firstChild;
			while (node) {
				dojo.withGlobal(this.window, "selectElement", dojo.html.selection, [node.firstChild]);
				var nativename = node.tagName.toLowerCase();
				this._local2NativeFormatNames[nativename] = this.queryCommandValue("formatblock");
				this._native2LocalFormatNames[this._local2NativeFormatNames[nativename]] = nativename;
				node = node.nextSibling;
			}
			with (this.editNode.style) {
				position = "";
				left = "";
				top = "";
			}
			this.editNode.innerHTML = html;
			if (this.height) {
				this.document.body.style.overflowY = "scroll";
			}
			dojo.lang.forEach(this.events, function (e) {
				dojo.event.connect(this.editNode, e.toLowerCase(), this, e);
			}, this);
			this.onLoad();
		} else {
			this._drawIframe(html);
			this.editorObject = this.iframe;
		}
	}
	if (this.domNode.nodeName == "LI") {
		this.domNode.lastChild.style.marginTop = "-1.2em";
	}
	dojo.html.addClass(this.domNode, "RichTextEditable");
	this.isClosed = false;
}, _hasCollapseableMargin:function (element, side) {
	if (dojo.html.getPixelValue(element, "border-" + side + "-width", false)) {
		return false;
	} else {
		if (dojo.html.getPixelValue(element, "padding-" + side, false)) {
			return false;
		} else {
			return true;
		}
	}
}, _getContributingMargin:function (element, topOrBottom) {
	if (topOrBottom == "top") {
		var siblingAttr = "previousSibling";
		var childSiblingAttr = "nextSibling";
		var childAttr = "firstChild";
		var marginProp = "margin-top";
		var siblingMarginProp = "margin-bottom";
	} else {
		var siblingAttr = "nextSibling";
		var childSiblingAttr = "previousSibling";
		var childAttr = "lastChild";
		var marginProp = "margin-bottom";
		var siblingMarginProp = "margin-top";
	}
	var elementMargin = dojo.html.getPixelValue(element, marginProp, false);
	function isSignificantNode(element) {
		return !(element.nodeType == 3 && dojo.string.isBlank(element.data)) && dojo.html.getStyle(element, "display") != "none" && !dojo.html.isPositionAbsolute(element);
	}
	var childMargin = 0;
	var child = element[childAttr];
	while (child) {
		while ((!isSignificantNode(child)) && child[childSiblingAttr]) {
			child = child[childSiblingAttr];
		}
		childMargin = Math.max(childMargin, dojo.html.getPixelValue(child, marginProp, false));
		if (!this._hasCollapseableMargin(child, topOrBottom)) {
			break;
		}
		child = child[childAttr];
	}
	if (!this._hasCollapseableMargin(element, topOrBottom)) {
		return parseInt(childMargin);
	}
	var contextMargin = 0;
	var sibling = element[siblingAttr];
	while (sibling) {
		if (isSignificantNode(sibling)) {
			contextMargin = dojo.html.getPixelValue(sibling, siblingMarginProp, false);
			break;
		}
		sibling = sibling[siblingAttr];
	}
	if (!sibling) {
		contextMargin = dojo.html.getPixelValue(element.parentNode, marginProp, false);
	}
	if (childMargin > elementMargin) {
		return parseInt(Math.max((childMargin - elementMargin) - contextMargin, 0));
	} else {
		return 0;
	}
}, _drawIframe:function (html) {
	var oldMoz = Boolean(dojo.render.html.moz && (typeof window.XML == "undefined"));
	if (!this.iframe) {
		var currentDomain = (new dojo.uri.Uri(dojo.doc().location)).host;
		this.iframe = dojo.doc().createElement("iframe");
		with (this.iframe) {
			style.border = "none";
			style.lineHeight = "0";
			style.verticalAlign = "bottom";
			scrolling = this.height ? "auto" : "no";
		}
	}
	if (djConfig["useXDomain"] && !djConfig["dojoRichTextFrameUrl"]) {
		dojo.debug("dojo.widget.RichText: When using cross-domain Dojo builds," + " please save src/widget/templates/richtextframe.html to your domain and set djConfig.dojoRichTextFrameUrl" + " to the path on your domain to richtextframe.html");
	}
	this.iframe.src = (djConfig["dojoRichTextFrameUrl"] || dojo.uri.moduleUri("dojo.widget", "templates/richtextframe.html")) + ((dojo.doc().domain != currentDomain) ? ("#" + dojo.doc().domain) : "");
	this.iframe.width = this.inheritWidth ? this._oldWidth : "100%";
	if (this.height) {
		this.iframe.style.height = this.height;
	} else {
		var height = this._oldHeight;
		if (this._hasCollapseableMargin(this.domNode, "top")) {
			height += this._firstChildContributingMargin;
		}
		if (this._hasCollapseableMargin(this.domNode, "bottom")) {
			height += this._lastChildContributingMargin;
		}
		this.iframe.height = height;
	}
	var tmpContent = dojo.doc().createElement("div");
	tmpContent.innerHTML = html;
	this.editingArea.appendChild(tmpContent);
	if (this.relativeImageUrls) {
		var imgs = tmpContent.getElementsByTagName("img");
		for (var i = 0; i < imgs.length; i++) {
			imgs[i].src = (new dojo.uri.Uri(dojo.global().location, imgs[i].src)).toString();
		}
		html = tmpContent.innerHTML;
	}
	var firstChild = dojo.html.firstElement(tmpContent);
	var lastChild = dojo.html.lastElement(tmpContent);
	if (firstChild) {
		firstChild.style.marginTop = this._firstChildContributingMargin + "px";
	}
	if (lastChild) {
		lastChild.style.marginBottom = this._lastChildContributingMargin + "px";
	}
	this.editingArea.appendChild(this.iframe);
	if (dojo.render.html.safari) {
		this.iframe.src = this.iframe.src;
	}
	var _iframeInitialized = false;
	var ifrFunc = dojo.lang.hitch(this, function () {
		if (!_iframeInitialized) {
			_iframeInitialized = true;
		} else {
			return;
		}
		if (!this.editNode) {
			if (this.iframe.contentWindow) {
				this.window = this.iframe.contentWindow;
				this.document = this.iframe.contentWindow.document;
			} else {
				if (this.iframe.contentDocument) {
					this.window = this.iframe.contentDocument.window;
					this.document = this.iframe.contentDocument;
				}
			}
			var getStyle = (function (domNode) {
				return function (style) {
					return dojo.html.getStyle(domNode, style);
				};
			})(this.domNode);
			var font = getStyle("font-weight") + " " + getStyle("font-size") + " " + getStyle("font-family");
			var lineHeight = "1.0";
			var lineHeightStyle = dojo.html.getUnitValue(this.domNode, "line-height");
			if (lineHeightStyle.value && lineHeightStyle.units == "") {
				lineHeight = lineHeightStyle.value;
			}
			dojo.html.insertCssText("body,html{background:transparent;padding:0;margin:0;}" + "body{top:0;left:0;right:0;" + (((this.height) || (dojo.render.html.opera)) ? "" : "position:fixed;") + "font:" + font + ";" + "min-height:" + this.minHeight + ";" + "line-height:" + lineHeight + "}" + "p{margin: 1em 0 !important;}" + "body > *:first-child{padding-top:0 !important;margin-top:" + this._firstChildContributingMargin + "px !important;}" + "body > *:last-child{padding-bottom:0 !important;margin-bottom:" + this._lastChildContributingMargin + "px !important;}" + "li > ul:-moz-first-node, li > ol:-moz-first-node{padding-top:1.2em;}\n" + "li{min-height:1.2em;}" + "", this.document);
			dojo.html.removeNode(tmpContent);
			this.document.body.innerHTML = html;
			if (oldMoz || dojo.render.html.safari) {
				this.document.designMode = "on";
			}
			this.onLoad();
		} else {
			dojo.html.removeNode(tmpContent);
			this.editNode.innerHTML = html;
			this.onDisplayChanged();
		}
	});
	if (this.editNode) {
		ifrFunc();
	} else {
		if (dojo.render.html.moz) {
			this.iframe.onload = function () {
				setTimeout(ifrFunc, 250);
			};
		} else {
			this.iframe.onload = ifrFunc;
		}
	}
}, _applyEditingAreaStyleSheets:function () {
	var files = [];
	if (this.styleSheets) {
		files = this.styleSheets.split(";");
		this.styleSheets = "";
	}
	files = files.concat(this.editingAreaStyleSheets);
	this.editingAreaStyleSheets = [];
	if (files.length > 0) {
		for (var i = 0; i < files.length; i++) {
			var url = files[i];
			if (url) {
				this.addStyleSheet(dojo.uri.dojoUri(url));
			}
		}
	}
}, addStyleSheet:function (uri) {
	var url = uri.toString();
	if (dojo.lang.find(this.editingAreaStyleSheets, url) > -1) {
		dojo.debug("dojo.widget.RichText.addStyleSheet: Style sheet " + url + " is already applied to the editing area!");
		return;
	}
	if (url.charAt(0) == "." || (url.charAt(0) != "/" && !uri.host)) {
		url = (new dojo.uri.Uri(dojo.global().location, url)).toString();
	}
	this.editingAreaStyleSheets.push(url);
	if (this.document.createStyleSheet) {
		this.document.createStyleSheet(url);
	} else {
		var head = this.document.getElementsByTagName("head")[0];
		var stylesheet = this.document.createElement("link");
		with (stylesheet) {
			rel = "stylesheet";
			type = "text/css";
			href = url;
		}
		head.appendChild(stylesheet);
	}
}, removeStyleSheet:function (uri) {
	var url = uri.toString();
	if (url.charAt(0) == "." || (url.charAt(0) != "/" && !uri.host)) {
		url = (new dojo.uri.Uri(dojo.global().location, url)).toString();
	}
	var index = dojo.lang.find(this.editingAreaStyleSheets, url);
	if (index == -1) {
		dojo.debug("dojo.widget.RichText.removeStyleSheet: Style sheet " + url + " is not applied to the editing area so it can not be removed!");
		return;
	}
	delete this.editingAreaStyleSheets[index];
	var links = this.document.getElementsByTagName("link");
	for (var i = 0; i < links.length; i++) {
		if (links[i].href == url) {
			if (dojo.render.html.ie) {
				links[i].href = "";
			}
			dojo.html.removeNode(links[i]);
			break;
		}
	}
}, _drawObject:function (html) {
	this.object = dojo.html.createExternalElement(dojo.doc(), "object");
	with (this.object) {
		classid = "clsid:2D360201-FFF5-11D1-8D03-00A0C959BC0A";
		width = this.inheritWidth ? this._oldWidth : "100%";
		style.height = this.height ? this.height : (this._oldHeight + "px");
		Scrollbars = this.height ? true : false;
		Appearance = this._activeX.appearance.flat;
	}
	this.editorObject = this.object;
	this.editingArea.appendChild(this.object);
	this.object.attachEvent("DocumentComplete", dojo.lang.hitch(this, "onLoad"));
	dojo.lang.forEach(this.events, function (e) {
		this.object.attachEvent(e.toLowerCase(), dojo.lang.hitch(this, e));
	}, this);
	this.object.DocumentHTML = "<!doctype HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">" + "<html><title></title>" + "<style type=\"text/css\">" + "	body,html { padding: 0; margin: 0; }" + (this.height ? "" : "	body,  { overflow: hidden; }") + "</style>" + "<body><div>" + html + "<div></body></html>";
	this._cacheLocalBlockFormatNames();
}, _local2NativeFormatNames:{}, _native2LocalFormatNames:{}, _cacheLocalBlockFormatNames:function () {
	if (!this._native2LocalFormatNames["p"]) {
		var obj = this.object;
		var error = false;
		if (!obj) {
			try {
				obj = dojo.html.createExternalElement(dojo.doc(), "object");
				obj.classid = "clsid:2D360201-FFF5-11D1-8D03-00A0C959BC0A";
				dojo.body().appendChild(obj);
				obj.DocumentHTML = "<html><head></head><body></body></html>";
			}
			catch (e) {
				error = true;
			}
		}
		try {
			var oNamesParm = new ActiveXObject("DEGetBlockFmtNamesParam.DEGetBlockFmtNamesParam");
			obj.ExecCommand(this._activeX.command["getblockformatnames"], 0, oNamesParm);
			var vbNamesArray = new VBArray(oNamesParm.Names);
			var localFormats = vbNamesArray.toArray();
			var nativeFormats = ["p", "pre", "address", "h1", "h2", "h3", "h4", "h5", "h6", "ol", "ul", "", "", "", "", "div"];
			for (var i = 0; i < nativeFormats.length; ++i) {
				if (nativeFormats[i].length > 0) {
					this._local2NativeFormatNames[localFormats[i]] = nativeFormats[i];
					this._native2LocalFormatNames[nativeFormats[i]] = localFormats[i];
				}
			}
		}
		catch (e) {
			error = true;
		}
		if (obj && !this.object) {
			dojo.body().removeChild(obj);
		}
	}
	return !error;
}, _isResized:function () {
	return false;
}, onLoad:function (e) {
	this.isLoaded = true;
	if (this.object) {
		this.document = this.object.DOM;
		this.window = this.document.parentWindow;
		this.editNode = this.document.body.firstChild;
		this.editingArea.style.height = this.height ? this.height : this.minHeight;
		if (!this.height) {
			this.connect(this, "onDisplayChanged", "_updateHeight");
		}
		this.window._frameElement = this.object;
	} else {
		if (this.iframe && !dojo.render.html.ie) {
			this.editNode = this.document.body;
			if (!this.height) {
				this.connect(this, "onDisplayChanged", "_updateHeight");
			}
			try {
				this.document.execCommand("useCSS", false, true);
				this.document.execCommand("styleWithCSS", false, false);
			}
			catch (e2) {
			}
			if (dojo.render.html.safari) {
				this.connect(this.editNode, "onblur", "onBlur");
				this.connect(this.editNode, "onfocus", "onFocus");
				this.connect(this.editNode, "onclick", "onFocus");
				this.interval = setInterval(dojo.lang.hitch(this, "onDisplayChanged"), 750);
			} else {
				if (dojo.render.html.mozilla || dojo.render.html.opera) {
					var doc = this.document;
					var addListener = dojo.event.browser.addListener;
					var self = this;
					dojo.lang.forEach(this.events, function (e) {
						var l = addListener(self.document, e.substr(2).toLowerCase(), dojo.lang.hitch(self, e));
						if (e == "onBlur") {
							var unBlur = {unBlur:function (e) {
								dojo.event.browser.removeListener(doc, "blur", l);
							}};
							dojo.event.connect("before", self, "close", unBlur, "unBlur");
						}
					});
				}
			}
		} else {
			if (dojo.render.html.ie) {
				if (!this.height) {
					this.connect(this, "onDisplayChanged", "_updateHeight");
				}
				this.editNode.style.zoom = 1;
			}
		}
	}
	this._applyEditingAreaStyleSheets();
	if (this.focusOnLoad) {
		this.focus();
	}
	this.onDisplayChanged(e);
	if (this.onLoadDeferred) {
		this.onLoadDeferred.callback(true);
	}
}, onKeyDown:function (e) {
	if ((!e) && (this.object)) {
		e = dojo.event.browser.fixEvent(this.window.event);
	}
	if ((dojo.render.html.ie) && (e.keyCode == e.KEY_TAB)) {
		e.preventDefault();
		e.stopPropagation();
		this.execCommand((e.shiftKey ? "outdent" : "indent"));
	} else {
		if (dojo.render.html.ie) {
			if ((65 <= e.keyCode) && (e.keyCode <= 90)) {
				e.charCode = e.keyCode;
				this.onKeyPress(e);
			}
		}
	}
}, onKeyUp:function (e) {
	return;
}, KEY_CTRL:1, onKeyPress:function (e) {
	if ((!e) && (this.object)) {
		e = dojo.event.browser.fixEvent(this.window.event);
	}
	var modifiers = e.ctrlKey ? this.KEY_CTRL : 0;
	if (this._keyHandlers[e.key]) {
		var handlers = this._keyHandlers[e.key], i = 0, handler;
		while (handler = handlers[i++]) {
			if (modifiers == handler.modifiers) {
				e.preventDefault();
				handler.handler.call(this);
				break;
			}
		}
	}
	dojo.lang.setTimeout(this, this.onKeyPressed, 1, e);
}, addKeyHandler:function (key, modifiers, handler) {
	if (!(this._keyHandlers[key] instanceof Array)) {
		this._keyHandlers[key] = [];
	}
	this._keyHandlers[key].push({modifiers:modifiers || 0, handler:handler});
}, onKeyPressed:function (e) {
	this.onDisplayChanged();
}, onClick:function (e) {
	this.onDisplayChanged(e);
}, onBlur:function (e) {
}, _initialFocus:true, onFocus:function (e) {
	if ((dojo.render.html.mozilla) && (this._initialFocus)) {
		this._initialFocus = false;
		if (dojo.string.trim(this.editNode.innerHTML) == "&nbsp;") {
			this.placeCursorAtStart();
		}
	}
}, blur:function () {
	if (this.iframe) {
		this.window.blur();
	} else {
		if (this.object) {
			this.document.body.blur();
		} else {
			if (this.editNode) {
				this.editNode.blur();
			}
		}
	}
}, focus:function () {
	if (this.iframe && !dojo.render.html.ie) {
		this.window.focus();
	} else {
		if (this.object) {
			this.document.focus();
		} else {
			if (this.editNode && this.editNode.focus) {
				this.editNode.focus();
			} else {
				dojo.debug("Have no idea how to focus into the editor!");
			}
		}
	}
}, onDisplayChanged:function (e) {
}, _activeX:{command:{bold:5000, italic:5023, underline:5048, justifycenter:5024, justifyleft:5025, justifyright:5026, cut:5003, copy:5002, paste:5032, "delete":5004, undo:5049, redo:5033, removeformat:5034, selectall:5035, unlink:5050, indent:5018, outdent:5031, insertorderedlist:5030, insertunorderedlist:5051, inserttable:5022, insertcell:5019, insertcol:5020, insertrow:5021, deletecells:5005, deletecols:5006, deleterows:5007, mergecells:5029, splitcell:5047, setblockformat:5043, getblockformat:5011, getblockformatnames:5012, setfontname:5044, getfontname:5013, setfontsize:5045, getfontsize:5014, setbackcolor:5042, getbackcolor:5010, setforecolor:5046, getforecolor:5015, findtext:5008, font:5009, hyperlink:5016, image:5017, lockelement:5027, makeabsolute:5028, sendbackward:5036, bringforward:5037, sendbelowtext:5038, bringabovetext:5039, sendtoback:5040, bringtofront:5041, properties:5052}, ui:{"default":0, prompt:1, noprompt:2}, status:{notsupported:0, disabled:1, enabled:3, latched:7, ninched:11}, appearance:{flat:0, inset:1}, state:{unchecked:0, checked:1, gray:2}}, _normalizeCommand:function (cmd) {
	var drh = dojo.render.html;
	var command = cmd.toLowerCase();
	if (command == "formatblock") {
		if (drh.safari) {
			command = "heading";
		}
	} else {
		if (this.object) {
			switch (command) {
			  case "createlink":
				command = "hyperlink";
				break;
			  case "insertimage":
				command = "image";
				break;
			}
		} else {
			if (command == "hilitecolor" && !drh.mozilla) {
				command = "backcolor";
			}
		}
	}
	return command;
}, _safariIsLeopard:function () {
	var gt420 = false;
	if (dojo.render.html.safari) {
		var tmp = dojo.render.html.UA.split("AppleWebKit/")[1];
		var ver = parseFloat(tmp.split(" ")[0]);
		if (ver >= 420) {
			gt420 = true;
		}
	}
	return gt420;
}, queryCommandAvailable:function (command) {
	var ie = 1;
	var mozilla = 1 << 1;
	var safari = 1 << 2;
	var opera = 1 << 3;
	var safari420 = 1 << 4;
	var gt420 = this._safariIsLeopard();
	function isSupportedBy(browsers) {
		return {ie:Boolean(browsers & ie), mozilla:Boolean(browsers & mozilla), safari:Boolean(browsers & safari), safari420:Boolean(browsers & safari420), opera:Boolean(browsers & opera)};
	}
	var supportedBy = null;
	switch (command.toLowerCase()) {
	  case "bold":
	  case "italic":
	  case "underline":
	  case "subscript":
	  case "superscript":
	  case "fontname":
	  case "fontsize":
	  case "forecolor":
	  case "hilitecolor":
	  case "justifycenter":
	  case "justifyfull":
	  case "justifyleft":
	  case "justifyright":
	  case "delete":
	  case "selectall":
		supportedBy = isSupportedBy(mozilla | ie | safari | opera);
		break;
	  case "createlink":
	  case "unlink":
	  case "removeformat":
	  case "inserthorizontalrule":
	  case "insertimage":
	  case "insertorderedlist":
	  case "insertunorderedlist":
	  case "indent":
	  case "outdent":
	  case "formatblock":
	  case "inserthtml":
	  case "undo":
	  case "redo":
	  case "strikethrough":
		supportedBy = isSupportedBy(mozilla | ie | opera | safari420);
		break;
	  case "blockdirltr":
	  case "blockdirrtl":
	  case "dirltr":
	  case "dirrtl":
	  case "inlinedirltr":
	  case "inlinedirrtl":
		supportedBy = isSupportedBy(ie);
		break;
	  case "cut":
	  case "copy":
	  case "paste":
		supportedBy = isSupportedBy(ie | mozilla | safari420);
		break;
	  case "inserttable":
		supportedBy = isSupportedBy(mozilla | (this.object ? ie : 0));
		break;
	  case "insertcell":
	  case "insertcol":
	  case "insertrow":
	  case "deletecells":
	  case "deletecols":
	  case "deleterows":
	  case "mergecells":
	  case "splitcell":
		supportedBy = isSupportedBy(this.object ? ie : 0);
		break;
	  default:
		return false;
	}
	return (dojo.render.html.ie && supportedBy.ie) || (dojo.render.html.mozilla && supportedBy.mozilla) || (dojo.render.html.safari && supportedBy.safari) || (gt420 && supportedBy.safari420) || (dojo.render.html.opera && supportedBy.opera);
}, execCommand:function (command, argument) {
	var returnValue;
	this.focus();
	command = this._normalizeCommand(command);
	if (argument != undefined) {
		if (command == "heading") {
			throw new Error("unimplemented");
		} else {
			if (command == "formatblock") {
				if (this.object) {
					argument = this._native2LocalFormatNames[argument];
				} else {
					if (dojo.render.html.ie) {
						argument = "<" + argument + ">";
					}
				}
			}
		}
	}
	if (this.object) {
		switch (command) {
		  case "hilitecolor":
			command = "setbackcolor";
			break;
		  case "forecolor":
		  case "backcolor":
		  case "fontsize":
		  case "fontname":
			command = "set" + command;
			break;
		  case "formatblock":
			command = "setblockformat";
		}
		if (command == "strikethrough") {
			command = "inserthtml";
			var range = this.document.selection.createRange();
			if (!range.htmlText) {
				return;
			}
			argument = range.htmlText.strike();
		} else {
			if (command == "inserthorizontalrule") {
				command = "inserthtml";
				argument = "<hr>";
			}
		}
		if (command == "inserthtml") {
			var range = this.document.selection.createRange();
			if (this.document.selection.type.toUpperCase() == "CONTROL") {
				for (var i = 0; i < range.length; i++) {
					range.item(i).outerHTML = argument;
				}
			} else {
				range.pasteHTML(argument);
				range.select();
			}
			returnValue = true;
		} else {
			if (arguments.length == 1) {
				returnValue = this.object.ExecCommand(this._activeX.command[command], this._activeX.ui.noprompt);
			} else {
				returnValue = this.object.ExecCommand(this._activeX.command[command], this._activeX.ui.noprompt, argument);
			}
		}
	} else {
		if (command == "inserthtml") {
			if (dojo.render.html.ie) {
				var insertRange = this.document.selection.createRange();
				insertRange.pasteHTML(argument);
				insertRange.select();
				return true;
			} else {
				return this.document.execCommand(command, false, argument);
			}
		} else {
			if ((command == "unlink") && (this.queryCommandEnabled("unlink")) && (dojo.render.html.mozilla)) {
				var selection = this.window.getSelection();
				var selectionRange = selection.getRangeAt(0);
				var selectionStartContainer = selectionRange.startContainer;
				var selectionStartOffset = selectionRange.startOffset;
				var selectionEndContainer = selectionRange.endContainer;
				var selectionEndOffset = selectionRange.endOffset;
				var a = dojo.withGlobal(this.window, "getAncestorElement", dojo.html.selection, ["a"]);
				dojo.withGlobal(this.window, "selectElement", dojo.html.selection, [a]);
				returnValue = this.document.execCommand("unlink", false, null);
				var selectionRange = this.document.createRange();
				selectionRange.setStart(selectionStartContainer, selectionStartOffset);
				selectionRange.setEnd(selectionEndContainer, selectionEndOffset);
				selection.removeAllRanges();
				selection.addRange(selectionRange);
				return returnValue;
			} else {
				if ((command == "hilitecolor") && (dojo.render.html.mozilla)) {
					this.document.execCommand("useCSS", false, false);
					returnValue = this.document.execCommand(command, false, argument);
					this.document.execCommand("useCSS", false, true);
				} else {
					if ((dojo.render.html.ie) && ((command == "backcolor") || (command == "forecolor"))) {
						argument = arguments.length > 1 ? argument : null;
						returnValue = this.document.execCommand(command, false, argument);
					} else {
						argument = arguments.length > 1 ? argument : null;
						if (argument || command != "createlink") {
							returnValue = this.document.execCommand(command, false, argument);
						}
					}
				}
			}
		}
	}
	this.onDisplayChanged();
	return returnValue;
}, queryCommandEnabled:function (command) {
	command = this._normalizeCommand(command);
	if (this.object) {
		switch (command) {
		  case "hilitecolor":
			command = "setbackcolor";
			break;
		  case "forecolor":
		  case "backcolor":
		  case "fontsize":
		  case "fontname":
			command = "set" + command;
			break;
		  case "formatblock":
			command = "setblockformat";
			break;
		  case "strikethrough":
			command = "bold";
			break;
		  case "inserthorizontalrule":
			return true;
		}
		if (typeof this._activeX.command[command] == "undefined") {
			return false;
		}
		var status = this.object.QueryStatus(this._activeX.command[command]);
		return ((status != this._activeX.status.notsupported) && (status != this._activeX.status.disabled));
	} else {
		if (dojo.render.html.mozilla) {
			if (command == "unlink") {
				return dojo.withGlobal(this.window, "hasAncestorElement", dojo.html.selection, ["a"]);
			} else {
				if (command == "inserttable") {
					return true;
				}
			}
		}
		var elem = (dojo.render.html.ie) ? this.document.selection.createRange() : this.document;
		return elem.queryCommandEnabled(command);
	}
}, queryCommandState:function (command) {
	command = this._normalizeCommand(command);
	if (this.object) {
		if (command == "forecolor") {
			command = "setforecolor";
		} else {
			if (command == "backcolor") {
				command = "setbackcolor";
			} else {
				if (command == "strikethrough") {
					return dojo.withGlobal(this.window, "hasAncestorElement", dojo.html.selection, ["strike"]);
				} else {
					if (command == "inserthorizontalrule") {
						return false;
					}
				}
			}
		}
		if (typeof this._activeX.command[command] == "undefined") {
			return null;
		}
		var status = this.object.QueryStatus(this._activeX.command[command]);
		return ((status == this._activeX.status.latched) || (status == this._activeX.status.ninched));
	} else {
		return this.document.queryCommandState(command);
	}
}, queryCommandValue:function (command) {
	command = this._normalizeCommand(command);
	if (this.object) {
		switch (command) {
		  case "forecolor":
		  case "backcolor":
		  case "fontsize":
		  case "fontname":
			command = "get" + command;
			return this.object.execCommand(this._activeX.command[command], this._activeX.ui.noprompt);
		  case "formatblock":
			var retvalue = this.object.execCommand(this._activeX.command["getblockformat"], this._activeX.ui.noprompt);
			if (retvalue) {
				return this._local2NativeFormatNames[retvalue];
			}
		}
	} else {
		if (dojo.render.html.ie && command == "formatblock") {
			return this._local2NativeFormatNames[this.document.queryCommandValue(command)] || this.document.queryCommandValue(command);
		}
		return this.document.queryCommandValue(command);
	}
}, placeCursorAtStart:function () {
	this.focus();
	if (dojo.render.html.moz && this.editNode.firstChild && this.editNode.firstChild.nodeType != dojo.dom.TEXT_NODE) {
		dojo.withGlobal(this.window, "selectElementChildren", dojo.html.selection, [this.editNode.firstChild]);
	} else {
		dojo.withGlobal(this.window, "selectElementChildren", dojo.html.selection, [this.editNode]);
	}
	dojo.withGlobal(this.window, "collapse", dojo.html.selection, [true]);
}, placeCursorAtEnd:function () {
	this.focus();
	if (dojo.render.html.moz && this.editNode.lastChild && this.editNode.lastChild.nodeType != dojo.dom.TEXT_NODE) {
		dojo.withGlobal(this.window, "selectElementChildren", dojo.html.selection, [this.editNode.lastChild]);
	} else {
		dojo.withGlobal(this.window, "selectElementChildren", dojo.html.selection, [this.editNode]);
	}
	dojo.withGlobal(this.window, "collapse", dojo.html.selection, [false]);
}, replaceEditorContent:function (html) {
	html = this._preFilterContent(html);
	if (this.isClosed) {
		this.domNode.innerHTML = html;
	} else {
		if (this.window && this.window.getSelection && !dojo.render.html.moz) {
			this.editNode.innerHTML = html;
		} else {
			if ((this.window && this.window.getSelection) || (this.document && this.document.selection)) {
				this.execCommand("selectall");
				if (dojo.render.html.moz && !html) {
					html = "&nbsp;";
				}
				this.execCommand("inserthtml", html);
			}
		}
	}
}, _preFilterContent:function (html) {
	var ec = html;
	dojo.lang.forEach(this.contentPreFilters, function (ef) {
		ec = ef(ec);
	});
	if (this.contentDomPreFilters.length > 0) {
		var dom = dojo.doc().createElement("div");
		dom.style.display = "none";
		dojo.body().appendChild(dom);
		dom.innerHTML = ec;
		dojo.lang.forEach(this.contentDomPreFilters, function (ef) {
			dom = ef(dom);
		});
		ec = dom.innerHTML;
		dojo.body().removeChild(dom);
	}
	return ec;
}, _postFilterContent:function (html) {
	var ec = html;
	if (this.contentDomPostFilters.length > 0) {
		var dom = this.document.createElement("div");
		dom.innerHTML = ec;
		dojo.lang.forEach(this.contentDomPostFilters, function (ef) {
			dom = ef(dom);
		});
		ec = dom.innerHTML;
	}
	dojo.lang.forEach(this.contentPostFilters, function (ef) {
		ec = ef(ec);
	});
	return ec;
}, _lastHeight:0, _updateHeight:function () {
	if (!this.isLoaded) {
		return;
	}
	if (this.height) {
		return;
	}
	var height = dojo.html.getBorderBox(this.editNode).height;
	if (!height) {
		height = dojo.html.getBorderBox(this.document.body).height;
	}
	if (height == 0) {
		dojo.debug("Can not figure out the height of the editing area!");
		return;
	}
	this._lastHeight = height;
	this.editorObject.style.height = this._lastHeight + "px";
	this.window.scrollTo(0, 0);
}, _saveContent:function (e) {
	var saveTextarea = dojo.doc().getElementById("dojo.widget.RichText.savedContent");
	saveTextarea.value += this._SEPARATOR + this.saveName + ":" + this.getEditorContent();
}, getEditorContent:function () {
	var ec = "";
	try {
		ec = (this._content.length > 0) ? this._content : this.editNode.innerHTML;
		if (dojo.string.trim(ec) == "&nbsp;") {
			ec = "";
		}
	}
	catch (e) {
	}
	if (dojo.render.html.ie && !this.object) {
		var re = new RegExp("(?:<p>&nbsp;</p>[\n\r]*)+$", "i");
		ec = ec.replace(re, "");
	}
	ec = this._postFilterContent(ec);
	if (this.relativeImageUrls) {
		var siteBase = dojo.global().location.protocol + "//" + dojo.global().location.host;
		var pathBase = dojo.global().location.pathname;
		if (pathBase.match(/\/$/)) {
		} else {
			var pathParts = pathBase.split("/");
			if (pathParts.length) {
				pathParts.pop();
			}
			pathBase = pathParts.join("/") + "/";
		}
		var sameSite = new RegExp("(<img[^>]* src=[\"'])(" + siteBase + "(" + pathBase + ")?)", "ig");
		ec = ec.replace(sameSite, "$1");
	}
	return ec;
}, close:function (save, force) {
	if (this.isClosed) {
		return false;
	}
	if (arguments.length == 0) {
		save = true;
	}
	this._content = this._postFilterContent(this.editNode.innerHTML);
	var changed = (this.savedContent != this._content);
	if (this.interval) {
		clearInterval(this.interval);
	}
	if (dojo.render.html.ie && !this.object) {
		dojo.event.browser.clean(this.editNode);
	}
	if (this.iframe) {
		delete this.iframe;
	}
	if (this.textarea) {
		with (this.textarea.style) {
			position = "";
			left = top = "";
			if (dojo.render.html.ie) {
				overflow = this.__overflow;
				this.__overflow = null;
			}
		}
		if (save) {
			this.textarea.value = this._content;
		} else {
			this.textarea.value = this.savedContent;
		}
		dojo.html.removeNode(this.domNode);
		this.domNode = this.textarea;
	} else {
		if (save) {
			if (dojo.render.html.moz) {
				var nc = dojo.doc().createElement("span");
				this.domNode.appendChild(nc);
				nc.innerHTML = this.editNode.innerHTML;
			} else {
				this.domNode.innerHTML = this._content;
			}
		} else {
			this.domNode.innerHTML = this.savedContent;
		}
	}
	dojo.html.removeClass(this.domNode, "RichTextEditable");
	this.isClosed = true;
	this.isLoaded = false;
	delete this.editNode;
	if (this.window._frameElement) {
		this.window._frameElement = null;
	}
	this.window = null;
	this.document = null;
	this.object = null;
	this.editingArea = null;
	this.editorObject = null;
	return changed;
}, destroyRendering:function () {
}, destroy:function () {
	this.destroyRendering();
	if (!this.isClosed) {
		this.close(false);
	}
	dojo.widget.RichText.superclass.destroy.call(this);
}, connect:function (targetObj, targetFunc, thisFunc) {
	dojo.event.connect(targetObj, targetFunc, this, thisFunc);
}, disconnect:function (targetObj, targetFunc, thisFunc) {
	dojo.event.disconnect(targetObj, targetFunc, this, thisFunc);
}, disconnectAllWithRoot:function (targetObj) {
	dojo.deprecated("disconnectAllWithRoot", "is deprecated. No need to disconnect manually", "0.5");
}, _fixContentForMoz:function (html) {
	html = html.replace(/<strong([ \>])/gi, "<b$1");
	html = html.replace(/<\/strong>/gi, "</b>");
	html = html.replace(/<em([ \>])/gi, "<i$1");
	html = html.replace(/<\/em>/gi, "</i>");
	return html;
}});

