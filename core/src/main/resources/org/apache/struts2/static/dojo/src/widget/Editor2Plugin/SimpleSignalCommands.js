/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

/*
 * This plugin adds save() and insertImage() to Editor2 widget, and two commands for each
 * of them. When the corresponding button is clicked in the toolbar, the added function in the
 * Editor2 widget is called. This mimics the original Editor2 behavior. If you want to have other
 * signals on the Editor2 widget, add them to dojo.widget.Editor2Plugin.SimpleSignalCommands.signals
 * NOTE: Please consider writing your own Editor2 plugin rather than using this backward compatible
 * plugin
 * ATTENTION: This plugin overwrites the new built-in insertImage dialog. (If this is not desired, set
 * dojo.widget.Editor2Plugin.SimpleSignalCommands.signals to not contain insertImage)
 */

//uncomment this line to add save only (do not overwrite the new built-in insertImage dialog
//this line should present before require dojo.widget.Editor2Plugin.SimpleSignalCommands
//dojo.widget.Editor2Plugin['SimpleSignalCommands'] = {signals: ['save']};

dojo.provide("dojo.widget.Editor2Plugin.SimpleSignalCommands");

dojo.require("dojo.widget.Editor2");

dojo.declare("dojo.widget.Editor2Plugin.SimpleSignalCommand", dojo.widget.Editor2Command,
	function(name){
		if(dojo.widget.Editor2.prototype[name] == undefined){
			dojo.widget.Editor2.prototype[name] = function(){ dojo.debug("Editor2::"+name); };
		}
	},
{
	execute: function(){
		var curInst = dojo.widget.Editor2Manager.getCurrentInstance();

		if(curInst){
			curInst[this._name]();
		}
	}
});

if(dojo.widget.Editor2Plugin['SimpleSignalCommands']){
	dojo.widget.Editor2Plugin['_SimpleSignalCommands']=dojo.widget.Editor2Plugin['SimpleSignalCommands'];
}

dojo.widget.Editor2Plugin.SimpleSignalCommands = {
	signals: ['save', 'insertImage'],
	Handler: function(name){
		if(name.toLowerCase() == 'save'){
			return new dojo.widget.Editor2ToolbarButton('Save');
		}else if(name.toLowerCase() == 'insertimage'){
			return new dojo.widget.Editor2ToolbarButton('InsertImage');
		}
	},
	registerAllSignalCommands: function(){
		for(var i=0;i<this.signals.length;i++){
			dojo.widget.Editor2Manager.registerCommand(this.signals[i],
				new dojo.widget.Editor2Plugin.SimpleSignalCommand(this.signals[i]));
		}
	}
};

if(dojo.widget.Editor2Plugin['_SimpleSignalCommands']){
	dojo.lang.mixin(dojo.widget.Editor2Plugin.SimpleSignalCommands, dojo.widget.Editor2Plugin['_SimpleSignalCommands']);
}

dojo.widget.Editor2Plugin.SimpleSignalCommands.registerAllSignalCommands();
dojo.widget.Editor2ToolbarItemManager.registerHandler(dojo.widget.Editor2Plugin.SimpleSignalCommands.Handler);