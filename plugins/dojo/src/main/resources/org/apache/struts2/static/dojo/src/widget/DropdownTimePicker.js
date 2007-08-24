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
dojo.requireLocalization("dojo.widget", "DropdownTimePicker", null, "ROOT");
dojo.widget.defineWidget("dojo.widget.DropdownTimePicker", dojo.widget.DropdownContainer, {iconURL:dojo.uri.moduleUri("dojo.widget", "templates/images/timeIcon.gif"), formatLength:"short", displayFormat:"", timeFormat:"", saveFormat:"", value:"", name:"", postMixInProperties:function () {
	dojo.widget.DropdownTimePicker.superclass.postMixInProperties.apply(this, arguments);
	var messages = dojo.i18n.getLocalization("dojo.widget", "DropdownTimePicker", this.lang);
	this.iconAlt = messages.selectTime;
	if (typeof (this.value) == "string" && this.value.toLowerCase() == "today") {
		this.value = new Date();
	}
	if (this.value && isNaN(this.value)) {
		var orig = this.value;
		this.value = dojo.date.fromRfc3339(this.value);
		if (!this.value) {
			var d = dojo.date.format(new Date(), {selector:"dateOnly", datePattern:"yyyy-MM-dd"});
			var c = orig.split(":");
			for (var i = 0; i < c.length; ++i) {
				if (c[i].length == 1) {
					c[i] = "0" + c[i];
				}
			}
			orig = c.join(":");
			this.value = dojo.date.fromRfc3339(d + "T" + orig);
			dojo.deprecated("dojo.widget.DropdownTimePicker", "time attributes must be passed in Rfc3339 format", "0.5");
		}
	}
	if (this.value && !isNaN(this.value)) {
		this.value = new Date(this.value);
	}
}, fillInTemplate:function () {
	dojo.widget.DropdownTimePicker.superclass.fillInTemplate.apply(this, arguments);
	var value = "";
	if (this.value instanceof Date) {
		value = this.value;
	} else {
		if (this.value) {
			var orig = this.value;
			var d = dojo.date.format(new Date(), {selector:"dateOnly", datePattern:"yyyy-MM-dd"});
			var c = orig.split(":");
			for (var i = 0; i < c.length; ++i) {
				if (c[i].length == 1) {
					c[i] = "0" + c[i];
				}
			}
			orig = c.join(":");
			value = dojo.date.fromRfc3339(d + "T" + orig);
		}
	}
	var tpArgs = {widgetContainerId:this.widgetId, lang:this.lang, value:value};
	this.timePicker = dojo.widget.createWidget("TimePicker", tpArgs, this.containerNode, "child");
	dojo.event.connect(this.timePicker, "onValueChanged", this, "_updateText");
	if (this.value) {
		this._updateText();
	}
	this.containerNode.style.zIndex = this.zIndex;
	this.containerNode.explodeClassName = "timeContainer";
	this.valueNode.name = this.name;
}, getValue:function () {
	return this.valueNode.value;
}, getTime:function () {
	return this.timePicker.storedTime;
}, setValue:function (rfcDate) {
	this.setTime(rfcDate);
}, setTime:function (dateObj) {
	var value = "";
	if (dateObj instanceof Date) {
		value = dateObj;
	} else {
		if (this.value) {
			var orig = this.value;
			var d = dojo.date.format(new Date(), {selector:"dateOnly", datePattern:"yyyy-MM-dd"});
			var c = orig.split(":");
			for (var i = 0; i < c.length; ++i) {
				if (c[i].length == 1) {
					c[i] = "0" + c[i];
				}
			}
			orig = c.join(":");
			value = dojo.date.fromRfc3339(d + "T" + orig);
		}
	}
	this.timePicker.setTime(value);
	this._syncValueNode();
}, _updateText:function () {
	if (this.timePicker.selectedTime.anyTime) {
		this.inputNode.value = "";
	} else {
		if (this.timeFormat) {
			dojo.deprecated("dojo.widget.DropdownTimePicker", "Must use displayFormat attribute instead of timeFormat.  See dojo.date.format for specification.", "0.5");
			this.inputNode.value = dojo.date.strftime(this.timePicker.time, this.timeFormat, this.lang);
		} else {
			this.inputNode.value = dojo.date.format(this.timePicker.time, {formatLength:this.formatLength, timePattern:this.displayFormat, selector:"timeOnly", locale:this.lang});
		}
	}
	this._syncValueNode();
	this.onValueChanged(this.getTime());
	this.hideContainer();
}, onValueChanged:function (dateObj) {
}, onInputChange:function () {
	if (this.dateFormat) {
		dojo.deprecated("dojo.widget.DropdownTimePicker", "Cannot parse user input.  Must use displayFormat attribute instead of dateFormat.  See dojo.date.format for specification.", "0.5");
	} else {
		var input = dojo.string.trim(this.inputNode.value);
		if (input) {
			var inputTime = dojo.date.parse(input, {formatLength:this.formatLength, timePattern:this.displayFormat, selector:"timeOnly", locale:this.lang});
			if (inputTime) {
				this.setTime(inputTime);
			}
		} else {
			this.valueNode.value = input;
		}
	}
	if (input) {
		this._updateText();
	}
}, _syncValueNode:function () {
	var time = this.timePicker.time;
	var value;
	switch (this.saveFormat.toLowerCase()) {
	  case "rfc":
	  case "iso":
	  case "":
		value = dojo.date.toRfc3339(time, "timeOnly");
		break;
	  case "posix":
	  case "unix":
		value = Number(time);
		break;
	  default:
		value = dojo.date.format(time, {datePattern:this.saveFormat, selector:"timeOnly", locale:this.lang});
	}
	this.valueNode.value = value;
}, destroy:function (finalize) {
	this.timePicker.destroy(finalize);
	dojo.widget.DropdownTimePicker.superclass.destroy.apply(this, arguments);
}});

