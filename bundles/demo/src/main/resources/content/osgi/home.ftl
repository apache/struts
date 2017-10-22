<#--
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
-->
<html>
    <head>
        <title>OSGi Demo Bundle</title>
    </head>
    <body>
        This demo contains actions that will be packaged into an OSGi bundle and loaded by the Struts 2 OSGi plugin.
        <br />
        <ul>
            <li><@s.a namespace="/osgi" action="hello-convention">Action mapped by the Convention plugin, with FreeMarker result</@s.a></li>
            <li><@s.a namespace="/osgi" action="hello-freemarker">Action mapped by XML, with FreeMarker result</@s.a></li>
            <li><@s.a namespace="/osgi" action="hello-velocity">Action mapped by XML, with Velocity result</@s.a></li>
        </ul>
    </body>
</html>