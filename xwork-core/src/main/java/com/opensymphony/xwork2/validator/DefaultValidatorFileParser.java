/*
 * Copyright 2002-2006,2009 The Apache Software Foundation.
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
package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.providers.XmlHelper;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.DomHelper;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import java.io.InputStream;
import java.util.*;


/**
 * Parse the validation file. (eg. MyAction-validation.xml, MyAction-actionAlias-validation.xml)
 * to return a List of ValidatorConfig encapsulating the validator information.
 *
 * @author Jason Carreira
 * @author James House
 * @author tm_jee ( tm_jee (at) yahoo.co.uk )
 * @author Rob Harrop
 * @author Rene Gielen
 * @author Martin Gilday
 * 
 * @see com.opensymphony.xwork2.validator.ValidatorConfig
 */
public class DefaultValidatorFileParser implements ValidatorFileParser {

    private static Logger LOG = LoggerFactory.getLogger(DefaultValidatorFileParser.class);

    static final String DEFAULT_MULTI_TEXTVALUE_SEPARATOR = " ";
    static final String MULTI_TEXTVALUE_SEPARATOR_CONFIG_KEY = "xwork.validatorfileparser.multi_textvalue_separator";

    private ObjectFactory objectFactory;
    private String multiTextvalueSeparator=DEFAULT_MULTI_TEXTVALUE_SEPARATOR;

    @Inject(value=MULTI_TEXTVALUE_SEPARATOR_CONFIG_KEY, required = false)
    public void setMultiTextvalueSeparator(String type) {
        multiTextvalueSeparator = type;
    }

    public String getMultiTextvalueSeparator() {
        return multiTextvalueSeparator;
    }

    @Inject
    public void setObjectFactory(ObjectFactory fac) {
        this.objectFactory = fac;
    }

    public List<ValidatorConfig> parseActionValidatorConfigs(ValidatorFactory validatorFactory, InputStream is, final String resourceName) {
        List<ValidatorConfig> validatorCfgs = new ArrayList<ValidatorConfig>();

        InputSource in = new InputSource(is);
        in.setSystemId(resourceName);

        Map<String, String> dtdMappings = new HashMap<String, String>();
        dtdMappings.put("-//Apache Struts//XWork Validator 1.0//EN", "xwork-validator-1.0.dtd");
        dtdMappings.put("-//Apache Struts//XWork Validator 1.0.2//EN", "xwork-validator-1.0.2.dtd");
        dtdMappings.put("-//Apache Struts//XWork Validator 1.0.3//EN", "xwork-validator-1.0.3.dtd");
        dtdMappings.put("-//Apache Struts//XWork Validator Config 1.0//EN", "xwork-validator-config-1.0.dtd");

        Document doc = DomHelper.parse(in, dtdMappings);

        if (doc != null) {
            NodeList fieldNodes = doc.getElementsByTagName("field");

            // BUG: xw-305: Let validator be parsed first and hence added to 
            // the beginning of list and therefore evaluated first, so short-circuting
            // it will not cause field-level validator to be kicked off.
            {
                NodeList validatorNodes = doc.getElementsByTagName("validator");
                addValidatorConfigs(validatorFactory, validatorNodes, new HashMap<String, Object>(), validatorCfgs);
            }

            for (int i = 0; i < fieldNodes.getLength(); i++) {
                Element fieldElement = (Element) fieldNodes.item(i);
                String fieldName = fieldElement.getAttribute("name");
                Map<String, Object> extraParams = new HashMap<String, Object>();
                extraParams.put("fieldName", fieldName);

                NodeList validatorNodes = fieldElement.getElementsByTagName("field-validator");
                addValidatorConfigs(validatorFactory, validatorNodes, extraParams, validatorCfgs);
            }
        }

        return validatorCfgs;
    }


    public void parseValidatorDefinitions(Map<String, String> validators, InputStream is, String resourceName) {

        InputSource in = new InputSource(is);
        in.setSystemId(resourceName);

        Map<String, String> dtdMappings = new HashMap<String, String>();
        dtdMappings.put("-//Apache Struts//XWork Validator Config 1.0//EN", "xwork-validator-config-1.0.dtd");
        dtdMappings.put("-//Apache Struts//XWork Validator Definition 1.0//EN", "xwork-validator-definition-1.0.dtd");

        Document doc = DomHelper.parse(in, dtdMappings);

        if (doc != null) {
            NodeList nodes = doc.getElementsByTagName("validator");

            for (int i = 0; i < nodes.getLength(); i++) {
                Element validatorElement = (Element) nodes.item(i);
                String name = validatorElement.getAttribute("name");
                String className = validatorElement.getAttribute("class");

                try {
                    // catch any problems here
                    objectFactory.buildValidator(className, new HashMap<String, Object>(), ActionContext.getContext().getContextMap());
                    validators.put(name, className);
                } catch (Exception e) {
                    throw new ConfigurationException("Unable to load validator class " + className, e, validatorElement);
                }
            }
        }
    }

    /**
     * Extract trimmed text value from the given DOM element, ignoring XML comments. Appends all CharacterData nodes
     * and EntityReference nodes into a single String value, excluding Comment nodes.
     * This method is based on a method originally found in DomUtils class of Springframework.
     *
     * @see org.w3c.dom.CharacterData
     * @see org.w3c.dom.EntityReference
     * @see org.w3c.dom.Comment
     */
    public String getTextValue(Element valueEle) {
        StringBuilder value = new StringBuilder();
        NodeList nl = valueEle.getChildNodes();
        boolean firstCDataFound = false;
        for (int i = 0; i < nl.getLength(); i++) {
            Node item = nl.item(i);
            if ((item instanceof CharacterData && !(item instanceof Comment)) || item instanceof EntityReference) {
                final String nodeValue = item.getNodeValue();
                if (nodeValue != null) {
                    if (firstCDataFound) {
                        value.append(getMultiTextvalueSeparator());
                    } else {
                        firstCDataFound = true;
                    }
                    value.append(nodeValue.trim());
                }
            }
        }
        return value.toString().trim();
    }

    private void addValidatorConfigs(ValidatorFactory factory, NodeList validatorNodes, Map<String, Object> extraParams, List<ValidatorConfig> validatorCfgs) {
        for (int j = 0; j < validatorNodes.getLength(); j++) {
            Element validatorElement = (Element) validatorNodes.item(j);
            String validatorType = validatorElement.getAttribute("type");
            Map<String, Object> params = new HashMap<String, Object>(extraParams);

            params.putAll(XmlHelper.getParams(validatorElement));

            // ensure that the type is valid...
            try {
                factory.lookupRegisteredValidatorType(validatorType);
            } catch (IllegalArgumentException ex) {
                throw new ConfigurationException("Invalid validation type: " + validatorType, validatorElement);
            }

            ValidatorConfig.Builder vCfg = new ValidatorConfig.Builder(validatorType)
                    .addParams(params)
                    .location(DomHelper.getLocationObject(validatorElement))
                    .shortCircuit(Boolean.valueOf(validatorElement.getAttribute("short-circuit")).booleanValue());

            NodeList messageNodes = validatorElement.getElementsByTagName("message");
            Element messageElement = (Element) messageNodes.item(0);

            final Node defaultMessageNode = messageElement.getFirstChild();
            String defaultMessage = (defaultMessageNode == null) ? "" : defaultMessageNode.getNodeValue();
            vCfg.defaultMessage(defaultMessage);

            Map<String, String> messageParams = XmlHelper.getParams(messageElement);
            String key = messageElement.getAttribute("key");


            if ((key != null) && (key.trim().length() > 0)) {
                vCfg.messageKey(key);

                // Get the default message when pattern 2 is used. We are only interested in the
                // i18n message parameters when an i18n message key is specified.
                // pattern 1:
                // <message key="someKey">Default message</message>
                // pattern 2:
                // <message key="someKey">
                //     <param name="1">'param1'</param>
                //     <param name="2">'param2'</param>
                //     <param name="defaultMessage>The Default Message</param>
                // </message>

                if (messageParams.containsKey("defaultMessage")) {
                    vCfg.defaultMessage(messageParams.get("defaultMessage").toString());
                }

                // Sort the message param. those with keys as '1', '2', '3' etc. (numeric values)
                // are i18n message parameter, others are excluded.
                TreeMap<Integer, String> sortedMessageParameters = new TreeMap<Integer, String>();
                for (Map.Entry<String, String> messageParamEntry : messageParams.entrySet()) {

                    try {
                        int _order = Integer.parseInt(messageParamEntry.getKey());
                        sortedMessageParameters.put(Integer.valueOf(_order), messageParamEntry.getValue().toString());
                    }
                    catch (NumberFormatException e) {
                        // ignore if its not numeric.
                    }
                }
                vCfg.messageParams(sortedMessageParameters.values().toArray(new String[sortedMessageParameters.values().size()]));
            } else {
                if (messageParams != null && (messageParams.size() > 0)) {
                    // we are i18n message parameters defined but no i18n message,
                    // let's warn the user.
                    if (LOG.isWarnEnabled()) {
                	LOG.warn("validator of type ["+validatorType+"] have i18n message parameters defined but no i18n message key, it's parameters will be ignored");
                    }
                }
            }

            validatorCfgs.add(vCfg.build());
        }
    }
}
