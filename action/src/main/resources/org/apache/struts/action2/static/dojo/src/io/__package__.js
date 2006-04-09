/*
	Copyright (c) 2004-2005, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.hostenv.conditionalLoadModule({
	common: ["dojo.io", false, false],
	rhino: ["dojo.io.RhinoIO", false, false],
	browser: [["dojo.io.BrowserIO", false, false], ["dojo.io.cookie", false, false]]
});
dojo.hostenv.moduleLoaded("dojo.io.*");
