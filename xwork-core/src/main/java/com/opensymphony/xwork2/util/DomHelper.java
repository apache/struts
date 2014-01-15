/*
 * Copyright 1999-2005 The Apache Software Foundation.
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
package com.opensymphony.xwork2.util;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.XWorkException;
import com.opensymphony.xwork2.util.location.Location;
import com.opensymphony.xwork2.util.location.LocationAttributes;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import java.util.Map;

/**
 * Helper class to create and retrieve information from location-enabled
 * DOM-trees.
 *
 * @since 1.2
 */
public class DomHelper {

    private static final Logger LOG = LoggerFactory.getLogger(DomHelper.class);
    
    public static final String XMLNS_URI = "http://www.w3.org/2000/xmlns/";

    public static Location getLocationObject(Element element) {
        return LocationAttributes.getLocation(element);
    }

    
    /**
     * Creates a W3C Document that remembers the location of each element in
     * the source file. The location of element nodes can then be retrieved
     * using the {@link #getLocationObject(Element)} method.
     *
     * @param inputSource the inputSource to read the document from
     */
    public static Document parse(InputSource inputSource) {
        return parse(inputSource, null);
    }
    
    
    /**
     * Creates a W3C Document that remembers the location of each element in
     * the source file. The location of element nodes can then be retrieved
     * using the {@link #getLocationObject(Element)} method.
     *
     * @param inputSource the inputSource to read the document from
     * @param dtdMappings a map of DTD names and public ids
     */
    public static Document parse(InputSource inputSource, Map<String, String> dtdMappings) {
                
        SAXParserFactory factory = null;
        String parserProp = System.getProperty("xwork.saxParserFactory");
        if (parserProp != null) {
            try {
                Class clazz = ObjectFactory.getObjectFactory().getClassInstance(parserProp);
                factory = (SAXParserFactory) clazz.newInstance();
            }
            catch (ClassNotFoundException e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error("Unable to load saxParserFactory set by system property 'xwork.saxParserFactory': #0", e, parserProp);
                }
            }
            catch (Exception e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error("Unable to load saxParserFactory set by system property 'xwork.saxParserFactory': #0", e, parserProp);
                }
            }
        }

        if (factory == null) {
            factory = SAXParserFactory.newInstance();
        }

        factory.setValidating((dtdMappings != null));
        factory.setNamespaceAware(true);

        SAXParser parser = null;
        try {
            parser = factory.newSAXParser();
        } catch (Exception ex) {
            throw new XWorkException("Unable to create SAX parser", ex);
        }
        
        
        DOMBuilder builder = new DOMBuilder();

        // Enhance the sax stream with location information
        ContentHandler locationHandler = new LocationAttributes.Pipe(builder);
        
        try {
            parser.parse(inputSource, new StartHandler(locationHandler, dtdMappings));
        } catch (Exception ex) {
            throw new XWorkException(ex);
        }
        
        return builder.getDocument();
    }
    
    /**
     * The <code>DOMBuilder</code> is a utility class that will generate a W3C
     * DOM Document from SAX events.
     *
     * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
     */
    static public class DOMBuilder implements ContentHandler {
    
        /** The default transformer factory shared by all instances */
        protected static SAXTransformerFactory FACTORY;
    
        /** The transformer factory */
        protected SAXTransformerFactory factory;
    
        /** The result */
        protected DOMResult result;
    
        /** The parentNode */
        protected Node parentNode;
        
        protected ContentHandler nextHandler;
    
        static {
            String parserProp = System.getProperty("xwork.saxTransformerFactory");
            if (parserProp != null) {
                try {
                    Class clazz = ObjectFactory.getObjectFactory().getClassInstance(parserProp);
                    FACTORY = (SAXTransformerFactory) clazz.newInstance();
                }
                catch (ClassNotFoundException e) {
                    if (LOG.isErrorEnabled()) {
                        LOG.error("Unable to load SAXTransformerFactory set by system property 'xwork.saxTransformerFactory': #0", e, parserProp);
                    }
                }
                catch (Exception e) {
                    if (LOG.isErrorEnabled()) {
                        LOG.error("Unable to load SAXTransformerFactory set by system property 'xwork.saxTransformerFactory': #0", e, parserProp);
                    }
                }
            }

            if (FACTORY == null) {
                 FACTORY = (SAXTransformerFactory) TransformerFactory.newInstance();
            }
        }

        /**
         * Construct a new instance of this DOMBuilder.
         */
        public DOMBuilder() {
            this((Node) null);
        }
    
        /**
         * Construct a new instance of this DOMBuilder.
         */
        public DOMBuilder(SAXTransformerFactory factory) {
            this(factory, null);
        }
    
        /**
         * Constructs a new instance that appends nodes to the given parent node.
         */
        public DOMBuilder(Node parentNode) {
            this(null, parentNode);
        }
    
        /**
         * Construct a new instance of this DOMBuilder.
         */
        public DOMBuilder(SAXTransformerFactory factory, Node parentNode) {
            this.factory = factory == null? FACTORY: factory;
            this.parentNode = parentNode;
            setup();
        }
    
        /**
         * Setup this instance transformer and result objects.
         */
        private void setup() {
            try {
                TransformerHandler handler = this.factory.newTransformerHandler();
                nextHandler = handler;
                if (this.parentNode != null) {
                    this.result = new DOMResult(this.parentNode);
                } else {
                    this.result = new DOMResult();
                }
                handler.setResult(this.result);
            } catch (javax.xml.transform.TransformerException local) {
                throw new XWorkException("Fatal-Error: Unable to get transformer handler", local);
            }
        }
    
        /**
         * Return the newly built Document.
         */
        public Document getDocument() {
            if (this.result == null || this.result.getNode() == null) {
                return null;
            } else if (this.result.getNode().getNodeType() == Node.DOCUMENT_NODE) {
                return (Document) this.result.getNode();
            } else {
                return this.result.getNode().getOwnerDocument();
            }
        }
    
        public void setDocumentLocator(Locator locator) {
            nextHandler.setDocumentLocator(locator);
        }
        
        public void startDocument() throws SAXException {
            nextHandler.startDocument();
        }
        
        public void endDocument() throws SAXException {
            nextHandler.endDocument();
        }
    
        public void startElement(String uri, String loc, String raw, Attributes attrs) throws SAXException {
            nextHandler.startElement(uri, loc, raw, attrs);
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
    
    public static class StartHandler extends DefaultHandler {
        
        private ContentHandler nextHandler;
        private Map<String, String> dtdMappings;
        
        /**
         * Create a filter that is chained to another handler.
         * @param next the next handler in the chain.
         */
        public StartHandler(ContentHandler next, Map<String, String> dtdMappings) {
            nextHandler = next;
            this.dtdMappings = dtdMappings;
        }

        @Override
        public void setDocumentLocator(Locator locator) {
            nextHandler.setDocumentLocator(locator);
        }
        
        @Override
        public void startDocument() throws SAXException {
            nextHandler.startDocument();
        }
        
        @Override
        public void endDocument() throws SAXException {
            nextHandler.endDocument();
        }

        @Override
        public void startElement(String uri, String loc, String raw, Attributes attrs) throws SAXException {
            nextHandler.startElement(uri, loc, raw, attrs);
        }

        @Override
        public void endElement(String arg0, String arg1, String arg2) throws SAXException {
            nextHandler.endElement(arg0, arg1, arg2);
        }

        @Override
        public void startPrefixMapping(String arg0, String arg1) throws SAXException {
            nextHandler.startPrefixMapping(arg0, arg1);
        }

        @Override
        public void endPrefixMapping(String arg0) throws SAXException {
            nextHandler.endPrefixMapping(arg0);
        }

        @Override
        public void characters(char[] arg0, int arg1, int arg2) throws SAXException {
            nextHandler.characters(arg0, arg1, arg2);
        }

        @Override
        public void ignorableWhitespace(char[] arg0, int arg1, int arg2) throws SAXException {
            nextHandler.ignorableWhitespace(arg0, arg1, arg2);
        }

        @Override
        public void processingInstruction(String arg0, String arg1) throws SAXException {
            nextHandler.processingInstruction(arg0, arg1);
        }

        @Override
        public void skippedEntity(String arg0) throws SAXException {
            nextHandler.skippedEntity(arg0);
        }
        
        @Override
        public InputSource resolveEntity(String publicId, String systemId) {
            if (dtdMappings != null && dtdMappings.containsKey(publicId)) {
                String dtdFile = dtdMappings.get(publicId);
                return new InputSource(ClassLoaderUtil.getResourceAsStream(dtdFile, DomHelper.class));
            } else if (LOG.isWarnEnabled()) {
                LOG.warn("Local DTD is missing for publicID: #0 - defined mappings: #1", publicId, dtdMappings);
            }
            return null;
        }
        
        @Override
        public void warning(SAXParseException exception) {
        }

        @Override
        public void error(SAXParseException exception) throws SAXException {
            LOG.error(exception.getMessage() + " at (" + exception.getPublicId() + ":" + 
                exception.getLineNumber() + ":" + exception.getColumnNumber() + ")", exception);
            throw exception;
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
            LOG.fatal(exception.getMessage() + " at (" + exception.getPublicId() + ":" + 
                exception.getLineNumber() + ":" + exception.getColumnNumber() + ")", exception);
            throw exception;
        }
    }

}
