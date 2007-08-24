/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.lfx.html");
dojo.require("dojo.gfx.color");
dojo.require("dojo.lfx.Animation");
dojo.require("dojo.lang.array");
dojo.require("dojo.html.display");
dojo.require("dojo.html.color");
dojo.require("dojo.html.layout");
dojo.lfx.html._byId = function (nodes) {
	if (!nodes) {
		return [];
	}
	if (dojo.lang.isArrayLike(nodes)) {
		if (!nodes.alreadyChecked) {
			var n = [];
			dojo.lang.forEach(nodes, function (node) {
				n.push(dojo.byId(node));
			});
			n.alreadyChecked = true;
			return n;
		} else {
			return nodes;
		}
	} else {
		var n = [];
		n.push(dojo.byId(nodes));
		n.alreadyChecked = true;
		return n;
	}
};
dojo.lfx.html.propertyAnimation = function (nodes, propertyMap, duration, easing, handlers) {
	nodes = dojo.lfx.html._byId(nodes);
	var targs = {"propertyMap":propertyMap, "nodes":nodes, "duration":duration, "easing":easing || dojo.lfx.easeDefault};
	var setEmUp = function (args) {
		if (args.nodes.length == 1) {
			var pm = args.propertyMap;
			if (!dojo.lang.isArray(args.propertyMap)) {
				var parr = [];
				for (var pname in pm) {
					pm[pname].property = pname;
					parr.push(pm[pname]);
				}
				pm = args.propertyMap = parr;
			}
			dojo.lang.forEach(pm, function (prop) {
				if (dj_undef("start", prop)) {
					if (prop.property != "opacity") {
						prop.start = parseInt(dojo.html.getComputedStyle(args.nodes[0], prop.property));
					} else {
						prop.start = dojo.html.getOpacity(args.nodes[0]);
					}
				}
			});
		}
	};
	var coordsAsInts = function (coords) {
		var cints = [];
		dojo.lang.forEach(coords, function (c) {
			cints.push(Math.round(c));
		});
		return cints;
	};
	var setStyle = function (n, style) {
		n = dojo.byId(n);
		if (!n || !n.style) {
			return;
		}
		for (var s in style) {
			try {
				if (s == "opacity") {
					dojo.html.setOpacity(n, style[s]);
				} else {
					n.style[s] = style[s];
				}
			}
			catch (e) {
				dojo.debug(e);
			}
		}
	};
	var propLine = function (properties) {
		this._properties = properties;
		this.diffs = new Array(properties.length);
		dojo.lang.forEach(properties, function (prop, i) {
			if (dojo.lang.isFunction(prop.start)) {
				prop.start = prop.start(prop, i);
			}
			if (dojo.lang.isFunction(prop.end)) {
				prop.end = prop.end(prop, i);
			}
			if (dojo.lang.isArray(prop.start)) {
				this.diffs[i] = null;
			} else {
				if (prop.start instanceof dojo.gfx.color.Color) {
					prop.startRgb = prop.start.toRgb();
					prop.endRgb = prop.end.toRgb();
				} else {
					this.diffs[i] = prop.end - prop.start;
				}
			}
		}, this);
		this.getValue = function (n) {
			var ret = {};
			dojo.lang.forEach(this._properties, function (prop, i) {
				var value = null;
				if (dojo.lang.isArray(prop.start)) {
				} else {
					if (prop.start instanceof dojo.gfx.color.Color) {
						value = (prop.units || "rgb") + "(";
						for (var j = 0; j < prop.startRgb.length; j++) {
							value += Math.round(((prop.endRgb[j] - prop.startRgb[j]) * n) + prop.startRgb[j]) + (j < prop.startRgb.length - 1 ? "," : "");
						}
						value += ")";
					} else {
						value = ((this.diffs[i]) * n) + prop.start + (prop.property != "opacity" ? prop.units || "px" : "");
					}
				}
				ret[dojo.html.toCamelCase(prop.property)] = value;
			}, this);
			return ret;
		};
	};
	var anim = new dojo.lfx.Animation({beforeBegin:function () {
		setEmUp(targs);
		anim.curve = new propLine(targs.propertyMap);
	}, onAnimate:function (propValues) {
		dojo.lang.forEach(targs.nodes, function (node) {
			setStyle(node, propValues);
		});
	}}, targs.duration, null, targs.easing);
	if (handlers) {
		for (var x in handlers) {
			if (dojo.lang.isFunction(handlers[x])) {
				anim.connect(x, anim, handlers[x]);
			}
		}
	}
	return anim;
};
dojo.lfx.html._makeFadeable = function (nodes) {
	var makeFade = function (node) {
		if (dojo.render.html.ie) {
			if ((node.style.zoom.length == 0) && (dojo.html.getStyle(node, "zoom") == "normal")) {
				node.style.zoom = "1";
			}
			if ((node.style.width.length == 0) && (dojo.html.getStyle(node, "width") == "auto")) {
				node.style.width = "auto";
			}
		}
	};
	if (dojo.lang.isArrayLike(nodes)) {
		dojo.lang.forEach(nodes, makeFade);
	} else {
		makeFade(nodes);
	}
};
dojo.lfx.html.fade = function (nodes, values, duration, easing, callback) {
	nodes = dojo.lfx.html._byId(nodes);
	var props = {property:"opacity"};
	if (!dj_undef("start", values)) {
		props.start = values.start;
	} else {
		props.start = function () {
			return dojo.html.getOpacity(nodes[0]);
		};
	}
	if (!dj_undef("end", values)) {
		props.end = values.end;
	} else {
		dojo.raise("dojo.lfx.html.fade needs an end value");
	}
	var anim = dojo.lfx.propertyAnimation(nodes, [props], duration, easing);
	anim.connect("beforeBegin", function () {
		dojo.lfx.html._makeFadeable(nodes);
	});
	if (callback) {
		anim.connect("onEnd", function () {
			callback(nodes, anim);
		});
	}
	return anim;
};
dojo.lfx.html.fadeIn = function (nodes, duration, easing, callback) {
	return dojo.lfx.html.fade(nodes, {end:1}, duration, easing, callback);
};
dojo.lfx.html.fadeOut = function (nodes, duration, easing, callback) {
	return dojo.lfx.html.fade(nodes, {end:0}, duration, easing, callback);
};
dojo.lfx.html.fadeShow = function (nodes, duration, easing, callback) {
	nodes = dojo.lfx.html._byId(nodes);
	dojo.lang.forEach(nodes, function (node) {
		dojo.html.setOpacity(node, 0);
	});
	var anim = dojo.lfx.html.fadeIn(nodes, duration, easing, callback);
	anim.connect("beforeBegin", function () {
		if (dojo.lang.isArrayLike(nodes)) {
			dojo.lang.forEach(nodes, dojo.html.show);
		} else {
			dojo.html.show(nodes);
		}
	});
	return anim;
};
dojo.lfx.html.fadeHide = function (nodes, duration, easing, callback) {
	var anim = dojo.lfx.html.fadeOut(nodes, duration, easing, function () {
		if (dojo.lang.isArrayLike(nodes)) {
			dojo.lang.forEach(nodes, dojo.html.hide);
		} else {
			dojo.html.hide(nodes);
		}
		if (callback) {
			callback(nodes, anim);
		}
	});
	return anim;
};
dojo.lfx.html.wipeIn = function (nodes, duration, easing, callback) {
	nodes = dojo.lfx.html._byId(nodes);
	var anims = [];
	dojo.lang.forEach(nodes, function (node) {
		var oprop = {};
		var origTop, origLeft, origPosition;
		with (node.style) {
			origTop = top;
			origLeft = left;
			origPosition = position;
			top = "-9999px";
			left = "-9999px";
			position = "absolute";
			display = "";
		}
		var nodeHeight = dojo.html.getBorderBox(node).height;
		with (node.style) {
			top = origTop;
			left = origLeft;
			position = origPosition;
			display = "none";
		}
		var anim = dojo.lfx.propertyAnimation(node, {"height":{start:1, end:function () {
			return nodeHeight;
		}}}, duration, easing);
		anim.connect("beforeBegin", function () {
			oprop.overflow = node.style.overflow;
			oprop.height = node.style.height;
			with (node.style) {
				overflow = "hidden";
				height = "1px";
			}
			dojo.html.show(node);
		});
		anim.connect("onEnd", function () {
			with (node.style) {
				overflow = oprop.overflow;
				height = oprop.height;
			}
			if (callback) {
				callback(node, anim);
			}
		});
		anims.push(anim);
	});
	return dojo.lfx.combine(anims);
};
dojo.lfx.html.wipeOut = function (nodes, duration, easing, callback) {
	nodes = dojo.lfx.html._byId(nodes);
	var anims = [];
	dojo.lang.forEach(nodes, function (node) {
		var oprop = {};
		var anim = dojo.lfx.propertyAnimation(node, {"height":{start:function () {
			return dojo.html.getContentBox(node).height;
		}, end:1}}, duration, easing, {"beforeBegin":function () {
			oprop.overflow = node.style.overflow;
			oprop.height = node.style.height;
			with (node.style) {
				overflow = "hidden";
			}
			dojo.html.show(node);
		}, "onEnd":function () {
			dojo.html.hide(node);
			with (node.style) {
				overflow = oprop.overflow;
				height = oprop.height;
			}
			if (callback) {
				callback(node, anim);
			}
		}});
		anims.push(anim);
	});
	return dojo.lfx.combine(anims);
};
dojo.lfx.html.slideTo = function (nodes, coords, duration, easing, callback) {
	nodes = dojo.lfx.html._byId(nodes);
	var anims = [];
	var compute = dojo.html.getComputedStyle;
	if (dojo.lang.isArray(coords)) {
		dojo.deprecated("dojo.lfx.html.slideTo(node, array)", "use dojo.lfx.html.slideTo(node, {top: value, left: value});", "0.5");
		coords = {top:coords[0], left:coords[1]};
	}
	dojo.lang.forEach(nodes, function (node) {
		var top = null;
		var left = null;
		var init = (function () {
			var innerNode = node;
			return function () {
				var pos = compute(innerNode, "position");
				top = (pos == "absolute" ? node.offsetTop : parseInt(compute(node, "top")) || 0);
				left = (pos == "absolute" ? node.offsetLeft : parseInt(compute(node, "left")) || 0);
				if (!dojo.lang.inArray(["absolute", "relative"], pos)) {
					var ret = dojo.html.abs(innerNode, true);
					dojo.html.setStyleAttributes(innerNode, "position:absolute;top:" + ret.y + "px;left:" + ret.x + "px;");
					top = ret.y;
					left = ret.x;
				}
			};
		})();
		init();
		var anim = dojo.lfx.propertyAnimation(node, {"top":{start:top, end:(coords.top || 0)}, "left":{start:left, end:(coords.left || 0)}}, duration, easing, {"beforeBegin":init});
		if (callback) {
			anim.connect("onEnd", function () {
				callback(nodes, anim);
			});
		}
		anims.push(anim);
	});
	return dojo.lfx.combine(anims);
};
dojo.lfx.html.slideBy = function (nodes, coords, duration, easing, callback) {
	nodes = dojo.lfx.html._byId(nodes);
	var anims = [];
	var compute = dojo.html.getComputedStyle;
	if (dojo.lang.isArray(coords)) {
		dojo.deprecated("dojo.lfx.html.slideBy(node, array)", "use dojo.lfx.html.slideBy(node, {top: value, left: value});", "0.5");
		coords = {top:coords[0], left:coords[1]};
	}
	dojo.lang.forEach(nodes, function (node) {
		var top = null;
		var left = null;
		var init = (function () {
			var innerNode = node;
			return function () {
				var pos = compute(innerNode, "position");
				top = (pos == "absolute" ? node.offsetTop : parseInt(compute(node, "top")) || 0);
				left = (pos == "absolute" ? node.offsetLeft : parseInt(compute(node, "left")) || 0);
				if (!dojo.lang.inArray(["absolute", "relative"], pos)) {
					var ret = dojo.html.abs(innerNode, true);
					dojo.html.setStyleAttributes(innerNode, "position:absolute;top:" + ret.y + "px;left:" + ret.x + "px;");
					top = ret.y;
					left = ret.x;
				}
			};
		})();
		init();
		var anim = dojo.lfx.propertyAnimation(node, {"top":{start:top, end:top + (coords.top || 0)}, "left":{start:left, end:left + (coords.left || 0)}}, duration, easing).connect("beforeBegin", init);
		if (callback) {
			anim.connect("onEnd", function () {
				callback(nodes, anim);
			});
		}
		anims.push(anim);
	});
	return dojo.lfx.combine(anims);
};
dojo.lfx.html.explode = function (start, endNode, duration, easing, callback) {
	var h = dojo.html;
	start = dojo.byId(start);
	endNode = dojo.byId(endNode);
	var startCoords = h.toCoordinateObject(start, true);
	var outline = document.createElement("div");
	h.copyStyle(outline, endNode);
	if (endNode.explodeClassName) {
		outline.className = endNode.explodeClassName;
	}
	with (outline.style) {
		position = "absolute";
		display = "none";
		var backgroundStyle = h.getStyle(start, "background-color");
		backgroundColor = backgroundStyle ? backgroundStyle.toLowerCase() : "transparent";
		backgroundColor = (backgroundColor == "transparent") ? "rgb(221, 221, 221)" : backgroundColor;
	}
	dojo.body().appendChild(outline);
	with (endNode.style) {
		visibility = "hidden";
		display = "block";
	}
	var endCoords = h.toCoordinateObject(endNode, true);
	with (endNode.style) {
		display = "none";
		visibility = "visible";
	}
	var props = {opacity:{start:0.5, end:1}};
	dojo.lang.forEach(["height", "width", "top", "left"], function (type) {
		props[type] = {start:startCoords[type], end:endCoords[type]};
	});
	var anim = new dojo.lfx.propertyAnimation(outline, props, duration, easing, {"beforeBegin":function () {
		h.setDisplay(outline, "block");
	}, "onEnd":function () {
		h.setDisplay(endNode, "block");
		outline.parentNode.removeChild(outline);
	}});
	if (callback) {
		anim.connect("onEnd", function () {
			callback(endNode, anim);
		});
	}
	return anim;
};
dojo.lfx.html.implode = function (startNode, end, duration, easing, callback) {
	var h = dojo.html;
	startNode = dojo.byId(startNode);
	end = dojo.byId(end);
	var startCoords = dojo.html.toCoordinateObject(startNode, true);
	var endCoords = dojo.html.toCoordinateObject(end, true);
	var outline = document.createElement("div");
	dojo.html.copyStyle(outline, startNode);
	if (startNode.explodeClassName) {
		outline.className = startNode.explodeClassName;
	}
	dojo.html.setOpacity(outline, 0.3);
	with (outline.style) {
		position = "absolute";
		display = "none";
		backgroundColor = h.getStyle(startNode, "background-color").toLowerCase();
	}
	dojo.body().appendChild(outline);
	var props = {opacity:{start:1, end:0.5}};
	dojo.lang.forEach(["height", "width", "top", "left"], function (type) {
		props[type] = {start:startCoords[type], end:endCoords[type]};
	});
	var anim = new dojo.lfx.propertyAnimation(outline, props, duration, easing, {"beforeBegin":function () {
		dojo.html.hide(startNode);
		dojo.html.show(outline);
	}, "onEnd":function () {
		outline.parentNode.removeChild(outline);
	}});
	if (callback) {
		anim.connect("onEnd", function () {
			callback(startNode, anim);
		});
	}
	return anim;
};
dojo.lfx.html.highlight = function (nodes, startColor, duration, easing, callback) {
	nodes = dojo.lfx.html._byId(nodes);
	var anims = [];
	dojo.lang.forEach(nodes, function (node) {
		var color = dojo.html.getBackgroundColor(node);
		var bg = dojo.html.getStyle(node, "background-color").toLowerCase();
		var bgImage = dojo.html.getStyle(node, "background-image");
		var wasTransparent = (bg == "transparent" || bg == "rgba(0, 0, 0, 0)");
		while (color.length > 3) {
			color.pop();
		}
		var rgb = new dojo.gfx.color.Color(startColor);
		var endRgb = new dojo.gfx.color.Color(color);
		var anim = dojo.lfx.propertyAnimation(node, {"background-color":{start:rgb, end:endRgb}}, duration, easing, {"beforeBegin":function () {
			if (bgImage) {
				node.style.backgroundImage = "none";
			}
			node.style.backgroundColor = "rgb(" + rgb.toRgb().join(",") + ")";
		}, "onEnd":function () {
			if (bgImage) {
				node.style.backgroundImage = bgImage;
			}
			if (wasTransparent) {
				node.style.backgroundColor = "transparent";
			}
			if (callback) {
				callback(node, anim);
			}
		}});
		anims.push(anim);
	});
	return dojo.lfx.combine(anims);
};
dojo.lfx.html.unhighlight = function (nodes, endColor, duration, easing, callback) {
	nodes = dojo.lfx.html._byId(nodes);
	var anims = [];
	dojo.lang.forEach(nodes, function (node) {
		var color = new dojo.gfx.color.Color(dojo.html.getBackgroundColor(node));
		var rgb = new dojo.gfx.color.Color(endColor);
		var bgImage = dojo.html.getStyle(node, "background-image");
		var anim = dojo.lfx.propertyAnimation(node, {"background-color":{start:color, end:rgb}}, duration, easing, {"beforeBegin":function () {
			if (bgImage) {
				node.style.backgroundImage = "none";
			}
			node.style.backgroundColor = "rgb(" + color.toRgb().join(",") + ")";
		}, "onEnd":function () {
			if (callback) {
				callback(node, anim);
			}
		}});
		anims.push(anim);
	});
	return dojo.lfx.combine(anims);
};
dojo.lang.mixin(dojo.lfx, dojo.lfx.html);

