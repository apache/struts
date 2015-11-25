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
<#include "/${parameters.templateDir}/${parameters.expandTheme}/control-close.ftl" />
<#include "/${parameters.templateDir}/simple/form-close.ftl" />
<#include "/${parameters.templateDir}/${parameters.expandTheme}/form-close-validate.ftl" />
<#if parameters.focusElement??>
<script type="text/javascript">
    StrutsUtils.addOnLoad(function() {
        var element = document.getElementById("${parameters.focusElement?html}");
        if(element) {
            element.focus();
        }
    });
</script>
</#if>
