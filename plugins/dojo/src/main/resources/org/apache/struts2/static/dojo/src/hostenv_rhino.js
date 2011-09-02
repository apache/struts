/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.hostenv.println = function (line) {
	if (arguments.length > 0) {
		print(arguments[0]);
		for (var i = 1; i < arguments.length; i++) {
			var valid = false;
			for (var p in arguments[i]) {
				valid = true;
				break;
			}
			if (valid) {
				dojo.debugShallow(arguments[i]);
			}
		}
	} else {
		print(line);
	}
};
dojo.locale = dojo.locale || java.util.Locale.getDefault().toString().replace("_", "-").toLowerCase();
dojo.render.name = dojo.hostenv.name_ = "rhino";
dojo.hostenv.getVersion = function () {
	return version();
};
if (dj_undef("byId")) {
	dojo.byId = function (id, doc) {
		if (id && (typeof id == "string" || id instanceof String)) {
			if (!doc) {
				doc = document;
			}
			return doc.getElementById(id);
		}
		return id;
	};
}
dojo.hostenv.loadUri = function (uri, cb) {
	try {
		var local = (new java.io.File(uri)).exists();
		if (!local) {
			try {
				var stream = (new java.net.URL(uri)).openStream();
				stream.close();
			}
			catch (e) {
				return false;
			}
		}
		if (cb) {
			var contents = (local ? readText : readUri)(uri, "UTF-8");
			cb(eval("(" + contents + ")"));
		} else {
			load(uri);
		}
		return true;
	}
	catch (e) {
		dojo.debug("rhino load('" + uri + "') failed. Exception: " + e);
		return false;
	}
};
dojo.hostenv.exit = function (exitcode) {
	quit(exitcode);
};
function dj_rhino_current_script_via_java(depth) {
	var optLevel = Packages.org.mozilla.javascript.Context.getCurrentContext().getOptimizationLevel();
	var caw = new java.io.CharArrayWriter();
	var pw = new java.io.PrintWriter(caw);
	var exc = new java.lang.Exception();
	var s = caw.toString();
	var matches = s.match(/[^\(]*\.js\)/gi);
	if (!matches) {
		throw Error("cannot parse printStackTrace output: " + s);
	}
	var fname = ((typeof depth != "undefined") && (depth)) ? matches[depth + 1] : matches[matches.length - 1];
	var fname = matches[3];
	if (!fname) {
		fname = matches[1];
	}
	if (!fname) {
		throw Error("could not find js file in printStackTrace output: " + s);
	}
	return fname;
}
function readText(path, encoding) {
	encoding = encoding || "utf-8";
	var jf = new java.io.File(path);
	var is = new java.io.FileInputStream(jf);
	return dj_readInputStream(is, encoding);
}
function readUri(uri, encoding) {
	var conn = (new java.net.URL(uri)).openConnection();
	encoding = encoding || conn.getContentEncoding() || "utf-8";
	var is = conn.getInputStream();
	return dj_readInputStream(is, encoding);
}
function dj_readInputStream(is, encoding) {
	var input = new java.io.BufferedReader(new java.io.InputStreamReader(is, encoding));
	try {
		var sb = new java.lang.StringBuffer();
		var line = "";
		while ((line = input.readLine()) !== null) {
			sb.append(line);
			sb.append(java.lang.System.getProperty("line.separator"));
		}
		return sb.toString();
	}
	finally {
		input.close();
	}
}
if (!djConfig.libraryScriptUri.length) {
	try {
		djConfig.libraryScriptUri = dj_rhino_current_script_via_java(1);
	}
	catch (e) {
		if (djConfig["isDebug"]) {
			print("\n");
			print("we have no idea where Dojo is located.");
			print("Please try loading rhino in a non-interpreted mode or set a");
			print("\n\tdjConfig.libraryScriptUri\n");
			print("Setting the dojo path to './'");
			print("This is probably wrong!");
			print("\n");
			print("Dojo will try to load anyway");
		}
		djConfig.libraryScriptUri = "./";
	}
}
dojo.doc = function () {
	return document;
};
dojo.body = function () {
	return document.body;
};
function setTimeout(func, delay) {
	var def = {sleepTime:delay, hasSlept:false, run:function () {
		if (!this.hasSlept) {
			this.hasSlept = true;
			java.lang.Thread.currentThread().sleep(this.sleepTime);
		}
		try {
			func();
		}
		catch (e) {
			dojo.debug("Error running setTimeout thread:" + e);
		}
	}};
	var runnable = new java.lang.Runnable(def);
	var thread = new java.lang.Thread(runnable);
	thread.start();
}
dojo.requireIf((djConfig["isDebug"] || djConfig["debugAtAllCosts"]), "dojo.debug");

