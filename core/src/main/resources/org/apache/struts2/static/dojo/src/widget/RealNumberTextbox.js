/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.RealNumberTextbox");

dojo.require("dojo.widget.IntegerTextbox");
dojo.require("dojo.validate.common");

/*
  ****** RealNumberTextbox ******

  A subclass that extends IntegerTextbox.
  Over-rides isValid/isInRange to test for real number input.
  Has 5 new properties that can be specified as attributes in the markup.

  @attr places    The exact number of decimal places.  If omitted, it's unlimited and optional.
  @attr exponent  Can be true or false.  If omitted the exponential part is optional.
  @attr eSigned   Is the exponent signed?  Can be true or false, if omitted the sign is optional.
  @attr min  Minimum signed value.  Default is -Infinity
  @attr max  Maximum signed value.  Default is +Infinity
*/
dojo.widget.defineWidget(
	"dojo.widget.RealNumberTextbox",
	dojo.widget.IntegerTextbox,
	{
		mixInProperties: function(localProperties, frag){
			// First initialize properties in super-class.
			dojo.widget.RealNumberTextbox.superclass.mixInProperties.apply(this, arguments);
	
			// Get properties from markup attributes, and assign to flags object.
			if (localProperties.places){ 
				this.flags.places = Number(localProperties.places);
			}
			if((localProperties.exponent == "true")||
				(localProperties.exponent == "always")){
				this.flags.exponent = true;
			}else if((localProperties.exponent == "false")||(localProperties.exponent == "never")){
				this.flags.exponent = false;
			}else{
				this.flags.exponent = [ true, false ]; // optional
			}
			if((localProperties.esigned == "true")||(localProperties.esigned == "always")){
				this.flags.eSigned = true;
			}else if((localProperties.esigned == "false")||(localProperties.esigned == "never")){
				this.flags.eSigned = false;
			}else{
				this.flags.eSigned = [ true, false ]; // optional
			}
			if(localProperties.min){ 
				this.flags.min = parseFloat(localProperties.min);
			}
			if(localProperties.max){ 
				this.flags.max = parseFloat(localProperties.max);
			}
		},

		// Over-ride for real number validation
		isValid: function(){
			return dojo.validate.isRealNumber(this.textbox.value, this.flags);
		},
		isInRange: function(){
			return dojo.validate.isInRange(this.textbox.value, this.flags);
		}

	}
);
