/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.Widget");

dojo.require("dojo.lang.func");
dojo.require("dojo.lang.array");
dojo.require("dojo.lang.extras");
dojo.require("dojo.lang.declare");
dojo.require("dojo.ns");
dojo.require("dojo.widget.Manager");
dojo.require("dojo.event.*");
dojo.require("dojo.a11y");

dojo.declare("dojo.widget.Widget", null,
	function(){
		//dojo.debug("NEW "+this.widgetType);
		// these properties aren't primitives and need to be created on a per-item
		// basis.
		this.children = [];
		// this.selection = new dojo.widget.Selection();
		// FIXME: need to replace this with context menu stuff
		this.extraArgs = {};
	},
{
	// FIXME: need to be able to disambiguate what our rendering context is
	//        here!
	//
	// needs to be a string with the end classname. Every subclass MUST
	// over-ride.
	//
	// base widget properties

	// Widget: the parent of this widget
	parent: null, 

	// NOTE: "children" and "extraArgs" re-defined in the constructor as they need to be local to the widget

	// Array:
	//		a list of all of the widgets that have been added as children of
	//		this component. Should only have values if isContainer is true.
	children: [],

	// Object:
	//		a map of properties which the widget system tried to assign from
	//		user input but did not correspond to any of the properties set on
	//		the class prototype. These names will also be available in all
	//		lower-case form in this map
	extraArgs: {},

	// obviously, top-level and modal widgets should set these appropriately

	// Boolean: should this widget eat all events that bubble up to it?
	isTopLevel:  false, 

	// Boolean: should this widget block other widgets?
	isModal: false, 

	// Boolean: should this widget respond to user input?
	isEnabled: true,

	// Boolean: have we hidden the widget via hide()?
	isHidden: false,

	// Boolean: can this widget contain other widgets?
	isContainer: false, 

	// String:
	//		a unique, opaque ID string that can be assigned by users or by the
	//		system. If the developer passes an ID which is known not to be
	//		unique, the specified ID is ignored and the system-generated ID is
	//		used instead.
	widgetId: "",

	// String: used for building generic widgets
	widgetType: "Widget",

	// String: defaults to 'dojo'.  "namespace" is a reserved word in JavaScript, so we abbreviate
	ns: "dojo",

	getNamespacedType: function(){ 
		// summary:
		//		get the "full" name of the widget. If the widget comes from the
		//		"dojo" namespace and is a Button, calling this method will
		//		return "dojo:button", all lower-case
		return (this.ns ? this.ns + ":" + this.widgetType : this.widgetType).toLowerCase(); // String
	},
	
	toString: function(){
		// summary:
		//		returns a string that represents the widget. When a widget is
		//		cast to a string, this method will be used to generate the
		//		output. Currently, it does not implement any sort of reversable
		//		serialization.
		return '[Widget ' + this.getNamespacedType() + ', ' + (this.widgetId || 'NO ID') + ']'; // String
	},

	repr: function(){
		// summary: returns the string representation of the widget.
		return this.toString(); // String
	},

	enable: function(){
		// summary:
		//		enables the widget, usually involving unmasking inputs and
		//		turning on event handlers. Not implemented here.
		this.isEnabled = true;
	},

	disable: function(){
		// summary:
		//		disables the widget, usually involves masking inputs and
		//		unsetting event handlers. Not implemented here.
		this.isEnabled = false;
	},

	hide: function(){
		// summary: hides the widget from view. Not implemented here.
		this.isHidden = true;
	},

	show: function(){
		// summary: re-adds the widget to the view. Not implemented here.
		this.isHidden = false;
	},

	onResized: function(){
		// summary:
		//		A signal that widgets will call when they have been resized.
		//		Can be connected to for determining if a layout needs to be
		//		reflowed. Clients should override this function to do special
		//		processing, then call this.notifyChildrenOfResize() to notify
		//		children of resize.
		this.notifyChildrenOfResize();
	},
	
	notifyChildrenOfResize: function(){
		// summary: dispatches resized events to all children of this widget
		for(var i=0; i<this.children.length; i++){
			var child = this.children[i];
			//dojo.debug(this.widgetId + " resizing child " + child.widgetId);
			if( child.onResized ){
				child.onResized();
			}
		}
	},

	create: function(/*Object*/ args, /*Object*/fragment, /*Widget, optional*/parent, /*String, optional*/ns){
		// summary:
		//		'create' manages the initialization part of the widget
		//		lifecycle. It's called implicitly when any widget is created.
		//		All other initialization functions for widgets, except for the
		//		constructor, are called as a result of 'create' being fired.
		// args:
		//		a normalized view of the parameters that the widget should take
		// fragment:
		//		if the widget is being instantiated from markup, this object 
		// parent:
		//		the widget, if any, that this widget will be the child of.  If
		//		none is passed, the global default widget is used.
		// ns: what namespace the widget belongs to
		// description:
		//		to understand the process by which widgets are instantiated, it
		//		is critical to understand what other methods 'create' calls and
		//		which of them you'll want to over-ride. Of course, adventurous
		//		developers could over-ride 'create' entirely, but this should
		//		only be done as a last resort.
		//
		//		Below is a list of the methods that are called, in the order
		//		they are fired, along with notes about what they do and if/when
		//		you should over-ride them in your widget:
		//			
		//			mixInProperties:
		//				takes the args and does lightweight type introspection
		//				on pre-existing object properties to initialize widget
		//				values by casting the values that are passed in args
		//			postMixInProperties:
		//				a stub function that you can over-ride to modify
		//				variables that may have been naively assigned by
		//				mixInProperties
		//			# widget is added to manager object here
		//			buildRendering
		//				subclasses use this method to handle all UI initialization
		//			initialize:
		//				a stub function that you can over-ride.
		//			postInitialize:
		//				a stub function that you can over-ride.
		//			postCreate
		//				a stub function that you can over-ride to modify take
		//				actions once the widget has been placed in the UI
		//
		//		all of these functions are passed the same arguments as are
		//		passed to 'create'

		//dojo.profile.start(this.widgetType + " create");
		if(ns){
			this.ns = ns;
		}
		// dojo.debug(this.widgetType, "create");
		//dojo.profile.start(this.widgetType + " satisfyPropertySets");
		this.satisfyPropertySets(args, fragment, parent);
		//dojo.profile.end(this.widgetType + " satisfyPropertySets");
		// dojo.debug(this.widgetType, "-> mixInProperties");
		//dojo.profile.start(this.widgetType + " mixInProperties");
		this.mixInProperties(args, fragment, parent);
		//dojo.profile.end(this.widgetType + " mixInProperties");
		// dojo.debug(this.widgetType, "-> postMixInProperties");
		//dojo.profile.start(this.widgetType + " postMixInProperties");
		this.postMixInProperties(args, fragment, parent);
		//dojo.profile.end(this.widgetType + " postMixInProperties");
		// dojo.debug(this.widgetType, "-> dojo.widget.manager.add");
		dojo.widget.manager.add(this);
		// dojo.debug(this.widgetType, "-> buildRendering");
		//dojo.profile.start(this.widgetType + " buildRendering");
		this.buildRendering(args, fragment, parent);
		//dojo.profile.end(this.widgetType + " buildRendering");
		// dojo.debug(this.widgetType, "-> initialize");
		//dojo.profile.start(this.widgetType + " initialize");
		this.initialize(args, fragment, parent);
		//dojo.profile.end(this.widgetType + " initialize");
		// dojo.debug(this.widgetType, "-> postInitialize");
		// postinitialize includes subcomponent creation
		// profile is put directly to function
		this.postInitialize(args, fragment, parent);
		// dojo.debug(this.widgetType, "-> postCreate");
		//dojo.profile.start(this.widgetType + " postCreate");
		this.postCreate(args, fragment, parent);
		//dojo.profile.end(this.widgetType + " postCreate");
		// dojo.debug(this.widgetType, "done!");
		
		//dojo.profile.end(this.widgetType + " create");
		
		return this;
	},

	destroy: function(/*Boolean*/finalize){
		// summary:
		// 		Destroy this widget and it's descendants. This is the generic
		// 		"destructor" function that all widget users should call to
		// 		clealy discard with a widget. Once a widget is destroyed, it's
		// 		removed from the manager object.
		// finalize:
		//		is this function being called part of global environment
		//		tear-down?

		// FIXME: this is woefully incomplete
		this.destroyChildren();
		this.uninitialize();
		this.destroyRendering(finalize);
		dojo.widget.manager.removeById(this.widgetId);
	},

	destroyChildren: function(){
		// summary:
		//		Recursively destroy the children of this widget and their
		//		descendents.
		var widget;
		var i=0;
		while(this.children.length > i){
			widget = this.children[i];
			if (widget instanceof dojo.widget.Widget) { // find first widget
				this.removeChild(widget);
				widget.destroy();
				continue;
			}
			
			i++; // skip data object
		}
				
	},

	getChildrenOfType: function(/*String*/type, /*Boolean*/recurse){
		// summary: 
		//		return an array of descendant widgets who match the passed type
		// recurse:
		//		should we try to get all descendants that match? Defaults to
		//		false.
		var ret = [];
		var isFunc = dojo.lang.isFunction(type);
		if(!isFunc){
			type = type.toLowerCase();
		}
		for(var x=0; x<this.children.length; x++){
			if(isFunc){
				if(this.children[x] instanceof type){
					ret.push(this.children[x]);
				}
			}else{
				if(this.children[x].widgetType.toLowerCase() == type){
					ret.push(this.children[x]);
				}
			}
			if(recurse){
				ret = ret.concat(this.children[x].getChildrenOfType(type, recurse));
			}
		}
		return ret; // Array
	},

	getDescendants: function(){
		// summary: returns a flattened array of all direct descendants including self
		var result = [];
		var stack = [this];
		var elem;
		while ((elem = stack.pop())){
			result.push(elem);
			// a child may be data object without children field set (not widget)
			if (elem.children) {
				dojo.lang.forEach(elem.children, function(elem) { stack.push(elem); });
			}
		}
		return result; // Array
	},


	isFirstChild: function(){
		return this === this.parent.children[0]; // Boolean
	},

	isLastChild: function() {
		return this === this.parent.children[this.parent.children.length-1]; // Boolean
	},

	satisfyPropertySets: function(args){
		// summary: not implemented!

		// dojo.profile.start("satisfyPropertySets");
		// get the default propsets for our component type
		/*
		var typePropSets = []; // FIXME: need to pull these from somewhere!
		var localPropSets = []; // pull out propsets from the parser's return structure

		// for(var x=0; x<args.length; x++){
		// }

		for(var x=0; x<typePropSets.length; x++){
		}

		for(var x=0; x<localPropSets.length; x++){
		}
		*/
		// dojo.profile.end("satisfyPropertySets");
		
		return args;
	},

	mixInProperties: function(/*Object*/args, /*Object*/frag){
		// summary:
		// 		takes the list of properties listed in args and sets values of
		// 		the current object based on existence of properties with the
		// 		same name (case insensitive) and the type of the pre-existing
		// 		property. This is a lightweight conversion and is not intended
		// 		to capture custom type semantics.
		// args:
		//		A map of properties and values to set on the current object. By
		//		default it is assumed that properties in args are in string
		//		form and need to be converted. However, if there is a
		//		'fastMixIn' property with the value 'true' in the args param,
		//		this assumption is ignored and all values in args are copied
		//		directly to the current object without any form of type
		//		casting.
		// description:
		//		The mix-in code attempts to do some type-assignment based on
		//		PRE-EXISTING properties of the "this" object. When a named
		//		property of args is located, it is first tested to make
		//		sure that the current object already "has one". Properties
		//		which are undefined in the base widget are NOT settable here.
		//		The next step is to try to determine type of the pre-existing
		//		property. If it's a string, the property value is simply
		//		assigned. If a function, it is first cast using "new
		//		Function()" and the execution scope modified such that it
		//		always evaluates in the context of the current object. This
		//		listener is then added to the original function via
		//		dojo.event.connect(). If an Array, the system attempts to split
		//		the string value on ";" chars, and no further processing is
		//		attempted (conversion of array elements to a integers, for
		//		instance). If the property value is an Object
		//		(testObj.constructor === Object), the property is split first
		//		on ";" chars, secondly on ":" chars, and the resulting
		//		key/value pairs are assigned to an object in a map style. The
		//		onus is on the property user to ensure that all property values
		//		are converted to the expected type before usage. Properties
		//		which do not occur in the "this" object are assigned to the
		//		this.extraArgs map using both the original name and the
		//		lower-case name of the property. This allows for consistent
		//		access semantics regardless of the case preservation of the
		//		source of the property names.
		
		if((args["fastMixIn"])||(frag["fastMixIn"])){
			// dojo.profile.start("mixInProperties_fastMixIn");
			// fast mix in assumes case sensitivity, no type casting, etc...
			// dojo.lang.mixin(this, args);
			for(var x in args){
				this[x] = args[x];
			}
			// dojo.profile.end("mixInProperties_fastMixIn");
			return;
		}
		// dojo.profile.start("mixInProperties");

		var undef;

		// NOTE: we cannot assume that the passed properties are case-correct
		// (esp due to some browser bugs). Therefore, we attempt to locate
		// properties for assignment regardless of case. This may cause
		// problematic assignments and bugs in the future and will need to be
		// documented with big bright neon lights.

		// FIXME: fails miserably if a mixin property has a default value of null in 
		// a widget

		// NOTE: caching lower-cased args in the prototype is only 
		// acceptable if the properties are invariant.
		// if we have a name-cache, get it
		var lcArgs = dojo.widget.lcArgsCache[this.widgetType];
		if ( lcArgs == null ){
			// build a lower-case property name cache if we don't have one
			lcArgs = {};
			for(var y in this){
				lcArgs[((new String(y)).toLowerCase())] = y;
			}
			dojo.widget.lcArgsCache[this.widgetType] = lcArgs;
		}
		var visited = {};
		for(var x in args){
			if(!this[x]){ // check the cache for properties
				var y = lcArgs[(new String(x)).toLowerCase()];
				if(y){
					args[y] = args[x];
					x = y; 
				}
			}
			if(visited[x]){ continue; }
			visited[x] = true;
			if((typeof this[x]) != (typeof undef)){
				if(typeof args[x] != "string"){
					this[x] = args[x];
				}else{
					if(dojo.lang.isString(this[x])){
						this[x] = args[x];
					}else if(dojo.lang.isNumber(this[x])){
						this[x] = new Number(args[x]); // FIXME: what if NaN is the result?
					}else if(dojo.lang.isBoolean(this[x])){
						this[x] = (args[x].toLowerCase()=="false") ? false : true;
					}else if(dojo.lang.isFunction(this[x])){

						// FIXME: need to determine if always over-writing instead
						// of attaching here is appropriate. I suspect that we
						// might want to only allow attaching w/ action items.
						
						// RAR, 1/19/05: I'm going to attach instead of
						// over-write here. Perhaps function objects could have
						// some sort of flag set on them? Or mixed-into objects
						// could have some list of non-mutable properties
						// (although I'm not sure how that would alleviate this
						// particular problem)? 

						// this[x] = new Function(args[x]);

						// after an IRC discussion last week, it was decided
						// that these event handlers should execute in the
						// context of the widget, so that the "this" pointer
						// takes correctly.
						
						// argument that contains no punctuation other than . is 
						// considered a function spec, not code
						if(args[x].search(/[^\w\.]+/i) == -1){
							this[x] = dojo.evalObjPath(args[x], false);
						}else{
							var tn = dojo.lang.nameAnonFunc(new Function(args[x]), this);
							dojo.event.kwConnect({
								srcObj: this, 
								srcFunc: x, 
								adviceObj: this, 
								adviceFunc: tn
							});
						}
					}else if(dojo.lang.isArray(this[x])){ // typeof [] == "object"
						this[x] = args[x].split(";");
					} else if (this[x] instanceof Date) {
						this[x] = new Date(Number(args[x])); // assume timestamp
					}else if(typeof this[x] == "object"){ 
						// FIXME: should we be allowing extension here to handle
						// other object types intelligently?

						// if we defined a URI, we probably want to allow plain strings
						// to override it
						if (this[x] instanceof dojo.uri.Uri){

							this[x] = args[x];
						}else{

							// FIXME: unlike all other types, we do not replace the
							// object with a new one here. Should we change that?
							var pairs = args[x].split(";");
							for(var y=0; y<pairs.length; y++){
								var si = pairs[y].indexOf(":");
								if((si != -1)&&(pairs[y].length>si)){
									this[x][pairs[y].substr(0, si).replace(/^\s+|\s+$/g, "")] = pairs[y].substr(si+1);
								}
							}
						}
					}else{
						// the default is straight-up string assignment. When would
						// we ever hit this?
						this[x] = args[x];
					}
				}
			}else{
				// collect any extra 'non mixed in' args
				this.extraArgs[x.toLowerCase()] = args[x];
			}
		}
		// dojo.profile.end("mixInProperties");
	},
	
	postMixInProperties: function(/*Object*/args, /*Object*/frag, /*Widget*/parent){
		// summary:
		//		stub function. Can be over-ridden to handle advanced property
		//		casting and object configuration.
	},

	initialize: function(/*Object*/args, /*Object*/frag, /*Widget*/parent){
		// summary: stub function.
		return false;
		// dojo.unimplemented("dojo.widget.Widget.initialize");
	},

	postInitialize: function(/*Object*/args, /*Object*/frag, /*Widget*/parent){
		// summary: stub function.
		return false;
	},

	postCreate: function(/*Object*/args, /*Object*/frag, /*Widget*/parent){
		// summary: stub function.
		return false;
	},

	uninitialize: function(){
		// summary: 
		//		stub function. Over-ride to implement custom widget tear-down
		//		behavior.
		return false;
	},

	buildRendering: function(/*Object*/args, /*Object*/frag, /*Widget*/parent){
		// summary: stub function. SUBCLASSES MUST IMPLEMENT
		dojo.unimplemented("dojo.widget.Widget.buildRendering, on "+this.toString()+", ");
		return false;
	},

	destroyRendering: function(){
		// summary: stub function. SUBCLASSES MUST IMPLEMENT
		dojo.unimplemented("dojo.widget.Widget.destroyRendering");
		return false;
	},

	cleanUp: function(){
		// summary: 
		//		stub function for destruction finalization. SUBCLASSES MUST
		//		IMPLEMENT
		dojo.unimplemented("dojo.widget.Widget.cleanUp");
		return false;
	},

	addedTo: function(/*Widget*/parent){
		// summary:
		//		stub function this is just a signal that can be caught
		// parent: instance of dojo.widget.Widget that we were added to
	},

	addChild: function(child){
		// summary: stub function. SUBCLASSES MUST IMPLEMENT
		dojo.unimplemented("dojo.widget.Widget.addChild");
		return false;
	},

	// Detach the given child widget from me, but don't destroy it
	removeChild: function(/*Widget*/widget){
		// summary: 
		//		removes the passed widget instance from this widget but does
		//		not destroy it
		for(var x=0; x<this.children.length; x++){
			if(this.children[x] === widget){
				this.children.splice(x, 1);
				break;
			}
		}
		return widget; // Widget
	},

	resize: function(/*String or int*/width, /*String or int*/height){
		// summary:
		// 		both width and height may be set as percentages. The setWidth
		// 		and setHeight  functions attempt to determine if the passed
		// 		param is specified in percentage or native units. Integers
		// 		without a measurement are assumed to be in the native unit of
		// 		measure.
		// width:
		//		the width, either in native measures, or as a percentage. If as
		//		percentage, pass it as a string in the form "30%".
		// height:
		//		the height, either in native measures, or as a percentage. If as
		//		percentage, pass it as a string in the form "30%".
		this.setWidth(width);
		this.setHeight(height);
	},

	setWidth: function(/*String or int*/width){
		// summary: like it says on the tin...
		// width:
		//		the width, either in native measures, or as a percentage. If as
		//		percentage, pass it as a string in the form "30%".
		if((typeof width == "string")&&(width.substr(-1) == "%")){
			this.setPercentageWidth(width);
		}else{
			this.setNativeWidth(width);
		}
	},

	setHeight: function(/*String or int*/height){
		// summary: like it says on the tin...
		// height:
		//		the height, either in native measures, or as a percentage. If
		//		as percentage, pass it as a string in the form "30%".
		if((typeof height == "string")&&(height.substr(-1) == "%")){
			this.setPercentageHeight(height);
		}else{
			this.setNativeHeight(height);
		}
	},

	setPercentageHeight: function(/*int*/height){
		// summary: stub function. SUBCLASSES MUST IMPLEMENT
		return false;
	},

	setNativeHeight: function(/*int*/height){
		// summary: stub function. SUBCLASSES MUST IMPLEMENT
		return false;
	},

	setPercentageWidth: function(/*int*/width){
		// summary: stub function. SUBCLASSES MUST IMPLEMENT
		return false;
	},

	setNativeWidth: function(/*int*/width){
		// summary: stub function. SUBCLASSES MUST IMPLEMENT
		return false;
	},

	getPreviousSibling: function(){
		// summary:
		//		returns null if this is the first child of the parent,
		//		otherwise returns the next sibling to the "left".
		var idx = this.getParentIndex();
 
		 // first node is idx=0 not found is idx<0
		if (idx<=0) return null;
 
		return this.parent.children[idx-1]; // Widget
	},
 
	getSiblings: function(){
		// summary: gets an array of all children of our parent, including "this"
		return this.parent.children; // Array
	},
 
	getParentIndex: function(){
		// summary: what index are we at in the parent's children array?
		return dojo.lang.indexOf(this.parent.children, this, true); // int
	},
 
	getNextSibling: function(){
		// summary:
		//		returns null if this is the last child of the parent,
		//		otherwise returns the next sibling to the "right".
 
		var idx = this.getParentIndex();
 
		if (idx == this.parent.children.length-1){return null;} // last node
		if (idx < 0){return null;} // not found
 
		return this.parent.children[idx+1]; // Widget
	}
});

// Lower case name cache: listing of the lower case elements in each widget.
// We can't store the lcArgs in the widget itself because if B subclasses A,
// then B.prototype.lcArgs might return A.prototype.lcArgs, which is not what we
// want
dojo.widget.lcArgsCache = {};

// TODO: should have a more general way to add tags or tag libraries?
// TODO: need a default tags class to inherit from for things like getting propertySets
// TODO: parse properties/propertySets into component attributes
// TODO: parse subcomponents
// TODO: copy/clone raw markup fragments/nodes as appropriate
dojo.widget.tags = {};
dojo.widget.tags.addParseTreeHandler = function(/*String*/type){
	// summary: deprecated!
	dojo.deprecated("addParseTreeHandler", ". ParseTreeHandlers are now reserved for components. Any unfiltered DojoML tag without a ParseTreeHandler is assumed to be a widget", "0.5");
	/*
	var ltype = type.toLowerCase();
	this[ltype] = function(fragment, widgetParser, parentComp, insertionIndex, localProps){
		var _ltype = ltype;
		dojo.profile.start(_ltype);
		var n = dojo.widget.buildWidgetFromParseTree(ltype, fragment, widgetParser, parentComp, insertionIndex, localProps);
		dojo.profile.end(_ltype);
		return n;
	}
	*/
}

//dojo.widget.tags.addParseTreeHandler("dojo:widget");

dojo.widget.tags["dojo:propertyset"] = function(fragment, widgetParser, parentComp){
	// FIXME: Is this needed?
	// FIXME: Not sure that this parses into the structure that I want it to parse into...
	// FIXME: add support for nested propertySets
	var properties = widgetParser.parseProperties(fragment["dojo:propertyset"]);
}

// FIXME: need to add the <dojo:connect />
dojo.widget.tags["dojo:connect"] = function(fragment, widgetParser, parentComp){
	var properties = widgetParser.parseProperties(fragment["dojo:connect"]);
}

// FIXME: if we know the insertion point (to a reasonable location), why then do we:
//	- create a template node
//	- clone the template node
//	- render the clone and set properties
//	- remove the clone from the render tree
//	- place the clone
// this is quite dumb
dojo.widget.buildWidgetFromParseTree = function(/*String*/				type,
												/*Object*/				frag, 
												/*dojo.widget.Parse*/	parser,
												/*Widget, optional*/	parentComp, 
												/*int, optional*/		insertionIndex,
												/*Object*/				localProps){

	// summary: creates a tree of widgets from the data structure produced by the first-pass parser (frag)
	
	// test for accessibility mode 
	dojo.a11y.setAccessibleMode();
	//dojo.profile.start("buildWidgetFromParseTree");
	// FIXME: for codepath from createComponentFromScript, we are now splitting a path 
	// that we already split and then joined
	var stype = type.split(":");
	stype = (stype.length == 2) ? stype[1] : type;
	
	// FIXME: we don't seem to be doing anything with this!
	// var propertySets = parser.getPropertySets(frag);
	var localProperties = localProps || parser.parseProperties(frag[frag["ns"]+":"+stype]);
	var twidget = dojo.widget.manager.getImplementation(stype,null,null,frag["ns"]);
	if(!twidget){
		throw new Error('cannot find "' + type + '" widget');
	}else if (!twidget.create){
		throw new Error('"' + type + '" widget object has no "create" method and does not appear to implement *Widget');
	}
	localProperties["dojoinsertionindex"] = insertionIndex;
	// FIXME: we lose no less than 5ms in construction!
	var ret = twidget.create(localProperties, frag, parentComp, frag["ns"]);
	// dojo.profile.end("buildWidgetFromParseTree");
	return ret;
}

dojo.widget.defineWidget = function(/*String*/			widgetClass, 
									/*String*/			renderer,
									/*function||array*/	superclasses, 
									/*function*/		init,
									/*Object*/			props){

	// summary: Create a widget constructor function (aka widgetClass)
	// widgetClass: the location in the object hierarchy to place the new widget class constructor
	// renderer: usually "html", determines when this delcaration will be used
	// superclasses:
	//		can be either a single function or an array of functions to be
	//		mixed in as superclasses. If an array, only the first will be used
	//		to set prototype inheritance.
	// init: an optional constructor function. Will be called after superclasses are mixed in.
	// props: a map of properties and functions to extend the class prototype with

	// This meta-function does parameter juggling for backward compat and overloading
	// if 4th argument is a string, we are using the old syntax
	// old sig: widgetClass, superclasses, props (object), renderer (string), init (function)
	if(dojo.lang.isString(arguments[3])){
		dojo.widget._defineWidget(arguments[0], arguments[3], arguments[1], arguments[4], arguments[2]);
	}else{
		// widgetClass
		var args = [ arguments[0] ], p = 3;
		if(dojo.lang.isString(arguments[1])){
			// renderer, superclass
			args.push(arguments[1], arguments[2]);
		}else{
			// superclass
			args.push('', arguments[1]);
			p = 2;
		}
		if(dojo.lang.isFunction(arguments[p])){
			// init (function), props (object) 
			args.push(arguments[p], arguments[p+1]);
		}else{
			// props (object) 
			args.push(null, arguments[p]);
		}
		dojo.widget._defineWidget.apply(this, args);
	}
}

dojo.widget.defineWidget.renderers = "html|svg|vml";

dojo.widget._defineWidget = function(widgetClass /*string*/, renderer /*string*/, superclasses /*function||array*/, init /*function*/, props /*object*/){
	// FIXME: uncomment next line to test parameter juggling ... remove when confidence improves
	// dojo.debug('(c:)' + widgetClass + '\n\n(r:)' + renderer + '\n\n(i:)' + init + '\n\n(p:)' + props);
	// widgetClass takes the form foo.bar.baz<.renderer>.WidgetName (e.g. foo.bar.baz.WidgetName or foo.bar.baz.html.WidgetName)
	var module = widgetClass.split(".");
	var type = module.pop(); // type <= WidgetName, module <= foo.bar.baz<.renderer>
	var regx = "\\.(" + (renderer ? renderer + '|' : '') + dojo.widget.defineWidget.renderers + ")\\.";
	var r = widgetClass.search(new RegExp(regx));
	module = (r < 0 ? module.join(".") : widgetClass.substr(0, r));

	// deprecated in favor of namespace system, remove for 0.5
	dojo.widget.manager.registerWidgetPackage(module);
	
	var pos = module.indexOf(".");
	var nsName = (pos > -1) ? module.substring(0,pos) : module;

	// FIXME: hrm, this might make things simpler
	//dojo.widget.tags.addParseTreeHandler(nsName+":"+type.toLowerCase());
	
	props=(props)||{};
	props.widgetType = type;
	if((!init)&&(props["classConstructor"])){
		init = props.classConstructor;
		delete props.classConstructor;
	}
	dojo.declare(widgetClass, superclasses, init, props);
}
