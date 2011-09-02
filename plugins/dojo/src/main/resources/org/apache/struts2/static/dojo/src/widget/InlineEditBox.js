/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.InlineEditBox");
dojo.require("dojo.widget.*");
dojo.require("dojo.event.*");
dojo.require("dojo.lfx.*");
dojo.require("dojo.gfx.color");
dojo.require("dojo.string");
dojo.require("dojo.html.*");
dojo.require("dojo.html.layout");
dojo.widget.defineWidget("dojo.widget.InlineEditBox", dojo.widget.HtmlWidget, function () {
	this.history = [];
}, {templateString:"<form class=\"inlineEditBox\" style=\"display: none\" dojoAttachPoint=\"form\" dojoAttachEvent=\"onSubmit:saveEdit; onReset:cancelEdit; onKeyUp: checkForValueChange;\">\n\t<input type=\"text\" dojoAttachPoint=\"text\" style=\"display: none;\" />\n\t<textarea dojoAttachPoint=\"textarea\" style=\"display: none;\"></textarea>\n\t<input type=\"submit\" value=\"Save\" dojoAttachPoint=\"submitButton\" />\n\t<input type=\"reset\" value=\"Cancel\" dojoAttachPoint=\"cancelButton\" />\n</form>\n", templateCssString:".editLabel {\n\tfont-size : small;\n\tpadding : 0 5px;\n\tdisplay : none;\n}\n\n.editableRegionDisabled {\n\tcursor : pointer;\n\t_cursor : hand;\n}\n\n.editableRegion {\n\tbackground-color : #ffc !important;\n\tcursor : pointer;\n\t_cursor : hand;\n}\n\n.editableRegion .editLabel {\n\tdisplay : inline;\n}\n\n.editableTextareaRegion .editLabel {\n\tdisplay : block;\n}\n\n.inlineEditBox {\n\t/*background-color : #ffc;*/\n\tdisplay : inline;\n}\n", templateCssPath:dojo.uri.moduleUri("dojo.widget", "templates/InlineEditBox.css"), mode:"text", name:"", minWidth:100, minHeight:200, editing:false, value:"", textValue:"", defaultText:"", postMixInProperties:function () {
	if (this.textValue) {
		dojo.deprecated("InlineEditBox: Use value parameter instead of textValue; will be removed in 0.5");
		this.value = this.textValue;
	}
	if (this.defaultText) {
		dojo.deprecated("InlineEditBox: Use value parameter instead of defaultText; will be removed in 0.5");
		this.value = this.defaultText;
	}
}, postCreate:function (args, frag) {
	this.editable = this.getFragNodeRef(frag);
	dojo.html.insertAfter(this.editable, this.form);
	dojo.event.connect(this.editable, "onmouseover", this, "onMouseOver");
	dojo.event.connect(this.editable, "onmouseout", this, "onMouseOut");
	dojo.event.connect(this.editable, "onclick", this, "_beginEdit");
	if (this.value) {
		this.editable.innerHTML = this.value;
		return;
	} else {
		this.value = dojo.string.trim(this.editable.innerHTML);
		this.editable.innerHTML = this.value;
	}
}, onMouseOver:function () {
	if (!this.editing) {
		if (this.disabled) {
			dojo.html.addClass(this.editable, "editableRegionDisabled");
		} else {
			dojo.html.addClass(this.editable, "editableRegion");
			if (this.mode == "textarea") {
				dojo.html.addClass(this.editable, "editableTextareaRegion");
			}
		}
	}
}, onMouseOut:function () {
	if (!this.editing) {
		dojo.html.removeClass(this.editable, "editableRegion");
		dojo.html.removeClass(this.editable, "editableTextareaRegion");
		dojo.html.removeClass(this.editable, "editableRegionDisabled");
	}
}, _beginEdit:function (e) {
	if (this.editing || this.disabled) {
		return;
	}
	this.onMouseOut();
	this.editing = true;
	var ee = this[this.mode.toLowerCase()];
	ee.value = dojo.string.trim(this.value);
	ee.style.fontSize = dojo.html.getStyle(this.editable, "font-size");
	ee.style.fontWeight = dojo.html.getStyle(this.editable, "font-weight");
	ee.style.fontStyle = dojo.html.getStyle(this.editable, "font-style");
	var bb = dojo.html.getBorderBox(this.editable);
	ee.style.width = Math.max(bb.width, this.minWidth) + "px";
	if (this.mode.toLowerCase() == "textarea") {
		ee.style.display = "block";
		ee.style.height = Math.max(bb.height, this.minHeight) + "px";
	} else {
		ee.style.display = "";
	}
	this.form.style.display = "";
	this.editable.style.display = "none";
	ee.focus();
	ee.select();
	this.submitButton.disabled = true;
}, saveEdit:function (e) {
	e.preventDefault();
	e.stopPropagation();
	var ee = this[this.mode.toLowerCase()];
	if ((this.value != ee.value) && (dojo.string.trim(ee.value) != "")) {
		this.doFade = true;
		this.history.push(this.value);
		this.onSave(ee.value, this.value, this.name);
		this.value = ee.value;
		this.editable.innerHTML = "";
		var textNode = document.createTextNode(this.value);
		this.editable.appendChild(textNode);
	} else {
		this.doFade = false;
	}
	this._finishEdit(e);
}, _stopEditing:function () {
	this.editing = false;
	this.form.style.display = "none";
	this.editable.style.display = "";
	return true;
}, cancelEdit:function (e) {
	this._stopEditing();
	this.onCancel();
	return true;
}, _finishEdit:function (e) {
	this._stopEditing();
	if (this.doFade) {
		dojo.lfx.highlight(this.editable, dojo.gfx.color.hex2rgb("#ffc"), 700).play(300);
	}
	this.doFade = false;
}, setText:function (txt) {
	dojo.deprecated("setText() is deprecated, call setValue() instead, will be removed in 0.5");
	this.setValue(txt);
}, setValue:function (txt) {
	txt = "" + txt;
	var tt = dojo.string.trim(txt);
	this.value = tt;
	this.editable.innerHTML = tt;
}, undo:function () {
	if (this.history.length > 0) {
		var curValue = this.value;
		var value = this.history.pop();
		this.editable.innerHTML = value;
		this.value = value;
		this.onUndo(value);
		this.onSave(value, curValue, this.name);
	}
}, onChange:function (newValue, oldValue) {
}, onSave:function (newValue, oldValue, name) {
}, onCancel:function () {
}, checkForValueChange:function () {
	var ee = this[this.mode.toLowerCase()];
	if ((this.value != ee.value) && (dojo.string.trim(ee.value) != "")) {
		this.submitButton.disabled = false;
	}
	this.onChange(this.value, ee.value);
}, disable:function () {
	this.submitButton.disabled = true;
	this.cancelButton.disabled = true;
	var ee = this[this.mode.toLowerCase()];
	ee.disabled = true;
	dojo.widget.InlineEditBox.superclass.disable.apply(this, arguments);
}, enable:function () {
	this.checkForValueChange();
	this.cancelButton.disabled = false;
	var ee = this[this.mode.toLowerCase()];
	ee.disabled = false;
	dojo.widget.InlineEditBox.superclass.enable.apply(this, arguments);
}});

