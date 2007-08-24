/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.html.layout");
dojo.require("dojo.html.common");
dojo.require("dojo.html.style");
dojo.require("dojo.html.display");
dojo.html.sumAncestorProperties = function (node, prop) {
	node = dojo.byId(node);
	if (!node) {
		return 0;
	}
	var retVal = 0;
	while (node) {
		if (dojo.html.getComputedStyle(node, "position") == "fixed") {
			return 0;
		}
		var val = node[prop];
		if (val) {
			retVal += val - 0;
			if (node == dojo.body()) {
				break;
			}
		}
		node = node.parentNode;
	}
	return retVal;
};
dojo.html.setStyleAttributes = function (node, attributes) {
	node = dojo.byId(node);
	var splittedAttribs = attributes.replace(/(;)?\s*$/, "").split(";");
	for (var i = 0; i < splittedAttribs.length; i++) {
		var nameValue = splittedAttribs[i].split(":");
		var name = nameValue[0].replace(/\s*$/, "").replace(/^\s*/, "").toLowerCase();
		var value = nameValue[1].replace(/\s*$/, "").replace(/^\s*/, "");
		switch (name) {
		  case "opacity":
			dojo.html.setOpacity(node, value);
			break;
		  case "content-height":
			dojo.html.setContentBox(node, {height:value});
			break;
		  case "content-width":
			dojo.html.setContentBox(node, {width:value});
			break;
		  case "outer-height":
			dojo.html.setMarginBox(node, {height:value});
			break;
		  case "outer-width":
			dojo.html.setMarginBox(node, {width:value});
			break;
		  default:
			node.style[dojo.html.toCamelCase(name)] = value;
		}
	}
};
dojo.html.boxSizing = {MARGIN_BOX:"margin-box", BORDER_BOX:"border-box", PADDING_BOX:"padding-box", CONTENT_BOX:"content-box"};
dojo.html.getAbsolutePosition = dojo.html.abs = function (node, includeScroll, boxType) {
	node = dojo.byId(node, node.ownerDocument);
	var ret = {x:0, y:0};
	var bs = dojo.html.boxSizing;
	if (!boxType) {
		boxType = bs.CONTENT_BOX;
	}
	var nativeBoxType = 2;
	var targetBoxType;
	switch (boxType) {
	  case bs.MARGIN_BOX:
		targetBoxType = 3;
		break;
	  case bs.BORDER_BOX:
		targetBoxType = 2;
		break;
	  case bs.PADDING_BOX:
	  default:
		targetBoxType = 1;
		break;
	  case bs.CONTENT_BOX:
		targetBoxType = 0;
		break;
	}
	var h = dojo.render.html;
	var db = document["body"] || document["documentElement"];
	if (h.ie) {
		with (node.getBoundingClientRect()) {
			ret.x = left - 2;
			ret.y = top - 2;
		}
	} else {
		if (document.getBoxObjectFor) {
			nativeBoxType = 1;
			try {
				var bo = document.getBoxObjectFor(node);
				ret.x = bo.x - dojo.html.sumAncestorProperties(node, "scrollLeft");
				ret.y = bo.y - dojo.html.sumAncestorProperties(node, "scrollTop");
			}
			catch (e) {
			}
		} else {
			if (node["offsetParent"]) {
				var endNode;
				if ((h.safari) && (node.style.getPropertyValue("position") == "absolute") && (node.parentNode == db)) {
					endNode = db;
				} else {
					endNode = db.parentNode;
				}
				if (node.parentNode != db) {
					var nd = node;
					if (dojo.render.html.opera) {
						nd = db;
					}
					ret.x -= dojo.html.sumAncestorProperties(nd, "scrollLeft");
					ret.y -= dojo.html.sumAncestorProperties(nd, "scrollTop");
				}
				var curnode = node;
				do {
					var n = curnode["offsetLeft"];
					if (!h.opera || n > 0) {
						ret.x += isNaN(n) ? 0 : n;
					}
					var m = curnode["offsetTop"];
					ret.y += isNaN(m) ? 0 : m;
					curnode = curnode.offsetParent;
				} while ((curnode != endNode) && (curnode != null));
			} else {
				if (node["x"] && node["y"]) {
					ret.x += isNaN(node.x) ? 0 : node.x;
					ret.y += isNaN(node.y) ? 0 : node.y;
				}
			}
		}
	}
	if (includeScroll) {
		var scroll = dojo.html.getScroll();
		ret.y += scroll.top;
		ret.x += scroll.left;
	}
	var extentFuncArray = [dojo.html.getPaddingExtent, dojo.html.getBorderExtent, dojo.html.getMarginExtent];
	if (nativeBoxType > targetBoxType) {
		for (var i = targetBoxType; i < nativeBoxType; ++i) {
			ret.y += extentFuncArray[i](node, "top");
			ret.x += extentFuncArray[i](node, "left");
		}
	} else {
		if (nativeBoxType < targetBoxType) {
			for (var i = targetBoxType; i > nativeBoxType; --i) {
				ret.y -= extentFuncArray[i - 1](node, "top");
				ret.x -= extentFuncArray[i - 1](node, "left");
			}
		}
	}
	ret.top = ret.y;
	ret.left = ret.x;
	return ret;
};
dojo.html.isPositionAbsolute = function (node) {
	return (dojo.html.getComputedStyle(node, "position") == "absolute");
};
dojo.html._sumPixelValues = function (node, selectors, autoIsZero) {
	var total = 0;
	for (var x = 0; x < selectors.length; x++) {
		total += dojo.html.getPixelValue(node, selectors[x], autoIsZero);
	}
	return total;
};
dojo.html.getMargin = function (node) {
	return {width:dojo.html._sumPixelValues(node, ["margin-left", "margin-right"], (dojo.html.getComputedStyle(node, "position") == "absolute")), height:dojo.html._sumPixelValues(node, ["margin-top", "margin-bottom"], (dojo.html.getComputedStyle(node, "position") == "absolute"))};
};
dojo.html.getBorder = function (node) {
	return {width:dojo.html.getBorderExtent(node, "left") + dojo.html.getBorderExtent(node, "right"), height:dojo.html.getBorderExtent(node, "top") + dojo.html.getBorderExtent(node, "bottom")};
};
dojo.html.getBorderExtent = function (node, side) {
	return (dojo.html.getStyle(node, "border-" + side + "-style") == "none" ? 0 : dojo.html.getPixelValue(node, "border-" + side + "-width"));
};
dojo.html.getMarginExtent = function (node, side) {
	return dojo.html._sumPixelValues(node, ["margin-" + side], dojo.html.isPositionAbsolute(node));
};
dojo.html.getPaddingExtent = function (node, side) {
	return dojo.html._sumPixelValues(node, ["padding-" + side], true);
};
dojo.html.getPadding = function (node) {
	return {width:dojo.html._sumPixelValues(node, ["padding-left", "padding-right"], true), height:dojo.html._sumPixelValues(node, ["padding-top", "padding-bottom"], true)};
};
dojo.html.getPadBorder = function (node) {
	var pad = dojo.html.getPadding(node);
	var border = dojo.html.getBorder(node);
	return {width:pad.width + border.width, height:pad.height + border.height};
};
dojo.html.getBoxSizing = function (node) {
	var h = dojo.render.html;
	var bs = dojo.html.boxSizing;
	if (((h.ie) || (h.opera)) && node.nodeName.toLowerCase() != "img") {
		var cm = document["compatMode"];
		if ((cm == "BackCompat") || (cm == "QuirksMode")) {
			return bs.BORDER_BOX;
		} else {
			return bs.CONTENT_BOX;
		}
	} else {
		if (arguments.length == 0) {
			node = document.documentElement;
		}
		var sizing;
		if (!h.ie) {
			sizing = dojo.html.getStyle(node, "-moz-box-sizing");
			if (!sizing) {
				sizing = dojo.html.getStyle(node, "box-sizing");
			}
		}
		return (sizing ? sizing : bs.CONTENT_BOX);
	}
};
dojo.html.isBorderBox = function (node) {
	return (dojo.html.getBoxSizing(node) == dojo.html.boxSizing.BORDER_BOX);
};
dojo.html.getBorderBox = function (node) {
	node = dojo.byId(node);
	return {width:node.offsetWidth, height:node.offsetHeight};
};
dojo.html.getPaddingBox = function (node) {
	var box = dojo.html.getBorderBox(node);
	var border = dojo.html.getBorder(node);
	return {width:box.width - border.width, height:box.height - border.height};
};
dojo.html.getContentBox = function (node) {
	node = dojo.byId(node);
	var padborder = dojo.html.getPadBorder(node);
	return {width:node.offsetWidth - padborder.width, height:node.offsetHeight - padborder.height};
};
dojo.html.setContentBox = function (node, args) {
	node = dojo.byId(node);
	var width = 0;
	var height = 0;
	var isbb = dojo.html.isBorderBox(node);
	var padborder = (isbb ? dojo.html.getPadBorder(node) : {width:0, height:0});
	var ret = {};
	if (typeof args.width != "undefined") {
		width = args.width + padborder.width;
		ret.width = dojo.html.setPositivePixelValue(node, "width", width);
	}
	if (typeof args.height != "undefined") {
		height = args.height + padborder.height;
		ret.height = dojo.html.setPositivePixelValue(node, "height", height);
	}
	return ret;
};
dojo.html.getMarginBox = function (node) {
	var borderbox = dojo.html.getBorderBox(node);
	var margin = dojo.html.getMargin(node);
	return {width:borderbox.width + margin.width, height:borderbox.height + margin.height};
};
dojo.html.setMarginBox = function (node, args) {
	node = dojo.byId(node);
	var width = 0;
	var height = 0;
	var isbb = dojo.html.isBorderBox(node);
	var padborder = (!isbb ? dojo.html.getPadBorder(node) : {width:0, height:0});
	var margin = dojo.html.getMargin(node);
	var ret = {};
	if (typeof args.width != "undefined") {
		width = args.width - padborder.width;
		width -= margin.width;
		ret.width = dojo.html.setPositivePixelValue(node, "width", width);
	}
	if (typeof args.height != "undefined") {
		height = args.height - padborder.height;
		height -= margin.height;
		ret.height = dojo.html.setPositivePixelValue(node, "height", height);
	}
	return ret;
};
dojo.html.getElementBox = function (node, type) {
	var bs = dojo.html.boxSizing;
	switch (type) {
	  case bs.MARGIN_BOX:
		return dojo.html.getMarginBox(node);
	  case bs.BORDER_BOX:
		return dojo.html.getBorderBox(node);
	  case bs.PADDING_BOX:
		return dojo.html.getPaddingBox(node);
	  case bs.CONTENT_BOX:
	  default:
		return dojo.html.getContentBox(node);
	}
};
dojo.html.toCoordinateObject = dojo.html.toCoordinateArray = function (coords, includeScroll, boxtype) {
	if (coords instanceof Array || typeof coords == "array") {
		dojo.deprecated("dojo.html.toCoordinateArray", "use dojo.html.toCoordinateObject({left: , top: , width: , height: }) instead", "0.5");
		while (coords.length < 4) {
			coords.push(0);
		}
		while (coords.length > 4) {
			coords.pop();
		}
		var ret = {left:coords[0], top:coords[1], width:coords[2], height:coords[3]};
	} else {
		if (!coords.nodeType && !(coords instanceof String || typeof coords == "string") && ("width" in coords || "height" in coords || "left" in coords || "x" in coords || "top" in coords || "y" in coords)) {
			var ret = {left:coords.left || coords.x || 0, top:coords.top || coords.y || 0, width:coords.width || 0, height:coords.height || 0};
		} else {
			var node = dojo.byId(coords);
			var pos = dojo.html.abs(node, includeScroll, boxtype);
			var marginbox = dojo.html.getMarginBox(node);
			var ret = {left:pos.left, top:pos.top, width:marginbox.width, height:marginbox.height};
		}
	}
	ret.x = ret.left;
	ret.y = ret.top;
	return ret;
};
dojo.html.setMarginBoxWidth = dojo.html.setOuterWidth = function (node, width) {
	return dojo.html._callDeprecated("setMarginBoxWidth", "setMarginBox", arguments, "width");
};
dojo.html.setMarginBoxHeight = dojo.html.setOuterHeight = function () {
	return dojo.html._callDeprecated("setMarginBoxHeight", "setMarginBox", arguments, "height");
};
dojo.html.getMarginBoxWidth = dojo.html.getOuterWidth = function () {
	return dojo.html._callDeprecated("getMarginBoxWidth", "getMarginBox", arguments, null, "width");
};
dojo.html.getMarginBoxHeight = dojo.html.getOuterHeight = function () {
	return dojo.html._callDeprecated("getMarginBoxHeight", "getMarginBox", arguments, null, "height");
};
dojo.html.getTotalOffset = function (node, type, includeScroll) {
	return dojo.html._callDeprecated("getTotalOffset", "getAbsolutePosition", arguments, null, type);
};
dojo.html.getAbsoluteX = function (node, includeScroll) {
	return dojo.html._callDeprecated("getAbsoluteX", "getAbsolutePosition", arguments, null, "x");
};
dojo.html.getAbsoluteY = function (node, includeScroll) {
	return dojo.html._callDeprecated("getAbsoluteY", "getAbsolutePosition", arguments, null, "y");
};
dojo.html.totalOffsetLeft = function (node, includeScroll) {
	return dojo.html._callDeprecated("totalOffsetLeft", "getAbsolutePosition", arguments, null, "left");
};
dojo.html.totalOffsetTop = function (node, includeScroll) {
	return dojo.html._callDeprecated("totalOffsetTop", "getAbsolutePosition", arguments, null, "top");
};
dojo.html.getMarginWidth = function (node) {
	return dojo.html._callDeprecated("getMarginWidth", "getMargin", arguments, null, "width");
};
dojo.html.getMarginHeight = function (node) {
	return dojo.html._callDeprecated("getMarginHeight", "getMargin", arguments, null, "height");
};
dojo.html.getBorderWidth = function (node) {
	return dojo.html._callDeprecated("getBorderWidth", "getBorder", arguments, null, "width");
};
dojo.html.getBorderHeight = function (node) {
	return dojo.html._callDeprecated("getBorderHeight", "getBorder", arguments, null, "height");
};
dojo.html.getPaddingWidth = function (node) {
	return dojo.html._callDeprecated("getPaddingWidth", "getPadding", arguments, null, "width");
};
dojo.html.getPaddingHeight = function (node) {
	return dojo.html._callDeprecated("getPaddingHeight", "getPadding", arguments, null, "height");
};
dojo.html.getPadBorderWidth = function (node) {
	return dojo.html._callDeprecated("getPadBorderWidth", "getPadBorder", arguments, null, "width");
};
dojo.html.getPadBorderHeight = function (node) {
	return dojo.html._callDeprecated("getPadBorderHeight", "getPadBorder", arguments, null, "height");
};
dojo.html.getBorderBoxWidth = dojo.html.getInnerWidth = function () {
	return dojo.html._callDeprecated("getBorderBoxWidth", "getBorderBox", arguments, null, "width");
};
dojo.html.getBorderBoxHeight = dojo.html.getInnerHeight = function () {
	return dojo.html._callDeprecated("getBorderBoxHeight", "getBorderBox", arguments, null, "height");
};
dojo.html.getContentBoxWidth = dojo.html.getContentWidth = function () {
	return dojo.html._callDeprecated("getContentBoxWidth", "getContentBox", arguments, null, "width");
};
dojo.html.getContentBoxHeight = dojo.html.getContentHeight = function () {
	return dojo.html._callDeprecated("getContentBoxHeight", "getContentBox", arguments, null, "height");
};
dojo.html.setContentBoxWidth = dojo.html.setContentWidth = function (node, width) {
	return dojo.html._callDeprecated("setContentBoxWidth", "setContentBox", arguments, "width");
};
dojo.html.setContentBoxHeight = dojo.html.setContentHeight = function (node, height) {
	return dojo.html._callDeprecated("setContentBoxHeight", "setContentBox", arguments, "height");
};

