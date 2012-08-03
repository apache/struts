/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.DropdownDatePicker");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.DropdownContainer");
dojo.require("dojo.widget.DatePicker");
dojo.require("dojo.event.*");
dojo.require("dojo.html.*");
dojo.require("dojo.date.format");
dojo.require("dojo.date.serialize");
dojo.require("dojo.string.common");
dojo.require("dojo.i18n.common");
dojo.requireLocalization("dojo.widget", "DropdownDatePicker", null, "ROOT");
dojo.widget.defineWidget("dojo.widget.DropdownDatePicker", dojo.widget.DropdownContainer, {iconURL:dojo.uri.moduleUri("dojo.widget", "templates/images/dateIcon.gif"), formatLength:"short", displayFormat:"", saveFormat:"", value:"", name:"", displayWeeks:6, adjustWeeks:false, startDate:"1492-10-12", endDate:"2941-10-12", weekStartsOn:"", staticDisplay:false, postMixInProperties:function (localProperties, frag) {
	dojo.widget.DropdownDatePicker.superclass.postMixInProperties.apply(this, arguments);
	var messages = dojo.i18n.getLocalization("dojo.widget", "DropdownDatePicker", this.lang);
	this.iconAlt = messages.selectDate;
	if (typeof (this.value) == "string" && this.value.toLowerCase() == "today") {
		this.value = new Date();
	}
	if (this.value && isNaN(this.value)) {
		var orig = this.value;
		this.value = dojo.date.fromRfc3339(this.value);
		if (!this.value) {
			this.value = new Date(orig);
			dojo.deprecated("dojo.widget.DropdownDatePicker", "date attributes must be passed in Rfc3339 format", "0.5");
		}
	}
	if (this.value && !isNaN(this.value)) {
		this.value = new Date(this.value);
	}
}, fillInTemplate:function (args, frag) {
	dojo.widget.DropdownDatePicker.superclass.fillInTemplate.call(this, args, frag);
	var dpArgs = {widgetContainerId:this.widgetId, lang:this.lang, value:this.value, startDate:this.startDate, endDate:this.endDate, displayWeeks:this.displayWeeks, weekStartsOn:this.weekStartsOn, adjustWeeks:this.adjustWeeks, staticDisplay:this.staticDisplay};
	this.datePicker = dojo.widget.createWidget("DatePicker", dpArgs, this.containerNode, "child");
	dojo.event.connect(this.datePicker, "onValueChanged", this, "_updateText");
	dojo.event.connect(this.inputNode, "onChange", this, "_updateText");
	if (this.value) {
		this._updateText();
	}
	this.containerNode.explodeClassName = "calendarBodyContainer";
	this.valueNode.name = this.name;
}, getValue:function () {
	return this.valueNode.value;
}, getDate:function () {
	return this.datePicker.value;
}, setValue:function (rfcDate) {
	this.setDate(rfcDate);
}, setDate:function (dateObj) {
	this.datePicker.setDate(dateObj);
	this._syncValueNode();
}, _updateText:function () {
	this.inputNode.value = this.datePicker.value ? dojo.date.format(this.datePicker.value, {formatLength:this.formatLength, datePattern:this.displayFormat, selector:"dateOnly", locale:this.lang}) : "";
	if (this.value < this.datePicker.startDate || this.value > this.datePicker.endDate) {
		this.inputNode.value = "";
	}
	this._syncValueNode();
	this.onValueChanged(this.getDate());
	this.hideContainer();
}, onValueChanged:function (dateObj) {
}, onInputChange:function () {
	var input = dojo.string.trim(this.inputNode.value);
	if (input) {
		var inputDate = dojo.date.parse(input, {formatLength:this.formatLength, datePattern:this.displayFormat, selector:"dateOnly", locale:this.lang});
		if (!this.datePicker._isDisabledDate(inputDate)) {
			this.setDate(inputDate);
		}
	} else {
		if (input == "") {
			this.datePicker.setDate("");
		}
		this.valueNode.value = input;
	}
	if (input) {
		this._updateText();
	}
}, _syncValueNode:function () {
	var date = this.datePicker.value;
	var value = "";
	switch (this.saveFormat.toLowerCase()) {
	  case "rfc":
	  case "iso":
	  case "":
		value = dojo.date.toRfc3339(date, "dateOnly");
		break;
	  case "posix":
	  case "unix":
		value = Number(date);
		break;
	  default:
		if (date) {
			value = dojo.date.format(date, {datePattern:this.saveFormat, selector:"dateOnly", locale:this.lang});
		}
	}
	this.valueNode.value = value;
}, destroy:function (finalize) {
	this.datePicker.destroy(finalize);
	dojo.widget.DropdownDatePicker.superclass.destroy.apply(this, arguments);
}});

