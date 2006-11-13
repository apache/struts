/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.Dialog");

dojo.require("dojo.widget.*");
dojo.require("dojo.widget.ContentPane");
dojo.require("dojo.event.*");
dojo.require("dojo.gfx.color");
dojo.require("dojo.html.layout");
dojo.require("dojo.html.display");
dojo.require("dojo.html.iframe");

// summary
//	Mixin for widgets implementing a modal dialog
dojo.declare(
	"dojo.widget.ModalDialogBase", 
	null,
	{
		isContainer: true,

		// static variables
		shared: {bg: null, bgIframe: null},

		// String
		//	provide a focusable element or element id if you need to
		//	work around FF's tendency to send focus into outer space on hide
		focusElement: "",

		// String
		//	color of viewport when displaying a dialog
		bgColor: "black",
		
		// Number
		//	opacity (0~1) of viewport color (see bgColor attribute)
		bgOpacity: 0.4,

		// Boolean
		//	if true, readjusts the dialog (and dialog background) when the user moves the scrollbar
		followScroll: true,

		trapTabs: function(/*Event*/ e){
			// summary
			//	callback on focus
			if(e.target == this.tabStartOuter) {
				if(this._fromTrap) {
					this.tabStart.focus();
					this._fromTrap = false;
				} else {
					this._fromTrap = true;
					this.tabEnd.focus();
				}
			} else if (e.target == this.tabStart) {
				if(this._fromTrap) {
					this._fromTrap = false;
				} else {
					this._fromTrap = true;
					this.tabEnd.focus();
				}
			} else if(e.target == this.tabEndOuter) {
				if(this._fromTrap) {
					this.tabEnd.focus();
					this._fromTrap = false;
				} else {
					this._fromTrap = true;
					this.tabStart.focus();
				}
			} else if(e.target == this.tabEnd) {
				if(this._fromTrap) {
					this._fromTrap = false;
				} else {
					this._fromTrap = true;
					this.tabStart.focus();
				}
			}
		},

		clearTrap: function(/*Event*/ e) {
			// summary
			//	callback on blur
			var _this = this;
			setTimeout(function() {
				_this._fromTrap = false;
			}, 100);
		},

		postCreate: function() {
			// summary
			//	if the target mixin class already defined postCreate,
			//	dojo.widget.ModalDialogBase.prototype.postCreate.call(this)
			//	should be called in its postCreate()
			with(this.domNode.style){
				position = "absolute";
				zIndex = 999;
				display = "none";
				overflow = "visible";
			}
			var b = dojo.body();
			b.appendChild(this.domNode);

			if(!this.shared.bg){
				this.shared.bg = document.createElement("div");
				this.shared.bg.className = "dialogUnderlay";
				with(this.shared.bg.style){
					position = "absolute";
					left = top = "0px";
					zIndex = 998;
					display = "none";
				}
				this.setBackgroundColor(this.bgColor);
				b.appendChild(this.shared.bg);
				this.shared.bgIframe = new dojo.html.BackgroundIframe(this.shared.bg);
			}
		},

		setBackgroundColor: function(/*String*/ color) {
			// summary
			//	changes background color specified by "bgColor" parameter
			//	usage:
			//		setBackgrounColor("black");
			//		setBackgroundColor(0xff, 0xff, 0xff);
			if(arguments.length >= 3) {
				color = new dojo.gfx.color.Color(arguments[0], arguments[1], arguments[2]);
			} else {
				color = new dojo.gfx.color.Color(color);
			}
			this.shared.bg.style.backgroundColor = color.toString();
			return this.bgColor = color;
		},

		setBackgroundOpacity: function(/*Number*/ op) {
			// summary
			//	changes background opacity set by "bgOpacity" parameter
			if(arguments.length == 0) { op = this.bgOpacity; }
			dojo.html.setOpacity(this.shared.bg, op);
			try {
				this.bgOpacity = dojo.html.getOpacity(this.shared.bg);
			} catch (e) {
				this.bgOpacity = op;
			}
			return this.bgOpacity;
		},

		_sizeBackground: function() {
			if(this.bgOpacity > 0) {
				
				var viewport = dojo.html.getViewport();
				var h = viewport.height;
				var w = viewport.width;
				with(this.shared.bg.style){
					width = w + "px";
					height = h + "px";
				}
				var scroll_offset = dojo.html.getScroll().offset;
				this.shared.bg.style.top = scroll_offset.y + "px";
				this.shared.bg.style.left = scroll_offset.x + "px";
				// process twice since the scroll bar may have been removed
				// by the previous resizing
				var viewport = dojo.html.getViewport();
				if (viewport.width != w) { this.shared.bg.style.width = viewport.width + "px"; }
				if (viewport.height != h) { this.shared.bg.style.height = viewport.height + "px"; }
			}
		},

		_showBackground: function() {
			if(this.bgOpacity > 0) {
				this.shared.bg.style.display = "block";
			}
		},

		placeModalDialog: function() {
			var scroll_offset = dojo.html.getScroll().offset;
			var viewport_size = dojo.html.getViewport();
			
			// find the size of the dialog
			var mb = dojo.html.getMarginBox(this.containerNode);
			
			var x = scroll_offset.x + (viewport_size.width - mb.width)/2;
			var y = scroll_offset.y + (viewport_size.height - mb.height)/2;

			with(this.domNode.style){
				left = x + "px";
				top = y + "px";
			}
		},

		showModalDialog: function() {
			// summary
			//	call this function in show() of subclass
			if (this.followScroll && !this._scrollConnected){
				this._scrollConnected = true;
				dojo.event.connect(window, "onscroll", this, "_onScroll");
			}
			
			this.setBackgroundOpacity();
			this._sizeBackground();
			this._showBackground();
		},

		hideModalDialog: function(){
			// summary
			//	call this function in hide() of subclass

			// workaround for FF focus going into outer space
			if (this.focusElement) { 
				dojo.byId(this.focusElement).focus(); 
				dojo.byId(this.focusElement).blur();
			}

			this.shared.bg.style.display = "none";
			this.shared.bg.style.width = this.shared.bg.style.height = "1px";

			if (this._scrollConnected){
				this._scrollConnected = false;
				dojo.event.disconnect(window, "onscroll", this, "_onScroll");
			}
		},

		_onScroll: function(){
			var scroll_offset = dojo.html.getScroll().offset;
			this.shared.bg.style.top = scroll_offset.y + "px";
			this.shared.bg.style.left = scroll_offset.x + "px";
			this.placeModalDialog();
		},

		checkSize: function() {
			if(this.isShowing()){
				this._sizeBackground();
				this.placeModalDialog();
				this.onResized();
			}
		}
	});

// summary
//	Pops up a modal dialog window, blocking access to the screen and also graying out the screen
//	Dialog is extended from ContentPane so it supports all the same parameters (href, etc.)
dojo.widget.defineWidget(
	"dojo.widget.Dialog",
	[dojo.widget.ContentPane, dojo.widget.ModalDialogBase],
	{
		templatePath: dojo.uri.dojoUri("src/widget/templates/Dialog.html"),

		// Integer
		//	number of seconds for which the user cannot dismiss the dialog
		blockDuration: 0,
		
		// Integer
		//	if set, this controls the number of seconds the dialog will be displayed before automatically disappearing
		lifetime: 0,

		show: function() {
			if(this.lifetime){
				this.timeRemaining = this.lifetime;
				if(!this.blockDuration){
					dojo.event.connect(this.shared.bg, "onclick", this, "hide");
				}else{
					dojo.event.disconnect(this.shared.bg, "onclick", this, "hide");
				}
				if(this.timerNode){
					this.timerNode.innerHTML = Math.ceil(this.timeRemaining/1000);
				}
				if(this.blockDuration && this.closeNode){
					if(this.lifetime > this.blockDuration){
						this.closeNode.style.visibility = "hidden";
					}else{
						this.closeNode.style.display = "none";
					}
				}
				this.timer = setInterval(dojo.lang.hitch(this, "_onTick"), 100);
			}

			this.showModalDialog();
			dojo.widget.Dialog.superclass.show.call(this);
		},

		onLoad: function(){
			// when href is specified we need to reposition
			// the dialog after the data is loaded
			this.placeModalDialog();
			dojo.widget.Dialog.superclass.onLoad.call(this);
		},
		
		fillInTemplate: function(){
			// dojo.event.connect(this.domNode, "onclick", this, "killEvent");
		},

		hide: function(){
			this.hideModalDialog();
			dojo.widget.Dialog.superclass.hide.call(this);

			if(this.timer){
				clearInterval(this.timer);
			}
		},
		
		setTimerNode: function(node){
			// summary
			//	specify into which node to write the remaining # of seconds
			// TODO: make this a parameter too
			this.timerNode = node;
		},

		setCloseControl: function(node) {
			// summary
			//	specify which node is the close button for this dialog
			// TODO: make this a parameter too
			this.closeNode = node;
			dojo.event.connect(node, "onclick", this, "hide");
		},

		setShowControl: function(node) {
			// summary
			//	when specified node is clicked, show this dialog
			// TODO: make this a parameter too
			dojo.event.connect(node, "onclick", this, "show");
		},
		
		_onTick: function(){
			// summary
			//	callback every second that the timer clicks
			if(this.timer){
				this.timeRemaining -= 100;
				if(this.lifetime - this.timeRemaining >= this.blockDuration){
					dojo.event.connect(this.shared.bg, "onclick", this, "hide");
					if(this.closeNode){
						this.closeNode.style.visibility = "visible";
					}
				}
				if(!this.timeRemaining){
					clearInterval(this.timer);
					this.hide();
				}else if(this.timerNode){
					this.timerNode.innerHTML = Math.ceil(this.timeRemaining/1000);
				}
			}
		}
	}
);
