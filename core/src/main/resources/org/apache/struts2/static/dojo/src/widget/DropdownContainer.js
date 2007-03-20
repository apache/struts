/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.DropdownContainer");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.HtmlWidget");
dojo.require("dojo.widget.PopupContainer");
dojo.require("dojo.event.*");
dojo.require("dojo.html.layout");
dojo.require("dojo.html.display");
dojo.require("dojo.html.iframe");
dojo.require("dojo.html.util");

// summary:
//		dojo.widget.DropdownContainer provides an input box and a button for a dropdown.
//		In subclass, the dropdown can be specified.
dojo.widget.defineWidget(
	"dojo.widget.DropdownContainer",
	dojo.widget.HtmlWidget,
	{
		// String: width of the input box
		inputWidth: "7em",
		// String: id of this widget
		id: "",
		// String: id of the input box
		inputId: "",
		// String: name of the input box
		inputName: "",
		// dojo.uri.Uri: icon for the dropdown button
		iconURL: dojo.uri.dojoUri("src/widget/templates/images/combo_box_arrow.png"),
		// dojo.uri.Uri: alt text for the dropdown button icon
		iconAlt: "",

		inputNode: null,
		buttonNode: null,
		containerNode: null,

		// String: toggle property of the dropdown
		containerToggle: "plain",
		// Int: toggle duration property of the dropdown
		containerToggleDuration: 150,
		containerAnimInProgress: false,

		templateString: '<span style="white-space:nowrap"><input type="hidden" name="" value="" dojoAttachPoint="valueNode" /><input name="" type="text" value="" style="vertical-align:middle;" dojoAttachPoint="inputNode" autocomplete="off" /> <img src="${this.iconURL}" alt="${this.iconAlt}" dojoAttachEvent="onclick: onIconClick" dojoAttachPoint="buttonNode" style="vertical-align:middle; cursor:pointer; cursor:hand" /></span>',
		templateCssPath: "",

		fillInTemplate: function(args, frag){
			var source = this.getFragNodeRef(frag);

			this.popup = dojo.widget.createWidget("PopupContainer", {toggle: this.containerToggle, toggleDuration: this.containerToggleDuration});

			this.containerNode = this.popup.domNode;

			this.domNode.appendChild(this.popup.domNode);
			if(this.id) { this.domNode.id = this.id; }
			if(this.inputId){ this.inputNode.id = this.inputId; }
			if(this.inputName){ this.inputNode.name = this.inputName; }
			this.inputNode.style.width = this.inputWidth;

			dojo.event.connect(this.inputNode, "onchange", this, "onInputChange");
		},

		onIconClick: function(evt){
			if(!this.isEnabled) return;
			if(!this.popup.isShowingNow){
				this.popup.open(this.inputNode, this, this.buttonNode);
			}else{
				this.popup.close();
			}
		},

		hideContainer: function(){
			// summary: hide the dropdown
			if(this.popup.isShowingNow){
				this.popup.close();
			}
		},

		onInputChange: function(){
			// summary: signal for changes in the input box
		}
	}
);
