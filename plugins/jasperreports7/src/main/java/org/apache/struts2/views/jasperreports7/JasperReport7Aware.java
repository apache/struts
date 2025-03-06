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
package org.apache.struts2.views.jasperreports7;

import net.sf.jasperreports.engine.JasperReport;
import org.apache.struts2.ActionInvocation;
import org.apache.struts2.StrutsException;

import java.util.Locale;

public interface JasperReport7Aware {

    /**
     * Used to perform an action before report is going to be generated
     *
     * @param invocation current {@link ActionInvocation}
     */
    default void beforeReportGeneration(ActionInvocation invocation) throws StrutsException {
    }

    /**
     * Used to perform an action before report is going to be generated
     *
     * @param invocation current {@link ActionInvocation}
     */
    default void afterReportGeneration(ActionInvocation invocation, JasperReport jasperReport) throws StrutsException {
    }

    /**
     * Allows to specify action specific CSV delimiter, if returns null,
     * default one specified by {@link JasperReport7Constants#STRUTS_JASPER_REPORT_CSV_DELIMITER} will be used
     *
     * @return delimiter or null
     */
    default String getCsvDelimiter(ActionInvocation invocation) {
        return null;
    }

    /**
     * Allows to specify different local than used by the framework or an action
     *
     * @param invocation current {@link ActionInvocation}
     * @return locale or null
     */
    default Locale getReportLocale(ActionInvocation invocation) {
        return invocation.getInvocationContext().getLocale();
    }

}
