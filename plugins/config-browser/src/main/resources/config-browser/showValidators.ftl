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
<!-- Validators -->
<table width="100%">
    <tr><th>Field</th><th>Type</th><th>&nbsp;</th></tr>
    <#assign row = 0>
    	<#if validators??>
        <#foreach i in validators>       
        <tr <#if i_index%2 gt 0>class="b"<#else>class="a"</#if>>
        	<td>${i.fieldName!"(see expression)"}</td>
            <td>${action.stripPackage(i.class)}</td>
            <td>
            <a href="#" onClick="window.open('validatorDetails.${extension}?clazz=${clazz}&context=${context}&selected=${row}', 'Validator Details', 'resizable=yes,toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,width=640,height=480');">details</a>
            </td>
        </tr>
        <#assign row = row + 1>
        </#foreach>
        </#if>
</table>
