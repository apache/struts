<#--
/*
 * $Id$
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
-->
<#include "tigris-macros.ftl">
<#assign hideNav = true>
<#call startPage pageTitle="Validator Details"/>
<table>
<tr><td>Validated Class:</td><td>${action.stripPackage(clazz)}</td></tr>
<tr><td>Context:</td><td>${context?html}</td></tr>
<tr><td>Validator Number:</td><td>${selected}</td></tr>
<tr><td>Validator Type:</td><td>${action.stripPackage(selectedValidator.class)}</td></tr>
</table>
<table width="100%" title="Properties">
    <tr><th>Name</th><th>Value</th><th>Type</th></tr>
    <#foreach prop in properties>
    	<tr <#if prop_index%2 gt 0>class="b"<#else>class="a"</#if>>
            <td>${prop.name}</td>
            <td><#if prop.value??>
                    <#if prop.value?is_collection>(size = ${prop.value?size})<#foreach v in prop.value>${v.value}, </#foreach>
                    <#else>${prop.value?string}</#if>
                <#else> <b>null</b> </#if></td>
            <td><#if prop.value?? && prop.value?is_collection>(collection)<#else>${prop.type.name}</#if></td>
        </tr>
    </#foreach></table>
<#call endPage>
