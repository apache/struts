/*
	Copyright (c) 2004-2005, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.svg");
dojo.require("dojo.lang");
dojo.require("dojo.dom");

dojo.lang.mixin(dojo.svg, dojo.dom);

/**
 *	The Graphics object.  Hopefully gives the user a way into
 *	XPlatform rendering functions supported correctly and incorrectly.
**/
dojo.svg.graphics = dojo.svg.g = new function(d){
	this.suspend = function(){
		try { d.documentElement.suspendRedraw(0); } catch(e){ }
	};
	this.resume = function(){
		try { d.documentElement.unsuspendRedraw(0); } catch(e){ }
	};
	this.force = function(){
		try { d.documentElement.forceRedraw(); } catch(e){ }
	};
}(document);

/**
 *	The Animations control object.  Hopefully gives the user a way into
 *	XPlatform animation functions supported correctly and incorrectly.
**/
dojo.svg.animations = dojo.svg.anim = new function(d){
	this.arePaused = function(){
		try {
			return d.documentElement.animationsPaused();
		} catch(e){
			return false;
		}
	} ;
	this.pause = function(){
		try { d.documentElement.pauseAnimations(); } catch(e){ }
	};
	this.resume = function(){
		try { d.documentElement.unpauseAnimations(); } catch(e){ }
	};
}(document);

/**
 *	signatures from dojo.style.
 */
dojo.svg.toCamelCase = function(selector){
	var arr = selector.split('-'), cc = arr[0];
	for(var i = 1; i < arr.length; i++) {
		cc += arr[i].charAt(0).toUpperCase() + arr[i].substring(1);
	}
	return cc;		
};
dojo.svg.toSelectorCase = function (selector) {
	return selector.replace(/([A-Z])/g, "-$1" ).toLowerCase() ;
};
dojo.svg.getStyle = function(node, cssSelector){
	return document.defaultView.getComputedStyle(node, cssSelector);
};
dojo.svg.getNumericStyle = function(node, cssSelector){
	return parseFloat(dojo.svg.getStyle(node, cssSelector));
};

/**
 *	alpha channel operations
 */
dojo.svg.getOpacity = function(node){
	return Math.min(1.0, dojo.svg.getNumericStyle(node, "fill-opacity"));
};
dojo.svg.setOpacity = function(node, opacity){
	node.setAttributeNS(this.xmlns.svg, "fill-opacity", opacity);
	node.setAttributeNS(this.xmlns.svg, "stroke-opacity", opacity);
};
dojo.svg.clearOpacity = function(node){
	node.setAttributeNS(this.xmlns.svg, "fill-opacity", "1.0");
	node.setAttributeNS(this.xmlns.svg, "stroke-opacity", "1.0");
};

/**
 *	Coordinates and dimensions.
 */
dojo.svg.getCoords = function(node){
	if (node.getBBox) {
		var box = node.getBBox();
		return { x: box.x, y: box.y };
	}
	return null;
};
dojo.svg.setCoords = function(node, coords){
	var p = dojo.svg.getCoords();
	if (!p) return;
	var dx = p.x - coords.x;
	var dy = p.y - coords.y;
	dojo.svg.translate(node, dx, dy);
};
dojo.svg.getDimensions = function(node){
	if (node.getBBox){
		var box = node.getBBox();
		return { width: box.width, height : box.height };
	}
	return null;
};
dojo.svg.setDimensions = function(node, dim){
	//	will only support shape-based and container elements; path-based elements are ignored.
	if (node.width){
		node.width.baseVal.value = dim.width;
		node.height.baseVal.vaule = dim.height;
	}
	else if (node.r){
		node.r.baseVal.value = Math.min(dim.width, dim.height)/2;
	}
	else if (node.rx){
		node.rx.baseVal.value = dim.width/2;
		node.ry.baseVal.value = dim.height/2;
	}
};

/**
 *	Transformations.
 */
dojo.svg.translate = function(node, dx, dy){
	if (node.transform && node.ownerSVGElement && node.ownerSVGElement.createSVGTransform){
		var t = node.ownerSVGElement.createSVGTransform();
		t.setTranslate(dx, dy);
		node.transform.baseVal.appendItem(t);
	}
};
dojo.svg.scale = function(node, scaleX, scaleY){
	if (!scaleY) var scaleY = scaleX;
	if (node.transform && node.ownerSVGElement && node.ownerSVGElement.createSVGTransform){
		var t = node.ownerSVGElement.createSVGTransform();
		t.setScale(scaleX, scaleY);
		node.transform.baseVal.appendItem(t);
	}
};
dojo.svg.rotate = function(node, ang, cx, cy){
	if (node.transform && node.ownerSVGElement && node.ownerSVGElement.createSVGTransform){
		var t = node.ownerSVGElement.createSVGTransform();
		if (!cx) t.setMatrix(t.matrix.rotate(ang));
		else t.setRotate(ang, cx, cy);
		node.transform.baseVal.appendItem(t);
	}
};
dojo.svg.skew = function(node, ang, axis){
	var dir = axis || "x";
	if (node.transform && node.ownerSVGElement && node.ownerSVGElement.createSVGTransform){
		var t = node.ownerSVGElement.createSVGTransform();
		if (dir != "x") t.setSkewY(ang);
		else t.setSkewX(ang);
		node.transform.baseVal.appendItem(t);
	}
};
dojo.svg.flip = function(node, axis){
	var dir = axis || "x";
	if (node.transform && node.ownerSVGElement && node.ownerSVGElement.createSVGTransform){
		var t = node.ownerSVGElement.createSVGTransform();
		t.setMatrix((dir != "x") ? t.matrix.flipY() : t.matrix.flipX());
		node.transform.baseVal.appendItem(t);
	}
};
dojo.svg.invert = function(node){
	if (node.transform && node.ownerSVGElement && node.ownerSVGElement.createSVGTransform){
		var t = node.ownerSVGElement.createSVGTransform();
		t.setMatrix(t.matrix.inverse());
		node.transform.baseVal.appendItem(t);
	}
};
dojo.svg.applyMatrix = function(node, a, b, c, d, e, f){
	if (node.transform && node.ownerSVGElement && node.ownerSVGElement.createSVGTransform){
		var m;
		if (b){
			var m = node.ownerSVGElement.createSVGMatrix();
			m.a = a;
			m.b = b;
			m.c = c;
			m.d = d;
			m.e = e;
			m.f = f;
		} else m = a;
		var t = node.ownerSVGElement.createSVGTransform();
		t.setMatrix(m);
		node.transform.baseVal.appendItem(t);
	}
};

/**
 *	Grouping and z-index operations.
 */
dojo.svg.group = function(nodes){
	//	expect an array of nodes, attaches the group to the parent of the first node.
	var p = nodes.item(0).parentNode;
	var g = document.createElementNS(this.xmlns.svg, "g");
	for (var i = 0; i < nodes.length; i++) g.appendChild(nodes.item(i));
	p.appendChild(g);
	return g;
};
dojo.svg.ungroup = function(g){
	//	puts the children of the group on the same level as group was.
	var p = g.parentNode;
	while (g.childNodes.length > 0) p.appendChild(g.childNodes.item(0));
	p.removeChild(g);
};
//	if the node is part of a group, return the group, else return null.
dojo.svg.getGroup = function(node){
	//	if the node is part of a group, return the group, else return null.
	var a = this.getAncestors(node);
	for (var i = 0; i < a.length; i++){
		if (a[i].nodeType == this.ELEMENT_NODE && a[i].nodeName.toLowerCase() == "g")
			return a[i];
	}
	return null;
};
dojo.svg.bringToFront = function(node){
	var n = this.getGroup(node) || node;
	n.ownerSVGElement.appendChild(n);
};
dojo.svg.sendToBack = function(node){
	var n = this.getGroup(node) || node;
	n.ownerSVGElement.insertBefore(n, n.ownerSVGElement.firstChild);
};
//	TODO: possibly push node up a level in the DOM if it's at the beginning or end of the childNodes list.
dojo.svg.bringForward = function(node){
	var n = this.getGroup(node) || node;
	if (this.getLastChildElement(n.parentNode) != n){
		this.insertAfter(n, this.getNextSiblingElement(n), true);
	}
};
dojo.svg.sendBackward = function(node){
	var n = this.getGroup(node) || node;
	if (this.getFirstChildElement(n.parentNode) != n){
		this.insertBefore(n, this.getPreviousSiblingElement(n), true);
	}
};
//	modded to account for FF 1.5 mixed environment, will try ASVG first, then w3 standard.
dojo.dom.createNodesFromText = function (txt, wrap){
	var docFrag;
	if (window.parseXML) docFrag = parseXML(txt, window.document);
	else if (window.DOMParser) docFrag = (new DOMParser()).parseFromString(txt, "text/xml");
	else dojo.raise("dojo.dom.createNodesFromText: environment does not support XML parsing");
	docFrag.normalize();
	if(wrap){ 
		var ret = [docFrag.firstChild.cloneNode(true)];
		return ret;
	}
	var nodes = [];
	for(var x=0; x<docFrag.childNodes.length; x++){
		nodes.push(docFrag.childNodes.item(x).cloneNode(true));
	}
	// tn.style.display = "none";
	return nodes;
}

// FIXME: this should be removed after 0.2 release
if(!dojo.evalObjPath("dojo.dom.createNodesFromText")) {
	dojo.dom.createNodesFromText = function() {
		dojo.deprecated("dojo.dom.createNodesFromText", "use dojo.svg.createNodesFromText instead");
		dojo.svg.createNodesFromText.apply(dojo.html, arguments);
	}
}

//	IE INLINE FIX
/*
if (dojo.render.html.ie && dojo.render.svg.adobe){
	document.write("<object id=\"AdobeSVG\" classid=\"clsid:78156a80-c6a1-4bbf-8e6a-3cd390eeb4e2\"></object>");
	document.write("<?import namespace=\"svg\" urn=\"http://www.w3.org/2000/svg\" implementation=\"#AdobeSVG\"?>");
}
*/
// vim:ts=4:noet:tw=0:
