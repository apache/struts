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
package org.apache.struts2.validator;

import org.apache.struts2.ActionSupport;
import org.apache.struts2.validator.annotations.DoubleRangeFieldValidator;
import org.apache.struts2.validator.annotations.ShortRangeFieldValidator;
import org.apache.struts2.validator.annotations.Validations;

/**
 * Test action to verify @Validations container annotation with doubleRangeFields and shortRangeFields
 */
public class AnnotationValidationsContainerAction extends ActionSupport {

    @Validations(
            doubleRangeFields = {
                    @DoubleRangeFieldValidator(fieldName = "price", minInclusive = "0.01", maxInclusive = "999999.99",
                            message = "Price must be between 0.01 and 999999.99"),
                    @DoubleRangeFieldValidator(fieldName = "discount", minInclusive = "0.0", maxInclusive = "100.0",
                            message = "Discount must be between 0.0 and 100.0")
            },
            shortRangeFields = {
                    @ShortRangeFieldValidator(fieldName = "quantity", min = "1", max = "1000",
                            message = "Quantity must be between 1 and 1000"),
                    @ShortRangeFieldValidator(fieldName = "priority", min = "1", max = "10",
                            message = "Priority must be between 1 and 10")
            }
    )
    @Override
    public String execute() {
        return SUCCESS;
    }

}
