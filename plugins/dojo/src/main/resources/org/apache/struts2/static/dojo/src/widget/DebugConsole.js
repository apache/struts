/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.DebugConsole");
dojo.require("dojo.widget.Widget");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.FloatingPane");
dojo.widget.defineWidget("dojo.widget.DebugConsole", dojo.widget.FloatingPane, {fillInTemplate:function () {
	dojo.widget.DebugConsole.superclass.fillInTemplate.apply(this, arguments);
	this.containerNode.id = "debugConsoleClientPane";
	djConfig.isDebug = true;
	djConfig.debugContainerId = this.containerNode.id;
}});

