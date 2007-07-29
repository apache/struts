/*
 * $Id: pom.xml 560558 2007-07-28 15:47:10Z apetrelli $
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

dojo.require("dojo.io.BrowserIO");
dojo.require("dojo.event.topic");

dojo.hostenv.setModulePrefix('struts', 'struts');
dojo.require('dojo.widget.*');
dojo.widget.manager.registerWidgetPackage('struts.widget');

dojo.require("struts.widget.Bind");
dojo.require("struts.widget.BindDiv");
dojo.require("struts.widget.BindAnchor");
dojo.require("struts.widget.ComboBox");
dojo.require("struts.widget.StrutsTimePicker")
dojo.require("dojo.widget.Editor2");
dojo.hostenv.writeIncludes(); // not needed, but allows the Venkman debugger to work with the includes
