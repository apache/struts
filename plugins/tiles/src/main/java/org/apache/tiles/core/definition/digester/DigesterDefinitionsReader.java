/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.tiles.core.definition.digester;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.apache.struts2.StrutsException;
import org.apache.tiles.api.Attribute;
import org.apache.tiles.api.Definition;
import org.apache.tiles.api.Expression;
import org.apache.tiles.api.ListAttribute;
import org.apache.tiles.core.definition.DefinitionsFactoryException;
import org.apache.tiles.core.definition.DefinitionsReader;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Reads {@link Definition} objects from
 * an XML InputStream using Digester. <p/>
 * <p>
 * This <code>DefinitionsReader</code> implementation expects the source to be
 * passed as an <code>InputStream</code>. It parses XML data from the source
 * and builds a Map of Definition objects.
 * </p>
 * <p/>
 * <p>
 * The Digester object can be configured by passing in initialization
 * parameters. Currently the only parameter that is supported is the
 * <code>validating</code> parameter. This value is set to <code>false</code>
 * by default. To enable DTD validation for XML Definition files, give the init
 * method a parameter with a key of
 * <code>org.apache.tiles.definition.digester.DigesterDefinitionsReader.PARSER_VALIDATE</code>
 * and a value of <code>&quot;true&quot;</code>. <p/>
 * <p>
 * The Definition objects are stored internally in a Map. The Map is stored as
 * an instance variable rather than a local variable in the <code>read</code>
 * method. This means that instances of this class are <strong>not</strong>
 * thread-safe and access by multiple threads must be synchronized.
 * </p>
 */
public class DigesterDefinitionsReader implements DefinitionsReader {

    /**
     * Digester validation parameter name.
     */
    public static final String PARSER_VALIDATE_PARAMETER_NAME = "org.apache.tiles.definition.digester.DigesterDefinitionsReader.PARSER_VALIDATE";

    // Digester rules constants for tag interception.

    /**
     * Intercepts a &lt;definition&gt; tag.
     */
    private static final String DEFINITION_TAG = "tiles-definitions/definition";

    /**
     * Intercepts a &lt;put-attribute&gt; tag.
     */
    private static final String PUT_TAG = "*/definition/put-attribute";

    /**
     * Intercepts a &lt;definition&gt; inside a  &lt;put-attribute&gt; tag.
     */
    private static final String PUT_DEFINITION_TAG = "*/put-attribute/definition";

    /**
     * Intercepts a &lt;definition&gt; inside an &lt;add-attribute&gt; tag.
     */
    private static final String ADD_DEFINITION_TAG = "*/add-attribute/definition";

    /**
     * Intercepts a &lt;put-list-attribute&gt; tag inside a %lt;definition&gt;
     * tag.
     */
    private static final String DEF_LIST_TAG = "*/definition/put-list-attribute";

    /**
     * Intercepts a &lt;add-attribute&gt; tag.
     */
    private static final String ADD_LIST_ELE_TAG = "*/add-attribute";

    /**
     * Intercepts a &lt;add-list-attribute&gt; tag.
     */
    private static final String NESTED_LIST = "*/add-list-attribute";

    // Handler class names.

    /**
     * The handler to create definitions.
     *
     * @since 2.1.0
     */
    protected static final String DEFINITION_HANDLER_CLASS =
        Definition.class.getName();

    /**
     * The handler to create attributes.
     *
     * @since 2.1.0
     */
    protected static final String PUT_ATTRIBUTE_HANDLER_CLASS =
        Attribute.class.getName();

    /**
     * The handler to create list attributes.
     *
     * @since 2.1.0
     */
    protected static final String LIST_HANDLER_CLASS =
        ListAttribute.class.getName();

    /**
     * Digester rule to manage definition filling.
     *
     * @since 2.1.2
     */
    public static class FillDefinitionRule extends Rule {

        /** {@inheritDoc} */
        @Override
        public void begin(String namespace, String name, Attributes attributes) {
            Definition definition = (Definition) digester.peek();
            definition.setName(attributes.getValue("name"));
            definition.setPreparer(attributes.getValue("preparer"));
            String extendsAttribute = attributes.getValue("extends");
            definition.setExtends(extendsAttribute);

            String template = attributes.getValue("template");
            Attribute attribute = Attribute.createTemplateAttribute(template);
            attribute.setExpressionObject(Expression
                    .createExpressionFromDescribedExpression(attributes
                            .getValue("templateExpression")));
            attribute.setRole(attributes.getValue("role"));
            String templateType = attributes.getValue("templateType");
            if (templateType != null) {
                attribute.setRenderer(templateType);
            } else if (extendsAttribute != null) {
                attribute.setRenderer(null);
            }
            definition.setTemplateAttribute(attribute);
        }
    }

    /**
     * Digester rule to manage attribute filling.
     *
     * @since 2.1.0
     */
    public static class FillAttributeRule extends Rule {

        /** {@inheritDoc} */
        @Override
        public void begin(String namespace, String name, Attributes attributes) {
            Attribute attribute = (Attribute) digester.peek();
            attribute.setValue(attributes.getValue("value"));
            String expression = attributes.getValue("expression");
            attribute.setExpressionObject(Expression
                    .createExpressionFromDescribedExpression(expression));
            attribute.setRole(attributes.getValue("role"));
            attribute.setRenderer(attributes.getValue("type"));
        }
    }

    /**
     * Digester rule to manage assignment of the attribute to the parent
     * element.
     *
     * @since 2.1.0
     */
    public static class PutAttributeRule extends Rule {

        /** {@inheritDoc} */
        @Override
        public void begin(String namespace, String name, Attributes attributes) {
            Attribute attribute = (Attribute) digester.peek(0);
            Definition definition = (Definition) digester.peek(1);
            definition.putAttribute(attributes.getValue("name"), attribute,
                    "true".equals(attributes.getValue("cascade")));
        }
    }

    /**
     * Digester rule to manage assignment of a nested definition in an attribute
     * value.
     *
     * @since 2.1.0
     */
    public class AddNestedDefinitionRule extends Rule {

        /** {@inheritDoc} */
        @Override
        public void begin(String namespace, String name, Attributes attributes) {
            Definition definition = (Definition) digester.peek(0);
            if (definition.getName() == null) {
                definition.setName(getNextUniqueDefinitionName(definitions));
            }
            Attribute attribute = (Attribute) digester.peek(1);
            attribute.setValue(definition.getName());
            attribute.setRenderer("definition");
        }
    }

    /**
     * <code>Digester</code> object used to read Definition data
     * from the source.
     */
    protected Digester digester;

    /**
     * The set of public identifiers, and corresponding resource names for
     * the versions of the configuration file DTDs we know about.  There
     * <strong>MUST</strong> be an even number of Strings in this list!
     */
    protected String[] registrations;

    /**
     * Stores Definition objects.
     */
    private Map<String, Definition> definitions;

    /**
     * Index to be used to create unique definition names for anonymous
     * (nested) definitions.
     */
    private int anonymousDefinitionIndex = 1;

    /**
     * Creates a new instance of DigesterDefinitionsReader.
     */
    public DigesterDefinitionsReader() {
        digester = new Digester();
        digester.setNamespaceAware(true);
        digester.setUseContextClassLoader(true);
        digester.setErrorHandler(new ThrowingErrorHandler());
        try {
            //OWASP
            //https://cheatsheetseries.owasp.org/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.html
            digester.setFeature("http://xml.org/sax/features/external-general-entities", false);
            digester.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            // Disable external DTDs as well
            digester.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            digester.setXIncludeAware(false);
        } catch (ParserConfigurationException | SAXNotRecognizedException | SAXNotSupportedException e) {
            throw new StrutsException("Unable to disable external XML entity parsing", e);
        }

        // Register our local copy of the DTDs that we can find
        String[] registrations = getRegistrations();
        for (int i = 0; i < registrations.length; i += 2) {
            URL url = this.getClass().getResource(
                registrations[i + 1]);
            if (url != null) {
                digester.register(registrations[i], url.toString());
            }
        }

        initSyntax(digester);
    }

    /**
     * Sets the validation of XML files.
     *
     * @param validating <code>true</code> means that XML validation is turned
     * on. <code>false</code> otherwise.
     * @since 3.3.0
     */
    public void setValidating(boolean validating) {
        digester.setValidating(validating);
    }

    /**
     * Reads <code>{@link Definition}</code> objects from a source.
     * <p/>
     * Implementations should publish what type of source object is expected.
     *
     * @param source The <code>InputStream</code> source from which definitions
     *               will be read.
     * @return a Map of <code>Definition</code> objects read from
     *         the source.
     * @throws DefinitionsFactoryException If the source is invalid or
     *          an error occurs when reading definitions.
     */
    public Map<String, Definition> read(Object source) {
        // This is an instance variable instead of a local variable because
        // we want to be able to call the addDefinition method to populate it.
        // But we reset the Map here, which, of course, has threading implications.
        definitions = new LinkedHashMap<>();

        if (source == null) {
            // Perhaps we should throw an exception here.
            return null;
        }

        InputStream input;
        try {
            input = (InputStream) source;
        } catch (ClassCastException e) {
            throw new DefinitionsFactoryException(
                "Invalid source type.  Requires java.io.InputStream.", e);
        }

        try {
            // set first object in stack
            //digester.clear();
            digester.push(this);
            // parse
            digester.parse(input);

        } catch (SAXException e) {
            throw new DefinitionsFactoryException(
                "XML error reading definitions.", e);
        } catch (IOException e) {
            throw new DefinitionsFactoryException(
                "I/O Error reading definitions.", e);
        } finally {
            digester.clear();
        }

        return definitions;
    }

    /**
     * Initialised the syntax for reading XML files containing Tiles
     * definitions.
     *
     * @param digester The digester to initialize.
     */
    protected void initSyntax(Digester digester) {
        initDigesterForTilesDefinitionsSyntax(digester);
    }


    /**
     * Init digester for Tiles syntax with first element = tiles-definitions.
     *
     * @param digester Digester instance to use.
     */
    private void initDigesterForTilesDefinitionsSyntax(Digester digester) {
        // syntax rules
        digester.addObjectCreate(DEFINITION_TAG, DEFINITION_HANDLER_CLASS);
        digester.addRule(DEFINITION_TAG, new FillDefinitionRule());
        digester.addSetNext(DEFINITION_TAG, "addDefinition", DEFINITION_HANDLER_CLASS);

        // nested definition rules
        digester.addObjectCreate(PUT_DEFINITION_TAG, DEFINITION_HANDLER_CLASS);
        digester.addRule(PUT_DEFINITION_TAG, new FillDefinitionRule());
        digester.addSetRoot(PUT_DEFINITION_TAG, "addDefinition");
        digester.addRule(PUT_DEFINITION_TAG, new AddNestedDefinitionRule());
        digester.addObjectCreate(ADD_DEFINITION_TAG, DEFINITION_HANDLER_CLASS);
        digester.addRule(ADD_DEFINITION_TAG, new FillDefinitionRule());
        digester.addSetRoot(ADD_DEFINITION_TAG, "addDefinition");
        digester.addRule(ADD_DEFINITION_TAG, new AddNestedDefinitionRule());

        // put / putAttribute rules
        // Rules for a same pattern are called in order, but rule.end() are called
        // in reverse order.
        // SetNext and CallMethod use rule.end() method. So, placing SetNext in
        // first position ensure it will be called last (sic).
        digester.addObjectCreate(PUT_TAG, PUT_ATTRIBUTE_HANDLER_CLASS);
        digester.addRule(PUT_TAG, new FillAttributeRule());
        digester.addRule(PUT_TAG, new PutAttributeRule());
        // Definition level list rules
        // This is rules for lists nested in a definition
        digester.addObjectCreate(DEF_LIST_TAG, LIST_HANDLER_CLASS);
        digester.addSetProperties(DEF_LIST_TAG);
        digester.addRule(DEF_LIST_TAG, new PutAttributeRule());
        // list elements rules
        // We use Attribute class to avoid rewriting a new class.
        // Name part can't be used in listElement attribute.
        digester.addObjectCreate(ADD_LIST_ELE_TAG, PUT_ATTRIBUTE_HANDLER_CLASS);
        digester.addRule(ADD_LIST_ELE_TAG, new FillAttributeRule());
        digester.addSetNext(ADD_LIST_ELE_TAG, "add", PUT_ATTRIBUTE_HANDLER_CLASS);

        // nested list elements rules
        // Create a list handler, and add it to parent list
        digester.addObjectCreate(NESTED_LIST, LIST_HANDLER_CLASS);
        digester.addSetProperties(NESTED_LIST);
        digester.addSetNext(NESTED_LIST, "add", PUT_ATTRIBUTE_HANDLER_CLASS);
    }

    /**
     * Adds a new <code>Definition</code> to the internal Map or replaces
     * an existing one.
     *
     * @param definition The Definition object to be added.
     */
    public void addDefinition(Definition definition) {
        String name = definition.getName();
        if (name == null) {
            throw new DigesterDefinitionsReaderException("A root definition has been defined with no name");
        }

        definitions.put(name, definition);
    }

    /**
     * Error Handler that throws every exception it receives.
     */
    private static class ThrowingErrorHandler implements ErrorHandler {

        /** {@inheritDoc} */
        public void warning(SAXParseException exception) throws SAXException {
            throw exception;
        }

        /** {@inheritDoc} */
        public void error(SAXParseException exception) throws SAXException {
            throw exception;
        }

        /** {@inheritDoc} */
        public void fatalError(SAXParseException exception) throws SAXException {
            throw exception;
        }
    }

    /**
     * Returns the registrations for local DTDs.
     *
     * @return An array containing the locations for registrations of local
     * DTDs.
     * @since 2.1.0
     */
    protected String[] getRegistrations() {
        if (registrations == null) {
            registrations = new String[] {
                "-//Apache Software Foundation//DTD Tiles Configuration 3.0//EN",
                "/org/apache/tiles/resources/tiles-config_3_0.dtd"};
        }
        return registrations;
    }

    /**
     * Create a unique definition name usable to store anonymous definitions.
     *
     * @param definitions The already created definitions.
     * @return The unique definition name to be used to store the definition.
     * @since 2.1.0
     */
    protected String getNextUniqueDefinitionName(
            Map<String, Definition> definitions) {
        String candidate;

        do {
            candidate = "$anonymousDefinition" + anonymousDefinitionIndex;
            anonymousDefinitionIndex++;
        } while (definitions.containsKey(candidate));

        return candidate;
    }
}
