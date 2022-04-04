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
${parameters.after!}<#t/>
    </td><#lt/>
</tr>
<#if (parameters.errorposition!"top") == 'bottom'>
<#assign hasFieldErrors = parameters.name?? && fieldErrors?? && fieldErrors[parameters.name]??/>
<#if hasFieldErrors>
<tr errorFor="${parameters.id}">
    <td class="tdErrorMessage" colspan="2"><#rt/>
        <#if hasFieldErrors>
            <#list fieldErrors[parameters.name] as error>
                <div class="errorMessage">${error?html}</div><#t/>
            </#list>
        </#if>
    </td><#lt/>
</tr>
</#if>
</#if>
