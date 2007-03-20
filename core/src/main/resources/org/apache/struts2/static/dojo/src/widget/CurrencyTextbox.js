/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.CurrencyTextbox");

dojo.require("dojo.widget.IntegerTextbox");
dojo.require("dojo.validate.common");

/*
  ****** CurrencyTextbox ******

  A subclass that extends IntegerTextbox.
  Over-rides isValid/isInRange to test if input denotes a monetary value .
  Has 5 new properties that can be specified as attributes in the markup.

  @attr fractional      The decimal places (e.g. for cents).  Can be true or false, optional if omitted.
  @attr symbol     A currency symbol such as Yen "???", Pound "???", or the Euro "???". Default is "$".
  @attr separator  Default is "," instead of no separator as in IntegerTextbox.
  @attr min  Minimum signed value.  Default is -Infinity
  @attr max  Maximum signed value.  Default is +Infinity
*/
dojo.widget.defineWidget(
	"dojo.widget.CurrencyTextbox",
	dojo.widget.IntegerTextbox,
	{
		mixInProperties: function(localProperties, frag){
			// First initialize properties in super-class.
			dojo.widget.CurrencyTextbox.superclass.mixInProperties.apply(this, arguments);
	
			// Get properties from markup attributes, and assign to flags object.
			if(localProperties.fractional){
				this.flags.fractional = (localProperties.fractional == "true");
			}else if(localProperties.cents){
				dojo.deprecated("dojo.widget.IntegerTextbox", "use fractional attr instead of cents", "0.5");
				this.flags.fractional = (localProperties.cents == "true");
			}
			if(localProperties.symbol){
				this.flags.symbol = localProperties.symbol;
			}
			if(localProperties.min){ 
				this.flags.min = parseFloat(localProperties.min);
			}
			if(localProperties.max){ 
				this.flags.max = parseFloat(localProperties.max);
			}
		},

		// Over-ride for currency validation
		isValid: function(){
			return dojo.validate.isCurrency(this.textbox.value, this.flags);
		},
		isInRange: function(){
			return dojo.validate.isInRange(this.textbox.value, this.flags);
		}
	}
);
