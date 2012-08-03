/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.html.display");
dojo.require("dojo.html.style");
dojo.html._toggle = function (node, tester, setter) {
	node = dojo.byId(node);
	setter(node, !tester(node));
	return tester(node);
};
dojo.html.show = function (node) {
	node = dojo.byId(node);
	if (dojo.html.getStyleProperty(node, "display") == "none") {
		dojo.html.setStyle(node, "display", (node.dojoDisplayCache || ""));
		node.dojoDisplayCache = undefined;
	}
};
dojo.html.hide = function (node) {
	node = dojo.byId(node);
	if (typeof node["dojoDisplayCache"] == "undefined") {
		var d = dojo.html.getStyleProperty(node, "display");
		if (d != "none") {
			node.dojoDisplayCache = d;
		}
	}
	dojo.html.setStyle(node, "display", "none");
};
dojo.html.setShowing = function (node, showing) {
	dojo.html[(showing ? "show" : "hide")](node);
};
dojo.html.isShowing = function (node) {
	return (dojo.html.getStyleProperty(node, "display") != "none");
};
dojo.html.toggleShowing = function (node) {
	return dojo.html._toggle(node, dojo.html.isShowing, dojo.html.setShowing);
};
dojo.html.displayMap = {tr:"", td:"", th:"", img:"inline", span:"inline", input:"inline", button:"inline"};
dojo.html.suggestDisplayByTagName = function (node) {
	node = dojo.byId(node);
	if (node && node.tagName) {
		var tag = node.tagName.toLowerCase();
		return (tag in dojo.html.displayMap ? dojo.html.displayMap[tag] : "block");
	}
};
dojo.html.setDisplay = function (node, display) {
	dojo.html.setStyle(node, "display", ((display instanceof String || typeof display == "string") ? display : (display ? dojo.html.suggestDisplayByTagName(node) : "none")));
};
dojo.html.isDisplayed = function (node) {
	return (dojo.html.getComputedStyle(node, "display") != "none");
};
dojo.html.toggleDisplay = function (node) {
	return dojo.html._toggle(node, dojo.html.isDisplayed, dojo.html.setDisplay);
};
dojo.html.setVisibility = function (node, visibility) {
	dojo.html.setStyle(node, "visibility", ((visibility instanceof String || typeof visibility == "string") ? visibility : (visibility ? "visible" : "hidden")));
};
dojo.html.isVisible = function (node) {
	return (dojo.html.getComputedStyle(node, "visibility") != "hidden");
};
dojo.html.toggleVisibility = function (node) {
	return dojo.html._toggle(node, dojo.html.isVisible, dojo.html.setVisibility);
};
dojo.html.setOpacity = function (node, opacity, dontFixOpacity) {
	node = dojo.byId(node);
	var h = dojo.render.html;
	if (!dontFixOpacity) {
		if (opacity >= 1) {
			if (h.ie) {
				dojo.html.clearOpacity(node);
				return;
			} else {
				opacity = 0.999999;
			}
		} else {
			if (opacity < 0) {
				opacity = 0;
			}
		}
	}
	if (h.ie) {
		if (node.nodeName.toLowerCase() == "tr") {
			var tds = node.getElementsByTagName("td");
			for (var x = 0; x < tds.length; x++) {
				tds[x].style.filter = "Alpha(Opacity=" + opacity * 100 + ")";
			}
		}
		node.style.filter = "Alpha(Opacity=" + opacity * 100 + ")";
	} else {
		if (h.moz) {
			node.style.opacity = opacity;
			node.style.MozOpacity = opacity;
		} else {
			if (h.safari) {
				node.style.opacity = opacity;
				node.style.KhtmlOpacity = opacity;
			} else {
				node.style.opacity = opacity;
			}
		}
	}
};
dojo.html.clearOpacity = function (node) {
	node = dojo.byId(node);
	var ns = node.style;
	var h = dojo.render.html;
	if (h.ie) {
		try {
			if (node.filters && node.filters.alpha) {
				ns.filter = "";
			}
		}
		catch (e) {
		}
	} else {
		if (h.moz) {
			ns.opacity = 1;
			ns.MozOpacity = 1;
		} else {
			if (h.safari) {
				ns.opacity = 1;
				ns.KhtmlOpacity = 1;
			} else {
				ns.opacity = 1;
			}
		}
	}
};
dojo.html.getOpacity = function (node) {
	node = dojo.byId(node);
	var h = dojo.render.html;
	if (h.ie) {
		var opac = (node.filters && node.filters.alpha && typeof node.filters.alpha.opacity == "number" ? node.filters.alpha.opacity : 100) / 100;
	} else {
		var opac = node.style.opacity || node.style.MozOpacity || node.style.KhtmlOpacity || 1;
	}
	return opac >= 0.999999 ? 1 : Number(opac);
};

