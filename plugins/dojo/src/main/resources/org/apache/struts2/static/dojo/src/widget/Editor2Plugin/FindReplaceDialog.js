/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.Editor2Plugin.FindReplaceDialog");
dojo.widget.defineWidget("dojo.widget.Editor2FindDialog", dojo.widget.Editor2DialogContent, {templateString:"<table style=\"white-space: nowrap;\">\n<tr><td colspan='2'>Find: <input type=\"text\" dojoAttachPoint=\"find_text\" /></td></tr>\n<tr><td><input type=\"checkbox\" dojoType=\"CheckBox\" dojoAttachPoint=\"find_option_casesens\" />\n\t\t<label for=\"find_option_casesens\">Case Sensitive</label></td>\n\t\t\t<td><input type=\"checkbox\" dojoType=\"CheckBox\" dojoAttachPoint=\"find_option_backwards\" />\n\t\t<label for=\"find_option_backwards\">Search Backwards</label></td></tr>\n<tr><td style=\"display: none;\"><input type=\"checkbox\" dojoType=\"CheckBox\" dojoAttachPoint=\"find_option_wholeword\" />\n\t\t<label for=\"find_option_wholeword\">Whole Word</label></td>\n<tr><td colspan=\"1\">\n\t<table><tr>\n\t<td><button dojoType='Button' dojoAttachEvent='onClick:find'>Find</button></td>\n\t<td><button dojoType='Button' dojoAttachEvent='onClick:cancel'>Close</button></td>\n\t</tr></table>\n\t</td></tr>\n</table>\n", find:function () {
	var curInst = dojo.widget.Editor2Manager.getCurrentInstance();
	var findcmd = curInst.getCommand("find");
	var option = 0;
	if (this["find_option_casesens"].checked) {
		option |= findcmd.SearchOption.CaseSensitive;
	}
	if (this["find_option_backwards"].checked) {
		option |= findcmd.SearchOption.SearchBackwards;
	}
	if (this["find_option_wholeword"].checked) {
		option |= findcmd.SearchOption.WholeWord;
	}
	findcmd.find(this["find_text"].value, option);
}});
dojo.widget.defineWidget("dojo.widget.Editor2ReplaceDialog", dojo.widget.Editor2DialogContent, {templateString:"<table style=\"white-space: nowrap;\">\n<tr><td>Find: </td><td> <input type=\"text\" dojoAttachPoint=\"replace_text\" /></td></tr>\n<tr><td>Replace with: </td><td> <input type=\"text\" dojoAttachPoint=\"replace_text\" /></td></tr>\n<tr><td colspan='2'><table><tr><td><input type=\"checkbox\" dojoType=\"CheckBox\" dojoAttachPoint=\"replace_option_casesens\" id=\"dojo_replace_option_casesens\" />\n\t\t<label for=\"dojo_replace_option_casesens\">Case Sensitive</label></td>\n\t\t\t<td><input type=\"checkbox\" dojoType=\"CheckBox\" dojoAttachPoint=\"replace_option_backwards\" id=\"dojo_replace_option_backwards\" />\n\t\t<label for=\"dojo_replace_option_backwards\">Search Backwards</label></td></tr></table></td></tr>\n<tr><td colspan=2\">\n\t<table><tr>\n\t<td><button dojoType='Button' dojoAttachEvent='onClick:replace'>Replace</button></td>\n\t<td><button dojoType='Button' dojoAttachEvent='onClick:replaceAll'>Replace All</button></td>\n\t<td><button dojoType='Button' dojoAttachEvent='onClick:cancel'>Close</button></td>\n\t</tr></table>\n\t</td></tr>\n</table>\n", replace:function () {
	alert("not implemented yet");
}, replaceAll:function () {
	alert("not implemented yet");
}});

