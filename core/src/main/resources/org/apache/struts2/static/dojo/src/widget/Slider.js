/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.Slider");

// load dependencies
dojo.require("dojo.event.*");
dojo.require("dojo.dnd.*");
// dojo.dnd.* doesn't include this package, because it's not in __package__.js
dojo.require("dojo.dnd.HtmlDragMove");
dojo.require("dojo.widget.*");
dojo.require("dojo.html.layout");


// summary
//	Slider Widget.
//	
//	The slider widget comes in three forms:
//	 1. Base Slider widget which supports movement in x and y dimensions
//	 2. Vertical Slider (SliderVertical) widget which supports movement
//	    only in the y dimension.
//	 3. Horizontal Slider (SliderHorizontal) widget which supports movement
//	    only in the x dimension.
//
//	The key objects in the widget are:
//	 - a container div which displays a bar in the background (Slider object)
//	 - a handle inside the container div, which represents the value
//	   (sliderHandle DOM node)
//	 - the object which moves the handle (_handleMove is of type 
//	   SliderDragMoveSource)
//
//	The values for the slider are calculated by grouping pixels together, 
//	based on the number of values to be represented by the slider.
//	The number of pixels in a group is called the _valueSize
//	 e.g. if slider is 150 pixels long, and is representing the values
//	      0,1,...10 then pixels are grouped into lots of 15 (_valueSize), where:
//	        value 0 maps to pixels  0 -  7
//	              1                 8 - 22
//	              2                23 - 37 etc.
//	The accuracy of the slider is limited to the number of pixels
//	(i.e tiles > pixels will result in the slider not being able to
//	 represent some values).
dojo.widget.defineWidget (
	"dojo.widget.Slider",
	dojo.widget.HtmlWidget,
	{
		// Number
		//	minimum value to be represented by slider in the horizontal direction
		minimumX: 0,
		// Number
		//	minimum value to be represented by slider in the vertical direction
		minimumY: 0,
		// Number
		//	maximum value to be represented by slider in the horizontal direction
		maximumX: 10,
		// Number
		//	maximum value to be represented by slider in the vertical direction
		maximumY: 10,
		// Number
		//	number of values to be represented by slider in the horizontal direction
		//	=0 means no snapping
		snapValuesX: 0,
		// Number
		//	number of values to be represented by slider in the vertical direction
		//	=0 means no snapping
		snapValuesY: 0,
		// should the handle snap to the grid or remain where it was dragged to?
		// FIXME: snapToGrid=false is logically in conflict with setting snapValuesX and snapValuesY
		_snapToGrid: true,
		// Boolean
		//	enables (disables) sliding in the horizontal direction
		isEnableX: true,
		// Boolean
		//	enables (disables) sliding in the vertical direction
		isEnableY: true,
		// value size (pixels) in the x dimension
		_valueSizeX: 0.0,
		// value size (pixels) in the y dimension
		_valueSizeY: 0.0,
		// left most edge of constraining container (pixels) in the X dimension
		_minX: 0,
		// top most edge of constraining container (pixels) in the Y dimension
		_minY: 0,
		// constrained slider size (pixels) in the x dimension
		_constraintWidth: 0,
		// constrained slider size (pixels) in the y dimension
		_constraintHeight: 0,
		// progress image right clip value (pixels) in the X dimension
		_clipLeft: 0,
		// progress image left clip value (pixels) in the X dimension
		_clipRight: 0,
		// progress image top clip value (pixels) in the Y dimension
		_clipTop: 0,
		// progress image bottom clip value (pixels) in the Y dimension
		_clipBottom: 0,
		// half the size of the slider handle (pixels) in the X dimension
		_clipXdelta: 0,
		// half the size of the slider handle (pixels) in the Y dimension
		_clipYdelta: 0,
		// Number
		//	initial value in the x dimension
		initialValueX: 0,
		// Number
		//	initial value in the y dimension
		initialValueY: 0,
		// Boolean
		//	values decrease in the X dimension
		flipX: false,
		// Boolean
		//	values decrease in the Y dimension
		flipY: false,
		// Boolean
		//	enables (disables) the user to click on the slider to set the position
		clickSelect: true,
		// Boolean
		//	disables (enables) the value to change while you are dragging, or just after drag finishes
		activeDrag: false,

		templateCssPath: dojo.uri.dojoUri ("src/widget/templates/Slider.css"),
		templatePath: dojo.uri.dojoUri ("src/widget/templates/Slider.html"),

		// This is set to true when a drag is started, so that it is not confused
		// with a click
		_isDragInProgress: false,

		// default user style attributes
		// String
		//	down arrow graphic URL
		bottomButtonSrc: dojo.uri.dojoUri("src/widget/templates/images/slider_down_arrow.png"),
		// String
		//	up arrow graphic URL
		topButtonSrc: dojo.uri.dojoUri("src/widget/templates/images/slider_up_arrow.png"),
		// String
		//	left arrow graphic URL
		leftButtonSrc: dojo.uri.dojoUri("src/widget/templates/images/slider_left_arrow.png"),
		// String
		//	right arrow graphic URL
		rightButtonSrc: dojo.uri.dojoUri("src/widget/templates/images/slider_right_arrow.png"),
		// String
		//	slider background graphic URL
		backgroundSrc: dojo.uri.dojoUri("src/widget/templates/images/blank.gif"),
		// String
		//	slider background graphic URL to overlay the normal background to show progress
		progressBackgroundSrc: dojo.uri.dojoUri("src/widget/templates/images/blank.gif"),
		// String
		//	sizing style attributes for the background image
		backgroundSize: "width:200px;height:200px;",
		// String
		//	style attributes (other than sizing) for the background image
		backgroundStyle: "",
		// String
		//	style attributes for the left and right arrow images
		buttonStyleX: "",
		// String
		//	style attributes for the up and down arrow images
		buttonStyleY: "",
		// String
		//	style attributes for the moveable slider image
		handleStyle: "",
		// String
		//	moveable slider graphic URL
		handleSrc: dojo.uri.dojoUri("src/widget/templates/images/slider-button.png"),
		// Boolean
		//	show (don't show) the arrow buttons
		showButtons: true,
		_eventCount: 0,
		_typamaticTimer: null,
		_typamaticFunction: null,
		// Number
		//	number of milliseconds before a held key or button becomes typematic 
		defaultTimeout: 500,
		// Number
		//	fraction of time used to change the typematic timer between events
		//	1.0 means that each typematic event fires at defaultTimeout intervals
		//	< 1.0 means that each typematic event fires at an increasing faster rate
		timeoutChangeRate: 0.90,
		_currentTimeout: this.defaultTimeout,

		// does the keyboard related stuff
		_handleKeyEvents: function(/*Event*/ evt){
			if(!evt.key){ return; }

			if(!evt.ctrlKey && !evt.altKey){
				switch(evt.key){
					case evt.KEY_LEFT_ARROW:
						dojo.event.browser.stopEvent(evt);
						this._leftButtonPressed(evt);
						return;
					case evt.KEY_RIGHT_ARROW:
						dojo.event.browser.stopEvent(evt);
						this._rightButtonPressed(evt);
						return;
					case evt.KEY_DOWN_ARROW:
						dojo.event.browser.stopEvent(evt);
						this._bottomButtonPressed(evt);
						return;
					case evt.KEY_UP_ARROW:
						dojo.event.browser.stopEvent(evt);
						this._topButtonPressed(evt);
						return;
				}
			}
			this._eventCount++;

		},

		_pressButton: function(/*DomNode*/ buttonNode){
			buttonNode.className = buttonNode.className.replace("Outset","Inset");
		},

		_releaseButton: function(/*DomNode*/ buttonNode){
			buttonNode.className = buttonNode.className.replace("Inset","Outset");
		},

		_buttonPressed: function(/*Event*/ evt, /*DomNode*/ buttonNode){
			this._setFocus();
			if(typeof evt == "object"){
				if(this._typamaticTimer != null){
					if(this._typamaticNode == buttonNode){
						return;
					}
					clearTimeout(this._typamaticTimer);
				}
				this._buttonReleased(null);
				this._eventCount++;
				this._typamaticTimer = null;
				this._currentTimeout = this.defaultTimeout;
				dojo.event.browser.stopEvent(evt);
			}else if (evt != this._eventCount){
				this._buttonReleased(null);
				return false;
			}
			if (buttonNode == this.leftButtonNode && this.isEnableX){
				this._snapX(dojo.html.getPixelValue (this.sliderHandleNode,"left") - this._valueSizeX);
			}
			else if (buttonNode == this.rightButtonNode && this.isEnableX){
				this._snapX(dojo.html.getPixelValue (this.sliderHandleNode,"left") + this._valueSizeX);
			}
			else if (buttonNode == this.topButtonNode && this.isEnableY){
				this._snapY(dojo.html.getPixelValue (this.sliderHandleNode,"top") - this._valueSizeY);
			}
			else if (buttonNode == this.bottomButtonNode && this.isEnableY){
				this._snapY(dojo.html.getPixelValue (this.sliderHandleNode,"top") + this._valueSizeY);
			}
			else {
				return false;
			}
			this._pressButton(buttonNode);
			this.notifyListeners();
			this._typamaticNode = buttonNode;
			this._typamaticTimer = dojo.lang.setTimeout(this, "_buttonPressed", this._currentTimeout, this._eventCount, buttonNode);
			this._currentTimeout = Math.round(this._currentTimeout * this.timeoutChangeRate);
			return false;
		},

		_bottomButtonPressed: function(/*Event*/ evt){
			return this._buttonPressed(evt,this.bottomButtonNode);
		},

		// IE sends these events when rapid clicking, mimic an extra single click
		_bottomButtonDoubleClicked: function(/*Event*/ evt){
			var rc = this._bottomButtonPressed(evt);
			dojo.lang.setTimeout( this, "_buttonReleased", 50, null);
			return rc;
		},

		_topButtonPressed: function(/*Event*/ evt){
			return this._buttonPressed(evt,this.topButtonNode);
		},

		// IE sends these events when rapid clicking, mimic an extra single click
		_topButtonDoubleClicked: function(/*Event*/ evt){
			var rc = this._topButtonPressed(evt);
			dojo.lang.setTimeout( this, "_buttonReleased", 50, null);
			return rc;
		},

		_leftButtonPressed: function(/*Event*/ evt){
			return this._buttonPressed(evt,this.leftButtonNode);
		},

		// IE sends these events when rapid clicking, mimic an extra single click
		_leftButtonDoubleClicked: function(/*Event*/ evt){
			var rc = this._leftButtonPressed(evt);
			dojo.lang.setTimeout( this, "_buttonReleased", 50, null);
			return rc;
		},

		_rightButtonPressed: function(/*Event*/ evt){
			return this._buttonPressed(evt,this.rightButtonNode);
		},

		// IE sends these events when rapid clicking, mimic an extra single click
		_rightButtonDoubleClicked: function(/*Event*/ evt){
			var rc = this._rightButtonPressed(evt);
			dojo.lang.setTimeout( this, "_buttonReleased", 50, null);
			return rc;
		},

		_buttonReleased: function(/*Event*/ evt){
			if(typeof evt == "object" && evt != null && typeof evt.keyCode != "undefined" && evt.keyCode != null){
				var keyCode = evt.keyCode;

				switch(keyCode){
					case evt.KEY_LEFT_ARROW:
					case evt.KEY_RIGHT_ARROW:
					case evt.KEY_DOWN_ARROW:
					case evt.KEY_UP_ARROW:
						dojo.event.browser.stopEvent(evt);
						break;
				}
			}
			this._releaseButton(this.topButtonNode);
			this._releaseButton(this.bottomButtonNode);
			this._releaseButton(this.leftButtonNode);
			this._releaseButton(this.rightButtonNode);
			this._eventCount++;
			if(this._typamaticTimer != null){
				clearTimeout(this._typamaticTimer);
			}
			this._typamaticTimer = null;
			this._currentTimeout = this.defaultTimeout;
		},

		_mouseWheeled: function(/*Event*/ evt){
			var scrollAmount = 0;
			if(typeof evt.wheelDelta == 'number'){ // IE
				scrollAmount = evt.wheelDelta;
			}else if (typeof evt.detail == 'number'){ // Mozilla+Firefox
				scrollAmount = -evt.detail;
			}
			if (this.isEnableY){
				if(scrollAmount > 0){
					this._topButtonPressed(evt);
					this._buttonReleased(evt);
				}else if (scrollAmount < 0){
					this._bottomButtonPressed(evt);
					this._buttonReleased(evt);
				}
			} else if (this.isEnableX){
				if(scrollAmount > 0){
					this._rightButtonPressed(evt);
					this._buttonReleased(evt);
				}else if (scrollAmount < 0){
					this._leftButtonPressed(evt);
					this._buttonReleased(evt);
				}
			}
		},

		_discardEvent: function(/*Event*/ evt){
			dojo.event.browser.stopEvent(evt);
		},

		_setFocus: function(){
			if (this.focusNode.focus){
				this.focusNode.focus();
			}
		},

		// This function is called when the template is loaded
		fillInTemplate: function (/*Object*/ args, /*Object*/ frag) 
		{
			var source = this.getFragNodeRef(frag);
			dojo.html.copyStyle(this.domNode, source);
			// the user's style for the widget might include border and padding
			// unfortunately, border isn't supported for inline elements
			// so I get to fake everyone out by setting the border and padding
			// of the outer table cells
			var padding = this.domNode.style.padding;
			if (dojo.lang.isString(padding) && padding != "" && padding != "0px" && padding != "0px 0px 0px 0px"){
				this.topBorderNode.style.padding = 
					this.bottomBorderNode.style.padding = padding;
				this.topBorderNode.style.paddingBottom = "0px";
				this.bottomBorderNode.style.paddingTop = "0px";
				this.rightBorderNode.style.paddingRight = this.domNode.style.paddingRight;
				this.leftBorderNode.style.paddingLeft= this.domNode.style.paddingLeft;
				this.domNode.style.padding = "0px 0px 0px 0px";
			}
			var borderWidth = this.domNode.style.borderWidth;
			if (dojo.lang.isString(borderWidth) && borderWidth != "" && borderWidth != "0px" && borderWidth != "0px 0px 0px 0px"){
				this.topBorderNode.style.borderStyle = 
					this.rightBorderNode.style.borderStyle = 
					this.bottomBorderNode.style.borderStyle = 
					this.leftBorderNode.style.borderStyle = 
						this.domNode.style.borderStyle;
				this.topBorderNode.style.borderColor = 
					this.rightBorderNode.style.borderColor = 
					this.bottomBorderNode.style.borderColor = 
					this.leftBorderNode.style.borderColor = 
						this.domNode.style.borderColor;
				this.topBorderNode.style.borderWidth = 
					this.bottomBorderNode.style.borderWidth = borderWidth;
				this.topBorderNode.style.borderBottomWidth = "0px";
				this.bottomBorderNode.style.borderTopWidth = "0px";
				this.rightBorderNode.style.borderRightWidth = this.domNode.style.borderRightWidth;
				this.leftBorderNode.style.borderLeftWidth = this.domNode.style.borderLeftWidth;
				this.domNode.style.borderWidth = "0px 0px 0px 0px";
			}

			// dojo.debug ("fillInTemplate - className = " + this.domNode.className);

			// setup drag-n-drop for the sliderHandle
			this._handleMove = new dojo.widget._SliderDragMoveSource (this.sliderHandleNode);
			this._handleMove.setParent (this);

			if (this.clickSelect){
				dojo.event.connect (this.constrainingContainerNode, "onmousedown", this, "_onClick");
			} 

			if (this.isEnableX){
				this.setValueX (!isNaN(this.initialValueX) ? this.initialValueX : (!isNaN(this.minimumX) ? this.minimumX : 0));
			}
			if (!this.isEnableX || !this.showButtons){
				this.rightButtonNode.style.width = "1px"; // allow the border to show
				this.rightButtonNode.style.visibility = "hidden";
				this.leftButtonNode.style.width = "1px"; // allow the border to show
				this.leftButtonNode.style.visibility = "hidden";
			}
			if (this.isEnableY){
				this.setValueY (!isNaN(this.initialValueY) ? this.initialValueY : (!isNaN(this.minimumY) ? this.minimumY : 0));
			}
			if (!this.isEnableY || !this.showButtons){
				this.bottomButtonNode.style.width = "1px"; // allow the border to show
				this.bottomButtonNode.style.visibility = "hidden";
				this.topButtonNode.style.width = "1px"; // allow the border to show
				this.topButtonNode.style.visibility = "hidden";
			}
			if(this.focusNode.addEventListener){
				// dojo.event.connect() doesn't seem to work with DOMMouseScroll
				this.focusNode.addEventListener('DOMMouseScroll', dojo.lang.hitch(this, "_mouseWheeled"), false); // Mozilla + Firefox + Netscape
			}
		},

		// move the X value to the closest allowable value
		_snapX: function(/*Number*/ x){
			if (x < 0){ x = 0; }
			else if (x > this._constraintWidth){ x = this._constraintWidth; }
			else {
				var selectedValue = Math.round (x / this._valueSizeX);
				x = Math.round (selectedValue * this._valueSizeX);
			}
			this.sliderHandleNode.style.left = x + "px";
			if (this.flipX){
				this._clipLeft = x + this._clipXdelta;
			} else {
				this._clipRight = x + this._clipXdelta;
			}
			this.progressBackgroundNode.style.clip = "rect("+this._clipTop+"px,"+this._clipRight+"px,"+this._clipBottom+"px,"+this._clipLeft+"px)";
		},

		// compute _valueSizeX & _constraintWidth & default snapValuesX
		_calc_valueSizeX: function (){
			var constrainingCtrBox = dojo.html.getContentBox(this.constrainingContainerNode);
			var sliderHandleBox = dojo.html.getContentBox(this.sliderHandleNode);
			if (isNaN(constrainingCtrBox.width) || isNaN(sliderHandleBox.width) || constrainingCtrBox.width <= 0 || sliderHandleBox.width <= 0){ 
				return false; 
			}

			this._constraintWidth = constrainingCtrBox.width 
				+ dojo.html.getPadding(this.constrainingContainerNode).width
				- sliderHandleBox.width;

			if (this.flipX){
				this._clipLeft = this._clipRight = constrainingCtrBox.width;
			} else {
				this._clipLeft = this._clipRight = 0;
			}
			this._clipXdelta = sliderHandleBox.width >> 1;
			if (!this.isEnableY){
				this._clipTop = 0;
				this._clipBottom = constrainingCtrBox.height;
			}

			if (this._constraintWidth <= 0){ return false; }
			if (this.snapValuesX == 0){
				this.snapValuesX = this._constraintWidth + 1;
			}

			this._valueSizeX = this._constraintWidth / (this.snapValuesX - 1);
			return true;
		},

		// summary
		//	move the handle horizontally to the specified value
		setValueX: function (/*Number*/ value){
			if (0.0 == this._valueSizeX){
				if (this._calc_valueSizeX () == false){
					dojo.lang.setTimeout(this, "setValueX", 100, value);
					return;
				}
			}
			if (isNaN(value)){
				value = 0;
			}
			if (value > this.maximumX){
				value = this.maximumX;
			}
			else if (value < this.minimumX){
				value = this.minimumX;
			}
			var pixelPercent = (value-this.minimumX) / (this.maximumX-this.minimumX);
			if (this.flipX){
				pixelPercent = 1.0 - pixelPercent;
			}
			this._snapX (pixelPercent * this._constraintWidth);
			this.notifyListeners();
		},


		// summary
		//	return the X value that the matches the position of the handle
		getValueX: function (){
			var pixelPercent = dojo.html.getPixelValue (this.sliderHandleNode,"left") / this._constraintWidth;
			if (this.flipX){
				pixelPercent = 1.0 - pixelPercent;
			}
			return Math.round (pixelPercent * (this.snapValuesX-1)) * ((this.maximumX-this.minimumX) / (this.snapValuesX-1)) + this.minimumX;
		},

		// move the Y value to the closest allowable value
		_snapY: function (/*Number*/ y){
			if (y < 0){ y = 0; }
			else if (y > this._constraintHeight){ y = this._constraintHeight; }
			else {
				var selectedValue = Math.round (y / this._valueSizeY);
				y = Math.round (selectedValue * this._valueSizeY);
			}
			this.sliderHandleNode.style.top = y + "px";
			if (this.flipY){
				this._clipTop = y + this._clipYdelta;
			} else {
				this._clipBottom = y + this._clipYdelta;
			}
			this.progressBackgroundNode.style.clip = "rect("+this._clipTop+"px,"+this._clipRight+"px,"+this._clipBottom+"px,"+this._clipLeft+"px)";
		},
		// compute _valueSizeY & _constraintHeight & default snapValuesY
		_calc_valueSizeY: function (){
			var constrainingCtrBox = dojo.html.getContentBox(this.constrainingContainerNode);
			var sliderHandleBox = dojo.html.getContentBox(this.sliderHandleNode);
			if (isNaN(constrainingCtrBox.height) || isNaN(sliderHandleBox.height) || constrainingCtrBox.height <= 0 || sliderHandleBox.height <= 0){ 
				return false; 
			}

			this._constraintHeight = constrainingCtrBox.height
				+ dojo.html.getPadding(this.constrainingContainerNode).height
				- sliderHandleBox.height;

			if (this.flipY){
				this._clipTop = this._clipBottom = constrainingCtrBox.height;
			} else {
				this._clipTop = this._clipBottom = 0;
			}
			this._clipYdelta = sliderHandleBox.height >> 1;
			if (!this.isEnableX){
				this._clipLeft = 0;
				this._clipRight = constrainingCtrBox.width;
			}

			if (this._constraintHeight <= 0){ return false; }
			if (this.snapValuesY == 0){
				this.snapValuesY = this._constraintHeight + 1;
			}

			this._valueSizeY = this._constraintHeight / (this.snapValuesY - 1);
			return true;
		},

		// summary
		//	move the handle vertically to the specified value
		setValueY: function (/*Number*/ value){
			if (0.0 == this._valueSizeY){
				if (this._calc_valueSizeY () == false){
					dojo.lang.setTimeout(this, "setValueY", 100, value);
					return;
				}
			}
			if (isNaN(value)){
				value = 0;
			}
			if (value > this.maximumY){
				value = this.maximumY;
			}
			else if (value < this.minimumY){
				value = this.minimumY;
			}
			var pixelPercent = (value-this.minimumY) / (this.maximumY-this.minimumY);
			if (this.flipY){
				pixelPercent = 1.0 - pixelPercent;
			}
			this._snapY (pixelPercent * this._constraintHeight);
			this.notifyListeners();
		},

		// summary
		//	return the Y value that the matches the position of the handle
		getValueY: function (){
			var pixelPercent = dojo.html.getPixelValue (this.sliderHandleNode,"top") / this._constraintHeight;
			if (this.flipY){
				pixelPercent = 1.0 - pixelPercent;
			}
			return Math.round (pixelPercent * (this.snapValuesY-1)) * ((this.maximumY-this.minimumY) / (this.snapValuesY-1)) + this.minimumY;
		},

		// set the position of the handle
		_onClick: function(/*Event*/ evt){
			if (this._isDragInProgress){
				return;
			}

			var parent = dojo.html.getAbsolutePosition(this.constrainingContainerNode, true, dojo.html.boxSizing.MARGIN_BOX);
			var content = dojo.html.getContentBox(this._handleMove.domNode);			
			if (this.isEnableX){
				var x = evt.pageX - parent.x - (content.width >> 1);
				this._snapX(x);
			}
			if (this.isEnableY){
				var y = evt.pageY - parent.y - (content.height >> 1);
				this._snapY(y);
			}
			this.notifyListeners();
		},

		// summary
		//	method to invoke user's onValueChanged method
		notifyListeners: function(){
			this.onValueChanged(this.getValueX(), this.getValueY());
		},

		// summary
		//	empty method to be overridden by user
		onValueChanged: function(/*Number*/ x, /*Number*/ y){
		}
	}
);



// summary
//	the horizontal slider widget subclass
dojo.widget.defineWidget (
	"dojo.widget.SliderHorizontal",
	dojo.widget.Slider,
	{
		widgetType: "SliderHorizontal",

		isEnableX: true,
		isEnableY: false,
		// Number
		//	sets initialValueX
		initialValue: "",
		// Number
		//	sets snapValuesX
		snapValues: "",
		// Number
		//	sets minimumX
		minimum: "",
		// Number
		//	sets maximumX
		maximum: "",
		// String
		//	sets buttonStyleX
		buttonStyle: "",
		backgroundSize: "height:10px;width:200px;",
		backgroundSrc: dojo.uri.dojoUri("src/widget/templates/images/slider-bg.gif"),
		// Boolean
		//	sets flipX
		flip: false,

		postMixInProperties: function(){
			dojo.widget.SliderHorizontal.superclass.postMixInProperties.apply(this, arguments);
			if (!isNaN(parseFloat(this.initialValue))){
				this.initialValueX = parseFloat(this.initialValue);
			}
			if (!isNaN(parseFloat(this.minimum))){
				this.minimumX = parseFloat(this.minimum);
			}
			if (!isNaN(parseFloat(this.maximum))){
				this.maximumX = parseFloat(this.maximum);
			}
			if (!isNaN(parseInt(this.snapValues))){
				this.snapValuesX = parseInt(this.snapValues);
			}
			if (dojo.lang.isString(this.buttonStyle) && this.buttonStyle != ""){
				this.buttonStyleX = this.buttonStyle;
			}
			if (dojo.lang.isBoolean(this.flip)){
				this.flipX = this.flip;
			}
		},

		notifyListeners: function(){
			this.onValueChanged(this.getValueX());
		},

		// summary
		//	wrapper for getValueX()
		getValue: function (){
			return this.getValueX ();
		},

		// summary
		//	wrapper for setValueX()
		setValue: function (/*Number*/ value){
			this.setValueX (value);
		},

		onValueChanged: function(/*Number*/ value){
		}
	}
);


/* ------------------------------------------------------------------------- */


// summary
//	the vertical slider widget subclass
dojo.widget.defineWidget (
	"dojo.widget.SliderVertical",
	dojo.widget.Slider,
	{
		widgetType: "SliderVertical",

		isEnableX: false,
		isEnableY: true,
		// Number
		//	sets initialValueY
		initialValue: "",
		// Number
		//	sets snapValuesY
		snapValues: "",
		// Number
		//	sets minimumY
		minimum: "",
		// Number
		//	sets maximumY
		maximum: "",
		// String
		//	sets buttonStyleY
		buttonStyle: "",
		backgroundSize: "width:10px;height:200px;",
		backgroundSrc: dojo.uri.dojoUri("src/widget/templates/images/slider-bg-vert.gif"),
		// Boolean
		//	sets flipY
		flip: false,

		postMixInProperties: function(){
			dojo.widget.SliderVertical.superclass.postMixInProperties.apply(this, arguments);
			if (!isNaN(parseFloat(this.initialValue))){
				this.initialValueY = parseFloat(this.initialValue);
			}
			if (!isNaN(parseFloat(this.minimum))){
				this.minimumY = parseFloat(this.minimum);
			}
			if (!isNaN(parseFloat(this.maximum))){
				this.maximumY = parseFloat(this.maximum);
			}
			if (!isNaN(parseInt(this.snapValues))){
				this.snapValuesY = parseInt(this.snapValues);
			}
			if (dojo.lang.isString(this.buttonStyle) && this.buttonStyle != ""){
				this.buttonStyleY = this.buttonStyle;
			}
			if (dojo.lang.isBoolean(this.flip)){
				this.flipY = this.flip;
			}
		},

		notifyListeners: function(){
			this.onValueChanged(this.getValueY());
		},

		// summary
		//	wrapper for getValueY()
		getValue: function (){
			return this.getValueY ();
		},

		// summary
		//	wrapper for setValueY()
		setValue: function (/*Number*/ value){
			this.setValueY (value);
		},

		onValueChanged: function(/*Number*/ value){
		}
	}
);


/* ------------------------------------------------------------------------- */


/**
 * This class extends the HtmlDragMoveSource class to provide
 * features for the slider handle.
 */
dojo.declare (
	"dojo.widget._SliderDragMoveSource",
	dojo.dnd.HtmlDragMoveSource,
{
	slider: null,

	/** Setup the handle for drag
	 *  Extends dojo.dnd.HtmlDragMoveSource by creating a SliderDragMoveSource */
	onDragStart: function(/*Event*/ evt){
		this.slider._isDragInProgress = true;
		var pos = dojo.html.getAbsolutePosition(this.slider.constrainingContainerNode, true, dojo.html.boxSizing.MARGIN_BOX);
		if (this.slider.isEnableX){
			this.slider._minX = pos.x;
		}
		if (this.slider.isEnableY){
			this.slider._minY = pos.y;
		}

		var dragObj = this.createDragMoveObject ();

		this.slider.notifyListeners();
		return dragObj;
	},

	onDragEnd: function(/*Event*/ evt){
		this.slider._isDragInProgress = false;
		this.slider.notifyListeners();
	},

	createDragMoveObject: function (){
		//dojo.debug ("SliderDragMoveSource#createDragMoveObject - " + this.slider);
		var dragObj = new dojo.widget._SliderDragMoveObject (this.dragObject, this.type);
		dragObj.slider = this.slider;

		// this code copied from dojo.dnd.HtmlDragSource#onDragStart
		if (this.dragClass){ 
			dragObj.dragClass = this.dragClass; 
		}

		return dragObj;
	},


	setParent: function (/*Widget*/ slider){
		this.slider = slider;
	}
});


/* ------------------------------------------------------------------------- */


/**
 * This class extends the HtmlDragMoveObject class to provide
 * features for the slider handle.
 */
dojo.declare (
	"dojo.widget._SliderDragMoveObject",
	dojo.dnd.HtmlDragMoveObject,
{
	// reference to dojo.widget.Slider
	slider: null,

	/** Moves the node to follow the mouse.
	 *  Extends functon HtmlDragObject by adding functionality to snap handle
	 *  to a discrete value */
	onDragMove: function(/*Event*/ evt){
		this.updateDragOffset ();

		if (this.slider.isEnableX){
			var x = this.dragOffset.x + evt.pageX - this.slider._minX;
			this.slider._snapX(x);
		}

		if (this.slider.isEnableY){
			var y = this.dragOffset.y + evt.pageY - this.slider._minY;
			this.slider._snapY(y);
		}
		if(this.slider.activeDrag){
			this.slider.notifyListeners();
		}
	}
});
