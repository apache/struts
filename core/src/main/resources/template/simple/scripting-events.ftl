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
<#if attributes.onclick??>
 onclick="<#outputformat 'JavaScript'>${attributes.onclick}</#outputformat>"<#rt/>
</#if>
<#if attributes.ondblclick??>
 ondblclick="<#outputformat 'JavaScript'>${attributes.ondblclick}</#outputformat>"<#rt/>
</#if>
<#if attributes.onmousedown??>
 onmousedown="<#outputformat 'JavaScript'>${attributes.onmousedown}</#outputformat>"<#rt/>
</#if>
<#if attributes.onmouseup??>
 onmouseup="<#outputformat 'JavaScript'>${attributes.onmouseup}</#outputformat>"<#rt/>
</#if>
<#if attributes.onmouseover??>
 onmouseover="<#outputformat 'JavaScript'>${attributes.onmouseover}</#outputformat>"<#rt/>
</#if>
<#if attributes.onmousemove??>
 onmousemove="<#outputformat 'JavaScript'>${attributes.onmousemove}</#outputformat>"<#rt/>
</#if>
<#if attributes.onmouseout??>
 onmouseout="<#outputformat 'JavaScript'>${attributes.onmouseout}</#outputformat>"<#rt/>
</#if>
<#if attributes.onfocus??>
 onfocus="<#outputformat 'JavaScript'>${attributes.onfocus}</#outputformat>"<#rt/>
</#if>
<#if attributes.onblur??>
 onblur="<#outputformat 'JavaScript'>${attributes.onblur}</#outputformat>"<#rt/>
</#if>
<#if attributes.onkeypress??>
 onkeypress="<#outputformat 'JavaScript'>${attributes.onkeypress}</#outputformat>"<#rt/>
</#if>
<#if attributes.onkeydown??>
 onkeydown="<#outputformat 'JavaScript'>${attributes.onkeydown}</#outputformat>"<#rt/>
</#if>
<#if attributes.onkeyup??>
 onkeyup="<#outputformat 'JavaScript'>${attributes.onkeyup}</#outputformat>"<#rt/>
</#if>
<#if attributes.onselect??>
 onselect="<#outputformat 'JavaScript'>${attributes.onselect}</#outputformat>"<#rt/>
</#if>
<#if attributes.onchange??>
 onchange="<#outputformat 'JavaScript'>${attributes.onchange}</#outputformat>"<#rt/>
</#if>