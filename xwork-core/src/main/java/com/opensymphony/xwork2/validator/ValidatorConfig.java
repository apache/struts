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

import com.opensymphony.xwork2.util.location.Located;
import com.opensymphony.xwork2.util.location.Location;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Holds the necessary information for configuring an instance of a Validator.
 * 
 * 
 * @author James House
 * @author Rainer Hermanns
 * @author tm_jee
 * @author Martin Gilday 
 */
public class ValidatorConfig extends Located {

    private String type;
    private Map<String,String> params;
    private String defaultMessage;
    private String messageKey;
    private boolean shortCircuit;
    private String[] messageParams;
    
    /**
     * @param validatorType
     */
    protected ValidatorConfig(String validatorType) {
        this.type = validatorType;
        params = new LinkedHashMap<String, String>();
    }

    protected ValidatorConfig(ValidatorConfig orig) {
        this.type = orig.type;
        this.params = new LinkedHashMap<String,String>(orig.params);
        this.defaultMessage = orig.defaultMessage;
        this.messageKey = orig.messageKey;
        this.shortCircuit = orig.shortCircuit;
        this.messageParams = orig.messageParams;
    }
    
    /**
     * @return Returns the defaultMessage for the validator.
     */
    public String getDefaultMessage() {
        return defaultMessage;
    }
    
    /**
     * @return Returns the messageKey for the validator.
     */
    public String getMessageKey() {
        return messageKey;
    }
    
    /**
     * @return Returns wether the shortCircuit flag should be set on the 
     * validator.
     */
    public boolean isShortCircuit() {
        return shortCircuit;
    }
    
    /**
     * @return Returns the configured params to set on the validator. 
     */
    public Map<String, String> getParams() {
        return params;
    }
    
    /**
     * @return Returns the type of validator to configure.
     */
    public String getType() {
        return type;
    }

    /**
     * @return The i18n message parameters/arguments to be used.
     */
    public String[] getMessageParams() {
        return messageParams;
    }

    /**
     * Builds a ValidatorConfig
     */
    public static final class Builder {
        private ValidatorConfig target;

        public Builder(String validatorType) {
            target = new ValidatorConfig(validatorType);
        }

        public Builder(ValidatorConfig config) {
            target = new ValidatorConfig(config);
        }

        public Builder shortCircuit(boolean shortCircuit) {
            target.shortCircuit = shortCircuit;
            return this;
        }

        public Builder defaultMessage(String msg) {
            if ((msg != null) && (msg.trim().length() > 0)) {
                target.defaultMessage = msg;
            }
            return this;
        }

        public Builder messageParams(String[] msgParams) {
            target.messageParams = msgParams;
            return this;
        }

        public Builder messageKey(String key) {
            if ((key != null) && (key.trim().length() > 0)) {
                target.messageKey = key;
            }
            return this;
        }

        public Builder addParam(String name, String value) {
            if (value != null && name != null) {
                target.params.put(name, value);
            }
            return this;
        }

        public Builder addParams(Map<String,String> params) {
            target.params.putAll(params);
            return this;
        }

        public Builder location(Location loc) {
            target.location = loc;
            return this;
        }

        public ValidatorConfig build() {
            target.params = Collections.unmodifiableMap(target.params);
            ValidatorConfig result = target;
            target = new ValidatorConfig(target);
            return result;
        }

        public Builder removeParam(String key) {
            target.params.remove(key);
            return this;
        }
    }
}
