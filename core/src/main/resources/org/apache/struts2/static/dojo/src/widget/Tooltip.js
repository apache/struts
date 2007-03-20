/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.Tooltip");
dojo.require("dojo.widget.ContentPane");
dojo.require("dojo.widget.PopupContainer");
dojo.require("dojo.uri.Uri");
dojo.require("dojo.widget.*");
dojo.require("dojo.event.*");
dojo.require("dojo.html.style");
dojo.require("dojo.html.util");
dojo.require("dojo.html.iframe");

dojo.widget.defineWidget(
	"dojo.widget.Tooltip",
	[dojo.widget.ContentPane, dojo.widget.PopupContainerBase],
	{
		isContainer: true,

		// Constructor arguments
		caption: "",
		showDelay: 500,
		hideDelay: 100,
		connectId: "",

		templateCssPath: dojo.uri.dojoUri("src/widget/templates/TooltipTemplate.css"),

		fillInTemplate: function(args, frag){
			if(this.caption != ""){
				this.domNode.appendChild(document.createTextNode(this.caption));
			}
			this._connectNode = dojo.byId(this.connectId);
			dojo.widget.Tooltip.superclass.fillInTemplate.call(this, args, frag);

			this.addOnLoad(this, "_loadedContent");
			dojo.html.addClass(this.domNode, "dojoTooltip");

			//copy style from input node to output node
			var source = this.getFragNodeRef(frag);
			dojo.html.copyStyle(this.domNode, source);

			//apply the necessary css rules to the node so that it can popup
			this.applyPopupBasicStyle();
		},

		postCreate: function(args, frag){
			dojo.event.connect(this._connectNode, "onmouseover", this, "onMouseOver");
			dojo.widget.Tooltip.superclass.postCreate.call(this, args, frag);
		},

		onMouseOver: function(e){
			this._mouse = {x: e.pageX, y: e.pageY};

			// Start tracking mouse movements, so we know when to cancel timers or erase the tooltip
			if(!this._tracking){
				dojo.event.connect(document.documentElement, "onmousemove", this, "onMouseMove");
				this.tracking=true;
			}

			this._onHover(e);			
		},

		onMouseMove: function(e) {
			this._mouse = {x: e.pageX, y: e.pageY};

			if(dojo.html.overElement(this._connectNode, e) || dojo.html.overElement(this.domNode, e)){
				this._onHover(e);
			} else {
				// mouse has been moved off the element/tooltip
				// note: can't use onMouseOut to detect this because the "explode" effect causes
				// spurious onMouseOut events (due to interference from outline), w/out corresponding onMouseOver
				this._onUnHover(e);
			}
		},

		_onHover: function(e) {
			if(this._hover){ return; }
			this._hover=true;

			// If the tooltip has been scheduled to be erased, cancel that timer
			// since we are hovering over element/tooltip again
			if(this._hideTimer) {
				clearTimeout(this._hideTimer);
				delete this._hideTimer;
			}
			
			// If tooltip not showing yet then set a timer to show it shortly
			if(!this.isShowingNow && !this._showTimer){
				this._showTimer = setTimeout(dojo.lang.hitch(this, "open"), this.showDelay);
			}
		},

		_onUnHover: function(e){
			if(!this._hover){ return; }
			this._hover=false;

			if(this._showTimer){
				clearTimeout(this._showTimer);
				delete this._showTimer;
			}
			if(this.isShowingNow && !this._hideTimer){
				this._hideTimer = setTimeout(dojo.lang.hitch(this, "close"), this.hideDelay);
			}
			
			// If we aren't showing the tooltip, then we can stop tracking the mouse now;
			// otherwise must track the mouse until tooltip disappears
			if(!this.isShowingNow){
				dojo.event.disconnect(document.documentElement, "onmousemove", this, "onMouseMove");
				this._tracking=false;
			}
		},

		open: function() {
			if (this.isShowingNow) { return; }

			dojo.widget.PopupContainerBase.prototype.open.call(this, this._mouse.x, this._mouse.y, null, [this._mouse.x, this._mouse.y], "TL,TR,BL,BR", [10,15]);
		},

		close: function() {
			if (this.isShowingNow) {
				if ( this._showTimer ) {
					clearTimeout(this._showTimer);
					delete this._showTimer;
				}
				if ( this._hideTimer ) {
					clearTimeout(this._hideTimer);
					delete this._hideTimer;
				}
				dojo.event.disconnect(document.documentElement, "onmousemove", this, "onMouseMove");
				this._tracking=false;
				dojo.widget.PopupContainerBase.prototype.close.call(this);
			}
		},

		_position: function(){
			this.move(this._mouse.x, this._mouse.y, [10,15], "TL,TR,BL,BR");
		},

		_loadedContent: function(){
			if(this.isShowingNow){
				// the tooltip has changed size due to downloaded contents, so reposition it
				this._position();
			}
		},

		checkSize: function(){
			// checkSize() is called when the user has resized the browser window,
			// but that doesn't affect this widget (or this widget's children)
			// so it can be safely ignored
		},

		uninitialize: function(){
			this.close();
			dojo.event.disconnect(this._connectNode, "onmouseover", this, "onMouseOver");
		}

	}
);
