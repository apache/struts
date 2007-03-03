/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.PopupContainer");

dojo.require("dojo.html.style");
dojo.require("dojo.html.layout");
dojo.require("dojo.html.selection");
dojo.require("dojo.html.iframe");
dojo.require("dojo.event.*");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.HtmlWidget");

// summary:
//		PopupContainerBase is the mixin class which provide popup behaviors:
//		it can open in a given position x,y or around a given node.
//		In addition, it handles animation and IE bleed through workaround.
// description:
//		This class can not be used standalone: it should be mixed-in to a
//		dojo.widget.HtmlWidget. Use PopupContainer instead if you want a
//		a standalone popup widget
dojo.declare(
	"dojo.widget.PopupContainerBase",
	null,
	function(){
		this.queueOnAnimationFinish = [];
	},
{
	isContainer: true,
	templateString: '<div dojoAttachPoint="containerNode" style="display:none;position:absolute;" class="dojoPopupContainer" ></div>',

	// Boolean: whether this popup is shown
	isShowingNow: false,

	// Widget: the shown sub popup if any
	currentSubpopup: null,

	// Int: the minimal popup zIndex
	beginZIndex: 1000,

	// Widget: parent popup widget
	parentPopup: null,
	// Widget: parent Widget
	parent: null,
	// Int: level of sub popup
	popupIndex: 0,

	// dojo.html.boxSizing: which bounding box to use for open aroundNode. By default use BORDER box of the aroundNode
	aroundBox: dojo.html.boxSizing.BORDER_BOX,

	// Object: in which window, the open() is triggered
	openedForWindow: null,

	processKey: function(/*Event*/evt){
		// summary: key event handler
		return false;
	},

	applyPopupBasicStyle: function(){
		// summary: apply necessary css rules to the top domNode
		// description:
		//		this function should be called in sub class where a custom
		//		templateString/templateStringPath is used (see Tooltip widget)
		with(this.domNode.style){
			display = 'none';
			position = 'absolute';
		}
	},

	aboutToShow: function() {
		// summary: connect to this stub to modify the content of the popup
	},

	open: function(/*Integer*/x, /*Integer*/y, /*DomNode*/parent, /*Object*/explodeSrc, /*String?*/orient, /*Array?*/padding){
		// summary:
		//		Open the popup at position (x,y), relative to dojo.body()
	 	//		Or open(node, parent, explodeSrc, aroundOrient) to open
	 	//		around node
		if (this.isShowingNow){ return; }

		this.aboutToShow();

		// if I click right button and menu is opened, then it gets 2 commands: close -> open
		// so close enables animation and next "open" is put to queue to occur at new location
		if(this.animationInProgress){
			this.queueOnAnimationFinish.push(this.open, arguments);
			return;
		}

		// save this so that the focus can be returned
		this.parent = parent;

		var around = false, node, aroundOrient;
		if(typeof x == 'object'){
			node = x;
			aroundOrient = explodeSrc;
			explodeSrc = parent;
			parent = y;
			around = true;
		}

		// for unknown reasons even if the domNode is attached to the body in postCreate(),
		// it's not attached here, so have to attach it here.
		dojo.body().appendChild(this.domNode);

		// if explodeSrc isn't specified then explode from my parent widget
		explodeSrc = explodeSrc || parent["domNode"] || [];

		//keep track of parent popup to decided whether this is a top level popup
		var parentPopup = null;
		this.isTopLevel = true;
		while(parent){
			if(parent !== this && (parent.setOpenedSubpopup != undefined && parent.applyPopupBasicStyle != undefined)){
				parentPopup = parent;
				this.isTopLevel = false;
				parentPopup.setOpenedSubpopup(this);
				break;
			}
			parent = parent.parent;
		}

		this.parentPopup = parentPopup;
		this.popupIndex = parentPopup ? parentPopup.popupIndex + 1 : 1;

		if(this.isTopLevel){
			var button = dojo.html.isNode(explodeSrc) ? explodeSrc : null;
			dojo.widget.PopupManager.opened(this, button);
		}

		//Store the current selection and restore it before the action for a menu item
		//is executed. This is required as clicking on an menu item deselects current selection
		if(this.isTopLevel && !dojo.withGlobal(this.openedForWindow||dojo.global(), dojo.html.selection.isCollapsed)){
			this._bookmark = dojo.withGlobal(this.openedForWindow||dojo.global(), dojo.html.selection.getBookmark);
		}else{
			this._bookmark = null;
		}

		//convert explodeSrc from format [x, y] to
		//{left: x, top: y, width: 0, height: 0} which is the new
		//format required by dojo.html.toCoordinateObject
		if(explodeSrc instanceof Array){
			explodeSrc = {left: explodeSrc[0], top: explodeSrc[1], width: 0, height: 0};
		}

		// display temporarily, and move into position, then hide again
		with(this.domNode.style){
			display="";
			zIndex = this.beginZIndex + this.popupIndex;
		}

		if(around){
			this.move(node, padding, aroundOrient);
		}else{
			this.move(x, y, padding, orient);
		}
		this.domNode.style.display="none";

		this.explodeSrc = explodeSrc;

		// then use the user defined method to display it
		this.show();

		this.isShowingNow = true;
	},

	// TODOC: move(node, padding, aroundOrient) how to do this?
	move: function(/*Int*/x, /*Int*/y, /*Integer?*/padding, /*String?*/orient){
		// summary: calculate where to place the popup

		var around = (typeof x == "object");
		if(around){
			var aroundOrient=padding;
			var node=x;
			padding=y;
			if(!aroundOrient){ //By default, attempt to open above the aroundNode, or below
				aroundOrient = {'BL': 'TL', 'TL': 'BL'};
			}
			dojo.html.placeOnScreenAroundElement(this.domNode, node, padding, this.aroundBox, aroundOrient);
		}else{
			if(!orient){ orient = 'TL,TR,BL,BR';}
			dojo.html.placeOnScreen(this.domNode, x, y, padding, true, orient);
		}
	},

	close: function(/*Boolean?*/force){
		// summary: hide the popup
		if(force){
			this.domNode.style.display="none";
		}

		// If we are in the process of opening the menu and we are asked to close it
		if(this.animationInProgress){
			this.queueOnAnimationFinish.push(this.close, []);
			return;
		}

		this.closeSubpopup(force);
		this.hide();
		if(this.bgIframe){
			this.bgIframe.hide();
			this.bgIframe.size({left: 0, top: 0, width: 0, height: 0});
		}
		if(this.isTopLevel){
			dojo.widget.PopupManager.closed(this);
		}
		this.isShowingNow = false;
		// return focus to the widget that opened the menu
		try {
			this.parent.domNode.focus();
		} catch(e) {}

		//do not need to restore if current selection is not empty
		//(use keyboard to select a menu item)
		if(this._bookmark && dojo.withGlobal(this.openedForWindow||dojo.global(), dojo.html.selection.isCollapsed)){
			if(this.openedForWindow){
				this.openedForWindow.focus()
			}
			dojo.withGlobal(this.openedForWindow||dojo.global(), "moveToBookmark", dojo.html.selection, [this._bookmark]);
		}
		this._bookmark = null;
	},

	closeAll: function(/*Boolean?*/force){
		// summary: hide all popups including sub ones
		if (this.parentPopup){
			this.parentPopup.closeAll(force);
		}else{
			this.close(force);
		}
	},

	setOpenedSubpopup: function(/*Widget*/popup) {
		// summary: used by sub popup to set currentSubpopup in the parent popup
		this.currentSubpopup = popup;
	},

	closeSubpopup: function(/*Boolean?*/force) {
		// summary: close opened sub popup
		if(this.currentSubpopup == null){ return; }

		this.currentSubpopup.close(force);
		this.currentSubpopup = null;
	},

	onShow: function() {
		dojo.widget.PopupContainer.superclass.onShow.apply(this, arguments);
		// With some animation (wipe), after close, the size of the domnode is 0
		// and next time when shown, the open() function can not determine
		// the correct place to popup, so we store the opened size here and
		// set it after close (in function onHide())
		this.openedSize={w: this.domNode.style.width, h: this.domNode.style.height};
		// prevent IE bleed through
		if(dojo.render.html.ie){
			if(!this.bgIframe){
				this.bgIframe = new dojo.html.BackgroundIframe();
				this.bgIframe.setZIndex(this.domNode);
			}

			this.bgIframe.size(this.domNode);
			this.bgIframe.show();
		}
		this.processQueue();
	},

	processQueue: function() {
		// summary: do events from queue
		if (!this.queueOnAnimationFinish.length) return;

		var func = this.queueOnAnimationFinish.shift();
		var args = this.queueOnAnimationFinish.shift();

		func.apply(this, args);
	},

	onHide: function() {
		dojo.widget.HtmlWidget.prototype.onHide.call(this);

		//restore size of the domnode, see comment in
		//function onShow()
		if(this.openedSize){
			with(this.domNode.style){
				width=this.openedSize.w;
				height=this.openedSize.h;
			}
		}

		this.processQueue();
	}
});

// summary: dojo.widget.PopupContainer is the widget version of dojo.widget.PopupContainerBase
dojo.widget.defineWidget(
	"dojo.widget.PopupContainer",
	[dojo.widget.HtmlWidget, dojo.widget.PopupContainerBase], {});


// summary:
//		the popup manager makes sure we don't have several popups
//		open at once. the root popup in an opening sequence calls
//		opened(). when a root menu closes it calls closed(). then
//		everything works. lovely.
dojo.widget.PopupManager = new function(){
	this.currentMenu = null;
	this.currentButton = null;		// button that opened current menu (if any)
	this.currentFocusMenu = null;	// the (sub)menu which receives key events
	this.focusNode = null;
	this.registeredWindows = [];

	this.registerWin = function(/*Window*/win){
		// summary: register a window so that when clicks/scroll in it, the popup can be closed automatically
		if(!win.__PopupManagerRegistered)
		{
			dojo.event.connect(win.document, 'onmousedown', this, 'onClick');
			dojo.event.connect(win, "onscroll", this, "onClick");
			dojo.event.connect(win.document, "onkey", this, 'onKey');
			win.__PopupManagerRegistered = true;
			this.registeredWindows.push(win);
		}
	};

	/*

	*/
	this.registerAllWindows = function(/*Window*/targetWindow){
		// summary:
		//		This function register all the iframes and the top window,
		//		so that whereever the user clicks in the page, the popup
		//		menu will be closed
		//		In case you add an iframe after onload event, please call
		//		dojo.widget.PopupManager.registerWin manually

		//starting from window.top, clicking everywhere in this page
		//should close popup menus
		if(!targetWindow) { //see comment below
			targetWindow = dojo.html.getDocumentWindow(window.top && window.top.document || window.document);
		}

		this.registerWin(targetWindow);

		for (var i = 0; i < targetWindow.frames.length; i++){
			try{
				//do not remove  dojo.html.getDocumentWindow, see comment in it
				var win = dojo.html.getDocumentWindow(targetWindow.frames[i].document);
				if(win){
					this.registerAllWindows(win);
				}
			}catch(e){ /* squelch error for cross domain iframes */ }
		}
	};

	this.unRegisterWin = function(/*Window*/win){
		// summary: remove listeners on the registered window
		if(win.__PopupManagerRegistered)
		{
			dojo.event.disconnect(win.document, 'onmousedown', this, 'onClick');
			dojo.event.disconnect(win, "onscroll", this, "onClick");
			dojo.event.disconnect(win.document, "onkey", this, 'onKey');
			win.__PopupManagerRegistered = false;
		}
	};

	this.unRegisterAllWindows = function(){
		// summary: remove listeners on all the registered windows
		for(var i=0;i<this.registeredWindows.length;++i){
			this.unRegisterWin(this.registeredWindows[i]);
		}
		this.registeredWindows = [];
	};

	dojo.addOnLoad(this, "registerAllWindows");
	dojo.addOnUnload(this, "unRegisterAllWindows");

	this.closed = function(/*Widget*/menu){
		// summary: notify the manager that menu is closed
		if (this.currentMenu == menu){
			this.currentMenu = null;
			this.currentButton = null;
			this.currentFocusMenu = null;
		}
	};

	this.opened = function(/*Widget*/menu, /*DomNode*/button){
		// summary: sets the current opened popup
		if (menu == this.currentMenu){ return; }

		if (this.currentMenu){
			this.currentMenu.close();
		}

		this.currentMenu = menu;
		this.currentFocusMenu = menu;
		this.currentButton = button;
	};

	this.setFocusedMenu = function(/*Widget*/menu){
		// summary:
		// 		Set the current focused popup, This is used by popups which supports keyboard navigation
		this.currentFocusMenu = menu;
	};

	this.onKey = function(/*Event*/e){
		if (!e.key) { return; }
		if(!this.currentMenu || !this.currentMenu.isShowingNow){ return; }

		var m = this.currentFocusMenu;
		while (m){
			if(m.processKey(e)){
				e.preventDefault();
				e.stopPropagation();
				break;
			}
			m = m.parentPopup;
		}
	},

	this.onClick = function(/*Event*/e){
		if (!this.currentMenu){ return; }

		var scrolloffset = dojo.html.getScroll().offset;

		// starting from the base menu, perform a hit test
		// and exit when one succeeds

		var m = this.currentMenu;

		while (m){
			if(dojo.html.overElement(m.domNode, e) || dojo.html.isDescendantOf(e.target, m.domNode)){
				return;
			}
			m = m.currentSubpopup;
		}

		// Also, if user clicked the button that opened this menu, then
		// that button will send the menu a close() command, so this code
		// shouldn't try to close the menu.  Closing twice messes up animation.
		if (this.currentButton && dojo.html.overElement(this.currentButton, e)){
			return;
		}

		// the click didn't fall within the open menu tree
		// so close it

		this.currentMenu.close();
	};
}