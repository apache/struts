/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.DomWidget");

dojo.require("dojo.event.*");
dojo.require("dojo.io.*");
dojo.require("dojo.widget.Widget");
dojo.require("dojo.dom");
dojo.require("dojo.html.style");
dojo.require("dojo.xml.Parse");
dojo.require("dojo.uri.*");
dojo.require("dojo.lang.func");
dojo.require("dojo.lang.extras");

dojo.widget._cssFiles = {};
dojo.widget._cssStrings = {};
dojo.widget._templateCache = {};

// Object: a mapping of strings that are used in template variable replacement
dojo.widget.defaultStrings = {
	dojoRoot: dojo.hostenv.getBaseScriptUri(),
	baseScriptUri: dojo.hostenv.getBaseScriptUri()
};

dojo.widget.fillFromTemplateCache = function(	/*DomWidget*/				obj, 
												/*String||dojo.uri.Uri*/	templatePath,
												/*String, optional*/		templateString,
												/*Boolean, optional*/		avoidCache){
	// summary:
	//		static method to build from a template w/ or w/o a real widget in
	//		place
	// obj: an instance of dojo.widget.DomWidget to initialize the template for
	// templatePath: the URL to get the template from	
	// templateString:
	//		a string to use in lieu of fetching the template from a URL
	// avoidCache:
	//		should the template system not use whatever is in the cache and
	//		always use the passed templatePath or templateString?

	// dojo.debug("avoidCache:", avoidCache);
	var tpath = templatePath || obj.templatePath;

	var tmplts = dojo.widget._templateCache;
	if(!obj["widgetType"]) { // don't have a real template here
		do {
			var dummyName = "__dummyTemplate__" + dojo.widget._templateCache.dummyCount++;
		} while(tmplts[dummyName]);
		obj.widgetType = dummyName;
	}
	var wt = obj.widgetType;

	var ts = tmplts[wt];
	if(!ts){
		tmplts[wt] = { "string": null, "node": null };
		if(avoidCache){
			ts = {};
		}else{
			ts = tmplts[wt];
		}
	}
	if((!obj.templateString)&&(!avoidCache)){
		obj.templateString = templateString || ts["string"];
	}
	if((!obj.templateNode)&&(!avoidCache)){
		obj.templateNode = ts["node"];
	}
	if((!obj.templateNode)&&(!obj.templateString)&&(tpath)){
		// fetch a text fragment and assign it to templateString
		// NOTE: we rely on blocking IO here!
		var tstring = dojo.hostenv.getText(tpath);
		if(tstring){
			// strip <?xml ...?> declarations so that external SVG and XML
			// documents can be added to a document without worry
			tstring = tstring.replace(/^\s*<\?xml(\s)+version=[\'\"](\d)*.(\d)*[\'\"](\s)*\?>/im, "");
			var matches = tstring.match(/<body[^>]*>\s*([\s\S]+)\s*<\/body>/im);
			if(matches){
				tstring = matches[1];
			}
		}else{
			tstring = "";
		}
		obj.templateString = tstring;
		if(!avoidCache){
			tmplts[wt]["string"] = tstring;
		}
	}
	if((!ts["string"])&&(!avoidCache)){
		ts.string = obj.templateString;
	}
}
dojo.widget._templateCache.dummyCount = 0;

// Array: list of properties to search for node-to-property mappings
dojo.widget.attachProperties = ["dojoAttachPoint", "id"];
// String: name of the property to use for mapping DOM events to widget functions
dojo.widget.eventAttachProperty = "dojoAttachEvent";
// String: property name of code to evaluate when the widget is constructed
dojo.widget.onBuildProperty = "dojoOnBuild";
// Array:  possible accessibility values to set on widget elements - role or state
dojo.widget.waiNames  = ["waiRole", "waiState"];
// Object: Contains functions to set accessibility roles and states 
// 	onto widget elements
dojo.widget.wai = {
	waiRole: { 	
				// String: information for mapping accessibility role
				name: "waiRole", 
				// String: URI of the namespace for the set of roles
				"namespace": "http://www.w3.org/TR/xhtml2", 
				// String: alias to assign the namespace
				alias: "x2",
				// String: prefix to assign to the role value
				prefix: "wairole:"
	},
	waiState: { 
				// String: informatin for mapping accessibility state
				name: "waiState", 
				// String: URI of the namespace for the set of states
				"namespace": "http://www.w3.org/2005/07/aaa", 
				// String: alias to assign the namespace
				alias: "aaa",
				// String: empty string - state value does not require prefix
				prefix: ""
	},
	setAttr: function(/*DomNode*/node, /*String*/ ns, /*String*/ attr, /*String|Boolean*/value){
		// Summary: Use appropriate API to set the role or state attribute onto the element.
		// Description: In IE use the generic setAttribute() api.  Append a namespace
		//   alias to the attribute name and appropriate prefix to the value. 
		//   Otherwise, use the setAttribueNS api to set the namespaced attribute. Also
		//   add the appropriate prefix to the attribute value.
		if(dojo.render.html.ie){
			node.setAttribute(this[ns].alias+":"+ attr, this[ns].prefix+value);
		}else{
			node.setAttributeNS(this[ns]["namespace"], attr, this[ns].prefix+value);
		}
	},

	getAttr: function(/*DomNode*/ node, /*String*/ ns, /*String|Boolena*/ attr){
		// Summary:  Use the appropriate API to retrieve the role or state value
		// Description: In IE use the generic getAttribute() api.  An alias value 
		// 	was added to the attribute name to simulate a namespace when the attribute
		//  was set.  Otherwise use the getAttributeNS() api to retrieve the state value
		if(dojo.render.html.ie){
			return node.getAttribute(this[ns].alias+":"+attr);
		}else{
			return node.getAttributeNS(this[ns]["namespace"], attr);
		}
	},
	removeAttr: function(/*DomNode*/ node, /*String*/ ns, /*String|Boolena*/ attr){
		// Summary:  Use the appropriate API to remove the role or state value
		// Description: In IE use the generic removeAttribute() api.  An alias value 
		// 	was added to the attribute name to simulate a namespace when the attribute
		//  was set.  Otherwise use the removeAttributeNS() api to remove the state value
		var success = true; //only IE returns a value
		if(dojo.render.html.ie){
			 success = node.removeAttribute(this[ns].alias+":"+attr);
		}else{
			node.removeAttributeNS(this[ns]["namespace"], attr);
		}
		return success;
	}
};

dojo.widget.attachTemplateNodes = function(	/*DomNode*/		rootNode, 
											/*Widget*/		targetObj, 
											/*Array*/		events ){
	// summary:
	//		map widget properties and functions to the handlers specified in
	//		the dom node and it's descendants. This function iterates over all
	//		nodes and looks for these properties:
	//			* dojoAttachPoint
	//			* dojoAttachEvent	
	//			* waiRole
	//			* waiState
	//			* any "dojoOn*" proprties passed in the events array
	// rootNode:
	//		the node to search for properties. All children will be searched.
	// events: a list of properties generated from getDojoEventsFromStr.

	// FIXME: this method is still taking WAAAY too long. We need ways of optimizing:
	//	a.) what we are looking for on each node
	//	b.) the nodes that are subject to interrogation (use xpath instead?)
	//	c.) how expensive event assignment is (less eval(), more connect())
	// var start = new Date();
	var elementNodeType = dojo.dom.ELEMENT_NODE;

	function trim(str){
		return str.replace(/^\s+|\s+$/g, "");
	}

	if(!rootNode){ 
		rootNode = targetObj.domNode;
	}

	if(rootNode.nodeType != elementNodeType){
		return;
	}
	// alert(events.length);

	var nodes = rootNode.all || rootNode.getElementsByTagName("*");
	var _this = targetObj;
	for(var x=-1; x<nodes.length; x++){
		var baseNode = (x == -1) ? rootNode : nodes[x];
		// FIXME: is this going to have capitalization problems?  Could use getAttribute(name, 0); to get attributes case-insensitve
		var attachPoint = [];
		if(!targetObj.widgetsInTemplate || !baseNode.getAttribute('dojoType')){
			for(var y=0; y<this.attachProperties.length; y++){
				var tmpAttachPoint = baseNode.getAttribute(this.attachProperties[y]);
				if(tmpAttachPoint){
					attachPoint = tmpAttachPoint.split(";");
					for(var z=0; z<attachPoint.length; z++){
						if(dojo.lang.isArray(targetObj[attachPoint[z]])){
							targetObj[attachPoint[z]].push(baseNode);
						}else{
							targetObj[attachPoint[z]]=baseNode;
						}
					}
					break;
				}
			}

			var attachEvent = baseNode.getAttribute(this.eventAttachProperty);
			if(attachEvent){
				// NOTE: we want to support attributes that have the form
				// "domEvent: nativeEvent; ..."
				var evts = attachEvent.split(";");
				for(var y=0; y<evts.length; y++){
					if((!evts[y])||(!evts[y].length)){ continue; }
					var thisFunc = null;
					var tevt = trim(evts[y]);
					if(evts[y].indexOf(":") >= 0){
						// oh, if only JS had tuple assignment
						var funcNameArr = tevt.split(":");
						tevt = trim(funcNameArr[0]);
						thisFunc = trim(funcNameArr[1]);
					}
					if(!thisFunc){
						thisFunc = tevt;
					}
	
					var tf = function(){ 
						var ntf = new String(thisFunc);
						return function(evt){
							if(_this[ntf]){
								_this[ntf](dojo.event.browser.fixEvent(evt, this));
							}
						};
					}();
					dojo.event.browser.addListener(baseNode, tevt, tf, false, true);
					// dojo.event.browser.addListener(baseNode, tevt, dojo.lang.hitch(_this, thisFunc));
				}
			}
	
			for(var y=0; y<events.length; y++){
				//alert(events[x]);
				var evtVal = baseNode.getAttribute(events[y]);
				if((evtVal)&&(evtVal.length)){
					var thisFunc = null;
					var domEvt = events[y].substr(4); // clober the "dojo" prefix
					thisFunc = trim(evtVal);
					var funcs = [thisFunc];
					if(thisFunc.indexOf(";")>=0){
						funcs = dojo.lang.map(thisFunc.split(";"), trim);
					}
					for(var z=0; z<funcs.length; z++){
						if(!funcs[z].length){ continue; }
						var tf = function(){ 
							var ntf = new String(funcs[z]);
							return function(evt){
								if(_this[ntf]){
									_this[ntf](dojo.event.browser.fixEvent(evt, this));
								}
							}
						}();
						dojo.event.browser.addListener(baseNode, domEvt, tf, false, true);
						// dojo.event.browser.addListener(baseNode, domEvt, dojo.lang.hitch(_this, funcs[z]));
					}
				}
			}
		}
		// continue;

		// FIXME: we need to put this into some kind of lookup structure
		// instead of direct assignment
		var tmpltPoint = baseNode.getAttribute(this.templateProperty);
		if(tmpltPoint){
			targetObj[tmpltPoint]=baseNode;
		}

		dojo.lang.forEach(dojo.widget.waiNames, function(name){
			var wai = dojo.widget.wai[name];
			var val = baseNode.getAttribute(wai.name);
			if(val){
				if(val.indexOf('-') == -1){ 
					dojo.widget.wai.setAttr(baseNode, wai.name, "role", val);
				}else{
					// this is a state-value pair
					var statePair = val.split('-');
					dojo.widget.wai.setAttr(baseNode, wai.name, statePair[0], statePair[1]);
				}
			}
		}, this);

		var onBuild = baseNode.getAttribute(this.onBuildProperty);
		if(onBuild){
			eval("var node = baseNode; var widget = targetObj; "+onBuild);
		}
	}

}

dojo.widget.getDojoEventsFromStr = function(/*String*/str){
	// summary:
	//		generates a list of properties with names that match the form
	//		dojoOn*
	// str: the template string to search
	
	// var lstr = str.toLowerCase();
	var re = /(dojoOn([a-z]+)(\s?))=/gi;
	var evts = str ? str.match(re)||[] : [];
	var ret = [];
	var lem = {};
	for(var x=0; x<evts.length; x++){
		if(evts[x].length < 1){ continue; }
		var cm = evts[x].replace(/\s/, "");
		cm = (cm.slice(0, cm.length-1));
		if(!lem[cm]){
			lem[cm] = true;
			ret.push(cm);
		}
	}
	return ret; // Array
}

/*
dojo.widget.buildAndAttachTemplate = function(obj, templatePath, templateCssPath, templateString, targetObj) {
	this.buildFromTemplate(obj, templatePath, templateCssPath, templateString);
	var node = dojo.dom.createNodesFromText(obj.templateString, true)[0];
	this.attachTemplateNodes(node, targetObj||obj, dojo.widget.getDojoEventsFromStr(templateString));
	return node;
}
*/


// summary:
//		dojo.widget.DomWidget is the superclass that provides behavior for all
//		DOM-based renderers, including HtmlWidget and SvgWidget. DomWidget
//		implements the templating system that most widget authors use to define
//		the UI for their widgets.
dojo.declare("dojo.widget.DomWidget", 
	dojo.widget.Widget,
	function(){
		if((arguments.length>0)&&(typeof arguments[0] == "object")){
			this.create(arguments[0]);
		}
	},
	{							 
		// DomNode: a node that represents the widget template. Pre-empts both templateString and templatePath.
		templateNode: null,

		// String:
		//		a string that represents the widget template. Pre-empts the
		//		templatePath. In builds that have their strings "interned", the
		//		templatePath is converted to an inline templateString, thereby
		//		preventing a synchronous network call.
		templateString: null,

		// String:
		//		a string that represents the CSS for the widgettemplate.
		//		Pre-empts the templateCssPath. In builds that have their
		//		strings "interned", the templateCssPath is converted to an
		//		inline templateCssString, thereby preventing a synchronous
		//		network call.
		templateCssString: null,

		// Boolean:
		//		should the widget not replace the node from which it was
		//		constructed? Widgets that apply behaviors to pre-existing parts
		//		of a page can be implemented easily by setting this to "true".
		//		In these cases, the domNode property will point to the node
		//		which the widget was created from.
		preventClobber: false,

		// DomNode:
		//		this is our visible representation of the widget! Other DOM
		//		Nodes may by assigned to other properties, usually through the
		//		template system's dojoAttachPonit syntax, but the domNode
		//		property is the canonical "top level" node in widget UI.
		domNode: null, 

		// DomNode:
		//		holds child elements. "containerNode" is generally set via a
		//		dojoAttachPoint assignment and it designates where widgets that
		//		are defined as "children" of the parent will be placed
		//		visually.
		containerNode: null,

		// Boolean:
		//		should we parse the template to find widgets that might be
		//		declared in markup inside it? false by default.
		widgetsInTemplate: false,

		addChild: function(	/*Widget*/				widget, 
							/*DomNode, optional*/	overrideContainerNode, 
							/*String, optional*/	pos, 
							/*DomNode, optional*/	ref,
							/*int, optional*/		insertIndex){
			// summary:
			//		Process the given child widget, inserting it's dom node as
			//		a child of our dom node
			// overrideContainerNode: a non-default container node for the widget
			// pos:
			//		can be one of "before", "after", "first", or "last". This
			//		has the same meaning as in dojo.dom.insertAtPosition()
			// ref: a node to place the widget relative to
			// insertIndex: DOM index, same meaning as in dojo.dom.insertAtIndex()

			// FIXME: should we support addition at an index in the children arr and
			// order the display accordingly? Right now we always append.
			if(!this.isContainer){ // we aren't allowed to contain other widgets, it seems
				dojo.debug("dojo.widget.DomWidget.addChild() attempted on non-container widget");
				return null;
			}else{
				if(insertIndex == undefined){
					insertIndex = this.children.length;
				}
				this.addWidgetAsDirectChild(widget, overrideContainerNode, pos, ref, insertIndex);
				this.registerChild(widget, insertIndex);
			}
			return widget; // Widget: the widget that was inserted
		},
		
		addWidgetAsDirectChild: function(	/*Widget*/				widget, 
											/*DomNode*/				overrideContainerNode, 
											/*String, optional*/	pos, 
											/*DomNode, optional*/	ref, 
											/*int, optional*/		insertIndex){
			// summary:
			//		Process the given child widget, inserting it's dom node as
			//		a child of our dom node
			// overrideContainerNode: a non-default container node for the widget
			// pos:
			//		can be one of "before", "after", "first", or "last". This
			//		has the same meaning as in dojo.dom.insertAtPosition()
			// ref: a node to place the widget relative to
			// insertIndex: DOM index, same meaning as in dojo.dom.insertAtIndex()
			if((!this.containerNode)&&(!overrideContainerNode)){
				this.containerNode = this.domNode;
			}
			var cn = (overrideContainerNode) ? overrideContainerNode : this.containerNode;
			if(!pos){ pos = "after"; }
			if(!ref){ 
				if(!cn){ cn = dojo.body(); }
				ref = cn.lastChild; 
			}
			if(!insertIndex) { insertIndex = 0; }
			widget.domNode.setAttribute("dojoinsertionindex", insertIndex);

			// insert the child widget domNode directly underneath my domNode, in the
			// specified position (by default, append to end)
			if(!ref){
				cn.appendChild(widget.domNode);
			}else{
				// FIXME: was this meant to be the (ugly hack) way to support insert @ index?
				//dojo.dom[pos](widget.domNode, ref, insertIndex);

				// CAL: this appears to be the intended way to insert a node at a given position...
				if (pos == 'insertAtIndex'){
					// dojo.debug("idx:", insertIndex, "isLast:", ref === cn.lastChild);
					dojo.dom.insertAtIndex(widget.domNode, ref.parentNode, insertIndex);
				}else{
					// dojo.debug("pos:", pos, "isLast:", ref === cn.lastChild);
					if((pos == "after")&&(ref === cn.lastChild)){
						cn.appendChild(widget.domNode);
					}else{
						dojo.dom.insertAtPosition(widget.domNode, cn, pos);
					}
				}
			}
		},

		registerChild: function(/*Widget*/widget, /*int*/insertionIndex){
			// summary: record that given widget descends from me
			// widget: the widget that is now a child
			// inesrtionIndex: where in the children[] array to place it

			// we need to insert the child at the right point in the parent's 
			// 'children' array, based on the insertionIndex

			widget.dojoInsertionIndex = insertionIndex;

			var idx = -1;
			for(var i=0; i<this.children.length; i++){

				//This appears to fix an out of order issue in the case of mixed
				//markup and programmatically added children.  Previously, if a child
				//existed from markup, and another child was addChild()d without specifying
				//any additional parameters, it would end up first in the list, when in fact
				//it should be after.  I can't see cases where this would break things, but
				//I could see no other obvious solution. -dustin

				if (this.children[i].dojoInsertionIndex <= insertionIndex){
					idx = i;
				}
			}

			this.children.splice(idx+1, 0, widget);

			widget.parent = this;
			widget.addedTo(this, idx+1);
			
			// If this widget was created programatically, then it was erroneously added
			// to dojo.widget.manager.topWidgets.  Fix that here.
			delete dojo.widget.manager.topWidgets[widget.widgetId];
		},

		removeChild: function(/*Widget*/widget){
			// summary: detach child domNode from parent domNode
			dojo.dom.removeNode(widget.domNode);

			// remove child widget from parent widget 
			return dojo.widget.DomWidget.superclass.removeChild.call(this, widget); // Widget
		},

		getFragNodeRef: function(/*Object*/frag){
			// summary:
			//		returns the source node, if any, that the widget was
			//		declared from
			// frag:
			//		an opaque data structure generated by the first-pass parser
			if(!frag){return null;} // null
			if(!frag[this.getNamespacedType()]){
				dojo.raise("Error: no frag for widget type " + this.getNamespacedType() 
					+ ", id " + this.widgetId
					+ " (maybe a widget has set it's type incorrectly)");
			}
			return frag[this.getNamespacedType()]["nodeRef"]; // DomNode
		},
		
		postInitialize: function(/*Object*/args, /*Object*/frag, /*Widget*/parentComp){
			// summary:
			//		Replace the source domNode with the generated dom
			//		structure, and register the widget with its parent.
			//		This is an implementation of the stub function defined in
			//		dojo.widget.Widget.
			
			//dojo.profile.start(this.widgetType + " postInitialize");
			
			var sourceNodeRef = this.getFragNodeRef(frag);
			// Stick my generated dom into the output tree
			//alert(this.widgetId + ": replacing " + sourceNodeRef + " with " + this.domNode.innerHTML);
			if (parentComp && (parentComp.snarfChildDomOutput || !sourceNodeRef)){
				// Add my generated dom as a direct child of my parent widget
				// This is important for generated widgets, and also cases where I am generating an
				// <li> node that can't be inserted back into the original DOM tree
				parentComp.addWidgetAsDirectChild(this, "", "insertAtIndex", "",  args["dojoinsertionindex"], sourceNodeRef);
			} else if (sourceNodeRef){
				// Do in-place replacement of the my source node with my generated dom
				if(this.domNode && (this.domNode !== sourceNodeRef)){
					var oldNode = sourceNodeRef.parentNode.replaceChild(this.domNode, sourceNodeRef);
				}
			}

			// Register myself with my parent, or with the widget manager if
			// I have no parent
			// TODO: the code below erroneously adds all programatically generated widgets
			// to topWidgets (since we don't know who the parent is until after creation finishes)
			if ( parentComp ) {
				parentComp.registerChild(this, args.dojoinsertionindex);
			} else {
				dojo.widget.manager.topWidgets[this.widgetId]=this;
			}

			if(this.widgetsInTemplate){
				var parser = new dojo.xml.Parse();

				var subContainerNode;
				//TODO: use xpath here?
				var subnodes = this.domNode.getElementsByTagName("*");
				for(var i=0;i<subnodes.length;i++){
					if(subnodes[i].getAttribute('dojoAttachPoint') == 'subContainerWidget'){
						subContainerNode = subnodes[i];
//						break;
					}
					if(subnodes[i].getAttribute('dojoType')){
						subnodes[i].setAttribute('_isSubWidget', true);
					}
				}
				if (this.isContainer && !this.containerNode){
					//no containerNode is available, which means a widget is used as a container. find it here and move
					//all dom nodes defined in the main html page as children of this.domNode into the actual container
					//widget's node (at this point, the subwidgets defined in the template file is not parsed yet)
					if(subContainerNode){
						var src = this.getFragNodeRef(frag);
						if (src){
							dojo.dom.moveChildren(src, subContainerNode);
							//do not need to follow children nodes in the main html page, as they
							//will be dealt with in the subContainerWidget
							frag['dojoDontFollow'] = true;
						}
					}else{
						dojo.debug("No subContainerWidget node can be found in template file for widget "+this);
					}
				}

				var templatefrag = parser.parseElement(this.domNode, null, true);
				// createSubComponents not createComponents because frag has already been created
				dojo.widget.getParser().createSubComponents(templatefrag, this);
	
				//find all the sub widgets defined in the template file of this widget
				var subwidgets = [];
				var stack = [this];
				var w;
				while((w = stack.pop())){
					for(var i = 0; i < w.children.length; i++){
						var cwidget = w.children[i];
						if(cwidget._processedSubWidgets || !cwidget.extraArgs['_issubwidget']){ continue; }
						subwidgets.push(cwidget);
						if(cwidget.isContainer){
							stack.push(cwidget);
						}
					}
				}
	
				//connect event to this widget/attach dom node
				for(var i = 0; i < subwidgets.length; i++){
					var widget = subwidgets[i];
					if(widget._processedSubWidgets){
						dojo.debug("This should not happen: widget._processedSubWidgets is already true!");
						return;
					}
					widget._processedSubWidgets = true;
					if(widget.extraArgs['dojoattachevent']){
						var evts = widget.extraArgs['dojoattachevent'].split(";");
						for(var j=0; j<evts.length; j++){
							var thisFunc = null;
							var tevt = dojo.string.trim(evts[j]);
							if(tevt.indexOf(":") >= 0){
								// oh, if only JS had tuple assignment
								var funcNameArr = tevt.split(":");
								tevt = dojo.string.trim(funcNameArr[0]);
								thisFunc = dojo.string.trim(funcNameArr[1]);
							}
							if(!thisFunc){
								thisFunc = tevt;
							}
							if(dojo.lang.isFunction(widget[tevt])){
								dojo.event.kwConnect({
									srcObj: widget, 
									srcFunc: tevt, 
									targetObj: this, 
									targetFunc: thisFunc
								});
							}else{
								alert(tevt+" is not a function in widget "+widget);
							}
						}
					}
	
					if(widget.extraArgs['dojoattachpoint']){
						//don't attach widget.domNode here, as we do not know which
						//dom node we should connect to (in checkbox widget case, 
						//it is inputNode). So we make the widget itself available
						this[widget.extraArgs['dojoattachpoint']] = widget;
					}
				}
			}

			//dojo.profile.end(this.widgetType + " postInitialize");

			// Expand my children widgets
			/* dojoDontFollow is important for a very special case
			 * basically if you have a widget that you instantiate from script
			 * and that widget is a container, and it contains a reference to a parent
			 * instance, the parser will start recursively parsing until the browser
			 * complains.  So the solution is to set an initialization property of 
			 * dojoDontFollow: true and then it won't recurse where it shouldn't
			 */
			if(this.isContainer && !frag["dojoDontFollow"]){
				//alert("recurse from " + this.widgetId);
				// build any sub-components with us as the parent
				dojo.widget.getParser().createSubComponents(frag, this);
			}
		},

		// method over-ride
		buildRendering: function(/*Object*/args, /*Object*/frag){
			// summary:
			//		Construct the UI for this widget, generally from a
			//		template. This can be over-ridden for custom UI creation to
			//		to side-step the template system.  This is an
			//		implementation of the stub function defined in
			//		dojo.widget.Widget.

			// DOM widgets construct themselves from a template
			var ts = dojo.widget._templateCache[this.widgetType];
			
			// Handle style for this widget here, as even if templatePath
			// is not set, style specified by templateCssString or templateCssPath
			// should be applied. templateCssString has higher priority
			// than templateCssPath
			if(args["templatecsspath"]){
				args["templateCssPath"] = args["templatecsspath"];
			}
			var cpath = args["templateCssPath"] || this.templateCssPath;
			if(cpath && !dojo.widget._cssFiles[cpath.toString()]){
				if((!this.templateCssString)&&(cpath)){
					this.templateCssString = dojo.hostenv.getText(cpath);
					this.templateCssPath = null;
				}
				dojo.widget._cssFiles[cpath.toString()] = true;
			}
		
			if((this["templateCssString"])&&(!this.templateCssString["loaded"])){
				dojo.html.insertCssText(this.templateCssString, null, cpath);
				if(!this.templateCssString){ this.templateCssString = ""; }
				this.templateCssString.loaded = true;
			}
			if(	
				(!this.preventClobber)&&(
					(this.templatePath)||
					(this.templateNode)||
					(
						(this["templateString"])&&(this.templateString.length) 
					)||
					(
						(typeof ts != "undefined")&&( (ts["string"])||(ts["node"]) )
					)
				)
			){
				// if it looks like we can build the thing from a template, do it!
				this.buildFromTemplate(args, frag);
			}else{
				// otherwise, assign the DOM node that was the source of the widget
				// parsing to be the root node
				this.domNode = this.getFragNodeRef(frag);
			}
			this.fillInTemplate(args, frag); 	// this is where individual widgets
												// will handle population of data
												// from properties, remote data
												// sets, etc.
	},

		buildFromTemplate: function(/*Object*/args, /*Object*/frag){
			// summary:
			//		Called by buildRendering, creates the actual UI in a DomWidget.

			// var start = new Date();
			// copy template properties if they're already set in the templates object
			// dojo.debug("buildFromTemplate:", this);
			var avoidCache = false;
			if(args["templatepath"]){
				avoidCache = true;
				args["templatePath"] = args["templatepath"];
			}
			dojo.widget.fillFromTemplateCache(	this, 
												args["templatePath"], 
												null,
												avoidCache);
			var ts = dojo.widget._templateCache[this.widgetType];
			if((ts)&&(!avoidCache)){
				if(!this.templateString.length){
					this.templateString = ts["string"];
				}
				if(!this.templateNode){
					this.templateNode = ts["node"];
				}
			}
			var matches = false;
			var node = null;
			// var tstr = new String(this.templateString); 
			var tstr = this.templateString; 
			// attempt to clone a template node, if there is one
			if((!this.templateNode)&&(this.templateString)){
				matches = this.templateString.match(/\$\{([^\}]+)\}/g);
				if(matches) {
					// if we do property replacement, don't create a templateNode
					// to clone from.
					var hash = this.strings || {};
					// FIXME: should this hash of default replacements be cached in
					// templateString?
					for(var key in dojo.widget.defaultStrings) {
						if(dojo.lang.isUndefined(hash[key])) {
							hash[key] = dojo.widget.defaultStrings[key];
						}
					}
					// FIXME: this is a lot of string munging. Can we make it faster?
					for(var i = 0; i < matches.length; i++) {
						var key = matches[i];
						key = key.substring(2, key.length-1);
						var kval = (key.substring(0, 5) == "this.") ? dojo.lang.getObjPathValue(key.substring(5), this) : hash[key];
						var value;
						if((kval)||(dojo.lang.isString(kval))){
							value = new String((dojo.lang.isFunction(kval)) ? kval.call(this, key, this.templateString) : kval);
							// Safer substitution, see heading "Attribute values" in  
							// http://www.w3.org/TR/REC-html40/appendix/notes.html#h-B.3.2
							while (value.indexOf("\"") > -1) {
								value=value.replace("\"","&quot;");
							}
							tstr = tstr.replace(matches[i], value);
						}
					}
				}else{
					// otherwise, we are required to instantiate a copy of the template
					// string if one is provided.
					
					// FIXME: need to be able to distinguish here what should be done
					// or provide a generic interface across all DOM implementations
					// FIMXE: this breaks if the template has whitespace as its first 
					// characters
					// node = this.createNodesFromText(this.templateString, true);
					// this.templateNode = node[0].cloneNode(true); // we're optimistic here
					this.templateNode = this.createNodesFromText(this.templateString, true)[0];
					if(!avoidCache){
						ts.node = this.templateNode;
					}
				}
			}
			if((!this.templateNode)&&(!matches)){ 
				dojo.debug("DomWidget.buildFromTemplate: could not create template");
				return false;
			}else if(!matches){
				node = this.templateNode.cloneNode(true);
				if(!node){ return false; }
			}else{
				node = this.createNodesFromText(tstr, true)[0];
			}

			// recurse through the node, looking for, and attaching to, our
			// attachment points which should be defined on the template node.

			this.domNode = node;
			// dojo.profile.start("attachTemplateNodes");
			this.attachTemplateNodes();
			// dojo.profile.end("attachTemplateNodes");
		
			// relocate source contents to templated container node
			// this.containerNode must be able to receive children, or exceptions will be thrown
			if (this.isContainer && this.containerNode){
				var src = this.getFragNodeRef(frag);
				if (src){
					dojo.dom.moveChildren(src, this.containerNode);
				}
			}
		},

		attachTemplateNodes: function(/*DomNode*/baseNode, /*Widget*/targetObj){
			// summary: 
			//		hooks up event handlers and property/node linkages. Calls
			//		dojo.widget.attachTemplateNodes to do all the hard work.
			// baseNode: defaults to "this.domNode"
			// targetObj: defaults to "this"
			if(!baseNode){ baseNode = this.domNode; }
			if(!targetObj){ targetObj = this; }
			return dojo.widget.attachTemplateNodes(baseNode, targetObj, 
						dojo.widget.getDojoEventsFromStr(this.templateString));
		},

		fillInTemplate: function(){
			// summary:
			//		stub function! sub-classes may use as a default UI
			//		initializer function. The UI rendering will be available by
			//		the time this is called from buildRendering. If
			//		buildRendering is over-ridden, this function may not be
			//		fired!
			// dojo.unimplemented("dojo.widget.DomWidget.fillInTemplate");
		},
		
		// method over-ride
		destroyRendering: function(){
			// summary: UI destructor
			try{
				delete this.domNode;
			}catch(e){ /* squelch! */ }
		},

		// FIXME: method over-ride
		cleanUp: function(){},
		
		getContainerHeight: function(){
			// summary: unimplemented!
			dojo.unimplemented("dojo.widget.DomWidget.getContainerHeight");
		},

		getContainerWidth: function(){
			// summary: unimplemented!
			dojo.unimplemented("dojo.widget.DomWidget.getContainerWidth");
		},

		createNodesFromText: function(){
			// summary: unimplemented!
			dojo.unimplemented("dojo.widget.DomWidget.createNodesFromText");
		}
	}
);
