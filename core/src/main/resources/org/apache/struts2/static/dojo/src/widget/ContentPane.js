/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.ContentPane");

dojo.require("dojo.widget.*");
dojo.require("dojo.io.*");
dojo.require("dojo.widget.HtmlWidget");
dojo.require("dojo.string");
dojo.require("dojo.string.extras");
dojo.require("dojo.html.style");


// summary:
//		dojo.widget.ContentPane, a widget that can be used as a standalone widget 
//		or as a baseclass for other widgets
//		Handles replacement of document fragment using either external uri or javascript/java 
//		generated markup or DomNode content, instanciating widgets within content and runs scripts.
//		Dont confuse it with an iframe, it only needs document fragments.
//		It's useful as a child of LayoutContainer, SplitContainer, or TabContainer.
//		But note that those classes can contain any widget as a child.
dojo.widget.defineWidget(
	"dojo.widget.ContentPane",
	dojo.widget.HtmlWidget,
	function(){
		// per widgetImpl variables
		this._styleNodes =  [];
		this._onLoadStack = [];
		this._onUnloadStack = [];
		this._callOnUnload = false;
		this._ioBindObj;
		// Function:
		//		reference holder to the inline scripts container, if scriptSeparation is true
		//	Note:
		//		dont change this value externally
		this.scriptScope; // undefined for now

		// loading option
		// Object:
		//		Send in extra args to the dojo.io.bind call
		//	example:
		//		bindArgs="preventCache:false;" overrides cacheContent
		this.bindArgs = {};

	
	}, {
		isContainer: true,

		// loading options
		// Boolean:
		//		adjust relative paths in markup to fit this page
		adjustPaths: true,

		// String:
		//		The href of the content that displays now
		//		Set this at construction if you want to load externally,
		//		changing href after creation doesnt have any effect, see setUrl
		href: "",

		// Boolean: Extract visible content from inside of <body> .... </body>
		extractContent: true,

		// Boolean: Construct all widgets that is in content
		parseContent:	true,

		// Boolean: Cache content retreived externally
		cacheContent:	true,

		// Boolean:
		//		Force load of data even if pane is hidden
		//	Note:
		//		In order to delay download you need to initially hide the node it constructs from
		preload: false,

		// Boolean:
		//		Refresh (re-download) content when pane goes from hidden to shown
		refreshOnShow: false,

		// String||Function:
		//		Generate pane content from a java function
		//		The name of the java proxy function
		handler: "",

		// Boolean:
		//		Run scripts within content, extractContent has NO effect on this
		//	Note:
		//		if true scripts in content will be evaled after content is innerHTML'ed
		executeScripts: false,

		// Boolean:
		//		Run scripts in a separate scope, unique for each ContentPane
		scriptSeparation: true,

		// String: Message that shows while downloading
		loadingMessage: "Loading...",

		// Boolean: Tells loading status
		isLoaded: false,

		postCreate: function(args, frag, parentComp){
			if (this.handler!==""){
				this.setHandler(this.handler);
			}
			if(this.isShowing() || this.preload){
				this.loadContents(); 
			}
		},
	
		show: function(){
			// if refreshOnShow is true, reload the contents every time; otherwise, load only the first time
			if(this.refreshOnShow){
				this.refresh();
			}else{
				this.loadContents();
			}
			dojo.widget.ContentPane.superclass.show.call(this);
		},
	
		refresh: function(){
			// summary:
			//		Force a refresh (re-download) of content, be sure to turn of cache
			this.isLoaded=false;
			this.loadContents();
		},
	
		loadContents: function() {
			// summary:
			//		Download if isLoaded is false, else ignore
			if ( this.isLoaded ){
				return;
			}
			if ( dojo.lang.isFunction(this.handler)) {
				this._runHandler();
			} else if ( this.href != "" ) {
				this._downloadExternalContent(this.href, this.cacheContent && !this.refreshOnShow);
			}
		},
		
		setUrl: function(/*String||dojo.uri.Uri*/ url) {
			// summary:
			//		Reset the (external defined) content of this pane and replace with new url
			//	Note:
			//		It delays the download until widget is shown if preload is false
			this.href = url;
			this.isLoaded = false;
			if ( this.preload || this.isShowing() ){
				this.loadContents();
			}
		},

		abort: function(){
			// summary
			//		Aborts a inflight download of content
			var bind = this._ioBindObj;
			if(!bind || !bind.abort){ return; }
			bind.abort();
			delete this._ioBindObj;
		},
	
		_downloadExternalContent: function(url, useCache) {
			this.abort();
			this._handleDefaults(this.loadingMessage, "onDownloadStart");
			var self = this;
			this._ioBindObj = dojo.io.bind(
				this._cacheSetting({
					url: url,
					mimetype: "text/html",
					handler: function(type, data, xhr){
						delete self._ioBindObj; // makes sure abort doesnt clear cache
						if(type=="load"){
							self.onDownloadEnd.call(self, url, data);
						}else{
							// XHR isnt a normal JS object, IE doesnt have prototype on XHR so we cant extend it or shallowCopy it
							var e = {
								responseText: xhr.responseText,
								status: xhr.status,
								statusText: xhr.statusText,
								responseHeaders: xhr.getAllResponseHeaders(),
								text: "Error loading '" + url + "' (" + xhr.status + " "+  xhr.statusText + ")"
							};
							self._handleDefaults.call(self, e, "onDownloadError");
							self.onLoad();
						}
					}
				}, useCache)
			);
		},
	
		_cacheSetting: function(bindObj, useCache){
			for(var x in this.bindArgs){
				if(dojo.lang.isUndefined(bindObj[x])){
					bindObj[x] = this.bindArgs[x];
				}
			}

			if(dojo.lang.isUndefined(bindObj.useCache)){ bindObj.useCache = useCache; }
			if(dojo.lang.isUndefined(bindObj.preventCache)){ bindObj.preventCache = !useCache; }
			if(dojo.lang.isUndefined(bindObj.mimetype)){ bindObj.mimetype = "text/html"; }
			return bindObj;
		},

		onLoad: function(e){
			// summary:
			//		Event hook, is called after everything is loaded and widgetified 
			this._runStack("_onLoadStack");
			this.isLoaded=true;
		},
	
		onUnLoad: function(e){
			// summary:
			//		Deprecated, use onUnload (lowercased load)
			dojo.deprecated(this.widgetType+".onUnLoad, use .onUnload (lowercased load)", 0.5);
		},

		onUnload: function(e){
			// summary:
			//		Event hook, is called before old content is cleared
			this._runStack("_onUnloadStack");
			delete this.scriptScope;
			// FIXME: remove for 0.5 along with onUnLoad
			if(this.onUnLoad !== dojo.widget.ContentPane.prototype.onUnLoad){
				this.onUnLoad.apply(this, arguments);
			}
		},
	
		_runStack: function(stName){
			var st = this[stName]; var err = "";
			var scope = this.scriptScope || window;
			for(var i = 0;i < st.length; i++){
				try{
					st[i].call(scope);
				}catch(e){ 
					err += "\n"+st[i]+" failed: "+e.description;
				}
			}
			this[stName] = [];
	
			if(err.length){
				var name = (stName== "_onLoadStack") ? "addOnLoad" : "addOnUnLoad";
				this._handleDefaults(name+" failure\n "+err, "onExecError", "debug");
			}
		},
	
		addOnLoad: function(/*Function||Object, optional*/ obj, /*Function*/ func){
			// summary
			//		Stores function refs and calls them one by one in the order they came in
			//		when load event occurs.
			//	obj:
			//		holder object
			//	func:
			//		function that will be called 
			this._pushOnStack(this._onLoadStack, obj, func);
		},
	
		addOnUnload: function(/*Function||Object, optional*/ obj, /*Function*/ func){
			// summary
			//		Stores function refs and calls them one by one in the order they came in
			//		when unload event occurs.
			//	obj:
			//		holder object
			//	func:
			//		function that will be called 
			this._pushOnStack(this._onUnloadStack, obj, func);
		},

		addOnUnLoad: function(){
			// summary:
			//		Deprecated use addOnUnload (lower cased load)
			dojo.deprecated(this.widgetType + ".addOnUnLoad, use addOnUnload instead. (lowercased Load)", 0.5);
			this.addOnUnload.apply(this, arguments);
		},
	
		_pushOnStack: function(stack, obj, func){
			if(typeof func == 'undefined') {
				stack.push(obj);
			}else{
				stack.push(function(){ obj[func](); });
			}
		},
	
		destroy: function(){
			// make sure we call onUnload
			this.onUnload();
			dojo.widget.ContentPane.superclass.destroy.call(this);
		},
 
		onExecError: function(/*Object*/e){
			// summary:
			//		called when content script eval error or Java error occurs, preventDefault-able
			//		default is to debug not alert as in 0.3.1
		},
	
		onContentError: function(/*Object*/e){
			// summary: 
			//		called on DOM faults, require fault etc in content, preventDefault-able
			//		default is to display errormessage inside pane
		},
	
		onDownloadError: function(/*Object*/e){
			// summary: 
			//		called when download error occurs, preventDefault-able
			//		default is to display errormessage inside pane
		},
	
		onDownloadStart: function(/*Object*/e){
			// summary:
			//		called before download starts, preventDefault-able
			//		default is to display loadingMessage inside pane
			//		by changing e.text in your event handler you can change loading message
		},
	
		// 
		onDownloadEnd: function(/*String*/ url, /*String*/ data){
			// summary:
			//		called when download is finished
			//
			//	url: url that downloaded data
			//	data: the markup that was downloaded
			data = this.splitAndFixPaths(data, url);
			this.setContent(data);
		},
	
		// useful if user wants to prevent default behaviour ie: _setContent("Error...")
		_handleDefaults: function(e, handler, messType){
			if(!handler){ handler = "onContentError"; }

			if(dojo.lang.isString(e)){ e = {text: e}; }

			if(!e.text){ e.text = e.toString(); }

			e.toString = function(){ return this.text; };

			if(typeof e.returnValue != "boolean"){
				e.returnValue = true; 
			}
			if(typeof e.preventDefault != "function"){
				e.preventDefault = function(){ this.returnValue = false; };
			}
			// call our handler
			this[handler](e);
			if(e.returnValue){
				switch(messType){
					case true: // fallthrough, old compat
					case "alert":
						alert(e.toString()); break;
					case "debug":
						dojo.debug(e.toString()); break;
					default:
					// makes sure scripts can clean up after themselves, before we setContent
					if(this._callOnUnload){ this.onUnload(); } 
					// makes sure we dont try to call onUnLoad again on this event,
					// ie onUnLoad before 'Loading...' but not before clearing 'Loading...'
					this._callOnUnload = false;

					// we might end up in a endless recursion here if domNode cant append content
					if(arguments.callee._loopStop){
						dojo.debug(e.toString());
					}else{
						arguments.callee._loopStop = true;
						this._setContent(e.toString());
					}
				}
			}
			arguments.callee._loopStop = false;
		},
	
		// pathfixes, require calls, css stuff and neccesary content clean
		splitAndFixPaths: function(/*String*/s, /*String||dojo.uri.Uri, optional*/url){
			// summary:
			// 		adjusts all relative paths in (hopefully) all cases, images, remote scripts, links etc.
			// 		splits up content in different pieces, scripts, title, style, link and whats left becomes .xml

			//	s:	The markup in string
			//	url: url that pulled in markup

			var titles = [], scripts = [],tmp = [];// init vars
			var match = [], requires = [], attr = [], styles = [];
			var str = '', path = '', fix = '', tagFix = '', tag = '', origPath = '';
	
			if(!url) { url = "./"; } // point to this page if not set

			if(s){ // make sure we dont run regexes on empty content

				/************** <title> ***********/
				// khtml is picky about dom faults, you can't attach a <style> or <title> node as child of body
				// must go into head, so we need to cut out those tags
				var regex = /<title[^>]*>([\s\S]*?)<\/title>/i;
				while(match = regex.exec(s)){
					titles.push(match[1]);
					s = s.substring(0, match.index) + s.substr(match.index + match[0].length);
				};
		
				/************** adjust paths *****************/
				if(this.adjustPaths){
					// attributepaths one tag can have multiple paths example:
					// <input src="..." style="url(..)"/> or <a style="url(..)" href="..">
					// strip out the tag and run fix on that.
					// this guarantees that we won't run replace on another tag's attribute + it was easier do
					var regexFindTag = /<[a-z][a-z0-9]*[^>]*\s(?:(?:src|href|style)=[^>])+[^>]*>/i;
					var regexFindAttr = /\s(src|href|style)=(['"]?)([\w()\[\]\/.,\\'"-:;#=&?\s@]+?)\2/i;
					// these are the supported protocols, all other is considered relative
					var regexProtocols = /^(?:[#]|(?:(?:https?|ftps?|file|javascript|mailto|news):))/;
		
					while(tag = regexFindTag.exec(s)){
						str += s.substring(0, tag.index);
						s = s.substring((tag.index + tag[0].length), s.length);
						tag = tag[0];
			
						// loop through attributes
						tagFix = '';
						while(attr = regexFindAttr.exec(tag)){
							path = ""; origPath = attr[3];
							switch(attr[1].toLowerCase()){
								case "src":// falltrough
								case "href":
									if(regexProtocols.exec(origPath)){
										path = origPath;
									} else {
										path = (new dojo.uri.Uri(url, origPath).toString());
									}
									break;
								case "style":// style
									path = dojo.html.fixPathsInCssText(origPath, url);
									break;
								default:
									path = origPath;
							}
							fix = " " + attr[1] + "=" + attr[2] + path + attr[2];
							// slices up tag before next attribute check
							tagFix += tag.substring(0, attr.index) + fix;
							tag = tag.substring((attr.index + attr[0].length), tag.length);
						}
						str += tagFix + tag; //dojo.debug(tagFix + tag);
					}
					s = str+s;
				}

				/****************  cut out all <style> and <link rel="stylesheet" href=".."> **************/
				regex = /(?:<(style)[^>]*>([\s\S]*?)<\/style>|<link ([^>]*rel=['"]?stylesheet['"]?[^>]*)>)/i;
				while(match = regex.exec(s)){
					if(match[1] && match[1].toLowerCase() == "style"){
						styles.push(dojo.html.fixPathsInCssText(match[2],url));
					}else if(attr = match[3].match(/href=(['"]?)([^'">]*)\1/i)){
						styles.push({path: attr[2]});
					}
					s = s.substring(0, match.index) + s.substr(match.index + match[0].length);
				};

				/***************** cut out all <script> tags, push them into scripts array ***************/
				var regex = /<script([^>]*)>([\s\S]*?)<\/script>/i;
				var regexSrc = /src=(['"]?)([^"']*)\1/i;
				var regexDojoJs = /.*(\bdojo\b\.js(?:\.uncompressed\.js)?)$/;
				var regexInvalid = /(?:var )?\bdjConfig\b(?:[\s]*=[\s]*\{[^}]+\}|\.[\w]*[\s]*=[\s]*[^;\n]*)?;?|dojo\.hostenv\.writeIncludes\(\s*\);?/g;
				var regexRequires = /dojo\.(?:(?:require(?:After)?(?:If)?)|(?:widget\.(?:manager\.)?registerWidgetPackage)|(?:(?:hostenv\.)?setModulePrefix|registerModulePath)|defineNamespace)\((['"]).*?\1\)\s*;?/;

				while(match = regex.exec(s)){
					if(this.executeScripts && match[1]){
						if(attr = regexSrc.exec(match[1])){
							// remove a dojo.js or dojo.js.uncompressed.js from remoteScripts
							// we declare all files named dojo.js as bad, regardless of path
							if(regexDojoJs.exec(attr[2])){
								dojo.debug("Security note! inhibit:"+attr[2]+" from  being loaded again.");
							}else{
								scripts.push({path: attr[2]});
							}
						}
					}
					if(match[2]){
						// remove all invalid variables etc like djConfig and dojo.hostenv.writeIncludes()
						var sc = match[2].replace(regexInvalid, "");
						if(!sc){ continue; }
		
						// cut out all dojo.require (...) calls, if we have execute 
						// scripts false widgets dont get there require calls
						// takes out possible widgetpackage registration as well
						while(tmp = regexRequires.exec(sc)){
							requires.push(tmp[0]);
							sc = sc.substring(0, tmp.index) + sc.substr(tmp.index + tmp[0].length);
						}
						if(this.executeScripts){
							scripts.push(sc);
						}
					}
					s = s.substr(0, match.index) + s.substr(match.index + match[0].length);
				}

				/********* extract content *********/
				if(this.extractContent){
					match = s.match(/<body[^>]*>\s*([\s\S]+)\s*<\/body>/im);
					if(match) { s = match[1]; }
				}
	
				/*** replace scriptScope prefix in html Event handler
				* working order: find tags with scriptScope in a tag attribute
				* then replace all standalone scriptScope occurencies with reference to to this widget
				* valid onClick="scriptScope.func()" or onClick="scriptScope['func']();scriptScope.i++"
				* not valid onClick="var.scriptScope.ref" nor onClick="var['scriptScope'].ref" */
				if(this.executeScripts && this.scriptSeparation){
					var regex = /(<[a-zA-Z][a-zA-Z0-9]*\s[^>]*?\S=)((['"])[^>]*scriptScope[^>]*>)/;
					var regexAttr = /([\s'";:\(])scriptScope(.*)/; // we rely on that attribute begins ' or "
					str = ""; 
					while(tag = regex.exec(s)){
						tmp = ((tag[3]=="'") ? '"': "'");fix= "";
						str += s.substring(0, tag.index) + tag[1];
						while(attr = regexAttr.exec(tag[2])){
							tag[2] = tag[2].substring(0, attr.index) + attr[1] + "dojo.widget.byId("+ tmp + this.widgetId + tmp + ").scriptScope" + attr[2];
						}
						str += tag[2];
						s = s.substr(tag.index + tag[0].length);
					}
					s = str + s;
				}
	 		}

			return {"xml": 		s, // Object
				"styles":		styles,
				"titles": 		titles,
				"requires": 	requires,
				"scripts": 		scripts,
				"url": 			url};
		},
	
		
		_setContent: function(cont){
			this.destroyChildren();
	
			// remove old stylenodes from HEAD
			for(var i = 0; i < this._styleNodes.length; i++){
				if(this._styleNodes[i] && this._styleNodes[i].parentNode){
					this._styleNodes[i].parentNode.removeChild(this._styleNodes[i]);
				}
			}
			this._styleNodes = [];
	
			var node = this.containerNode || this.domNode;
			while(node.firstChild){
				try{
					dojo.event.browser.clean(node.firstChild);
				}catch(e){}
				node.removeChild(node.firstChild);
			}
			try{
				if(typeof cont != "string"){
					node.innerHTML = "";
					node.appendChild(cont);
				}else{
					node.innerHTML = cont;
				}
			}catch(e){
				e.text = "Couldn't load content:"+e.description;
				this._handleDefaults(e, "onContentError");
			}
		},
	
		setContent: function(/*String||DomNode*/ data){
			// summary:
			//		Replaces old content with data content, include style classes from old content
			//	data:	new content, be it Document fragment or a DomNode chain
			//			If data contains style tags, link rel=stylesheet it inserts those styles into DOM
			this.abort();
			if(this._callOnUnload){ this.onUnload(); }// this tells a remote script clean up after itself
			this._callOnUnload = true;
	
			if(!data || dojo.html.isNode(data)){
				// if we do a clean using setContent(""); or setContent(#node) bypass all parsing, extractContent etc
				this._setContent(data);
				this.onResized();
				this.onLoad();
			}else{
				// need to run splitAndFixPaths? ie. manually setting content
				// adjustPaths is taken care of inside splitAndFixPaths
				if(typeof data.xml != "string"){ 
					this.href = ""; // so we can refresh safely
					data = this.splitAndFixPaths(data); 
				}

				this._setContent(data.xml);

				// insert styles from content (in same order they came in)
				for(var i = 0; i < data.styles.length; i++){
					if(data.styles[i].path){
						this._styleNodes.push(dojo.html.insertCssFile(data.styles[i].path));
					}else{
						this._styleNodes.push(dojo.html.insertCssText(data.styles[i]));
					}
				}
	
				if(this.parseContent){
					for(var i = 0; i < data.requires.length; i++){
						try{
							eval(data.requires[i]);
						} catch(e){
							e.text = "ContentPane: error in package loading calls, " + (e.description||e);
							this._handleDefaults(e, "onContentError", "debug");
						}
					}
				}
				// need to allow async load, Xdomain uses it
				// is inline function because we cant send args to dojo.addOnLoad
				var _self = this;
				function asyncParse(){
					if(_self.executeScripts){
						_self._executeScripts(data.scripts);
					}
	
					if(_self.parseContent){
						var node = _self.containerNode || _self.domNode;
						var parser = new dojo.xml.Parse();
						var frag = parser.parseElement(node, null, true);
						// createSubComponents not createComponents because frag has already been created
						dojo.widget.getParser().createSubComponents(frag, _self);
					}
	
					_self.onResized();
					_self.onLoad();
				}
				// try as long as possible to make setContent sync call
				if(dojo.hostenv.isXDomain && data.requires.length){
					dojo.addOnLoad(asyncParse);
				}else{
					asyncParse();
				}
			}
		},

		setHandler: function(/*Function*/ handler) {
			// summary:
			//		Generate pane content from given java function
			var fcn = dojo.lang.isFunction(handler) ? handler : window[handler];
			if(!dojo.lang.isFunction(fcn)) {
				// FIXME: needs testing! somebody with java knowledge needs to try this
				this._handleDefaults("Unable to set handler, '" + handler + "' not a function.", "onExecError", true);
				return;
			}
			this.handler = function() {
				return fcn.apply(this, arguments);
			}
		},
	
		_runHandler: function() {
			var ret = true;
			if(dojo.lang.isFunction(this.handler)) {
				this.handler(this, this.domNode);
				ret = false;
			}
			this.onLoad();
			return ret;
		},
	
		_executeScripts: function(scripts) {
			// loop through the scripts in the order they came in
			var self = this;
			var tmp = "", code = "";
			for(var i = 0; i < scripts.length; i++){
				if(scripts[i].path){ // remotescript
					dojo.io.bind(this._cacheSetting({
						"url": 		scripts[i].path,
						"load":     function(type, scriptStr){
								dojo.lang.hitch(self, tmp = ";"+scriptStr);
						},
						"error":    function(type, error){
								error.text = type + " downloading remote script";
								self._handleDefaults.call(self, error, "onExecError", "debug");
						},
						"mimetype": "text/plain",
						"sync":     true
					}, this.cacheContent));
					code += tmp;
				}else{
					code += scripts[i];
				}
			}


			try{
				if(this.scriptSeparation){
					// initialize a new anonymous container for our script, dont make it part of this widgets scope chain
					// instead send in a variable that points to this widget, useful to connect events to onLoad, onUnload etc..
					delete this.scriptScope;
					this.scriptScope = new (new Function('_container_', code+'; return this;'))(self);
				}else{
					// exec in global, lose the _container_ feature
					var djg = dojo.global();
					if(djg.execScript){
						djg.execScript(code);
					}else{
						var djd = dojo.doc();
						var sc = djd.createElement("script");
						sc.appendChild(djd.createTextNode(code));
						(this.containerNode||this.domNode).appendChild(sc);
					}
				}
			}catch(e){
				e.text = "Error running scripts from content:\n"+e.description;
				this._handleDefaults(e, "onExecError", "debug");
			}
		}
	}
);
