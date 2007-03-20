/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.charting.Plotters");

/*	
 *	Plotters is the placeholder; what will happen is that the proper renderer types
 *	will be mixed into this object (as opposed to creating a new one).
 */

dojo["requireIf"](dojo.render.svg.capable, "dojo.charting.svg.Plotters");
dojo["requireIf"](!dojo.render.svg.capable && dojo.render.vml.capable, "dojo.charting.vml.Plotters");
