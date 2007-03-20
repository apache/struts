/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.Editor2Plugin.FindReplace");

dojo.require("dojo.widget.Editor2");

//TODO replace, better GUI

dojo.declare("dojo.widget.Editor2Plugin.FindCommand", dojo.widget.Editor2DialogCommand,{
	SearchOption: {
		CaseSensitive: 4,
		SearchBackwards: 64,
		WholeWord: 2,
		WrapSearch: 128
	},
	find: function(text, option){
		var curInst = dojo.widget.Editor2Manager.getCurrentInstance();
		if(curInst){
			curInst.focus();
			if(window.find){ //moz
				curInst.window.find(text, 
					option & this.SearchOption.CaseSensitive ? true : false,
					option & this.SearchOption.SearchBackwards ? true : false,
					option & this.SearchOption.WrapSearch ? true : false,
					option & this.SearchOption.WholeWord ? true : false
					);
			}else if(dojo.body().createTextRange){ //IE
				var range = curInst.document.body.createTextRange();
				var found = range.findText(text, (option&this.SearchOption.SearchBackwards)?1:-1, option );
				if(found){
					range.scrollIntoView() ;
					range.select() ;
				}else{
					alert("Can not find "+text+" in the document");
				}
			}else{
				alert("No idea how to search in this browser. Please submit patch if you know.");
			}
		}
	}
});

dojo.widget.Editor2Manager.registerCommand("Find", new dojo.widget.Editor2Plugin.FindCommand('find', 
		{contentFile: "dojo.widget.Editor2Plugin.FindReplaceDialog", 
			contentClass: "Editor2FindDialog",
			title: "Find", width: "350px", height: "150px", modal: false}));
dojo.widget.Editor2Manager.registerCommand("Replace", new dojo.widget.Editor2DialogCommand('replace', 
		{contentFile: "dojo.widget.Editor2Plugin.FindReplaceDialog", 
			contentClass: "Editor2ReplaceDialog",
			href: dojo.uri.dojoUri("src/widget/templates/Editor2/Dialog/replace.html"), 
			title: "Replace", width: "350px", height: "200px", modal: false}));

dojo.widget.Editor2Plugin.FindReplace = function(name){
	var name = name.toLowerCase();

	var item;
	if(name == 'replace'){
		item = new dojo.widget.Editor2ToolbarButton('Replace');
	}else if(name == 'find') {
		item = new dojo.widget.Editor2ToolbarButton('Find');
	}

	return item;
}

dojo.widget.Editor2ToolbarItemManager.registerHandler(dojo.widget.Editor2Plugin.FindReplace);