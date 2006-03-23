/*
	Copyright (c) 2004-2005, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.Manager");
dojo.require("dojo.lang");
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
		return widgetType + "_" + (widgetTypeCtr[widgetType] != undefined ?
			++widgetTypeCtr[widgetType] : widgetTypeCtr[widgetType] = 0);
	}

	this.add = function(widget){
		dojo.profile.start("dojo.widget.manager.add");
		this.widgets.push(widget);
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
		dojo.profile.end("dojo.widget.manager.add");
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
		var tw = this.widgets[widgetIndex].widgetId;
		delete this.widgetIds[tw];
		this.widgets.splice(widgetIndex, 1);
	}
	
	// FIXME: suboptimal performance
	this.removeById = function(id) {
		for (var i=0; i<this.widgets.length; i++){
			if(this.widgets[i].widgetId == id){
				this.remove(i);
				break;
			}
		}
	}

	this.getWidgetById = function(id){
		return this.widgetIds[id];
	}

	this.getWidgetsByType = function(type){
		var lt = type.toLowerCase();
		var ret = [];
		dojo.lang.forEach(this.widgets, function(x){
			if(x.widgetType.toLowerCase() == lt){
				ret.push(x);
			}
		});
		return ret;
	}

	this.getWidgetsOfType = function (id) {
		dj_deprecated("getWidgetsOfType is depecrecated, use getWidgetsByType");
		return dojo.widget.manager.getWidgetsByType(id);
	}

	this.getWidgetsByFilter = function(unaryFunc){
		var ret = [];
		dojo.lang.forEach(this.widgets, function(x){
			if(unaryFunc(x)){
				ret.push(x);
			}
		});
		return ret;
	}

	this.getAllWidgets = function() {
		return this.widgets.concat();
	}

	// shortcuts, baby
	this.byId = this.getWidgetById;
	this.byType = this.getWidgetsByType;
	this.byFilter = this.getWidgetsByFilter;

	// map of previousally discovered implementation names to constructors
	var knownWidgetImplementations = {};

	// support manually registered widget packages
	var widgetPackages = ["dojo.widget", "dojo.webui.widgets"];
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
	
	this.getImplementation = function(widgetName, ctorObject, mixins){
		// try and find a name for the widget
		var impl = this.getImplementationName(widgetName);
		if(impl){ 
			// var tic = new Date();
			var ret = new impl(ctorObject);
			// dojo.debug(new Date() - tic);
			return ret;
		}
	}

	this.getImplementationName = function(widgetName){
		/*
		 * This is the overly-simplistic implemention of getImplementation (har
		 * har). In the future, we are going to want something that allows more
		 * freedom of expression WRT to specifying different specializations of
		 * a widget.
		 *
		 * Additionally, this implementation treats widget names as case
		 * insensitive, which does not necessarialy mesh with the markup which
		 * can construct a widget.
		 */

		var lowerCaseWidgetName = widgetName.toLowerCase();

		var impl = knownWidgetImplementations[lowerCaseWidgetName];
		if(impl){
			return impl;
		}

		// first store a list of the render prefixes we are capable of rendering
		if(!renderPrefixCache.length){
			for(var renderer in dojo.render){
				if(dojo.render[renderer]["capable"] === true){
					var prefixes = dojo.render[renderer].prefixes;
					for(var i = 0; i < prefixes.length; i++){
						renderPrefixCache.push(prefixes[i].toLowerCase());
					}
				}
			}
			// make sure we don't HAVE to prefix widget implementation names
			// with anything to get them to render
			renderPrefixCache.push("");
		}

		// look for a rendering-context specific version of our widget name
		for(var i = 0; i < widgetPackages.length; i++){
			var widgetPackage = dojo.evalObjPath(widgetPackages[i]);
			if(!widgetPackage) { continue; }

			for (var j = 0; j < renderPrefixCache.length; j++) {
				if (!widgetPackage[renderPrefixCache[j]]) { continue; }
				for (var widgetClass in widgetPackage[renderPrefixCache[j]]) {
					if (widgetClass.toLowerCase() != lowerCaseWidgetName) { continue; }
					knownWidgetImplementations[lowerCaseWidgetName] =
						widgetPackage[renderPrefixCache[j]][widgetClass];
					return knownWidgetImplementations[lowerCaseWidgetName];
				}
			}

			for (var j = 0; j < renderPrefixCache.length; j++) {
				for (var widgetClass in widgetPackage) {
					if (widgetClass.toLowerCase() !=
						(renderPrefixCache[j] + lowerCaseWidgetName)) { continue; }
	
					knownWidgetImplementations[lowerCaseWidgetName] =
						widgetPackage[widgetClass];
					return knownWidgetImplementations[lowerCaseWidgetName];
				}
			}
		}
		
		throw new Error('Could not locate "' + widgetName + '" class');
	}

	// FIXME: does it even belong in this name space?
	// NOTE: this method is implemented by DomWidget.js since not all
	// hostenv's would have an implementation.
	/*this.getWidgetFromPrimitive = function(baseRenderType){
		dj_unimplemented("dojo.widget.manager.getWidgetFromPrimitive");
	}

	this.getWidgetFromEvent = function(nativeEvt){
		dj_unimplemented("dojo.widget.manager.getWidgetFromEvent");
	}*/

	// Catch window resize events and notify top level widgets
	this.onResized = function() {
		for(var id in this.topWidgets) {
			var child = this.topWidgets[id];
			//dojo.debug("root resizing child " + child.widgetId);
			if ( child.onResized ) {
				child.onResized();
			}
		}
	}
	if(typeof window != "undefined") {
		dojo.addOnLoad(this, 'onResized');							// initial sizing
		dojo.event.connect(window, 'onresize', this, 'onResized');	// window resize
	}

	// FIXME: what else?
}

// copy the methods from the default manager (this) to the widget namespace
dojo.widget.getUniqueId = function () { return dojo.widget.manager.getUniqueId.apply(dojo.widget.manager, arguments); }
dojo.widget.addWidget = function () { return dojo.widget.manager.add.apply(dojo.widget.manager, arguments); }
dojo.widget.destroyAllWidgets = function () { return dojo.widget.manager.destroyAll.apply(dojo.widget.manager, arguments); }
dojo.widget.removeWidget = function () { return dojo.widget.manager.remove.apply(dojo.widget.manager, arguments); }
dojo.widget.removeWidgetById = function () { return dojo.widget.manager.removeById.apply(dojo.widget.manager, arguments); }
dojo.widget.getWidgetById = function () { return dojo.widget.manager.getWidgetById.apply(dojo.widget.manager, arguments); }
dojo.widget.getWidgetsByType = function () { return dojo.widget.manager.getWidgetsByType.apply(dojo.widget.manager, arguments); }
dojo.widget.getWidgetsByFilter = function () { return dojo.widget.manager.getWidgetsByFilter.apply(dojo.widget.manager, arguments); }
dojo.widget.byId = function () { return dojo.widget.manager.getWidgetById.apply(dojo.widget.manager, arguments); }
dojo.widget.byType = function () { return dojo.widget.manager.getWidgetsByType.apply(dojo.widget.manager, arguments); }
dojo.widget.byFilter = function () { return dojo.widget.manager.getWidgetsByFilter.apply(dojo.widget.manager, arguments); }
dojo.widget.all = function () { return dojo.widget.manager.getAllWidgets.apply(dojo.widget.manager, arguments); }
dojo.widget.registerWidgetPackage = function () { return dojo.widget.manager.registerWidgetPackage.apply(dojo.widget.manager, arguments); }
dojo.widget.getWidgetImplementation = function () { return dojo.widget.manager.getImplementation.apply(dojo.widget.manager, arguments); }
dojo.widget.getWidgetImplementationName = function () { return dojo.widget.manager.getImplementationName.apply(dojo.widget.manager, arguments); }

dojo.widget.widgets = dojo.widget.manager.widgets;
dojo.widget.widgetIds = dojo.widget.manager.widgetIds;
dojo.widget.root = dojo.widget.manager.root;
