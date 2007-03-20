/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.ProgressBar");

// requires here
dojo.require("dojo.widget.*"); // necessary
dojo.require("dojo.event");
dojo.require("dojo.dom.*");
dojo.require("dojo.html.style");
dojo.require("dojo.string.*");
dojo.require("dojo.lfx.*");

dojo.widget.defineWidget(
	"dojo.widget.ProgressBar",
	dojo.widget.HtmlWidget,
	{
		// Constructor arguments
		progressValue: "0",
		maxProgressValue: 100,
		width: 300,
		height: 30,
		frontPercentClass: "frontPercent",
		backPercentClass: "backPercent",
		frontBarClass: "frontBar",
		backBarClass: "backBar",
		hasText: "false",
		isVertical:"false",
		showOnlyIntegers: "false",
		dataSource: "",
		pollInterval: "3000",
		duration: "1000",
//		leftImage: null,
//		centerImage: null,
//		rightImage: null,
		templatePath: dojo.uri.dojoUri("src/widget/templates/ProgressBar.html"),
		templateCssPath: dojo.uri.dojoUri("src/widget/templates/ProgressBar.css"),
		
	
		// attach points
		containerNode: null,
		internalProgress: null,
	
		// private members
		_pixelUnitRatio: 0.0,
		// _pixelRatio := width/100
		_pixelPercentRatio: 0.0,
		_unitPercentRatio: 0.0,
		_unitPixelRatio: 0.0,
		_floatDimension: 0.0,
		_intDimension: 0,
		_progressPercentValue: "0%",
		_floatMaxProgressValue: 0.0,
		_dimension: "width",
		_pixelValue: 0,
		_oInterval: null,
		_animation: null,
		_animationStopped: true,
		_progressValueBak: false,
		_hasTextBak: false,
		// public functions
		fillInTemplate: function(args, frag){
			this.internalProgress.className = this.frontBarClass;
			this.containerNode.className = this.backBarClass;
			if (this.isVertical == "true"){
				this.internalProgress.style.bottom="0px";
				this.internalProgress.style.left="0px";
				this._dimension = "height";
			} else {
				this.internalProgress.style.top="0px";
				this.internalProgress.style.left="0px";
				this._dimension = "width";
			}
			this.frontPercentLabel.className = this.frontPercentClass;
			this.backPercentLabel.className = this.backPercentClass;
			this.progressValue = "" + this.progressValue; 
			this.domNode.style.height = this.height; 
			this.domNode.style.width = this.width;
			this._intDimension = parseInt("0" + eval("this." + this._dimension));
			this._floatDimension = parseFloat("0" + eval("this."+this._dimension));
			this._pixelPercentRatio = this._floatDimension/100;
			this.setMaxProgressValue(this.maxProgressValue, true);
			this.setProgressValue(dojo.string.trim(this.progressValue), true);
			dojo.debug("float dimension: " + this._floatDimension);
			dojo.debug("this._unitPixelRatio: " + this._unitPixelRatio);
			this.showText(this.hasText);
		},
		showText: function(visible){
			if (visible == "true"){
				this.backPercentLabel.style.display="block";
				this.frontPercentLabel.style.display="block";
			} else {
				this.backPercentLabel.style.display="none";
				this.frontPercentLabel.style.display="none";
			}
			this.hasText = visible;
		},
		postCreate: function(args, frag){
			// labels position
			this.render();
		},
		_backupValues: function(){
			this._progressValueBak = this.progressValue;
			this._hasTextBak = this.hasText;
		},
		_restoreValues: function(){
				this.setProgressValue(this._progressValueBak);
				this.showText(this._hasTextBak);
		},
		_setupAnimation: function(){
			var _self = this;
			dojo.debug("internalProgress width: " + this.internalProgress.style.width);
			this._animation = dojo.lfx.html.slideTo(this.internalProgress, 
				{top: 0, left: this.width-parseInt(this.internalProgress.style.width)}, parseInt(this.duration), null, 
					function(){
						var _backAnim = dojo.lfx.html.slideTo(_self.internalProgress, 
						{ top: 0, left: 0 }, parseInt(_self.duration));
						dojo.event.connect(_backAnim, "onEnd", function(){
							if (!_self._animationStopped){
								_self._animation.play();
							}
							});
						if (!_self._animationStopped){
							_backAnim.play();
						}
						_backAnim = null; // <-- to avoid memory leaks in IE
					}
				);
		},
		getMaxProgressValue: function(){
			return this.maxProgressValue;
		},
		setMaxProgressValue: function(maxValue, noRender){
			if (!this._animationStopped){
				return;
			}
			this.maxProgressValue = maxValue;
			this._floatMaxProgressValue = parseFloat("0" + this.maxProgressValue);
			this._pixelUnitRatio = this._floatDimension/this.maxProgressValue;
			this._unitPercentRatio = this._floatMaxProgressValue/100;
			this._unitPixelRatio = this._floatMaxProgressValue/this._floatDimension;
			this.setProgressValue(this.progressValue, true);
			if (!noRender){
				this.render();
			}
		},
		setProgressValue: function(value, noRender){
			if (!this._animationStopped){
				return;
			}
			// transformations here
			this._progressPercentValue = "0%";
			var _value=dojo.string.trim("" + value);
			var _floatValue = parseFloat("0" + _value);
			var _intValue = parseInt("0" + _value);
			var _pixelValue = 0;
			if (dojo.string.endsWith(_value, "%", false)){
				this._progressPercentValue = Math.min(_floatValue.toFixed(1), 100) + "%";
				_value = Math.min((_floatValue)*this._unitPercentRatio, this.maxProgressValue);
				_pixelValue = Math.min((_floatValue)*this._pixelPercentRatio, eval("this."+this._dimension));
			} else {
				this.progressValue = Math.min(_floatValue, this.maxProgressValue);
				this._progressPercentValue = Math.min((_floatValue/this._unitPercentRatio).toFixed(1), 100) + "%";
				_pixelValue = Math.min(_floatValue/this._unitPixelRatio, eval("this."+this._dimension));
			}
			this.progressValue = dojo.string.trim(_value);
			this._pixelValue = _pixelValue;
			if (!noRender){
				this.render();
			}
		},
		setCurrentPercentProgress: function(percentProgress){
			this._setCurrentPixelProgress(percentProgress);
		},
		getProgressValue: function(){
			return this.progressValue;
		},
		getProgressPercentValue: function(){
			return this._progressPercentValue;
		},
		setDataSource: function(dataSource){
			this.dataSource = dataSource;
		},
		setPollInterval: function(pollInterval){
			this.pollInterval = pollInterval;
		},
		start: function(){
			var _showFunction = dojo.lang.hitch(this, this._showRemoteProgress);
			this._oInterval = setInterval(_showFunction, this.pollInterval);
		},
		startAnimation: function(){
			if (this._animationStopped) {
				this._backupValues();
				this.setProgressValue("10%");
				this._animationStopped = false;
				this._setupAnimation();
				this.showText(false);
				this.internalProgress.style.height="105%";
				this._animation.play();
			}
		},
		stopAnimation: function(){
			if (this._animation) {
				this._animationStopped = true;
				this._animation.stop();
				this.internalProgress.style.height="100%";
				this.internalProgress.style.left = "0px";
				this._restoreValues();
				this._setLabelPosition();
			}
		},
		_showRemoteProgress: function(){
			var _self = this;
//			dojo.debug("getMax: "+this.getMaxProgressValue()+" getprval: "+this.getProgressValue());
			if ( (this.getMaxProgressValue() == this.getProgressValue()) &&
				this._oInterval){
				clearInterval(this._oInterval);
				this._oInterval = null;
				this.setProgressValue("100%");
				return;	
			}
			var bArgs = {
				url: _self.dataSource,
				method: "POST",
				mimetype: "text/json",
				error: function(type, errorObj){
					dojo.debug("[ProgressBar] showRemoteProgress error");
				},
				load: function(type, data, evt){
					//dojo.debug(data["progress"]);
					_self.setProgressValue(
						(_self._oInterval ? data["progress"] : "100%")
					);
//				dojo.debug("_oInterval: "+_self._oInterval);
				}
			};
			dojo.io.bind(bArgs);
		},
		render: function(){
			this._setPercentLabel(dojo.string.trim(this._progressPercentValue));
			this._setPixelValue(this._pixelValue);
			this._setLabelPosition();
		},
		// private functions
		_setLabelPosition: function(){
			var _widthFront = 
				dojo.html.getContentBox(this.frontPercentLabel).width;
			var _heightFront = 
				dojo.html.getContentBox(this.frontPercentLabel).height;
			var _widthBack = 
				dojo.html.getContentBox(this.backPercentLabel).width;
			var _heightBack = 
				dojo.html.getContentBox(this.backPercentLabel).height;
			var _leftFront = (this.width - _widthFront)/2 + "px";
			var _bottomFront = (parseInt(this.height) - parseInt(_heightFront))/2 + "px";
			var _leftBack = (this.width - _widthBack)/2 + "px";
			var _bottomBack = (parseInt(this.height) - parseInt(_heightBack))/2 + "px";
			this.frontPercentLabel.style.left = _leftFront;
			this.backPercentLabel.style.left = _leftBack; 
			this.frontPercentLabel.style.bottom = _bottomFront;
			this.backPercentLabel.style.bottom = _bottomBack; 
//			dojo.debug("bottom: "+this.backPercentLabel.style.bottom);
//			dojo.debug("BOTTOM: "+_bottom);
		},
		_setPercentLabel: function(percentValue){
			dojo.dom.removeChildren(this.frontPercentLabel);
			dojo.dom.removeChildren(this.backPercentLabel);
			var _percentValue = this.showOnlyIntegers == "false" ? 
				percentValue : parseInt(percentValue) + "%";
			this.frontPercentLabel.
				appendChild(document.createTextNode(_percentValue));
			this.backPercentLabel.
				appendChild(document.createTextNode(_percentValue));
		},
		_setPixelValue: function(value){
			eval("this.internalProgress.style." + this._dimension + " = " + value + " + 'px'");
			this.onChange();
		},
		onChange: function(){
		}
	});
	
