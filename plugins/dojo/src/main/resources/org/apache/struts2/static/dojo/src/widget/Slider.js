/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.Slider");
dojo.require("dojo.event.*");
dojo.require("dojo.dnd.*");
dojo.require("dojo.dnd.HtmlDragMove");
dojo.require("dojo.widget.*");
dojo.require("dojo.html.layout");
dojo.widget.defineWidget("dojo.widget.Slider", dojo.widget.HtmlWidget, {minimumX:0, minimumY:0, maximumX:10, maximumY:10, snapValuesX:0, snapValuesY:0, _snapToGrid:true, isEnableX:true, isEnableY:true, _valueSizeX:0, _valueSizeY:0, _minX:0, _minY:0, _constraintWidth:0, _constraintHeight:0, _clipLeft:0, _clipRight:0, _clipTop:0, _clipBottom:0, _clipXdelta:0, _clipYdelta:0, initialValueX:0, initialValueY:0, flipX:false, flipY:false, clickSelect:true, activeDrag:false, templateCssString:".sliderMain {\n  border: 0px !important;\n  border-spacing: 0px !important;\n  line-height: 0px !important;\n  padding: 0px !important;\n  display: -moz-inline-table !important;\n  display: inline !important;\n  -moz-user-focus: normal !important;\n}\n\n.sliderComponent {\n  border: 0px;\n  padding: 0px;\n  margin: 0px;\n}\n\n.sliderHandle { \n  top: 0px;\n  left: 0px;\n  z-index: 1000;\n  position: absolute !important;\n}\n\n.sliderOutsetButton { \n  border-style: outset;\n  border-width: 0px 1px 1px 0px;\n  border-color: black;\n}\n\n.sliderInsetButton { \n  border-style: inset;\n  border-width: 1px 0px 0px 1px;\n  border-color: black;\n}\n\n.sliderButtonY {\n  margin: 0px;\n  padding: 0px;\n  z-index: 900;\n}\n\n.sliderButtonX {\n  margin: 0px;\n  padding: 0px;\n  z-index: 900;\n}\n\n.sliderBackground { \n  z-index: 0;\n  display: block !important;\n  position: relative !important;\n}\n\n.sliderProgressBackground { \n  z-index: 800;\n  position: absolute !important;\n  clip: rect(0px,0px,0px,0px);\n}\n\n.sliderBackgroundSizeOnly { \n}\n", templateCssPath:dojo.uri.moduleUri("dojo.widget", "templates/Slider.css"), templateString:"<table _=\"weird end tag formatting is to prevent whitespace from becoming &nbsp;\" \n\tclass=\"sliderMain\" \n\tdojoAttachPoint=\"focusNode\" \n\tdojoAttachEvent=\"onmousedown:_setFocus; onkey:_handleKeyEvents; onkeyup:_buttonReleased; onmouseup:_buttonReleased; onmousewheel:_mouseWheeled;\"\n\ttabindex=\"0\" cols=3 cellpadding=0 cellspacing=0 style=\"\">\n\t<tr valign=middle align=center>\n\t\t<td class=\"sliderComponent\" colspan=3 dojoAttachPoint=topBorderNode style=\"\"\n\t\t\t><img class=\"sliderOutsetButton sliderButtonY\"\n\t\t\t\tdojoAttachPoint=topButtonNode \n\t\t\t\tdojoAttachEvent=\"onmousedown:_topButtonPressed; onmousemove:_discardEvent; ondblclick:_topButtonDoubleClicked;\"\n\t\t\t\tsrc=\"${this.topButtonSrc}\" \n\t\t\t\tstyle=\"${this.buttonStyleY}\"\n\t\t></td>\n\t</tr>\n\t<tr valign=middle align=center>\n\t\t<td class=\"sliderComponent\" dojoAttachPoint=leftBorderNode style=\"\"\n\t\t\t><img class=\"sliderOutsetButton sliderButtonX\"\n\t\t\t\tdojoAttachPoint=leftButtonNode\n\t\t\t\tdojoAttachEvent=\"onmousedown:_leftButtonPressed; onmousemove:_discardEvent; ondblclick:_leftButtonDoubleClicked;\"\n\t\t\t\tsrc=\"${this.leftButtonSrc}\" \n\t\t\t\tstyle=\"${this.buttonStyleX}\"\n\t\t></td>\n\t\t<td dojoAttachPoint=constrainingContainerNode \n\t\t\tclass=\"sliderComponent sliderBackground\"\n\t\t\tstyle=\"${this.backgroundStyle}\"\n\t\t\t><img src=\"${this.handleSrc}\" \n\t\t\t\tclass=sliderHandle\n\t\t\t\tdojoAttachPoint=sliderHandleNode\n\t\t\t\tstyle=\"${this.handleStyle}\"\n\t\t\t><img src=\"${this.progressBackgroundSrc}\"\n\t\t\t\tclass=\"sliderBackgroundSizeOnly sliderProgressBackground\"\n\t\t\t\tdojoAttachPoint=progressBackgroundNode\n\t\t\t\tstyle=\"${this.backgroundSize}\"\n\t\t\t><img src=\"${this.backgroundSrc}\" \n\t\t\t\tclass=sliderBackgroundSizeOnly\n\t\t\t\tdojoAttachPoint=sliderBackgroundNode\n\t\t\t\tstyle=\"${this.backgroundSize}\"\n\t\t></td>\n\t\t<td class=\"sliderComponent\" dojoAttachPoint=rightBorderNode style=\"\"\n\t\t\t><img class=\"sliderOutsetButton sliderButtonX\"\n\t\t\t\tdojoAttachPoint=rightButtonNode\n\t\t\t\tdojoAttachEvent=\"onmousedown:_rightButtonPressed; onmousemove:_discardEvent; ondblclick:_rightButtonDoubleClicked;\"\n\t\t\t\tsrc=\"${this.rightButtonSrc}\" \n\t\t\t\tstyle=\"${this.buttonStyleX}\"\n\t\t></td>\n\t</tr>\n\t<tr valign=middle align=center>\n\t\t<td class=\"sliderComponent\" colspan=3 dojoAttachPoint=bottomBorderNode style=\"\"\n\t\t\t><img class=\"sliderOutsetButton sliderButtonY\"\n\t\t\t\tdojoAttachPoint=bottomButtonNode \n\t\t\t\tdojoAttachEvent=\"onmousedown:_bottomButtonPressed; onmousemove:_discardEvent; ondblclick:_bottomButtonDoubleClicked;\"\n\t\t\t\tsrc=\"${this.bottomButtonSrc}\" \n\t\t\t\tstyle=\"${this.buttonStyleY}\"\n\t\t></td>\n\t</tr>\n</table>\n", _isDragInProgress:false, bottomButtonSrc:dojo.uri.moduleUri("dojo.widget", "templates/images/slider_down_arrow.png"), topButtonSrc:dojo.uri.moduleUri("dojo.widget", "templates/images/slider_up_arrow.png"), leftButtonSrc:dojo.uri.moduleUri("dojo.widget", "templates/images/slider_left_arrow.png"), rightButtonSrc:dojo.uri.moduleUri("dojo.widget", "templates/images/slider_right_arrow.png"), backgroundSrc:dojo.uri.moduleUri("dojo.widget", "templates/images/blank.gif"), progressBackgroundSrc:dojo.uri.moduleUri("dojo.widget", "templates/images/blank.gif"), backgroundSize:"width:200px;height:200px;", backgroundStyle:"", buttonStyleX:"", buttonStyleY:"", handleStyle:"", handleSrc:dojo.uri.moduleUri("dojo.widget", "templates/images/slider-button.png"), showButtons:true, _eventCount:0, _typamaticTimer:null, _typamaticFunction:null, defaultTimeout:500, timeoutChangeRate:0.9, _currentTimeout:this.defaultTimeout, _handleKeyEvents:function (evt) {
	if (!evt.key) {
		return;
	}
	if (!evt.ctrlKey && !evt.altKey) {
		switch (evt.key) {
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
}, _pressButton:function (buttonNode) {
	buttonNode.className = buttonNode.className.replace("Outset", "Inset");
}, _releaseButton:function (buttonNode) {
	buttonNode.className = buttonNode.className.replace("Inset", "Outset");
}, _buttonPressed:function (evt, buttonNode) {
	this._setFocus();
	if (typeof evt == "object") {
		if (this._typamaticTimer != null) {
			if (this._typamaticNode == buttonNode) {
				return;
			}
			clearTimeout(this._typamaticTimer);
		}
		this._buttonReleased(null);
		this._eventCount++;
		this._typamaticTimer = null;
		this._currentTimeout = this.defaultTimeout;
		dojo.event.browser.stopEvent(evt);
	} else {
		if (evt != this._eventCount) {
			this._buttonReleased(null);
			return false;
		}
	}
	if (buttonNode == this.leftButtonNode && this.isEnableX) {
		this._snapX(dojo.html.getPixelValue(this.sliderHandleNode, "left") - this._valueSizeX);
	} else {
		if (buttonNode == this.rightButtonNode && this.isEnableX) {
			this._snapX(dojo.html.getPixelValue(this.sliderHandleNode, "left") + this._valueSizeX);
		} else {
			if (buttonNode == this.topButtonNode && this.isEnableY) {
				this._snapY(dojo.html.getPixelValue(this.sliderHandleNode, "top") - this._valueSizeY);
			} else {
				if (buttonNode == this.bottomButtonNode && this.isEnableY) {
					this._snapY(dojo.html.getPixelValue(this.sliderHandleNode, "top") + this._valueSizeY);
				} else {
					return false;
				}
			}
		}
	}
	this._pressButton(buttonNode);
	this.notifyListeners();
	this._typamaticNode = buttonNode;
	this._typamaticTimer = dojo.lang.setTimeout(this, "_buttonPressed", this._currentTimeout, this._eventCount, buttonNode);
	this._currentTimeout = Math.round(this._currentTimeout * this.timeoutChangeRate);
	return false;
}, _bottomButtonPressed:function (evt) {
	return this._buttonPressed(evt, this.bottomButtonNode);
}, _bottomButtonDoubleClicked:function (evt) {
	var rc = this._bottomButtonPressed(evt);
	dojo.lang.setTimeout(this, "_buttonReleased", 50, null);
	return rc;
}, _topButtonPressed:function (evt) {
	return this._buttonPressed(evt, this.topButtonNode);
}, _topButtonDoubleClicked:function (evt) {
	var rc = this._topButtonPressed(evt);
	dojo.lang.setTimeout(this, "_buttonReleased", 50, null);
	return rc;
}, _leftButtonPressed:function (evt) {
	return this._buttonPressed(evt, this.leftButtonNode);
}, _leftButtonDoubleClicked:function (evt) {
	var rc = this._leftButtonPressed(evt);
	dojo.lang.setTimeout(this, "_buttonReleased", 50, null);
	return rc;
}, _rightButtonPressed:function (evt) {
	return this._buttonPressed(evt, this.rightButtonNode);
}, _rightButtonDoubleClicked:function (evt) {
	var rc = this._rightButtonPressed(evt);
	dojo.lang.setTimeout(this, "_buttonReleased", 50, null);
	return rc;
}, _buttonReleased:function (evt) {
	if (typeof evt == "object" && evt != null && typeof evt.keyCode != "undefined" && evt.keyCode != null) {
		var keyCode = evt.keyCode;
		switch (keyCode) {
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
	if (this._typamaticTimer != null) {
		clearTimeout(this._typamaticTimer);
	}
	this._typamaticTimer = null;
	this._currentTimeout = this.defaultTimeout;
}, _mouseWheeled:function (evt) {
	var scrollAmount = 0;
	if (typeof evt.wheelDelta == "number") {
		scrollAmount = evt.wheelDelta;
	} else {
		if (typeof evt.detail == "number") {
			scrollAmount = -evt.detail;
		}
	}
	if (this.isEnableY) {
		if (scrollAmount > 0) {
			this._topButtonPressed(evt);
			this._buttonReleased(evt);
		} else {
			if (scrollAmount < 0) {
				this._bottomButtonPressed(evt);
				this._buttonReleased(evt);
			}
		}
	} else {
		if (this.isEnableX) {
			if (scrollAmount > 0) {
				this._rightButtonPressed(evt);
				this._buttonReleased(evt);
			} else {
				if (scrollAmount < 0) {
					this._leftButtonPressed(evt);
					this._buttonReleased(evt);
				}
			}
		}
	}
}, _discardEvent:function (evt) {
	dojo.event.browser.stopEvent(evt);
}, _setFocus:function () {
	if (this.focusNode.focus) {
		this.focusNode.focus();
	}
}, fillInTemplate:function (args, frag) {
	var source = this.getFragNodeRef(frag);
	dojo.html.copyStyle(this.domNode, source);
	var padding = this.domNode.style.padding;
	if (dojo.lang.isString(padding) && padding != "" && padding != "0px" && padding != "0px 0px 0px 0px") {
		this.topBorderNode.style.padding = this.bottomBorderNode.style.padding = padding;
		this.topBorderNode.style.paddingBottom = "0px";
		this.bottomBorderNode.style.paddingTop = "0px";
		this.rightBorderNode.style.paddingRight = this.domNode.style.paddingRight;
		this.leftBorderNode.style.paddingLeft = this.domNode.style.paddingLeft;
		this.domNode.style.padding = "0px 0px 0px 0px";
	}
	var borderWidth = this.domNode.style.borderWidth;
	if (dojo.lang.isString(borderWidth) && borderWidth != "" && borderWidth != "0px" && borderWidth != "0px 0px 0px 0px") {
		this.topBorderNode.style.borderStyle = this.rightBorderNode.style.borderStyle = this.bottomBorderNode.style.borderStyle = this.leftBorderNode.style.borderStyle = this.domNode.style.borderStyle;
		this.topBorderNode.style.borderColor = this.rightBorderNode.style.borderColor = this.bottomBorderNode.style.borderColor = this.leftBorderNode.style.borderColor = this.domNode.style.borderColor;
		this.topBorderNode.style.borderWidth = this.bottomBorderNode.style.borderWidth = borderWidth;
		this.topBorderNode.style.borderBottomWidth = "0px";
		this.bottomBorderNode.style.borderTopWidth = "0px";
		this.rightBorderNode.style.borderRightWidth = this.domNode.style.borderRightWidth;
		this.leftBorderNode.style.borderLeftWidth = this.domNode.style.borderLeftWidth;
		this.domNode.style.borderWidth = "0px 0px 0px 0px";
	}
	this._handleMove = new dojo.widget._SliderDragMoveSource(this.sliderHandleNode);
	this._handleMove.setParent(this);
	if (this.clickSelect) {
		dojo.event.connect(this.constrainingContainerNode, "onmousedown", this, "_onClick");
	}
	if (this.isEnableX) {
		this.setValueX(!isNaN(this.initialValueX) ? this.initialValueX : (!isNaN(this.minimumX) ? this.minimumX : 0));
	}
	if (!this.isEnableX || !this.showButtons) {
		this.rightButtonNode.style.width = "1px";
		this.rightButtonNode.style.visibility = "hidden";
		this.leftButtonNode.style.width = "1px";
		this.leftButtonNode.style.visibility = "hidden";
	}
	if (this.isEnableY) {
		this.setValueY(!isNaN(this.initialValueY) ? this.initialValueY : (!isNaN(this.minimumY) ? this.minimumY : 0));
	}
	if (!this.isEnableY || !this.showButtons) {
		this.bottomButtonNode.style.width = "1px";
		this.bottomButtonNode.style.visibility = "hidden";
		this.topButtonNode.style.width = "1px";
		this.topButtonNode.style.visibility = "hidden";
	}
	if (this.focusNode.addEventListener) {
		this.focusNode.addEventListener("DOMMouseScroll", dojo.lang.hitch(this, "_mouseWheeled"), false);
	}
}, _snapX:function (x) {
	if (x < 0) {
		x = 0;
	} else {
		if (x > this._constraintWidth) {
			x = this._constraintWidth;
		} else {
			var selectedValue = Math.round(x / this._valueSizeX);
			x = Math.round(selectedValue * this._valueSizeX);
		}
	}
	this.sliderHandleNode.style.left = x + "px";
	if (this.flipX) {
		this._clipLeft = x + this._clipXdelta;
	} else {
		this._clipRight = x + this._clipXdelta;
	}
	this.progressBackgroundNode.style.clip = "rect(" + this._clipTop + "px," + this._clipRight + "px," + this._clipBottom + "px," + this._clipLeft + "px)";
}, _calc_valueSizeX:function () {
	var constrainingCtrBox = dojo.html.getContentBox(this.constrainingContainerNode);
	var sliderHandleBox = dojo.html.getContentBox(this.sliderHandleNode);
	if (isNaN(constrainingCtrBox.width) || isNaN(sliderHandleBox.width) || constrainingCtrBox.width <= 0 || sliderHandleBox.width <= 0) {
		return false;
	}
	this._constraintWidth = constrainingCtrBox.width + dojo.html.getPadding(this.constrainingContainerNode).width - sliderHandleBox.width;
	if (this.flipX) {
		this._clipLeft = this._clipRight = constrainingCtrBox.width;
	} else {
		this._clipLeft = this._clipRight = 0;
	}
	this._clipXdelta = sliderHandleBox.width >> 1;
	if (!this.isEnableY) {
		this._clipTop = 0;
		this._clipBottom = constrainingCtrBox.height;
	}
	if (this._constraintWidth <= 0) {
		return false;
	}
	if (this.snapValuesX == 0) {
		this.snapValuesX = this._constraintWidth + 1;
	}
	this._valueSizeX = this._constraintWidth / (this.snapValuesX - 1);
	return true;
}, setValueX:function (value) {
	if (0 == this._valueSizeX) {
		if (this._calc_valueSizeX() == false) {
			dojo.lang.setTimeout(this, "setValueX", 100, value);
			return;
		}
	}
	if (isNaN(value)) {
		value = 0;
	}
	if (value > this.maximumX) {
		value = this.maximumX;
	} else {
		if (value < this.minimumX) {
			value = this.minimumX;
		}
	}
	var pixelPercent = (value - this.minimumX) / (this.maximumX - this.minimumX);
	if (this.flipX) {
		pixelPercent = 1 - pixelPercent;
	}
	this._snapX(pixelPercent * this._constraintWidth);
	this.notifyListeners();
}, getValueX:function () {
	var pixelPercent = dojo.html.getPixelValue(this.sliderHandleNode, "left") / this._constraintWidth;
	if (this.flipX) {
		pixelPercent = 1 - pixelPercent;
	}
	return Math.round(pixelPercent * (this.snapValuesX - 1)) * ((this.maximumX - this.minimumX) / (this.snapValuesX - 1)) + this.minimumX;
}, _snapY:function (y) {
	if (y < 0) {
		y = 0;
	} else {
		if (y > this._constraintHeight) {
			y = this._constraintHeight;
		} else {
			var selectedValue = Math.round(y / this._valueSizeY);
			y = Math.round(selectedValue * this._valueSizeY);
		}
	}
	this.sliderHandleNode.style.top = y + "px";
	if (this.flipY) {
		this._clipTop = y + this._clipYdelta;
	} else {
		this._clipBottom = y + this._clipYdelta;
	}
	this.progressBackgroundNode.style.clip = "rect(" + this._clipTop + "px," + this._clipRight + "px," + this._clipBottom + "px," + this._clipLeft + "px)";
}, _calc_valueSizeY:function () {
	var constrainingCtrBox = dojo.html.getContentBox(this.constrainingContainerNode);
	var sliderHandleBox = dojo.html.getContentBox(this.sliderHandleNode);
	if (isNaN(constrainingCtrBox.height) || isNaN(sliderHandleBox.height) || constrainingCtrBox.height <= 0 || sliderHandleBox.height <= 0) {
		return false;
	}
	this._constraintHeight = constrainingCtrBox.height + dojo.html.getPadding(this.constrainingContainerNode).height - sliderHandleBox.height;
	if (this.flipY) {
		this._clipTop = this._clipBottom = constrainingCtrBox.height;
	} else {
		this._clipTop = this._clipBottom = 0;
	}
	this._clipYdelta = sliderHandleBox.height >> 1;
	if (!this.isEnableX) {
		this._clipLeft = 0;
		this._clipRight = constrainingCtrBox.width;
	}
	if (this._constraintHeight <= 0) {
		return false;
	}
	if (this.snapValuesY == 0) {
		this.snapValuesY = this._constraintHeight + 1;
	}
	this._valueSizeY = this._constraintHeight / (this.snapValuesY - 1);
	return true;
}, setValueY:function (value) {
	if (0 == this._valueSizeY) {
		if (this._calc_valueSizeY() == false) {
			dojo.lang.setTimeout(this, "setValueY", 100, value);
			return;
		}
	}
	if (isNaN(value)) {
		value = 0;
	}
	if (value > this.maximumY) {
		value = this.maximumY;
	} else {
		if (value < this.minimumY) {
			value = this.minimumY;
		}
	}
	var pixelPercent = (value - this.minimumY) / (this.maximumY - this.minimumY);
	if (this.flipY) {
		pixelPercent = 1 - pixelPercent;
	}
	this._snapY(pixelPercent * this._constraintHeight);
	this.notifyListeners();
}, getValueY:function () {
	var pixelPercent = dojo.html.getPixelValue(this.sliderHandleNode, "top") / this._constraintHeight;
	if (this.flipY) {
		pixelPercent = 1 - pixelPercent;
	}
	return Math.round(pixelPercent * (this.snapValuesY - 1)) * ((this.maximumY - this.minimumY) / (this.snapValuesY - 1)) + this.minimumY;
}, _onClick:function (evt) {
	if (this._isDragInProgress) {
		return;
	}
	var parent = dojo.html.getAbsolutePosition(this.constrainingContainerNode, true, dojo.html.boxSizing.MARGIN_BOX);
	var content = dojo.html.getContentBox(this._handleMove.domNode);
	if (this.isEnableX) {
		var x = evt.pageX - parent.x - (content.width >> 1);
		this._snapX(x);
	}
	if (this.isEnableY) {
		var y = evt.pageY - parent.y - (content.height >> 1);
		this._snapY(y);
	}
	this.notifyListeners();
}, notifyListeners:function () {
	this.onValueChanged(this.getValueX(), this.getValueY());
}, onValueChanged:function (x, y) {
}});
dojo.widget.defineWidget("dojo.widget.SliderHorizontal", dojo.widget.Slider, {isEnableX:true, isEnableY:false, initialValue:"", snapValues:"", minimum:"", maximum:"", buttonStyle:"", backgroundSize:"height:10px;width:200px;", backgroundSrc:dojo.uri.moduleUri("dojo.widget", "templates/images/slider-bg.gif"), flip:false, postMixInProperties:function () {
	dojo.widget.SliderHorizontal.superclass.postMixInProperties.apply(this, arguments);
	if (!isNaN(parseFloat(this.initialValue))) {
		this.initialValueX = parseFloat(this.initialValue);
	}
	if (!isNaN(parseFloat(this.minimum))) {
		this.minimumX = parseFloat(this.minimum);
	}
	if (!isNaN(parseFloat(this.maximum))) {
		this.maximumX = parseFloat(this.maximum);
	}
	if (!isNaN(parseInt(this.snapValues))) {
		this.snapValuesX = parseInt(this.snapValues);
	}
	if (dojo.lang.isString(this.buttonStyle) && this.buttonStyle != "") {
		this.buttonStyleX = this.buttonStyle;
	}
	if (dojo.lang.isBoolean(this.flip)) {
		this.flipX = this.flip;
	}
}, notifyListeners:function () {
	this.onValueChanged(this.getValueX());
}, getValue:function () {
	return this.getValueX();
}, setValue:function (value) {
	this.setValueX(value);
}, onValueChanged:function (value) {
}});
dojo.widget.defineWidget("dojo.widget.SliderVertical", dojo.widget.Slider, {isEnableX:false, isEnableY:true, initialValue:"", snapValues:"", minimum:"", maximum:"", buttonStyle:"", backgroundSize:"width:10px;height:200px;", backgroundSrc:dojo.uri.moduleUri("dojo.widget", "templates/images/slider-bg-vert.gif"), flip:false, postMixInProperties:function () {
	dojo.widget.SliderVertical.superclass.postMixInProperties.apply(this, arguments);
	if (!isNaN(parseFloat(this.initialValue))) {
		this.initialValueY = parseFloat(this.initialValue);
	}
	if (!isNaN(parseFloat(this.minimum))) {
		this.minimumY = parseFloat(this.minimum);
	}
	if (!isNaN(parseFloat(this.maximum))) {
		this.maximumY = parseFloat(this.maximum);
	}
	if (!isNaN(parseInt(this.snapValues))) {
		this.snapValuesY = parseInt(this.snapValues);
	}
	if (dojo.lang.isString(this.buttonStyle) && this.buttonStyle != "") {
		this.buttonStyleY = this.buttonStyle;
	}
	if (dojo.lang.isBoolean(this.flip)) {
		this.flipY = this.flip;
	}
}, notifyListeners:function () {
	this.onValueChanged(this.getValueY());
}, getValue:function () {
	return this.getValueY();
}, setValue:function (value) {
	this.setValueY(value);
}, onValueChanged:function (value) {
}});
dojo.declare("dojo.widget._SliderDragMoveSource", dojo.dnd.HtmlDragMoveSource, {slider:null, onDragStart:function (evt) {
	this.slider._isDragInProgress = true;
	var dragObj = this.createDragMoveObject();
	this.slider.notifyListeners();
	return dragObj;
}, onDragEnd:function (evt) {
	this.slider._isDragInProgress = false;
	this.slider.notifyListeners();
}, createDragMoveObject:function () {
	var dragObj = new dojo.widget._SliderDragMoveObject(this.dragObject, this.type);
	dragObj.slider = this.slider;
	if (this.dragClass) {
		dragObj.dragClass = this.dragClass;
	}
	return dragObj;
}, setParent:function (slider) {
	this.slider = slider;
}});
dojo.declare("dojo.widget._SliderDragMoveObject", dojo.dnd.HtmlDragMoveObject, {slider:null, onDragMove:function (evt) {
	this.updateDragOffset();
	if (this.slider.isEnableX) {
		var x = this.dragOffset.x + evt.pageX;
		this.slider._snapX(x);
	}
	if (this.slider.isEnableY) {
		var y = this.dragOffset.y + evt.pageY;
		this.slider._snapY(y);
	}
	if (this.slider.activeDrag) {
		this.slider.notifyListeners();
	}
}});

