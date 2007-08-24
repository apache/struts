/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.html.iframe");
dojo.require("dojo.html.util");
dojo.html.iframeContentWindow = function (iframe_el) {
	var win = dojo.html.getDocumentWindow(dojo.html.iframeContentDocument(iframe_el)) || dojo.html.iframeContentDocument(iframe_el).__parent__ || (iframe_el.name && document.frames[iframe_el.name]) || null;
	return win;
};
dojo.html.iframeContentDocument = function (iframe_el) {
	var doc = iframe_el.contentDocument || ((iframe_el.contentWindow) && (iframe_el.contentWindow.document)) || ((iframe_el.name) && (document.frames[iframe_el.name]) && (document.frames[iframe_el.name].document)) || null;
	return doc;
};
dojo.html.BackgroundIframe = function (node) {
	if (dojo.render.html.ie55 || dojo.render.html.ie60) {
		var html = "<iframe src='javascript:false'" + " style='position: absolute; left: 0px; top: 0px; width: 100%; height: 100%;" + "z-index: -1; filter:Alpha(Opacity=\"0\");' " + ">";
		this.iframe = dojo.doc().createElement(html);
		this.iframe.tabIndex = -1;
		if (node) {
			node.appendChild(this.iframe);
			this.domNode = node;
		} else {
			dojo.body().appendChild(this.iframe);
			this.iframe.style.display = "none";
		}
	}
};
dojo.lang.extend(dojo.html.BackgroundIframe, {iframe:null, onResized:function () {
	if (this.iframe && this.domNode && this.domNode.parentNode) {
		var outer = dojo.html.getMarginBox(this.domNode);
		if (outer.width == 0 || outer.height == 0) {
			dojo.lang.setTimeout(this, this.onResized, 100);
			return;
		}
		this.iframe.style.width = outer.width + "px";
		this.iframe.style.height = outer.height + "px";
	}
}, size:function (node) {
	if (!this.iframe) {
		return;
	}
	var coords = dojo.html.toCoordinateObject(node, true, dojo.html.boxSizing.BORDER_BOX);
	with (this.iframe.style) {
		width = coords.width + "px";
		height = coords.height + "px";
		left = coords.left + "px";
		top = coords.top + "px";
	}
}, setZIndex:function (node) {
	if (!this.iframe) {
		return;
	}
	if (dojo.dom.isNode(node)) {
		this.iframe.style.zIndex = dojo.html.getStyle(node, "z-index") - 1;
	} else {
		if (!isNaN(node)) {
			this.iframe.style.zIndex = node;
		}
	}
}, show:function () {
	if (this.iframe) {
		this.iframe.style.display = "block";
	}
}, hide:function () {
	if (this.iframe) {
		this.iframe.style.display = "none";
	}
}, remove:function () {
	if (this.iframe) {
		dojo.html.removeNode(this.iframe, true);
		delete this.iframe;
		this.iframe = null;
	}
}});

