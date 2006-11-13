/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.io.RhinoIO");

dojo.require("dojo.io.common");
dojo.require("dojo.lang.func");
dojo.require("dojo.lang.array");
dojo.require("dojo.string.extras");

dojo.io.RhinoHTTPTransport = new function(){
		this.canHandle = function(req){
			// We have to limit to text types because Rhino doesnt support 
			// a W3C dom implementation out of the box.  In the future we 
			// should provide some kind of hook to inject your own, because
			// in all my projects I use XML for Script to provide a W3C DOM.
			if(!dojo.lang.inArray((req.mimetype.toLowerCase() || ""),
				["text/plain", "text/html", "text/javascript", "text/json", "application/json"])){
				return false;
			}
			
			// We only handle http requests!  Unfortunately, because the method is 
			// protected, I can't directly create a java.net.HttpURLConnection, so
			// this is the only way to test.
			if(req.url.substr(0, 7) != "http://"){
				return false;
			}
			
			return true;
		}

		function doLoad(req, conn){
			var ret;
			if (req.method.toLowerCase() == "head"){
				// TODO: return the headers
			}else{
				var stream = conn.getContent();
				var reader = new java.io.BufferedReader(new java.io.InputStreamReader(stream));

				// read line-by-line because why not?
				var text = "";
				var line = null;
				while((line = reader.readLine()) != null){
					text += line;
				}

				if(req.mimetype == "text/javascript"){
					try{
						ret = dj_eval(text);
					}catch(e){
						dojo.debug(e);
						dojo.debug(text);
						ret = null;
					}
				}else if(req.mimetype == "text/json" || req.mimetype == "application/json"){
					try{
						ret = dj_eval("("+text+")");
					}catch(e){
						dojo.debug(e);
						dojo.debug(text);
						ret = false;
					}
				}else{
					ret = text;
				}
			}

			req.load("load", ret, req);
		}
		
		function connect(req){
			var content = req.content || {};
			var query;
	
			if (req.sendTransport){
				content["dojo.transport"] = "rhinohttp";
			}
	
			if(req.postContent){
				query = req.postContent;
			}else{
				query = dojo.io.argsFromMap(content, req.encoding);
			}
	
			var url_text = req.url;
			if(req.method.toLowerCase() == "get" && query != ""){
				url_text = url_text + "?" + query;
			}
			
			var url  = new java.net.URL(url_text);
			var conn = url.openConnection();
			
			//
			// configure the connection
			//
			
			conn.setRequestMethod(req.method.toUpperCase());
			
			if(req.headers){
				for(var header in req.headers){
					if(header.toLowerCase() == "content-type" && !req.contentType){
						req.contentType = req.headers[header];
					}else{
						conn.setRequestProperty(header, req.headers[header]);
					}
				}
			}
			if(req.contentType){
				conn.setRequestProperty("Content-Type", req.contentType);
			}

			if(req.method.toLowerCase() == "post"){
				conn.setDoOutput(true);

				// write the post data
				var output_stream = conn.getOutputStream();
				var byte_array = (new java.lang.String(query)).getBytes();
				output_stream.write(byte_array, 0, byte_array.length);
			}
			
			// do it to it!
			conn.connect();

			// perform the load
			doLoad(req, conn);
		}
		
		this.bind = function(req){
			var async = req["sync"] ? false : true;
			if (async){
				setTimeout(dojo.lang.hitch(this, function(){
					connect(req);
				}), 1);
			} else {
				connect(req);
			}
		}

		dojo.io.transports.addTransport("RhinoHTTPTransport");
}
