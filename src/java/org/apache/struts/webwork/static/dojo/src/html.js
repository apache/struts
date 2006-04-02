/*
	Copyright (c) 2004-2005, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.html");
dojo.require("dojo.dom");
dojo.require("dojo.style");
dojo.require("dojo.string");

dojo.lang.mixin(dojo.html, dojo.dom);
dojo.lang.mixin(dojo.html, dojo.style);

// FIXME: we are going to assume that we can throw any and every rendering
// engine into the IE 5.x box model. In Mozilla, we do this w/ CSS.
// Need to investigate for KHTML and Opera

dojo.html.clearSelection = function(){
	try{
		if(window["getSelection"]){ 
			if(dojo.render.html.safari){
				// pulled from WebCore/ecma/kjs_window.cpp, line 2536
				window.getSelection().collapse();
			}else{
				window.getSelection().removeAllRanges();
			}
		}else if((document.selection)&&(document.selection.clear)){
			document.selection.clear();
		}
		return true;
	}catch(e){
		dojo.debug(e);
		return false;
	}
}

dojo.html.disableSelection = function(element){
	element = element||dojo.html.body();
	var h = dojo.render.html;
	
	if(h.mozilla){
		element.style.MozUserSelect = "none";
	}else if(h.safari){
		element.style.KhtmlUserSelect = "none"; 
	}else if(h.ie){
		element.unselectable = "on";
	}else{
		return false;
	}
	return true;
}

dojo.html.enableSelection = function(element){
	element = element||dojo.html.body();
	
	var h = dojo.render.html;
	if(h.mozilla){ 
		element.style.MozUserSelect = ""; 
	}else if(h.safari){
		element.style.KhtmlUserSelect = "";
	}else if(h.ie){
		element.unselectable = "off";
	}else{
		return false;
	}
	return true;
}

dojo.html.selectElement = function(element){
	if(document.selection && dojo.html.body().createTextRange){ // IE
		var range = dojo.html.body().createTextRange();
		range.moveToElementText(element);
		range.select();
	}else if(window["getSelection"]){
		var selection = window.getSelection();
		// FIXME: does this work on Safari?
		if(selection["selectAllChildren"]){ // Mozilla
			selection.selectAllChildren(element);
		}
	}
}

dojo.html.isSelectionCollapsed = function(){
	if(document["selection"]){ // IE
		return document.selection.createRange().text == "";
	}else if(window["getSelection"]){
		var selection = window.getSelection();
		if(dojo.lang.isString(selection)){ // Safari
			return selection == "";
		}else{ // Mozilla/W3
			return selection.isCollapsed;
		}
	}
}

dojo.html.getEventTarget = function(evt){
	if(!evt) { evt = window.event || {} };
	if(evt.srcElement) {
		return evt.srcElement;
	} else if(evt.target) {
		return evt.target;
	}
	return null;
}

// FIXME: should the next set of functions take an optional document to operate
// on so as to be useful for getting this information from iframes?
dojo.html.getScrollTop = function(){
	return document.documentElement.scrollTop || dojo.html.body().scrollTop || 0;
}

dojo.html.getScrollLeft = function(){
	return document.documentElement.scrollLeft || dojo.html.body().scrollLeft || 0;
}

dojo.html.getDocumentWidth = function(){
	dojo.deprecated("dojo.html.getDocument* has been deprecated in favor of dojo.html.getViewport*");
	return dojo.html.getViewportWidth();
}

dojo.html.getDocumentHeight = function(){
	dojo.deprecated("dojo.html.getDocument* has been deprecated in favor of dojo.html.getViewport*");
	return dojo.html.getViewportHeight();
}

dojo.html.getDocumentSize = function(){
	dojo.deprecated("dojo.html.getDocument* has been deprecated in favor of dojo.html.getViewport*");
	return dojo.html.getViewportSize();
}

dojo.html.getViewportWidth = function(){
	var w = 0;

	if(window.innerWidth){
		w = window.innerWidth;
	}

	if(dojo.exists(document, "documentElement.clientWidth")){
		// IE6 Strict
		var w2 = document.documentElement.clientWidth;
		// this lets us account for scrollbars
		if(!w || w2 && w2 < w) {
			w = w2;
		}
		return w;
	}

	if(document.body){
		// IE
		return document.body.clientWidth;
	}

	return 0;
}

dojo.html.getViewportHeight = function(){
	if (window.innerHeight){
		return window.innerHeight;
	}

	if (dojo.exists(document, "documentElement.clientHeight")){
		// IE6 Strict
		return document.documentElement.clientHeight;
	}

	if (document.body){
		// IE
		return document.body.clientHeight;
	}

	return 0;
}

dojo.html.getViewportSize = function(){
	var ret = [dojo.html.getViewportWidth(), dojo.html.getViewportHeight()];
	ret.w = ret[0];
	ret.h = ret[1];
	return ret;
}

dojo.html.getScrollOffset = function(){
	var ret = [0, 0];

	if(window.pageYOffset){
		ret = [window.pageXOffset, window.pageYOffset];
	}else if(dojo.exists(document, "documentElement.scrollTop")){
		ret = [document.documentElement.scrollLeft, document.documentElement.scrollTop];
	} else if(document.body){
		ret = [document.body.scrollLeft, document.body.scrollTop];
	}

	ret.x = ret[0];
	ret.y = ret[1];
	return ret;
}

dojo.html.getParentOfType = function(node, type){
	dojo.deprecated("dojo.html.getParentOfType has been deprecated in favor of dojo.html.getParentByType*");
	return dojo.html.getParentByType(node, type);
}

dojo.html.getParentByType = function(node, type) {
	var parent = node;
	type = type.toLowerCase();
	while((parent)&&(parent.nodeName.toLowerCase()!=type)){
		if(parent==(document["body"]||document["documentElement"])){
			return null;
		}
		parent = parent.parentNode;
	}
	return parent;
}

// RAR: this function comes from nwidgets and is more-or-less unmodified.
// We should probably look ant Burst and f(m)'s equivalents
dojo.html.getAttribute = function(node, attr){
	// FIXME: need to add support for attr-specific accessors
	if((!node)||(!node.getAttribute)){
		// if(attr !== 'nwType'){
		//	alert("getAttr of '" + attr + "' with bad node"); 
		// }
		return null;
	}
	var ta = typeof attr == 'string' ? attr : new String(attr);

	// first try the approach most likely to succeed
	var v = node.getAttribute(ta.toUpperCase());
	if((v)&&(typeof v == 'string')&&(v!="")){ return v; }

	// try returning the attributes value, if we couldn't get it as a string
	if(v && v.value){ return v.value; }

	// this should work on Opera 7, but it's a little on the crashy side
	if((node.getAttributeNode)&&(node.getAttributeNode(ta))){
		return (node.getAttributeNode(ta)).value;
	}else if(node.getAttribute(ta)){
		return node.getAttribute(ta);
	}else if(node.getAttribute(ta.toLowerCase())){
		return node.getAttribute(ta.toLowerCase());
	}
	return null;
}
	
/**
 *	Determines whether or not the specified node carries a value for the
 *	attribute in question.
 */
dojo.html.hasAttribute = function(node, attr){
	return dojo.html.getAttribute(node, attr) ? true : false;
}
	
/**
 * Returns the string value of the list of CSS classes currently assigned
 * directly to the node in question. Returns an empty string if no class attribute
 * is found;
 */
dojo.html.getClass = function(node){
  if(!node){ return ""; }
  var cs = "";
	if(node.className){
		cs = node.className;
	}else if(dojo.html.hasAttribute(node, "class")){
		cs = dojo.html.getAttribute(node, "class");
	}
	return dojo.string.trim(cs);
}

/**
 * Returns an array of CSS classes currently assigned
 * directly to the node in question. Returns an empty array if no classes
 * are found;
 */
dojo.html.getClasses = function(node) {
	var c = dojo.html.getClass(node);
	return (c == "") ? [] : c.split(/\s+/g);
}

/**
 * Returns whether or not the specified classname is a portion of the
 * class list currently applied to the node. Does not cover cascaded
 * styles, only classes directly applied to the node.
 */
dojo.html.hasClass = function(node, classname){
	return dojo.lang.inArray(dojo.html.getClasses(node), classname);
}

/**
 * Adds the specified class to the beginning of the class list on the
 * passed node. This gives the specified class the highest precidence
 * when style cascading is calculated for the node. Returns true or
 * false; indicating success or failure of the operation, respectively.
 */
dojo.html.prependClass = function(node, classStr){
	if(!node){ return false; }
	classStr += " " + dojo.html.getClass(node);
	return dojo.html.setClass(node, classStr);
}

/**
 * Adds the specified class to the end of the class list on the
 *	passed &node;. Returns &true; or &false; indicating success or failure.
 */
dojo.html.addClass = function(node, classStr){
	if (!node) { return false; }
	if (dojo.html.hasClass(node, classStr)) {
	  return false;
	}
	classStr = dojo.string.trim(dojo.html.getClass(node) + " " + classStr);
	return dojo.html.setClass(node, classStr);
}

/**
 *	Clobbers the existing list of classes for the node, replacing it with
 *	the list given in the 2nd argument. Returns true or false
 *	indicating success or failure.
 */
dojo.html.setClass = function(node, classStr){
	if(!node){ return false; }
	var cs = new String(classStr);
	try{
		if(typeof node.className == "string"){
			node.className = cs;
		}else if(node.setAttribute){
			node.setAttribute("class", classStr);
			node.className = cs;
		}else{
			return false;
		}
	}catch(e){
		dojo.debug("dojo.html.setClass() failed", e);
	}
	return true;
}

/**
 * Removes the className from the node;. Returns
 * true or false indicating success or failure.
 */ 
dojo.html.removeClass = function(node, classStr, allowPartialMatches){
	if(!node){ return false; }
	var classStr = dojo.string.trim(new String(classStr));

	try{
		var cs = dojo.html.getClasses(node);
		var nca	= [];
		if(allowPartialMatches){
			for(var i = 0; i<cs.length; i++){
				if(cs[i].indexOf(classStr) == -1){ 
					nca.push(cs[i]);
				}
			}
		}else{
			for(var i=0; i<cs.length; i++){
				if(cs[i] != classStr){ 
					nca.push(cs[i]);
				}
			}
		}
		dojo.html.setClass(node, nca.join(" "));
	}catch(e){
		dojo.debug("dojo.html.removeClass() failed", e);
	}

	return true;
}

/**
 * Replaces 'oldClass' and adds 'newClass' to node
 */
dojo.html.replaceClass = function(node, newClass, oldClass) {
	dojo.html.removeClass(node, oldClass);
	dojo.html.addClass(node, newClass);
}

// Enum type for getElementsByClass classMatchType arg:
dojo.html.classMatchType = {
	ContainsAll : 0, // all of the classes are part of the node's class (default)
	ContainsAny : 1, // any of the classes are part of the node's class
	IsOnly : 2 // only all of the classes are part of the node's class
}


/**
 * Returns an array of nodes for the given classStr, children of a
 * parent, and optionally of a certain nodeType
 */
dojo.html.getElementsByClass = function(classStr, parent, nodeType, classMatchType){
	if(!parent){ parent = document; }
	var classes = classStr.split(/\s+/g);
	var nodes = [];
	if( classMatchType != 1 && classMatchType != 2 ) classMatchType = 0; // make it enum
	var reClass = new RegExp("(\\s|^)((" + classes.join(")|(") + "))(\\s|$)");

	// FIXME: doesn't have correct parent support!
	if(!nodeType){ nodeType = "*"; }
	var candidateNodes = parent.getElementsByTagName(nodeType);

	outer:
	for(var i = 0; i < candidateNodes.length; i++) {
		var node = candidateNodes[i];
		var nodeClasses = dojo.html.getClasses(node);
		if(nodeClasses.length == 0) { continue outer; }
		var matches = 0;

		for(var j = 0; j < nodeClasses.length; j++) {
			if( reClass.test(nodeClasses[j]) ) {
				if( classMatchType == dojo.html.classMatchType.ContainsAny ) {
					nodes.push(node);
					continue outer;
				} else {
					matches++;
				}
			} else {
				if( classMatchType == dojo.html.classMatchType.IsOnly ) {
					continue outer;
				}
			}
		}

		if( matches == classes.length ) {
			if( classMatchType == dojo.html.classMatchType.IsOnly && matches == nodeClasses.length ) {
				nodes.push(node);
			} else if( classMatchType == dojo.html.classMatchType.ContainsAll ) {
				nodes.push(node);
			}
		}
	}
	
	return nodes;
}
dojo.html.getElementsByClassName = dojo.html.getElementsByClass;

/**
 * Calculates the mouse's direction of gravity relative to the centre
 * of the given node.
 * <p>
 * If you wanted to insert a node into a DOM tree based on the mouse
 * position you might use the following code:
 * <pre>
 * if (gravity(node, e) & gravity.NORTH) { [insert before]; }
 * else { [insert after]; }
 * </pre>
 *
 * @param node The node
 * @param e		The event containing the mouse coordinates
 * @return		 The directions, NORTH or SOUTH and EAST or WEST. These
 *						 are properties of the function.
 */
dojo.html.gravity = function(node, e){
	var mousex = e.pageX || e.clientX + dojo.html.body().scrollLeft;
	var mousey = e.pageY || e.clientY + dojo.html.body().scrollTop;
	
	with (dojo.html) {
		var nodecenterx = getAbsoluteX(node) + (getInnerWidth(node) / 2);
		var nodecentery = getAbsoluteY(node) + (getInnerHeight(node) / 2);
	}
	
	with (dojo.html.gravity) {
		return ((mousex < nodecenterx ? WEST : EAST) |
			(mousey < nodecentery ? NORTH : SOUTH));
	}
}

dojo.html.gravity.NORTH = 1;
dojo.html.gravity.SOUTH = 1 << 1;
dojo.html.gravity.EAST = 1 << 2;
dojo.html.gravity.WEST = 1 << 3;
	
dojo.html.overElement = function(element, e){
	var mousex = e.pageX || e.clientX + dojo.html.body().scrollLeft;
	var mousey = e.pageY || e.clientY + dojo.html.body().scrollTop;
	
	with(dojo.html){
		var top = getAbsoluteY(element);
		var bottom = top + getInnerHeight(element);
		var left = getAbsoluteX(element);
		var right = left + getInnerWidth(element);
	}
	
	return (mousex >= left && mousex <= right &&
		mousey >= top && mousey <= bottom);
}

/**
 * Attempts to return the text as it would be rendered, with the line breaks
 * sorted out nicely. Unfinished.
 */
dojo.html.renderedTextContent = function(node){
	var result = "";
	if (node == null) { return result; }
	for (var i = 0; i < node.childNodes.length; i++) {
		switch (node.childNodes[i].nodeType) {
			case 1: // ELEMENT_NODE
			case 5: // ENTITY_REFERENCE_NODE
				var display = "unknown";
				try {
					display = dojo.style.getStyle(node.childNodes[i], "display");
				} catch(E) {}
				switch (display) {
					case "block": case "list-item": case "run-in":
					case "table": case "table-row-group": case "table-header-group":
					case "table-footer-group": case "table-row": case "table-column-group":
					case "table-column": case "table-cell": case "table-caption":
						// TODO: this shouldn't insert double spaces on aligning blocks
						result += "\n";
						result += dojo.html.renderedTextContent(node.childNodes[i]);
						result += "\n";
						break;
					
					case "none": break;
					
					default:
						if(node.childNodes[i].tagName && node.childNodes[i].tagName.toLowerCase() == "br") {
							result += "\n";
						} else {
							result += dojo.html.renderedTextContent(node.childNodes[i]);
						}
						break;
				}
				break;
			case 3: // TEXT_NODE
			case 2: // ATTRIBUTE_NODE
			case 4: // CDATA_SECTION_NODE
				var text = node.childNodes[i].nodeValue;
				var textTransform = "unknown";
				try {
					textTransform = dojo.style.getStyle(node, "text-transform");
				} catch(E) {}
				switch (textTransform){
					case "capitalize": text = dojo.string.capitalize(text); break;
					case "uppercase": text = text.toUpperCase(); break;
					case "lowercase": text = text.toLowerCase(); break;
					default: break; // leave as is
				}
				// TODO: implement
				switch (textTransform){
					case "nowrap": break;
					case "pre-wrap": break;
					case "pre-line": break;
					case "pre": break; // leave as is
					default:
						// remove whitespace and collapse first space
						text = text.replace(/\s+/, " ");
						if (/\s$/.test(result)) { text.replace(/^\s/, ""); }
						break;
				}
				result += text;
				break;
			default:
				break;
		}
	}
	return result;
}

dojo.html.setActiveStyleSheet = function(title){
	var i, a, main;
	for(i=0; (a = document.getElementsByTagName("link")[i]); i++){
		if(a.getAttribute("rel").indexOf("style") != -1 && a.getAttribute("title")){
			a.disabled = true;
			if (a.getAttribute("title") == title) { a.disabled = false; }
		}
	}
}

dojo.html.getActiveStyleSheet = function(){
	var i, a;
	// FIXME: getElementsByTagName returns a live collection. This seems like a
	// bad key for iteration.
	for(i=0; (a = document.getElementsByTagName("link")[i]); i++){
		if (a.getAttribute("rel").indexOf("style") != -1 &&
			a.getAttribute("title") && !a.disabled) { return a.getAttribute("title"); }
	}
	return null;
}

dojo.html.getPreferredStyleSheet = function(){
	var i, a;
	for(i=0; (a = document.getElementsByTagName("link")[i]); i++){
		if(a.getAttribute("rel").indexOf("style") != -1
			&& a.getAttribute("rel").indexOf("alt") == -1
			&& a.getAttribute("title")) { return a.getAttribute("title"); }
	}
	return null;
}

dojo.html.body = function(){
	return document.body || document.getElementsByTagName("body")[0];
}

dojo.html.createNodesFromText = function(txt, wrap){
	var tn = document.createElement("div");
	// tn.style.display = "none";
	tn.style.visibility= "hidden";
	document.body.appendChild(tn);
	tn.innerHTML = txt;
	tn.normalize();
	if(wrap){ 
		var ret = [];
		// start hack
		var fc = tn.firstChild;
		ret[0] = ((fc.nodeValue == " ")||(fc.nodeValue == "\t")) ? fc.nextSibling : fc;
		// end hack
		// tn.style.display = "none";
		document.body.removeChild(tn);
		return ret;
	}
	var nodes = [];
	for(var x=0; x<tn.childNodes.length; x++){
		nodes.push(tn.childNodes[x].cloneNode(true));
	}
	tn.style.display = "none";
	document.body.removeChild(tn);
	return nodes;
}

// FIXME: this should be removed after 0.2 release
if(!dojo.evalObjPath("dojo.dom.createNodesFromText")){
	dojo.dom.createNodesFromText = function() {
		dojo.deprecated("dojo.dom.createNodesFromText", "use dojo.html.createNodesFromText instead");
		return dojo.html.createNodesFromText.apply(dojo.html, arguments);
	}
}

dojo.html.isVisible = function(node){
	// FIXME: this should also look at visibility!
	return dojo.style.getComputedStyle(node||this.domNode, "display") != "none";
}

dojo.html.show  = function(node){
	if(node.style){
		node.style.display = dojo.lang.inArray(['tr', 'td', 'th'], node.tagName.toLowerCase()) ? "" : "block";
	}
}

dojo.html.hide = function(node){
	if(node.style){
		node.style.display = "none";
	}
}

// in: coordinate array [x,y,w,h] or dom node
// return: coordinate array
dojo.html.toCoordinateArray = function(coords, includeScroll) {
	if(dojo.lang.isArray(coords)){
		// coords is already an array (of format [x,y,w,h]), just return it
		while ( coords.length < 4 ) { coords.push(0); }
		while ( coords.length > 4 ) { coords.pop(); }
		var ret = coords;
	} else {
		// coords is an dom object (or dom object id); return it's coordinates
		var node = dojo.byId(coords);
		var ret = [
			dojo.html.getAbsoluteX(node, includeScroll),
			dojo.html.getAbsoluteY(node, includeScroll),
			dojo.html.getInnerWidth(node),
			dojo.html.getInnerHeight(node)
		];
	}
	ret.x = ret[0];
	ret.y = ret[1];
	ret.w = ret[2];
	ret.h = ret[3];
	return ret;
};

/* TODO: merge placeOnScreen and placeOnScreenPoint to make 1 function that allows you
 * to define which corner(s) you want to bind to. Something like so:
 *
 * kes(node, desiredX, desiredY, "TR")
 * kes(node, [desiredX, desiredY], ["TR", "BL"])
 *
 * TODO: make this function have variable call sigs
 *
 * kes(node, ptArray, cornerArray, padding, hasScroll)
 * kes(node, ptX, ptY, cornerA, cornerB, cornerC, paddingArray, hasScroll)
 */

/**
 * Keeps 'node' in the visible area of the screen while trying to
 * place closest to desiredX, desiredY. The input coordinates are
 * expected to be the desired screen position, not accounting for
 * scrolling. If you already accounted for scrolling, set 'hasScroll'
 * to true. Set padding to either a number or array for [paddingX, paddingY]
 * to put some buffer around the element you want to position.
 * NOTE: node is assumed to be absolutely or relatively positioned.
 *
 * Alternate call sig:
 *  placeOnScreen(node, [x, y], padding, hasScroll)
 *
 * Examples:
 *  placeOnScreen(node, 100, 200)
 *  placeOnScreen("myId", [800, 623], 5)
 *  placeOnScreen(node, 234, 3284, [2, 5], true)
 */
dojo.html.placeOnScreen = function(node, desiredX, desiredY, padding, hasScroll) {
	if(dojo.lang.isArray(desiredX)) {
		hasScroll = padding;
		padding = desiredY;
		desiredY = desiredX[1];
		desiredX = desiredX[0];
	}

	if(!isNaN(padding)) {
		padding = [Number(padding), Number(padding)];
	} else if(!dojo.lang.isArray(padding)) {
		padding = [0, 0];
	}

	var scroll = dojo.html.getScrollOffset();
	var view = dojo.html.getViewportSize();

	node = dojo.byId(node);
	var w = node.offsetWidth + padding[0];
	var h = node.offsetHeight + padding[1];

	if(hasScroll) {
		desiredX -= scroll.x;
		desiredY -= scroll.y;
	}

	var x = desiredX + w;
	if(x > view.w) {
		x = view.w - w;
	} else {
		x = desiredX;
	}
	x = Math.max(padding[0], x) + scroll.x;

	var y = desiredY + h;
	if(y > view.h) {
		y = view.h - h;
	} else {
		y = desiredY;
	}
	y = Math.max(padding[1], y) + scroll.y;

	node.style.left = x + "px";
	node.style.top = y + "px";

	var ret = [x, y];
	ret.x = x;
	ret.y = y;
	return ret;
}

/**
 * Like placeOnScreenPoint except that it attempts to keep one of the node's
 * corners at desiredX, desiredY. Also note that padding is only taken into
 * account if none of the corners can be kept and thus placeOnScreenPoint falls
 * back to placeOnScreen to place the node.
 *
 * Examples placing node at mouse position (where e = [Mouse event]):
 *  placeOnScreenPoint(node, e.clientX, e.clientY);
 */
dojo.html.placeOnScreenPoint = function(node, desiredX, desiredY, padding, hasScroll) {
	if(dojo.lang.isArray(desiredX)) {
		hasScroll = padding;
		padding = desiredY;
		desiredY = desiredX[1];
		desiredX = desiredX[0];
	}

	var scroll = dojo.html.getScrollOffset();
	var view = dojo.html.getViewportSize();

	node = dojo.byId(node);
	var w = node.offsetWidth;
	var h = node.offsetHeight;

	if(hasScroll) {
		desiredX -= scroll.x;
		desiredY -= scroll.y;
	}

	var x = -1, y = -1;
	//dojo.debug(desiredX + w, "<=", view.w, "&&", desiredY + h, "<=", view.h);
	if(desiredX + w <= view.w && desiredY + h <= view.h) { // TL
		x = desiredX;
		y = desiredY;
		//dojo.debug("TL", x, y);
	}

	//dojo.debug(desiredX, "<=", view.w, "&&", desiredY + h, "<=", view.h);
	if((x < 0 || y < 0) && desiredX <= view.w && desiredY + h <= view.h) { // TR
		x = desiredX - w;
		y = desiredY;
		//dojo.debug("TR", x, y);
	}

	//dojo.debug(desiredX + w, "<=", view.w, "&&", desiredY, "<=", view.h);
	if((x < 0 || y < 0) && desiredX + w <= view.w && desiredY <= view.h) { // BL
		x = desiredX;
		y = desiredY - h;
		//dojo.debug("BL", x, y);
	}

	//dojo.debug(desiredX, "<=", view.w, "&&", desiredY, "<=", view.h);
	if((x < 0 || y < 0) && desiredX <= view.w && desiredY <= view.h) { // BR
		x = desiredX - w;
		y = desiredY - h;
		//dojo.debug("BR", x, y);
	}

	if(x < 0 || y < 0 || (x + w > view.w) || (y + h > view.h)) {
		return dojo.html.placeOnScreen(node, desiredX, desiredY, padding, hasScroll);
	}

	x += scroll.x;
	y += scroll.y;

	node.style.left = x + "px";
	node.style.top = y + "px";

	var ret = [x, y];
	ret.x = x;
	ret.y = y;
	return ret;
}

/**
 * For IE z-index schenanigans
 * See Dialog widget for sample use
 */
dojo.html.BackgroundIframe = function() {
	if(this.ie) {
		this.iframe = document.createElement("<iframe frameborder='0' src='about:blank'>");
		var s = this.iframe.style;
		s.position = "absolute";
		s.left = s.top = "0px";
		s.zIndex = 2;
		s.display = "none";
		dojo.style.setOpacity(this.iframe, 0.0);
		dojo.html.body().appendChild(this.iframe);
	} else {
		this.enabled = false;
	}
}
dojo.lang.extend(dojo.html.BackgroundIframe, {
	ie: dojo.render.html.ie,
	enabled: true,
	visibile: false,
	iframe: null,
	sizeNode: null,
	sizeCoords: null,

	size: function(node /* or coords */) {
		if(!this.ie || !this.enabled) { return; }

		if(dojo.dom.isNode(node)) {
			this.sizeNode = node;
		} else if(arguments.length > 0) {
			this.sizeNode = null;
			this.sizeCoords = node;
		}
		this.update();
	},

	update: function() {
		if(!this.ie || !this.enabled) { return; }

		if(this.sizeNode) {
			this.sizeCoords = dojo.html.toCoordinateArray(this.sizeNode, true);
		} else if(this.sizeCoords) {
			this.sizeCoords = dojo.html.toCoordinateArray(this.sizeCoords, true);
		} else {
			return;
		}

		var s = this.iframe.style;
		var dims = this.sizeCoords;
		s.width = dims.w + "px";
		s.height = dims.h + "px";
		s.left = dims.x + "px";
		s.top = dims.y + "px";
	},

	setZIndex: function(node /* or number */) {
		if(!this.ie || !this.enabled) { return; }

		if(dojo.dom.isNode(node)) {
			this.iframe.zIndex = dojo.html.getStyle(node, "z-index") - 1;
		} else if(!isNaN(node)) {
			this.iframe.zIndex = node;
		}
	},

	show: function(node /* or coords */) {
		if(!this.ie || !this.enabled) { return; }

		this.size(node);
		this.iframe.style.display = "block";
	},

	hide: function() {
		if(!this.ie) { return; }
		var s = this.iframe.style;
		s.display = "none";
		s.width = s.height = "1px";
	},

	remove: function() {
		dojo.dom.removeNode(this.iframe);
	}
});
