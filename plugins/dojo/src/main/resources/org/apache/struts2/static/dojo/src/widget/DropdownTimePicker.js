/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.DropdownTimePicker");

dojo.require("dojo.widget.*");
dojo.require("dojo.widget.DropdownContainer");
dojo.require("dojo.widget.TimePicker");
dojo.require("dojo.event.*");
dojo.require("dojo.html.*");
dojo.require("dojo.date.format");
dojo.require("dojo.date.serialize");
dojo.require("dojo.i18n.common");
dojo.requireLocalization("dojo.widget", "DropdownTimePicker");

// summary
//	input box with a drop-down gui control, for setting the time (hours, minutes, seconds, am/pm) of an event
dojo.widget.defineWidget(
	"dojo.widget.DropdownTimePicker",
	dojo.widget.DropdownContainer,
	{
		// URL
		//	path of icon for button to display time picker widget
		iconURL: dojo.uri.dojoUri("src/widget/templates/images/timeIcon.gif"),
		
		// Number
		//	z-index of time picker widget
		zIndex: "10",

		// pattern used in display of formatted time.  Uses locale-specific format by default.  See dojo.date.format.
		displayFormat: "",

		// String
		//	Deprecated. format string for how time is displayed in the input box using strftime, see dojo.date.strftime	
		timeFormat: "",

//FIXME: need saveFormat attribute support

		// type of format appropriate to locale.  see dojo.date.format
		formatLength: "short",

		// String
		//	time value in RFC3339 format (http://www.ietf.org/rfc/rfc3339.txt)
		//	ex: 12:00
		value: "",

		postMixInProperties: function() {
			dojo.widget.DropdownTimePicker.superclass.postMixInProperties.apply(this, arguments);
			var messages = dojo.i18n.getLocalization("dojo.widget", "DropdownTimePicker", this.lang);
			this.iconAlt = messages.selectTime;
		},

		fillInTemplate: function(){
			dojo.widget.DropdownTimePicker.superclass.fillInTemplate.apply(this, arguments);

			var timeProps = { widgetContainerId: this.widgetId, lang: this.lang };
			this.timePicker = dojo.widget.createWidget("TimePicker", timeProps, this.containerNode, "child");
			dojo.event.connect(this.timePicker, "onSetTime", this, "onSetTime");
			dojo.event.connect(this.inputNode,  "onchange",  this, "onInputChange");
			this.containerNode.style.zIndex = this.zIndex;
			this.containerNode.explodeClassName = "timeBorder";
			if(this.value){
				this.timePicker.selectedTime.anyTime = false;
				this.timePicker.setDateTime("2005-01-01T" + this.value);
				this.timePicker.initData();
				this.timePicker.initUI();
				this.onSetTime();
			}
		},
		
		onSetTime: function(){
			// summary: callback when user sets the time via the TimePicker widget
			if(this.timePicker.selectedTime.anyTime){
				this.inputNode.value = "";
			}else if(this.timeFormat){
				dojo.deprecated("dojo.widget.DropdownTimePicker",
				"Must use displayFormat attribute instead of timeFormat.  See dojo.date.format for specification.", "0.5");
				this.inputNode.value = dojo.date.strftime(this.timePicker.time, this.timeFormat, this.lang);
			}else{
				this.inputNode.value = dojo.date.format(this.timePicker.time,
					{formatLength:this.formatLength, datePattern:this.displayFormat, selector:'timeOnly', locale:this.lang});
			}

			this.hideContainer();
		},
		
		onInputChange: function(){
			// summary: callback when the user has typed in a time value manually
			this.timePicker.time = "2005-01-01T" + this.inputNode.value; //FIXME: i18n
			this.timePicker.setDateTime(this.timePicker.time);
			this.timePicker.initData();
			this.timePicker.initUI();
		},
		
		enable: function() {
			// summary: enable this widget to accept user input
			this.inputNode.disabled = false;
			this.timePicker.enable();
			dojo.widget.DropdownTimePicker.superclass.enable.apply(this, arguments);
		},
		
		disable: function() {
			// summary: lock this widget so that the user can't change the value
			this.inputNode.disabled = true;
			this.timePicker.disable();
			dojo.widget.DropdownTimePicker.superclass.disable.apply(this, arguments);
		}
	}
);
