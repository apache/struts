/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.html.loader");

dojo.require("dojo.widget.HtmlWidget");
dojo.require("dojo.io.*");
dojo.require("dojo.lang.common");
dojo.require("dojo.lang.extras");

dojo.require("dojo.experimental");

// as this is a singleton dojo.declare doesn't buy us anything here
dojo.widget.html.loader = new (function(){
	// summary:
	// 	loading stuff moved out of contentpane to make it directly accessible by other widgets
	this.toString = function(){ return "dojo.widget.html.loader"; }
	var _loader = this;

	// back/forward tracking
	dojo.addOnLoad(function(){
		dojo.experimental(_loader.toString());
		var undo = dojo.evalObjPath("dojo.undo.browser");
		if(djConfig["preventBackButtonFix"] && undo && !undo.initialState){
			undo.setInitialState(new trackerObj);
		}
	});

	var logger = {};
	var trackerObj = function(id, data){
		this.id = id;
		this.data = data
	};
	trackerObj.prototype.handle = function(type){
		if(typeof dojo == 'undefined'){ return; } // wtf? how can dojo become undef?
		var wg = dojo.widget.byId(this.id);
		if(wg){ wg.setContent(this.data, true); }
	};

	this._log = function(widget, data){
		// if a loader widget B is a child of loader widget A
		// we need to destroy all of B's undo if we switch content
		if(widget.trackHistory){
			if(!logger[widget.widgetId]){
				logger[widget.widgetId] = { childrenIds: [], stack:[data] };
			}var children = logger[widget.widgetId].childrenIds;
			while(children && children.length){
				delete logger[children.pop()];
			}
			for(var child in widget.children){
				logger[widget.widgetId].childrenIds = child.widgetId;
			}
			dojo.undo.browser.addToHistory(new trackerObj(widget.widgetId, dojo.lang.shallowCopy(data, true)));
		}
	}

	// shortCuts
	var undef = dojo.lang.isUndefined;
	var isFunc = dojo.lang.isFunction;



	/************ private needed functions, no need to be part of widget API ***********/
	// useful if user wants to prevent default behaviour ie: _setContent("Error...")
	function handleDefaults(e, handler, useAlert){
		if(!handler){ handler = "onContentError"; }
		if(dojo.lang.isString(e)){ e = {_text: e}; }
		if(!e._text){ e._text = e.toString(); }
		e.toString = function(){ return this._text; };
		if(typeof e.returnValue != "boolean"){
			e.returnValue = true; 
		}
		if(typeof e.preventDefault != "function"){
			e.preventDefault = function(){ this.returnValue = false; };
		}
		// call our handler
		this[handler](e);
		if(e.returnValue){
			if(useAlert){
				alert(e.toString());
			}else{
				this.loader.callOnUnLoad.call(this, false);
				this.onSetContent(e.toString());
			}
		}
	};

	// set up downloader, used by both scripts and content
	function downloader(bindArgs) {
		for(var x in this.bindArgs){
			bindArgs[x] = (undef(bindArgs[x]) ? this.bindArgs[x] : undefined);
		}
		var cache = this.cacheContent;
		if(undef(bindArgs.useCache)){ bindArgs.useCache = cache; }
		if(undef(bindArgs.preventCache)){ bindArgs.preventCache = !cache; }
		if(undef(bindArgs.mimetype)){ bindArgs.mimetype = "text/html"; }
		this.loader.bindObj = dojo.io.bind(bindArgs);
	};

	// runs addOnLoad/addOnUnLoad functions
	function stackRunner(st){
		var err = "", func = null;
		var scope = this.scriptScope || dojo.global();
		while(st.length){
			func = st.shift();
			try{
				func.call(scope);
			}catch(e){
				err += "\n"+func+" failed: "+e;
			}
		}
		if(err.length){
			var name = (st== this.loader.addOnLoads) ? "addOnLoad" : "addOnUnLoad";
			handleDefaults.call(this, name+" failure\n "+err, "onExecError", true);
		}
	};

	// push addOnLoad and addOnUnLoad functions onto stack
	function stackPusher(st, obj, func){
		if(typeof func == 'undefined') {
			st.push(obj);
		}else{
			st.push(function(){ obj[func](); });
		}
	};

	// code saver, collects onLoad, onResized and isLoaded
	function refreshed(){
		this.onResized();
		this.onLoad();
		this.isLoaded = true;
	};

	// runs scripts and starts the content parser
	function asyncParse(data){
		if(this.executeScripts){
			this.onExecScript.call(this, data.scripts);
		}
		if(this.parseContent){
			this.onContentParse.call(this);
		}
		refreshed.call(this);
	};

	// run java function
	function runHandler(){
		//FIXME: current behaviour is to return false if handler is there; is that intended?
		if(dojo.lang.isFunction(this.handler)) {
			this.handler(this, this.containerNode||this.domNode);
			refreshed.call(this);
			return false;
		}
		return true;
	};

	// divided up splitAndFixPaths in different parts
	this.htmlContentBasicFix = function(/*string*/s, /*string||dojo.uri.Uri*/url){
		// summary:
		//	strips out <style, <link rel=stylesheet and <title tags
		//	intended to take out tags that might cause DOM faults
		var titles = [], styles = [];
		/************** <title> ***********/
		// khtml can't attach a <style> or <title> node as child of body
		var regex = /<title[^>]*>([\s\S]*?)<\/title>/i;
		var match, attr;
		while(match = regex.exec(s)){
			titles.push(match[1]);
			s = s.substring(0, match.index) + s.substr(match.index + match[0].length);
		};
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
		return {'s': s, 'titles': titles, 'styles': styles};//object
	};

	this.htmlContentAdjustPaths = function(/*string*/s, /*string||dojo.uri.Uri*/url){
		// summary:
		//	adjusts relative paths in content to be relative to current page
		var tag = "", str = "", tagFix = "", path = "";
		var attr = [], origPath = "", fix = "";

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
			str += tagFix + tag;
		}
		return str+s; // string
	};


	this.htmlContentScripts = function(/*string*/s, /*boolean*/collectScripts){
		// summary:
		// 	handles scripts and dojo .require(...) etc calls
		// NOTE: we need to go through here even if we have executeScripts=false
		//		 and if we have parseWidgets true 
		var scripts = [], requires = [], match = [];
		var attr = "", tmp = null, tag = "", sc = "", str = "";
		
		/***************** cut out all <script> tags, push them into scripts array ***************/
		var regex = /<script([^>]*)>([\s\S]*?)<\/script>/i;
		var regexSrc = /src=(['"]?)([^"']*)\1/i;
		var regexDojoJs = /.*(\bdojo\b\.js(?:\.uncompressed\.js)?)$/;
		var regexInvalid = /(?:var )?\bdjConfig\b(?:[\s]*=[\s]*\{[^}]+\}|\.[\w]*[\s]*=[\s]*[^;\n]*)?;?|dojo\.hostenv\.writeIncludes\(\s*\);?/g;
		var regexRequires = /dojo\.(?:(?:require(?:After)?(?:If)?)|(?:widget\.(?:manager\.)?registerWidgetPackage)|(?:(?:hostenv\.)?setModulePrefix)|defineNamespace)\((['"]).*?\1\)\s*;?/;

		while(match = regex.exec(s)){
			if(this.executeScripts && match[1]){
				if(attr = regexSrc.exec(match[1])){
					// remove a dojo.js or dojo.js.uncompressed.js from remoteScripts
					// we declare all files named dojo.js as bad, regardless of path
					if(regexDojoJs.exec(attr[2])){
						dojo.debug("Security note! inhibit:"+attr[2]+" from  beeing loaded again.");
					}else{
						scripts.push({path: attr[2]});
					}
				}
			}
			if(match[2]){
				// remove all invalid variables etc like djConfig and dojo.hostenv.writeIncludes()
				sc = match[2].replace(regexInvalid, "");
				if(!sc){ continue; }

				// cut out all dojo .require (...) calls, if we have execute 
				// scripts false widgets don't get their require calls
				// takes out possible widgetpackage registration as well
				while(tmp = regexRequires.exec(sc)){
					requires.push(tmp[0]);
					sc = sc.substring(0, tmp.index) + sc.substr(tmp.index + tmp[0].length);
				}
				if(collectScripts){
					scripts.push(sc);
				}
			}
			s = s.substr(0, match.index) + s.substr(match.index + match[0].length);
		}
		/******** scan for scriptScope in html eventHandlers 
					and replace with link to this widget *********/
		if(collectScripts){
			var regex = /(<[a-zA-Z][a-zA-Z0-9]*\s[^>]*\S=(['"])[^>]*[^\.\]])scriptScope([^>]*>)/;
			str = "";
			while(tag = regex.exec(s)){
				tmp = ((tag[2]=="'") ? '"': "'");
				str += s.substring(0, tag.index);
				s = s.substr(tag.index).replace(regex, "$1dojo.widget.byId("+ tmp + this.widgetId + tmp + ").scriptScope$3");
			}
			s = str + s;
		}
		return {'s': s, 'requires': requires, 'scripts': scripts}; // object
	};

		
	this.splitAndFixPaths = function(/*object*/args){
		// summary:
		//	pathfixes, require calls, css stuff and neccesary content clean
		// args:
		//	content 		string
		//	url 			string? or dojo.uri.Uri that that pulled the content in, for path adjust
		//	adjustPaths		boolean, if true adjust relative paths in content to match this page
		//	collectScripts	boolean, if true it takes out all <script and <script src=.. tags and collects
		//					 dojo.require calls in a separate array, useful for eval
		//	collectRequires	boolean, if true and collectScripts is false it still collects scripts along with
		//					 dojo.require calls
		//	bodyExtract		boolean, if true only return content inside of the body tag

		// return:			{xml: string,
		//					styles: array, remote style get object {path: /*string*/url}
		//					requires: array,
		//					scripts: array, remote scripts get object {path: /*string*/url}
		//					url: string}
		if(!args.url) { args.url = "./"; } // point to this page if not set
		// make sure back/forward buttons don't mess up url.
		url = new dojo.uri.Uri(location, args.url).toString();
		var ret = {'xml': 	"",
				'styles':	[],
				'titles':	[],
				'requires':	[],
				'scripts':	[],
				'url':		url };

		if(args.content){ // make sure we don't run regexes on empty content
			var tmp = null, content = args.content;
			if(args.adjustPaths){
				content = _loader.htmlContentAdjustPaths.call(this, content, url);
			}

			tmp = _loader.htmlContentBasicFix.call(this, content, url);
			content = tmp.s;
			ret.styles = tmp.styles;
			ret.titles = tmp.titles;

			if(args.collectRequires || args.collectScripts){
				tmp = _loader.htmlContentScripts.call(this, content, args.collectScripts);
				content = tmp.s;
				ret.requires = tmp.requires;
				ret.scripts = tmp.scripts;
			}

			/********* extract content *********/
			var match = [];
			if(args.bodyExtract){
				match = content.match(/<body[^>]*>\s*([\s\S]+)\s*<\/body>/im);
				if(match) { content = match[1]; }
			}
			ret.xml = content;
		}
		return ret;// object 
	};


	// the all important startup function
	this.hookUp = function(/*object*/args){
		// summary:
		// 	mixin or extend loader into a widget
		// args:
		//	widget: widget reference
		//	mixin: boolean, default false
		// 		if mixin true, it will only extend the current widget, not its prototype
		var widget = args.widget;
		if(dojo.lang.isString(widget)){
			if(args.mixin){	
				dojo.raise(this.toString()+", cant use mixin when widget is a string");
			 }
			widget = dojo.evalObjPath(widget);
		}
		if(!widget || !(widget instanceof dojo.widget.HtmlWidget)){
			dojo.raise(this.toString()+" Widget isn't defined or isn't a HtmlWidget instance"); 
		}
		// make sure we don't mixin more than once
		if(widget.loader && widget.setUrl){ return; }

		// extend widget prototype or mixin this widget instance
		var widgetProto = (args.mixin) ? widget : widget.constructor.prototype;
	
		/********************************************
		** per widgetImpl variables, mixin into widget 
		********************************************/
		// stuff it into a loader obj
		widget.loader = {
			isLoaded: false,
			styleNodes:  [],
			addOnLoads: [],
			addOnUnLoads: [],
			callOnUnLoad:(function(canCall){
							return function(after){ this.abort();
								if(canCall){ this.onUnLoad(); }
								canCall = after;
							};
						})(false),
			bindObj: null,
			// to disconnect widget
			unHook: (function(w, wg){
				var oldProps = {
					isContainer: w.isContainer,
					adjustPats: w.adjustPaths,
					href: w.href,
					extractContent: w.extractContent,
					parseContent: w.parseContent,
					cacheContent: w.cacheContent,
					bindArgs: w.bindArgs,
					preload: w.preload,
					refreshOnShow: w.refreshOnShow,
					handler: w.handler,
					trackHistory: w.trackHistory,
					executeScripts: w.executeScripts,
					scriptScope: w.scriptScope,
					// functions
					postCreate: w.postCreate,
					show: w.show,
					refresh: w.refresh,
					loadContents: w.loadContents,
					abort: w.abort,
					destroy: w.destroy,
					onLoad: w.onLoad,
					onUnLoad: w.onUnLoad,
					addOnLoad: w.addOnLoad,
					addOnUnLoad: w.addOnUnLoad,
					onDownloadStart: w.onDownloadStart,
					onDownloadEnd: w.onDownloadEnd,
					onDownloadError: w.onDownloadError,
					onContentError: w.onContentError,
					onExecError: w.onExecError,
					onSetContent: w.onSetContent,
					setUrl: w.setUrl,
					setContent: w.setContent,
					onContentParse: w.onContentParse,
					onExecScript: w.onExecScript,
					setHandler: w.setHandler
				};
				return function(){
					if(wg.abort){ wg.abort(); }
					// make sure we don't unhook prototype if there are more widgets of this type left
					if((w != wg) && (dojo.widget.byType(wg.widgetType).length>1)){ return; }
					for(var x in oldProps){
						if(oldProps[x]===undefined){
							delete w[x]; continue;
						}
						w[x] = oldProps[x];
					}
					delete wg._loader_defined;
					delete wg.loader;
				};
			})(widgetProto, widget)
		};

		// make sure we don't do this more than once per widget/widgetprototype
		if(widgetProto._loader_defined || widget._loader_defined){ return; }		

		/**************** private variables *********************/

		// loading options, prototype parts of widget's mixin to prototype
		dojo.mixin(widgetProto, {
			// always set to a containerwidget
			isContainer: true,
			// fix relative paths in content to fit into this page
			adjustPaths: 	undef(widgetProto.adjustPaths) ? true : widgetProto.adjustPaths,
			// only usable on construction, use setUrl or setContent after that
			href: 			undef(widgetProto.href) ? "" : widgetProto.href,
			// extract visible content from inside of <body> .... </body>
			extractContent: undef(widgetProto.extractContent) ? true : widgetProto.extractContent,
			// construct all widgets that is in content
			// FIXME: rename to parseWidgets?
			parseContent: 	undef(widgetProto.parseContent) ? true : widgetProto.parseContent,
			// use io binds javascript cache, or if false, prevent browsercache
			cacheContent: 	undef(widgetProto.cacheContent) ? true : widgetProto.cacheContent,
			// specify  specific  io.bind arguments such as transport and useCache
			bindArgs:		undef(widgetProto.bindArgs) ? {} : widgetProto.bindArgs,
			// force load even if widget isn't shown (lazyload setting)
			preload: 		undef(widgetProto.preload) ? false : widgetProto.preload,
			// reload content automatically onShow, use with cacheContent = flase
			refreshOnShow:	undef(widgetProto.refreshOnShow) ? false : widgetProto.refreshOnShow,
			// name of java function which should generate content
			handler: 		undef(widgetProto.handler) ? "" : widgetProto.handler,
			// if true scripts in content will be evaled after content is innerHTML'ed
			executeScripts: undef(widgetProto.executeScripts) ? false : widgetProto.executeScripts,
			// log contents (back/forward support)
			trackHistory:	undef(widgetProto.tracHistory) ? false : widgetProto.trackHistory,
			scriptScope: null // always overwrite
		});

		/****************************************************
		******* public functions, becomes part of widget's API
		*****************************************************/

		/*********** Public functions that wigets cant overide **********/
		// set up postCreate, call originalcode before our own
		widgetProto.postCreate = (function(postCreate){
			return function(){
				if(widgetProto.constructor.superclass.postCreate != postCreate){
					postCreate.apply(this, arguments);
				}else{
					widgetProto.constructor.superclass.postCreate.apply(this, arguments);
				}
				if(this.handler!==""){ this.setHandler(this.handler); }
				if(this.isShowing() || this.preload){ 
					this.loadContents();
					if(!this.href){ // back/forward save initial state
						_loader._log(this,(this.domNode||this.containerNode).innerHTML);
					}
				}
			}
		})(widgetProto.postCreate);

		// set up onShow listener, call original code after this block
		widgetProto.show = (function(show){
			return function(){
				// if refreshOnShow is true, reload the contents every time; otherwise, load only the first time
				if(this.refreshOnShow){
					this.refresh();
				}else{ 
					this.loadContents();
				}
				if((widgetProto.constructor.superclass.show == show) || !isFunc(show)){
					widgetProto.constructor.superclass.show.apply(this, arguments);
				}else{
					show.apply(this, arguments);
				}
			};
		})(widgetProto.show);

		// destroy cleanups, original code in the middle
		widgetProto.destroy = (function(destroy){
			return function(destroy){
				this.onUnLoad();
				this.abort();
				this.loader.unHook();
				if((widgetProto.constructor.superclass.destroy != destroy) && isFunc(destroy)){
					destroy.apply(this, arguments);
				}else{
					widgetProto.constructor.superclass.destroy.apply(this, arguments);
				}
			}
		})(widgetProto.destroy);


		/******* Public functions that widgets can overide *****/
		// set up a refresh function
		if(!widgetProto.refresh){
			widgetProto.refresh = function(){
				this.loader.isLoaded = false;
				this.loadContents();
			};
		}

		// set up html loading contents
		if(!widgetProto.loadContents){
			widgetProto.loadContents = function(){
				if(this.loader.isLoaded){ return; }
				// javafunction
				if(isFunc(this.handler)){
					runHandler.call(this);
				}else if(this.href !== ""){
					handleDefaults.call(this, "Loading...", "onDownloadStart");
					var self = this, url = this.href;
					downloader.call(this, {
						url: url,
						load: function(type, data, xhr){
							self.onDownloadEnd.call(self, url, data);
						},
						error: function(type, err, xhr){
							// XHR insnt a normal JS object, copy esentials
							var e = {
								responseText: xhr.responseText,
								status: xhr.status,
								statusText: xhr.statusText,
								responseHeaders: (xhr.getAllResponseHeaders) ? xhr.getAllResponseHeaders():[],
								_text: "Error loading '" + url + "' (" + xhr.status + " "+  xhr.statusText + ")"
							};
							handleDefaults.call(self, e, "onDownloadError");
							self.onLoad();
						}
					});
				}
			};
		}

		// set up abort
		if(!widgetProto.abort){
			widgetProto.abort = function(){
				if(!this.loader || !this.loader.bindObj || !this.loader.bindObj.abort){ return; }
				this.loader.bindObj.abort();
				this.loader.bindObj = null;
			};
		}

		// onLoad
		if(!widgetProto.onLoad){
			widgetProto.onLoad = function(){
				stackRunner.call(this, this.loader.addOnLoads);
				this.loader.isLoaded = true;
			};
		}

		// onUnLoad, original code in the middle
		if(!widgetProto.onUnLoad){
			widgetProto.onUnLoad = function(){
				stackRunner.call(this, this.loader.addOnUnLoads);
				delete this.scriptScope;
			}
		}

		// add to onLoad queue
		if(!widgetProto.addOnLoad){
			widgetProto.addOnLoad = function(obj, func){
				stackPusher.call(this, this.loader.addOnLoads, obj, func);
			};
		}

		// add to onUnLoad queue 
		if(!widgetProto.addOnUnLoad){
			widgetProto.addOnUnLoad = function(obj, func){
				stackPusher.call(this, this.loader.addOnUnLoads, obj, func);
			}
		}

		// script or java errors, preventDefault-able
		if(!widgetProto.onExecError){
			widgetProto.onExecError = function(){/*stub*/};
		}
	
		// called on DOM faults, require fault etc in content, preventDefault-able
		if(!widgetProto.onContentError){
			widgetProto.onContentError = function(){/*stub*/};
		}
	
		// called when download error occurs, preventDefault-able
		if(!widgetProto.onDownloadError){
			widgetProto.onDownloadError = function(){/*stub*/};
		}
	
		// called before download starts, preventDefault-able
		if(!widgetProto.onDownloadStart){
			widgetProto.onDownloadStart = function(onDownloadStart){/*stub*/};
		}
	
		// called when download is finished successfully
		if(!widgetProto.onDownloadEnd){
			widgetProto.onDownloadEnd = function(url, data){
				var args =  {content: data,
							url: url,
							adjustPaths: this.adjustPaths,
							collectScripts: this.executeScripts,
							collectRequires: this.parseContent,
							bodyExtract: this.extractContent };
				data = _loader.splitAndFixPaths.call(this, args);
				this.setContent(data);
			}
		}

		// previously called _setContent, widget defined onSetContent can modify content or cancel
		if(!widgetProto.onSetContent){
			widgetProto.onSetContent = function(cont){
				this.destroyChildren();
		
				// remove old stylenodes from HEAD
				var styleNodes = this.loader.styleNodes;
				while(styleNodes.length){
					var st = styleNodes.pop();
					if(st && st.parentNode){
						st.parentNode.removeChild(st);
					}
				}
		
				var node = this.containerNode || this.domNode;
				while(node.firstChild){
					try{
						dojo.event.browser.clean(node.firstChild);
					}catch(e){}
					node.removeChild(node.firstChild);
				}
				try{
					if(typeof cont != "string"){
						node.appendChild(cont);
					}else{
						try{// hack to deal with domfaults, ie. appending div to tablenodes
							node.innerHTML = cont;
						}catch(e){var tmp;
							(tmp = dojo.doc().createElement("div")).innerHTML = cont;
							while(tmp.firstChild){
								node.appendChild(tmp.removeChild(tmp.firstChild));
							}
						}
					}
				}catch(e){
					e._text = "Could'nt load content: "+e;
					var useAlert = (this.loader._onSetContent_err == e._text); // make sure we don't loop
					this.loader._onSetContent_err = e._text;
					handleDefaults.call(this, e, "onContentError", useAlert);
				}
			};
		}

		if(!widgetProto.setUrl){
			widgetProto.setUrl = function(url){
				this.href = url;
				this.loader.isLoaded = false;
				if ( this.preload || this.isShowing() ){
					this.loadContents();
				}
			}
		}

		if(!widgetProto.setContent){
			widgetProto.setContent = function(data, don'tLog){
				this.loader.callOnUnLoad.call(this, true);
		
				if(!data||dojo.html.isNode(data)){
					this.onSetContent(data);
					refreshed.call(this);
				}else{
					// need to run splitAndFixPaths? ie. manually setting content
					// adjustPaths is taken care of inside splitAndFixPaths
					if(typeof data.xml != 'string'){
						this.href = ""; // so we can refresh safely
						var args =  {content: data,
							url: this.href,
							adjustPaths: this.adjustPaths,
							collectScripts: this.executeScripts,
							collectRequires: this.parseContent,
							bodyExtract: this.extractContent };
						data = _loader.splitAndFixPaths.call(this, args); 
					}else if(data.url!="./"){
						 this.url = data.url;// backbutton thing
					}
					this.onSetContent(data.xml);
	
					// insert styles from content (in same order they came in)
					for(var i = 0, styles = data.styles; i < styles.length; i++){
						if(styles[i].path){
							this.loader.styleNodes.push(dojo.html.insertCssFile(styles[i].path));
						}else{
							this.loader.styleNodes.push(dojo.html.insertCssText(styles[i]));
						}
					}
		
					if(this.parseContent){
						for(var i = 0, requires = data.requires; i < requires.length; i++){
							try{
								eval(requires[i]);
							} catch(e){
								e._text = "dojo.widget.html.loader.hookUp: error in package loading calls, "+(e.description||e);
								handleDefaults.call(this, e, "onContentError", true);
							}
						}
					}
					// need to allow async load, Xdomain uses it
					// NOTE: on Xdomain loads this can break the sync thread of setContent
					// 		if you you do any dojo. require(...) etc
					if(dojo.hostenv.isXDomain && data.requires.length){
						dojo.addOnLoad(function(){ 
							asyncParse.call(this, data);
							if(!don'tLog){
								_loader._log(this, data);
							}
						});// this opens a thread need abort undo
						don'tLog = true;
					}else{
						asyncParse.call(this, data);
					}
				}if(!don'tLog){
// 					_loader._log(this, data);
				}
			};
		}

		if(!widgetProto.onContentParse){
			widgetProto.onContentParse = function(){
				var node = this.containerNode || this.domNode;
				var parser = new dojo.xml.Parse();
				var frag = parser.parseElement(node, null, true);
				dojo.widget.getParser().createSubComponents(frag, this);
			};
		}

		// previously called _executeScripts
		if(!widgetProto.onExecScript){
			widgetProto.onExecScript = function(scripts){
				// loop through the scripts in the order they came in
				var self = this, tmp = "", code = "";
				for(var i = 0; i < scripts.length; i++){ // remotescript
					if(scripts[i].path){
						var url = scripts[i].path;
						downloader.call(this,{
							'url': 		url,
							'load': function(type, scriptStr){
								(function(){tmp = scriptStr; scripts[i] = scriptStr;}).call(self);
							},
							'error': function(type, error){
								error._text = type + " downloading remote script";
								handleDefaults.call(self, error, "onExecError", true);
							},
							'mimetype': "text/plain",
							'sync':     true
						});
						code += tmp;
					}else{
						code += scripts[i];
					}
				}

				try{
					// initialize a new anonymous container for our script, don't make it part of this widget's scope chain
					// instead send in a variable that points to this widget, useful to connect events to onLoad, onUnLoad etc..
					delete this.scriptScope;
					this.scriptScope = new (new Function('_container_', code+'; return this;'))(self);
				}catch(e){
					e._text = "Error running scripts from content:\n"+(e.description||e.toString());
					handleDefaults.call(this, e, "onExecError", true);
				}
			};
		}

		// Generate content from given java function
		if(!widgetProto.setHandler){
			widgetProto.setHandler = function(handler) {
				var fcn = dojo.lang.isFunction(handler) ? handler : window[handler];
				if(!isFunc(fcn)) {
					// FIXME: needs testing! somebody with java knowledge needs to try this
					handleDefaults.call(this, "Unable to set handler, '" + handler + "' not a function.", "onExecError", true);
					return;
				}
				this.handler = function() {
					return fcn.apply(this, arguments);
				};
			};
		}

		// make sure we extend this widget only once
		widgetProto._loader_defined = true;
	};


})();
