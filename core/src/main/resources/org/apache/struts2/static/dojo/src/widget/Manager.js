/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.Manager");
dojo.require("dojo.lang.array");
dojo.require("dojo.lang.func");
dojo.require("dojo.event.*");

// Manager class
dojo.widget.manager = new function(){
	this.widgets = [];
	this.widgetIds = [];
	
	// map of widgetId-->widget for widgets without parents (top level widgets)
	this.topWidgets = {};

	var widgetTypeCtr = {};
	var renderPrefixCache = [];

	this.getUniqueId = function (widgetType) {
		var widgetId;
		do{
			widgetId = widgetType + "_" + (widgetTypeCtr[widgetType] != undefined ?
			++widgetTypeCtr[widgetType] : widgetTypeCtr[widgetType] = 0);
		}while(this.getWidgetById(widgetId));
		return widgetId;
	}

	this.add = function(widget){
		//dojo.profile.start("dojo.widget.manager.add");
		this.widgets.push(widget);
		// Opera9 uses ID (caps)
		if(!widget.extraArgs["id"]){
			widget.extraArgs["id"] = widget.extraArgs["ID"];
		}
		// FIXME: the rest of this method is very slow!
		if(widget.widgetId == ""){
			if(widget["id"]){
				widget.widgetId = widget["id"];
			}else if(widget.extraArgs["id"]){
				widget.widgetId = widget.extraArgs["id"];
			}else{
				widget.widgetId = this.getUniqueId(widget.widgetType);
			}
		}
		if(this.widgetIds[widget.widgetId]){
			dojo.debug("widget ID collision on ID: "+widget.widgetId);
		}
		this.widgetIds[widget.widgetId] = widget;
		// Widget.destroy already calls removeById(), so we don't need to
		// connect() it here
		//dojo.profile.end("dojo.widget.manager.add");
	}

	this.destroyAll = function(){
		for(var x=this.widgets.length-1; x>=0; x--){
			try{
				// this.widgets[x].destroyChildren();
				this.widgets[x].destroy(true);
				delete this.widgets[x];
			}catch(e){ }
		}
	}

	// FIXME: we should never allow removal of the root widget until all others
	// are removed!
	this.remove = function(widgetIndex){
		if(dojo.lang.isNumber(widgetIndex)){
			var tw = this.widgets[widgetIndex].widgetId;
			delete this.widgetIds[tw];
			this.widgets.splice(widgetIndex, 1);
		}else{
			this.removeById(widgetIndex);
		}
	}
	
	// FIXME: suboptimal performance
	this.removeById = function(id) {
		if(!dojo.lang.isString(id)){
			id = id["widgetId"];
			if(!id){ dojo.debug("invalid widget or id passed to removeById"); return; }
		}
		for (var i=0; i<this.widgets.length; i++){
			if(this.widgets[i].widgetId == id){
				this.remove(i);
				break;
			}
		}
	}

	this.getWidgetById = function(id){
		if(dojo.lang.isString(id)){
			return this.widgetIds[id];
		}
		return id;
	}

	this.getWidgetsByType = function(type){
		var lt = type.toLowerCase();
		var getType = (type.indexOf(":") < 0 ? 
			function(x) { return x.widgetType.toLowerCase(); } :
			function(x) { return x.getNamespacedType(); }
		);
		var ret = [];
		dojo.lang.forEach(this.widgets, function(x){
			if(getType(x) == lt){ret.push(x);}
		});
		return ret;
	}

	this.getWidgetsByFilter = function(unaryFunc, onlyOne){
		var ret = [];
		dojo.lang.every(this.widgets, function(x){
			if(unaryFunc(x)){
				ret.push(x);
				if(onlyOne){return false;}
			}
			return true;
		});
		return (onlyOne ? ret[0] : ret);
	}

	this.getAllWidgets = function() {
		return this.widgets.concat();
	}

	//	added, trt 2006-01-20
	this.getWidgetByNode = function(/* DOMNode */ node){
		var w=this.getAllWidgets();
		node = dojo.byId(node);
		for(var i=0; i<w.length; i++){
			if(w[i].domNode==node){
				return w[i];
			}
		}
		return null;
	}

	// shortcuts, baby
	this.byId = this.getWidgetById;
	this.byType = this.getWidgetsByType;
	this.byFilter = this.getWidgetsByFilter;
	this.byNode = this.getWidgetByNode;

	// map of previousally discovered implementation names to constructors
	var knownWidgetImplementations = {};

	// support manually registered widget packages
	var widgetPackages = ["dojo.widget"];
	for (var i=0; i<widgetPackages.length; i++) {
		// convenience for checking if a package exists (reverse lookup)
		widgetPackages[widgetPackages[i]] = true;
	}

	this.registerWidgetPackage = function(pname) {
		if(!widgetPackages[pname]){
			widgetPackages[pname] = true;
			widgetPackages.push(pname);
		}
	}
	
	this.getWidgetPackageList = function() {
		return dojo.lang.map(widgetPackages, function(elt) { return(elt!==true ? elt : undefined); });
	}
	
	this.getImplementation = function(widgetName, ctorObject, mixins, ns){
		// try and find a name for the widget
		var impl = this.getImplementationName(widgetName, ns);
		if(impl){ 
			// var tic = new Date();
			var ret = ctorObject ? new impl(ctorObject) : new impl();
			// dojo.debug(new Date() - tic);
			return ret;
		}
	}

	function buildPrefixCache() {
		for(var renderer in dojo.render){
			if(dojo.render[renderer]["capable"] === true){
				var prefixes = dojo.render[renderer].prefixes;
				for(var i=0; i<prefixes.length; i++){
					renderPrefixCache.push(prefixes[i].toLowerCase());
				}
			}
		}
		// make sure we don't HAVE to prefix widget implementation names
		// with anything to get them to render
		//renderPrefixCache.push("");
		// empty prefix is included automatically
	}
	
	var findImplementationInModule = function(lowerCaseWidgetName, module){
		if(!module){return null;}
		for(var i=0, l=renderPrefixCache.length, widgetModule; i<=l; i++){
			widgetModule = (i<l ? module[renderPrefixCache[i]] : module);
			if(!widgetModule){continue;}
			for(var name in widgetModule){
				if(name.toLowerCase() == lowerCaseWidgetName){
					return widgetModule[name];
				}
			}
		}
		return null;
	}

	var findImplementation = function(lowerCaseWidgetName, moduleName){
		// locate registered widget module
		var module = dojo.evalObjPath(moduleName, false);
		// locate a widget implementation in the registered module for our current rendering environment
		return (module ? findImplementationInModule(lowerCaseWidgetName, module) : null);
	}

	this.getImplementationName = function(widgetName, ns){
		/*
		 * Locate an implementation (constructor) for 'widgetName' in namespace 'ns' 
		 * widgetNames are case INSENSITIVE
		 * 
		 * 1. Return value from implementation cache, if available, for quick turnaround.
		 * 2. Locate a namespace registration for 'ns'
		 * 3. If no namespace found, register the conventional one (ns.widget)
		 * 4. Allow the namespace resolver (if any) to load a module for this widget.
		 * 5. Permute the widget name and capable rendering prefixes to locate, cache, and return 
		 *    an appropriate widget implementation.
		 * 6. If no implementation is found, attempt to load the namespace manifest,
		 *    and then look again for an implementation to cache and return.
		 * 7. Use the deprecated widgetPackages registration system to attempt to locate the widget
		 * 8. Fail
		 */
		var lowerCaseWidgetName = widgetName.toLowerCase();

		// default to dojo namespace
		ns=ns||"dojo";
		// use cache if available
		var imps = knownWidgetImplementations[ns] || (knownWidgetImplementations[ns]={});
		//if(!knownWidgetImplementations[ns]){knownWidgetImplementations[ns]={};}
		var impl = imps[lowerCaseWidgetName];
		if(impl){
			return impl;
		}
		
		// (one time) store a list of the render prefixes we are capable of rendering
		if(!renderPrefixCache.length){
			buildPrefixCache();
		}

		// lookup namespace
		var nsObj = dojo.ns.get(ns);
		if(!nsObj){
			// default to <ns>.widget by convention
			dojo.ns.register(ns, ns + '.widget');
			nsObj = dojo.ns.get(ns);
		}
		
		// allow the namespace to resolve the widget module
		if(nsObj){nsObj.resolve(widgetName);}

		// locate a widget implementation in the registered module for our current rendering environment
		impl = findImplementation(lowerCaseWidgetName, nsObj.module);
		if(impl){return(imps[lowerCaseWidgetName] = impl)};

		// try to load a manifest to resolve this implemenation
		nsObj = dojo.ns.require(ns);
		if((nsObj)&&(nsObj.resolver)){
			nsObj.resolve(widgetName);
			impl = findImplementation(lowerCaseWidgetName, nsObj.module);
			if(impl){return(imps[lowerCaseWidgetName] = impl)};
		}
	
		// this is an error condition under new rules
		dojo.deprecated('dojo.widget.Manager.getImplementationName', 
			'Could not locate widget implementation for "' + widgetName + '" in "' + nsObj.module + '" registered to namespace "' + nsObj.name + '". '										
			+ "Developers must specify correct namespaces for all non-Dojo widgets", "0.5");

		// backward compat: if the user has not specified any namespace and their widget is not in dojo.widget.*
		// search registered widget packages [sic]
		// note: registerWidgetPackage itself is now deprecated 
		for(var i=0; i<widgetPackages.length; i++){
			impl = findImplementation(lowerCaseWidgetName, widgetPackages[i]);
			if(impl){return(imps[lowerCaseWidgetName] = impl)};
		}
		
		throw new Error('Could not locate widget implementation for "' + widgetName + '" in "' + nsObj.module + '" registered to namespace "' + nsObj.name + '"');
	}

	// FIXME: does it even belong in this module?
	// NOTE: this method is implemented by DomWidget.js since not all
	// hostenv's would have an implementation.
	/*this.getWidgetFromPrimitive = function(baseRenderType){
		dojo.unimplemented("dojo.widget.manager.getWidgetFromPrimitive");
	}

	this.getWidgetFromEvent = function(nativeEvt){
		dojo.unimplemented("dojo.widget.manager.getWidgetFromEvent");
	}*/

	// Catch window resize events and notify top level widgets
	this.resizing=false;
	this.onWindowResized = function(){
		if(this.resizing){
			return;	// duplicate event
		}
		try{
			this.resizing=true;
			for(var id in this.topWidgets){
				var child = this.topWidgets[id];
				if(child.checkSize ){
					child.checkSize();
				}
			}
		}catch(e){
		}finally{
			this.resizing=false;
		}
	}
	if(typeof window != "undefined") {
		dojo.addOnLoad(this, 'onWindowResized');							// initial sizing
		dojo.event.connect(window, 'onresize', this, 'onWindowResized');	// window resize
	}

	// FIXME: what else?
};

(function(){
	var dw = dojo.widget;
	var dwm = dw.manager;
	var h = dojo.lang.curry(dojo.lang, "hitch", dwm);
	var g = function(oldName, newName){
		dw[(newName||oldName)] = h(oldName);
	}
	// copy the methods from the default manager (this) to the widget namespace
	g("add", "addWidget");
	g("destroyAll", "destroyAllWidgets");
	g("remove", "removeWidget");
	g("removeById", "removeWidgetById");
	g("getWidgetById");
	g("getWidgetById", "byId");
	g("getWidgetsByType");
	g("getWidgetsByFilter");
	g("getWidgetsByType", "byType");
	g("getWidgetsByFilter", "byFilter");
	g("getWidgetByNode", "byNode");
	dw.all = function(n){
		var widgets = dwm.getAllWidgets.apply(dwm, arguments);
		if(arguments.length > 0) {
			return widgets[n];
		}
		return widgets;
	}
	g("registerWidgetPackage");
	g("getImplementation", "getWidgetImplementation");
	g("getImplementationName", "getWidgetImplementationName");

	dw.widgets = dwm.widgets;
	dw.widgetIds = dwm.widgetIds;
	dw.root = dwm.root;
})();
