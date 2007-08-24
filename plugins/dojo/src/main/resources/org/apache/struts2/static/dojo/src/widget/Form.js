/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.Form");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.HtmlWidget");
dojo.widget.defineWidget("dojo.widget.Form", dojo.widget.HtmlWidget, {isContainer:true, templateString:"<form dojoAttachPoint='containerNode' dojoAttachEvent='onSubmit:onSubmit'></form>", formElements:[], ignoreNullValues:false, postCreate:function (args, frag) {
	for (var key in args) {
		if (key == "dojotype") {
			continue;
		}
		var attr = document.createAttribute(key);
		attr.nodeValue = args[key];
		this.containerNode.setAttributeNode(attr);
	}
}, _createRepeaters:function (obj, widget) {
	for (var i = 0; i < widget.children.length; ++i) {
		if (widget.children[i].widgetType == "RepeaterContainer") {
			var rIndex = widget.children[i].index;
			var rIndexPos = rIndex.indexOf("%{index}");
			rIndex = rIndex.substr(0, rIndexPos - 1);
			var myObj = this._getObject(obj, rIndex);
			if (typeof (myObj) == "object" && myObj.length == 0) {
				myObj = new Array();
			}
			var rowCount = widget.children[i].getRowCount();
			for (var j = 0, len = rowCount; j < len; ++j) {
				widget.children[i].deleteRow(0);
			}
			for (var j = 0; j < myObj.length; j++) {
				widget.children[i].addRow(false);
			}
		}
		if (widget.children[i].isContainer) {
			this._createRepeaters(obj, widget.children[i]);
		}
	}
}, _createFormElements:function () {
	if (dojo.render.html.safari) {
		this.formElements = [];
		var elems = ["INPUT", "SELECT", "TEXTAREA"];
		for (var k = 0; k < elems.length; k++) {
			var list = this.containerNode.getElementsByTagName(elems[k]);
			for (var j = 0, len2 = list.length; j < len2; j++) {
				this.formElements.push(list[j]);
			}
		}
	} else {
		this.formElements = this.containerNode.elements;
	}
}, onSubmit:function (e) {
	e.preventDefault();
}, submit:function () {
	this.containerNode.submit();
}, _getFormElement:function (name) {
	if (dojo.render.html.ie) {
		for (var i = 0, len = this.formElements.length; i < len; i++) {
			var element = this.formElements[i];
			if (element.name == name) {
				return element;
			}
		}
	} else {
		var elem = this.formElements[name];
		if (typeof (elem) != "undefined") {
			return elem;
		}
	}
	return null;
}, _getObject:function (obj, searchString) {
	var namePath = [];
	namePath = searchString.split(".");
	var myObj = obj;
	var name = namePath[namePath.length - 1];
	for (var j = 0, len = namePath.length; j < len; ++j) {
		var p = namePath[j];
		if (typeof (myObj[p]) == "undefined") {
			myObj[p] = {};
		}
		myObj = myObj[p];
	}
	return myObj;
}, _setToContainers:function (obj, widget) {
	for (var i = 0, len = widget.children.length; i < len; ++i) {
		var currentWidget = widget.children[i];
		if (currentWidget.widgetType == "Repeater") {
			for (var j = 0, len = currentWidget.getRowCount(); j < len; ++j) {
				currentWidget._initRow(j);
			}
		}
		if (currentWidget.isContainer) {
			this._setToContainers(obj, currentWidget);
			continue;
		}
		switch (currentWidget.widgetType) {
		  case "Checkbox":
			currentWidget.setValue(currentWidget.inputNode.checked);
			break;
		  case "DropdownDatePicker":
			currentWidget.setValue(currentWidget.getValue());
			break;
		  case "Select":
			continue;
			break;
		  case "ComboBox":
			continue;
			break;
		  default:
			break;
		}
	}
}, setValues:function (obj) {
	this._createFormElements();
	this._createRepeaters(obj, this);
	for (var i = 0, len = this.formElements.length; i < len; i++) {
		var element = this.formElements[i];
		if (element.name == "") {
			continue;
		}
		var namePath = new Array();
		namePath = element.name.split(".");
		var myObj = obj;
		var name = namePath[namePath.length - 1];
		for (var j = 1, len2 = namePath.length; j < len2; ++j) {
			var p = namePath[j - 1];
			if (typeof (myObj[p]) == "undefined") {
				myObj = undefined;
				break;
			}
			myObj = myObj[p];
		}
		if (typeof (myObj) == "undefined") {
			continue;
		}
		if (typeof (myObj[name]) == "undefined" && this.ignoreNullValues) {
			continue;
		}
		var type = element.type;
		if (type == "hidden" || type == "text" || type == "textarea" || type == "password") {
			type = "text";
		}
		switch (type) {
		  case "checkbox":
			element.checked = false;
			if (typeof (myObj[name]) == "undefined") {
				continue;
			}
			for (var j = 0, len2 = myObj[name].length; j < len2; ++j) {
				if (element.value == myObj[name][j]) {
					element.checked = true;
				}
			}
			break;
		  case "radio":
			element.checked = false;
			if (typeof (myObj[name]) == "undefined") {
				continue;
			}
			if (myObj[name] == element.value) {
				element.checked = true;
			}
			break;
		  case "select-multiple":
			element.selectedIndex = -1;
			for (var j = 0, len2 = element.options.length; j < len2; ++j) {
				for (var k = 0, len3 = myObj[name].length; k < len3; ++k) {
					if (element.options[j].value == myObj[name][k]) {
						element.options[j].selected = true;
					}
				}
			}
			break;
		  case "select-one":
			element.selectedIndex = "0";
			for (var j = 0, len2 = element.options.length; j < len2; ++j) {
				if (element.options[j].value == myObj[name]) {
					element.options[j].selected = true;
				} else {
				}
			}
			break;
		  case "text":
			var value = "";
			if (typeof (myObj[name]) != "undefined") {
				value = myObj[name];
			}
			element.value = value;
			break;
		  default:
			dojo.debug("Not supported type (" + type + ")");
			break;
		}
	}
	this._setToContainers(obj, this);
}, getValues:function () {
	this._createFormElements();
	var obj = {};
	for (var i = 0, len = this.formElements.length; i < len; i++) {
		var elm = this.formElements[i];
		var namePath = [];
		if (elm.name == "") {
			continue;
		}
		namePath = elm.name.split(".");
		var myObj = obj;
		var name = namePath[namePath.length - 1];
		for (var j = 1, len2 = namePath.length; j < len2; ++j) {
			var nameIndex = null;
			var p = namePath[j - 1];
			var nameA = p.split("[");
			if (nameA.length > 1) {
				if (typeof (myObj[nameA[0]]) == "undefined") {
					myObj[nameA[0]] = [];
				}
				nameIndex = parseInt(nameA[1]);
				if (typeof (myObj[nameA[0]][nameIndex]) == "undefined") {
					myObj[nameA[0]][nameIndex] = {};
				}
			} else {
				if (typeof (myObj[nameA[0]]) == "undefined") {
					myObj[nameA[0]] = {};
				}
			}
			if (nameA.length == 1) {
				myObj = myObj[nameA[0]];
			} else {
				myObj = myObj[nameA[0]][nameIndex];
			}
		}
		if ((elm.type != "select-multiple" && elm.type != "checkbox" && elm.type != "radio") || (elm.type == "radio" && elm.checked)) {
			if (name == name.split("[")[0]) {
				myObj[name] = elm.value;
			} else {
			}
		} else {
			if (elm.type == "checkbox" && elm.checked) {
				if (typeof (myObj[name]) == "undefined") {
					myObj[name] = [];
				}
				myObj[name].push(elm.value);
			} else {
				if (elm.type == "select-multiple") {
					if (typeof (myObj[name]) == "undefined") {
						myObj[name] = [];
					}
					for (var jdx = 0, len3 = elm.options.length; jdx < len3; ++jdx) {
						if (elm.options[jdx].selected) {
							myObj[name].push(elm.options[jdx].value);
						}
					}
				}
			}
		}
		name = undefined;
	}
	return obj;
}});

