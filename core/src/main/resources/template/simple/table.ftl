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
<#assign webTable = tag/>
<#assign tableModel = webTable.model/>

<#if tableModel??>
<p align="center">
<table bgcolor="white" border="0" cellpadding="1" cellspacing="0" >
    <tr>
        <td>
            <table  border="0" cellpadding="2" cellspacing="1">
                <tr bgcolor="yellow">
<#list webTable.columns as curColumn>
<#if curColumn.visible>
                    <th>
<#if webTable.sortable>
                        <table border="0" cellspacing="0" cellpadding="0">
                            <tr>
                                <td>${curColumn.displayName}</td>
                                <td>
                                    <table border="0" cellspacing="0" cellpadding="0">
                                        <tr>
                                            <td align="bottom">
<#if false>
                                                 <img src="images/sorted_asc.gif" border="0" align="bottom" />
<#else>
                                                <a href="<@s.url><@s.param name="${webTable.sortColumnLinkName}" value="${curColumn.offset}"/><@s.param name="${webTable.sortOrderLinkName}" value="ASC"/></@s.url>"><img src="images/unsorted_asc.gif" border="0" align="bottom"/></a>
</#if>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td align="top"></td>
                                        </tr>
                                    </table>
                                </td>
                            </tr>
                        </table>
<#else>
                        ${curColumn.displayName}
</#if>
                    </th>
</#if>
</#list>
                </tr>
<#list webTable.rowIterator as curRow>
                <tr>
<#list curRow as curColumn>
                    <td>${curColumn}</td>
</#list>
                </tr>
</#list>
            </table>
        </td>
    </tr>
</table>
</#if>