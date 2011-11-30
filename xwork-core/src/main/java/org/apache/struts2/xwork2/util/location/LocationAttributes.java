/*
 * Copyright 2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opensymphony.xwork2.util.location;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * A class to handle location information stored in attributes.
 * These attributes are typically setup using {@link com.opensymphony.xwork2.util.location.LocationAttributes.Pipe}
 * which augments the SAX stream with additional attributes, e.g.:
 * <pre>
 * &lt;root xmlns:loc="http://struts.apache.org/xwork/location"
 *       loc:src="file://path/to/file.xml"
 *       loc:line="1" loc:column="1"&gt;
 *   &lt;foo loc:src="file://path/to/file.xml" loc:line="2" loc:column="3"/&gt;
 * &lt;/root&gt;
 * </pre>
 * 
 * @see com.opensymphony.xwork2.util.location.LocationAttributes.Pipe
 * @since 2.1.8
 * @version $Id$
 */
public class LocationAttributes {
    /** Prefix for the location namespace */
    public static final String PREFIX = "loc";
    /** Namespace URI for location attributes */
    public static final String URI = "http://struts.apache.org/xwork/location";

    /** Attribute name for the location URI */
    public static final String SRC_ATTR  = "src";
    /** Attribute name for the line number */
    public static final String LINE_ATTR = "line";
    /** Attribute name for the column number */
    public static final String COL_ATTR  = "column";

    /** Attribute qualified name for the location URI */
    public static final String Q_SRC_ATTR  = "loc:src";
    /** Attribute qualified name for the line number */
    public static final String Q_LINE_ATTR = "loc:line";
    /** Attribute qualified name for the column number */
    public static final String Q_COL_ATTR  = "loc:column";
    
    // Private constructor, we only have static methods
    private LocationAttributes() {
        // Nothing
    }
    
    /**
     * Add location attributes to a set of SAX attributes.
     * 
     * @param locator the <code>Locator</code> (can be null)
     * @param attrs the <code>Attributes</code> where locator information should be added
     * @return Location enabled Attributes.
     */
    public static Attributes addLocationAttributes(Locator locator, Attributes attrs) {
        if (locator == null || attrs.getIndex(URI, SRC_ATTR) != -1) {
            // No location information known, or already has it
            return attrs;
        }
        
        // Get an AttributeImpl so that we can add new attributes.
        AttributesImpl newAttrs = attrs instanceof AttributesImpl ?
            (AttributesImpl)attrs : new AttributesImpl(attrs);

        newAttrs.addAttribute(URI, SRC_ATTR, Q_SRC_ATTR, "CDATA", locator.getSystemId());
        newAttrs.addAttribute(URI, LINE_ATTR, Q_LINE_ATTR, "CDATA", Integer.toString(locator.getLineNumber()));
        newAttrs.addAttribute(URI, COL_ATTR, Q_COL_ATTR, "CDATA", Integer.toString(locator.getColumnNumber()));
        
        return newAttrs;
    }
    
    /**
     * Returns the {@link Location} of an element (SAX flavor).
     * 
     * @param attrs the element's attributes that hold the location information
     * @param description a description for the location (can be null)
     * @return a {@link Location} object
     */
    public static Location getLocation(Attributes attrs, String description) {
        String src = attrs.getValue(URI, SRC_ATTR);
        if (src == null) {
            return Location.UNKNOWN;
        }
        
        return new LocationImpl(description, src, getLine(attrs), getColumn(attrs));
    }

    /**
     * Returns the location of an element (SAX flavor). If the location is to be kept
     * into an object built from this element, consider using {@link #getLocation(Attributes, String)}
     * and the {@link Locatable} interface.
     * 
     * @param attrs the element's attributes that hold the location information
     * @return a location string as defined by {@link Location}.
     */
    public static String getLocationString(Attributes attrs) {
        String src = attrs.getValue(URI, SRC_ATTR);
        if (src == null) {
            return LocationUtils.UNKNOWN_STRING;
        }
        
        return src + ":" + attrs.getValue(URI, LINE_ATTR) + ":" + attrs.getValue(URI, COL_ATTR);
    }
    
    /**
     * Returns the URI of an element (SAX flavor)
     * 
     * @param attrs the element's attributes that hold the location information
     * @return the element's URI or "<code>[unknown location]</code>" if <code>attrs</code>
     *         has no location information.
     */
    public static String getURI(Attributes attrs) {
        String src = attrs.getValue(URI, SRC_ATTR);
        return src != null ? src : LocationUtils.UNKNOWN_STRING;
    }
    
    /**
     * Returns the line number of an element (SAX flavor)
     * 
     * @param attrs the element's attributes that hold the location information
     * @return the element's line number or <code>-1</code> if <code>attrs</code>
     *         has no location information.
     */
    public static int getLine(Attributes attrs) {
        String line = attrs.getValue(URI, LINE_ATTR);
        return line != null ? Integer.parseInt(line) : -1;
    }
    
    /**
     * Returns the column number of an element (SAX flavor)
     * 
     * @param attrs the element's attributes that hold the location information
     * @return the element's column number or <code>-1</code> if <code>attrs</code>
     *         has no location information.
     */
    public static int getColumn(Attributes attrs) {
        String col = attrs.getValue(URI, COL_ATTR);
        return col != null ? Integer.parseInt(col) : -1;
    }
    
    /**
     * Returns the {@link Location} of an element (DOM flavor).
     * 
     * @param elem the element that holds the location information
     * @param description a description for the location (if <code>null</code>, the element's name is used)
     * @return a {@link Location} object
     */
    public static Location getLocation(Element elem, String description) {
        Attr srcAttr = elem.getAttributeNodeNS(URI, SRC_ATTR);
        if (srcAttr == null) {
            return Location.UNKNOWN;
        }

        return new LocationImpl(description == null ? elem.getNodeName() : description,
                srcAttr.getValue(), getLine(elem), getColumn(elem));
    }
    
    /**
     * Same as <code>getLocation(elem, null)</code>.
     */
    public static Location getLocation(Element elem) {
        return getLocation(elem, null);
    }
   

    /**
     * Returns the location of an element that has been processed by this pipe (DOM flavor).
     * If the location is to be kept into an object built from this element, consider using
     * {@link #getLocation(Element)} and the {@link Locatable} interface.
     * 
     * @param elem the element that holds the location information
     * @return a location string as defined by {@link Location}.
     */
    public static String getLocationString(Element elem) {
        Attr srcAttr = elem.getAttributeNodeNS(URI, SRC_ATTR);
        if (srcAttr == null) {
            return LocationUtils.UNKNOWN_STRING;
        }
        
        return srcAttr.getValue() + ":" + elem.getAttributeNS(URI, LINE_ATTR) + ":" + elem.getAttributeNS(URI, COL_ATTR);
    }
    
    /**
     * Returns the URI of an element (DOM flavor)
     * 
     * @param elem the element that holds the location information
     * @return the element's URI or "<code>[unknown location]</code>" if <code>elem</code>
     *         has no location information.
     */
    public static String getURI(Element elem) {
        Attr attr = elem.getAttributeNodeNS(URI, SRC_ATTR);
        return attr != null ? attr.getValue() : LocationUtils.UNKNOWN_STRING;
    }

    /**
     * Returns the line number of an element (DOM flavor)
     * 
     * @param elem the element that holds the location information
     * @return the element's line number or <code>-1</code> if <code>elem</code>
     *         has no location information.
     */
    public static int getLine(Element elem) {
        Attr attr = elem.getAttributeNodeNS(URI, LINE_ATTR);
        return attr != null ? Integer.parseInt(attr.getValue()) : -1;
    }

    /**
     * Returns the column number of an element (DOM flavor)
     * 
     * @param elem the element that holds the location information
     * @return the element's column number or <code>-1</code> if <code>elem</code>
     *         has no location information.
     */
    public static int getColumn(Element elem) {
        Attr attr = elem.getAttributeNodeNS(URI, COL_ATTR);
        return attr != null ? Integer.parseInt(attr.getValue()) : -1;
    }
    
    /**
     * Remove the location attributes from a DOM element.
     * 
     * @param elem the element to remove the location attributes from.
     * @param recurse if <code>true</code>, also remove location attributes on descendant elements.
     */
    public static void remove(Element elem, boolean recurse) {
        elem.removeAttributeNS(URI, SRC_ATTR);
        elem.removeAttributeNS(URI, LINE_ATTR);
        elem.removeAttributeNS(URI, COL_ATTR);
        if (recurse) {
            NodeList children = elem.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    remove((Element)child, recurse);
                }
            }
        }
    }

    /**
     * A SAX filter that adds the information available from the <code>Locator</code> as attributes.
     * The purpose of having location as attributes is to allow this information to survive transformations
     * of the document (an XSL could copy these attributes over) or conversion of SAX events to a DOM.
     * <p>
     * The location is added as 3 attributes in a specific namespace to each element.
     * <pre>
     * &lt;root xmlns:loc="http://opensymphony.com/xwork/location"
     *       loc:src="file://path/to/file.xml"
     *       loc:line="1" loc:column="1"&gt;
     *   &lt;foo loc:src="file://path/to/file.xml" loc:line="2" loc:column="3"/&gt;
     * &lt;/root&gt;
     * </pre>
     * <strong>Note:</strong> Although this adds a lot of information to the serialized form of the document,
     * the overhead in SAX events is not that big, as attribute names are interned, and all <code>src</code>
     * attributes point to the same string.
     * 
     * @see com.opensymphony.xwork2.util.location.LocationAttributes
     */
    public static class Pipe implements ContentHandler {
        
        private Locator locator;
        
        private ContentHandler nextHandler;
        
        /**
         * Create a filter. It has to be chained to another handler to be really useful.
         */
        public Pipe() {
        }

        /**
         * Create a filter that is chained to another handler.
         * @param next the next handler in the chain.
         */
        public Pipe(ContentHandler next) {
            nextHandler = next;
        }

        public void setDocumentLocator(Locator locator) {
            this.locator = locator;
            nextHandler.setDocumentLocator(locator);
        }
        
        public void startDocument() throws SAXException {
            nextHandler.startDocument();
            nextHandler.startPrefixMapping(LocationAttributes.PREFIX, LocationAttributes.URI);
        }
        
        public void endDocument() throws SAXException {
            endPrefixMapping(LocationAttributes.PREFIX);
            nextHandler.endDocument();
        }

        public void startElement(String uri, String loc, String raw, Attributes attrs) throws SAXException {
            // Add location attributes to the element
            nextHandler.startElement(uri, loc, raw, LocationAttributes.addLocationAttributes(locator, attrs));
        }

        public void endElement(String arg0, String arg1, String arg2) throws SAXException {
            nextHandler.endElement(arg0, arg1, arg2);
        }

        public void startPrefixMapping(String arg0, String arg1) throws SAXException {
            nextHandler.startPrefixMapping(arg0, arg1);
        }

        public void endPrefixMapping(String arg0) throws SAXException {
            nextHandler.endPrefixMapping(arg0);
        }

        public void characters(char[] arg0, int arg1, int arg2) throws SAXException {
            nextHandler.characters(arg0, arg1, arg2);
        }

        public void ignorableWhitespace(char[] arg0, int arg1, int arg2) throws SAXException {
            nextHandler.ignorableWhitespace(arg0, arg1, arg2);
        }

        public void processingInstruction(String arg0, String arg1) throws SAXException {
            nextHandler.processingInstruction(arg0, arg1);
        }

        public void skippedEntity(String arg0) throws SAXException {
            nextHandler.skippedEntity(arg0);
        }
    }
}
