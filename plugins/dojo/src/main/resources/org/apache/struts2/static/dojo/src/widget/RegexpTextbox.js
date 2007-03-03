/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.RegexpTextbox");

dojo.require("dojo.widget.ValidationTextbox");

/*
  ****** RegexpTextbox ******

  A subclass of ValidationTextbox.
  Over-rides isValid to test input based on a regular expression.
  Has a new property that can be specified as attributes in the markup. 

  @attr regexp     The regular expression string to use
  @attr flags      Flags to pass to the regular expression (e.g. 'i', 'g', etc)
*/
dojo.widget.defineWidget(
	"dojo.widget.RegexpTextbox",
	dojo.widget.ValidationTextbox,
	{
	    mixInProperties: function(localProperties, frag){
	        // First initialize properties in super-class.
	        dojo.widget.RegexpTextbox.superclass.mixInProperties.apply(this, arguments);

	        // Get properties from markup attibutes, and assign to flags object.
	        if(localProperties.regexp){
	            this.flags.regexp = localProperties.regexp;
	        }
	        if(localProperties.flags){
	            this.flags.flags = localProperties.flags;
	        }
	    },

	    // Over-ride for integer validation
	    isValid: function(){
	        var regexp = new RegExp(this.flags.regexp, this.flags.flags);
	        return regexp.test(this.textbox.value);
	    }
	}
);
