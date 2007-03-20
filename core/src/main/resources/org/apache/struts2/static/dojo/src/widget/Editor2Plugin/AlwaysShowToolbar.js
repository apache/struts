/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.Editor2Plugin.AlwaysShowToolbar");

//dojo.widget.Editor2Manager.registerPerInstancePlugin("dojo.widget.Editor2Plugin.AlwaysShowToolbar");

dojo.event.topic.subscribe("dojo.widget.Editor2::onLoad", function(editor){
	if(editor.toolbarAlwaysVisible){
		var p = new dojo.widget.Editor2Plugin.AlwaysShowToolbar(editor);
	}
});
dojo.declare("dojo.widget.Editor2Plugin.AlwaysShowToolbar", null,
	function(editor){
		this.editor = editor;
		this.editor.registerLoadedPlugin(this);
		this.setup();
	},
	{
	_scrollSetUp: false,
	_fixEnabled: false,
	_scrollThreshold: false,
	_handleScroll: true,

	setup: function(){
		var tdn = this.editor.toolbarWidget;
		if(!tdn.tbBgIframe){
			tdn.tbBgIframe = new dojo.html.BackgroundIframe(tdn.domNode);
			tdn.tbBgIframe.onResized();
		}
		this.scrollInterval = setInterval(dojo.lang.hitch(this, "globalOnScrollHandler"), 100);

		dojo.event.connect("before", this.editor.toolbarWidget, "destroy", this, "destroy");
	},

	globalOnScrollHandler: function(){
		var isIE = dojo.render.html.ie;
		if(!this._handleScroll){ return; }
		var dh = dojo.html;
		var tdn = this.editor.toolbarWidget.domNode;
		var db = dojo.body();

		if(!this._scrollSetUp){
			this._scrollSetUp = true;
			var editorWidth =  dh.getMarginBox(this.editor.domNode).width; 
			this._scrollThreshold = dh.abs(tdn, true).y;
			// dojo.debug("threshold:", this._scrollThreshold);
			if((isIE)&&(db)&&(dh.getStyle(db, "background-image")=="none")){
				with(db.style){
					backgroundImage = "url(" + dojo.uri.dojoUri("src/widget/templates/images/blank.gif") + ")";
					backgroundAttachment = "fixed";
				}
			}
		}

		var scrollPos = (window["pageYOffset"]) ? window["pageYOffset"] : (document["documentElement"]||document["body"]).scrollTop;

		// FIXME: need to have top and bottom thresholds so toolbar doesn't keep scrolling past the bottom
		if(scrollPos > this._scrollThreshold){
			// dojo.debug(scrollPos);
			if(!this._fixEnabled){
				var tdnbox = dojo.html.getMarginBox(tdn);
				this.editor.editorObject.style.marginTop = tdnbox.height+"px";

				if(isIE){
					// FIXME: should we just use setBehvior() here instead?
					tdn.style.left = dojo.html.abs(tdn, dojo.html.boxSizing.MARGIN_BOX).x;
					dojo.body().appendChild(tdn);

					dojo.html.addClass(tdn, "IEFixedToolbar");
				}else{
					with(tdn.style){
						position = "fixed";
						top = "0px";
					}
				}

				tdn.style.width = tdnbox.width + "px";
				tdn.style.zIndex = 1000;
				this._fixEnabled = true;
			}
			// if we're showing the floating toolbar, make sure that if
			// we've scrolled past the bottom of the editor that we hide
			// the toolbar for this instance of the editor.

			// TODO: when we get multiple editor toolbar support working
			// correctly, ensure that we check this against the scroll
			// position of the bottom-most editor instance.
			if(!dojo.render.html.safari){
				// safari reports a bunch of things incorrectly here
				var eHeight = (this.height) ? parseInt(this.editor.height) : this.editor._lastHeight;
				if(scrollPos > (this._scrollThreshold+eHeight)){
					tdn.style.display = "none";
				}else{
					tdn.style.display = "";
				}
			}
		}else if(this._fixEnabled){
			(this.editor.object || this.editor.iframe).style.marginTop = null;
			with(tdn.style){
				position = "";
				top = "";
				zIndex = "";
				display = "";
			}
			if(isIE){
				tdn.style.left = "";
				dojo.html.removeClass(tdn, "IEFixedToolbar");
				dojo.html.insertBefore(tdn, this.editor.object||this.editor.iframe);
			}
			tdn.style.width = "";
			this._fixEnabled = false;
		}
	},

	destroy: function(){
		this._handleScroll = false;
		clearInterval(this.scrollInterval);
		this.editor.unregisterLoadedPlugin(this);

		if(dojo.render.html.ie){
			dojo.html.removeClass(this.editor.toolbarWidget.domNode, "IEFixedToolbar");
		}
	}
});