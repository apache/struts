/*
	Copyright (c) 2004-2005, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.flash");

dojo.require("dojo.string.*");
dojo.require("dojo.uri.*");

/** 
		Provides an easy object for interacting with the Flash plugin. This
		object provides methods to determine the current version of the Flash
		plugin (dojo.flash.info); execute Flash instance methods 
		independent of the Flash version
		being used (dojo.flash.comm); write out the necessary markup to 
		dynamically insert a Flash object into the page (dojo.flash.Embed; and 
		do dynamic installation and upgrading of the current Flash plugin in 
		use (dojo.flash.Install).
		
		To use dojo.flash, you must first wait until Flash is finished loading 
		and initializing before you attempt communication or interaction. 
		To know when Flash is finished use dojo.event:
		
		dojo.event.bind(dojo.flash, "loaded", myInstance, "myCallback");
		
		Then, while the page is still loading provide the file name
		and the major version of Flash that will be used for Flash/JavaScript
		communication (see "Flash Communication" below for information on the 
		different kinds of Flash/JavaScript communication supported and how they 
		depend on the version of Flash installed):
		
		dojo.flash.setSwf({flash8: "src/storage/storage_flash8.swf"});
		
		This will cause dojo.flash to load and initialize your
		Flash file "src/storage/storage_flash8.swf, and use the Flash 8
		ExternalInterface for Flash/JavaScript communication.
		
		If you want to use Flash 6 features for communication between
		Flash and JavaScript, use the following:
		
		dojo.flash.setSwf({flash6: "src/storage/storage_flash6.swf"});
		
		Flash 6 is currently the best way to do Flash/JavaScript communication
		(see the section "Flash Communication" below for further
		details), but doesn't work on all browers. If you want dojo.flash to 
		pick the best way of communicating
		based on the platform, specify Flash files for both forms of 
		communication:
		
		dojo.flash.setSwf({flash6: "src/storage/storage_flash6.swf",
											 flash8: "src/storage/storage_flash8.swf"});
											 
		If no SWF files are specified, then Flash is not initialized.
		
		Your Flash must use DojoExternalInterface to expose Flash methods and
		to call JavaScript; see "Flash Communication" below for details.
		
		setSwf can take an optional 'visible' attribute to control whether
		the Flash file is visible or not; the default is visible:
		
		dojo.flash.setSwf({flash6: "src/storage/storage_flash6.swf",
											 flash8: "src/storage/storage_flash8.swf",
											 visible: false});
		
		Once finished, you can query Flash version information:
		
		dojo.flash.info.version
		
		Or can communicate with Flash methods that were exposed:
		
		var results = dojo.flash.comm.sayHello("Some Message");
		
		Only string values are currently supported.
		
		-------------------
		Flash Communication
		-------------------
		
		dojo.flash allows Flash/JavaScript communication in 
		a way that can pass large amounts of data back and forth reliably,
		very fast, and with synchronous method calls. The dojo.flash
		framework encapsulates the specific way in which this communication occurs,
		presenting a common interface to JavaScript irrespective of the underlying
		Flash version.
		
		There are currently three major ways to do Flash/JavaScript communication
		in the Flash community:
		
		1) Flash 6+ - Uses Flash methods, such as SetVariable and TCallLabel,
		and the fscommand handler to do communication. Strengths: Very fast,
		mature, and can send extremely large amounts of data; can do
		synchronous method calls. Problems: Does not work on Safari; works on 
		Firefox/Mac OS X only if Flash 8 plugin is installed; cryptic to work with.
		
		2) Flash 8+ - Uses ExternalInterface, which provides a way for Flash
		methods to register themselves for callbacks from JavaScript, and a way
		for Flash to call JavaScript. Strengths: Works on Safari; elegant to
		work with; can do synchronous method calls. Problems: Extremely buggy 
		(fails if there are new lines in the data, for example); two orders of 
		magnitude slower than the Flash 6+ method; locks up the browser while
		it is communicating.
		
		3) Flash 6+ - Uses two seperate Flash applets, one that we 
		create over and over, passing input data into it using the PARAM tag, 
		which then uses a Flash LocalConnection to pass the data to the main Flash
		applet; communication back to Flash is accomplished using a getURL
		call with a javascript protocol handler, such as "javascript:myMethod()".
		Strengths: the most cross browser, cross platform pre-Flash 8 method
		of Flash communication known; works on Safari. Problems: Timing issues;
		clunky and complicated; slow; can only send very small amounts of
		data (several K); all method calls are asynchronous.
		
		dojo.flash.comm uses only the first two methods. This framework
		was created primarily for dojo.storage, which needs to pass very large
		amounts of data synchronously and reliably across the Flash/JavaScript
		boundary. We use the first method, the Flash 6 method, on all platforms
		that support it, while using the Flash 8 ExternalInterface method
		only on Safari with some special code to help correct ExternalInterface's
		bugs.
		
		Since dojo.flash needs to have two versions of the Flash
		file it wants to generate, a Flash 6 and a Flash 8 version to gain
		true cross-browser compatibility, several tools are provided to ease
		development on the Flash side.
		
		In your Flash file, if you want to expose Flash methods that can be
		called, use the DojoExternalInterface class to register methods. This
		class is an exact API clone of the standard ExternalInterface class, but
		can work in Flash 6+ browsers. Under the covers it uses the best
		mechanism to do communication:
		
		class HelloWorld{
			function HelloWorld(){
				// Initialize the DojoExternalInterface class
				DojoExternalInterface.initialize();
				
				// Expose your methods
				DojoExternalInterface.addCallback("sayHello", this, this.sayHello);
				
				// Tell JavaScript that you are ready to have method calls
				DojoExternalInterface.loaded();
				
				// Call some JavaScript
				DojoExternalInterface.call("someJavaScriptMethod");
			}
			
			function sayHello(){ ... }
			
			static main(){ ... }
		}
		
		DojoExternalInterface adds to new functions to the ExternalInterface
		API: initialize() and loaded(). Initialize() must be called before
		any addCallback() or call() methods are run, and loaded() must be
		called after you are finished adding your callbacks. Calling loaded()
		will fire the dojo.flash.loaded() event, so that JavaScript can know that
		Flash has finished loading and adding its callbacks, and can begin to
		interact with the Flash file.
		
		To generate your SWF files, use the ant task
		"buildFlash". You must have the open source Motion Twin ActionScript 
		compiler (mtasc) installed and in your path to use the "buildFlash"
		ant task; download and install mtasc from http://www.mtasc.org/.
		
		buildFlash usage:
		
		ant buildFlash -Ddojo.flash.file=../tests/flash/HelloWorld.as
		
		where "dojo.flash.file" is the relative path to your Flash 
		ActionScript file.
		
		This will generate two SWF files, one ending in _flash6.swf and the other
		ending in _flash8.swf in the same directory as your ActionScript method:
		
		HelloWorld_flash6.swf
		HelloWorld_flash8.swf
		
		Initialize dojo.flash with the filename and Flash communication version to
		use during page load; see the documentation for dojo.flash for details:
		
		dojo.flash.setSwf({flash6: "tests/flash/HelloWorld_flash6.swf",
											 flash8: "tests/flash/HelloWorld_flash8.swf"});
		
		Now, your Flash methods can be called from JavaScript as if they are native
		Flash methods, mirrored exactly on the JavaScript side:
		
		dojo.flash.comm.sayHello();
		
		Only Strings are supported being passed back and forth currently.
		
		-------------------
		Notes
		-------------------
		
		If you have both Flash 6 and Flash 8 versions of your file:
		
		dojo.flash.setSwf({flash6: "tests/flash/HelloWorld_flash6.swf",
											 flash8: "tests/flash/HelloWorld_flash8.swf"});
											 
		but want to force the browser to use a certain version of Flash for
		all platforms (for testing, for example), use the djConfig
		variable 'forceFlashComm' with the version number to force:
		
		var djConfig = { forceFlashComm: 6 };
		
		Two values are currently supported, 6 and 8, for the two styles of
		communication described above.
		
		Also note that dojo.flash can currently only work with one Flash applet
		on the page; it and the API do not yet support multiple Flash applets on
		the same page.
		
		@author Brad Neuberg, bkn3@columbia.edu
*/

dojo.flash = {
	flash6_version: null,
	flash8_version: null,
	_visible: true,
	
	/** Sets the SWF files and versions we are using. */
	setSwf: function(fileInfo){
		if(fileInfo == null || dojo.lang.isUndefined(fileInfo)){
			return;
		}
		
		if(fileInfo.flash6 != null && !dojo.lang.isUndefined(fileInfo.flash6)){
			this.flash6_version = fileInfo.flash6;
		}
		
		if(fileInfo.flash8 != null && !dojo.lang.isUndefined(fileInfo.flash8)){
			this.flash8_version = fileInfo.flash8;
		}
		
		if(fileInfo.visible){
			this._visible = fileInfo.visible;
		}
		
		// now initialize ourselves
		this._initialize();
	},
	
	/** Returns whether we are using Flash 6 for communication on this platform. */
	useFlash6: function(){
		if(this.flash6_version == null){
			return false;
		}else if (this.flash6_version != null && dojo.flash.info.commVersion == 6){
			// if we have a flash 6 version of this SWF, and this browser supports 
			// communicating using Flash 6 features...
			return true;
		}else{
			return false;
		}
	},
	
	/** Returns whether we are using Flash 8 for communication on this platform. */
	useFlash8: function(){
		if(this.flash8_version == null){
			return false;
		}else if (this.flash8_version != null && dojo.flash.info.commVersion == 8){
			// if we have a flash 8 version of this SWF, and this browser supports
			// communicating using Flash 8 features...
			return true;
		}else{
			return false;
		}
	},
	
	/** Initializes dojo.flash. */
	_initialize: function(){
		// do nothing if no SWF files are defined
		if(this.flash6_version == null && this.flash8_version == null){
			this.info = new Object();
			this.info.capable = false;
			return;
		}
	
		// find out if Flash is installed
		this.info = new dojo.flash.Info();
		
		// if we are not installed, install Flash
		if(this.info.capable == false){
			var installer = new dojo.flash.Install();
			installer.install();
		}else if(this.info.capable == true){
			// write the flash object into the page
			dojo.flash.obj = new dojo.flash.Embed();
			dojo.flash.obj.setVisible(this._visible);
			dojo.flash.obj.write();
			
			// initialize the way we do Flash/JavaScript communication
			dojo.flash.comm = new dojo.flash.Communicator();
		}
	},

	/** 
			A callback when the Flash subsystem is finished loading and can be
			worked with. To be notified when Flash is finished loading, connect
			your callback to this method using the following:
			
			dojo.event.connect(dojo.flash, "loaded", myInstance, "myCallback");
	*/
	loaded: function(){
	}
};


/** 
		A class that helps us determine whether Flash is available,
		it's major and minor versions, and what Flash version features should
		be used for Flash/JavaScript communication. Parts of this code
		are adapted from the automatic Flash plugin detection code autogenerated 
		by the Macromedia Flash 8 authoring environment. 
		
		An instance of this class can be accessed on dojo.flash.info after
		the page is finished loading.
		
		This constructor must be called before the page is finished loading. 
*/
dojo.flash.Info = function(){
	// Visual basic helper required to detect Flash Player ActiveX control 
	// version information on Internet Explorer
	if(dojo.render.html.ie){
		document.writeln('<script language="VBScript" type="text/vbscript"\>');
		document.writeln('Function VBGetSwfVer(i)');
		document.writeln('  on error resume next');
		document.writeln('  Dim swControl, swVersion');
		document.writeln('  swVersion = 0');
		document.writeln('  set swControl = CreateObject("ShockwaveFlash.ShockwaveFlash." + CStr(i))');
		document.writeln('  if (IsObject(swControl)) then');
		document.writeln('    swVersion = swControl.GetVariable("$version")');
		document.writeln('  end if');
		document.writeln('  VBGetSwfVer = swVersion');
		document.writeln('End Function');
		document.writeln('</script\>');
	}
	
	this._detectVersion();
	this._detectCommunicationVersion();
}

dojo.flash.Info.prototype = {
	/** The full version string, such as "8r22". */
	version: -1,
	
	/** 
			The major, minor, and revisions of the plugin. For example, if the
			plugin is 8r22, then the major version is 8, the minor version is 0,
			and the revision is 22. 
	*/
	versionMajor: -1,
	versionMinor: -1,
	versionRevision: -1,
	
	/** Whether this platform has Flash already installed. */
	capable: false,
	
	/** 
			The major version number for how our Flash and JavaScript communicate.
			This can currently be the following values:
			6 - We use a combination of the Flash plugin methods, such as SetVariable
			and TCallLabel, along with fscommands, to do communication.
			8 - We use the ExternalInterface API. 
			-1 - For some reason neither method is supported, and no communication
			is possible. 
	*/
	commVersion: 6,
	
	/** 
			Asserts that this environment has the given major, minor, and revision
			numbers for the Flash player. Returns true if the player is equal
			or above the given version, false otherwise.
			
			Example: To test for Flash Player 7r14:
			
			dojo.flash.info.isVersionOrAbove(7, 0, 14)
	*/
	isVersionOrAbove: function(reqMajorVer, reqMinorVer, reqVer){
		// make the revision a decimal (i.e. transform revision 14 into
		// 0.14
		reqVer = parseFloat("." + reqVer);
		if(this.versionMajor > reqMajorVer && this.version >= reqVer){
			return true;
		}else if(this.version >= reqVer && this.versionMinor >= reqMinorVer){
			return true;
		}else{
			return false;
		}
	},
	
	_detectVersion: function(){
		var versionStr;
		
		// loop backwards through the versions until we find the newest version	
		for(var testVersion = 25; testVersion > 0; testVersion--){
			if(dojo.render.html.ie){
				versionStr = VBGetSwfVer(testVersion);
			}else{
				versionStr = this._JSFlashInfo(testVersion);		
			}
				
			if(versionStr == -1 ){
				this.capable = false; 
				return;
			}else if(versionStr != 0){
				var versionArray;
				if(dojo.render.html.ie){
					var tempArray = versionStr.split(" ");
					var tempString = tempArray[1];
					versionArray = tempString.split(",");
				}else{
					versionArray = versionStr.split(".");
				}
					
				this.versionMajor = versionArray[0];
				this.versionMinor = versionArray[1];
				this.versionRevision = versionArray[2];
				
				// 7.0r24 == 7.24
				versionString = this.versionMajor + "." + this.versionRevision;
				this.version = parseFloat(versionString);
				
				this.capable = true;
				
				break;
			}
		}
	},
	
	/** 
			JavaScript helper required to detect Flash Player PlugIn version 
			information. Internet Explorer uses a corresponding Visual Basic
			version to interact with the Flash ActiveX control. 
	*/
	_JSFlashInfo: function(testVersion){
		// NS/Opera version >= 3 check for Flash plugin in plugin array
		if(navigator.plugins != null && navigator.plugins.length > 0){
			if(navigator.plugins["Shockwave Flash 2.0"] || 
				 navigator.plugins["Shockwave Flash"]){
				var swVer2 = navigator.plugins["Shockwave Flash 2.0"] ? " 2.0" : "";
				var flashDescription = navigator.plugins["Shockwave Flash" + swVer2].description;
				var descArray = flashDescription.split(" ");
				var tempArrayMajor = descArray[2].split(".");
				var versionMajor = tempArrayMajor[0];
				var versionMinor = tempArrayMajor[1];
				if(descArray[3] != ""){
					tempArrayMinor = descArray[3].split("r");
				}else{
					tempArrayMinor = descArray[4].split("r");
				}
				var versionRevision = tempArrayMinor[1] > 0 ? tempArrayMinor[1] : 0;
				var version = versionMajor + "." + versionMinor + "." 
											+ versionRevision;
											
				return version;
			}
		}
		
		return -1;
	},
	
	/** 
			Detects the mechanisms that should be used for Flash/JavaScript 
			communication, setting 'commVersion' to either 6 or 8. If the value is
			6, we use Flash Plugin 6+ features, such as GetVariable, TCallLabel,
			and fscommand, to do Flash/JavaScript communication; if the value is
			8, we use the ExternalInterface API for communication. 
	*/
	_detectCommunicationVersion: function(){
		// we prefer Flash 6 features over Flash 8, because they are much faster
		// and much less buggy
		
		// does the Flash plugin have some of the Flash methods?
		
		// otherwise, is the ExternalInterface API present?
	}
};

/** A class that is used to write out the Flash object into the page. */
dojo.flash.Embed = function(){
}

dojo.flash.Embed.prototype = {
	/** 
			The width of this Flash applet. The default is the minimal width
			necessary to show the Flash settings dialog. 
	*/
	width: 215,
	
	/** 
			The height of this Flash applet. The default is the minimal height
			necessary to show the Flash settings dialog. 
	*/
	width: 138,
	
	/** The id of the Flash object. */
	id: "flashObject",
	
	/** Controls whether this is a visible Flash applet or not. */
	_visible: true,
			
	/** 
			Writes the Flash into the page. This must be called before the page
			is finished loading. 
	*/
	write: function(){
		// determine our container div's styling
		var containerStyle = new dojo.string.Builder();
		containerStyle.append("width: " + this.width + "px; ");
		containerStyle.append("height: " + this.height + "px; ");
		if(this._visible == false){
			containerStyle.append("position: absolute; ");
			containerStyle.append("z-index: 100; ");
			containerStyle.append("top: -1000px; ");
			containerStyle.append("left: -1000px; ");
		}
		containerStyle = containerStyle.toString();
	
		// Flash 6
		if(dojo.flash.useFlash6()){
			var swfloc = dojo.flash.flash6_version;
			
			document.writeln('<div id="' + this.id + 'Div" style="' + containerStyle + '">');
			document.writeln('  <embed id="' + this.id + '" src="' + swfloc + '" ');
			document.writeln('    quality="high" bgcolor="#ffffff" ');
			document.writeln('    width="' + this.width + '" height="' + this.height + '" name="' + this.id + '" ');
			document.writeln('    align="middle" allowScriptAccess="sameDomain" ');
			document.writeln('    type="application/x-shockwave-flash" swLiveConnect="true" ');
			document.writeln('    pluginspage="http://www.macromedia.com/go/getflashplayer"> ');
			document.writeln('</div>');
		}
		// Flash 8
		else if (dojo.flash.useFlash8()){
			var swfloc = dojo.uri.dojoUri(dojo.flash.flash8_version).toString();
		}
	},
	
	/** Gets the Flash object DOM node. */
	get: function(){
		return (dojo.render.html.ie) ? window[this.id] : document[this.id];
	},
	
	/** Sets the visibility of this Flash object. */
	setVisible: function(){
		//FIXME: Dynamically make the movie visible or not
	},
	
	/** Centers the flash applet on the page. */
	center: function(){
	}
};


/** 
		A class that is used to communicate between Flash and JavaScript in 
		a way that can pass large amounts of data back and forth reliably,
		very fast, and with synchronous method calls. This class encapsulates the 
		specific way in which this communication occurs,
		presenting a common interface to JavaScript irrespective of the underlying
		Flash version.
*/
dojo.flash.Communicator = function(){
	if(dojo.flash.useFlash6()){
		this._writeFlash6();
	}else if (dojo.flash.useFlash8()){
		this._writeFlash8();
	}
}

dojo.flash.Communicator.prototype = {
	_writeFlash6: function(){
		var id = dojo.flash.obj.id;
		
		// global function needed for Flash 6 callback;
		// we write it out as a script tag because the VBScript hook for IE
		// callbacks does not work properly if this function is evalled() from
		// within the Dojo system
		document.writeln('<script language="JavaScript">');
		document.writeln('  function ' + id + '_DoFSCommand(command, args){ ');
		document.writeln('    dojo.flash.comm._handleFSCommand(command, args); ');
		document.writeln('}');
		document.writeln('</script>');
		
		// hook for Internet Explorer to receive FSCommands from Flash
		if(dojo.render.html.ie){
			document.writeln('<SCRIPT LANGUAGE=VBScript\> ');
			document.writeln('on error resume next ');
			document.writeln('Sub ' + id + '_FSCommand(ByVal command, ByVal args)');
			document.writeln(' call ' + id + '_DoFSCommand(command, args)');
			document.writeln('end sub');
			document.writeln('</SCRIPT\> ');
		}
	},
	
	_writeFlash8: function(){
		// nothing needed for Flash 8 communication; happens automatically
	},
	
	/** Handles fscommand's from Flash to JavaScript. Flash 6 communication. */
	_handleFSCommand: function(command, args){
		if(command == "addCallback"){ // add Flash method for JavaScript callback
			this._fscommandAddCallback(command, args);
		}else if (command == "call"){ // Flash to JavaScript method call
			this._fscommandCall(command, args);
		}
	},
	
	_fscommandAddCallback: function(command, args){
		var functionName = args;
			
		// do a trick, where we link this function name to our wrapper
		// function, _call, that does the actual JavaScript to Flash call
		var callFunc = function(){
			return dojo.flash.comm._call(functionName, arguments);
		};			
		dojo.flash.comm[functionName] = callFunc;
		
		// indicate that the call was successful
		dojo.flash.obj.get().SetVariable("_succeeded", true);
	},
	
	_fscommandCall: function(command, args){
		var plugin = dojo.flash.obj.get();
		var functionName = args;
		
		// get the number of arguments to this method call and build them up
		var numArgs = parseInt(plugin.GetVariable("_numArgs"));
		var flashArgs = new Array();
		for(var i = 0; i < numArgs; i++){
			var currentArg = plugin.GetVariable("_" + i);
			flashArgs.push(currentArg);
		}
		
		// get the function instance; we technically support more capabilities
		// than ExternalInterface, which can only call global functions; if
		// the method name has a dot in it, such as "dojo.flash.loaded", we
		// eval it so that the method gets run against an instance
		var runMe;
		if(functionName.indexOf(".") == -1){ // global function
			runMe = window[functionName];
		}else{
			// instance function
			runMe = eval(functionName);
		}
		
		// make the call and get the results
		var results = null;
		if(!dojo.lang.isUndefined(runMe) && runMe != null){
			results = runMe.apply(null, flashArgs);
		}
		
		// return the results to flash
		plugin.SetVariable("_returnResult", results);
	},
	
	/** 
			The actual function that will execute a JavaScript to Flash call; used
			by the Flash 6 communication method. 
	*/
	_call: function(functionName, args){
		// we do JavaScript to Flash method calls by setting a Flash variable
		// "_functionName" with the function name; "_numArgs" with the number
		// of arguments; and "_0", "_1", etc for each numbered argument. Flash
		// reads these, executes the function call, and returns the result
		// in "_returnResult"
		var plugin = dojo.flash.obj.get();
		plugin.SetVariable("_functionName", functionName);
		plugin.SetVariable("_numArgs", args.length);
		for(var i = 0; i < args.length; i++){
			plugin.SetVariable("_" + i, args[i]);
		}
		
		// now tell Flash to execute this method using the Flash Runner
		plugin.SetVariable("_execute", true);
		plugin.Play();
		
		// get the results
		var results = plugin.GetVariable("_returnResult");
		dojo.debug("inside, results="+results);
		
		return results;
	}
}

/** 
		Figures out the best way to automatically install the Flash plugin
		for this browser and platform. 
*/
dojo.flash.Install = function(){
}

dojo.flash.Install.prototype = {
	install: function(){
	}
}

// vim:ts=4:noet:tw=0:
