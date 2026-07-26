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
package org.apache.struts2.interceptor;

import org.apache.struts2.ActionSupport;

import java.math.BigDecimal;

/**
 * Action fixture for WW-3427. The {@code aliasDest} property is bound through a custom
 * {@link ThrowingTypeConverter} (registered via {@code AliasConversionAction-conversion.properties}),
 * so aliasing a value onto it triggers a {@code TypeConversionException}.
 */
public class AliasConversionAction extends ActionSupport {

    private String aliasSource;
    private BigDecimal aliasDest;

    public String getAliasSource() {
        return aliasSource;
    }

    public void setAliasSource(String aliasSource) {
        this.aliasSource = aliasSource;
    }

    public BigDecimal getAliasDest() {
        return aliasDest;
    }

    public void setAliasDest(BigDecimal aliasDest) {
        this.aliasDest = aliasDest;
    }
}
