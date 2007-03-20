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

// Object: Manager available editor2 toolbar items
dojo.widget.Editor2ToolbarItemManager = {
	_registeredItemHandlers: [],
	registerHandler: function(/*Object*/obj, /*String*/func){
		// summary: register a toolbar item handler
		// obj: object which has the function to call
		// func: the function in the object
		if(arguments.length == 2){
			this._registeredItemHandlers.push(function(){return obj[func].apply(obj, arguments);});
		}else{
			/* obj: Function
			    func: null
			    pId: f */
//			for(i in this._registeredItemHandlers){
//				if(func === this._registeredItemHandlers[i]){
//					dojo.debug("Editor2ToolbarItemManager handler "+func+" is already registered, ignored");
//					return;
//				}
//			}
			this._registeredItemHandlers.push(obj);
		}
	},
	removeHandler: function(func){
		// summary: remove a registered handler
		for(var i in this._registeredItemHandlers){
			if(func === this._registeredItemHandlers[i]){
				delete this._registeredItemHandlers[i];
				return;
			}
		}
		dojo.debug("Editor2ToolbarItemManager handler "+func+" is not registered, can not remove.");
	},
	destroy: function(){
		for(var i in this._registeredItemHandlers){
			delete this._registeredItemHandlers[i];
		}
	},
	getToolbarItem: function(/*String*/name){
		// summary: return a toobar item with the given name
		var item;
		name = name.toLowerCase();
		for(var i in this._registeredItemHandlers){
			item = this._registeredItemHandlers[i](name);
			if(item){
				break;
			}
		}

		if(!item){
			switch(name){
				//button for builtin functions
				case 'bold':
				case 'copy':
				case 'cut':
				case 'delete':
				case 'indent':
				case 'inserthorizontalrule':
				case 'insertorderedlist':
				case 'insertunorderedlist':
				case 'italic':
				case 'justifycenter':
				case 'justifyfull':
				case 'justifyleft':
				case 'justifyright':
				case 'outdent':
				case 'paste':
				case 'redo':
				case 'removeformat':
				case 'selectall':
				case 'strikethrough':
				case 'subscript':
				case 'superscript':
				case 'underline':
				case 'undo':
				case 'unlink':
				case 'createlink':
				case 'insertimage':
				//extra simple buttons
				case 'htmltoggle':
					item = new dojo.widget.Editor2ToolbarButton(name);
					break;
				case 'forecolor':
				case 'hilitecolor':
					item = new dojo.widget.Editor2ToolbarColorPaletteButton(name);
					break;
				case 'plainformatblock':
					item = new dojo.widget.Editor2ToolbarFormatBlockPlainSelect("formatblock");
					break;
				case 'formatblock':
					item = new dojo.widget.Editor2ToolbarFormatBlockSelect("formatblock");
					break;
				case 'fontsize':
					item = new dojo.widget.Editor2ToolbarFontSizeSelect("fontsize");
					break;
				case 'fontname':
					item = new dojo.widget.Editor2ToolbarFontNameSelect("fontname");
					break;
				case 'inserttable':
				case 'insertcell':
				case 'insertcol':
				case 'insertrow':
				case 'deletecells':
				case 'deletecols':
				case 'deleterows':
				case 'mergecells':
				case 'splitcell':
					dojo.debug(name + " is implemented in dojo.widget.Editor2Plugin.TableOperation, please require it first.");
					break;
				//TODO:
				case 'inserthtml':
				case 'blockdirltr':
				case 'blockdirrtl':
				case 'dirltr':
				case 'dirrtl':
				case 'inlinedirltr':
				case 'inlinedirrtl':
					dojo.debug("Not yet implemented toolbar item: "+name);
					break;
				default:
					dojo.debug("dojo.widget.Editor2ToolbarItemManager.getToolbarItem: Unknown toolbar item: "+name);
			}
		}
		return item;
	}
};

dojo.addOnUnload(dojo.widget.Editor2ToolbarItemManager, "destroy");
// summary:
//		dojo.widget.Editor2ToolbarButton is the base class for all toolbar item in Editor2Toolbar
dojo.declare("dojo.widget.Editor2ToolbarButton", null,{
	initializer: function(name){
		// summary: constructor
		this._name = name;
		this._command = dojo.widget.Editor2Manager.getCommand(name);
	},
	create: function(/*DomNode*/node, /*dojo.widget.Editor2Toolbar*/toolbar, /*Boolean*/nohover){
		// summary: create the item
		// node: the dom node which is the root of this toolbar item
		// toolbar: the Editor2Toolbar widget this toolbar item belonging to
		// nohover: whether this item in charge of highlight this item
		this._domNode = node;
		//make this unselectable: different browsers
		//use different properties for this, so use
		//js do it automatically
		this.disableSelection(this._domNode);
		this._parentToolbar = toolbar;
		dojo.event.connect(this._domNode, 'onclick', this, 'onClick');
		if(!nohover){
			dojo.event.connect(this._domNode, 'onmouseover', this, 'onMouseOver');
			dojo.event.connect(this._domNode, 'onmouseout', this, 'onMouseOut');
		}
	},
	disableSelection: function(/*DomNode*/rootnode){
		// summary: disable selection on the passed node and all its children
		dojo.html.disableSelection(rootnode);
		var nodes = rootnode.all || rootnode.getElementsByTagName("*");
		for(var x=0; x<nodes.length; x++){
			dojo.html.disableSelection(nodes[x]);
		}
	},
	onMouseOver: function(){
		if(this._command.getState() != dojo.widget.Editor2Manager.commandState.Disabled){
			this.highlightToolbarItem();
		}
	},
	onMouseOut: function(){
		this.unhighlightToolbarItem();
	},
	destroy: function(){
		// summary: destructor
		this._domNode = null;
		delete this._command;
		this._parentToolbar = null;
	},
	onClick: function(e){
		if(this._domNode && !this._domNode.disabled && this._command){
			e.preventDefault();
			e.stopPropagation();
			this._command.execute();
		}
	},
	refreshState: function(){
		// summary: update the state of the toolbar item
		if(this._domNode && this._command){
			var em = dojo.widget.Editor2Manager;
			var state = this._command.getState();
			if(state != this._lastState){
				switch(state){
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
			return state;
		}
	},

	latchToolbarItem: function(){
		this._domNode.disabled = false;
		this.removeToolbarItemStyle(this._domNode);
		dojo.html.addClass(this._domNode, this._parentToolbar.ToolbarLatchedItemStyle);
	},

	enableToolbarItem: function(){
		this._domNode.disabled = false;
		this.removeToolbarItemStyle(this._domNode);
		dojo.html.addClass(this._domNode, this._parentToolbar.ToolbarEnabledItemStyle);
	},

	disableToolbarItem: function(){
		this._domNode.disabled = true;
		this.removeToolbarItemStyle(this._domNode);
		dojo.html.addClass(this._domNode, this._parentToolbar.ToolbarDisabledItemStyle);
	},

	highlightToolbarItem: function(){
		dojo.html.addClass(this._domNode, this._parentToolbar.ToolbarHighlightedItemStyle);
	},

	unhighlightToolbarItem: function(){
		dojo.html.removeClass(this._domNode, this._parentToolbar.ToolbarHighlightedItemStyle);
	},

	removeToolbarItemStyle: function(){
		dojo.html.removeClass(this._domNode, this._parentToolbar.ToolbarEnabledItemStyle);
		dojo.html.removeClass(this._domNode, this._parentToolbar.ToolbarLatchedItemStyle);
		dojo.html.removeClass(this._domNode, this._parentToolbar.ToolbarDisabledItemStyle);
		this.unhighlightToolbarItem();
	}
});

// summary: dojo.widget.Editor2ToolbarDropDownButton extends the basic button with a dropdown list
dojo.declare("dojo.widget.Editor2ToolbarDropDownButton", dojo.widget.Editor2ToolbarButton,{
	onClick: function(){
		if(this._domNode){
			if(!this._dropdown){
				this._dropdown = dojo.widget.createWidget("PopupContainer", {});
				this._domNode.appendChild(this._dropdown.domNode);
			}
			if(this._dropdown.isShowingNow){
				this._dropdown.close();
			}else{
				this.onDropDownShown();
				this._dropdown.open(this._domNode, null, this._domNode);
			}
		}
	},
	destroy: function(){
		this.onDropDownDestroy();
		if(this._dropdown){
			this._dropdown.destroy();
		}
		dojo.widget.Editor2ToolbarDropDownButton.superclass.destroy.call(this);
	},
	onDropDownShown: function(){},
	onDropDownDestroy: function(){}
});

// summary: dojo.widget.Editor2ToolbarColorPaletteButton provides a dropdown color palette picker
dojo.declare("dojo.widget.Editor2ToolbarColorPaletteButton", dojo.widget.Editor2ToolbarDropDownButton,{
	onDropDownShown: function(){
		if(!this._colorpalette){
			this._colorpalette = dojo.widget.createWidget("ColorPalette", {});
			this._dropdown.addChild(this._colorpalette);

			this.disableSelection(this._dropdown.domNode);
			this.disableSelection(this._colorpalette.domNode);
			//do we need a destory to delete this._colorpalette manually?
			//I assume as it is added to this._dropdown via addChild, it
			//should be deleted when this._dropdown is destroyed

			dojo.event.connect(this._colorpalette, "onColorSelect", this, 'setColor');
			dojo.event.connect(this._dropdown, "open", this, 'latchToolbarItem');
			dojo.event.connect(this._dropdown, "close", this, 'enableToolbarItem');
		}
	},
	setColor: function(color){
		this._dropdown.close();
		this._command.execute(color);
	}
});

// summary: dojo.widget.Editor2ToolbarFormatBlockPlainSelect provides a simple select for setting block format
dojo.declare("dojo.widget.Editor2ToolbarFormatBlockPlainSelect", dojo.widget.Editor2ToolbarButton,{
	create: function(node, toolbar){
		//TODO: check node is a select
		this._domNode = node;
		this.disableSelection(this._domNode);
		this._parentToolbar = toolbar;
		dojo.event.connect(this._domNode, 'onchange', this, 'onChange');
	},

	destroy: function(){
		this._domNode = null;
		this._command = null;
		this._parentToolbar = null;
	},

	onChange: function(){
		if(this._domNode){
			var sv = this._domNode.value.toLowerCase();
			this._command.execute(sv);
		}
	},

	refreshState: function(){
		if(this._domNode && this._command){
			dojo.widget.Editor2ToolbarFormatBlockPlainSelect.superclass.refreshState.call(this);
			var format = this._command.getValue();
			if(!format){ format = ""; }
			dojo.lang.forEach(this._domNode.options, function(item){
				if(item.value.toLowerCase() == format.toLowerCase()){
					item.selected = true;
				}
			});
		}
	}
});

// summary: dojo.widget.Editor2ToolbarComboItem provides an external loaded dropdown list
dojo.declare("dojo.widget.Editor2ToolbarComboItem", dojo.widget.Editor2ToolbarDropDownButton,{
	href: null,
	create: function(node, toolbar){
		dojo.widget.Editor2ToolbarComboItem.superclass.create.call(this, node, toolbar);
		//do not use lazy initilization, as we need the local names in refreshState()
		if(!this._contentPane){
			dojo.require("dojo.widget.ContentPane");
			this._contentPane = dojo.widget.createWidget("ContentPane", {preload: 'true'});
			this._contentPane.addOnLoad(this, "setup");
			this._contentPane.setUrl(this.href);
		}
	},

	onMouseOver: function(e){
		dojo.html.addClass(e.currentTarget, this._parentToolbar.ToolbarHighlightedSelectStyle);
	},
	onMouseOut:function(e){
		dojo.html.removeClass(e.currentTarget, this._parentToolbar.ToolbarHighlightedSelectStyle);
	},

	onDropDownShown: function(){
		if(!this._dropdown.__addedContentPage){
			this._dropdown.addChild(this._contentPane);
			this._dropdown.__addedContentPage = true;
		}
	},

	setup: function(){
		// summary: overload this to connect event
	},

	onChange: function(e){
		var name = e.currentTarget.getAttribute("dropDownItemName");
		this._command.execute(name);
		this._dropdown.close();
	},

	onMouseOverItem: function(e){
		dojo.html.addClass(e.currentTarget, this._parentToolbar.ToolbarHighlightedSelectItemStyle);
	},

	onMouseOutItem: function(e){
		dojo.html.removeClass(e.currentTarget, this._parentToolbar.ToolbarHighlightedSelectItemStyle);
	},

	refreshState: function(){
		// summary: overload this to update GUI item
	}
});

// summary: dojo.widget.Editor2ToolbarFormatBlockSelect is an improved format block setting item
dojo.declare("dojo.widget.Editor2ToolbarFormatBlockSelect", dojo.widget.Editor2ToolbarComboItem,{
	href: dojo.uri.dojoUri("src/widget/templates/Editor2/EditorToolbar_FormatBlock.html"),

	setup: function(){
		dojo.widget.Editor2ToolbarFormatBlockSelect.superclass.setup.call(this);

		var nodes = this._contentPane.domNode.all || this._contentPane.domNode.getElementsByTagName("*");
		this._blockNames = {};
		this._blockDisplayNames = {};
		for(var x=0; x<nodes.length; x++){
			var node = nodes[x];
			dojo.html.disableSelection(node);
			var name=node.getAttribute("dropDownItemName")
			if(name){
				this._blockNames[name] = node;
				var childrennodes = node.getElementsByTagName(name);
				this._blockDisplayNames[name] = childrennodes[childrennodes.length-1].innerHTML;
			}
		}
		for(var name in this._blockNames){
			dojo.event.connect(this._blockNames[name], "onclick", this, "onChange");
			dojo.event.connect(this._blockNames[name], "onmouseover", this, "onMouseOverItem");
			dojo.event.connect(this._blockNames[name], "onmouseout", this, "onMouseOutItem");
		}
	},

	onDropDownDestroy: function(){
		if(this._blockNames){
			for(var name in this._blockNames){
				delete this._blockNames[name];
				delete this._blockDisplayNames[name];
			}
		}
	},

	refreshState: function(){
		if(this._command){
			//dojo.widget.Editor2ToolbarFormatBlockSelect.superclass.refreshState.call(this);
			var format = this._command.getValue();
			if(format == this._lastSelectedFormat && this._blockDisplayNames){
				return;
			}
			this._lastSelectedFormat = format;
			var label = this._domNode.getElementsByTagName("label")[0];
			var isSet = false;
			if(this._blockDisplayNames){
				for(var name in this._blockDisplayNames){
					if(name == format){
						label.innerHTML = 	this._blockDisplayNames[name];
						isSet = true;
						break;
					}
				}
				if(!isSet){
					label.innerHTML = "&nbsp;";
				}
			}
		}
	}
});

// summary: dojo.widget.Editor2ToolbarFontSizeSelect provides a dropdown list for setting fontsize
dojo.declare("dojo.widget.Editor2ToolbarFontSizeSelect", dojo.widget.Editor2ToolbarComboItem,{
	href: dojo.uri.dojoUri("src/widget/templates/Editor2/EditorToolbar_FontSize.html"),

	setup: function(){
		dojo.widget.Editor2ToolbarFormatBlockSelect.superclass.setup.call(this);

		var nodes = this._contentPane.domNode.all || this._contentPane.domNode.getElementsByTagName("*");
		this._fontsizes = {};
		this._fontSizeDisplayNames = {};
		for(var x=0; x<nodes.length; x++){
			var node = nodes[x];
			dojo.html.disableSelection(node);
			var name=node.getAttribute("dropDownItemName")
			if(name){
				this._fontsizes[name] = node;
				this._fontSizeDisplayNames[name] = node.getElementsByTagName('font')[0].innerHTML;
			}
		}
		for(var name in this._fontsizes){
			dojo.event.connect(this._fontsizes[name], "onclick", this, "onChange");
			dojo.event.connect(this._fontsizes[name], "onmouseover", this, "onMouseOverItem");
			dojo.event.connect(this._fontsizes[name], "onmouseout", this, "onMouseOutItem");
		}
	},

	onDropDownDestroy: function(){
		if(this._fontsizes){
			for(var name in this._fontsizes){
				delete this._fontsizes[name];
				delete this._fontSizeDisplayNames[name];
			}
		}
	},

	refreshState: function(){
		if(this._command){
			//dojo.widget.Editor2ToolbarFormatBlockSelect.superclass.refreshState.call(this);
			var size = this._command.getValue();
			if(size == this._lastSelectedSize && this._fontSizeDisplayNames){
				return;
			}
			this._lastSelectedSize = size;
			var label = this._domNode.getElementsByTagName("label")[0];
			var isSet = false;
			if(this._fontSizeDisplayNames){
				for(var name in this._fontSizeDisplayNames){
					if(name == size){
						label.innerHTML = 	this._fontSizeDisplayNames[name];
						isSet = true;
						break;
					}
				}
				if(!isSet){
					label.innerHTML = "&nbsp;";
				}
			}
		}
	}
});

// summary: dojo.widget.Editor2ToolbarFontNameSelect provides a dropdown list for setting fontname
dojo.declare("dojo.widget.Editor2ToolbarFontNameSelect", dojo.widget.Editor2ToolbarFontSizeSelect,{
	href: dojo.uri.dojoUri("src/widget/templates/Editor2/EditorToolbar_FontName.html")
});

// summary:
//		dojo.widget.Editor2Toolbar is the main widget for the toolbar associated with an Editor2
dojo.widget.defineWidget(
	"dojo.widget.Editor2Toolbar",
	dojo.widget.HtmlWidget,
	{
		templatePath: dojo.uri.dojoUri("src/widget/templates/EditorToolbar.html"),
		templateCssPath: dojo.uri.dojoUri("src/widget/templates/EditorToolbar.css"),

		// DOM Nodes
//		saveButton: null,

		// String: class name for latched toolbar button items
		ToolbarLatchedItemStyle: "ToolbarButtonLatched",
		// String: class name for enabled toolbar button items
		ToolbarEnabledItemStyle: "ToolbarButtonEnabled",
		// String: class name for disabled toolbar button items
		ToolbarDisabledItemStyle: "ToolbarButtonDisabled",
		// String: class name for highlighted toolbar button items
		ToolbarHighlightedItemStyle: "ToolbarButtonHighlighted",
		// String: class name for highlighted toolbar select items
		ToolbarHighlightedSelectStyle: "ToolbarSelectHighlighted",
		// String: class name for highlighted toolbar select dropdown items
		ToolbarHighlightedSelectItemStyle: "ToolbarSelectHighlightedItem",

//		itemNodeType: 'span', //all the items (with attribute dojoETItemName set) defined in the toolbar should be a of this type

		postCreate: function(){
			var nodes = dojo.html.getElementsByClass("dojoEditorToolbarItem", this.domNode/*, this.itemNodeType*/);

			this.items = {};
			for(var x=0; x<nodes.length; x++){
				var node = nodes[x];
				var itemname = node.getAttribute("dojoETItemName");
				if(itemname){
					var item = dojo.widget.Editor2ToolbarItemManager.getToolbarItem(itemname);
					if(item){
						item.create(node, this);
						this.items[itemname.toLowerCase()] = item;
					}else{
						//hide unsupported toolbar items
						node.style.display = "none";
					}
				}
			}
		},

		update: function(){
			// summary: update all the toolbar items
			for(var cmd in this.items){
				this.items[cmd].refreshState();
			}
		},

		destroy: function(){
			for(var it in this.items){
				this.items[it].destroy();
				delete this.items[it];
			}
			dojo.widget.Editor2Toolbar.superclass.destroy.call(this);
		}//,

		// stub for observers
//		exec: function(what, arg){ /* dojo.debug(what, new Date()); */ }
	},
	"html",
	function(){
		dojo.event.connect(this, "fillInTemplate", dojo.lang.hitch(this, function(){
			if(dojo.render.html.ie){
				this.domNode.style.zoom = 1.0;
			}
		}));
	}
);
