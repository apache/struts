/*
	Copyright (c) 2004-2005, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.Parse");

dojo.require("dojo.widget.Manager");
dojo.require("dojo.string");
dojo.require("dojo.dom");

dojo.widget.Parse = function(fragment) {
	this.propertySetsList = [];
	this.fragment = fragment;

	/*	createComponents recurses over a raw JavaScript object structure,
			and calls the corresponding handler for its normalized tagName if it exists
	*/
	this.createComponents = function(fragment, parentComp){
		var djTags = dojo.widget.tags;
		var returnValue = [];
		// this allows us to parse without having to include the parent
		// it is commented out as it currently breaks the existing mechanism for
		// adding widgets programmatically.  Once that is fixed, this can be used
		/*if( (fragment["tagName"])&&
			(fragment != fragment["nodeRef"])){
			var tn = new String(fragment["tagName"]);
			// we split so that you can declare multiple
			// non-destructive widgets from the same ctor node
			var tna = tn.split(";");
			for(var x=0; x<tna.length; x++){
				var ltn = dojo.text.trim(tna[x]).toLowerCase();
				if(djTags[ltn]){
					fragment.tagName = ltn;
					returnValue.push(djTags[ltn](fragment, this, parentComp, count++));
				}else{
					if(ltn.substr(0, 5)=="dojo:"){
						dj_debug("no tag handler registed for type: ", ltn);
					}
				}
			}
		}*/
		for(var item in fragment){
			var built = false;
			// if we have items to parse/create at this level, do it!
			try{
				if( fragment[item] && (fragment[item]["tagName"])&&
					(fragment[item] != fragment["nodeRef"])){
					var tn = new String(fragment[item]["tagName"]);
					// we split so that you can declare multiple
					// non-destructive widgets from the same ctor node
					var tna = tn.split(";");
					for(var x=0; x<tna.length; x++){
						var ltn = dojo.string.trim(tna[x]).toLowerCase();
						if(djTags[ltn]){
							built = true;
							// var tic = new Date();
							fragment[item].tagName = ltn;
							returnValue.push(djTags[ltn](fragment[item], this, parentComp, fragment[item]["index"]));
						}else{
							if(ltn.substr(0, 5)=="dojo:"){
								dojo.debug("no tag handler registed for type: ", ltn);
							}
						}
					}
				}
			}catch(e){
				dojo.debug(e);
				// throw(e);
				// IE is such a bitch sometimes
			}

			// if there's a sub-frag, build widgets from that too
			if( (!built) && (typeof fragment[item] == "object")&&
				(fragment[item] != fragment.nodeRef)&&
				(fragment[item] != fragment["tagName"])){
				returnValue.push(this.createComponents(fragment[item], parentComp));
			}
		}
		return returnValue;
	}

	/*  parsePropertySets checks the top level of a raw JavaScript object
			structure for any propertySets.  It stores an array of references to 
			propertySets that it finds.
	*/
	this.parsePropertySets = function(fragment) {
		return [];
		var propertySets = [];
		for(var item in fragment){
			if(	(fragment[item]["tagName"] == "dojo:propertyset") ) {
				propertySets.push(fragment[item]);
			}
		}
		// FIXME: should we store these propertySets somewhere for later retrieval
		this.propertySetsList.push(propertySets);
		return propertySets;
	}
	
	/*  parseProperties checks a raw JavaScript object structure for
			properties, and returns an array of properties that it finds.
	*/
	this.parseProperties = function(fragment) {
		var properties = {};
		for(var item in fragment){
			// FIXME: need to check for undefined?
			// case: its a tagName or nodeRef
			if((fragment[item] == fragment["tagName"])||
				(fragment[item] == fragment.nodeRef)){
				// do nothing
			}else{
				if((fragment[item]["tagName"])&&
					(dojo.widget.tags[fragment[item].tagName.toLowerCase()])){
					// TODO: it isn't a property or property set, it's a fragment, 
					// so do something else
					// FIXME: needs to be a better/stricter check
					// TODO: handle xlink:href for external property sets
				}else if((fragment[item][0])&&(fragment[item][0].value!="")){
					try{
						// FIXME: need to allow more than one provider
						if(item.toLowerCase() == "dataprovider") {
							var _this = this;
							this.getDataProvider(_this, fragment[item][0].value);
							properties.dataProvider = this.dataProvider;
						}
						properties[item] = fragment[item][0].value;
						var nestedProperties = this.parseProperties(fragment[item]);
						// FIXME: this kind of copying is expensive and inefficient!
						for(var property in nestedProperties){
							properties[property] = nestedProperties[property];
						}
					}catch(e){ dj_debug(e); }
				}
			}
		}
		return properties;
	}

	/* getPropertySetById returns the propertySet that matches the provided id
	*/
	
	this.getDataProvider = function(objRef, dataUrl) {
		// FIXME: this is currently sync.  To make this async, we made need to move 
		//this step into the widget ctor, so that it is loaded when it is needed 
		// to populate the widget
		dojo.io.bind({
			url: dataUrl,
			load: function(type, evaldObj){
				if(type=="load"){
					objRef.dataProvider = evaldObj;
				}
			},
			mimetype: "text/javascript",
			sync: true
		});
	}

	
	this.getPropertySetById = function(propertySetId){
		for(var x = 0; x < this.propertySetsList.length; x++){
			if(propertySetId == this.propertySetsList[x]["id"][0].value){
				return this.propertySetsList[x];
			}
		}
		return "";
	}
	
	/* getPropertySetsByType returns the propertySet(s) that match(es) the
	 * provided componentClass
	 */
	this.getPropertySetsByType = function(componentType){
		var propertySets = [];
		for(var x=0; x < this.propertySetsList.length; x++){
			var cpl = this.propertySetsList[x];
			var cpcc = cpl["componentClass"]||cpl["componentType"]||null;
			if((cpcc)&&(propertySetId == cpcc[0].value)){
				propertySets.push(cpl);
			}
		}
		return propertySets;
	}
	
	/* getPropertySets returns the propertySet for a given component fragment
	*/
	this.getPropertySets = function(fragment){
		var ppl = "dojo:propertyproviderlist";
		var propertySets = [];
		var tagname = fragment["tagName"];
		if(fragment[ppl]){ 
			var propertyProviderIds = fragment[ppl].value.split(" ");
			// FIXME: should the propertyProviderList attribute contain #
			// 		  syntax for reference to ids or not?
			// FIXME: need a better test to see if this is local or external
			// FIXME: doesn't handle nested propertySets, or propertySets that
			// 		  just contain information about css documents, etc.
			for(propertySetId in propertyProviderIds){
				if((propertySetId.indexOf("..")==-1)&&(propertySetId.indexOf("://")==-1)){
					// get a reference to a propertySet within the current parsed structure
					var propertySet = this.getPropertySetById(propertySetId);
					if(propertySet != ""){
						propertySets.push(propertySet);
					}
				}else{
					// FIXME: add code to parse and return a propertySet from
					// another document
					// alex: is this even necessaray? Do we care? If so, why?
				}
			}
		}
		// we put the typed ones first so that the parsed ones override when
		// iteration happens.
		return (this.getPropertySetsByType(tagname)).concat(propertySets);
	}
	
	/* 
		nodeRef is the node to be replaced... in the future, we might want to add 
		an alternative way to specify an insertion point

		componentName is the expected dojo widget name, i.e. Button of ContextMenu

		properties is an object of name value pairs
	*/
	this.createComponentFromScript = function(nodeRef, componentName, properties, fastMixIn){
		var frag = {};
		var tagName = "dojo:" + componentName.toLowerCase();
		frag[tagName] = {};
		var bo = {};
		properties.dojotype = componentName;
		for(var prop in properties){
			if(typeof bo[prop] == "undefined"){
				frag[tagName][prop] = [{value: properties[prop]}];
			}
		}
		frag[tagName].nodeRef = nodeRef;
		frag.tagName = tagName;
		var fragContainer = [frag];
		if(fastMixIn){
			fragContainer[0].fastMixIn = true;
		}
		// FIXME: should this really return an array?
		return this.createComponents(fragContainer);
	}
}


dojo.widget._parser_collection = {"dojo": new dojo.widget.Parse() };
dojo.widget.getParser = function(name){
	if(!name){ name = "dojo"; }
	if(!this._parser_collection[name]){
		this._parser_collection[name] = new dojo.widget.Parse();
	}
	return this._parser_collection[name];
}

/**
 * Creates widget.
 *
 * @param name     The name of the widget to create
 * @param props    Key-Value pairs of properties of the widget
 * @param refNode  If the last argument is specified this node is used as
 *                 a reference for inserting this node into a DOM tree else
 *                 it beomces the domNode
 * @param position The position to insert this widget's node relative to the
 *                 refNode argument
 * @return The new Widget object
 */
 dojo.widget.fromScript = function(name, props, refNode, position){
	if(	(typeof name != "string")&&
		(typeof props == "string")){
		// we got called with the old function signature, so just pass it on through
		// use full deref in case we're called from an alias
		return dojo.widget._oldFromScript(name, props, refNode);
	}
	/// otherwise, we just need to keep working a bit...
	props = props||{};
	var notRef = false;
	var tn = null;
	var h = dojo.render.html.capable;
	if(h){
		tn = document.createElement("span");
	}
	if(!refNode){
		notRef = true;
		refNode = tn;
		if(h){
			dojo.html.body().appendChild(refNode);
		}
	}else if(position){
		dojo.dom.insertAtPosition(tn, refNode, position);
	}else{ // otherwise don't replace, but build in-place
		tn = refNode;
	}
	var widgetArray = dojo.widget._oldFromScript(tn, name, props);
	if (!widgetArray[0] || typeof widgetArray[0].widgetType == "undefined") {
		throw new Error("Creation of \"" + name + "\" widget fromScript failed.");
	}
	if (notRef) {
		if (widgetArray[0].domNode.parentNode) {
			widgetArray[0].domNode.parentNode.removeChild(widgetArray[0].domNode);
		}
	}
	return widgetArray[0]; // not sure what the array wrapper is for, but just return the widget
}

dojo.widget._oldFromScript = function(placeKeeperNode, name, props){
	var ln = name.toLowerCase();
	var tn = "dojo:"+ln;
	props[tn] = { 
		dojotype: [{value: ln}],
		nodeRef: placeKeeperNode,
		fastMixIn: true
	};
	var ret = dojo.widget.getParser().createComponentFromScript(placeKeeperNode, name, props, true);
	return ret;
}


