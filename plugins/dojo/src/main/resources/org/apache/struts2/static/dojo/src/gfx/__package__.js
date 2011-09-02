/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.kwCompoundRequire({common:["dojo.gfx.color", "dojo.gfx.matrix", "dojo.gfx.common"]});
dojo.requireIf(dojo.render.svg.capable, "dojo.gfx.svg");
dojo.requireIf(dojo.render.vml.capable, "dojo.gfx.vml");
dojo.provide("dojo.gfx.*");

