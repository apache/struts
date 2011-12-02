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
package com.opensymphony.xwork2.validator.metadata;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * <code>URLValidatorDescription</code>
 *
 * @author Rainer Hermanns
 * @version $Id$
 */
public class URLValidatorDescription extends AbstractFieldValidatorDescription {


    public URLValidatorDescription() {
    }

    /**
     * Creates an AbstractFieldValidatorDescription with the specified aliasNames.
     *
     * @param fieldName
     */
    public URLValidatorDescription(String fieldName) {
        super(fieldName);
    }


   /**
     * Returns the field validator XML definition.
     *
     * @return the field validator XML definition.
     */
    @Override
    public String asFieldXml() {
        StringWriter sw = new StringWriter();
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(sw);

            if ( shortCircuit) {
                writer.println("\t\t<field-validator type=\"url\">");
            } else {
                writer.println("\t\t<field-validator type=\"url\" short-circuit=\"true\">");
            }

            if ( !"".equals(key)) {
                writer.println("\t\t\t<message key=\"" + key + "\">" + message + "</message>");
            } else {
                writer.println("\t\t\t<message>" + message + "</message>");
            }

            writer.println("\t\t</field-validator>");

        } finally {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        }
        return sw.toString();

    }

    /**
     * Returns the validator XML definition.
     *
     * @return the validator XML definition.
     */
    @Override
    public String asSimpleXml() {
        StringWriter sw = new StringWriter();
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(sw);

            if ( shortCircuit) {
                writer.println("\t<validator type=\"url\">");
            } else {
                writer.println("\t<validator type=\"url\" short-circuit=\"true\">");
            }

            writer.println("\t\t<param name=\"fieldName\">" + fieldName+ "</param>");

            if ( !"".equals(key)) {
                writer.println("\t\t<message key=\"" + key + "\">" + message + "</message>");
            } else {
                writer.println("\t\t<message>" + message + "</message>");
            }

            writer.println("\t</validator>");

        } finally {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        }
        return sw.toString();
    }
}
