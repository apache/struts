/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.xml.Parse");
dojo.require("dojo.dom");
dojo.xml.Parse = function () {
	var isIE = ((dojo.render.html.capable) && (dojo.render.html.ie));
	function getTagName(node) {
		try {
			return node.tagName.toLowerCase();
		}
		catch (e) {
			return "";
		}
	}
	function getDojoTagName(node) {
		var tagName = getTagName(node);
		if (!tagName) {
			return "";
		}
		if ((dojo.widget) && (dojo.widget.tags[tagName])) {
			return tagName;
		}
		var p = tagName.indexOf(":");
		if (p >= 0) {
			return tagName;
		}
		if (tagName.substr(0, 5) == "dojo:") {
			return tagName;
		}
		if (dojo.render.html.capable && dojo.render.html.ie && node.scopeName != "HTML") {
			return node.scopeName.toLowerCase() + ":" + tagName;
		}
		if (tagName.substr(0, 4) == "dojo") {
			return "dojo:" + tagName.substring(4);
		}
		var djt = node.getAttribute("dojoType") || node.getAttribute("dojotype");
		if (djt) {
			if (djt.indexOf(":") < 0) {
				djt = "dojo:" + djt;
			}
			return djt.toLowerCase();
		}
		djt = node.getAttributeNS && node.getAttributeNS(dojo.dom.dojoml, "type");
		if (djt) {
			return "dojo:" + djt.toLowerCase();
		}
		try {
			djt = node.getAttribute("dojo:type");
		}
		catch (e) {
		}
		if (djt) {
			return "dojo:" + djt.toLowerCase();
		}
		if ((dj_global["djConfig"]) && (!djConfig["ignoreClassNames"])) {
			var classes = node.className || node.getAttribute("class");
			if ((classes) && (classes.indexOf) && (classes.indexOf("dojo-") != -1)) {
				var aclasses = classes.split(" ");
				for (var x = 0, c = aclasses.length; x < c; x++) {
					if (aclasses[x].slice(0, 5) == "dojo-") {
						return "dojo:" + aclasses[x].substr(5).toLowerCase();
					}
				}
			}
		}
		return "";
	}
	this.parseElement = function (node, hasParentNodeSet, optimizeForDojoML, thisIdx) {
		var tagName = getTagName(node);
		if (isIE && tagName.indexOf("/") == 0) {
			return null;
		}
		try {
			var attr = node.getAttribute("parseWidgets");
			if (attr && attr.toLowerCase() == "false") {
				return {};
			}
		}
		catch (e) {
		}
		var process = true;
		if (optimizeForDojoML) {
			var dojoTagName = getDojoTagName(node);
			tagName = dojoTagName || tagName;
			process = Boolean(dojoTagName);
		}
		var parsedNodeSet = {};
		parsedNodeSet[tagName] = [];
		var pos = tagName.indexOf(":");
		if (pos > 0) {
			var ns = tagName.substring(0, pos);
			parsedNodeSet["ns"] = ns;
			if ((dojo.ns) && (!dojo.ns.allow(ns))) {
				process = false;
			}
		}
		if (process) {
			var attributeSet = this.parseAttributes(node);
			for (var attr in attributeSet) {
				if ((!parsedNodeSet[tagName][attr]) || (typeof parsedNodeSet[tagName][attr] != "array")) {
					parsedNodeSet[tagName][attr] = [];
				}
				parsedNodeSet[tagName][attr].push(attributeSet[attr]);
			}
			parsedNodeSet[tagName].nodeRef = node;
			parsedNodeSet.tagName = tagName;
			parsedNodeSet.index = thisIdx || 0;
		}
		var count = 0;
		for (var i = 0; i < node.childNodes.length; i++) {
			var tcn = node.childNodes.item(i);
			switch (tcn.nodeType) {
			  case dojo.dom.ELEMENT_NODE:
				var ctn = getDojoTagName(tcn) || getTagName(tcn);
				if (!parsedNodeSet[ctn]) {
					parsedNodeSet[ctn] = [];
				}
				parsedNodeSet[ctn].push(this.parseElement(tcn, true, optimizeForDojoML, count));
				if ((tcn.childNodes.length == 1) && (tcn.childNodes.item(0).nodeType == dojo.dom.TEXT_NODE)) {
					parsedNodeSet[ctn][parsedNodeSet[ctn].length - 1].value = tcn.childNodes.item(0).nodeValue;
				}
				count++;
				break;
			  case dojo.dom.TEXT_NODE:
				if (node.childNodes.length == 1) {
					parsedNodeSet[tagName].push({value:node.childNodes.item(0).nodeValue});
				}
				break;
			  default:
				break;
			}
		}
		return parsedNodeSet;
	};
	this.parseAttributes = function (node) {
		var parsedAttributeSet = {};
		var atts = node.attributes;
		var attnode, i = 0;
		while ((attnode = atts[i++])) {
			if (isIE) {
				if (!attnode) {
					continue;
				}
				if ((typeof attnode == "object") && (typeof attnode.nodeValue == "undefined") || (attnode.nodeValue == null) || (attnode.nodeValue == "")) {
					continue;
				}
			}
			var nn = attnode.nodeName.split(":");
			nn = (nn.length == 2) ? nn[1] : attnode.nodeName;
			parsedAttributeSet[nn] = {value:attnode.nodeValue};
		}
		return parsedAttributeSet;
	};
};

