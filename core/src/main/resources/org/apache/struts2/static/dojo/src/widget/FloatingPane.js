/*
	Copyright (c) 2004-2005, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.FloatingPane");
dojo.provide("dojo.widget.html.FloatingPane");

//
// this widget provides a window-like floating pane
//

dojo.require("dojo.widget.*");
dojo.require("dojo.widget.Manager");
dojo.require("dojo.html");
dojo.require("dojo.style");
dojo.require("dojo.dom");
dojo.require("dojo.widget.ContentPane");
dojo.require("dojo.widget.LayoutPane");
dojo.require("dojo.dnd.HtmlDragMove");
dojo.require("dojo.dnd.HtmlDragMoveSource");
dojo.require("dojo.dnd.HtmlDragMoveObject");

dojo.widget.html.FloatingPane = function(){
	dojo.widget.html.LayoutPane.call(this);
}

dojo.inherits(dojo.widget.html.FloatingPane, dojo.widget.html.LayoutPane);

dojo.lang.extend(dojo.widget.html.FloatingPane, {
	widgetType: "FloatingPane",

	// Constructor arguments
	title: '',
	iconSrc: '',
	hasShadow: false,
	constrainToContainer: false,
	taskBarId: "",
	resizable: true,	// note: if specified, user must include ResizeHandle
	overflow: "",

	resizable: false,
	titleBarDisplay: "fancy",
	titleHeight: 22,	// workaround to CSS loading race condition bug

	href: "",
	extractContent: true,
	parseContent: true,
	cacheContent: true,

	// FloatingPane supports 3 modes for the client area (the part below the title bar)
	//  default - client area  is a ContentPane, that can hold
	//      either inlined data and/or data downloaded from a URL
	//  layout - the client area is a layout pane
	//  none - the user specifies a single widget which becomes the content pane
	contentWrapper: "default",

	containerNode: null,
	domNode: null,
	clientPane: null,
	dragBar: null,

	windowState: "normal",
	displayCloseAction: false,

	maxTaskBarConnectAttempts: 5,
	taskBarConnectAttempts: 0,

	minimizeIcon: dojo.uri.dojoUri("src/widget/templates/images/floatingPaneMinimize.gif"),
	maximizeIcon: dojo.uri.dojoUri("src/widget/templates/images/floatingPaneMaximize.gif"),
	restoreIcon: dojo.uri.dojoUri("src/widget/templates/images/floatingPaneRestore.gif"),
	closeIcon: dojo.uri.dojoUri("src/widget/templates/images/floatingPaneClose.gif"),
	titleBarBackground: dojo.uri.dojoUri("src/widget/templates/images/titlebar-bg.jpg"),

	shadowPng: dojo.uri.dojoUri("src/widget/templates/images/shadow"),
	shadowThickness: 8,
	shadowOffset: 15,

	templateString: '<div></div>',
	templateCssPath: dojo.uri.dojoUri("src/widget/templates/HtmlFloatingPane.css"),

	initialized: false,

	addChild: function(child, overrideContainerNode, pos, ref, insertIndex) {
		this.clientPane.addChild(child, overrideContainerNode, pos, ref, insertIndex);
	},

	// make a widget container to hold all the contents of the floating pane (other than the
	// title and the resize bar at the bottom)
	_makeClientPane: function(clientDiv){
		var args = {
			layoutAlign: "client", 
			id:this.widgetId+"_client",
			href: this.href, 
			cacheContent: this.cacheContent, 
			extractContent: this.extractContent,
			parseContent: this.parseContent
		};
		var pane = this._createPane(this.contentWrapper=="layout"?"LayoutPane":"ContentPane", clientDiv, args);
		return pane;
	},

	fillInTemplate: function(args, frag){
		var source = this.getFragNodeRef(frag);

		// Copy style info and id from input node to output node
		this.domNode.style.cssText = source.style.cssText;
		dojo.html.addClass(this.domNode, dojo.html.getClass(source));
		dojo.html.addClass(this.domNode, "dojoFloatingPane");
		this.domNode.style.position="absolute";
		this.domNode.id = source.id;
		if(dojo.render.html.safari){
			dojo.html.body().appendChild(this.domNode);
		}

		// make client pane wrapper to hold the contents of this floating pane
		if(this.contentWrapper!="none"){
			var clientDiv = document.createElement('div');
			dojo.dom.moveChildren(source, clientDiv, 0);
			this.clientPane = this._makeClientPane(clientDiv);
		}
		
		if (this.titleBarDisplay != "none") {
			// this is our chrome
			var chromeDiv = document.createElement('div');
			dojo.html.addClass(chromeDiv, 'dojoFloatingPaneDragbar');
			chromeDiv.style.height=this.titleHeight+"px";	// workaround CSS loading race condition bug
			
			this.dragBar = this._createPane("LayoutPane", chromeDiv, {layoutAlign: 'top', id:this.widgetId+"_chrome"});
			dojo.html.disableSelection(this.dragBar.domNode);

			if( this.titleBarDisplay == "fancy"){
				// image background to get gradient
				var img = document.createElement('img');
				img.src = this.titleBarBackground;
				dojo.html.addClass(img, 'dojoFloatingPaneDragbarBackground');
				var backgroundPane = dojo.widget.createWidget("ContentPane", {layoutAlign:"flood", id:this.widgetId+"_titleBackground"}, img);
				this.dragBar.addChild(backgroundPane);
			}

			//Title Bar
			var titleBar = document.createElement('div');
			dojo.html.addClass(titleBar, "dojoFloatingPaneTitleBar");
			dojo.html.disableSelection(titleBar);

			//TitleBarActions
			var titleBarActions = document.createElement('div');
			dojo.html.addClass(titleBarActions, "dojoFloatingPaneActions");

			//Title Icon
			if(this.iconSrc!=""){
				var titleIcon = document.createElement('img');
				dojo.html.addClass(titleIcon,"dojoTitleBarIcon");
				titleIcon.src = this.iconSrc;						
				titleBar.appendChild(titleIcon);
			}

			//Title text  
			var titleText = document.createTextNode(this.title)
			titleBar.appendChild(titleText);

			if (this.resizable) {

				//FloatingPane Action Minimize
				this.minimizeAction = document.createElement("img");
				dojo.html.addClass(this.minimizeAction, "dojoFloatingPaneActionItem");
				this.minimizeAction.src = this.minimizeIcon;	
				titleBarActions.appendChild(this.minimizeAction);
				dojo.event.connect(this.minimizeAction, 'onclick', this, 'minimizeWindow');

				//FloatingPane Action Restore
				this.restoreAction = document.createElement("img");
				dojo.html.addClass(this.restoreAction, "dojoFloatingPaneActionItem");
				this.restoreAction.src = this.restoreIcon;	
				titleBarActions.appendChild(this.restoreAction);
				dojo.event.connect(this.restoreAction, 'onclick', this, 'restoreWindow');

				if (this.windowState != "normal") {
					this.restoreAction.style.display="inline";
				} else {
					this.restoreAction.style.display="none";
				}

				//FloatingPane Action Maximize
				this.maximizeAction = document.createElement("img");
				dojo.html.addClass(this.maximizeAction, "dojoFloatingPaneActionItem");
				this.maximizeAction.src = this.maximizeIcon;	
				titleBarActions.appendChild(this.maximizeAction);
				dojo.event.connect(this.maximizeAction, 'onclick', this, 'maximizeWindow');

				if (this.windowState != "maximized") {
					this.maximizeAction.style.display="inline";	
				} else {
					this.maximizeAction.style.display="none";	
				}	

			}

			if (this.displayCloseAction) {
				//FloatingPane Action Close
				var closeAction= document.createElement("img");
				dojo.html.addClass(closeAction, "dojoFloatingPaneActionItem");
				closeAction.src = this.closeIcon;	
				titleBarActions.appendChild(closeAction);
				dojo.event.connect(closeAction, 'onclick', this, 'closeWindow');
			}


			chromeDiv.appendChild(titleBar);
			chromeDiv.appendChild(titleBarActions);
		}

		if ( this.resizable ) {
			// add the resize handle
			var resizeDiv = document.createElement('div');
			dojo.html.addClass(resizeDiv, "dojoFloatingPaneResizebar");
			dojo.html.disableSelection(resizeDiv);
			var rh = dojo.widget.createWidget("ResizeHandle", {targetElmId: this.widgetId, id:this.widgetId+"_resize"});
			this.resizePane = this._createPane("ContentPane", resizeDiv, {layoutAlign: "bottom"});
			this.resizePane.addChild(rh);
		}

		// add a drop shadow
		this._makeShadow();

		dojo.event.connect(this.domNode, 'onmousedown', this, 'onMouseDown');

		// Prevent IE bleed-through problem
		this.bgIframe = new dojo.html.BackgroundIframe();
		if( this.bgIframe.iframe ){
			this.domNode.appendChild(this.bgIframe.iframe);
		}
		if ( this.isVisible() ) {
			this.bgIframe.show();
		};

		if( this.taskBarId ){
			this.taskBarSetup();
		}

		if (dojo.hostenv.post_load_) {
			this.setInitialWindowState();
		} else {
			dojo.addOnLoad(this, "setInitialWindowState");
		}
		if(dojo.render.html.safari){
			dojo.html.body().removeChild(this.domNode);
		}

		dojo.widget.html.FloatingPane.superclass.postCreate.call(this, args, frag);
	},

	postCreate: function(args, frag){
		// Make the client pane.  It will either be the widget specified by the user,
		// or a wrapper widget
		if(this.contentWrapper=="none"){
			// the user has specified a single widget which will become our content
			this.clientPane = this.children[0];
			this.domNode.appendChild(this.clientPane.domNode);
		}else{
			// move our 'children' into the client pane
			// we already moved the domnodes, but now we need to move the 'children'
			var kids = this.children.concat();
			this.children = [];
	
			for(var i=0; i<kids.length; i++){
				if (kids[i].ownerPane == this){
					this.children.push(kids[i]);
				}else{
					if(this.contentWrapper=="layout"){
						this.clientPane.addChild(kids[i]);
					}else{
						this.clientPane.children.push(kids[i]);
					}
				}
			}
		}
		dojo.html.addClass(this.clientPane.domNode, 'dojoFloatingPaneClient');
		this.clientPane.layoutAlign="client";
		this.clientPane.ownerPane=this;
		if (this.overflow != "") {
			this.clientPane.domNode.style.overflow=this.overflow;
		}

		if (this.titleBarDisplay != "none") {
			var drag = new dojo.dnd.HtmlDragMoveSource(this.domNode);
	
			if (this.constrainToContainer) {
				drag.constrainTo();
			}
	
			drag.setDragHandle(this.dragBar.domNode);
		}

		this.initialized=true;
	},

	_makeShadow: function(){
		if ( this.hasShadow ) {
			// make all the pieces of the shadow, and position/size them as much
			// as possible (but a lot of the coordinates are set in sizeShadow
			this.shadow={};
			var x1 = -1 * this.shadowThickness;
			var y0 = this.shadowOffset;
			var y1 = this.shadowOffset + this.shadowThickness;
			this._makeShadowPiece("ul", "top", y0, "left", x1);
			this._makeShadowPiece("l", "top", y1, "left", x1, "scale");
			this._makeShadowPiece("ur", "top", y0, "left", 0);
			this._makeShadowPiece("r", "top", y1, "left", 0, "scale");
			this._makeShadowPiece("bl", "top", 0, "left", x1);
			this._makeShadowPiece("b", "top", 0, "left", 0, "crop");
			this._makeShadowPiece("br", "top", 0, "left", 0);
		}
	},

	_makeShadowPiece: function(name, vertAttach, vertCoord, horzAttach, horzCoord, sizing){
		var img;
		var url = this.shadowPng + name.toUpperCase() + ".png";
		if(dojo.render.html.ie){
			img=document.createElement("div");
			img.style.filter="progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+url+"'"+
			(sizing?", sizingMethod='"+sizing+"'":"") + ")";
		}else{
			img=document.createElement("img");
			img.src=url;
		}
		img.style.position="absolute";
		img.style[vertAttach]=vertCoord+"px";
		img.style[horzAttach]=horzCoord+"px";
		img.style.width=this.shadowThickness+"px";
		img.style.height=this.shadowThickness+"px";
		this.shadow[name]=img;
		this.domNode.appendChild(img);
	},

	_sizeShadow: function(width, height){
		if ( this.shadow ) {
			var sideHeight = height - (this.shadowOffset+this.shadowThickness+1);
			this.shadow.l.style.height = sideHeight+"px";
			this.shadow.r.style.height = sideHeight+"px";
			this.shadow.b.style.width = (width-1)+"px";
			this.shadow.bl.style.top = (height-1)+"px";
			this.shadow.b.style.top = (height-1)+"px";
			this.shadow.br.style.top = (height-1)+"px";
			this.shadow.ur.style.left = (width-1)+"px";
			this.shadow.r.style.left = (width-1)+"px";
			this.shadow.br.style.left = (width-1)+"px";
		}
	},

	maximizeWindow: function(evt) {
		this.previousWidth= this.domNode.style.width;
		this.previousHeight= this.domNode.style.height;
		this.previousLeft = this.domNode.style.left;
		this.previousTop = this.domNode.style.top;

		this.domNode.style.left =
			dojo.style.getPixelValue(this.domNode.parentNode, "padding-left", true) + "px";
		this.domNode.style.top =
			dojo.style.getPixelValue(this.domNode.parentNode, "padding-top", true) + "px";

		if ((this.domNode.parentNode.nodeName.toLowerCase() == 'body')) {
			dojo.style.setOuterWidth(this.domNode, dojo.html.getViewportWidth()-dojo.style.getPaddingWidth(dojo.html.body()));
			dojo.style.setOuterHeight(this.domNode, dojo.html.getViewportHeight()-dojo.style.getPaddingHeight(dojo.html.body()));
		} else {
			dojo.style.setOuterWidth(this.domNode, dojo.style.getContentWidth(this.domNode.parentNode));
			dojo.style.setOuterHeight(this.domNode, dojo.style.getContentHeight(this.domNode.parentNode));
		}	
		this.maximizeAction.style.display="none";	
		this.restoreAction.style.display="inline";	
		this.windowState="maximized";
		this.onResized();
	},

	minimizeWindow: function(evt) {
		this.hide();
		if (this.resizable) {
			this.maximizeAction.style.display="inline";	
			this.restoreAction.style.display="inline";	
		}

		this.windowState = "minimized";
	},

	restoreWindow: function(evt) {
		if (this.previousWidth && this.previousHeight && this.previousLeft && this.previousTop) {
			this.domNode.style.width = this.previousWidth;
			this.domNode.style.height = this.previousHeight;
			this.domNode.style.left = this.previousLeft;
			this.domNode.style.top = this.previousTop;
			dojo.widget.html.FloatingPane.superclass.onResized.call(this);
		}

		if (this.widgetState != "maximized") {
			this.show();
		}

		if (this.resizable) {
			this.maximizeAction.style.display="inline";	
			this.restoreAction.style.display="none";	
		}

		this.bringToTop();
		this.windowState="normal";
	},

	closeWindow: function(evt) {
		this.destroy();
	},

	onMouseDown: function(evt) {
		this.bringToTop();
	},

	bringToTop: function() {
		var floatingPaneStartingZ = 100;
		var floatingPanes= dojo.widget.manager.getWidgetsByType("FloatingPane");
		var windows = []
		var y=0;
		for (var x=0; x<floatingPanes.length; x++) {
			if (this.widgetId != floatingPanes[x].widgetId) {
					windows.push(floatingPanes[x]);
			}
		}

		windows.sort(function(a,b) {
			return a.domNode.style.zIndex - b.domNode.style.zIndex;
		});
		
		windows.push(this);

		for (x=0; x<windows.length;x++) {
			windows[x].domNode.style.zIndex = floatingPaneStartingZ + x;
		}
	},

	setInitialWindowState: function() {
		if (this.windowState == "maximized") {
			this.maximizeWindow();
			this.show();
			this.bringToTop();
			return;
		}

		if (this.windowState=="normal") {
			dojo.lang.setTimeout(this, this.onResized, 50);
			this.show();
			this.bringToTop();
			return;
		}

		if (this.windowState=="minimized") {
			this.hide();
			return;
		}

		this.windowState="minimized";
	},

	// add icon to task bar, connected to me
	taskBarSetup: function() {
		var taskbar = dojo.widget.getWidgetById(this.taskBarId);
		if (!taskbar){
			if (this.taskBarConnectAttempts <  this.maxTaskBarConnectAttempts) {
				dojo.lang.setTimeout(this, this.taskBarSetup, 50);
				this.taskBarConnectAttempts++;
			} else {
				dojo.debug("Unable to connect to the taskBar");
			}
			return;
		}
		taskbar.addChild(this);
	},

	onResized: function(){
		if( !this.isVisible() ){ return; }
		var newHeight = dojo.style.getInnerHeight(this.domNode);
		var newWidth = dojo.style.getInnerWidth(this.domNode);
	
		//if ( newWidth != this.width || newHeight != this.height ) {
			this.width = newWidth;
			this.height = newHeight;
			this._sizeShadow(newWidth, newHeight);
			dojo.widget.html.FloatingPane.superclass.onResized.call(this);
		//}

		// bgIframe is a child of this.domNode, so position should be relative to [0,0]
		if(this.bgIframe){
			this.bgIframe.size([0, 0, newWidth, newHeight]);
		}
	},

	hide: function(){
		dojo.widget.html.FloatingPane.superclass.hide.call(this);
		if(this.bgIframe){
			this.bgIframe.hide();
		}
	},

	show: function(){
		dojo.widget.html.FloatingPane.superclass.show.call(this);
		if(this.bgIframe){
			this.bgIframe.show();
		}
	},

	_createPane: function(type, node, args){
		var pane = dojo.widget.createWidget(type, args, node);
		dojo.widget.html.FloatingPane.superclass.addChild.call(this,pane);
		pane.ownerPane=this;
		return pane;
	},
	
	setUrl: function(url){
		this.clientPane.setUrl(url);
	},
	
	setContent: function(str){
		this.clientPane.setContent(str);
	}
});

dojo.widget.tags.addParseTreeHandler("dojo:FloatingPane");
