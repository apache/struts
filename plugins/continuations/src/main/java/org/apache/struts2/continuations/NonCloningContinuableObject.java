/*
 * $Id: URL.java 474191 2006-11-13 08:30:40Z mrdon $
 *
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
package org.apache.struts2.continuations;

import com.uwyn.rife.continuations.ContinuableObject;

/**
 * Implementing this interface indicates that the action should not be cloned, but instead should be re-used. This is
 * needed when you are using objects, fields, and method variables that cannot be cloned. The downside to using this is
 * that the advanced forward/backward historical support that normally automatically comes with continuations is no
 * longer available.
 */
public interface NonCloningContinuableObject extends ContinuableObject {
}
