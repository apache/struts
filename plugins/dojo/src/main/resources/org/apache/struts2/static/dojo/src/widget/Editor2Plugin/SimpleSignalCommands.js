/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.Editor2Plugin.SimpleSignalCommands");
dojo.require("dojo.widget.Editor2");
dojo.declare("dojo.widget.Editor2Plugin.SimpleSignalCommand", dojo.widget.Editor2Command, function (editor, name) {
	if (dojo.widget.Editor2.prototype[name] == undefined) {
		dojo.widget.Editor2.prototype[name] = function () {
		};
	}
}, {execute:function () {
	this._editor[this._name]();
}});
if (dojo.widget.Editor2Plugin["SimpleSignalCommands"]) {
	dojo.widget.Editor2Plugin["_SimpleSignalCommands"] = dojo.widget.Editor2Plugin["SimpleSignalCommands"];
}
dojo.widget.Editor2Plugin.SimpleSignalCommands = {signals:["save", "insertImage"], Handler:function (name) {
	if (name.toLowerCase() == "save") {
		return new dojo.widget.Editor2ToolbarButton("Save");
	} else {
		if (name.toLowerCase() == "insertimage") {
			return new dojo.widget.Editor2ToolbarButton("InsertImage");
		}
	}
}, getCommand:function (editor, name) {
	var signal;
	dojo.lang.every(this.signals, function (s) {
		if (s.toLowerCase() == name.toLowerCase()) {
			signal = s;
			return false;
		}
		return true;
	});
	if (signal) {
		return new dojo.widget.Editor2Plugin.SimpleSignalCommand(editor, signal);
	}
}};
if (dojo.widget.Editor2Plugin["_SimpleSignalCommands"]) {
	dojo.lang.mixin(dojo.widget.Editor2Plugin.SimpleSignalCommands, dojo.widget.Editor2Plugin["_SimpleSignalCommands"]);
}
dojo.widget.Editor2Manager.registerHandler(dojo.widget.Editor2Plugin.SimpleSignalCommands, "getCommand");
dojo.widget.Editor2ToolbarItemManager.registerHandler(dojo.widget.Editor2Plugin.SimpleSignalCommands.Handler);

