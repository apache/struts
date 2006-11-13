/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.Textbox");

dojo.require("dojo.widget.*");
dojo.require("dojo.widget.HtmlWidget");
dojo.require("dojo.widget.Manager");
dojo.require("dojo.widget.Parse");
dojo.require("dojo.xml.Parse");
dojo.require("dojo.lang.array");
dojo.require("dojo.lang.common");

dojo.require("dojo.i18n.common");
dojo.requireLocalization("dojo.widget", "validate");

/*
  ****** Textbox ******

  This widget is a generic textbox field.
  Serves as a base class to derive more specialized functionality in subclasses.
  Has the following properties that can be specified as attributes in the markup.

  @attr id         The textbox id attribute.
  @attr className  The textbox class attribute.
  @attr name       The textbox name attribute.
  @attr value      The textbox value attribute.
  @attr trim       Removes leading and trailing whitespace if true.  Default is false.
  @attr uppercase  Converts all characters to uppercase if true.  Default is false.
  @attr lowercase  Converts all characters to lowercase if true.  Default is false.
  @attr ucFirst    Converts the first character of each word to uppercase if true.
  @attr lowercase  Removes all characters that are not digits if true.  Default is false.
*/
dojo.widget.defineWidget(
	"dojo.widget.Textbox",
	dojo.widget.HtmlWidget,
	{
		// default values for new subclass properties
		className: "",
		name: "",
		value: "",
		type: "",
		trim: false,
		uppercase: false,
		lowercase: false,
		ucFirst: false,
		digit: false,
		htmlfloat: "none",

		templatePath: dojo.uri.dojoUri("src/widget/templates/Textbox.html"),
	
		// our DOM nodes
		textbox: null,
	
		// Apply various filters to textbox value
		filter: function() { 
			if (this.trim) {
				this.textbox.value = this.textbox.value.replace(/(^\s*|\s*$)/g, "");
			} 
			if (this.uppercase) {
				this.textbox.value = this.textbox.value.toUpperCase();
			} 
			if (this.lowercase) {
				this.textbox.value = this.textbox.value.toLowerCase();
			} 
			if (this.ucFirst) {
				this.textbox.value = this.textbox.value.replace(/\b\w+\b/g, 
					function(word) { return word.substring(0,1).toUpperCase() + word.substring(1).toLowerCase(); });
			} 
			if (this.digit) {
				this.textbox.value = this.textbox.value.replace(/\D/g, "");
			} 
		},
	
		// event handlers, you can over-ride these in your own subclasses
		onfocus: function() {},
		onblur: function() { this.filter(); },
	
		// All functions below are called by create from dojo.widget.Widget
		mixInProperties: function(localProperties, frag) {
			dojo.widget.Textbox.superclass.mixInProperties.apply(this, arguments);
			if ( localProperties["class"] ) { 
				this.className = localProperties["class"];
			}
		}
	}
);
