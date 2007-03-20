/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.Editor2Plugin.CreateLinkDialog");

dojo.widget.defineWidget(
	"dojo.widget.Editor2CreateLinkDialog",
	dojo.widget.Editor2DialogContent,
{
	templatePath: dojo.uri.dojoUri("src/widget/templates/Editor2/Dialog/createlink.html"),

	editableAttributes: ['href', 'target', 'class'],
	loadContent: function(){
		var curInst = dojo.widget.Editor2Manager.getCurrentInstance();

		curInst.saveSelection(); //save selection (none-activeX IE)

		this.linkNode = dojo.withGlobal(curInst.window, "getAncestorElement", dojo.html.selection, ['a']);
		var linkAttributes = {};
		this.extraAttribText = "";
		if(this.linkNode){
			var attrs = this.linkNode.attributes;
			for(var i=0; i<attrs.length; i++) {
				if(dojo.lang.find(this.editableAttributes, attrs[i].name.toLowerCase())>-1){
					linkAttributes[attrs[i].name] = attrs[i].value;
				}else{
					//IE lists all attributes, even default ones, filter them
					if(attrs[i].specified == undefined || attrs[i].specified){
						this.extraAttribText += attrs[i].name + '="'+attrs[i].value+'" ';
					}
				}
			}
		}else{
			var html = dojo.withGlobal(curInst.window, "getSelectedText", dojo.html.selection);
			if(html == null || html.length == 0){
				alert("Please select some text to create a link.");
				return false;//do not show the dialog
			}
		}

		for(var i=0; i<this.editableAttributes.length; ++i){
			name = this.editableAttributes[i];
			this["link_"+name].value = (linkAttributes[name] == undefined) ? "" : linkAttributes[name] ;
		}
		return true;
	},
	ok: function(){
		var curInst = dojo.widget.Editor2Manager.getCurrentInstance();
		curInst.restoreSelection(); //restore previous selection, required for none-activeX IE

		if(!this.linkNode){
			var html = dojo.withGlobal(curInst.window, "getSelectedHtml", dojo.html.selection);
		}else{
			var html = this.linkNode.innerHTML;
			dojo.withGlobal(curInst.window, "selectElement", dojo.html.selection, [this.linkNode]);
		}

		var attstr='';
		for(var i=0; i<this.editableAttributes.length; ++i){
			name = this.editableAttributes[i];
			var value = this["link_"+name].value;
			if(value.length > 0){
				attstr += name + '="'+value+'" ';
			}
		}

		curInst.execCommand('inserthtml', '<a '+attstr+this.extraAttribText+'>'+html+'</a>');

		this.cancel();
	}
});