/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.Button");

dojo.require("dojo.lang.extras");
dojo.require("dojo.html.*");
dojo.require("dojo.html.selection");
dojo.require("dojo.widget.*");

/*
 * summary
 *	Basically the same thing as a normal HTML button, but with special styling.
 * usage
 *	<button dojoType="button" onClick="...">Hello world</button>
 *
 *  var button1 = dojo.widget.createWidget("Button", {caption: "hello world", onClick: foo});
 *	document.body.appendChild(button1.domNode);
 */
dojo.widget.defineWidget(
	"dojo.widget.Button",
	dojo.widget.HtmlWidget,
	{
		isContainer: true,

		// String
		//	text to display in button
		caption: "",
		
		// Boolean
		//	if true, cannot click button
		disabled: false,

		templatePath: dojo.uri.dojoUri("src/widget/templates/ButtonTemplate.html"),
		templateCssPath: dojo.uri.dojoUri("src/widget/templates/ButtonTemplate.css"),
		
		// Url
		//	prefix of filename holding images (left, center, right) for button in normal state
		inactiveImg: "src/widget/templates/images/soriaButton-",
		
		// Url
		//	prefix of filename holding images (left, center, right) for button when it's being hovered over
		activeImg: "src/widget/templates/images/soriaActive-",

		// Url
		//	prefix of filename holding images (left, center, right) for button between mouse-down and mouse-up
		pressedImg: "src/widget/templates/images/soriaPressed-",

		// Url
		//	prefix of filename holding images (left, center, right) for button when it's disabled (aka, grayed-out)
		disabledImg: "src/widget/templates/images/soriaDisabled-",
		
		// Number
		//	shape of the button's end pieces;
		//	the height of the end pieces is a function of the button's height (which in turn is a function of the button's content),
		//	and then the width of the end pieces is relative to their height.
		width2height: 1.0/3.0,

		fillInTemplate: function(){
			if(this.caption){
				this.containerNode.appendChild(document.createTextNode(this.caption));
			}
			dojo.html.disableSelection(this.containerNode);
		},

		postCreate: function(){
			this._sizeMyself();
		},
	
		_sizeMyself: function(){
			// we cannot size correctly if any of our ancestors are hidden (display:none),
			// so temporarily attach to document.body
			if(this.domNode.parentNode){
				var placeHolder = document.createElement("span");
				dojo.html.insertBefore(placeHolder, this.domNode);
			}
			dojo.body().appendChild(this.domNode);
			
			this._sizeMyselfHelper();
			
			// Put this.domNode back where it was originally
			if(placeHolder){
				dojo.html.insertBefore(this.domNode, placeHolder);
				dojo.html.removeNode(placeHolder);
			}
		},

		_sizeMyselfHelper: function(){
			var mb = dojo.html.getMarginBox(this.containerNode);
			this.height = mb.height;
			this.containerWidth = mb.width;
			var endWidth= this.height * this.width2height;
	
			this.containerNode.style.left=endWidth+"px";
	
			this.leftImage.height = this.rightImage.height = this.centerImage.height = this.height;
			this.leftImage.width = this.rightImage.width = endWidth+1;
			this.centerImage.width = this.containerWidth;
			this.centerImage.style.left=endWidth+"px";
			this._setImage(this.disabled ? this.disabledImg : this.inactiveImg);

			if ( this.disabled ) {
				dojo.html.prependClass(this.domNode, "dojoButtonDisabled");
				this.domNode.removeAttribute("tabIndex");
				dojo.widget.wai.setAttr(this.domNode, "waiState", "disabled", true);
			} else {
				dojo.html.removeClass(this.domNode, "dojoButtonDisabled");
				this.domNode.setAttribute("tabIndex", "0");
				dojo.widget.wai.setAttr(this.domNode, "waiState", "disabled", false);
			}
				
			this.domNode.style.height=this.height + "px";
			this.domNode.style.width= (this.containerWidth+2*endWidth) + "px";
		},
	
		onMouseOver: function(/*Event*/ e){
			// summary: callback when user mouses-over the button
			if( this.disabled ){ return; }
			dojo.html.prependClass(this.buttonNode, "dojoButtonHover");
			this._setImage(this.activeImg);
		},
	
		onMouseDown: function(/*Event*/ e){
			// summary: callback when user starts to click the button
			if( this.disabled ){ return; }
			dojo.html.prependClass(this.buttonNode, "dojoButtonDepressed");
			dojo.html.removeClass(this.buttonNode, "dojoButtonHover");
			this._setImage(this.pressedImg);
		},

		onMouseUp: function(/*Event*/ e){
			// summary: callback when the user finishes clicking
			if( this.disabled ){ return; }
			dojo.html.prependClass(this.buttonNode, "dojoButtonHover");
			dojo.html.removeClass(this.buttonNode, "dojoButtonDepressed");
			this._setImage(this.activeImg);
		},
	
		onMouseOut: function(/*Event*/ e){
			// summary: callback when the user moves the mouse off the button
			if( this.disabled ){ return; }
			if( e.toElement && dojo.html.isDescendantOf(e.toElement, this.buttonNode) ){
				return; // Ignore IE mouseOut events that dont actually leave button - Prevents hover image flicker in IE
			}
			dojo.html.removeClass(this.buttonNode, "dojoButtonHover");
			this._setImage(this.inactiveImg);
		},

		onKey: function(/*Event*/ e){
			// summary: callback when the user presses a key (on key-down)
			if (!e.key) { return; }
			var menu = dojo.widget.getWidgetById(this.menuId);
			if (e.key == e.KEY_ENTER || e.key == " "){
				this.onMouseDown(e);
				this.buttonClick(e);
				dojo.lang.setTimeout(this, "onMouseUp", 75, e);
				dojo.event.browser.stopEvent(e);
			}
			if(menu && menu.isShowingNow && e.key == e.KEY_DOWN_ARROW){
				// disconnect onBlur when focus moves into menu
				dojo.event.disconnect(this.domNode, "onblur", this, "onBlur");
				// allow event to propagate to menu
			}
		},

		onFocus: function(/*Event*/ e){
			// summary: callback on focus to the button
			var menu = dojo.widget.getWidgetById(this.menuId);
			if (menu){
				dojo.event.connectOnce(this.domNode, "onblur", this, "onBlur");
			}
		},

		onBlur: function(/*Event*/ e){
			// summary: callback when button loses focus
			var menu = dojo.widget.getWidgetById(this.menuId);
			if ( !menu ) { return; }
	
			if ( menu.close && menu.isShowingNow ){
				menu.close();
			}
		},

		buttonClick: function(/*Event*/ e){
			// summary: internal function for handling button clicks
			if(!this.disabled){ 
				// focus may fail when tabIndex is not supported on div's
				// by the browser, or when the node is disabled
				try { this.domNode.focus(); } catch(e2) {};
				this.onClick(e); 
			}
		},

		onClick: function(/*Event*/ e) {
			// summary: callback for when button is clicked; user can override this function
		},

		_setImage: function(/*String*/ prefix){
			this.leftImage.src=dojo.uri.dojoUri(prefix + "l.gif");
			this.centerImage.src=dojo.uri.dojoUri(prefix + "c.gif");
			this.rightImage.src=dojo.uri.dojoUri(prefix + "r.gif");
		},
		
		_toggleMenu: function(/*String*/ menuId){
			var menu = dojo.widget.getWidgetById(menuId); 
			if ( !menu ) { return; }
			if ( menu.open && !menu.isShowingNow) {
				var pos = dojo.html.getAbsolutePosition(this.domNode, false);
				menu.open(pos.x, pos.y+this.height, this);
			} else if ( menu.close && menu.isShowingNow ){
				menu.close();
			} else {
				menu.toggle();
			}
		},
		
		setCaption: function(/*String*/ content){
			// summary: reset the caption (text) of the button; takes an HTML string
			this.caption=content;
			this.containerNode.innerHTML=content;
			this._sizeMyself();
		},
		
		setDisabled: function(/*Boolean*/ disabled){
			// summary: set disabled state of button
			this.disabled=disabled;
			this._sizeMyself();
		}
	});

/*
 * summary
 *	push the button and a menu shows up
 * usage
 *	<button dojoType="DropDownButton" menuId="mymenu">Hello world</button>
 *
 *  var button1 = dojo.widget.createWidget("DropDownButton", {caption: "hello world", menuId: foo});
 *	document.body.appendChild(button1.domNode);
 */
dojo.widget.defineWidget(
	"dojo.widget.DropDownButton",
	dojo.widget.Button,
	{
		// String
		//	widget id of the menu that this button should activate
		menuId: "",

		// Url
		//	path of arrow image to display to the right of the button text
		downArrow: "src/widget/templates/images/whiteDownArrow.gif",

		// Url
		//	path of arrow image to display to the right of the button text, when the button is disabled
		disabledDownArrow: "src/widget/templates/images/whiteDownArrow.gif",
	
		fillInTemplate: function(){
			dojo.widget.DropDownButton.superclass.fillInTemplate.apply(this, arguments);
	
			this.arrow = document.createElement("img");
			dojo.html.setClass(this.arrow, "downArrow");

			dojo.widget.wai.setAttr(this.domNode, "waiState", "haspopup", this.menuId);
		},

		_sizeMyselfHelper: function(){
			// draw the arrow (todo: why is the arror in containerNode rather than outside it?)
			this.arrow.src = dojo.uri.dojoUri(this.disabled ? this.disabledDownArrow : this.downArrow);
			this.containerNode.appendChild(this.arrow);

			dojo.widget.DropDownButton.superclass._sizeMyselfHelper.call(this);
		},

		onClick: function(/*Event*/ e){
			// summary: callback when button is clicked; user shouldn't override this function or else the menu won't toggle
			this._toggleMenu(this.menuId);
		}
	});

/*
 * summary
 *	left side is normal button, right side displays menu
 * usage
 *	<button dojoType="ComboButton" onClick="..." menuId="mymenu">Hello world</button>
 *
 *  var button1 = dojo.widget.createWidget("DropDownButton", {caption: "hello world", onClick: foo, menuId: "myMenu"});
 *	document.body.appendChild(button1.domNode);
 */
dojo.widget.defineWidget(
	"dojo.widget.ComboButton",
	dojo.widget.Button,
	{
		// String
		//	widget id of the menu that this button should activate
		menuId: "",
	
		templatePath: dojo.uri.dojoUri("src/widget/templates/ComboButtonTemplate.html"),
	
		// Integer
		//	# of pixels between left & right part of button
		splitWidth: 2,
		
		// Integer
		//	width of segment holding down arrow
		arrowWidth: 5,
	
		_sizeMyselfHelper: function(/*Event*/ e){
			var mb = dojo.html.getMarginBox(this.containerNode);
			this.height = mb.height;
			this.containerWidth = mb.width;

			var endWidth= this.height/3;

			if(this.disabled){
				dojo.widget.wai.setAttr(this.domNode, "waiState", "disabled", true);
				this.domNode.removeAttribute("tabIndex");
			}
			else {
				dojo.widget.wai.setAttr(this.domNode, "waiState", "disabled", false);
				this.domNode.setAttribute("tabIndex", "0");
			}
	
			// left part
			this.leftImage.height = this.rightImage.height = this.centerImage.height = 
				this.arrowBackgroundImage.height = this.height;
			this.leftImage.width = endWidth+1;
			this.centerImage.width = this.containerWidth;
			this.buttonNode.style.height = this.height + "px";
			this.buttonNode.style.width = endWidth + this.containerWidth + "px";
			this._setImage(this.disabled ? this.disabledImg : this.inactiveImg);

			// right part
			this.arrowBackgroundImage.width=this.arrowWidth;
			this.rightImage.width = endWidth+1;
			this.rightPart.style.height = this.height + "px";
			this.rightPart.style.width = this.arrowWidth + endWidth + "px";
			this._setImageR(this.disabled ? this.disabledImg : this.inactiveImg);
	
			// outer container
			this.domNode.style.height=this.height + "px";
			var totalWidth = this.containerWidth+this.splitWidth+this.arrowWidth+2*endWidth;
			this.domNode.style.width= totalWidth + "px";
		},
	
		_setImage: function(prefix){
			this.leftImage.src=dojo.uri.dojoUri(prefix + "l.gif");
			this.centerImage.src=dojo.uri.dojoUri(prefix + "c.gif");
		},
	
		/*** functions on right part of button ***/
		rightOver: function(/*Event*/ e){
			// summary:
			//	callback when mouse-over right part of button;
			//	onMouseOver() is the callback for the left side of the button.
			if( this.disabled ){ return; }
			dojo.html.prependClass(this.rightPart, "dojoButtonHover");
			this._setImageR(this.activeImg);
		},
	
		rightDown: function(/*Event*/ e){
			// summary:
			//	callback when mouse-down right part of button;
			//	onMouseDown() is the callback for the left side of the button.
			if( this.disabled ){ return; }
			dojo.html.prependClass(this.rightPart, "dojoButtonDepressed");
			dojo.html.removeClass(this.rightPart, "dojoButtonHover");
			this._setImageR(this.pressedImg);
		},

		rightUp: function(/*Event*/ e){
			// summary:
			//	callback when mouse-up right part of button;
			//	onMouseUp() is the callback for the left side of the button.
			if( this.disabled ){ return; }
			dojo.html.prependClass(this.rightPart, "dojoButtonHover");
			dojo.html.removeClass(this.rightPart, "dojoButtonDepressed");
			this._setImageR(this.activeImg);
		},
	
		rightOut: function(/*Event*/ e){
			// summary:
			//	callback when moving the mouse off of the right part of button;
			//	onMouseOut() is the callback for the left side of the button.
			if( this.disabled ){ return; }
			dojo.html.removeClass(this.rightPart, "dojoButtonHover");
			this._setImageR(this.inactiveImg);
		},

		rightClick: function(/*Event*/ e){
			// summary:
			//	callback when clicking the right part of button;
			//	onClick() is the callback for the left side of the button.
			if( this.disabled ){ return; }
			// focus may fail when tabIndex is not supported on div's
			// by the browser, or when the node is disabled
			try { this.domNode.focus(); } catch(e2) {};
			this._toggleMenu(this.menuId);
		},
	
		_setImageR: function(prefix){
			this.arrowBackgroundImage.src=dojo.uri.dojoUri(prefix + "c.gif");
			this.rightImage.src=dojo.uri.dojoUri(prefix + "r.gif");
		},

		/*** keyboard functions ***/
		
		onKey: function(/*Event*/ e){
			if (!e.key) { return; }
			var menu = dojo.widget.getWidgetById(this.menuId);
			if(e.key== e.KEY_ENTER || e.key == " "){
				this.onMouseDown(e);
				this.buttonClick(e);
				dojo.lang.setTimeout(this, "onMouseUp", 75, e);
				dojo.event.browser.stopEvent(e);
			} else if (e.key == e.KEY_DOWN_ARROW && e.altKey){
				this.rightDown(e);
				this.rightClick(e);
				dojo.lang.setTimeout(this, "rightUp", 75, e);
				dojo.event.browser.stopEvent(e);
			} else if(menu && menu.isShowingNow && e.key == e.KEY_DOWN_ARROW){
				// disconnect onBlur when focus moves into menu
				dojo.event.disconnect(this.domNode, "onblur", this, "onBlur");
				// allow event to propagate to menu
			}
		}
	});
