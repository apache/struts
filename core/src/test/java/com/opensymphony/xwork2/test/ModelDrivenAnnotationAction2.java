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
package com.opensymphony.xwork2.test;

import com.opensymphony.xwork2.ModelDrivenAnnotationAction;


/**
 * Extend ModelDrivenAction to test class hierarchy traversal.
 *
 * @author Mark Woon
 * @author Rainer Hermanns
 */
public class ModelDrivenAnnotationAction2 extends ModelDrivenAnnotationAction {

    private AnnotationTestBean2 model = new AnnotationTestBean2();


    /**
     * @return the model to be pushed onto the ValueStack after the Action itself
     */
    @Override
    public Object getModel() {
        return model;
    }
}
