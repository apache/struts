/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.IntegerTextbox");

dojo.require("dojo.widget.ValidationTextbox");
dojo.require("dojo.validate.common");

/*
  ****** IntegerTextbox ******

  A subclass of ValidationTextbox.
  Over-rides isValid/isInRange to test for integer input.
  Has 4 new properties that can be specified as attributes in the markup.

  @attr signed     The leading plus-or-minus sign. Can be true or false, default is either.
  @attr separator  The character used as the thousands separator.  Default is no separator.
  @attr min  Minimum signed value.  Default is -Infinity
  @attr max  Maximum signed value.  Default is +Infinity
*/
dojo.widget.defineWidget(
	"dojo.widget.IntegerTextbox",
	dojo.widget.ValidationTextbox,
	{
		mixInProperties: function(localProperties, frag){
			// First initialize properties in super-class.
			dojo.widget.IntegerTextbox.superclass.mixInProperties.apply(this, arguments);
	
			// Get properties from markup attributes, and assign to flags object.
			if((localProperties.signed == "true")||
				(localProperties.signed == "always")){
				this.flags.signed = true;
			}else if((localProperties.signed == "false")||
					(localProperties.signed == "never")){
				this.flags.signed = false;
				this.flags.min = 0;
			}else{
				this.flags.signed = [ true, false ]; // optional
			}
			if(localProperties.separator){ 
				this.flags.separator = localProperties.separator;
			}
			if(localProperties.min){ 
				this.flags.min = parseInt(localProperties.min);
			}
			if(localProperties.max){ 
				this.flags.max = parseInt(localProperties.max);
			}
		},

		// Over-ride for integer validation
		isValid: function(){
			return dojo.validate.isInteger(this.textbox.value, this.flags);
		},
		isInRange: function(){
			return dojo.validate.isInRange(this.textbox.value, this.flags);
		}
	}
);
