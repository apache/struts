/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.lfx.rounded");
dojo.require("dojo.lang.common");
dojo.require("dojo.html.common");
dojo.require("dojo.html.style");
dojo.require("dojo.html.display");
dojo.require("dojo.html.layout");
dojo.lfx.rounded = function (settings) {
	var options = {validTags:settings.validTags || ["div"], autoPad:settings.autoPad != null ? settings.autoPad : true, antiAlias:settings.antiAlias != null ? settings.antiAlias : true, radii:{tl:(settings.tl && settings.tl.radius != null) ? settings.tl.radius : 5, tr:(settings.tr && settings.tr.radius != null) ? settings.tr.radius : 5, bl:(settings.bl && settings.bl.radius != null) ? settings.bl.radius : 5, br:(settings.br && settings.br.radius != null) ? settings.br.radius : 5}};
	var nodes;
	if (typeof (arguments[1]) == "string") {
		nodes = dojo.html.getElementsByClass(arguments[1]);
	} else {
		if (dojo.lang.isArrayLike(arguments[1])) {
			nodes = arguments[1];
			for (var i = 0; i < nodes.length; i++) {
				nodes[i] = dojo.byId(nodes[i]);
			}
		}
	}
	if (nodes.length == 0) {
		return;
	}
	for (var i = 0; i < nodes.length; i++) {
		dojo.lfx.rounded.applyCorners(options, nodes[i]);
	}
};
dojo.lfx.rounded.applyCorners = function (options, node) {
	var top = null;
	var bottom = null;
	var contentNode = null;
	var fns = dojo.lfx.rounded._fns;
	var width = node.offsetWidth;
	var height = node.offsetHeight;
	var borderWidth = parseInt(dojo.html.getComputedStyle(node, "border-top-width"));
	var borderColor = dojo.html.getComputedStyle(node, "border-top-color");
	var color = dojo.html.getComputedStyle(node, "background-color");
	var bgImage = dojo.html.getComputedStyle(node, "background-image");
	var position = dojo.html.getComputedStyle(node, "position");
	var padding = parseInt(dojo.html.getComputedStyle(node, "padding-top"));
	var format = {height:height, width:width, borderWidth:borderWidth, color:fns.getRGB(color), padding:padding, borderColor:fns.getRGB(borderColor), borderString:borderWidth + "px" + " solid " + fns.getRGB(borderColor), bgImage:((bgImage != "none") ? bgImage : ""), content:node.innerHTML};
	if (!dojo.html.isPositionAbsolute(node)) {
		node.style.position = "relative";
	}
	node.style.padding = "0px";
	if (dojo.render.html.ie && width == "auto" && height == "auto") {
		node.style.width = "100%";
	}
	if (options.autoPad && format.padding > 0) {
		node.innerHTML = "";
	}
	var topHeight = Math.max(options.radii.tl, options.radii.tr);
	var bottomHeight = Math.max(options.radii.bl, options.radii.br);
	if (options.radii.tl || options.radii.tr) {
		top = document.createElement("div");
		top.style.width = "100%";
		top.style.fontSize = "1px";
		top.style.overflow = "hidden";
		top.style.position = "absolute";
		top.style.paddingLeft = format.borderWidth + "px";
		top.style.paddingRight = format.borderWidth + "px";
		top.style.height = topHeight + "px";
		top.style.top = (0 - topHeight) + "px";
		top.style.left = (0 - format.borderWidth) + "px";
		node.appendChild(top);
	}
	if (options.radii.bl || options.radii.br) {
		bottom = document.createElement("div");
		bottom.style.width = "100%";
		bottom.style.fontSize = "1px";
		bottom.style.overflow = "hidden";
		bottom.style.position = "absolute";
		bottom.style.paddingLeft = format.borderWidth + "px";
		bottom.style.paddingRight = format.borderWidth + "px";
		bottom.style.height = bottomHeight + "px";
		bottom.style.bottom = (0 - bottomHeight) + "px";
		bottom.style.left = (0 - format.borderWidth) + "px";
		node.appendChild(bottom);
	}
	if (top) {
		node.style.borderTopWidth = "0px";
	}
	if (bottom) {
		node.style.borderBottomWidth = "0px";
	}
	var corners = ["tr", "tl", "br", "bl"];
	for (var i = 0; i < corners.length; i++) {
		var cc = corners[i];
		if (options.radii[cc] == 0) {
			if ((cc.charAt(0) == "t" && top) || (cc.charAt(0) == "b" && bottom)) {
				var corner = document.createElement("div");
				corner.style.position = "relative";
				corner.style.fontSize = "1px;";
				corner.style.overflow = "hidden";
				if (format.bgImage == "") {
					corner.style.backgroundColor = format.color;
				} else {
					corner.style.backgroundImage = format.bgImage;
				}
				switch (cc) {
				  case "tl":
					corner.style.height = topHeight - format.borderWidth + "px";
					corner.style.marginRight = options.radii[cc] - (format.borderWidth * 2) + "px";
					corner.style.borderLeft = format.borderString;
					corner.style.borderTop = format.borderString;
					corner.style.left = -format.borderWidth + "px";
					break;
				  case "tr":
					corner.style.height = topHeight - format.borderWidth + "px";
					corner.style.marginLeft = options.radii[cc] - (format.borderWidth * 2) + "px";
					corner.style.borderRight = format.borderString;
					corner.style.borderTop = format.borderString;
					corner.style.backgroundPosition = "-" + (topHeight - format.borderWidth) + "px 0px";
					corner.style.left = format.borderWidth + "px";
					break;
				  case "bl":
					corner.style.height = bottomHeight - format.borderWidth + "px";
					corner.style.marginRight = options.radii[cc] - (format.borderWidth * 2) + "px";
					corner.style.borderLeft = format.borderString;
					corner.style.borderBottom = format.borderString;
					corner.style.left = format.borderWidth + "px";
					corner.style.backgroundPosition = "-" + format.borderWidth + "px -" + (format.height + (bottomHeight + format.borderWidth)) + "px";
					break;
				  case "br":
					corner.style.height = bottomHeight - format.borderWidth + "px";
					corner.style.marginLeft = options.radii[cc] - (format.borderWidth * 2) + "px";
					corner.style.borderRight = format.borderString;
					corner.style.borderBottom = format.borderString;
					corner.style.left = format.borderWidth + "px";
					corner.style.backgroundPosition = "-" + (bottomHeight + format.borderWidth) + "px -" + (format.height + (bottomHeight + format.borderWidth)) + "px";
					break;
				}
			}
		} else {
			var corner = document.createElement("div");
			corner.style.height = options.radii[cc] + "px";
			corner.style.width = options.radii[cc] + "px";
			corner.style.position = "absolute";
			corner.style.fontSize = "1px";
			corner.style.overflow = "hidden";
			var borderRadius = Math.floor(options.radii[cc] - format.borderWidth);
			for (var x = 0, j = options.radii[cc]; x < j; x++) {
				var y1 = Math.floor(Math.sqrt(Math.pow(borderRadius, 2) - Math.pow((x + 1), 2))) - 1;
				if ((x + 1) >= borderRadius) {
					var y1 = -1;
				}
				var y2 = Math.ceil(Math.sqrt(Math.pow(borderRadius, 2) - Math.pow(x, 2)));
				if (x >= borderRadius) {
					y2 = -1;
				}
				var y3 = Math.floor(Math.sqrt(Math.pow(j, 2) - Math.pow((x + 1), 2))) - 1;
				if ((x + 1) >= j) {
					y3 = -1;
				}
				var y4 = Math.ceil(Math.sqrt(Math.pow(j, 2) - Math.pow(x, 2)));
				if (x >= j) {
					y4 = -1;
				}
				if (y1 > -1) {
					fns.draw(x, 0, format.color, 100, (y1 + 1), corner, -1, j, topHeight, format);
				}
				for (var y = (y1 + 1); y < y2; y++) {
					if (options.antiAlias) {
						if (format.bgImage != "") {
							var fract = fns.fraction(x, y, borderRadius) * 100;
							if (fract < 30) {
								fns.draw(x, y, format.borderColor, 100, 1, corner, 0, options.radii[cc], topHeight, format);
							} else {
								fns.draw(x, y, format.borderColor, 100, 1, corner, -1, options.radii[cc], topHeight, format);
							}
						} else {
							var clr = fns.blend(format.color, format.borderColor, fns.fraction(x, y, borderRadius));
							fns.draw(x, y, clr, 100, 1, corner, 0, options.radii[cc], topHeight, format);
						}
					}
				}
				if (options.antiAlias) {
					if (y3 >= y2) {
						if (y2 == -1) {
							y2 = 0;
						}
						fns.draw(x, y2, format.borderColor, 100, (y3 - y2 + 1), corner, 0, 0, topHeight, format);
					} else {
						if (y3 >= y1) {
							fns.draw(x, (y1 + 1), format.borderColor, 100, (y3 - y1), corner, 0, 0, topHeight, format);
						}
					}
					for (var y = (y3 + 1); y < y4; y++) {
						fns.draw(x, y, format.borderColor, (fns.fraction(x, y, j) * 100), 1, corner, (format.borderWidth > 0 ? 0 : -1), options.radii[cc], topHeight, format);
					}
				} else {
					y3 = y1;
				}
			}
			if (cc != "br") {
				for (var t = 0, k = corner.childNodes.length; t < k; t++) {
					var bar = corner.childNodes[t];
					var barTop = parseInt(dojo.html.getComputedStyle(bar, "top"));
					var barLeft = parseInt(dojo.html.getComputedStyle(bar, "left"));
					var barHeight = parseInt(dojo.html.getComputedStyle(bar, "height"));
					if (cc.charAt(1) == "l") {
						bar.style.left = (options.radii[cc] - barLeft - 1) + "px";
					}
					if (cc == "tr") {
						bar.style.top = (options.radii[cc] - barHeight - barTop) + "px";
						bar.style.backgroundPosition = "-" + Math.abs((format.width - options.radii[cc] + format.borderWidth) + barLeft) + "px -" + Math.abs(options.radii[cc] - barHeight - barTop - format.borderWidth) + "px";
					} else {
						if (cc == "tl") {
							bar.style.top = (options.radii[cc] - barHeight - barTop) + "px";
							bar.style.backgroundPosition = "-" + Math.abs((options.radii[cc] - barLeft - 1) - format.borderWidth) + "px -" + Math.abs(options.radii[cc] - barHeight - barTop - format.borderWidth) + "px";
						} else {
							bar.style.backgroundPosition = "-" + Math.abs((options.radii[cc] + barLeft) + format.borderWidth) + "px -" + Math.abs((format.height + options.radii[cc] + barTop) - format.borderWidth) + "px";
						}
					}
				}
			}
		}
		if (corner) {
			var psn = [];
			if (cc.charAt(0) == "t") {
				psn.push("top");
			} else {
				psn.push("bottom");
			}
			if (cc.charAt(1) == "l") {
				psn.push("left");
			} else {
				psn.push("right");
			}
			if (corner.style.position == "absolute") {
				for (var z = 0; z < psn.length; z++) {
					corner.style[psn[z]] = "0px";
				}
			}
			if (psn[0] == "top") {
				if (top) {
					top.appendChild(corner);
				}
			} else {
				if (bottom) {
					bottom.appendChild(corner);
				}
			}
		}
	}
	var diff = {t:Math.abs(options.radii.tl - options.radii.tr), b:Math.abs(options.radii.bl - options.radii.br)};
	for (var z in diff) {
		var smaller = (options.radii[z + "l"] < options.radii[z + "r"] ? z + "l" : z + "r");
		var filler = document.createElement("div");
		filler.style.height = diff[z] + "px";
		filler.style.width = options.radii[smaller] + "px";
		filler.style.position = "absolute";
		filler.style.fontSize = "1px";
		filler.style.overflow = "hidden";
		filler.style.backgroundColor = format.color;
		switch (smaller) {
		  case "tl":
			filler.style.bottom = "0px";
			filler.style.left = "0px";
			filler.style.borderLeft = format.borderString;
			top.appendChild(filler);
			break;
		  case "tr":
			filler.style.bottom = "0px";
			filler.style.right = "0px";
			filler.style.borderRight = format.borderString;
			top.appendChild(filler);
			break;
		  case "bl":
			filler.style.top = "0px";
			filler.style.left = "0px";
			filler.style.borderLeft = format.borderString;
			bottom.appendChild(filler);
			break;
		  case "br":
			filler.style.top = "0px";
			filler.style.right = "0px";
			filler.style.borderRight = format.borderString;
			bottom.appendChild(filler);
			break;
		}
		var fillBar = document.createElement("div");
		fillBar.style.position = "relative";
		fillBar.style.fontSize = "1px";
		fillBar.style.overflow = "hidden";
		fillBar.style.backgroundColor = format.color;
		fillBar.style.backgroundImage = format.bgImage;
		if (z == "t") {
			if (top) {
				if (options.radii.tl && options.radii.tr) {
					fillBar.style.height = (topHeight - format.borderWidth) + "px";
					fillBar.style.marginLeft = (options.radii.tl - format.borderWidth) + "px";
					fillBar.style.marginRight = (options.radii.tr - format.borderWidth) + "px";
					fillBar.style.borderTop = format.borderString;
					if (format.bgImage != "") {
						fillBar.style.backgroundPosition = "-" + (topHeight + format.borderWidth) + "px 0px";
					}
				}
				top.appendChild(fillBar);
			}
		} else {
			if (bottom) {
				if (options.radii.bl && options.radii.br) {
					fillBar.style.height = (bottomHeight - format.borderWidth) + "px";
					fillBar.style.marginLeft = (options.radii.bl - format.borderWidth) + "px";
					fillBar.style.marginRight = (options.radii.br - format.borderWidth) + "px";
					fillBar.style.borderBottom = format.borderString;
					if (format.bgImage != "") {
						fillBar.style.backgroundPosition = "-" + (bottomHeight + format.borderWidth) + "px -" + (format.height + (topHeight + format.borderWidth)) + "px";
					}
				}
				bottom.appendChild(fillBar);
			}
		}
	}
	if (options.autoPad && format.padding > 0) {
		var content = document.createElement("div");
		content.style.position = "relative";
		content.innerHTML = format.content;
		content.className = "autoPadDiv";
		if (topHeight < format.padding) {
			content.style.paddingTop = Math.abs(topHeight - format.padding) + "px";
		}
		if (bottomHeight < format.padding) {
			content.style.paddingBottom = Math.abs(bottomHeight - format.padding) + "px";
		}
		content.style.paddingLeft = format.padding + "px";
		content.style.paddingRight = format.padding + "px";
		node.appendChild(content);
	}
};
var count = 0;
dojo.lfx.rounded._fns = {blend:function (clr1, clr2, frac) {
	var c1 = {r:parseInt(clr1.substr(1, 2), 16), g:parseInt(clr1.substr(3, 2), 16), b:parseInt(clr1.substr(5, 2), 16)};
	var c2 = {r:parseInt(clr2.substr(1, 2), 16), g:parseInt(clr2.substr(3, 2), 16), b:parseInt(clr2.substr(5, 2), 16)};
	if (frac > 1 || frac < 0) {
		frac = 1;
	}
	var ret = [Math.min(Math.max(Math.round((c1.r * frac) + (c2.r * (1 - frac))), 0), 255), Math.min(Math.max(Math.round((c1.g * frac) + (c2.g * (1 - frac))), 0), 255), Math.min(Math.max(Math.round((c1.b * frac) + (c2.b * (1 - frac))), 0), 255)];
	for (var i = 0; i < ret.length; i++) {
		var n = ret[i].toString(16);
		if (n.length < 2) {
			n = "0" + n;
		}
		ret[i] = n;
	}
	return "#" + ret.join("");
}, fraction:function (x, y, r) {
	var frac = 0;
	var xval = [];
	var yval = [];
	var point = 0;
	var whatsides = "";
	var intersect = Math.sqrt((Math.pow(r, 2) - Math.pow(x, 2)));
	if (intersect >= y && intersect < (y + 1)) {
		whatsides = "Left";
		xval[point] = 0;
		yval[point++] = intersect - y;
	}
	intersect = Math.sqrt((Math.pow(r, 2) - Math.pow(y + 1, 2)));
	if (intersect >= x && intersect < (x + 1)) {
		whatsides += "Top";
		xval[point] = intersect - x;
		yval[point++] = 1;
	}
	intersect = Math.sqrt((Math.pow(r, 2) - Math.pow(x + 1, 2)));
	if (intersect >= y && intersect < (y + 1)) {
		whatsides += "Right";
		xval[point] = 1;
		yval[point++] = intersect - y;
	}
	intersect = Math.sqrt((Math.pow(r, 2) - Math.pow(y, 2)));
	if (intersect >= x && intersect < (x + 1)) {
		whatsides += "Bottom";
		xval[point] = intersect - x;
		yval[point] = 1;
	}
	switch (whatsides) {
	  case "LeftRight":
		return Math.min(yval[0], yval[1]) + ((Math.max(yval[0], yval[1]) - Math.min(yval[0], yval[1])) / 2);
	  case "TopRight":
		return 1 - (((1 - xval[0]) * (1 - yval[1])) / 2);
	  case "TopBottom":
		return Math.min(xval[0], xval[1]) + ((Math.max(xval[0], xval[1]) - Math.min(xval[0], xval[1])) / 2);
	  case "LeftBottom":
		return (yval[0] * xval[1]) / 2;
	  default:
		return 1;
	}
}, draw:function (x, y, color, opac, height, corner, image, radius, top, format) {
	var px = document.createElement("div");
	px.style.height = height + "px";
	px.style.width = "1px";
	px.style.position = "absolute";
	px.style.fontSize = "1px";
	px.style.overflow = "hidden";
	if (image == -1 && format.bgImage != "") {
		px.style.backgroundImage = format.bgImage;
		px.style.backgroundPosition = "-" + (format.width - (radius - x) + format.borderWidth) + "px -" + ((format.height + top + y) - format.borderWidth) + "px";
	} else {
		px.style.backgroundColor = color;
	}
	if (opac != 100) {
		dojo.html.setOpacity(px, (opac / 100));
	}
	px.style.top = y + "px";
	px.style.left = x + "px";
	corner.appendChild(px);
}, getRGB:function (clr) {
	var ret = "#ffffff";
	if (clr != "" && clr != "transparent") {
		if (clr.substr(0, 3) == "rgb") {
			var t = clr.substring(4, clr.indexOf(")"));
			t = t.split(",");
			for (var i = 0; i < t.length; i++) {
				var n = parseInt(t[i]).toString(16);
				if (n.length < 2) {
					n = "0" + n;
				}
				t[i] = n;
			}
			ret = "#" + t.join("");
		} else {
			if (clr.length == 4) {
				ret = "#" + clr.substring(1, 2) + clr.substring(1, 2) + clr.substring(2, 3) + clr.substring(2, 3) + clr.substring(3, 4) + clr.substring(3, 4);
			} else {
				ret = clr;
			}
		}
	}
	return ret;
}};

