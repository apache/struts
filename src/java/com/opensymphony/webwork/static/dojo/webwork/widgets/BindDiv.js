dojo.provide("webwork.widgets.BindDiv");
dojo.provide("webwork.widgets.HTMLBindDiv");

dojo.require("dojo.io.*");
dojo.require("dojo.event.*");
dojo.require("dojo.widget.*");
dojo.require("dojo.xml.Parse");

dojo.require("webwork.Util");
dojo.require("webwork.widgets.HTMLBind");

/*
 * Component to do remote updating of a DOM tree.
 */

webwork.widgets.HTMLBindDiv = function() {

	// inheritance
    // see: http://www.cs.rit.edu/~atk/JavaScript/manuals/jsobj/
	webwork.widgets.HTMLBind.call(this);
	var self = this;

	this.widgetType = "BindDiv";
	this.templatePath = dojo.uri.dojoUri("webwork/widgets/BindDiv.html");


	// register a global object to use for window.setTimeout callbacks
	this.callback = webwork.Util.makeGlobalCallback(this);

	//
	// default properties that can be provided by the widget user
	//

	// html to display while loading remote content
    this.loadingHtml = "";

	// initial dealy before fetching content
	this.delay = 0;
	
	// how often to update the content from the server, after the initial delay
	this.refresh = 0;

	// does the timeout loop start automatically ?
	this.autoStart = true;

	// dom node in the template that will contain the remote content
	this.contentDiv = null;

	// support a toggelable div - each listenEvent will trigger a change in the display state
	// the bind call will only happen when the remote div is displayed
	this.toggle = false;
		
	this._nextTimeout = function(millis) {
		webwork.Util.setTimeout(self.callback, "afterTimeout", millis);
	}

	var super_fillInTemplate = this.fillInTemplate;
	this.fillInTemplate = function(args, frag) {
		super_fillInTemplate(args, frag);

		if (self.id == "") { 
			self.contentDiv.id = webwork.Util.nextId();		
		}else {
			self.contentDiv.id = self.id;
		}

		self.targetDiv = self.contentDiv.id;
		
		webwork.Util.passThroughArgs(self.extraArgs, self.contentDiv);
		webwork.Util.passThroughWidgetTagContent(self, frag, self.contentDiv);

		// hook into before the bind operation to display the loading message
		// do this always - to allow for on the fuy changes to the loadingHtml
		dojo.event.kwConnect({
			srcObj: self,
			srcFunc: "bind",
			adviceObj: self,
			adviceFunc: "loading"
		});

		if (self.autoStart) {
			self.start();
		}

	   	if (self.toggle) {
			dojo.event.kwConnect({
				type: 'around',
				srcObj: self,
				srcFunc: "bind",
				adviceObj: self,
				adviceFunc: "__toggleInterceptor"
			});
    	}

	}
	
	this.__toggleInterceptor = function(invocation) {
		var hidden = self.contentDiv.style.display == 'none';
		self.contentDiv.style.display = (hidden)?'':'none';
		if (hidden) {
			invocation.proceed();
		}
	}
	
	this.error = function(type, error) {
		//for (a in error) dojo.debug("error." + a + ":" + error[a]);
		if (self.showTransportError) {
			self.contentDiv.innerHTML = error.message;
		}else{
			self.contentDiv.innerHTML = self.errorHtml;
		}
	}
	
    this.loading = function() {
        if( self.loadingHtml != "" ) {
        	self.contentDiv.innerHTML = self.loadingHtml;
        }
	}

	this.afterTimeout = function() {
		if (running) {
		
			// do the bind
			self.bind();
			
			// setup the next timeout
			if (self.refresh > 0) {
				self._nextTimeout(self.refresh);
			}
		}
	}

	
	var running = false;
	this.stop = function() {
		if (!running) return;
		running = false;
		webwork.Util.clearTimeout(self.callback);
	}

	this.start = function() {
		if (running) return;
		running = true;
		
		if (self.delay > 0) {
			self._nextTimeout(self.delay);
		}
	}

}
dojo.inherits(webwork.widgets.HTMLBindDiv, webwork.widgets.HTMLBind);

// make it a tag
dojo.widget.tags.addParseTreeHandler("dojo:BindDiv");

// HACK - register this module as a widget package - to be replaced when dojo implements a propper widget namspace manager
dojo.widget.manager.registerWidgetPackage('webwork.widgets');
