/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.InternetTextbox");

dojo.require("dojo.widget.ValidationTextbox");
dojo.require("dojo.validate.web");

dojo.widget.defineWidget(
	"dojo.widget.IpAddressTextbox",
	dojo.widget.ValidationTextbox,
	{
		// summary:  A Textbox which tests for a valid IP address
		// description:  Can specify formats for ipv4 or ipv6 as attributes in the markup.
		//
		// allowDottedDecimal  true or false, default is true.
		// allowDottedHex      true or false, default is true.
		// allowDottedOctal    true or false, default is true.
		// allowDecimal        true or false, default is true.
		// allowHex            true or false, default is true.
		// allowIPv6           true or false, default is true.
		// allowHybrid         true or false, default is true.

		mixInProperties: function(/*Object*/localProperties){
			// summary: see dojo.widget.Widget

			// First initialize properties in super-class.
			dojo.widget.IpAddressTextbox.superclass.mixInProperties.apply(this, arguments);

			// Get properties from markup attributes, and assign to flags object.
			if(localProperties.allowdotteddecimal){ 
				this.flags.allowDottedDecimal = (localProperties.allowdotteddecimal == "true");
			}
			if(localProperties.allowdottedhex){ 
				this.flags.allowDottedHex = (localProperties.allowdottedhex == "true");
			}
			if(localProperties.allowdottedoctal){ 
				this.flags.allowDottedOctal = (localProperties.allowdottedoctal == "true");
			}
			if(localProperties.allowdecimal){ 
				this.flags.allowDecimal = (localProperties.allowdecimal == "true");
			}
			if(localProperties.allowhex){ 
				this.flags.allowHex = (localProperties.allowhex == "true");
			}
			if(localProperties.allowipv6){ 
				this.flags.allowIPv6 = (localProperties.allowipv6 == "true");
			}
			if(localProperties.allowhybrid){ 
				this.flags.allowHybrid = (localProperties.allowhybrid == "true");
			}
		},

		isValid: function(){ 
			// summary: see dojo.widget.ValidationTextbox
			return dojo.validate.isIpAddress(this.textbox.value, this.flags);
		}
	}
);

dojo.widget.defineWidget(
	"dojo.widget.UrlTextbox",
	dojo.widget.IpAddressTextbox,
	{
		// summary:  A Textbox which tests for a valid URL
		// scheme        Can be true or false.  If omitted the scheme is optional.
		// allowIP       Allow an IP address for hostname.  Default is true.
		// allowLocal    Allow the host to be "localhost".  Default is false.
		// allowCC       Allow 2 letter country code domains.  Default is true.
		// allowGeneric  Allow generic domains.  Can be true or false, default is true.

		mixInProperties: function(/*Object*/localProperties){
			// summary: see dojo.widget.Widget

			// First initialize properties in super-class.
			dojo.widget.UrlTextbox.superclass.mixInProperties.apply(this, arguments);

			// Get properties from markup attributes, and assign to flags object.
			if ( localProperties.scheme ) { 
				this.flags.scheme = ( localProperties.scheme == "true" );
			}
			if ( localProperties.allowip ) { 
				this.flags.allowIP = ( localProperties.allowip == "true" );
			}
			if ( localProperties.allowlocal ) { 
				this.flags.allowLocal = ( localProperties.allowlocal == "true" );
			}
			if ( localProperties.allowcc ) { 
				this.flags.allowCC = ( localProperties.allowcc == "true" );
			}
			if ( localProperties.allowgeneric ) { 
				this.flags.allowGeneric = ( localProperties.allowgeneric == "true" );
			}
		},

		isValid: function(){ 
			// summary: see dojo.widget.ValidationTextbox
			return dojo.validate.isUrl(this.textbox.value, this.flags);
		}
	}
);

//FIXME: DOC: need more consistent explanation on whether attributes are inherited from the parent.  Some make sense, some don't?

dojo.widget.defineWidget(
	"dojo.widget.EmailTextbox",
	dojo.widget.UrlTextbox,
	{
		// summary:  A Textbox which tests for a valid email address
		// description:
		//  Can use all markup attributes/properties of UrlTextbox except scheme.
		//
		// allowCruft: Allow address like <mailto:foo@yahoo.com>.  Default is false.

		mixInProperties: function(/*Object*/localProperties){
			// summary: see dojo.widget.Widget

			// First initialize properties in super-class.
			dojo.widget.EmailTextbox.superclass.mixInProperties.apply(this, arguments);
	
			// Get properties from markup attributes, and assign to flags object.
			if(localProperties.allowcruft){ 
				this.flags.allowCruft = (localProperties.allowcruft == "true");
			}
		},

		isValid: function(){
			// summary: see dojo.widget.ValidationTextbox
			return dojo.validate.isEmailAddress(this.textbox.value, this.flags);
		}
	}
);

//TODO: perhaps combine with EmailTextbox?
dojo.widget.defineWidget(
	"dojo.widget.EmailListTextbox",
	dojo.widget.EmailTextbox,
	{
		// summary:  A Textbox which tests for a list of valid email addresses
		//
		// listSeparator:  The character used to separate email addresses.  
		//    Default is ";", ",", "\n" or " "

		mixInProperties: function(/*Object*/localProperties){
			// summary: see dojo.widget.Widget

			// First initialize properties in super-class.
			dojo.widget.EmailListTextbox.superclass.mixInProperties.apply(this, arguments);
	
			// Get properties from markup attributes, and assign to flags object.
			if(localProperties.listseparator){ 
				this.flags.listSeparator = localProperties.listseparator;
			}
		},

		isValid: function(){
			// summary: see dojo.widget.ValidationTextbox
			return dojo.validate.isEmailAddressList(this.textbox.value, this.flags);
		}
	}
);
