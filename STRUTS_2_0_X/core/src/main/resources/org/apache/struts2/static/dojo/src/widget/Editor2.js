/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

/* TODO:
 * - font selector
 * - test, bug fix, more features :)
*/
dojo.provide("dojo.widget.Editor2");

dojo.require("dojo.io.*");
dojo.require("dojo.html.*");
dojo.require("dojo.html.layout");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.RichText");
dojo.require("dojo.widget.Editor2Toolbar");

// Object: Manager of current focused Editor2 Instance and available editor2 commands
dojo.widget.Editor2Manager = {
	_currentInstance: null,
	_loadedCommands: {},

	// Object: state a command may be in
	commandState: {Disabled: 0, Latched: 1, Enabled: 2},

	getCurrentInstance: function(){
		// summary: Return the current focused Editor2 instance
		return this._currentInstance;
	},
	setCurrentInstance: function(/*Widget*/inst){
		// summary: Set current focused Editor2 instance
		this._currentInstance = inst;
	},
	registerCommand: function(/*String*/name, /*Object*/cmd){
		// summary: Register an Editor2 command
		// name: name of the command (case insensitive)
		// cmd: an object which implements interface dojo.widget.Editor2Command
		name = name.toLowerCase();
		if(this._loadedCommands[name]){
			delete this._loadedCommands[name];
		}
		this._loadedCommands[name] = cmd;
	},
	getCommand: function(/*String*/name){
		// summary: Return Editor2 command with the given name
		// name: name of the command (case insensitive)
		name = name.toLowerCase();
		var oCommand = this._loadedCommands[name];
		if(oCommand){
			return oCommand;
		}

		switch(name){
			case 'htmltoggle':
				//Editor2 natively provide the htmltoggle functionalitity
				//and it is treated as a builtin command
				oCommand = new dojo.widget.Editor2BrowserCommand(name);
				break;
			case 'formatblock':
				oCommand = new dojo.widget.Editor2FormatBlockCommand(name);
				break;
			case 'anchor':
				oCommand = new dojo.widget.Editor2Command(name);
				break;

			//dialog command
			case 'createlink':
				oCommand = new dojo.widget.Editor2DialogCommand(name,
						{contentFile: "dojo.widget.Editor2Plugin.CreateLinkDialog",
							contentClass: "Editor2CreateLinkDialog",
							title: "Insert/Edit Link", width: "300px", height: "200px"});
				break;
			case 'insertimage':
				oCommand = new dojo.widget.Editor2DialogCommand(name,
						{contentFile: "dojo.widget.Editor2Plugin.InsertImageDialog",
							contentClass: "Editor2InsertImageDialog",
							title: "Insert/Edit Image", width: "400px", height: "270px"});
				break;
			// By default we assume that it is a builtin simple command.
			default:
				var curtInst = this.getCurrentInstance();
				if((curtInst && curtInst.queryCommandAvailable(name)) ||
					(!curtInst && dojo.widget.Editor2.prototype.queryCommandAvailable(name))){
					oCommand = new dojo.widget.Editor2BrowserCommand(name);
				}else{
					dojo.debug("dojo.widget.Editor2Manager.getCommand: Unknown command "+name);
					return;
				}
		}
		this._loadedCommands[name] = oCommand;
		return oCommand;
	},
	destroy: function(){
		// summary: Cleaning up. This is called automatically on page unload.
		this._currentInstance = null;
		for(var cmd in this._loadedCommands){
			this._loadedCommands[cmd].destory();
		}
	}
};

dojo.addOnUnload(dojo.widget.Editor2Manager, "destroy");

// summary:
//		dojo.widget.Editor2Command is the base class for all command in Editor2
dojo.lang.declare("dojo.widget.Editor2Command",null,{
		initializer: function(name){
			// summary: Constructor of this class
			this._name = name;
		},
		//this function should be re-implemented in subclass
		execute: function(para){
			// summary: Execute the command. should be implemented in subclass
			dojo.unimplemented("dojo.widget.Editor2Command.execute");
		},
		//default implemetation always returns Enabled
		getState: function(){
			// summary:
			//		Return the state of the command. The default behavior is
			//		to always return Enabled
			return dojo.widget.Editor2Manager.commandState.Enabled;
		},
		destory: function(){
			// summary: Destructor
		}
	}
);

// summary:
//		dojo.widget.Editor2BrowserCommand is the base class for all the browser built
//		in commands
dojo.lang.declare("dojo.widget.Editor2BrowserCommand", dojo.widget.Editor2Command, {
		execute: function(para){
			var curInst = dojo.widget.Editor2Manager.getCurrentInstance();
			if(curInst){
				curInst.execCommand(this._name, para);
			}
		},
		getState: function(){
			var curInst = dojo.widget.Editor2Manager.getCurrentInstance();
			if(curInst){
				try{
					if(curInst.queryCommandEnabled(this._name)){
						if(curInst.queryCommandState(this._name)){
							return dojo.widget.Editor2Manager.commandState.Latched;
						}else{
							return dojo.widget.Editor2Manager.commandState.Enabled;
						}
					}else{
						return dojo.widget.Editor2Manager.commandState.Disabled;
					}
				}catch (e) {
					//dojo.debug("exception when getting state for command "+this._name+": "+e);
					return dojo.widget.Editor2Manager.commandState.Enabled;
				}
			}
			return dojo.widget.Editor2Manager.commandState.Disabled;
		},
		getValue: function(){
			var curInst = dojo.widget.Editor2Manager.getCurrentInstance();
			if(curInst){
				try{
					return curInst.queryCommandValue(this._name);
				}catch(e){}
			}
		}
	}
);

dojo.lang.declare("dojo.widget.Editor2FormatBlockCommand", dojo.widget.Editor2BrowserCommand, {
		/* In none-ActiveX mode under IE, <p> and no <p> text can not be distinguished
		getCurrentValue: function(){
			var curInst = dojo.widget.Editor2Manager.getCurrentInstance();
			if(!curInst){ return ''; }

			var h = dojo.render.html;

			// safari f's us for selection primitives
			if(h.safari){ return ''; }

			var selectedNode = (h.ie) ? curInst.document.selection.createRange().parentElement() : curInst.window.getSelection().anchorNode;
			// make sure we actuall have an element
			while((selectedNode)&&(selectedNode.nodeType != 1)){
				selectedNode = selectedNode.parentNode;
			}
			if(!selectedNode){ return ''; }

			var formats = ["p", "pre", "h1", "h2", "h3", "h4", "h5", "h6", "address"];
			// gotta run some specialized updates for the various
			// formatting options
			var type = formats[dojo.lang.find(formats, selectedNode.nodeName.toLowerCase())];
			while((selectedNode!=curInst.editNode)&&(!type)){
				selectedNode = selectedNode.parentNode;
				if(!selectedNode){ break; }
				type = formats[dojo.lang.find(formats, selectedNode.nodeName.toLowerCase())];
			}
			if(!type){
				type = "";
			}
			return type;
		}*/
	}
);

dojo.require("dojo.widget.FloatingPane");
// summary:
//		dojo.widget.Editor2Dialog provides a Dialog which can be modal or normal for the Editor2.
dojo.widget.defineWidget(
	"dojo.widget.Editor2Dialog",
	[dojo.widget.HtmlWidget, dojo.widget.FloatingPaneBase, dojo.widget.ModalDialogBase],
	{
		templatePath: dojo.uri.dojoUri("src/widget/templates/Editor2/EditorDialog.html"),
		// Boolean: Whether this is a modal dialog. True by default.
		modal: true,
//		refreshOnShow: true, //for debug for now

		// String: Wwidth of the dialog. None by default
		width: false,
		// String: Height of the dialog. None by default
		height: false,

		// String: startup state of the dialog
		windowState: "minimized",

		displayCloseAction: true,

		contentFile: "",
		contentClass: "",

		fillInTemplate: function(args, frag){
			this.fillInFloatingPaneTemplate(args, frag);
			dojo.widget.Editor2Dialog.superclass.fillInTemplate.call(this, args, frag);
		},
		postCreate: function(){
			if(this.contentFile){
				dojo.require(this.contentFile);
			}
			if(this.modal){
				dojo.widget.ModalDialogBase.prototype.postCreate.call(this);
			}else{
				with(this.domNode.style) {
					zIndex = 999;
					display = "none";
				}
			}
			dojo.widget.FloatingPaneBase.prototype.postCreate.apply(this, arguments);
			dojo.widget.Editor2Dialog.superclass.postCreate.call(this);
			if(this.width && this.height){
				with(this.domNode.style){
					width = this.width;
					height = this.height;
				}
			}
		},
		createContent: function(){
			if(!this.contentWidget && this.contentClass){
				this.contentWidget = dojo.widget.createWidget(this.contentClass);
				this.addChild(this.contentWidget);
			}
		},
		show: function(){
			if(!this.contentWidget){
				//buggy IE: if the dialog is hidden, the button widgets
				//in the dialog can not be shown, so show it temporary (as the
				//dialog may decide not to show it in loadContent() later)
				dojo.widget.Editor2Dialog.superclass.show.apply(this, arguments);
				this.createContent();
				dojo.widget.Editor2Dialog.superclass.hide.call(this);
			}

			if(!this.contentWidget || !this.contentWidget.loadContent()){
				return;
			}
			this.showFloatingPane();
			dojo.widget.Editor2Dialog.superclass.show.apply(this, arguments);
			if(this.modal){
				this.showModalDialog();
			}
			this.placeModalDialog();
			if(this.modal){
				//place the background div under this modal pane
				this.shared.bg.style.zIndex = this.domNode.style.zIndex-1;
			}
		},
		onShow: function(){
			dojo.widget.Editor2Dialog.superclass.onShow.call(this);
			this.onFloatingPaneShow();
		},
		closeWindow: function(){
			this.hide();
			dojo.widget.Editor2Dialog.superclass.closeWindow.apply(this, arguments);
		},
		hide: function(){
			if(this.modal){
				this.hideModalDialog();
			}
			dojo.widget.Editor2Dialog.superclass.hide.call(this);
		}
	}
);

// summary:
//		dojo.widget.Editor2DialogContent is the actual content of a Editor2Dialog.
//		This class should be subclassed to provide the content.
dojo.widget.defineWidget(
	"dojo.widget.Editor2DialogContent",
	dojo.widget.HtmlWidget,
{
	widgetsInTemplate: true,

	loadContent:function(){
		// summary: Load the content. Called by Editor2Dialog when first shown
		return true;
	},
	cancel: function(){
		// summary: Default handler when cancel button is clicked.
		this.parent.hide();
	}
});

// summary:
//		dojo.widget.Editor2DialogCommand provides an easy way to popup a dialog when
//		the command is executed.
dojo.lang.declare("dojo.widget.Editor2DialogCommand", dojo.widget.Editor2BrowserCommand,
	function(name, dialogParas){
		this.dialogParas = dialogParas;
	},
{
	execute: function(){
		if(!this.dialog){
			if(!this.dialogParas.contentFile || !this.dialogParas.contentClass){
				alert("contentFile and contentClass should be set for dojo.widget.Editor2DialogCommand.dialogParas!");
				return;
			}
			this.dialog = dojo.widget.createWidget("Editor2Dialog", this.dialogParas);

			dojo.body().appendChild(this.dialog.domNode);

			dojo.event.connect(this, "destroy", this.dialog, "destroy");
		}
		this.dialog.show();
	}
});

// summary:
//		dojo.widget.Editor2 is the WYSIWYG editor in dojo with toolbar. It supports a plugin
//		framework which can be used to extend the functionalities of the editor, such as
//		adding a context menu, table operation etc.
// description:
//		Plugins are available using dojo's require syntax. Please find available built-in plugins
//		under src/widget/Editor2Plugin.
dojo.widget.defineWidget(
	"dojo.widget.Editor2",
	dojo.widget.RichText,
	{
//		// String: url to which save action should send content to
//		saveUrl: "",
//		// String: HTTP method for save (post or get)
//		saveMethod: "post",
//		saveArgName: "editorContent",
//		closeOnSave: false,

		// Boolean: Whether to share toolbar with other instances of Editor2
		shareToolbar: false,
		// Boolean: Whether the toolbar should scroll to keep it in the view
		toolbarAlwaysVisible: false,

//		htmlEditing: false,

		toolbarWidget: null,
		scrollInterval: null,

		// Object: dojo.uri.Uri object to specify the template file for the toolbar
		toolbarTemplatePath: dojo.uri.dojoUri("src/widget/templates/EditorToolbarOneline.html"),
		// Object: dojo.uri.Uri object to specify the css file for the toolbar
		toolbarTemplateCssPath: null,
//		toolbarTemplatePath: dojo.uri.dojoUri("src/widget/templates/Editor2/EditorToolbarFCKStyle.html"),
//		toolbarTemplateCssPath: dojo.uri.dojoUri("src/widget/templates/Editor2/FCKDefault/EditorToolbarFCKStyle.css"),

		_inSourceMode: false,
		_htmlEditNode: null,

		editorOnLoad: function(){
			// summary:
			//		Create toolbar and other initialization routines. This is called after
			//		the finish of the loading of document in the editing element
//			dojo.profile.start("dojo.widget.Editor2::editorOnLoad");

			dojo.event.topic.publish("dojo.widget.Editor2::preLoadingToolbar", this);
			if(this.toolbarAlwaysVisible){
				dojo.require("dojo.widget.Editor2Plugin.AlwaysShowToolbar");
			}

			var toolbars = dojo.widget.byType("Editor2Toolbar");
			if((!toolbars.length)||(!this.shareToolbar)){
				if(this.toolbarWidget){
					this.toolbarWidget.show();
					//re-add the toolbar to the new domNode (caused by open() on another element)
					dojo.html.insertBefore(this.toolbarWidget.domNode, this.domNode.firstChild);
				}else{
					var tbOpts = {};
					tbOpts.templatePath = this.toolbarTemplatePath;
					if(this.toolbarTemplateCssPath){
						tbOpts.templateCssPath = this.toolbarTemplateCssPath;
					}
					this.toolbarWidget = dojo.widget.createWidget("Editor2Toolbar", tbOpts, this.domNode.firstChild, "before");

					dojo.event.connect(this, "close", this.toolbarWidget, "hide");

					this.toolbarLoaded();
				}
			}else{
				// FIXME: 	selecting in one shared toolbar doesn't clobber
				// 			selection in the others. This is problematic.
				this.toolbarWidget = toolbars[0];
			}

			dojo.event.topic.registerPublisher("Editor2.clobberFocus", this, "clobberFocus");
			dojo.event.topic.subscribe("Editor2.clobberFocus", this, "setBlur");

			dojo.event.topic.publish("dojo.widget.Editor2::onLoad", this);
//			dojo.profile.end("dojo.widget.Editor2::editorOnLoad");
		},

		//event for plugins to use
		toolbarLoaded: function(){
			// summary:
			//		Fired when the toolbar for this editor is created.
			//		This event is for plugins to use
		},

		//TODO: provide a query mechanism about loaded plugins?
		registerLoadedPlugin: function(/*Object*/obj){
			// summary: Register a plugin which is loaded for this instance
			if(!this.loadedPlugins){
				this.loadedPlugins = [];
			}
			this.loadedPlugins.push(obj);
		},
		unregisterLoadedPlugin: function(/*Object*/obj){
			// summery: Delete a loaded plugin for this instance
			for(var i in this.loadedPlugins){
				if(this.loadedPlugins[i] === obj){
					delete this.loadedPlugins[i];
					return;
				}
			}
			dojo.debug("dojo.widget.Editor2.unregisterLoadedPlugin: unknow plugin object: "+obj);
		},

		//overload the original ones to provide extra commands
		execCommand: function(/*String*/command, argument){
			switch(command.toLowerCase()){
				case 'htmltoggle':
					this.toggleHtmlEditing();
					break;
				default:
					dojo.widget.Editor2.superclass.execCommand.apply(this, arguments);
			}
		},
		queryCommandEnabled: function(/*String*/command, argument){
			switch(command.toLowerCase()){
				case 'htmltoggle':
					return true;
				default:
					if(this._inSourceMode){ return false;}
					return dojo.widget.Editor2.superclass.queryCommandEnabled.apply(this, arguments);
			}
		},
		queryCommandState: function(/*String*/command, argument){
			switch(command.toLowerCase()){
				case 'htmltoggle':
					return this._inSourceMode;
				default:
					return dojo.widget.Editor2.superclass.queryCommandState.apply(this, arguments);
			}
		},

		onClick: function(/*Event*/e){
			dojo.widget.Editor2.superclass.onClick.call(this, e);
			//if Popup is used, call dojo.widget.PopupManager.onClick
			//manually when click in the editing area to close all
			//open popups (dropdowns)
			if(dojo.widget.PopupManager){
				if(!e){ //IE
					e = this.window.event;
				}
				dojo.widget.PopupManager.onClick(e);
			}
		},

		clobberFocus: function(){
			// summary: stub to signal other instances to clobber focus
		},
		toggleHtmlEditing: function(){
			// summary: toggle between WYSIWYG mode and HTML source mode
			if(this===dojo.widget.Editor2Manager.getCurrentInstance()){
				if(!this._inSourceMode){
					this._inSourceMode = true;

					if(!this._htmlEditNode){
						this._htmlEditNode = dojo.doc().createElement("textarea");
						dojo.html.insertAfter(this._htmlEditNode, this.editorObject);
					}
					this._htmlEditNode.style.display = "";
					this._htmlEditNode.style.width = "100%";
					this._htmlEditNode.style.height = dojo.html.getBorderBox(this.editNode).height+"px";
					this._htmlEditNode.value = this.editNode.innerHTML;

					//activeX object (IE) doesn't like to be hidden, so move it outside of screen instead
					with(this.editorObject.style){
						position = "absolute";
						left = "-2000px";
						top = "-2000px";
					}
				}else{
					this._inSourceMode = false;

					//In IE activeX mode, if _htmlEditNode is focused,
					//when toggling, an error would occur, so unfocus it
					this._htmlEditNode.blur();

					with(this.editorObject.style){
						position = "";
						left = "";
						top = "";
					}

					dojo.lang.setTimeout(this, "replaceEditorContent", 1, this._htmlEditNode.value);
					this._htmlEditNode.style.display = "none";
					this.focus();
				}
				this.updateToolbar(true);
			}
		},

		setFocus: function(){
			// summary: focus is set on this instance
//			dojo.debug("setFocus: start "+this.widgetId);
			if(dojo.widget.Editor2Manager.getCurrentInstance() === this){ return; }

			this.clobberFocus();
//			dojo.debug("setFocus:", this);
			dojo.widget.Editor2Manager.setCurrentInstance(this);
		},

		setBlur: function(){
			// summary: focus on this instance is lost
//			 dojo.debug("setBlur:", this);
			//dojo.event.disconnect(this.toolbarWidget, "exec", this, "execCommand");
		},

		saveSelection: function(){
			// summary: save the current selection for restoring it
			this._bookmark = null;
			this._bookmark = dojo.withGlobal(this.window, dojo.html.selection.getBookmark);
		},
		restoreSelection: function(){
			// summary: restore the last saved selection
			if(this._bookmark){
				this.focus(); //require for none-activeX IE
				dojo.withGlobal(this.window, "moveToBookmark", dojo.html.selection, [this._bookmark]);
				this._bookmark = null;
			}else{
				dojo.debug("restoreSelection: no saved selection is found!");
			}
		},

		_updateToolbarLastRan: null,
		_updateToolbarTimer: null,
		_updateToolbarFrequency: 500,

		updateToolbar: function(/*Boolean*/force){
			// summary: update the associated toolbar of this Editor2
			if((!this.isLoaded)||(!this.toolbarWidget)){ return; }

			// keeps the toolbar from updating too frequently
			// TODO: generalize this functionality?
			var diff = new Date() - this._updateToolbarLastRan;
			if( (!force)&&(this._updateToolbarLastRan)&&
				((diff < this._updateToolbarFrequency)) ){

				clearTimeout(this._updateToolbarTimer);
				var _this = this;
				this._updateToolbarTimer = setTimeout(function() {
					_this.updateToolbar();
				}, this._updateToolbarFrequency/2);
				return;

			}else{
				this._updateToolbarLastRan = new Date();
			}
			// end frequency checker

			//IE has the habit of generating events even when this editor is blurred, prevent this
			if(dojo.widget.Editor2Manager.getCurrentInstance() !== this){ return; }

			this.toolbarWidget.update();
		},

		destroy: function(/*Boolean*/finalize){
			this._htmlEditNode = null;
			dojo.event.disconnect(this, "close", this.toolbarWidget, "hide");
			if(!finalize){
				this.toolbarWidget.destroy();
			}
			dojo.widget.Editor2.superclass.destroy.call(this);
		},

		onDisplayChanged: function(/*Object*/e){
			dojo.widget.Editor2.superclass.onDisplayChanged.call(this,e);
			this.updateToolbar();
		},

		onLoad: function(){
			try{
				dojo.widget.Editor2.superclass.onLoad.call(this);
			}catch(e){ // FIXME: debug why this is throwing errors in IE!
				dojo.debug(e);
			}
			this.editorOnLoad();
		},

		onFocus: function(){
			dojo.widget.Editor2.superclass.onFocus.call(this);
			this.setFocus();
		},

		//overload to support source editing mode
		getEditorContent: function(){
			if(this._inSourceMode){
				this.replaceEditorContent(this._htmlEditNode.value);
			}
			return dojo.widget.Editor2.superclass.getEditorContent.call(this);
		}
		/*,
		// FIXME: probably not needed any more with new design, but need to verify
		_save: function(e){
			// FIXME: how should this behave when there's a larger form in play?
			if(!this.isClosed){
				dojo.debug("save attempt");
				if(this.saveUrl.length){
					var content = {};
					content[this.saveArgName] = this.getEditorContent();
					dojo.io.bind({
						method: this.saveMethod,
						url: this.saveUrl,
						content: content
					});
				}else{
					dojo.debug("please set a saveUrl for the editor");
				}
				if(this.closeOnSave){
					this.close(e.getName().toLowerCase() == "save");
				}
			}
		}*/
	},
	"html"
);