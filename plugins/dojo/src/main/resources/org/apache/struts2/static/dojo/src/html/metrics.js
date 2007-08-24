/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.html.metrics");
dojo.require("dojo.html.layout");
dojo.html.getScrollbar = function () {
	var scroll = document.createElement("div");
	scroll.style.width = "100px";
	scroll.style.height = "100px";
	scroll.style.overflow = "scroll";
	scroll.style.position = "absolute";
	scroll.style.top = "-300px";
	scroll.style.left = "0px";
	var test = document.createElement("div");
	test.style.width = "400px";
	test.style.height = "400px";
	scroll.appendChild(test);
	dojo.body().appendChild(scroll);
	var width = scroll.offsetWidth - scroll.clientWidth;
	dojo.body().removeChild(scroll);
	scroll.removeChild(test);
	scroll = test = null;
	return {width:width};
};
dojo.html.getFontMeasurements = function () {
	var heights = {"1em":0, "1ex":0, "100%":0, "12pt":0, "16px":0, "xx-small":0, "x-small":0, "small":0, "medium":0, "large":0, "x-large":0, "xx-large":0};
	if (dojo.render.html.ie) {
		document.documentElement.style.fontSize = "100%";
	}
	var div = document.createElement("div");
	div.style.position = "absolute";
	div.style.left = "-100px";
	div.style.top = "0";
	div.style.width = "30px";
	div.style.height = "1000em";
	div.style.border = "0";
	div.style.margin = "0";
	div.style.padding = "0";
	div.style.outline = "0";
	div.style.lineHeight = "1";
	div.style.overflow = "hidden";
	dojo.body().appendChild(div);
	for (var p in heights) {
		div.style.fontSize = p;
		heights[p] = Math.round(div.offsetHeight * 12 / 16) * 16 / 12 / 1000;
	}
	dojo.body().removeChild(div);
	div = null;
	return heights;
};
dojo.html._fontMeasurements = null;
dojo.html.getCachedFontMeasurements = function (recalculate) {
	if (recalculate || !dojo.html._fontMeasurements) {
		dojo.html._fontMeasurements = dojo.html.getFontMeasurements();
	}
	return dojo.html._fontMeasurements;
};
dojo.html.measureFragment = function (node, html, boxType) {
	var clone = node.cloneNode(true);
	clone.innerHTML = html;
	node.parentNode.appendChild(clone);
	var ret = dojo.html.getElementBox(clone, boxType);
	node.parentNode.removeChild(clone);
	clone = null;
	return ret;
};
dojo.html.getFittedFragment = function (node, html) {
	function cl(node) {
		var element = document.createElement(node.tagName);
		element.id = node.id + "-clone";
		element.className = node.className;
		for (var j = 0; j < node.attributes.length; j++) {
			if (node.attributes[j].specified) {
				if (node.attributes[j].nodeName.toLowerCase() != "style" && node.attributes[j].nodeName.toLowerCase() != "edited" && node.attributes[j].nodeName.toLowerCase() != "contenteditable" && node.attributes[j].nodeName.toLowerCase() != "id" && node.attributes[j].nodeName.toLowerCase() != "class") {
					element.setAttribute(node.attributes[j].nodeName.toLowerCase(), node.attributes[j].nodeValue);
				}
			}
		}
		return element;
	}
	var height = dojo.html.getFontMeasurements()["16px"];
	var n = cl(node);
	n.style.width = dojo.html.getBorderBox(node).width + "px";
	n.style.height = (height + 4) + "px";
	node.parentNode.appendChild(n);
	var rem = dojo.html.fitToElement(n, html);
	var ret = n.innerHTML;
	n.parentNode.removeChild(n);
	return ret;
};
dojo.html.fitToElement = function (node, html) {
	function cl(node) {
		var element = document.createElement(node.tagName);
		element.id = node.id + "-clone";
		element.className = node.className;
		for (var j = 0; j < node.attributes.length; j++) {
			if (node.attributes[j].specified) {
				if (node.attributes[j].nodeName.toLowerCase() != "style" && node.attributes[j].nodeName.toLowerCase() != "edited" && node.attributes[j].nodeName.toLowerCase() != "contenteditable" && node.attributes[j].nodeName.toLowerCase() != "id" && node.attributes[j].nodeName.toLowerCase() != "class") {
					element.setAttribute(node.attributes[j].nodeName.toLowerCase(), node.attributes[j].nodeValue);
				}
			}
		}
		return element;
	}
	var clone = cl(node);
	node.parentNode.appendChild(clone);
	var t = dojo.html.getBorderBox(node);
	clone.style.width = t.width + "px";
	var singletons = ["br", "img", "hr", "input", "!--"];
	var chop = ["<BR>", "<br>", "<br/>", "<br />", "<p></p>", "<P></P>"];
	var openTags = [];
	var str = html;
	var i = 0;
	var limit = str.length;
	var add = 0;
	var doLoop = true;
	clone.innerHTML = str;
	while (doLoop) {
		add = Math.round((limit - i) / 2);
		if (add <= 1) {
			doLoop = false;
		}
		i += add;
		clone.innerHTML = str.substr(0, i);
		if (clone.offsetHeight > t.height) {
			limit = i;
			i -= add;
		}
	}
	if (str.substr(0, i) != str) {
		var lastSpace = str.substr(0, i).lastIndexOf(" ");
		var lastNewLine = str.substr(0, i).lastIndexOf("\n");
		var lastGreater = str.substr(0, i).lastIndexOf(">");
		var lastLess = str.substr(0, i).lastIndexOf("<");
		if (lastLess <= lastGreater && lastNewLine == i - 1) {
			i = i;
		} else {
			if (lastSpace != -1 && lastSpace > lastGreater && lastGreater > lastLess) {
				i = lastSpace + 1;
			} else {
				if (lastLess > lastGreater) {
					i = lastLess;
				} else {
					if (lastGreater != -1) {
						i = lastGreater + 1;
					}
				}
			}
		}
	}
	str = str.substr(0, i);
	var ret = html.substr(str.length);
	var doPush = true;
	var tags = str.split("<");
	tags.shift();
	for (var j = 0; j < tags.length; j++) {
		tags[j] = tags[j].split(">")[0];
		if (tags[j].charAt(tags[j].length - 1) == "/") {
			continue;
		}
		if (tags[j].charAt(0) != "/") {
			for (var k = 0; k < singletons.length; k++) {
				if (tags[j].split(" ")[0].toLowerCase() == singletons[k]) {
					doPush = false;
				}
			}
			if (doPush) {
				openTags.push(tags[j]);
			}
			doPush = true;
		} else {
			openTags.pop();
		}
	}
	for (var j = 0; j < chop.length; j++) {
		if (ret.charAt(0) == "\n") {
			ret = ret.substr(1);
		}
		while (ret.indexOf(chop[j]) == 0) {
			ret = ret.substr(chop[j].length);
		}
	}
	for (var j = openTags.length - 1; j >= 0; j--) {
		if (str.lastIndexOf(openTags[j]) == (str.length - openTags[j].length - 1)) {
			str = str.substring(0, str.lastIndexOf(openTags[j]));
		} else {
			str += "</" + openTags[j] + ">";
		}
		if (ret.length > 0) {
			ret = "<" + openTags[j] + ">" + ret;
		}
	}
	for (var j = 0; j < chop.length; j++) {
		if (ret.charAt(0) == "\n") {
			ret = ret.substr(1);
		}
		while (ret.indexOf(chop[j]) == 0) {
			ret = ret.substr(chop[j].length);
		}
	}
	node.innerHTML = str;
	clone.parentNode.removeChild(clone);
	clone = null;
	return ret;
};

