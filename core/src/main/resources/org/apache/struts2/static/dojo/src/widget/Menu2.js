/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.Menu2");

dojo.require("dojo.widget.PopupContainer");

// summary
//	provides a menu that can be used as a context menu (typically shown by right-click),
//	or as the drop down on a DropDownButton, ComboButton, etc.
dojo.widget.defineWidget(
	"dojo.widget.PopupMenu2",
	dojo.widget.PopupContainer,
	function(){
		this.targetNodeIds = []; // fill this with nodeIds upon widget creation and it becomes context menu for those nodes
	
		this.eventNames =  {
			open: ""
		};
	},
{
	snarfChildDomOutput: true,

	// String
	//	if "default" event names are based on widget id, otherwise user must define
	//	TODO: write real documentation about the events
	eventNaming: "default",

	templateString: '<table class="dojoPopupMenu2" border=0 cellspacing=0 cellpadding=0 style="display: none;"><tbody dojoAttachPoint="containerNode"></tbody></table>',
	templateCssPath: dojo.uri.dojoUri("src/widget/templates/Menu2.css"),
	templateCssString: "",

	// Integer
	//	number of milliseconds before hovering (without clicking) causes the submenu to automatically open
	submenuDelay: 500,
	
	// Integer
	//	a submenu usually appears to the right, but slightly overlapping, it's parent menu;
	//	this controls the number of pixels the two menus overlap.
	submenuOverlap: 5,
	
	// Boolean
	//	if true, right clicking anywhere on the window will cause this context menu to open;
	//	if false, must specify targetNodeIds
	contextMenuForWindow: false,

	// Array
	//	Array of dom node ids of nodes to attach to
	targetNodeIds: [],

	initialize: function(args, frag) {
		if (this.eventNaming == "default") {
			for (var eventName in this.eventNames) {
				this.eventNames[eventName] = this.widgetId+"/"+eventName;
			}
		}
	},

	postCreate: function(){
		if (this.contextMenuForWindow){
			var doc = dojo.body();
			this.bindDomNode(doc);
		} else if ( this.targetNodeIds.length > 0 ){
			dojo.lang.forEach(this.targetNodeIds, this.bindDomNode, this);
		}

		this._subscribeSubitemsOnOpen();
	},

	_subscribeSubitemsOnOpen: function() {
		var subItems = this.getChildrenOfType(dojo.widget.MenuItem2);

		for(var i=0; i<subItems.length; i++) {
			dojo.event.topic.subscribe(this.eventNames.open, subItems[i], "menuOpen")
		}
	},

	getTopOpenEvent: function() {
		// summary: get event that initially caused current chain of menus to open
		var menu = this;
		while (menu.parentPopup){ menu = menu.parentPopup; }
		return menu.openEvent;	// Event
	},

	bindDomNode: function(/*String|DomNode*/ node){
		// summary: attach menu to given node
		node = dojo.byId(node);

		var win = dojo.html.getElementWindow(node);
		if(dojo.html.isTag(node,'iframe') == 'iframe'){
			win = dojo.html.iframeContentWindow(node);
			node = dojo.withGlobal(win, dojo.body);
		}
		// fixes node so that it supports oncontextmenu if not natively supported, Konqueror, Opera more?
		dojo.widget.Menu2.OperaAndKonqFixer.fixNode(node);

		dojo.event.kwConnect({
			srcObj:     node,
			srcFunc:    "oncontextmenu",
			targetObj:  this,
			targetFunc: "onOpen",
			once:       true
		});

		//normal connect does not work if document.designMode is on in FF, use addListener instead
		if(dojo.render.html.moz && win.document.designMode.toLowerCase() == 'on'){
			dojo.event.browser.addListener(node, "contextmenu", dojo.lang.hitch(this, "onOpen"));
		}
		dojo.widget.PopupManager.registerWin(win);
	},

	unBindDomNode: function(/*String|DomNode*/ nodeName){
		// summary: detach menu from given node
		var node = dojo.byId(nodeName);
		dojo.event.kwDisconnect({
			srcObj:     node,
			srcFunc:    "oncontextmenu",
			targetObj:  this,
			targetFunc: "onOpen",
			once:       true
		});

		// cleans a fixed node, konqueror and opera
		dojo.widget.Menu2.OperaAndKonqFixer.cleanNode(node);
	},

	_moveToNext: function(/*Event*/ evt){
		this._highlightOption(1);
		return true; //do not pass to parent menu
	},

	_moveToPrevious: function(/*Event*/ evt){
		this._highlightOption(-1);
		return true; //do not pass to parent menu
	},

	_moveToParentMenu: function(/*Event*/ evt){
		if(this._highlighted_option && this.parentPopup){
			//only process event in the focused menu
			//and its immediate parentPopup to support
			//MenuBar2
			if(evt._menu2UpKeyProcessed){
				return true; //do not pass to parent menu
			}else{
				this._highlighted_option.onUnhover();
				this.closeSubpopup();
				evt._menu2UpKeyProcessed = true;
			}
		}
		return false;
	},

	_moveToChildMenu: function(/*Event*/ evt){
		if(this._highlighted_option && this._highlighted_option.submenuId){
			this._highlighted_option._onClick(true);
			return true; //do not pass to parent menu
		}
		return false;
	},

	_selectCurrentItem: function(/*Event*/ evt){
		if(this._highlighted_option){
			this._highlighted_option._onClick();
			return true;
		}
		return false;
	},

	processKey: function(/*Event*/ evt){
		// summary
		//	callback to process key strokes
		//	return true to stop the event being processed by the
		//	parent popupmenu

		if(evt.ctrlKey || evt.altKey || !evt.key){ return false; }

		var rval = false;
		switch(evt.key){
 			case evt.KEY_DOWN_ARROW:
				rval = this._moveToNext(evt);
				break;
			case evt.KEY_UP_ARROW:
				rval = this._moveToPrevious(evt);
				break;
			case evt.KEY_RIGHT_ARROW:
				rval = this._moveToChildMenu(evt);
				break;
			case evt.KEY_LEFT_ARROW:
				rval = this._moveToParentMenu(evt);
				break;
			case " ": //fall through
			case evt.KEY_ENTER: 
				if(rval = this._selectCurrentItem(evt)){
					break;
				}
				//fall through
			case evt.KEY_ESCAPE:
				dojo.widget.PopupManager.currentMenu.close();
				rval = true;
				break;
		}

		return rval;
	},

	_findValidItem: function(dir, curItem){
		if(curItem){
			curItem = dir>0 ? curItem.getNextSibling() : curItem.getPreviousSibling();
		}

		for(var i=0; i < this.children.length; ++i){
			if(!curItem){
				curItem = dir>0 ? this.children[0] : this.children[this.children.length-1];
			}
			//find next/previous visible menu item, not including separators
			if(curItem.onHover && curItem.isShowing()){
				return curItem;
			}
			curItem = dir>0 ? curItem.getNextSibling() : curItem.getPreviousSibling();
		}
	},
	
	_highlightOption: function(dir){
		var item;
		// || !this._highlighted_option.parentNode
		if((!this._highlighted_option)){
			item = this._findValidItem(dir);
		}else{
			item = this._findValidItem(dir, this._highlighted_option);
		}
		if(item){
			if(this._highlighted_option) {
				this._highlighted_option.onUnhover();
			}
			item.onHover();
			dojo.html.scrollIntoView(item.domNode);
			// navigate into the item table and select the first caption tag
			try {
				var node = dojo.html.getElementsByClass("dojoMenuItem2Label", item.domNode)[0];
				node.focus();
			} catch(e) { }
		}
	},

	onItemClick: function(/*Widget*/ item) {
		// summary: user defined function to handle clicks on an item
	},

	close: function(/*Boolean*/ force){
		// summary: close the menu
		if(this.animationInProgress){
			dojo.widget.PopupMenu2.superclass.close.apply(this, arguments);
			return;
		}

		if(this._highlighted_option){
			this._highlighted_option.onUnhover();
		}

		dojo.widget.PopupMenu2.superclass.close.apply(this, arguments);
	},

	closeSubpopup: function(force){
		// summary: close the currently displayed submenu
		if (this.currentSubpopup == null){ return; }

		this.currentSubpopup.close(force);
		this.currentSubpopup = null;

		this.currentSubmenuTrigger.is_open = false;
		this.currentSubmenuTrigger._closedSubmenu(force);
		this.currentSubmenuTrigger = null;
	},

	_openSubmenu: function(submenu, from_item){
		// summary: open the menu to the right of the current menu item
		var fromPos = dojo.html.getAbsolutePosition(from_item.domNode, true);
		var our_w = dojo.html.getMarginBox(this.domNode).width;
		var x = fromPos.x + our_w - this.submenuOverlap;
		var y = fromPos.y;

		//the following is set in open, so we do not need it
		//this.currentSubpopup = submenu;
		submenu.open(x, y, this, from_item.domNode);

		this.currentSubmenuTrigger = from_item;
		this.currentSubmenuTrigger.is_open = true;
	},

	onOpen: function(/*Event*/ e){
		// summary: callback when menu is opened
		this.openEvent = e;
		if(e["target"]){
			this.openedForWindow = dojo.html.getElementWindow(e.target);
		}else{
			this.openedForWindow = null;
		}
		var x = e.pageX, y = e.pageY;

		var win = dojo.html.getElementWindow(e.target);
		var iframe = win._frameElement || win.frameElement;
		if(iframe){
			var cood = dojo.html.abs(iframe, true);
			x += cood.x - dojo.withGlobal(win, dojo.html.getScroll).left;
			y += cood.y - dojo.withGlobal(win, dojo.html.getScroll).top;
		}
		this.open(x, y, null, [x, y]);

		e.preventDefault();
		e.stopPropagation();
	}
});

// summary
//	A line item in a Menu2
dojo.widget.defineWidget(
	"dojo.widget.MenuItem2",
	dojo.widget.HtmlWidget,
	function(){
		this.eventNames = {
			engage: ""
		};
	},
{
	// Make 4 columns
	//   icon, label, accelerator-key, and right-arrow indicating sub-menu
	templateString:
		 '<tr class="dojoMenuItem2" dojoAttachEvent="onMouseOver: onHover; onMouseOut: onUnhover; onClick: _onClick; onKey:onKey;">'
		+'<td><div class="${this.iconClass}" style="${this.iconStyle}"></div></td>'
		+'<td tabIndex="-1" class="dojoMenuItem2Label">${this.caption}</td>'
		+'<td class="dojoMenuItem2Accel">${this.accelKey}</td>'
		+'<td><div class="dojoMenuItem2Submenu" style="display:${this.arrowDisplay};"></div></td>'
		+'</tr>',

	//
	// internal settings
	//

	is_hovering: false,
	hover_timer: null,
	is_open: false,
	topPosition: 0,

	//
	// options
	//

	// String
	//	text of the menu item
	caption: 'Untitled',
	
	// String
	//	accelerator key (not supported yet!)
	accelKey: '',
	
	// String
	//	path to icon to display to the left of the menu text
	iconSrc: '',
	
	// String
	//	CSS class name to use for menu item (if CSS class specifies a background image then iconSrc is not necessary)
	iconClass: 'dojoMenuItem2Icon',
	
	// String
	//	widget ID of Menu2 widget to open when this menu item is clicked
	submenuId: '',
	
	// Boolean
	//	if true, this menu item cannot be selected
	disabled: false,
	
	// String
	//	event names for announcing when menu item is clicked.
	//	if "default", then use the default name, based on the widget ID
	eventNaming: "default",
	
	// String
	//	CSS class for menu item when it's hovered over
	highlightClass: 'dojoMenuItem2Hover',

	postMixInProperties: function(){
		this.iconStyle="";
		if (this.iconSrc){
			if ((this.iconSrc.toLowerCase().substring(this.iconSrc.length-4) == ".png") && (dojo.render.html.ie55 || dojo.render.html.ie60)){
				this.iconStyle="filter: progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+this.iconSrc+"', sizingMethod='image')";
			}else{
				this.iconStyle="background-image: url("+this.iconSrc+")";
			}
		}
		this.arrowDisplay = this.submenuId ? 'block' : 'none';
		dojo.widget.MenuItem2.superclass.postMixInProperties.apply(this, arguments);
	},

	fillInTemplate: function(){
		dojo.html.disableSelection(this.domNode);

		if (this.disabled){
			this.setDisabled(true);
		}

		if (this.eventNaming == "default") {
			for (var eventName in this.eventNames) {
				this.eventNames[eventName] = this.widgetId+"/"+eventName;
			}
		}
	},

	onHover: function(){
		// summary: callback when mouse is moved onto menu item

		//this is to prevent some annoying behavior when both mouse and keyboard are used
		this.onUnhover();

		if (this.is_hovering){ return; }
		if (this.is_open){ return; }

		if(this.parent._highlighted_option){
			this.parent._highlighted_option.onUnhover();
		}
		this.parent.closeSubpopup();
		this.parent._highlighted_option = this;
		dojo.widget.PopupManager.setFocusedMenu(this.parent);

		this._highlightItem();

		if (this.is_hovering){ this._stopSubmenuTimer(); }
		this.is_hovering = true;
		this._startSubmenuTimer();
	},

	onUnhover: function(){
		// summary: callback when mouse is moved off of menu item
		if(!this.is_open){ this._unhighlightItem(); }

		this.is_hovering = false;

		this.parent._highlighted_option = null;

		if(this.parent.parentPopup){
			dojo.widget.PopupManager.setFocusedMenu(this.parent.parentPopup);
		}

		this._stopSubmenuTimer();
	},

	_onClick: function(focus){
		// summary: internal function for clicks
		var displayingSubMenu = false;
		if (this.disabled){ return false; }

		if (this.submenuId){
			if (!this.is_open){
				this._stopSubmenuTimer();
				this._openSubmenu();
			}
			displayingSubMenu = true;
		}else{
			// for some browsers the onMouseOut doesn't get called (?), so call it manually
			this.onUnhover(); //only onUnhover when no submenu is available
			this.parent.closeAll(true);
		}

		// user defined handler for click
		this.onClick();

		dojo.event.topic.publish(this.eventNames.engage, this);

		if(displayingSubMenu && focus){
			dojo.widget.getWidgetById(this.submenuId)._highlightOption(1);
		}
		return;
	},

	onClick: function() {
		// summary
		//	User defined function to handle clicks
		//	this default function call the parent
		//	menu's onItemClick
		this.parent.onItemClick(this);
	},

	_highlightItem: function(){
		dojo.html.addClass(this.domNode, this.highlightClass);
	},

	_unhighlightItem: function(){
		dojo.html.removeClass(this.domNode, this.highlightClass);
	},

	_startSubmenuTimer: function(){
		this._stopSubmenuTimer();

		if (this.disabled){ return; }

		var self = this;
		var closure = function(){ return function(){ self._openSubmenu(); } }();

		this.hover_timer = dojo.lang.setTimeout(closure, this.parent.submenuDelay);
	},

	_stopSubmenuTimer: function(){
		if (this.hover_timer){
			dojo.lang.clearTimeout(this.hover_timer);
			this.hover_timer = null;
		}
	},

	_openSubmenu: function(){
		if (this.disabled){ return; }

		// first close any other open submenu
		this.parent.closeSubpopup();

		var submenu = dojo.widget.getWidgetById(this.submenuId);
		if (submenu){
			this.parent._openSubmenu(submenu, this);
		}
	},

	_closedSubmenu: function(){
		this.onUnhover();
	},

	setDisabled: function(/*Boolean*/ value){
		// summary: enable or disable this menu item
		this.disabled = value;

		if (this.disabled){
			dojo.html.addClass(this.domNode, 'dojoMenuItem2Disabled');
		}else{
			dojo.html.removeClass(this.domNode, 'dojoMenuItem2Disabled');
		}
	},

	enable: function(){
		// summary: enable this menu item so user can click it
		this.setDisabled(false);
	},

	disable: function(){
		// summary: disable this menu item so user can't click it
		this.setDisabled(true);
	},

	menuOpen: function(message) {
		// summary: callback when menu is opened
		// TODO: I don't see anyone calling this menu item
	}

});

// summary
//	A line between two menu items
dojo.widget.defineWidget(
	"dojo.widget.MenuSeparator2",
	dojo.widget.HtmlWidget,
{
	templateString: '<tr class="dojoMenuSeparator2"><td colspan=4>'
			+'<div class="dojoMenuSeparator2Top"></div>'
			+'<div class="dojoMenuSeparator2Bottom"></div>'
			+'</td></tr>',

	postCreate: function(){
		dojo.html.disableSelection(this.domNode);
	}
});

// summary
//	A menu bar, listing menu choices horizontally, like the "File" menu in most desktop applications
dojo.widget.defineWidget(
	"dojo.widget.MenuBar2",
	dojo.widget.PopupMenu2,
{
	menuOverlap: 2,

	templateString: '<div class="dojoMenuBar2" tabIndex="0"><table class="dojoMenuBar2Client"><tr dojoAttachPoint="containerNode"></tr></table></div>',

	close: function(force){
		if(this._highlighted_option){
			this._highlighted_option.onUnhover();
		}

		this.closeSubpopup(force);
	},

	processKey: function(/*Event*/ evt){
		if(evt.ctrlKey || evt.altKey){ return false; }

		if (!dojo.html.hasClass(evt.target,"dojoMenuBar2")) { return false; }
		var rval = false;

		switch(evt.key){
 			case evt.KEY_DOWN_ARROW:
				rval = this._moveToChildMenu(evt);
				break;
			case evt.KEY_UP_ARROW:
				rval = this._moveToParentMenu(evt);
				break;
			case evt.KEY_RIGHT_ARROW:
				rval = this._moveToNext(evt);
				break;
			case evt.KEY_LEFT_ARROW:
				rval = this._moveToPrevious(evt);
				break;
			default:
				rval = 	dojo.widget.MenuBar2.superclass.processKey.apply(this, arguments);
				break;
		}

		return rval;
	},

	postCreate: function(){
		dojo.widget.MenuBar2.superclass.postCreate.apply(this, arguments);
		dojo.widget.PopupManager.opened(this);
		this.isShowingNow = true;
	},

	/*
	 * override PopupMenu2 to open the submenu below us rather than to our right
	 */
	_openSubmenu: function(submenu, from_item){
		var fromPos = dojo.html.getAbsolutePosition(from_item.domNode, true);
		var ourPos = dojo.html.getAbsolutePosition(this.domNode, true);
		var our_h = dojo.html.getBorderBox(this.domNode).height;
		var x = fromPos.x;
		var y = ourPos.y + our_h - this.menuOverlap;

		submenu.open(x, y, this, from_item.domNode);

		this.currentSubmenuTrigger = from_item;
		this.currentSubmenuTrigger.is_open = true;
	}
});

// summary
//	Item in a Menu2Bar
dojo.widget.defineWidget(
	"dojo.widget.MenuBarItem2",
	dojo.widget.MenuItem2,
{
	templateString:
		 '<td class="dojoMenuBarItem2" dojoAttachEvent="onMouseOver: onHover; onMouseOut: onUnhover; onClick: _onClick;">'
		+'<span>${this.caption}</span>'
		+'</td>',

	highlightClass: 'dojoMenuBarItem2Hover',

	setDisabled: function(value){
		this.disabled = value;
		if (this.disabled){
			dojo.html.addClass(this.domNode, 'dojoMenuBarItem2Disabled');
		}else{
			dojo.html.removeClass(this.domNode, 'dojoMenuBarItem2Disabled');
		}
	}
});


// ************************** make contextmenu work in konqueror and opera *********************
dojo.widget.Menu2.OperaAndKonqFixer = new function(){
 	var implement = true;
 	var delfunc = false;

 	/** 	dom event check
 	*
 	*	make a event and dispatch it and se if it calls function below,
 	*	if it indeed is supported and we dont need to implement our own
 	*/

 	// gets called if we have support for oncontextmenu
 	if (!dojo.lang.isFunction(dojo.doc().oncontextmenu)){
 		dojo.doc().oncontextmenu = function(){
 			implement = false;
 			delfunc = true;
 		}
 	}

 	if (dojo.doc().createEvent){ // moz, safari has contextmenu event, need to do livecheck on this env.
 		try {
 			var e = dojo.doc().createEvent("MouseEvents");
 			e.initMouseEvent("contextmenu", 1, 1, dojo.global(), 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, null);
 			dojo.doc().dispatchEvent(e);
 		} catch (e) {/* assume not supported */}
 	} else {
 		// IE no need to implement custom contextmenu
 		implement = false;
 	}

 	// clear this one if it wasn't there before
 	if (delfunc){
 		delete dojo.doc().oncontextmenu;
 	}
 	/***** end dom event check *****/


 	/**
 	*	this fixes a dom node by attaching a custom oncontextmenu function that gets called when apropriate
 	*	@param	node	a dom node
 	*
 	*	no returns
 	*/
 	this.fixNode = function(node){
 		if (implement){
 			// attach stub oncontextmenu function
 			if (!dojo.lang.isFunction(node.oncontextmenu)){
 				node.oncontextmenu = function(e){/*stub*/}
 			}

 			// attach control function for oncontextmenu
 			if (dojo.render.html.opera){
 				// opera
 				// listen to ctrl-click events
 				node._menufixer_opera = function(e){
 					if (e.ctrlKey){
 						this.oncontextmenu(e);
 					}
 				};

 				dojo.event.connect(node, "onclick", node, "_menufixer_opera");

 			} else {
 				// konqueror
 				// rightclick, listen to mousedown events
 				node._menufixer_konq = function(e){
 					if (e.button==2 ){
 						e.preventDefault(); // need to prevent browsers menu
 						this.oncontextmenu(e);
 					}
 				};

 				dojo.event.connect(node, "onmousedown", node, "_menufixer_konq");
 			}
 		}
 	}

 	/**
 	*	this cleans up a fixed node, prevent memoryleak?
 	*	@param node	node to clean
 	*
 	*	no returns
 	*/
 	this.cleanNode = function(node){
 		if (implement){
 			// checks needed if we gets a non fixed node
 			if (node._menufixer_opera){
 				dojo.event.disconnect(node, "onclick", node, "_menufixer_opera");
 				delete node._menufixer_opera;
 			} else if(node._menufixer_konq){
 				dojo.event.disconnect(node, "onmousedown", node, "_menufixer_konq");
 				delete node._menufixer_konq;
 			}
 			if (node.oncontextmenu){
 				delete node.oncontextmenu;
 			}
 		}
 	}
};