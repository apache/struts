/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.Select");
dojo.require("dojo.widget.ComboBox");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.html.stabile");
dojo.widget.defineWidget("dojo.widget.Select", dojo.widget.ComboBox, {forceValidOption:true, setValue:function (value) {
	this.comboBoxValue.value = value;
	dojo.widget.html.stabile.setState(this.widgetId, this.getState(), true);
	this.onValueChanged(value);
}, setLabel:function (value) {
	this.comboBoxSelectionValue.value = value;
	if (this.textInputNode.value != value) {
		this.textInputNode.value = value;
	}
}, getLabel:function () {
	return this.comboBoxSelectionValue.value;
}, getState:function () {
	return {value:this.getValue(), label:this.getLabel()};
}, onKeyUp:function (evt) {
	this.setLabel(this.textInputNode.value);
}, setState:function (state) {
	this.setValue(state.value);
	this.setLabel(state.label);
}, setAllValues:function (value1, value2) {
	this.setLabel(value1);
	this.setValue(value2);
}});

