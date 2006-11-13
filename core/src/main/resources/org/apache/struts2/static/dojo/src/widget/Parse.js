/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.Parse");
dojo.require("dojo.widget.Manager");
dojo.require("dojo.dom");

//
// dojoML parser should be moved out of 'widget', codifying the difference between a 'component'
// and a 'widget'. A 'component' being anything that can be generated from a tag.
//
// a particular dojoML tag would be handled by a registered tagHandler with a hook for a default handler
// if the widget system is loaded, a widget builder would be attach itself as the default handler
// 
// widget tags are no longer registered themselves:
// they are now arbitrarily namespaced, so we cannot register them all, and the non-prefixed portions 
// are no longer guaranteed unique 
// 
// therefore dojo.widget.tags should go with this parser code out of the widget module
//

dojo.widget.Parse = function(fragment){
	this.propertySetsList = [];
	this.fragment = fragment;
	
	this.createComponents = function(frag, parentComp){
		var comps = [];
		var built = false;
		// if we have items to parse/create at this level, do it!
		try{
			if((frag)&&(frag["tagName"])&&(frag!=frag["nodeRef"])){
				
				// these are in fact, not ever for widgets per-se anymore, 
				// but for other markup elements (aka components)
				var djTags = dojo.widget.tags;
				
				// we split so that you can declare multiple 
				// non-destructive components from the same ctor node
				var tna = String(frag["tagName"]).split(";");
				for(var x=0; x<tna.length; x++){
					var ltn = (tna[x].replace(/^\s+|\s+$/g, "")).toLowerCase();
					// FIXME: unsure what this does
					frag.tagName = ltn;
					if(djTags[ltn]){
						built = true;
						var ret = djTags[ltn](frag, this, parentComp, frag["index"]);
						comps.push(ret);
					} else {
						// we require a namespace prefix, default to dojo:
						if (ltn.indexOf(":") == -1){
							ltn = "dojo:"+ltn;
						}
						// FIXME: handling failure condition correctly?
						//var ret = djTags[ltn](frag, this, parentComp, frag["index"]);
						var ret = dojo.widget.buildWidgetFromParseTree(ltn, frag, this, parentComp, frag["index"]);
						if (ret) {
							built = true;
							comps.push(ret);
						}
					}
				}
			}
		}catch(e){
			dojo.debug("dojo.widget.Parse: error:" + e);
			// note, commenting out the next line is breaking several widgets for me
			// throw e;
			// IE is such a bitch sometimes
		}
		// if there's a sub-frag, build widgets from that too
		if(!built){
			comps = comps.concat(this.createSubComponents(frag, parentComp));
		}
		return comps;
	}

	/*	createSubComponents recurses over a raw JavaScript object structure,
			and calls the corresponding handler for its normalized tagName if it exists
	*/
	this.createSubComponents = function(fragment, parentComp){
		var frag, comps = [];
		for(var item in fragment){
			frag = fragment[item];
			if ((frag)&&(typeof frag == "object")&&(frag!=fragment.nodeRef)&&(frag!=fragment["tagName"])){
				comps = comps.concat(this.createComponents(frag, parentComp));
			}
		}
		return comps;
	}

	/*  parsePropertySets checks the top level of a raw JavaScript object
			structure for any propertySets.  It stores an array of references to 
			propertySets that it finds.
	*/
	this.parsePropertySets = function(fragment){
		return [];
		/*
		var propertySets = [];
		for(var item in fragment){
			if((fragment[item]["tagName"] == "dojo:propertyset")){
				propertySets.push(fragment[item]);
			}
		}
		// FIXME: should we store these propertySets somewhere for later retrieval
		this.propertySetsList.push(propertySets);
		return propertySets;
		*/
	}
	
	/*  parseProperties checks a raw JavaScript object structure for
			properties, and returns an array of properties that it finds.
	*/
	this.parseProperties = function(fragment){
		var properties = {};
		for(var item in fragment){
			// FIXME: need to check for undefined?
			// case: its a tagName or nodeRef
			if((fragment[item] == fragment["tagName"])||(fragment[item] == fragment.nodeRef)){
				// do nothing
			}else{
				if((fragment[item]["tagName"])&&
					(dojo.widget.tags[fragment[item].tagName.toLowerCase()])){
					// TODO: it isn't a property or property set, it's a fragment, 
					// so do something else
					// FIXME: needs to be a better/stricter check
					// TODO: handle xlink:href for external property sets
				}else if((fragment[item][0])&&(fragment[item][0].value!="")&&(fragment[item][0].value!=null)){
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
					}catch(e){ dojo.debug(e); }
				}
				switch(item.toLowerCase()){
				case "checked":
				case "disabled":
					if (typeof properties[item] != "boolean"){ 
						properties[item] = true;
					}
				break;
				}
			} 
		}
		return properties;
	}

	/* getPropertySetById returns the propertySet that matches the provided id
	*/
	
	this.getDataProvider = function(objRef, dataUrl){
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
			var propertySetId = this.propertySetsList[x]["id"][0].value;
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
			for(var propertySetId in propertyProviderIds){
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
		ns is the namespace of the widget.  Defaults to "dojo"
	*/
	this.createComponentFromScript = function(nodeRef, componentName, properties, ns){
		properties.fastMixIn = true;			
		// FIXME: we pulled it apart and now we put it back together ... 
		var ltn = (ns || "dojo") + ":" + componentName.toLowerCase();
		if(dojo.widget.tags[ltn]){
			return [dojo.widget.tags[ltn](properties, this, null, null, properties)];
		}
		return [dojo.widget.buildWidgetFromParseTree(ltn, properties, this, null, null, properties)];
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
 * @param name     The name of the widget to create with optional namespace prefix,
 *                 e.g."ns:widget", namespace defaults to "dojo".
 * @param props    Key-Value pairs of properties of the widget
 * @param refNode  If the position argument is specified, this node is used as
 *                 a reference for inserting this node into a DOM tree; else
 *                 the widget becomes the domNode
 * @param position The position to insert this widget's node relative to the
 *                 refNode argument
 * @return The new Widget object
 */

dojo.widget.createWidget = function(name, props, refNode, position){
	var isNode = false;
	var isNameStr = (typeof name == "string");
	if(isNameStr){
		var pos = name.indexOf(":");
		var ns = (pos > -1) ? name.substring(0,pos) : "dojo";
		if(pos > -1){ name = name.substring(pos+1); }
		var lowerCaseName = name.toLowerCase();
		var namespacedName = ns + ":" + lowerCaseName;
		isNode = (dojo.byId(name) && (!dojo.widget.tags[namespacedName])); 
	}

	if((arguments.length == 1) && ((isNode)||(!isNameStr))){
		// we got a DOM node 
		var xp = new dojo.xml.Parse(); 
		// FIXME: we should try to find the parent! 
		var tn = (isNode) ? dojo.byId(name) : name; 
		return dojo.widget.getParser().createComponents(xp.parseElement(tn, null, true))[0]; 
	}
	
	function fromScript(placeKeeperNode, name, props, ns){
		props[namespacedName] = { 
			dojotype: [{value: lowerCaseName}],
			nodeRef: placeKeeperNode,
			fastMixIn: true
		};
		props.ns = ns;
		return dojo.widget.getParser().createComponentFromScript(placeKeeperNode, name, props, ns);
	}

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
			dojo.body().appendChild(refNode);
		}
	}else if(position){
		dojo.dom.insertAtPosition(tn, refNode, position);
	}else{ // otherwise don't replace, but build in-place
		tn = refNode;
	}
	var widgetArray = fromScript(tn, name.toLowerCase(), props, ns);
	if(	(!widgetArray)||(!widgetArray[0])||
		(typeof widgetArray[0].widgetType == "undefined") ){
		throw new Error("createWidget: Creation of \"" + name + "\" widget failed.");
	}
	try{
		if(notRef){
			if(widgetArray[0].domNode.parentNode){
				widgetArray[0].domNode.parentNode.removeChild(widgetArray[0].domNode);
			}
		}
	}catch(e){
		/* squelch for Safari */
		dojo.debug(e);
	}
	return widgetArray[0]; // just return the widget
}
