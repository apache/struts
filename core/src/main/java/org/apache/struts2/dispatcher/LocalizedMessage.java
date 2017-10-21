/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2.dispatcher;

import java.util.Arrays;

public class LocalizedMessage {
    private final Class clazz;
    private final String textKey;
    private final String defaultMessage; 
    private final Object[] args;

    public LocalizedMessage(Class clazz, String textKey, String defaultMessage, Object[] args) {
        this.clazz = clazz;
        this.textKey = textKey;
        this.defaultMessage = defaultMessage;
        this.args = args;
    }

    public Class getClazz() {
        return clazz;
    }

    public String getTextKey() {
        return textKey;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public Object[] getArgs() {
        return args;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(args);
        result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
        result = prime * result + ((defaultMessage == null) ? 0 : defaultMessage.hashCode());
        result = prime * result + ((textKey == null) ? 0 : textKey.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        LocalizedMessage other = (LocalizedMessage) obj;
        if (!Arrays.equals(args, other.args)) {
            return false;
        }
        if (clazz == null) {
            if (other.clazz != null) {
                return false;
            }
        } else if (!clazz.equals(other.clazz)) {
            return false;
        }
        if (defaultMessage == null) {
            if (other.defaultMessage != null) {
                return false;
            }
        } else if (!defaultMessage.equals(other.defaultMessage)) {
            return false;
        }
        if (textKey == null) {
            if (other.textKey != null) {
                return false;
            }
        } else if (!textKey.equals(other.textKey)) {
            return false;
        }
        return true;
    }
}
