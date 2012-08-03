/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.html.layout");
dojo.require("dojo.lang.common");
dojo.require("dojo.string.extras");
dojo.require("dojo.html.style");
dojo.require("dojo.html.layout");
dojo.widget.html.layout = function (container, children, layoutPriority) {
	dojo.html.addClass(container, "dojoLayoutContainer");
	children = dojo.lang.filter(children, function (child, idx) {
		child.idx = idx;
		return dojo.lang.inArray(["top", "bottom", "left", "right", "client", "flood"], child.layoutAlign);
	});
	if (layoutPriority && layoutPriority != "none") {
		var rank = function (child) {
			switch (child.layoutAlign) {
			  case "flood":
				return 1;
			  case "left":
			  case "right":
				return (layoutPriority == "left-right") ? 2 : 3;
			  case "top":
			  case "bottom":
				return (layoutPriority == "left-right") ? 3 : 2;
			  default:
				return 4;
			}
		};
		children.sort(function (a, b) {
			return (rank(a) - rank(b)) || (a.idx - b.idx);
		});
	}
	var f = {top:dojo.html.getPixelValue(container, "padding-top", true), left:dojo.html.getPixelValue(container, "padding-left", true)};
	dojo.lang.mixin(f, dojo.html.getContentBox(container));
	dojo.lang.forEach(children, function (child) {
		var elm = child.domNode;
		var pos = child.layoutAlign;
		with (elm.style) {
			left = f.left + "px";
			top = f.top + "px";
			bottom = "auto";
			right = "auto";
		}
		dojo.html.addClass(elm, "dojoAlign" + dojo.string.capitalize(pos));
		if ((pos == "top") || (pos == "bottom")) {
			dojo.html.setMarginBox(elm, {width:f.width});
			var h = dojo.html.getMarginBox(elm).height;
			f.height -= h;
			if (pos == "top") {
				f.top += h;
			} else {
				elm.style.top = f.top + f.height + "px";
			}
			if (child.onResized) {
				child.onResized();
			}
		} else {
			if (pos == "left" || pos == "right") {
				var w = dojo.html.getMarginBox(elm).width;
				if (child.resizeTo) {
					child.resizeTo(w, f.height);
				} else {
					dojo.html.setMarginBox(elm, {width:w, height:f.height});
				}
				f.width -= w;
				if (pos == "left") {
					f.left += w;
				} else {
					elm.style.left = f.left + f.width + "px";
				}
			} else {
				if (pos == "flood" || pos == "client") {
					if (child.resizeTo) {
						child.resizeTo(f.width, f.height);
					} else {
						dojo.html.setMarginBox(elm, {width:f.width, height:f.height});
					}
				}
			}
		}
	});
};
dojo.html.insertCssText(".dojoLayoutContainer{ position: relative; display: block; overflow: hidden; }\n" + "body .dojoAlignTop, body .dojoAlignBottom, body .dojoAlignLeft, body .dojoAlignRight { position: absolute; overflow: hidden; }\n" + "body .dojoAlignClient { position: absolute }\n" + ".dojoAlignClient { overflow: auto; }\n");

