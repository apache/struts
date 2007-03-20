/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.graphics.color.hsv");
dojo.require("dojo.gfx.color.hsv");

dojo.deprecated("dojo.graphics.color.hsv has been replaced by dojo.gfx.color.hsv", "0.5");

dojo.graphics.color.rgb2hsv = function(r, g, b){
	dojo.deprecated("dojo.graphics.color.rgb2hsv has been replaced by dojo.gfx.color.rgb2hsv", "0.5");
	return dojo.gfx.color.rgb2hsv(r, g, b);
}
dojo.graphics.color.hsv2rgb = function(h, s, v){
	dojo.deprecated("dojo.graphics.color.hsv2rgb has been replaced by dojo.gfx.color.hsv2rgb", "0.5");
	return dojo.gfx.color.hsv2rgb(h, s, v);
}
