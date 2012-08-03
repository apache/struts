/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.Editor2Toolbar");
dojo.require("dojo.lang.*");
dojo.require("dojo.widget.*");
dojo.require("dojo.event.*");
dojo.require("dojo.html.layout");
dojo.require("dojo.html.display");
dojo.require("dojo.widget.RichText");
dojo.require("dojo.widget.PopupContainer");
dojo.require("dojo.widget.ColorPalette");
dojo.lang.declare("dojo.widget.HandlerManager", null, function () {
	this._registeredHandlers = [];
}, {registerHandler:function (obj, func) {
	if (arguments.length == 2) {
		this._registeredHandlers.push(function () {
			return obj[func].apply(obj, arguments);
		});
	} else {
		this._registeredHandlers.push(obj);
	}
}, removeHandler:function (func) {
	for (var i = 0; i < this._registeredHandlers.length; i++) {
		if (func === this._registeredHandlers[i]) {
			delete this._registeredHandlers[i];
			return;
		}
	}
	dojo.debug("HandlerManager handler " + func + " is not registered, can not remove.");
}, destroy:function () {
	for (var i = 0; i < this._registeredHandlers.length; i++) {
		delete this._registeredHandlers[i];
	}
}});
dojo.widget.Editor2ToolbarItemManager = new dojo.widget.HandlerManager;
dojo.lang.mixin(dojo.widget.Editor2ToolbarItemManager, {getToolbarItem:function (name) {
	var item;
	name = name.toLowerCase();
	for (var i = 0; i < this._registeredHandlers.length; i++) {
		item = this._registeredHandlers[i](name);
		if (item) {
			return item;
		}
	}
	switch (name) {
	  case "bold":
	  case "copy":
	  case "cut":
	  case "delete":
	  case "indent":
	  case "inserthorizontalrule":
	  case "insertorderedlist":
	  case "insertunorderedlist":
	  case "italic":
	  case "justifycenter":
	  case "justifyfull":
	  case "justifyleft":
	  case "justifyright":
	  case "outdent":
	  case "paste":
	  case "redo":
	  case "removeformat":
	  case "selectall":
	  case "strikethrough":
	  case "subscript":
	  case "superscript":
	  case "underline":
	  case "undo":
	  case "unlink":
	  case "createlink":
	  case "insertimage":
	  case "htmltoggle":
		item = new dojo.widget.Editor2ToolbarButton(name);
		break;
	  case "forecolor":
	  case "hilitecolor":
		item = new dojo.widget.Editor2ToolbarColorPaletteButton(name);
		break;
	  case "plainformatblock":
		item = new dojo.widget.Editor2ToolbarFormatBlockPlainSelect("formatblock");
		break;
	  case "formatblock":
		item = new dojo.widget.Editor2ToolbarFormatBlockSelect("formatblock");
		break;
	  case "fontsize":
		item = new dojo.widget.Editor2ToolbarFontSizeSelect("fontsize");
		break;
	  case "fontname":
		item = new dojo.widget.Editor2ToolbarFontNameSelect("fontname");
		break;
	  case "inserttable":
	  case "insertcell":
	  case "insertcol":
	  case "insertrow":
	  case "deletecells":
	  case "deletecols":
	  case "deleterows":
	  case "mergecells":
	  case "splitcell":
		dojo.debug(name + " is implemented in dojo.widget.Editor2Plugin.TableOperation, please require it first.");
		break;
	  case "inserthtml":
	  case "blockdirltr":
	  case "blockdirrtl":
	  case "dirltr":
	  case "dirrtl":
	  case "inlinedirltr":
	  case "inlinedirrtl":
		dojo.debug("Not yet implemented toolbar item: " + name);
		break;
	  default:
		dojo.debug("dojo.widget.Editor2ToolbarItemManager.getToolbarItem: Unknown toolbar item: " + name);
	}
	return item;
}});
dojo.addOnUnload(dojo.widget.Editor2ToolbarItemManager, "destroy");
dojo.declare("dojo.widget.Editor2ToolbarButton", null, function (name) {
	this._name = name;
}, {create:function (node, toolbar, nohover) {
	this._domNode = node;
	var cmd = toolbar.parent.getCommand(this._name);
	if (cmd) {
		this._domNode.title = cmd.getText();
	}
	this.disableSelection(this._domNode);
	this._parentToolbar = toolbar;
	dojo.event.connect(this._domNode, "onclick", this, "onClick");
	if (!nohover) {
		dojo.event.connect(this._domNode, "onmouseover", this, "onMouseOver");
		dojo.event.connect(this._domNode, "onmouseout", this, "onMouseOut");
	}
}, disableSelection:function (rootnode) {
	dojo.html.disableSelection(rootnode);
	var nodes = rootnode.all || rootnode.getElementsByTagName("*");
	for (var x = 0; x < nodes.length; x++) {
		dojo.html.disableSelection(nodes[x]);
	}
}, onMouseOver:function () {
	var curInst = dojo.widget.Editor2Manager.getCurrentInstance();
	if (curInst) {
		var _command = curInst.getCommand(this._name);
		if (_command && _command.getState() != dojo.widget.Editor2Manager.commandState.Disabled) {
			this.highlightToolbarItem();
		}
	}
}, onMouseOut:function () {
	this.unhighlightToolbarItem();
}, destroy:function () {
	this._domNode = null;
	this._parentToolbar = null;
}, onClick:function (e) {
	if (this._domNode && !this._domNode.disabled && this._parentToolbar.checkAvailability()) {
		e.preventDefault();
		e.stopPropagation();
		var curInst = dojo.widget.Editor2Manager.getCurrentInstance();
		if (curInst) {
			var _command = curInst.getCommand(this._name);
			if (_command) {
				_command.execute();
			}
		}
	}
}, refreshState:function () {
	var curInst = dojo.widget.Editor2Manager.getCurrentInstance();
	var em = dojo.widget.Editor2Manager;
	if (curInst) {
		var _command = curInst.getCommand(this._name);
		if (_command) {
			var state = _command.getState();
			if (state != this._lastState) {
				switch (state) {
				  case em.commandState.Latched:
					this.latchToolbarItem();
					break;
				  case em.commandState.Enabled:
					this.enableToolbarItem();
					break;
				  case em.commandState.Disabled:
				  default:
					this.disableToolbarItem();
				}
				this._lastState = state;
			}
		}
	}
	return em.commandState.Enabled;
}, latchToolbarItem:function () {
	this._domNode.disabled = false;
	this.removeToolbarItemStyle(this._domNode);
	dojo.html.addClass(this._domNode, this._parentToolbar.ToolbarLatchedItemStyle);
}, enableToolbarItem:function () {
	this._domNode.disabled = false;
	this.removeToolbarItemStyle(this._domNode);
	dojo.html.addClass(this._domNode, this._parentToolbar.ToolbarEnabledItemStyle);
}, disableToolbarItem:function () {
	this._domNode.disabled = true;
	this.removeToolbarItemStyle(this._domNode);
	dojo.html.addClass(this._domNode, this._parentToolbar.ToolbarDisabledItemStyle);
}, highlightToolbarItem:function () {
	dojo.html.addClass(this._domNode, this._parentToolbar.ToolbarHighlightedItemStyle);
}, unhighlightToolbarItem:function () {
	dojo.html.removeClass(this._domNode, this._parentToolbar.ToolbarHighlightedItemStyle);
}, removeToolbarItemStyle:function () {
	dojo.html.removeClass(this._domNode, this._parentToolbar.ToolbarEnabledItemStyle);
	dojo.html.removeClass(this._domNode, this._parentToolbar.ToolbarLatchedItemStyle);
	dojo.html.removeClass(this._domNode, this._parentToolbar.ToolbarDisabledItemStyle);
	this.unhighlightToolbarItem();
}});
dojo.declare("dojo.widget.Editor2ToolbarDropDownButton", dojo.widget.Editor2ToolbarButton, {onClick:function () {
	if (this._domNode && !this._domNode.disabled && this._parentToolbar.checkAvailability()) {
		if (!this._dropdown) {
			this._dropdown = dojo.widget.createWidget("PopupContainer", {});
			this._domNode.appendChild(this._dropdown.domNode);
		}
		if (this._dropdown.isShowingNow) {
			this._dropdown.close();
		} else {
			this.onDropDownShown();
			this._dropdown.open(this._domNode, null, this._domNode);
		}
	}
}, destroy:function () {
	this.onDropDownDestroy();
	if (this._dropdown) {
		this._dropdown.destroy();
	}
	dojo.widget.Editor2ToolbarDropDownButton.superclass.destroy.call(this);
}, onDropDownShown:function () {
}, onDropDownDestroy:function () {
}});
dojo.declare("dojo.widget.Editor2ToolbarColorPaletteButton", dojo.widget.Editor2ToolbarDropDownButton, {onDropDownShown:function () {
	if (!this._colorpalette) {
		this._colorpalette = dojo.widget.createWidget("ColorPalette", {});
		this._dropdown.addChild(this._colorpalette);
		this.disableSelection(this._dropdown.domNode);
		this.disableSelection(this._colorpalette.domNode);
		dojo.event.connect(this._colorpalette, "onColorSelect", this, "setColor");
		dojo.event.connect(this._dropdown, "open", this, "latchToolbarItem");
		dojo.event.connect(this._dropdown, "close", this, "enableToolbarItem");
	}
}, setColor:function (color) {
	this._dropdown.close();
	var curInst = dojo.widget.Editor2Manager.getCurrentInstance();
	if (curInst) {
		var _command = curInst.getCommand(this._name);
		if (_command) {
			_command.execute(color);
		}
	}
}});
dojo.declare("dojo.widget.Editor2ToolbarFormatBlockPlainSelect", dojo.widget.Editor2ToolbarButton, {create:function (node, toolbar) {
	this._domNode = node;
	this._parentToolbar = toolbar;
	this._domNode = node;
	this.disableSelection(this._domNode);
	dojo.event.connect(this._domNode, "onchange", this, "onChange");
}, destroy:function () {
	this._domNode = null;
}, onChange:function () {
	if (this._parentToolbar.checkAvailability()) {
		var sv = this._domNode.value.toLowerCase();
		var curInst = dojo.widget.Editor2Manager.getCurrentInstance();
		if (curInst) {
			var _command = curInst.getCommand(this._name);
			if (_command) {
				_command.execute(sv);
			}
		}
	}
}, refreshState:function () {
	if (this._domNode) {
		dojo.widget.Editor2ToolbarFormatBlockPlainSelect.superclass.refreshState.call(this);
		var curInst = dojo.widget.Editor2Manager.getCurrentInstance();
		if (curInst) {
			var _command = curInst.getCommand(this._name);
			if (_command) {
				var format = _command.getValue();
				if (!format) {
					format = "";
				}
				dojo.lang.forEach(this._domNode.options, function (item) {
					if (item.value.toLowerCase() == format.toLowerCase()) {
						item.selected = true;
					}
				});
			}
		}
	}
}});
dojo.declare("dojo.widget.Editor2ToolbarComboItem", dojo.widget.Editor2ToolbarDropDownButton, {href:null, create:function (node, toolbar) {
	dojo.widget.Editor2ToolbarComboItem.superclass.create.apply(this, arguments);
	if (!this._contentPane) {
		dojo.require("dojo.widget.ContentPane");
		this._contentPane = dojo.widget.createWidget("ContentPane", {preload:"true"});
		this._contentPane.addOnLoad(this, "setup");
		this._contentPane.setUrl(this.href);
	}
}, onMouseOver:function (e) {
	if (this._lastState != dojo.widget.Editor2Manager.commandState.Disabled) {
		dojo.html.addClass(e.currentTarget, this._parentToolbar.ToolbarHighlightedSelectStyle);
	}
}, onMouseOut:function (e) {
	dojo.html.removeClass(e.currentTarget, this._parentToolbar.ToolbarHighlightedSelectStyle);
}, onDropDownShown:function () {
	if (!this._dropdown.__addedContentPage) {
		this._dropdown.addChild(this._contentPane);
		this._dropdown.__addedContentPage = true;
	}
}, setup:function () {
}, onChange:function (e) {
	if (this._parentToolbar.checkAvailability()) {
		var name = e.currentTarget.getAttribute("dropDownItemName");
		var curInst = dojo.widget.Editor2Manager.getCurrentInstance();
		if (curInst) {
			var _command = curInst.getCommand(this._name);
			if (_command) {
				_command.execute(name);
			}
		}
	}
	this._dropdown.close();
}, onMouseOverItem:function (e) {
	dojo.html.addClass(e.currentTarget, this._parentToolbar.ToolbarHighlightedSelectItemStyle);
}, onMouseOutItem:function (e) {
	dojo.html.removeClass(e.currentTarget, this._parentToolbar.ToolbarHighlightedSelectItemStyle);
}});
dojo.declare("dojo.widget.Editor2ToolbarFormatBlockSelect", dojo.widget.Editor2ToolbarComboItem, {href:dojo.uri.moduleUri("dojo.widget", "templates/Editor2/EditorToolbar_FormatBlock.html"), setup:function () {
	dojo.widget.Editor2ToolbarFormatBlockSelect.superclass.setup.call(this);
	var nodes = this._contentPane.domNode.all || this._contentPane.domNode.getElementsByTagName("*");
	this._blockNames = {};
	this._blockDisplayNames = {};
	for (var x = 0; x < nodes.length; x++) {
		var node = nodes[x];
		dojo.html.disableSelection(node);
		var name = node.getAttribute("dropDownItemName");
		if (name) {
			this._blockNames[name] = node;
			var childrennodes = node.getElementsByTagName(name);
			this._blockDisplayNames[name] = childrennodes[childrennodes.length - 1].innerHTML;
		}
	}
	for (var name in this._blockNames) {
		dojo.event.connect(this._blockNames[name], "onclick", this, "onChange");
		dojo.event.connect(this._blockNames[name], "onmouseover", this, "onMouseOverItem");
		dojo.event.connect(this._blockNames[name], "onmouseout", this, "onMouseOutItem");
	}
}, onDropDownDestroy:function () {
	if (this._blockNames) {
		for (var name in this._blockNames) {
			delete this._blockNames[name];
			delete this._blockDisplayNames[name];
		}
	}
}, refreshState:function () {
	dojo.widget.Editor2ToolbarFormatBlockSelect.superclass.refreshState.call(this);
	if (this._lastState != dojo.widget.Editor2Manager.commandState.Disabled) {
		var curInst = dojo.widget.Editor2Manager.getCurrentInstance();
		if (curInst) {
			var _command = curInst.getCommand(this._name);
			if (_command) {
				var format = _command.getValue();
				if (format == this._lastSelectedFormat && this._blockDisplayNames) {
					return this._lastState;
				}
				this._lastSelectedFormat = format;
				var label = this._domNode.getElementsByTagName("label")[0];
				var isSet = false;
				if (this._blockDisplayNames) {
					for (var name in this._blockDisplayNames) {
						if (name == format) {
							label.innerHTML = this._blockDisplayNames[name];
							isSet = true;
							break;
						}
					}
					if (!isSet) {
						label.innerHTML = "&nbsp;";
					}
				}
			}
		}
	}
	return this._lastState;
}});
dojo.declare("dojo.widget.Editor2ToolbarFontSizeSelect", dojo.widget.Editor2ToolbarComboItem, {href:dojo.uri.moduleUri("dojo.widget", "templates/Editor2/EditorToolbar_FontSize.html"), setup:function () {
	dojo.widget.Editor2ToolbarFormatBlockSelect.superclass.setup.call(this);
	var nodes = this._contentPane.domNode.all || this._contentPane.domNode.getElementsByTagName("*");
	this._fontsizes = {};
	this._fontSizeDisplayNames = {};
	for (var x = 0; x < nodes.length; x++) {
		var node = nodes[x];
		dojo.html.disableSelection(node);
		var name = node.getAttribute("dropDownItemName");
		if (name) {
			this._fontsizes[name] = node;
			this._fontSizeDisplayNames[name] = node.getElementsByTagName("font")[0].innerHTML;
		}
	}
	for (var name in this._fontsizes) {
		dojo.event.connect(this._fontsizes[name], "onclick", this, "onChange");
		dojo.event.connect(this._fontsizes[name], "onmouseover", this, "onMouseOverItem");
		dojo.event.connect(this._fontsizes[name], "onmouseout", this, "onMouseOutItem");
	}
}, onDropDownDestroy:function () {
	if (this._fontsizes) {
		for (var name in this._fontsizes) {
			delete this._fontsizes[name];
			delete this._fontSizeDisplayNames[name];
		}
	}
}, refreshState:function () {
	dojo.widget.Editor2ToolbarFormatBlockSelect.superclass.refreshState.call(this);
	if (this._lastState != dojo.widget.Editor2Manager.commandState.Disabled) {
		var curInst = dojo.widget.Editor2Manager.getCurrentInstance();
		if (curInst) {
			var _command = curInst.getCommand(this._name);
			if (_command) {
				var size = _command.getValue();
				if (size == this._lastSelectedSize && this._fontSizeDisplayNames) {
					return this._lastState;
				}
				this._lastSelectedSize = size;
				var label = this._domNode.getElementsByTagName("label")[0];
				var isSet = false;
				if (this._fontSizeDisplayNames) {
					for (var name in this._fontSizeDisplayNames) {
						if (name == size) {
							label.innerHTML = this._fontSizeDisplayNames[name];
							isSet = true;
							break;
						}
					}
					if (!isSet) {
						label.innerHTML = "&nbsp;";
					}
				}
			}
		}
	}
	return this._lastState;
}});
dojo.declare("dojo.widget.Editor2ToolbarFontNameSelect", dojo.widget.Editor2ToolbarFontSizeSelect, {href:dojo.uri.moduleUri("dojo.widget", "templates/Editor2/EditorToolbar_FontName.html")});
dojo.widget.defineWidget("dojo.widget.Editor2Toolbar", dojo.widget.HtmlWidget, function () {
	dojo.event.connect(this, "fillInTemplate", dojo.lang.hitch(this, function () {
		if (dojo.render.html.ie) {
			this.domNode.style.zoom = 1;
		}
	}));
}, {templateString:"<div dojoAttachPoint=\"domNode\" class=\"EditorToolbarDomNode\" unselectable=\"on\">\n\t<table cellpadding=\"3\" cellspacing=\"0\" border=\"0\">\n\t\t<!--\n\t\t\tour toolbar should look something like:\n\n\t\t\t+=======+=======+=======+=============================================+\n\t\t\t| w   w | style | copy  | bo | it | un | le | ce | ri |\n\t\t\t| w w w | style |=======|==============|==============|\n\t\t\t|  w w  | style | paste |  undo | redo | change style |\n\t\t\t+=======+=======+=======+=============================================+\n\t\t-->\n\t\t<tbody>\n\t\t\t<tr valign=\"top\">\n\t\t\t\t<td rowspan=\"2\">\n\t\t\t\t\t<div class=\"bigIcon\" dojoAttachPoint=\"wikiWordButton\"\n\t\t\t\t\t\tdojoOnClick=\"wikiWordClick; buttonClick;\">\n\t\t\t\t\t\t<span style=\"font-size: 30px; margin-left: 5px;\">\n\t\t\t\t\t\t\tW\n\t\t\t\t\t\t</span>\n\t\t\t\t\t</div>\n\t\t\t\t</td>\n\t\t\t\t<td rowspan=\"2\">\n\t\t\t\t\t<div class=\"bigIcon\" dojoAttachPoint=\"styleDropdownButton\"\n\t\t\t\t\t\tdojoOnClick=\"styleDropdownClick; buttonClick;\">\n\t\t\t\t\t\t<span unselectable=\"on\"\n\t\t\t\t\t\t\tstyle=\"font-size: 30px; margin-left: 5px;\">\n\t\t\t\t\t\t\tS\n\t\t\t\t\t\t</span>\n\t\t\t\t\t</div>\n\t\t\t\t\t<div class=\"StyleDropdownContainer\" style=\"display: none;\"\n\t\t\t\t\t\tdojoAttachPoint=\"styleDropdownContainer\">\n\t\t\t\t\t\t<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"\n\t\t\t\t\t\t\theight=\"100%\" width=\"100%\">\n\t\t\t\t\t\t\t<tr valign=\"top\">\n\t\t\t\t\t\t\t\t<td rowspan=\"2\">\n\t\t\t\t\t\t\t\t\t<div style=\"height: 245px; overflow: auto;\">\n\t\t\t\t\t\t\t\t\t\t<div class=\"headingContainer\"\n\t\t\t\t\t\t\t\t\t\t\tunselectable=\"on\"\n\t\t\t\t\t\t\t\t\t\t\tdojoOnClick=\"normalTextClick\">normal</div>\n\t\t\t\t\t\t\t\t\t\t<h1 class=\"headingContainer\"\n\t\t\t\t\t\t\t\t\t\t\tunselectable=\"on\"\n\t\t\t\t\t\t\t\t\t\t\tdojoOnClick=\"h1TextClick\">Heading 1</h1>\n\t\t\t\t\t\t\t\t\t\t<h2 class=\"headingContainer\"\n\t\t\t\t\t\t\t\t\t\t\tunselectable=\"on\"\n\t\t\t\t\t\t\t\t\t\t\tdojoOnClick=\"h2TextClick\">Heading 2</h2>\n\t\t\t\t\t\t\t\t\t\t<h3 class=\"headingContainer\"\n\t\t\t\t\t\t\t\t\t\t\tunselectable=\"on\"\n\t\t\t\t\t\t\t\t\t\t\tdojoOnClick=\"h3TextClick\">Heading 3</h3>\n\t\t\t\t\t\t\t\t\t\t<h4 class=\"headingContainer\"\n\t\t\t\t\t\t\t\t\t\t\tunselectable=\"on\"\n\t\t\t\t\t\t\t\t\t\t\tdojoOnClick=\"h4TextClick\">Heading 4</h4>\n\t\t\t\t\t\t\t\t\t\t<div class=\"headingContainer\"\n\t\t\t\t\t\t\t\t\t\t\tunselectable=\"on\"\n\t\t\t\t\t\t\t\t\t\t\tdojoOnClick=\"blahTextClick\">blah</div>\n\t\t\t\t\t\t\t\t\t\t<div class=\"headingContainer\"\n\t\t\t\t\t\t\t\t\t\t\tunselectable=\"on\"\n\t\t\t\t\t\t\t\t\t\t\tdojoOnClick=\"blahTextClick\">blah</div>\n\t\t\t\t\t\t\t\t\t\t<div class=\"headingContainer\"\n\t\t\t\t\t\t\t\t\t\t\tunselectable=\"on\"\n\t\t\t\t\t\t\t\t\t\t\tdojoOnClick=\"blahTextClick\">blah</div>\n\t\t\t\t\t\t\t\t\t\t<div class=\"headingContainer\">blah</div>\n\t\t\t\t\t\t\t\t\t\t<div class=\"headingContainer\">blah</div>\n\t\t\t\t\t\t\t\t\t\t<div class=\"headingContainer\">blah</div>\n\t\t\t\t\t\t\t\t\t\t<div class=\"headingContainer\">blah</div>\n\t\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t<!--\n\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t<span class=\"iconContainer\" dojoOnClick=\"buttonClick;\">\n\t\t\t\t\t\t\t\t\t\t<span class=\"icon justifyleft\" \n\t\t\t\t\t\t\t\t\t\t\tstyle=\"float: left;\">&nbsp;</span>\n\t\t\t\t\t\t\t\t\t</span>\n\t\t\t\t\t\t\t\t\t<span class=\"iconContainer\" dojoOnClick=\"buttonClick;\">\n\t\t\t\t\t\t\t\t\t\t<span class=\"icon justifycenter\" \n\t\t\t\t\t\t\t\t\t\t\tstyle=\"float: left;\">&nbsp;</span>\n\t\t\t\t\t\t\t\t\t</span>\n\t\t\t\t\t\t\t\t\t<span class=\"iconContainer\" dojoOnClick=\"buttonClick;\">\n\t\t\t\t\t\t\t\t\t\t<span class=\"icon justifyright\" \n\t\t\t\t\t\t\t\t\t\t\tstyle=\"float: left;\">&nbsp;</span>\n\t\t\t\t\t\t\t\t\t</span>\n\t\t\t\t\t\t\t\t\t<span class=\"iconContainer\" dojoOnClick=\"buttonClick;\">\n\t\t\t\t\t\t\t\t\t\t<span class=\"icon justifyfull\" \n\t\t\t\t\t\t\t\t\t\t\tstyle=\"float: left;\">&nbsp;</span>\n\t\t\t\t\t\t\t\t\t</span>\n\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t-->\n\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t<tr valign=\"top\">\n\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\tthud\n\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t</table>\n\t\t\t\t\t</div>\n\t\t\t\t</td>\n\t\t\t\t<td>\n\t\t\t\t\t<!-- copy -->\n\t\t\t\t\t<span class=\"iconContainer\" dojoAttachPoint=\"copyButton\"\n\t\t\t\t\t\tunselectable=\"on\"\n\t\t\t\t\t\tdojoOnClick=\"copyClick; buttonClick;\">\n\t\t\t\t\t\t<span class=\"icon copy\" \n\t\t\t\t\t\t\tunselectable=\"on\"\n\t\t\t\t\t\t\tstyle=\"float: left;\">&nbsp;</span> copy\n\t\t\t\t\t</span>\n\t\t\t\t\t<!-- \"droppable\" options -->\n\t\t\t\t\t<span class=\"iconContainer\" dojoAttachPoint=\"boldButton\"\n\t\t\t\t\t\tunselectable=\"on\"\n\t\t\t\t\t\tdojoOnClick=\"boldClick; buttonClick;\">\n\t\t\t\t\t\t<span class=\"icon bold\" unselectable=\"on\">&nbsp;</span>\n\t\t\t\t\t</span>\n\t\t\t\t\t<span class=\"iconContainer\" dojoAttachPoint=\"italicButton\"\n\t\t\t\t\t\tdojoOnClick=\"italicClick; buttonClick;\">\n\t\t\t\t\t\t<span class=\"icon italic\" unselectable=\"on\">&nbsp;</span>\n\t\t\t\t\t</span>\n\t\t\t\t\t<span class=\"iconContainer\" dojoAttachPoint=\"underlineButton\"\n\t\t\t\t\t\tdojoOnClick=\"underlineClick; buttonClick;\">\n\t\t\t\t\t\t<span class=\"icon underline\" unselectable=\"on\">&nbsp;</span>\n\t\t\t\t\t</span>\n\t\t\t\t\t<span class=\"iconContainer\" dojoAttachPoint=\"leftButton\"\n\t\t\t\t\t\tdojoOnClick=\"leftClick; buttonClick;\">\n\t\t\t\t\t\t<span class=\"icon justifyleft\" unselectable=\"on\">&nbsp;</span>\n\t\t\t\t\t</span>\n\t\t\t\t\t<span class=\"iconContainer\" dojoAttachPoint=\"fullButton\"\n\t\t\t\t\t\tdojoOnClick=\"fullClick; buttonClick;\">\n\t\t\t\t\t\t<span class=\"icon justifyfull\" unselectable=\"on\">&nbsp;</span>\n\t\t\t\t\t</span>\n\t\t\t\t\t<span class=\"iconContainer\" dojoAttachPoint=\"rightButton\"\n\t\t\t\t\t\tdojoOnClick=\"rightClick; buttonClick;\">\n\t\t\t\t\t\t<span class=\"icon justifyright\" unselectable=\"on\">&nbsp;</span>\n\t\t\t\t\t</span>\n\t\t\t\t</td>\n\t\t\t</tr>\n\t\t\t<tr>\n\t\t\t\t<td>\n\t\t\t\t\t<!-- paste -->\n\t\t\t\t\t<span class=\"iconContainer\" dojoAttachPoint=\"pasteButton\"\n\t\t\t\t\t\tdojoOnClick=\"pasteClick; buttonClick;\" unselectable=\"on\">\n\t\t\t\t\t\t<span class=\"icon paste\" style=\"float: left;\" unselectable=\"on\">&nbsp;</span> paste\n\t\t\t\t\t</span>\n\t\t\t\t\t<!-- \"droppable\" options -->\n\t\t\t\t\t<span class=\"iconContainer\" dojoAttachPoint=\"undoButton\"\n\t\t\t\t\t\tdojoOnClick=\"undoClick; buttonClick;\" unselectable=\"on\">\n\t\t\t\t\t\t<span class=\"icon undo\" style=\"float: left;\" unselectable=\"on\">&nbsp;</span> undo\n\t\t\t\t\t</span>\n\t\t\t\t\t<span class=\"iconContainer\" dojoAttachPoint=\"redoButton\"\n\t\t\t\t\t\tdojoOnClick=\"redoClick; buttonClick;\" unselectable=\"on\">\n\t\t\t\t\t\t<span class=\"icon redo\" style=\"float: left;\" unselectable=\"on\">&nbsp;</span> redo\n\t\t\t\t\t</span>\n\t\t\t\t</td>\t\n\t\t\t</tr>\n\t\t</tbody>\n\t</table>\n</div>\n", templateCssString:".StyleDropdownContainer {\n\tposition: absolute;\n\tz-index: 1000;\n\toverflow: auto;\n\tcursor: default;\n\twidth: 250px;\n\theight: 250px;\n\tbackground-color: white;\n\tborder: 1px solid black;\n}\n\n.ColorDropdownContainer {\n\tposition: absolute;\n\tz-index: 1000;\n\toverflow: auto;\n\tcursor: default;\n\twidth: 250px;\n\theight: 150px;\n\tbackground-color: white;\n\tborder: 1px solid black;\n}\n\n.EditorToolbarDomNode {\n\tbackground-image: url(buttons/bg-fade.png);\n\tbackground-repeat: repeat-x;\n\tbackground-position: 0px -50px;\n}\n\n.EditorToolbarSmallBg {\n\tbackground-image: url(images/toolbar-bg.gif);\n\tbackground-repeat: repeat-x;\n\tbackground-position: 0px 0px;\n}\n\n/*\nbody {\n\tbackground:url(images/blank.gif) fixed;\n}*/\n\n.IEFixedToolbar {\n\tposition:absolute;\n\t/* top:0; */\n\ttop: expression(eval((document.documentElement||document.body).scrollTop));\n}\n\ndiv.bigIcon {\n\twidth: 40px;\n\theight: 40px; \n\t/* background-color: white; */\n\t/* border: 1px solid #a6a7a3; */\n\tfont-family: Verdana, Trebuchet, Tahoma, Arial;\n}\n\n.iconContainer {\n\tfont-family: Verdana, Trebuchet, Tahoma, Arial;\n\tfont-size: 13px;\n\tfloat: left;\n\theight: 18px;\n\tdisplay: block;\n\t/* background-color: white; */\n\tcursor: pointer;\n\tpadding: 1px 4px 1px 1px; /* almost the same as a transparent border */\n\tborder: 0px;\n}\n\n.dojoE2TBIcon {\n\tdisplay: block;\n\ttext-align: center;\n\tmin-width: 18px;\n\twidth: 18px;\n\theight: 18px;\n\t/* background-color: #a6a7a3; */\n\tbackground-repeat: no-repeat;\n\tbackground-image: url(buttons/aggregate.gif);\n}\n\n\n.dojoE2TBIcon[class~=dojoE2TBIcon] {\n}\n\n.ToolbarButtonLatched {\n	border: #316ac5 1px solid; !important;\n	padding: 0px 3px 0px 0px; !important; /* make room for border */\n	background-color: #c1d2ee;\n}\n\n.ToolbarButtonHighlighted {\n	border: #316ac5 1px solid; !important;\n	padding: 0px 3px 0px 0px; !important; /* make room for border */\n	background-color: #dff1ff;\n}\n\n.ToolbarButtonDisabled{\n	filter: gray() alpha(opacity=30); /* IE */\n	opacity: 0.30; /* Safari, Opera and Mozilla */\n}\n\n.headingContainer {\n\twidth: 150px;\n\theight: 30px;\n\tmargin: 0px;\n\t/* padding-left: 5px; */\n\toverflow: hidden;\n\tline-height: 25px;\n\tborder-bottom: 1px solid black;\n\tborder-top: 1px solid white;\n}\n\n.EditorToolbarDomNode select {\n\tfont-size: 14px;\n}\n \n.dojoE2TBIcon_Sep { width: 5px; min-width: 5px; max-width: 5px; background-position: 0px 0px}\n.dojoE2TBIcon_Backcolor { background-position: -18px 0px}\n.dojoE2TBIcon_Bold { background-position: -36px 0px}\n.dojoE2TBIcon_Cancel { background-position: -54px 0px}\n.dojoE2TBIcon_Copy { background-position: -72px 0px}\n.dojoE2TBIcon_Link { background-position: -90px 0px}\n.dojoE2TBIcon_Cut { background-position: -108px 0px}\n.dojoE2TBIcon_Delete { background-position: -126px 0px}\n.dojoE2TBIcon_TextColor { background-position: -144px 0px}\n.dojoE2TBIcon_BackgroundColor { background-position: -162px 0px}\n.dojoE2TBIcon_Indent { background-position: -180px 0px}\n.dojoE2TBIcon_HorizontalLine { background-position: -198px 0px}\n.dojoE2TBIcon_Image { background-position: -216px 0px}\n.dojoE2TBIcon_NumberedList { background-position: -234px 0px}\n.dojoE2TBIcon_Table { background-position: -252px 0px}\n.dojoE2TBIcon_BulletedList { background-position: -270px 0px}\n.dojoE2TBIcon_Italic { background-position: -288px 0px}\n.dojoE2TBIcon_CenterJustify { background-position: -306px 0px}\n.dojoE2TBIcon_BlockJustify { background-position: -324px 0px}\n.dojoE2TBIcon_LeftJustify { background-position: -342px 0px}\n.dojoE2TBIcon_RightJustify { background-position: -360px 0px}\n.dojoE2TBIcon_left_to_right { background-position: -378px 0px}\n.dojoE2TBIcon_list_bullet_indent { background-position: -396px 0px}\n.dojoE2TBIcon_list_bullet_outdent { background-position: -414px 0px}\n.dojoE2TBIcon_list_num_indent { background-position: -432px 0px}\n.dojoE2TBIcon_list_num_outdent { background-position: -450px 0px}\n.dojoE2TBIcon_Outdent { background-position: -468px 0px}\n.dojoE2TBIcon_Paste { background-position: -486px 0px}\n.dojoE2TBIcon_Redo { background-position: -504px 0px}\ndojoE2TBIcon_RemoveFormat { background-position: -522px 0px}\n.dojoE2TBIcon_right_to_left { background-position: -540px 0px}\n.dojoE2TBIcon_Save { background-position: -558px 0px}\n.dojoE2TBIcon_Space { background-position: -576px 0px}\n.dojoE2TBIcon_StrikeThrough { background-position: -594px 0px}\n.dojoE2TBIcon_Subscript { background-position: -612px 0px}\n.dojoE2TBIcon_Superscript { background-position: -630px 0px}\n.dojoE2TBIcon_Underline { background-position: -648px 0px}\n.dojoE2TBIcon_Undo { background-position: -666px 0px}\n.dojoE2TBIcon_WikiWord { background-position: -684px 0px}\n\n", templateCssPath:dojo.uri.moduleUri("dojo.widget", "templates/EditorToolbar.css"), ToolbarLatchedItemStyle:"ToolbarButtonLatched", ToolbarEnabledItemStyle:"ToolbarButtonEnabled", ToolbarDisabledItemStyle:"ToolbarButtonDisabled", ToolbarHighlightedItemStyle:"ToolbarButtonHighlighted", ToolbarHighlightedSelectStyle:"ToolbarSelectHighlighted", ToolbarHighlightedSelectItemStyle:"ToolbarSelectHighlightedItem", postCreate:function () {
	var nodes = dojo.html.getElementsByClass("dojoEditorToolbarItem", this.domNode);
	this.items = {};
	for (var x = 0; x < nodes.length; x++) {
		var node = nodes[x];
		var itemname = node.getAttribute("dojoETItemName");
		if (itemname) {
			var item = dojo.widget.Editor2ToolbarItemManager.getToolbarItem(itemname);
			if (item) {
				item.create(node, this);
				this.items[itemname.toLowerCase()] = item;
			} else {
				node.style.display = "none";
			}
		}
	}
}, update:function () {
	for (var cmd in this.items) {
		this.items[cmd].refreshState();
	}
}, shareGroup:"", checkAvailability:function () {
	if (!this.shareGroup) {
		this.parent.focus();
		return true;
	}
	var curInst = dojo.widget.Editor2Manager.getCurrentInstance();
	if (this.shareGroup == curInst.toolbarGroup) {
		return true;
	}
	return false;
}, destroy:function () {
	for (var it in this.items) {
		this.items[it].destroy();
		delete this.items[it];
	}
	dojo.widget.Editor2Toolbar.superclass.destroy.call(this);
}});

