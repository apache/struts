/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.dom");
dojo.dom.ELEMENT_NODE = 1;
dojo.dom.ATTRIBUTE_NODE = 2;
dojo.dom.TEXT_NODE = 3;
dojo.dom.CDATA_SECTION_NODE = 4;
dojo.dom.ENTITY_REFERENCE_NODE = 5;
dojo.dom.ENTITY_NODE = 6;
dojo.dom.PROCESSING_INSTRUCTION_NODE = 7;
dojo.dom.COMMENT_NODE = 8;
dojo.dom.DOCUMENT_NODE = 9;
dojo.dom.DOCUMENT_TYPE_NODE = 10;
dojo.dom.DOCUMENT_FRAGMENT_NODE = 11;
dojo.dom.NOTATION_NODE = 12;
dojo.dom.dojoml = "http://www.dojotoolkit.org/2004/dojoml";
dojo.dom.xmlns = {svg:"http://www.w3.org/2000/svg", smil:"http://www.w3.org/2001/SMIL20/", mml:"http://www.w3.org/1998/Math/MathML", cml:"http://www.xml-cml.org", xlink:"http://www.w3.org/1999/xlink", xhtml:"http://www.w3.org/1999/xhtml", xul:"http://www.mozilla.org/keymaster/gatekeeper/there.is.only.xul", xbl:"http://www.mozilla.org/xbl", fo:"http://www.w3.org/1999/XSL/Format", xsl:"http://www.w3.org/1999/XSL/Transform", xslt:"http://www.w3.org/1999/XSL/Transform", xi:"http://www.w3.org/2001/XInclude", xforms:"http://www.w3.org/2002/01/xforms", saxon:"http://icl.com/saxon", xalan:"http://xml.apache.org/xslt", xsd:"http://www.w3.org/2001/XMLSchema", dt:"http://www.w3.org/2001/XMLSchema-datatypes", xsi:"http://www.w3.org/2001/XMLSchema-instance", rdf:"http://www.w3.org/1999/02/22-rdf-syntax-ns#", rdfs:"http://www.w3.org/2000/01/rdf-schema#", dc:"http://purl.org/dc/elements/1.1/", dcq:"http://purl.org/dc/qualifiers/1.0", "soap-env":"http://schemas.xmlsoap.org/soap/envelope/", wsdl:"http://schemas.xmlsoap.org/wsdl/", AdobeExtensions:"http://ns.adobe.com/AdobeSVGViewerExtensions/3.0/"};
dojo.dom.isNode = function (wh) {
	if (typeof Element == "function") {
		try {
			return wh instanceof Element;
		}
		catch (e) {
		}
	} else {
		return wh && !isNaN(wh.nodeType);
	}
};
dojo.dom.getUniqueId = function () {
	var _document = dojo.doc();
	do {
		var id = "dj_unique_" + (++arguments.callee._idIncrement);
	} while (_document.getElementById(id));
	return id;
};
dojo.dom.getUniqueId._idIncrement = 0;
dojo.dom.firstElement = dojo.dom.getFirstChildElement = function (parentNode, tagName) {
	var node = parentNode.firstChild;
	while (node && node.nodeType != dojo.dom.ELEMENT_NODE) {
		node = node.nextSibling;
	}
	if (tagName && node && node.tagName && node.tagName.toLowerCase() != tagName.toLowerCase()) {
		node = dojo.dom.nextElement(node, tagName);
	}
	return node;
};
dojo.dom.lastElement = dojo.dom.getLastChildElement = function (parentNode, tagName) {
	var node = parentNode.lastChild;
	while (node && node.nodeType != dojo.dom.ELEMENT_NODE) {
		node = node.previousSibling;
	}
	if (tagName && node && node.tagName && node.tagName.toLowerCase() != tagName.toLowerCase()) {
		node = dojo.dom.prevElement(node, tagName);
	}
	return node;
};
dojo.dom.nextElement = dojo.dom.getNextSiblingElement = function (node, tagName) {
	if (!node) {
		return null;
	}
	do {
		node = node.nextSibling;
	} while (node && node.nodeType != dojo.dom.ELEMENT_NODE);
	if (node && tagName && tagName.toLowerCase() != node.tagName.toLowerCase()) {
		return dojo.dom.nextElement(node, tagName);
	}
	return node;
};
dojo.dom.prevElement = dojo.dom.getPreviousSiblingElement = function (node, tagName) {
	if (!node) {
		return null;
	}
	if (tagName) {
		tagName = tagName.toLowerCase();
	}
	do {
		node = node.previousSibling;
	} while (node && node.nodeType != dojo.dom.ELEMENT_NODE);
	if (node && tagName && tagName.toLowerCase() != node.tagName.toLowerCase()) {
		return dojo.dom.prevElement(node, tagName);
	}
	return node;
};
dojo.dom.moveChildren = function (srcNode, destNode, trim) {
	var count = 0;
	if (trim) {
		while (srcNode.hasChildNodes() && srcNode.firstChild.nodeType == dojo.dom.TEXT_NODE) {
			srcNode.removeChild(srcNode.firstChild);
		}
		while (srcNode.hasChildNodes() && srcNode.lastChild.nodeType == dojo.dom.TEXT_NODE) {
			srcNode.removeChild(srcNode.lastChild);
		}
	}
	while (srcNode.hasChildNodes()) {
		destNode.appendChild(srcNode.firstChild);
		count++;
	}
	return count;
};
dojo.dom.copyChildren = function (srcNode, destNode, trim) {
	var clonedNode = srcNode.cloneNode(true);
	return this.moveChildren(clonedNode, destNode, trim);
};
dojo.dom.replaceChildren = function (node, newChild) {
	var nodes = [];
	if (dojo.render.html.ie) {
		for (var i = 0; i < node.childNodes.length; i++) {
			nodes.push(node.childNodes[i]);
		}
	}
	dojo.dom.removeChildren(node);
	node.appendChild(newChild);
	for (var i = 0; i < nodes.length; i++) {
		dojo.dom.destroyNode(nodes[i]);
	}
};
dojo.dom.removeChildren = function (node) {
	var count = node.childNodes.length;
	while (node.hasChildNodes()) {
		dojo.dom.removeNode(node.firstChild);
	}
	return count;
};
dojo.dom.replaceNode = function (node, newNode) {
	return node.parentNode.replaceChild(newNode, node);
};
dojo.dom.destroyNode = function (node) {
	if (node.parentNode) {
		node = dojo.dom.removeNode(node);
	}
	if (node.nodeType != 3) {
		if (dojo.evalObjPath("dojo.event.browser.clean", false)) {
			dojo.event.browser.clean(node);
		}
		if (dojo.render.html.ie) {
			node.outerHTML = "";
		}
	}
};
dojo.dom.removeNode = function (node) {
	if (node && node.parentNode) {
		return node.parentNode.removeChild(node);
	}
};
dojo.dom.getAncestors = function (node, filterFunction, returnFirstHit) {
	var ancestors = [];
	var isFunction = (filterFunction && (filterFunction instanceof Function || typeof filterFunction == "function"));
	while (node) {
		if (!isFunction || filterFunction(node)) {
			ancestors.push(node);
		}
		if (returnFirstHit && ancestors.length > 0) {
			return ancestors[0];
		}
		node = node.parentNode;
	}
	if (returnFirstHit) {
		return null;
	}
	return ancestors;
};
dojo.dom.getAncestorsByTag = function (node, tag, returnFirstHit) {
	tag = tag.toLowerCase();
	return dojo.dom.getAncestors(node, function (el) {
		return ((el.tagName) && (el.tagName.toLowerCase() == tag));
	}, returnFirstHit);
};
dojo.dom.getFirstAncestorByTag = function (node, tag) {
	return dojo.dom.getAncestorsByTag(node, tag, true);
};
dojo.dom.isDescendantOf = function (node, ancestor, guaranteeDescendant) {
	if (guaranteeDescendant && node) {
		node = node.parentNode;
	}
	while (node) {
		if (node == ancestor) {
			return true;
		}
		node = node.parentNode;
	}
	return false;
};
dojo.dom.innerXML = function (node) {
	if (node.innerXML) {
		return node.innerXML;
	} else {
		if (node.xml) {
			return node.xml;
		} else {
			if (typeof XMLSerializer != "undefined") {
				return (new XMLSerializer()).serializeToString(node);
			}
		}
	}
};
dojo.dom.createDocument = function () {
	var doc = null;
	var _document = dojo.doc();
	if (!dj_undef("ActiveXObject")) {
		var prefixes = ["MSXML2", "Microsoft", "MSXML", "MSXML3"];
		for (var i = 0; i < prefixes.length; i++) {
			try {
				doc = new ActiveXObject(prefixes[i] + ".XMLDOM");
			}
			catch (e) {
			}
			if (doc) {
				break;
			}
		}
	} else {
		if ((_document.implementation) && (_document.implementation.createDocument)) {
			doc = _document.implementation.createDocument("", "", null);
		}
	}
	return doc;
};
dojo.dom.createDocumentFromText = function (str, mimetype) {
	if (!mimetype) {
		mimetype = "text/xml";
	}
	if (!dj_undef("DOMParser")) {
		var parser = new DOMParser();
		return parser.parseFromString(str, mimetype);
	} else {
		if (!dj_undef("ActiveXObject")) {
			var domDoc = dojo.dom.createDocument();
			if (domDoc) {
				domDoc.async = false;
				domDoc.loadXML(str);
				return domDoc;
			} else {
				dojo.debug("toXml didn't work?");
			}
		} else {
			var _document = dojo.doc();
			if (_document.createElement) {
				var tmp = _document.createElement("xml");
				tmp.innerHTML = str;
				if (_document.implementation && _document.implementation.createDocument) {
					var xmlDoc = _document.implementation.createDocument("foo", "", null);
					for (var i = 0; i < tmp.childNodes.length; i++) {
						xmlDoc.importNode(tmp.childNodes.item(i), true);
					}
					return xmlDoc;
				}
				return ((tmp.document) && (tmp.document.firstChild ? tmp.document.firstChild : tmp));
			}
		}
	}
	return null;
};
dojo.dom.prependChild = function (node, parent) {
	if (parent.firstChild) {
		parent.insertBefore(node, parent.firstChild);
	} else {
		parent.appendChild(node);
	}
	return true;
};
dojo.dom.insertBefore = function (node, ref, force) {
	if ((force != true) && (node === ref || node.nextSibling === ref)) {
		return false;
	}
	var parent = ref.parentNode;
	parent.insertBefore(node, ref);
	return true;
};
dojo.dom.insertAfter = function (node, ref, force) {
	var pn = ref.parentNode;
	if (ref == pn.lastChild) {
		if ((force != true) && (node === ref)) {
			return false;
		}
		pn.appendChild(node);
	} else {
		return this.insertBefore(node, ref.nextSibling, force);
	}
	return true;
};
dojo.dom.insertAtPosition = function (node, ref, position) {
	if ((!node) || (!ref) || (!position)) {
		return false;
	}
	switch (position.toLowerCase()) {
	  case "before":
		return dojo.dom.insertBefore(node, ref);
	  case "after":
		return dojo.dom.insertAfter(node, ref);
	  case "first":
		if (ref.firstChild) {
			return dojo.dom.insertBefore(node, ref.firstChild);
		} else {
			ref.appendChild(node);
			return true;
		}
		break;
	  default:
		ref.appendChild(node);
		return true;
	}
};
dojo.dom.insertAtIndex = function (node, containingNode, insertionIndex) {
	var siblingNodes = containingNode.childNodes;
	if (!siblingNodes.length || siblingNodes.length == insertionIndex) {
		containingNode.appendChild(node);
		return true;
	}
	if (insertionIndex == 0) {
		return dojo.dom.prependChild(node, containingNode);
	}
	return dojo.dom.insertAfter(node, siblingNodes[insertionIndex - 1]);
};
dojo.dom.textContent = function (node, text) {
	if (arguments.length > 1) {
		var _document = dojo.doc();
		dojo.dom.replaceChildren(node, _document.createTextNode(text));
		return text;
	} else {
		if (node.textContent != undefined) {
			return node.textContent;
		}
		var _result = "";
		if (node == null) {
			return _result;
		}
		for (var i = 0; i < node.childNodes.length; i++) {
			switch (node.childNodes[i].nodeType) {
			  case 1:
			  case 5:
				_result += dojo.dom.textContent(node.childNodes[i]);
				break;
			  case 3:
			  case 2:
			  case 4:
				_result += node.childNodes[i].nodeValue;
				break;
			  default:
				break;
			}
		}
		return _result;
	}
};
dojo.dom.hasParent = function (node) {
	return Boolean(node && node.parentNode && dojo.dom.isNode(node.parentNode));
};
dojo.dom.isTag = function (node) {
	if (node && node.tagName) {
		for (var i = 1; i < arguments.length; i++) {
			if (node.tagName == String(arguments[i])) {
				return String(arguments[i]);
			}
		}
	}
	return "";
};
dojo.dom.setAttributeNS = function (elem, namespaceURI, attrName, attrValue) {
	if (elem == null || ((elem == undefined) && (typeof elem == "undefined"))) {
		dojo.raise("No element given to dojo.dom.setAttributeNS");
	}
	if (!((elem.setAttributeNS == undefined) && (typeof elem.setAttributeNS == "undefined"))) {
		elem.setAttributeNS(namespaceURI, attrName, attrValue);
	} else {
		var ownerDoc = elem.ownerDocument;
		var attribute = ownerDoc.createNode(2, attrName, namespaceURI);
		attribute.nodeValue = attrValue;
		elem.setAttributeNode(attribute);
	}
};

