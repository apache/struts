/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.html.style");
dojo.require("dojo.html.common");
dojo.require("dojo.uri.Uri");
dojo.html.getClass = function (node) {
	node = dojo.byId(node);
	if (!node) {
		return "";
	}
	var cs = "";
	if (node.className) {
		cs = node.className;
	} else {
		if (dojo.html.hasAttribute(node, "class")) {
			cs = dojo.html.getAttribute(node, "class");
		}
	}
	return cs.replace(/^\s+|\s+$/g, "");
};
dojo.html.getClasses = function (node) {
	var c = dojo.html.getClass(node);
	return (c == "") ? [] : c.split(/\s+/g);
};
dojo.html.hasClass = function (node, classname) {
	return (new RegExp("(^|\\s+)" + classname + "(\\s+|$)")).test(dojo.html.getClass(node));
};
dojo.html.prependClass = function (node, classStr) {
	classStr += " " + dojo.html.getClass(node);
	return dojo.html.setClass(node, classStr);
};
dojo.html.addClass = function (node, classStr) {
	if (dojo.html.hasClass(node, classStr)) {
		return false;
	}
	classStr = (dojo.html.getClass(node) + " " + classStr).replace(/^\s+|\s+$/g, "");
	return dojo.html.setClass(node, classStr);
};
dojo.html.setClass = function (node, classStr) {
	node = dojo.byId(node);
	var cs = new String(classStr);
	try {
		if (typeof node.className == "string") {
			node.className = cs;
		} else {
			if (node.setAttribute) {
				node.setAttribute("class", classStr);
				node.className = cs;
			} else {
				return false;
			}
		}
	}
	catch (e) {
		dojo.debug("dojo.html.setClass() failed", e);
	}
	return true;
};
dojo.html.removeClass = function (node, classStr, allowPartialMatches) {
	try {
		if (!allowPartialMatches) {
			var newcs = dojo.html.getClass(node).replace(new RegExp("(^|\\s+)" + classStr + "(\\s+|$)"), "$1$2");
		} else {
			var newcs = dojo.html.getClass(node).replace(classStr, "");
		}
		dojo.html.setClass(node, newcs);
	}
	catch (e) {
		dojo.debug("dojo.html.removeClass() failed", e);
	}
	return true;
};
dojo.html.replaceClass = function (node, newClass, oldClass) {
	dojo.html.removeClass(node, oldClass);
	dojo.html.addClass(node, newClass);
};
dojo.html.classMatchType = {ContainsAll:0, ContainsAny:1, IsOnly:2};
dojo.html.getElementsByClass = function (classStr, parent, nodeType, classMatchType, useNonXpath) {
	useNonXpath = false;
	var _document = dojo.doc();
	parent = dojo.byId(parent) || _document;
	var classes = classStr.split(/\s+/g);
	var nodes = [];
	if (classMatchType != 1 && classMatchType != 2) {
		classMatchType = 0;
	}
	var reClass = new RegExp("(\\s|^)((" + classes.join(")|(") + "))(\\s|$)");
	var srtLength = classes.join(" ").length;
	var candidateNodes = [];
	if (!useNonXpath && _document.evaluate) {
		var xpath = ".//" + (nodeType || "*") + "[contains(";
		if (classMatchType != dojo.html.classMatchType.ContainsAny) {
			xpath += "concat(' ',@class,' '), ' " + classes.join(" ') and contains(concat(' ',@class,' '), ' ") + " ')";
			if (classMatchType == 2) {
				xpath += " and string-length(@class)=" + srtLength + "]";
			} else {
				xpath += "]";
			}
		} else {
			xpath += "concat(' ',@class,' '), ' " + classes.join(" ') or contains(concat(' ',@class,' '), ' ") + " ')]";
		}
		var xpathResult = _document.evaluate(xpath, parent, null, XPathResult.ANY_TYPE, null);
		var result = xpathResult.iterateNext();
		while (result) {
			try {
				candidateNodes.push(result);
				result = xpathResult.iterateNext();
			}
			catch (e) {
				break;
			}
		}
		return candidateNodes;
	} else {
		if (!nodeType) {
			nodeType = "*";
		}
		candidateNodes = parent.getElementsByTagName(nodeType);
		var node, i = 0;
	outer:
		while (node = candidateNodes[i++]) {
			var nodeClasses = dojo.html.getClasses(node);
			if (nodeClasses.length == 0) {
				continue outer;
			}
			var matches = 0;
			for (var j = 0; j < nodeClasses.length; j++) {
				if (reClass.test(nodeClasses[j])) {
					if (classMatchType == dojo.html.classMatchType.ContainsAny) {
						nodes.push(node);
						continue outer;
					} else {
						matches++;
					}
				} else {
					if (classMatchType == dojo.html.classMatchType.IsOnly) {
						continue outer;
					}
				}
			}
			if (matches == classes.length) {
				if ((classMatchType == dojo.html.classMatchType.IsOnly) && (matches == nodeClasses.length)) {
					nodes.push(node);
				} else {
					if (classMatchType == dojo.html.classMatchType.ContainsAll) {
						nodes.push(node);
					}
				}
			}
		}
		return nodes;
	}
};
dojo.html.getElementsByClassName = dojo.html.getElementsByClass;
dojo.html.toCamelCase = function (selector) {
	var arr = selector.split("-"), cc = arr[0];
	for (var i = 1; i < arr.length; i++) {
		cc += arr[i].charAt(0).toUpperCase() + arr[i].substring(1);
	}
	return cc;
};
dojo.html.toSelectorCase = function (selector) {
	return selector.replace(/([A-Z])/g, "-$1").toLowerCase();
};
if (dojo.render.html.ie) {
	dojo.html.getComputedStyle = function (node, property, value) {
		node = dojo.byId(node);
		if (!node || !node.currentStyle) {
			return value;
		}
		return node.currentStyle[dojo.html.toCamelCase(property)];
	};
	dojo.html.getComputedStyles = function (node) {
		return node.currentStyle;
	};
} else {
	dojo.html.getComputedStyle = function (node, property, value) {
		node = dojo.byId(node);
		if (!node || !node.style) {
			return value;
		}
		var s = document.defaultView.getComputedStyle(node, null);
		return (s && s[dojo.html.toCamelCase(property)]) || "";
	};
	dojo.html.getComputedStyles = function (node) {
		return document.defaultView.getComputedStyle(node, null);
	};
}
dojo.html.getStyleProperty = function (node, cssSelector) {
	node = dojo.byId(node);
	return (node && node.style ? node.style[dojo.html.toCamelCase(cssSelector)] : undefined);
};
dojo.html.getStyle = function (node, cssSelector) {
	var value = dojo.html.getStyleProperty(node, cssSelector);
	return (value ? value : dojo.html.getComputedStyle(node, cssSelector));
};
dojo.html.setStyle = function (node, cssSelector, value) {
	node = dojo.byId(node);
	if (node && node.style) {
		var camelCased = dojo.html.toCamelCase(cssSelector);
		node.style[camelCased] = value;
	}
};
dojo.html.setStyleText = function (target, text) {
	try {
		target.style.cssText = text;
	}
	catch (e) {
		target.setAttribute("style", text);
	}
};
dojo.html.copyStyle = function (target, source) {
	if (!source.style.cssText) {
		target.setAttribute("style", source.getAttribute("style"));
	} else {
		target.style.cssText = source.style.cssText;
	}
	dojo.html.addClass(target, dojo.html.getClass(source));
};
dojo.html.getUnitValue = function (node, cssSelector, autoIsZero) {
	var s = dojo.html.getComputedStyle(node, cssSelector);
	if ((!s) || ((s == "auto") && (autoIsZero))) {
		return {value:0, units:"px"};
	}
	var match = s.match(/(\-?[\d.]+)([a-z%]*)/i);
	if (!match) {
		return dojo.html.getUnitValue.bad;
	}
	return {value:Number(match[1]), units:match[2].toLowerCase()};
};
dojo.html.getUnitValue.bad = {value:NaN, units:""};
if (dojo.render.html.ie) {
	dojo.html.toPixelValue = function (element, styleValue) {
		if (!styleValue) {
			return 0;
		}
		if (styleValue.slice(-2) == "px") {
			return parseFloat(styleValue);
		}
		var pixelValue = 0;
		with (element) {
			var sLeft = style.left;
			var rsLeft = runtimeStyle.left;
			runtimeStyle.left = currentStyle.left;
			try {
				style.left = styleValue || 0;
				pixelValue = style.pixelLeft;
				style.left = sLeft;
				runtimeStyle.left = rsLeft;
			}
			catch (e) {
			}
		}
		return pixelValue;
	};
} else {
	dojo.html.toPixelValue = function (element, styleValue) {
		return (styleValue && (styleValue.slice(-2) == "px") ? parseFloat(styleValue) : 0);
	};
}
dojo.html.getPixelValue = function (node, styleProperty, autoIsZero) {
	return dojo.html.toPixelValue(node, dojo.html.getComputedStyle(node, styleProperty));
};
dojo.html.setPositivePixelValue = function (node, selector, value) {
	if (isNaN(value)) {
		return false;
	}
	node.style[selector] = Math.max(0, value) + "px";
	return true;
};
dojo.html.styleSheet = null;
dojo.html.insertCssRule = function (selector, declaration, index) {
	if (!dojo.html.styleSheet) {
		if (document.createStyleSheet) {
			dojo.html.styleSheet = document.createStyleSheet();
		} else {
			if (document.styleSheets[0]) {
				dojo.html.styleSheet = document.styleSheets[0];
			} else {
				return null;
			}
		}
	}
	if (arguments.length < 3) {
		if (dojo.html.styleSheet.cssRules) {
			index = dojo.html.styleSheet.cssRules.length;
		} else {
			if (dojo.html.styleSheet.rules) {
				index = dojo.html.styleSheet.rules.length;
			} else {
				return null;
			}
		}
	}
	if (dojo.html.styleSheet.insertRule) {
		var rule = selector + " { " + declaration + " }";
		return dojo.html.styleSheet.insertRule(rule, index);
	} else {
		if (dojo.html.styleSheet.addRule) {
			return dojo.html.styleSheet.addRule(selector, declaration, index);
		} else {
			return null;
		}
	}
};
dojo.html.removeCssRule = function (index) {
	if (!dojo.html.styleSheet) {
		dojo.debug("no stylesheet defined for removing rules");
		return false;
	}
	if (dojo.render.html.ie) {
		if (!index) {
			index = dojo.html.styleSheet.rules.length;
			dojo.html.styleSheet.removeRule(index);
		}
	} else {
		if (document.styleSheets[0]) {
			if (!index) {
				index = dojo.html.styleSheet.cssRules.length;
			}
			dojo.html.styleSheet.deleteRule(index);
		}
	}
	return true;
};
dojo.html._insertedCssFiles = [];
dojo.html.insertCssFile = function (URI, doc, checkDuplicates, fail_ok) {
	if (!URI) {
		return;
	}
	if (!doc) {
		doc = document;
	}
	var cssStr = dojo.hostenv.getText(URI, false, fail_ok);
	if (cssStr === null) {
		return;
	}
	cssStr = dojo.html.fixPathsInCssText(cssStr, URI);
	if (checkDuplicates) {
		var idx = -1, node, ent = dojo.html._insertedCssFiles;
		for (var i = 0; i < ent.length; i++) {
			if ((ent[i].doc == doc) && (ent[i].cssText == cssStr)) {
				idx = i;
				node = ent[i].nodeRef;
				break;
			}
		}
		if (node) {
			var styles = doc.getElementsByTagName("style");
			for (var i = 0; i < styles.length; i++) {
				if (styles[i] == node) {
					return;
				}
			}
			dojo.html._insertedCssFiles.shift(idx, 1);
		}
	}
	var style = dojo.html.insertCssText(cssStr, doc);
	dojo.html._insertedCssFiles.push({"doc":doc, "cssText":cssStr, "nodeRef":style});
	if (style && djConfig.isDebug) {
		style.setAttribute("dbgHref", URI);
	}
	return style;
};
dojo.html.insertCssText = function (cssStr, doc, URI) {
	if (!cssStr) {
		return;
	}
	if (!doc) {
		doc = document;
	}
	if (URI) {
		cssStr = dojo.html.fixPathsInCssText(cssStr, URI);
	}
	var style = doc.createElement("style");
	style.setAttribute("type", "text/css");
	var head = doc.getElementsByTagName("head")[0];
	if (!head) {
		dojo.debug("No head tag in document, aborting styles");
		return;
	} else {
		head.appendChild(style);
	}
	if (style.styleSheet) {
		var setFunc = function () {
			try {
				style.styleSheet.cssText = cssStr;
			}
			catch (e) {
				dojo.debug(e);
			}
		};
		if (style.styleSheet.disabled) {
			setTimeout(setFunc, 10);
		} else {
			setFunc();
		}
	} else {
		var cssText = doc.createTextNode(cssStr);
		style.appendChild(cssText);
	}
	return style;
};
dojo.html.fixPathsInCssText = function (cssStr, URI) {
	if (!cssStr || !URI) {
		return;
	}
	var match, str = "", url = "", urlChrs = "[\\t\\s\\w\\(\\)\\/\\.\\\\'\"-:#=&?~]+";
	var regex = new RegExp("url\\(\\s*(" + urlChrs + ")\\s*\\)");
	var regexProtocol = /(file|https?|ftps?):\/\//;
	regexTrim = new RegExp("^[\\s]*(['\"]?)(" + urlChrs + ")\\1[\\s]*?$");
	if (dojo.render.html.ie55 || dojo.render.html.ie60) {
		var regexIe = new RegExp("AlphaImageLoader\\((.*)src=['\"](" + urlChrs + ")['\"]");
		while (match = regexIe.exec(cssStr)) {
			url = match[2].replace(regexTrim, "$2");
			if (!regexProtocol.exec(url)) {
				url = (new dojo.uri.Uri(URI, url).toString());
			}
			str += cssStr.substring(0, match.index) + "AlphaImageLoader(" + match[1] + "src='" + url + "'";
			cssStr = cssStr.substr(match.index + match[0].length);
		}
		cssStr = str + cssStr;
		str = "";
	}
	while (match = regex.exec(cssStr)) {
		url = match[1].replace(regexTrim, "$2");
		if (!regexProtocol.exec(url)) {
			url = (new dojo.uri.Uri(URI, url).toString());
		}
		str += cssStr.substring(0, match.index) + "url(" + url + ")";
		cssStr = cssStr.substr(match.index + match[0].length);
	}
	return str + cssStr;
};
dojo.html.setActiveStyleSheet = function (title) {
	var i = 0, a, els = dojo.doc().getElementsByTagName("link");
	while (a = els[i++]) {
		if (a.getAttribute("rel").indexOf("style") != -1 && a.getAttribute("title")) {
			a.disabled = true;
			if (a.getAttribute("title") == title) {
				a.disabled = false;
			}
		}
	}
};
dojo.html.getActiveStyleSheet = function () {
	var i = 0, a, els = dojo.doc().getElementsByTagName("link");
	while (a = els[i++]) {
		if (a.getAttribute("rel").indexOf("style") != -1 && a.getAttribute("title") && !a.disabled) {
			return a.getAttribute("title");
		}
	}
	return null;
};
dojo.html.getPreferredStyleSheet = function () {
	var i = 0, a, els = dojo.doc().getElementsByTagName("link");
	while (a = els[i++]) {
		if (a.getAttribute("rel").indexOf("style") != -1 && a.getAttribute("rel").indexOf("alt") == -1 && a.getAttribute("title")) {
			return a.getAttribute("title");
		}
	}
	return null;
};
dojo.html.applyBrowserClass = function (node) {
	var drh = dojo.render.html;
	var classes = {dj_ie:drh.ie, dj_ie55:drh.ie55, dj_ie6:drh.ie60, dj_ie7:drh.ie70, dj_iequirks:drh.ie && drh.quirks, dj_opera:drh.opera, dj_opera8:drh.opera && (Math.floor(dojo.render.version) == 8), dj_opera9:drh.opera && (Math.floor(dojo.render.version) == 9), dj_khtml:drh.khtml, dj_safari:drh.safari, dj_gecko:drh.mozilla};
	for (var p in classes) {
		if (classes[p]) {
			dojo.html.addClass(node, p);
		}
	}
};

