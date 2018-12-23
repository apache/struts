<#ftl output_format="JavaScript">
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
<#if parameters.onclick??>
 onclick="${parameters.onclick}"<#rt/>
</#if>
<#if parameters.ondblclick??>
 ondblclick="${parameters.ondblclick}"<#rt/>
</#if>
<#if parameters.onmousedown??>
 onmousedown="${parameters.onmousedown}"<#rt/>
</#if>
<#if parameters.onmouseup??>
 onmouseup="${parameters.onmouseup}"<#rt/>
</#if>
<#if parameters.onmouseover??>
 onmouseover="${parameters.onmouseover}"<#rt/>
</#if>
<#if parameters.onmousemove??>
 onmousemove="${parameters.onmousemove}"<#rt/>
</#if>
<#if parameters.onmouseout??>
 onmouseout="${parameters.onmouseout}"<#rt/>
</#if>
<#if parameters.onfocus??>
 onfocus="${parameters.onfocus}"<#rt/>
</#if>
<#if parameters.onblur??>
 onblur="${parameters.onblur}"<#rt/>
</#if>
<#if parameters.onkeypress??>
 onkeypress="${parameters.onkeypress}"<#rt/>
</#if>
<#if parameters.onkeydown??>
 onkeydown="${parameters.onkeydown}"<#rt/>
</#if>
<#if parameters.onkeyup??>
 onkeyup="${parameters.onkeyup}"<#rt/>
</#if>
<#if parameters.onselect??>
 onselect="${parameters.onselect}"<#rt/>
</#if>
<#if parameters.onchange??>
 onchange="${parameters.onchange}"<#rt/>
</#if>