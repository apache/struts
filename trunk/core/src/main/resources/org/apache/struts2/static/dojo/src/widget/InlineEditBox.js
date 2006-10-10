/*
	Copyright (c) 2004-2005, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.InlineEditBox");
dojo.provide("dojo.widget.html.InlineEditBox");

dojo.require("dojo.widget.*");
dojo.require("dojo.fx.*");
dojo.require("dojo.graphics.color");
dojo.require("dojo.string");
dojo.require("dojo.style");
dojo.require("dojo.html");

dojo.widget.tags.addParseTreeHandler("dojo:inlineeditbox");

dojo.widget.html.InlineEditBox = function(){
	dojo.widget.HtmlWidget.call(this);
	// mutable objects need to be in constructor to give each instance its own copy
	this.history = [];
	this.storage = document.createElement("span");
}

dojo.inherits(dojo.widget.html.InlineEditBox, dojo.widget.HtmlWidget);

dojo.lang.extend(dojo.widget.html.InlineEditBox, {
	templatePath: dojo.uri.dojoUri("src/widget/templates/HtmlInlineEditBox.html"),
	templateCssPath: dojo.uri.dojoUri("src/widget/templates/HtmlInlineEditBox.css"),
	widgetType: "InlineEditBox",

	form: null,
	editBox: null,
	edit: null,
	text: null,
	textarea: null,
	submitButton: null,
	cancelButton: null,
	mode: "text",

	minWidth: 100, //px. minimum width of edit box
	minHeight: 200, //px. minimum width of edit box, if it's a TA

	editing: false,
	textValue: "",
	defaultText: "",
	doFade: false,

	onSave: function(newValue, oldValue){},
	onUndo: function(value){},

	// overwrite buildRendering so we don't clobber our list
	buildRendering: function(args, frag){
		this.nodeRef = frag["dojo:"+this.widgetType.toLowerCase()]["nodeRef"];
		var node = this.nodeRef;
		if(node.normalize){ node.normalize(); }

		dojo.widget.buildAndAttachTemplate(this);

		this.editable = document.createElement("span");
		// this.editable.appendChild(node.firstChild);
		while(node.firstChild){
			this.editable.appendChild(node.firstChild);
		}
		// this.textValue = this.editable.firstChild.nodeValue;
		this.textValue = dojo.string.trim(this.editable.innerHTML);
		if(dojo.string.trim(this.textValue).length == 0){
			this.editable.innerHTML = this.defaultText;
		}
		/*
		if(node.hasChildNodes()) {
			node.insertBefore(this.editable, node.firstChild);
		} else {
		}
		*/
		node.appendChild(this.editable);

		// delay to try and show up before stylesheet
		var _this = this;
		setTimeout(function(){
			_this.editable.appendChild(_this.edit);
		}, 30);

		dojo.event.connect(this.editable, "onmouseover", this, "mouseover");
		dojo.event.connect(this.editable, "onmouseout", this, "mouseout");
		dojo.event.connect(this.editable, "onclick", this, "beginEdit");

		this.fillInTemplate(args, frag);
	},

	mouseover: function(e){
		if(!this.editing){
			dojo.html.addClass(this.editable, "editableRegion");
			if(this.mode == "textarea"){
				dojo.html.addClass(this.editable, "editableTextareaRegion");
			}
		}
	},

	mouseout: function(e){
		// if((e)&&(e.target != this.domNode)){ return; }
		if(!this.editing){
			dojo.html.removeClass(this.editable, "editableRegion");
			dojo.html.removeClass(this.editable, "editableTextareaRegion");
		}
	},

	beginEdit: function(e){
		if(this.editing){ return; }
		this.mouseout();
		this.editing = true;

		var ee = this[this.mode.toLowerCase()];

		ee.style.display = "";
		ee.value = dojo.string.trim(this.textValue);
		ee.style.fontSize = dojo.style.getStyle(this.editable, "font-size");
		ee.style.fontWeight = dojo.style.getStyle(this.editable, "font-weight");
		ee.style.fontStyle = dojo.style.getStyle(this.editable, "font-style");
		//this.text.style.fontFamily = dojo.dom.getStyle(this.editable, "font-family");

		ee.style.width = Math.max(dojo.html.getInnerWidth(this.editable), this.minWidth) + "px";
		// ee.style.width = "100%";

		if(this.mode.toLowerCase()=="textarea"){
			ee.style.display = "block";
			ee.style.height = Math.max(dojo.html.getInnerHeight(this.editable), this.minHeight) + "px";
		}
		this.editable.style.display = "none";
		this.nodeRef.appendChild(this.form);
		ee.select();
		this.submitButton.disabled = true;
	},

	saveEdit: function(e){
		e.preventDefault();
		e.stopPropagation();
		var ee = this[this.mode.toLowerCase()];
		if((this.textValue != ee.value)&&
			(dojo.string.trim(ee.value) != "")){
			this.doFade = true;
			this.history.push(this.textValue);
			this.onSave(ee.value, this.textValue);
			this.textValue = ee.value;
			this.editable.innerHTML = this.textValue;
		}else{
			this.doFade = false;
		}
		this.finishEdit(e);
	},

	cancelEdit: function(e){
		if(!this.editing){ return false; }
		this.editing = false;
		this.nodeRef.removeChild(this.form);
		this.editable.style.display = "";
		return true;
	},

	finishEdit: function(e){
		if(!this.cancelEdit(e)){ return; }
		if(this.doFade) {
			dojo.fx.highlight(this.editable, dojo.graphics.color.hex2rgb("#ffc"), 700, 300);
		}
		this.doFade = false;
	},

	setText: function(txt){
		// sets the text without informing the server
		var tt = dojo.string.trim(txt);
		this.textValue = tt
		this.editable.innerHTML = tt;
	},

	undo: function(){
		if(this.history.length > 0){
			var value = this.history.pop();
			this.editable.innerHTML = value;
			this.textValue = value;
			this.onUndo(value);
		}
	},

	checkForValueChange: function(){
		var ee = this[this.mode.toLowerCase()];
		if((this.textValue != ee.value)&&
			(dojo.string.trim(ee.value) != "")){
			this.submitButton.disabled = false;
		}
	}
});
