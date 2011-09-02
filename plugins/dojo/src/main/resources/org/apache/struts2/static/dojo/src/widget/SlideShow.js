/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.SlideShow");
dojo.require("dojo.event.*");
dojo.require("dojo.widget.*");
dojo.require("dojo.lfx.*");
dojo.require("dojo.html.display");
dojo.widget.defineWidget("dojo.widget.SlideShow", dojo.widget.HtmlWidget, {templateString:"<div style=\"position: relative; padding: 3px;\">\n\t\t<div>\n\t\t\t<input type=\"button\" value=\"pause\" \n\t\t\t\tdojoAttachPoint=\"startStopButton\"\n\t\t\t\tdojoAttachEvent=\"onClick: togglePaused;\">\n\t\t</div>\n\t\t<div style=\"position: relative; width: ${this.width}; height: ${this.height};\"\n\t\t\tdojoAttachPoint=\"imagesContainer\"\n\t\t\tdojoAttachEvent=\"onClick: togglePaused;\">\n\t\t\t<img dojoAttachPoint=\"img1\" class=\"slideShowImg\" \n\t\t\t\tstyle=\"z-index: 1; width: ${this.width}; height: ${this.height};\"  />\n\t\t\t<img dojoAttachPoint=\"img2\" class=\"slideShowImg\" \n\t\t\t\tstyle=\"z-index: 0; width: ${this.width}; height: ${this.height};\" />\n\t\t</div>\t\n</div>\n", templateCssString:".slideShowImg {\n\tposition: absolute;\n\tleft: 0px;\n\ttop: 0px; \n\tborder: 2px solid #4d4d4d;\n\tpadding: 0px;\n\tmargin: 0px;\n}\n\n", templateCssPath:dojo.uri.moduleUri("dojo.widget", "templates/SlideShow.css"), imgUrls:[], imgUrlBase:"", delay:4000, transitionInterval:2000, imgWidth:800, imgHeight:600, preventCache:false, stopped:false, _urlsIdx:0, _background:"img2", _foreground:"img1", fadeAnim:null, startStopButton:null, img1:null, img2:null, postMixInProperties:function () {
	this.width = this.imgWidth + "px";
	this.height = this.imgHeight + "px";
}, fillInTemplate:function () {
	if (dojo.render.html.safari && this.imgUrls.length == 2) {
		this.preventCache = true;
	}
	dojo.html.setOpacity(this.img1, 0.9999);
	dojo.html.setOpacity(this.img2, 0.9999);
	if (this.imgUrls.length > 1) {
		this.img2.src = this.imgUrlBase + this.imgUrls[this._urlsIdx++] + this._getUrlSuffix();
		this._endTransition();
	} else {
		this.img1.src = this.imgUrlBase + this.imgUrls[this._urlsIdx++] + this._getUrlSuffix();
	}
}, _getUrlSuffix:function () {
	if (this.preventCache) {
		return "?ts=" + (new Date()).getTime();
	} else {
		return "";
	}
}, togglePaused:function () {
	if (this.stopped) {
		this.stopped = false;
		this._backgroundImageLoaded();
		this.startStopButton.value = "pause";
	} else {
		this.stopped = true;
		this.startStopButton.value = "play";
	}
}, _backgroundImageLoaded:function () {
	if (this.stopped) {
		return;
	}
	if (this.fadeAnim) {
		this.fadeAnim.stop();
	}
	this.fadeAnim = dojo.lfx.fadeOut(this[this._foreground], this.transitionInterval, null);
	dojo.event.connect(this.fadeAnim, "onEnd", this, "_endTransition");
	this.fadeAnim.play();
}, _endTransition:function () {
	with (this[this._background].style) {
		zIndex = parseInt(zIndex) + 1;
	}
	with (this[this._foreground].style) {
		zIndex = parseInt(zIndex) - 1;
	}
	var tmp = this._foreground;
	this._foreground = this._background;
	this._background = tmp;
	this._loadNextImage();
}, _loadNextImage:function () {
	dojo.event.kwConnect({srcObj:this[this._background], srcFunc:"onload", adviceObj:this, adviceFunc:"_backgroundImageLoaded", once:true, delay:this.delay});
	dojo.html.setOpacity(this[this._background], 1);
	this[this._background].src = this.imgUrlBase + this.imgUrls[this._urlsIdx++];
	if (this._urlsIdx > (this.imgUrls.length - 1)) {
		this._urlsIdx = 0;
	}
}});

