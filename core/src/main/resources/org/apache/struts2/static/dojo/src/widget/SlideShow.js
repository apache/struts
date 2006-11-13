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

dojo.widget.defineWidget(
	"dojo.widget.SlideShow",
	dojo.widget.HtmlWidget,
	{
		templatePath: dojo.uri.dojoUri("src/widget/templates/SlideShow.html"),
		templateCssPath: dojo.uri.dojoUri("src/widget/templates/SlideShow.css"),

		// useful properties
		imgUrls: [],		// the images we'll go through
		imgUrlBase: "",
		urlsIdx: 0,		// where in the images we are
		delay: 4000, 		// give it 4 seconds
		transitionInterval: 2000, // 2 seconds
		imgWidth: 800,	// img width
		imgHeight: 600,	// img height
		background: "img2", // what's in the bg
		foreground: "img1", // what's in the fg
		stopped: false,	// should I stay or should I go?
		fadeAnim: null, // references our animation

		// our DOM nodes:
		imagesContainer: null,
		startStopButton: null,
		controlsContainer: null,
		img1: null,
		img2: null,
		preventCache: false,

		fillInTemplate: function(){
			// safari will cache the images and not fire an image onload event if
			// there are only two images in the slideshow
			if(dojo.render.html.safari && this.imgUrls.length == 2) {
				this.preventCache = true;
			}
			dojo.html.setOpacity(this.img1, 0.9999);
			dojo.html.setOpacity(this.img2, 0.9999);
			with(this.imagesContainer.style){
				width = this.imgWidth+"px";
				height = this.imgHeight+"px";
			}
			with(this.img1.style){
				width = this.imgWidth+"px";
				height = this.imgHeight+"px";
			}
			with(this.img2.style){
				width = this.imgWidth+"px";
				height = this.imgHeight+"px";
			}
			if(this.imgUrls.length>1){
				this.img2.src = this.imgUrlBase+this.imgUrls[this.urlsIdx++] + this.getUrlSuffix();
				this.endTransition();
			}else{
				this.img1.src = this.imgUrlBase+this.imgUrls[this.urlsIdx++] + this.getUrlSuffix();
			}
		},

		getUrlSuffix: function() {
			if(this.preventCache) {
				return "?ts=" + (new Date()).getTime();
			} else {
				return "";
			}
		},
		
		togglePaused: function(){
			dojo.debug("pause");
			if(this.stopped){
				this.stopped = false;
				this.backgroundImageLoaded();
				this.startStopButton.value= "pause";
			}else{
				this.stopped = true;
				this.startStopButton.value= "play";
			}
		},

		backgroundImageLoaded: function(){
			// start fading out the foreground image
			if(this.stopped){ return; }

			// actually start the fadeOut effect
			// NOTE: if we wanted to use other transition types, we'd set them up
			// 		 here as well
			if(this.fadeAnim) {
				this.fadeAnim.stop();
			}
			this.fadeAnim = dojo.lfx.fadeOut(this[this.foreground], 
				this.transitionInterval, null);
			dojo.event.connect(this.fadeAnim,"onEnd",this,"endTransition");
			this.fadeAnim.play();
		},

		endTransition: function(){
			// move the foreground image to the background 
			with(this[this.background].style){ zIndex = parseInt(zIndex)+1; }
			with(this[this.foreground].style){ zIndex = parseInt(zIndex)-1; }

			// fg/bg book-keeping
			var tmp = this.foreground;
			this.foreground = this.background;
			this.background = tmp;
			// keep on truckin
			this.loadNextImage();
		},

		loadNextImage: function(){
			// load a new image in that container, and make sure it informs
			// us when it finishes loading
			dojo.event.kwConnect({
				srcObj: this[this.background],
				srcFunc: "onload",
				adviceObj: this,
				adviceFunc: "backgroundImageLoaded",
				once: true, // make sure we only ever hear about it once
				delay: this.delay
			});
			dojo.html.setOpacity(this[this.background], 1.0);
			this[this.background].src = this.imgUrlBase+this.imgUrls[this.urlsIdx++];
			if(this.urlsIdx>(this.imgUrls.length-1)){
				this.urlsIdx = 0;
			}
		}
	}
);
