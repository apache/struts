/*
	Copyright (c) 2004-2005, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.Menu2");
dojo.provide("dojo.widget.html.Menu2");
dojo.provide("dojo.widget.PopupMenu2");
dojo.provide("dojo.widget.MenuItem2");

dojo.require("dojo.html");
dojo.require("dojo.style");
dojo.require("dojo.event.*");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.HtmlWidget");


dojo.widget.PopupMenu2 = function(){
	dojo.widget.HtmlWidget.call(this);
	this.items = [];
}

dojo.inherits(dojo.widget.PopupMenu2, dojo.widget.HtmlWidget);

dojo.lang.extend(dojo.widget.PopupMenu2, {
	widgetType: "PopupMenu2",
	isContainer: true,

	snarfChildDomOutput: true,

	currentSubmenu: null,
	currentSubmenuTrigger: null,
	parentMenu: null,
	isShowing: false,
	menuX: 0,
	menuY: 0,
	menuWidth: 0,
	menuHeight: 0,
	menuIndex: 0,

	domNode: null,
	containerNode: null,

	templateString: '<div><div dojoAttachPoint="containerNode"></div></div>',
	templateCssPath: dojo.uri.dojoUri("src/widget/templates/HtmlMenu2.css"),

	itemHeight: 18,
	iconGap: 1,
	accelGap: 10,
	submenuGap: 2,
	finalGap: 5,
	submenuIconSize: 4,
	separatorHeight: 9,
	submenuDelay: 500,
	submenuOverlap: 5,
	contextMenuForWindow: false,

	submenuIconSrc: dojo.uri.dojoUri("src/widget/templates/images/submenu_off.gif").toString(),
	submenuIconOnSrc: dojo.uri.dojoUri("src/widget/templates/images/submenu_on.gif").toString(),

	postCreate: function(){

		dojo.html.addClass(this.domNode, 'dojoPopupMenu2');
		dojo.html.addClass(this.containerNode, 'dojoPopupMenu2Client');

		this.domNode.style.left = '-9999px'
		this.domNode.style.top = '-9999px'

		if (this.contextMenuForWindow){
			var doc = document.documentElement  || dojo.html.body(); 
			dojo.event.connect(doc, "oncontextmenu", this, "onOpen");
		}

		this.layoutMenuSoon();
	},

	layoutMenuSoon: function(){

		dojo.lang.setTimeout(this, "layoutMenu", 0);
	},

	layoutMenu: function(){

		// determine menu width

		var max_label_w = 0;
		var max_accel_w = 0;

		for(var i=0; i<this.children.length; i++){

			if (this.children[i].getLabelWidth){

				max_label_w = Math.max(max_label_w, this.children[i].getLabelWidth());
			}

			if (dojo.lang.isFunction(this.children[i].getAccelWidth)){

				max_accel_w = Math.max(max_accel_w, this.children[i].getAccelWidth());
			}
		}

		if( isNaN(max_label_w) || isNaN(max_accel_w) ){
			// Browser needs some more time to calculate sizes
			this.layoutMenuSoon();
			return;
		}

		var clientLeft = dojo.style.getPixelValue(this.domNode, "padding-left", true) + dojo.style.getPixelValue(this.containerNode, "padding-left", true);
		var clientTop  = dojo.style.getPixelValue(this.domNode, "padding-top", true)  + dojo.style.getPixelValue(this.containerNode, "padding-top", true);

		if( isNaN(clientLeft) || isNaN(clientTop) ){
			// Browser needs some more time to calculate sizes
			this.layoutMenuSoon();
			return;
		}
		
		var y = clientTop;
		var max_item_width = 0;

		for(var i=0; i<this.children.length; i++){

			var ch = this.children[i];

			ch.layoutItem(max_label_w, max_accel_w);

			ch.topPosition = y;

			y += dojo.style.getOuterHeight(ch.domNode);
			max_item_width = Math.max(max_item_width, dojo.style.getOuterWidth(ch.domNode));
		}

		dojo.style.setContentWidth(this.containerNode, max_item_width);
		dojo.style.setContentHeight(this.containerNode, y-clientTop);

		dojo.style.setContentWidth(this.domNode, dojo.style.getOuterWidth(this.containerNode));
		dojo.style.setContentHeight(this.domNode, dojo.style.getOuterHeight(this.containerNode));

		this.menuWidth = dojo.style.getOuterWidth(this.domNode);
		this.menuHeight = dojo.style.getOuterHeight(this.domNode);
	},

	open: function(x, y, parentMenu, explodeSrc){

		// NOTE: alex:
		//	this couldn't have possibly worked. this.open wound up calling
		//	this.close, which called open...etc..
		if (this.isShowing){ /* this.close(); */ return; }

		if ( !parentMenu ) {
			// record whenever a top level menu is opened
			dojo.widget.html.Menu2Manager.opened(this);
		}

		var viewport = dojo.html.getViewportSize();
		var scrolloffset = dojo.html.getScrollOffset();

		var clientRect = {
			'left'  : scrolloffset[0],
			'right' : scrolloffset[0] + viewport[0],
			'top'   : scrolloffset[1],
			'bottom': scrolloffset[1] + viewport[1]
		};

		if (parentMenu){
			// submenu is opening

			if (x + this.menuWidth > clientRect.right){ x = x - (this.menuWidth + parentMenu.menuWidth - (2 * this.submenuOverlap)); }

			if (y + this.menuHeight > clientRect.bottom){ y = y -
			(this.menuHeight - (this.itemHeight + 5)); } // TODO: why 5?

		}else{
			// top level menu is opening

			if (x < clientRect.left){ x = clientRect.left; }
			if (x + this.menuWidth > clientRect.right){ x = x - this.menuWidth; }

			if (y < clientRect.top){ y = clientRect.top; }
			if (y + this.menuHeight > clientRect.bottom){ y = y - this.menuHeight; }
		}

		this.parentMenu = parentMenu;
		this.explodeSrc = explodeSrc;
		this.menuIndex = parentMenu ? parentMenu.menuIndex + 1 : 1;

		this.menuX = x;
		this.menuY = y;

		// move the menu into position but make it invisible
		// (because when menus are initially constructed they are visible but off-screen)
		this.domNode.style.zIndex = 10 + this.menuIndex;
		this.domNode.style.left = x + 'px';
		this.domNode.style.top = y + 'px';
		this.domNode.style.display='none';
		
		// then use the user defined method to display it
		this.show();

		this.isShowing = true;
	},

	close: function(){
		this.closeSubmenu();
		this.hide();
		this.isShowing = false;
		dojo.widget.html.Menu2Manager.closed(this);
	},

	closeAll: function(){

		if (this.parentMenu){
			this.parentMenu.closeAll();
		}else{
			this.close();
		}
	},

	closeSubmenu: function(){
		if (this.currentSubmenu == null){ return; }

		this.currentSubmenu.close();
		this.currentSubmenu = null;

		this.currentSubmenuTrigger.is_open = false;
		this.currentSubmenuTrigger.closedSubmenu();
		this.currentSubmenuTrigger = null;
	},

	openSubmenu: function(submenu, from_item){

		var our_x = dojo.style.getPixelValue(this.domNode, 'left');
		var our_y = dojo.style.getPixelValue(this.domNode, 'top');
		var our_w = dojo.style.getOuterWidth(this.domNode);
		var item_y = from_item.topPosition;

		var x = our_x + our_w - this.submenuOverlap;
		var y = our_y + item_y;

		this.currentSubmenu = submenu;
		this.currentSubmenu.open(x, y, this, from_item.domNode);

		this.currentSubmenuTrigger = from_item;
		this.currentSubmenuTrigger.is_open = true;
	},

	onOpen: function(e){

		//dojo.debugShallow(e);
		this.open(e.clientX, e.clientY, null, [e.clientX, e.clientY]);

		if(e["preventDefault"]){
			e.preventDefault();
		}
	},

	isPointInMenu: function(x, y){

		if (x < this.menuX){ return 0; }
		if (x > this.menuX + this.menuWidth){ return 0; }

		if (y < this.menuY){ return 0; }
		if (y > this.menuY + this.menuHeight){ return 0; }

		return 1;
	}
});


dojo.widget.MenuItem2 = function(){
	dojo.widget.HtmlWidget.call(this);
}

dojo.inherits(dojo.widget.MenuItem2, dojo.widget.HtmlWidget);

dojo.lang.extend(dojo.widget.MenuItem2, {
	widgetType: "MenuItem2",
	templateString:
			 '<div class="dojoMenuItem2">'
			+'<div dojoAttachPoint="iconNode" class="dojoMenuItem2Icon"></div>'
			+'<span dojoAttachPoint="labelNode" class="dojoMenuItem2Label"><span><span></span></span></span>'
			+'<span dojoAttachPoint="accelNode" class="dojoMenuItem2Accel"><span><span></span></span></span>'
			+'<div dojoAttachPoint="submenuNode" class="dojoMenuItem2Submenu"></div>'
			+'<div dojoAttachPoint="targetNode" class="dojoMenuItem2Target" dojoAttachEvent="onMouseOver: onHover; onMouseOut: onUnhover; onClick;">&nbsp;</div>'
			+'</div>',

	//
	// nodes
	//

	domNode: null,
	iconNode: null,
	labelNode: null,
	accelNode: null,
	submenuNode: null,
	targetNode: null,

	//
	// internal settings
	//

	is_hovering: false,
	hover_timer: null,
	is_open: false,
	topPosition: 0,
	is_disabled: false,

	//
	// options
	//

	caption: 'Untitled',
	accelKey: '',
	iconSrc: '',
	submenuId: '',
	isDisabled: false,


	postCreate: function(){

		dojo.html.disableSelection(this.domNode);

		if (this.isDisabled){
			this.setDisabled(true);
		}

		this.labelNode.childNodes[0].appendChild(document.createTextNode(this.caption));
		this.accelNode.childNodes[0].appendChild(document.createTextNode(this.accelKey));

		this.labelShadowNode = this.labelNode.childNodes[0].childNodes[0];
		this.accelShadowNode = this.accelNode.childNodes[0].childNodes[0];

		this.labelShadowNode.appendChild(document.createTextNode(this.caption));
		this.accelShadowNode.appendChild(document.createTextNode(this.accelKey));
	},

	layoutItem: function(label_w, accel_w){

		var x_label = this.parent.itemHeight + this.parent.iconGap;
		var x_accel = x_label + label_w + this.parent.accelGap;
		var x_submu = x_accel + accel_w + this.parent.submenuGap;
		var total_w = x_submu + this.parent.submenuIconSize + this.parent.finalGap;


		this.iconNode.style.left = '0px';
		this.iconNode.style.top = '0px';


		if (this.iconSrc){

			if ((this.iconSrc.toLowerCase().substring(this.iconSrc.length-4) == ".png") && (dojo.render.html.ie)){

				this.iconNode.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+this.iconSrc+"', sizingMethod='image')";
				this.iconNode.style.backgroundImage = '';
			}else{
				this.iconNode.style.backgroundImage = 'url('+this.iconSrc+')';
			}
		}else{
			this.iconNode.style.backgroundImage = '';
		}

		dojo.style.setOuterWidth(this.iconNode, this.parent.itemHeight);
		dojo.style.setOuterHeight(this.iconNode, this.parent.itemHeight);

		dojo.style.setOuterHeight(this.labelNode, this.parent.itemHeight);
		dojo.style.setOuterHeight(this.accelNode, this.parent.itemHeight);

		dojo.style.setContentWidth(this.domNode, total_w);
		dojo.style.setContentHeight(this.domNode, this.parent.itemHeight);

		this.labelNode.style.left = x_label + 'px';
		this.accelNode.style.left = x_accel + 'px';
		this.submenuNode.style.left = x_submu + 'px';

		dojo.style.setOuterWidth(this.submenuNode, this.parent.submenuIconSize);
		dojo.style.setOuterHeight(this.submenuNode, this.parent.itemHeight);

		this.submenuNode.style.display = this.submenuId ? 'block' : 'none';
		this.submenuNode.style.backgroundImage = 'url('+this.parent.submenuIconSrc+')';

		dojo.style.setOuterWidth(this.targetNode, total_w);
		dojo.style.setOuterHeight(this.targetNode, this.parent.itemHeight);
	},

	onHover: function(){

		if (this.is_hovering){ return; }
		if (this.is_open){ return; }

		this.parent.closeSubmenu();
		this.highlightItem();

		if (this.is_hovering){ this.stopSubmenuTimer(); }
		this.is_hovering = 1;
		this.startSubmenuTimer();
	},

	onUnhover: function(){

		if (!this.is_open){ this.unhighlightItem(); }

		this.is_hovering = 0;
		this.stopSubmenuTimer();
	},

	onClick: function(){

		if (this.is_disabled){ return; }

		if (this.submenuId){

			if (!this.is_open){
				this.stopSubmenuTimer();
				this.openSubmenu();
			}

		}else{

			this.parent.closeAll();
		}
	},

	highlightItem: function(){

		dojo.html.addClass(this.domNode, 'dojoMenuItem2Hover');
		this.submenuNode.style.backgroundImage = 'url('+this.parent.submenuIconOnSrc+')';
	},

	unhighlightItem: function(){

		dojo.html.removeClass(this.domNode, 'dojoMenuItem2Hover');
		this.submenuNode.style.backgroundImage = 'url('+this.parent.submenuIconSrc+')';
	},

	startSubmenuTimer: function(){
		this.stopSubmenuTimer();

		if (this.is_disabled){ return; }

		var self = this;
		var closure = function(){ return function(){ self.openSubmenu(); } }();

		this.hover_timer = window.setTimeout(closure, this.parent.submenuDelay);
	},

	stopSubmenuTimer: function(){
		if (this.hover_timer){
			window.clearTimeout(this.hover_timer);
			this.hover_timer = null;
		}
	},

	openSubmenu: function(){
		// first close any other open submenu
		this.parent.closeSubmenu();

		var submenu = dojo.widget.getWidgetById(this.submenuId);
		if (submenu){

			this.parent.openSubmenu(submenu, this);
		}

		//dojo.debug('open submenu for item '+this.widgetId);
	},

	closedSubmenu: function(){

		this.onUnhover();
	},

	setDisabled: function(value){

		if (value == this.is_disabled){ return; }

		this.is_disabled = value;

		if (this.is_disabled){
			dojo.html.addClass(this.domNode, 'dojoMenuItem2Disabled');
		}else{
			dojo.html.removeClass(this.domNode, 'dojoMenuItem2Disabled');
		}
	},

	getLabelWidth: function(){

		var node = this.labelNode.childNodes[0];

		return dojo.style.getOuterWidth(node);
	},

	getAccelWidth: function(){

		var node = this.accelNode.childNodes[0];

		return dojo.style.getOuterWidth(node);
	}
});


dojo.widget.MenuSeparator2 = function(){
	dojo.widget.HtmlWidget.call(this);
}

dojo.inherits(dojo.widget.MenuSeparator2, dojo.widget.HtmlWidget);

dojo.lang.extend(dojo.widget.MenuSeparator2, {
	widgetType: "MenuSeparator2",

	domNode: null,
	topNode: null,
	bottomNode: null,

	templateString: '<div>'
			+'<div dojoAttachPoint="topNode"></div>'
			+'<div dojoAttachPoint="bottomNode"></div>'
			+'</div>',

	postCreate: function(){

		dojo.html.addClass(this.domNode, 'dojoMenuSeparator2');
		dojo.html.addClass(this.topNode, 'dojoMenuSeparator2Top');
		dojo.html.addClass(this.bottomNode, 'dojoMenuSeparator2Bottom');

		dojo.html.disableSelection(this.domNode);

		this.layoutItem();
	},

	layoutItem: function(label_w, accel_w){

		var full_width = this.parent.itemHeight
				+ this.parent.iconGap
				+ label_w
				+ this.parent.accelGap
				+ accel_w
				+ this.parent.submenuGap
				+ this.parent.submenuIconSize
				+ this.parent.finalGap;

		if (isNaN(full_width)){ return; }

		dojo.style.setContentHeight(this.domNode, this.parent.separatorHeight);
		dojo.style.setContentWidth(this.domNode, full_width);		
	}
});

//
// the menu manager makes sure we don't have several menus
// open at once. the root menu in an opening sequence calls
// opened(). when a root menu closes it calls closed(). then
// everything works. lovely.
//

dojo.widget.html.Menu2Manager = new function(){

	this.currentMenu = null;
	this.focusNode = null;

	dojo.event.connect(document, 'onmousedown', this, 'onClick');

	this.closed = function(menu){
		if (this.currentMenu == menu){
			this.currentMenu = null;
		}
	};

	this.opened = function(menu){
		if (menu == this.currentMenu){ return; }

		if (this.currentMenu){
			this.currentMenu.close();
		}

		this.currentMenu = menu;
	};

	this.onClick = function(e){

		if (!this.currentMenu){ return; }

		var x = e.clientX;
		var y = e.clientY;
		var m = this.currentMenu;

		// starting from the base menu, perform a hit test
		// and exit when one succeeds

		while (m){

			if (m.isPointInMenu(x, y)){

				return;
			}

			m = m.currentSubmenu;
		}

		// the click didn't fall within the open menu tree
		// so close it

		this.currentMenu.close();
	};
}


// make it a tag
dojo.widget.tags.addParseTreeHandler("dojo:PopupMenu2");
dojo.widget.tags.addParseTreeHandler("dojo:MenuItem2");
dojo.widget.tags.addParseTreeHandler("dojo:MenuSeparator2");

