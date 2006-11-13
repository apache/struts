/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.UsTextbox");

dojo.require("dojo.widget.ValidationTextbox");
dojo.require("dojo.validate.us");

dojo.widget.defineWidget(
	"dojo.widget.UsStateTextbox",
	dojo.widget.ValidationTextbox,
	{
		// summary: a Textbox which tests for a United States state abbreviation
		// allowTerritories  Allow Guam, Puerto Rico, etc.  Default is true.
		// allowMilitary     Allow military 'states', e.g. Armed Forces Europe (AE). Default is true.

		mixInProperties: function(/*Object*/localProperties){
			// summary: see dojo.widget.Widget

			// Initialize properties in super-class.
			dojo.widget.UsStateTextbox.superclass.mixInProperties.apply(this, arguments);

			// Get properties from markup attributes, and assign to flags object.
			if(localProperties.allowterritories){
				this.flags.allowTerritories = (localProperties.allowterritories == "true");
			}
			if(localProperties.allowmilitary){
				this.flags.allowMilitary = (localProperties.allowmilitary == "true");
			}
		},

		isValid: function(){
			// summary: see dojo.widget.ValidationTextbox
			return dojo.validate.us.isState(this.textbox.value, this.flags);
		}
	}
);

/*
  ****** UsZipTextbox ******

  A subclass of ValidationTextbox.
  Over-rides isValid to test if input is a US zip code.
  Validates zip-5 and zip-5 plus 4.
*/
dojo.widget.defineWidget(
	"dojo.widget.UsZipTextbox",
	dojo.widget.ValidationTextbox,
	{
		// summary: a Textbox which tests for a United States postal code
		isValid: function(){
			// summary: see dojo.widget.ValidationTextbox
			return dojo.validate.us.isZipCode(this.textbox.value);
		}
	}
);

dojo.widget.defineWidget(
	"dojo.widget.UsSocialSecurityNumberTextbox",
	dojo.widget.ValidationTextbox,
	{
		// summary: a Textbox which tests for a United States Social Security number
		isValid: function(){
			// summary: see dojo.widget.ValidationTextbox
			return dojo.validate.us.isSocialSecurityNumber(this.textbox.value);
		}
	}
);

dojo.widget.defineWidget(
	"dojo.widget.UsPhoneNumberTextbox",
	dojo.widget.ValidationTextbox,
	{
		// summary: a Textbox which tests for a United States 10-digit telephone number, extension is optional.

		isValid: function(){
			// summary: see dojo.widget.ValidationTextbox
			return dojo.validate.us.isPhoneNumber(this.textbox.value);
		}
	}
);
