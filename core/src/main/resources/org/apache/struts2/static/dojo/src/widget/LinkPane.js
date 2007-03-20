/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.LinkPane");

dojo.require("dojo.widget.*");
dojo.require("dojo.widget.ContentPane");
dojo.require("dojo.html.style");

// summary
//	LinkPane is just a ContentPane that loads data remotely (via the href attribute),
//	and has markup similar to an anchor.  The anchor's body (the words between <a> and </a>)
//	become the label of the widget (used for TabContainer, AccordionContainer, etc.)
// usage
//	<a href="foo.html">my label</a>
dojo.widget.defineWidget(
	"dojo.widget.LinkPane",
	dojo.widget.ContentPane,
{
	// I'm using a template because the user may specify the input as
	// <a href="foo.html">label</a>, in which case we need to get rid of the
	// <a> because we don't want a link.
	templateString: '<div class="dojoLinkPane"></div>',

	fillInTemplate: function(args, frag){
		var source = this.getFragNodeRef(frag);

		// If user has specified node contents, they become the label
		// (the link must be plain text)
		this.label += source.innerHTML;

		var source = this.getFragNodeRef(frag);
		dojo.html.copyStyle(this.domNode, source);
	}
});
