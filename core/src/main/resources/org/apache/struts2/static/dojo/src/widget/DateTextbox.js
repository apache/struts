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

//TODO: combine date and time widgets?
dojo.widget.defineWidget(
	"dojo.widget.DateTextbox",
	dojo.widget.ValidationTextbox,
	{
		// summary: A TextBox which tests for a valid date
		// format: Deprecated. Style as described in v0.3 in dojo.validate.  Default is  "MM/DD/YYYY".

		// optional pattern used in display of formatted date.  Uses locale-specific format by default.  See dojo.date.format.
		displayFormat: "",
		// type of format appropriate to locale.  see dojo.date.format
		formatLength: "short",
//TODO: add date, saveFormat attributes like DropdownDatePicker?

		mixInProperties: function(/*Object*/localProperties){
			// summary: see dojo.widget.Widget

			// First initialize properties in super-class.
			dojo.widget.DateTextbox.superclass.mixInProperties.apply(this, arguments);
	
			// Get properties from markup attributes, and assign to flags object.
			if(localProperties.format){ 
				this.flags.format = localProperties.format;
			}
		},

		isValid: function(){ 
			// summary: see dojo.widget.ValidationTextbox

			if(this.flags.format){
				dojo.deprecated("dojo.widget.DateTextbox", "format attribute is deprecated; use displayFormat or formatLength instead", "0.5");
				return dojo.validate.isValidDate(this.textbox.value, this.flags.format);
			}

			return dojo.date.parse(this.textbox.value, {formatLength:this.formatLength, selector:'dateOnly', locale:this.lang, datePattern: this.displayFormat});
		}
	}
);

dojo.widget.defineWidget(
	"dojo.widget.TimeTextbox",
	dojo.widget.ValidationTextbox,
	{
		// summary: A TextBox which tests for a valid time
		// format: Deprecated. Described in v0.3 in dojo.validate.  Default is  "h:mm:ss t".
		// amSymbol: Deprecated. Used with format. The symbol used for AM.  Default is "AM" or "am".
		// pmSymbol: Deprecated. Used with format. The symbol used for PM.  Default is "PM" or "pm".

		// optional pattern used in display of formatted date.  Uses locale-specific format by default.  See dojo.date.format.
		displayFormat: "",
		// type of format appropriate to locale.  see dojo.date.format
		formatLength: "short",

		mixInProperties: function(/*Object*/localProperties){
			// summary: see dojo.widget.Widget

			// First initialize properties in super-class.
			dojo.widget.TimeTextbox.superclass.mixInProperties.apply(this, arguments);
	
			// Get properties from markup attributes, and assign to flags object.
			if(localProperties.format){ 
				this.flags.format = localProperties.format;
			}
			if(localProperties.amsymbol){ 
				this.flags.amSymbol = localProperties.amsymbol;
			}
			if(localProperties.pmsymbol){ 
				this.flags.pmSymbol = localProperties.pmsymbol;
			}
		},

		isValid: function(){ 
			// summary: see dojo.widget.ValidationTextbox
			if(this.flags.format){
				dojo.deprecated("dojo.widget.TimeTextbox", "format attribute is deprecated; use displayFormat or formatLength instead", "0.5");
				return dojo.validate.isValidTime(this.textbox.value, this.flags);
			}

			return dojo.date.parse(this.textbox.value, {formatLength:this.formatLength, selector:'timeOnly', locale:this.lang, timePattern: this.displayFormat});
		}
	}
);
