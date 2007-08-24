/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.DateTextbox");
dojo.require("dojo.widget.ValidationTextbox");
dojo.require("dojo.date.format");
dojo.require("dojo.validate.datetime");
dojo.widget.defineWidget("dojo.widget.DateTextbox", dojo.widget.ValidationTextbox, {displayFormat:"", formatLength:"short", mixInProperties:function (localProperties) {
	dojo.widget.DateTextbox.superclass.mixInProperties.apply(this, arguments);
	if (localProperties.format) {
		this.flags.format = localProperties.format;
	}
}, isValid:function () {
	if (this.flags.format) {
		dojo.deprecated("dojo.widget.DateTextbox", "format attribute is deprecated; use displayFormat or formatLength instead", "0.5");
		return dojo.validate.isValidDate(this.textbox.value, this.flags.format);
	}
	return dojo.date.parse(this.textbox.value, {formatLength:this.formatLength, selector:"dateOnly", locale:this.lang, datePattern:this.displayFormat});
}});
dojo.widget.defineWidget("dojo.widget.TimeTextbox", dojo.widget.ValidationTextbox, {displayFormat:"", formatLength:"short", mixInProperties:function (localProperties) {
	dojo.widget.TimeTextbox.superclass.mixInProperties.apply(this, arguments);
	if (localProperties.format) {
		this.flags.format = localProperties.format;
	}
	if (localProperties.amsymbol) {
		this.flags.amSymbol = localProperties.amsymbol;
	}
	if (localProperties.pmsymbol) {
		this.flags.pmSymbol = localProperties.pmsymbol;
	}
}, isValid:function () {
	if (this.flags.format) {
		dojo.deprecated("dojo.widget.TimeTextbox", "format attribute is deprecated; use displayFormat or formatLength instead", "0.5");
		return dojo.validate.isValidTime(this.textbox.value, this.flags);
	}
	return dojo.date.parse(this.textbox.value, {formatLength:this.formatLength, selector:"timeOnly", locale:this.lang, timePattern:this.displayFormat});
}});

