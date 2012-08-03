/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.html.common");
dojo.require("dojo.lang.common");
dojo.require("dojo.dom");
dojo.lang.mixin(dojo.html, dojo.dom);
dojo.html.body = function () {
	dojo.deprecated("dojo.html.body() moved to dojo.body()", "0.5");
	return dojo.body();
};
dojo.html.getEventTarget = function (evt) {
	if (!evt) {
		evt = dojo.global().event || {};
	}
	var t = (evt.srcElement ? evt.srcElement : (evt.target ? evt.target : null));
	while ((t) && (t.nodeType != 1)) {
		t = t.parentNode;
	}
	return t;
};
dojo.html.getViewport = function () {
	var _window = dojo.global();
	var _document = dojo.doc();
	var w = 0;
	var h = 0;
	if (dojo.render.html.mozilla) {
		w = _document.documentElement.clientWidth;
		h = _window.innerHeight;
	} else {
		if (!dojo.render.html.opera && _window.innerWidth) {
			w = _window.innerWidth;
			h = _window.innerHeight;
		} else {
			if (!dojo.render.html.opera && dojo.exists(_document, "documentElement.clientWidth")) {
				var w2 = _document.documentElement.clientWidth;
				if (!w || w2 && w2 < w) {
					w = w2;
				}
				h = _document.documentElement.clientHeight;
			} else {
				if (dojo.body().clientWidth) {
					w = dojo.body().clientWidth;
					h = dojo.body().clientHeight;
				}
			}
		}
	}
	return {width:w, height:h};
};
dojo.html.getScroll = function () {
	var _window = dojo.global();
	var _document = dojo.doc();
	var top = _window.pageYOffset || _document.documentElement.scrollTop || dojo.body().scrollTop || 0;
	var left = _window.pageXOffset || _document.documentElement.scrollLeft || dojo.body().scrollLeft || 0;
	return {top:top, left:left, offset:{x:left, y:top}};
};
dojo.html.getParentByType = function (node, type) {
	var _document = dojo.doc();
	var parent = dojo.byId(node);
	type = type.toLowerCase();
	while ((parent) && (parent.nodeName.toLowerCase() != type)) {
		if (parent == (_document["body"] || _document["documentElement"])) {
			return null;
		}
		parent = parent.parentNode;
	}
	return parent;
};
dojo.html.getAttribute = function (node, attr) {
	node = dojo.byId(node);
	if ((!node) || (!node.getAttribute)) {
		return null;
	}
	var ta = typeof attr == "string" ? attr : new String(attr);
	var v = node.getAttribute(ta.toUpperCase());
	if ((v) && (typeof v == "string") && (v != "")) {
		return v;
	}
	if (v && v.value) {
		return v.value;
	}
	if ((node.getAttributeNode) && (node.getAttributeNode(ta))) {
		return (node.getAttributeNode(ta)).value;
	} else {
		if (node.getAttribute(ta)) {
			return node.getAttribute(ta);
		} else {
			if (node.getAttribute(ta.toLowerCase())) {
				return node.getAttribute(ta.toLowerCase());
			}
		}
	}
	return null;
};
dojo.html.hasAttribute = function (node, attr) {
	return dojo.html.getAttribute(dojo.byId(node), attr) ? true : false;
};
dojo.html.getCursorPosition = function (e) {
	e = e || dojo.global().event;
	var cursor = {x:0, y:0};
	if (e.pageX || e.pageY) {
		cursor.x = e.pageX;
		cursor.y = e.pageY;
	} else {
		var de = dojo.doc().documentElement;
		var db = dojo.body();
		cursor.x = e.clientX + ((de || db)["scrollLeft"]) - ((de || db)["clientLeft"]);
		cursor.y = e.clientY + ((de || db)["scrollTop"]) - ((de || db)["clientTop"]);
	}
	return cursor;
};
dojo.html.isTag = function (node) {
	node = dojo.byId(node);
	if (node && node.tagName) {
		for (var i = 1; i < arguments.length; i++) {
			if (node.tagName.toLowerCase() == String(arguments[i]).toLowerCase()) {
				return String(arguments[i]).toLowerCase();
			}
		}
	}
	return "";
};
if (dojo.render.html.ie && !dojo.render.html.ie70) {
	if (window.location.href.substr(0, 6).toLowerCase() != "https:") {
		(function () {
			var xscript = dojo.doc().createElement("script");
			xscript.src = "javascript:'dojo.html.createExternalElement=function(doc, tag){ return doc.createElement(tag); }'";
			dojo.doc().getElementsByTagName("head")[0].appendChild(xscript);
		})();
	}
} else {
	dojo.html.createExternalElement = function (doc, tag) {
		return doc.createElement(tag);
	};
}
dojo.html._callDeprecated = function (inFunc, replFunc, args, argName, retValue) {
	dojo.deprecated("dojo.html." + inFunc, "replaced by dojo.html." + replFunc + "(" + (argName ? "node, {" + argName + ": " + argName + "}" : "") + ")" + (retValue ? "." + retValue : ""), "0.5");
	var newArgs = [];
	if (argName) {
		var argsIn = {};
		argsIn[argName] = args[1];
		newArgs.push(args[0]);
		newArgs.push(argsIn);
	} else {
		newArgs = args;
	}
	var ret = dojo.html[replFunc].apply(dojo.html, args);
	if (retValue) {
		return ret[retValue];
	} else {
		return ret;
	}
};
dojo.html.getViewportWidth = function () {
	return dojo.html._callDeprecated("getViewportWidth", "getViewport", arguments, null, "width");
};
dojo.html.getViewportHeight = function () {
	return dojo.html._callDeprecated("getViewportHeight", "getViewport", arguments, null, "height");
};
dojo.html.getViewportSize = function () {
	return dojo.html._callDeprecated("getViewportSize", "getViewport", arguments);
};
dojo.html.getScrollTop = function () {
	return dojo.html._callDeprecated("getScrollTop", "getScroll", arguments, null, "top");
};
dojo.html.getScrollLeft = function () {
	return dojo.html._callDeprecated("getScrollLeft", "getScroll", arguments, null, "left");
};
dojo.html.getScrollOffset = function () {
	return dojo.html._callDeprecated("getScrollOffset", "getScroll", arguments, null, "offset");
};

