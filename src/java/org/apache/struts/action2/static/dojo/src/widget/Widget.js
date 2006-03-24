/*
	Copyright (c) 2004-2005, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.Widget");
dojo.provide("dojo.widget.tags");

dojo.require("dojo.lang");
dojo.require("dojo.widget.Manager");
dojo.require("dojo.event.*");
dojo.require("dojo.string");

dojo.widget.Widget = function(){
	// these properties aren't primitives and need to be created on a per-item
	// basis.
	this.children = [];
	// this.selection = new dojo.widget.Selection();
	// FIXME: need to replace this with context menu stuff
	this.extraArgs = {};
}
// FIXME: need to be able to disambiguate what our rendering context is
//        here!

// needs to be a string with the end classname. Every subclass MUST
// over-ride.
dojo.lang.extend(dojo.widget.Widget, {
	// base widget properties
	parent: null,
	// obviously, top-level and modal widgets should set these appropriately
	isTopLevel:  false,
	isModal: false,

	isEnabled: true,
	isHidden: false,
	isContainer: false, // can we contain other widgets?
	widgetId: "",
	widgetType: "Widget", // used for building generic widgets

	toString: function() {
		return '[Widget ' + this.widgetType + ', ' + (this.widgetId || 'NO ID') + ']';
	},

	repr: function(){
		return this.toString();
	},

	enable: function(){
		// should be over-ridden
		this.isEnabled = true;
	},

	disable: function(){
		// should be over-ridden
		this.isEnabled = false;
	},

	hide: function(){
		// should be over-ridden
		this.isHidden = true;
	},

	show: function(){
		// should be over-ridden
		this.isHidden = false;
	},

	create: function(args, fragment, parentComp){
		this.satisfyPropertySets(args, fragment, parentComp);
		this.mixInProperties(args, fragment, parentComp);
		this.postMixInProperties(args, fragment, parentComp);
		dojo.widget.manager.add(this);
		this.buildRendering(args, fragment, parentComp);
		this.initialize(args, fragment, parentComp);
		this.postInitialize(args, fragment, parentComp);
		this.postCreate(args, fragment, parentComp);
		return this;
	},

	destroy: function(finalize){
		// FIXME: this is woefully incomplete
		this.uninitialize();
		this.destroyRendering(finalize);
		dojo.widget.manager.removeById(this.widgetId);
	},

	destroyChildren: function(testFunc){
		testFunc = (!testFunc) ? function(){ return true; } : testFunc;
		for(var x=0; x<this.children.length; x++){
			var tc = this.children[x];
			if((tc)&&(testFunc(tc))){
				tc.destroy();
			}
		}
		// this.children = [];
	},

	destroyChildrenOfType: function(type){
		type = type.toLowerCase();
		this.destroyChildren(function(item){
			if(item.widgetType.toLowerCase() == type){
				return true;
			}else{
				return false;
			}
		});
	},

	getChildrenOfType: function(type, recurse){
		var ret = [];
		type = type.toLowerCase();
		for(var x=0; x<this.children.length; x++){
			if(this.children[x].widgetType.toLowerCase() == type){
				ret.push(this.children[x]);
			}
			if(recurse){
				ret = ret.concat(this.children[x].getChildrenOfType(type, recurse));
			}
		}
		return ret;
	},

	satisfyPropertySets: function(args){
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

	mixInProperties: function(args, frag){
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
		/*
		 * the actual mix-in code attempts to do some type-assignment based on
		 * PRE-EXISTING properties of the "this" object. When a named property
		 * of a propset is located, it is first tested to make sure that the
		 * current object already "has one". Properties which are undefined in
		 * the base widget are NOT settable here. The next step is to try to
		 * determine type of the pre-existing property. If it's a string, the
		 * property value is simply assigned. If a function, the property is
		 * replaced with a "new Function()" declaration. If an Array, the
		 * system attempts to split the string value on ";" chars, and no
		 * further processing is attempted (conversion of array elements to a
		 * integers, for instance). If the property value is an Object
		 * (testObj.constructor === Object), the property is split first on ";"
		 * chars, secondly on ":" chars, and the resulting key/value pairs are
		 * assigned to an object in a map style. The onus is on the property
		 * user to ensure that all property values are converted to the
		 * expected type before usage.
		 */

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
						var tn = dojo.lang.nameAnonFunc(new Function(args[x]), this);
						dojo.event.connect(this, x, this, tn);
					}else if(dojo.lang.isArray(this[x])){ // typeof [] == "object"
						this[x] = args[x].split(";");
					} else if (this[x] instanceof Date) {
						this[x] = new Date(Number(args[x])); // assume timestamp
					}else if(typeof this[x] == "object"){ 
						// FIXME: should we be allowing extension here to handle
						// other object types intelligently?

						// FIXME: unlike all other types, we do not replace the
						// object with a new one here. Should we change that?
						var pairs = args[x].split(";");
						for(var y=0; y<pairs.length; y++){
							var si = pairs[y].indexOf(":");
							if((si != -1)&&(pairs[y].length>si)){
								this[x][dojo.string.trim(pairs[y].substr(0, si))] = pairs[y].substr(si+1);
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
				this.extraArgs[x] = args[x];
			}
		}
		// dojo.profile.end("mixInProperties");
	},
	
	postMixInProperties: function(){
	},

	initialize: function(args, frag){
		// dj_unimplemented("dojo.widget.Widget.initialize");
		return false;
	},

	postInitialize: function(args, frag){
		return false;
	},

	postCreate: function(args, frag){
		return false;
	},

	uninitialize: function(){
		// dj_unimplemented("dojo.widget.Widget.uninitialize");
		return false;
	},

	buildRendering: function(){
		// SUBCLASSES MUST IMPLEMENT
		dj_unimplemented("dojo.widget.Widget.buildRendering, on "+this.toString()+", ");
		return false;
	},

	destroyRendering: function(){
		// SUBCLASSES MUST IMPLEMENT
		dj_unimplemented("dojo.widget.Widget.destroyRendering");
		return false;
	},

	cleanUp: function(){
		// SUBCLASSES MUST IMPLEMENT
		dj_unimplemented("dojo.widget.Widget.cleanUp");
		return false;
	},

	addedTo: function(parent){
		// this is just a signal that can be caught
	},

	addChild: function(child){
		// SUBCLASSES MUST IMPLEMENT
		dj_unimplemented("dojo.widget.Widget.addChild");
		return false;
	},

	addChildAtIndex: function(child, index){
		// SUBCLASSES MUST IMPLEMENT
		dj_unimplemented("dojo.widget.Widget.addChildAtIndex");
		return false;
	},

	removeChild: function(childRef){
		// SUBCLASSES MUST IMPLEMENT
		dj_unimplemented("dojo.widget.Widget.removeChild");
		return false;
	},

	removeChildAtIndex: function(index){
		// SUBCLASSES MUST IMPLEMENT
		dj_unimplemented("dojo.widget.Widget.removeChildAtIndex");
		return false;
	},

	resize: function(width, height){
		// both width and height may be set as percentages. The setWidth and
		// setHeight  functions attempt to determine if the passed param is
		// specified in percentage or native units. Integers without a
		// measurement are assumed to be in the native unit of measure.
		this.setWidth(width);
		this.setHeight(height);
	},

	setWidth: function(width){
		if((typeof width == "string")&&(width.substr(-1) == "%")){
			this.setPercentageWidth(width);
		}else{
			this.setNativeWidth(width);
		}
	},

	setHeight: function(height){
		if((typeof height == "string")&&(height.substr(-1) == "%")){
			this.setPercentageHeight(height);
		}else{
			this.setNativeHeight(height);
		}
	},

	setPercentageHeight: function(height){
		// SUBCLASSES MUST IMPLEMENT
		return false;
	},

	setNativeHeight: function(height){
		// SUBCLASSES MUST IMPLEMENT
		return false;
	},

	setPercentageWidth: function(width){
		// SUBCLASSES MUST IMPLEMENT
		return false;
	},

	setNativeWidth: function(width){
		// SUBCLASSES MUST IMPLEMENT
		return false;
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
dojo.widget.tags.addParseTreeHandler = function(type){
	var ltype = type.toLowerCase();
	this[ltype] = function(fragment, widgetParser, parentComp, insertionIndex){ 
		return dojo.widget.buildWidgetFromParseTree(ltype, fragment, widgetParser, parentComp, insertionIndex);
	}
}
dojo.widget.tags.addParseTreeHandler("dojo:widget");

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

dojo.widget.buildWidgetFromParseTree = function(type, frag, parser, parentComp, insertionIndex){
	var localProperties = {};
	var stype = type.split(":");
	stype = (stype.length == 2) ? stype[1] : type;
	// outputObjectInfo(frag["dojo:"+stype]);
	// FIXME: we don't seem to be doing anything with this!
	// var propertySets = parser.getPropertySets(frag);
	var localProperties = parser.parseProperties(frag["dojo:"+stype]);
	// var tic = new Date();
	var twidget = dojo.widget.manager.getImplementation(stype);
	if(!twidget){
		throw new Error("cannot find \"" + stype + "\" widget");
	}else if (!twidget.create){
		throw new Error("\"" + stype + "\" widget object does not appear to implement *Widget");
	}
	localProperties["dojoinsertionindex"] = insertionIndex;
	// FIXME: we loose no less than 5ms in construction!
	var ret = twidget.create(localProperties, frag, parentComp);
	// dojo.debug(new Date() - tic);
	return ret;
}
