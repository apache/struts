/*
	Copyright (c) 2004-2005, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.rpc.JsonService");
dojo.require("dojo.io.*");
dojo.require("dojo.json");
dojo.require("dojo.lang");

dojo.rpc.JsonService = function(url){
	if(url){
		this.connect(url);
	}
}

dojo.lang.extend(dojo.rpc.JsonService, {

	status: "LOADING",
	lastSubmissionId:0,

	createJsonRpcRequest: function(parameters, method, id){
		dojo.debug("JsonService: Create JSON-RPC Request.");

		var req = { "params": parameters, "method": method, "id": id };
		data = dojo.json.serialize(req);
		dojo.debug("JsonService: JSON-RPC Request: " + data);
		return data;
	},

	JsonRpcCallback: function(resultsCallbackFunction, /* optional */  errorCallbackFunction){
		
		return function(type, object, e){

			//dojo.debug("Returned object: Id: " + object.id + "  Results: " + object.result + "  Error: " + object.error);
			this.error = function(e){
				dojo.debug("JsonService: Error in Callback: " + e);
			}

			if(dojo.lang.isFunction(resultsCallbackFunction)){
				this.results = resultsCallbackFunction;
			}else{
				dojo.raise("JsonService: First argument to JsonRpcCallback must be the resultCallbackFunction");
			}

			if(dojo.lang.isFunction(errorCallbackFunction)){
				this.error = errorCallbackFunction;
			} 

			if(object.e != null){
				if(dojo.lang.isFunction(this.error)){
					this.error(object.error);
				}else{
					
				}
			}else{
				if(dojo.lang.isFunction(this.results)){
					this.results(object.result, /* optional */ object.id);
				}else{
					dojo.debug("JsonService: Results received but no callback method was specified.");
				}
			}
		};
	},

	createRemoteJsonRpcMethod: function(serviceURL, method, params){

	   	return function(){
			dojo.debug("JsonService: Executing Remote Method");
			if(params){
				var numberExpectedParameters = params.length;
			}else{
				var numberExpectedParameters = 0;
			}

			if(arguments.length < numberExpectedParameters){
				dojo.raise("Invalid number of parameters for remote method.");
   	        		// put error stuff here, no enough params
			}else if(arguments.length > numberExpectedParameters){
				if(dojo.lang.isFunction(arguments[arguments.length-1])){
					if(arguments.length-1 == numberExpectedParameters){
						var p = [];
						for(var n=0; n<numberExpectedParameters; n++){
							p[n] = arguments[n];
						}

						dojo.io.bind({
                 			url: serviceURL,
							postContent: this.createJsonRpcRequest(p, method, this.lastSubmissionId++),
							method: "POST",
                    		mimetype: "text/json",
                    		load: this.JsonRpcCallback(arguments[arguments.length-1])
                   		});
						return this.lastSubmissionId-1;
					}else{
						dojo.raise("Too many parameters supplied for remote method.");	
					}
				}else{
					//put error stuff here, extra params but the last one isn't a callback func
					dojo.raise("More parameters than require and/or the extra parameter isn't a callback function");
				}
			}else{
				dojo.raise("No Callback function supplied and synchronous rpc calls haven't been implemented");
			}
   	  	};
	},

	processJSDL: function (type, object, e) {
		dojo.debug("JsonService: Processing returned JSDL.");
		dojo.debug("JsonService: Creating " + object.className + " object.");
		for(var n = 0; n < object.methods.length; n++){
			dojo.debug("JsonService: Creating Method: this." + object.methods[n].name + "()");
  			this[object.methods[n].name] = this.createRemoteJsonRpcMethod(object.serviceURL, object.methods[n].name,object.methods[n].parameters);
		}
		this.status="READY";
		dojo.debug("JsonService: Dojo RPC Object is ready for use.");
	},

	viewJSDL:function(type, object, e){
		dojo.debug(object);
	},

	connect: function(jsdlURL){
		dojo.debug("JsonService: Attempting to load jsdl document from " + jsdlURL);
		dojo.io.bind({
			url: jsdlURL,
			mimetype: "text/json",
			load: dojo.lang.hitch(this, function(type, object, e){ return this.processJSDL(type,object,e) }) 
			//comment out the above and change to below if you need to see the jsdl in a debug statement
			//mimetype: "text/plain",
			//load: dojo.lang.hitch(this,function(type,object,e){ return this.viewJSDL(type,object,e) })
		});		
	}
});
