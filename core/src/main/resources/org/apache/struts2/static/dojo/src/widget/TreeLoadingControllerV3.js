/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/


dojo.provide("dojo.widget.TreeLoadingControllerV3");

dojo.require("dojo.widget.TreeBasicControllerV3");
dojo.require("dojo.event.*");
dojo.require("dojo.json")
dojo.require("dojo.io.*");
dojo.require("dojo.Deferred");
dojo.require("dojo.DeferredList");

dojo.declare(
	"dojo.Error",
	Error,
	function(message, extra) {
		this.message = message;
		this.extra = extra;
		this.stack = (new Error()).stack;	
	}
);

dojo.declare(
	"dojo.CommunicationError",
	dojo.Error,
	function() {
		this.name="CommunicationError";
	}
);

dojo.declare(
	"dojo.LockedError",
	dojo.Error,
	function() {
		this.name="LockedError";
	}
);

dojo.declare(
	"dojo.FormatError",
	dojo.Error,
	function() {
		this.name="FormatError";
	}
);

dojo.declare(
	"dojo.RpcError",
	dojo.Error,
	function() {
		this.name="RpcError";
	}
);

dojo.widget.defineWidget(
	"dojo.widget.TreeLoadingControllerV3",
	dojo.widget.TreeBasicControllerV3,
{	
	RpcUrl: "",

	RpcActionParam: "action", // used for GET for RpcUrl

	preventCache: true,

	checkValidRpcResponse: function(type, obj) {
		if (type != "load") {
			var extra = {}				
			for(var i=1; i<arguments.length;i++) {
				dojo.lang.mixin(extra, arguments[i]);					
			}
			return new dojo.CommunicationError(obj, extra);				
		}
		
		if (typeof obj != 'object') {
			return new dojo.FormatError("Wrong server answer format "+(obj && obj.toSource ? obj.toSource() : obj)+" type "+(typeof obj), obj);
		}
		
		//dojo.debugShallow(obj);
			
		if (!dojo.lang.isUndefined(obj.error)) {
			return new dojo.RpcError(obj.error, obj);
		}
		
		return false;
	},
		

	getDeferredBindHandler: function(/* dojo.rpc.Deferred */ deferred){
		// summary
		// create callback that calls the Deferred's callback method		
		
		return dojo.lang.hitch(this, 
			function(type, obj){				
				//dojo.debug("getDeferredBindHandler "+obj.toSource());
								
				var error = this.checkValidRpcResponse.apply(this, arguments);
				
				if (error) {
					deferred.errback(error);
					return;
				}
	
				deferred.callback(obj);								
			}
		);
		
	},

	getRpcUrl: function(action) {

		// RpcUrl=local meant SOLELY for DEMO and LOCAL TESTS
		if (this.RpcUrl == "local") {
			var dir = document.location.href.substr(0, document.location.href.lastIndexOf('/'));
			var localUrl = dir+"/local/"+action;
			//dojo.debug(localUrl);
			return localUrl;	
		}

		if (!this.RpcUrl) {
			dojo.raise("Empty RpcUrl: can't load");
		}
		
		var url = this.RpcUrl;
		
		if (url.indexOf("/") != 0) { // not absolute
			var protocol = document.location.href.replace(/:\/\/.*/,'');
			var prefix = document.location.href.substring(protocol.length+3);
			
			if (prefix.lastIndexOf("/") != prefix.length-1) {
				prefix = prefix.replace(/\/[^\/]+$/,'/'); // strip file name
			}
			if (prefix.lastIndexOf("/") != prefix.length-1) {
				prefix = prefix+'/'; // add / if not exists it all
			}
			//dojo.debug(url);
			url = protocol + '://' + prefix + url;
		}
			

		return url + (url.indexOf("?")>-1 ? "&" : "?") + this.RpcActionParam+"="+action;
	},


	/**
	 * Add all loaded nodes from array obj as node children and expand it
	*/
	loadProcessResponse: function(node, result) {
		//dojo.debug("Process response "+node);
				
		if (!dojo.lang.isArray(result)) {
			throw new dojo.FormatError('loadProcessResponse: Not array loaded: '+result);
		}

		node.setChildren(result);
		
	},

	/**
	 * kw = { url, sync, params }
	 */
	runRpc: function(kw) {
		var _this = this;
		
		var deferred = new dojo.Deferred();
		
		dojo.io.bind({
			url: kw.url,			
			handle: this.getDeferredBindHandler(deferred),
			mimetype: "text/javascript",
			preventCache: this.preventCache,
			sync: kw.sync,
			content: { data: dojo.json.serialize(kw.params) }
		});
		
		return deferred;

	},



	/**
	 * Load children of the node from server
	 * Synchroneous loading doesn't break control flow
	 * I need sync mode for DnD
	*/
	loadRemote: function(node, sync){
		var _this = this;

		var params = {
			node: this.getInfo(node),
			tree: this.getInfo(node.tree)
		};

		
		var deferred = this.runRpc({
			url: this.getRpcUrl('getChildren'),
			sync: sync,
			params: params
		});
		
		deferred.addCallback(function(res) { return _this.loadProcessResponse(node,res) });
		
				
		
		return deferred;

	},

	batchExpandTimeout: 0,

	recurseToLevel: function(widget, level, callFunc, callObj, skipFirst, sync) {
		if (level == 0) return;


		
		if (!skipFirst) {
			var deferred = callFunc.call(callObj, widget, sync);
		} else {
			var deferred = dojo.Deferred.prototype.makeCalled();
		}
		
		//dojo.debug("expand deferred saved "+node+" sync "+sync);
		
		
		var _this = this;
		
		var recurseOnExpand = function() {
			var children = widget.children;
			var deferreds = [];		
			for(var i=0; i<children.length; i++) {
				//dojo.debug("push recursive call for "+node.children[i]+" level "+level);
				deferreds.push(_this.recurseToLevel(children[i], level-1, callFunc, callObj, sync));
			}
			return new dojo.DeferredList(deferreds);
		}
		
		deferred.addCallback(recurseOnExpand);
		
		return deferred;
	},
	
	
	expandToLevel: function(nodeOrTree, level, sync) {
		return this.recurseToLevel(nodeOrTree, nodeOrTree.isTree ? level+1 : level, this.expand, this, nodeOrTree.isTree, sync);
	},
	
	loadToLevel: function(nodeOrTree, level, sync) {
		return this.recurseToLevel(nodeOrTree, nodeOrTree.isTree ? level+1 : level, this.loadIfNeeded, this, nodeOrTree.isTree, sync);
	},
	
	
	loadAll: function(nodeOrTree, sync) {
		return this.loadToLevel(nodeOrTree, Number.POSITIVE_INFINITY, sync);
	},
		
	
	
	expand: function(node, sync) {		
		// widget which children are data objects, is UNCHECKED, but has children and shouldn't be loaded
		// so I put children check here too
		
		var _this = this;
		
		var deferred = this.startProcessing(node);
		
		deferred.addCallback(function() {
			return _this.loadIfNeeded(node, sync);
		});
				
		deferred.addCallback(function(res) {
			//dojo.debug("Activated callback dojo.widget.TreeBasicControllerV3.prototype.expand(node); "+res);
			dojo.widget.TreeBasicControllerV3.prototype.expand(node);
			return res;
		});
		
		deferred.addBoth(function(res) {
			_this.finishProcessing(node);
			return res;
		});
		
		
		
		return deferred;
	},

	
	loadIfNeeded: function(node, sync) {
		var deferred
		if (node.state == node.loadStates.UNCHECKED && node.isFolder && !node.children.length) {
			// populate deferred with other things to pre-do
			deferred = this.loadRemote(node, sync);			
		} else {
			/* "fake action" here */
			deferred = new dojo.Deferred();
			deferred.callback();
		}
		
		return deferred;
	},
	
	/**
	 * 1) if specified, run check, return false if failed
	 * 2) if specified, run prepare
	 * 3) run make if prepare if no errors
	 * 4) run finalize no matter what happened, pass through make result
	 * 5) if specified, run expose if no errors
	 */
	runStages: function(check, prepare, make, finalize, expose, args) {
		var _this = this;
		
		if (check && !check.apply(this, args)) {
			return false;
		}
		
		var deferred = dojo.Deferred.prototype.makeCalled();
		
		
		if (prepare) {
			deferred.addCallback(function() {
				return prepare.apply(_this, args);
			});
		}
		
		
		//deferred.addCallback(function(res) { dojo.debug("Prepare fired "+res); return res});
		
		if (make) {
			deferred.addCallback(function() {			
			var res = make.apply(_this, args);
			//res.addBoth(function(r) {dojo.debugShallow(r); return r;});
			return res;
			});
		}
		
		//deferred.addCallback(function(res) { dojo.debug("Main fired "+res); return res});
		
		if (finalize) {
			deferred.addBoth(function(res) {
				finalize.apply(_this, args);
				return res;
			});
		}
			
				
		// exposer does not affect result
		if (expose) {
			deferred.addCallback(function(res) {
				expose.apply(_this, args);
				return res;
			});
		}
		
		return deferred;
	},
		
	startProcessing: function(nodesArray) {
		var deferred = new dojo.Deferred();
		
		
		var nodes = dojo.lang.isArray(nodesArray) ? nodesArray : arguments;
		
		/*
		for(var i=0;i<nodes.length;i++) {
			dojo.debug(nodes[i]);
		}*/
		
		for(var i=0;i<nodes.length;i++) {
			if (nodes[i].isLocked()) {
				deferred.errback(new dojo.LockedError("item locked "+nodes[i], nodes[i]));
				//dojo.debug("startProcessing errback "+arguments[i]);
				return deferred;
			}
			if (nodes[i].isTreeNode) {
				//dojo.debug("mark "+nodes[i]);
				nodes[i].markProcessing();
			}
			nodes[i].lock();
		}
				
		//dojo.debug("startProcessing callback");
				
		deferred.callback();
		
		return deferred;
	},
	
	finishProcessing: function(nodesArray) {
		
		var nodes = dojo.lang.isArray(nodesArray) ? nodesArray : arguments;
		
		for(var i=0;i<nodes.length;i++) {
			if (!nodes[i].hasLock()) {
				// is not processed. probably we locked it and then met bad node in startProcessing
				continue; 
			}
			//dojo.debug("has lock");	
			nodes[i].unlock();
			if (nodes[i].isTreeNode) {
				//dojo.debug("unmark "+nodes[i]);
				nodes[i].unmarkProcessing();
			}
		}
	},
	
	// ----------------- refresh -----------------
	
	refreshChildren: function(nodeOrTree, sync) {		
		return this.runStages(null, this.prepareRefreshChildren, this.doRefreshChildren, this.finalizeRefreshChildren, this.exposeRefreshChildren, arguments);
	},


	prepareRefreshChildren: function(nodeOrTree, sync) {
		var deferred = this.startProcessing(nodeOrTree);
		nodeOrTree.destroyChildren();
						
		nodeOrTree.state = nodeOrTree.loadStates.UNCHECKED;
		
		return deferred;
	},
	
	doRefreshChildren: function(nodeOrTree, sync) {
		return this.loadRemote(nodeOrTree, sync);
	},
	
	finalizeRefreshChildren: function(nodeOrTree, sync) {
		this.finishProcessing(nodeOrTree);
	},
	
	exposeRefreshChildren: function(nodeOrTree, sync) {
		nodeOrTree.expand();
	},

	// ----------------- move -----------------

	move: function(child, newParent, index/*,...*/) {
		return this.runStages(this.canMove, this.prepareMove, this.doMove, this.finalizeMove, this.exposeMove, arguments);			
	},

	doMove: function(child, newParent, index) {
		//dojo.debug("MOVE "+child);
		child.tree.move(child, newParent, index);

		return true;
	},
	
	
	prepareMove: function(child, newParent, index, sync) {
		var deferred = this.startProcessing(newParent);
		deferred.addCallback(dojo.lang.hitch(this, function() {
			return this.loadIfNeeded(newParent, sync);
		}));
		return deferred;
	},
	
	finalizeMove: function(child, newParent) {
		this.finishProcessing(newParent);
	},

	// -------------------- createChild ------------

	prepareCreateChild: function(parent, index, data, sync) {
		var deferred = this.startProcessing(parent);
		
		deferred.addCallback(dojo.lang.hitch(this, function() {
			return this.loadIfNeeded(parent, sync);
		}));
		return deferred;
	},
	
	finalizeCreateChild: function(parent) {
		this.finishProcessing(parent);
	},

	// ---------------- clone ---------------
	
	prepareClone: function(child, newParent, index, deep, sync) {
		var deferred = this.startProcessing(child, newParent);
		deferred.addCallback(dojo.lang.hitch(this, function() {
			return this.loadIfNeeded(newParent, sync);
		}));		
		return deferred;	
	},	
	
	finalizeClone: function(child, newParent) {
		this.finishProcessing(child, newParent);
	}

});
