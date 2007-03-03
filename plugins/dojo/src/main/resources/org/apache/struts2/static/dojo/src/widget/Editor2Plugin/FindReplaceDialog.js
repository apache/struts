/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

﻿dojo.provide("dojo.widget.Editor2Plugin.FindReplaceDialog");

dojo.widget.defineWidget(
	"dojo.widget.Editor2FindDialog",
	dojo.widget.Editor2DialogContent,
{
	templatePath: dojo.uri.dojoUri("src/widget/templates/Editor2/Dialog/find.html"),

	find: function(){
		var findcmd = dojo.widget.Editor2Manager.getCommand('find');
		var option = 0;
	
		if(this["find_option_casesens"].checked){
			option |= findcmd.SearchOption.CaseSensitive;
		}
		if(this["find_option_backwards"].checked){
			option |= findcmd.SearchOption.SearchBackwards;
		}
	
		if(this["find_option_wholeword"].checked){
			option |= findcmd.SearchOption.WholeWord;
		}
		findcmd.find(this["find_text"].value, option);
	}
});

dojo.widget.defineWidget(
	"dojo.widget.Editor2ReplaceDialog",
	dojo.widget.Editor2DialogContent,
{
	templatePath: dojo.uri.dojoUri("src/widget/templates/Editor2/Dialog/replace.html"),

	replace: function(){
		alert("not implemented yet");
	},
	replaceAll: function(){
		alert("not implemented yet");
	}
});