/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.Show");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.HtmlWidget");
dojo.require("dojo.uri.Uri");
dojo.require("dojo.event.*");
dojo.require("dojo.lfx.*");
dojo.require("dojo.math.curves");
dojo.require("dojo.lang.common");
dojo.require("dojo.lang.func");
dojo.widget.defineWidget("dojo.widget.Show", dojo.widget.HtmlWidget, function () {
	this._slides = [];
}, {isContainer:true, _slide:-1, body:null, nav:null, hider:null, select:null, option:null, inNav:false, debugPane:null, noClick:false, templateString:"<div class=\"dojoShow\">\n\t<div dojoAttachPoint=\"contentNode\"></div>\n\t<div class=\"dojoShowNav\" dojoAttachPoint=\"nav\">\n\t\t<div class=\"dojoShowHider\" dojoAttachPoint=\"hider\"></div>\n\t\t<span unselectable=\"on\" style=\"cursor: default;\" dojoAttachEvent=\"onClick:previousSlide\">&lt;</span>\n\t\t<select dojoAttachEvent=\"onClick:gotoSlideByEvent\" dojoAttachPoint=\"select\">\n\t\t\t<option dojoAttachPoint=\"option\">Title</option>\n\t\t</select>\n\t\t<span unselectable=\"on\" style=\"cursor: default;\" dojoAttachEvent=\"onClick:nextSlide\">&gt;</span>\n\t</div>\n</div>\n", templateCssString:"@media screen {\n\thtml, body {\n\t\tmargin: 0px;\n\t\tpadding: 0px;\n\t\twidth: 100%;\n\t}\n\th1 {\n\t\tfont-size: 50px;\n\t}\n\tp, li {\n\t\tfont-size: 30px;\n\t}\n\t.dojoShowNav {\n\t\tbackground: #369;\n\t\toverflow: hidden;\n\t\tposition: absolute;\n\t\theight: 5px;\n\t\tbottom: 0px;\n\t\tleft: 0px;\n\t\twidth: 100%;\n\t\ttext-align: center;\n\t}\n\t.dojoShowNav input {\n\t\tmargin: 0px;\n\t}\n\t.dojoShowHider {\n\t\theight: 5px;\n\t\toverflow: hidden;\n\t\twidth: 100%;\n\t}\n\t.dojoShowPrint {\n\t\tposition: absolute;\n\t\tleft: 5px;\n\t\ttop: 0px;\n\t}\n\t.dojoShow {\n\t\tdisplay: none;\n\t}\n}\n@media print {\n\t.dojoShow {\n\t\tdisplay: none !important;\n\t}\n\t.dojoShowPrint {\n\t\tdisplay: block !important;\n\t}\n\t.dojoShowPrintSlide {\n\t\tborder: 1px solid #aaa;\n\t\tpadding: 10px;\n\t\tmargin-bottom: 15px;\n\t}\n\t.dojoShowPrintSlide, ul {\n\tpage-break-inside: avoid;\n\t}\n\th1 {\n\t\tmargin-top: 0;\n\t\tpage-break-after: avoid;\n\t}\n}\n", templateCssPath:dojo.uri.moduleUri("dojo.widget", "templates/Show.css"), fillInTemplate:function (args, frag) {
	if (args.debugPane) {
		var dp = this.debugPane = dojo.widget.byId(args.debugPane);
		dp.hide();
		dojo.event.connect(dp, "closeWindow", dojo.lang.hitch(this, function () {
			this.debugPane = false;
		}));
	}
	var source = this.getFragNodeRef(frag);
	this.sourceNode = dojo.body().appendChild(source.cloneNode(true));
	for (var i = 0, child; child = this.sourceNode.childNodes[i]; i++) {
		if (child.tagName && child.getAttribute("dojotype").toLowerCase() == "showslide") {
			child.className = "dojoShowPrintSlide";
			child.innerHTML = "<h1>" + child.title + "</h1>" + child.innerHTML;
		}
	}
	this.sourceNode.className = "dojoShowPrint";
	this.sourceNode.style.display = "none";
	dojo.event.connect(document, "onclick", this, "gotoSlideByEvent");
	if (dojo.render.html.ie) {
		dojo.event.connect(document, "onkeydown", this, "gotoSlideByEvent");
	} else {
		dojo.event.connect(document, "onkeypress", this, "gotoSlideByEvent");
	}
	dojo.event.connect(window, "onresize", this, "resizeWindow");
	dojo.event.connect(this.nav, "onmousemove", this, "popUpNav");
}, postCreate:function () {
	this._slides = [];
	for (var i = 0, child; child = this.children[i]; i++) {
		if (child.widgetType == "ShowSlide") {
			this._slides.push(child);
			this.option.text = child.title + " (" + (i + 1) + ")";
			this.option.parentNode.insertBefore(this.option.cloneNode(true), this.option);
		}
	}
	this.option.parentNode.removeChild(this.option);
	this.domNode.style.display = "block";
	this.resizeWindow();
	this.gotoSlide(0, true);
	dojo.addOnLoad(dojo.lang.hitch(this, function () {
		var th = window.location.hash;
		if (th.length) {
			var parts = ("" + window.location).split(this.widgetId + "_SlideNo_");
			if (parts.length > 1) {
				setTimeout(dojo.lang.hitch(this, function () {
					this.gotoSlide(parseInt(parts[1]), true);
				}), 300);
			}
		}
	}));
}, gotoSlide:function (slide, preventSetHash) {
	if (slide == this._slide) {
		return;
	}
	if (!this._slides[slide]) {
		for (var i = 0, child; child = this._slides[i]; i++) {
			if (child.title == slide) {
				slide = i;
				break;
			}
		}
	}
	if (!this._slides[slide]) {
		return;
	}
	if (this.debugPane) {
		if (this._slides[slide].debug) {
			this.debugPane.show();
		} else {
			this.debugPane.hide();
		}
	}
	if (this._slide != -1) {
		while (this._slides[this._slide].previousAction()) {
		}
	}
	if (!preventSetHash) {
		window.location.href = "#" + this.widgetId + "_SlideNo_" + slide;
	}
	if (this._slides[this._slide]) {
		this._slides[this._slide].hide();
	}
	this._slide = slide;
	this.select.selectedIndex = slide;
	var cn = this.contentNode;
	while (cn.firstChild) {
		cn.removeChild(cn.firstChild);
	}
	cn.appendChild(this._slides[slide].domNode);
	this._slides[slide].show();
}, gotoSlideByEvent:function (event) {
	var node = event.target;
	var type = event.type;
	if (type == "click") {
		if (node.tagName == "OPTION" && node.parentNode == this.select) {
			this.gotoSlide(node.index);
		} else {
			if (node == this.select) {
				this.gotoSlide(node.selectedIndex);
			} else {
				this.nextSlide(event);
			}
		}
	} else {
		if (type == "keydown" || type == "keypress") {
			var key = event.keyCode;
			var ch = event.charCode;
			if (key == 63234 || key == 37) {
				this.previousSlide(event);
			} else {
				if (key == 63235 || key == 39 || ch == 32) {
					this.nextSlide(event);
				}
			}
		}
	}
}, nextSlide:function (event) {
	if (!this.stopEvent(event)) {
		return false;
	}
	if (!this._slides[this._slide].nextAction(event)) {
		if ((this._slide + 1) != this._slides.length) {
			this.gotoSlide(this._slide + 1);
			return true;
		}
		return false;
	}
}, previousSlide:function (event) {
	if (!this.stopEvent(event)) {
		return false;
	}
	if (!this._slides[this._slide].previousAction(event)) {
		if ((this._slide - 1) != -1) {
			this.gotoSlide(this._slide - 1);
			return true;
		}
		return false;
	}
}, stopEvent:function (ev) {
	if (!ev) {
		return true;
	}
	if (ev.type == "click" && (this._slides[this._slide].noClick || this.noClick)) {
		return false;
	}
	var target = ev.target;
	while (target != null) {
		if (target == this.domNode) {
			target = ev.target;
			break;
		}
		target = target.parentNode;
	}
	if (!dojo.dom.isDescendantOf(target, this.nav)) {
		while (target && target != this.domNode) {
			if (target.tagName == "A" || target.tagName == "INPUT" || target.tagName == "TEXTAREA" || target.tagName == "SELECT") {
				return false;
			}
			if (typeof target.onclick == "function" || typeof target.onkeypress == "function") {
				return false;
			}
			target = target.parentNode;
		}
	}
	if (window.event) {
		ev.returnValue = false;
		ev.cancelBubble = true;
	} else {
		ev.preventDefault();
		ev.stopPropagation();
	}
	return true;
}, popUpNav:function () {
	if (!this.inNav) {
		dojo.lfx.propertyAnimation(this.nav, {"height":{start:5, end:30}}, 250).play();
	}
	clearTimeout(this.inNav);
	this.inNav = setTimeout(dojo.lang.hitch(this, "hideNav"), 2000);
}, hideNav:function () {
	clearTimeout(this.inNav);
	this.inNav = false;
	dojo.lfx.propertyAnimation(this.nav, {"height":{start:30, end:5}}, 250).play();
}, resizeWindow:function (ev) {
	dojo.body().style.height = "auto";
	var h = Math.max(document.documentElement.scrollHeight || dojo.body().scrollHeight, dojo.html.getViewport().height);
	dojo.body().style.height = h + "px";
}});

